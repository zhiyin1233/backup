package com.yiziton.dataweb.waybill.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wanglianbo
 * @date 2018-12-05 17:38
 */
@Data
public class AfterSaleTaskInfo {

    private String id;

    private String waybillId;

    private String taskBillId;

    private String taskType;

    //private String taskStatus;

    private String createTime;

    //承运商信息

    private String carrierCode;

    private String carrier;

    private String pickUpMethod;

    private String carrierReceiver;

    private String carrierReceiverPhone;


    private String masterName;

    private String masterPhone;

    private String exceptionCode;

    private BigDecimal totalPrice;

    private List<GoodsInfo> goodsInfoList;

}
