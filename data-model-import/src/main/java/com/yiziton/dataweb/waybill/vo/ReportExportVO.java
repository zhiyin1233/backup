package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

import java.util.Date;

/**
 * 报表公共导出字段
 * Created by ouweijian on 2018-12-05 15:26:52.
 */
@Data
public class ReportExportVO {

    /*---------------运单基本信息---------------*/
    /**
     * 运单号
     */
    private String waybillId;
    /**
     * 开单时间
     */
    private Date openBillTime;
    /**
     * 签收时间
     */
    private Date signTime;
    /**
     * 运单状态
     */
    private Integer waybillStatus;
    /**
     * 运单类型
     */
    private Integer waybillType;
    /**
     * 客户类型,结算方式
     */
    private Integer settlementType;
    /**
     * 服务类型
     */
    private Integer serviceType;
    /**
     * 总费用(销售价)
     */
    private Double saleTotalPrice;
    /**
     * 制单人
     */
    private String openBillOperator;
    /**
     * 备注
     */
    private String openBillRemark;
    /**
     * 物流单号
     */
    private String logisticsBillId;
    /**
     * 核销类型
     */
    private Integer checkType;
    /**
     * 核销方式
     */
    private Integer checkMethod;
    /**
     * 核销编号
     */
    private String checkCode;
    /**
     * 核销单号
     */
    private String checkBillId;
    /**
     * 核销时间
     */
    private String checkTime;
    /**
     * 是否核销
     */
    private Integer checkStatus;
    /**
     * 始发节点
     */
    private String originatingNode;
    /**
     * 产品类型
     */
    private Integer productType;
    /**
     * 开单网点
     */
    private String openBillNode;
    /**
     * 付款方式
     */
    private Integer paymentType;
    /**
     * 声明价值
     */
    private String statementValue;
    /**
     * 运单来源
     */
    private Integer channelSource;
    /**
     * 提货电话
     */
    private String pickUpGoodsPhone;
    /**
     * 提货地址
     */
    private String arrivalAddress;
    /**
     * 业务员
     */
    private String salesman;
    /**
     * 商品类型
     */
    private String goodsType;
    /**
     * 包装
     */
    private String packing;
    /**
     * 拣货时间
     */
    private String pickingTime;

    /*---------------发货人---------------*/
    /**
     * 发货人
     */
    private String consignorName;
    /**
     * 发货人手机
     */
    private String consignorMobile;
    /**
     * 二级商家名称
     */
    private String secondMerchantName;
    /**
     * 商家类型(商家标识)
     */
    private Integer merchantType;

    /*---------------收货人---------------*/
    /**
     * 收货人
     */
    private String receiveName;
    /**
     * 收货人手机
     */
    private String receiveMobile;
    /**
     * 目的地
     */
    private String destination;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String area;
    /**
     * 街道
     */
    private String street;
    /**
     * 详细地址
     */
    private String receiveAddress;

    /*---------------承运商---------------*/
    /**
     * 承运商
     */
    private String carrier;

    /*---------------师傅信息---------------*/
    /**
     * 安装师傅账号
     */
    private String masterPhone;
    /**
     * 安装师傅
     */
    private String masterName;
    /**
     * 安装师傅所属网点=末端网点
     */
    private String masterNode;
    /**
     * 师傅类型
     */
    private Integer masterType;
}
