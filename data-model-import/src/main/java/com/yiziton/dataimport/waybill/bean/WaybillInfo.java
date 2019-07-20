package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class WaybillInfo {
    private String id;

    private Integer productType;

    private Integer serviceType;

    private Integer waybillStatus;

    private Integer checkType;

    private String checkCode;

    private String checkBillId;

    private Integer checkStatus;

    private Integer checkMethod;

    private Integer channelSource;

    private Integer paymentType;

    private Integer settlementType;

    private String thirdBillingId;

    private String destination;

    private Double totalVolume;

    private Integer totalInstallItems;

    private Integer totalPackingItems;

    private Double totalWeight;

    private Double statementValue;

    private String openBillNode;

    private String openBillOperator;

    private String arrivalNode;

    private Timestamp openBillTime;

    private Timestamp signTime;

    private Double saleTotalPrice;

    private Integer marbleBlocks;

    private String salesman;

    private String salesmanPhone;

    private Integer status;

    private Timestamp createTime;

    private String updateId;

    private Timestamp updateTime;

    private Integer waybillType;

    private String arrivalArea;

    private String arrivalAddress;

    private String pickUpGoodsPhone;

    private String pickUpGoodsPassword;

    private String logisticsBillId;

    private Integer dataType;

    private String originatingNode;

    private String openBillRemark;

    private Timestamp checkTime;

    private Integer incrementServiceType;

    private Double incrementServiceFee;

    private String goodsType;

    private String packing;

    private Timestamp pickingTime;
}