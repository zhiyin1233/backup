package com.yiziton.dataweb.waybill.vo.report_vo;

/**
 * @author: zhangSiYuan
 */
public class IncomeStatementReportsVo {
    /**
     * 发货人
     */
    private String consignorName;
    /**
     * 客户标识
     */
    private String consignorLabel;
    /**
     * 当天日期
     */
    private String today;
    /**
     * 当天日期
     */
    private String todayNum;
    /**
     * 当天开单总金额(元)
     */
    private String todayPrice;
    /**
     * 当天开单金额占比(%)
     */
    private String todayRatio;
    /**
     * 当月月份
     */
    private String month;
    /**
     * 累计当月开单总金额(元)
     */
    private String monthPrice;
    /**
     * 累计当月开单总票数
     */
    private String monthCount;
    /**
     * 累计当月开单金额占比(%)
     */
    private String monthRatio;

    public String getConsignorName() {
        return consignorName;
    }

    public void setConsignorName(String consignorName) {
        this.consignorName = consignorName;
    }

    public String getConsignorLabel() {
        return consignorLabel;
    }

    public void setConsignorLabel(String consignorLabel) {
        this.consignorLabel = consignorLabel;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public String getTodayNum() {
        return todayNum;
    }

    public void setTodayNum(String todayNum) {
        this.todayNum = todayNum;
    }

    public String getTodayPrice() {
        return todayPrice;
    }

    public void setTodayPrice(String todayPrice) {
        this.todayPrice = todayPrice;
    }

    public String getTodayRatio() {
        return todayRatio;
    }

    public void setTodayRatio(String todayRatio) {
        this.todayRatio = todayRatio;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getMonthPrice() {
        return monthPrice;
    }

    public void setMonthPrice(String monthPrice) {
        this.monthPrice = monthPrice;
    }

    public String getMonthCount() {
        return monthCount;
    }

    public void setMonthCount(String monthCount) {
        this.monthCount = monthCount;
    }

    public String getMonthRatio() {
        return monthRatio;
    }

    public void setMonthRatio(String monthRatio) {
        this.monthRatio = monthRatio;
    }
}
