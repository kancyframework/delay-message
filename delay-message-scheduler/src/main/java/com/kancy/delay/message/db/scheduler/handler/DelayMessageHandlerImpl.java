package com.kancy.delay.message.db.scheduler.handler;

import com.kancy.delay.message.db.exception.CallbackNoticeException;
import com.kancy.delay.message.db.exception.NotFundDelayMessageHandlerException;
import com.kancy.delay.message.db.scheduler.DelayMessageRef;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * DefaultDelayMessageHandler
 *
 * @author kancy
 * @date 2020/7/18 0:08
 */
public class DelayMessageHandlerImpl implements DelayMessageHandler<String>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private static Map<String, Class<? extends DelayMessageHandler<String>>> handleClasses = new HashMap<>();

    @Override
    public void handle(DelayMessageRef<String> ref) {
        // 匹配DelayMessageHandler
        DelayMessageHandler<String> delayMessageHandler = null;
        try {
            delayMessageHandler = applicationContext.getBean(handleClasses.get(ref.getNoticeType()));
        } catch (Exception e) {
            throw new NotFundDelayMessageHandlerException(String.format("Not impl delay message [%s] noticeType : %s",
                    ref.getMessageKey(), ref.getNoticeType()), e);
        }

        // 执行回调通知
        try {
            delayMessageHandler.handle(ref);
        } catch (Exception e) {
            throw new CallbackNoticeException(String.format("延时消息回调通知[%s,%s]处理失败：%s",
                    ref.getNoticeType(), ref.getNoticeAddress(), e.getMessage()), e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    static {
        handleClasses.put("MQ", DelayMessageStreamHandler.class);
        handleClasses.put("STREAM", DelayMessageStreamHandler.class);
        handleClasses.put("HTTP", DelayMessageHttpHandler.class);
        handleClasses.put("BEAN", DelayMessageBeanHandler.class);
    }

}
