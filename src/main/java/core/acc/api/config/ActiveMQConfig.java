/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.acc.api.config;

import core.acc.api.common.Util1;
import core.acc.api.repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.DeliveryMode;
import javax.jms.Topic;
import java.util.List;

/**
 * @author wai yan
 */
@Slf4j
@Configuration
@Conditional(ActiveMqCondition.class)
@PropertySource(value = {"file:config/application.properties"})
@EnableJms
public class ActiveMQConfig {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private Environment environment;


    public ActiveMQConnectionFactory connectionFactory(String url) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(url);
        connectionFactory.setTrustedPackages(List.of("core"));
        return connectionFactory;
    }

    @Bean
    public Topic topic() {
        return new ActiveMQTopic("ACCOUNT_MSG");
    }

    @Bean(name = "topicSender")
    public JmsTemplate topicSender() {
        JmsTemplate template = new JmsTemplate();
        String url = userRepo.getProperty("cloud.activemq.url");
        template.setConnectionFactory(connectionFactory(url));
        template.setDefaultDestination(topic());
        template.setPubSubDomain(true);
        return template;
    }

    @Bean(name = "cloudMQTemplate")
    public JmsTemplate cloudMQTemplate() {
        JmsTemplate template = new JmsTemplate();
        String url = userRepo.getProperty("cloud.activemq.url");
        template.setConnectionFactory(connectionFactory(url));
        template.setDeliveryMode(DeliveryMode.PERSISTENT);
        template.setMessageIdEnabled(true);
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory queueContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        String url = userRepo.getProperty("cloud.activemq.url");
        factory.setConnectionFactory(connectionFactory(url));
        factory.setConcurrency("1-1");
        log.info("ActiveMQ connection configured at " + url);
        return factory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory topicContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        String url = userRepo.getProperty("cloud.activemq.url");
        factory.setConnectionFactory(connectionFactory(url));
        factory.setConcurrency("1-1");
        factory.setPubSubDomain(true);
        return factory;
    }

    @Bean
    public void initSyncDate() {
        Util1.SYNC_DATE = environment.getProperty("sync.date");
    }

}
