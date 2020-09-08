package com.github.kancyframework.delay.message.data.jdbc.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * DelayMessageInfoDao
 *
 * @author kancy
 * @date 2020/7/25 12:51
 */
public class DelayMessageInfoDao {

    private final JdbcTemplate masterJdbcTemplate;
    private final JdbcTemplate slaveJdbcTemplate;

    public DelayMessageInfoDao(JdbcTemplate masterJdbcTemplate) {
        this.masterJdbcTemplate = masterJdbcTemplate;
        this.slaveJdbcTemplate = masterJdbcTemplate;
    }

    public DelayMessageInfoDao(JdbcTemplate masterJdbcTemplate, JdbcTemplate slaveJdbcTemplate) {
        this.masterJdbcTemplate = masterJdbcTemplate;
        this.slaveJdbcTemplate = slaveJdbcTemplate;
    }

    public boolean save(String tableName, DelayMessageInfoEntity message){
        Assert.hasText(tableName, "call save() tableName is empty.");
        return masterJdbcTemplate.update(String.format("insert into %s (id, message) values (?,?)", tableName),
                message.getId(), message.getMessage()) > 0;
    }

    public Map<String, String> queryMessageMap(String tableName, Set<String> ids){
        Map<String, String> map = new HashMap<>();
        slaveJdbcTemplate.query(String.format("select id, message from %s where id in (%s) ", tableName, getInSql(ids)),
                rs -> {
                    map.put(rs.getString("id"), rs.getString("message"));
                }) ;
        return map;
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
