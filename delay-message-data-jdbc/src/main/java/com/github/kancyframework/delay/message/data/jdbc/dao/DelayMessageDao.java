package com.github.kancyframework.delay.message.data.jdbc.dao;

import com.github.kancyframework.delay.message.message.MessageStatus;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * DelayMessageDao
 *
 * @author kancy
 * @date 2020/7/15 20:36
 */
public class DelayMessageDao {

    private final JdbcTemplate masterJdbcTemplate;
    private final JdbcTemplate slaveJdbcTemplate;

    public DelayMessageDao(JdbcTemplate masterJdbcTemplate, JdbcTemplate slaveJdbcTemplate) {
        this.masterJdbcTemplate = masterJdbcTemplate;
        this.slaveJdbcTemplate = slaveJdbcTemplate;
    }

    public boolean saveWhitDelay(String tableName, DelayMessageEntity message, Duration delay) {
        Assert.hasText(tableName, "call saveWhitDelay() tableName is empty.");
        return masterJdbcTemplate.update(String.format("insert into %s (id, message_key, data_id, message_status, scan_times, expired_time, trace_id, created_time, updated_time) values (?,?,?,?,?, date_add(now(), interval ? second),?, now(), now())", tableName),
                message.getId(), message.getMessageKey(), message.getDataId(), message.getMessageStatus(), message.getScanTimes(), delay.getSeconds(), Objects.isNull(message.getTraceId()) ? "" : message.getTraceId()) > 0;
    }


    public List<DelayMessageEntity> scan(String tableName, long minExpireTime, int limit) {
        Assert.hasText(tableName, "tableName is empty.");
        Assert.state(limit > 0, "limit must greater than zero.");

        String sql = String.format(
                "select id, message_key, data_id, message_status, scan_times, expired_time,trace_id, created_time, updated_time " +
                        "from %s " +
                        "where expired_time >= FROM_UNIXTIME(%d) " +
                        "and expired_time <= now() " +
                        "and message_status in (%d,%d) " +
                        "order by expired_time asc " +
                        "limit %d",
                tableName, (minExpireTime / 1000), MessageStatus.WAITING.ordinal(), MessageStatus.TIMEOUT.ordinal(), limit);

        return slaveJdbcTemplate.query(sql, (rs, rowNum) -> {
            DelayMessageEntity entity = new DelayMessageEntity();
            fillDelayMessageEntity(rs, entity);
            return entity;
        });
    }

    private DelayMessageEntity fillDelayMessageEntity(ResultSet rs, DelayMessageEntity entity) throws SQLException {
        entity.setId(rs.getString("id"));
        entity.setMessageKey(rs.getString("message_key"));
        entity.setDataId(rs.getString("data_id"));
        entity.setMessageStatus(rs.getInt("message_status"));
        entity.setScanTimes(rs.getInt("scan_times") + 1);
        entity.setExpiredTime(rs.getTimestamp("expired_time"));
        entity.setTraceId(rs.getString("trace_id"));
        entity.setCreatedTime(rs.getTimestamp("created_time"));
        entity.setUpdatedTime(rs.getTimestamp("updated_time"));
        return entity;
    }

    public void updateStatus(String tableName, String id, int status) {
        masterJdbcTemplate.update(String.format("update %s set message_status = ?, updated_time = now() where id = ? ", tableName), status, id);
    }

    public void batchUpdateStatus(String tableName, List<String> ids, int fromStatus, int toStatus) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        masterJdbcTemplate.batchUpdate(String.format("update %s set message_status = ?, updated_time = now() where id = ? and message_status = ?", tableName),
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, toStatus);
                        ps.setString(2, ids.get(i));
                        ps.setLong(3, fromStatus);
                    }

                    @Override
                    public int getBatchSize() {
                        return ids.size();
                    }
                });
    }

    public void batchUpdateOnProcessing(String tableName, List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        masterJdbcTemplate.batchUpdate(String.format("update %s set scan_times = scan_times + 1 , message_status = ? , updated_time = now() where id = ? ", tableName),
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, MessageStatus.RUNNING.ordinal());
                        ps.setString(2, ids.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return ids.size();
                    }
                });
    }

    /**
     * 在指定时间范围内，找出超时处理的数据ID (时间滑动窗口)
     * @param tableName
     * @param duration
     * @param recentSeconds
     * @return
     */
    public List<String> findAllExecuteTimeoutMessageIds(String tableName, Duration duration, long recentSeconds) {
        List<String> ids = new ArrayList<>();
        slaveJdbcTemplate.query(String.format("select id from %s " +
                        "where expired_time >= date_add(now(), interval -%d second) " +
                        "and expired_time <= now() " +
                        "and message_status = %d " +
                        "and (UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(updated_time)) > %d " +
                        "order by expired_time asc limit 1000",
                tableName, recentSeconds, MessageStatus.RUNNING.ordinal(), duration.getSeconds()), new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                ids.add(rs.getString("id"));
            }
        });
        return ids;
    }

    public Date findMinExpireTime(String tableName, long recentSeconds) {
        Assert.hasText(tableName, "tableName is empty.");
        String sql = String.format("select expired_time " +
                "from %s " +
                "where expired_time >= date_add(now(), interval -%d second) " +
                "expired_time <= now() " +
                "and message_status not in (%d,%d) " +
                "order by expired_time asc " +
                "limit 1", tableName,recentSeconds, MessageStatus.SUCCESS.ordinal(), MessageStatus.FAIL.ordinal());
        return slaveJdbcTemplate.queryForObject(sql, (RowMapper<Date>) (rs, rowNum) -> rs.getTimestamp("expired_time"));
    }
}
