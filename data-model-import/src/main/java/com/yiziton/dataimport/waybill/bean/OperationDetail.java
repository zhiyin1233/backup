package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class OperationDetail {
    private String id;

    private String waybillId;

    private String milestoneId;

    private String field;

    private String val;

    private Integer status;

    private Timestamp createTime;

    private Timestamp updateTime;

    private Integer dataType;
}