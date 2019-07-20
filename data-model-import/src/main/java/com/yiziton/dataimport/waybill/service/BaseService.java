package com.yiziton.dataimport.waybill.service;

import com.yiziton.commons.utils.OID;
import com.yiziton.commons.utils.bean.Beans;
import com.yiziton.commons.vo.enums.*;
import com.yiziton.commons.vo.waybill.*;
import com.yiziton.dataimport.exception.dao.SysExceptionMapper;
import com.yiziton.dataimport.waybill.bean.*;
import com.yiziton.dataimport.waybill.dao.CompensationInfoMapper;
import com.yiziton.dataimport.waybill.dao.ext.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Description: 基础服务类</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: YZT</p>
 *
 * @author TY
 * @version 1.0
 * @date 2018/11/16 15:30
 */
@Slf4j
@Transactional
public class BaseService {

    final String oldmsg = "处理历史数据错误:";
    @Autowired
    AfterSaleInfoExtMapper afterSaleInfoExtMapper;
    @Autowired
    BillingInfoExtMapper billingInfoExtMapper;
    @Autowired
    ConsignorInfoExtMapper consignorInfoExtMapper;
    @Autowired
    ReceviceInfoExtMapper receviceInfoExtMapper;
    @Autowired
    ExceptionInfoExtMapper exceptionInfoExtMapper;
    @Autowired
    FeeInfoExtMapper feeInfoExtMapper;
    @Autowired
    GoodsInfoExtMapper goodsInfoExtMapper;
    @Autowired
    MilestoneInfoExtMapper milestoneInfoExtMapper;
    @Autowired
    OperationDetailExtMapper operationDetailExtMapper;
    @Autowired
    WaybillInfoExtMapper waybillInfoExtMapper;
    @Autowired
    CarrierInfoExtMapper carrierInfoExtMapper;
    @Autowired
    MasterInfoExtMapper masterInfoExtMapper;
    @Autowired
    BillRelationInfoExtMapper billRelationInfoExtMapper;
    @Autowired
    SysExceptionMapper sysExceptionMapper;
    @Autowired
    CompensationInfoExtMapper compensationInfoExtMapper;

    /**
     * 操作表抽取方法
     *
     * @param message
     * @throws Exception
     */

    public MilestoneInfo messageExtMilestoneInfo(Message message) throws Exception {

        MilestoneInfo milestoneInfo = null;
        if (null != message) {
            milestoneInfo = new MilestoneInfo();
            //对象copy，将后一个对象中属性的值赋予给前一个
            Beans.from(message).to(milestoneInfo);
            milestoneInfo.setId(OID.get().toString());
            //milestoneInfo.setSentTime(message.getSentTime());
            //milestoneInfo.setOperateTime(message.getOperateTime());
            Operation operation = Operation.getEnumByEnumNameString(message.getOperation());
            OperationType operationType = OperationType.getEnumByEnumNameString(message.getOperationType());
            if (null != operation) {
                milestoneInfo.setOperation(operation.getCode());
            } else {
                milestoneInfo.setOperation(null);
            }
            if (null != operationType) {
                milestoneInfo.setOperationType(operationType.getCode());
            } else {
                milestoneInfo.setOperation(null);
            }
            milestoneInfo.setStatus(DeleteStatus.ENABLE.getCode());
        }
        return milestoneInfo;
    }

    /**
     * 运单表抽取方法
     *
     * @param message
     * @throws Exception
     */

    public WaybillInfo messageExtWaybillInfo(Message message) throws Exception {
        WaybillInfo waybillInfo = null;
        String msg = "运单表抽取方法:";
        if (null != message && null != message.getMessageBody() && null != message.getMessageBody().getWaybillInfoVO() && null != message.getMessageBody().getWaybillInfoVO().getWaybillStatus()) {
            waybillInfo = new WaybillInfo();
            WaybillInfoVO waybillInfoVO = message.getMessageBody().getWaybillInfoVO();
            //对象copy，将后一个对象中属性的值赋予给前一个
            Beans.from(message.getMessageBody().getWaybillInfoVO()).to(waybillInfo);
            waybillInfo.setId(OID.get().toString());
            checkLimitWaybill(waybillInfoVO, waybillInfo);
        }
        return waybillInfo;
    }

    /**
     * 发货人表抽取方法
     *
     * @param message
     * @throws Exception
     */

    public ConsignorInfo messageExtConsignorInfo(Message message) throws Exception {
        ConsignorInfo consignorInfo = null;
        //对象copy，将后一个对象中属性的值赋予给前一个
        if (null != message && null != message.getMessageBody() && null != message.getMessageBody().getConsignorInfoVO() && null != message.getMessageBody().getConsignorInfoVO().getCustomerType()) {
            consignorInfo = new ConsignorInfo();
            Beans.from(message.getMessageBody().getConsignorInfoVO()).to(consignorInfo);
            consignorInfo.setId(OID.get().toString());
            CustomerType customerType = CustomerType.getEnumByEnumNameString(message.getMessageBody().getConsignorInfoVO().getCustomerType());
            if (null != customerType) {
                consignorInfo.setCustomerType(customerType.getCode());
            } else {
                consignorInfo.setCustomerType(null);
            }
            consignorInfo.setStatus(DeleteStatus.ENABLE.getCode());
        }
        return consignorInfo;
    }

    /**
     * 收货人表抽取方法
     *
     * @param message
     * @throws Exception
     */

    public ReceviceInfo messageExtReceviceInfo(Message message) throws Exception {
        ReceviceInfo receviceInfo = null;
        //对象copy，将后一个对象中属性的值赋予给前一个
        if (null != message && null != message.getMessageBody() && null != message.getMessageBody().getReceviceInfoVO()) {
            receviceInfo = new ReceviceInfo();
            Beans.from(message.getMessageBody().getReceviceInfoVO()).to(receviceInfo);
            receviceInfo.setId(OID.get().toString());
            receviceInfo.setStatus(DeleteStatus.ENABLE.getCode());
        }
        return receviceInfo;
    }

    /**
     * 商品表插入方法
     *
     * @param message
     * @throws Exception
     */

    public void messageInsertGoodsInfo(Message message, Timestamp ts, Integer dataType) throws Exception {
        if (null != message && null != message.getMessageBody() && null != message.getMessageBody().getGoodsInfoVOList()) {
            List<GoodsInfoVO> goodsInfoVOList = message.getMessageBody().getGoodsInfoVOList();
            for (GoodsInfoVO goodsInfoVO : goodsInfoVOList) {
                GoodsInfo goodsInfo = new GoodsInfo();
                //对象copy，将后一个对象中属性的值赋予给前一个
                Beans.from(goodsInfoVO).to(goodsInfo);
                goodsInfo.setId(OID.get().toString());
                goodsInfo.setStatus(DeleteStatus.ENABLE.getCode());
                goodsInfo.setWaybillId(message.getRelatedBillId());
                goodsInfo.setCreateTime(ts);
                goodsInfo.setUpdateTime(ts);
                goodsInfo.setDataType(dataType);
                goodsInfoExtMapper.insert(goodsInfo);
            }
        }
    }

    /**
     * 操作明细表插入方法
     *
     * @param message
     * @throws Exception
     */

    public void messageInsertOperationDetail(Message message, String milestoneId, Timestamp ts, Integer dataType) throws Exception {
        if (null != message && null != message.getMessageBody() && null != message.getMessageBody().getMilestoneInfoVO() && null != message.getMessageBody().getMilestoneInfoVO().getOperationContext()) {
            Map<String, Object> operationContext = message.getMessageBody().getMilestoneInfoVO().getOperationContext();
            if (operationContext != null) {
                Set<String> keySet = operationContext.keySet();
                OperationDetail operationDetail = null;
                for (String key : keySet) {
                    operationDetail = new OperationDetail();
                    operationDetail.setId(OID.get().toString());
                    operationDetail.setWaybillId(message.getWaybillId());
                    operationDetail.setMilestoneId(milestoneId);
                    operationDetail.setField(key);
                    operationDetail.setVal(operationContext.get(key) == null ? "" : operationContext.get(key).toString());
                    operationDetail.setStatus(DeleteStatus.ENABLE.getCode());
                    operationDetail.setCreateTime(ts);
                    operationDetail.setUpdateTime(ts);
                    operationDetail.setDataType(dataType);
                    operationDetailExtMapper.insert(operationDetail);
                }
            }
        }
    }


    /**
     * 异常表抽取方法
     *
     * @param message
     * @throws Exception
     */

    public ExceptionInfo messageExtExceptionInfo(Message message) throws Exception {
        ExceptionInfo exceptionInfo = null;
        //对象copy，将后一个对象中属性的值赋予给前一个
        if (null != message && null != message.getMessageBody() && null != message.getMessageBody().getExceptionInfoVO()) {
            exceptionInfo = new ExceptionInfo();
            Beans.from(message.getMessageBody().getExceptionInfoVO()).to(exceptionInfo);
            exceptionInfo.setId(OID.get().toString());
            exceptionInfo.setStatus(DeleteStatus.ENABLE.getCode());
        }
        return exceptionInfo;
    }

