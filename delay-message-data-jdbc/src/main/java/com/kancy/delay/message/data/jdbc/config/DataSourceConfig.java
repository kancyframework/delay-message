package com.kancy.delay.message.data.jdbc.config;

import com.kancy.delay.message.db.health.DelayMessageHealthIndicator;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * DataSourceConfig
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/22 10:28
 **/
@AutoConfigureAfter(name = {
        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
        "com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration"
})
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Autowired(required = false)
    private DelayMessageHealthIndicator delayMessageHealthIndicator;

    @Autowired(required = false)
    private Map<String, DataSource> dataSourceMap = Collections.emptyMap();

    @Bean("masterJdbcTemplate")
    @ConditionalOnProperty(prefix = "delay.message.datasource.master", name = "reference")
    public JdbcTemplate masterJdbcTemplate1(DataSourceProperties properties) {
        String reference = properties.getMaster().getReference();
        DataSource dataSource = findDataSourceRef(reference);
        if (Objects.isNull(dataSource)){
            dataSource = properties.getMaster().initializeDataSourceBuilder().build();
            setDruidDataSourceProperty(dataSource, properties.getMaster().getDruid(),true);
            setHikariDataSourceProperty(dataSource, properties.getMaster().getHikari(), true);
            addDatasourceHealthIndicator(dataSource);
            log.info("Init delay message master datasource by create : {}", properties.getMaster().getUrl());
        }else {
            log.info("Init delay message master datasource by reference : {}", reference);
        }
        return new JdbcTemplate(dataSource);
    }

    @Bean("masterJdbcTemplate")
    @ConditionalOnProperty(prefix = "delay.message.datasource.master", name = "url")
    @ConditionalOnMissingBean(name = "masterJdbcTemplate")
    public JdbcTemplate masterJdbcTemplate2(DataSourceProperties properties) {

        DataSource dataSource = properties.getMaster().initializeDataSourceBuilder().build();
        setDruidDataSourceProperty(dataSource, properties.getMaster().getDruid(),true);
        setHikariDataSourceProperty(dataSource, properties.getMaster().getHikari(), true);
        addDatasourceHealthIndicator(dataSource);
        log.info("Init delay message master datasource by create : {}", properties.getMaster().getUrl());
        return new JdbcTemplate(dataSource);
    }

    @Bean("slaveJdbcTemplate")
    @ConditionalOnProperty(prefix = "delay.message.datasource.slave", name = "reference")
    public JdbcTemplate slaveJdbcTemplate1(DataSourceProperties properties) {
        String reference = properties.getSlave().getReference();
        DataSource dataSource = findDataSourceRef(properties.getSlave().getReference());
        if (Objects.isNull(dataSource)){
            dataSource = properties.getSlave().initializeDataSourceBuilder().build();
            setDruidDataSourceProperty(dataSource, properties.getSlave().getDruid(), false);
            setHikariDataSourceProperty(dataSource, properties.getSlave().getHikari(), false);
            addDatasourceHealthIndicator(dataSource);
            log.info("Init delay message slave datasource by create : {}", properties.getMaster().getUrl());
        }else {
            log.info("Init delay message slave datasource by reference : {}", reference);
        }
        return new JdbcTemplate(dataSource);
    }


    @Bean("slaveJdbcTemplate")
    @ConditionalOnProperty(prefix = "delay.message.datasource.slave", name = "url")
    @ConditionalOnMissingBean(name = "slaveJdbcTemplate")
    public JdbcTemplate slaveJdbcTemplate2(DataSourceProperties properties) {
        DataSource dataSource = properties.getSlave().initializeDataSourceBuilder().build();
        setDruidDataSourceProperty(dataSource, properties.getSlave().getDruid(), false);
        setHikariDataSourceProperty(dataSource, properties.getSlave().getHikari(), false);
        addDatasourceHealthIndicator(dataSource);
        log.info("Init delay message slave datasource by create : {}", properties.getSlave().getUrl());
        return new JdbcTemplate(dataSource);
    }


    /**
     * 设置DruidDataSource属性
     * @param dataSource
     * @param druidProperties
     * @param isMaster
     */
    private void setDruidDataSourceProperty(DataSource dataSource, DruidProperties druidProperties, boolean isMaster) {
        if (Objects.nonNull(druidProperties) && Objects.equals(dataSource.getClass().getName(), "com.alibaba.druid.pool.DruidDataSource")){
            druidProperties.getConnectProperties().put("druid.stat.mergeSql",String.valueOf(druidProperties.isMergeSql()));
            druidProperties.getConnectProperties().put("druid.stat.slowSqlMillis",String.valueOf(druidProperties.getSlowSqlMillis()));
            if (StringUtils.isEmpty(druidProperties.getName())){
                druidProperties.setName(isMaster ? "MasterDelayMessageDruidDataSource" : "SlaveDelayMessageDruidDataSource");
            }
            BeanUtils.copyProperties(druidProperties, dataSource);
        }
    }

    /**
     * 设置HikariDataSource属性
     * @param dataSource
     * @param hikariProperties
     */
    private void setHikariDataSourceProperty(DataSource dataSource, HikariProperties hikariProperties, boolean isMaster) {
        if (Objects.nonNull(hikariProperties) && dataSource instanceof HikariDataSource){
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            hikariDataSource.setMaximumPoolSize(hikariProperties.getMaxPoolSize());
            hikariDataSource.setMaxLifetime(hikariProperties.getMaxLifetime());
            hikariDataSource.setMinimumIdle(hikariProperties.getMinIdle());
            hikariDataSource.setConnectionTestQuery(hikariProperties.getConnectionTestQuery());
            hikariDataSource.setConnectionTimeout(hikariProperties.getConnectionTimeout());
            hikariDataSource.setIdleTimeout(hikariProperties.getIdleTimeout());
            hikariDataSource.setPoolName(hikariProperties.getPoolName());
            if (StringUtils.isEmpty(hikariProperties.getPoolName())){
                hikariDataSource.setPoolName(isMaster ? "MasterDelayMessageHikariDataSource" : "SlaveDelayMessageHikariDataSource");
            }
        }
    }

    private DataSource findDataSourceRef(String dataSourceRefName) {
        if (StringUtils.isEmpty(dataSourceRefName)){
            return null;
        }
        DataSource dataSource = dataSourceMap.get(dataSourceRefName);
        if (Objects.isNull(dataSource)){
            Class<?> dynamicRoutingDataSourceClass =
                    null;
            try {
                dynamicRoutingDataSourceClass = ClassUtils.forName("com.baomidou.dynamic.datasource.DynamicRoutingDataSource", getClass().getClassLoader());
            } catch (ClassNotFoundException e) {
                return null;
            }
            for (Map.Entry<String, DataSource> entry : dataSourceMap.entrySet()) {
                try {
                    DataSource value = entry.getValue();
                    if (ClassUtils.isAssignableValue(dynamicRoutingDataSourceClass, value)){
                        Method getDataSourceMethod = ReflectionUtils.findMethod(dynamicRoutingDataSourceClass, "getDataSource", String.class);
                        ReflectionUtils.makeAccessible(getDataSourceMethod);
                        Object object = getDataSourceMethod.invoke(value, dataSourceRefName);
                        if (Objects.nonNull(object) && object instanceof DataSource){
                            return (DataSource) object;
                        }
                    }
                } catch (Exception e) {
                    log.warn("findDataSourceRef : {}", e.getMessage());
                }
            }
        }
        return dataSource;
    }

    private void addDatasourceHealthIndicator(DataSource dataSource) {
        if (Objects.nonNull(delayMessageHealthIndicator)){
            delayMessageHealthIndicator.addHealthIndicator(new DataSourceHealthIndicator(dataSource));
        }
    }
}
