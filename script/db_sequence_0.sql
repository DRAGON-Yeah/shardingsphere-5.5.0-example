/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80033 (8.0.33)
 Source Host           : localhost:3306
 Source Schema         : db_sequence_0

 Target Server Type    : MySQL
 Target Server Version : 80033 (8.0.33)
 File Encoding         : 65001

 Date: 28/08/2024 22:46:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sequence
-- ----------------------------
DROP TABLE IF EXISTS `sequence`;
CREATE TABLE `sequence` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sharding_key` bigint NOT NULL COMMENT '分库分表键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'name',
  `value` bigint DEFAULT NULL COMMENT 'value',
  `gmt_modified` datetime DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name_key` (`name`) USING BTREE COMMENT '唯一name key'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='sequence表';

-- ----------------------------
-- Records of sequence
-- ----------------------------
BEGIN;
INSERT INTO `sequence` (`id`, `sharding_key`, `name`, `value`, `gmt_modified`) VALUES (1, 0, 'user_seq', 18000, '2023-07-04 10:23:23');
COMMIT;

-- ----------------------------
-- Table structure for sequence_0
-- ----------------------------
DROP TABLE IF EXISTS `sequence_0`;
CREATE TABLE `sequence_0` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sharding_key` bigint NOT NULL COMMENT '分库分表键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'name',
  `value` bigint DEFAULT NULL COMMENT 'value',
  `gmt_modified` datetime DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name_key` (`name`) USING BTREE COMMENT '唯一name key'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='sequence表';

-- ----------------------------
-- Records of sequence_0
-- ----------------------------
BEGIN;
INSERT INTO `sequence_0` (`id`, `sharding_key`, `name`, `value`, `gmt_modified`) VALUES (1, 0, 'user_seq', 18000, '2023-07-04 10:23:23');
COMMIT;

-- ----------------------------
-- Table structure for sequence_1
-- ----------------------------
DROP TABLE IF EXISTS `sequence_1`;
CREATE TABLE `sequence_1` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sharding_key` bigint NOT NULL COMMENT '分库分表键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'name',
  `value` bigint DEFAULT NULL COMMENT 'value',
  `gmt_modified` datetime DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name_key` (`name`) USING BTREE COMMENT '唯一name key'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='sequence表';

-- ----------------------------
-- Records of sequence_1
-- ----------------------------
BEGIN;
INSERT INTO `sequence_1` (`id`, `sharding_key`, `name`, `value`, `gmt_modified`) VALUES (1, 0, 'user_seq', 18000, '2023-07-04 10:23:23');
COMMIT;

-- ----------------------------
-- Table structure for sequence_2
-- ----------------------------
DROP TABLE IF EXISTS `sequence_2`;
CREATE TABLE `sequence_2` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sharding_key` bigint NOT NULL COMMENT '分库分表键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'name',
  `value` bigint DEFAULT NULL COMMENT 'value',
  `gmt_modified` datetime DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name_key` (`name`) USING BTREE COMMENT '唯一name key'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='sequence表';

-- ----------------------------
-- Records of sequence_2
-- ----------------------------
BEGIN;
INSERT INTO `sequence_2` (`id`, `sharding_key`, `name`, `value`, `gmt_modified`) VALUES (1, 0, 'user_seq', 18000, '2023-07-04 10:23:23');
COMMIT;

-- ----------------------------
-- Table structure for sequence_3
-- ----------------------------
DROP TABLE IF EXISTS `sequence_3`;
CREATE TABLE `sequence_3` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sharding_key` bigint NOT NULL COMMENT '分库分表键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'name',
  `value` bigint DEFAULT NULL COMMENT 'value',
  `gmt_modified` datetime DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name_key` (`name`) USING BTREE COMMENT '唯一name key'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='sequence表';

-- ----------------------------
-- Records of sequence_3
-- ----------------------------
BEGIN;
INSERT INTO `sequence_3` (`id`, `sharding_key`, `name`, `value`, `gmt_modified`) VALUES (1, 0, 'user_seq', 18000, '2023-07-04 10:23:23');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
