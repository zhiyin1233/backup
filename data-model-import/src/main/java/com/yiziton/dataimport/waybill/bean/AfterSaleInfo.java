package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class AfterSaleInfo {
    private String id;

    private String waybillId;

    private String taskBillId;

    private String taskType;

    //private String taskStatus;

    private Integer status;

    private Timestamp createTime;

    private Timestamp updateTime;

    private String exceptionCode;

    private Timestamp afterSaleCreateTime;

    private Integer dataType;
}