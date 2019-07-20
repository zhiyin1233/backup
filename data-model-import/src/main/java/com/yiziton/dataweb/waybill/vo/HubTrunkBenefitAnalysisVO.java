package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * 干线效益分析-枢纽 VO
 */
@Data
public class HubTrunkBenefitAnalysisVO {
    /**
     * 枢纽
     */
    private String openBillNode;
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