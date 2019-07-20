package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 发货人
 */
@Data
public class ConsignorInfo {
    private String id;

    private String waybillId;

    private String code;

    private String name;

    private Integer customerType;

    private String secondName;

    private String mobile;

    private Integer status;

    private Timestamp createTime;

    private Timestamp updateTime;

    private String secondCode;

    private String province;

    private String city;

    private String area;

    private String street;

    private String areaId;

    private String longitude;

    private String latitude;

    private Integer dataType;

    private Integer consignorType;

}