    /**
     * 公共方法
     *
     * @param message
     * @throws Exception
     */
    @Transactional
    public void commonMethod(Message message) throws Exception {
        // TODO: 2018/11/14
        Operation operation = Operation.getEnumByEnumNameString(message.getOperation());
        String operationMessage = operation.getMessage();
        log.info("调用" + operationMessage + "接口开始");
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        String waybillId = message.getWaybillId();
        if (null == waybillId) {
            throw new RuntimeException(operationMessage + " : 运单号不能为空!");
        }

        String[] split = waybillId.split(",");
        for (String id : split) {
            //里程碑
            // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
            MilestoneInfo milestoneInfo = new MilestoneInfo();
            //对象copy，将后一个对象中属性的值赋予给前一个
            //里程碑信息在消息头里面
            Beans.from(message).to(milestoneInfo);
            milestoneInfo.setId(OID.get().toString());
            OperationType operationType = OperationType.getEnumByEnumNameString(message.getOperationType());
            if (null != operation) {
                milestoneInfo.setOperation(operation.getCode());
            } else {
                milestoneInfo.setOperation(null);
            }
            if (null != operationType) {
                milestoneInfo.setOperationType(operationType.getCode());
            } else {
                milestoneInfo.setOperationType(null);
            }
            milestoneInfo.setWaybillId(id);
            milestoneInfo.setStatus(DeleteStatus.ENABLE.getCode());
            milestoneInfo.setCreateTime(ts);
            milestoneInfo.setUpdateTime(ts);
            milestoneInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
            if (StringUtils.isBlank(message.getOperator())) {
                milestoneInfo.setOperator(message.getOperationSys() + "系统");
                log.error("业务系统 " + message.getOperationSys() + " 没有将当前操作人传过来, 运单号为 " + waybillId);
            }
            milestoneInfoExtMapper.insert(milestoneInfo);

            MessageBody messageBody = message.getMessageBody();
            if (messageBody == null) {
                throw new RuntimeException("消息体不能为空!");
            }

            // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
            WaybillInfoVO waybillInfoVO = messageBody.getWaybillInfoVO();
            if (waybillInfoVO != null) {
                String waybillStatus = waybillInfoVO.getWaybillStatus();
                WaybillInfo waybillInfo = waybillInfoExtMapper.selectByPrimaryKey(id);
                if (waybillInfo != null) {
                    WaybillStatus waybillStatus1 = WaybillStatus.getEnumByEnumNameString(waybillStatus);
                    if (null != waybillStatus1) {
                        waybillInfo.setWaybillStatus(waybillStatus1.getCode());
                    }
                    //是否签收
                    if (operation.equals(Operation.HEADQUARTERS_SIGN) || operation.equals(Operation.DIRECT_OR_OUTLET_SIGN) || operation.equals(Operation.MASTER_SIGN)) {
                        Timestamp operateTime = message.getOperateTime();
                        if(!(waybillInfo.getServiceType().equals(ServiceType.CARRIER_RETURN_GOODS) || waybillInfo.getServiceType().equals(ServiceType.CARRIER_RETURN_GOODS))){
                            waybillInfo.setSignTime(operateTime);//更新签收时间,返货的签收时间是返回客户的时间
                        }
                    }
                    //是否取消签收
                    if (operation.equals(Operation.HEADQUARTERS_SIGN_CANCEL) || operation.equals(Operation.DIRECT_OR_OUTLET_SIGN_CANCEL) || operation.equals(Operation.MASTER_SIGN_CANCEL)) {
                        if(!(waybillInfo.getServiceType().equals(ServiceType.CARRIER_RETURN_GOODS) || waybillInfo.getServiceType().equals(ServiceType.CARRIER_RETURN_GOODS))){
                            waybillInfo.setSignTime(null);//更新签收时间,返货的签收时间是返回客户的时间
                        }
                    }
                    // 核销
                    if (operation.equals(Operation.CHECK_WAYBILL)) {
                        waybillInfo.setCheckStatus(CheckStatus.CHECKED.getCode());//更新核销状态
                        waybillInfo.setCheckTime(message.getOperateTime());//更新核销时间
                    }
                    // 拣货
                    if (operation.equals(Operation.PICKING)) {
                        waybillInfo.setPickingTime(message.getOperateTime());//更新拣货时间
                    }
                    // 取消拣货
                    if (operation.equals(Operation.PICKING_CANCEL)) {
                        waybillInfo.setPickingTime(null);//更新拣货时间
                    }
                    // 返回客户
                    if (operation.equals(Operation.RETURN_COUSTOMER)) {
                        waybillInfo.setSignTime(message.getOperateTime());//更新签收时间
                    }
                    waybillInfo.setUpdateTime(ts);
                    waybillInfoExtMapper.updateByPrimaryKey(waybillInfo);//updateByPrimaryKeySelective
                } else {
                    log.debug("运单不存在 : " + id);
                }
                BillRelationInfoVO billRelationInfoVO = messageBody.getBillRelationInfoVO();
                //
                List<String> list = new ArrayList<>();
                list.add(waybillId);
                List<BillRelationInfo> billRelationInfos = billRelationInfoExtMapper.selectByWaybillIds(list);
                switch (operation) {
                    case BIND_BILL:
                        if (StringUtils.isNotBlank(billRelationInfoVO.getCustomerBillId()) || StringUtils.isNotBlank(billRelationInfoVO.getOrderBillId())) {
                            if (billRelationInfos != null && billRelationInfos.size() > 0) {
                                for (BillRelationInfo billRelationInfo : billRelationInfos) {
                                    billRelationInfo.setCustomerBillId(billRelationInfoVO.getCustomerBillId());
                                    billRelationInfo.setOrderBillId(billRelationInfoVO.getOrderBillId());
                                    billRelationInfo.setUpdateTime(ts);
                                    billRelationInfoExtMapper.updateByPrimaryKey(billRelationInfo);
                                }
                            } else {
                                BillRelationInfo billRelationInfo = new BillRelationInfo();
                                Beans.from(billRelationInfoVO).to(billRelationInfo);
                                billRelationInfo.setId(OID.get().toString());
                                billRelationInfo.setWaybillId(message.getWaybillId());
                                billRelationInfo.setStatus(DeleteStatus.ENABLE.getCode());
                                billRelationInfo.setCreateTime(ts);
                                billRelationInfo.setUpdateTime(ts);
                                billRelationInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
                                billRelationInfoExtMapper.insert(billRelationInfo);
                            }
                        }
                        break;
                    case UNLASH_BILL:
                        if (StringUtils.isNotBlank(billRelationInfoVO.getCustomerBillId()) || StringUtils.isNotBlank(billRelationInfoVO.getOrderBillId())) {
                            if (billRelationInfos != null && billRelationInfos.size() > 0) {
                                for (BillRelationInfo billRelationInfo : billRelationInfos) {
                                    if (billRelationInfo.getOrderBillId() != null
                                            && billRelationInfo.getOrderBillId().equals(billRelationInfoVO.getOrderBillId())
                                            && billRelationInfo.getWaybillId().equals(waybillId)) {
                                        //billRelationInfo.setCustomerBillId(billRelationInfoVO.getCustomerBillId());
                                        //billRelationInfo.setOrderBillId(null);
                                        billRelationInfo.setUpdateTime(ts);
                                        billRelationInfo.setStatus(DeleteStatus.DISABLE.getCode());
                                        billRelationInfoExtMapper.updateByPrimaryKey(billRelationInfo);
                                    }
                                }
//                                billRelationInfos.stream().parallel().forEach(x -> {
//                                    if (x.getCustomerBillId().equals(billRelationInfoVO.getCustomerBillId()) && x.getOrderBillId().equals(billRelationInfoVO.getOrderBillId()) && x.getWaybillId().equals(waybillId)) {
//                                        x.setStatus(DeleteStatus.DISABLE.getCode());
//                                        billRelationInfoExtMapper.updateByPrimaryKey(x);
//                                    }
//                                });
                            }
                        }
                        break;
                }
            }

            //部分操作节点没有 operationContext
            Timestamp operateTime = message.getOperateTime();
            //判断是否是取消入库, 新建外发还没有确认外发时撤销，operation是WAREHOUSING_CANCEL
            if (Operation.WAREHOUSING_CANCEL.equals(operation)) {
                insertOrUpdateCarrierInfo(null, id, ts, operation, operateTime);
            }
            //判断是否是取消外发交接节点, 没有operationContext
            if (Operation.OUT_DELIVER_CANCEL.equals(operation)) {
                insertOrUpdateCarrierInfo(null, id, ts, operation, operateTime);
                updateWaybillForPickUpInfo(operation, ts, id, null);
            }
            //判断是否是移除外发交接节点, 没有operationContext，对应外发交接
            if (Operation.OUT_DELIVER_REMOVE.equals(operation)) {
                insertOrUpdateCarrierInfo(null, id, ts, operation, operateTime);
                updateWaybillForPickUpInfo(operation, ts, id, null);
            }
            //判断是否是承运商到达目的地节点, 没有operationContext
            if (Operation.ARRIVAL_DESTINATION_CANCEL.equals(operation)) {
                insertOrUpdateCarrierInfo(null, id, ts, operation, operateTime);
            }
            //判断是否是取消干结节点, 没有operationContext
            if (Operation.TRUNK_END_CANCEL.equals(operation)) {
                insertOrUpdateMasterInfo(null, id, ts, operation, operateTime);
                updateWaybillForPickUpInfo(operation, ts, id, null);
                insertOrUpdateCarrierInfo(null, id, ts, operation, operateTime);
            }
            //判断是否是外发交接节点
            if (Operation.DIRECT_TRANSFER_REMOVE.equals(operation)) {
                insertOrUpdateCarrierInfo(null, id, ts, operation, operateTime);
            }
            // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
            MilestoneInfoVO milestoneInfoVO = messageBody.getMilestoneInfoVO();//用于获取operationContext
            if (milestoneInfoVO != null) {
                Map<String, Object> operationContext = milestoneInfoVO.getOperationContext();
                if (operationContext != null) {
                    Set<String> keySet = operationContext.keySet();
                    String milestoneId = milestoneInfo.getId();
                    OperationDetail operationDetail = null;
                    for (String key : keySet) {
                        operationDetail = new OperationDetail();
                        operationDetail.setId(OID.get().toString());
                        operationDetail.setWaybillId(id);
                        operationDetail.setMilestoneId(milestoneId);
                        operationDetail.setField(key);
                        operationDetail.setVal(operationContext.get(key) == null ? "" : operationContext.get(key).toString());
                        operationDetail.setCreateTime(ts);
                        operationDetail.setStatus(DeleteStatus.ENABLE.getCode());
                        operationDetail.setDataType(DataType.REAL_TIME_DATA.getCode());
                        operationDetailExtMapper.insert(operationDetail);
                    }
                    // 通融处理
                    if(Operation.ACCOMMODATION_DEAL.equals(operation)){
                        String compensationBillId = operationContext.get("compensationBillId") == null ? "" : operationContext.get("compensationBillId").toString();
                        if(StringUtils.isNotEmpty(compensationBillId)){
                            CompensationInfo compensationInfoOld = compensationInfoExtMapper.selectByWaybillIdAndCompensationBillId(id, compensationBillId);
                            double accommodationFee = compensationInfoOld.getAccommodationFee() == null ? 0.00 : compensationInfoOld.getAccommodationFee();
                            String accommodationFeeStr = operationContext.get("accommodationFee") == null ? "0.00" : operationContext.get("accommodationFee").toString();
                            if(StringUtils.isNotEmpty(accommodationFeeStr)){
                                double value = Double.valueOf(accommodationFeeStr);
                                accommodationFee += value;
                                accommodationFee = new BigDecimal(accommodationFee).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                                compensationInfoOld.setAccommodationFee(accommodationFee);
                                compensationInfoExtMapper.updateByPrimaryKeySelective(compensationInfoOld);
                            }
                        }
                    }
                }
                //判断是否是外发交接节点
                if (Operation.OUT_DELIVER.equals(operation)) {
                    insertOrUpdateCarrierInfo(operationContext, id, ts, operation, operateTime);
                }
                //判断是修改否是承运商节点
                if (Operation.CARRIER_MODIFY.equals(operation)) {
                    insertOrUpdateCarrierInfo(operationContext, id, ts, operation, operateTime);
                }
                //判断是否是承运商受理节点
                if (Operation.CARRIER_ACCEPT.equals(operation)) {
                    insertOrUpdateCarrierInfo(operationContext, id, ts, operation, operateTime);
                    updateWaybillForPickUpInfo(operation, ts, id, operationContext);
                }
                //判断是否是取消承运商受理节点
                if (Operation.CARRIER_ACCEPT_CANCEL.equals(operation)) {
                    insertOrUpdateCarrierInfo(operationContext, id, ts, operation, operateTime);
                    updateWaybillForPickUpInfo(operation, ts, id, operationContext);
                }
                //判断是否是承运商中转节点
                if (Operation.CARRIER_TRANSFER.equals(operation)) {
                    insertOrUpdateCarrierInfo(operationContext, id, ts, operation, operateTime);
                }
                //判断是否是取消承运商中转节点
                if (Operation.CARRIER_TRANSFER_CANCEL.equals(operation)) {
                    insertOrUpdateCarrierInfo(operationContext, id, ts, operation, operateTime);
                }
                //判断是否是承运商发车节点
                if (Operation.CARRIER_DEPARTURE.equals(operation)) {
                    insertOrUpdateCarrierInfo(operationContext, id, ts, operation, operateTime);
                }
                //判断是否是承运商发车节点
                if (Operation.CARRIER_DEPARTURE_CANCEL.equals(operation)) {
                    insertOrUpdateCarrierInfo(operationContext, id, ts, operation, operateTime);
                }
                //判断是否是承运商到达目的地节点
                if (Operation.ARRIVAL_DESTINATION.equals(operation)) {
                    insertOrUpdateCarrierInfo(operationContext, id, ts, operation, operateTime);
                }
                //判断是否是分配节点
                if (Operation.HEADQUARTERS_DISTRIBUTION.equals(operation) || Operation.DIRECT_OR_OUTLET_DISTRIBUTION.equals(operation)) {
                    insertOrUpdateMasterInfo(operationContext, id, ts, operation, operateTime);
                }
                //判断是否是取消分配节点
                if (Operation.HEADQUARTERS_DISTRIBUTION_CANCEL.equals(operation) || Operation.DIRECT_OR_OUTLET_DISTRIBUTION_CANCEL.equals(operation)) {
                    insertOrUpdateMasterInfo(operationContext, id, ts, operation, operateTime);
                }
                //判断是否是预约节点
                if (Operation.HEADQUARTERS_APPOINTMENT.equals(operation) || Operation.DIRECT_OR_OUTLET_APPOINTMENT.equals(operation) || Operation.MASTER_APPOINTMENT.equals(operation)) {
                    insertOrUpdateMasterInfo(operationContext, id, ts, operation, operateTime);
                }
                //判断是否是取消预约节点
                if (Operation.HEADQUARTERS_APPOINTMENT_CANCEL.equals(operation) || Operation.DIRECT_OR_OUTLET_APPOINTMENT_CANCEL.equals(operation) || Operation.MASTER_APPOINTMENT_CANCEL.equals(operation)) {
                    insertOrUpdateMasterInfo(operationContext, id, ts, operation, operateTime);
                }
                //判断是否是上门打卡节点
                if (Operation.MASTER_HOME_CHECK.equals(operation)) {
                    insertOrUpdateMasterInfo(operationContext, id, ts, operation, operateTime);
                }
                //判断是否是取消上门打卡节点
                if (Operation.MASTER_HOME_CHECK_CANCEL.equals(operation)) {
                    insertOrUpdateMasterInfo(operationContext, id, ts, operation, operateTime);
                }
                //判断是否是干结节点
                if (Operation.TRUNK_END.equals(operation)) {
                    insertOrUpdateMasterInfo(operationContext, id, ts, operation, operateTime);
                    updateWaybillForPickUpInfo(operation, ts, id, operationContext);
                    insertOrUpdateCarrierInfo(null, id, ts, operation, operateTime);
                }
                //判断是否是修改提货信息
                if (Operation.PICK_MODIFY.equals(operation)) {
                    updateWaybillForPickUpInfo(operation, ts, id, operationContext);
                }
                //判断是否是签收节点
                if (Operation.HEADQUARTERS_SIGN.equals(operation) || Operation.DIRECT_OR_OUTLET_SIGN.equals(operation) || Operation.MASTER_SIGN.equals(operation)) {
                    insertOrUpdateMasterInfo(operationContext, id, ts, operation, operateTime);
                }
                //判断是否是签收节点
                if (Operation.HEADQUARTERS_SIGN_CANCEL.equals(operation) || Operation.DIRECT_OR_OUTLET_SIGN_CANCEL.equals(operation) || Operation.MASTER_SIGN_CANCEL.equals(operation)) {
                    insertOrUpdateMasterInfo(operationContext, id, ts, operation, operateTime);
                }
                //判断是否是修改承运商费用（外发费用录入）节点
                if (Operation.CARRIER_MODIFY_FEES.equals(operation)) {
                    updateWaybillForPickUpInfo(operation, ts, id, operationContext);
                }
                //判断是否是外发交接节点
                if (Operation.DIRECT_TRANSFER.equals(operation)) {
                    insertOrUpdateCarrierInfo(operationContext, id, ts, operation, operateTime);
                }
            }

            // 4.exceptionInfo的字段插入到exception_info表，同时插入运单号创建时间，更新时间
            ExceptionInfoVO exceptionInfoVO = messageBody.getExceptionInfoVO();//用于获取operationContext
            if (exceptionInfoVO != null) {
                if(exceptionInfoVO.getExceptionCode() == null){
                    throw new RuntimeException("异常编号不能为空：" + id + "" + exceptionInfoVO.getExceptionCode());
                }
                ExceptionInfo exceptionInfoOld = exceptionInfoExtMapper.selectByWaybillIdAndExceptionCode(id, exceptionInfoVO.getExceptionCode());
                if(exceptionInfoOld == null){
                    ExceptionInfo exceptionInfo = new ExceptionInfo();
                    //对象copy，将后一个对象中属性的值赋予给前一个
                    Beans.from(exceptionInfoVO).to(exceptionInfo);
                    exceptionInfo.setId(OID.get().toString());
                    exceptionInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    exceptionInfo.setWaybillId(id);
                    exceptionInfo.setCreateTime(ts);
                    exceptionInfo.setUpdateTime(ts);
                    exceptionInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
                    /*String exceptionStatus = exceptionInfoVO.getExceptionStatus();
                    ExceptionStatus enumByEnumNameString = ExceptionStatus.getEnumByEnumNameString(exceptionStatus);
                    if (null != enumByEnumNameString) {
                        exceptionInfo.setExceptionStatus(enumByEnumNameString.getCode());
                    }*/
                    exceptionInfoExtMapper.insert(exceptionInfo);
                } else {
                    //throw new RuntimeException("已存在相同的运单号与异常编号：" + id + "" + exceptionInfo.getExceptionCode());
                    log.info("已存在相同的运单号与异常编号：" + id + "" + exceptionInfoOld.getExceptionCode());
                    //if(exceptionInfoVO.getId() != null){}
                    //if(exceptionInfoVO.getWaybillId() != null){}
                    //if(exceptionInfoVO.getExceptionCode() != null){}
                    if(exceptionInfoVO.getExceptionType() != null){
                        exceptionInfoOld.setExceptionType(exceptionInfoVO.getExceptionType());
                    }
                    if(exceptionInfoVO.getExceptionMessage() != null){
                        exceptionInfoOld.setExceptionMessage(exceptionInfoVO.getExceptionMessage());
                    }
                    if(exceptionInfoVO.getExceptionDescribe() != null){
                        exceptionInfoOld.setExceptionDescribe(exceptionInfoVO.getExceptionDescribe());
                    }
                    if(exceptionInfoVO.getFeedbackSys() != null){
                        exceptionInfoOld.setFeedbackSys(exceptionInfoVO.getFeedbackSys());
                    }
                    if(exceptionInfoVO.getFeedbackTime() != null){
                        exceptionInfoOld.setFeedbackTime(exceptionInfoVO.getFeedbackTime());
                    }
                    if(exceptionInfoVO.getFeedback() != null){
                        exceptionInfoOld.setFeedback(exceptionInfoVO.getFeedback());
                    }
                    if(exceptionInfoVO.getFeedbackParty() != null){
                        exceptionInfoOld.setFeedbackParty(exceptionInfoVO.getFeedbackParty());
                    }
                    if(exceptionInfoVO.getDispose() != null){
                        exceptionInfoOld.setDispose(exceptionInfoVO.getDispose());
                    }
                    if(exceptionInfoVO.getDisposeResult() != null){
                        exceptionInfoOld.setDisposeResult(exceptionInfoVO.getDisposeResult());
                    }
                    if(exceptionInfoVO.getArbitrationResult() != null){
                        exceptionInfoOld.setArbitrationResult(exceptionInfoVO.getArbitrationResult());
                    }
                    if(exceptionInfoVO.getRestrictDisposeTime() != null){
                        exceptionInfoOld.setRestrictDisposeTime(exceptionInfoVO.getRestrictDisposeTime());
                    }
                    if(exceptionInfoVO.getActualDisposeTime() != null){
                        exceptionInfoOld.setActualDisposeTime(exceptionInfoVO.getActualDisposeTime());
                    }
                    //if(exceptionInfoVO.getStatus() != null){}
                    //if(exceptionInfoVO.getCreateTime() != null){}
                    //if(exceptionInfoVO.getUpdateTime() != null){}
                    exceptionInfoOld.setUpdateTime(ts);
                    if(exceptionInfoVO.getExceptionStatus() != null){
                        /*String exceptionStatus = exceptionInfoVO.getExceptionStatus();
                        ExceptionStatus enumByEnumNameString = ExceptionStatus.getEnumByEnumNameString(exceptionStatus);
                        if (null != enumByEnumNameString) {
                            exceptionInfoOld.setExceptionStatus(enumByEnumNameString.getCode());
                        }*/
                        exceptionInfoOld.setExceptionStatus(exceptionInfoVO.getExceptionStatus());
                    }
                    if(exceptionInfoVO.getFeedbackPhone() != null){
                        exceptionInfoOld.setFeedbackPhone(exceptionInfoVO.getFeedbackPhone());
                    }
                    if(exceptionInfoVO.getFeedbackNode() != null){
                        exceptionInfoOld.setFeedbackNode(exceptionInfoVO.getFeedbackNode());
                    }
                    if(exceptionInfoVO.getArbitrationCode() != null){
                        exceptionInfoOld.setArbitrationCode(exceptionInfoVO.getArbitrationCode());
                    }
                    if(exceptionInfoVO.getArbitrationType() != null){
                        exceptionInfoOld.setArbitrationType(exceptionInfoVO.getArbitrationType());
                    }
                    if(exceptionInfoVO.getDisposeNode() != null){
                        exceptionInfoOld.setDisposeNode(exceptionInfoVO.getDisposeNode());
                    }
                    //if(exceptionInfoVO.getDataType() != null){}
                    if(exceptionInfoVO.getDisposeRemark() != null){
                        exceptionInfoOld.setDisposeRemark(exceptionInfoVO.getDisposeRemark());
                    }
                    if(exceptionInfoVO.getExceptionLargeType() != null){
                        exceptionInfoOld.setExceptionLargeType(exceptionInfoVO.getExceptionLargeType());
                    }
                    if(exceptionInfoVO.getExceptionSubType() != null){
                        exceptionInfoOld.setExceptionSubType(exceptionInfoVO.getExceptionSubType());
                    }
                    if (null != exceptionInfoVO.getDisposePhone()) {
                        exceptionInfoOld.setDisposePhone(exceptionInfoVO.getDisposePhone());
                    }
                    if (null != exceptionInfoVO.getDisposeBillId()) {
                        exceptionInfoOld.setDisposeBillId(exceptionInfoVO.getDisposeBillId());
                    }
                    if (null != exceptionInfoVO.getDuty()) {
                        exceptionInfoOld.setDuty(exceptionInfoVO.getDuty());
                    }
                    if (null != exceptionInfoVO.getDutyAccount()) {
                        exceptionInfoOld.setDutyAccount(exceptionInfoVO.getDutyAccount());
                    }
                    if (null != exceptionInfoVO.getIsClaim()) {
                        exceptionInfoOld.setIsClaim(exceptionInfoVO.getIsClaim());
                    }
                    if (null != exceptionInfoVO.getClaimBillId()) {
                        exceptionInfoOld.setClaimBillId(exceptionInfoVO.getClaimBillId());
                    }
                    if (null != exceptionInfoVO.getDisposeSys()) {
                        exceptionInfoOld.setDisposeSys(exceptionInfoVO.getDisposeSys());
                    }
                    if (null != exceptionInfoVO.getDisposeObject()) {
                        exceptionInfoOld.setDisposeObject(exceptionInfoVO.getDisposeObject());
                    }
                    exceptionInfoExtMapper.updateByPrimaryKeySelective(exceptionInfoOld);
                }
            }

            CompensationInfoVO compensationInfoVO = messageBody.getCompensationInfoVO();
            if(compensationInfoVO != null){
                String msg = "";
                if (Operation.CLAIM_NEW.equals(operation) || Operation.CLAIMS_DEAL.equals(operation)
                        || Operation.CLAIMS_CLOSE.equals(operation) || Operation.CLAIMS_CANCEL.equals(operation)
                        || Operation.CLAIMS_MODIFY.equals(operation)) {
                    msg = "理赔";
                }
                if (Operation.COMPENSATION_NEW.equals(operation) || Operation.COMPENSATION_DEAL.equals(operation)
                        || Operation.COMPENSATION_CLOSE.equals(operation) || Operation.COMPENSATION_CANCEL.equals(operation)) {
                    msg = "追赔";
                }
                if (Operation.ACCOMMODATION_DEAL.equals(operation)) {
                    msg = "通融处理";
                }
                if(compensationInfoVO.getCompensationBillId() == null){
                    throw new RuntimeException(msg + "编号不能为空：" + id + "" + compensationInfoVO.getCompensationBillId());
                }
                CompensationInfo compensationInfoOld = compensationInfoExtMapper.selectByWaybillIdAndCompensationBillId(id, compensationInfoVO.getCompensationBillId());
                if(compensationInfoOld == null){
                    CompensationInfo compensationInfo = new CompensationInfo();
                    //对象copy，将后一个对象中属性的值赋予给前一个
                    Beans.from(compensationInfoVO).to(compensationInfo);
                    compensationInfo.setId(OID.get().toString());
                    compensationInfo.setWaybillId(id);
                    compensationInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    compensationInfo.setCreateTime(ts);
                    compensationInfo.setUpdateTime(ts);
                    compensationInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
                    compensationInfoExtMapper.insert(compensationInfo);
                }else{
                    //if(compensationInfoVO.getId() != null){compensationInfoOld.setId();}
                    //if(compensationInfoVO.getWaybillId() != null){compensationInfoOld.setWaybillId();}
                    //if(compensationInfoVO.getStatus() != null){compensationInfoOld.setStatus();}
                    if (compensationInfoVO.getCompensationBillId() != null) {
                        compensationInfoOld.setCompensationBillId(compensationInfoVO.getCompensationBillId());
                    }
                    if (compensationInfoVO.getCompensationClass() != null) {
                        compensationInfoOld.setCompensationClass(compensationInfoVO.getCompensationClass());
                    }
                    if (compensationInfoVO.getBillId() != null) {
                        compensationInfoOld.setBillId(compensationInfoVO.getBillId());
                    }
                    if (compensationInfoVO.getBillName() != null) {
                        compensationInfoOld.setBillName(compensationInfoVO.getBillName());
                    }
                    if (compensationInfoVO.getCompensationBillMonth() != null) {
                        compensationInfoOld.setCompensationBillMonth(compensationInfoVO.getCompensationBillMonth());
                    }
                    if (compensationInfoVO.getDutyObject() != null) {
                        compensationInfoOld.setDutyObject(compensationInfoVO.getDutyObject());
                    }
                    if (compensationInfoVO.getDutyName() != null) {
                        compensationInfoOld.setDutyName(compensationInfoVO.getDutyName());
                    }
                    if (compensationInfoVO.getDutyCode() != null) {
                        compensationInfoOld.setDutyCode(compensationInfoVO.getDutyCode());
                    }
                    if (compensationInfoVO.getCompensationGoods() != null) {
                        compensationInfoOld.setCompensationGoods(compensationInfoVO.getCompensationGoods());
                    }
                    if (compensationInfoVO.getCompensationType() != null) {
                        compensationInfoOld.setCompensationType(compensationInfoVO.getCompensationType());
                    }
                    if (compensationInfoVO.getCompensationReason() != null) {
                        compensationInfoOld.setCompensationReason(compensationInfoVO.getCompensationReason());
                    }
                    if (compensationInfoVO.getFullOffer() != null) {
                        compensationInfoOld.setFullOffer(compensationInfoVO.getFullOffer());
                    }
                    if (compensationInfoVO.getHandleView() != null) {
                        compensationInfoOld.setHandleView(compensationInfoVO.getHandleView());
                    }
                    if (compensationInfoVO.getCloseView() != null) {
                        compensationInfoOld.setCloseView(compensationInfoVO.getCloseView());
                    }
                    if (compensationInfoVO.getApplyTime() != null) {
                        compensationInfoOld.setApplyTime(compensationInfoVO.getApplyTime());
                    }
                    if (compensationInfoVO.getFirstHandleTime() != null) {
                        compensationInfoOld.setFirstHandleTime(compensationInfoVO.getFirstHandleTime());
                    }
                    if (compensationInfoVO.getCloseTime() != null) {
                        compensationInfoOld.setCloseTime(compensationInfoVO.getCloseTime());
                    }
                    if (compensationInfoVO.getApplyName() != null) {
                        compensationInfoOld.setApplyName(compensationInfoVO.getApplyName());
                    }
                    if (compensationInfoVO.getHandleName() != null) {
                        compensationInfoOld.setHandleName(compensationInfoVO.getHandleName());
                    }
                    if (compensationInfoVO.getCloseName() != null) {
                        compensationInfoOld.setCloseName(compensationInfoVO.getCloseName());
                    }
                    if (compensationInfoVO.getCompensationFee() != null) {
                        compensationInfoOld.setCompensationFee(compensationInfoVO.getCompensationFee());
                    }
                    if (compensationInfoVO.getHandleFee() != null) {
                        compensationInfoOld.setHandleFee(compensationInfoVO.getHandleFee());
                    }
                    if (compensationInfoVO.getAccommodationFee() != null) {
                        double accommodationFee = 0.00;
                        if (Operation.CLAIMS_CLOSE.equals(operation)) {
                            accommodationFee = compensationInfoVO.getAccommodationFee();
                        } else if(Operation.ACCOMMODATION_DEAL.equals(operation)){
                            accommodationFee = compensationInfoOld.getAccommodationFee() == null ? 0.00 : compensationInfoOld.getAccommodationFee();
                            if (milestoneInfoVO != null) {
                                Map<String, Object> operationContext = milestoneInfoVO.getOperationContext();
                                if (operationContext != null) {
                                    String accommodationFeeStr = operationContext.get("accommodationFee") == null ? "0.00" : operationContext.get("accommodationFee").toString();
                                    if(StringUtils.isNotEmpty(accommodationFeeStr)){
                                        double value = Double.valueOf(accommodationFeeStr);
                                        accommodationFee += value;
                                    }
                                }
                            }
                        }
                        accommodationFee = new BigDecimal(accommodationFee).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                        compensationInfoOld.setAccommodationFee(accommodationFee);
                    }
                    if (compensationInfoVO.getCloseFee() != null) {
                        compensationInfoOld.setCloseFee(compensationInfoVO.getCloseFee());
                    }
                    if (compensationInfoVO.getCountFee() != null) {
                        compensationInfoOld.setCountFee(compensationInfoVO.getCountFee());
                    }
                    if (compensationInfoVO.getCompensationLetter() != null) {
                        compensationInfoOld.setCompensationLetter(compensationInfoVO.getCompensationLetter());
                    }
                    if (compensationInfoVO.getLostList() != null) {
                        compensationInfoOld.setLostList(compensationInfoVO.getLostList());
                    }
                    if (compensationInfoVO.getSignBill() != null) {
                        compensationInfoOld.setSignBill(compensationInfoVO.getSignBill());
                    }
                    if (compensationInfoVO.getGoodsValueInstruction() != null) {
                        compensationInfoOld.setGoodsValueInstruction(compensationInfoVO.getGoodsValueInstruction());
                    }
                    if (compensationInfoVO.getDamagePicture() != null) {
                        compensationInfoOld.setDamagePicture(compensationInfoVO.getDamagePicture());
                    }
                    //if(compensationInfoVO.getCreateTime() != null){compensationInfoOld.setCreateTime();}
                    //if(compensationInfoVO.getUpdateTime() != null){compensationInfoOld.setUpdateTime();}
                    //if(compensationInfoVO.getDataType() != null){compensationInfoOld.setDataType();}
                    if (compensationInfoVO.getDamageDegree() != null) {
                        compensationInfoOld.setDamageDegree(compensationInfoVO.getDamageDegree());
                    }
                    if (compensationInfoVO.getCompensationStatus() != null) {
                        compensationInfoOld.setCompensationStatus(compensationInfoVO.getCompensationStatus());
                    }
                    if(Operation.CLAIMS_IMPORT.equals(operation) || Operation.COMPENSATION_IMPORT.equals(operation)){
                        compensationInfoOld.setDataType(DataType.IMPORT_DATA.getCode());
                    }
                    compensationInfoExtMapper.updateByPrimaryKeySelective(compensationInfoOld);
                }
            }
        }
        log.info("调用" + operationMessage + "接口结束");
    }

