package com.yiziton.dataweb.waybill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yiziton.commons.utils.DateUtil;
import com.yiziton.commons.utils.bean.Beans;
import com.yiziton.commons.vo.enums.*;
import com.yiziton.dataimport.waybill.bean.*;
import com.yiziton.dataimport.waybill.dao.ReportMapper;
import com.yiziton.dataimport.waybill.dao.ext.*;
import com.yiziton.dataweb.waybill.service.ReportService;
import com.yiziton.dataweb.waybill.utils.GridRequest;
import com.yiziton.dataweb.waybill.utils.excel.ExportExcelUtils;
import com.yiziton.dataweb.waybill.utils.excel.WriteService;
import com.yiziton.dataweb.waybill.vo.*;
import com.yiziton.dataweb.waybill.vo.header.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service("reportService")
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final static MilestoneInfo EMPTY_MILESTONE = new MilestoneInfo();
    @Autowired
    BillRelationInfoExtMapper billRelationInfoExtMapper;
    @Autowired
    FeeInfoExtMapper feeInfoExtMapper;
    @Autowired
    BillingInfoExtMapper billingInfoExtMapper;
    @Autowired
    WaybillInfoExtMapper waybillInfoExtMapper;
    @Autowired
    CarrierInfoExtMapper carrierInfoExtMapper;
    @Autowired
    MasterInfoExtMapper masterInfoExtMapper;
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private GoodsInfoExtMapper goodsInfoExtMapper;
    @Autowired
    private MilestoneInfoExtMapper milestoneInfoExtMapper;
    @Autowired
    private OperationDetailExtMapper operationDetailExtMapper;

    /**
     * 运单时效报表导出
     *
     * @param conditionVO
     * @return
     */
    @Override
    public Workbook exportReportForWaybillAging(ReportConditionVO conditionVO) throws Exception {
//        WriteService writeService = ExportExcelUtils.getWriteService(new WaybillAgingReportInfoHeader(), conditionVO.getColumns(), null);
        log.info("运单时效报表导出开始");
        long l1 = System.currentTimeMillis();
        WriteService writeService = ExportExcelUtils.getWriteService(new WaybillAgingHeader(), null);
        Integer count = 0;
        while (true) {
            List<WayBillAgingExportVO> list = findWayBillAging(null, conditionVO);
            count += list.size();
            List<Map<String, Object>> prescriptionList = list.parallelStream().map(x -> ((JSONObject) JSON.toJSON(x))).collect(Collectors.toList());
            if (prescriptionList != null && prescriptionList.size() > 0) {
                writeService.createContents(prescriptionList);
                conditionVO.setOffset(conditionVO.getOffset() + conditionVO.getSize());
            } else {
                break;
            }
        }
        log.info("运单时效报表导出结束. 导出数量: {}, 总耗时: {}", count, System.currentTimeMillis() - l1);
        return writeService.build();
    }

    /**
     * 运单时效查询
     */
    @Override
    public List<WayBillAgingExportVO> findWayBillAging(GridRequest gridRequest, ReportConditionVO conditionVO) throws Exception {
        if (gridRequest != null) {
            conditionVO.setOffset((gridRequest.getPage() - 1) * gridRequest.getSize());
            conditionVO.setSize(gridRequest.getSize());
        }
        long l1 = System.currentTimeMillis();
        List<ReportExportVO> dataList = reportMapper.selectWayBillInfoForReport(conditionVO);
        log.info("查询基本信息耗时: {}", System.currentTimeMillis() - l1);
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        long l2 = System.currentTimeMillis();
        List<String> waybillIdList = dataList.stream().map(ReportExportVO::getWaybillId).collect(Collectors.toList());
        // 准备商品数据
        List<GoodsInfo> goodsInfoList = goodsInfoExtMapper.selectByWaybillIds(waybillIdList);
        Map<String, List<GoodsInfo>> goodsMap = goodsInfoList.stream().collect(Collectors.groupingBy(GoodsInfo::getWaybillId));
        // 准备时间节点相关数据
        List<MilestoneInfo> allMilestoneInfoList = milestoneInfoExtMapper.selectByWaybillIdsAndOperations(waybillIdList, null);
        Map<String, List<MilestoneInfo>> milestoneInfoMap = allMilestoneInfoList.stream().collect(Collectors.groupingBy(MilestoneInfo::getWaybillId));
        // 节点关联的节点详情(只取预约节点的上门日期、时间段等信息)
        List<Integer> codes = Arrays.asList(Operation.HEADQUARTERS_APPOINTMENT.getCode(), Operation.DIRECT_OR_OUTLET_APPOINTMENT.getCode(),
                Operation.MASTER_APPOINTMENT.getCode(), Operation.HEADQUARTERS_DISTRIBUTION.getCode());
        List<String> milestoneIds = allMilestoneInfoList.stream().filter(x -> codes.contains(x.getOperation())).map(MilestoneInfo::getId).collect(Collectors.toList());
        Map<String, List<OperationDetail>> operationDetailMap = milestoneIds.isEmpty() ? Collections.emptyMap() :
                operationDetailExtMapper.selectByMilestoneIds(milestoneIds).stream().collect(Collectors.groupingBy(OperationDetail::getMilestoneId));
        // 运单关联数据
        List<BillRelationInfo> relationInfoList = billRelationInfoExtMapper.selectByWaybillIds(waybillIdList);
        Map<String, List<BillRelationInfo>> billRelationInfoMap = relationInfoList.stream().collect(Collectors.groupingBy(BillRelationInfo::getWaybillId));
        // 运单关联数据
        List<BillingInfo> billingInfoList = billingInfoExtMapper.select4WaybillAging(waybillIdList);
        Map<String, Double> billingInfoMap = billingInfoList.stream().collect(Collectors.toMap(BillingInfo::getWaybillId, BillingInfo::getTotalPrice, Double::sum));
        log.info("准备数据耗时: {}", System.currentTimeMillis() - l2);

        long l3 = System.currentTimeMillis();
        List<WayBillAgingExportVO> list = new ArrayList<>(dataList.size());
        for (ReportExportVO data : dataList) {
            WayBillAgingExportVO exportVO = new WayBillAgingExportVO();
            Beans.from(data).to(exportVO);

            exportVO.setTotalFee(billingInfoMap.get(data.getWaybillId())); // 总费用

            /*------------------------------------运单基本信息相关------------------------------------*/
            exportVO.setOpenBillTime(DateUtil.fromDateToH(data.getOpenBillTime()));// 开单时间
            WaybillStatus waybillStatus = WaybillStatus.getEnumByCode(data.getWaybillStatus());
            if (waybillStatus != null) {
                exportVO.setWaybillStatus(waybillStatus.getMessage());// 运单状态
            }
            WaybillType waybillType = WaybillType.getEnumByCode(data.getWaybillType());
            if (waybillType != null) {
                exportVO.setWaybillType(waybillType.getMessage());// 运单类型
            }
            SettlementType settlementType = SettlementType.getEnumByCode(data.getSettlementType());
            if (settlementType != null) {
                exportVO.setSettlementType(settlementType.getMessage());// 结算类型
            }
            CustomerType customerType = CustomerType.getEnumByCode(data.getMerchantType());
            if (customerType != null) {
                exportVO.setMerchantType(customerType.getMessage());// 商家类型(商家标识)
            }
            ServiceType serviceType = ServiceType.getEnumByCode(data.getServiceType());
            if (serviceType != null) {
                exportVO.setServiceType(serviceType.getMessage());// 服务类型
            }
            // 未分配到安装师傅不显示师傅名
            MasterType masterType = MasterType.getEnumByCode(data.getMasterType());
            if (masterType != MasterType.MASTER) {
                exportVO.setMasterName(null);
                exportVO.setMasterPhone(null);
                exportVO.setMasterNode(null);
            }
            List<GoodsInfo> goodsInfos = goodsMap.get(data.getWaybillId());
            if (goodsInfos != null) {
                StringBuilder goodsNameBuilder = new StringBuilder(), packingBuilder = new StringBuilder();
                int packingTotalCount = 0;
                double totalVolume = 0d;
                for (GoodsInfo goodsInfo : goodsInfos) {
                    packingTotalCount += goodsInfo.getPackingItems() == null ? 0 : goodsInfo.getPackingItems();
                    totalVolume += goodsInfo.getVolume() == null ? 0d : goodsInfo.getVolume();
                    goodsNameBuilder.append(goodsInfo.getGoodsName())
                            .append("_").append(goodsInfo.getInstallItems() == null?"0":goodsInfo.getInstallItems())
                            //.append("_").append(goodsInfo.getPackingItems() == null?"0":goodsInfo.getPackingItems())
                            .append(";");
                    /*// 格式：商品名*包装类型*包装件数
                    if(goodsInfo.getPacking() != null){
                        exportVO.setGoodsPacking(Packing.getEnumByCode(goodsInfo.getPacking()).getMessage());// 包装
                    }*/
                }
                exportVO.setGoodsName(goodsNameBuilder.substring(0, goodsNameBuilder.length() - 1));// 品名
                //exportVO.setGoodsPacking(packingBuilder.substring(0, packingBuilder.length() - 1));// 包装
                exportVO.setPackingTotalVolume(totalVolume);// 总体积
                exportVO.setPackingTotalCount(packingTotalCount);// 包装总件数
                exportVO.setGoodsPacking(data.getPacking() == null?"":data.getPacking());// 包装
            }
            /*------------------------------------运单关联信息------------------------------------*/
            List<BillRelationInfo> billRelationInfo = billRelationInfoMap.get(data.getWaybillId());
            if (billRelationInfo != null) {
                billRelationInfo.sort(Comparator.comparing(BillRelationInfo::getCreateTime));
                StringBuilder orderBillIdBuilder = new StringBuilder(), customerBillIdBuilder = new StringBuilder();
                for (BillRelationInfo relationInfo : billRelationInfo) {
                    if (relationInfo.getOrderBillId() != null) {
                        orderBillIdBuilder.append(relationInfo.getOrderBillId()).append(",");
                    }
                    if (relationInfo.getCustomerBillId() != null) {
                        customerBillIdBuilder.append(relationInfo.getCustomerBillId()).append(",");
                    }
                }
                exportVO.setOrderBillId(orderBillIdBuilder.substring(0, orderBillIdBuilder.length() == 0 ? 0 : orderBillIdBuilder.length() - 1));// 订单号
                exportVO.setCustomerBillId(customerBillIdBuilder.substring(0, customerBillIdBuilder.length() == 0 ? 0 : customerBillIdBuilder.length() - 1));// 客户单号
            }

            /*------------------------------------核销相关------------------------------------*/
            CheckType checkType = CheckType.getEnumByCode(data.getCheckType());
            if (checkType != null) {
                exportVO.setCheckType(checkType.getMessage());// 核销类型
            }
            CheckMethod checkMethod = CheckMethod.getEnumByCode(data.getCheckMethod());
            if (checkMethod != null) {
                exportVO.setCheckMethod(checkMethod.getMessage());// 核销方式
            }
            CheckStatus checkStatus = CheckStatus.getEnumByCode(data.getCheckStatus());
            if (checkStatus != null) {
                exportVO.setCheckStatus(checkMethod == CheckMethod.CHECK_NO ? StringUtils.EMPTY : checkStatus.getMessage());// 是否已核销
            }

            /*------------------------------------节点时间相关------------------------------------*/
            List<MilestoneInfo> milestoneInfos = milestoneInfoMap.getOrDefault(data.getWaybillId(), Collections.emptyList());
            // 移除失效节点
            milestoneInfos = removeInvalidMilestone(milestoneInfos);
            // 按照操作节点分组
            Map<Operation, List<MilestoneInfo>> operationMap = milestoneInfos.stream().collect(Collectors.groupingBy(x -> Operation.getEnumByCode(x.getOperation())));
            exportVO.setWarehousingTime(getLastMilestoneTime(operationMap, Operation.WAREHOUSING, Operation.WAREHOUSING_CANCEL));// 入库时间
            exportVO.setOutDeliverTime(getLastMilestoneTime(operationMap, Operation.OUT_DELIVER_COMFIRM, Operation.OUT_DELIVER_CANCEL));// 外发交接时间
            exportVO.setCarrierDepartureTime(getLastMilestoneTime(operationMap, Operation.CARRIER_DEPARTURE, Operation.CARRIER_DEPARTURE_CANCEL));// 承运商发车
            exportVO.setCarrierArriveTime(getLastMilestoneTime(operationMap, Operation.CARRIER_ARRIVE, Operation.CARRIER_ARRIVE_CANCEL));// 承运商内部到达中转时间
            exportVO.setCarrierTransferTime(getLastMilestoneTime(operationMap, Operation.CARRIER_TRANSFER, Operation.CARRIER_TRANSFER_CANCEL));// 承运商内部中转发车时间
            exportVO.setArrivalDestinationTime(getLastMilestoneTime(operationMap, Operation.ARRIVAL_DESTINATION, Operation.ARRIVAL_DESTINATION_CANCEL));// 承运商到达时间
            exportVO.setDirectReceiptTime(getLastMilestoneTime(operationMap, Operation.DIRECT_RECEIPT, Operation.DIRECT_RECEIPT_CANCEL));// 直营收货时间
            exportVO.setTrunkEndTime(getLastMilestoneTime(operationMap, Operation.TRUNK_END, Operation.TRUNK_END_CANCEL));// 干结时间
            // 直营外发
            MilestoneInfo lastMilestone = getLastMilestone(operationMap, Arrays.asList(Operation.DIRECT_TRANSFER), Arrays.asList(Operation.DIRECT_TRANSFER_REMOVE));
            exportVO.setDirectTransferTime(DateUtil.fromDateToH(lastMilestone.getOperateTime()));// 直营外发时间
            exportVO.setDepartmentOutDeliverBillNo(lastMilestone.getRelatedBillId());// 直营外发单号

            //服务类型为专线送货和自提时，下面几列为空：服中心派单时间、客服中心派单师傅、首次预约操作时间、首次预约上门服务时间、预约次数、最新一次预约操作时间、最新一次预约上门服务时间、安装师傅账号、安装师傅、末端网点
            if(!(serviceType.equals(ServiceType.PICKUP) || serviceType.equals(ServiceType.DEDICATED_DELIVERY))) {
                // 分配
//            MilestoneInfo lastDistributeMilestone = getLastMilestone(operationMap, Arrays.asList(Operation.HEADQUARTERS_DISTRIBUTION, Operation.DIRECT_OR_OUTLET_DISTRIBUTION),
//                    Arrays.asList(Operation.HEADQUARTERS_DISTRIBUTION_CANCEL, Operation.DIRECT_OR_OUTLET_DISTRIBUTION_CANCEL));
                exportVO.setDistributionTime(getNodeTimeByOperationType(operationMap, Operation.HEADQUARTERS_DISTRIBUTION));// 客服中心派单时间
                MilestoneInfo lastHeadquartersDistribution = getLastMilestone(operationMap, Arrays.asList(Operation.HEADQUARTERS_DISTRIBUTION), null);
                List<OperationDetail> operationDetailList = operationDetailMap.getOrDefault(lastHeadquartersDistribution.getId(), Collections.emptyList());
                String masterName = operationDetailList.stream().filter(x -> x.getField().equals("masterName"))
                        .map(OperationDetail::getVal).findFirst().orElse(StringUtils.EMPTY);
                exportVO.setDistributionMaster(masterName);// 客服中心派单师傅
//            // 未分配或取消分配不显示预约节点相关信息
//            if (lastDistributeMilestone != EMPTY_MILESTONE) {
//                // 最后取消分配的时间
//                MilestoneInfo lastCancelDisMilestone = getLastMilestone(operationMap,
//                        Arrays.asList(Operation.HEADQUARTERS_DISTRIBUTION_CANCEL, Operation.DIRECT_OR_OUTLET_DISTRIBUTION_CANCEL), null);
                // 取总部预约、网点预约、师傅预约的集合
                List<MilestoneInfo> milestones = new ArrayList<>();
                milestones.addAll(operationMap.getOrDefault(Operation.HEADQUARTERS_APPOINTMENT, Collections.emptyList()));
                milestones.addAll(operationMap.getOrDefault(Operation.DIRECT_OR_OUTLET_APPOINTMENT, Collections.emptyList()));
                milestones.addAll(operationMap.getOrDefault(Operation.MASTER_APPOINTMENT, Collections.emptyList()));
//                // 取消分配后，之前所有预约记录无效，这里需要过滤取消分配前的预约节点
//                if (lastCancelDisMilestone != EMPTY_MILESTONE) {
//                    milestones = milestones.stream().filter(milestone -> milestone.getOperateTime().after(lastCancelDisMilestone.getOperateTime())).collect(Collectors.toList());
//                }
                if (CollectionUtils.isNotEmpty(milestones)) {
                    // 按操作时间倒序排序
                    milestones.sort(Comparator.comparing(MilestoneInfo::getOperateTime).reversed());
                    MilestoneInfo firstAppointMilestone = milestones.get(milestones.size() - 1);
                    MilestoneInfo lastAppointMilestone = milestones.get(0);
                    // 获取首次上门服务时间
                    List<OperationDetail> operationDetails = operationDetailMap.get(firstAppointMilestone.getId());
                    if(operationDetails != null){
                        Map<String, String> detailsMap = operationDetails.stream().collect(Collectors.toMap(OperationDetail::getField, OperationDetail::getVal));
                        // 预约上门时间取预约上门日期及时段拼接
                        String firstVisitTime = detailsMap.get("doorTime").substring(0, "yyyy-MM-dd ".length()) + parseDoorPeriod(detailsMap.get("doorPeriod"));
                        exportVO.setFirstVisitTime(firstVisitTime);// 首次预约上门服务时间
                    }
                    exportVO.setFirstAppointmentTime(DateUtil.fromDateToH(firstAppointMilestone.getOperateTime()));// 首次预约操作时间
                    // 获取最新上门服务时间
                    operationDetails = operationDetailMap.get(lastAppointMilestone.getId());
                    if(operationDetails != null){
                        Map<String, String> detailsMap = operationDetails.stream().collect(Collectors.toMap(OperationDetail::getField, OperationDetail::getVal));
                        String lastVisitTime = detailsMap.get("doorTime").substring(0, "yyyy-MM-dd ".length()) + parseDoorPeriod(detailsMap.get("doorPeriod"));
                        exportVO.setLastVisitTime(lastVisitTime);// 最新预约上门服务时间
                    }
                    exportVO.setLastAppointmentTime(DateUtil.fromDateToH(lastAppointMilestone.getOperateTime()));// 最新预约操作时间
                    exportVO.setAppointmentCount(milestones.size());// 预约次数
                }
//            }
            }else{
                exportVO.setMasterNode(null);
                exportVO.setMasterName(null);
                exportVO.setMasterPhone(null);
            }
            exportVO.setHomeCheckTime(getLastMilestoneTime(operationMap, Operation.MASTER_HOME_CHECK, Operation.MASTER_HOME_CHECK_CANCEL));// 上门打卡时间
            // 取总部签收、网点签收、师傅签收的集合
            lastMilestone = getLastMilestone(operationMap, Arrays.asList(Operation.DIRECT_OR_OUTLET_SIGN, Operation.HEADQUARTERS_SIGN, Operation.MASTER_SIGN),
                    Arrays.asList(Operation.DIRECT_OR_OUTLET_SIGN_CANCEL, Operation.HEADQUARTERS_SIGN_CANCEL, Operation.MASTER_SIGN_CANCEL));
            exportVO.setSignTime(DateUtil.fromDateToH(lastMilestone.getOperateTime()));// 签收时间

            list.add(exportVO);
        }
        log.info("导出数据耗时: {}", System.currentTimeMillis() - l3);
        return list;
    }

    /**
     * 递归去掉正向节点与逆向节点之间失效的节点，返回按照操作时间倒序排列的list
     * @param milestoneInfos
     */
    public static List<MilestoneInfo> removeInvalidMilestone(List<MilestoneInfo> milestoneInfos) {
        if (CollectionUtils.isEmpty(milestoneInfos)) {
            return Collections.emptyList();
        }
        // 按照操作时间正序排序
        milestoneInfos.sort(Comparator.comparing(MilestoneInfo::getOperateTime));
        List<MilestoneInfo> result = new ArrayList<>();
        // 逆向节点对应的正向节点
        Operation normalOperation = null;
        // 逆向节点index
        int cancelMilestoneIndex = -1;
        // 从list尾部向前遍历
        for (int len = milestoneInfos.size(), index = len - 1; index >= 0; index--) {
            MilestoneInfo current = milestoneInfos.get(index);
            if (normalOperation == null) {
                normalOperation = getNormalOperation(current);
                if(normalOperation != null){
                    boolean before = false;
                    List<MilestoneInfo> milestoneInfos2 = milestoneInfos.subList(0, len - result.size());
                    for(MilestoneInfo temp : milestoneInfos2){
                        if(temp.getOperation().equals(normalOperation.getCode())){
                            before = true;
                        }
                    }
                    if(!before){
                        log.info("运单 "+current.getWaybillId()+" 的节点有可能存在问题，请修复");
                        result.add(current);
                        normalOperation = null;
                        continue;
                    }
                }else{
                    // 记录尾部逆向节点的index
                    cancelMilestoneIndex = index;
                }
            }
            // 尾节点为逆向节点
            if (normalOperation != null) {
                if (Objects.equals(cancelMilestoneIndex, index)) {
                    continue;
                }
                // 找到逆向节点相匹配的正向节点, 递归去掉正向节点与逆向节点之间失效的节点
                if (normalOperation.getCode().equals(current.getOperation())) {
                    result.addAll(removeInvalidMilestone(milestoneInfos.subList(0, index)));
                    break;
                // 直营或加盟取消分配时，保留预约节点
                } else if (Operation.DIRECT_OR_OUTLET_DISTRIBUTION == normalOperation
                        && (Operation.DIRECT_OR_OUTLET_APPOINTMENT.getCode().equals(current.getOperation())
                            || Operation.MASTER_APPOINTMENT.getCode().equals(current.getOperation()))) {
                    result.add(current);
                }
                // 尾节点为逆向节点但找不到相匹配的正向节点, 忽略尾部无效的逆向节点，递归去掉正向节点与逆向节点之间失效的节点
                if (index == 0) {
                    result.addAll(removeInvalidMilestone(milestoneInfos.subList(0, len - 1)));
                    break;
                }
            // 该节点为正向节点, 加入结果集
            } else {
                result.add(current);
//                normalOperation = null;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(1);
        objects.add(2);
        objects.add(3);
        List<Object> objects1 = objects.subList(0, 0);
        System.out.println(objects1.size());
    }

    /**
     * 根据逆向节点获取对应的正向节点，返回null表示节点为正向节点(传入正向节点必定返回null)
     * @param milestoneInfo 逆向节点
     * @return 逆向节点对应正向节点
     */
    public static Operation getNormalOperation(MilestoneInfo milestoneInfo) {
        if (milestoneInfo.getOperation() % 10 == 0) {
              return Operation.getEnumByCode(milestoneInfo.getOperation() / 10);
        }
        return null;
    }

    private String getNodeTimeByOperationType(Map<Operation, List<MilestoneInfo>> operationMap, Operation operation) throws Exception {
        List<MilestoneInfo> milestoneInfos = operationMap.get(operation);
        if (CollectionUtils.isEmpty(milestoneInfos)) {
            return null;
        }
        MilestoneInfo milestoneInfo = milestoneInfos.get(0);
        return DateUtil.fromDateToH(milestoneInfo.getOperateTime());
    }

    private String parseDoorPeriod(String doorPeriod) {
        if (StringUtils.isBlank(doorPeriod)) {
            return StringUtils.EMPTY;
        }
        return doorPeriod.equals("Morning") ? "上午" : doorPeriod.equals("Afternoon") ? "下午" : "晚上";
    }

    /**
     * 标准利润报表
     *
     * @param conditionVO
     * @return
     */
    @Override
    public Workbook exportReportForStandardProfit(ReportConditionVO conditionVO) throws Exception {
        long start = System.currentTimeMillis();
        WriteService writeService = ExportExcelUtils.getWriteService(new StandardProfitHeader(), null);
        while (true) {
            List<StandardProfitExportVO> standardProfitList = getStandardProfitList(conditionVO);
            List<Map<String, Object>> list = standardProfitList.stream().parallel().map(x -> ((JSONObject) JSON.toJSON(x))).collect(Collectors.toList());
            if (list != null && list.size() > 0) {
                writeService.createContents(list);
                conditionVO.setOffset(conditionVO.getOffset() + conditionVO.getSize());
            } else {
                break;
            }
        }
        long end = System.currentTimeMillis();
        log.error("标准利润报表总耗时：{}", (end - start));
        return writeService.build();
    }

    /**
     * 拼接标准利润列表数据
     *
     * @param gridRequest
     * @param conditionVO
     * @return
     * @throws Exception
     */
    @Override
    public List<StandardProfitExportVO> findStandardProfit(GridRequest gridRequest, ReportConditionVO conditionVO) throws Exception {
        long start = System.currentTimeMillis();
        if (gridRequest != null) {
            conditionVO.setOffset((gridRequest.getPage() - 1) * gridRequest.getSize());
            conditionVO.setSize(gridRequest.getSize());
        }
        List<ReportExportVO> dataList = reportMapper.selectWayBillInfoForReport(conditionVO);
        log.info("查询基本信息耗时: {}", System.currentTimeMillis() - start);
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        List<String> waybillIdList = dataList.stream().parallel().map(ReportExportVO::getWaybillId).collect(Collectors.toList());
        List<StandardProfitExportVO> list = getStandardProfitExportVOS(conditionVO, dataList, waybillIdList);
        long end = System.currentTimeMillis();
        log.error("拼接标准利润列表数据耗时：{}", (end - start));
        return list;
    }


    /**
     * 拼接标准利润报表数据
     *
     * @param conditionVO
     */
    private List<StandardProfitExportVO> getStandardProfitList(ReportConditionVO conditionVO) throws Exception {
        long start = System.currentTimeMillis();
        /* 开单基本信息 start */
        //old 运单号	开单网点	开单时间	分配日期	发货人	商家名称	付款方式	服务类型	目的省	目的市	目的区	详细地址	家具类型	品名	总体积	签收时间
        //new 运单号	开单网点	开单时间	分配日期	发货人	商家名称	付款方式	服务类型	目的省	目的市	目的区	详细地址	家具类型	品名	总体积	签收时间    （声明价值）
        /* 开单基本信息 end */
        List<ReportExportVO> dataList = reportMapper.selectWayBillInfoForReport(conditionVO);
        log.info("查询基本信息耗时: {}", System.currentTimeMillis() - start);
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        List<String> waybillIdList = dataList.stream().parallel().map(ReportExportVO::getWaybillId).collect(Collectors.toList());
        List<StandardProfitExportVO> list = getStandardProfitExportVOS(conditionVO, dataList, waybillIdList);
        long end = System.currentTimeMillis();
        log.error("拼接标准利润报表数据耗时：{}", (end - start));
        return list;

    }

    private List<StandardProfitExportVO> getStandardProfitExportVOS(ReportConditionVO conditionVO, List<ReportExportVO> dataList, List<String> waybillIdList) throws Exception {
        long start = System.currentTimeMillis();

        // 准备商品数据
        List<GoodsInfo> goodsInfoList = goodsInfoExtMapper.selectByWaybillIds(waybillIdList);
        Map<String, List<GoodsInfo>> goodsMap = goodsInfoList.stream().collect(Collectors.groupingBy(GoodsInfo::getWaybillId));
        // 准备时间节点相关数据
        List<MilestoneInfo> allMilestoneInfoList = milestoneInfoExtMapper.selectByWaybillIdsAndOperations(waybillIdList, null);
        Map<String, List<MilestoneInfo>> milestoneInfoMap = allMilestoneInfoList.stream().collect(Collectors.groupingBy(MilestoneInfo::getWaybillId));

        /* 开单基本费用信息 start */
        //old 运费	开单送货费	声明价值(waybill)    保价费	安装费	上楼费	揽货费	其他费	时效服务费	上门服务费	入户费	大理石费	干支促销费	超区费	超方费	优惠卷减免费用	赠送金减免费用	返现金减免	开单合计
        //new 运费	开单送货费	保价费	安装费	上楼费	揽货费	其他费	时效服务费	上门服务费	入户费	大理石费	干支促销费	超区费	超方费	优惠卷减免费用	赠送金减免费用	返现金减免	代收货款手续费 开单合计
        //transportFee,deliveryFee,insuranceFee,installFee,upstairsFee,takeGoodsFee,otherFee,agingServiceFee,doorFee,entryHomeFee,marbleFee,promotionFee,exceedAreaFee,exceedSquareFee,couponRelief,giftRelief,cashRelief,collectionServiceFee
        String total = "total";
        String fee = ">";
        String baseFeeType = "transportFee,deliveryFee,insuranceFee,installFee,upstairsFee,takeGoodsFee,otherFee,agingServiceFee,doorFee,entryHomeFee,marbleFee,promotionFee,exceedAreaFee,exceedSquareFee,couponRelief,giftRelief,cashRelief,collectionServiceFee,woodenFee,largeExtraFee";
        String columnList = spliceSumColumnList(baseFeeType);
        List<String> feeTypeList = spliceFeeTypeList(baseFeeType);
        List<FeeVO> openBillBaseFeeList = billingInfoExtMapper.getFeeList(conditionVO, columnList, total, waybillIdList, BillingObject.CUSTOMER.getCode(), BillingType.NORMAL.getCode(), feeTypeList, null);//开单的时候的流水
        Map<String, List<FeeVO>> openBillBaseFeeMap = openBillBaseFeeList.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));
        // 运单关联数据
        List<BillingInfo> billingInfoList = billingInfoExtMapper.select4WaybillAging(waybillIdList);
        Map<String, Double> billingInfoMap = billingInfoList.stream().collect(Collectors.toMap(BillingInfo::getWaybillId, BillingInfo::getTotalPrice, Double::sum));
        /* 开单基本费用信息 end */

        /* 异常处理责任费用信息，包含异常处理责任费，追配处理追配费 start */
        //商家责任异常费合计	服务商责任异常费合计	承运商责任异常费合计	追赔收入	总收入合计
        //relatedBillId is not null and relatedBillId like 'YC%' and fee<0.00(只计算负数)
        String abnFeeType = "abnAdvanceFee,abnAllocateFee,abnAppeaseFee,abnBackFee,abnDeliveryFee,abnDismountingFee,abnDropGoodFee,abnEmptyRunFee," +
                "abnExceedAreaFee,abnExceedSquareFee,abnFloorFee,abnHoistingBuildFee,abnInstallationRate,abnInstallFee,abnInsuranceFee,abnRecoveryFee," +
                "abnMaxUpstairsFee,abnMinUpstairsFee,abnMoveBuildFee,abnPackFee,abnPartFee,abnPickUpGoodsFee,abnPutawayFee,abnRepairFee," +
                "abnReservationRate,abnSecondDoorFee,abnServiceDebit,abnSpecialAreaFee,abnStorageFee,abnTransferFee,abnTranslationalFee,abnTransportFee," +
                "abnUnloadFee,abnUpstairsFee,abnUrgentFee,repairFee,backFee,abnCollectionServiceFee,abnWoodenFee,abnOtherFee";
        String abnColumnList = spliceSumColumnList(abnFeeType);
        List<String> abnFeeTypeList = spliceFeeTypeList(abnFeeType);
        List<FeeVO> abnFeeListForCustomer = billingInfoExtMapper.getFeeList(conditionVO, abnColumnList, total, waybillIdList, BillingObject.CUSTOMER.getCode(), null, abnFeeTypeList, fee);//商家责任异常费合计
        Map<String, List<FeeVO>> abnFeeMapForCustomer = abnFeeListForCustomer.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));
        List<FeeVO> abnFeeListForMaster = billingInfoExtMapper.getFeeList(conditionVO, abnColumnList, total, waybillIdList, BillingObject.MASTER.getCode(), null, abnFeeTypeList, fee);//服务商责任异常费合计
        Map<String, List<FeeVO>> abnFeeMapForMaster = abnFeeListForMaster.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));
        List<FeeVO> abnFeeListForCarrier = billingInfoExtMapper.getFeeList(conditionVO, abnColumnList, total, waybillIdList, BillingObject.CARRIER.getCode(), null, abnFeeTypeList, fee);//承运商责任异常费合计
        Map<String, List<FeeVO>> abnFeeMapForCarrier = abnFeeListForCarrier.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));
        //追赔收入 addFee
        //String compensationFeeType ="addFee";
        String compensationColumnList = spliceSumColumnList("addFee");
        //List<String> compensationFeeTypeList = spliceFeeTypeList(compensationFeeType);
        List<FeeVO> compensationFeeList = billingInfoExtMapper.getFeeList(conditionVO, compensationColumnList, null, waybillIdList, BillingObject.CARRIER.getCode(), BillingType.COMPENSATE.getCode(), null, null);
        Map<String, List<FeeVO>> compensationFeeMap = compensationFeeList.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));
        //总收入合计 前面费用合计（除声明价值、开单合计） + 提点费（后面）
        /* 异常处理责任费用信息，包含异常处理责任费，追配处理追配费 end */


        //外发成本	承运商	安装费成本	支线费成本	提点费	安装师傅 代收手续费成本	安装异常费合计
        // 外发成本 sum(fee) where fee_type='outDeliverCost'
        //String compensationFeeType ="outDeliverCost";
        String outDeliverCostFeeColumnList = spliceSumColumnList("outDeliverCost");
        //List<String> compensationFeeTypeList = spliceFeeTypeList(outDeliverCostFeeFeeType);
        List<FeeVO> outDeliverCostFeeList = billingInfoExtMapper.getFeeList(conditionVO, outDeliverCostFeeColumnList, null, waybillIdList, BillingObject.CARRIER.getCode(), BillingType.NORMAL.getCode(), null, null);
        Map<String, List<FeeVO>> outDeliverCostFeeMap = outDeliverCostFeeList.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));
        //承运商, 一个运单只有一个承运商(carrier_info)

        /* 师傅安装费用信息，包含给的异常费用 */
        //安装费成本	支线费成本   提点费	安装师傅	安装异常费合计
        //安装费成本 ? installFee 服务商
        String installFeeType = "installFee,mediateFee";
        String installFeeColumnList = spliceSumColumnList(installFeeType);
        List<String> installFeeTypeList = spliceFeeTypeList(installFeeType);
        List<FeeVO> installFeeList = billingInfoExtMapper.getFeeList(conditionVO, installFeeColumnList, null, waybillIdList, BillingObject.MASTER.getCode(), BillingType.NORMAL.getCode(), installFeeTypeList, null);
        Map<String, List<FeeVO>> installFeeMap = installFeeList.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));
        //支线费成本 ? transportFee 服务商
        //String transportFeeType ="transportFee";
        String transportFeeColumnList = spliceSumColumnList("deliveryFee");
        //List<String> compensationFeeTypeList = spliceFeeTypeList(transportFeeColumnList);
        List<FeeVO> transportFeeList = billingInfoExtMapper.getFeeList(conditionVO, transportFeeColumnList, null, waybillIdList, BillingObject.MASTER.getCode(), BillingType.NORMAL.getCode(), null, null);
        Map<String, List<FeeVO>> transportFeeMap = transportFeeList.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));
        //提点费 pickUpNodeFee,师傅的钱，对公司来说是收入（提成）mediateFee
        //String pickUpNodeFeeType ="pickUpNodeFee";
        String pickUpNodeFeeColumnList = spliceSumColumnList("pickUpNodeFee");
        //List<String> compensationFeeTypeList = spliceFeeTypeList(pickUpNodeFeeColumnList);
        List<FeeVO> pickUpNodeFeeList = billingInfoExtMapper.getFeeList(conditionVO, pickUpNodeFeeColumnList, null, waybillIdList, null, BillingType.NORMAL.getCode(), null, null);
        Map<String, List<FeeVO>> pickUpNodeFeeMap = pickUpNodeFeeList.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));//提点费 pickUpNodeFee,师傅的钱，对公司来说是收入（提成）
        //代收手续费成本 String collectionServiceFeeType ="collectionServiceFee";
        String collectionServiceFeeColumnList = spliceSumColumnList("collectionServiceFee");
        //List<String> collectionServiceFeeTypeList = spliceFeeTypeList(collectionServiceFeeColumnList);
        List<FeeVO> collectionServiceFeeList = billingInfoExtMapper.getFeeList(conditionVO, collectionServiceFeeColumnList, null, waybillIdList, BillingObject.MASTER.getCode(), BillingType.NORMAL.getCode(), null, null);
        Map<String, List<FeeVO>> collectionServiceFeeMap = collectionServiceFeeList.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));

        //安装师傅 与运单关联

        //安装异常费合计 正数全部异常费用合计 不包含追赔（除理赔，理赔对象是商家）,也不包含维修/返货的费用/追偿责任费
        // abnAdvanceFee,abnAllocateFee,abnAppeaseFee,abnBackFee,abnDeliveryFee,abnDismountingFee,abnDropGoodFee,abnEmptyRunFee,
        // abnExceedAreaFee,abnExceedSquareFee,abnFloorFee,abnHoistingBuildFee,abnInstallationRate,abnInstallFee,abnInsuranceFee,abnRecoveryFee,
        // abnMaxUpstairsFee,abnMinUpstairsFee,abnMoveBuildFee,abnPackFee,abnPartFee,abnPickUpGoodsFee,abnPutawayFee,
        // abnReservationRate,abnSecondDoorFee,abnServiceDebit,abnSpecialAreaFee,abnStorageFee,abnTransferFee,abnTranslationalFee,abnTransportFee,
        // abnUnloadFee,abnUpstairsFee,abnUrgentFee
        String abnFeeType1 = "abnAdvanceFee,abnAllocateFee,abnAppeaseFee,abnDeliveryFee,abnDismountingFee,abnDropGoodFee,abnEmptyRunFee," +
                "abnExceedAreaFee,abnExceedSquareFee,abnFloorFee,abnHoistingBuildFee,abnInstallationRate,abnInstallFee,abnInsuranceFee," +
                "abnMaxUpstairsFee,abnMinUpstairsFee,abnMoveBuildFee,abnPackFee,abnPartFee,abnPickUpGoodsFee,abnPutawayFee," +
                "abnReservationRate,abnSecondDoorFee,abnServiceDebit,abnSpecialAreaFee,abnStorageFee,abnTransferFee,abnTranslationalFee,abnTransportFee," +
                "abnUnloadFee,abnUpstairsFee,abnUrgentFee,abnCollectionServiceFee,abnWoodenFee,abnOtherFee";
        String abnColumnList1 = spliceSumColumnList(abnFeeType1);
        List<String> abnFeeTypeList1 = spliceFeeTypeList(abnFeeType1);
        List<FeeVO> masterInstallFeeList = billingInfoExtMapper.getFeeList(conditionVO, abnColumnList1, total, waybillIdList,  BillingObject.MASTER.getCode(), BillingType.ABNORMAL.getCode(), abnFeeTypeList1, null);
        Map<String, List<FeeVO>> masterInstallFeeMap = masterInstallFeeList.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));


        //维修费	返货费	理赔费用	总成本合计	毛利
        //abnRepairFee abnBackFee claimFee+dribbletAddFee+accommodation
        //维修费，服务商 sum(abnRepairFee)（维修任务与处理给师傅维修的钱）
        fee = "<";
        String abnRepairFeeType = "abnRepairFee,repairFee";//异常维修（必须正数）+正常维修（必须正数）=维修费
        String abnRepairFeeColumnList = spliceSumColumnList(abnRepairFeeType);
        List<String> abnRepairFeeTypeList = spliceFeeTypeList(abnRepairFeeType);
        List<FeeVO> abnRepairFeeList = billingInfoExtMapper.getFeeList(conditionVO, abnRepairFeeColumnList, null, waybillIdList, null, null, abnRepairFeeTypeList, fee);
        Map<String, List<FeeVO>> abnRepairFeeMap = abnRepairFeeList.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));
        //返货费，服务商 sum(abnBackFee)（返货任务的钱,通过售后单查询费用）
        String abnBackFeeType = "abnBackFee,backFee";//异常返货费（必须正数）+正常返货费（必须正数）=返货费
        String abnBackFeeColumnList = spliceSumColumnList(abnBackFeeType);
        List<String> abnBackFeeTypeList = spliceFeeTypeList(abnBackFeeType);
        List<FeeVO> abnBackFeeList = billingInfoExtMapper.getFeeList(conditionVO, abnBackFeeColumnList, null, waybillIdList, null, null, abnBackFeeTypeList, fee);
        Map<String, List<FeeVO>> abnBackFeeMap = abnBackFeeList.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));

        //理赔费用，发货人（理赔给商家的钱）
        // sum(accommodation:"s_通融金额",claimFee:"s_理赔", dribbletAddFee:"s_小额理赔")
        // sum(claimFee + dribbletAddFee + accommodation)
        String claimFeeType = "claimFee,dribbletAddFee,accommodation";
        String claimColumnList = spliceSumColumnList(claimFeeType);
        List<String> claimFeeTypeList = spliceFeeTypeList(claimFeeType);
        List<FeeVO> feeListForClaim = billingInfoExtMapper.getFeeList(conditionVO, claimColumnList, total, waybillIdList, null, BillingType.COMPENSATE.getCode(), claimFeeTypeList, null);
        Map<String, List<FeeVO>> claimFeeMap = feeListForClaim.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));

        //部分好评、师傅退款
        String feeType2 = "abnPraiseFee,abnPartialRefundFee";
        String columnList2 = spliceSumColumnList(feeType2);
        //List<String> feeTypeList2 = spliceFeeTypeList(feeType2);
        List<FeeVO> feeList2 = billingInfoExtMapper.getFeeList(conditionVO, columnList2, null, waybillIdList, null, null, null, null);
        Map<String, List<FeeVO>> feeMap = feeList2.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));

        //总成本合计=外发成本+安装费成本+支线费成本+安装师傅+安装异常费合计+维修费+返货费+理赔费用

        //毛利=总收入合计-总成本合计

        /* 拼接数据 */
        List<StandardProfitExportVO> list = new ArrayList<StandardProfitExportVO>(dataList.size());
        /*for (WayBillAgingExportVO data : dataList) {*/
        for (ReportExportVO data : dataList) {
            StandardProfitExportVO exportVO = new StandardProfitExportVO();
            Beans.from(data).to(exportVO);

            /* 参考运单时效报表如有修改，请同时修改，start */
            /*------------------------------------运单基本信息相关------------------------------------*/
            exportVO.setOpenBillTime(DateUtil.fromDateToH(data.getOpenBillTime()));// 开单时间
            WaybillStatus waybillStatus = WaybillStatus.getEnumByCode(data.getWaybillStatus());
            if (waybillStatus != null) {
                exportVO.setWaybillStatus(waybillStatus.getMessage());// 运单状态
            }
            SettlementType settlementType = SettlementType.getEnumByCode(data.getSettlementType());
            if (settlementType != null) {
                exportVO.setSettlementType(settlementType.getMessage());// 结算类型
            }
            CustomerType customerType = CustomerType.getEnumByCode(data.getMerchantType());
            if (customerType != null) {
                exportVO.setCustomerType(customerType.getMessage());// 商家类型(商家标识)
            }
            ServiceType serviceType = ServiceType.getEnumByCode(data.getServiceType());
            if (serviceType != null) {
                exportVO.setServiceType(serviceType.getMessage());// 服务类型
            }
            PaymentType paymentType = PaymentType.getEnumByCode(data.getPaymentType());
            if (paymentType != null) {
                exportVO.setPaymentType(paymentType.getMessage());// 付款方式
            }
            List<GoodsInfo> goodsInfos = goodsMap.get(data.getWaybillId());
            if (goodsInfos != null) {
                StringBuilder goodsNameBuilder = new StringBuilder(), packingBuilder = new StringBuilder();
                int packingTotalCount = 0;
                double totalVolume = 0d;
                for (GoodsInfo goodsInfo : goodsInfos) {
                    packingTotalCount += goodsInfo.getPackingItems() == null ? 0 : goodsInfo.getPackingItems();
                    totalVolume += goodsInfo.getVolume() == null ? 0d : goodsInfo.getVolume();
                    goodsNameBuilder.append(goodsInfo.getGoodsName())
                            .append("_").append(goodsInfo.getInstallItems() == null?"0":goodsInfo.getInstallItems())
                            //.append("_").append(goodsInfo.getPackingItems() == null?"0":goodsInfo.getPackingItems())
                            .append(";");
                }
                exportVO.setGoodsName(goodsNameBuilder.substring(0, goodsNameBuilder.length() - 1));// 品名
                exportVO.setTotalVolume(totalVolume);// 总体积
                exportVO.setGoodsType(data.getGoodsType()==null?"":data.getGoodsType());// 货物类型
                exportVO.setPackingTotalCount(packingTotalCount);// 包装总件数
            }

            /*------------------------------------节点时间相关------------------------------------*/
            List<MilestoneInfo> milestoneInfos = milestoneInfoMap.getOrDefault(data.getWaybillId(), Collections.emptyList());
            // 移除失效节点
            milestoneInfos = removeInvalidMilestone(milestoneInfos);
            // 按照操作节点分组
            Map<Operation, List<MilestoneInfo>> operationMap = milestoneInfos.stream().collect(Collectors.groupingBy(x -> Operation.getEnumByCode(x.getOperation())));
            // 分配
            //MilestoneInfo lastMilestone = getLastMilestone(operationMap, Arrays.asList(Operation.HEADQUARTERS_DISTRIBUTION, Operation.DIRECT_OR_OUTLET_DISTRIBUTION),
            //        Arrays.asList(Operation.HEADQUARTERS_DISTRIBUTION_CANCEL, Operation.DIRECT_OR_OUTLET_DISTRIBUTION_CANCEL));
            //exportVO.setDistributionTime(DateUtil.fromDateToH(lastMilestone.getOperateTime()));// 客服中心派单时间
            exportVO.setDistributionTime(getNodeTimeByOperationType(operationMap, Operation.HEADQUARTERS_DISTRIBUTION));// 客服中心派单时间
            // 取总部签收、网点签收、师傅签收的集合
            MilestoneInfo lastMilestone = getLastMilestone(operationMap, Arrays.asList(Operation.DIRECT_OR_OUTLET_SIGN, Operation.HEADQUARTERS_SIGN, Operation.MASTER_SIGN),
                    Arrays.asList(Operation.DIRECT_OR_OUTLET_SIGN_CANCEL, Operation.HEADQUARTERS_SIGN_CANCEL, Operation.MASTER_SIGN_CANCEL));
            exportVO.setSignTime(DateUtil.fromDateToH(lastMilestone.getOperateTime()));// 签收时间
            /* 以上参考运单时效报表如有修改，请同时修改，end */

            String waybillId = data.getWaybillId();
            exportVO.setCarrierName(data.getCarrier());
            exportVO.setAddress(data.getReceiveAddress());
            exportVO.setSecondCustomer(data.getSecondMerchantName());

            /* 开单基本费用信息 */
            List<FeeVO> feeVOS = openBillBaseFeeMap.get(waybillId);
            if (feeVOS != null) {
                FeeVO feeVO = feeVOS.get(0);
                exportVO.setConsignorTransportFee(feeVO.getTransportFee());//运费
                exportVO.setDeliveryFee(feeVO.getDeliveryFee());//开单送货费
                exportVO.setInsuranceFee(feeVO.getInsuranceFee());//保价费
                exportVO.setOpenBillInstallFee(feeVO.getInstallFee());//安装费
                exportVO.setUpstairsFee(feeVO.getUpstairsFee());//上楼费
                exportVO.setTakeGoodsFee(feeVO.getTakeGoodsFee());//揽货费
                exportVO.setOtherFee(feeVO.getOtherFee());//其他费
                exportVO.setAgingServiceFee(feeVO.getAgingServiceFee());//时效服务费
                exportVO.setDoorFee(feeVO.getDoorFee());//上门服务费
                exportVO.setEntryHomeFee(feeVO.getEntryHomeFee());//入户费
                exportVO.setMarbleFee(feeVO.getMarbleFee());//大理石费
                exportVO.setPromotionFee(feeVO.getPromotionFee());//干支促销费
                exportVO.setExceedAreaFee(feeVO.getExceedAreaFee());//超区费
                exportVO.setExceedSquareFee(feeVO.getExceedSquareFee());//超方费
                exportVO.setCouponRelief(feeVO.getCouponRelief());//优惠卷减免费用
                exportVO.setGiftRelief(feeVO.getGiftRelief());//赠送金减免费用
                exportVO.setCashRelief(feeVO.getCashRelief());//返现金减免
                exportVO.setCollectionServiceFee(feeVO.getCollectionServiceFee());//代收货款手续费
                exportVO.setWoodenFee(feeVO.getWoodenFee());//打木架费
                exportVO.setLargeExtraFee(feeVO.getLargeExtraFee());//大票附加费
                //exportVO.setTotalOpenBillFee(feeVO.getTotal());//开单合计
            }

            Double totalFee = billingInfoMap.get(waybillId)==null?0.00:billingInfoMap.get(waybillId);
            exportVO.setTotalOpenBillFee(totalFee);//开单合计

            List<FeeVO> feeVOS1 = abnFeeMapForCustomer.get(waybillId);
            if (feeVOS1 != null) {
                FeeVO feeVO = feeVOS1.get(0);
                exportVO.setTotalConsignorAbnFee(feeVO.getTotal());//商家承担异常费合计
            }
            List<FeeVO> feeVOS2 = abnFeeMapForMaster.get(waybillId);
            if (feeVOS2 != null) {
                FeeVO feeVO = feeVOS2.get(0);
                exportVO.setTotalMasterAbnFee(feeVO.getTotal());//服务商责任异常费合计
            }
            List<FeeVO> feeVOS3 = abnFeeMapForCarrier.get(waybillId);
            if (feeVOS3 != null) {
                FeeVO feeVO = feeVOS3.get(0);
                exportVO.setTotalCarrierAbnFee(feeVO.getTotal());//承运商责任异常费合计
            }
            List<FeeVO> feeVOS4 = compensationFeeMap.get(waybillId);
            if (feeVOS4 != null) {
                FeeVO feeVO = feeVOS4.get(0);
                exportVO.setAddFee(feeVO.getAddFee());//追赔收入
            }
            List<FeeVO> feeVOS5 = outDeliverCostFeeMap.get(waybillId);
            if (feeVOS5 != null) {
                FeeVO feeVO = feeVOS5.get(0);
                exportVO.setOutDeliverCost(feeVO.getOutDeliverCost());//外发成本
            }
            List<FeeVO> feeVOS6 = installFeeMap.get(waybillId);
            if (feeVOS6 != null) {
                FeeVO feeVO = feeVOS6.get(0);
                exportVO.setMasterInstallFee(feeVO.getTotal());//安装费成本
            }
            List<FeeVO> feeVOS7 = transportFeeMap.get(waybillId);
            if (feeVOS7 != null) {
                FeeVO feeVO = feeVOS7.get(0);
                exportVO.setMasterTransportFee(feeVO.getDeliveryFee());//支线费成本
            }
            List<FeeVO> feeVOS8 = pickUpNodeFeeMap.get(waybillId);
            if (feeVOS8 != null) {
                FeeVO feeVO = feeVOS8.get(0);
                exportVO.setPickUpNodeFee(feeVO.getPickUpNodeFee());//提点费
            }
            List<FeeVO> feeVOS9 = masterInstallFeeMap.get(waybillId);
            if (feeVOS9 != null) {
                FeeVO feeVO = feeVOS9.get(0);
                exportVO.setTotalInstallAbnFee(feeVO.getTotal());//安装异常费合计
            }
            List<FeeVO> feeVOS90 = collectionServiceFeeMap.get(waybillId);
            if (feeVOS90 != null) {
                FeeVO feeVO = feeVOS90.get(0);
                Double collectionServiceFee = feeVO.getCollectionServiceFee() == null ? 0.00 : feeVO.getCollectionServiceFee();
                Double totalInstallAbnFee = exportVO.getTotalInstallAbnFee() == null ? 0.00 : exportVO.getTotalInstallAbnFee();
                totalInstallAbnFee = totalInstallAbnFee + collectionServiceFee;
                exportVO.setTotalInstallAbnFee(totalInstallAbnFee);//安装异常费合计
            }
            List<FeeVO> feeVOS10 = abnRepairFeeMap.get(waybillId);
            if (feeVOS10 != null) {
                FeeVO feeVO = feeVOS10.get(0);
                exportVO.setRepairFee(feeVO.getTotal());//维修费
            }
            List<FeeVO> feeVOS11 = abnBackFeeMap.get(waybillId);
            if (feeVOS11 != null) {
                FeeVO feeVO = feeVOS11.get(0);
                exportVO.setBackFee(feeVO.getTotal());//返货费
            }
            List<FeeVO> feeVOS12 = claimFeeMap.get(waybillId);
            if (feeVOS12 != null) {
                FeeVO feeVO = feeVOS12.get(0);
                exportVO.setClaimFee(feeVO.getTotal());//理赔费用
            }
            List<FeeVO> feeVOS13 = feeMap.get(waybillId);
            if (feeVOS13 != null) {
                FeeVO feeVO = feeVOS13.get(0);
                exportVO.setAbnPraiseFee(feeVO.getAbnPraiseFee());//师傅好评
                exportVO.setAbnPartialRefundFee(feeVO.getAbnPartialRefundFee());//部分退款
            }
            /* 总收入合计 */
            double totalIncome = exportVO.getTotalOpenBillFee() + exportVO.getTotalConsignorAbnFee() + exportVO.getTotalMasterAbnFee()
                    + exportVO.getTotalCarrierAbnFee() + exportVO.getAddFee() + exportVO.getPickUpNodeFee();
            /* 总成本合计 给出去的钱 */
            double totalCost = exportVO.getOutDeliverCost() + exportVO.getMasterInstallFee() + exportVO.getMasterTransportFee()
                    + exportVO.getTotalInstallAbnFee() + exportVO.getRepairFee() + exportVO.getBackFee()
                    + exportVO.getClaimFee() + exportVO.getAbnPraiseFee() + exportVO.getAbnPartialRefundFee();
            totalIncome = new BigDecimal(totalIncome).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            totalCost = new BigDecimal(totalCost).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            exportVO.setTotalIncome(totalIncome);
            exportVO.setTotalCost(totalCost);
            /* 毛利 */
            double grossProfit = totalIncome + totalCost;
            grossProfit = new BigDecimal(grossProfit).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            exportVO.setGrossProfit(grossProfit);
            list.add(exportVO);
        }
        long end = System.currentTimeMillis();
        log.error("拼接标准利润数据耗时：{}", (end - start));
        return list;
    }


    /**
     * 拼接Column，sum聚合
     *
     * @param s
     * @return
     */
    private String spliceSumColumnList(String s) throws Exception {
        if (StringUtils.isNotBlank(s)) {
            String[] split = s.split(",");
            if (split.length > 0) {
                List<String> stringList = Arrays.asList(split);
                String columnList = "";
                for (String str : stringList) {
                    str = StringUtils.trim(str);
                    columnList += ",sum(if(a.fee_type='" + str + "',a.fee,0.00)) AS " + str;
                }
                return columnList;
            }
            return null;
        }
        return null;
    }

    /**
     * 费用类型 string 转 list
     *
     * @param s
     * @return
     */
    private List<String> spliceFeeTypeList(String s) throws Exception {
        if (StringUtils.isNotBlank(s)) {
            String[] split = s.split(",");
            if (split.length > 0) {
                List<String> stringList = Arrays.asList(split);
                List<String> strings = new ArrayList<String>();
                for (String str : stringList) {
                    str = StringUtils.trim(str);
                    strings.add(str);
                }
                return strings;
            }
            return null;
        }
        return null;
    }

    /**
     * 异常利润报表: 所有费用都是取异常的费用
     *
     * @param conditionVO
     * @return
     */
    @Override
    public Workbook exportReportForAbnormalProfit(ReportConditionVO conditionVO) throws Exception {
        long start = System.currentTimeMillis();
        WriteService writeService = ExportExcelUtils.getWriteService(new AbnormalProfitHeader(), null);
        while (true) {
            List<AbnormalProfitExportVO> standardProfitList = getAbnormalProfitList(conditionVO);
            List<Map<String, Object>> list = standardProfitList.parallelStream().map(x -> ((JSONObject) JSON.toJSON(x))).collect(Collectors.toList());
            if (list != null && list.size() > 0) {
                writeService.createContents(list);
                conditionVO.setOffset(conditionVO.getOffset() + conditionVO.getSize());
            } else {
                break;
            }
        }
        long end = System.currentTimeMillis();
        log.error("异常利润报表总耗时：{}", (end - start));
        return writeService.build();
    }

    /**
     * 异常利润查询
     */
    @Override
    public List<AbnormalProfitExportVO> findAbnormalProfit(GridRequest gridRequest, ReportConditionVO conditionVO) throws Exception {
        long start = System.currentTimeMillis();
        if (gridRequest != null) {
            conditionVO.setOffset((gridRequest.getPage() - 1) * gridRequest.getSize());
            conditionVO.setSize(gridRequest.getSize());
        }
        List<ReportExportVO> dataList = reportMapper.selectWayBillInfoForReport(conditionVO);
        log.info("查询基本信息耗时: {}", System.currentTimeMillis() - start);
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        List<String> waybillIdList = dataList.stream().parallel().map(ReportExportVO::getWaybillId).collect(Collectors.toList());
        List<AbnormalProfitExportVO> list = getAbnormalProfitExportVOS(conditionVO, dataList, waybillIdList);
        long end = System.currentTimeMillis();
        log.error("拼接异常利润列表数据耗时：{}", (end - start));
        return list;
    }


    /**
     * 拼接拼接利润报表数据
     *
     * @param conditionVO
     * @return
     * @throws Exception
     */
    private List<AbnormalProfitExportVO> getAbnormalProfitList(ReportConditionVO conditionVO) throws Exception {
        long start = System.currentTimeMillis();
        List<ReportExportVO> dataList = reportMapper.selectWayBillInfoForReport(conditionVO);
        log.info("查询基本信息耗时: {}", System.currentTimeMillis() - start);
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        List<String> waybillIdList = dataList.stream().parallel().map(ReportExportVO::getWaybillId).collect(Collectors.toList());
        List<AbnormalProfitExportVO> list = getAbnormalProfitExportVOS(conditionVO, dataList, waybillIdList);
        long end = System.currentTimeMillis();
        log.error("拼接标准利润报表数据耗时：{}", (end - start));
        return list;
    }

    private List<AbnormalProfitExportVO> getAbnormalProfitExportVOS(ReportConditionVO conditionVO, List<ReportExportVO> dataList, List<String> waybillIdList) throws Exception {
        long start = System.currentTimeMillis();

        String total = "total";
        String fee = ">";
        String abnFeeType = "abnAdvanceFee,abnAllocateFee,abnAppeaseFee,abnBackFee,abnDeliveryFee,abnDismountingFee,abnDropGoodFee,abnEmptyRunFee," +
                "abnExceedAreaFee,abnExceedSquareFee,abnFloorFee,abnHoistingBuildFee,abnInstallationRate,abnInstallFee,abnInsuranceFee,abnRecoveryFee," +
                "abnMaxUpstairsFee,abnMinUpstairsFee,abnMoveBuildFee,abnPackFee,abnPartFee,abnPickUpGoodsFee,abnPutawayFee,abnRepairFee," +
                "abnReservationRate,abnSecondDoorFee,abnServiceDebit,abnSpecialAreaFee,abnStorageFee,abnTransferFee,abnTranslationalFee,abnTransportFee," +
                "abnUnloadFee,abnUpstairsFee,abnUrgentFee,repairFee,backFee,abnCollectionServiceFee,abnWoodenFee,abnOtherFee";
        String abnColumnList = spliceSumColumnList(abnFeeType);
        List<String> abnFeeTypeList = spliceFeeTypeList(abnFeeType);
        /* 流水对象是发货人，流水是负数扣师傅流水 */
        List<FeeVO> abnFeeListForCustomerPay = billingInfoExtMapper.getFeeList(conditionVO, abnColumnList, total, waybillIdList, BillingObject.CUSTOMER.getCode(), null, abnFeeTypeList, fee);
        Map<String, List<FeeVO>> abnFeeMapForCustomerPay = abnFeeListForCustomerPay.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));

        String abnExceedAreaFee = spliceSumColumnList("abnExceedAreaFee");
        List<String> abnExceedAreaFeeList = spliceFeeTypeList("abnExceedAreaFee");
        List<FeeVO> abnExceedAreaFeeListForCustomerPay = billingInfoExtMapper.getFeeList(conditionVO, abnExceedAreaFee, total, waybillIdList, BillingObject.CUSTOMER.getCode(), null, abnExceedAreaFeeList, null);
        Map<String, List<FeeVO>> abnExceedAreaFeeMapForCustomerPay = abnExceedAreaFeeListForCustomerPay.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));


        /* 流水对象是承运商，费用为负数，扣承运商的钱*/
        List<FeeVO> abnFeeListForCarrierPay = billingInfoExtMapper.getFeeList(conditionVO, abnColumnList, total, waybillIdList, BillingObject.CARRIER.getCode(), null, abnFeeTypeList, fee);
        Map<String, List<FeeVO>> abnFeeMapForCarrierPay = abnFeeListForCarrierPay.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));

        /* 服务商没有安装费和上楼费 */
        /* 流水对象是服务商，流水是负数，扣师傅钱 */
        List<FeeVO> abnFeeListForMatserPay = billingInfoExtMapper.getFeeList(conditionVO, abnColumnList, total, waybillIdList, BillingObject.MASTER.getCode(), null, abnFeeTypeList, fee);
        Map<String, List<FeeVO>> abnFeeMapForMatserPay = abnFeeListForMatserPay.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));
        /* 给师傅加钱的流水，也就是服务商，流水是正数，的异常费用 */
        fee = "<";
        List<FeeVO> abnFeeListForMatserCollect = billingInfoExtMapper.getFeeList(conditionVO, abnColumnList, total, waybillIdList, BillingObject.MASTER.getCode(), null, abnFeeTypeList, fee);
        Map<String, List<FeeVO>> abnFeeMapForMatserCollect = abnFeeListForMatserCollect.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));

        //部分好评、师傅退款
        String feeType2 = "abnPraiseFee,abnPartialRefundFee";
        String columnList2 = spliceSumColumnList(feeType2);
        //List<String> feeTypeList2 = spliceFeeTypeList(feeType2);
        List<FeeVO> feeList2 = billingInfoExtMapper.getFeeList(conditionVO, columnList2, total, waybillIdList, null, null, null, null);
        Map<String, List<FeeVO>> feeMap = feeList2.stream().parallel().collect(Collectors.groupingBy(FeeVO::getWaybillId));

        // 准备时间节点相关数据
        List<MilestoneInfo> allMilestoneInfoList = milestoneInfoExtMapper.selectByWaybillIdsAndOperations(waybillIdList, null);
        Map<String, List<MilestoneInfo>> milestoneInfoMap = allMilestoneInfoList.stream().collect(Collectors.groupingBy(MilestoneInfo::getWaybillId));

        List<AbnormalProfitExportVO> list = new ArrayList<AbnormalProfitExportVO>(dataList.size());
        for (ReportExportVO data : dataList) {
            AbnormalProfitExportVO exportVO = new AbnormalProfitExportVO();
            Beans.from(data).to(exportVO);

            /*------------------------------------节点时间相关------------------------------------*/
            List<MilestoneInfo> milestoneInfos = milestoneInfoMap.getOrDefault(data.getWaybillId(), Collections.emptyList());
            // 移除失效节点
            milestoneInfos = removeInvalidMilestone(milestoneInfos);
            // 按照操作节点分组
            Map<Operation, List<MilestoneInfo>> operationMap = milestoneInfos.stream().collect(Collectors.groupingBy(x -> Operation.getEnumByCode(x.getOperation())));
            // 取总部签收、网点签收、师傅签收的集合
            MilestoneInfo lastMilestone = getLastMilestone(operationMap, Arrays.asList(Operation.DIRECT_OR_OUTLET_SIGN, Operation.HEADQUARTERS_SIGN, Operation.MASTER_SIGN),
                    Arrays.asList(Operation.DIRECT_OR_OUTLET_SIGN_CANCEL, Operation.HEADQUARTERS_SIGN_CANCEL, Operation.MASTER_SIGN_CANCEL));
            exportVO.setSignTime(DateUtil.fromDateToH(lastMilestone.getOperateTime()));// 签收时间
            /* 以上参考运单时效报表如有修改，请同时修改，end */

            /* 参考运单时效报表如有修改，请同时修改，start */
            /*------------------------------------运单基本信息相关------------------------------------*/
            exportVO.setOpenBillTime(DateUtil.fromDateToH(data.getOpenBillTime()));// 开单时间
            WaybillStatus waybillStatus = WaybillStatus.getEnumByCode(data.getWaybillStatus());
            if (waybillStatus != null) {
                exportVO.setWaybillStatus(waybillStatus.getMessage());// 运单状态
            }
            SettlementType settlementType = SettlementType.getEnumByCode(data.getSettlementType());
            if (settlementType != null) {
                exportVO.setSettlementType(settlementType.getMessage());// 结算类型
            }
            CustomerType customerType = CustomerType.getEnumByCode(data.getMerchantType());
            if (customerType != null) {
                exportVO.setCustomerType(customerType.getMessage());// 商家类型(商家标识)
            }
            ServiceType serviceType = ServiceType.getEnumByCode(data.getServiceType());
            if (serviceType != null) {
                exportVO.setServiceType(serviceType.getMessage());// 服务类型
            }

            exportVO.setSecondCustomer(data.getSecondMerchantName());

            String waybillId = data.getWaybillId();

            List<FeeVO> feeVOS = abnFeeMapForCustomerPay.get(waybillId);
            if (feeVOS != null) {
                FeeVO feeVO = feeVOS.get(0);
                exportVO.setConsignorDeliveryFee(feeVO.getAbnDeliveryFee());//发货人送货费
                //exportVO.setConsignorInstallFee2(feeVO.getAbnInstallFee2());//发货人安装费,无,2018.06前旧数据可能有
                exportVO.setConsignorInstallFee2(0.00d);//发货人安装费,无,2018.06前旧数据可能有
                exportVO.setConsignorAdvanceFee(feeVO.getAbnAdvanceFee());//发货人垫付费
                //exportVO.setConsignorUpstairsFee2(feeVO.getAbnUpstairsFee2());//发货人上楼费,无,2018.06前旧数据可能有
                exportVO.setConsignorUpstairsFee2(0.00d);//发货人上楼费,无,2018.06前旧数据可能有
                exportVO.setConsignorExceedSquareFee(feeVO.getAbnExceedSquareFee());//发货人超方费
                exportVO.setConsignorExceedAreaFee(feeVO.getAbnExceedAreaFee());//发货人超区费
                exportVO.setConsignorDismountingFee(feeVO.getAbnDismountingFee());//发货人拆装费
                exportVO.setConsignorHoistingBuildFee(feeVO.getAbnHoistingBuildFee());//发货人吊楼费
                exportVO.setConsignorTranslationalFee(feeVO.getAbnTranslationalFee());//发货人平移费
                exportVO.setConsignorEmptyRunFee(feeVO.getAbnEmptyRunFee());//发货人空跑费
                exportVO.setConsignorSpecialAreaFee(feeVO.getAbnSpecialAreaFee());//发货人特殊区域费
                exportVO.setConsignorStorageFee(feeVO.getAbnStorageFee());//发货人仓储费
                exportVO.setConsignorUrgentFee(feeVO.getAbnUrgentFee());//发货人加急费
                exportVO.setConsignorSecondDoorFee(feeVO.getAbnSecondDoorFee());//发货人二次上门费
                exportVO.setConsignorMinUpstairsFee(feeVO.getAbnMinUpstairsFee());//发货人＞7楼搬楼费
                exportVO.setConsignorMaxUpstairsFee(feeVO.getAbnMaxUpstairsFee());//发货人≤7楼搬楼费
                exportVO.setConsignorPickUpGoodsFee(feeVO.getAbnPickUpGoodsFee());//发货人提货费
                exportVO.setConsignorInsuranceFee(feeVO.getAbnInsuranceFee());//发货人保价费
                exportVO.setConsignorServiceDebit(feeVO.getAbnServiceDebit());//发货人的服务商扣款
                exportVO.setConsignorReservationRate(feeVO.getAbnReservationRate());//发货人预约超时费
                exportVO.setConsignorInstallationRate(feeVO.getAbnInstallationRate());//发货人安装超时费
                exportVO.setConsignorInstallFee(feeVO.getAbnInstallFee());//发货人异常安装费
                exportVO.setConsignorUpstairsFee(feeVO.getAbnUpstairsFee());//发货人异常上楼费
                exportVO.setConsignorDropGoodFee(feeVO.getAbnDropGoodFee());//发货人弃货费
                exportVO.setConsignorRepairFee(feeVO.getRepairFee());//发货人维修费
                exportVO.setConsignorAbnRepairFee(feeVO.getAbnRepairFee());//发货人维修费
                exportVO.setConsignorBackFee(feeVO.getBackFee());//发货人返货费
                exportVO.setConsignorAbnBackFee(feeVO.getAbnBackFee());//发货人返货费
                exportVO.setConsignorPartFee(feeVO.getAbnPartFee());//发货人补件费
                exportVO.setConsignorAbnRecoveryFee(feeVO.getAbnRecoveryFee());//发货人异常追偿责任费
                exportVO.setConsignorAbnCollectionServiceFee(feeVO.getAbnCollectionServiceFee());//发货人异常代收货款手续费
                exportVO.setConsignorAbnWoodenFee(feeVO.getAbnWoodenFee());//发货人异常打木架费
                exportVO.setConsignorAbnOtherFee(feeVO.getAbnOtherFee());//发货人异常其他费
                exportVO.setTotalConsignorAbnFee(feeVO.getTotal());//发货人异常合计
            }
            List<FeeVO> feeVOS0 = abnExceedAreaFeeMapForCustomerPay.get(waybillId);
            if (feeVOS0 != null) {
                FeeVO feeVO = feeVOS0.get(0);
                exportVO.setConsignorExceedAreaFee(feeVO.getTotal());//发货人超区费
            }
            List<FeeVO> feeVOS1 = abnFeeMapForCarrierPay.get(waybillId);
            if (feeVOS1 != null) {
                FeeVO feeVO = feeVOS1.get(0);
                exportVO.setCarrierDeliveryFee(feeVO.getAbnDeliveryFee());//承运商送货费
                //exportVO.setCarrierInstallFee2(feeVO.getAbnInstallFee2());//承运商安装费,无,2018.06前旧数据可能有
                exportVO.setCarrierInstallFee2(0.00d);//承运商安装费,无,2018.06前旧数据可能有
                exportVO.setCarrierAdvanceFee(feeVO.getAbnAdvanceFee());//承运商垫付费
                //exportVO.setCarrierUpstairsFee2(feeVO.getAbnUpstairsFee2());//承运商上楼费,无,2018.06前旧数据可能有
                exportVO.setCarrierUpstairsFee2(0.00d);//承运商上楼费,无,2018.06前旧数据可能有
                exportVO.setCarrierExceedSquareFee(feeVO.getAbnExceedSquareFee());//承运商超方费
                exportVO.setCarrierExceedAreaFee(feeVO.getAbnExceedAreaFee());//承运商超区费
                exportVO.setCarrierDismountingFee(feeVO.getAbnDismountingFee());//承运商拆装费
                exportVO.setCarrierHoistingBuildFee(feeVO.getAbnHoistingBuildFee());//承运商吊楼费
                exportVO.setCarrierTranslationalFee(feeVO.getAbnTranslationalFee());//承运商平移费
                exportVO.setCarrierEmptyRunFee(feeVO.getAbnEmptyRunFee());//承运商空跑费
                exportVO.setCarrierSpecialAreaFee(feeVO.getAbnSpecialAreaFee());//承运商特殊区域费
                exportVO.setCarrierStorageFee(feeVO.getAbnStorageFee());//承运商仓储费
                exportVO.setCarrierUrgentFee(feeVO.getAbnUrgentFee());//承运商加急费
                exportVO.setCarrierSecondDoorFee(feeVO.getAbnSecondDoorFee());//承运商二次上门费
                exportVO.setCarrierMinUpstairsFee(feeVO.getAbnMinUpstairsFee());//承运商＞7楼搬楼费
                exportVO.setCarrierMaxUpstairsFee(feeVO.getAbnMaxUpstairsFee());//承运商≤7楼搬楼费
                exportVO.setCarrierPickUpGoodsFee(feeVO.getAbnPickUpGoodsFee());//承运商提货费
                exportVO.setCarrierInsuranceFee(feeVO.getAbnInsuranceFee());//承运商保价费
                exportVO.setCarrierServiceDebit(feeVO.getAbnServiceDebit());//承运商的服务商扣款
                exportVO.setCarrierReservationRate(feeVO.getAbnReservationRate());//承运商预约超时费
                exportVO.setCarrierInstallationRate(feeVO.getAbnInstallationRate());//承运商安装超时费
                exportVO.setCarrierInstallFee(feeVO.getAbnInstallFee());//承运商异常安装费
                exportVO.setCarrierUpstairsFee(feeVO.getAbnUpstairsFee());//承运商异常上楼费
                exportVO.setCarrierDropGoodFee(feeVO.getAbnDropGoodFee());//承运商弃货费
                exportVO.setCarrierRepairFee(feeVO.getRepairFee());//承运商维修费
                exportVO.setCarrierAbnRepairFee(feeVO.getAbnRepairFee());//承运商维修费
                exportVO.setCarrierBackFee(feeVO.getBackFee());//承运商返货费
                exportVO.setCarrierAbnBackFee(feeVO.getAbnBackFee());//承运商返货费
                exportVO.setCarrierPartFee(feeVO.getAbnPartFee());//承运商补件费
                exportVO.setCarrierAbnRecoveryFee(feeVO.getAbnRecoveryFee());//承运商异常追偿责任费
                exportVO.setCarrierAbnCollectionServiceFee(feeVO.getAbnCollectionServiceFee());//承运商异常代收货款手续费
                exportVO.setCarrierAbnWoodenFee(feeVO.getAbnWoodenFee());//承运商异常打木架费
                exportVO.setCarrierAbnOtherFee(feeVO.getAbnOtherFee());//承运商异常其他费
                exportVO.setTotalCarrierAbnFee(feeVO.getTotal());//承运商异常合计
            }
            List<FeeVO> feeVOS2 = abnFeeMapForMatserPay.get(waybillId);
            if (feeVOS2 != null) {
                FeeVO feeVO = feeVOS2.get(0);
                exportVO.setMasterDeliveryFee(feeVO.getAbnDeliveryFee());//服务商送货费
                exportVO.setMasterAdvanceFee(feeVO.getAbnAdvanceFee());//服务商垫付费
                exportVO.setMasterExceedSquareFee(feeVO.getAbnExceedSquareFee());//服务商超方费
                exportVO.setMasterExceedAreaFee(feeVO.getAbnExceedAreaFee());//服务商超区费
                exportVO.setMasterDismountingFee(feeVO.getAbnDismountingFee());//服务商拆装费
                exportVO.setMasterHoistingBuildFee(feeVO.getAbnHoistingBuildFee());//服务商吊楼费
                exportVO.setMasterTranslationalFee(feeVO.getAbnTranslationalFee());//服务商平移费
                exportVO.setMasterEmptyRunFee(feeVO.getAbnEmptyRunFee());//服务商空跑费
                exportVO.setMasterSpecialAreaFee(feeVO.getAbnSpecialAreaFee());//服务商特殊区域费
                exportVO.setMasterStorageFee(feeVO.getAbnStorageFee());//服务商仓储费
                exportVO.setMasterUrgentFee(feeVO.getAbnUrgentFee());//服务商加急费
                exportVO.setMasterSecondDoorFee(feeVO.getAbnSecondDoorFee());//服务商二次上门费
                exportVO.setMasterMinUpstairsFee(feeVO.getAbnMinUpstairsFee());//服务商＞7楼搬楼费
                exportVO.setMasterMaxUpstairsFee(feeVO.getAbnMaxUpstairsFee());//服务商≤7楼搬楼费
                exportVO.setMasterPickUpGoodsFee(feeVO.getAbnPickUpGoodsFee());//服务商提货费
                exportVO.setMasterInsuranceFee(feeVO.getAbnInsuranceFee());//服务商保价费
                exportVO.setMasterServiceDebit(feeVO.getAbnServiceDebit());//服务商的服务商扣款
                exportVO.setMasterReservationRate(feeVO.getAbnReservationRate());//服务商预约超时费
                exportVO.setMasterInstallationRate(feeVO.getAbnInstallationRate());//服务商安装超时费
                exportVO.setMasterInstallFee(feeVO.getAbnInstallFee());//服务商异常责任安装费
                exportVO.setMasterUpstairsFee(feeVO.getAbnUpstairsFee());//服务商异常责任上楼费
                exportVO.setMasterDropGoodFee(feeVO.getAbnDropGoodFee());//服务商弃货费
                exportVO.setMasterRepairFee(feeVO.getRepairFee());//服务商维修费
                exportVO.setMasterAbnRepairFee(feeVO.getAbnRepairFee());//服务商维修费
                exportVO.setMasterBackFee(feeVO.getBackFee());//服务商返货费
                exportVO.setMasterAbnBackFee(feeVO.getAbnBackFee());//服务商返货费
                exportVO.setMasterPartFee(feeVO.getAbnPartFee());//服务商补件费
                exportVO.setMasterAbnRecoveryFee(feeVO.getAbnRecoveryFee());//服务商异常追偿责任费
                exportVO.setMasterAbnCollectionServiceFee(feeVO.getAbnCollectionServiceFee());//服务商异常代收货款手续费
                exportVO.setMasterWoodenFee(feeVO.getAbnWoodenFee());//服务商异常打木架费
                exportVO.setMasterOtherFee(feeVO.getAbnOtherFee());//服务商异常其他费
                exportVO.setTotalMasterAbnFee(feeVO.getTotal());//服务商异常合计
            }
            List<FeeVO> feeVOS3 = abnFeeMapForMatserCollect.get(waybillId);
            if (feeVOS3 != null) {
                FeeVO feeVO = feeVOS3.get(0);
                exportVO.setMasterAbnDeliveryFee(feeVO.getAbnDeliveryFee());//服务商异常送货费
                exportVO.setMasterAbnAdvanceFee(feeVO.getAbnAdvanceFee());//服务商异常垫付费
                exportVO.setMasterAbnExceedSquareFee(feeVO.getAbnExceedSquareFee());//服务商异常超方费
                exportVO.setMasterAbnExceedAreaFee(feeVO.getAbnExceedAreaFee());//服务商异常超区费
                exportVO.setMasterAbnDismountingFee(feeVO.getAbnDismountingFee());//服务商异常拆装费
                exportVO.setMasterAbnHoistingBuildFee(feeVO.getAbnHoistingBuildFee());//服务商异常吊楼费
                exportVO.setMasterAbnTranslationalFee(feeVO.getAbnTranslationalFee());//服务商异常平移费
                exportVO.setMasterAbnEmptyRunFee(feeVO.getAbnEmptyRunFee());//服务商异常空跑费
                exportVO.setMasterAbnSpecialAreaFee(feeVO.getAbnSpecialAreaFee());//服务商异常特殊区域费
                exportVO.setMasterAbnStorageFee(feeVO.getAbnStorageFee());//服务商异常仓储费
                exportVO.setMasterAbnUrgentFee(feeVO.getAbnUrgentFee());//服务商异常加急费
                exportVO.setMasterAbnSecondDoorFee(feeVO.getAbnSecondDoorFee());//服务商异常二次上门费
                exportVO.setMasterAbnMinUpstairsFee(feeVO.getAbnMinUpstairsFee());//服务商异常＞7楼搬楼费
                exportVO.setMasterAbnMaxUpstairsFee(feeVO.getAbnMaxUpstairsFee());//服务商异常≤7楼搬楼费
                exportVO.setMasterAbnPickUpGoodsFee(feeVO.getAbnPickUpGoodsFee());//服务商异常提货费
                exportVO.setMasterAbnInsuranceFee(feeVO.getAbnInsuranceFee());//服务商异常保价费
                exportVO.setMasterAbnServiceDebit(feeVO.getAbnServiceDebit());//服务商异常的服务商异常扣款
                exportVO.setMasterAbnReservationRate(feeVO.getAbnReservationRate());//服务商异常预约超时费
                exportVO.setMasterAbnInstallationRate(feeVO.getAbnInstallationRate());//服务商异常安装超时费
                exportVO.setMasterAbnInstallFee(feeVO.getAbnInstallFee());//服务商异常安装费
                exportVO.setMasterAbnUpstairsFee(feeVO.getAbnUpstairsFee());//服务商异常上楼费
                exportVO.setMasterAbnDropGoodFee(feeVO.getAbnDropGoodFee());//服务商异常弃货费
                exportVO.setMasterRepairFee2(feeVO.getRepairFee());//服务商异常维修费
                exportVO.setMasterAbnRepairFee2(feeVO.getAbnRepairFee());//服务商异常维修费
                exportVO.setMasterBackFee2(feeVO.getBackFee());//服务商异常返货费
                exportVO.setMasterAbnBackFee2(feeVO.getAbnBackFee());//服务商异常返货费
                exportVO.setMasterAbnPartFee(feeVO.getAbnPartFee());//服务商异常补件费
                exportVO.setMasterAbnCollectionServiceFee2(feeVO.getAbnCollectionServiceFee());//服务商异常代收货款手续成本
                exportVO.setMasterAbnWoodenFee(feeVO.getAbnWoodenFee());//服务商异常打木架成本
                exportVO.setMasterAbnOtherFee(feeVO.getAbnOtherFee());//服务器异常其他费成本
                exportVO.setTotalMasterAbnCompensationFee(feeVO.getTotal());//服务商异常补偿合计
            }

            List<FeeVO> feeVOS4 = feeMap.get(waybillId);
            if (feeVOS4 != null) {
                FeeVO feeVO = feeVOS4.get(0);
                exportVO.setAbnPraiseFee(feeVO.getAbnPraiseFee());//师傅好评
                exportVO.setAbnPartialRefundFee(feeVO.getAbnPartialRefundFee());//部分退款
            }

            /* 异常费毛利 */
            double abnGrossProfit = exportVO.getTotalConsignorAbnFee() + exportVO.getTotalCarrierAbnFee() + exportVO.getTotalMasterAbnFee()
                    - (exportVO.getTotalMasterAbnCompensationFee() + exportVO.getAbnPraiseFee() + exportVO.getAbnPartialRefundFee());
            abnGrossProfit = new BigDecimal(abnGrossProfit).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            exportVO.setAbnGrossProfit(abnGrossProfit);
            list.add(exportVO);
        }
        long end = System.currentTimeMillis();
        log.error("拼接异常利润数据耗时：{}", (end - start));
        return list;
    }

    /**
     * 业绩统计报表
     *
     * @param conditionVO
     * @return
     */
    @Override
    public Workbook exportReportForPerformanceStatistics(ReportConditionVO conditionVO) throws Exception {
        log.info("业绩统计报表导出开始");
        long l1 = System.currentTimeMillis();
        WriteService writeService = ExportExcelUtils.getWriteService(new PerformanceStatisticsHeader(), null);
        Integer count = 0;
        while (true) {
            List<PerformanceStatisticsExportVO> data = findPerformanceStatistics(null, conditionVO);
            count += data.size();
            List<Map<String, Object>> list = data.parallelStream().map(x -> ((JSONObject) JSON.toJSON(x))).collect(Collectors.toList());
            if (list != null && list.size() > 0) {
                writeService.createContents(list);
                conditionVO.setOffset(conditionVO.getOffset() + conditionVO.getSize());
            } else {
                break;
            }
        }
        log.info("业绩统计报表导出结束. 导出数量: {}, 总耗时: {}", count, System.currentTimeMillis() - l1);
        return writeService.build();
    }

    @Override
    public List<PerformanceStatisticsExportVO> findPerformanceStatistics(GridRequest gridRequest, ReportConditionVO conditionVO) throws Exception {
        if (gridRequest != null) {
            conditionVO.setOffset((gridRequest.getPage() - 1) * gridRequest.getSize());
            conditionVO.setSize(gridRequest.getSize());
        }
        long l1 = System.currentTimeMillis();
        List<ReportExportVO> dataList = reportMapper.selectWayBillInfoForReport(conditionVO);
        log.info("查询基本信息耗时: {}", System.currentTimeMillis() - l1);
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        List<String> waybillIdList = dataList.stream().map(ReportExportVO::getWaybillId).collect(Collectors.toList());

        long l2 = System.currentTimeMillis();
        // 费用相关
        List<FeeInfo> feeInfoList = feeInfoExtMapper.selectByWaybillIds(waybillIdList);
        Map<String, List<FeeInfo>> feeMap = feeInfoList.stream().collect(Collectors.groupingBy(FeeInfo::getWaybillId));
        // 准备商品数据
        List<GoodsInfo> goodsInfoList = goodsInfoExtMapper.selectByWaybillIds(waybillIdList);
        Map<String, List<GoodsInfo>> goodsMap = goodsInfoList.stream().collect(Collectors.groupingBy(GoodsInfo::getWaybillId));
        // 准备时间节点相关数据
        List<Integer> signCodes = Arrays.asList(Operation.HEADQUARTERS_SIGN.getCode(), Operation.DIRECT_OR_OUTLET_SIGN.getCode(), Operation.MASTER_SIGN.getCode());
        List<Integer> cancelSignCodes = Arrays.asList(Operation.HEADQUARTERS_SIGN_CANCEL.getCode(), Operation.DIRECT_OR_OUTLET_SIGN_CANCEL.getCode(), Operation.MASTER_SIGN_CANCEL.getCode());
        // 只获取签收和取消签收的节点
        List<Integer> codes = Lists.newArrayList();
        codes.addAll(signCodes);
        codes.addAll(cancelSignCodes);
        List<MilestoneInfo> milestoneInfoList = milestoneInfoExtMapper.selectByWaybillIdsAndOperations(waybillIdList, codes);
        Map<String, List<MilestoneInfo>> milestoneInfoMap = milestoneInfoList.stream().collect(Collectors.groupingBy(MilestoneInfo::getWaybillId));
        // 节点关联的节点详情(只取签收人、签收时间等信息)
        List<String> milestoneIds = milestoneInfoList.stream().filter(x -> signCodes.contains(x.getOperation())).map(MilestoneInfo::getId).collect(Collectors.toList());
        Map<String, List<OperationDetail>> operationDetailMap = milestoneIds.isEmpty() ? Collections.emptyMap() :
                operationDetailExtMapper.selectByMilestoneIds(milestoneIds).stream().collect(Collectors.groupingBy(OperationDetail::getMilestoneId));
        // 运单关联数据
        List<BillingInfo> billingInfoList = billingInfoExtMapper.select4WaybillAging(waybillIdList);
        Map<String, Double> billingInfoMap = billingInfoList.stream().collect(Collectors.toMap(BillingInfo::getWaybillId, BillingInfo::getTotalPrice, Double::sum));
        log.info("准备数据耗时: {}", System.currentTimeMillis() - l2);

        long l3 = System.currentTimeMillis();
        List<PerformanceStatisticsExportVO> list = new ArrayList<>(dataList.size());
        for (ReportExportVO data : dataList) {
            PerformanceStatisticsExportVO exportVO = new PerformanceStatisticsExportVO();
            Beans.from(data).to(exportVO);

            exportVO.setOpenBillTime(DateUtil.fromDateToH(data.getOpenBillTime()));// 开单时间
            PaymentType paymentType = PaymentType.getEnumByCode(data.getPaymentType());
            if (paymentType != null) {
                exportVO.setPaymentType(paymentType.getMessage());// 付款方式
            }
            ServiceType serviceType = ServiceType.getEnumByCode(data.getServiceType());
            if (serviceType != null) {
                exportVO.setServiceType(serviceType.getMessage());// 服务类型
            }
            WaybillStatus waybillStatus = WaybillStatus.getEnumByCode(data.getWaybillStatus());
            if (waybillStatus != null) {
                exportVO.setWaybillStatus(waybillStatus.getMessage());// 运单状态
            }
            CustomerType customerType = CustomerType.getEnumByCode(data.getMerchantType());
            if (customerType != null) {
                exportVO.setMerchantType(customerType.getMessage());// 商家类型
            }
            ChannelSource channelSource = ChannelSource.getEnumByCode(data.getChannelSource());
            if (channelSource != null) {
                exportVO.setChannelSource(channelSource.getMessage());// 运单来源
            }
            ProductType productType = ProductType.getEnumByCode(data.getProductType());
            if (productType != null) {
                exportVO.setProductType(productType.getMessage());// 产品类型
            }
            /*------------------------------------货物信息------------------------------------*/
            List<GoodsInfo> goodsInfos = goodsMap.get(data.getWaybillId());
            if (goodsInfos != null) {
                StringBuilder goodsNameBuilder = new StringBuilder(), packingBuilder = new StringBuilder();
                int packingTotalCount = 0;
                double totalVolume = 0d, totalWeight = 0d;
                for (GoodsInfo goodsInfo : goodsInfos) {
                    packingTotalCount += goodsInfo.getPackingItems() == null ? 0 : goodsInfo.getPackingItems();
                    totalVolume += goodsInfo.getVolume() == null ? 0d : goodsInfo.getVolume();
                    totalWeight += goodsInfo.getWeight() == null ? 0d : goodsInfo.getWeight();
                    goodsNameBuilder.append(goodsInfo.getGoodsName())
                            .append("_").append(goodsInfo.getInstallItems() == null?"0":goodsInfo.getInstallItems())
                            //.append("_").append(goodsInfo.getPackingItems() == null?"0":goodsInfo.getPackingItems())
                            .append(";");
                    // 格式：商品名*包装类型*包装件数
                    //packingBuilder
                    //        .append(goodsInfo.getGoodsName()).append("*")
                    //        .append(goodsInfo.getPacking() == null ? "" : Packing.getEnumByCode(goodsInfo.getPacking()).getMessage()).append("*")
                    //        // 格式化保留1位小数点
                    //        .append(goodsInfo.getPackingItems() == null ? 0d : goodsInfo.getPackingItems().doubleValue())
                    //        .append(";");
                    /*if(goodsInfo.getPacking() != null){
                        exportVO.setGoodsPacking(Packing.getEnumByCode(goodsInfo.getPacking()).getMessage());// 包装
                    }*/
                }
                exportVO.setGoodsType(data.getGoodsType()==null?"":data.getGoodsType());// 货物类型
                exportVO.setGoodsName(goodsNameBuilder.substring(0, goodsNameBuilder.length() - 1));// 品名
                //exportVO.setGoodsPacking(packingBuilder.substring(0, packingBuilder.length() - 1));// 包装
                exportVO.setPackingTotalVolume(totalVolume);// 总体积
                exportVO.setPackingTotalWeight(totalWeight);// 总重量
                exportVO.setPackingTotalCount(packingTotalCount);// 包装总件数
                exportVO.setGoodsPacking(data.getPacking() == null?"":data.getPacking());// 包装
            }
            List<FeeInfo> feeInfos = feeMap.get(data.getWaybillId());
            if (CollectionUtils.isNotEmpty(feeInfos)) {
                Map<String, Double> feeTypeMap = Collections.EMPTY_MAP;
                feeTypeMap = feeInfos.stream().collect(Collectors.toMap(FeeInfo::getFeeType, FeeInfo::getFee, Double::sum));
                exportVO.setTransportFee(getFeeByType(feeTypeMap, "transportFee"));// 运费
                exportVO.setDeliveryFee(getFeeByType(feeTypeMap, "deliveryFee"));// 开单送货费
                exportVO.setInsuranceFee(getFeeByType(feeTypeMap, "insuranceFee"));// 保价费
                exportVO.setInstallFee(getFeeByType(feeTypeMap, "installFee"));// 安装费
                exportVO.setUpstairsFee(getFeeByType(feeTypeMap, "upstairsFee"));// 上楼费
                exportVO.setOtherFee(getFeeByType(feeTypeMap, "otherFee"));// 其他费
//                exportVO.setTakeGoodsFee(fee(feeTypeMap, "takeGoodsFee"));// 揽货费
//                exportVO.setAgingServiceFee(fee(feeTypeMap, "agingServiceFee"));// 时效服务费
//                exportVO.setDoorFee(fee(feeTypeMap, "doorFee"));// 上门服务费
//                exportVO.setEntryHomeFee(fee(feeTypeMap, "entryHomeFee"));// 入户费
//                exportVO.setMarbleFee(fee(feeTypeMap, "marbleFee"));// 大理石费
//                exportVO.setPromotionFee(fee(feeTypeMap, "promotionFee"));// 干支促销费
//                exportVO.setExceedAreaFee(fee(feeTypeMap, "exceedAreaFee"));// 超区费
//                exportVO.setExceedSquareFee(fee(feeTypeMap, "exceedSquareFee"));// 超方费
//                exportVO.setCouponRelief(fee(feeTypeMap, "couponRelief"));// 优惠券减免费用
//                exportVO.setGiftRelief(fee(feeTypeMap, "giftRelief"));// 赠送金减免费用
//                exportVO.setCashRelief(fee(feeTypeMap, "cashRelief"));// 返现金减免费用
                // 开单合计=以上费用总和
//                Double totalFee = exportVO.getTransportFee() + exportVO.getDeliveryFee() + exportVO.getInsuranceFee()
//                        + exportVO.getInstallFee() + exportVO.getUpstairsFee() + exportVO.getOtherFee()
//                        + getFeeByType(feeTypeMap, "takeGoodsFee") + getFeeByType(feeTypeMap, "agingServiceFee")
//                        + getFeeByType(feeTypeMap, "doorFee") + getFeeByType(feeTypeMap, "entryHomeFee")
//                        + getFeeByType(feeTypeMap, "marbleFee") + getFeeByType(feeTypeMap, "promotionFee")
//                        + getFeeByType(feeTypeMap, "exceedAreaFee") + getFeeByType(feeTypeMap, "exceedSquareFee")
//                        + getFeeByType(feeTypeMap, "couponRelief") + getFeeByType(feeTypeMap, "giftRelief")
//                        + getFeeByType(feeTypeMap, "cashRelief");
                Double totalFee =billingInfoMap.get(data.getWaybillId());
                exportVO.setTotalFee(totalFee);
            }

            // 签收时间及签收人
            List<MilestoneInfo> milestoneInfos = milestoneInfoMap.getOrDefault(data.getWaybillId(), Collections.emptyList());
            Map<Operation, List<MilestoneInfo>> operationMap = milestoneInfos.stream().collect(Collectors.groupingBy(x -> Operation.getEnumByCode(x.getOperation())));
            MilestoneInfo lastMilestone = getLastMilestone(operationMap, Arrays.asList(Operation.HEADQUARTERS_SIGN, Operation.DIRECT_OR_OUTLET_SIGN, Operation.MASTER_SIGN),
                    Arrays.asList(Operation.HEADQUARTERS_SIGN_CANCEL, Operation.DIRECT_OR_OUTLET_SIGN_CANCEL, Operation.MASTER_SIGN_CANCEL));
            if (lastMilestone != null) {
                List<OperationDetail> operationDetails = operationDetailMap.getOrDefault(lastMilestone.getId(), Collections.emptyList());
                String signName = operationDetails.stream()
                        .filter(x -> Objects.equals(x.getField(), "signName"))
                        .map(OperationDetail::getVal).findFirst().orElse(null);
                exportVO.setSignName(signName);
                exportVO.setSignTime(DateUtil.fromDateToH(lastMilestone.getOperateTime()));

            }

            list.add(exportVO);
        }
        log.info("导出数据耗时: {}", System.currentTimeMillis() - l3);
        return list;
    }

    private Double getFeeByType(Map<String, Double> feeTypeMap, String feeType) throws Exception {
        Double fee = feeTypeMap.get(feeType);
        return fee == null ? 0.00d : (double) Math.round(fee * 100) / 100;
    }

    /**
     * 核销报表
     *
     * @param conditionVO
     * @return
     */
    @Override
    public Workbook exportReportForCheck(ReportConditionVO conditionVO) throws Exception {
        log.info("核销报表导出开始");
        long l1 = System.currentTimeMillis();
        WriteService writeService = ExportExcelUtils.getWriteService(new CheckHeader(), null);
        Integer count = 0;
        while (true) {
            List<CheckExportVO> data = findCheck(null, conditionVO);
            count += data.size();
            List<Map<String, Object>> list = data.parallelStream().map(x -> ((JSONObject) JSON.toJSON(x))).collect(Collectors.toList());
            if (list != null && list.size() > 0) {
                writeService.createContents(list);
                conditionVO.setOffset(conditionVO.getOffset() + conditionVO.getSize());
            } else {
                break;
            }
        }
        log.info("核销报表报表导出结束. 导出数量: {}, 总耗时: {}", count, System.currentTimeMillis() - l1);
        return writeService.build();
    }

    @Override
    public List<CheckExportVO> findCheck(GridRequest gridRequest, ReportConditionVO conditionVO) throws Exception {
        if (gridRequest != null) {
            conditionVO.setOffset((gridRequest.getPage() - 1) * gridRequest.getSize());
            conditionVO.setSize(gridRequest.getSize());
        }
        long l1 = System.currentTimeMillis();
        List<ReportExportVO> dataList = reportMapper.selectWayBillInfoForReport(conditionVO);
        log.info("查询基本信息耗时: {}", System.currentTimeMillis() - l1);
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        long l2 = System.currentTimeMillis();
        List<String> waybillIdList = dataList.stream().map(ReportExportVO::getWaybillId).collect(Collectors.toList());
        // 准备时间节点相关数据
        List<MilestoneInfo> allMilestoneInfoList = milestoneInfoExtMapper.selectByWaybillIdsAndOperations(waybillIdList, null);
        Map<String, List<MilestoneInfo>> milestoneInfoMap = allMilestoneInfoList.stream().collect(Collectors.groupingBy(MilestoneInfo::getWaybillId));
        // 运单关联数据
        List<BillRelationInfo> relationInfoList = billRelationInfoExtMapper.selectByWaybillIds(waybillIdList);
        Map<String, List<BillRelationInfo>> billRelationInfoMap = relationInfoList.stream().collect(Collectors.groupingBy(BillRelationInfo::getWaybillId));
        // 准备商品数据
        List<GoodsInfo> goodsInfoList = goodsInfoExtMapper.selectByWaybillIds(waybillIdList);
        Map<String, List<GoodsInfo>> goodsMap = goodsInfoList.stream().collect(Collectors.groupingBy(GoodsInfo::getWaybillId));
        log.info("准备数据耗时: {}", System.currentTimeMillis() - l2);

        long l3 = System.currentTimeMillis();
        List<CheckExportVO> list = new ArrayList<>(dataList.size());
        for (ReportExportVO data : dataList) {
            CheckExportVO exportVO = new CheckExportVO();
            Beans.from(data).to(exportVO);

            exportVO.setOpenBillTime(DateUtil.fromDateToH(data.getOpenBillTime()));// 开单时间
            ServiceType serviceType = ServiceType.getEnumByCode(data.getServiceType());
            if (serviceType != null) {
                exportVO.setServiceType(serviceType.getMessage());// 服务类型
            }
            WaybillStatus waybillStatus = WaybillStatus.getEnumByCode(data.getWaybillStatus());
            if (waybillStatus != null) {
                exportVO.setWaybillStatus(waybillStatus.getMessage());// 运单状态
            }
            ChannelSource channelSource = ChannelSource.getEnumByCode(data.getChannelSource());
            if (channelSource != null) {
                exportVO.setChannelSource(channelSource.getMessage());// 运单来源
            }
            CheckType checkType = CheckType.getEnumByCode(data.getCheckType());
            if (checkType != null) {
                exportVO.setCheckType(checkType.getMessage());// 核销类型
            }
            CheckMethod checkMethod = CheckMethod.getEnumByCode(data.getCheckMethod());
            if (checkMethod != null) {
                exportVO.setCheckMethod(checkMethod.getMessage());// 核销方式
            }
            CheckStatus checkStatus = CheckStatus.getEnumByCode(data.getCheckStatus());
            if (checkStatus != null) {
                exportVO.setCheckStatus(checkMethod == CheckMethod.CHECK_NO ? StringUtils.EMPTY : checkStatus.getMessage());// 是否已核销
            }

            /*------------------------------------运单关联信息------------------------------------*/
            List<BillRelationInfo> billRelationInfo = billRelationInfoMap.get(data.getWaybillId());
            if (billRelationInfo != null) {
                billRelationInfo.sort(Comparator.comparing(BillRelationInfo::getCreateTime));
                StringBuilder orderBillIdBuilder = new StringBuilder(), customerBillIdBuilder = new StringBuilder();
                for (BillRelationInfo relationInfo : billRelationInfo) {
                    if (relationInfo.getOrderBillId() != null) {
                        orderBillIdBuilder.append(relationInfo.getOrderBillId()).append(",");
                    }
                    if (relationInfo.getCustomerBillId() != null) {
                        customerBillIdBuilder.append(relationInfo.getCustomerBillId()).append(",");
                    }
                }
                exportVO.setOrderBillId(orderBillIdBuilder.substring(0, orderBillIdBuilder.length() == 0 ? 0 : orderBillIdBuilder.length() - 1));// 订单号
                exportVO.setCustomerBillId(customerBillIdBuilder.substring(0, customerBillIdBuilder.length() == 0 ? 0 : customerBillIdBuilder.length() - 1));// 客户单号
            }

            /*------------------------------------货物信息------------------------------------*/
            List<GoodsInfo> goodsInfos = goodsMap.get(data.getWaybillId());
            if (goodsInfos != null) {
                StringBuilder goodsNameBuilder = new StringBuilder(), packingBuilder = new StringBuilder();
                int packingTotalCount = 0;
                double totalVolume = 0d, totalWeight = 0d;
                for (GoodsInfo goodsInfo : goodsInfos) {
                    packingTotalCount += goodsInfo.getPackingItems() == null ? 0 : goodsInfo.getPackingItems();
                    totalVolume += goodsInfo.getVolume() == null ? 0d : goodsInfo.getVolume();
                    totalWeight += goodsInfo.getWeight() == null ? 0d : goodsInfo.getWeight();
                    goodsNameBuilder.append(goodsInfo.getGoodsName())
                            .append("_").append(goodsInfo.getInstallItems() == null?"0":goodsInfo.getInstallItems())
                            //.append("_").append(goodsInfo.getPackingItems() == null?"0":goodsInfo.getPackingItems())
                            .append(";");
                    // 格式：商品名*包装类型*包装件数
                    //packingBuilder
                    //        .append(goodsInfo.getGoodsName()).append("*")
                    //        .append(goodsInfo.getPacking() == null ? "" : Packing.getEnumByCode(goodsInfo.getPacking()).getMessage()).append("*")
                    //        // 格式化保留1位小数点
                    //        .append(goodsInfo.getPackingItems() == null ? 0d : goodsInfo.getPackingItems().doubleValue())
                    //        .append(";");
                    /*if(goodsInfo.getPacking() != null){
                        exportVO.setGoodsPacking(Packing.getEnumByCode(goodsInfo.getPacking()).getMessage());// 包装
                    }*/
                }
                exportVO.setGoodsName(goodsNameBuilder.substring(0, goodsNameBuilder.length() - 1));// 品名
                //exportVO.setGoodsPacking(packingBuilder.substring(0, packingBuilder.length() - 1));// 包装
                exportVO.setPackingTotalVolume(totalVolume);// 总体积
                exportVO.setPackingTotalWeight(totalWeight);// 总重量
                exportVO.setPackingTotalCount(packingTotalCount);// 包装总件数
                exportVO.setGoodsPacking(data.getPacking() == null?"":data.getPacking());// 包装
            }
            /*------------------------------------节点时间相关------------------------------------*/
            List<MilestoneInfo> milestoneInfos = milestoneInfoMap.getOrDefault(data.getWaybillId(), Collections.emptyList());
            // 移除失效节点
            milestoneInfos = removeInvalidMilestone(milestoneInfos);
            // 按照操作节点分组
            Map<Operation, List<MilestoneInfo>> operationMap = milestoneInfos.stream().collect(Collectors.groupingBy(x -> Operation.getEnumByCode(x.getOperation())));
            exportVO.setTrunkStartTime(getNodeTimeByOperationType(operationMap, Operation.OUT_DELIVER_COMFIRM));// 干线开始时间(承运商发车时间)

            //服务类型为专线送货和自提时，下面几列为空：服中心派单时间、客服中心派单师傅、首次预约操作时间、首次预约上门服务时间、预约次数、最新一次预约操作时间、最新一次预约上门服务时间、安装师傅账号、安装师傅、末端网点
            if (!(serviceType.equals(ServiceType.PICKUP) || serviceType.equals(ServiceType.DEDICATED_DELIVERY))) {
                // 取总部、网点、师傅操作的集合
                // 分配
                MilestoneInfo lastDistributeMilestone = getLastMilestone(operationMap, Arrays.asList(Operation.HEADQUARTERS_DISTRIBUTION, Operation.DIRECT_OR_OUTLET_DISTRIBUTION),
                        Arrays.asList(Operation.HEADQUARTERS_DISTRIBUTION_CANCEL, Operation.DIRECT_OR_OUTLET_DISTRIBUTION_CANCEL));
                exportVO.setDistributionTime(DateUtil.fromDateToH(lastDistributeMilestone.getOperateTime()));// 分配时间
                exportVO.setDistributionOperator(lastDistributeMilestone.getOperator());// 分配人
                // 最后取消分配的时间
                MilestoneInfo lastCancelDisMilestone = getLastMilestone(operationMap,
                        Arrays.asList(Operation.HEADQUARTERS_DISTRIBUTION_CANCEL, Operation.DIRECT_OR_OUTLET_DISTRIBUTION_CANCEL), null);
                if (lastDistributeMilestone != EMPTY_MILESTONE) {
                    // 最后预约时间
                    MilestoneInfo lastAppointMilestone = getLastMilestone(operationMap, Arrays.asList(Operation.HEADQUARTERS_APPOINTMENT,
                            Operation.DIRECT_OR_OUTLET_APPOINTMENT, Operation.MASTER_APPOINTMENT), null);
                    // 取消分配后，之前所有预约记录无效，这里需要过滤取消分配前的预约节点
                    if (lastCancelDisMilestone == EMPTY_MILESTONE ||
                            (lastAppointMilestone != EMPTY_MILESTONE && lastAppointMilestone.getOperateTime().after(lastCancelDisMilestone.getOperateTime()))) {
                        exportVO.setAppointmentTime(DateUtil.fromDateToH(lastAppointMilestone.getOperateTime()));// 预约时间
                    }
                    // 最后提货时间
                    MilestoneInfo lastPickupMilestone = getLastMilestone(operationMap, Arrays.asList(Operation.HEADQUARTERS_PICK,
                            Operation.DIRECT_OR_OUTLET_PICK, Operation.MASTER_PICK), null);
                    // 取消分配后，之前所有提货记录无效，这里需要过滤取消分配前的提货节点
                    if (lastCancelDisMilestone == EMPTY_MILESTONE ||
                            (lastPickupMilestone != EMPTY_MILESTONE && lastPickupMilestone.getOperateTime().after(lastCancelDisMilestone.getOperateTime()))) {
                        exportVO.setPickUpTime(DateUtil.fromDateToH(lastPickupMilestone.getOperateTime()));// 提货时间
                    }
                }
            }else{
                exportVO.setMasterNode(null);
                exportVO.setMasterName(null);
            }
            // 签收
            MilestoneInfo lastSignMilestone = getLastMilestone(operationMap, Arrays.asList(Operation.HEADQUARTERS_SIGN,
                    Operation.DIRECT_OR_OUTLET_SIGN, Operation.MASTER_SIGN),
                    Arrays.asList(Operation.DIRECT_OR_OUTLET_SIGN_CANCEL, Operation.HEADQUARTERS_SIGN_CANCEL, Operation.MASTER_SIGN_CANCEL));
            exportVO.setSignTime(DateUtil.fromDateToH(lastSignMilestone.getOperateTime()));// 签收时间
            // 核销
            exportVO.setCheckWaybillTime(data.getCheckTime());// 核销时间

            list.add(exportVO);
        }
        log.info("导出数据耗时: {}", System.currentTimeMillis() - l3);
        return list;
    }

    /**
     * 根据节点类型获取最新操作节点(考虑存在取消操作的情况)
     *
     * @param operationMap     节点数据
     * @param operations       正向操作类型
     * @param cancelOperations 反向操作节点类型(仅取消分配、取消签收时传值)
     * @return 最新节点
     */
    private MilestoneInfo getLastMilestone(Map<Operation, List<MilestoneInfo>> operationMap, List<Operation> operations, List<Operation> cancelOperations) throws Exception {
        List<MilestoneInfo> milestones = new ArrayList<>(), cancelMilestones = null;
        for (Operation operation : operations) {
            List<MilestoneInfo> milestoneInfos = operationMap.get(operation);
            if (CollectionUtils.isNotEmpty(milestoneInfos)) {
                milestones.addAll(milestoneInfos);
            }
        }
        if (cancelOperations != null) {
            cancelMilestones = new ArrayList<>();
            for (Operation cancelOperation : cancelOperations) {
                List<MilestoneInfo> cancelMilestoneInfos = operationMap.get(cancelOperation);
                if (CollectionUtils.isNotEmpty(cancelMilestoneInfos)) {
                    cancelMilestones.addAll(cancelMilestoneInfos);
                }
            }
        }
        Comparator<MilestoneInfo> comparator = Comparator.comparing(MilestoneInfo::getOperateTime).reversed();
        // 正向节点(分配、签收)与反向节点(取消分配、取消签收)均取最新时间节点进行比较，如果正向节点是最新，则取正向操作节点数据，反之返回空(即最新操作为取消分配或取消签收)
        // 最新正向节点
        MilestoneInfo lastMilestone = milestones.stream().sorted(comparator).findFirst().orElse(null);
        // 最新反向节点(取消节点)
        MilestoneInfo lastCancelMilestone = null;
        if (cancelOperations != null) {
            lastCancelMilestone = cancelMilestones.stream().sorted(comparator).findFirst().orElse(null);
        }
        if (lastMilestone != null &&
                (lastCancelMilestone == null || lastMilestone.getOperateTime().after(lastCancelMilestone.getOperateTime()))) {
            return lastMilestone;
        }
        // 不返回null,避免NPE
        return EMPTY_MILESTONE;
    }

    /**
     * 根据节点类型获取最新操作时间(考虑存在取消操作的情况)
     */
    private String getLastMilestoneTime(Map<Operation, List<MilestoneInfo>> operationMap, Operation operation, Operation cancelOperation) throws Exception {
        MilestoneInfo lastMilestone = getLastMilestone(operationMap, Arrays.asList(operation), Arrays.asList(cancelOperation));
        return DateUtil.fromDateToH(lastMilestone.getOperateTime());
    }

    @Override
    public Long countWayBillInfoForReport(ReportConditionVO conditionVO) throws Exception {
        return reportMapper.countWayBillInfoForReport(conditionVO);
    }

    @Override
    public SalesmanVO findSalesmanList(SalesmanVO salesmanVO) throws Exception {
        List<String> names = waybillInfoExtMapper.selectSalesmanByName(salesmanVO.getSalesman());
        if (CollectionUtils.isNotEmpty(names)) {
            names.sort(String.CASE_INSENSITIVE_ORDER);
            salesmanVO.setSalesmanList(names);
        }
        return salesmanVO;
    }

    @Override
    public ConsignorVO findConsignorList(ConsignorVO consignorVO) throws Exception {
        List<String> names = reportMapper.selectByParams("consignor_info", "name", consignorVO.getConsignor());
        if (CollectionUtils.isNotEmpty(names)) {
            names.sort(String.CASE_INSENSITIVE_ORDER);
            consignorVO.setConsignorList(names);
        }
        return consignorVO;
    }

    @Override
    public ReceviceVO findReceviceList(ReceviceVO receviceVO) throws Exception {
        List<String> names = reportMapper.selectByParams("recevice_info", "name", receviceVO.getRecevice());
        if (CollectionUtils.isNotEmpty(names)) {
            names.sort(String.CASE_INSENSITIVE_ORDER);
            receviceVO.setReceviceList(names);
        }
        return receviceVO;
    }

    @Override
    public SecondCustomerVO findSecondCustomerList(SecondCustomerVO secondCustomerVO) throws Exception {
        List<String> names = reportMapper.selectByParams("consignor_info", "second_name", secondCustomerVO.getSecondCustomer());
        if (CollectionUtils.isNotEmpty(names)) {
            names.sort(String.CASE_INSENSITIVE_ORDER);
            secondCustomerVO.setSecondCustomerList(names);
        }
        return secondCustomerVO;
    }

    @Override
    public OpenBillNodeVO findOpenBillNodeList(OpenBillNodeVO openBillNodeVO) throws Exception {
        List<String> names = reportMapper.selectByParams("waybill_info", "open_bill_node", openBillNodeVO.getOpenBillNode());
        if (CollectionUtils.isNotEmpty(names)) {
            names.sort(String.CASE_INSENSITIVE_ORDER);
            openBillNodeVO.setOpenBillNodeList(names);
        }
        return openBillNodeVO;
    }

    @Override
    public MasterVO findMasterList(MasterVO masterVO) throws Exception {
        List<String> names = reportMapper.selectByParams("master_info", "master_name", masterVO.getMaster());
        if (CollectionUtils.isNotEmpty(names)) {
            names.sort(String.CASE_INSENSITIVE_ORDER);
            masterVO.setMasterList(names);
        }
        return masterVO;
    }

    @Override
    public MasterNodeVO findMasterNodeList(MasterNodeVO masterNodeVO) throws Exception {
        List<String> names = reportMapper.selectByParams("master_info", "master_node", masterNodeVO.getMasterNode());
        if (CollectionUtils.isNotEmpty(names)) {
            names.sort(String.CASE_INSENSITIVE_ORDER);
            masterNodeVO.setMasterNodeList(names);
        }
        return masterNodeVO;
    }

    @Override
    public CarrierVO findCarrierList(CarrierVO carrierVO) throws Exception {
        List<String> names = reportMapper.selectByParams("carrier_info", "carrier", carrierVO.getCarrier());
        if (CollectionUtils.isNotEmpty(names)) {
            names.sort(String.CASE_INSENSITIVE_ORDER);
            carrierVO.setCarrierList(names);
        }
        return carrierVO;
    }

    @Override
    public List<CommonVO> selectForCommon(GridRequest gridRequest, SelectConditionVO conditionVO) throws Exception {
        if (gridRequest != null) {
            conditionVO.setOffset((gridRequest.getPage() - 1) * gridRequest.getSize());
            conditionVO.setSize(gridRequest.getSize());
        }
        List<CommonVO> names = reportMapper.selectForCommon(conditionVO);
        return names;
    }

    @Override
    public Long countForCommon(SelectConditionVO conditionVO) throws Exception {
        return reportMapper.countForCommon(conditionVO);
    }
}
