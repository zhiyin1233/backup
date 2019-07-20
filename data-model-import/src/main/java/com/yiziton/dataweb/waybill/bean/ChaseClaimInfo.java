package com.yiziton.dataweb.waybill.bean;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author wanglianbo
 * @date 2018-12-09 15:08
 */
@Data
public class ChaseClaimInfo {

    private String id;

    private String waybillId;

    private Integer status;

    private String compensationBillId;

    private String compensationClass;

    private String billId;

    private String billName;

    private String compensationBillMonth;

    private String dutyObject;

    private String dutyName;

    private String dutyCode;

    private String compensationGoods;

    private String compensationType;

    private String compensationReason;

    private String fullOffer;

    private String handleView;

    private String closeView;

    private String applyTime;

    private String firstHandleTime;

    private String closeTime;

    private String applyName;

    private String handleName;

    private String closeName;

    private Double compensationFee;

    private Double handleFee;

    private Double accommodationFee;

    private Double closeFee;

    private Double countFee;

    private String compensationLetter;

    private String lostList;

    private String signBill;

    private String goodsValueInstruction;

    private String damagePicture;

    private Timestamp createTime;

    private Timestamp updateTime;

    private Integer dataType;

    private String damageDegree;

    private String compensationStatus;

}
