package com.autumncode.examples.queueconsumption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import javax.jms.TextMessage;

import static org.testng.Assert.assertEquals;

@ContextConfiguration(locations = {"classpath:spring-test-config.xml"})
public class TestWriteToQueue extends AbstractTestNGSpringContextTests {
    @Autowired
    @Qualifier("exchangeJMSTemplate")
    JmsTemplate jmsTemplate;

    @Autowired
    ExchangeListener listener;

    @Test
    public void testExchange() {
        jmsTemplate.send(session -> {
            TextMessage message = session.createTextMessage();
            message.setText("this is a test");
            return message;
        });
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(listener.messages.size(), 1);
        assertEquals(listener.messages.get(0), "this is a test");
    }
}
