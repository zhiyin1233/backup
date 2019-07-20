package com.yiziton.dataweb.waybill.bean;

import java.util.List;

/**
 * @author wanglianbo
 * @date 2018-12-05 11:52
 */
public class MilestoneInfo {

    private String id;

    private String waybillId;

    private String relatedBillId;

    private String operateTime;

    private Integer operationType;

    private String operationTypeVal;

    private Integer operation;

    private String operationVal;

    private String operator;

    private String operationOganization;

    private String operationSys;

    private List<OperationDetailInfo> operationDetailInfoList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(String waybillId) {
        this.waybillId = waybillId;
    }

    public String getRelatedBillId() {
        return relatedBillId;
    }

    public void setRelatedBillId(String relatedBillId) {
        this.relatedBillId = relatedBillId;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }


    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public String getOperationTypeVal() {
        return operationTypeVal;
    }

    public void setOperationTypeVal(String operationTypeVal) {
        this.operationTypeVal = operationTypeVal;
    }

    public String getOperationVal() {
        return operationVal;
    }

    public void setOperationVal(String operationVal) {
        this.operationVal = operationVal;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperationOganization() {
        return operationOganization;
    }

    public void setOperationOganization(String operationOganization) {
        this.operationOganization = operationOganization;
    }

    public String getOperationSys() {
        return operationSys;
    }

    public void setOperationSys(String operationSys) {
        this.operationSys = operationSys;
    }

    public List<OperationDetailInfo> getOperationDetailInfoList() {
        return operationDetailInfoList;
    }

    public void setOperationDetailInfoList(List<OperationDetailInfo> operationDetailInfoList) {
        this.operationDetailInfoList = operationDetailInfoList;
    }
}
