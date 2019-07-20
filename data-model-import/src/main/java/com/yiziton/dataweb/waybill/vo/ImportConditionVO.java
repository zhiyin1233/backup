package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 历史数据导入公共参数
 */
@Data
public class ImportConditionVO {

    //-------------------公共参数-------------------
    private Timestamp openBillTimeBegin;//开单开始日期

    private Timestamp openBillTimeEnd;//开单结束日期

    private List<String> waybillIds;//多个运单号

    //-------------------cmp-------------------
    private List<String> agentCode;//承运商

}
