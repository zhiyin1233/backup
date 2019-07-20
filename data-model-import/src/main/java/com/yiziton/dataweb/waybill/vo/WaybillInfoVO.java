package com.yiziton.dataweb.waybill.vo;

import java.math.BigDecimal;

/**
 * @author wanglianbo
 * @date 2018-12-04 21:31
 */
public class WaybillInfoVO {

    /**
     * 运单号
     */
    private String wayBillId;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 服务类型
     */
    private String serviceType;

    /**
     * 增值服务类型
     */
    private String incrementServiceType;


    public String getIncrementServiceType() {
        return incrementServiceType;
    }

    public void setIncrementServiceType(String incrementServiceType) {
        this.incrementServiceType = incrementServiceType;
    }

    /**
     * 增值服务费用
     */
    private BigDecimal incrementServiceFee;

    public BigDecimal getIncrementServiceFee() {
        return incrementServiceFee;
    }

    public void setIncrementServiceFee(BigDecimal incrementServiceFee) {
        this.incrementServiceFee = incrementServiceFee;
    }

    /**
     * 运单状态
     */
    private String waybillStatus;

    /**
     * 核销类型
     */
    private String checkType;

    /**
     * 核销方式
     */
    private String checkMethod;

    /**
     * 核销单号
     */
    private String checkBillId;

    /**
     * 核销单号
     */
    private String checkCode;

    /**
     * 核销状态
     */
    private String checkStatus;

    private String paymentType;

    /**
     * 渠道来源
     */
    private String channelSource;

    /**
     *
     */
    private String consignorCode;

    private String consignorName;

    private String consignorMobile;

    private String secondCode;

    private String secondName;

    private String customerType;

    private String settlementType;

    private String consigneeName;

    private String consigneeMobile;

    private String consigneeAddress;

    private String consigneeArea;

    private Integer elevator;

    private Integer floor;

    private String actualDoorTime;

    /**
     * 提货目的地
     */
    private String arrivalArea;

    private String arrivalAddress;

    private String pickUpGoodsPhone;

    private String pickUpGoodsPassword;

    private String logisticsBillId;

    /**
     * 商品信息, 需要组装
     */
    private String goodsInfo;

    private Integer marbleBlocks;

    private BigDecimal totalVolume;

    private Integer totalPackingItems;

    private Integer totalInstallItems;

    private BigDecimal totalWeight;

    private BigDecimal statementValue;

    private String openBillNode;

    private String openBillOperator;

    private BigDecimal saleTotalPrice;

    /**
     * 订单总价
     */
    private BigDecimal waybillTotalPrice;

    /**
     * 商家金额
     */
    private BigDecimal customerPrice;

    private String salesman;

    // TODO: 2018-12-04 业务员组织
    /**
     * 业务员组织
     */
    private String salesmanOrg;

    private String salesmanPhone;

    private String openBillTime;

    private String signTime;

    private String masterName;

    private String masterPhone;

    private String masterNode;

    private String carrier;

    /**
     * 目的地
     */
    private String destination;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getWayBillId() {
        return wayBillId;
    }

