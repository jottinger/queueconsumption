package com.autumncode.examples.queueconsumption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class TestConfig extends AbstractTestNGSpringContextTests {
    @Autowired
    SampleService service;

    @Test
    void testConfig() {
        System.out.println(service);
    }
}
