package com.yiziton.dataweb.waybill.vo;

import com.yiziton.dataweb.waybill.bean.BillDetailInfo;

import java.util.List;

/**
 * @author wanglianbo
 * @date 2018-12-06 15:26
 */
public class BillDetailInfoVO {

    private String billObject;

    private List<BillDetailInfo> billDetailInfoList;

    public String getBillObject() {
        return billObject;
    }

    public void setBillObject(String billObject) {
        this.billObject = billObject;
    }

    public List<BillDetailInfo> getBillDetailInfoList() {
        return billDetailInfoList;
    }

    public void setBillDetailInfoList(List<BillDetailInfo> billDetailInfoList) {
        this.billDetailInfoList = billDetailInfoList;
    }
}
