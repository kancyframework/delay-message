package com.kancy.delay.message.data.jdbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * DataSourceProperties
 *
 * @author kancy
 * @date 2020/7/25 11:59
 */
@ConfigurationProperties(prefix = "delay.message.datasource")
public class DataSourceProperties {

    /**
     * master数据源
     */
    @NestedConfigurationProperty
    private DataSourceConfig master;

    /**
     * slave数据源
     */
    @NestedConfigurationProperty
    private DataSourceConfig slave;


    public static class DataSourceConfig extends
            org.springframework.boot.autoconfigure.jdbc.DataSourceProperties {
        /**
         * 引用数据源
         */
        private String reference;

        /**
         * druid连接池
         */
        @NestedConfigurationProperty
        private DruidProperties druid;

        /**
         * hikari连接池
         */
        @NestedConfigurationProperty
        private HikariProperties hikari;

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public DruidProperties getDruid() {
            return druid;
        }

        public void setDruid(DruidProperties druid) {
            this.druid = druid;
        }

        public HikariProperties getHikari() {
            return hikari;
        }

        public void setHikari(HikariProperties hikari) {
            this.hikari = hikari;
        }
    }

    public DataSourceConfig getMaster() {
        return master;
    }

    public void setMaster(DataSourceConfig master) {
        this.master = master;
    }

    public DataSourceConfig getSlave() {
        return slave;
    }

    public void setSlave(DataSourceConfig slave) {
        this.slave = slave;
    }
}
