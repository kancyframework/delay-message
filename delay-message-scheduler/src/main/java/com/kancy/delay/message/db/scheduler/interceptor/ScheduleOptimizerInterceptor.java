package com.kancy.delay.message.db.scheduler.interceptor;

import com.kancy.delay.message.db.interceptor.DelayMessageSchedulerInterceptor;
import com.kancy.delay.message.db.scheduler.DelayMessageScheduleOptimizer;
import com.kancy.delay.message.db.service.DelayMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * ScheduleOptimizerInterceptor
 * <p>
 *
 * @author kancy
 * @see 2020/7/28 12:47
 **/
@Component
public class ScheduleOptimizerInterceptor implements DelayMessageSchedulerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ScheduleOptimizerInterceptor.class);

    @Autowired
    private DelayMessageScheduleOptimizer optimizer;

    /**
     * 扫描完成
     *
     * @param tableName
     * @param limit
     * @param minExpireTime
     * @param startTime
     * @param delayMessageEntities
     */
    @Override
    public void scanCompleted(String tableName, int limit, long minExpireTime,
                              long startTime, List<DelayMessage> delayMessageEntities) {
        // 更新优化器参数
        if (!CollectionUtils.isEmpty(delayMessageEntities)) {
            updateOptimizer(tableName, delayMessageEntities);
        }
    }

    /**
     * 更新优化器参数
     *
     * @param tableName
     * @param entityList
     */
    private void updateOptimizer(String tableName, List<DelayMessage> entityList) {
        Optional<Date> optional = entityList.stream()
                .map(DelayMessage::getExpiredTime)
                .min(Date::compareTo);

        if (optional.isPresent()) {
            Date minExpireDate = optional.get();
            optimizer.setMinExpireTime(tableName, minExpireDate.getTime() - 1000);
            log.info("更新延时消息表[{}]的最小的到期时间：{}", tableName, minExpireDate);
        }
    }
}
