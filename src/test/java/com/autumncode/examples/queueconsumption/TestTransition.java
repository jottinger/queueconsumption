package com.autumncode.examples.queueconsumption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import static org.testng.Assert.assertEquals;

@ContextConfiguration(locations = {"classpath:spring-test-config.xml"})
public class TestTransition extends AbstractTestNGSpringContextTests {
    @Autowired
    @Qualifier("jmsTemplateTransition")
    JmsTemplate jmsTemplate;

    @Autowired
    TransitionQueueListener listener;

    @Test
    public void testOrderedTransitions() {
        listener.reset();
        for(int i=0;i<10;i++) {
            final int finalI = i;
            jmsTemplate.send(session -> {
                Message message=session.createMessage();
                message.setIntProperty("from", finalI);
                message.setIntProperty("to", finalI+1);
                return message;
            });
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(listener.getState(), 10);
    }

    @Test
    public void testOutOfOrderTransitions() {
        listener.reset();
        for(int i=9;i>=0;i--) {
            final int finalI = i;
            jmsTemplate.send(session -> {
                Message message=session.createMessage();
                message.setIntProperty("from", finalI);
                message.setIntProperty("to", finalI+1);
                return message;
            });
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(listener.getState(), 10);
    }

}
