package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ExceptionInfo {
    private String id;

    private String waybillId;

    private String exceptionCode;

    private String exceptionType;

    private String exceptionMessage;

    private String exceptionDescribe;

    private String feedbackSys;

    private Timestamp feedbackTime;

    private String feedback;

    private String feedbackParty;

    private String dispose;

    private String disposeResult;

    private String arbitrationResult;

    private Timestamp restrictDisposeTime;

    private Timestamp actualDisposeTime;

    private Integer status;

    private Timestamp createTime;

    private Timestamp updateTime;

    private String exceptionStatus;

    private String feedbackPhone;

    private String feedbackNode;

    private String arbitrationCode;

    private String arbitrationType;

    private String disposeNode;

    private Integer dataType;

    private String disposeRemark;

    private String exceptionLargeType;

    private String exceptionSubType;

    private String disposePhone;

    private String disposeBillId;

    private String duty;

    private String dutyAccount;

    private String isClaim;

    private String claimBillId;

    private String disposeSys;

    private String disposeObject;

}