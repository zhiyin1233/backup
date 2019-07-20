package com.yiziton.dataweb.waybill.vo.report_vo;

import lombok.Data;

/**客户利润报表VO*/
@Data
public class CustomerProfitReportsVO {
    /**发货人*/
    private String consignorName;
    /**当天总收入*/
    private String todayIncome;
    /**当天总成本*/
    private String todayCost;
    /**当天利润率*/
    private String todayProfitMargin;
    /**当月总收入*/
    private String monthIncome;
    /**当月总成本*/
    private String monthCost;
    /**当月利润率*/
    private String monthProfitMargin;

}
