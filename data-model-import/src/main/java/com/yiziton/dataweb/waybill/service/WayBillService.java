package com.yiziton.dataweb.waybill.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yiziton.commons.utils.DateUtil;
import com.yiziton.commons.utils.bean.Beans;
import com.yiziton.commons.vo.enums.*;
import com.yiziton.dataimport.waybill.bean.CompensationInfo;
import com.yiziton.dataimport.waybill.bean.WaybillInfo;
import com.yiziton.dataimport.waybill.dao.WayBillExportInfoMapper;
import com.yiziton.dataimport.waybill.dao.WaybillInfoMapper;
import com.yiziton.dataimport.waybill.dao.ext.BillingInfoExtMapper;
import com.yiziton.dataimport.waybill.dao.ext.CompensationInfoExtMapper;
import com.yiziton.dataweb.waybill.bean.*;
import com.yiziton.dataweb.waybill.dao.CustomWaybillInfoMapper;
import com.yiziton.dataweb.waybill.dataobject.WaybillbaseInfoDO;
import com.yiziton.dataweb.waybill.utils.GridRequest;
import com.yiziton.dataweb.waybill.utils.Pages;
import com.yiziton.dataweb.waybill.utils.excel.ExportExcelUtils;
import com.yiziton.dataweb.waybill.utils.excel.WriteService;
import com.yiziton.dataweb.waybill.vo.*;
import com.yiziton.dataweb.waybill.vo.header.WayBillInfoHeader;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/11/27
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Component("wayBillService")
public class WayBillService {

    private static final Logger LOG = LoggerFactory.getLogger(WayBillService.class);

    @Autowired
    private WayBillExportInfoMapper wayBillExportInfoMapper;

    @Autowired
    private WaybillInfoMapper waybillInfoMapper;

    @Autowired
    private CustomWaybillInfoMapper customWaybillInfoMapper;

    @Autowired
    private BillingInfoExtMapper billingInfoExtMapper;

    @Autowired
    private CompensationInfoExtMapper compensationInfoExtMapper;

    public Workbook exportWayBill(WayBillConditionVO conditionVO) {
        WriteService writeService = ExportExcelUtils.getWriteService(new WayBillInfoHeader(), null);
        while (true) {
            List<Map<String, Object>> wayBillInfoList = wayBillExportInfoMapper.selectWayBillInfoByCondition(conditionVO);
            if (wayBillInfoList != null && wayBillInfoList.size() > 0) {
                writeService.createContents(wayBillInfoList);
                conditionVO.setOffset(conditionVO.getOffset() + conditionVO.getSize());
            } else {
                break;
            }

        }
        return writeService.build();
    }

    public org.springframework.data.domain.Page<WayBillInfo> findWayBillByCode(GridRequest gridRequest, WayBillConditionVO conditionVo) {
        Page<WayBillInfo> wayBillInfoPage = wayBillExportInfoMapper.selectWayBillInfoPageByCondition(gridRequest.getPage(), gridRequest.getSize());

        return Pages.page(wayBillInfoPage, wayBillInfo -> {
            WayBillInfo wayBillInfoVo = new WayBillInfo();
//            wayBillInfoVo.setWayBillId(wayBillInfo.getWayBillId());
//            wayBillInfoVo.setOperation(wayBillInfo.getOperation());
            return wayBillInfoVo;
        });
    }


