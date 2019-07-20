package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ReceviceInfo {
    private String id;

    private String waybillId;

    private String name;

    private String mobile;

    private String address;

    private Integer elevator;

    private Integer floor;

    private Integer status;

    private Timestamp createTime;

    private Timestamp updateTime;

    private String province;

    private String city;

    private String area;

    private String street;

    private String areaId;

    private String longitude;

    private String latitude;

    private Integer dataType;

}