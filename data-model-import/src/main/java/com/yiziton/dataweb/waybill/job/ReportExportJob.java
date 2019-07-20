package com.yiziton.dataweb.waybill.job;

import com.alibaba.druid.util.StringUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.yiziton.dataweb.waybill.dao.ReportExportMapper;
import com.yiziton.dataweb.waybill.job.component.BusinessReportComponent;
import com.yiziton.dataweb.waybill.utils.MailUtil;
import com.yiziton.dataweb.waybill.vo.*;
import com.yiziton.dataweb.waybill.vo.report_vo.CustomerProfitReportsVO;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeBodyPart;
import java.util.*;

/**
 * @author: zhangSiYuan
 */
@JobHandler(value = "reportExportJob")
@Component
@Slf4j
public class ReportExportJob extends IJobHandler {
    @Autowired
    ReportExportMapper reportExportMapper;
    @Autowired
    MailUtil mailUtil;
    @Autowired
    BusinessReportComponent businessServiceReport;

    @Value("#{'${spring.mail.to-receiver}'.split(',')}")
    String[] receiver;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        log.info("ReportExportJob   start");
        if (!StringUtils.isEmpty(param)) {
            receiver = param.split(",");
        }
        String date = (new DateTime()).minusDays(1).toString("-MMdd");
        List<MimeBodyPart> mimeBodyParts = new ArrayList<>();
        List<String> consignorList = new ArrayList<>();
        consignorList.add("工程业务客户");
        //销售中心收入报表
        mimeBodyParts.add(businessServiceReport.selectIncomeStatementReports(Arrays.asList("非标准价格客户", "标准价格客户", "销售中心工程业务客户"), "销售中心收入报表" + date + ".xlsx"));
        //标准客户毛利率报表
//        mimeBodyParts.add(this.customerMimeBodyPart("标准价格客户","标准客户毛利率报表.xlsx"));
        //非标准价格客户毛利率报表
//        mimeBodyParts.add(this.customerMimeBodyPart("非标准价格客户","非标准价格客户毛利率报表.xlsx"));

