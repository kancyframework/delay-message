package com.github.kancyframework.delay.message.scheduler.xxljob;

import com.github.kancyframework.delay.message.config.DelayMessageTaskExecutor;
import com.github.kancyframework.delay.message.scheduler.DelayMessageScheduler;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import net.dreamlu.mica.core.utils.$;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * <p>
 * DelayMessageTableJob
 * 参数1：{"table":"t_delay_message", "limit": 200}
 * 参数2：{"table":"t_delay_message", "limit": 200, "minScanExpiredTime":"2020-07-28 12:00:00"}
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/18 23:30
 **/
@JobHandler(value = "DelayMessageTableJob")
public class DelayMessageTableJob extends IJobHandler {

    @Autowired
    private DelayMessageTaskExecutor delayMessageTaskExecutor;
    @Autowired
    private DelayMessageScheduler delayMessageScheduler;

    /**
     * 调度
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("DelayMessageJob")
    public ReturnT<String> executeJob(String param) throws Exception {
        return execute(param);
    }

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log(String.format("接收到[DelayMessageTableJob]的调度指令：%s", param));
        JobParam jobParam = JobParam.parse(param);
        Set<String> tables = jobParam.getTables();
        if (tables.size() > 1) {
            CountDownLatch countDownLatch = new CountDownLatch(tables.size());
            for (String table : tables) {
                delayMessageTaskExecutor.execute(() -> {
                    try {
                        XxlJobLogger.log(String.format("=====>开始消费[%s]表的延时消息", table));
                        int scheduleSize = delayMessageScheduler.schedule(table, jobParam.getLimit(), jobParam.convertAndGetMinScanExpiredTime());
                        XxlJobLogger.log(String.format("消费[%s]表的延时消息数量：%s", table, scheduleSize));
                    } catch (Exception e){
                        XxlJobLogger.log(String.format("消费[%s]表的延时消息失败：%s", table, e.getMessage()));
                    } finally {
                        countDownLatch.countDown();
                        XxlJobLogger.log(String.format("=====>结束消费[%s]表的延时消息", table));
                    }
                });
            }
            countDownLatch.await();
        } else {
            XxlJobLogger.log(String.format("=====>开始消费[%s]表的延时消息", jobParam.getTable()));
            try {
                int scheduleSize = delayMessageScheduler.schedule(jobParam.getTable(), jobParam.getLimit(), jobParam.convertAndGetMinScanExpiredTime());
                XxlJobLogger.log(String.format("消费[%s]表的延时消息数量：%s", jobParam.getTable(), scheduleSize));
            } catch (Exception e) {
                XxlJobLogger.log(String.format("消费[%s]表的延时消息失败：%s", jobParam.getTable(), e.getMessage()));
            }
            XxlJobLogger.log(String.format("=====>结束消费[%s]表的延时消息", jobParam.getTable()));
        }
        XxlJobLogger.log("成功执行[DelayMessageTableJob]的调度");
        return ReturnT.SUCCESS;
    }

    static class JobParam {
        /**
         * 限量
         */
        private int limit;

        /**
         * 表，支持逗号分隔
         */
        private String table;
        /**
         * 最小的扫描到期时间
         */
        private String minScanExpiredTime;

        public static JobParam parse(String param) {
            return $.readJson(param, JobParam.class);
        }

        /**
         * 分割
         *
         * @return
         */
        public Set<String> getTables() {
            checkParam();
            return StringUtils.commaDelimitedListToSet(table);
        }

        /**
         * 检察参数
         */
        public void checkParam() {
            Assert.state(limit > 0, "param limit must greater than zero.");
            Assert.hasText(table, "param table is empty.");

            if (StringUtils.hasText(minScanExpiredTime)){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dateFormat.setLenient(false);
                try {
                    dateFormat.parse(minScanExpiredTime);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("The date format is incorrect, please use the “yyyy-MM-dd HH:mm:ss” format.");
                }
            }
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public String getTable() {
            return table;
        }

        public void setTable(String table) {
            this.table = table;
        }

        public String getMinScanExpiredTime() {
            return minScanExpiredTime;
        }

        public void setMinScanExpiredTime(String minScanExpiredTime) {
            this.minScanExpiredTime = minScanExpiredTime;
        }

        public Date convertAndGetMinScanExpiredTime() {
            if (StringUtils.hasText(minScanExpiredTime)){
                return $.parseDate(minScanExpiredTime, "yyyy-MM-dd HH:mm:ss");
            }
            return null;
        }
    }
}
