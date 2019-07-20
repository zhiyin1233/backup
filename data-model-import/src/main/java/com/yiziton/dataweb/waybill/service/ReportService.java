package com.yiziton.dataweb.waybill.service;

import com.yiziton.dataweb.waybill.utils.GridRequest;
import com.yiziton.dataweb.waybill.vo.*;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public interface ReportService {

    /**
     * 运单时效查询
     */
    List<WayBillAgingExportVO> findWayBillAging(GridRequest gridRequest, ReportConditionVO conditionVO) throws Exception;

    /**
     * 运单时效报表导出
     *
     * @param conditionVO
     * @return
     */
    Workbook exportReportForWaybillAging(ReportConditionVO conditionVO) throws Exception;

    /**
     * 标准利润报表
     *
     * @param conditionVO
     * @return
     */
    Workbook exportReportForStandardProfit(ReportConditionVO conditionVO) throws Exception;

    /**
     * 标准利润列表
     *
     * @param conditionVO
     * @return
     */
    public List<StandardProfitExportVO> findStandardProfit(GridRequest gridRequest, ReportConditionVO conditionVO) throws Exception;

    /**
     * 异常利润报表
     *
     * @param conditionVO
     * @return
     */
    Workbook exportReportForAbnormalProfit(ReportConditionVO conditionVO) throws Exception;

    /**
     * 异常利润列表
     *
     * @param conditionVO
     * @return
     */
    public List<AbnormalProfitExportVO> findAbnormalProfit(GridRequest gridRequest, ReportConditionVO conditionVO) throws Exception;

    /**
     * 业绩统计查询
     */
    List<PerformanceStatisticsExportVO> findPerformanceStatistics(GridRequest gridRequest, ReportConditionVO conditionVO) throws Exception;

    /**
     * 业绩统计报表
     *
     * @param conditionVO
     * @return
     */
    Workbook exportReportForPerformanceStatistics(ReportConditionVO conditionVO) throws Exception;

    /**
     * 核销报表查询
     */
    List<CheckExportVO> findCheck(GridRequest gridRequest, ReportConditionVO conditionVO) throws Exception;

    /**
     * 核销报表
     *
     * @param conditionVO
     * @return
     */
    Workbook exportReportForCheck(ReportConditionVO conditionVO) throws Exception;

    Long countWayBillInfoForReport(ReportConditionVO conditionVO) throws Exception;

    SalesmanVO findSalesmanList(SalesmanVO salesmanVO) throws Exception;

    ConsignorVO findConsignorList(ConsignorVO consignorVO) throws Exception;

    ReceviceVO findReceviceList(ReceviceVO receviceVO) throws Exception;

    SecondCustomerVO findSecondCustomerList(SecondCustomerVO secondCustomerVO) throws Exception;

    OpenBillNodeVO findOpenBillNodeList(OpenBillNodeVO openBillNodeVO) throws Exception;

    MasterVO findMasterList(MasterVO masterVO) throws Exception;

    MasterNodeVO findMasterNodeList(MasterNodeVO masterNodeVO) throws Exception;

    CarrierVO findCarrierList(CarrierVO carrierVO) throws Exception;


    List<CommonVO> selectForCommon(GridRequest gridRequest, SelectConditionVO conditionVO) throws Exception;

    Long countForCommon(SelectConditionVO conditionVO) throws Exception;
}
