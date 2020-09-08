package com.kancy.delay.message.db.demo.performance;

import com.kancy.delay.message.db.scheduler.DelayMessageScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final DelayMessageScheduler DelayMessageScheduler;

    @Async
//    @Scheduled(cron = "0/5 * * * * ? ")
    public void onConsume() {
        DelayMessageScheduler.schedule("t_delay_message", 500);
    }

}
