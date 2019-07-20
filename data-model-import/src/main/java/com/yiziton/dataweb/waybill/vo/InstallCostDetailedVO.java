package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * @author: zhangSiYuan
 */
@Data
public class InstallCostDetailedVO {

    /**
     * 客户标识
     */
    private String customerLabel;
    /**
     * 单号
     */
    private String waybillId;
    /**
     * 签收日期
     */
    private String signTime;
    /**
     * 师傅名称
     */
    private String masterMame;
    /**
     * 手机号
     */
    private String masterPhone;
    /**
     * 所属网点
     */
    private String masterNode;
    /**
     * 收货人姓名
     */
    private String receiveName;
    /**
     * 收货人手机号码
     */
    private String receiveMobile;
    /**
     * 收货省
     */
    private String province;
    /**
     * 收货市
     */
    private String city;
    /**
     * 收货区
     */
    private String area;
    /**
     * 收货人信息地址
     */
    private String address;
    /**
     * 品名
     */
    private String goodsName;
    /**
     * 安装件数
     */
    private String installItems;
    /**
     * 安装费
     */
    private String installFee;
    /**
     * 发货人
     */
    private String consignorName;
}
