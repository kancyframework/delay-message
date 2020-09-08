package com.github.kancyframework.delay.message.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>
 * Application
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/16 20:57
 **/
@EnableBinding
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class DemoSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoSchedulerApplication.class, args);
    }
}
