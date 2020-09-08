package com.kancy.delay.message.data.jdbc.config;

import com.kancy.delay.message.data.jdbc.dao.DelayMessageConfigDao;
import com.kancy.delay.message.data.jdbc.dao.DelayMessageDao;
import com.kancy.delay.message.data.jdbc.dao.DelayMessageInfoDao;
import com.kancy.delay.message.data.jdbc.service.JdbcDelayMessageConfigService;
import com.kancy.delay.message.data.jdbc.service.JdbcDelayMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * JdbcDelayMessageAutoConfiguration
 *
 * @author kancy
 * @date 2020/7/25 12:09
 */
@EnableConfigurationProperties(DataSourceProperties.class)
public class JdbcDelayMessageAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DelayMessageDao delayMessageDao(@Qualifier("masterJdbcTemplate") JdbcTemplate masterJdbcTemplate,
                                           @Autowired(required = false)
                                         @Qualifier("slaveJdbcTemplate") JdbcTemplate slaveJdbcTemplate){
        if (slaveJdbcTemplate != null){
            return new DelayMessageDao(masterJdbcTemplate, slaveJdbcTemplate);
        }
        return new DelayMessageDao(masterJdbcTemplate, masterJdbcTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public DelayMessageConfigDao delayMessageConfigDao(@Qualifier("masterJdbcTemplate") JdbcTemplate masterJdbcTemplate,
                                                       @Autowired(required = false)
                                                       @Qualifier("slaveJdbcTemplate") JdbcTemplate slaveJdbcTemplate){
        if (slaveJdbcTemplate != null){
            return new DelayMessageConfigDao(slaveJdbcTemplate);
        }
        return new DelayMessageConfigDao(masterJdbcTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public DelayMessageInfoDao delayMessageInfoDao(@Qualifier("masterJdbcTemplate") JdbcTemplate masterJdbcTemplate,
                                                   @Autowired(required = false)
                                                   @Qualifier("slaveJdbcTemplate") JdbcTemplate slaveJdbcTemplate){
        if (slaveJdbcTemplate != null){
            return new DelayMessageInfoDao(masterJdbcTemplate, slaveJdbcTemplate);
        }
        return new DelayMessageInfoDao(masterJdbcTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public JdbcDelayMessageConfigService jdbcDelayMessageConfigService(DelayMessageConfigDao delayMessageConfigDao){
        return new JdbcDelayMessageConfigService(delayMessageConfigDao);
    }

    @Bean
    @ConditionalOnMissingBean
    public JdbcDelayMessageService jdbcDelayMessageService(DelayMessageDao delayMessageDao, DelayMessageInfoDao delayMessageInfoDao){
        return new JdbcDelayMessageService(delayMessageDao, delayMessageInfoDao);
    }
}