    /**
     * 公共方法, 更改提货信息
     *
     * @param operation
     * @param ts
     * @param id
     * @param operationContext
     */
    private void updateWaybillForPickUpInfo(Operation operation, Timestamp ts, String id, Map<String, Object> operationContext) {
        WaybillInfo waybillInfo = waybillInfoExtMapper.selectByPrimaryKey(id);
        if (waybillInfo != null) {
            String arrivalArea = "";
            String arrivalAddress = "";
            String pickUpGoodsPhone = "";
            String pickUpGoodsPassword = "";
            String logisticsBillId = "";
            boolean other = false;
            switch (operation) {
                case TRUNK_END:
                    arrivalArea = operationContext.get("arrivalArea") == null ? "" : operationContext.get("arrivalArea").toString();
                    arrivalAddress = operationContext.get("arrivalAddress") == null ? "" : operationContext.get("arrivalAddress").toString();
                    pickUpGoodsPhone = operationContext.get("pickUpGoodsPhone") == null ? "" : operationContext.get("pickUpGoodsPhone").toString();
                    pickUpGoodsPassword = operationContext.get("pickUpGoodsPassword") == null ? "" : operationContext.get("pickUpGoodsPassword").toString();
                    logisticsBillId = operationContext.get("logisticsBillId") == null ? "" : operationContext.get("logisticsBillId").toString();
                    break;
                case TRUNK_END_CANCEL:
                    break;
                case OUT_DELIVER_CANCEL:
                    break;
                case OUT_DELIVER_REMOVE:
                    break;
                case PICK_MODIFY:
                    arrivalArea = operationContext.get("arrivalArea") == null ? "" : operationContext.get("arrivalArea").toString();
                    arrivalAddress = operationContext.get("arrivalAddress") == null ? "" : operationContext.get("arrivalAddress").toString();
                    pickUpGoodsPhone = operationContext.get("pickUpGoodsPhone") == null ? "" : operationContext.get("pickUpGoodsPhone").toString();
                    pickUpGoodsPassword = operationContext.get("pickUpGoodsPassword") == null ? "" : operationContext.get("pickUpGoodsPassword").toString();
                    logisticsBillId = operationContext.get("logisticsBillId") == null ? "" : operationContext.get("logisticsBillId").toString();
                    break;
                case CARRIER_MODIFY_FEES:
                    logisticsBillId = operationContext.get("logisticsBillId") == null ? "" : operationContext.get("logisticsBillId").toString();
                    other = true;
                    break;
                case CARRIER_ACCEPT:
                    logisticsBillId = operationContext.get("carrierBillId") == null ? "" : operationContext.get("carrierBillId").toString();
                    other = true;
                    break;
                case CARRIER_ACCEPT_CANCEL:
                    other = true;
                    break;
                default:
                    break;
            }
            if (other) {
                waybillInfo.setLogisticsBillId(logisticsBillId);
                waybillInfo.setUpdateTime(ts);
                waybillInfoExtMapper.updateByPrimaryKeySelective(waybillInfo);
            } else {
                waybillInfo.setArrivalArea(arrivalArea);
                waybillInfo.setArrivalAddress(arrivalAddress);
                waybillInfo.setPickUpGoodsPhone(pickUpGoodsPhone);
                waybillInfo.setPickUpGoodsPassword(pickUpGoodsPassword);
                waybillInfo.setLogisticsBillId(logisticsBillId);
                waybillInfo.setUpdateTime(ts);
                waybillInfoExtMapper.updateByPrimaryKey(waybillInfo);
            }
        }
    }

