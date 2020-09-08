package com.kancy.delay.message.db.scheduler.xxljob;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.IJobHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * XxlJobConfig
 *
 * @author kancy
 * @date 2019/12/21 14:08
 */
@ConditionalOnClass({XxlJobExecutor.class, IJobHandler.class})
public class XxlJobConfig {

    @Bean
    @ConfigurationProperties(prefix = "xxl.job")
    @ConditionalOnProperty(prefix = "xxl.job", name = {"admin.addresses", "executor.appname"})
    public XxlJobProperties xxlJobProperties(){
        return new XxlJobProperties();
    }

    /**
     * xxl-job执行器
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "xxl.job", name = {"admin.addresses", "executor.appname"})
    @ConditionalOnMissingBean(name = {"xxlJobExecutor","xxlJobSpringExecutor"})
    @ConditionalOnClass(name={"com.xxl.job.core.executor.impl.XxlJobSpringExecutor"})
    public XxlJobExecutor xxlSpringJobExecutor(XxlJobProperties properties) {
        XxlJobExecutor xxlJobExecutor = getXxlJobExecutor();
        setXxlJobProperties(properties, xxlJobExecutor);
        return xxlJobExecutor;
    }

    /**
     * xxl-job执行器
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "destroy")
    @ConditionalOnProperty(prefix = "xxl.job", name = {"admin.addresses", "executor.appname"})
    @ConditionalOnMissingBean(name = {"xxlJobExecutor","xxlJobSpringExecutor"})
    @ConditionalOnMissingClass({"com.xxl.job.core.executor.impl.XxlJobSpringExecutor"})
    public XxlJobExecutor xxlJobExecutor(XxlJobProperties properties) {
        XxlJobExecutor xxlJobExecutor = new XxlJobExecutor();
        setXxlJobProperties(properties, xxlJobExecutor);
        return xxlJobExecutor;
    }

    private void setXxlJobProperties(XxlJobProperties properties, XxlJobExecutor xxlJobExecutor) {
        xxlJobExecutor.setAdminAddresses(properties.getAdmin().getAddresses());
        Method setAppNameMethod = ReflectionUtils.findMethod(xxlJobExecutor.getClass(), "setAppName", String.class);
        if (Objects.isNull(setAppNameMethod)){
            // version >= 2.2.0
            setAppNameMethod = ReflectionUtils.findMethod(xxlJobExecutor.getClass(), "setAppname", String.class);
        }
        if (Objects.nonNull(setAppNameMethod)){
            try {
                ReflectionUtils.makeAccessible(setAppNameMethod);
                setAppNameMethod.invoke(xxlJobExecutor, properties.getExecutor().getAppname());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        xxlJobExecutor.setIp(properties.getExecutor().getIp());
        xxlJobExecutor.setPort(properties.getExecutor().getPort());
        xxlJobExecutor.setAccessToken(properties.getAdmin().getAccessToken());
        xxlJobExecutor.setLogPath(properties.getExecutor().getLogpath());
        xxlJobExecutor.setLogRetentionDays(properties.getAdmin().getLogretentiondays());
    }

    private XxlJobExecutor getXxlJobExecutor() {
        try {
            Class<?> xxlJobSpringExecutorClass =
                    ClassUtils.forName("com.xxl.job.core.executor.impl.XxlJobSpringExecutor",
                            getClass().getClassLoader());
            return (XxlJobExecutor) org.springframework.objenesis.instantiator.util.ClassUtils
                    .newInstance(xxlJobSpringExecutorClass);
        } catch (Exception e) {
            return new XxlJobExecutor();
        }
    }

    @Bean
    public DelayMessageTableJob delayMessageTableJobHandler(){
        return new DelayMessageTableJob();
    }

}
