package com.yiziton.dataweb.waybill.dao;

import com.yiziton.dataweb.waybill.vo.IndicatorConditionVO;
import com.yiziton.dataweb.waybill.vo.IndicatorExcelVO;

/**
 * @program: bigdataplatform
 * @description: 运营管理>运营指标
 * @author: kdh
 * @create: 2019-05-28 18:30
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
public interface OperationIndicatorMapper {

    IndicatorExcelVO getOneTimeCompletion(IndicatorConditionVO indicatorCondition);
    IndicatorExcelVO getCircleOneTimeCompletion(IndicatorConditionVO indicatorCondition);

    IndicatorExcelVO getTimelyReturnGoods(IndicatorConditionVO indicatorCondition);
    IndicatorExcelVO getCircleTimelyReturnGoods(IndicatorConditionVO indicatorCondition);

    IndicatorExcelVO getTimelyOutDeliver(IndicatorConditionVO indicatorCondition);
    IndicatorExcelVO getCircleTimelyOutDeliver(IndicatorConditionVO indicatorCondition);

    IndicatorExcelVO getOvertimeNotOutDeliver(IndicatorConditionVO indicatorCondition);

    IndicatorExcelVO getTimelyAppointment(IndicatorConditionVO indicatorCondition);
    IndicatorExcelVO getCircleTimelyAppointment(IndicatorConditionVO indicatorCondition);

    IndicatorExcelVO getOvertimeNotAppointment(IndicatorConditionVO indicatorCondition);

    IndicatorExcelVO getTimelyInstall(IndicatorConditionVO indicatorCondition);
    IndicatorExcelVO getCircleTimelyInstall(IndicatorConditionVO indicatorCondition);

    IndicatorExcelVO getH48TimelyInstall(IndicatorConditionVO indicatorCondition);
    IndicatorExcelVO getCircleH48TimelyInstall(IndicatorConditionVO indicatorCondition);

    IndicatorExcelVO getOvertimeNotInstall(IndicatorConditionVO indicatorCondition);
}
