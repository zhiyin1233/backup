/*
 * Copyright 2018 1ziton.com.
 **/
package com.yiziton.dataweb.core.oauth2;

import java.io.Serializable;

/**
 * 请求输出
 *
 * @author xiaoHong
 * @create 2018-02-03
 **/
public class ActionResponse implements Serializable {
    private String resultCode;
    private String msg;
    private Object content;
    private String cause;
    public String getResultCode() {
        return resultCode;
    }
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}