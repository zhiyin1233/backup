package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.util.Date;

@Data
public class Consignor {
    private String id;

    private String code;

    private String name;

    private String telephone;

    private Integer type;

    private String typeName;

    private Boolean isVip;

    private String remark;

    private String contact;

    private String contactTelephone;

    private String contactAddress;

    private String businessContact;

    private String businessContactJobNum;

    private String businessContactTelephone;

    private String userCode;

    private String userAccount;

    private String payPassword;

    private Integer shippingMethod;

    private String shippingMethodName;

    private String sourceSystem;

    private String creator;

    private String creatorName;

    private String modifier;

    private String modifierName;

    private Integer status;

    private Integer level;

    private String levelName;

    private Integer label;

    private String labelName;

    private Integer accountPeriod;

    private String accountPeriodName;

    private Double logisticsCost;

    private Date createTime;

    private Date updateTime;

}