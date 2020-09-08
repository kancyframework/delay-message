package com.github.kancyframework.delay.message.message;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * MessageKey
 *
 * @author kancy
 * @date 2020/7/15 21:54
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DelayMessage {
    /**
     * key
     *
     * @return
     */
    @AliasFor("topic")
    String value() default "";

    /**
     * topic
     *
     * @return
     */
    @AliasFor("value")
    String topic() default "";

    /**
     * 延时时间
     *
     * @return
     */
    String delay() default "";
}
