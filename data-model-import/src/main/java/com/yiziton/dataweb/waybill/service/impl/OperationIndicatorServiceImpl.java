package com.yiziton.dataweb.waybill.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.yiziton.dataweb.waybill.dao.OperationIndicatorMapper;
import com.yiziton.dataweb.waybill.service.OperationIndicatorService;
import com.yiziton.dataweb.waybill.vo.IndicatorConditionVO;
import com.yiziton.dataweb.waybill.vo.IndicatorExcelVO;
import com.yiziton.dataweb.waybill.vo.IndicatorVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service("operationIndicatorService")
@Slf4j
public class OperationIndicatorServiceImpl implements OperationIndicatorService {

    @Autowired
    OperationIndicatorMapper operationIndicatorMapper;

    @Override
    public List<IndicatorVO> getIndicators(IndicatorConditionVO indicatorConditionVO) throws Exception {
        try {
            IndicatorExcelVO indicatorExcelVO = new IndicatorExcelVO();
            if (!(indicatorConditionVO.getAssessTimeBegin() == null || indicatorConditionVO.getAssessTimeEnd() == null
                    || indicatorConditionVO.getCircleAssessTimeBegin() == null || indicatorConditionVO.getCircleAssessTimeEnd() == null)) {
                indicatorExcelVO = getIndicator(indicatorConditionVO);
            }
            if (indicatorExcelVO == null) {
                indicatorExcelVO = new IndicatorExcelVO();
            }
            List<IndicatorVO> indicatorVOS = new ArrayList<IndicatorVO>();

            //订单一次性完成率	全公司	考核		{{oneTimeCompletedRate}}	{{circleOneTimeCompletedRate}}
            IndicatorVO oneTimeCompleted = new IndicatorVO();
            oneTimeCompleted.setSerialNumber(1);
            oneTimeCompleted.setPrimaryIndicator("订单一次性完成率");
            oneTimeCompleted.setAssessmentDepartment("全公司");
            oneTimeCompleted.setAssessmentMethod("考核");
            oneTimeCompleted.setTarget(null);
            oneTimeCompleted.setActualCompletion(indicatorExcelVO.getOneTimeCompletedRate());
            oneTimeCompleted.setCircleCompletion(indicatorExcelVO.getCircleOneTimeCompletedRate());
            indicatorVOS.add(oneTimeCompleted);

            //返货及时率	全公司	考核		{{timelyReturnGoodsRate}}	{{circleTimelyReturnGoodsRate}}
            IndicatorVO timelyReturn = new IndicatorVO();
            timelyReturn.setSerialNumber(2);
            timelyReturn.setPrimaryIndicator("返货及时率");
            timelyReturn.setAssessmentDepartment("全公司");
            timelyReturn.setAssessmentMethod("考核");
            timelyReturn.setTarget(null);
            timelyReturn.setActualCompletion(indicatorExcelVO.getTimelyReturnGoodsRate());
            timelyReturn.setCircleCompletion(indicatorExcelVO.getCircleTimelyReturnGoodsRate());
            indicatorVOS.add(timelyReturn);

            //外发及时率	仓储分拨中心	考核		{{timelyOutDeliverRate}}	{{circleTimelyOutDeliverRate}}
            IndicatorVO timelyOutDeliver = new IndicatorVO();
            timelyOutDeliver.setSerialNumber(3);
            timelyOutDeliver.setPrimaryIndicator("外发及时率");
            timelyOutDeliver.setAssessmentDepartment("仓储分拨中心");
            timelyOutDeliver.setAssessmentMethod("考核");
            timelyOutDeliver.setTarget(null);
            timelyOutDeliver.setActualCompletion(indicatorExcelVO.getTimelyOutDeliverRate());
            timelyOutDeliver.setCircleCompletion(indicatorExcelVO.getCircleTimelyOutDeliverRate());
            indicatorVOS.add(timelyOutDeliver);

            //超时未外发票数	仓储分拨中心	观测		{{overtimeNotOutDeliverTotal}}
            IndicatorVO overtimeNotOutDeliver = new IndicatorVO();
            overtimeNotOutDeliver.setSerialNumber(4);
            overtimeNotOutDeliver.setPrimaryIndicator("超时未外发票数");
            overtimeNotOutDeliver.setAssessmentDepartment("仓储分拨中心");
            overtimeNotOutDeliver.setAssessmentMethod("观测");
            overtimeNotOutDeliver.setTarget(null);
            overtimeNotOutDeliver.setActualCompletion(indicatorExcelVO.getOvertimeNotOutDeliverTotal());
            overtimeNotOutDeliver.setCircleCompletion(null);
            indicatorVOS.add(overtimeNotOutDeliver);

            //未外发超时率	仓储分拨中心	观测		{{overtimeNotOutDeliverRate}}
            IndicatorVO overtimeNotOutDeliver2 = new IndicatorVO();
            overtimeNotOutDeliver2.setSerialNumber(5);
            overtimeNotOutDeliver2.setPrimaryIndicator("未外发超时率");
            overtimeNotOutDeliver2.setAssessmentDepartment("仓储分拨中心");
            overtimeNotOutDeliver2.setAssessmentMethod("观测");
            overtimeNotOutDeliver2.setTarget(null);
            overtimeNotOutDeliver2.setActualCompletion(indicatorExcelVO.getOvertimeNotOutDeliverRate());
            overtimeNotOutDeliver2.setCircleCompletion(null);
            indicatorVOS.add(overtimeNotOutDeliver2);

            //预约及时率	末端交付中心	观测		{{timelyAppointmentRate}}	{{circleTimelyAppointmentRate}}
            IndicatorVO timelyAppointment = new IndicatorVO();
            timelyAppointment.setSerialNumber(6);
            timelyAppointment.setPrimaryIndicator("预约及时率");
            timelyAppointment.setAssessmentDepartment("末端交付中心");
            timelyAppointment.setAssessmentMethod("观测");
            timelyAppointment.setTarget(null);
            timelyAppointment.setActualCompletion(indicatorExcelVO.getTimelyAppointmentRate());
            timelyAppointment.setCircleCompletion(indicatorExcelVO.getCircleTimelyAppointmentRate());
            indicatorVOS.add(timelyAppointment);

            //累计超时未预约票数	末端交付中心	考核		{{overtimeNotAppointmentTotal}}
            IndicatorVO overtimeNotAppointment = new IndicatorVO();
            overtimeNotAppointment.setSerialNumber(7);
            overtimeNotAppointment.setPrimaryIndicator("累计超时未预约票数");
            overtimeNotAppointment.setAssessmentDepartment("末端交付中心");
            overtimeNotAppointment.setAssessmentMethod("考核");
            overtimeNotAppointment.setTarget(null);
            overtimeNotAppointment.setActualCompletion(indicatorExcelVO.getOvertimeNotAppointmentTotal());
            overtimeNotAppointment.setCircleCompletion(null);
            indicatorVOS.add(overtimeNotAppointment);

            //未预约超时率	末端交付中心	考核		{{overtimeNotAppointmentRate}}
            IndicatorVO overtimeNotAppointment2 = new IndicatorVO();
            overtimeNotAppointment2.setSerialNumber(8);
            overtimeNotAppointment2.setPrimaryIndicator("未预约超时率");
            overtimeNotAppointment2.setAssessmentDepartment("末端交付中心");
            overtimeNotAppointment2.setAssessmentMethod("考核");
            overtimeNotAppointment2.setTarget(null);
            overtimeNotAppointment2.setActualCompletion(indicatorExcelVO.getOvertimeNotAppointmentRate());
            overtimeNotAppointment2.setCircleCompletion(null);
            indicatorVOS.add(overtimeNotAppointment2);

            //安装准时率	末端交付中心	考核		{{timelyInstallRate}}	{{circleTimelyInstallRate}}
            IndicatorVO timelyInstall = new IndicatorVO();
            timelyInstall.setSerialNumber(9);
            timelyInstall.setPrimaryIndicator("安装准时率");
            timelyInstall.setAssessmentDepartment("末端交付中心");
            timelyInstall.setAssessmentMethod("考核");
            timelyInstall.setTarget(null);
            timelyInstall.setActualCompletion(indicatorExcelVO.getTimelyInstallRate());
            timelyInstall.setCircleCompletion(indicatorExcelVO.getCircleTimelyInstallRate());
            indicatorVOS.add(timelyInstall);

            //48小时安装及时率	末端交付中心	考核		{{h48TimelyInstallRate}}	{{circleH48TimelyInstallRate}}
            IndicatorVO h48TimelyInstall = new IndicatorVO();
            h48TimelyInstall.setSerialNumber(10);
            h48TimelyInstall.setPrimaryIndicator("48小时安装及时率");
            h48TimelyInstall.setAssessmentDepartment("末端交付中心");
            h48TimelyInstall.setAssessmentMethod("考核");
            h48TimelyInstall.setTarget(null);
            h48TimelyInstall.setActualCompletion(indicatorExcelVO.getH48TimelyInstallRate());
            h48TimelyInstall.setCircleCompletion(indicatorExcelVO.getCircleH48TimelyInstallRate());
            indicatorVOS.add(h48TimelyInstall);

            //累计超时未安装票数	末端交付中心	考核		{{overtimeNotInstallTotal}}
            IndicatorVO overtimeNotInstall = new IndicatorVO();
            overtimeNotInstall.setSerialNumber(11);
            overtimeNotInstall.setPrimaryIndicator("累计超时未安装票数");
            overtimeNotInstall.setAssessmentDepartment("末端交付中心");
            overtimeNotInstall.setAssessmentMethod("考核");
            overtimeNotInstall.setTarget(null);
            overtimeNotInstall.setActualCompletion(indicatorExcelVO.getOvertimeNotInstallTotal());
            overtimeNotInstall.setCircleCompletion(null);
            indicatorVOS.add(overtimeNotInstall);

            //未安装超时率	末端交付中心	考核		{{overtimeNotInstallRate}}
            IndicatorVO overtimeNotInstall2 = new IndicatorVO();
            overtimeNotInstall2.setSerialNumber(12);
            overtimeNotInstall2.setPrimaryIndicator("未安装超时率");
            overtimeNotInstall2.setAssessmentDepartment("末端交付中心");
            overtimeNotInstall2.setAssessmentMethod("考核");
            overtimeNotInstall2.setTarget(null);
            overtimeNotInstall2.setActualCompletion(indicatorExcelVO.getOvertimeNotInstallRate());
            overtimeNotInstall2.setCircleCompletion(null);
            indicatorVOS.add(overtimeNotInstall2);

            return indicatorVOS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public IndicatorExcelVO getIndicator(IndicatorConditionVO indicatorConditionVO) throws Exception {
        IndicatorExcelVO indicatorExcelVO = new IndicatorExcelVO();
        //一次性完成率
        try {
            IndicatorExcelVO oneTimeCompletion = operationIndicatorMapper.getOneTimeCompletion(indicatorConditionVO);
            if (oneTimeCompletion != null) {
                indicatorExcelVO.setOneTimeCompletedRate(oneTimeCompletion.getOneTimeCompletedRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //一次性完成率环比
        try {
            IndicatorExcelVO circleOneTimeCompletion = operationIndicatorMapper.getCircleOneTimeCompletion(indicatorConditionVO);
            if (circleOneTimeCompletion != null) {
                indicatorExcelVO.setCircleOneTimeCompletedRate(circleOneTimeCompletion.getCircleOneTimeCompletedRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返货及时率
        try {
            IndicatorExcelVO timelyReturnGoods = operationIndicatorMapper.getTimelyReturnGoods(indicatorConditionVO);
            if (timelyReturnGoods != null) {
                indicatorExcelVO.setTimelyReturnGoodsRate(timelyReturnGoods.getTimelyReturnGoodsRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返货及时率环比
        try {
            IndicatorExcelVO circleTimelyReturnGoods = operationIndicatorMapper.getCircleTimelyReturnGoods(indicatorConditionVO);
            if (circleTimelyReturnGoods != null) {
                indicatorExcelVO.setCircleTimelyReturnGoodsRate(circleTimelyReturnGoods.getCircleTimelyReturnGoodsRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //外发及时率
        try {
            IndicatorExcelVO timelyOutDeliver = operationIndicatorMapper.getTimelyOutDeliver(indicatorConditionVO);
            if (timelyOutDeliver != null) {
                indicatorExcelVO.setTimelyOutDeliverRate(timelyOutDeliver.getTimelyOutDeliverRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //外发及时率环比
        try {
            IndicatorExcelVO circleTimelyOutDeliver = operationIndicatorMapper.getCircleTimelyOutDeliver(indicatorConditionVO);
            if (circleTimelyOutDeliver != null) {
                indicatorExcelVO.setCircleTimelyOutDeliverRate(circleTimelyOutDeliver.getCircleTimelyOutDeliverRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //超时未外发票数//未外发超时率
        try {
            IndicatorExcelVO overtimeNotOutDeliver = operationIndicatorMapper.getOvertimeNotOutDeliver(indicatorConditionVO);
            if (overtimeNotOutDeliver != null) {
                indicatorExcelVO.setOvertimeNotOutDeliverTotal(overtimeNotOutDeliver.getOvertimeNotOutDeliverTotal());
                indicatorExcelVO.setOvertimeNotOutDeliverRate(overtimeNotOutDeliver.getOvertimeNotOutDeliverRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //干线运行及时率
        //IndicatorVO timelyTrunk = operationIndicatorMapper.getOneTimeCompletion(indicatorCondition);
        //超时未干结票数//未干结超时率
        //IndicatorVO overtimeNotTrunkEnd = operationIndicatorMapper.getOneTimeCompletion(indicatorCondition);
        //预约及时率
        try {
            IndicatorExcelVO timelyAppointment = operationIndicatorMapper.getTimelyAppointment(indicatorConditionVO);
            if (timelyAppointment != null) {
                indicatorExcelVO.setTimelyAppointmentRate(timelyAppointment.getTimelyAppointmentRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //预约及时率环比
        try {
            IndicatorExcelVO circleTimelyAppointment = operationIndicatorMapper.getCircleTimelyAppointment(indicatorConditionVO);
            if (circleTimelyAppointment != null) {
                indicatorExcelVO.setCircleTimelyAppointmentRate(circleTimelyAppointment.getCircleTimelyAppointmentRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //累计超时未预约票数//未预约超时率
        try {
            IndicatorExcelVO overtimeNotAppointment = operationIndicatorMapper.getOvertimeNotAppointment(indicatorConditionVO);
            if (overtimeNotAppointment != null) {
                indicatorExcelVO.setOvertimeNotAppointmentTotal(overtimeNotAppointment.getOvertimeNotAppointmentTotal());
                indicatorExcelVO.setOvertimeNotAppointmentRate(overtimeNotAppointment.getOvertimeNotAppointmentRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //安装准时率
        try {
            IndicatorExcelVO timelyInstall = operationIndicatorMapper.getTimelyInstall(indicatorConditionVO);
            if (timelyInstall != null) {
                indicatorExcelVO.setTimelyInstallRate(timelyInstall.getTimelyInstallRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //安装准时率环比
        try {
            IndicatorExcelVO circleTimelyInstall = operationIndicatorMapper.getCircleTimelyInstall(indicatorConditionVO);
            if (circleTimelyInstall != null) {
                indicatorExcelVO.setCircleTimelyInstallRate(circleTimelyInstall.getCircleTimelyInstallRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 48小时安装及时率
        try {
            IndicatorExcelVO h48TimelyInstall = operationIndicatorMapper.getH48TimelyInstall(indicatorConditionVO);
            if (h48TimelyInstall != null) {
                indicatorExcelVO.setH48TimelyInstallRate(h48TimelyInstall.getH48TimelyInstallRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 48小时安装及时率环比
        try {
            IndicatorExcelVO circleH48TimelyInstall = operationIndicatorMapper.getCircleH48TimelyInstall(indicatorConditionVO);
            if (circleH48TimelyInstall != null) {
                indicatorExcelVO.setCircleH48TimelyInstallRate(circleH48TimelyInstall.getCircleH48TimelyInstallRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //累计超时未安装票数//未安装超时率
        try {
            IndicatorExcelVO overtimeNotInstall = operationIndicatorMapper.getOvertimeNotInstall(indicatorConditionVO);
            if (overtimeNotInstall != null) {
                indicatorExcelVO.setOvertimeNotInstallTotal(overtimeNotInstall.getOvertimeNotInstallTotal());
                indicatorExcelVO.setOvertimeNotInstallRate(overtimeNotInstall.getOvertimeNotInstallRate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return indicatorExcelVO;
    }

    @Override
    public Workbook exportIndicator(IndicatorConditionVO indicatorConditionVO) throws Exception {
        try {
            IndicatorExcelVO indicatorExcelVO = getIndicator(indicatorConditionVO);
            TemplateExportParams params = new TemplateExportParams();
            //params.setHeadingRows(2);
            //params.setHeadingStartRow(2);
            params.setTemplateUrl("template/运营规划部时效类指标.xls");
            Map<String, Object> map = object2Map(indicatorExcelVO);
            log.info(map.toString());
            Workbook book = ExcelExportUtil.exportExcel(params, map);
            return book;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 实体对象转成Map
     *
     * @param obj 实体对象
     * @return
     */
    public static Map<String, Object> object2Map(Object obj) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (obj == null) {
            return map;
        }
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}
