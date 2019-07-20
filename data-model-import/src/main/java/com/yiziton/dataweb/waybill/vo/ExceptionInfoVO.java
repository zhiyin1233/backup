package com.yiziton.dataweb.waybill.vo;

import com.yiziton.dataweb.waybill.bean.ExceptionInfo;

import java.util.List;

/**
 * @author wanglianbo
 * @date 2018-12-05 21:51
 */
public class ExceptionInfoVO {

    private String waybillId;

    private List<ExceptionInfo> exceptionInfoList;

    public String getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(String waybillId) {
        this.waybillId = waybillId;
    }

    public List<ExceptionInfo> getExceptionInfoList() {
        return exceptionInfoList;
    }

    public void setExceptionInfoList(List<ExceptionInfo> exceptionInfoList) {
        this.exceptionInfoList = exceptionInfoList;
    }
}



