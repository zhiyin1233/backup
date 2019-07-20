package com.yiziton.dataweb.waybill.dataobject;

import lombok.Data;

import java.sql.Timestamp;

/**
 * <p>Description: 运单基本信息传输对象</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: YZT</p>
 *
 * @author TY
 * @version 1.0
 * @date 2019/01/22 15:59
 */
@Data
public class WaybillbaseInfoDO {
    private String waybillId;
    private Integer serviceType;
    private String openBillNode;
    private String openBillOperator;
    private Integer channelSource;
    private Integer productType;
    private Double merchantFee;
    private Double saleTotalPrice;
    private Integer paymentType;
    private Timestamp openBillTime;
    private Timestamp signTime;
    private String goodsType;
    private String cName;
    private String cSecondName;
    private String cMobile;
    private String cSecondCode;
    private String cCode;
    private Integer cCustomerType;
    private String rName;
    private String rDestination;
    private String rMobile;
    private String rAddress;
    private String doorTime;
    private Integer floor;
    private Integer checkType;
    private Integer checkMethod;
    private String checkBillId;
    private String checkCode;
    private Integer checkStatus;
    private String logisticsBillId;
    private String pickUpGoodsPhone;
    private String pickUpGoodsPassword;
    private String arrivalAddress;
    private String salesman;
    private String salesmanPhone;
    private Integer incrementServiceType;
    private Double incrementServiceFee;
    private String incrementServiceStatus;
}
