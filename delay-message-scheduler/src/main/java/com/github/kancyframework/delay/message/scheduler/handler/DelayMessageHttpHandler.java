package com.github.kancyframework.delay.message.scheduler.handler;

import com.github.kancyframework.delay.message.message.DelayMessageResponse;
import com.github.kancyframework.delay.message.message.IDelayMessage;
import com.github.kancyframework.delay.message.scheduler.DelayMessageRef;
import net.dreamlu.mica.core.utils.$;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * DelayMessageRabbitMqHandler
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/17 20:14
 **/

public class DelayMessageHttpHandler implements DelayMessageHandler<String> {

    private static final Logger log = LoggerFactory.getLogger(DelayMessageHttpHandler.class);

    private final RestTemplate restTemplate;

    public DelayMessageHttpHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void handle(DelayMessageRef<String> ref) {
        String url = ref.getNoticeAddress();
        Map<String, Object> paramMap = null;
        if (ref.isUseCache()) {
            paramMap = $.readJsonAsMap(ref.getPayload(), String.class, Object.class);
            paramMap.put(IDelayMessage.MESSAGE_KEY, ref.getMessageKey());
            paramMap.put(IDelayMessage.DATA_ID, ref.getDataId());
        } else {
            paramMap = new HashMap<>();
            paramMap.put(IDelayMessage.MESSAGE_KEY, ref.getMessageKey());
            paramMap.put(IDelayMessage.DATA_ID, ref.getDataId());
        }
        paramMap.put(IDelayMessage.TABLE_NAME, ref.getTableName());
        paramMap.put(IDelayMessage.MESSAGE_TYPE, ref.getMessageType());
        paramMap.put(IDelayMessage.MESSAGE_ID, ref.getDelayMessageId());

        String strResult = restTemplate.postForObject(url, paramMap, String.class);
        log.info("delay message [{},{}] http notice callback result : {}", ref.getMessageKey(), ref.getDelayMessageId(), strResult);

        Assert.hasText(strResult, "回调返回结果格式有误");
        DelayMessageResponse response = $.readJson(strResult, DelayMessageResponse.class);
        Assert.notNull(response, "回调返回结果格式有误");
        Assert.isTrue(response.isSuccess(), "回调返回结果失败");
    }
}
