package com.kancy.delay.message.data.jdbc.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * DelayMessageConfigDao
 *
 * @author kancy
 * @date 2020/7/17 22:19
 */
public class DelayMessageConfigDao {

    private final JdbcTemplate slaveJdbcTemplate;

    public DelayMessageConfigDao(JdbcTemplate slaveJdbcTemplate) {
        this.slaveJdbcTemplate = slaveJdbcTemplate;
    }

    public DelayMessageConfigEntity queryConfigByMessageKey(@NonNull String messageKey) {
        DelayMessageConfigEntity entity = new DelayMessageConfigEntity();
        slaveJdbcTemplate.query("select id,message_key,message_type,`table_name`,notice_type,notice_address,use_cache,max_scan_times,alive_time from t_delay_message_config where message_key = ?",
                new Object[]{messageKey}, new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException {
                        fillDelayMessageConfigEntity(rs, entity);
                    }
                });
        return Objects.isNull(entity.getId()) ? null : entity;
    }

    public List<DelayMessageConfigEntity> queryConfigByMessageKeys(@NonNull Set<String> messageKeys) {
        List<DelayMessageConfigEntity> configs = new ArrayList<>();
        slaveJdbcTemplate.query(String.format("select id,message_key,message_type,`table_name`,notice_type,notice_address,use_cache,max_scan_times,alive_time from t_delay_message_config where message_key in (%s)", getInSql(messageKeys)),
                rs -> {
                    DelayMessageConfigEntity entity = new DelayMessageConfigEntity();
                    fillDelayMessageConfigEntity(rs, entity);
                    configs.add(entity);
                });
        return configs;
    }


    private void fillDelayMessageConfigEntity(ResultSet rs, DelayMessageConfigEntity entity) throws SQLException {
        entity.setId(rs.getLong("id"));
        entity.setMessageKey(rs.getString("message_key"));
        entity.setMessageType(rs.getString("message_type"));
        entity.setTableName(rs.getString("table_name"));
        entity.setNoticeType(rs.getString("notice_type"));
        entity.setNoticeAddress(rs.getString("notice_address"));
        entity.setUseCache(rs.getBoolean("use_cache"));
        entity.setMaxScanTimes(rs.getInt("max_scan_times"));
        String aliveTime = rs.getString("alive_time");
        entity.setAliveTime(aliveTime);
    }

    /**
     * 拼接IN条件值
     *
     * @return
     */
    private static String getInSql(Collection<String> collection) {
        StringBuilder sb = new StringBuilder();
        for (String value : collection) {
            sb.append("'").append(value).append("'").append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
