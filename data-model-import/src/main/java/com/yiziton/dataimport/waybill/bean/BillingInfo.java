package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BillingInfo {
    private String id;

    private String waybillId;

    private Integer billingObject;

    private String billingName;

    private String billingPhone;

    private Integer billingType;

    private Double totalPrice;

    private String transactionName;

    private Timestamp billingTime;

    private Integer status;

    private Timestamp sentTime;

    private Timestamp createTime;

    private Timestamp updateTime;

    private String relatedBillId;

    private Integer dataType;

    private Integer accounting;

    private Integer settlementType;

}