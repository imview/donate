/*
Navicat MySQL Data Transfer

Source Server         : donate_init
Source Server Version : 50714
Source Host           : 172.17.22.41:4306
Source Database       : 

Target Server Type    : MYSQL
Target Server Version : 50714
File Encoding         : 65001

Date: 2017-10-26 16:50:28
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for donate
-- ----------------------------
DROP TABLE IF EXISTS `donate`;
CREATE TABLE `donate` (
  `id` varchar(50) NOT NULL,
  `amount` decimal(18,2) NOT NULL,
  `user_name` varchar(50) DEFAULT NULL,
  `mobile` varchar(50) DEFAULT NULL,
  `class` varchar(50) DEFAULT NULL,
  `period` varchar(50) DEFAULT NULL,
  `open_id` varchar(50) NOT NULL,
  `nick_name` varchar(300) NOT NULL,
  `head_img_url` varchar(400) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for log_interface
-- ----------------------------
DROP TABLE IF EXISTS `log_interface`;
CREATE TABLE `log_interface` (
  `id` varchar(50) NOT NULL,
  `open_id` varchar(50) DEFAULT NULL,
  `wx_order_no` varchar(50) DEFAULT NULL,
  `url` varchar(400) NOT NULL,
  `request` varchar(4000) NOT NULL,
  `response` varchar(4000) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for ship
-- ----------------------------
DROP TABLE IF EXISTS `ship`;
CREATE TABLE `ship` (
  `mmsi` varchar(20) NOT NULL,
  `ship_name` varchar(50) DEFAULT NULL,
  `longitude` varchar(30) DEFAULT NULL,
  `latitude` varchar(30) DEFAULT NULL,
  `course` varchar(30) DEFAULT NULL,
  `heading` varchar(30) DEFAULT NULL,
  `speed` varchar(20) DEFAULT NULL,
  `postime` varchar(50) DEFAULT NULL,
  `length` varchar(10) DEFAULT NULL,
  `breadth` varchar(10) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`mmsi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `open_id` varchar(50) NOT NULL,
  `nick_name` varchar(300) NOT NULL,
  `sex` int(11) NOT NULL COMMENT '值为1时是男性，值为2时是女性，值为0时是未知',
  `province` varchar(50) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL COMMENT '如中国为CN',
  `head_img_url` varchar(4000) DEFAULT NULL COMMENT '最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。',
  `privilege` varchar(4000) DEFAULT NULL COMMENT 'json 数组，如微信沃卡用户为（chinaunicom）',
  `union_id` varchar(50) DEFAULT NULL,
  `raw_head_img_url` varchar(4000) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for wx_order
-- ----------------------------
DROP TABLE IF EXISTS `wx_order`;
CREATE TABLE `wx_order` (
  `no` varchar(50) NOT NULL,
  `donate_id` varchar(50) DEFAULT NULL,
  `return_code` varchar(50) NOT NULL,
  `return_msg` varchar(200) NOT NULL,
  `status` int(11) NOT NULL COMMENT '0 待处理 1成功 2失败',
  `prepay_id` varchar(100) NOT NULL,
  `is_subscribe` int(11) DEFAULT NULL COMMENT '0否1是',
  `pay_time` datetime DEFAULT NULL,
  `transaction_id` varchar(50) DEFAULT NULL COMMENT '微信订单号',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
