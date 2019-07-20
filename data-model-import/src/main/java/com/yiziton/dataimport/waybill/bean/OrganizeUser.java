package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.util.Date;

@Data
public class OrganizeUser {
    private String id;

    private String name;

    private String code;

    private String telphone;

    private String jobNum;

    private String mail;

    private String position;

    private Boolean isLesseeAdmin;

    private Boolean isDirector;

    private String roles;

    private String organizeCode;

    private String organizeName;

    private String lesseeCode;

    private Integer status;

    private Date createTime;

    private Date updateTime;

}