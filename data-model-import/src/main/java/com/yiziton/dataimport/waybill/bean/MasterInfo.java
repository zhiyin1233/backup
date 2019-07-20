package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MasterInfo {
    private String id;

    private String waybillId;

    private String masterName;

    private String masterNode;

    private String masterPhone;

    private Integer masterType;

    private String masterCode;

    private Integer status;

    private Timestamp limitAppointmentTime;

    private Timestamp actualAppoinmentTime;

    private Timestamp limitDoorTime;

    private Timestamp actualDoorTime;

    private Timestamp limitSignTime;

    private Timestamp actualSignTime;

    private Timestamp createTime;

    private Timestamp updateTime;

    private Timestamp actualDistributeTime;

    private Timestamp limitDistributeTime;

    private Integer dataType;

    private String nodeCode;

    private Integer nodeType;

    private String nodeDutyName;

    private Timestamp trunkEndTime;

    private Integer distributionType;

}