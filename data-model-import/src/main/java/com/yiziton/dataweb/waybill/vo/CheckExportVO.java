package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * 核销报表数据
 * Created by ouweijian on 2018-12-06 16:55:30.
 */
@Data
public class CheckExportVO {

    /**
     * 运单号
     */
    private String waybillId;
    /**
     * 开单网点
     */
    private String openBillNode;
    /**
     * 开单时间
     */
    private String openBillTime;
    /**
     * 始发节点
     */
    private String originatingNode;
    /**
     * 服务类型
     */
    private String serviceType;
    /**
     * 分配人
     */
    private String distributionOperator;
    /**
     * 运单来源
     */
    private String channelSource;
    /**
     * 提货电话
     */
    private String pickUpGoodsPhone;
    /**
     * 提货地址
     */
    private String arrivalAddress;
    /**
     * 运单状态
     */
    private String waybillStatus;
    /**
     * 客户单号(第三方单号)
     */
    private String customerBillId;
    /**
     * 订单编号
     */
    private String orderBillId;
//    /**
//     * 天猫订单号状态
//      */
//    private String tmallOrderNoStatus;

    /*-----------------货物相关-------------------*/
    /**
     * 品名
     */
    private String goodsName;
    /**
     * 包装
     */
    private String goodsPacking;
    /**
     * 包装总件数
     */
    private Integer packingTotalCount;
    /**
     * 包装总体积
     */
    private Double packingTotalVolume;
    /**
     * 包装总重量
     */
    private Double packingTotalWeight;

    /*-----------------师傅信息-------------------*/
    /**
     * 安装师傅
     */
    private String masterName;
    /**
     * 安装师傅所属网点=末端网点
     */
    private String masterNode;
    /*-----------------核销相关-------------------*/
    /**
     * 核销类型
     */
    private String checkType;
    /**
     * 核销方式
     */
    private String checkMethod;
    /**
     * 核销单号
     */
    private String checkBillId;
    /**
     * 是否核销(核销状态)
     */
    private String checkStatus;

    /*-----------------节点时间相关-------------------*/
    /**
     * 干线开始时间
     */
    private String trunkStartTime;
    /**
     * 预约时间
     */
    private String appointmentTime;
    /**
     * 提货时间
     */
    private String pickUpTime;
    /**
     * 签收时间
     */
    private String signTime;
    /**
     * 分配时间
     */
    private String distributionTime;
    /**
     * 核销时间
     */
    private String checkWaybillTime;

    /*-----------------发货人-------------------*/
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

    /*-----------------收货人-------------------*/
    /**
     * 收货人
     */
    private String receiveName;
    /**
     * 收货人手机
     */
    private String receiveMobile;
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
}
