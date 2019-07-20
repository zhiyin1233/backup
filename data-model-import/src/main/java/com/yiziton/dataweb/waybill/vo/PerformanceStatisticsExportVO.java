package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * 业绩统计报表
 * Created by ouweijian on 2018-12-05 17:56:07.
 */
@Data
public class PerformanceStatisticsExportVO {

    /**
     * 运单号
     */
    private String waybillId;
    /**
     * 开单时间
     */
    private String openBillTime;
    /**
     * 开单网点
     */
    private String openBillNode;
    /**
     * 产品类型
     */
    private String productType;
    /**
     * 付款方式
     */
    private String paymentType;
    /**
     * 服务类型
     */
    private String serviceType;
    /**
     * 声明价值
     */
    private String statementValue;
    /**
     * 制单人(开单员)
     */
    private String openBillOperator;
    /**
     * 运单状态
     */
    private String waybillStatus;
    /**
     * 运单来源
     */
    private String channelSource;
    /**
     * 总费用(销售价)
     */
    private Double saleTotalPrice;
    /**
     * 备注
     */
    private String openBillRemark;

    /*-----------------货物相关-------------------*/
    /**
     * 货物类型
     */
    private String goodsType;
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

    /*-----------------发货人-------------------*/
    /**
     * 商家类型(商家标识)
     */
    private String merchantType;
    /**
     * 二级商家名称
     */
    private String secondMerchantName;
    /**
     * 发货人
     */
    private String consignorName;
    /**
     * 发货人手机
     */
    private String consignorMobile;
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


    /*-----------------节点时间相关-------------------*/
    /**
     * 签收时间
     */
    private String signTime;
    /**
     * 签收人
     */
    private String signName;

    /*-----------------费用相关-------------------*/
    /**
     * 运费
     */
    private Double transportFee;
    /**
     * 开单送货费
     */
    private Double deliveryFee;
    /**
     * 保价费
     */
    private Double insuranceFee;
    /**
     * 安装费
     */
    private Double installFee;
    /**
     * 上楼费
     */
    private Double upstairsFee;
    /**
     * 其他费
     */
    private Double otherFee;
//    /**
//     * 揽货费
//     */
//    private Double takeGoodsFee;
//    /**
//     * 时效服务费
//     */
//    private Double agingServiceFee;
//    /**
//     * 上门服务费
//     */
//    private Double doorFee;
//    /**
//     * 入户费
//     */
//    private Double entryHomeFee;
//    /**
//     * 大理石费
//     */
//    private Double marbleFee;
//    /**
//     * 干支促销费
//     */
//    private Double promotionFee;
//    /**
//     * 超区费
//     */
//    private Double exceedAreaFee;
//    /**
//     * 超方费
//     */
//    private Double exceedSquareFee;
//    /**
//     * 优惠券减免费用
//     */
//    private Double couponRelief;
//    /**
//     * 赠送金减免费用
//     */
//    private Double giftRelief;
//    /**
//     * 返现金减免费用
//     */
//    private Double cashRelief;
    /**
     * 开单合计
     */
    private Double totalFee;
}
