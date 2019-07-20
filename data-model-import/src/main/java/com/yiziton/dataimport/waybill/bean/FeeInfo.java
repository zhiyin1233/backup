package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class FeeInfo {
    private String id;

    private String waybillId;

    private String billingId;

    private Double fee;

    private String feeType;

    private Integer status;

    private Timestamp createTime;

    private Timestamp updateTime;

    private Integer dataType;

    private Integer accounting;
}