    public void setWayBillId(String wayBillId) {
        this.wayBillId = wayBillId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getWaybillStatus() {
        return waybillStatus;
    }

    public void setWaybillStatus(String waybillStatus) {
        this.waybillStatus = waybillStatus;
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

    public String getCheckBillId() {
        return checkBillId;
    }

    public void setCheckBillId(String checkBillId) {
        this.checkBillId = checkBillId;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getChannelSource() {
        return channelSource;
    }

    public void setChannelSource(String channelSource) {
        this.channelSource = channelSource;
    }

    public String getConsignorCode() {
        return consignorCode;
    }

    public void setConsignorCode(String consignorCode) {
        this.consignorCode = consignorCode;
    }

    public String getConsignorName() {
        return consignorName;
    }

    public void setConsignorName(String consignorName) {
        this.consignorName = consignorName;
    }

    public String getConsignorMobile() {
        return consignorMobile;
    }

    public void setConsignorMobile(String consignorMobile) {
        this.consignorMobile = consignorMobile;
    }

    public String getSecondCode() {
        return secondCode;
    }

    public void setSecondCode(String secondCode) {
        this.secondCode = secondCode;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
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

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
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

    public String getConsigneeArea() {
        return consigneeArea;
    }

    public void setConsigneeArea(String consigneeArea) {
        this.consigneeArea = consigneeArea;
    }

    public Integer getElevator() {
        return elevator;
    }

    public void setElevator(Integer elevator) {
        this.elevator = elevator;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getActualDoorTime() {
        return actualDoorTime;
    }

    public void setActualDoorTime(String actualDoorTime) {
        this.actualDoorTime = actualDoorTime;
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

    public String getPickUpGoodsPhone() {
        return pickUpGoodsPhone;
    }

    public void setPickUpGoodsPhone(String pickUpGoodsPhone) {
        this.pickUpGoodsPhone = pickUpGoodsPhone;
    }

    public String getPickUpGoodsPassword() {
        return pickUpGoodsPassword;
    }

    public void setPickUpGoodsPassword(String pickUpGoodsPassword) {
        this.pickUpGoodsPassword = pickUpGoodsPassword;
    }

    public String getLogisticsBillId() {
        return logisticsBillId;
    }

    public void setLogisticsBillId(String logisticsBillId) {
        this.logisticsBillId = logisticsBillId;
    }

    public Integer getMarbleBlocks() {
        return marbleBlocks;
    }

    public void setMarbleBlocks(Integer marbleBlocks) {
        this.marbleBlocks = marbleBlocks;
    }

    public BigDecimal getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(BigDecimal totalVolume) {
        this.totalVolume = totalVolume;
    }

    public Integer getTotalPackingItems() {
        return totalPackingItems;
    }

    public void setTotalPackingItems(Integer totalPackingItems) {
        this.totalPackingItems = totalPackingItems;
    }

    public Integer getTotalInstallItems() {
        return totalInstallItems;
    }

    public void setTotalInstallItems(Integer totalInstallItems) {
        this.totalInstallItems = totalInstallItems;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public BigDecimal getStatementValue() {
        return statementValue;
    }

    public void setStatementValue(BigDecimal statementValue) {
        this.statementValue = statementValue;
    }

    public String getOpenBillNode() {
        return openBillNode;
    }

    public void setOpenBillNode(String openBillNode) {
        this.openBillNode = openBillNode;
    }

    public String getOpenBillOperator() {
        return openBillOperator;
    }

    public void setOpenBillOperator(String openBillOperator) {
        this.openBillOperator = openBillOperator;
    }

    public BigDecimal getSaleTotalPrice() {
        return saleTotalPrice;
    }

    public void setSaleTotalPrice(BigDecimal saleTotalPrice) {
        this.saleTotalPrice = saleTotalPrice;
    }

    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public String getSalesmanPhone() {
        return salesmanPhone;
    }

    public void setSalesmanPhone(String salesmanPhone) {
        this.salesmanPhone = salesmanPhone;
    }

    public String getOpenBillTime() {
        return openBillTime;
    }

    public void setOpenBillTime(String openBillTime) {
        this.openBillTime = openBillTime;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getMasterPhone() {
        return masterPhone;
    }

    public void setMasterPhone(String masterPhone) {
        this.masterPhone = masterPhone;
    }

    public String getMasterNode() {
        return masterNode;
    }

    public void setMasterNode(String masterNode) {
        this.masterNode = masterNode;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public BigDecimal getWaybillTotalPrice() {
        return waybillTotalPrice;
    }

    public void setWaybillTotalPrice(BigDecimal waybillTotalPrice) {
        this.waybillTotalPrice = waybillTotalPrice;
    }

    public BigDecimal getCustomerPrice() {
        return customerPrice;
    }

    public void setCustomerPrice(BigDecimal customerPrice) {
        this.customerPrice = customerPrice;
    }

    public String getSalesmanOrg() {
        return salesmanOrg;
    }

    public void setSalesmanOrg(String salesmanOrg) {
        this.salesmanOrg = salesmanOrg;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(String goodsInfo) {
        this.goodsInfo = goodsInfo;
    }
}
