package com.yiziton.dataweb.waybill.service;

import com.yiziton.dataweb.waybill.vo.IndicatorConditionVO;
import com.yiziton.dataweb.waybill.vo.IndicatorExcelVO;
import com.yiziton.dataweb.waybill.vo.IndicatorVO;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public interface OperationIndicatorService {

    public List<IndicatorVO> getIndicators(IndicatorConditionVO indicatorConditionVO) throws Exception;

    public IndicatorExcelVO getIndicator(IndicatorConditionVO indicatorConditionVO) throws Exception;

    public Workbook exportIndicator(IndicatorConditionVO indicatorConditionVO) throws Exception;
}
