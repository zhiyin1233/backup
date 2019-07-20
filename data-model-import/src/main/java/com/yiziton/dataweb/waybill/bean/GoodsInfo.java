package com.yiziton.dataweb.waybill.bean;

import java.math.BigDecimal;

/**
 * 商品信息
 * @author wanglianbo
 * @date 2018-12-04 21:09
 */
public class GoodsInfo {

    private String id;

    private String waybillId;

    private String customerGoodsName;

    private String goodsName;

    private Integer packingItems;

    private Integer installItems;

    private BigDecimal installFee;

    private BigDecimal volume;

    private BigDecimal weight;

    private String goodsType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(String waybillId) {
        this.waybillId = waybillId;
    }

    public String getCustomerGoodsName() {
        return customerGoodsName;
    }

    public void setCustomerGoodsName(String customerGoodsName) {
        this.customerGoodsName = customerGoodsName;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getPackingItems() {
        return packingItems;
    }

    public void setPackingItems(Integer packingItems) {
        this.packingItems = packingItems;
    }

    public Integer getInstallItems() {
        return installItems;
    }

    public void setInstallItems(Integer installItems) {
        this.installItems = installItems;
    }

    public BigDecimal getInstallFee() {
        return installFee;
    }

    public void setInstallFee(BigDecimal installFee) {
        this.installFee = installFee;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }
}