    /**
     * 新增或者更新承运商
     *
     * @param operationContext
     * @param waybillId
     * @param ts
     * @param operation
     */
    private void insertOrUpdateCarrierInfo(Map<String, Object> operationContext, String waybillId, Timestamp ts, Operation operation, Timestamp operateTime) throws Exception {
        int carrierType = 0;
        if (operation.equals(Operation.OUT_DELIVER) || operation.equals(Operation.OUT_DELIVER_REMOVE)
                || operation.equals(Operation.OUT_DELIVER_COMFIRM) || operation.equals(Operation.OUT_DELIVER_CANCEL)
                || operation.equals(Operation.CARRIER_ACCEPT) || operation.equals(Operation.CARRIER_ACCEPT_CANCEL)
                || operation.equals(Operation.CARRIER_TRANSFER) || operation.equals(Operation.CARRIER_TRANSFER_CANCEL)
                || operation.equals(Operation.CARRIER_DEPARTURE) || operation.equals(Operation.CARRIER_DEPARTURE_CANCEL)
                || operation.equals(Operation.ARRIVAL_DESTINATION) || operation.equals(Operation.ARRIVAL_DESTINATION_CANCEL)
                || operation.equals(Operation.CARRIER_MODIFY)) {
            carrierType = CarrierType.CARRIER.getCode();
        }
        if (operation.equals(Operation.DIRECT_TRANSFER) || operation.equals(Operation.DIRECT_TRANSFER_REMOVE)
                || operation.equals(Operation.TRUNK_END) || operation.equals(Operation.TRUNK_END_CANCEL)) {
            carrierType = CarrierType.DIRECT_CARRIER.getCode();
        }
        CarrierInfo carrierInfo = carrierInfoExtMapper.selectByWaybillIdAndStatus(waybillId, null, carrierType);
        boolean flag = true;
        if (null == carrierInfo) {
            flag = false;
            carrierInfo = new CarrierInfo();
            carrierInfo.setId(OID.get().toString());
            carrierInfo.setWaybillId(waybillId);
            carrierInfo.setStatus(DeleteStatus.ENABLE.getCode());
            carrierInfo.setCreateTime(ts);
            carrierInfo.setUpdateTime(ts);
            carrierInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
        }
        if (flag && operation.equals(Operation.WAREHOUSING_CANCEL)) {
            //获取相关值
            String id = carrierInfo.getId();
            carrierInfo = new CarrierInfo();
            carrierInfo.setId(id);
            carrierInfo.setWaybillId(waybillId);
            carrierInfo.setStatus(DeleteStatus.DISABLE.getCode());
            carrierInfo.setCreateTime(ts);
            carrierInfo.setUpdateTime(ts);
            carrierInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
            carrierInfoExtMapper.updateByPrimaryKey(carrierInfo);
        } else if (!flag && (operation.equals(Operation.TRUNK_END) || operation.equals(Operation.TRUNK_END_CANCEL) || operation.equals(Operation.WAREHOUSING_CANCEL))) {
            //干结与撤销干结只会影响到直营中转的承运商，如果这两个节点没有查询到承运商，则不做任何操作
            //取消入库, 新建外发还没有确认外发时撤销将承运商逻辑删除，如果这两个节点没有查询到承运商，则不做任何操作
        } else {
            //获取相关值
            String id = carrierInfo.getId();
            switch (operation) {
                case OUT_DELIVER:
                    if (operationContext.get("outDeliverRelayId") != null) {
                        carrierInfo.setOutDeliverRelayid(operationContext.get("outDeliverRelayId").toString());
                    }
                    if (operationContext.get("carrier") != null) {
                        carrierInfo.setCarrier(operationContext.get("carrier").toString());
                    }
                    if (operationContext.get("carrierCode") != null) {
                        carrierInfo.setCarrierCode(operationContext.get("carrierCode").toString());
                    }
                    if (operationContext.get("pickUpMethod") != null) {
                        carrierInfo.setPickUpMethod(operationContext.get("pickUpMethod").toString());
                    }
                    if (operationContext.get("carrierReceiver") != null) {
                        carrierInfo.setCarrierReceiver(operationContext.get("carrierReceiver").toString());
                    }
                    if (operationContext.get("carrierReceiverPhone") != null) {
                        carrierInfo.setCarrierReceiverPhone(operationContext.get("carrierReceiverPhone").toString());
                    }
                    if (operationContext.get("areaId") != null) {
                        carrierInfo.setAreaId(operationContext.get("areaId").toString());
                    }
                    if (operationContext.get("carrierAddress") != null) {
                        carrierInfo.setCarrierAddress(operationContext.get("carrierAddress").toString());
                    }
                    if (operationContext.get("carrierCompanyName") != null) {
                        carrierInfo.setCarrierCompanyName(operationContext.get("carrierCompanyName").toString());
                    }
                    if (operationContext.get("startSentArea") != null) {
                        carrierInfo.setStartSentArea(operationContext.get("startSentArea").toString());
                    }
                    if (operationContext.get("carrierDestination") != null) {
                        carrierInfo.setCarrierDestination(operationContext.get("carrierDestination").toString());
                    }
                    if (operationContext.get("carrierSettlementType") != null) {
                        carrierInfo.setCarrierSettlementType(operationContext.get("carrierSettlementType").toString());
                    }
                    carrierInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    break;
                case OUT_DELIVER_CANCEL:
                    carrierInfo = new CarrierInfo();
                    carrierInfo.setId(id);
                    carrierInfo.setWaybillId(waybillId);
                    carrierInfo.setStatus(DeleteStatus.DISABLE.getCode());//取消确认外发, 把该记录删除
                    carrierInfo.setCreateTime(ts);
                    carrierInfo.setUpdateTime(ts);
                    carrierInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
                    break;
                case OUT_DELIVER_REMOVE:
                    carrierInfo = new CarrierInfo();
                    carrierInfo.setId(id);
                    carrierInfo.setWaybillId(waybillId);
                    carrierInfo.setStatus(DeleteStatus.DISABLE.getCode());//取消外发交接, 把该记录删除
                    carrierInfo.setCreateTime(ts);
                    carrierInfo.setUpdateTime(ts);
                    carrierInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
                    break;
                case CARRIER_MODIFY:
                    if (operationContext.get("outDeliverRelayId") != null) {
                        carrierInfo.setOutDeliverRelayid(operationContext.get("outDeliverRelayId").toString());
                    }
                    if (operationContext.get("carrier") != null) {
                        carrierInfo.setCarrier(operationContext.get("carrier").toString());
                    }
                    if (operationContext.get("carrierCode") != null) {
                        carrierInfo.setCarrierCode(operationContext.get("carrierCode").toString());
                    }
                    carrierInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    break;
                case CARRIER_ACCEPT:
                    if (operationContext.get("carrierBillId") != null) {
                        carrierInfo.setCarrierBillId(operationContext.get("carrierBillId").toString());
                    }
                    carrierInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    break;
                case CARRIER_ACCEPT_CANCEL:
                    carrierInfo.setCarrierBillId(null);
                    carrierInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    break;
                case CARRIER_TRANSFER:
                    if (operationContext.get("transferBillId") != null) {
                        carrierInfo.setTransferBillId(operationContext.get("transferBillId").toString());
                    }
                    carrierInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    break;
                case CARRIER_TRANSFER_CANCEL:
                    carrierInfo.setTransferBillId(null);
                    carrierInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    break;
                case CARRIER_DEPARTURE:
                    carrierInfo.setCarrierDepartureTime(operateTime);
                    if (operationContext.get("route") != null) {
                        carrierInfo.setCarrierDepartureLine(operationContext.get("route").toString());
                    }
                    carrierInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    break;
                case CARRIER_DEPARTURE_CANCEL:
                    carrierInfo.setCarrierDepartureTime(null);
                    carrierInfo.setCarrierDepartureLine(null);
                    carrierInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    break;
                case ARRIVAL_DESTINATION:
                    carrierInfo.setActualArrivalTime(operateTime);
                    carrierInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    break;
                case ARRIVAL_DESTINATION_CANCEL:
                    carrierInfo.setActualArrivalTime(null);
                    carrierInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    break;
                case DIRECT_TRANSFER:
                    if (operationContext.get("outDeliverRelayId") != null) {
                        carrierInfo.setOutDeliverRelayid(operationContext.get("outDeliverRelayId").toString());
                    }
                    if (operationContext.get("carrier") != null) {
                        carrierInfo.setCarrier(operationContext.get("carrier").toString());
                    }
                    if (operationContext.get("carrierCode") != null) {
                        carrierInfo.setCarrierCode(operationContext.get("carrierCode").toString());
                    }
                    if (operationContext.get("pickUpMethod") != null) {
                        carrierInfo.setPickUpMethod(operationContext.get("pickUpMethod").toString());
                    }
                    if (operationContext.get("carrierReceiver") != null) {
                        carrierInfo.setCarrierReceiver(operationContext.get("carrierReceiver").toString());
                    }
                    if (operationContext.get("carrierReceiverPhone") != null) {
                        carrierInfo.setCarrierReceiverPhone(operationContext.get("carrierReceiverPhone").toString());
                    }
                    if (operationContext.get("areaId") != null) {
                        carrierInfo.setAreaId(operationContext.get("areaId").toString());
                    }
                    if (operationContext.get("carrierAddress") != null) {
                        carrierInfo.setCarrierAddress(operationContext.get("carrierAddress").toString());
                    }
                    if (operationContext.get("carrierCompanyName") != null) {
                        carrierInfo.setCarrierCompanyName(operationContext.get("carrierCompanyName").toString());
                    }
                    if (operationContext.get("startSentArea") != null) {
                        carrierInfo.setStartSentArea(operationContext.get("startSentArea").toString());
                    }
                    if (operationContext.get("carrierDestination") != null) {
                        carrierInfo.setCarrierDestination(operationContext.get("carrierDestination").toString());
                    }
                    if (operationContext.get("carrierSettlementType") != null) {
                        carrierInfo.setCarrierSettlementType(operationContext.get("carrierSettlementType").toString());
                    }
                    carrierInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    break;
                case DIRECT_TRANSFER_REMOVE:
                    carrierInfo = new CarrierInfo();
                    carrierInfo.setId(id);
                    carrierInfo.setWaybillId(waybillId);
                    carrierInfo.setStatus(DeleteStatus.DISABLE.getCode());//取消直营外发, 把该记录删除
                    carrierInfo.setCreateTime(ts);
                    carrierInfo.setUpdateTime(ts);
                    carrierInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
                    break;
                case TRUNK_END://只用于直营中转
                    carrierInfo.setActualArrivalTime(operateTime);//直营中转到达时间取干结时间
                    carrierInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    break;
                case TRUNK_END_CANCEL://只用于直营中转
                    carrierInfo.setActualArrivalTime(null);//直营中转到达时间取干结时间，所以撤销干结的时候置空
                    carrierInfo.setStatus(DeleteStatus.DISABLE.getCode());//取消干结, 把该记录删除
                    break;
                default:
                    break;
            }

            if (carrierType == 0) {
                log.info("carrierType=0");
            } else {
                carrierInfo.setCarrierType(carrierType);
            }
            if (flag) {
                carrierInfoExtMapper.updateByPrimaryKey(carrierInfo);
            } else {
                carrierInfoExtMapper.insert(carrierInfo);
            }
        }
    }

