package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * 仓储枢纽-外发货量 VO
 */
@Data
public class OutDeliveryVolumeVO {
    /**
     * 外发网点
     */
    private String operationOganization;
    /**
     * 当天外发时间
     */
    private String today;
    /**
     * 当天外发总费用
     */
    private String todayOutDeliverCost;
    /**
     * 当天揽货总费用
     */
    private String todayTakeGoodsFee;
    /**
     * 当天外发总方数
     */
    private String todayVolume;
    /**
     * 当月月份
     */
    private String month;
    /**
     * 当月累计外发货总费用
     */
    private String monthOutDeliverCost;
    /**
     * 当月累计揽货总费用
     */
    private String monthTakeGoodsFee;
    /**
     * 当月累计外发总方数
     */
    private String monthVolume;
}

