package com.yiziton.dataimport.waybill.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.yiziton.dataimport.waybill.action.HistoryDataImportAction;
import com.yiziton.dataimport.waybill.action.OrderAction;
import com.yiziton.dataimport.waybill.action.SysExceptionDealAction;
import com.yiziton.dataimport.waybill.vo.SysExceptionWayBillVO;
import com.yiziton.dataimport.waybill.vo.SysWayBillVO;
import com.yiziton.dataweb.waybill.vo.ImportConditionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 序号错误修复线程
 * @Author: xiaoHong
 * @Date: 2019/5/4
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@JobHandler(value="orderNoRepairJob")
@Component
@Slf4j
public class OrderNoRepairJob  extends IJobHandler {

    @Autowired
    private OrderAction orderAction;

    @Autowired
    private SysExceptionDealAction sysExceptionDealAction;

    @Autowired
    HistoryDataImportAction historyDataImportAction;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        log.info("orderNoRepairJob start");
        ReturnT returnT = new ReturnT();
        SysExceptionWayBillVO sysExceptionWayBillVO = orderAction.getAllExceptionWaybillNo(param);

        List<String>  wayBillNos = sysExceptionWayBillVO.getWayBillNos();

        if(wayBillNos != null && wayBillNos.size() > 0){
            if(wayBillNos.size() <=100){
                SysWayBillVO sysWayBillVO = new SysWayBillVO();
                sysWayBillVO.setWayBillNos(wayBillNos);
                sysExceptionDealAction.deleteWayBillInfoCache(sysWayBillVO);
                ImportConditionVO importConditionVO = new ImportConditionVO();
                importConditionVO.setWaybillIds(wayBillNos);
                String result = historyDataImportAction.dataImport(importConditionVO);
                log.info("orderNoRepairJob info:" + result);
            }else{
                log.error("修复的异常序号数量大于100，请排查原因");
                returnT.setContent("修复的异常序号数量大于100，请排查原因");
                returnT.setMsg("修复的异常序号数量大于100，请排查原因");
                returnT.setCode(ReturnT.FAIL_CODE);
                return returnT;
            }
        }
        log.info("orderNoRepairJob end");
        return ReturnT.SUCCESS;
    }
}
