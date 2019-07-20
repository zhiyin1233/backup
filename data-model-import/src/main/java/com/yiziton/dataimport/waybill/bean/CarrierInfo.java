package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CarrierInfo {
    private String id;

    private String waybillId;

    private String carrier;

    private String carrierCode;

    private String pickUpMethod;

    private String carrierReceiver;

    private String carrierReceiverPhone;

    private Integer status;

    private String areaId;

    private String carrierAddress;

    private String carrierCompanyName;

    private String startSentArea;

    private String carrierDestination;

    private String carrierSettlementType;

    private String outDeliverRelayid;

    private Timestamp carrierDepartureTime;

    private String carrierDepartureLine;

    private String transferBillId;

    private Timestamp limitArrivalTime;

    private Timestamp actualArrivalTime;

    private Timestamp createTime;

    private Timestamp updateTime;

    private Integer dataType;

    private String carrierBillId;

    private Integer carrierType;
}