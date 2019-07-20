package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * 运力管理部-整车业务毛利率 VO
 */
@Data
public class VehicleBusinessGrossProfitRateVO {
    /**
     * 客户类型
     */
    private String customerLabel;
    /**
     * 发货人名称
     */
    private String consignorName;
    /**
     * 当天开单总金额(元)
     */
    private String todayTotalPrice;
    /**
     * 当天异常收入(元)
     */
    private String todayAbnIncome;
    /**
     * 当天干线外发成本(元)
     */
    private String todayOutCost;
    /**
     * 当天异常成本(元)
     */
    private String todayAbnCost;
    /**
     * 当天总收入(元)
     */
    private String todayIncome;
    /**
     * 当天总成本(元)
     */
    private String todayCost;
    /**
     * 当天毛利率(%)
     */
    private String todayGrossProfitRate;
    /**
     * 当月开单总金额(元)
     */
    private String monthTotalPrice;
    /**
     * 当月异常收入(元)
     */
    private String monthAbnIncome;
    /**
     * 当月干线外发成本(元)
     */
    private String monthOutCost;
    /**
     * 当月异常成本(元)
     */
    private String monthAbnCost;
    /**
     * 当月总收入(元)
     */
    private String monthIncome;
    /**
     * 当月总成本(元)
     */
    private String monthCost;
    /**
     * 当月毛利率(%)
     */
    private String monthGrossProfitRate;
}