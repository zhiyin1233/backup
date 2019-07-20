package com.yiziton.dataweb.waybill.bean;

import lombok.Data;

import java.util.List;

/**
 * @author wanglianbo
 * @date 2018-12-05 11:35
 */
@Data
public class ExceptionInfo {

    private String id;

    private String waybillId;

    private String exceptionCode;

    private String exceptionType;

    private String exceptionMessage;

    private String feedbackTime;

    private String exceptionDescribe;

    private List<String> exceptionDescribeUrlList;

    private String feedback;

    private String dispose;

    private String actualDisposeTime;

    private String disposeNode;

    private String disposeResult;

    private String disposeRemark;

    private String exceptionStatus;

    private String arbitrationType;

    private String arbitrationResult;

    private List<String> taskBillIdList;

    private String exceptionLargeType;

    private String exceptionSubType;

    private String disposeSys;

    private String disposeObject;

}
