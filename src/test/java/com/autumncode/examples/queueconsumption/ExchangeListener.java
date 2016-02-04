package com.autumncode.examples.queueconsumption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;

public class ExchangeListener implements SessionAwareMessageListener {
    Logger log= LoggerFactory.getLogger(this.getClass());
    List<String> messages = new ArrayList<>();

    public void reset() {
        log.info("Clearing messages");
        messages.clear();
    }

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        log.info("Received message");
        if (message instanceof TextMessage) {
            TextMessage textMessage= (TextMessage) message;
            log.info("Adding message to list: "+textMessage.getText());
            messages.add(textMessage.getText());
        }
    }
}
