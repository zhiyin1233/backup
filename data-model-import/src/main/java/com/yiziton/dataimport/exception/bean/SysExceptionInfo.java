package com.yiziton.dataimport.exception.bean;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Description: 系统异常信息
 * @Author: xiaoHong
 * @Date: 2018/11/16
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Data
public class SysExceptionInfo {
    private String id;

    private String no;

    private String noType;

    private Integer dataType;

    private String exceptionBody;

    private String exceptionInfo;

    private String source;

    private Timestamp sentTime;

    private Timestamp createTime;

    private Timestamp updateTime;
}
