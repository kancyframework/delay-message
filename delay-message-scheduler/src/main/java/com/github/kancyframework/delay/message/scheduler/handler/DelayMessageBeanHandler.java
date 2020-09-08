package com.github.kancyframework.delay.message.scheduler.handler;

import com.github.kancyframework.delay.message.exception.NotFundDelayMessageHandlerException;
import com.github.kancyframework.delay.message.scheduler.DelayMessageRef;
import net.dreamlu.mica.core.utils.$;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * DelayMessageBeanHandler
 *
 * @author kancy
 * @date 2020/7/18 0:14
 */
public class DelayMessageBeanHandler implements DelayMessageHandler<String>{

    @Autowired(required = false)
    private Map<String, DelayMessageBeanProcessor> processorMap = Collections.emptyMap();

    @Override
    public void handle(DelayMessageRef<String> ref) {
        DelayMessageBeanProcessor processor = getDelayMessageBeanProcessor(ref.getNoticeAddress());
        if (Objects.isNull(processor)){
            throw new NotFundDelayMessageHandlerException(
                    String.format("Not Found [%s] DelayMessageBeanProcessor Spring Bean.", ref.getNoticeAddress()));
        }

        Class<?> genericClass = ResolvableType.forClass(processor.getClass()).as(DelayMessageBeanProcessor.class).getGeneric(0).resolve();
        if (Objects.equals(genericClass, String.class) || Objects.equals(genericClass, Object.class)) {
            processor.process(ref);
            return;
        }
        DelayMessageRef objectDelayMessageRef = new DelayMessageRef();
        $.copy(ref, objectDelayMessageRef);
        objectDelayMessageRef.setPayload($.readJson(ref.getPayload(), genericClass));
        processor.process(objectDelayMessageRef);
    }

    private DelayMessageBeanProcessor getDelayMessageBeanProcessor(String beanName) {
        return processorMap.getOrDefault(beanName,
                    processorMap.getOrDefault(String.format("%sBeanProcessor", beanName),
                        processorMap.get(String.format("%sDelayMessageBeanProcessor", beanName))));
    }

}
