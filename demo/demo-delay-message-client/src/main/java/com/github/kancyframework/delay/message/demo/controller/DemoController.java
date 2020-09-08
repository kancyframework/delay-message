package com.github.kancyframework.delay.message.demo.controller;

import com.github.kancyframework.delay.message.client.DelayMessageClient;
import com.github.kancyframework.delay.message.config.TraceContextResolver;
import com.github.kancyframework.delay.message.message.CustomTraceDelayMessage;
import com.github.kancyframework.delay.message.message.IDelayMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.result.R;
import net.dreamlu.mica.core.utils.$;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * DemoController
 *
 * @author kancy
 * @date 2020/7/18 10:30
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class DemoController {

    private final DelayMessageClient delayMessageClient;
    private final TraceContextResolver traceContextResolver;

    @PostMapping("/callback")
    public R<Map<String, Object>> callback(@RequestBody String req){
        log.info(req);
        return R.success($.readJsonAsMap(req, String.class, Object.class));
    }

    @GetMapping("/send")
    public R<IDelayMessage> send(String messageKey, @RequestParam(defaultValue = "PT5S") String delay){
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", UUID.randomUUID().toString());
        payload.put("name", "kancy");

        CustomTraceDelayMessage<Object> delayMessage = new CustomTraceDelayMessage<>();
        delayMessage.setPayload(payload);
        delayMessage.setDelay(Duration.parse(delay));
        delayMessage.setTraceId(traceContextResolver.getTraceId());
        delayMessage.setMessageKey(messageKey);

        delayMessageClient.send(delayMessage);
        return R.success(delayMessage);
    }
}
