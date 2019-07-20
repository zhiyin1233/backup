package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @program: big-data-platform
 * @description: 运营指标参数
 * @author: kdh
 * @create: 2019-05-24 18:26
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@Data
public class IndicatorConditionVO {

    private Timestamp assessTimeBegin;//考核时间开始

    private Timestamp assessTimeEnd;//考核时间结束

    private Timestamp circleAssessTimeBegin;//环比考核时间开始

    private Timestamp circleAssessTimeEnd;//环比考核时间结束

    private List<String> secondCustomer;//二级商家，商家对于一智通来说是客户

    private List<String> consignor;//发货人

    private List<String> destination;//目的地

    private List<String> openBillNode;//开单网点

    private List<String> carrier;//承运商

    private List<String> master;//安装师傅

    private List<String> masterNode;//安装师傅所属网点=末端网点

}
