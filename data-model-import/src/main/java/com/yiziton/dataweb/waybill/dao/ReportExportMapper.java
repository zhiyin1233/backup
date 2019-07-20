package com.yiziton.dataweb.waybill.dao;


import com.yiziton.dataweb.waybill.vo.*;
import com.yiziton.dataweb.waybill.vo.report_vo.CustomerProfitReportsVO;
import com.yiziton.dataweb.waybill.vo.report_vo.IncomeStatementReportsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wuxinghai
 * @date 2019-03-18
 */
public interface ReportExportMapper {

    /**客户毛利报表*/
    List<CustomerProfitReportsVO> selectCustomerReports(@Param("label") String label);
    /**工程业务部业务毛利报表*/
    List<CustomerProfitReportsVO> selectEngineeringReports(@Param("services") List<Integer> services);
    /**运单收入报表报表*/
    List<IncomeStatementReportsVo> selectIncomeStatementReports(@Param("consignorList") List<String> consignorList);
    /** 仓储枢纽-外发货量（方） */
    List<OutDeliveryVolumeVO> selectOutDeliveryVolume();
    /** 运力管理部-整车业务毛利率
     * @param labels
     * @param services*/
    List<VehicleBusinessGrossProfitRateVO> selectVehicleBusinessGrossProfitRate(@Param("labels") List<String> labels, @Param("services") List<Integer> services);

    /**
     * 运单明细，安装费，配送费
     */
    List<WaybillInstallDeliveryFeeVO> selectWaybillInstallDeliveryFee(@Param("startTime") String startTime, @Param("endTime") String endTime);
    /**
     * 运力管理部报表-按承运商导明细报表-前一天
     * @return
     */
    List<CapacityManagementVO> selectCapacityManagementDay();

    /**
     * 运力管理部报表-按承运商导明细报表-当月
     * @return
     */
    List<CapacityManagementVO> selectCapacityManagementMonty();

    /**
     * 安装成本明细报表
     * @param startTime
     * @param endTime
     * @return
     */
    List<InstallCostDetailedVO> installCostDetailedReports(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     *
     * @return
     */
    List<StandardAndNonStandardCustomerVO> selectStandardAndNonStandardCustomer();

    /**
     *
     * @return
     */
    List<CarrierTrunkBenefitAnalysisVO> selectCarrierTrunkBenefitAnalysis();

    /**
     *
     * @return
     */
    List<HubTrunkBenefitAnalysisVO> selectHubTrunkBenefitAnalysis();
}
