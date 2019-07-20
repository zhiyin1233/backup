package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.util.Date;

@Data
public class SysConfigInfo {
    private String id;

    private String configKey;

    private String configValue;

    private Integer status;

    private Date createTime;

    private Date updateTime;

}