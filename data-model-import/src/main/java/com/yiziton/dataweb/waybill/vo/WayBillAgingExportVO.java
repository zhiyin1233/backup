package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * Created by ouweijian on 2018-12-03 15:43:35.
 */
@Data
public class WayBillAgingExportVO {

    /**
     * 运单号
     */
    private String waybillId;
    /**
     * 开单时间
     */
    private String openBillTime;
    /**
     * 运单状态
     */
    private String waybillStatus;
    /**
     * 客户类型、结算方式
     */
    private String settlementType;
    /**
     * 服务类型
     */
    private String serviceType;
    /**
     * 品名
     */
    private String goodsName;
    /**
     * 包装
     */
    private String goodsPacking;
    /**
     * 包装总件数
     */
    private Integer packingTotalCount;
    /**
     * 包装总体积
     */
    private Double packingTotalVolume;
    /**
     * 总费用
     */
    private Double totalFee;
    /**
     * 制单人
     */
    private String openBillOperator;
    /**
     * 承运商
     */
    private String carrier;
    /**
     * 物流单号
     */
    private String logisticsBillId;
    /**
     * 运单类型
     */
    private String waybillType;
    /**
     * 备注
     */
    private String openBillRemark;
    /**
     * 始发节点
     */
    private String originatingNode;
    /**
     * 订单号
     */
    private String orderBillId;
    /**
     * 客户单号
     */
    private String customerBillId;
    /**
     * 付款方式
     */
    private String paymentType;
    /**
     * 业务员
     */
    private String salesman;
    /**
     * 开单网点
     */
    private String openBillNode;

    /*-----------------发货人-------------------*/
    /**
     * 发货人
     */
    private String consignorName;
    /**
     * 发货人手机
     */
    private String consignorMobile;
    /**
     * 二级商家名称
     */
    private String secondMerchantName;
    /**
     * 商家类型(商家标识)
     */
    private String merchantType;

    /*-----------------收货人-------------------*/
    /**
     * 收货人
     */
    private String receiveName;
    /**
     * 收货人手机
     */
    private String receiveMobile;
    /**
     * 目的地
     */
    private String destination;
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
     * 详细地址
     */
    private String receiveAddress;

    /*-----------------节点时间相关-------------------*/
    /**
     * 入库时间
     */
    private String warehousingTime;
    /**
     * 拣货时间
     */
    private String pickingTime;
    /**
     * 外发交接时间
     */
    private String outDeliverTime;
    /**
     * 承运商发车
     */
    private String carrierDepartureTime;
    /**
     * 承运商内部到达中转时间
     */
    private String carrierArriveTime;
    /**
     * 承运商内部中转发车时间
     */
    private String carrierTransferTime;
    /**
     * 承运商到达时间
     */
    private String arrivalDestinationTime;
    /**
     * 直营收货时间
     */
    private String directReceiptTime;
    /**
     * 直营外发时间
     */
    private String directTransferTime;
    /**
     * 直营外发单号
     */
    private String departmentOutDeliverBillNo;
    /**
     * 干结时间
     */
    private String trunkEndTime;
    /**
     * 客服中心派单时间
     */
    private String distributionTime;
    /**
     * 客服中心派单师傅
     */
    private String distributionMaster;
    /**
     * 首次预约操作时间
     */
    private String firstAppointmentTime;
    /**
     * 首次预约上门服务时间
     */
    private String firstVisitTime;
    /**
     * 预约次数
     */
    private Integer appointmentCount;
    /**
     * 最新一次预约操作时间
     */
    private String lastAppointmentTime;
    /**
     * 最新一次预约上门服务时间
     */
    private String lastVisitTime;
    /**
     * 最新一次上门打卡时间
     */
    private String homeCheckTime;
    /**
     * 签收时间
     */
    private String signTime;

    /*-----------------核销相关-------------------*/
    /**
     * 核销类型
     */
    private String checkType;
    /**
     * 核销方式
     */
    private String checkMethod;
    /**
     * 核销编码
     */
    private String checkCode;
    /**
     * 核销单号
     */
    private String checkBillId;
    /**
     * 是否核销
     */
    private String checkStatus;

    /*-----------------师傅信息-------------------*/
    /**
     * 安装师傅账号
     */
    private String masterPhone;
    /**
     * 安装师傅
     */
    private String masterName;
    /**
     * 安装师傅所属网点=末端网点
     */
    private String masterNode;
}
