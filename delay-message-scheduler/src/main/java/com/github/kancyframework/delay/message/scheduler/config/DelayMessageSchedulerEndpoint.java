package com.github.kancyframework.delay.message.scheduler.config;

import com.github.kancyframework.delay.message.scheduler.DelayMessageScheduler;
import net.dreamlu.mica.core.result.R;
import net.dreamlu.mica.core.utils.$;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.http.ActuatorMediaType;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import java.util.Collections;

/**
 * <p>
 * MyEndpoint
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/23 10:03
 **/
@ConditionalOnClass({ActuatorMediaType.class, HealthIndicator.class})
@Endpoint(id = "delayMessageScheduler")
public class DelayMessageSchedulerEndpoint {

    @Autowired
    private DelayMessageScheduler delayMessageScheduler;

    /**
     * 调度消息
     * @return
     */
    @ReadOperation
    public R schedule(String table, int limit) {
        int scheduleSize = 0;
        try {
            scheduleSize = delayMessageScheduler.schedule(table, limit);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.success(Collections.singletonMap("size", scheduleSize));
    }

    /**
     * 调度消息(带扫描时间)
     * @return
     */
    @WriteOperation(produces = {"application/json", ActuatorMediaType.V2_JSON})
    public R scheduleWithScanTime(String table, int limit, String minScanTime) {
        int scheduleSize = 0;
        try {
            scheduleSize = delayMessageScheduler.schedule(table, limit, $.parseDate(minScanTime, "yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.success(Collections.singletonMap("size", scheduleSize));
    }
}