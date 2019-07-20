package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CarrierDictInfo {
    private String id;

    private String carrier;

    private String carrierCode;

    private String carrierPhone;

    private String pickUpMethod;

    private Integer status;

    private String areaId;

    private String carrierAddress;

    private String carrierCoverArea;

    private String carrierCompanyName;

    private String carrierNode;

    private Timestamp registerTime;

    private Timestamp beginCooperateTime;

    private String startSentArea;

    private String carrierDestination;

    private String carrierSettlementType;

    private Integer carrierStatus;

    private String businessLicenseCode;

    private String businessLicensePicture;

    private String legalCertificates;

    private String legalCertificatesPicture;

    private String legalCertificatesName;

    private String legalCertificatesPhone;

    private String settlementRules;

    private String salesman;

    private String salesmanPhone;

    private String lastModify;

    private Timestamp lastModifyTime;

    private Timestamp createTime;

    private Timestamp updateTime;

    }