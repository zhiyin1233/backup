package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * @Description: 标准利润VO
 * @Author: kdh
 * @Date: 2018/12/5
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Data
public class StandardProfitExportVO {

    private String waybillId;//运单号
    private String openBillNode;//开单网点
    private String openBillTime;//开单时间
    private String originatingNode;//始发节点
    private String distributionTime;//分配时间
    private String consignorName;//发货人
    private String secondCustomer;//商家名称
    private String customerType;//商家类型
    private String paymentType;//付款方式
    private String settlementType;//结算方式
    private String serviceType;//服务类型
    private String province;//目的省
    private String city;//目的市
    private String area;//目的区
    private String address;//详细地址
    private String goodsType;//货物类型
    private String goodsName;//品名
    private Double totalVolume;//总体积
    private Integer packingTotalCount;//包装总件数
    private String signTime;//签收时间
    private String salesman;//业务员
    private String waybillStatus;//运单状态

    private Double consignorTransportFee = 0.00;//运费
    private Double deliveryFee = 0.00;//开单送货费
    private Double statementValue = 0.00;//声明价值
    private Double insuranceFee = 0.00;//保价费
    private Double openBillInstallFee = 0.00;//安装费
    private Double upstairsFee = 0.00;//上楼费
    private Double takeGoodsFee = 0.00;//揽货费
    private Double otherFee = 0.00;//其他费
    private Double agingServiceFee = 0.00;//时效服务费
    private Double doorFee = 0.00;//上门服务费
    private Double entryHomeFee = 0.00;//入户费
    private Double marbleFee = 0.00;//大理石费
    private Double promotionFee = 0.00;//干支促销费
    private Double exceedAreaFee = 0.00;//超区费
    private Double exceedSquareFee = 0.00;//超方费
    private Double couponRelief = 0.00;//优惠卷减免费用
    private Double giftRelief = 0.00;//赠送金减免费用
    private Double cashRelief = 0.00;//返现金减免
    private Double collectionServiceFee = 0.00;//代收货款手续费
    private Double woodenFee = 0.00;//打木架费
    private Double largeExtraFee = 0.00;//大票附加费
    private Double totalOpenBillFee = 0.00;//开单合计

    private Double totalConsignorAbnFee = 0.00;//商家承担异常费合计
    private Double totalMasterAbnFee = 0.00;//服务商责任异常费合计
    private Double totalCarrierAbnFee = 0.00;//承运商责任异常费合计
    private Double addFee = 0.00;//追赔收入
    private Double totalIncome = 0.00;//总收入合计

    private Double pickUpNodeFee = 0.00;//提点费，计算在 总收入合计中

    private Double outDeliverCost = 0.00;//外发成本
    private String carrierName;//承运商
    private Double masterInstallFee = 0.00;//安装费成本
    private Double masterTransportFee = 0.00;//支线费成本
    private String masterName;//安装师傅
    private Double totalInstallAbnFee = 0.00;//安装异常费合计
    private Double repairFee = 0.00;//维修费(异常维修（必须正数）+正常维修（必须正数）)
    private Double backFee = 0.00;//返货费(异常返货费（必须正数）+正常返货费（必须正数）)
    private Double claimFee = 0.00;//理赔费用
    private Double abnPraiseFee = 0.00;//师傅好评
    private Double abnPartialRefundFee = 0.00;//商家部分退款
    private Double totalCost = 0.00;//总成本合计

    private Double grossProfit = 0.00;//毛利
    private Double saleTotalPrice = 0.00;//销售总价
}
