package core.acc.api.cloud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.acc.api.common.Util1;
import core.acc.api.config.ActiveMqCondition;
import core.acc.api.entity.Gl;
import core.acc.api.repo.UserRepo;
import core.acc.api.service.GlService;
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
import java.text.DateFormat;
import java.util.List;

@Slf4j
@Component
@Conditional(ActiveMqCondition.class)
public class CloudMQReceiver {
    private final String SAVE = "SAVE";
    private final String REC = "REC";
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
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
        String senderQ = message.getString("SENDER_QUEUE");
        if (data != null) {
            try {
                log.info(String.format("receivedMessage : %s - %s - %s", entity, option, senderQ));
                String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
                switch (entity) {
                    case "GL" -> {
                        Gl obj = gson.fromJson(data, Gl.class);
                        switch (option) {
                            case "SAVE", "RESPONSE_TRAN" -> save(obj);
                            case "RECEIVE" -> update(obj);
                            case "DELETE" -> glService.delete(obj.getKey());
                            case "REQUEST_TRAN" -> {
                                List<Gl> list = glService.search(Util1.toDateStr(obj.getModifyDate(), dateTimeFormat), obj.getDeptCode());
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseTran(entity, senderQ, gson.toJson(v)));
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
    }

    private void update(Gl gl) {

    }

    private void save(Gl gl) {

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
