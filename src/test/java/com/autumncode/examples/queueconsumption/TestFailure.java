package com.autumncode.examples.queueconsumption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@ContextConfiguration(locations = {"classpath:spring-test-config.xml"})
public class TestFailure extends AbstractTestNGSpringContextTests {
    @Qualifier("failingJMSTemplate")
    @Autowired
    JmsTemplate jmsTemplate;
    @Autowired
    FailingQueueListener listener;

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testFailure() {
        jmsTemplate.send(session -> session.createTextMessage("this is a test"));
        log.info("Sending message");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(listener.getLastMessage(), "this is a test");
    }
}
