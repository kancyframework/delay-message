package com.github.kancyframework.delay.message.demo.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Consumer
 *
 * @author kancy
 * @date 2020/7/18 16:35
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DemoScheduler {
    private final com.github.kancyframework.delay.message.scheduler.DelayMessageScheduler DelayMessageScheduler;

    @Async
//    @Scheduled(cron = "0/5 * * * * ? ")
    public void onConsume() {
        DelayMessageScheduler.schedule("t_delay_message", 500);
    }

}
