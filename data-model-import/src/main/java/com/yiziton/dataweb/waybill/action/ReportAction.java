package com.yiziton.dataweb.waybill.action;

import com.yiziton.commons.utils.DateUtil;
import com.yiziton.dataweb.core.action.contentType.excel.ExcelFile;
import com.yiziton.dataweb.core.annotation.Action;
import com.yiziton.dataweb.waybill.service.ReportService;
import com.yiziton.dataweb.waybill.utils.GridRequest;
import com.yiziton.dataweb.waybill.utils.excel.ExportExcelUtils;
import com.yiziton.dataweb.waybill.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.List;

/**
 * @Description:
 * @Author: kdh
 * @Date: 2018/12/3
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Action("report")
@Slf4j
public class ReportAction {

    @Autowired
    ReportService reportService;
    @Autowired
    ExportExcelUtils exportExcelUtils;

    /**
     * 运单时效展示
     */
    public Page<WayBillAgingExportVO> findWaybillAging(GridRequest gridRequest, ReportConditionVO conditionVO) throws Exception {
        Timestamp openBillTimeEnd = conditionVO.getOpenBillTimeEnd();
        if(openBillTimeEnd != null){
            conditionVO.setOpenBillTimeEnd(new Timestamp(openBillTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Timestamp signTimeEnd = conditionVO.getSignTimeEnd();
        if(signTimeEnd != null){
            conditionVO.setSignTimeEnd(new Timestamp(signTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Timestamp homeCheckTimeEnd = conditionVO.getHomeCheckTimeEnd();
        if(homeCheckTimeEnd != null){
            conditionVO.setHomeCheckTimeEnd(new Timestamp(homeCheckTimeEnd.getTime()+ 86400000L));//加上一天
        }
        List<WayBillAgingExportVO> data = reportService.findWayBillAging(gridRequest, conditionVO);
        Long total = reportService.countWayBillInfoForReport(conditionVO);

        return new PageImpl(data, new PageRequest(gridRequest.getPage() - 1, gridRequest.getSize()), total);
    }

    /**
     * 运单时效报表本地导出
     *
     * @param conditionVO
     * @throws Exception
     */
    public void exportReportForWaybillAgingLocal(ReportConditionVO conditionVO) throws Exception {
        Timestamp openBillTimeEnd = conditionVO.getOpenBillTimeEnd();
        if(openBillTimeEnd != null){
            conditionVO.setOpenBillTimeEnd(new Timestamp(openBillTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Timestamp signTimeEnd = conditionVO.getSignTimeEnd();
        if(signTimeEnd != null){
            conditionVO.setSignTimeEnd(new Timestamp(signTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Timestamp trunkEndTimeEnd = conditionVO.getTrunkEndTimeEnd();
        if(trunkEndTimeEnd != null){
            conditionVO.setTrunkEndTimeEnd(new Timestamp(trunkEndTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Workbook workbook = reportService.exportReportForWaybillAging(conditionVO);
        String outPath = "D:\\运单时效报表"+System.currentTimeMillis()+".xlsx";
        OutputStream os = null;
        try {
            //根据路径创建输出流
            //如果是在controller里可以使用reponse.getOutputStream()方法获取输出流
            os = new FileOutputStream(outPath);
            workbook.write(os);
        } catch (Exception e) {
            System.out.println("导出出错");
            e.printStackTrace();
        }finally {
            try {
                if (os != null) {
                    os.close();
                }
                workbook.close();
            } catch (Exception e) {
                System.out.println("workbook 流关闭失败");
            }
        }
    }

    /**
     * 运单时效报表
     *
     * @param conditionVO
     * @throws Exception
     */
    public ExcelFile exportReportForWaybillAging(ReportConditionVO conditionVO) throws Exception {
        Timestamp openBillTimeEnd = conditionVO.getOpenBillTimeEnd();
        if(openBillTimeEnd != null){
            conditionVO.setOpenBillTimeEnd(new Timestamp(openBillTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Timestamp signTimeEnd = conditionVO.getSignTimeEnd();
        if(signTimeEnd != null){
            conditionVO.setSignTimeEnd(new Timestamp(signTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Timestamp homeCheckTimeEnd = conditionVO.getHomeCheckTimeEnd();
        if(homeCheckTimeEnd != null){
            conditionVO.setHomeCheckTimeEnd(new Timestamp(homeCheckTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Workbook workbook = reportService.exportReportForWaybillAging(conditionVO);
        ExcelFile file = new ExcelFile();
        file.setFileName("运单时效报表" + DateUtil.fromDateH());
        file.setWorkbook(workbook);
        return file;
    }

    /**
     * 标准利润展示
     */
    public Page<StandardProfitExportVO> findStandardProfit(GridRequest gridRequest, ReportConditionVO conditionVO) throws Exception {
        Timestamp openBillTimeEnd = conditionVO.getOpenBillTimeEnd();
        if(openBillTimeEnd != null){
            conditionVO.setOpenBillTimeEnd(new Timestamp(openBillTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Timestamp signTimeEnd = conditionVO.getSignTimeEnd();
        if(signTimeEnd != null){
            conditionVO.setSignTimeEnd(new Timestamp(signTimeEnd.getTime()+ 86400000L));//加上一天
        }
        List<StandardProfitExportVO> data = reportService.findStandardProfit(gridRequest, conditionVO);
        Long total = reportService.countWayBillInfoForReport(conditionVO);
        return new PageImpl(data, new PageRequest(gridRequest.getPage() - 1, gridRequest.getSize()), total);
    }

    /**
     * 标准利润报表
     *
     * @param conditionVO
     * @throws Exception
     */
    public ExcelFile exportReportForStandardProfit(ReportConditionVO conditionVO) throws Exception {
        Timestamp openBillTimeEnd = conditionVO.getOpenBillTimeEnd();
        if(openBillTimeEnd != null){
            conditionVO.setOpenBillTimeEnd(new Timestamp(openBillTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Timestamp signTimeEnd = conditionVO.getSignTimeEnd();
        if(signTimeEnd != null){
            conditionVO.setSignTimeEnd(new Timestamp(signTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Workbook workbook = reportService.exportReportForStandardProfit(conditionVO);
        ExcelFile file = new ExcelFile();
        file.setFileName("标准利润报表" + DateUtil.fromDateH());
        file.setWorkbook(workbook);
        return file;
    }

    /**
     * 运单时效报表
     *
     * @param conditionVO
     * @throws Exception
     */
    public void exportReportForStandardProfitLocal(ReportConditionVO conditionVO) throws Exception {
        Timestamp openBillTimeEnd = conditionVO.getOpenBillTimeEnd();
        if(openBillTimeEnd != null){
            conditionVO.setOpenBillTimeEnd(new Timestamp(openBillTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Timestamp signTimeEnd = conditionVO.getSignTimeEnd();
        if(signTimeEnd != null){
            conditionVO.setSignTimeEnd(new Timestamp(signTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Workbook workbook = reportService.exportReportForStandardProfit(conditionVO);
        String outPath = "D:\\标准利润报表"+System.currentTimeMillis()+".xlsx";
        OutputStream os = null;
        try {
            //根据路径创建输出流
            //如果是在controller里可以使用reponse.getOutputStream()方法获取输出流
            os = new FileOutputStream(outPath);
            workbook.write(os);
        } catch (Exception e) {
            System.out.println("导出出错");
            e.printStackTrace();
        }finally {
            try {
                if (os != null) {
                    os.close();
                }
                workbook.close();
            } catch (Exception e) {
                System.out.println("workbook 流关闭失败");
            }
        }
    }

    /**
     * 异常利润展示
     */
    public Page<AbnormalProfitExportVO> findAbnormalProfit(GridRequest gridRequest, ReportConditionVO conditionVO) throws Exception {
        Timestamp openBillTimeEnd = conditionVO.getOpenBillTimeEnd();
        if(openBillTimeEnd != null){
            conditionVO.setOpenBillTimeEnd(new Timestamp(openBillTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Timestamp signTimeEnd = conditionVO.getSignTimeEnd();
        if(signTimeEnd != null){
            conditionVO.setSignTimeEnd(new Timestamp(signTimeEnd.getTime()+ 86400000L));//加上一天
        }
        List<AbnormalProfitExportVO> data = reportService.findAbnormalProfit(gridRequest, conditionVO);
        Long total = reportService.countWayBillInfoForReport(conditionVO);
        return new PageImpl(data, new PageRequest(gridRequest.getPage() - 1, gridRequest.getSize()), total);
    }

    /**
     * 异常利润报表
     *
     * @param conditionVO
     * @throws Exception
     */
    public ExcelFile exportReportForAbnormalProfit(ReportConditionVO conditionVO) throws Exception {
        Timestamp openBillTimeEnd = conditionVO.getOpenBillTimeEnd();
        if(openBillTimeEnd != null){
            conditionVO.setOpenBillTimeEnd(new Timestamp(openBillTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Timestamp signTimeEnd = conditionVO.getSignTimeEnd();
        if(signTimeEnd != null){
            conditionVO.setSignTimeEnd(new Timestamp(signTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Workbook workbook = reportService.exportReportForAbnormalProfit(conditionVO);
        ExcelFile file = new ExcelFile();
        file.setFileName("标准利润报表" + DateUtil.fromDateH());
        file.setWorkbook(workbook);
        return file;
    }

    /**
     * 业绩统计展示
     */
    public Page<PerformanceStatisticsExportVO> findPerformanceStatistics(GridRequest gridRequest, ReportConditionVO conditionVO) throws Exception {
        Timestamp openBillTimeEnd = conditionVO.getOpenBillTimeEnd();
        if(openBillTimeEnd != null){
            conditionVO.setOpenBillTimeEnd(new Timestamp(openBillTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Timestamp signTimeEnd = conditionVO.getSignTimeEnd();
        if(signTimeEnd != null){
            conditionVO.setSignTimeEnd(new Timestamp(signTimeEnd.getTime()+ 86400000L));//加上一天
        }
        List<PerformanceStatisticsExportVO> data = reportService.findPerformanceStatistics(gridRequest, conditionVO);
        Long total = reportService.countWayBillInfoForReport(conditionVO);
        return new PageImpl(data, new PageRequest(gridRequest.getPage() - 1, gridRequest.getSize()), total);
    }

    /**
     * 业绩统计报表
     */
    public ExcelFile exportReportForPerformanceStatistics(ReportConditionVO conditionVO) throws Exception {
        Timestamp openBillTimeEnd = conditionVO.getOpenBillTimeEnd();
        if(openBillTimeEnd != null){
            conditionVO.setOpenBillTimeEnd(new Timestamp(openBillTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Timestamp signTimeEnd = conditionVO.getSignTimeEnd();
        if(signTimeEnd != null){
            conditionVO.setSignTimeEnd(new Timestamp(signTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Workbook workbook = reportService.exportReportForPerformanceStatistics(conditionVO);
        ExcelFile file = new ExcelFile();
        file.setFileName("业绩统计报表" + DateUtil.fromDateH());
        file.setWorkbook(workbook);
        return file;
    }

    /**
     * 核销报表展示
     */
    public Page<CheckExportVO> findCheck(GridRequest gridRequest, ReportConditionVO conditionVO) throws Exception {
        Timestamp openBillTimeEnd = conditionVO.getOpenBillTimeEnd();
        if(openBillTimeEnd != null){
            conditionVO.setOpenBillTimeEnd(new Timestamp(openBillTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Timestamp signTimeEnd = conditionVO.getSignTimeEnd();
        if(signTimeEnd != null){
            conditionVO.setSignTimeEnd(new Timestamp(signTimeEnd.getTime()+ 86400000L));//加上一天
        }
        List<CheckExportVO> data = reportService.findCheck(gridRequest, conditionVO);
        Long total = reportService.countWayBillInfoForReport(conditionVO);
        return new PageImpl(data, new PageRequest(gridRequest.getPage() - 1, gridRequest.getSize()), total);
    }

    /**
     * 核销报表
     */
    public ExcelFile exportReportForCheck(ReportConditionVO conditionVO) throws Exception {
        Timestamp openBillTimeEnd = conditionVO.getOpenBillTimeEnd();
        if(openBillTimeEnd != null){
            conditionVO.setOpenBillTimeEnd(new Timestamp(openBillTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Timestamp signTimeEnd = conditionVO.getSignTimeEnd();
        if(signTimeEnd != null){
            conditionVO.setSignTimeEnd(new Timestamp(signTimeEnd.getTime()+ 86400000L));//加上一天
        }
        Workbook workbook = reportService.exportReportForCheck(conditionVO);
        ExcelFile file = new ExcelFile();
        file.setFileName("核销报表" + DateUtil.fromDateH());
        file.setWorkbook(workbook);
        return file;
    }

    /**
     * 业务员
     * @param salesmanVO
     * @return
     */
    public SalesmanVO findSalesmanList(SalesmanVO salesmanVO) throws Exception {
        return reportService.findSalesmanList(salesmanVO);
    }

    /**
     * 发货人
     * @param consignorVO
     * @return
     */
    public ConsignorVO findConsignorList(ConsignorVO consignorVO) throws Exception {
        return reportService.findConsignorList(consignorVO);
    }

    /**
     * 收货人
     * @param receviceVO
     * @return
     */
    public ReceviceVO findReceviceList(ReceviceVO receviceVO) throws Exception {
        return reportService.findReceviceList(receviceVO);
    }

    /**
     * 二级商家
     * @param secondCustomerVO
     * @return
     */
    public SecondCustomerVO findSecondCustomerList(SecondCustomerVO secondCustomerVO) throws Exception {
        return reportService.findSecondCustomerList(secondCustomerVO);
    }

    /**
     * 开单网点
     * @param openBillNodeVO
     * @return
     */
    public OpenBillNodeVO findOpenBillNodeList(OpenBillNodeVO openBillNodeVO) throws Exception {
        return reportService.findOpenBillNodeList(openBillNodeVO);
    }


    /**
     * 安装师傅
     * @param masterVO
     * @return
     */
    public MasterVO findMasterList(MasterVO masterVO) throws Exception {
        return reportService.findMasterList(masterVO);
    }

    /**
     * 安装师傅所属网点=末端网点
     * @param masterNodeVO
     * @return
     */
    public MasterNodeVO findMasterNodeList(MasterNodeVO masterNodeVO) throws Exception {
        return reportService.findMasterNodeList(masterNodeVO);
    }

    /**
     * 承运商
     * @param carrierVO
     * @return
     */
    public CarrierVO findCarrierList(CarrierVO carrierVO) throws Exception {
        return reportService.findCarrierList(carrierVO);
    }

    /**
     * 获取下拉选择列表
     * @param commonVO
     * @return
     */
    public Page<CommonVO> findCommonList(GridRequest gridRequest, CommonVO commonVO) throws Exception {
        SelectConditionVO selectConditionVO = null;
        CommonKey key = commonVO.getKey();
        switch (key){
            case SALESMAN:
                selectConditionVO = new SelectConditionVO();
                selectConditionVO.setTable("waybill_info");
                selectConditionVO.setColumn("salesman");
                selectConditionVO.setValue(commonVO.getValue());
                break;
            case OPEN_BILL_NODE:
                selectConditionVO = new SelectConditionVO();
                selectConditionVO.setTable("waybill_info");
                selectConditionVO.setColumn("open_bill_node");
                selectConditionVO.setValue(commonVO.getValue());
                break;
            case CARRIER:
                selectConditionVO = new SelectConditionVO();
                selectConditionVO.setTable("carrier_info");
                selectConditionVO.setColumn("carrier");
                selectConditionVO.setValue(commonVO.getValue());
                break;
            case RECEVICE:
                selectConditionVO = new SelectConditionVO();
                selectConditionVO.setTable("recevice_info");
                selectConditionVO.setColumn("name");
                selectConditionVO.setValue(commonVO.getValue());
                break;
            case MASTER:
                selectConditionVO = new SelectConditionVO();
                selectConditionVO.setTable("master_info");
                selectConditionVO.setColumn("master_name");
                selectConditionVO.setValue(commonVO.getValue());
                break;
            case MASTER_NODE:
                selectConditionVO = new SelectConditionVO();
                selectConditionVO.setTable("master_info");
                selectConditionVO.setColumn("master_node");
                selectConditionVO.setValue(commonVO.getValue());
                break;
            case CONSIGNOR:
                selectConditionVO = new SelectConditionVO();
                selectConditionVO.setTable("consignor_info");
                selectConditionVO.setColumn("name");
                selectConditionVO.setValue(commonVO.getValue());
                break;
            case SECOND_CUSTOMER:
                selectConditionVO = new SelectConditionVO();
                selectConditionVO.setTable("consignor_info");
                selectConditionVO.setColumn("second_name");
                selectConditionVO.setValue(commonVO.getValue());
                break;
            default:
                return new PageImpl(null, new PageRequest(0, 0), 0L);
        }

        List<CommonVO> data = reportService.selectForCommon(gridRequest, selectConditionVO);
        Long total = reportService.countForCommon(selectConditionVO);
        return new PageImpl(data, new PageRequest(gridRequest.getPage() - 1, gridRequest.getSize()), total);
    }


}
