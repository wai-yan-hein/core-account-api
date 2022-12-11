package core.acc.api.cloud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.acc.api.common.Util1;
import core.acc.api.config.ActiveMqCondition;
import core.acc.api.entity.Department;
import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;
import core.acc.api.model.DepartmentUser;
import core.acc.api.repo.UserRepo;
import core.acc.api.service.DepartmentService;
import core.acc.api.service.GlService;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.*;
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
    private DepartmentService departmentService;
    @Autowired
    private JmsTemplate cloudMQTemplate;
    @Autowired
    private JmsTemplate topicSender;
    @Autowired
    private UserRepo userRepo;
    private final HashMap<String, String> hmQueue = new HashMap<>();

    //service
    private boolean client;
    private String serverQ;
    private boolean progress = false;

    @Scheduled(fixedRate = 10000000)
    private void uploadToServer() {
        initQueue();
        client = Util1.getBoolean(userRepo.getProperty("cloud.upload.server"));
        serverQ = userRepo.getProperty("cloud.activemq.account.server.queue");
        if (client) {
            log.info("ActiveMQ Server Q : " + serverQ);
            if (!progress) {
                progress = true;
                destroyQ(serverQ);
                uploadSetup();
                uploadTransaction();
                downloadSetup();
                downloadTransaction();
                progress = false;
            }
        }
    }

    private void initQueue() {
        List<DepartmentUser> listDep = userRepo.getDepartment();
        HashMap<Integer, String> hmDep = new HashMap<>();
        listDep.forEach(d -> hmDep.put(d.getDeptId(), d.getQueueName()));
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
        }
    }

    private void deleteMessage(String entity, String data, String queue) {
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
    }

    private void truncateMessage(String entity, String data, String queue) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("SENDER_QUEUE", listenQ);
            mm.setString("ENTITY", entity);
            mm.setString("OPTION", "TRUNCATE");
            mm.setString("DATA", data);
            return mm;
        };
        if (queue != null) {
            cloudMQTemplate.send(queue, mc);
        }
    }

    private void restoreMessage(String entity, String data, String queue) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("SENDER_QUEUE", listenQ);
            mm.setString("ENTITY", entity);
            mm.setString("OPTION", "RESTORE");
            mm.setString("DATA", data);
            return mm;
        };
        if (queue != null) {
            cloudMQTemplate.send(queue, mc);
        }
    }

    private void destroyQ(String queue) {
        try {
            if (cloudMQTemplate != null) {
                ConnectionFactory factory = cloudMQTemplate.getConnectionFactory();

                if (factory != null) {
                    Connection connection = factory.createConnection();
                    if (connection instanceof ActiveMQConnection con) {
                        con.destroyDestination(new ActiveMQQueue(queue));
                    }

                }
            }
        } catch (JMSException e) {
            log.error("destroyQ : " + e.getMessage());
        }
    }

    private void sendTopicMessage(String entity, String data) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("SENDER_QUEUE", listenQ);
            mm.setString("ENTITY", entity);
            mm.setString("OPTION", "SETUP");
            mm.setString("DATA", data);
            return mm;
        };
        if (topicSender != null) {
            topicSender.send(mc);
        }
    }

    private void requestSetup(String entity, String date) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("SENDER_QUEUE", listenQ);
            mm.setString("ENTITY", entity);
            mm.setString("OPTION", "REQUEST_SETUP");
            mm.setString("DATA", date);
            return mm;
        };
        if (serverQ != null) {
            cloudMQTemplate.send(serverQ, mc);
        }
    }

    private void requestTran(String entity, String date) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("SENDER_QUEUE", listenQ);
            mm.setString("ENTITY", entity);
            mm.setString("OPTION", "REQUEST_TRAN");
            mm.setString("DATA", date);
            return mm;
        };
        if (serverQ != null) {
            cloudMQTemplate.send(serverQ, mc);
        }
    }

    private void uploadSetup() {

    }

    private void downloadSetup() {

    }

    private void downloadTransaction() {
        requestTran("GL", gson.toJson(new Gl(glService.getMaxDate(), userRepo.getDepCode())));
    }

    private void uploadTransaction() {
        uploadGl();
    }

    private void uploadGl() {
        log.info(Util1.toDateStr(Util1.getSyncDate(), "yyyy-MM-dd"));
        List<Gl> list = glService.unUpload(Util1.toDateStr(Util1.getSyncDate(), "yyyy-MM-dd"));
        if (!list.isEmpty()) log.info("upload gl : " + list.size());
        list.forEach(o -> saveMessage("GL", gson.toJson(o), serverQ));
    }

    public void send(Gl gl) {
        if (gl != null) {
            saveMessage("GL", gson.toJson(gl), getQueue(gl));
        }
    }

    private String getQueue(Gl sh) {
        return client ? serverQ : hmQueue.get(sh.getDeptCode());
    }

    public void delete(GlKey key) {
        Gl obj = new Gl();
        obj.setKey(key);
        deleteMessage("GL", gson.toJson(obj), serverQ);
    }
}
