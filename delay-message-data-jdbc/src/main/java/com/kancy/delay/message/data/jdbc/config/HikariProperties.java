package com.kancy.delay.message.data.jdbc.config;

/**
 * HikariProperties
 *
 * @author kancy
 * @date 2020/7/18 14:46
 */
public class HikariProperties {
    /**
     * 连接池名称
     */
    private String poolName;
    /**
     * 最大存活时间
     */
    private long maxLifetime = 1800000;
    /**
     * 连接池大小
     */
    private int maxPoolSize = 10;
    /**
     * 空闲连接数
     */
    private int minIdle = 1;
    /**
     * 空闲连接存活时间
     */
    private long idleTimeout = 600000;
    /**
     * 连接超时时间
     */
    private long connectionTimeout = 30000;
    /**
     * 连接测试查询语句
     */
    private String connectionTestQuery;

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public long getMaxLifetime() {
        return maxLifetime;
    }

    public void setMaxLifetime(long maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public String getConnectionTestQuery() {
        return connectionTestQuery;
    }

    public void setConnectionTestQuery(String connectionTestQuery) {
        this.connectionTestQuery = connectionTestQuery;
    }
}