    public org.springframework.data.domain.Page<WaybillInfoVO> findWaybillList(GridRequest gridRequest, WayBillConditionVO wayBillConditionVo) {
        LOG.info("findWaybillList input params: {}", JSON.toJSONString(wayBillConditionVo, SerializerFeature.WriteDateUseDateFormat));
        String serviceType = wayBillConditionVo.getServiceType();
        if (serviceType != null) {
            wayBillConditionVo.setServiceTypeCode(ServiceType.getEnumByEnumNameString(serviceType).getCode());
        }

        String incrementServiceType = wayBillConditionVo.getIncrementServiceType();
        if (incrementServiceType != null) {
            wayBillConditionVo.setIncrementServiceCode(IncrementServiceType.getEnumByEnumNameString(incrementServiceType).getCode());
        }

        String checkType = wayBillConditionVo.getCheckType();
        if (checkType != null) {
            wayBillConditionVo.setCheckTypeCode(CheckType.getEnumByEnumNameString(checkType).getCode());
        }

        String checkMethod = wayBillConditionVo.getCheckMethod();
        if (checkMethod != null) {
            wayBillConditionVo.setCheckMethodCode(CheckMethod.getEnumByEnumNameString(checkMethod).getCode());
        }

        String waybillStatus = wayBillConditionVo.getWaybillStatus();
        if (waybillStatus != null) {
            wayBillConditionVo.setWaybillStatusCode(WaybillStatus.getEnumByEnumNameString(waybillStatus).getCode());
        }

        String customerType = wayBillConditionVo.getCustomerType();
        if (customerType != null) {
            wayBillConditionVo.setCustomerTypeCode(CustomerType.getEnumByEnumNameString(customerType).getCode());
        }

        String settlementType = wayBillConditionVo.getSettlementType();
        if (settlementType != null) {
            wayBillConditionVo.setSettlementTypeCode(SettlementType.getEnumByEnumNameString(settlementType).getCode());
        }

        Page<WayBillInfo> wayBillInfoPage = customWaybillInfoMapper.selectWaybillInfoListByCondition(wayBillConditionVo,
                gridRequest.getPage(), gridRequest.getSize());

        return Pages.page(wayBillInfoPage, wayBillInfo -> {
            WaybillInfoVO waybillInfoVO = new WaybillInfoVO();
            waybillInfoVO.setWayBillId(wayBillInfo.getId());
            waybillInfoVO.setIncrementServiceFee(wayBillInfo.getIncrementServiceFee());

            try {
                ProductType productType = ProductType.getEnumByCode(wayBillInfo.getProductType());
                waybillInfoVO.setProductType(productType != null ? productType.getMessage() : null);

                ServiceType serviceTypeVal = ServiceType.getEnumByCode(wayBillInfo.getServiceType());
                waybillInfoVO.setServiceType(serviceTypeVal != null ? serviceTypeVal.getMessage() : null);

                IncrementServiceType incrementServiceTypeVal = IncrementServiceType.getEnumByCode(wayBillInfo.getIncrementServiceType());
                waybillInfoVO.setIncrementServiceType(incrementServiceTypeVal != null ? incrementServiceTypeVal.getMessage() : null);

                WaybillStatus waybillStatusVal = WaybillStatus.getEnumByCode(wayBillInfo.getWaybillStatus());
                waybillInfoVO.setWaybillStatus(waybillStatusVal != null ? waybillStatusVal.getMessage() : null);

                CheckType checkTypeVal = CheckType.getEnumByCode(wayBillInfo.getCheckType());
                waybillInfoVO.setCheckType(checkTypeVal != null ? checkTypeVal.getMessage() : null);

                CheckMethod checkMethodVal = CheckMethod.getEnumByCode(wayBillInfo.getCheckMethod());
                waybillInfoVO.setCheckMethod(checkMethodVal != null ? checkMethodVal.getMessage() : null);

                waybillInfoVO.setCheckBillId(wayBillInfo.getCheckBillId());
                waybillInfoVO.setCheckCode(wayBillInfo.getCheckCode());

                CheckStatus checkStatus = CheckStatus.getEnumByCode(wayBillInfo.getCheckStatus());
                waybillInfoVO.setCheckStatus(checkStatus != null ? checkStatus.getMessage() : null);

                PaymentType paymentType = PaymentType.getEnumByCode(wayBillInfo.getPaymentType());
                waybillInfoVO.setPaymentType(paymentType != null ? paymentType.getMessage() : null);

                ChannelSource channelSource = ChannelSource.getEnumByCode(wayBillInfo.getChannelSource());
                waybillInfoVO.setChannelSource(channelSource != null ? channelSource.getMessage() : null);

                waybillInfoVO.setConsignorCode(wayBillInfo.getConsignorCode());
                waybillInfoVO.setConsignorName(wayBillInfo.getConsignorName());
                waybillInfoVO.setConsignorMobile(wayBillInfo.getConsignorMobile());
                waybillInfoVO.setSecondCode(wayBillInfo.getSecondCode());
                waybillInfoVO.setSecondName(wayBillInfo.getSecondName());

                CustomerType customerTypeVal = CustomerType.getEnumByCode(wayBillInfo.getCustomerType());
                waybillInfoVO.setCustomerType(customerTypeVal != null ? customerTypeVal.getMessage() : null);

                SettlementType settlementTypeVal = SettlementType.getEnumByCode(wayBillInfo.getSettlementType());
                waybillInfoVO.setSettlementType(settlementTypeVal != null ? settlementTypeVal.getMessage() : null);

                waybillInfoVO.setDestination(wayBillInfo.getDestination().replace(",", ""));
                waybillInfoVO.setConsigneeName(wayBillInfo.getConsigneeName());
                waybillInfoVO.setConsigneeMobile(wayBillInfo.getConsigneeMobile());
                waybillInfoVO.setConsigneeAddress(wayBillInfo.getConsigneeAddress());
                waybillInfoVO.setConsigneeArea(wayBillInfo.getProvince() + wayBillInfo.getCity() + wayBillInfo.getArea());
                waybillInfoVO.setElevator(wayBillInfo.getElevator());
                waybillInfoVO.setFloor(wayBillInfo.getFloor());
                waybillInfoVO.setActualDoorTime(wayBillInfo.getActualDoorTime());
                waybillInfoVO.setArrivalArea(wayBillInfo.getArrivalArea());
                waybillInfoVO.setArrivalAddress(wayBillInfo.getArrivalAddress());
                waybillInfoVO.setPickUpGoodsPhone(wayBillInfo.getPickUpGoodsPhone());
                waybillInfoVO.setPickUpGoodsPassword(wayBillInfo.getPickUpGoodsPassword());
                waybillInfoVO.setLogisticsBillId(wayBillInfo.getLogisticsBillId());
                waybillInfoVO.setMarbleBlocks(wayBillInfo.getMarbleBlocks());
                waybillInfoVO.setTotalVolume(wayBillInfo.getTotalVolume());
                waybillInfoVO.setTotalPackingItems(wayBillInfo.getTotalPackingItems());
                waybillInfoVO.setTotalInstallItems(wayBillInfo.getTotalInstallItems());
                waybillInfoVO.setTotalWeight(wayBillInfo.getTotalWeight());
                waybillInfoVO.setStatementValue(wayBillInfo.getStatementValue());
                waybillInfoVO.setOpenBillNode(wayBillInfo.getOpenBillNode());
                waybillInfoVO.setOpenBillOperator(wayBillInfo.getOpenBillOperator());
                waybillInfoVO.setSaleTotalPrice(wayBillInfo.getSaleTotalPrice());
                waybillInfoVO.setSalesman(wayBillInfo.getSalesman());
                waybillInfoVO.setSalesmanPhone(wayBillInfo.getSalesmanPhone());
                waybillInfoVO.setOpenBillTime(wayBillInfo.getOpenBillTime());
                waybillInfoVO.setSignTime(wayBillInfo.getSignTime());
                waybillInfoVO.setMasterName(wayBillInfo.getMasterName());
                waybillInfoVO.setMasterPhone(wayBillInfo.getMasterPhone());
                waybillInfoVO.setMasterNode(wayBillInfo.getMasterNode());
                waybillInfoVO.setCarrier(wayBillInfo.getCarrier());
                // TODO: 2018-12-06 业务员组织
                waybillInfoVO.setWaybillTotalPrice(wayBillInfo.getSaleTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
                //计算商家费用
                Double totalPrice = billingInfoExtMapper.selectTotalPriceByWaybillId(wayBillInfo.getId());
                waybillInfoVO.setCustomerPrice(new BigDecimal(totalPrice).setScale(2, BigDecimal.ROUND_HALF_UP));
                waybillInfoVO.setSalesmanOrg("");

                //拼接货物信息
                WayBillConditionVO condition = new WayBillConditionVO();
                condition.setWaybillId(wayBillInfo.getId());
                List<GoodsInfo> goodsInfoList = customWaybillInfoMapper.selectGoodsInfo(condition);
                if (CollectionUtils.isNotEmpty(goodsInfoList)) {
                    String goodsInfo = goodsInfoList.stream().map(o -> o.getGoodsName() + "*" + o.getPackingItems()).collect(Collectors.joining(","));
                    waybillInfoVO.setGoodsInfo(goodsInfo);
                }

                //查询预约上门时间

            } catch (Exception e) {
                LOG.error(wayBillInfo.getId() + "_查询基本信息_解析返回结果失败：", e);
            }

            return waybillInfoVO;
        });
    }

    /**
     * 查询运单明细
     */
    public WaybillDetailVO findWaybillDetailInfo(WayBillConditionVO wayBillConditionVO) {
        String waybillId = wayBillConditionVO.getWaybillId();
        WaybillDetailVO waybillDetailVO = new WaybillDetailVO();
        waybillDetailVO.setWaybillId(waybillId);
        try {
            WaybillInfo waybill = waybillInfoMapper.selectByPrimaryKey(waybillId);
            String doorTime = customWaybillInfoMapper.selectWaybillOperationDetailVal(waybillId, Operation.MASTER_APPOINTMENT.getCode(), "doorTime");
            String doorPeriod = customWaybillInfoMapper.selectWaybillOperationDetailVal(waybillId, Operation.MASTER_APPOINTMENT.getCode(), "doorPeriod");
            BigDecimal customerPrice = customWaybillInfoMapper.selectWaybillBillingSum(waybillId, BillingObject.CUSTOMER.getCode(), BillingType.NORMAL.getCode());
            String incrementServiceStatus = customWaybillInfoMapper.selectWaybillIncrementStatus(waybillId);
            waybillDetailVO.setSaleTotalPrice(waybill.getSaleTotalPrice());
            ChannelSource channelSource = ChannelSource.getEnumByCode(waybill.getChannelSource());
            if (channelSource != null) {
                waybillDetailVO.setChannelSource(channelSource.getMessage());// 渠道来源
            }
            ProductType productType = ProductType.getEnumByCode(waybill.getProductType());
            if (productType != null) {
                waybillDetailVO.setProductType(productType.getMessage());
            }
            PaymentType paymentType = PaymentType.getEnumByCode(waybill.getPaymentType());
            if (paymentType != null) {
                waybillDetailVO.setPaymentType(paymentType.getMessage());
            }
            waybillDetailVO.setDoorTime(doorTime);
            waybillDetailVO.setDoorPeriod(doorPeriod);
            waybillDetailVO.setCustomerPrice(customerPrice);
            waybillDetailVO.setIncrementServiceStatus(incrementServiceStatus);
        } catch (Exception e) {
            LOG.error(waybillId + "_查询运单明细信息失败：", e);
        }

        return waybillDetailVO;
    }


    public List<GoodsInfo> findGoodsInfo(WayBillConditionVO wayBillConditionVO) {
        return customWaybillInfoMapper.selectGoodsInfo(wayBillConditionVO);
    }

    public List<WaybillRelationInfo> findWaybillRelationInfo(WaybillRelationInfo waybillRelationInfo) {
        return customWaybillInfoMapper.selectWaybillRelationInfo(waybillRelationInfo);
    }

    public TransferInfo findWaybillTransferInfo(WayBillConditionVO wayBillConditionVO) {
        TransferInfo transferInfo = customWaybillInfoMapper.selectTransferInfo(wayBillConditionVO);
        if (null != transferInfo) {
            MasterType masterType = MasterType.getEnumByCode(transferInfo.getMasterType());
            // 未分配到网点下师傅不显示安装师傅信息
            if (masterType != MasterType.MASTER) {
                transferInfo.setMasterName(null);
                transferInfo.setMasterPhone(null);
            }
            if(transferInfo.getNodeType() != null){
                NodeType nodeType = NodeType.getEnumByCode(Integer.valueOf(transferInfo.getNodeType()));
                if (nodeType != null) {
                    transferInfo.setNodeType(nodeType.getMessage());
                }
            }
        }
        return transferInfo;
    }


    public ExceptionInfoVO findAbnormalInfo(WayBillConditionVO wayBillConditionVO) {
        ExceptionInfoVO exceptionInfoVO = new ExceptionInfoVO();

        try {
            List<ExceptionInfo> exceptionInfoList = customWaybillInfoMapper.selectExceptionInfoList(wayBillConditionVO);
            exceptionInfoVO.setWaybillId(wayBillConditionVO.getWaybillId());
            if (CollectionUtils.isNotEmpty(exceptionInfoList)) {
                List<ExceptionInfo> list = exceptionInfoList.stream().map(o -> {
                    String exceptionDescribe = o.getExceptionDescribe();
                    if (StringUtils.isNotBlank(exceptionDescribe)) {
                        try {
                            List<String> exceptionDescribeUrlList = JSONArray.parseArray(exceptionDescribe, String.class);
                            o.setExceptionDescribeUrlList(exceptionDescribeUrlList);
                        } catch (Exception e) {
                            LOG.error(o.getExceptionCode() + "_异常图片路径解析失败：", e);
                        }
                    }
                    return o;
                }).collect(Collectors.toList());
                exceptionInfoVO.setExceptionInfoList(list);
            }
        } catch (Exception e) {
            LOG.error(wayBillConditionVO.getWaybillId() + "_查询异常信息失败：", e);
        }
        return exceptionInfoVO;
    }


    public List<AfterSaleTaskInfo> findAfterSaleInfo(WayBillConditionVO wayBillConditionVO) {
        return customWaybillInfoMapper.selectAfterSaleTaskList(wayBillConditionVO);
    }

    public List<BillDetailInfoVO> findFeeInfo(WayBillConditionVO wayBillConditionVO) {
        List<BillDetailInfoVO> billDetailInfoVOList = Lists.newArrayList();

        try {
            List<BillDetailInfo> billDetailInfoList = customWaybillInfoMapper.selectBillDetailList(wayBillConditionVO);
            if (CollectionUtils.isNotEmpty(billDetailInfoList)) {
                Map<String, List<BillDetailInfo>> map = Maps.newLinkedHashMap();
                for (BillDetailInfo billDetailInfo : billDetailInfoList) {
                    //费用类型转换
                    String feeTypeCode = billDetailInfo.getFeeTypeCode();
                    FeeType feeType = FeeType.getEnumByEnumNameString(StringUtils.upperCase(feeTypeCode));
                    billDetailInfo.setFeeType(feeType != null ? feeType.getMessage() : null);

                    String billingObject = String.valueOf(BillingObject.getEnumByCode(billDetailInfo.getBillingObject()));
                    List<BillDetailInfo> list = map.get(billingObject);
                    if (CollectionUtils.isEmpty(list)) {
                        list = Lists.newArrayList();
                    }
                    list.add(billDetailInfo);
                    map.put(billingObject, list);
                }

                map.entrySet().stream().forEach(o -> {
                    BillDetailInfoVO billDetailInfoVO = new BillDetailInfoVO();
                    billDetailInfoVO.setBillObject(o.getKey());
                    billDetailInfoVO.setBillDetailInfoList(o.getValue());
                    billDetailInfoVOList.add(billDetailInfoVO);
                });
            }
        } catch (Exception e) {
            LOG.error(wayBillConditionVO.getWaybillId() + "_查询运单费用信息失败：", e);
        }

        return billDetailInfoVOList;
    }

    public List<List<MilestoneInfo>> findMilestoneInfo(WayBillConditionVO wayBillConditionVO) {
        List<MilestoneInfo> milestoneInfoList = customWaybillInfoMapper.selectMilestoneInfoList(wayBillConditionVO);
        if (CollectionUtils.isNotEmpty(milestoneInfoList)) {
            if (wayBillConditionVO.getMilestoneType() != null) {
                MilestoneType milestoneType = MilestoneType.getEnumByEnumNameString(wayBillConditionVO.getMilestoneType());
                if (milestoneType != null) {
                    switch (milestoneType) {
                        case ALL:
                            break;
                        case MILESTONE://轨迹信息
                            milestoneInfoList.removeIf(o -> {
                                Operation operation = Operation.getEnumByCode(o.getOperation());
                                return operation.equals(Operation.TRACKING_ADD_NOTES);
                            });
                            break;
                        case TRACK://跟踪信息
                            milestoneInfoList.removeIf(o -> {
                                Operation operation = Operation.getEnumByCode(o.getOperation());
                                return !operation.equals(Operation.TRACKING_ADD_NOTES);
                            });
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(milestoneInfoList)) {
            milestoneInfoList = milestoneInfoList.stream().map(o -> {
                Operation operation = Operation.getEnumByCode(o.getOperation());
                if (operation != null) {
                    o.setOperationVal(operation.getMessage());
                }
                OperationType operationType = OperationType.getEnumByCode(o.getOperationType());
                if (operationType != null) {
                    o.setOperationTypeVal(operationType.getMessage());
                }
                return o;
            }).collect(Collectors.toList());
        }
        // 按天分组，再按操作时间倒序排序
        Map<Date, List<MilestoneInfo>> milestoneMap = milestoneInfoList.stream().collect(
                Collectors.groupingBy(milestoneInfo -> DateUtil.parseString2Date(milestoneInfo.getOperateTime(), DateUtil.DF_yyyy_MM_dd),
                        () -> new TreeMap<Date, List<MilestoneInfo>>(Comparator.reverseOrder()),
                        Collectors.toList()));
        // 同一天的节点按操作时间倒序排序
        milestoneMap.forEach((k, v) -> v.sort(Comparator.comparing(milestoneInfo -> DateUtil.fromStringToDate(milestoneInfo.getOperateTime()), Comparator.reverseOrder())));
        return new ArrayList<>(milestoneMap.values());
    }

    public ClaimInfoVO findClaimInfo(WayBillConditionVO wayBillConditionVO) {
        String waybillId = wayBillConditionVO.getWaybillId();
        ClaimInfoVO claimInfoVO = new ClaimInfoVO();
        List<ClaimInfo> claimInfoList = Lists.newArrayList();
        List<ChaseClaimInfo> chaseClaimInfoList = Lists.newArrayList();

        try {
            List<CompensationInfo> compensationInfos = compensationInfoExtMapper.selectByWaybillId(waybillId);
            // 用于 claimInfoList
            List<CompensationInfo> claims = compensationInfos.stream().parallel().filter(x -> x.getCompensationClass().equals("理赔")).collect(Collectors.toList());
            // 用于 chaseClaimInfoList
            List<CompensationInfo> compensations = compensationInfos.stream().parallel().filter(x -> x.getCompensationClass().equals("追赔")).collect(Collectors.toList());

            List<HashMap<String, String>> applyClaimTypeList = customWaybillInfoMapper.selectApplyClaimType(waybillId);
            Map<String, String> applyClaimTypeMap = new HashMap<String, String>();
            if (CollectionUtils.isNotEmpty(applyClaimTypeList)) {
                for (HashMap<String, String> claimInfoMap : applyClaimTypeList) {
                    String claimNo = MapUtils.getString(claimInfoMap, "claimNo");
                    String applyClaimType = MapUtils.getString(claimInfoMap, "applyClaimType");
                    applyClaimTypeMap.put(claimNo, applyClaimType);
                }
            }
            List<HashMap<String, String>> handleClaimTypeList = customWaybillInfoMapper.selectHandleClaimType(waybillId);
            Map<String, String> handleClaimTypeMap = new HashMap<String, String>();
            if (CollectionUtils.isNotEmpty(handleClaimTypeList)) {
                for (HashMap<String, String> claimInfoMap : handleClaimTypeList) {
                    String claimNo = MapUtils.getString(claimInfoMap, "claimNo");
                    String applyClaimType = MapUtils.getString(claimInfoMap, "applyClaimType");
                    handleClaimTypeMap.put(claimNo, applyClaimType);
                }
            }

            if (CollectionUtils.isNotEmpty(claims)) {
                for (CompensationInfo compensationInfo : claims) {
                    ClaimInfo claimInfo = new ClaimInfo();
                    //创建
                    Beans.from(compensationInfo).to(claimInfo);
                    String s = applyClaimTypeMap.get(compensationInfo.getCompensationBillId()) == null ? compensationInfo.getCompensationType() : applyClaimTypeMap.get(compensationInfo.getCompensationBillId());
                    claimInfo.setApplyCompensationType(s);
                    claimInfo.setApplyTime(compensationInfo.getApplyTime().toString());
                    //处理
                    if(compensationInfo.getFirstHandleTime() == null){
                        claimInfo.setFirstHandleTime(null);
                        claimInfo.setCompensationType(null);
                        claimInfo.setHandleFee(null);
                        claimInfo.setFullOffer(null);
                        claimInfo.setHandleView(null);
                    } else {
                        String s1 = handleClaimTypeMap.get(compensationInfo.getCompensationBillId()) == null ? compensationInfo.getCompensationType() : handleClaimTypeMap.get(compensationInfo.getCompensationBillId());
                        claimInfo.setCompensationType(s1);
                        claimInfo.setFirstHandleTime(compensationInfo.getFirstHandleTime().toString());
                    }
                    //结案
                    if(compensationInfo.getCloseTime() == null){
                        claimInfo.setCloseTime(null);
                        claimInfo.setCompensationBillMonth(null);
                        claimInfo.setCloseFee(null);
                        claimInfo.setCloseView(null);
                        claimInfo.setAccommodationFee(null);
                    }else{
                        claimInfo.setCloseTime(compensationInfo.getCloseTime().toString());
                    }
                    claimInfoList.add(claimInfo);
                }
            }

            if (CollectionUtils.isNotEmpty(compensations)) {
                for (CompensationInfo compensationInfo : compensations) {
                    ChaseClaimInfo chaseClaimInfo = new ChaseClaimInfo();
                    //创建
                    Beans.from(compensationInfo).to(chaseClaimInfo);
                    chaseClaimInfo.setApplyTime(compensationInfo.getApplyTime().toString());
                    //处理
                    if(compensationInfo.getFirstHandleTime() == null){
                        chaseClaimInfo.setFirstHandleTime(null);
                        chaseClaimInfo.setHandleFee(null);
                        chaseClaimInfo.setHandleView(null);
                    }else{
                        chaseClaimInfo.setFirstHandleTime(compensationInfo.getFirstHandleTime().toString());
                    }
                    //结案
                    if(compensationInfo.getCloseTime() == null){
                        chaseClaimInfo.setCloseTime(null);
                        chaseClaimInfo.setCompensationBillMonth(null);
                        chaseClaimInfo.setCloseFee(null);
                        chaseClaimInfo.setCloseView(null);
                    }else{
                        chaseClaimInfo.setCloseTime(compensationInfo.getCloseTime().toString());
                    }
                    chaseClaimInfoList.add(chaseClaimInfo);
                }
            }
        } catch (Exception e) {
            LOG.error(waybillId + "_查询运单理赔信息失败：", e);
        }

        claimInfoVO.setWaybillId(waybillId);
        claimInfoVO.setClaimInfoList(claimInfoList);
        claimInfoVO.setChaseClaimInfoList(chaseClaimInfoList);

        return claimInfoVO;
    }


    public WaybillNodeVO findWaybillNodeList(WayBillConditionVO wayBillConditionVO) {
        String waybillId = wayBillConditionVO.getWaybillId();
        WaybillInfo waybillInfo = waybillInfoMapper.selectByPrimaryKey(waybillId);
        Integer serviceType = waybillInfo.getServiceType();
        Integer waybillStatus1 = waybillInfo.getWaybillStatus();
        Operation currentOperation = null;
        //运单实际节点
        List<Operation> waybillMilestoneList = null;
//        Map allWaybillMilestoneList = null;
        List<MilestoneInfo> list = customWaybillInfoMapper.selectWaybillMilestone(waybillId);
        List<MilestoneInfo> allList = customWaybillInfoMapper.selectAllWaybillMilestone(waybillId);
        if (CollectionUtils.isNotEmpty(list)) {
            waybillMilestoneList = list.stream().map(o -> {
                Integer operation = o.getOperation();
                return Operation.getEnumByCode(operation);
            }).collect(Collectors.toList());
            System.out.println(waybillMilestoneList);
            System.out.println(waybillMilestoneList.contains(Operation.WAREHOUSING));
        }
//        if (CollectionUtils.isNotEmpty(allList)) {
//            waybillMilestoneList = allList.stream().map(o -> {
//                Map map = new HashMap();
//                map.put(o.getOperation(),o.getOperateTime());
//                return map;
//            }).collect(Collectors.toList());
//            System.out.println(waybillMilestoneList);
//        }

        //运单标准操作
        //标准流程：开单 -> 入库 -> 外发交接 -> 确认外发 -> 承运商受理 -> 承运商发车 -> 承运商到达 -> 干线结束 -> 总部分配 -> 师傅受理 -> 师傅预约 -> 师傅提货 -> 师傅签收
        List<Operation> standardOperationList = Lists.newArrayList(Operation.WAYBILL_OPEN, Operation.WAREHOUSING,
                Operation.OUT_DELIVER, Operation.OUT_DELIVER_COMFIRM, Operation.CARRIER_ACCEPT, Operation.CARRIER_DEPARTURE,
                Operation.ARRIVAL_DESTINATION, Operation.TRUNK_END, Operation.HEADQUARTERS_DISTRIBUTION,
                Operation.MASTER_ACCEPT, Operation.MASTER_APPOINTMENT, Operation.MASTER_PICK, Operation.MASTER_SIGN);

        //短途交接 -> 确认短途发车 -> 短途到达
        List<Operation> list3 = Lists.newArrayList(Operation.SHORT_DELIVER, Operation.SHORT_DELIVER_COMFIRM, Operation.SHORT_DELIVER_ARRIVE);
        //承运商到达直营 -> 承运商中转
        List<Operation> list4 = Lists.newArrayList(Operation.CARRIER_ARRIVE, Operation.CARRIER_TRANSFER);
        //直营入库 -> 直营外发交接 ->确认直营外发
        List<Operation> list5 = Lists.newArrayList(Operation.DIRECT_RECEIPT, Operation.DIRECT_TRANSFER, Operation.DIRECT_COMFIRM);
        //网点受理 -> 网点预约 -> 网点分配 -> 网点师傅受理
        List<Operation> list6 = Lists.newArrayList(Operation.DIRECT_OR_OUTLET_ACCEPT, Operation.DIRECT_OR_OUTLET_APPOINTMENT,
                Operation.DIRECT_OR_OUTLET_DISTRIBUTION, Operation.MASTER_ACCEPT);

        if (CollectionUtils.isNotEmpty(waybillMilestoneList)) {
            if (serviceType.equals(ServiceType.PICKUP.getCode()) || serviceType.equals(ServiceType.DEDICATED_DELIVERY.getCode())) {
                Iterator<Operation> iterator = standardOperationList.iterator();
                while (iterator.hasNext()) {
                    Operation item = iterator.next();
                    //总部分配 , 师傅受理 ,师傅预约, 师傅提货
                    if (Arrays.asList(Operation.HEADQUARTERS_DISTRIBUTION, Operation.MASTER_ACCEPT, Operation.MASTER_APPOINTMENT, Operation.MASTER_PICK).contains(item)) {
                        iterator.remove();
                    }
                }
            }
            if (serviceType.equals(ServiceType.CITY_DISTRIBUTION.getCode()) ||
                    serviceType.equals(ServiceType.CITY_DISTRIBUTION_DOWNSTAIRS.getCode()) ||
                    serviceType.equals(ServiceType.CITY_DISTRIBUTION_HOME.getCode()) ||
                    serviceType.equals(ServiceType.CITY_DISTRIBUTION_INSTALLATION.getCode())) {
                //入库，外发交接，确认外发，承运商受理 ，承运商发车 ， 承运商到达
                standardOperationList.removeIf(item -> Arrays.asList(Operation.WAREHOUSING, Operation.OUT_DELIVER, Operation.OUT_DELIVER_COMFIRM, Operation.CARRIER_ACCEPT, Operation.CARRIER_DEPARTURE,
                        Operation.ARRIVAL_DESTINATION).contains(item));
            }
            //判断操作第二位是否是外发,是否有入库，有的话不改标准流程，没有则更改
            if ((waybillMilestoneList.size() > 1) && Operation.OUT_DELIVER.equals(waybillMilestoneList.get(1)) && (waybillMilestoneList.contains(Operation.WAREHOUSING)) == false) {
                //入库，确认外发，承运商受理 ，承运商发车 ， 承运商到达
                    standardOperationList.removeIf(item -> Arrays.asList(Operation.WAREHOUSING, Operation.CARRIER_ACCEPT, Operation.CARRIER_DEPARTURE,
                            Operation.ARRIVAL_DESTINATION).contains(item));

            }

            if ((waybillMilestoneList.size() > 1) && Operation.TRUNK_END.equals(waybillMilestoneList.get(1))) {
                //入库，外发交接，确认外发，承运商受理 ，承运商发车 ，承运商到达
                standardOperationList.removeIf(item -> Arrays.asList(Operation.WAREHOUSING, Operation.OUT_DELIVER, Operation.OUT_DELIVER_COMFIRM,
                        Operation.CARRIER_ACCEPT, Operation.CARRIER_DEPARTURE, Operation.ARRIVAL_DESTINATION)
                        .contains(item));
            }
            //实际操作节点移除短途交接的时间比短途交接小,入库前插入list3,移除短途交接的时间比短途交接大，删掉list3
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date shortRemoveTime = null;
            Date shortTime = null;

            for (int i = 0; i < allList.size(); i++) {
                if (allList.get(i).getOperation().equals(Operation.SHORT_DELIVER_REMOVE.getCode())) {
                    try {
                        shortRemoveTime = formatter.parse(allList.get(i).getOperateTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (allList.get(i).getOperation().equals(Operation.SHORT_DELIVER.getCode())) {
                    try {
                        System.out.println("shortTime" + allList.get(i).getOperateTime());
                        shortTime = formatter.parse(allList.get(i).getOperateTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            if ((shortTime != null && shortRemoveTime != null && (shortTime.getTime() - shortRemoveTime.getTime()) > 0) ||
                    (shortRemoveTime == null && shortTime != null)) {
                if (standardOperationList.contains(Operation.WAREHOUSING)) {
                    int index = ListUtils.indexOf(standardOperationList, Operation.WAREHOUSING::equals);
                    standardOperationList.addAll(index, list3);
                }
            }
            if (shortTime != null && shortRemoveTime != null && (shortTime.getTime() - shortRemoveTime.getTime()) < 0) {
                if (standardOperationList.contains(Operation.SHORT_DELIVER)) {
                    standardOperationList.removeAll(list3);
                }
            }

            //承运商到达直营 节点 有的话在 承运商到达 前插入list4
            if (waybillMilestoneList.contains(Operation.CARRIER_ARRIVE)) {
                int index = ListUtils.indexOf(standardOperationList, Operation.ARRIVAL_DESTINATION::equals);
                standardOperationList.addAll(index, list4);
            }
            //直营入库 节点 有的话在 承运商到达 后插入list5
            if (waybillStatus1.equals(WaybillStatus.WAITING_DIRECT_ARRIVAL.getCode())
                    || waybillMilestoneList.contains(Operation.DIRECT_RECEIPT)) {
                int index = ListUtils.indexOf(standardOperationList, Operation.ARRIVAL_DESTINATION::equals);
                standardOperationList.addAll(index + 1, list5);
            }
            //网点受理 节点 有的话 删除师傅受理、师傅预约 后在总部分配后插入list6
            if (waybillMilestoneList.contains(Operation.DIRECT_OR_OUTLET_ACCEPT)) {
                Iterator<Operation> iterator = standardOperationList.iterator();
                while (iterator.hasNext()) {
                    Operation item = iterator.next();
                    if (Arrays.asList(Operation.MASTER_ACCEPT, Operation.MASTER_APPOINTMENT).contains(item)) {
                        iterator.remove();
                        System.out.println("" + standardOperationList);
                    }
                }
                int index = ListUtils.indexOf(standardOperationList, Operation.HEADQUARTERS_DISTRIBUTION::equals);
                standardOperationList.addAll(index + 1, list6);
            }
            //判断有没有总部预约，有的话将师傅预约替换成总部预约
            if (waybillMilestoneList.contains(Operation.HEADQUARTERS_APPOINTMENT)) {
                Collections.replaceAll(standardOperationList, Operation.MASTER_APPOINTMENT, Operation.HEADQUARTERS_APPOINTMENT);
            }
            //判断有没有总部提货，有的话将师傅提货替换成总部提货
            if (waybillMilestoneList.contains(Operation.HEADQUARTERS_PICK)) {
                Collections.replaceAll(standardOperationList, Operation.MASTER_PICK, Operation.HEADQUARTERS_PICK);
            }
            //判断有没有总部签收，有的话将师傅签收替换成总部签收
            if (waybillMilestoneList.contains(Operation.HEADQUARTERS_SIGN)) {
                Collections.replaceAll(standardOperationList, Operation.MASTER_SIGN, Operation.HEADQUARTERS_SIGN);
            }
            // 判断有没有网点提货，有的话将师傅提货替换成网点提货
            if (waybillMilestoneList.contains(Operation.DIRECT_OR_OUTLET_PICK)) {
                Collections.replaceAll(standardOperationList, Operation.MASTER_PICK, Operation.DIRECT_OR_OUTLET_PICK);
            }
            //判断有没有网点签收，有的话将师傅签收替换成网点签收
            if (waybillMilestoneList.contains(Operation.DIRECT_OR_OUTLET_SIGN)) {
                Collections.replaceAll(standardOperationList, Operation.MASTER_SIGN, Operation.DIRECT_OR_OUTLET_SIGN);
            }

        }
        //运单状态映射到对应的操作
//            WaybillInfo waybillInfo = waybillInfoMapper.selectByPrimaryKey(waybillId);
        if (waybillInfo != null) {
            Integer waybillStatusCode = waybillInfo.getWaybillStatus();
            if (waybillStatusCode != null) {
                WaybillStatus waybillStatus = WaybillStatus.getEnumByCode(waybillStatusCode);
                if (waybillStatus != null) {
                    switch (waybillStatus) {
                        case WAITING_WAREHOUSING:
                            currentOperation = Operation.WAYBILL_OPEN;
                            break;
                        case WAITING_SHORT_DELIVER_COMFIRM:
                            currentOperation = Operation.SHORT_DELIVER;
                            break;
                        case WAITING_SHORT_DELIVER_ARRIVE:
                            currentOperation = Operation.SHORT_DELIVER_COMFIRM;
                            break;
                        case WAITING_SHORT_DELIVER_WAREHOUSING:
                            currentOperation = Operation.SHORT_DELIVER_ARRIVE;
                            break;
                        case WAITING_OUT_DELIVER:
                            currentOperation = Operation.WAREHOUSING;
                            break;
                        case WAITING_OUT_DELIVER_COMFIRM:
                            currentOperation = Operation.OUT_DELIVER;
                            break;
                        case WAITING_CARRIER_ACCEPT:
                            currentOperation = Operation.OUT_DELIVER_COMFIRM;
                            break;
                        case WAITING_CARRIER_DEPARTURE:
                            currentOperation = Operation.CARRIER_ACCEPT;
                            break;
                        case WAITING_ARRIVE:
                            currentOperation = Operation.CARRIER_DEPARTURE;
                            break;
                        case WAITING_CARRIER_TRANSFER:
                            currentOperation = Operation.CARRIER_ARRIVE;
                            break;
                        case WAITING_CARRIER_ARRIVE:
                            currentOperation = Operation.CARRIER_TRANSFER;
                            break;
                        case WAITING_DIRECT_ARRIVAL:
                            currentOperation = Operation.ARRIVAL_DESTINATION;
                            break;
                        case WAITING_DIRECT_TRANSFER:
                            currentOperation = Operation.DIRECT_RECEIPT;
                            break;
                        case WAITING_DIRECT_RECEIVE:
                            currentOperation = Operation.ARRIVAL_DESTINATION;
                            break;
                        case WAITING_DIRECT_COMFIRM:
                            currentOperation = Operation.DIRECT_TRANSFER;
                            break;
                        case WAITING_TRUNK_END:
                            currentOperation = (
                                    serviceType.equals(ServiceType.CITY_DISTRIBUTION.getCode()) ||
                                    serviceType.equals(ServiceType.CITY_DISTRIBUTION_DOWNSTAIRS.getCode()) ||
                                    serviceType.equals(ServiceType.CITY_DISTRIBUTION_HOME.getCode()) ||
                                    serviceType.equals(ServiceType.CITY_DISTRIBUTION_INSTALLATION.getCode())
                            ) ? Operation.WAYBILL_OPEN : standardOperationList.contains(Operation.DIRECT_TRANSFER) ? Operation.DIRECT_COMFIRM : Operation.ARRIVAL_DESTINATION;
                            break;
                        case WAITING_HEADQUARTERS_DISTRIBUTION:
                            currentOperation = Operation.TRUNK_END;
                            break;
                        case WAITING_ACCEPT:
                            currentOperation = Operation.HEADQUARTERS_DISTRIBUTION;
                            break;
                        case WAITING_DIRECT_OR_OUTLET_APPOINTMENT:
                            currentOperation = Operation.DIRECT_OR_OUTLET_ACCEPT;
                            break;
                        case WAITING_MASTER_APPOINTMENT:
                            currentOperation = Operation.MASTER_ACCEPT;
                            break;
                        case WAITING_DIRECT_OR_OUTLET_DISTRIBUTION:
                            currentOperation = Operation.DIRECT_OR_OUTLET_APPOINTMENT;
                            break;
                        case WAITING_MASTER_PICK:
                            currentOperation = standardOperationList.contains(Operation.DIRECT_OR_OUTLET_APPOINTMENT) ? Operation.MASTER_ACCEPT : Operation.MASTER_APPOINTMENT;
                            break;
                        case WAITING_MASTER_ACCEPT:
                            currentOperation = Operation.DIRECT_OR_OUTLET_DISTRIBUTION;
                            break;
                        case WAITING_MASTER_SIGN:
                            currentOperation = serviceType.equals(ServiceType.PICKUP.getCode()) ? Operation.TRUNK_END : Operation.MASTER_PICK;
                            break;
                        case FINISH:
                            currentOperation = Operation.MASTER_SIGN;
                            break;
                        default:
                            break;
                    }
                }
            }
        }


        //去重
        LinkedHashSet<String> set = Sets.newLinkedHashSet();
        List<String> resultList = standardOperationList.stream().map(Operation::getMessage).collect(Collectors.toList());
        set.addAll(resultList);
        resultList.clear();
        resultList.addAll(set);
        //构建返回结果
        WaybillNodeVO waybillNodeVO = new WaybillNodeVO();
        waybillNodeVO.setCurrentOperation(currentOperation != null ? currentOperation.getMessage() : null);
        waybillNodeVO.setOperationList(resultList);
        return waybillNodeVO;
    }

    public WaybillbaseInfoVO findWaybillBaseInfo(String waybillId) {
        WaybillbaseInfoDO waybillbaseInfoDO = customWaybillInfoMapper.findWaybillBaseInfo(waybillId);
        WaybillbaseInfoVO waybillbaseInfoVO = new WaybillbaseInfoVO();
        if (null != waybillbaseInfoDO) {
            Beans.from(waybillbaseInfoDO).to(waybillbaseInfoVO);
            if (null != ServiceType.getEnumByCode(waybillbaseInfoDO.getServiceType())) {
                waybillbaseInfoVO.setServiceType(ServiceType.getEnumByCode(waybillbaseInfoDO.getServiceType()).getMessage());
            }
            if (null != ChannelSource.getEnumByCode(waybillbaseInfoDO.getChannelSource())) {
                waybillbaseInfoVO.setChannelSource(ChannelSource.getEnumByCode(waybillbaseInfoDO.getChannelSource()).getMessage());
            }
            if (null != ProductType.getEnumByCode(waybillbaseInfoDO.getProductType())) {
                waybillbaseInfoVO.setProductType(ProductType.getEnumByCode(waybillbaseInfoDO.getProductType()).getMessage());
            }
            if (null != PaymentType.getEnumByCode(waybillbaseInfoDO.getPaymentType())) {
                waybillbaseInfoVO.setPaymentType(PaymentType.getEnumByCode(waybillbaseInfoDO.getPaymentType()).getMessage());
            }
            if (null != CustomerType.getEnumByCode(waybillbaseInfoDO.getCCustomerType())) {
                waybillbaseInfoVO.setCCustomerType(CustomerType.getEnumByCode(waybillbaseInfoDO.getCCustomerType()).getMessage());
            }
            if (null != CheckType.getEnumByCode(waybillbaseInfoDO.getCheckType())) {
                waybillbaseInfoVO.setCheckType(CheckType.getEnumByCode(waybillbaseInfoDO.getCheckType()).getMessage());
            }
            if (null != CheckMethod.getEnumByCode(waybillbaseInfoDO.getCheckMethod())) {
                waybillbaseInfoVO.setCheckMethod(CheckMethod.getEnumByCode(waybillbaseInfoDO.getCheckMethod()).getMessage());
            }
            if (null != CheckStatus.getEnumByCode(waybillbaseInfoDO.getCheckStatus())) {
                waybillbaseInfoVO.setCheckStatus(CheckStatus.getEnumByCode(waybillbaseInfoDO.getCheckStatus()).getMessage());
            }
            if (null != IncrementServiceType.getEnumByCode(waybillbaseInfoDO.getIncrementServiceType())) {
                waybillbaseInfoVO.setIncrementServiceType(IncrementServiceType.getEnumByCode(waybillbaseInfoDO.getIncrementServiceType()).getMessage());
            }
            Timestamp openBillTime = waybillbaseInfoDO.getOpenBillTime();
            if (null != openBillTime) {
                waybillbaseInfoVO.setOpenBillTime(DateUtil.fromTimestampToDate(openBillTime.getTime()));
            }
            Timestamp signTime = waybillbaseInfoDO.getSignTime();
            if (null != signTime) {
                waybillbaseInfoVO.setSignTime(DateUtil.fromTimestampToDate(signTime.getTime()));
            }
        }
        return waybillbaseInfoVO;
    }
}