    /**
     * 新增或者更新服务商（包括网点/师傅）
     *
     * @param operationContext
     * @param waybillId
     * @param ts
     * @param operation
     * @param operateTime
     */
    private void insertOrUpdateMasterInfo(Map<String, Object> operationContext, String waybillId, Timestamp ts, Operation operation, Timestamp operateTime) throws Exception {
        int distributionType = 0;
        if (operation.equals(Operation.HEADQUARTERS_DISTRIBUTION) || operation.equals(Operation.HEADQUARTERS_DISTRIBUTION_CANCEL)
                || operation.equals(Operation.HEADQUARTERS_APPOINTMENT) || operation.equals(Operation.HEADQUARTERS_APPOINTMENT_CANCEL)
                || operation.equals(Operation.HEADQUARTERS_SIGN) || operation.equals(Operation.HEADQUARTERS_SIGN_CANCEL)
                || operation.equals(Operation.TRUNK_END) || operation.equals(Operation.TRUNK_END_CANCEL)) {
            distributionType = DistributionType.HEADQUARTERS_DISTRIBUTION.getCode();
        }
        if (operation.equals(Operation.DIRECT_OR_OUTLET_DISTRIBUTION) || operation.equals(Operation.DIRECT_OR_OUTLET_DISTRIBUTION_CANCEL)
                || operation.equals(Operation.DIRECT_OR_OUTLET_APPOINTMENT) || operation.equals(Operation.DIRECT_OR_OUTLET_APPOINTMENT_CANCEL)
                || operation.equals(Operation.DIRECT_OR_OUTLET_SIGN) || operation.equals(Operation.DIRECT_OR_OUTLET_SIGN_CANCEL)) {
            distributionType = DistributionType.DIRECT_OR_OUTLET_DISTRIBUTION.getCode();
        }
        MasterInfo masterInfo = masterInfoExtMapper.selectByWaybillIdAndStatus(waybillId, distributionType == 0 ? DeleteStatus.ENABLE.getCode() : null, distributionType);
        boolean flag = true;
        if (null == masterInfo) {
            flag = false;
            masterInfo = new MasterInfo();
            masterInfo.setId(OID.get().toString());
            masterInfo.setWaybillId(waybillId);
            masterInfo.setStatus(DeleteStatus.ENABLE.getCode());
            masterInfo.setCreateTime(ts);
            masterInfo.setUpdateTime(ts);
            masterInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
        }
        String id = masterInfo.getId();
        Timestamp trunkEndTime = masterInfo.getTrunkEndTime();
        //获取相关值
        if (operation.equals(Operation.HEADQUARTERS_DISTRIBUTION) || operation.equals(Operation.DIRECT_OR_OUTLET_DISTRIBUTION)) {
            if (operationContext.get("masterName") != null) {
                masterInfo.setMasterName(operationContext.get("masterName").toString());
            }
            if (operationContext.get("masterNode") != null) {
                masterInfo.setMasterNode(operationContext.get("masterNode").toString());
            }
            if (operationContext.get("masterPhone") != null) {
                masterInfo.setMasterPhone(operationContext.get("masterPhone").toString());
            }
            if (operationContext.get("masterType") != null) {
                MasterType masterType = MasterType.getEnumByEnumNameString(operationContext.get("masterType").toString());
                if (masterType != null) {
                    masterInfo.setMasterType(masterType.getCode());
                } else {
                    masterInfo.setMasterType(null);
                }
            }
            if (operationContext.get("masterCode") != null) {
                masterInfo.setMasterCode(operationContext.get("masterCode").toString());
            }
            if (operationContext.get("nodeCode") != null) {
                masterInfo.setNodeCode(operationContext.get("nodeCode").toString());
            }
            if (operationContext.get("nodeType") != null) {
                String nodeTypeStr = operationContext.get("nodeType").toString();
                NodeType nodeType = NodeType.getEnumByEnumNameString(nodeTypeStr);
                if (nodeType != null) {
                    masterInfo.setNodeType(nodeType.getCode());
                } else {
                    masterInfo.setNodeType(null);
                }
            }
            if (operationContext.get("nodeDutyName") != null) {
                masterInfo.setNodeDutyName(operationContext.get("nodeDutyName").toString());
            }
            masterInfo.setActualDistributeTime(operateTime);
            masterInfo.setStatus(DeleteStatus.ENABLE.getCode());
            //masterInfo.setDistributionType(distributionType);
            if (operation.equals(Operation.DIRECT_OR_OUTLET_DISTRIBUTION)) {//网点或者直营分配时需要将干结时间取过来
                MasterInfo masterInfoOld = masterInfoExtMapper.selectByWaybillIdAndStatus(waybillId, null, DistributionType.HEADQUARTERS_DISTRIBUTION.getCode());
                if(masterInfoOld != null){
                    Timestamp trunkEndTimeOld = masterInfoOld.getTrunkEndTime();
                    masterInfo.setTrunkEndTime(trunkEndTimeOld);
                }
            }
        } else if (operation.equals(Operation.HEADQUARTERS_DISTRIBUTION_CANCEL)) {
            //取消总部分配,只保留运单号、干线结束时间
            masterInfo = new MasterInfo();
            masterInfo.setId(id);
            masterInfo.setWaybillId(waybillId);
            masterInfo.setStatus(DeleteStatus.ENABLE.getCode());
            masterInfo.setCreateTime(ts);
            masterInfo.setUpdateTime(ts);
            masterInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
            masterInfo.setTrunkEndTime(trunkEndTime);
            //masterInfo.setDistributionType(distributionType);
            //如果直接在 ips 取消（总部）分配，没有传取消网点分配节点
            //insertOrUpdateMasterInfo(operationContext, waybillId, ts, Operation.DIRECT_OR_OUTLET_DISTRIBUTION_CANCEL, operateTime);
            //取消网点分配，并不会把网点预约取消，网点预约在网点分配之前
            MasterInfo masterInfoOld = masterInfoExtMapper.selectByWaybillIdAndStatus(waybillId, null, DistributionType.DIRECT_OR_OUTLET_DISTRIBUTION.getCode());
            if(masterInfoOld != null) {
                //masterInfoOld.setId();
                //masterInfoOld.setWaybillId();
                masterInfoOld.setMasterName(null);
                masterInfoOld.setMasterNode(null);
                masterInfoOld.setMasterPhone(null);
                masterInfoOld.setMasterType(null);
                masterInfoOld.setMasterCode(null);
                masterInfoOld.setStatus(DeleteStatus.DISABLE.getCode());
                masterInfoOld.setLimitAppointmentTime(null);
                masterInfoOld.setActualAppoinmentTime(null);
                masterInfoOld.setLimitDoorTime(null);
                masterInfoOld.setActualDoorTime(null);
                masterInfoOld.setLimitSignTime(null);
                masterInfoOld.setActualSignTime(null);
                //masterInfoOld.setCreateTime();
                masterInfoOld.setUpdateTime(ts);
                masterInfoOld.setActualDistributeTime(null);
                masterInfoOld.setLimitDistributeTime(null);
                //masterInfoOld.setDataType();
                masterInfoOld.setNodeCode(null);
                masterInfoOld.setNodeType(null);
                masterInfoOld.setNodeDutyName(null);
                masterInfoOld.setTrunkEndTime(null);
                //masterInfoOld.setdistributionType();
                masterInfoExtMapper.updateByPrimaryKey(masterInfoOld);
            }
        } else if (operation.equals(Operation.DIRECT_OR_OUTLET_DISTRIBUTION_CANCEL)) {
            //取消分配,只保留运单号、干线结束时间、预约时间
            masterInfo = new MasterInfo();
            masterInfo.setId(id);
            masterInfo.setWaybillId(waybillId);
            masterInfo.setStatus(DeleteStatus.DISABLE.getCode());
            masterInfo.setCreateTime(ts);
            masterInfo.setUpdateTime(ts);
            masterInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
            masterInfo.setTrunkEndTime(trunkEndTime);
            MasterInfo masterInfoOld = masterInfoExtMapper.selectByWaybillIdAndStatus(waybillId, null, DistributionType.DIRECT_OR_OUTLET_DISTRIBUTION.getCode());
            if(masterInfoOld != null) {
                Timestamp actualAppoinmentTime = masterInfoOld.getActualAppoinmentTime();
                masterInfo.setActualAppoinmentTime(actualAppoinmentTime);
            }
            masterInfo.setActualDistributeTime(null);
            //masterInfo.setDistributionType(distributionType);
        } else if (operation.equals(Operation.HEADQUARTERS_APPOINTMENT) || operation.equals(Operation.DIRECT_OR_OUTLET_APPOINTMENT) || operation.equals(Operation.MASTER_APPOINTMENT)) {
            masterInfo.setActualAppoinmentTime(operateTime);
            masterInfo.setStatus(DeleteStatus.ENABLE.getCode());
            //masterInfo.setDistributionType(distributionType);
        } else if (operation.equals(Operation.HEADQUARTERS_APPOINTMENT_CANCEL) || operation.equals(Operation.DIRECT_OR_OUTLET_APPOINTMENT_CANCEL) || operation.equals(Operation.MASTER_APPOINTMENT_CANCEL)) {
            masterInfo.setActualAppoinmentTime(null);
            masterInfo.setStatus(DeleteStatus.ENABLE.getCode());
            //masterInfo.setDistributionType(distributionType);
        } else if (operation.equals(Operation.MASTER_HOME_CHECK)) {
            masterInfo.setActualDoorTime(operateTime);
            masterInfo.setStatus(DeleteStatus.ENABLE.getCode());
            //masterInfo.setDistributionType(distributionType);
        } else if (operation.equals(Operation.MASTER_HOME_CHECK_CANCEL)) {
            masterInfo.setActualDoorTime(null);
            masterInfo.setStatus(DeleteStatus.ENABLE.getCode());
            masterInfo.setDistributionType(distributionType);
        } else if (operation.equals(Operation.TRUNK_END)) {
            masterInfo.setTrunkEndTime(operateTime);
            masterInfo.setStatus(DeleteStatus.ENABLE.getCode());
            //masterInfo.setDistributionType(DistributionType.HEADQUARTERS_DISTRIBUTION.getCode());
        } else if (operation.equals(Operation.TRUNK_END_CANCEL)) {
            masterInfo.setTrunkEndTime(null);
            masterInfo.setStatus(DeleteStatus.DISABLE.getCode());//取消干结, 把该记录删除
            //masterInfo.setDistributionType(distributionType);
        } else if (operation.equals(Operation.HEADQUARTERS_SIGN) || operation.equals(Operation.DIRECT_OR_OUTLET_SIGN) || operation.equals(Operation.MASTER_SIGN)) {
            masterInfo.setActualSignTime(operateTime);
            masterInfo.setStatus(DeleteStatus.ENABLE.getCode());
            //masterInfo.setDistributionType(distributionType);
        } else if (operation.equals(Operation.HEADQUARTERS_SIGN_CANCEL) || operation.equals(Operation.DIRECT_OR_OUTLET_SIGN_CANCEL) || operation.equals(Operation.MASTER_SIGN_CANCEL)) {
            masterInfo.setActualSignTime(null);
            masterInfo.setStatus(DeleteStatus.ENABLE.getCode());
            //masterInfo.setDistributionType(distributionType);
        }

        if (distributionType == 0) {
            log.info("distributionType=0");
        } else {
            masterInfo.setDistributionType(distributionType);
        }
        if (flag) {
            masterInfoExtMapper.updateByPrimaryKey(masterInfo);
        } else {
            masterInfoExtMapper.insert(masterInfo);
        }
    }


