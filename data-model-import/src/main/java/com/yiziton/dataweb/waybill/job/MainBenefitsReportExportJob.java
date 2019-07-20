package com.yiziton.dataweb.waybill.job;

import com.alibaba.druid.util.StringUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.yiziton.dataweb.waybill.dao.ReportExportMapper;
import com.yiziton.dataweb.waybill.job.component.BusinessReportComponent;
import com.yiziton.dataweb.waybill.utils.MailUtil;
import com.yiziton.dataweb.waybill.vo.CarrierTrunkBenefitAnalysisVO;
import com.yiziton.dataweb.waybill.vo.HubTrunkBenefitAnalysisVO;
import com.yiziton.dataweb.waybill.vo.StandardAndNonStandardCustomerVO;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeBodyPart;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2019/3/30
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@JobHandler(value = "mainBenefitsReportExportJob")
@Component
@Slf4j
public class MainBenefitsReportExportJob extends IJobHandler {

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
        log.info("mainBenefitsReportExportJob   start");
        if(!StringUtils.isEmpty(param)){
            receiver = param.split(",");
        }
        List<MimeBodyPart> mimeBodyParts = new ArrayList<>();
        mimeBodyParts.add(this.standardAndNonStandardCustomer("标准及非标准客户毛利率.xlsx"));
        mimeBodyParts.add(this.hubTrunkBenefitAnalysis("干线效益分析-枢纽.xlsx"));
        mimeBodyParts.add(this.carrierTrunkBenefitAnalysis("干线效益分析-承运商.xlsx"));
        mailUtil.sendEmail("效益分析报告 " + (new DateTime()).minusDays(1).toString("yyyy-MM"), "", mimeBodyParts, receiver);
        log.info("mainBenefitsReportExportJob   end");
        return null;
    }

    /**
     * 标准及非标准客户毛利率
     * @param fileName
     * @return
     * @throws Exception
     */
    public MimeBodyPart standardAndNonStandardCustomer(String fileName) throws Exception {
        List<StandardAndNonStandardCustomerVO> incomeStatement = reportExportMapper.selectStandardAndNonStandardCustomer();
        List<Map<String, Object>> list = new ArrayList<>();
        incomeStatement.forEach((item) -> {
            Map<String, Object> map = new HashMap<String, Object>(16);
            map.put("name", item.getName());
            map.put("customerLabel", item.getCustomerLabel());
            map.put("signs", item.getSigns());
            map.put("branchs", item.getBranchs());
            map.put("trunks", item.getTrunks());
            map.put("endings", item.getEndings());
            map.put("customerTransportFee", item.getCustomerTransportFee());
            map.put("customerDeliveryFee", item.getCustomerDeliveryFee());
            map.put("customerInstallFee", item.getCustomerInstallFee());
            map.put("customerUpstairsFee", item.getCustomerUpstairsFee());
            map.put("customerInsuranceFee", item.getCustomerInsuranceFee());
            map.put("customerTotalPrice", item.getCustomerTotalPrice());
            map.put("customerAddFee", item.getCustomerAddFee());
            map.put("carrierOutDeliverCost", item.getCarrierOutDeliverCost());
            map.put("masterInstallFee", item.getMasterInstallFee());
            map.put("masterDeliveryFee", item.getMasterDeliveryFee());
            map.put("abnTotal", item.getAbnTotal());
            list.add(map);
        });
        return businessServiceReport.getMimeBodyPart(list, fileName, businessServiceReport.getStandardAndNonStandardCustomerReportsHeader());
    }

    /**
     * 干线效益分析-承运商
     * @param fileName
     * @return
     * @throws Exception
     */
    public MimeBodyPart carrierTrunkBenefitAnalysis(String fileName) throws Exception {
        List<CarrierTrunkBenefitAnalysisVO> incomeStatement = reportExportMapper.selectCarrierTrunkBenefitAnalysis();
        List<Map<String, Object>> list = new ArrayList<>();
        incomeStatement.forEach((item) -> {
            Map<String, Object> map = new HashMap<String, Object>(4);
            map.put("carrier", item.getCarrier());
            map.put("transportFee", item.getTransportFee());
            map.put("outDeliverCost", item.getOutDeliverCost());
            map.put("profitRate", item.getProfitRate());
            list.add(map);
        });
        return businessServiceReport.getMimeBodyPart(list, fileName, businessServiceReport.getCarrierTrunkBenefitAnalysisReportsHeader());
    }

    /**
     * 干线效益分析-枢纽
     * @param fileName
     * @return
     * @throws Exception
     */
    public MimeBodyPart hubTrunkBenefitAnalysis(String fileName) throws Exception {
        List<HubTrunkBenefitAnalysisVO> incomeStatement = reportExportMapper.selectHubTrunkBenefitAnalysis();
        List<Map<String, Object>> list = new ArrayList<>();
        incomeStatement.forEach((item) -> {
            Map<String, Object> map = new HashMap<String, Object>(4);
            map.put("openBillNode", item.getOpenBillNode());
            map.put("transportFee", item.getTransportFee());
            map.put("outDeliverCost", item.getOutDeliverCost());
            map.put("profitRate", item.getProfitRate());
            list.add(map);
        });
        return businessServiceReport.getMimeBodyPart(list, fileName, businessServiceReport.getHubTrunkBenefitAnalysisReportsHeader());
    }

}
