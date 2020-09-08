package com.kancy.delay.message.db.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;

/**
 * <p>
 * Application
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/16 20:57
 **/
@EnableBinding({Sink.class})
@SpringBootApplication
public class DemoClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoClientApplication.class, args);
    }
}
