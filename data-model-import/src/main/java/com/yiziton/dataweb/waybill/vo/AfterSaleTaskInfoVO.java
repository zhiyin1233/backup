package com.yiziton.dataweb.waybill.vo;

import com.yiziton.dataweb.waybill.bean.GoodsInfo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wanglianbo
 * @date 2018-12-05 21:56
 */
@Data
public class AfterSaleTaskInfoVO {

    private String id;

    private String waybillId;

    private String taskBillId;

    private String taskType;

    //private String taskStatus;

    private String createTime;

    private String masterName;

    private String masterPhone;

    private String exceptionCode;

    private BigDecimal totalPrice;

    private List<GoodsInfo> goodsInfoList;

}
