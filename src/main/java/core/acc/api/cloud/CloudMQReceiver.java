package core.acc.api.cloud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import core.acc.api.common.Util1;
import core.acc.api.config.ActiveMqCondition;
import core.acc.api.entity.COAKey;
import core.acc.api.entity.ChartOfAccount;
import core.acc.api.entity.Gl;
import core.acc.api.entity.GlKey;
import core.acc.api.repo.UserRepo;
import core.acc.api.service.COAService;
import core.acc.api.service.GlService;
import core.acc.api.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Conditional(ActiveMqCondition.class)
public class CloudMQReceiver {
    private final String SAVE = "SAVE";
    private final String REC = "REC";
    private final Gson gson = new GsonBuilder()
            .setDateFormat(DateFormat.FULL, DateFormat.FULL)
            .create();
    @Value("${cloud.activemq.listen.queue}")
    private String listenQ;

    @Autowired
    private JmsTemplate cloudMQTemplate;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private GlService glService;
    @Autowired
    private COAService coaService;
    @Autowired
    private ReportService service;

    private void responseSetup(String entity, String distQ, String data) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("ENTITY", entity);
            mm.setString("SENDER_QUEUE", listenQ);
            mm.setString("OPTION", "RESPONSE_SETUP");
            mm.setString("DATA", data);
            return mm;
        };
        if (distQ != null) {
            cloudMQTemplate.send(distQ, mc);
            log.info("responseSetup : " + entity);
        }
    }

    private void responseTran(String entity, String distQ, String data) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("ENTITY", entity);
            mm.setString("OPTION", "RESPONSE_TRAN");
            mm.setString("DATA", data);
            return mm;
        };
        if (distQ != null) {
            cloudMQTemplate.send(distQ, mc);
            log.info("responseTran : " + entity);
        }
    }


    @JmsListener(destination = "ACCOUNT_MSG", containerFactory = "topicContainerFactory")
    public void receivedTopicMessage(final MapMessage message) throws JMSException {
        String entity = message.getString("ENTITY");
        String option = message.getString("OPTION");
        String data = message.getString("DATA");
        String senderQ = message.getString("SENDER_QUEUE");
        String serverQ = userRepo.getProperty("cloud.activemq.server.queue");
        if (senderQ.equals(serverQ)) {
            if (data != null) {
                try {
                    log.info(String.format("receivedMessage : %s - %s - %s", entity, option, senderQ));
                    switch (option) {

                    }
                } catch (Exception e) {
                    log.error(String.format("%s : %s", entity, e.getMessage()));
                }
            }
        }

    }

    @JmsListener(destination = "${cloud.activemq.listen.queue}", containerFactory = "queueContainerFactory")
    public void receivedMessage(final MapMessage message) throws JMSException {
        String entity = message.getString("ENTITY");
        String option = message.getString("OPTION");
        String data = message.getString("DATA");
        byte[] file = message.getBytes("DATA_FILE");
        String senderQ = message.getString("SENDER_QUEUE");
        String path = String.format("temp%s%s", File.separator, "Gl");
        try {
            log.info(String.format("receivedMessage : %s - %s - %s", entity, option, senderQ));
            String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
            switch (entity) {
                case "GL" -> {
                    Gl obj = gson.fromJson(data, Gl.class);
                    switch (option) {
                        case "SAVE" -> {
                            save(obj);
                        }
                        case "RECEIVE" -> update(obj);
                        case "DELETE" -> glService.delete(obj.getKey());

                    }
                }
                case "FILE" -> {
                    Reader reader = null;
                    if (file != null) {
                        Util1.extractZipToJson(file, path);
                        reader = Files.newBufferedReader(Paths.get(path.concat(".json")));
                    }
                    switch (option) {
                        case "COA" -> {
                            assert reader != null;
                            List<ChartOfAccount> list = gson.fromJson(reader, new TypeToken<ArrayList<ChartOfAccount>>() {
                            }.getType());
                            List<ChartOfAccount> objList = new ArrayList<>();
                            if (!list.isEmpty()) {
                                list.forEach(c -> {
                                    try {
                                        c.setIntgUpdStatus(SAVE);
                                        coaService.save(c);
                                        ChartOfAccount obj = new ChartOfAccount();
                                        obj.setKey(c.getKey());
                                        objList.add(obj);
                                        log.info("saved coa : " + c.getKey().getCoaCode());
                                    } catch (Exception e) {
                                        log.error("save coa : " + e.getMessage());
                                    }
                                });
                            }
                            if (!objList.isEmpty()) {
                                fileMessage("COA_RESPONSE", objList, senderQ);
                            }
                        }
                        case "COA_REQUEST" -> {
                            ChartOfAccount obj = gson.fromJson(data, ChartOfAccount.class);
                            List<ChartOfAccount> list = coaService.search(Util1.toDateStr(obj.getModifiedDate(), dateTimeFormat));
                            if (!list.isEmpty()) {
                                fileMessage("COA", list, senderQ);
                            }
                        }
                        case "COA_RESPONSE" -> {
                            assert reader != null;
                            List<ChartOfAccount> list = gson.fromJson(reader, new TypeToken<ArrayList<ChartOfAccount>>() {
                            }.getType());
                            for (ChartOfAccount obj : list) {
                                update(obj);
                            }
                        }
                        case "GL_REQUEST" -> {
                            Gl obj = gson.fromJson(data, Gl.class);
                            List<Gl> list = glService.search(Util1.toDateStr(obj.getModifyDate(), dateTimeFormat), obj.getDeptCode());
                            if (!list.isEmpty()) {
                                fileMessage("GL", list, senderQ);
                            }
                        }
                        case "GL" -> {
                            assert reader != null;
                            List<Gl> list = gson.fromJson(reader, new TypeToken<ArrayList<Gl>>() {
                            }.getType());
                            List<Gl> objList = new ArrayList<>();
                            if (!list.isEmpty()) {
                                log.info("gl list size :" + list.size());
                                list.forEach(gl -> {
                                    try {
                                        gl.setIntgUpdStatus(SAVE);
                                        glService.save(gl);
                                        Gl obj = new Gl();
                                        obj.setKey(gl.getKey());
                                        objList.add(obj);
                                        log.info("saved : " + gl.getKey().getGlCode());
                                        sleep();
                                    } catch (Exception e) {
                                        log.error("save Gl : " + e.getMessage());
                                    }
                                });
                            }
                            if (!objList.isEmpty()) {
                                fileMessage("GL_RESPONSE", objList, senderQ);
                            }

                        }
                        case "GL_RESPONSE" -> {
                            assert reader != null;
                            List<Gl> list = gson.fromJson(reader, new TypeToken<ArrayList<Gl>>() {
                            }.getType());
                            for (Gl obj : list) {
                                update(obj);
                            }

                        }
                    }
                }
            }
            if (option.equals("SAVE")) {
                sendReceiveMessage(senderQ, entity, data);
            }

        } catch (Exception e) {
            log.error(String.format("%s : %s", entity, e.getMessage()));
        }

    }

    private void sleep() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private void fileMessage(String option, Object data, String queue) {
        String path = String.format("config%s%s", File.separator, "Gl.json");
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
            log.error("File Message : " + e.getMessage());
        }
    }

    private void update(Gl gl) {
        GlKey key = gl.getKey();
        String sql = "update gl set intg_upd_status ='" + SAVE + "'\n"
                + "where gl_code ='" + key.getGlCode() + "' and comp_code ='" + key.getCompCode() + "'";
        try {
            service.executeSql(sql);
        } catch (Exception e) {
            log.error("update Gl : " + e.getMessage());
        }
        log.info("update gl.");
    }

    private void update(ChartOfAccount coa) {
        COAKey key = coa.getKey();
        String sql = "update chart_of_account set intg_upd_status ='" + SAVE + "'\n"
                + "where coa_code ='" + key.getCoaCode() + "' and comp_code ='" + key.getCompCode() + "'";
        try {
            service.executeSql(sql);
        } catch (Exception e) {
            log.error("update coa : " + e.getMessage());
        }
        log.info("update coa.");
    }

    private void save(Gl gl) {
        try {
            String vouNo = gl.getRefNo();
            if (vouNo != null) {
                glService.deleteGl(vouNo, gl.getTranSource());
            }
            glService.save(gl);
        } catch (Exception e) {
            log.error("save Gl : " + e.getMessage());
        }
    }

    private void sendReceiveMessage(String senderQ, String entity, String data) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("ENTITY", entity);
            mm.setString("SENDER_QUEUE", listenQ);
            mm.setString("OPTION", "RECEIVE");
            mm.setString("DATA", data);
            return mm;
        };
        cloudMQTemplate.send(senderQ, mc);
        log.info(String.format("%s received and sent to %s.", entity, senderQ));
    }

}
