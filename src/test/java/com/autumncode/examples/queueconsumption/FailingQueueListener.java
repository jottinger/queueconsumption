package com.autumncode.examples.queueconsumption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

public class FailingQueueListener implements SessionAwareMessageListener {
    static boolean fail = true;
    Logger log = LoggerFactory.getLogger(this.getClass());

    String lastMessage = null;

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Qualifier("failingJMSTemplate")
    @Autowired
    JmsTemplate jmsTemplate;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        log.info("Received message: "+message);
        try {
            log.debug("Fail status is " + fail);
            if (fail) {
                jmsTemplate.send(session1 -> message);
            } else {
                if (message instanceof TextMessage) {
                    TextMessage textMessage= (TextMessage) message;
                    log.info("Consuming message: " + textMessage.getText());
                    setLastMessage(textMessage.getText());
                }
            }
        } finally {
            fail = !fail;
        }
    }
}
