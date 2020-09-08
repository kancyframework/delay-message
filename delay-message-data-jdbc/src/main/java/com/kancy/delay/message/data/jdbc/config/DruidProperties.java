package com.kancy.delay.message.data.jdbc.config;

import java.util.Properties;

/**
 * HikariProperties
 *
 * @author kancy
 * @date 2020/7/18 14:46
 */
public class DruidProperties {
    /**
     * 名称
     */
    private String name;
    /**
     * 初始化连接数
     */
    private int initialSize = 0;
    /**
     * 最大连接数
     */
    private int maxActive = 8;
    /**
     * 最小闲置连接
     */
    private int minIdle = 0;
    /**
     * 连接属性
     */
    private Properties connectProperties = new Properties();
    /**
     * 过滤
     */
    private String filters = "stat,wall";

    /**
     * druid.stat.mergeSql
     */
    private boolean mergeSql = true;
    /**
     * druid.stat.slowSqlMillis
     */
    private long slowSqlMillis = 5000;

    /**
     * 最大等待时间
     */
    private long maxWait = 60000;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public Properties getConnectProperties() {
        return connectProperties;
    }

    public void setConnectProperties(Properties connectProperties) {
        this.connectProperties = connectProperties;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public boolean isMergeSql() {
        return mergeSql;
    }

    public void setMergeSql(boolean mergeSql) {
        this.mergeSql = mergeSql;
    }

    public long getSlowSqlMillis() {
        return slowSqlMillis;
    }

    public void setSlowSqlMillis(long slowSqlMillis) {
        this.slowSqlMillis = slowSqlMillis;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }
}
