package com.github.kancyframework.delay.message.client.config;

import com.github.kancyframework.delay.message.client.DelayMessageClient;
import com.github.kancyframework.delay.message.message.SimpleDelayMessage;
import net.dreamlu.mica.core.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.http.ActuatorMediaType;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.util.StringUtils;

import java.util.HashMap;

/**
 * <p>
 * MyEndpoint
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/23 10:03
 **/
@ConditionalOnClass({ActuatorMediaType.class, HealthIndicator.class})
@Endpoint(id = "delayMessage")
public class DelayMessageClientEndpoint {

    @Autowired
    private DelayMessageClient delayMessageClient;

    /**
     * 发送消息
     * @param messageKey
     * @param delay
     * @param payload
     * @return
     */
    @WriteOperation(produces = {"application/json", ActuatorMediaType.V2_JSON})
    public R send(String messageKey, String delay, String payload) {
        HashMap<String, Object> map = null;
        try {
            SimpleDelayMessage<String> message = new SimpleDelayMessage<>(payload);
            message.setDelay(DurationStyle.detectAndParse(delay));
            message.setMessageKey(messageKey);
            delayMessageClient.send(message);
            map = new HashMap<>();
            map.put("dataId", message.getDataId());
            map.put("messageId", message.getDelayMessageId());
            map.put("tableName", message.getTableName());
            if (StringUtils.hasText(message.getTraceId())){
                map.put("traceId", message.getTraceId());
            }
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.success(map);
    }
}