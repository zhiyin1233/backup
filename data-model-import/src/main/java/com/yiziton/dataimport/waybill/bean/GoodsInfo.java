package com.yiziton.dataimport.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class GoodsInfo {
    private String id;

    private String waybillId;

    private String goodsCode;

    private String goodsName;

    private String customerGoodsCode;

    private String customerGoodsName;

    private Integer installItems;

    private Integer packingItems;

    private Double volume;

    private Double weight;

    private Double installFee;

    private Double deliveryFee;

    private Integer status;

    private Timestamp createTime;

    private Timestamp updateTime;

    private Integer dataType;

    private Integer packing;
}