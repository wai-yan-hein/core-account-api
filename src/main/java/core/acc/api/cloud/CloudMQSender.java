package core.acc.api.cloud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.acc.api.common.Util1;
import core.acc.api.config.ActiveMqCondition;
import core.acc.api.entity.ChartOfAccount;
import core.acc.api.entity.Department;
import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;
import core.acc.api.model.DepartmentUser;
import core.acc.api.model.ReturnObject;
import core.acc.api.repo.UserRepo;
import core.acc.api.service.COAService;
import core.acc.api.service.DepartmentService;
import core.acc.api.service.GlService;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQTempQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@Conditional(ActiveMqCondition.class)
public class CloudMQSender {
    private final Gson gson = new GsonBuilder()
            .setDateFormat(DateFormat.FULL, DateFormat.FULL)
            .create();
    @Value("${cloud.activemq.listen.queue}")
    private String listenQ;
    @Autowired
    private GlService glService;
    @Autowired
    private COAService coaService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private JmsTemplate cloudMQTemplate;
    @Autowired
    private UserRepo userRepo;
    private final HashMap<String, String> hmQueue = new HashMap<>();

    //service
    private boolean client = false;
    private String serverQ;
    private boolean progress = false;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    private void uploadToServer() {
        initQueue();
        client = Util1.getBoolean(userRepo.getProperty("cloud.upload.server"));
        serverQ = userRepo.getProperty("cloud.activemq.account.server.queue");
        if (client) {
            log.info("This program is running as a client.");
            if (!progress) {
                progress = true;
                //destroyQ(serverQ);
                downloadSetup();
                uploadSetup();
                downloadTransaction();
                uploadTransaction();
                progress = false;
            }
        } else {
            log.info("This program is running as a server.");
        }
    }

    private void initQueue() {
        List<DepartmentUser> listDep = userRepo.getDepartment();
        HashMap<Integer, String> hmDep = new HashMap<>();
        listDep.forEach(d -> hmDep.put(d.getDeptId(), d.getAccountQ()));
        List<Department> list = departmentService.findAll();
        if (!list.isEmpty()) {
            for (Department l : list) {
                String deptCode = l.getKey().getDeptCode();
                Integer deptId = l.getMapDeptId();
                hmQueue.put(deptCode, hmDep.get(deptId));
            }
        }
    }

    private void saveMessage(String entity, String data, String queue) {
        try {
            MessageCreator mc = (Session session) -> {
                MapMessage mm = session.createMapMessage();
                mm.setString("SENDER_QUEUE", listenQ);
                mm.setString("ENTITY", entity);
                mm.setString("OPTION", "SAVE");
                mm.setString("DATA", data);
                return mm;
            };
            if (queue != null) {
                cloudMQTemplate.send(queue, mc);
                log.info(entity + " sent to " + queue);
            }
        } catch (Exception e) {
            log.error(String.format("saveMessage : %s : %s", entity, e.getMessage()));
        }
    }

    private void uploadFile(String option, Object data, String queue) {
        String path = String.format("temp%s%s", File.separator, option + ".json");
        try {
            Util1.writeJsonFile(data, path);
            byte[] file = Util1.zipJsonFile(path);
            MessageCreator mc = (Session session) -> {
                MapMessage mm = session.createMapMessage();
                mm.setString("SENDER_QUEUE", listenQ);
                mm.setString("ENTITY", "FILE");
                mm.setString("OPTION", option);
                mm.setBytes("DATA_FILE", file);
                return mm;
            };
            if (queue != null) {
                cloudMQTemplate.send(queue, mc);
            }
        } catch (IOException e) {
            log.error(String.format("uploadFile : %s : %s", option, e.getMessage()));
        }

    }

    private void deleteMessage(String entity, String data, String queue) {
        try {
            MessageCreator mc = (Session session) -> {
                MapMessage mm = session.createMapMessage();
                mm.setString("SENDER_QUEUE", listenQ);
                mm.setString("ENTITY", entity);
                mm.setString("OPTION", "DELETE");
                mm.setString("DATA", data);
                return mm;
            };
            if (queue != null) {
                cloudMQTemplate.send(queue, mc);
            }
        } catch (Exception e) {
            log.error(String.format("deleteMessage : %s : %s", entity, e.getMessage()));
        }
    }


    private void destroyQ(String queue) {
        try {
            if (cloudMQTemplate != null) {
                ConnectionFactory factory = cloudMQTemplate.getConnectionFactory();

                if (factory != null) {
                    Connection connection = factory.createConnection();
                    if (connection instanceof ActiveMQConnection con) {
                        con.deleteTempDestination(new ActiveMQTempQueue(queue));
                    }

                }
            }
        } catch (JMSException e) {
            log.error("destroyQ : " + e.getMessage());
        }
    }


    private void requestFile(String option, String date) {
        try {
            MessageCreator mc = (Session session) -> {
                MapMessage mm = session.createMapMessage();
                mm.setString("SENDER_QUEUE", listenQ);
                mm.setString("ENTITY", "FILE");
                mm.setString("OPTION", option);
                mm.setString("DATA", date);
                return mm;
            };
            if (serverQ != null) {
                cloudMQTemplate.send(serverQ, mc);
            }
        } catch (Exception e) {
            log.error(String.format("requestFile : %s : %s", option, e.getMessage()));
        }
    }


    private void downloadSetup() {
        requestFile("COA_REQUEST", gson.toJson(new ChartOfAccount(coaService.getMaxDate())));
    }

    private void downloadTransaction() {
        requestFile("GL_REQUEST", gson.toJson(new Gl(glService.getMaxDate(), userRepo.getDepCode())));
    }

    private void uploadSetup() {
        uploadCOA();
    }

    private void uploadTransaction() {
        uploadGl();
    }

    private void uploadGl() {
        List<Gl> list = glService.unUpload(Util1.toDateStr(Util1.getSyncDate(), "yyyy-MM-dd"));
        if (!list.isEmpty()) {
            log.info("upload gl : " + list.size());
            uploadFile("GL_UPLOAD", list, serverQ);
        }
    }

    private void uploadCOA() {
        List<ChartOfAccount> list = coaService.unUpload();
        if (!list.isEmpty()) {
            log.info("upload coa : " + list.size());
            uploadFile("COA_UPLOAD", list, serverQ);
        }
    }

    public void send(Gl gl) {
        if (gl != null) {
            saveMessage("GL", gson.toJson(gl), getQueue(gl));
        }
    }

    public void send(ReturnObject ro) {
        if (ro != null) {
            uploadGl();
        }
    }

    private String getQueue(Gl sh) {
        return client ? serverQ : hmQueue.get(sh.getDeptCode());
    }

    public void delete(GlKey key) {
        Gl obj = glService.findByCode(key);
        if (obj != null) {
            deleteMessage("GL", gson.toJson(obj), getQueue(obj));
        }
    }
}
