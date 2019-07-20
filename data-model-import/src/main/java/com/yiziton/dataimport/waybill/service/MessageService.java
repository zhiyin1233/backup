package com.yiziton.dataimport.waybill.service;

import com.yiziton.commons.utils.OID;
import com.yiziton.commons.utils.bean.Beans;
import com.yiziton.commons.vo.enums.*;
import com.yiziton.commons.vo.waybill.*;
import com.yiziton.dataimport.waybill.bean.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description:
 * @Author: kdh
 * @Date: 2018/11/12
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Component("messageService")
@Slf4j
public class MessageService extends BaseService {

    /**
     * 开单接口
     *
     * @param message
     * @throws Exception
     */
    @Transactional
    public void waybillOpen(Message message) throws Exception {
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段插入waybill_info表，同时插入运单号，创建时间，更新时间
        // 3.consignorInfo的字段插入consignor_info表，同时插入运单号，创建时间，更新时间
        // 4.receviceInfo的字符插入recevice_info表，同时插入运单号，创建时间，更新时间
        // 5.goodsInfo的字段插入goods_info表，同时插入运单号，创建时间，更新时间
        log.info("调用开单接口开始");
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        MessageBody messageBody = message.getMessageBody();
        String waybillId = message.getWaybillId();
        /*
        售后开单：
            补件在scm开单，xxx-bj
            返货在ips 开单，xxx-fh | 原件回单在ips 开单，xxx-hd
            维修在ips 开单，xxx-wx
            二次上门在ips 开单，xxx-sm
         */
        WaybillInfo waybillInfo1 = waybillInfoExtMapper.selectByPrimaryKey(waybillId);
        //if (waybillInfo1 != null && (waybillId.indexOf("-bj") > 0 || waybillId.indexOf("-fh") > 0 || waybillId.indexOf("-hd") > 0 || waybillId.indexOf("-wx") > 0 || waybillId.indexOf("-sm") > 0)) {
        if (waybillInfo1 != null) {
            log.info(waybillId + "该运单为已存在，跳转到修改运单接口");
            waybillModify(message);
        } else {
            // 2.waybillInfo的字段插入waybill_info表，同时插入运单号，创建时间，更新时间
            WaybillInfoVO waybillInfoVO = messageBody.getWaybillInfoVO();
            if (waybillInfoVO != null) {
                WaybillInfo waybillInfo = new WaybillInfo();
                waybillInfo.setId(waybillId);
                //对象copy，将后一个对象中属性的值赋予给前一个
                Beans.from(waybillInfoVO).to(waybillInfo);
                waybillInfo.setStatus(DeleteStatus.ENABLE.getCode());
                WaybillType waybillType = WaybillType.getEnumByEnumNameString(waybillInfoVO.getWaybillType());
                if (null != waybillType) {
                    waybillInfo.setWaybillType(waybillType.getCode());
                } else {
                    waybillInfo.setWaybillType(null);
                }
                waybillInfo.setCreateTime(ts);
                waybillInfo.setUpdateId("");
                waybillInfo.setUpdateTime(ts);
                ServiceType serviceType = ServiceType.getEnumByEnumNameString(waybillInfoVO.getServiceType());
                if (null != serviceType) {
                    waybillInfo.setServiceType(serviceType.getCode());
                } else {
                    waybillInfo.setServiceType(null);
                }
                WaybillStatus waybillStatus = WaybillStatus.getEnumByEnumNameString(waybillInfoVO.getWaybillStatus());
                if (null != waybillStatus) {
                    waybillInfo.setWaybillStatus(waybillStatus.getCode());
                } else {
                    waybillInfo.setWaybillStatus(null);
                }
                CheckMethod checkMethod = CheckMethod.getEnumByEnumNameString(waybillInfoVO.getCheckMethod());
                if (null != checkMethod) {
                    waybillInfo.setCheckMethod(checkMethod.getCode());
                } else {
                    waybillInfo.setCheckMethod(null);
                }
                CheckType checkType = CheckType.getEnumByEnumNameString(waybillInfoVO.getCheckType());
                if (null != checkType) {
                    waybillInfo.setCheckType(checkType.getCode());
                } else {
                    waybillInfo.setCheckType(null);
                }
                CheckStatus checkStatus = CheckStatus.getEnumByEnumNameString(waybillInfoVO.getCheckStatus());
                if (checkStatus != null) {
                    waybillInfo.setCheckStatus(checkStatus.getCode());
                } else {
                    waybillInfo.setCheckStatus(null);
                }
                PaymentType paymentType = PaymentType.getEnumByEnumNameString(waybillInfoVO.getPaymentType());
                if (null != paymentType) {
                    waybillInfo.setPaymentType(paymentType.getCode());
                } else {
                    waybillInfo.setPaymentType(null);
                }
                ProductType productType = ProductType.getEnumByEnumNameString(waybillInfoVO.getProductType());
                if (null != productType) {
                    waybillInfo.setProductType(productType.getCode());
                } else {
                    waybillInfo.setProductType(null);
                }
                SettlementType settlementType = SettlementType.getEnumByEnumNameString(waybillInfoVO.getSettlementType());
                if (null != settlementType) {
                    waybillInfo.setSettlementType(settlementType.getCode());
                } else {
                    waybillInfo.setSettlementType(null);
                }
                ChannelSource channelSource = ChannelSource.getEnumByEnumNameString(waybillInfoVO.getChannelSource());
                if (null != channelSource) {
                    waybillInfo.setChannelSource(channelSource.getCode());
                } else {
                    waybillInfo.setChannelSource(null);
                }
                IncrementServiceType incrementServiceType = IncrementServiceType.getEnumByEnumNameString(waybillInfoVO.getIncrementServiceType());
                if (null != incrementServiceType) {
                    waybillInfo.setIncrementServiceType(incrementServiceType.getCode());
                } else {
                    waybillInfo.setIncrementServiceType(null);
                }
                waybillInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
                waybillInfoExtMapper.insert(waybillInfo);
            }
            BillRelationInfoVO billRelationInfoVO = messageBody.getBillRelationInfoVO();
            //插入bill_relation_info表
            if (null != billRelationInfoVO) {
                BillRelationInfo billRelationInfo = new BillRelationInfo();
                Beans.from(billRelationInfoVO).to(billRelationInfo);
                if (StringUtils.isNotBlank(billRelationInfo.getCustomerBillId()) || StringUtils.isNotBlank(billRelationInfo.getOrderBillId())) {
                    billRelationInfo.setId(OID.get().toString());
                    billRelationInfo.setWaybillId(message.getWaybillId());
                    billRelationInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    billRelationInfo.setCreateTime(ts);
                    billRelationInfo.setUpdateTime(ts);
                    billRelationInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
                    billRelationInfoExtMapper.insert(billRelationInfo);
                } else {
                    log.info(waybillId + "该运单无客户单号或订单号");
                }
            }

            //里程碑
            // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
            MilestoneInfo milestoneInfo = new MilestoneInfo();
            milestoneInfo.setId(OID.get().toString());
            //对象copy，将后一个对象中属性的值赋予给前一个
            //里程碑信息在消息头里面
            Beans.from(message).to(milestoneInfo);
            milestoneInfo.setWaybillId(waybillId);
            milestoneInfo.setStatus(DeleteStatus.ENABLE.getCode());
            milestoneInfo.setCreateTime(ts);
            milestoneInfo.setUpdateTime(ts);
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
                milestoneInfo.setOperationType(null);
            }
            milestoneInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
            if (StringUtils.isBlank(message.getOperator())) {
                milestoneInfo.setOperator(message.getOperationSys() + "系统");
                log.error("业务系统 " + message.getOperationSys() + " 没有将当前操作人传过来, 运单号为 " + waybillId);
            }
            milestoneInfoExtMapper.insert(milestoneInfo);

            //发货人
            // 3.consignorInfo的字段插入consignor_info表，同时插入运单号，创建时间，更新时间
            ConsignorInfoVO consignorInfoVO = messageBody.getConsignorInfoVO();
            if (consignorInfoVO != null) {
                ConsignorInfo consignorInfo = new ConsignorInfo();
                consignorInfo.setId(OID.get().toString());
                //对象copy，将后一个对象中属性的值赋予给前一个
                Beans.from(consignorInfoVO).to(consignorInfo);
                consignorInfo.setWaybillId(waybillId);
                consignorInfo.setStatus(DeleteStatus.ENABLE.getCode());
                consignorInfo.setCreateTime(ts);
                consignorInfo.setUpdateTime(ts);
                CustomerType customerType = CustomerType.getEnumByEnumNameString(consignorInfoVO.getCustomerType());
                if (null != customerType) {
                    consignorInfo.setCustomerType(customerType.getCode());
                } else {
                    consignorInfo.setCustomerType(null);
                }
                ConsignorType consignorType = ConsignorType.getEnumByEnumNameString(consignorInfoVO.getConsignorType());
                if (null != consignorType) {
                    consignorInfo.setConsignorType(consignorType.getCode());
                } else {
                    consignorInfo.setConsignorType(null);
                }
                consignorInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
                consignorInfoExtMapper.insert(consignorInfo);
            }

            //收货人
            // 4.receviceInfo的字符插入recevice_info表，同时插入运单号，创建时间，更新时间
            ReceviceInfoVO receviceInfoVO = messageBody.getReceviceInfoVO();
            if (receviceInfoVO != null) {
                ReceviceInfo receviceInfo = new ReceviceInfo();
                receviceInfo.setId(OID.get().toString());
                //对象copy，将后一个对象中属性的值赋予给前一个
                Beans.from(receviceInfoVO).to(receviceInfo);
                receviceInfo.setWaybillId(waybillId);
                receviceInfo.setStatus(DeleteStatus.ENABLE.getCode());
                receviceInfo.setCreateTime(ts);
                receviceInfo.setUpdateTime(ts);
                receviceInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
                receviceInfoExtMapper.insert(receviceInfo);
            }

            //商品信息
            // 5.goodsInfo的字段插入goods_info表，同时插入运单号，创建时间，更新时间
            List<GoodsInfoVO> goodsInfoVOList = messageBody.getGoodsInfoVOList();
            if (goodsInfoVOList != null) {
                for (GoodsInfoVO goodsInfoVO : goodsInfoVOList) {
                    GoodsInfo goodsInfo = new GoodsInfo();
                    goodsInfo.setId(OID.get().toString());
                    goodsInfo.setWaybillId(waybillId);
                    goodsInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    goodsInfo.setCreateTime(ts);
                    goodsInfo.setUpdateTime(ts);
                    //对象copy，将后一个对象中属性的值赋予给前一个
                    Beans.from(goodsInfoVO).to(goodsInfo);
                    goodsInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
                    Packing packing = Packing.getEnumByEnumNameString(goodsInfoVO.getPacking());
                    if (null != packing) {
                        goodsInfo.setPacking(packing.getCode());
                    } else {
                        goodsInfo.setPacking(null);
                    }
                    goodsInfoExtMapper.insert(goodsInfo);
                }
            }
        }
        log.info("调用开单接口结束");
    }

    /**
     * 短途交接接口
     *
     * @param message
     * @throws Exception
     */
    public void shortDeliver(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 短途到达接口
     *
     * @param message
     * @throws Exception
     */
    public void shortDeliverArrive(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 入库接口
     *
     * @param message
     * @throws Exception
     */
    public void warehousing(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 直营收货接口
     *
     * @param message
     * @throws Exception
     */
    public void directReceipt(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 外发交接接口
     *
     * @param message
     * @throws Exception
     */
    public void outDeliver(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 直营外发交接接口
     *
     * @param message
     * @throws Exception
     */
    public void directTransfer(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 确认外发接口
     *
     * @param message
     * @throws Exception
     */
    public void outDeliverComfirm(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 取消外发接口
     *
     * @param message
     * @throws Exception
     */
    public void outDeliverCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 取消入库接口
     *
     * @param message
     * @throws Exception
     */
    public void warehousingCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 承运商受理接口
     *
     * @param message
     * @throws Exception
     */
    public void carrierAccept(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 承运商发车接口
     *
     * @param message
     * @throws Exception
     */
    public void carrierDeparture(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 承运商到达直营接口
     *
     * @param message
     * @throws Exception
     */
    public void carrierArrive(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
        /*MilestoneInfo milestoneInfo = messageExtMilestoneInfo(message);
        Timestamp dt = new Timestamp(System.currentTimeMillis());
        milestoneInfo.setCreateTime(dt);
        milestoneInfo.setUpdateTime(dt);
        milestoneInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
        milestoneInfoExtMapper.insert(milestoneInfo);

        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        WaybillStatus enumByEnumNameString = WaybillStatus.getEnumByEnumNameString(message.getMessageBody().getWaybillInfoVO().getWaybillStatus());
        if (null != enumByEnumNameString) {
            WaybillInfo waybillInfo = waybillInfoExtMapper.selectByPrimaryKey(milestoneInfo.getWaybillId());
            if (null != waybillInfo) {
                waybillInfo.setWaybillStatus(enumByEnumNameString.getCode());
                waybillInfo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                waybillInfoExtMapper.updateByPrimaryKeySelective(waybillInfo);
            } else {
                log.error("承运商到达直营接口:运单号\t" + milestoneInfo.getWaybillId() + "\t不存在!");
            }
        } else {
            log.error("承运商到达直营接口:运单状态错误!");
        }*/
    }

    /**
     * 承运商中转接口
     *
     * @param message
     * @throws Exception
     */
    public void carrierTransfer(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 到达目的地接口
     *
     * @param message
     * @throws Exception
     */
    public void arrivalDestination(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 承运商撤销受理接口
     *
     * @param message
     * @throws Exception
     */
    public void carrierAcceptCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 承运商撤销发车接口
     *
     * @param message
     * @throws Exception
     */
    public void carrierDepartureCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 承运商撤销到达直营接口
     *
     * @param message
     * @throws Exception
     */
    public void carrierArriveCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 承运商撤销中转接口
     *
     * @param message
     * @throws Exception
     */
    public void carrierTransferCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 承运商撤销到达目的地接口
     *
     * @param message
     * @throws Exception
     */
    public void arrivalDestinationCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 干结接口
     *
     * @param message
     * @throws Exception
     */
    public void trunkEnd(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);//需要把提货信息更新到waybill_info
    }

    /**
     * 取消干结接口
     *
     * @param message
     * @throws Exception
     */
    public void trunkEndCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);//把提货信息清空
    }


    /**
     * 总部分配接口
     *
     * @param message
     * @throws Exception
     */
    public void headquartersDistribution(Message message) throws Exception {
        // TODO: 2018/11/14
        //1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        //2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        //3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }


    /**
     * 总部预约接口
     *
     * @param message
     * @throws Exception
     */
    public void headquartersAppointment(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 总部提货接口
     *
     * @param message
     * @throws Exception
     */
    public void headquartersPick(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 总部签收接口
     *
     * @param message
     * @throws Exception
     */
    public void headquartersSign(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time，sign_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 撤销总部分配接口
     *
     * @param message
     * @throws Exception
     */
    public void headquartersDistributionCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 撤销总部预约接口
     *
     * @param message
     * @throws Exception
     */
    public void headquartersAppointmentCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 撤销总部提货接口
     *
     * @param message
     * @throws Exception
     */
    public void headquartersPickCancel(Message message) throws Exception {
        // TODO: 2018/11/16
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 撤销总部签收接口
     *
     * @param message
     * @throws Exception
     */
    public void headquartersSignCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time，sign_time = null
        commonMethod(message);
    }

    /**
     * 直营网点受理接口
     *
     * @param message
     * @throws Exception
     */
    public void directOrOutletAccept(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 直营网点预约接口
     *
     * @param message
     * @throws Exception
     */
    public void directOrOutletAppointment(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 直营网点分配接口
     *
     * @param message
     * @throws Exception
     */
    public void directOrOutletDistribution(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 直营网点提货接口
     *
     * @param message
     * @throws Exception
     */
    public void directOrOutletPick(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 直营网点签收接口
     *
     * @param message
     * @throws Exception
     */
    public void directOrOutletSign(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time，sign_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 撤销直营网点受理接口
     *
     * @param message
     * @throws Exception
     */
    public void directOrOutletAcceptCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 撤销直营网点分配接口
     *
     * @param message
     * @throws Exception
     */
    public void directOrOutletDistributionCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 撤销营网点提货接口
     *
     * @param message
     * @throws Exception
     */
    public void directOrOutletPickCancel(Message message) throws Exception {
        // TODO: 2018/11/16
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);

    }

    /**
     * 撤销直营网点预约接口
     *
     * @param message
     * @throws Exception
     */
    public void directOrOutletAppointmentCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 撤销直营网点签收接口
     *
     * @param message
     * @throws Exception
     */
    public void directOrOutletSignCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time，sign_time = null
        commonMethod(message);
    }

    /**
     * 师傅受理接口
     *
     * @param message
     * @throws Exception
     */
    public void masterAccept(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 师傅预约接口
     *
     * @param message
     * @throws Exception
     */
    public void masterAppointment(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 师傅提货接口
     *
     * @param message
     * @throws Exception
     */
    public void masterPick(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 二次预约接口
     *
     * @param message
     * @throws Exception
     */
    public void secondAppointment(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 上门打卡接口
     *
     * @param message
     * @throws Exception
     */
    public void masterHomeCheck(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 师傅签收接口
     *
     * @param message
     * @throws Exception
     */
    public void masterSign(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time，sign_time
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 撤销师傅受理接口
     *
     * @param message
     * @throws Exception
     */
    public void masterAcceptCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 撤销师傅提货接口
     *
     * @param message
     * @throws Exception
     */
    public void masterPickCancel(Message message) throws Exception {
        // TODO: 2018/11/16
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);

    }

    /**
     * 撤销师傅预约接口
     *
     * @param message
     * @throws Exception
     */
    public void masterAppointmentCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 撤销师傅上门打卡接口
     *
     * @param message
     * @throws Exception
     */
    public void masterHomeCheckCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        commonMethod(message);
    }

    /**
     * 撤销师傅签收接口
     *
     * @param message
     * @throws Exception
     */
    public void masterSignCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time，sign_time = null
        commonMethod(message);
    }

    /**
     * 修改提货信息接口
     *
     * @param message
     * @throws Exception
     */
    public void pickModify(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);//更新waybill_info中的提货信息
    }

    /**
     * 修改运单信息接口
     *
     * @param message
     * @throws Exception
     */
    @Transactional
    public void waybillModify(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的不为null字段更新waybill_info表对应的字段，同时更新update_time
        // 3.consignorInfo的不为null字段更新consignor_info表对应的字段，同时更新update_time
        // 4.receviceInfo的不为null字段更新recevice_info表对应的字段，同时更新update_time
        // 5.goodsInfo的不为null字段更新goods_info表对应的字段，同时更新update_time
        // 6.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        String messageOperation = message.getOperation();
        boolean sign = messageOperation.contains("SIGN");//是否签收
        boolean cancelSign = messageOperation.contains("SIGN_CANCEL");//是否取消签收
        String operatinoMessage = Operation.getEnumByEnumNameString(messageOperation).getMessage();
        log.info("调用" + operatinoMessage + "接口开始");
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        MessageBody messageBody = message.getMessageBody();
        String waybillId = message.getWaybillId();
        String operationClass = message.getOperationClass();

        //里程碑
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        MilestoneInfo milestoneInfo = new MilestoneInfo();
        milestoneInfo.setId(OID.get().toString());
        //对象copy，将后一个对象中属性的值赋予给前一个
        //里程碑信息在消息头里面
        Beans.from(message).to(milestoneInfo);
        milestoneInfo.setWaybillId(waybillId);
        milestoneInfo.setStatus(DeleteStatus.ENABLE.getCode());
        milestoneInfo.setCreateTime(ts);
        milestoneInfo.setUpdateTime(ts);
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
            milestoneInfo.setOperationType(null);
        }
        milestoneInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
        if (StringUtils.isBlank(message.getOperator())) {
            milestoneInfo.setOperator(message.getOperationSys() + "系统");
            log.error("业务系统 " + message.getOperationSys() + " 没有将当前操作人传过来, 运单号为 " + waybillId);
        }
        milestoneInfoExtMapper.insert(milestoneInfo);

        // 2.waybillInfo的不为null字段更新waybill_info表对应的字段，同时更新update_time
        WaybillInfoVO waybillInfoVO = messageBody.getWaybillInfoVO();
        if (waybillInfoVO != null) {
            WaybillInfo waybillInfo = waybillInfoExtMapper.selectByPrimaryKey(waybillId);
            if (waybillInfo != null) {
                // Integer
                if (waybillInfoVO.getProductType() != null) {
                    ProductType pt = ProductType.getEnumByEnumNameString(waybillInfoVO.getProductType());
                    if (pt != null) {
                        waybillInfo.setProductType(pt.getCode());
                    }
                }
                if (waybillInfoVO.getServiceType() != null) {
                    ServiceType st = ServiceType.getEnumByEnumNameString(waybillInfoVO.getServiceType());
                    if (st != null) {
                        waybillInfo.setServiceType(st.getCode());
                    }
                }
                if (waybillInfoVO.getWaybillStatus() != null && operationClass.equals("C") ) {
                    WaybillStatus ws = WaybillStatus.getEnumByEnumNameString(waybillInfoVO.getWaybillStatus());
                    if (ws != null) {
                        waybillInfo.setWaybillStatus(ws.getCode());
                    }
                }
                if (waybillInfoVO.getCheckType() != null) {
                    CheckType ct = CheckType.getEnumByEnumNameString(waybillInfoVO.getCheckType());
                    if (ct != null) {
                        waybillInfo.setCheckType(ct.getCode());
                    }
                }
                if (waybillInfoVO.getCheckMethod() != null) {
                    CheckMethod cm = CheckMethod.getEnumByEnumNameString(waybillInfoVO.getCheckMethod());
                    if (cm != null) {
                        waybillInfo.setCheckMethod(cm.getCode());
                    }
                }
                if (waybillInfoVO.getCheckStatus() != null) {
                    CheckStatus checkStatus = CheckStatus.getEnumByEnumNameString(waybillInfoVO.getCheckStatus());
                    if (checkStatus != null) {
                        waybillInfo.setCheckStatus(checkStatus.getCode());
                    }
                }
                if (waybillInfoVO.getChannelSource() != null) {
                    ChannelSource channelSource = ChannelSource.getEnumByEnumNameString(waybillInfoVO.getChannelSource());
                    if (channelSource != null) {
                        waybillInfo.setChannelSource(channelSource.getCode());
                    }
                }
                if (waybillInfoVO.getPaymentType() != null) {
                    PaymentType pt = PaymentType.getEnumByEnumNameString(waybillInfoVO.getPaymentType());
                    if (pt != null) {
                        waybillInfo.setPaymentType(pt.getCode());
                    }
                }
                if (waybillInfoVO.getSettlementType() != null) {
                    SettlementType st = SettlementType.getEnumByEnumNameString(waybillInfoVO.getSettlementType());
                    if (st != null) {
                        waybillInfo.setSettlementType(st.getCode());
                    }
                }
                if (waybillInfoVO.getWaybillType() != null) {
                    WaybillType wt = WaybillType.getEnumByEnumNameString(waybillInfoVO.getWaybillType());
                    if (wt != null) {
                        waybillInfo.setWaybillType(wt.getCode());
                    }
                }
                if (waybillInfoVO.getIncrementServiceType() != null) {
                    IncrementServiceType incrementServiceType = IncrementServiceType.getEnumByEnumNameString(waybillInfoVO.getIncrementServiceType());
                    if (incrementServiceType != null) {
                        waybillInfo.setIncrementServiceType(incrementServiceType.getCode());
                    }else {
                        waybillInfo.setIncrementServiceType(null);
                        waybillInfo.setIncrementServiceFee(null);
                    }
                }else{
                    waybillInfo.setIncrementServiceType(null);
                    waybillInfo.setIncrementServiceFee(null);
                }
                if (waybillInfoVO.getTotalInstallItems() != null) {
                    waybillInfo.setTotalInstallItems(waybillInfoVO.getTotalInstallItems());
                }
                if (waybillInfoVO.getTotalPackingItems() != null) {
                    waybillInfo.setTotalPackingItems(waybillInfoVO.getTotalPackingItems());
                }
                if (waybillInfoVO.getMarbleBlocks() != null) {
                    waybillInfo.setMarbleBlocks(waybillInfoVO.getMarbleBlocks());
                }

                // Double
                if (waybillInfoVO.getTotalVolume() != null) {
                    waybillInfo.setTotalVolume(waybillInfoVO.getTotalVolume());
                }
                if (waybillInfoVO.getTotalWeight() != null) {
                    waybillInfo.setTotalWeight(waybillInfoVO.getTotalWeight());
                }
                if (waybillInfoVO.getStatementValue() != null) {
                    waybillInfo.setStatementValue(waybillInfoVO.getStatementValue());
                }
                if (waybillInfoVO.getSaleTotalPrice() != null) {
                    waybillInfo.setSaleTotalPrice(waybillInfoVO.getSaleTotalPrice());
                }
                if (waybillInfoVO.getIncrementServiceFee() != null) {
                    waybillInfo.setIncrementServiceFee(waybillInfoVO.getIncrementServiceFee());
                }

                // Timestamp
                if (waybillInfoVO.getOpenBillTime() != null) {
                    waybillInfo.setOpenBillTime(waybillInfoVO.getOpenBillTime());
                }
                if (waybillInfoVO.getSignTime() != null) {
                    waybillInfo.setSignTime(waybillInfoVO.getSignTime());
                }
                if (waybillInfoVO.getCheckTime() != null) {
                    waybillInfo.setCheckTime(waybillInfoVO.getCheckTime());
                }

                // String
                if (waybillInfoVO.getCheckCode() != null) {
                    waybillInfo.setCheckCode(waybillInfoVO.getCheckCode());
                }
                if (waybillInfoVO.getCheckBillId() != null) {
                    waybillInfo.setCheckBillId(waybillInfoVO.getCheckBillId());
                }
                if (waybillInfoVO.getThirdBillingId() != null) {
                    waybillInfo.setThirdBillingId(waybillInfoVO.getThirdBillingId());
                }
                if (waybillInfoVO.getDestination() != null) {
                    waybillInfo.setDestination(waybillInfoVO.getDestination());
                }
                if (waybillInfoVO.getOpenBillNode() != null) {
                    waybillInfo.setOpenBillNode(waybillInfoVO.getOpenBillNode());
                }
                if (waybillInfoVO.getOpenBillOperator() != null) {
                    waybillInfo.setOpenBillOperator(waybillInfoVO.getOpenBillOperator());
                }
                if (waybillInfoVO.getArrivalNode() != null) {
                    waybillInfo.setArrivalNode(waybillInfoVO.getArrivalNode());
                }
                if (waybillInfoVO.getSalesman() != null) {
                    waybillInfo.setSalesman(waybillInfoVO.getSalesman());
                }
                if (waybillInfoVO.getSalesmanPhone() != null) {
                    waybillInfo.setSalesmanPhone(waybillInfoVO.getSalesmanPhone());
                }
                if (waybillInfoVO.getArrivalArea() != null) {
                    waybillInfo.setArrivalArea(waybillInfoVO.getArrivalArea());
                }
                if (waybillInfoVO.getArrivalAddress() != null) {
                    waybillInfo.setArrivalAddress(waybillInfoVO.getArrivalAddress());
                }
                if (waybillInfoVO.getPickUpGoodsPhone() != null) {
                    waybillInfo.setPickUpGoodsPhone(waybillInfoVO.getPickUpGoodsPhone());
                }
                if (waybillInfoVO.getPickUpGoodsPassword() != null) {
                    waybillInfo.setPickUpGoodsPassword(waybillInfoVO.getPickUpGoodsPassword());
                }
                if (waybillInfoVO.getLogisticsBillId() != null) {
                    waybillInfo.setLogisticsBillId(waybillInfoVO.getLogisticsBillId());
                }
                if (waybillInfoVO.getOriginatingNode() != null) {
                    waybillInfo.setOriginatingNode(waybillInfoVO.getOriginatingNode());
                }
                if (waybillInfoVO.getOpenBillRemark() != null) {
                    waybillInfo.setOpenBillRemark(waybillInfoVO.getOpenBillRemark());
                }
                if (waybillInfoVO.getGoodsType() != null) {
                    waybillInfo.setGoodsType(waybillInfoVO.getGoodsType());
                }
                if (waybillInfoVO.getPacking() != null) {
                    String packing = waybillInfoVO.getPacking();
                    int length = packing.length();
                    if(length > 0){
                        String substring = packing.substring(length - 1, length);
                        if(substring.equals(";")){
                            packing = packing.substring(0,length - 1);
                        }
                        waybillInfo.setPacking(packing);
                    }
                }

                waybillInfo.setUpdateTime(ts);
                waybillInfo.setStatus(DeleteStatus.ENABLE.getCode());
                waybillInfoExtMapper.updateByPrimaryKey(waybillInfo);
            }
        }
        //获取运单关联表信息, 可能有多条记录，所以先删后增
        List<String> waybillIdList = new ArrayList<String>();
        waybillIdList.add(waybillId);
        List<BillRelationInfo> billRelationInfoList = billRelationInfoExtMapper.selectByWaybillIds(waybillIdList);
        if (billRelationInfoList != null && billRelationInfoList.size() > 0) {
            for (BillRelationInfo billRelationInfo : billRelationInfoList) {
                billRelationInfo.setStatus(DeleteStatus.DISABLE.getCode());
                billRelationInfo.setUpdateTime(ts);
                billRelationInfoExtMapper.updateByPrimaryKeySelective(billRelationInfo);
            }
        }
        BillRelationInfoVO billRelationInfoVO = messageBody.getBillRelationInfoVO();
        //插入bill_relation_info表
        if (null != billRelationInfoVO) {
            BillRelationInfo billRelationInfo = new BillRelationInfo();
            Beans.from(billRelationInfoVO).to(billRelationInfo);
            if (StringUtils.isNotBlank(billRelationInfo.getCustomerBillId()) || StringUtils.isNotBlank(billRelationInfo.getOrderBillId())) {
                billRelationInfo.setId(OID.get().toString());
                billRelationInfo.setWaybillId(message.getWaybillId());
                billRelationInfo.setStatus(DeleteStatus.ENABLE.getCode());
                billRelationInfo.setCreateTime(ts);
                billRelationInfo.setUpdateTime(ts);
                billRelationInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
                billRelationInfoExtMapper.insert(billRelationInfo);
            } else {
                log.info(waybillId + "该运单无客户单号或订单号");
            }
        }
        //发货人
        // 3.consignorInfo的不为null字段更新consignor_info表对应的字段，同时更新update_time
        ConsignorInfoVO consignorInfoVO = messageBody.getConsignorInfoVO();
        if (consignorInfoVO != null) {
            ConsignorInfo consignorInfo = consignorInfoExtMapper.selectByParams("waybill_id", waybillId);
            if (consignorInfo != null) {
                //对象copy，将后一个对象中属性的值赋予给前一个
                //Beans.from(consignorInfoVO).to(consignorInfo);
                if (consignorInfoVO.getCode() != null) {
                    consignorInfo.setCode(consignorInfoVO.getCode());
                }
                if (consignorInfoVO.getName() != null) {
                    consignorInfo.setName(consignorInfoVO.getName());
                }
                if (consignorInfoVO.getCustomerType() != null) {
                    CustomerType customerType = CustomerType.getEnumByEnumNameString(consignorInfoVO.getCustomerType());
                    if (null != customerType) {
                        consignorInfo.setCustomerType(customerType.getCode());
                    }
                }
                if (consignorInfoVO.getSecondName() != null) {
                    consignorInfo.setSecondName(consignorInfoVO.getSecondName());
                }
                if (consignorInfoVO.getMobile() != null) {
                    consignorInfo.setMobile(consignorInfoVO.getMobile());
                }
                if (consignorInfoVO.getSecondCode() != null) {
                    consignorInfo.setSecondCode(consignorInfoVO.getSecondCode());
                }
                if (consignorInfoVO.getProvince() != null) {
                    consignorInfo.setProvince(consignorInfoVO.getProvince());
                }
                if (consignorInfoVO.getCity() != null) {
                    consignorInfo.setCity(consignorInfoVO.getCity());
                }
                if (consignorInfoVO.getArea() != null) {
                    consignorInfo.setArea(consignorInfoVO.getArea());
                }
                if (consignorInfoVO.getStreet() != null) {
                    consignorInfo.setStreet(consignorInfoVO.getStreet());
                }
                if (consignorInfoVO.getAreaId() != null) {
                    consignorInfo.setAreaId(consignorInfoVO.getAreaId());
                }
                if (consignorInfoVO.getLongitude() != null) {
                    consignorInfo.setLongitude(consignorInfoVO.getLongitude());
                }
                if (consignorInfoVO.getLatitude() != null) {
                    consignorInfo.setLatitude(consignorInfoVO.getLatitude());
                }
                //consignorInfo.setDataType(consignorInfoVO.getDataType());
                if (consignorInfoVO.getConsignorType() != null) {
                    ConsignorType consignorType = ConsignorType.getEnumByEnumNameString(consignorInfoVO.getConsignorType());
                    if (null != consignorType) {
                        consignorInfo.setConsignorType(consignorType.getCode());
                    }
                }
                consignorInfo.setStatus(DeleteStatus.ENABLE.getCode());
                consignorInfo.setUpdateTime(ts);
                consignorInfoExtMapper.updateByPrimaryKeySelective(consignorInfo);
            } else {
                log.info(operatinoMessage + "正常流程不会走到这里");
                consignorInfo = new ConsignorInfo();
                consignorInfo.setId(OID.get().toString());
                //对象copy，将后一个对象中属性的值赋予给前一个
                Beans.from(consignorInfoVO).to(consignorInfo);
                consignorInfo.setWaybillId(waybillId);
                consignorInfo.setStatus(DeleteStatus.ENABLE.getCode());
                consignorInfo.setCreateTime(ts);
                consignorInfo.setUpdateTime(ts);
                CustomerType customerType = CustomerType.getEnumByEnumNameString(consignorInfoVO.getCustomerType());
                if (null != customerType) {
                    consignorInfo.setCustomerType(customerType.getCode());
                }
                ConsignorType consignorType = ConsignorType.getEnumByEnumNameString(consignorInfoVO.getConsignorType());
                if (null != consignorType) {
                    consignorInfo.setConsignorType(consignorType.getCode());
                }
                consignorInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
                consignorInfoExtMapper.insert(consignorInfo);
            }
        }

        //收货人
        // 4.receviceInfo的不为null字段更新recevice_info表对应的字段，同时更新update_time
        ReceviceInfoVO receviceInfoVO = messageBody.getReceviceInfoVO();
        if (receviceInfoVO != null) {
            ReceviceInfo receviceInfo = receviceInfoExtMapper.selectByParams("waybill_id", waybillId);
            if (receviceInfo != null) {
                //对象copy，将后一个对象中属性的值赋予给前一个
                //Beans.from(receviceInfoVO).to(receviceInfo);
                if (receviceInfoVO.getName() != null) {
                    receviceInfo.setName(receviceInfoVO.getName());
                }
                if (receviceInfoVO.getMobile() != null) {
                    receviceInfo.setMobile(receviceInfoVO.getMobile());
                }
                if (receviceInfoVO.getAddress() != null) {
                    receviceInfo.setAddress(receviceInfoVO.getAddress());
                }
                if (receviceInfoVO.getElevator() != null) {
                    receviceInfo.setElevator(receviceInfoVO.getElevator());
                }
                if (receviceInfoVO.getFloor() != null) {
                    receviceInfo.setFloor(receviceInfoVO.getFloor());
                }
                if (receviceInfoVO.getProvince() != null) {
                    receviceInfo.setProvince(receviceInfoVO.getProvince());
                }
                if (receviceInfoVO.getCity() != null) {
                    receviceInfo.setCity(receviceInfoVO.getCity());
                }
                if (receviceInfoVO.getArea() != null) {
                    receviceInfo.setArea(receviceInfoVO.getArea());
                }
                if (receviceInfoVO.getStreet() != null) {
                    receviceInfo.setStreet(receviceInfoVO.getStreet());
                }
                if (receviceInfoVO.getAreaId() != null) {
                    receviceInfo.setAreaId(receviceInfoVO.getAreaId());
                }
                if (receviceInfoVO.getLongitude() != null) {
                    receviceInfo.setLongitude(receviceInfoVO.getLongitude());
                }
                if (receviceInfoVO.getLatitude() != null) {
                    receviceInfo.setLatitude(receviceInfoVO.getLatitude());
                }
                receviceInfo.setStatus(DeleteStatus.ENABLE.getCode());
                receviceInfo.setUpdateTime(ts);
                receviceInfoExtMapper.updateByPrimaryKeySelective(receviceInfo);
            } else {
                log.info(operatinoMessage + "正常流程不会走到这里");
                receviceInfo = new ReceviceInfo();
                receviceInfo.setId(OID.get().toString());
                //对象copy，将后一个对象中属性的值赋予给前一个
                Beans.from(receviceInfoVO).to(receviceInfo);
                receviceInfo.setWaybillId(waybillId);
                receviceInfo.setStatus(DeleteStatus.ENABLE.getCode());
                receviceInfo.setCreateTime(ts);
                receviceInfo.setUpdateTime(ts);
                receviceInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
                receviceInfoExtMapper.insert(receviceInfo);
            }
        }

        //商品信息
        // 5.goodsInfo的不为null字段更新goods_info表对应的字段，同时更新update_time
        List<GoodsInfoVO> goodsInfoVOList = messageBody.getGoodsInfoVOList();
        List<GoodsInfo> goodsInfoList = goodsInfoExtMapper.selectListByParams("waybill_id", waybillId);
        for (GoodsInfo goodsInfo : goodsInfoList) {
            goodsInfo.setStatus(DeleteStatus.DISABLE.getCode());
            goodsInfo.setUpdateTime(ts);
            goodsInfoExtMapper.updateByPrimaryKeySelective(goodsInfo);
        }

        for (GoodsInfoVO goodsInfoVO : goodsInfoVOList) {
            GoodsInfo goodsInfo = new GoodsInfo();
            goodsInfo.setId(OID.get().toString());
            goodsInfo.setWaybillId(waybillId);
            goodsInfo.setStatus(DeleteStatus.ENABLE.getCode());
            goodsInfo.setCreateTime(ts);
            goodsInfo.setUpdateTime(ts);
            //对象copy，将后一个对象中属性的值赋予给前一个
            Beans.from(goodsInfoVO).to(goodsInfo);
            Packing packing = Packing.getEnumByEnumNameString(goodsInfoVO.getPacking());
            if (null != packing) {
                goodsInfo.setPacking(packing.getCode());
            } else {
                goodsInfo.setPacking(null);
            }
            goodsInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
            goodsInfoExtMapper.insert(goodsInfo);
        }

        // 6.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
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
                    operationDetail.setWaybillId(waybillId);
                    operationDetail.setMilestoneId(milestoneId);
                    operationDetail.setField(key);
                    operationDetail.setVal(operationContext.get(key) == null ? "" : operationContext.get(key).toString());
                    operationDetail.setCreateTime(ts);
                    operationDetail.setDataType(DataType.REAL_TIME_DATA.getCode());
                    operationDetailExtMapper.insert(operationDetail);
                }
            }
        }
        log.info("调用" + operatinoMessage + "接口结束");

    }

    /**
     * 上报异常接口
     *
     * @param message
     * @throws Exception
     */
    public void exceptionReport(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.exceptionInfo的字段插入到exception_info表，同时插入运单号创建时间，更新时间
        commonMethod(message);
    }

    /**
     * 修改异常接口
     *
     * @param message
     * @throws Exception
     */
    public void exceptionModify(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.exceptionInfo的字段插入到exception_info表，同时插入运单号创建时间，更新时间
        commonMethod(message);
    }

    /**
     * 处理异常接口
     * <p>
     * 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
     * 2.取relatedBillId作为下一个运单号，waybillInfo的字段插入waybill_info表，同时插入创建时间，更新时间
     * 3.consignorInfo的字段插入consignor_info表，同时插入下一个运单号，创建时间，更新时间
     * 4.receviceInfo的字段插入recevice_info表，同时插入下一个运单号，创建时间，更新时间
     * 5.goodsInfo的字段插入goods_info表，同时插入下一个运单号，创建时间，更新时间
     * 6.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
     * 8.将售后单号，exceptionInfo的字段按照运单号更新exception_info表，同时更新update_time
     * 7.milestoneInfo的operationContext的任务单号作为售后单号插入售后表after_sale_info,同时插入运单号，还有exceptionInfo的字段异常编号创建时间更新时间
     *
     * @param message
     * @throws Exception
     */
    public void exceptionHandling(Message message) throws Exception {
        // TODO: 2018/11/14

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        if (null == message || null == message.getMessageBody() || null == message.getMessageBody().getExceptionInfoVO() || null == message.getMessageBody().getExceptionInfoVO().getExceptionCode()) {
            throw new RuntimeException("处理异常接口:" + message.getWaybillId() + "异常信息不能为空!");
        } else if (null == message.getWaybillId()) {
            throw new RuntimeException("处理异常接口:" + message.getWaybillId() + "运单号不能为空!");
        }
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        MilestoneInfo milestoneInfo = messageExtMilestoneInfo(message);

        if (null == milestoneInfo) {
            throw new RuntimeException("处理异常接口:" + message.getWaybillId() + "操作信息不能为空!");
        } else if (null == milestoneInfo.getOperation()) {
            throw new RuntimeException("处理异常接口:" + message.getWaybillId() + "操作节点名称不能为空!");
        } else if (null == milestoneInfo.getOperationType()) {
            throw new RuntimeException("处理异常接口:" + message.getWaybillId() + "操作节点类型不能为空!");
        } else {
            milestoneInfo.setCreateTime(ts);
            milestoneInfo.setUpdateTime(ts);
            milestoneInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
            if (StringUtils.isBlank(message.getOperator())) {
                milestoneInfo.setOperator(message.getOperationSys() + "系统");
                log.error("业务系统 " + message.getOperationSys() + " 没有将当前操作人传过来, 运单号为 " + milestoneInfo.getWaybillId());
            }
            milestoneInfoExtMapper.insert(milestoneInfo);
            if (null != message.getMessageBody().getMilestoneInfoVO()) {
                Map<String, Object> operationContext = message.getMessageBody().getMilestoneInfoVO().getOperationContext();
                if (null != operationContext && null != milestoneInfo) {
                    //插入operation_detail表
                    operationContext.keySet().stream().parallel().forEach(key -> {
                        OperationDetail operationDetail = getOperationDetail(message.getWaybillId(), ts, milestoneInfo, operationContext, key);
                        operationDetail.setDataType(DataType.REAL_TIME_DATA.getCode());
                        operationDetailExtMapper.insert(operationDetail);
                    });
                }
            }
        }

        // 3.consignorInfo的字段插入consignor_info表，同时插入下一个运单号，创建时间，更新时间
        ConsignorInfo consignorInfo = messageExtConsignorInfo(message);
        if (null != consignorInfo) {
            consignorInfo.setWaybillId(message.getRelatedBillId());
            consignorInfo.setCreateTime(ts);
            consignorInfo.setUpdateTime(ts);
            consignorInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
            consignorInfoExtMapper.insert(consignorInfo);
        }
        // 4.receviceInfo的字段插入recevice_info表，同时插入下一个运单号，创建时间，更新时间
        ReceviceInfo receviceInfo = messageExtReceviceInfo(message);
        if (null != receviceInfo) {
            receviceInfo.setWaybillId(message.getRelatedBillId());
            receviceInfo.setCreateTime(ts);
            receviceInfo.setUpdateTime(ts);
            receviceInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
            receviceInfoExtMapper.insert(receviceInfo);
        }
        // 5.goodsInfo的字段插入goods_info表，同时插入下一个运单号，创建时间，更新时间
        messageInsertGoodsInfo(message, ts, DataType.REAL_TIME_DATA.getCode());
        // 6.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        messageInsertOperationDetail(message, milestoneInfo.getId(), ts, DataType.REAL_TIME_DATA.getCode());
        // 7.将exceptionInfo的字段按照运单号更新exception_info表，同时更新update_time
        /*ExceptionInfo exceptionInfo = messageExtExceptionInfo(message);
        if (null == exceptionInfo) {
            log.error("处理异常接口:" + message.getWaybillId() + "异常信息不能为空!");
            return;
        } else {
            exceptionInfo.setStatus(null);
            exceptionInfo.setUpdateTime(ts);
            exceptionInfoExtMapper.updateByExceptionCodeSelective(exceptionInfo);
        }*/
        ExceptionInfo exceptionInfo = null;
        if (null != message && null != message.getMessageBody() && null != message.getMessageBody().getExceptionInfoVO()) {
            if (null != message.getWaybillId() && null != message.getMessageBody().getExceptionInfoVO().getExceptionCode()) {
                exceptionInfo = exceptionInfoExtMapper.selectByWaybillIdAndExceptionCode(message.getWaybillId(), message.getMessageBody().getExceptionInfoVO().getExceptionCode());
                if (null == exceptionInfo) {
                    throw new RuntimeException("处理异常接口:" + message.getWaybillId() + "异常信息不存在!");
                }
                ExceptionInfoVO exceptionInfoVO = message.getMessageBody().getExceptionInfoVO();


                if (null != exceptionInfoVO) {
                    if (null != exceptionInfoVO.getExceptionCode()) {
                        exceptionInfo.setExceptionCode(exceptionInfoVO.getExceptionCode());
                    }
                    //异常大类不能为空
                    if (null == exceptionInfoVO.getExceptionLargeType()) {
                        throw new RuntimeException(oldmsg + "异常大类不能为空!");
                    }
                    //exceptionInfo.setExceptionLargeType(exceptionInfoVO.getExceptionLargeType());
                    //异常小类不能为空
                    if (null == exceptionInfoVO.getExceptionSubType()) {
                        throw new RuntimeException(oldmsg + "异常小类不能为空!");
                    }
                    //exceptionInfo.setExceptionSubType(exceptionInfoVO.getExceptionSubType());
                    //异常处理状态不能为空
                    if (null == exceptionInfoVO.getExceptionStatus()) {
                        throw new RuntimeException(oldmsg + "ExceptionStatus 找不到对应枚举");
                    }
                    /*if (null == ExceptionStatus.getEnumByEnumNameString(exceptionInfoVO.getExceptionStatus())) {
                        throw new RuntimeException(oldmsg + "ExceptionStatus 找不到对应枚举");
                    }*/
                    //exceptionInfo.setExceptionStatus(ExceptionStatus.getEnumByEnumNameString(exceptionInfoVO.getExceptionStatus()).getCode());
                    if(null != exceptionInfoVO.getExceptionStatus()){
                        /*String exceptionStatus = exceptionInfoVO.getExceptionStatus();
                        ExceptionStatus enumByEnumNameString = ExceptionStatus.getEnumByEnumNameString(exceptionStatus);
                        if (null != enumByEnumNameString) {
                            exceptionInfo.setExceptionStatus(enumByEnumNameString.getCode());
                        }*/
                        exceptionInfo.setExceptionStatus(exceptionInfoVO.getExceptionStatus());
                    }
                    if (null != exceptionInfoVO.getExceptionType()) {
                        exceptionInfo.setExceptionType(exceptionInfoVO.getExceptionType());
                    }
                    if (null != exceptionInfoVO.getExceptionMessage()) {
                        exceptionInfo.setExceptionMessage(exceptionInfoVO.getExceptionMessage());
                    }
                    if (null != exceptionInfoVO.getExceptionDescribe()) {
                        exceptionInfo.setExceptionDescribe(exceptionInfoVO.getExceptionDescribe());
                    }
                    if (null != exceptionInfoVO.getFeedbackSys()) {
                        exceptionInfo.setFeedbackSys(exceptionInfoVO.getFeedbackSys());
                    }
                    if (null != exceptionInfoVO.getFeedbackTime()) {
                        exceptionInfo.setFeedbackTime(exceptionInfoVO.getFeedbackTime());
                    }
                    if (null != exceptionInfoVO.getFeedback()) {
                        exceptionInfo.setFeedback(exceptionInfoVO.getFeedback());
                    }
                    if (null != exceptionInfoVO.getFeedbackParty()) {
                        exceptionInfo.setFeedbackParty(exceptionInfoVO.getFeedbackParty());
                    }
                    if (null != exceptionInfoVO.getFeedbackPhone()) {
                        exceptionInfo.setFeedbackPhone(exceptionInfoVO.getFeedbackPhone());
                    }
                    if (null != exceptionInfoVO.getFeedbackNode()) {
                        exceptionInfo.setFeedbackNode(exceptionInfoVO.getFeedbackNode());
                    }
                    if (null != exceptionInfoVO.getDispose()) {
                        exceptionInfo.setDispose(exceptionInfoVO.getDispose());
                    }
                    if (null != exceptionInfoVO.getDisposeNode()) {
                        exceptionInfo.setDisposeNode(exceptionInfoVO.getDisposeNode());
                    }
                    if (null != exceptionInfoVO.getDisposeResult()) {
                        exceptionInfo.setDisposeResult(exceptionInfoVO.getDisposeResult());
                    }
                    if (null != exceptionInfoVO.getArbitrationCode()) {
                        exceptionInfo.setArbitrationCode(exceptionInfoVO.getArbitrationCode());
                    }
                    if (null != exceptionInfoVO.getArbitrationType()) {
                        exceptionInfo.setArbitrationType(exceptionInfoVO.getArbitrationType());
                    }
                    if (null != exceptionInfoVO.getArbitrationResult()) {
                        exceptionInfo.setArbitrationResult(exceptionInfoVO.getArbitrationResult());
                    }
                    if (null != exceptionInfoVO.getRestrictDisposeTime()) {
                        exceptionInfo.setRestrictDisposeTime(exceptionInfoVO.getRestrictDisposeTime());
                    }
                    if (null != exceptionInfoVO.getActualDisposeTime()) {
                        exceptionInfo.setActualDisposeTime(exceptionInfoVO.getActualDisposeTime());
                    }
                    if (null != exceptionInfoVO.getDisposeRemark()) {
                        exceptionInfo.setDisposeRemark(exceptionInfoVO.getDisposeRemark());
                    }
                    if (null != exceptionInfoVO.getExceptionLargeType()) {
                        exceptionInfo.setExceptionLargeType(exceptionInfoVO.getExceptionLargeType());
                    }
                    if (null != exceptionInfoVO.getExceptionSubType()) {
                        exceptionInfo.setExceptionSubType(exceptionInfoVO.getExceptionSubType());
                    }
                    if (null != exceptionInfoVO.getDisposePhone()) {
                        exceptionInfo.setDisposePhone(exceptionInfoVO.getDisposePhone());
                    }
                    if (null != exceptionInfoVO.getDisposeBillId()) {
                        exceptionInfo.setDisposeBillId(exceptionInfoVO.getDisposeBillId());
                    }
                    if (null != exceptionInfoVO.getDuty()) {
                        exceptionInfo.setDuty(exceptionInfoVO.getDuty());
                    }
                    if (null != exceptionInfoVO.getDutyAccount()) {
                        exceptionInfo.setDutyAccount(exceptionInfoVO.getDutyAccount());
                    }
                    if (null != exceptionInfoVO.getIsClaim()) {
                        exceptionInfo.setIsClaim(exceptionInfoVO.getIsClaim());
                    }
                    if (null != exceptionInfoVO.getClaimBillId()) {
                        exceptionInfo.setClaimBillId(exceptionInfoVO.getClaimBillId());
                    }
                    if (null != exceptionInfoVO.getDisposeSys()) {
                        exceptionInfo.setDisposeSys(exceptionInfoVO.getDisposeSys());
                    }
                    if (null != exceptionInfoVO.getDisposeObject()) {
                        exceptionInfo.setDisposeObject(exceptionInfoVO.getDisposeObject());
                    }
                    exceptionInfo.setUpdateTime(ts);
                }
                exceptionInfoExtMapper.updateByWaybillIdAndExceptionCodeSelective(exceptionInfo);
            }
        }

        // 2.取relatedBillId作为下一个运单号，waybillInfo的字段插入waybill_info表，同时插入创建时间，更新时间
        WaybillInfo waybillInfo = messageExtWaybillInfo(message);
        if (null != waybillInfo) {
            waybillInfo.setId(message.getRelatedBillId());
            if (ServiceType.ADD_PIECES.getCode().equals(waybillInfo.getServiceType())) {
                waybillInfo.setWaybillType(WaybillType.NORMAL.getCode());
            } else {
                waybillInfo.setWaybillType(WaybillType.AFTERSALE.getCode());
                if (null != exceptionInfo) {
                    waybillInfo.setOpenBillOperator(exceptionInfo.getDispose());
                    waybillInfo.setOpenBillNode(exceptionInfo.getDisposeNode());
                }
            }
            waybillInfo.setCreateTime(ts);
            waybillInfo.setUpdateTime(ts);
            waybillInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
            waybillInfoExtMapper.insert(waybillInfo);
            BillRelationInfoVO billRelationInfoVO = message.getMessageBody().getBillRelationInfoVO();
            //插入bill_relation_info表
            if (null != billRelationInfoVO) {
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

        // 8.milestoneInfo的operationContext的任务单号作为售后单号插入售后表after_sale_info,同时插入运单号，还有exceptionInfo的字段异常编号创建时间更新时间
        if (null != message && null != message.getRelatedBillId() && null != message.getMessageBody().getWaybillInfoVO()) {
            AfterSaleInfo afterSaleInfo = new AfterSaleInfo();
            afterSaleInfo.setId(OID.get().toString());
            if (null != message && null != message.getWaybillId()) {
                afterSaleInfo.setWaybillId(message.getWaybillId());
            }
            if (null != message && null != message.getRelatedBillId()) {
                afterSaleInfo.setTaskBillId(message.getRelatedBillId());
            }
            //服务类型
            ServiceType serviceType = ServiceType.getEnumByEnumNameString(message.getMessageBody().getWaybillInfoVO().getServiceType());
            if (null != serviceType) {
                afterSaleInfo.setTaskType(serviceType.getMessage());
            }
            //if (null != message && null != message.getMessageBody() && null != message.getMessageBody().getMilestoneInfoVO()) {
                //afterSaleInfo.setTaskType(message.getMessageBody().getMilestoneInfoVO().getOperationContext().get("taskType").toString());
                //afterSaleInfo.setTaskStatus(message.getMessageBody().getMilestoneInfoVO().getOperationContext().get("taskStatus").toString());
            //}
            afterSaleInfo.setAfterSaleCreateTime(message.getMessageBody().getWaybillInfoVO().getOpenBillTime());
            afterSaleInfo.setStatus(DeleteStatus.ENABLE.getCode());
            afterSaleInfo.setCreateTime(ts);
            afterSaleInfo.setUpdateTime(ts);
            if (null != message && null != message.getMessageBody() && null != message.getMessageBody().getExceptionInfoVO()) {
                afterSaleInfo.setExceptionCode(message.getMessageBody().getExceptionInfoVO().getExceptionCode());
            }
            if (null != message && null != message.getMessageBody() && null != message.getMessageBody().getExceptionInfoVO() && null != message.getMessageBody().getExceptionInfoVO().getActualDisposeTime() && null == message.getMessageBody().getExceptionInfoVO().getExceptionCode()) {
                afterSaleInfo.setAfterSaleCreateTime(message.getMessageBody().getExceptionInfoVO().getActualDisposeTime());
            }
            afterSaleInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
            afterSaleInfoExtMapper.insert(afterSaleInfo);
        }
        //commonMethod(message);
    }

    /**
     * 添加跟踪备注接口
     *
     * @param message
     * @throws Exception
     */
    public void trackingAddNotes(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 新建理赔接口
     *
     * @param message
     * @throws Exception
     */
    public void claimNew(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 处理理赔接口
     *
     * @param message
     * @throws Exception
     */
    public void claimsDeal(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 取消理赔接口
     *
     * @param message
     * @throws Exception
     */
    public void claimsCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 结案理赔接口
     *
     * @param message
     * @throws Exception
     */
    public void claimsClose(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 修改理赔接口
     *
     * @param message
     * @throws Exception
     */
    public void claimsModify(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 修改理赔接口
     *
     * @param message
     * @throws Exception
     */
    public void claimsImport(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 修改理赔接口
     *
     * @param message
     * @throws Exception
     */
    public void compensationImport(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 通融处理接口
     *
     * @param message
     * @throws Exception
     */
    public void accommodationDeal(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 新建追赔接口
     *
     * @param message
     * @throws Exception
     */
    public void compensationNew(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 处理追赔接口
     *
     * @param message
     * @throws Exception
     */
    public void compensationDeal(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 取消追赔接口
     *
     * @param message
     * @throws Exception
     */
    public void compensationCancel(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 结案追赔接口
     *
     * @param message
     * @throws Exception
     */
    public void compensationClose(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 判责确认接口
     *
     * @param message
     * @throws Exception
     */
    public void judgmentComfirm(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 修改承运商费用接口
     *
     * @param message
     * @throws Exception
     */
    public void carrierModifyFees(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 补偿费用接口
     *
     * @param message
     * @throws Exception
     */
    public void compensationFee(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 运单核销接口
     *
     * @param message
     * @throws Exception
     */
    public void checkWaybill(Message message) throws Exception {
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time，
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 运单作废接口
     *
     * @param message
     * @throws Exception
     */
    @Transactional
    public void cancelWaybill(Message message) throws Exception {
        //运单作废接口没有传运单状态故单独一个处理 mark by kdh
        // TODO: 2018/11/14
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.更新waybill_info表对应运单号的status为0（无效）
        String messageOperation = message.getOperation();
        String operationMessage = Operation.getEnumByEnumNameString(messageOperation).getMessage();
        log.info("调用" + operationMessage + "接口开始");
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        String waybillId = message.getWaybillId();
        MessageBody messageBody = message.getMessageBody();
        //里程碑
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        MilestoneInfo milestoneInfo = new MilestoneInfo();
        milestoneInfo.setId(OID.get().toString());
        //对象copy，将后一个对象中属性的值赋予给前一个
        //里程碑信息在消息头里面
        Beans.from(message).to(milestoneInfo);
        milestoneInfo.setWaybillId(waybillId);
        milestoneInfo.setStatus(DeleteStatus.ENABLE.getCode());
        milestoneInfo.setCreateTime(ts);
        milestoneInfo.setUpdateTime(ts);
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
            milestoneInfo.setOperationType(null);
        }
        milestoneInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
        if (StringUtils.isBlank(message.getOperator())) {
            milestoneInfo.setOperator(message.getOperationSys() + "系统");
            log.error("业务系统 " + message.getOperationSys() + " 没有将当前操作人传过来, 运单号为 " + waybillId);
        }
        milestoneInfoExtMapper.insert(milestoneInfo);

        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time
        WaybillInfo waybillInfo = waybillInfoExtMapper.selectByPrimaryKey(waybillId);
        if (waybillInfo != null) {
            waybillInfo.setWaybillStatus(WaybillStatus.OBSOLETE.getCode());
            waybillInfo.setStatus(DeleteStatus.DISABLE.getCode());//作废
            waybillInfo.setUpdateTime(ts);
            waybillInfoExtMapper.updateByPrimaryKeySelective(waybillInfo);
        } else {
            log.info("运单不存在 : " + waybillId);
        }
        log.info("调用" + operationMessage + "接口结束");
    }

    /**
     * 修改承运商信息
     *
     * @param message
     * @throws Exception
     */
    public void carrierModify(Message message) throws Exception {
        // TODO: 2018/11/17
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 移除外发交接
     *
     * @param message
     * @throws Exception
     */
    public void outDeliverRemove(Message message) throws Exception {
        // TODO: 2018/11/17
        // 1.将运单号在milestone_info及operation_detail两张表中当前最新的外发交接的记录的status设置为0，作废掉该记录
        // 2.修改waybill_info的运单状态
        commonMethod(message);
        /*Operation operation = Operation.getEnumByEnumNameString(message.getOperation());
        String operatinoMessage = operation.getMessage();
        log.debug("调用" + operatinoMessage + "接口开始");
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        String waybillId = message.getWaybillId();
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
            milestoneInfoExtMapper.insert(milestoneInfo);

            MessageBody messageBody = message.getMessageBody();
            if (messageBody == null) throw new RuntimeException("消息体不能为空!");

            //OUT_DELIVER DIRECT_TRANSFER 将operation_detail两张表中当前最新的外发交接的记录的status设置为删除，作废掉该记录
            List<Integer> keys = new ArrayList<Integer>();
            keys.add(Operation.OUT_DELIVER.getCode());
            keys.add(Operation.DIRECT_TRANSFER.getCode());
            List<OperationDetail> list = operationDetailExtMapper.selectByWaybillIdAndOparation(id, keys);
            if (list != null && list.size() > 0) {
                for (OperationDetail operationDetail : list) {
                    operationDetail.setStatus(DeleteStatus.DISABLE.getCode());
                    operationDetailExtMapper.updateByPrimaryKeySelective(operationDetail);
                }
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
                    Timestamp operateTime = (Timestamp) message.getOperateTime();//更新签收时间
                    waybillInfo.setSignTime(operateTime);
                    waybillInfo.setUpdateTime(ts);
                    waybillInfoExtMapper.updateByPrimaryKeySelective(waybillInfo);
                } else {
                    log.debug("运单不存在 : " + id);
                }
            }
        }
        log.debug("调用" + operatinoMessage + "接口结束");*/
    }

    /**
     * 确认短途
     *
     * @param message
     * @throws Exception
     */
    public void shortDeliverComfirm(Message message) throws Exception {
        // TODO: 2018/11/17
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time，
        // 3.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 移除短途交接
     *
     * @param message
     * @throws Exception
     */
    public void shortDeliverRemove(Message message) throws Exception {
        // TODO: 2018/11/17
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time，
        commonMethod(message);
    }

    /**
     * 撤销短途到达
     *
     * @param message
     * @throws Exception
     */
    public void shortDeliverArriveCancel(Message message) throws Exception {
        // TODO: 2018/11/17
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.milestoneInfo的operationContext的字段按列插入operation_detail表，同时插入运单号，创建时间，milestone_info.id
        commonMethod(message);
    }

    /**
     * 撤销短途
     *
     * @param message
     * @throws Exception
     */
    public void shortDeliverCancel(Message message) throws Exception {
        // TODO: 2018/11/17
        // 1.从messageFrom到nextNoderestictTime的字段插入milestone_info表，同时插入创建时间，更新时间
        // 2.waybillInfo的字段更新waybill_info表的waybill_status，同时更新update_time，
        commonMethod(message);
    }

    /**
     * 撤销直营收货
     *
     * @param message
     * @throws Exception
     */
    public void directReceiptCancel(Message message) throws Exception {
        commonMethod(message);
    }

    /**
     * 移除直营外发交接
     *
     * @param message
     * @throws Exception
     */
    public void directTransferRemove(Message message) throws Exception {
        commonMethod(message);
    }

    /**
     * 货物到达
     *
     * @param message
     * @throws Exception
     */
    public void goodsArrival(Message message) throws Exception {
        commonMethod(message);
    }

    /**
     * 返回客户
     *
     * @param message
     * @throws Exception
     */
    public void returnCoustomer(Message message) throws Exception {
        commonMethod(message);
    }

    /**
     * 确认直营外发
     *
     * @param message
     * @throws Exception
     */
    public void directComfirm(Message message) throws Exception {
        commonMethod(message);
    }

    /**
     * 撤销直营外发
     *
     * @param message
     * @throws Exception
     */
    public void directCancel(Message message) throws Exception {
        commonMethod(message);
    }

    /**
     * 绑单节点
     *
     * @param message
     * @throws Exception
     */
    public void bindBill(Message message) throws Exception {
        commonMethod(message);
    }

    /**
     * 解绑节点
     *
     * @param message
     * @throws Exception
     */
    public void unlashBill(Message message) throws Exception {
        commonMethod(message);
    }

    /**
     * 拣货节点
     *
     * @param message
     * @throws Exception
     */
    public void picking(Message message) throws Exception {
        commonMethod(message);
    }

    /**
     * 取消拣货节点
     *
     * @param message
     * @throws Exception
     */
    public void pickingCancel(Message message) throws Exception {
        commonMethod(message);
    }


}
