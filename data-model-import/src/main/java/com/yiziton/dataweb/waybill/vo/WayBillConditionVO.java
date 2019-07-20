package com.yiziton.dataweb.waybill.vo;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/11/27
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class WayBillConditionVO {

    private String waybillId;

    /**
     * 运单号
     */
    private List<String> waybillIdList;

    /**
     * 增值服务类型
     */
    private String incrementServiceType;

    private Integer incrementServiceCode;

    /**
     * 发货人名称
     */
    private List<String> consignorNameList;


    /**
     * 收货人信息
     */
    private List<String> consigneeNameList;

    private String consigneeMobile;

    private String consigneeAddress;

    private List<String> secondNameList;

    private String openBillNode;

    private String serviceType;

    private Integer serviceTypeCode;

    private String checkType;
    private Integer checkTypeCode;

    private String checkMethod;
    private Integer checkMethodCode;

    private String waybillStatus;
    private Integer waybillStatusCode;

    private String customerType;
    private Integer customerTypeCode;

    private String settlementType;
    private Integer settlementTypeCode;

    private String arrivalArea;

    private String arrivalAddress;

    private Timestamp openBillTimeBegin;

    private Timestamp openBillTimeEnd;

    private Timestamp signTimeBegin;

    private Timestamp signTimeEnd;

    private List<String> masterNameList;

    private String destination;

    private String milestoneType;

    private Timestamp startTime;

    private Timestamp endTime;

    private String condition;

    private String columns;

    //--------------- 费用参数-----------------
    /**
     * 流水对象：商家, 承运商和服务商
     */
    private String billingObject;


    //--------------- 分页参数-----------------
    /**
     * 初始化偏移量
     */
    private int offset = 0;

    /**
     * 初始化查询数量
     */
    private int size = 100;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getMilestoneType() {
        return milestoneType;
    }

    public void setMilestoneType(String milestoneType) {
        this.milestoneType = milestoneType;
    }

    public String getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(String waybillId) {
        this.waybillId = waybillId;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<String> getConsigneeNameList() {
        return consigneeNameList;
    }

    public void setConsigneeNameList(List<String> consigneeNameList) {
        this.consigneeNameList = consigneeNameList;
    }

    public String getConsigneeMobile() {
        return consigneeMobile;
    }

    public void setConsigneeMobile(String consigneeMobile) {
        this.consigneeMobile = consigneeMobile;
    }

    public String getConsigneeAddress() {
        return consigneeAddress;
    }

    public void setConsigneeAddress(String consigneeAddress) {
        this.consigneeAddress = consigneeAddress;
    }

    public List<String> getSecondNameList() {
        return secondNameList;
    }

    public void setSecondNameList(List<String> secondNameList) {
        this.secondNameList = secondNameList;
    }

    public String getOpenBillNode() {
        return openBillNode;
    }

    public void setOpenBillNode(String openBillNode) {
        this.openBillNode = openBillNode;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getCheckMethod() {
        return checkMethod;
    }

    public void setCheckMethod(String checkMethod) {
        this.checkMethod = checkMethod;
    }

    public String getWaybillStatus() {
        return waybillStatus;
    }

    public void setWaybillStatus(String waybillStatus) {
        this.waybillStatus = waybillStatus;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
    }

    public String getArrivalArea() {
        return arrivalArea;
    }

    public void setArrivalArea(String arrivalArea) {
        this.arrivalArea = arrivalArea;
    }

    public String getArrivalAddress() {
        return arrivalAddress;
    }

    public void setArrivalAddress(String arrivalAddress) {
        this.arrivalAddress = arrivalAddress;
    }

    public Timestamp getOpenBillTimeBegin() {
        return openBillTimeBegin;
    }

    public void setOpenBillTimeBegin(Timestamp openBillTimeBegin) {
        this.openBillTimeBegin = openBillTimeBegin;
    }

    public Timestamp getOpenBillTimeEnd() {
        return openBillTimeEnd;
    }

    public void setOpenBillTimeEnd(Timestamp openBillTimeEnd) {
        this.openBillTimeEnd = openBillTimeEnd;
    }

    public Timestamp getSignTimeBegin() {
        return signTimeBegin;
    }

    public void setSignTimeBegin(Timestamp signTimeBegin) {
        this.signTimeBegin = signTimeBegin;
    }

    public Timestamp getSignTimeEnd() {
        return signTimeEnd;
    }

    public void setSignTimeEnd(Timestamp signTimeEnd) {
        this.signTimeEnd = signTimeEnd;
    }

    public List<String> getConsignorNameList() {
        return consignorNameList;
    }

    public void setConsignorNameList(List<String> consignorNameList) {
        this.consignorNameList = consignorNameList;
    }

    public List<String> getWaybillIdList() {
        return waybillIdList;
    }

    public void setWaybillIdList(List<String> waybillIdList) {
        this.waybillIdList = waybillIdList;
    }

    public List<String> getMasterNameList() {
        return masterNameList;
    }

    public void setMasterNameList(List<String> masterNameList) {
        this.masterNameList = masterNameList;
    }

    public String getBillingObject() {
        return billingObject;
    }

    public void setBillingObject(String billingObject) {
        this.billingObject = billingObject;
    }

    public Integer getServiceTypeCode() {
        return serviceTypeCode;
    }

    public void setServiceTypeCode(Integer serviceTypeCode) {
        this.serviceTypeCode = serviceTypeCode;
    }

    public Integer getCheckTypeCode() {
        return checkTypeCode;
    }

    public void setCheckTypeCode(Integer checkTypeCode) {
        this.checkTypeCode = checkTypeCode;
    }

    public Integer getCheckMethodCode() {
        return checkMethodCode;
    }

    public void setCheckMethodCode(Integer checkMethodCode) {
        this.checkMethodCode = checkMethodCode;
    }

    public Integer getWaybillStatusCode() {
        return waybillStatusCode;
    }

    public void setWaybillStatusCode(Integer waybillStatusCode) {
        this.waybillStatusCode = waybillStatusCode;
    }

    public Integer getCustomerTypeCode() {
        return customerTypeCode;
    }

    public void setCustomerTypeCode(Integer customerTypeCode) {
        this.customerTypeCode = customerTypeCode;
    }

    public Integer getSettlementTypeCode() {
        return settlementTypeCode;
    }

    public void setSettlementTypeCode(Integer settlementTypeCode) {
        this.settlementTypeCode = settlementTypeCode;
    }

    public String getIncrementServiceType() {
        return incrementServiceType;
    }

    public void setIncrementServiceType(String incrementServiceType) {
        this.incrementServiceType = incrementServiceType;
    }

    public Integer getIncrementServiceCode() {
        return incrementServiceCode;
    }

    public void setIncrementServiceCode(Integer incrementServiceCode) {
        this.incrementServiceCode = incrementServiceCode;
    }
}
