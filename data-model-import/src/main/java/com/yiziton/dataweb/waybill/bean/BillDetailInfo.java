package com.yiziton.dataweb.waybill.bean;

import java.math.BigDecimal;

/**
 * @author wanglianbo
 * @date 2018-12-05 15:46
 */
public class BillDetailInfo {

    private String id;

    private Integer billingObject;

    private String billingName;

    private Integer billingType;

    private String transactionName;

    private BigDecimal totalPrice;

    private String relatedBillId;

    private String feeType;

    private String feeTypeCode;

    private BigDecimal fee;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getBillingObject() {
        return billingObject;
    }

    public void setBillingObject(Integer billingObject) {
        this.billingObject = billingObject;
    }

    public Integer getBillingType() {
        return billingType;
    }

    public void setBillingType(Integer billingType) {
        this.billingType = billingType;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getBillingName() {
        return billingName;
    }

    public void setBillingName(String billingName) {
        this.billingName = billingName;
    }

    public String getRelatedBillId() {
        return relatedBillId;
    }

    public void setRelatedBillId(String relatedBillId) {
        this.relatedBillId = relatedBillId;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getFeeTypeCode() {
        return feeTypeCode;
    }

    public void setFeeTypeCode(String feeTypeCode) {
        this.feeTypeCode = feeTypeCode;
    }
}
