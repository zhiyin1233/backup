package com.yiziton.dataweb.waybill.vo;

import java.math.BigDecimal;

/**
 * @author wanglianbo
 * @date 2018-12-19 16:54
 */
public class WaybillDetailVO {

    private String waybillId;

    /**
     * 商家费用
     */
    private BigDecimal customerPrice;

    /**
     * 开单时间
     */
    private String doorTime;

    /**
     * 上门时段
     */
    private String doorPeriod;

    /**
     * 增值服务收款状态
     */
    private  String incrementServiceStatus;

    /**
     * 销售总价
     */
    private Double saleTotalPrice;

    /**
     * 渠道来源
     */
    private String channelSource;

    /**
     * 产品时效类型
     */
    private String productType;

    /**
     * 付款方式
     */
    private String paymentType;

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getChannelSource() {
        return channelSource;
    }

    public void setChannelSource(String channelSource) {
        this.channelSource = channelSource;
    }

    public Double getSaleTotalPrice() {
        return saleTotalPrice;
    }

    public void setSaleTotalPrice(Double saleTotalPrice) {
        this.saleTotalPrice = saleTotalPrice;
    }

    public String getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(String waybillId) {
        this.waybillId = waybillId;
    }

    public BigDecimal getCustomerPrice() {
        return customerPrice;
    }

    public void setCustomerPrice(BigDecimal customerPrice) {
        this.customerPrice = customerPrice;
    }

    public String getDoorTime() {
        return doorTime;
    }

    public void setDoorTime(String doorTime) {
        this.doorTime = doorTime;
    }

    public String getDoorPeriod() {
        return doorPeriod;
    }

    public void setDoorPeriod(String doorPeriod) {
        this.doorPeriod = doorPeriod;
    }

    public String getIncrementServiceStatus() { return incrementServiceStatus; }

    public void setIncrementServiceStatus(String incrementServiceStatus) { this.incrementServiceStatus = incrementServiceStatus; }
}