        //工程业务部收入报表
        mimeBodyParts.add(businessServiceReport.selectIncomeStatementReports(consignorList, "工程业务部收入报表" + date + ".xlsx"));
        //整车业务毛利率
//        mimeBodyParts.add(this.engineeringReports(Arrays.asList(1, 2), "整车业务毛利率.xlsx"));
        //工程安装业务毛利率
        mimeBodyParts.add(this.engineeringReports(Arrays.asList(6, 9, 3, 10, 12, 5, 8), "工程安装业务毛利率" + date + ".xlsx"));
        //仓储枢纽-外发货量（方）
        mimeBodyParts.add(this.outDeliveryVolume("仓储枢纽-外发、揽货数据" + date + ".xlsx"));
        //运力管理部-工程部整车业务毛利率
        mimeBodyParts.add(this.vehicleBusinessGrossProfitRate(Arrays.asList("工程业务客户"), Arrays.asList(1, 2), "运力管理部-工程部整车业务毛利率" + date + ".xlsx"));
        //运力管理部-销售中心整车业务毛利率
        mimeBodyParts.add(this.vehicleBusinessGrossProfitRate(Arrays.asList("销售中心工程业务客户"), Arrays.asList(1, 2), "运力管理部-销售中心整车业务毛利率" + date + ".xlsx"));
        //运力管理部报表-按承运商导明细报表-当天
        mimeBodyParts.add(this.capacityManagementReportsDay("运力管理部报表-按承运商导明细报表" + date + ".xlsx"));
        //支装成本明细
        mimeBodyParts.add(this.selectWaybillInstallDeliveryFee("支装成本明细" + date + ".xlsx"));
        mimeBodyParts.add(this.installCostDetailedReports("安装成本明细报表" + date + ".xlsx"));
        mailUtil.sendEmail("效益报表 " + (new DateTime()).minusDays(1).toString("yyyy-MM-dd"), "", mimeBodyParts, receiver);
        log.info("ReportExportJob   end");
        return null;
    }

    /**
     * 工程业务部-毛利附件
     */
    public MimeBodyPart engineeringReports(List<Integer> services, String fileName) throws Exception {
        List<CustomerProfitReportsVO> result = reportExportMapper.selectEngineeringReports(services);
        List<Map<String, Object>> list = businessServiceReport.getCustomerMimeMaps(result);
        return businessServiceReport.getMimeBodyPart(list, fileName, businessServiceReport.getProfitMarginHeader());
    }

    /**
     * 标准客户毛利附件
     */
    public MimeBodyPart customerMimeBodyPart(String label, String fileName) throws Exception {
        List<CustomerProfitReportsVO> standardCustomer = reportExportMapper.selectCustomerReports(label);
        List<Map<String, Object>> list = businessServiceReport.getCustomerMimeMaps(standardCustomer);
        return businessServiceReport.getMimeBodyPart(list, fileName, businessServiceReport.getProfitMarginHeader());
    }

    /**
     * 仓储枢纽-外发货量（方）
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public MimeBodyPart outDeliveryVolume(String fileName) throws Exception {
        List<OutDeliveryVolumeVO> result = reportExportMapper.selectOutDeliveryVolume();
        List<Map<String, Object>> list = businessServiceReport.getOutDeliveryVolumeMaps(result);
        return businessServiceReport.getMimeBodyPart(list, fileName, businessServiceReport.getOutDeliveryVolumeHeader());
    }

    /**
     * 运力管理部-整车业务毛利率
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public MimeBodyPart vehicleBusinessGrossProfitRate(List<String> labels, List<Integer> services, String fileName) throws Exception {
        List<VehicleBusinessGrossProfitRateVO> result = reportExportMapper.selectVehicleBusinessGrossProfitRate(labels, services);
        List<Map<String, Object>> list = businessServiceReport.getVehicleBusinessGrossProfitRateMaps(result);
        return businessServiceReport.getMimeBodyPart(list, fileName, businessServiceReport.getVehicleBusinessGrossProfitRateHeader());
    }

    /**
     * 运力管理部报表-按承运商导明细-当天
     */
    public MimeBodyPart capacityManagementReportsDay(String fileName) throws Exception {
        List<CapacityManagementVO> result = reportExportMapper.selectCapacityManagementDay();
        List<Map<String, Object>> list = businessServiceReport.getCapacityManagementMaps(result);
        return businessServiceReport.getMimeBodyPart(list, fileName, businessServiceReport.getCapacityManagementHeader());
    }

    /**
     * 运力管理部报表-按承运商导明细-当月
     */
    public MimeBodyPart capacityManagementReportsMonty(String fileName) throws Exception {
        List<CapacityManagementVO> result = reportExportMapper.selectCapacityManagementMonty();
        List<Map<String, Object>> list = businessServiceReport.getCapacityManagementMaps(result);
        return businessServiceReport.getMimeBodyPart(list, fileName, businessServiceReport.getCapacityManagementHeader());
    }

    public MimeBodyPart selectWaybillInstallDeliveryFee(String fileName) throws Exception {

        DateTime dateTime = new DateTime();
        DateTime start = dateTime.minusDays(1).millisOfDay().withMinimumValue();
        DateTime end = dateTime.minusDays(1).millisOfDay().withMaximumValue();

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");

        String startTime = start.toString(dateTimeFormatter);
        String endTime = end.toString(dateTimeFormatter);

        List<WaybillInstallDeliveryFeeVO> mapList = reportExportMapper.selectWaybillInstallDeliveryFee(startTime, endTime);

        List<Map<String, Object>> list = new ArrayList<>();
        mapList.forEach((item) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("customerLabel", item.getCustomerLabel());
            map.put("id", item.getId());
            map.put("code", item.getCode());
            map.put("name", item.getName());
            map.put("mobile", item.getMobile());
            map.put("secondName", item.getSecondName());
            map.put("customerType", item.getCustomerType());
            map.put("serviceType", item.getServiceType());
            map.put("installFee", item.getInstallFee());
            map.put("deliveryFee", item.getDeliveryFee());
            map.put("productType", item.getProductType());
            map.put("incrementServiceType", item.getIncrementServiceType());
            map.put("incrementServiceFee", item.getIncrementServiceFee());
            map.put("waybillStatus", item.getWaybillStatus());
            map.put("checkType", item.getCheckType());
            map.put("checkMethod", item.getCheckMethod());
            map.put("checkCode", item.getCheckCode());
            map.put("checkStatus", item.getCheckStatus());
            map.put("checkBillId", item.getCheckBillId());
            map.put("paymentType", item.getPaymentType());
            map.put("channelSource", item.getChannelSource());
            map.put("settlementType", item.getSettlementType());
            map.put("goodsName", item.getGoodsName());
            map.put("marbleBlocks", item.getMarbleBlocks());
            map.put("totalVolume", item.getTotalVolume());
            map.put("totalPackingItems", item.getTotalPackingItems());
            map.put("totalInstallItems", item.getTotalInstallItems());
            map.put("totalWeight", item.getTotalWeight());
            map.put("statementValue", item.getStatementValue());
            map.put("openBillNode", item.getOpenBillNode());
            map.put("openBillOperator", item.getOpenBillOperator());
            map.put("saleTotalPrice", item.getSaleTotalPrice());
            map.put("salesman", item.getSalesman());
            map.put("salesmanPhone", item.getSalesmanPhone());
            map.put("openBillTime", item.getOpenBillTime());
            map.put("signTime", item.getSignTime());
            map.put("destination", item.getDestination());
            map.put("consigneeName", item.getConsigneeName());
            map.put("consigneeMobile", item.getConsigneeMobile());
            map.put("address", item.getAddress());
            map.put("elevator", item.getElevator());
            map.put("floor", item.getFloor());
            map.put("province", item.getProvince());
            map.put("city", item.getCity());
            map.put("area", item.getArea());
            map.put("masterPhone", item.getMasterPhone());
            map.put("masterName", item.getMasterName());
            map.put("masterNode", item.getMasterNode());
            map.put("arrivalArea", item.getArrivalArea());
            map.put("arrivalAddress", item.getArrivalAddress());
            map.put("pickUpGoodsPhone", item.getPickUpGoodsPhone());
            map.put("pickUpGoodsPassword", item.getPickUpGoodsPassword());
            map.put("logisticsBillId", item.getLogisticsBillId());
            map.put("carrier", item.getCarrier());
            list.add(map);
        });

        return businessServiceReport.getMimeBodyPart(list, fileName, businessServiceReport.getWaybillInstallDeliveryHeader());
    }

    public MimeBodyPart installCostDetailedReports(String fileName) throws Exception {
        DateTime dateTime = new DateTime();
        DateTime start = dateTime.minusDays(1).millisOfDay().withMinimumValue();
        DateTime end = dateTime.minusDays(1).millisOfDay().withMaximumValue();

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");

        String startTime = start.toString(dateTimeFormatter);
        String endTime = end.toString(dateTimeFormatter);

        List<InstallCostDetailedVO> incomeStatement = reportExportMapper.installCostDetailedReports(startTime, endTime);
        List<Map<String, Object>> list = new ArrayList<>();
        incomeStatement.forEach((item) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("customerLabel", item.getCustomerLabel());
            map.put("waybillId", item.getWaybillId());
            map.put("signTime", item.getSignTime());
            map.put("masterMame", item.getMasterMame());
            map.put("masterPhone", item.getMasterPhone());
            map.put("masterNode", item.getMasterNode());
            map.put("receiveName", item.getReceiveName());
            map.put("receiveMobile", item.getReceiveMobile());
            map.put("province", item.getProvince());
            map.put("city", item.getCity());
            map.put("area", item.getArea());
            map.put("address", item.getAddress());
            map.put("goodsName", item.getGoodsName());
            map.put("installItems", item.getInstallItems());
            map.put("installFee", item.getInstallFee());
            map.put("consignorName", item.getConsignorName());
            list.add(map);
        });
        return businessServiceReport.getMimeBodyPart(list, fileName, businessServiceReport.getInstallCostDetailedReportsHeader());
    }

}
