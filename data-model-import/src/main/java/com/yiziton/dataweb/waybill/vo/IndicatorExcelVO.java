package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * @program: big-data-platform
 * @description: 运营指标，有关系的属性不换行
 * @author: kdh
 * @create: 2019-06-21 18:26
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@Data
public class IndicatorExcelVO {

    private String oneTimeCompletedRate = "";//一次性完成率
    private String circleOneTimeCompletedRate = "";//一次性完成率环比

    private String timelyReturnGoodsRate = "";//返货及时率
    private String circleTimelyReturnGoodsRate = "";//返货及时率环比

    private String timelyOutDeliverRate = "";//外发及时率
    private String circleTimelyOutDeliverRate = "";//外发及时率环比

    private String overtimeNotOutDeliverTotal = "";//超时未外发票数
    private String overtimeNotOutDeliverRate = "";//未外发超时率

    private String timelyTrunkRate = "";//干线运行及时率
    private String circleTimelyTrunkRate = "";//干线运行及时率

    private String overtimeNotTrunkEndTotal = "";//超时未干结票数
    private String overtimeNotTrunkEndRate = "";//未干结超时率

    private String timelyAppointmentRate = "";//预约及时率
    private String circleTimelyAppointmentRate = "";//预约及时率环比

    private String overtimeNotAppointmentTotal = "";//累计超时未预约票数
    private String overtimeNotAppointmentRate = "";//未预约超时率

    private String timelyInstallRate = "";//安装准时率
    private String circleTimelyInstallRate = "";//安装准时率环比

    private String h48TimelyInstallRate = "";//48小时安装及时率
    private String circleH48TimelyInstallRate = "";//48小时安装及时率环比

    private String overtimeNotInstallTotal = "";//累计超时未安装票数
    private String overtimeNotInstallRate = "";//未安装超时率

}
