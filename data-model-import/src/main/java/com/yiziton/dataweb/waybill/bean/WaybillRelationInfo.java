package com.yiziton.dataweb.waybill.bean;

/**
 * @author wanglianbo
 * @date 2018-12-12 14:55
 */
public class WaybillRelationInfo {

    private String waybillId;

    private String customerBillId;

    private String orderBillId;

    public String getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(String waybillId) {
        this.waybillId = waybillId;
    }

    public String getCustomerBillId() {
        return customerBillId;
    }

    public void setCustomerBillId(String customerBillId) {
        this.customerBillId = customerBillId;
    }

    public String getOrderBillId() {
        return orderBillId;
    }

    public void setOrderBillId(String orderBillId) {
        this.orderBillId = orderBillId;
    }
}
