package com.yiziton.dataweb.waybill.job.component;

import com.yiziton.dataweb.waybill.dao.ReportExportMapper;
import com.yiziton.dataweb.waybill.utils.MailUtil;
import com.yiziton.dataweb.waybill.utils.excel.ExportExcelUtils;
import com.yiziton.dataweb.waybill.utils.excel.Header;
import com.yiziton.dataweb.waybill.utils.excel.WriteService;
import com.yiziton.dataweb.waybill.vo.CapacityManagementVO;
import com.yiziton.dataweb.waybill.vo.OutDeliveryVolumeVO;
import com.yiziton.dataweb.waybill.vo.VehicleBusinessGrossProfitRateVO;
import com.yiziton.dataweb.waybill.vo.header.InfoHeader;
import com.yiziton.dataweb.waybill.vo.report_vo.CustomerProfitReportsVO;
import com.yiziton.dataweb.waybill.vo.report_vo.IncomeStatementReportsVo;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeBodyPart;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zhangSiYuan
 */
@Component
public class BusinessReportComponent {
    @Autowired
    ReportExportMapper reportExportMapper;
    @Autowired
    MailUtil mailUtil;

    public MimeBodyPart selectIncomeStatementReports(List<String> consignorList, String fileName) throws Exception {
        List<IncomeStatementReportsVo> incomeStatement = reportExportMapper.selectIncomeStatementReports(consignorList);

        List<Map<String, Object>> list = new ArrayList<>();
        incomeStatement.forEach((item) -> {
            Map<String, Object> map = new HashMap<>(10);
            map.put("consignorName", item.getConsignorName());
            map.put("consignorLabel", item.getConsignorLabel());
            map.put("today", item.getToday());
            map.put("todayNum", item.getTodayNum());
            map.put("todayPrice", item.getTodayPrice());
            map.put("todayRatio", item.getTodayRatio());
            map.put("month", item.getMonth());
            map.put("monthPrice", item.getMonthPrice());
            map.put("monthCount", item.getMonthCount());
            map.put("monthRatio", item.getMonthRatio());
            list.add(map);
        });

        return getMimeBodyPart(list, fileName, new IncomeStatementHeader());
    }

    public MimeBodyPart getMimeBodyPart(List<Map<String, Object>> list, String fileName, InfoHeader infoHeader) throws Exception {
        WriteService writeService = ExportExcelUtils.getWriteService(infoHeader, null);
        writeService.createContents(list);
        Workbook workbook = writeService.build();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        bos.close();
        byte[] standardCustomerBytes = bos.toByteArray();
        return MailUtil.byte2Multpart(standardCustomerBytes, fileName, MailUtil.EXCEL_FORMAT);
    }

