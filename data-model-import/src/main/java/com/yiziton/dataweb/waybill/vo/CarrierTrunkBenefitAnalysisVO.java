package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * 干线效益分析-承运商 VO
 */
@Data
public class CarrierTrunkBenefitAnalysisVO {
    /**
     * 客户类型
     */
    private String carrier;
    /**
     * 运费收入
     */
    private String transportFee;
    /**
     * 外发成本
     */
    private String outDeliverCost;
    /**
     * 毛利率(%)
     */
    private String profitRate;
}