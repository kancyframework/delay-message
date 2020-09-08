package com.github.kancyframework.delay.message.actuator;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import net.dreamlu.mica.core.utils.$;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * DelayMessageMetrics
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/19 11:08
 **/

public class DelayMessageMetrics implements EnvironmentAware {

    private static final String UNKNOWN = "UNKNOWN";

    private static final ThreadLocal<Long> MESSAGE_START_TIME = new ThreadLocal<>();
    private static final ThreadLocal<Long> SCHEDULER_START_TIME = new ThreadLocal<>();

    private String hostIp;
    private String application;
    private String port;

    private boolean addApplicationTag = false;
    private boolean addInstanceTag = false;

    @Autowired
    private MeterRegistry registry;

    public void putMessageSendOrHandleStartClock() {
        MESSAGE_START_TIME.set(registry.config().clock().monotonicTime());
    }

    public void putSchedulerScheduleStartClock() {
        SCHEDULER_START_TIME.set(registry.config().clock().monotonicTime());
    }

    public void scheduler(String tableName) {
        try {
            Timer.Builder builder = Timer.builder("delay.message.db.scheduler")
                    .description("延时消息消费调度监控")
                    .tag("table", StringUtils.hasLength(tableName) ? tableName : UNKNOWN)
                    .tag("date", $.formatDate(new Date()));
            if (addInstanceTag) {
                builder.tag("instance", String.format("%s:%s", hostIp, port));
            }
            if (addApplicationTag) {
                builder.tag("application", application);
            }
            builder.register(registry)
                    .record(registry.config().clock().monotonicTime() - SCHEDULER_START_TIME.get(), TimeUnit.NANOSECONDS);
        } finally {
            SCHEDULER_START_TIME.remove();
        }

    }

    public void scheduled(String tableName, String messageKey, boolean result) {
        time(tableName, messageKey, "schedule", result);
    }

    public void sendCompleted(String tableName, String messageKey, boolean result) {
        time(tableName, messageKey, "send", result);
    }

    private void time(String tableName, String messageKey, String type, boolean result) {
        try {
            Timer.Builder builder = Timer.builder(String.format("delay.message.db.%s", type))
                    .description("基于数据实现的延时消息系统监控")
                    .tag("table", StringUtils.hasLength(tableName) ? tableName : UNKNOWN)
                    .tag("date", $.formatDate(new Date()))
                    .tag("key", StringUtils.hasLength(messageKey) ? messageKey : UNKNOWN)
                    .tag("success", String.valueOf(result));
            if (addInstanceTag) {
                builder.tag("instance", String.format("%s:%s", hostIp, port));
            }
            if (addApplicationTag) {
                builder.tag("application", application);
            }
            builder.register(registry)
                    .record(registry.config().clock().monotonicTime() - MESSAGE_START_TIME.get(), TimeUnit.NANOSECONDS);
        } finally {
            MESSAGE_START_TIME.remove();
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.application = environment.getProperty("spring.application.name", "");
        addApplicationTag = StringUtils.hasText(application);

        this.port = environment.getProperty("server.port", "");
        InetAddress inetAddress = getInetAddress();
        if (Objects.nonNull(inetAddress)) {
            this.hostIp = inetAddress.getHostAddress();
        }
        addInstanceTag = StringUtils.hasText(hostIp) && StringUtils.hasText(port);
    }

    private InetAddress getInetAddress() {
        try {
            InetAddress candidateAddress = null;
            Enumeration ifaces = NetworkInterface.getNetworkInterfaces();

            List<InetAddress> availableInetAddresses = new ArrayList<>();
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                Enumeration inetAddrs = iface.getInetAddresses();

                while (inetAddrs.hasMoreElements()) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            availableInetAddresses.add(inetAddr);
                            continue;
                        }
                        if (candidateAddress == null) {
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }

            if (availableInetAddresses.isEmpty()) {
                if (candidateAddress != null) {
                    availableInetAddresses.add(candidateAddress);
                } else {
                    InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
                    availableInetAddresses.add(jdkSuppliedAddress);
                }
            }

            for (InetAddress inetAddress : availableInetAddresses) {
                if (!inetAddress.getHostAddress().endsWith(".1")) {
                    return inetAddress;
                }
            }

        } catch (Exception e) {
        }

        return null;
    }
}