    public List<Map<String, Object>> getCustomerMimeMaps(List<CustomerProfitReportsVO> standardCustomer) {
        List<Map<String, Object>> list = new ArrayList<>();
        standardCustomer.forEach((item) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("consignorName", item.getConsignorName());
            map.put("todayIncome", item.getTodayIncome());
            map.put("todayCost", item.getTodayCost());
            map.put("todayProfitMargin", item.getTodayProfitMargin());
            map.put("monthIncome", item.getMonthIncome());
            map.put("monthCost", item.getMonthCost());
            map.put("monthProfitMargin", item.getMonthProfitMargin());
            list.add(map);
        });
        return list;
    }

    public List<Map<String, Object>> getCapacityManagementMaps(List<CapacityManagementVO> result) {
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

    /**
     * 仓储枢纽-外发货量（方）数据准备
     * @param result
     * @return
     */
    public List<Map<String, Object>> getOutDeliveryVolumeMaps(List<OutDeliveryVolumeVO> result) {
        List<Map<String, Object>> list = new ArrayList<>();
        result.forEach((item) -> {
            Map<String, Object> map = new HashMap<String, Object>(5);
            map.put("operationOganization", item.getOperationOganization());
            map.put("today", item.getToday());
            map.put("todayOutDeliverCost", item.getTodayOutDeliverCost());
            map.put("todayTakeGoodsFee", item.getTodayTakeGoodsFee());
            map.put("todayVolume", item.getTodayVolume());
            map.put("month", item.getMonth());
            map.put("monthOutDeliverCost", item.getMonthOutDeliverCost());
            map.put("monthTakeGoodsFee", item.getMonthTakeGoodsFee());
            map.put("monthVolume", item.getMonthVolume());
            list.add(map);
        });
        return list;
    }

    /**
     * 运力管理部-整车业务毛利率 数据准备
     * @param standardCustomer
     * @return
     */
    public List<Map<String, Object>> getVehicleBusinessGrossProfitRateMaps(List<VehicleBusinessGrossProfitRateVO> standardCustomer) {
        List<Map<String, Object>> list = new ArrayList<>();
        standardCustomer.forEach((item) -> {
            Map<String, Object> map = new HashMap<String, Object>(5);
            map.put("customerLabel", item.getCustomerLabel());
            map.put("consignorName", item.getConsignorName());
            map.put("todayTotalPrice", item.getTodayTotalPrice());
            map.put("todayAbnIncome", item.getTodayAbnIncome());
            map.put("todayOutCost", item.getTodayOutCost());
            map.put("todayAbnCost", item.getTodayAbnCost());
            map.put("todayIncome", item.getTodayIncome());
            map.put("todayCost", item.getTodayCost());
            map.put("todayGrossProfitRate", item.getTodayGrossProfitRate());
            map.put("monthTotalPrice", item.getMonthTotalPrice());
            map.put("monthAbnIncome", item.getMonthAbnIncome());
            map.put("monthOutCost", item.getMonthOutCost());
            map.put("monthAbnCost", item.getMonthAbnCost());
            map.put("monthIncome", item.getMonthIncome());
            map.put("monthCost", item.getMonthCost());
            map.put("monthGrossProfitRate", item.getMonthGrossProfitRate());
            list.add(map);
        });
        return list;
    }

    public ProfitMarginHeader getProfitMarginHeader() {
        return new ProfitMarginHeader();
    }

    public class IncomeStatementHeader extends InfoHeader {
        IncomeStatementHeader() {
            headerMap.put("consignorName", new Header("consignorName", "发货人", "string", 20));
            headerMap.put("consignorLabel", new Header("consignorLabel", "客户标识", "string", 20));
            headerMap.put("today", new Header("today", "当天日期", "string", 20));
            headerMap.put("todayNum", new Header("todayNum", "当天开单总票数", "string", 20));
            headerMap.put("todayPrice", new Header("todayPrice", "当天开单总金额(元)", "string", 20));
            headerMap.put("todayRatio", new Header("todayRatio", "当天开单金额占比(%)", "string", 20));
            headerMap.put("month", new Header("month", "当月月份", "string", 20));
            headerMap.put("monthPrice", new Header("monthPrice", "累计当月开单总金额(元)", "string", 20));
            headerMap.put("monthCount", new Header("monthCount", "累计当月开单总票数", "string", 20));
            headerMap.put("monthRatio", new Header("monthRatio", "累计当月开单金额占比(%)", "string", 20));
        }
    }

    /**
     * 仓储枢纽-外发货量 报表头
     */
    public class OutDeliveryVolumeHeader extends InfoHeader {
        public OutDeliveryVolumeHeader(){
            headerMap.put("operationOganization",new Header("operationOganization","外发网点","string",12));
            headerMap.put("today",new Header("today","当天外发时间","string",12));
            headerMap.put("todayOutDeliverCost",new Header("todayOutDeliverCost","当天外发总费用","string",12));
            headerMap.put("todayTakeGoodsFee",new Header("todayTakeGoodsFee","当天揽货总费用","string",12));
            headerMap.put("todayVolume",new Header("todayVolume","当天外发总方数","string",12));
            headerMap.put("month",new Header("month","当月月份","string",12));
            headerMap.put("monthOutDeliverCost",new Header("monthOutDeliverCost","当月累计外发总费用","string",12));
            headerMap.put("monthTakeGoodsFee",new Header("monthTakeGoodsFee","当月累计揽货总费用","string",12));
            headerMap.put("monthVolume",new Header("monthVolume","当月累计外发总方数","string",12));
        }
    }

    public OutDeliveryVolumeHeader getOutDeliveryVolumeHeader(){return new OutDeliveryVolumeHeader();}

    /**
     * 运力管理部-整车业务毛利率 报表头
     */
    public class VehicleBusinessGrossProfitRateHeader extends InfoHeader {
        public VehicleBusinessGrossProfitRateHeader(){
            headerMap.put("customerLabel",new Header("customerLabel","客户类型","string",12));
            headerMap.put("consignorName",new Header("consignorName","发货人名称","string",12));
            headerMap.put("todayTotalPrice",new Header("todayTotalPrice","当天开单总金额(元)","string",12));
            headerMap.put("todayAbnIncome",new Header("todayAbnIncome","当天异常收入(元)","string",12));
            headerMap.put("todayOutCost",new Header("todayOutCost","当天干线外发成本(元)","string",12));
            headerMap.put("todayAbnCost",new Header("todayAbnCost","当天异常成本(元)","string",12));
            headerMap.put("todayIncome",new Header("todayIncome","当天总收入(元)","string",12));
            headerMap.put("todayCost",new Header("todayCost","当天总成本(元)","string",12));
            headerMap.put("todayGrossProfitRate",new Header("todayGrossProfitRate","当天毛利率(%)","string",12));
            headerMap.put("monthTotalPrice",new Header("monthTotalPrice","当月开单总金额(元)","string",12));
            headerMap.put("monthAbnIncome",new Header("monthAbnIncome","当月异常收入(元)","string",12));
            headerMap.put("monthOutCost",new Header("monthOutCost","当月干线外发成本(元)","string",12));
            headerMap.put("monthAbnCost",new Header("monthAbnCost","当月异常成本(元)","string",12));
            headerMap.put("monthIncome",new Header("monthIncome","当月总收入(元)","string",12));
            headerMap.put("monthCost",new Header("monthCost","当月总成本(元)","string",12));
            headerMap.put("monthGrossProfitRate",new Header("monthGrossProfitRate","当月毛利率(%)","string",12));
        }
    }
    public VehicleBusinessGrossProfitRateHeader getVehicleBusinessGrossProfitRateHeader(){return new VehicleBusinessGrossProfitRateHeader();}

    public CapacityManagementHeader getCapacityManagementHeader() {
        return new CapacityManagementHeader();
    }
    public class CapacityManagementHeader extends InfoHeader {
        CapacityManagementHeader() {

            headerMap.put("operateTime", new Header("operateTime", "外发时间", "string", 20));
            headerMap.put("signTime", new Header("signTime", "签收时间", "string", 20));
            headerMap.put("waybillId", new Header("waybillId", "单号", "string", 20));
            headerMap.put("carrier", new Header("carrier", "承运商", "string", 20));
            headerMap.put("fee", new Header("fee", "外发成本(元)", "string", 20));
            headerMap.put("operationOganization", new Header("operationOganization", "外发网点", "string", 20));
            headerMap.put("customerLabel", new Header("customerLabel", "客户类型", "string", 20));
            headerMap.put("ciName", new Header("ciName", "发货人", "string", 20));
            headerMap.put("secondName", new Header("secondName", "商家名称", "string", 20));
            headerMap.put("serviceType", new Header("serviceType", "服务类型", "string", 20));
            headerMap.put("goodsType", new Header("goodsType", "货物类型", "string", 20));
            headerMap.put("goodsName", new Header("goodsName", "品名(用;号分隔)", "string", 20));
            headerMap.put("marbleBlocks", new Header("marbleBlocks", "大理石数量(件)", "string", 20));
            headerMap.put("totalInstallItems", new Header("totalInstallItems", "安装件数(件)", "string", 20));
            headerMap.put("totalPackingItems", new Header("totalPackingItems", "包装件数(件)", "string", 20));
            headerMap.put("totalVolume", new Header("totalVolume", "总体积(m³)", "string", 20));
            headerMap.put("totalWeight", new Header("totalWeight", "总重量(kg)", "string", 20));
            headerMap.put("riName", new Header("riName", "收货人姓名", "string", 20));
            headerMap.put("mobile", new Header("mobile", "收货人电话", "string", 20));
            headerMap.put("province", new Header("province", "省", "string", 20));
            headerMap.put("city", new Header("city", "市", "string", 20));
            headerMap.put("area", new Header("area", "区", "string", 20));
            headerMap.put("address", new Header("address", "收货人地址", "string", 20));

        }
    }

    public class ProfitMarginHeader extends InfoHeader {
        ProfitMarginHeader() {
            headerMap.put("consignorName", new Header("consignorName", "客户名称", "string", 20));
            headerMap.put("todayIncome", new Header("todayIncome", "当天总收入(元)", "string", 20));
            headerMap.put("todayCost", new Header("todayCost", "当天总支出(元)", "string", 20));
            headerMap.put("todayProfitMargin", new Header("todayProfitMargin", "当天毛利率(%)", "string", 20));
            headerMap.put("monthIncome", new Header("monthIncome", "当月总收入(元)", "string", 20));
            headerMap.put("monthCost", new Header("monthCost", "当月总支出(元)", "string", 20));
            headerMap.put("monthProfitMargin", new Header("monthProfitMargin", "当月毛利率(%)", "string", 20));
        }
    }

    public WaybillInstallDeliveryHeader  getWaybillInstallDeliveryHeader(){
        return new WaybillInstallDeliveryHeader();
    }

    public class WaybillInstallDeliveryHeader extends InfoHeader {
        WaybillInstallDeliveryHeader() {
            headerMap.put("customerLabel",new Header("customerLabel","客户类型","string",20));
            headerMap.put("id",new Header("id","运单号","string",20));
            headerMap.put("code",new Header("code","发货人编号","string",20));
            headerMap.put("name",new Header("name","发货人名称","string",20));
            headerMap.put("mobile",new Header("mobile","'","string",20));
            headerMap.put("secondName",new Header("secondName","二级商家名称","string",20));
            headerMap.put("customerType",new Header("customerType","商家类型","string",20));
            headerMap.put("serviceType",new Header("serviceType","服务类型","string",20));
            headerMap.put("installFee",new Header("installFee","安装费","string",20));
            headerMap.put("deliveryFee",new Header("deliveryFee","送货费","string",20));
            headerMap.put("productType",new Header("productType","产品类型","string",20));
            headerMap.put("incrementServiceType",new Header("incrementServiceType","增值服务类型","string",20));
            headerMap.put("incrementServiceFee",new Header("incrementServiceFee","增值服务费","string",20));
            headerMap.put("waybillStatus",new Header("waybillStatus","运单状态","string",20));
            headerMap.put("checkType",new Header("checkType","核销类型","string",20));
            headerMap.put("checkMethod",new Header("checkMethod","核销方式","string",20));
            headerMap.put("checkCode",new Header("checkCode","核销编码","string",20));
            headerMap.put("checkStatus",new Header("checkStatus","核销状态","string",20));
            headerMap.put("checkBillId",new Header("checkBillId","核销单号","string",20));
            headerMap.put("paymentType",new Header("paymentType","付款方式","string",20));
            headerMap.put("channelSource",new Header("channelSource","渠道来源","string",20));
            headerMap.put("settlementType",new Header("settlementType","结算方式","string",20));
            headerMap.put("goodsName",new Header("goodsName","货品信息","string",20));
            headerMap.put("marbleBlocks",new Header("marbleBlocks","大理石数","string",20));
            headerMap.put("totalVolume",new Header("totalVolume","总体积","string",20));
            headerMap.put("totalPackingItems",new Header("totalPackingItems","总包装件数","string",20));
            headerMap.put("totalInstallItems",new Header("totalInstallItems","总安装件数","string",20));
            headerMap.put("totalWeight",new Header("totalWeight","总重量","string",20));
            headerMap.put("statementValue",new Header("statementValue","声明价值","string",20));
            headerMap.put("openBillNode",new Header("openBillNode","开单网点","string",20));
            headerMap.put("openBillOperator",new Header("openBillOperator","开单人员","string",20));
            headerMap.put("saleTotalPrice",new Header("saleTotalPrice","销售总价","string",20));
            headerMap.put("salesman",new Header("salesman","业务员","string",20));
            headerMap.put("salesmanPhone",new Header("salesmanPhone","业务手机","string",20));
            headerMap.put("openBillTime",new Header("openBillTime","开单时间","string",20));
            headerMap.put("signTime",new Header("signTime","签收时间","string",20));
            headerMap.put("destination",new Header("destination","目的地","string",20));
            headerMap.put("consigneeName",new Header("consigneeName","收货人名称","string",20));
            headerMap.put("consigneeMobile",new Header("consigneeMobile","收货人手机","string",20));
            headerMap.put("address",new Header("address","收货人地址","string",20));
            headerMap.put("elevator",new Header("elevator","是否有电梯","string",20));
            headerMap.put("floor",new Header("floor","楼层","string",20));
            headerMap.put("province",new Header("province","省","string",20));
            headerMap.put("city",new Header("city","市","string",20));
            headerMap.put("area",new Header("area","区","string",20));
            headerMap.put("masterPhone",new Header("masterPhone","师傅手机号","string",20));
            headerMap.put("masterName",new Header("masterName","师傅名称","string",20));
            headerMap.put("masterNode",new Header("masterNode","末端网点","string",20));
            headerMap.put("arrivalArea",new Header("arrivalArea","提货目的地","string",20));
            headerMap.put("arrivalAddress",new Header("arrivalAddress","提货区域","string",20));
            headerMap.put("pickUpGoodsPhone",new Header("pickUpGoodsPhone","提货电话","string",20));
            headerMap.put("pickUpGoodsPassword",new Header("pickUpGoodsPassword","提货密码","string",20));
            headerMap.put("logisticsBillId",new Header("logisticsBillId","物流单号","string",20));
            headerMap.put("carrier",new Header("carrier","承运商","string",20));
        }
    }

    public InstallCostDetailedReportsHeader getInstallCostDetailedReportsHeader() {
        return new InstallCostDetailedReportsHeader();
    }

    public class InstallCostDetailedReportsHeader extends InfoHeader {
        InstallCostDetailedReportsHeader() {
            headerMap.put("customerLabel", new Header("customerLabel", "客户标识", "string", 20));
            headerMap.put("waybillId", new Header("waybillId", "单号", "string", 20));
            headerMap.put("signTime", new Header("signTime", "签收日期", "string", 20));
            headerMap.put("masterMame", new Header("masterMame", "师傅名称", "string", 20));
            headerMap.put("masterPhone", new Header("masterPhone", "手机号", "string", 20));
            headerMap.put("masterNode", new Header("masterNode", "所属网点", "string", 20));
            headerMap.put("receiveName", new Header("receiveName", "收货人姓名", "string", 20));
            headerMap.put("receiveMobile", new Header("receiveMobile", "收货人手机号码", "string", 20));
            headerMap.put("province", new Header("province", "收货省", "string", 20));
            headerMap.put("city", new Header("city", "收货市", "string", 20));
            headerMap.put("area", new Header("area", "收货区", "string", 20));
            headerMap.put("address", new Header("address", "收货人信息地址", "string", 20));
            headerMap.put("goodsName", new Header("goodsName", "品名", "string", 20));
            headerMap.put("installItems", new Header("installItems", "安装件数", "string", 20));
            headerMap.put("installFee", new Header("installFee", "安装费", "string", 20));
            headerMap.put("consignorName", new Header("consignorName", "发货人", "string", 20));
        }
    }

    public StandardAndNonStandardCustomerReportsHeader getStandardAndNonStandardCustomerReportsHeader() {
        return new StandardAndNonStandardCustomerReportsHeader();
    }

    public class StandardAndNonStandardCustomerReportsHeader extends InfoHeader {
        StandardAndNonStandardCustomerReportsHeader() {
            headerMap.put("name", new Header("name", "商家名称", "string", 20));
            headerMap.put("customerLabel", new Header("customerLabel", "标准/非标商家标记", "string", 20));
            headerMap.put("signs", new Header("signs", "签收单量", "string", 20));
            headerMap.put("branchs", new Header("branchs", "支装单量", "string", 20));
            headerMap.put("trunks", new Header("trunks", "干支装单量", "string", 20));
            headerMap.put("endings", new Header("endings", "需末端操作单量", "string", 20));
            headerMap.put("customerTransportFee", new Header("customerTransportFee", "运费收入", "string", 20));
            headerMap.put("customerDeliveryFee", new Header("customerDeliveryFee", "送货费收入", "string", 20));
            headerMap.put("customerInstallFee", new Header("customerInstallFee", "安装费收入", "string", 20));
            headerMap.put("customerUpstairsFee", new Header("customerUpstairsFee", "上楼费", "string", 20));
            headerMap.put("customerInsuranceFee", new Header("customerInsuranceFee", "保价费", "string", 20));
            headerMap.put("customerTotalPrice", new Header("customerTotalPrice", "开单收入合计", "string", 20));
            headerMap.put("customerAddFee", new Header("customerAddFee", "异常收入合计", "string", 20));
            headerMap.put("carrierOutDeliverCost", new Header("carrierOutDeliverCost", "外发成本", "string", 20));
            headerMap.put("masterInstallFee", new Header("masterInstallFee", "安装费成本", "string", 20));
            headerMap.put("masterDeliveryFee", new Header("masterDeliveryFee", "支线费成本", "string", 20));
            headerMap.put("abnTotal", new Header("abnTotal", "异常费用支出合计", "string", 20));
        }
    }

    public CarrierTrunkBenefitAnalysisReportsHeader getCarrierTrunkBenefitAnalysisReportsHeader() {
        return new CarrierTrunkBenefitAnalysisReportsHeader();
    }

    public class CarrierTrunkBenefitAnalysisReportsHeader extends InfoHeader {
        CarrierTrunkBenefitAnalysisReportsHeader() {
            headerMap.put("carrier", new Header("carrier", "承运商", "string", 20));
            headerMap.put("transportFee", new Header("transportFee", "运费收入", "string", 20));
            headerMap.put("outDeliverCost", new Header("outDeliverCost", "外发成本", "string", 20));
            headerMap.put("profitRate", new Header("profitRate", "毛利率(%)", "string", 20));
        }
    }

    public HubTrunkBenefitAnalysisReportsHeader getHubTrunkBenefitAnalysisReportsHeader() {
        return new HubTrunkBenefitAnalysisReportsHeader();
    }

    public class HubTrunkBenefitAnalysisReportsHeader extends InfoHeader {
        HubTrunkBenefitAnalysisReportsHeader() {
            headerMap.put("openBillNode", new Header("openBillNode", "枢纽", "string", 20));
            headerMap.put("transportFee", new Header("transportFee", "运费收入", "string", 20));
            headerMap.put("outDeliverCost", new Header("outDeliverCost", "外发成本", "string", 20));
            headerMap.put("profitRate", new Header("profitRate", "毛利率(%)", "string", 20));
        }
    }
}
