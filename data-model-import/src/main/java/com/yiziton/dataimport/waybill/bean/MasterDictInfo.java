package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MasterDictInfo {
    private String id;

    private String name;

    private String node;

    private String accountNumber;

    private String phone;

    private Integer type;

    private String code;

    private Integer certificatesType;

    private String certificatesCode;

    private String certificatesPicture;

    private Integer serviceType;

    private Integer addedValueService;

    private String serviceAreas;

    private Integer masterStatus;

    private Integer settlementType;

    private Integer reflectType;

    private Integer status;

    private String province;

    private String city;

    private String account;

    private String accountName;

    private String bankName;

    private String bankCode;

    private String subBankName;

    private Integer carType;

    private String carNumber;

    private String carLength;

    private String carWeightBearing;

    private String carSpace;

    private String carPicture;

    private String drivingLicensePicture;

    private String warehouseAddress;

    private String warehouseArea;

    private String warehouseSize;

    private Integer teamNumber;

    private Integer maximumBillCount;

    private Integer bailmoney;

    private String repairNode;

    private String backNode;

    private String installNode;

    private String secondDoorNode;

    private Timestamp registerTime;

    private Timestamp authenticationTime;

    private Timestamp lastModifyTime;

    private String lastModify;

    private String remark;

    private Timestamp createTime;

    private Timestamp updateTime;
}