    public AfterSaleInfo getAfterSaleInfo(String waybillId, Timestamp ts, OldExceptionInfoVO oldExceptionInfoVo, AfterSaleInfoVO afterSaleInfoVo) {
        AfterSaleInfo afterSaleInfo = new AfterSaleInfo();
        Beans.from(afterSaleInfoVo).to(afterSaleInfo);
        afterSaleInfo.setId(OID.get().toString());
        afterSaleInfo.setWaybillId(waybillId);
        afterSaleInfo.setExceptionCode(oldExceptionInfoVo.getExceptionCode());
        afterSaleInfo.setStatus(DeleteStatus.ENABLE.getCode());
        afterSaleInfo.setCreateTime(ts);
        afterSaleInfo.setUpdateTime(ts);
        afterSaleInfo.setDataType(DataType.HISTORICAL_DATA.getCode());
        return afterSaleInfo;
    }

    public CompensationInfo getCompensationInfo(String waybillId, Timestamp ts, CompensationInfoVO compensationInfoVO) {
        CompensationInfo compensationInfo = new CompensationInfo();
        Beans.from(compensationInfoVO).to(compensationInfo);
        compensationInfo.setId(OID.get().toString());
        compensationInfo.setWaybillId(waybillId);
        compensationInfo.setStatus(DeleteStatus.ENABLE.getCode());
        compensationInfo.setCreateTime(ts);
        compensationInfo.setUpdateTime(ts);
        compensationInfo.setDataType(DataType.HISTORICAL_DATA.getCode());
        return compensationInfo;
    }

