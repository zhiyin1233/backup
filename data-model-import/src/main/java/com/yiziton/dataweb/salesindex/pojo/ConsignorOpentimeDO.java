package com.yiziton.dataweb.salesindex.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 *
 * @author HuangHuai on 2019-07-11 15:54
 */
@Data
public class ConsignorOpentimeDO implements Serializable {
    /**
     * 客户编号
     */
    public String customerCode;

    /**
     * 客户名称
     */
    public String customerName;

    /**
     * 开单月份
     */
    public String openBillMonth;
    /**
     *   业务员
     */
    public String salesman;
    /**
     *  客户等级
     */
    public String clientGrade;
    /**
     *  部门code
     */
    public String organizeCode;
    /**
     *  客户（发货人）是否新客户标识
     */
    public String customerLabel;
    /**
     *  客户价格类型(标准/非标准)
     */
    public  String clientType;
    /**
     *  结算方式
     */
    public  String settlementType;


}
