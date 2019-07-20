package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * Created by administrator
 */
@Data
public class WaybillInstallDeliveryFeeVO {

    /**
     * 客户类
     *
     */
    private String customerLabel;
    /**
     *运单号
     *
     */
    private String id;
    /**
     *发货人编号
     *
     */
    private String code;
    /**
     *发货人名称
     *
     */
    private String name;
    /**
     *发货人手机'
     *
     */
    private String mobile;
    /**
     *二级商家名称
     *
     */
    private String secondName;
    /**
     *商家类型
     *
     */
    private String customerType;
    /**
     *服务类型
     *
     */
    private String serviceType;
    /**
     *安装费
     *
     */
    private String installFee;
    /**
     *送货费
     *
     */
    private String deliveryFee;
    /**
     *产品类型
     *
     */
    private String productType;
    /**
     *增值服务类型
     *
     */
    private String incrementServiceType;
    /**
     *增值服务费
     *
     */
    private String incrementServiceFee;
    /**
     *运单状态
     *
     */
    private String waybillStatus;
    /**
     *核销类型
     *
     */
    private String checkType;
    /**
     *核销方式
     *
     */
    private String checkMethod;
    /**
     *核销编码
     *
     */
    private String checkCode;
    /**
     *核销状态
     *
     */
    private String checkStatus;
    /**
     *核销单号
     *
     */
    private String checkBillId;
    /**
     *付款方式
     *
     */
    private String paymentType;
    /**
     *渠道来源
     *
     */
    private String channelSource;
    /**
     *结算方式
     *
     */
    private String settlementType;
    /**
     *货品信息
     *
     */
    private String goodsName;
    /**
     *大理石数
     *
     */
    private String marbleBlocks;
    /**
     *总体积
     *
     */
    private String totalVolume;
    /**
     *总包装件数
     *
     */
    private String totalPackingItems;
    /**
     *总安装件数
     *
     */
    private String totalInstallItems;
    /**
     *总重量
     *
     */
    private String totalWeight;
    /**
     *声明价值
     *
     */
    private String statementValue;
    /**
     *开单网点
     *
     */
    private String openBillNode;
    /**
     *开单人员
     *
     */
    private String openBillOperator;
    /**
     *销售总价
     *
     */
    private String saleTotalPrice;
    /**
     *业务员
     *
     */
    private String salesman;
    /**
     *业务手机
     *
     */
    private String salesmanPhone;
    /**
     *开单时间
     *
     */
    private String openBillTime;
    /**
     *签收时间
     *
     */
    private String signTime;
    /**
     *目的地
     *
     */
    private String destination;
    /**
     *收货人名称
     *
     */
    private String consigneeName;
    /**
     *收货人手机
     *
     */
    private String consigneeMobile;
    /**
     *收货人地址
     *
     */
    private String address;
    /**
     *是否有电梯
     *
     */
    private String elevator;
    /**
     *楼层
     *
     */
    private String floor;
    /**
     *省
     *
     */
    private String province;
    /**
     *市
     *
     */
    private String city;
    /**
     *区
     *
     */
    private String area;
    /**
     *师傅手机号
     *
     */
    private String masterPhone;
    /**
     *师傅名称
     *
     */
    private String masterName;
    /**
     *师傅所属网点=末端网点
     *
     */
    private String masterNode;
    /**
     *提货目的地
     *
     */
    private String arrivalArea;
    /**
     *提货区域
     *
     */
    private String arrivalAddress;
    /**
     *提货电话
     *
     */
    private String pickUpGoodsPhone;
    /**
     *提货密码
     *
     */
    private String pickUpGoodsPassword;
    /**
     *物流单号
     *
     */
    private String logisticsBillId;
    /**
     *承运
     *
     */
    private String carrier;
}
