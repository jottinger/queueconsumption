package com.autumncode.examples.queueconsumption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import javax.jms.Message;

import static org.testng.Assert.assertEquals;

@ContextConfiguration(locations = {"classpath:spring-timed-transition.xml"})
public class TestTimedTransition extends AbstractTestNGSpringContextTests {
    @Autowired
    @Qualifier("transitionJMSTemplate")
    JmsTemplate jmsTemplate;

    @Autowired
    TimedTransitionQueueListener listener;

    @Test
    public void testOrderedTransitions() {
        listener.reset();
        for (int i = 0; i < 10; i++) {
            sendMessage(i);
        }
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(listener.getState(), 10);
    }

    private void sendMessage(int i) {
        final int finalI = i;
        jmsTemplate.send(session -> {
            Message message = session.createMessage();
            message.setIntProperty("from", finalI);
            message.setIntProperty("to", finalI + 1);
            return message;
        });
    }

    @Test
    public void testOutOfOrderTransitions() {
        listener.reset();
        for (int i = 9; i >= 0; i--) {
            sendMessage(i);
        }
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(listener.getState(), 10);
    }

}
