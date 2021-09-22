/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50734
 Source Host           : localhost:3306
 Source Schema         : scms

 Target Server Type    : MySQL
 Target Server Version : 50734
 File Encoding         : 65001

 Date: 22/09/2021 10:24:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for athlete
-- ----------------------------
DROP TABLE IF EXISTS `athlete`;
CREATE TABLE `athlete`  (
  `athlete_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '运动员ID',
  `u_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `user_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '多人项目运动员ID',
  `i_id` int(11) NULL DEFAULT NULL COMMENT '项目ID',
  `score_status` int(11) NULL DEFAULT NULL COMMENT '分数是否录入(0未录入，1已录入）',
  `sign_time` datetime NULL DEFAULT NULL COMMENT '报名时间',
  PRIMARY KEY (`athlete_id`) USING BTREE,
  INDEX `u_id`(`u_id`) USING BTREE,
  INDEX `i_id`(`i_id`) USING BTREE,
  CONSTRAINT `athlete_ibfk_1` FOREIGN KEY (`u_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `athlete_ibfk_2` FOREIGN KEY (`i_id`) REFERENCES `item` (`item_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of athlete
-- ----------------------------

-- ----------------------------
-- Table structure for item
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item`  (
  `item_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `parent_id` int(11) NULL DEFAULT NULL COMMENT '父项目ID',
  `se_id` int(11) NULL DEFAULT NULL COMMENT '届时ID',
  `u_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `item_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '项目名称',
  `item_unit` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '项目分数单位',
  `item_place` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目地点',
  `item_sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目性别',
  `item_amount` int(11) NOT NULL COMMENT '项目报名规定人数',
  `athlete_amount` int(11) NULL DEFAULT NULL COMMENT '项目参赛运动员人数',
  `start_time` datetime NULL DEFAULT NULL COMMENT '项目开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '项目结束时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `edit_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`item_id`) USING BTREE,
  INDEX `u_id`(`u_id`) USING BTREE,
  INDEX `item_name`(`item_name`) USING BTREE,
  INDEX `se_id`(`se_id`) USING BTREE,
  CONSTRAINT `item_ibfk_1` FOREIGN KEY (`u_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `item_ibfk_2` FOREIGN KEY (`se_id`) REFERENCES `season` (`season_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of item
-- ----------------------------

-- ----------------------------
-- Table structure for mainmenu
-- ----------------------------
DROP TABLE IF EXISTS `mainmenu`;
CREATE TABLE `mainmenu`  (
  `mainmenu_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主菜单ID',
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主菜单标题',
  `path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主菜单URL路径',
  `type` int(11) NOT NULL COMMENT '菜单类型(0所有人可见、1管理员可见、2记分员可见、3运动员可见)',
  PRIMARY KEY (`mainmenu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1001 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mainmenu
-- ----------------------------
INSERT INTO `mainmenu` VALUES (100, '运动会管理', '/season', 1);
INSERT INTO `mainmenu` VALUES (200, '团体信息管理', '/team', 1);
INSERT INTO `mainmenu` VALUES (300, '用户信息管理', '/user', 1);
INSERT INTO `mainmenu` VALUES (400, '项目管理', '/item', 1);
INSERT INTO `mainmenu` VALUES (500, '参赛运动员管理', '/athlete', 1);
INSERT INTO `mainmenu` VALUES (600, '我的参赛项目', '/athleteItem', 3);
INSERT INTO `mainmenu` VALUES (700, '参赛成绩管理', '/score', 0);
INSERT INTO `mainmenu` VALUES (800, '总分排名管理', '/ranking', 0);
INSERT INTO `mainmenu` VALUES (900, '项目记录管理', '/record', 0);
INSERT INTO `mainmenu` VALUES (1000, '系统管理', '/syslog', 1);

-- ----------------------------
-- Table structure for ranking
-- ----------------------------
DROP TABLE IF EXISTS `ranking`;
CREATE TABLE `ranking`  (
  `ranking_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '排名ID',
  `a_id` int(11) NULL DEFAULT NULL COMMENT '运动员ID',
  `rank` int(1) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '项目分数排名',
  `edit_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`ranking_id`) USING BTREE,
  INDEX `a_id`(`a_id`) USING BTREE,
  CONSTRAINT `ranking_ibfk_1` FOREIGN KEY (`a_id`) REFERENCES `athlete` (`athlete_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ranking
-- ----------------------------

-- ----------------------------
-- Table structure for record
-- ----------------------------
DROP TABLE IF EXISTS `record`;
CREATE TABLE `record`  (
  `record_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `a_id` int(11) NULL DEFAULT NULL COMMENT '运动员ID',
  `record_score` decimal(11, 2) NOT NULL COMMENT '项目记录分数',
  `record_status` int(1) NOT NULL COMMENT '记录状态(0不可用、1可用)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `edit_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `a_id`(`a_id`) USING BTREE,
  CONSTRAINT `record_ibfk_1` FOREIGN KEY (`a_id`) REFERENCES `athlete` (`athlete_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of record
-- ----------------------------

-- ----------------------------
-- Table structure for score
-- ----------------------------
DROP TABLE IF EXISTS `score`;
CREATE TABLE `score`  (
  `score_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分数ID',
  `a_id` int(11) NULL DEFAULT NULL COMMENT '运动员ID',
  `score` decimal(11, 2) NOT NULL COMMENT '项目分数',
  `is_break_record` int(1) NOT NULL COMMENT '是否破纪录(0未破纪录、1破纪录)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `edit_time` datetime NULL DEFAULT NULL COMMENT '修改时间\r\n',
  PRIMARY KEY (`score_id`) USING BTREE,
  INDEX `a_id`(`a_id`) USING BTREE,
  CONSTRAINT `score_ibfk_1` FOREIGN KEY (`a_id`) REFERENCES `athlete` (`athlete_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of score
-- ----------------------------

-- ----------------------------
-- Table structure for season
-- ----------------------------
DROP TABLE IF EXISTS `season`;
CREATE TABLE `season`  (
  `season_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '届时ID',
  `season_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '运动会名称',
  `season_topic_desc` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '运动会主题描述',
  `season_begin_time` datetime NOT NULL COMMENT '运动会开始时间',
  `season_end_time` datetime NOT NULL COMMENT '运动会结束时间',
  `season_status` int(1) NOT NULL COMMENT '运动会状态(0结束、1举行中)',
  `season_athlete_amount` int(11) NULL DEFAULT 0 COMMENT '运动会参赛人数',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `edit_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`season_id`) USING BTREE,
  UNIQUE INDEX `season_name`(`season_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of season
-- ----------------------------

-- ----------------------------
-- Table structure for submenu
-- ----------------------------
DROP TABLE IF EXISTS `submenu`;
CREATE TABLE `submenu`  (
  `submenu_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '副菜单ID',
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '副菜单标题',
  `path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '副菜单URL路径',
  `mid` int(11) NOT NULL COMMENT '主菜单ID',
  `type` int(11) NOT NULL COMMENT '菜单类型(0所有人可见、1管理员可见、2记分员可见、3运动员可见)',
  PRIMARY KEY (`submenu_id`) USING BTREE,
  INDEX `mid`(`mid`) USING BTREE,
  CONSTRAINT `submenu_ibfk_1` FOREIGN KEY (`mid`) REFERENCES `mainmenu` (`mainmenu_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1003 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of submenu
-- ----------------------------
INSERT INTO `submenu` VALUES (101, '运动会届时', '/seasonlist', 100, 1);
INSERT INTO `submenu` VALUES (201, '团队信息', '/teamlist', 200, 1);
INSERT INTO `submenu` VALUES (301, '用户信息', '/userlist', 300, 1);
INSERT INTO `submenu` VALUES (401, '项目信息', '/itemlist', 400, 1);
INSERT INTO `submenu` VALUES (501, '参赛运动员信息', '/athletelist', 500, 1);
INSERT INTO `submenu` VALUES (601, '参赛项目报名', '/signitem', 600, 3);
INSERT INTO `submenu` VALUES (602, '个人参赛项目', '/myitem', 600, 3);
INSERT INTO `submenu` VALUES (701, '项目成绩录入', '/scorelist', 700, 2);
INSERT INTO `submenu` VALUES (702, '项目成绩信息', '/athletescorelist', 700, 0);
INSERT INTO `submenu` VALUES (801, '个人总分排名', '/personranking', 800, 0);
INSERT INTO `submenu` VALUES (802, '团体总分排名', '/teamranking', 800, 0);
INSERT INTO `submenu` VALUES (901, '项目记录信息', '/recordlist', 900, 0);
INSERT INTO `submenu` VALUES (1001, '系统操作日志', '/sysloglist', 1000, 1);
INSERT INTO `submenu` VALUES (1002, '重置系统', '/systemreset', 1000, 1);

-- ----------------------------
-- Table structure for syslog
-- ----------------------------
DROP TABLE IF EXISTS `syslog`;
CREATE TABLE `syslog`  (
  `syslog_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '系统日志ID',
  `execution_time` datetime NULL DEFAULT NULL COMMENT '执行时间',
  `execution_uid` int(11) NULL DEFAULT NULL COMMENT '执行用户ID',
  `method` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '执行方法名称',
  `parameter` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '执行参数',
  PRIMARY KEY (`syslog_id`) USING BTREE,
  INDEX `execution_uid`(`execution_uid`) USING BTREE,
  CONSTRAINT `syslog_ibfk_1` FOREIGN KEY (`execution_uid`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of syslog
-- ----------------------------

-- ----------------------------
-- Table structure for team
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team`  (
  `team_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '团体ID',
  `team_name` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '团体名称',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `edit_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`team_id`) USING BTREE,
  UNIQUE INDEX `team_name`(`team_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of team
-- ----------------------------
INSERT INTO `team` VALUES (1, '超级管理员', '2021-09-18 20:24:00', '2021-09-18 20:24:00');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_no` varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户编号',
  `nickname` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户姓名',
  `username` varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户账号',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户密码',
  `user_sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户性别',
  `user_type` int(11) NOT NULL DEFAULT 1 COMMENT '用户类型',
  `t_id` int(11) NOT NULL COMMENT '团体ID',
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `edit_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `user_no`(`user_no`) USING BTREE,
  INDEX `t_id`(`t_id`) USING BTREE,
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`t_id`) REFERENCES `team` (`team_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '17140809011', 'AShuo', 'ashuo', 'e10adc3949ba59abbe56e057f20f883e', '男', 1, 1, '1772726XXXX', '2021-09-18 20:24:00', '2021-09-18 20:24:00');

SET FOREIGN_KEY_CHECKS = 1;
