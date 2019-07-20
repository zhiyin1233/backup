package com.yiziton.dataweb.salesindex.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 第二期销售指标实体对象
 *
 * @author HuangHuai on 2019-07-11 15:54
 */
@Data
public class SalesIndexDO implements Serializable {
    /**
     * 运单号
     */
    public String waybill_id;

    /**
     * 揽货费
     */
    public Double take_goods_fee;

    /**
     * 运输费
     */
    public Double transport_fee;

    /**
     * 基础送货费
     */
    public Double delivery_fee;

    /**
     * 安装费
     */
    public Double install_fee;

    /**
     * 保价费
     */
    public Double insurance_fee;

    /**
     * 上楼费
     */
    public Double upstairs_fee;

    /**
     * 入户费
     */
    public Double entry_home_fee;

    /**
     * 上门服务费
     */
    public Double door_fee;

    /**
     * 超方费
     */
    public Double exceed_square_fee;

    /**
     * 木架费
     */
    public Double wooden_fee;

    /**
     * 超区费
     */
    public Double exceed_area_fee;

    /**
     * 大票货附加费
     */
    public Double large_extra_fee;

    /**
     * 大理石费
     */
    public Double marble_fee;

    /**
     * 其他费
     */
    public Double other_fee;

    /**
     * 增值服务费
     */
    public Double increment_service_fee;

    /**
     * 优惠券
     */
    public Double coupon_relief;

    /**
     * 赠送金
     */
    public Double gift_relief;

    /**
     * 返现金
     */
    public Double cash_relief;

    /**
     * 客户物流费
     */
    public Double client_fee;

    /**
     * 开单收入
     */
    public Double billing_income;

    /**
     * 部门
     */
    public String sales_departments;

    /**
     * 部门等级
     */
    public Integer departments_grade;

    /**
     * 部门编号
     */
    public String organize_code;

    /**
     * 隶属部门编号
     */
    public String parent_code;

    /**
     * 职位
     */
    public String position;

    /**
     * 业务员
     */
    public String salesman;

    /**
     * 客户名称
     */
    public String customer_name;

    /**
     * 客户名称
     */
    public String customer_code;

    /**
     * 二级商家
     */
    public String second_name;

    /**
     * 始发网点
     */
    public String originating_node;
    public String client_type;



    /**
     * 始发网点类型
     */
    public String originating_node_type;

    /**
     * 目的地
     */
    public String destination;

    /**
     * 运单类型
     */
    public String waybill_type;

    /**
     * 客户等级
     */
    public String client_grade;

    /**
     * 服务类型
     */
    public String service_type;

    /**
     * 结算方式
     */
    public String settlement_type;

    /**
     * 账期
     */
    public String payment_period;

    /**
     * 开单时间
     */
    public Date open_bill_time;

    /**
     * 签收时间
     */
    public Date sign_time;
}
