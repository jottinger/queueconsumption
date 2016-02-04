package com.autumncode.examples.queueconsumption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

public class TransitionQueueListener implements SessionAwareMessageListener {
    Logger log = LoggerFactory.getLogger(this.getClass());
    int state = 0;
    @Qualifier("transitionJMSTemplate")
    @Autowired
    JmsTemplate jmsTemplate;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        try {
            int from = message.getIntProperty("from");
            int to = message.getIntProperty("to");
            if (from == getState()) {
                log.info("Setting state from " + from + " to " + to);
                setState(to);
            } else {
                log.info("State transition error: expected " + from + ", was " + getState());
                int attempts = 0;
                try {
                    attempts = message.getIntProperty("attempts");
                } catch (NumberFormatException ignored) {
                }
                if (attempts < 20) {
                    Message newMessage = session.createMessage();
                    newMessage.setIntProperty("from", from);
                    newMessage.setIntProperty("to", to);
                    newMessage.setIntProperty("attempts", attempts + 1);
                    log.info("Resending transition from "+from+" to "+to+", attempt #"+(attempts+1));
                    jmsTemplate.send(session1 -> newMessage);
                } else {
                    log.error("Discarding message: too many attempts. " + message);
                }

            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        state = 0;
    }
}
