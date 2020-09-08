package com.github.kancyframework.delay.message.scheduler.handler.processor;

import com.github.kancyframework.delay.message.scheduler.DelayMessageRef;
import com.github.kancyframework.delay.message.scheduler.handler.DelayMessageBeanProcessor;
import net.dreamlu.mica.core.utils.$;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * LogBeanProcessor
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/21 20:17
 **/

public class LogBeanProcessor implements DelayMessageBeanProcessor<String> {
    private static final Logger log = LoggerFactory.getLogger(LogBeanProcessor.class);
    /**
     * 处理
     *
     * @param ref
     */
    @Override
    public void process(DelayMessageRef<String> ref) {
        String jsonMessage = $.toJson(ref);
        log.info("接收延时消息：{}", jsonMessage);
    }
}
