/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50726
 Source Host           : localhost:3306
 Source Schema         : delay_message_db

 Target Server Type    : MySQL
 Target Server Version : 50726
 File Encoding         : 65001

 Date: 18/07/2020 18:20:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_delay_message_info
-- ----------------------------
DROP TABLE IF EXISTS `t_delay_message_info`;
CREATE TABLE `t_delay_message_info` (
  `id` bigint(20) NOT NULL DEFAULT 0 COMMENT '数据主键ID',
  `message` varchar(2000) NOT NULL DEFAULT '' COMMENT '消息内容',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `created_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '延时消息信息表';
-- ----------------------------
-- Records of t_delay_message_info
-- ----------------------------



-- ----------------------------
-- Table structure for t_delay_message
-- ----------------------------
DROP TABLE IF EXISTS `t_delay_message`;
CREATE TABLE `t_delay_message`  (
  `id` bigint(20) NOT NULL DEFAULT 0 COMMENT '主键',
  `message_key` varchar(40) NOT NULL DEFAULT '' COMMENT '消息key',
  `data_id` varchar(40) NOT NULL DEFAULT '' COMMENT '数据ID',
  `trace_id` varchar(32) NOT NULL DEFAULT '' COMMENT '链路ID',
  `scan_times` tinyint(4) NOT NULL DEFAULT 0 COMMENT '扫描次数',
  `message_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '消息状态（ 0：待处理，1：处理中，2：处理成功，3：处理失败，4：处理超时）',
  `expired_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '到期时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `created_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_expired_time`(`expired_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT '延时消息表';

-- ----------------------------
-- Records of t_delay_message
-- ----------------------------

-- ----------------------------
-- Table structure for t_delay_message_config
-- ----------------------------
DROP TABLE IF EXISTS `t_delay_message_config`;
CREATE TABLE `t_delay_message_config`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `message_key` varchar(80) NOT NULL COMMENT '消息KEY',
  `message_type` varchar(80) NOT NULL DEFAULT '' COMMENT '消息类型',
  `table_name` varchar(80) NOT NULL DEFAULT '' COMMENT '延时消息表名',
  `notice_type` varchar(20) NOT NULL DEFAULT '' COMMENT 'HTTP,MQ,BEAN',
  `notice_address` varchar(1000) NOT NULL DEFAULT '' COMMENT 'URL/渠道名称/SpringBean名称',
  `use_cache` tinyint(1) NOT NULL DEFAULT 0 COMMENT '使用缓存（0：不使用，1：使用）',
  `max_scan_times` tinyint(4) NOT NULL DEFAULT 3 COMMENT '最大扫描次数',
  `alive_time` varchar(20) NOT NULL DEFAULT '30d' COMMENT '消息存活时间（表达式）',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `created_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_message_key`(`message_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT '延时消息配置表';;

-- ----------------------------
-- Records of t_delay_message_config
-- ----------------------------
INSERT INTO `t_delay_message_config` VALUES (1, 'test_bean', 'test_bean', 't_delay_message', 'BEAN', 'log', 1, 5,'30d', 0, '2020-07-18 18:12:54', '2020-07-18 18:12:54');
INSERT INTO `t_delay_message_config` VALUES (2, 'test_http', 'test_http', 't_delay_message', 'HTTP', 'http://localhost:8080/callback', 1, 5,'30d', 0, '2020-07-18 18:12:54', '2020-07-18 18:12:54');
INSERT INTO `t_delay_message_config` VALUES (3, 'test_stream', 'test_stream', 't_delay_message', 'MQ', 'test.business', 1, 5,'30d', 0, '2020-07-18 18:12:54', '2020-07-18 18:12:54');

SET FOREIGN_KEY_CHECKS = 1;
