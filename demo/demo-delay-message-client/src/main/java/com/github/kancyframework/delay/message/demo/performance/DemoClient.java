package com.github.kancyframework.delay.message.demo.performance;

import com.github.kancyframework.delay.message.client.DelayMessageClient;
import com.github.kancyframework.delay.message.message.CustomTraceDelayMessage;
import com.github.kancyframework.delay.message.message.IDelayMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.utils.ThreadUtil;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Producer
 *
 * @author kancy
 * @date 2020/7/18 16:35
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DemoClient implements ApplicationListener<ApplicationStartedEvent> {
    private final DelayMessageClient delayMessageClient;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        new Thread(() -> {
            List<String> keys = Arrays.asList("test_bean","test_http","test_stream");
            while (true){
                for (int i = 0; i < 10; i++) {
                    delayMessageClient.send(getMessage(keys));
                }
                ThreadUtil.sleep(1000);
            }
        }).start();
    }

    private IDelayMessage getMessage(List<String> keys) {
        String key = keys.get(ThreadLocalRandom.current().nextInt(0, keys.size()));
        Duration delay = Duration.ofSeconds(ThreadLocalRandom.current().nextLong(5, 10));

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", UUID.randomUUID().toString());
        payload.put("name", "kancy");

        if (Objects.equals(key, "test_bean")){
            TestDelayMessage testDelayMessage = new TestDelayMessage();
            testDelayMessage.setPayload(payload);
            testDelayMessage.setDelay(delay);
            return testDelayMessage;
        }

        CustomTraceDelayMessage<Object> delayMessage = new CustomTraceDelayMessage<>();
        delayMessage.setPayload(payload);
        delayMessage.setDelay(delay);
        delayMessage.setMessageKey(key);
        return delayMessage;
    }
}
