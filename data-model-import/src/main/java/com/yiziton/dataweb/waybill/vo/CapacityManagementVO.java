package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * <p>Description: 运力管理部报表-按承运商导明细VO</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: YZT</p>
 *
 * @author TY
 * @version 1.0
 * @date 2019/03/19 19:59
 */
@Data
public class CapacityManagementVO {
    /**
     * 外发时间
     */
    private String operateTime;
    /**
     * 签收时间
     */
    private String signTime;
    /**
     * 单号
     */
    private String waybillId;
    /**
     * 承运商
     */
    private String carrier;
    /**
     * 外发成本(元)
     */
    private String fee;
    /**
     * 外发网点
     */
    private String operationOganization;
    /**
     * 客户类型
     */
    private String customerLabel;
    /**
     * 发货人
     */
    private String ciName;

    /**
     * 商家名称
     */
    private String secondName;
    /**
     * 服务类型
     */
    private String serviceType;
    /**
     * 货物类型
     */
    private String goodsType;
    /**
     * 品名(用;号分隔)
     */
    private String goodsName;
    /**
     * 大理石数量(件)
     */
    private String marbleBlocks;
    /**
     * 安装件数(件)
     */
    private String totalInstallItems;
    /**
     * 包装件数(件)
     */
    private String totalPackingItems;
    /**
     * 总体积(m³)
     */
    private String totalVolume;
    /**
     * 总重量(kg)
     */
    private String totalWeight;
    /**
     * 收货人姓名
     */
    private String riName;
    /**
     * 收货人电话
     */
    private String mobile;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String area;
    /**
     * 收货人地址
     */
    private String address;
}
