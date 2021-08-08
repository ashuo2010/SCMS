/*
 Navicat Premium Data Transfer

 Source Server         : AShuo
 Source Server Type    : MySQL
 Source Server Version : 50734
 Source Host           : localhost:3306
 Source Schema         : scms

 Target Server Type    : MySQL
 Target Server Version : 50734
 File Encoding         : 65001

 Date: 06/06/2021 21:40:16
*/
CREATE DATABASE scms;
USE scms;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for athlete
-- ----------------------------
DROP TABLE IF EXISTS `athlete`;
CREATE TABLE `athlete`  (
  `athlete_id` int(11) NOT NULL AUTO_INCREMENT,
  `u_id` int(11) NULL DEFAULT NULL,
  `i_id` int(11) NULL DEFAULT NULL,
  `status` int(11) NULL DEFAULT NULL,
  `sign_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`athlete_id`) USING BTREE,
  INDEX `u_id`(`u_id`) USING BTREE,
  INDEX `i_id`(`i_id`) USING BTREE,
  CONSTRAINT `athlete_ibfk_1` FOREIGN KEY (`u_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `athlete_ibfk_2` FOREIGN KEY (`i_id`) REFERENCES `item` (`item_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 78 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of athlete
-- ----------------------------

-- ----------------------------
-- Table structure for item
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item`  (
  `item_id` int(11) NOT NULL AUTO_INCREMENT,
  `u_id` int(11) NULL DEFAULT NULL,
  `item_name` varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `item_unit` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `item_place` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `item_sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `athlete_amount` int(11) NULL DEFAULT NULL,
  `start_time` datetime NULL DEFAULT NULL,
  `end_time` datetime NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `edit_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`item_id`) USING BTREE,
  INDEX `u_id`(`u_id`) USING BTREE,
  INDEX `item_name`(`item_name`) USING BTREE,
  CONSTRAINT `item_ibfk_1` FOREIGN KEY (`u_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of item
-- ----------------------------

-- ----------------------------
-- Table structure for mainmenu
-- ----------------------------
DROP TABLE IF EXISTS `mainmenu`;
CREATE TABLE `mainmenu`  (
  `mainmenu_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`mainmenu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 801 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mainmenu
-- ----------------------------
INSERT INTO `mainmenu` VALUES (100, '团体信息管理', '/team', 1);
INSERT INTO `mainmenu` VALUES (200, '用户信息管理', '/user', 1);
INSERT INTO `mainmenu` VALUES (300, '参赛项目管理', '/item', 1);
INSERT INTO `mainmenu` VALUES (400, '参赛运动员管理', '/athlete', 1);
INSERT INTO `mainmenu` VALUES (500, '参赛成绩管理', '/score', 0);
INSERT INTO `mainmenu` VALUES (600, '总分排名管理', '/ranking', 0);
INSERT INTO `mainmenu` VALUES (700, '系统管理', '/syslog', 1);
INSERT INTO `mainmenu` VALUES (800, '参赛项目', '/athleteItem', 3);

-- ----------------------------
-- Table structure for ranking
-- ----------------------------
DROP TABLE IF EXISTS `ranking`;
CREATE TABLE `ranking`  (
  `ranking_id` int(11) NOT NULL AUTO_INCREMENT,
  `i_id` int(11) NULL DEFAULT NULL,
  `u_id` int(11) NULL DEFAULT NULL,
  `t_id` int(11) NULL DEFAULT NULL,
  `rank` int(11) UNSIGNED ZEROFILL NULL DEFAULT NULL,
  `edit_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`ranking_id`) USING BTREE,
  INDEX `i_id`(`i_id`) USING BTREE,
  INDEX `u_id`(`u_id`) USING BTREE,
  INDEX `t_id`(`t_id`) USING BTREE,
  CONSTRAINT `ranking_ibfk_1` FOREIGN KEY (`i_id`) REFERENCES `item` (`item_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ranking_ibfk_2` FOREIGN KEY (`u_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ranking_ibfk_3` FOREIGN KEY (`t_id`) REFERENCES `team` (`team_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 160 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ranking
-- ----------------------------

-- ----------------------------
-- Table structure for score
-- ----------------------------
DROP TABLE IF EXISTS `score`;
CREATE TABLE `score`  (
  `score_id` int(11) NOT NULL AUTO_INCREMENT,
  `i_id` int(11) NULL DEFAULT NULL,
  `u_id` int(11) NULL DEFAULT NULL,
  `score` int(255) NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `edit_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`score_id`) USING BTREE,
  INDEX `u_id`(`u_id`) USING BTREE,
  INDEX `i_id`(`i_id`) USING BTREE,
  CONSTRAINT `score_ibfk_1` FOREIGN KEY (`u_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `score_ibfk_2` FOREIGN KEY (`i_id`) REFERENCES `item` (`item_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 61 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of score
-- ----------------------------

-- ----------------------------
-- Table structure for submenu
-- ----------------------------
DROP TABLE IF EXISTS `submenu`;
CREATE TABLE `submenu`  (
  `submenu_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `mid` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`submenu_id`) USING BTREE,
  INDEX `mid`(`mid`) USING BTREE,
  CONSTRAINT `submenu_ibfk_1` FOREIGN KEY (`mid`) REFERENCES `mainmenu` (`mainmenu_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 803 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of submenu
-- ----------------------------
INSERT INTO `submenu` VALUES (101, '运动团体', '/teamlist', 100, 1);
INSERT INTO `submenu` VALUES (201, '用户列表', '/userlist', 200, 1);
INSERT INTO `submenu` VALUES (301, '参赛项目', '/itemlist', 300, 1);
INSERT INTO `submenu` VALUES (401, '参赛运动员', '/athletelist', 400, 1);
INSERT INTO `submenu` VALUES (501, '项目参赛录入', '/scorelist', 500, 2);
INSERT INTO `submenu` VALUES (502, '项目参赛成绩', '/athletescorelist', 500, 0);
INSERT INTO `submenu` VALUES (601, '个人总分排名', '/personranking', 600, 0);
INSERT INTO `submenu` VALUES (602, '团体总分排名', '/teamranking', 600, 0);
INSERT INTO `submenu` VALUES (701, '系统操作日志', '/sysloglist', 700, 1);
INSERT INTO `submenu` VALUES (702, '重置系统', '/systemreset', 700, 1);
INSERT INTO `submenu` VALUES (801, '参赛项目报名', '/signitem', 800, 3);
INSERT INTO `submenu` VALUES (802, '个人参赛项目', '/myitem', 800, 3);

-- ----------------------------
-- Table structure for syslog
-- ----------------------------
DROP TABLE IF EXISTS `syslog`;
CREATE TABLE `syslog`  (
  `syslog_id` int(11) NOT NULL AUTO_INCREMENT,
  `execution_time` datetime NULL DEFAULT NULL,
  `execution_uid` int(11) NULL DEFAULT NULL,
  `method` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `parameter` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  PRIMARY KEY (`syslog_id`) USING BTREE,
  INDEX `execution_uid`(`execution_uid`) USING BTREE,
  CONSTRAINT `syslog_ibfk_1` FOREIGN KEY (`execution_uid`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 451 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of syslog
-- ----------------------------

-- ----------------------------
-- Table structure for team
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team`  (
  `team_id` int(11) NOT NULL AUTO_INCREMENT,
  `team_name` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `edit_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`team_id`) USING BTREE,
  UNIQUE INDEX `team_name`(`team_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of team
-- ----------------------------
INSERT INTO `team` VALUES (1, '超级管理员', '2021-08-08 21:17:52', '2021-08-08 21:17:52');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_no` varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `nickname` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `username` varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `user_sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `user_type` int(11) NOT NULL DEFAULT 1,
  `t_id` int(11) NOT NULL,
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `edit_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `user_no`(`user_no`) USING BTREE,
  INDEX `t_id`(`t_id`) USING BTREE,
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`t_id`) REFERENCES `team` (`team_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 98 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '17140809011', 'AShuo', 'ashuo', 'e10adc3949ba59abbe56e057f20f883e', '男', 1, 1, '1772726XXXX', '2021-06-15 21:17:52', '2021-06-15 21:17:52');

SET FOREIGN_KEY_CHECKS = 1;
