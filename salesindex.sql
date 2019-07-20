/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.100.76 
 Source Server Type    : MySQL
 Source Server Version : 50722
 Source Host           : 192.168.100.76:9002
 Source Schema         : bigdata

 Target Server Type    : MySQL
 Target Server Version : 50722
 File Encoding         : 65001
INSERT INTO ``(`waybill_id`, `primary_sector`, `secondary_sector`, `tertiary_sector`, `four_sector`, `sales_departments`, `departments_grade`, `organize_code`, `salesman`, `position`, `customer_code`, `customer_name`, `second_name`, `client_type`, `originating_node`, `destination`, `waybill_type`, `open_bill_time`, `sign_time`, `client_grade`, `service_type`, `settlement_type`, `take_goods_fee`, `transport_fee`, `delivery_fee`, `install_fee`, `insurance_fee`, `upstairs_fee`, `entry_home_fee`, `door_fee`, `exceed_square_fee`, `wooden_fee`, `exceed_area_fee`, `large_extra_fee`, `marble_fee`, `other_fee`, `collection_fee`, `foreight_fee`, `coupon_relief`, `gift_relief`, `cash_relief`, `out_deliver_cost`, `client_fee`, `payment_period`, `billing_income`) VALUES ('1zt0000185798', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '无人认领', NULL, 'KH2018068308', '中源物流', '', '标准', '总部支装业务开单组', '山东省青岛市市南区', '正常运单', '2018-10-07 17:28:00.000', '2018-10-11 10:40:07.000', 'D类客户', '同城配送并安装', '月结', 0.00, 0.00, 15.00, -5.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, NULL, NULL, '60', 160.00);
INSERT INTO ``(`waybill_id`, `primary_sector`, `secondary_sector`, `tertiary_sector`, `four_sector`, `sales_departments`, `departments_grade`, `organize_code`, `salesman`, `position`, `customer_code`, `customer_name`, `second_name`, `client_type`, `originating_node`, `destination`, `waybill_type`, `open_bill_time`, `sign_time`, `client_grade`, `service_type`, `settlement_type`, `take_goods_fee`, `transport_fee`, `delivery_fee`, `install_fee`, `insurance_fee`, `upstairs_fee`, `entry_home_fee`, `door_fee`, `exceed_square_fee`, `wooden_fee`, `exceed_area_fee`, `large_extra_fee`, `marble_fee`, `other_fee`, `collection_fee`, `foreight_fee`, `coupon_relief`, `gift_relief`, `cash_relief`, `out_deliver_cost`, `client_fee`, `payment_period`, `billing_income`) VALUES ('1zt0000185947', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '无人认领', NULL, 'KH2018068308', '中源物流', '', '标准', '总部支装业务开单组', '湖南省娄底市娄星区', '正常运单', '2018-09-28 14:47:00.000', '2018-09-29 20:11:11.000', 'D类客户', '同城配送并安装', '月结', 0.00, 0.00, 12.00, -30.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, NULL, NULL, '60', 510.00);
INSERT INTO ``(`waybill_id`, `primary_sector`, `secondary_sector`, `tertiary_sector`, `four_sector`, `sales_departments`, `departments_grade`, `organize_code`, `salesman`, `position`, `customer_code`, `customer_name`, `second_name`, `client_type`, `originating_node`, `destination`, `waybill_type`, `open_bill_time`, `sign_time`, `client_grade`, `service_type`, `settlement_type`, `take_goods_fee`, `transport_fee`, `delivery_fee`, `install_fee`, `insurance_fee`, `upstairs_fee`, `entry_home_fee`, `door_fee`, `exceed_square_fee`, `wooden_fee`, `exceed_area_fee`, `large_extra_fee`, `marble_fee`, `other_fee`, `collection_fee`, `foreight_fee`, `coupon_relief`, `gift_relief`, `cash_relief`, `out_deliver_cost`, `client_fee`, `payment_period`, `billing_income`) VALUES ('1zt0000239225', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '无人认领', NULL, 'KH2018068308', '中源物流', '', '标准', '总部支装业务开单组', '江西省南昌市东湖区', '正常运单', '2018-10-07 17:16:00.000', '2018-10-12 21:03:59.000', 'D类客户', '同城配送并安装', '月结', 0.00, 0.00, 12.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, NULL, NULL, '60', 150.00);

 Date: 20/07/2019 18:27:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for salesindex
