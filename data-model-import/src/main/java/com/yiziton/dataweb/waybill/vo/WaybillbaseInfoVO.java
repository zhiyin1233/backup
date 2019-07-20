package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

import java.sql.Timestamp;

/**
 * <p>Description: 运单基本信息返回对象</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: YZT</p>
 *
 * @author TY
 * @version 1.0
 * @date 2019/01/22 15:59
 */
@Data
public class WaybillbaseInfoVO {
    private String waybillId;
    private String serviceType;
    private String openBillNode;
    private String openBillOperator;
    private String channelSource;
    private String productType;
    private Double merchantFee;
    private Double saleTotalPrice;
    private String paymentType;
    private String openBillTime;
    private String signTime;
    private String goodsType;
    private String cName;
    private String cSecondName;
    private String cMobile;
    private String cSecondCode;
    private String cCode;
    private String cCustomerType;
    private String rName;
    private String rDestination;
    private String rMobile;
    private String rAddress;
    private String doorTime;
    private Integer floor;
    private String checkType;
    private String checkMethod;
    private String checkBillId;
    private String checkCode;
    private String checkStatus;
    private String logisticsBillId;
    private String pickUpGoodsPhone;
    private String pickUpGoodsPassword;
    private String arrivalAddress;
    private String salesman;
    private String salesmanPhone;
    private String incrementServiceType;
    private Double incrementServiceFee;
    private String incrementServiceStatus;
}
