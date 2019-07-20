package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * 标准及非标准客户毛利率 VO
 */
@Data
public class StandardAndNonStandardCustomerVO {
    /**
     * 发货人名称
     */
    private String name;
    /**
     * 标准/非标商家标记
     */
    private String customerLabel;
    /**
     * 签收单量
     */
    private String signs;
    /**
     * 支装单量
     */
    private String branchs;
    /**
     * 干支装单量
     */
    private String trunks;
    /**
     * 需末端操作单量
     */
    private String endings;
    /**
     * 运费收入
     */
    private String customerTransportFee;
    /**
     * 送货费收入
     */
    private String customerDeliveryFee;
    /**
     * 安装费收入
     */
    private String customerInstallFee;
    /**
     * 上楼费
     */
    private String customerUpstairsFee;
    /**
     * 保价费
     */
    private String customerInsuranceFee;
    /**
     * 开单收入合计
     */
    private String customerTotalPrice;
    /**
     * 异常收入合计
     */
    private String customerAddFee;
    /**
     * 外发成本
     */
    private String carrierOutDeliverCost;
    /**
     * 安装费成本
     */
    private String masterInstallFee;
    /**
     * 支线费成本
     */
    private String masterDeliveryFee;
    /**
     * 异常费用支出合计
     */
    private String abnTotal;
}