-- ----------------------------
DROP TABLE IF EXISTS `salesindex`;
CREATE TABLE `salesindex`  (
  `waybill_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '运单号',
  `primary_sector` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '一级部门',
  `secondary_sector` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '二级部门',
  `tertiary_sector` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '三级部门',
  `four_sector` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '四级部门',
  `sales_departments` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '部门',
  `departments_grade` int(11) NULL DEFAULT NULL COMMENT '部门等级',
  `organize_code` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '部门编号',
  `salesman` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '业务员',
  `position` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '职位',
  `customer_code` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '客户编号',
  `customer_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '客户名称',
  `second_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '二级商家',
  `client_type` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '客户价格类型(标准/非标准)',
  `originating_node` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '始发网点',
  `destination` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '目的地',
  `waybill_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '运单类型:运单，售后运单',
  `open_bill_time` timestamp(3) NULL DEFAULT NULL COMMENT '开单时间',
  `sign_time` timestamp(3) NULL DEFAULT NULL COMMENT '签收时间',
  `client_grade` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '客户等级',
  `service_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '服务类型',
  `settlement_type` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '结算方式',
  `take_goods_fee` double(16, 2) NULL DEFAULT NULL COMMENT '揽货费',
  `transport_fee` double(16, 2) NULL DEFAULT NULL COMMENT '运输费',
  `delivery_fee` double(16, 2) NULL DEFAULT NULL COMMENT '基础送货费',
  `install_fee` double(16, 2) NULL DEFAULT NULL COMMENT '安装费',
  `insurance_fee` double(16, 2) NULL DEFAULT NULL COMMENT '保价费',
  `upstairs_fee` double(16, 2) NULL DEFAULT NULL COMMENT '上楼费',
  `entry_home_fee` double(16, 2) NULL DEFAULT NULL COMMENT '入户费',
  `door_fee` double(16, 2) NULL DEFAULT NULL COMMENT '上门服务费',
  `exceed_square_fee` double(16, 2) NULL DEFAULT NULL COMMENT '超方费',
  `wooden_fee` double(16, 2) NULL DEFAULT NULL COMMENT '木架费',
  `exceed_area_fee` double(16, 2) NULL DEFAULT NULL COMMENT '超区费',
  `large_extra_fee` double(16, 2) NULL DEFAULT NULL COMMENT '大票货附加费',
  `marble_fee` double(16, 2) NULL DEFAULT NULL COMMENT '大理石费',
  `other_fee` double(16, 2) NULL DEFAULT NULL COMMENT '其他费',
  `collection_fee` double(16, 2) NULL DEFAULT NULL COMMENT '代收货款',
  `foreight_fee` double(16, 2) NULL DEFAULT NULL COMMENT '到付运费',
  `coupon_relief` double(16, 2) NULL DEFAULT NULL COMMENT '优惠券',
  `gift_relief` double(16, 2) NULL DEFAULT NULL COMMENT '赠送金',
  `cash_relief` double(16, 2) NULL DEFAULT NULL COMMENT '返现金',
  `out_deliver_cost` double(16, 2) NULL DEFAULT NULL COMMENT '外发成本',
  `client_fee` double(16, 2) NULL DEFAULT NULL COMMENT '客户物流费用',
  `payment_period` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '账期',
  `billing_income` double(16, 2) NULL DEFAULT NULL COMMENT '开单收入',
  INDEX `idx_salesindex_customer_code`(`customer_code`) USING BTREE,
  INDEX `idx_salesindex_client_grade`(`client_grade`) USING BTREE,
  INDEX `idx_salesindex_settlement_type`(`settlement_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '中间结果表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
