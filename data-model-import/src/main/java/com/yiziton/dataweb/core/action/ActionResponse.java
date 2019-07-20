/*
 * Copyright 2018 1ziton.com.
 **/
package com.yiziton.dataweb.core.action;

import java.io.Serializable;

/**
 * 请求输出
 *
 * @author hsw
 * @create 2018-02-03
 **/
public class ActionResponse implements Serializable {
    private String resultCode;
    private String msg;
    private Object content;
    private Object cause;
//    private Map m = new HashMap();
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

    public Object getCause() {
        return cause;
    }
    public void setCause(Object cause) {
        this.cause = cause;
    }

}