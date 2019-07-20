package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BillRelationInfo {
    private String id;

    private String waybillId;

    private String customerBillId;

    private String orderBillId;

    private Integer status;

    private Timestamp createTime;

    private Timestamp updateTime;

    private Integer dataType;

}