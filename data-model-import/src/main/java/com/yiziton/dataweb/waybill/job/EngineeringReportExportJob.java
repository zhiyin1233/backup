package com.yiziton.dataweb.waybill.job;

import com.alibaba.druid.util.StringUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.yiziton.dataweb.waybill.dao.ReportExportMapper;
import com.yiziton.dataweb.waybill.job.component.BusinessReportComponent;
import com.yiziton.dataweb.waybill.utils.MailUtil;
import com.yiziton.dataweb.waybill.vo.CapacityManagementVO;
import com.yiziton.dataweb.waybill.vo.WaybillInstallDeliveryFeeVO;
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
 * @Description: ReportExportJob
 * @Author: wuxinghai
 * @Date: 2019/03/18
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@JobHandler(value = "engineeringReportExportJob")
@Component
@Slf4j
public class EngineeringReportExportJob extends IJobHandler {
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
        log.info("EngineeringReportExportJob   start");
        if(!StringUtils.isEmpty(param)){
            receiver = param.split(",");
        }
        List<MimeBodyPart> mimeBodyParts = new ArrayList<>();
        mimeBodyParts.add(this.capacityManagementReportsMonty());

        mailUtil.sendEmail("工程业务部-效益报表 " + (new DateTime()).minusDays(1).toString("yyyy-MM"), "", mimeBodyParts, receiver);
        log.info("EngineeringReportExportJob   end");
        return null;
    }

    /**
     * 运力管理部报表-按承运商导明细-当月
     */
    public MimeBodyPart capacityManagementReportsMonty() throws Exception {
        List<CapacityManagementVO> result = reportExportMapper.selectCapacityManagementMonty();
        List<Map<String, Object>> list = getCapacityManagementMaps(result);
        String date = (new DateTime()).minusDays(1).toString("-MM");
        return businessServiceReport.getMimeBodyPart(list, "运力管理部报表-按承运商导明细报表" + date + "月累计.xlsx", businessServiceReport.getCapacityManagementHeader());
    }

    private List<Map<String, Object>> getCapacityManagementMaps(List<CapacityManagementVO> result) {
        List<Map<String, Object>> list = new ArrayList<>();
        result.forEach((item) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("operateTime", item.getOperateTime());
            map.put("signTime", item.getSignTime());
            map.put("waybillId", item.getWaybillId());
            map.put("carrier", item.getCarrier());
            map.put("fee", item.getFee());
            map.put("operationOganization", item.getOperationOganization());
            map.put("customerLabel", item.getCustomerLabel());
            map.put("ciName", item.getCiName());
            map.put("secondName", item.getSecondName());
            map.put("serviceType", item.getServiceType());
            map.put("goodsType", item.getGoodsType());
            map.put("goodsName", item.getGoodsName());
            map.put("marbleBlocks", item.getMarbleBlocks());
            map.put("totalInstallItems", item.getTotalInstallItems());
            map.put("totalPackingItems", item.getTotalPackingItems());
            map.put("totalVolume", item.getTotalVolume());
            map.put("totalWeight", item.getTotalWeight());
            map.put("riName", item.getRiName());
            map.put("mobile", item.getMobile());
            map.put("province", item.getProvince());
            map.put("city", item.getCity());
            map.put("area", item.getArea());
            map.put("address", item.getAddress());

            list.add(map);
        });
        return list;
    }
}
