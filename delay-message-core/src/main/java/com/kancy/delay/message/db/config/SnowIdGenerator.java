package com.kancy.delay.message.db.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.concurrent.ThreadLocalRandom;

/**
 * SnowIdGenerator
 *
 * 分布式高效有序ID生产黑科技(sequence)
 * <p>优化开源项目：https://gitee.com/yu120/sequence</p>
 *
 * @author kancy
 * @since 2016-08-18
 */
public class SnowIdGenerator {

    private static final Logger log = LoggerFactory.getLogger(SnowIdGenerator.class);

    /**
     * 时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）
     */
    private static long twepoch = 1288834974657L;
    /**
     * 机器标识位数
     */
    private static long workerIdBits = 5L;
    private static long datacenterIdBits = 5L;
    private static long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private static long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    /**
     * 毫秒内自增位
     */
    private static long sequenceBits = 12L;
    private static long workerIdShift = sequenceBits;
    private static long datacenterIdShift = sequenceBits + workerIdBits;
    /**
     * 时间戳左移动位
     */
    private static long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private static long sequenceMask = -1L ^ (-1L << sequenceBits);

    private final long workerId;

    /**
     * 数据标识 ID 部分
     */
    private final long datacenterId;
    /**
     * 并发控制
     */
    private long sequence = 0L;
    /**
     * 上次生产 ID 时间戳
     */
    private long lastTimestamp = -1L;

    public SnowIdGenerator() {
        this.datacenterId = getDatacenterId(maxDatacenterId);
        this.workerId = getMaxWorkerId(datacenterId, maxWorkerId);
    }

    /**
     * 有参构造器
     *
     * @param workerId     工作机器 ID
     * @param datacenterId 序列号
     */
    public SnowIdGenerator(long workerId, long datacenterId) {
        Assert.isTrue(!(workerId > maxWorkerId || workerId < 0),
                String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        Assert.isTrue(!(datacenterId > maxDatacenterId || datacenterId < 0),
                String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 获取 maxWorkerId
     */
    protected static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
        StringBuilder mpid = new StringBuilder();
        mpid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (StringUtils.hasText(name)) {
            /*
             * GET jvmPid
             */
            mpid.append(name.split("@")[0]);
        }
        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * 数据标识id部分
     */
    protected static long getDatacenterId(long maxDatacenterId) {
        long id = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                    id = id % (maxDatacenterId + 1);
                }
            }
        } catch (Exception e) {
            log.warn(" getDatacenterId: {}", e.getMessage());
        }
        return id;
    }

    /**
     * 获取下一个 ID
     *
     * @return 下一个 ID
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        //闰秒
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = timeGen();
                    if (timestamp < lastTimestamp) {
                        throw new IllegalStateException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            } else {
                throw new IllegalStateException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
            }
        }

        if (lastTimestamp == timestamp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 同一毫秒的序列数已经达到最大
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒内，序列号置为 1 - 3 随机数
            sequence = ThreadLocalRandom.current().nextLong(1, 3);
        }

        lastTimestamp = timestamp;

        // 时间戳部分 | 数据中心部分 | 机器标识部分 | 序列号部分
        return ((timestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

}
