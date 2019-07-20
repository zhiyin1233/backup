package com.yiziton.dataweb.waybill.vo;

import com.yiziton.commons.vo.enums.CustomerType;
import com.yiziton.commons.vo.enums.ServiceType;
import com.yiziton.commons.vo.enums.SettlementType;
import com.yiziton.commons.vo.enums.WaybillStatus;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 报表导出公共参数
 */
@Data
public class ReportConditionVO {

    //-------------------公共参数-------------------

    //指定导出列
    private String columns;

    //初始化偏移量
    private int offset = 0;

    //初始化查询数量
    private int size = 10000;

    private Timestamp openBillTimeBegin;//开单开始日期

    private Timestamp openBillTimeEnd;//开单结束日期

    private Timestamp signTimeBegin;//签收开始日期

    private Timestamp signTimeEnd;//签收结束日期

    private Timestamp homeCheckTimeBegin;//上门打卡开始日期

    private Timestamp homeCheckTimeEnd;//上门打卡结束日期

    private Timestamp trunkEndTimeBegin;//干线结束开始日期

    private Timestamp trunkEndTimeEnd;//干线结束结束日期

    private String destination;//目的地

    private String waybillId;//运单号

    private List<String> waybillIdList;//多个运单号

    private List<String> openBillNode;//开单网点

    private String originatingNode;//始发网点，暂时未需要

    private List<String> consignor;//发货人

    private List<WaybillStatus> waybillStatus;//运单状态

    private List<String> secondCustomer;//二级商家，商家对于一智通来说是客户

    private List<CustomerType> customerType;//商家类型，对应 ConsignorInfo 中的customerType，商家对于一智通来说是客户

    private List<ServiceType> serviceType;//服务类型

    //-------------------时效报表-------------------
    private List<String> carrier;//承运商

    //-------------------时效报表、核销报表-------------------
    private List<String> master;//安装师傅

    private List<String> masterNode;//安装师傅所属网点=末端网点


    //-------------------标准利润报表、异常利润报表、业绩统计报表-------------------
    private List<SettlementType> settlementType;//结算方式

    private List<String> salesman;//业务员

}
