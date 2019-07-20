package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ReceviceDictInfo {
    private String id;

    private String receviceName;

    private String receviceMobile;

    private String recevicePhone;

    private String receiveAddress;

    private String receiveProvince;

    private String receviceCity;

    private Integer status;

    private Timestamp receviceArea;

    private Timestamp receviceTown;

    private Timestamp createTime;

    private Timestamp updateTime;

}