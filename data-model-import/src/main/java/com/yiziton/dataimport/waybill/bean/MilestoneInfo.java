package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MilestoneInfo {
    private String id;

    private String waybillId;

    private String relatedBillId;

    private Integer operation;

    private Integer operationType;

    private Timestamp operateTime;

    private String operator;

    private String operationSys;

    private String operationOganization;

    private Timestamp nextNodeRestictTime;

    private Timestamp sentTime;

    private String messageFrom;

    private String apiName;

    private Integer status;

    private Timestamp createTime;

    private Timestamp updateTime;

    private Integer dataType;
}