    public ExceptionInfo getExceptionInfo(String waybillId, Timestamp ts, OldExceptionInfoVO oldExceptionInfoVo) {

        ExceptionInfo exceptionInfo = new ExceptionInfo();
        Beans.from(oldExceptionInfoVo).to(exceptionInfo);
        exceptionInfo.setId(OID.get().toString());

        /*//异常大类不能为空
        if (null == oldExceptionInfoVo.getExceptionLargeType()) {
            throw new RuntimeException(oldmsg + "异常大类不能为空!");
        }

        //异常小类不能为空
        if (null == oldExceptionInfoVo.getExceptionSubType()) {
            throw new RuntimeException(oldmsg + "异常小类不能为空!");
        }
        //异常处理状态不能为空
        if (null == ExceptionStatus.getEnumByEnumNameString(oldExceptionInfoVo.getExceptionStatus())) {
            throw new RuntimeException(oldmsg + "ExceptionStatus 找不到对应枚举");
        }
        //exceptionInfo.setExceptionStatus(ExceptionStatus.getEnumByEnumNameString(oldExceptionInfoVo.getExceptionStatus()).getCode());
        exceptionInfo.setExceptionStatus(oldExceptionInfoVo.getExceptionStatus());*/

        exceptionInfo.setStatus(DeleteStatus.ENABLE.getCode());
        exceptionInfo.setWaybillId(waybillId);
        exceptionInfo.setCreateTime(ts);
        exceptionInfo.setUpdateTime(ts);
        exceptionInfo.setDataType(DataType.HISTORICAL_DATA.getCode());
        return exceptionInfo;
    }

    public GoodsInfo getGoodsInfo(String waybillId, Timestamp ts, GoodsInfoVO goodsInfoVO) {
        GoodsInfo goodsInfo = new GoodsInfo();
        //对象copy，将后一个对象中属性的值赋予给前一个
        Beans.from(goodsInfoVO).to(goodsInfo);
        goodsInfo.setId(OID.get().toString());
        goodsInfo.setStatus(DeleteStatus.ENABLE.getCode());
        goodsInfo.setWaybillId(waybillId);
        goodsInfo.setCreateTime(ts);
        goodsInfo.setUpdateTime(ts);
        goodsInfo.setDataType(DataType.HISTORICAL_DATA.getCode());
        Packing packing = Packing.getEnumByEnumNameString(goodsInfoVO.getPacking());
        if (null != packing) {
            goodsInfo.setPacking(packing.getCode());
        }
        return goodsInfo;
    }

    public BillRelationInfo getBillRelationInfo(String waybillId, Timestamp ts, String customerBillId, String obId) {
        BillRelationInfo billRelationInfo = new BillRelationInfo();
        //Beans.from(billRelationInfo, billRelationInfoVO);
        billRelationInfo.setId(OID.get().toString());
        billRelationInfo.setWaybillId(waybillId);
        if (StringUtils.isNoneBlank(customerBillId)) {
            billRelationInfo.setCustomerBillId(customerBillId);
        }
        billRelationInfo.setOrderBillId(obId);
        billRelationInfo.setStatus(DeleteStatus.ENABLE.getCode());
        billRelationInfo.setCreateTime(ts);
        billRelationInfo.setUpdateTime(ts);
        billRelationInfo.setDataType(DataType.HISTORICAL_DATA.getCode());
        return billRelationInfo;
    }

    public CarrierInfo getCarrierInfo(String waybillId, Timestamp ts, CarrierInfoVO carrierInfoVO) {
        CarrierInfo carrierInfo = new CarrierInfo();
        Beans.from(carrierInfoVO).to(carrierInfo);
        carrierInfo.setId(OID.get().toString());
        carrierInfo.setWaybillId(waybillId);

        if (null == CarrierType.getEnumByEnumNameString(carrierInfoVO.getCarrierType())) {
            throw new RuntimeException(oldmsg + "CarrierType 找不到对应枚举");
        }
        carrierInfo.setCarrierType(CarrierType.getEnumByEnumNameString(carrierInfoVO.getCarrierType()).getCode());
        carrierInfo.setStatus(DeleteStatus.ENABLE.getCode());
        carrierInfo.setCreateTime(ts);
        carrierInfo.setUpdateTime(ts);
        carrierInfo.setDataType(DataType.HISTORICAL_DATA.getCode());
        return carrierInfo;
    }

    public MasterInfo getMasterInfo(String waybillId, Timestamp ts, MasterInfoVO masterInfoVO) {
        MasterInfo masterInfo = new MasterInfo();
        Beans.from(masterInfoVO).to(masterInfo);
        masterInfo.setId(OID.get().toString());
        masterInfo.setWaybillId(waybillId);
        if (null != MasterType.getEnumByEnumNameString(masterInfoVO.getMasterType())) {
            masterInfo.setMasterType(MasterType.getEnumByEnumNameString(masterInfoVO.getMasterType()).getCode());
        }
        /*if (null == MasterType.getEnumByEnumNameString(masterInfoVO.getMasterType())) {
            throw new RuntimeException(oldmsg + "MasterType 找不到对应枚举");
        }
        masterInfo.setMasterType(MasterType.getEnumByEnumNameString(masterInfoVO.getMasterType()).getCode());*/
        if (null == DistributionType.getEnumByEnumNameString(masterInfoVO.getDistributionType())) {
            throw new RuntimeException(oldmsg + "DistributionType 找不到对应枚举");
        }
        masterInfo.setDistributionType(DistributionType.getEnumByEnumNameString(masterInfoVO.getDistributionType()).getCode());
        if (null != NodeType.getEnumByEnumNameString(masterInfoVO.getNodeType())) {
            masterInfo.setNodeType(NodeType.getEnumByEnumNameString(masterInfoVO.getNodeType()).getCode());
        }
        masterInfo.setStatus(DeleteStatus.ENABLE.getCode());
        masterInfo.setCreateTime(ts);
        masterInfo.setUpdateTime(ts);
        masterInfo.setDataType(DataType.HISTORICAL_DATA.getCode());
        masterInfo.setActualDistributeTime(masterInfoVO.getDistributeTime());
        return masterInfo;
    }

    public ReceviceInfo getReceviceInfo(String waybillId, Timestamp ts, ReceviceInfoVO receviceInfoVO) {
        ReceviceInfo receviceInfo = new ReceviceInfo();
        Beans.from(receviceInfoVO).to(receviceInfo);
        receviceInfo.setId(OID.get().toString());
        receviceInfo.setWaybillId(waybillId);
        receviceInfo.setStatus(DeleteStatus.ENABLE.getCode());
        receviceInfo.setCreateTime(ts);
        receviceInfo.setUpdateTime(ts);
        receviceInfo.setDataType(DataType.HISTORICAL_DATA.getCode());
        return receviceInfo;
    }

    public ConsignorInfo getConsignorInfo(String waybillId, Timestamp ts, ConsignorInfoVO consignorInfoVO) {
        ConsignorInfo consignorInfo = new ConsignorInfo();
        Beans.from(consignorInfoVO).to(consignorInfo);
        consignorInfo.setId(OID.get().toString());
        if (null == CustomerType.getEnumByEnumNameString(consignorInfoVO.getCustomerType())) {
            throw new RuntimeException(oldmsg + "CustomerType 找不到对应枚举");
        }
        consignorInfo.setCustomerType(CustomerType.getEnumByEnumNameString(consignorInfoVO.getCustomerType()).getCode());
        if (null != ConsignorType.getEnumByEnumNameString(consignorInfoVO.getConsignorType())) {
            consignorInfo.setConsignorType(ConsignorType.getEnumByEnumNameString(consignorInfoVO.getConsignorType()).getCode());
        }
        consignorInfo.setWaybillId(waybillId);
        consignorInfo.setStatus(DeleteStatus.ENABLE.getCode());
        consignorInfo.setCreateTime(ts);
        consignorInfo.setUpdateTime(ts);
        consignorInfo.setDataType(DataType.HISTORICAL_DATA.getCode());
        return consignorInfo;
    }

    public WaybillInfo getWaybillInfo(String waybillId, Timestamp ts, WaybillInfoVO waybillInfoVO) {
        WaybillInfo waybillInfo = new WaybillInfo();
        Beans.from(waybillInfoVO).to(waybillInfo);
        waybillInfo.setId(waybillId);
        checkWaybillInfo(waybillInfoVO, waybillInfo);
        waybillInfo.setCreateTime(ts);
        waybillInfo.setUpdateTime(ts);
        waybillInfo.setDataType(DataType.HISTORICAL_DATA.getCode());
        return waybillInfo;
    }

    public void checkWaybillInfo(WaybillInfoVO waybillInfoVO, WaybillInfo waybillInfo) {
        //运单类型
        if (null == WaybillType.getEnumByEnumNameString(waybillInfoVO.getWaybillType())) {
            throw new RuntimeException(oldmsg + "WaybillType 找不到对应枚举");
        }
        waybillInfo.setWaybillType(WaybillType.getEnumByEnumNameString(waybillInfoVO.getWaybillType()).getCode());
        //产品时效类型
        if (null == ProductType.getEnumByEnumNameString(waybillInfoVO.getProductType())) {
            throw new RuntimeException(oldmsg + "ProductType 找不到对应枚举");
        }
        waybillInfo.setProductType(ProductType.getEnumByEnumNameString(waybillInfoVO.getProductType()).getCode());


        //核销类型（天猫，京东）
        if (null != CheckType.getEnumByEnumNameString(waybillInfoVO.getCheckType())) {
            //throw new RuntimeException(oldmsg + "CheckType 找不到对应枚举");
            waybillInfo.setCheckType(CheckType.getEnumByEnumNameString(waybillInfoVO.getCheckType()).getCode());
        }
        //核销状态
        if (null != CheckStatus.getEnumByEnumNameString(waybillInfoVO.getCheckStatus())) {
            //throw new RuntimeException(oldmsg + "CheckStatus 找不到对应枚举");
            waybillInfo.setCheckStatus(CheckStatus.getEnumByEnumNameString(waybillInfoVO.getCheckStatus()).getCode());
        }
        //核销方式
        if (null != CheckMethod.getEnumByEnumNameString(waybillInfoVO.getCheckMethod())) {
            //throw new RuntimeException(oldmsg + "CheckMethod 找不到对应枚举");
            waybillInfo.setCheckMethod(CheckMethod.getEnumByEnumNameString(waybillInfoVO.getCheckMethod()).getCode());
        }


        //渠道来源
        if (null == ChannelSource.getEnumByEnumNameString(waybillInfoVO.getChannelSource())) {
            throw new RuntimeException(oldmsg + "ChannelSource 找不到对应枚举");
        }
        waybillInfo.setChannelSource(ChannelSource.getEnumByEnumNameString(waybillInfoVO.getChannelSource()).getCode());
        //付款方式
        if (null == PaymentType.getEnumByEnumNameString(waybillInfoVO.getPaymentType())) {
            throw new RuntimeException(oldmsg + "PaymentType 找不到对应枚举");
        }
        waybillInfo.setPaymentType(PaymentType.getEnumByEnumNameString(waybillInfoVO.getPaymentType()).getCode());
        //结算方式
        if (null == SettlementType.getEnumByEnumNameString(waybillInfoVO.getSettlementType())) {
            throw new RuntimeException(oldmsg + "SettlementType 找不到对应枚举");
        }
        waybillInfo.setSettlementType(SettlementType.getEnumByEnumNameString(waybillInfoVO.getSettlementType()).getCode());
        //增值服务类型
        if (null != IncrementServiceType.getEnumByEnumNameString(waybillInfoVO.getIncrementServiceType())) {
            //throw new RuntimeException(oldmsg + "IncrementServiceType 找不到对应枚举");
            waybillInfo.setIncrementServiceType(IncrementServiceType.getEnumByEnumNameString(waybillInfoVO.getIncrementServiceType()).getCode());
        }
        checkLimitWaybill(waybillInfoVO, waybillInfo);
    }

    public void checkLimitWaybill(WaybillInfoVO waybillInfoVO, WaybillInfo waybillInfo) {

        //服务类型
        if (null == ServiceType.getEnumByEnumNameString(waybillInfoVO.getServiceType())) {
            throw new RuntimeException(oldmsg + "ServiceType 找不到对应枚举");
        }
        waybillInfo.setServiceType(ServiceType.getEnumByEnumNameString(waybillInfoVO.getServiceType()).getCode());
        //运单状态
        if (null == WaybillStatus.getEnumByEnumNameString(waybillInfoVO.getWaybillStatus())) {
            throw new RuntimeException(oldmsg + "WaybillStatus 找不到对应枚举");
        }
        if (WaybillStatus.OBSOLETE.equals(WaybillStatus.getEnumByEnumNameString(waybillInfoVO.getWaybillStatus()))) {
            waybillInfo.setStatus(DeleteStatus.DISABLE.getCode());
        } else {
            waybillInfo.setStatus(DeleteStatus.ENABLE.getCode());
        }
        waybillInfo.setWaybillStatus(WaybillStatus.getEnumByEnumNameString(waybillInfoVO.getWaybillStatus()).getCode());

    }

