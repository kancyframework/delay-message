package com.github.kancyframework.delay.message.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.*;

/**
 * DelayMessageDbHealthChecker
 *
 * @author kancy
 * @date 2020/7/21 0:18
 */
@ConditionalOnProperty(prefix = "management.health.delay-message", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass({HealthIndicator.class, Status.class, Health.class})
public class DelayMessageHealthIndicator implements HealthIndicator {

    private List<HealthIndicator> healthIndicatorList = new ArrayList<>();

    @Override
    public Health health() {
        Map<String, Object> map = new HashMap<>();
        for (HealthIndicator healthIndicator : healthIndicatorList) {
            Health health = healthIndicator.health();
            if (!Objects.equals(health.getStatus(), Status.UP)){
                return Health.down().withDetails(health.getDetails()).build();
            }
            map.putAll(health.getDetails());
        }
        return  Health.up().withDetails(map).build();
    }

    public void addHealthIndicator(HealthIndicator healthIndicator){
        if (Objects.nonNull(healthIndicator)){
            healthIndicatorList.add(healthIndicator);
        }
    }

}
