package com.autumncode.examples.queueconsumption;

import org.apache.activemq.ScheduledMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Random;

public class TimedTransitionQueueListener implements SessionAwareMessageListener {
    final static Random random = new Random();
    Logger log = LoggerFactory.getLogger(this.getClass());
    int state = 0;
    @Qualifier("transitionJMSTemplate")
    @Autowired
    JmsTemplate jmsTemplate;
    int expireTime;

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

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
                try {
                    Thread.sleep(getExpireTime() * 2 - random.nextInt(getExpireTime()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                    newMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,
                            (from - getState()) * getExpireTime());
                    log.info("Resending transition from " + from + " to " + to + ", attempt #" + (attempts + 1) +
                            " with delay of " + newMessage.getLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY));
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