    public OperationDetail getOperationDetail(String waybillId, Timestamp ts, MilestoneInfo milestoneInfo, Map<String, Object> operationContext, String key) {
        OperationDetail operationDetail = new OperationDetail();
        operationDetail.setId(OID.get().toString());
        operationDetail.setWaybillId(waybillId);
        operationDetail.setMilestoneId(milestoneInfo.getId());
        operationDetail.setField(key);
        operationDetail.setVal(operationContext.get(key) == null ? "" : operationContext.get(key).toString());
        operationDetail.setStatus(DeleteStatus.ENABLE.getCode());
        operationDetail.setCreateTime(ts);
        operationDetail.setUpdateTime(ts);
        operationDetail.setDataType(DataType.HISTORICAL_DATA.getCode());
        return operationDetail;
    }

    public MilestoneInfo getMilestoneInfo(OldDataMessage oldDataMessage, Timestamp ts, MilestoneInfoVO milestoneInfoVo) {
        MilestoneInfo milestoneInfo = new MilestoneInfo();
        Beans.from(milestoneInfoVo).to(milestoneInfo);
        milestoneInfo.setId(OID.get().toString());
        if (null == Operation.getEnumByEnumNameString(milestoneInfoVo.getOperation())) {
            throw new RuntimeException(oldmsg + "Operation 找不到对应枚举");
        }
        milestoneInfo.setOperation(Operation.getEnumByEnumNameString(milestoneInfoVo.getOperation()).getCode());
        if (null == OperationType.getEnumByEnumNameString(milestoneInfoVo.getOperationType())) {
            throw new RuntimeException(oldmsg + "OperationType 找不到对应枚举");
        }
        milestoneInfo.setOperationType(OperationType.getEnumByEnumNameString(milestoneInfoVo.getOperationType()).getCode());
        milestoneInfo.setMessageFrom(oldDataMessage.getMessageFrom());
        milestoneInfo.setWaybillId(oldDataMessage.getWaybillId());
        milestoneInfo.setSentTime(oldDataMessage.getSentTime());
        milestoneInfo.setApiName(oldDataMessage.getApiName());
        milestoneInfo.setStatus(DeleteStatus.ENABLE.getCode());
        milestoneInfo.setCreateTime(ts);
        milestoneInfo.setUpdateTime(ts);
        milestoneInfo.setDataType(DataType.HISTORICAL_DATA.getCode());
        return milestoneInfo;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void deleteOldData(OldDataMessage oldDataMessage) {
        if ("IPS".equals(oldDataMessage.getMessageFrom())) {
            //如果运单号存在，先删除再插入(导入数据时不用手动删除原来的数据，只需给定时间范围或者运单号即可)
            //if (0 < waybillInfoExtMapper.selectCountById(oldDataMessage.getWaybillId())) {
            // 根据waybillId删除after_sale_info数据(售后服务)
            afterSaleInfoExtMapper.deleteByWaybillId(oldDataMessage.getWaybillId());
            // 根据waybillId删除bill_relation_info数据(单号关联表)
            billRelationInfoExtMapper.deleteByWaybillId(oldDataMessage.getWaybillId());
            // 根据waybillId删除consignor_info数据(发货人)
            consignorInfoExtMapper.deleteByWaybillId(oldDataMessage.getWaybillId());
            // 根据waybillId删除exception_info数据(异常信息)
            exceptionInfoExtMapper.deleteByWaybillId(oldDataMessage.getWaybillId());
            // 根据waybillId删除goods_info数据(商品表)
            goodsInfoExtMapper.deleteByWaybillId(oldDataMessage.getWaybillId());
            // 根据waybillId删除master_info数据(服务商-师傅表)
            masterInfoExtMapper.deleteByWaybillId(oldDataMessage.getWaybillId());
            // 根据waybillId+milestoneInfo中messageFrom=IPS的id列表删除operation_detail数据(操作明细表)
            operationDetailExtMapper.deleteByWaybillIdAndMessageFrom(oldDataMessage.getWaybillId(), oldDataMessage.getMessageFrom());
            // 根据waybillId删除recevice_info数据(收货人表)
            receviceInfoExtMapper.deleteByWaybillId(oldDataMessage.getWaybillId());
            // 根据id删除waybill_info数据(运单表)
            waybillInfoExtMapper.deleteByWaybillId(oldDataMessage.getWaybillId());
            // 根据waybillId+messageFrom=IPS删除milestone_info数据(里程碑、操作信息，追踪轨迹表)
            milestoneInfoExtMapper.deleteByWaybillIdAndMessageFrom(oldDataMessage.getWaybillId(), oldDataMessage.getMessageFrom());
            // 赔(追)偿处理
            compensationInfoExtMapper.deleteByWaybillId(oldDataMessage.getWaybillId());
            // 根据no+source=IPS删除sys_exception_info数据(系统异常信息表)
            sysExceptionMapper.deleteByNoAndSource(oldDataMessage.getWaybillId(), oldDataMessage.getMessageFrom());
            //}
        } else if ("CMP".equals(oldDataMessage.getMessageFrom())) {
            // 如果waybillId+messageFrom=CMP在milestone_info有数据或者waybillId在carrier_info有数据，先删除再插入(导入数据时不用手动删除原来的数据，只需给定时间范围或者运单号即可)
            //if (0 < milestoneInfoExtMapper.selectCountByWaybillIdAndMessageFrom(oldDataMessage.getWaybillId(), oldDataMessage.getMessageFrom()) || 0 < carrierInfoExtMapper.selectCountByWaybillId(oldDataMessage.getWaybillId())) {
            // 根据waybillId删除carrier_info数据(承运商)
            carrierInfoExtMapper.deleteByWaybillId(oldDataMessage.getWaybillId(), CarrierType.CARRIER.getCode());
            // 根据waybillId+messageFrom=CMP删除milestone_info数据(里程碑、操作信息，追踪轨迹表)
            milestoneInfoExtMapper.deleteByWaybillIdAndMessageFrom(oldDataMessage.getWaybillId(), oldDataMessage.getMessageFrom());
            // 根据no+source=CMP删除sys_exception_info数据(系统异常信息表)
            sysExceptionMapper.deleteByNoAndSource(oldDataMessage.getWaybillId(), oldDataMessage.getMessageFrom());
            //}
        } else if ("SCM".equals(oldDataMessage.getMessageFrom())) {
            //如果waybillId+messageFrom=SCM在milestone_info有数据，先删除再插入(导入数据时不用手动删除原来的数据，只需给定时间范围或者运单号即可)
            //if (0 < milestoneInfoExtMapper.selectCountByWaybillIdAndMessageFrom(oldDataMessage.getWaybillId(), oldDataMessage.getMessageFrom())) {
            // 根据waybillId+milestoneInfo中messageFrom=SCM的id列表删除operation_detail数据(操作明细表)
            operationDetailExtMapper.deleteByWaybillIdAndMessageFrom(oldDataMessage.getWaybillId(), oldDataMessage.getMessageFrom());
            // 根据waybillId+messageFrom=SCM删除milestone_info数据(里程碑、操作信息，追踪轨迹表)
            milestoneInfoExtMapper.deleteByWaybillIdAndMessageFrom(oldDataMessage.getWaybillId(), oldDataMessage.getMessageFrom());
            // 根据waybillId删除carrier_info数据(承运商)
            carrierInfoExtMapper.deleteByWaybillId(oldDataMessage.getWaybillId(), CarrierType.DIRECT_CARRIER.getCode());
            // 根据no+source=SCM删除sys_exception_info数据(系统异常信息表)
            sysExceptionMapper.deleteByNoAndSource(oldDataMessage.getWaybillId(), oldDataMessage.getMessageFrom());
            //}
        } else {
            throw new RuntimeException(oldmsg + "非IPS、SCM、CMP数据!");
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void deleteCompensationAndMilestoneAndOperationContext(OldDataMessage oldDataMessage) {
        if ("IPS".equals(oldDataMessage.getMessageFrom())) {
            // 根据waybillId+messageFrom=IPS删除milestone_info数据(里程碑、操作信息，追踪轨迹表)
            milestoneInfoExtMapper.deleteByWaybillIdAndMessageFromAndOperationClass(oldDataMessage.getWaybillId(), oldDataMessage.getMessageFrom());
            // 赔(追)偿处理
            compensationInfoExtMapper.deleteByWaybillId(oldDataMessage.getWaybillId());
            // 根据no+source=IPS删除sys_exception_info数据(系统异常信息表)
            //sysExceptionMapper.deleteByNoAndSource(oldDataMessage.getWaybillId(), oldDataMessage.getMessageFrom());
            //}
        } else {
            throw new RuntimeException(oldmsg + "非IPS理赔(追赔)数据!");
        }
    }

    public void instCompensation(OldDataMessage oldDataMessage, Timestamp ts) {
        if (null != oldDataMessage.getOldMessageBody().getCompensationInfoVOList() && 0 < oldDataMessage.getOldMessageBody().getCompensationInfoVOList().size()) {
            oldDataMessage.getOldMessageBody().getCompensationInfoVOList().stream().parallel().forEach(compensationInfoVO -> {
                CompensationInfo compensationInfo = getCompensationInfo(oldDataMessage.getWaybillId(), ts, compensationInfoVO);
                compensationInfoExtMapper.insert(compensationInfo);
            });
        }
    }

    public void instMilestoneAndOperationContext(OldDataMessage oldDataMessage, Timestamp ts) {
        if (null != oldDataMessage.getOldMessageBody().getMilestoneInfoVOList() && 0 < oldDataMessage.getOldMessageBody().getMilestoneInfoVOList().size()) {
            oldDataMessage.getOldMessageBody().getMilestoneInfoVOList().stream().parallel().forEach(milestoneInfoVo -> {
                MilestoneInfo milestoneInfo = getMilestoneInfo(oldDataMessage, ts, milestoneInfoVo);
                milestoneInfoExtMapper.insert(milestoneInfo);
                // 2.milestoneInfo的operationContext跟milestone的id，运单号插入到operation_detail，创建时间，更新时间。
                Map<String, Object> operationContext = milestoneInfoVo.getOperationContext();
                if (null != operationContext) {
                    //插入operation_detail表
                    operationContext.keySet().stream().parallel().forEach(key -> {
                        OperationDetail operationDetail = getOperationDetail(oldDataMessage.getWaybillId(), ts, milestoneInfo, operationContext, key);
                        operationDetailExtMapper.insert(operationDetail);
                    });
                    /*for (String key : operationContext.keySet()) {
                        OperationDetail operationDetail = getOperationDetail(oldDataMessage.getWaybillId(), ts, milestoneInfo, operationContext, key);
                        operationDetailExtMapper.insert(operationDetail);
                    }*/
                }
            });
            /*for (MilestoneInfoVO milestoneInfoVo : oldDataMessage.getOldMessageBody().getMilestoneInfoVOList()) {
                MilestoneInfo milestoneInfo = getMilestoneInfo(oldDataMessage, ts, milestoneInfoVo);
                milestoneInfoExtMapper.insert(milestoneInfo);
                // 2.milestoneInfo的operationContext跟milestone的id，运单号插入到operation_detail，创建时间，更新时间。
                Map<String, Object> operationContext = milestoneInfoVo.getOperationContext();
                if (null != operationContext) {
                    //插入operation_detail表
                    for (String key : operationContext.keySet()) {
                        OperationDetail operationDetail = getOperationDetail(oldDataMessage.getWaybillId(), ts, milestoneInfo, operationContext, key);
                        operationDetailExtMapper.insert(operationDetail);
                    }
                }
            }*/
        }
    }

}
