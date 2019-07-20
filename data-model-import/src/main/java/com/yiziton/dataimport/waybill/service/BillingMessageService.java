package com.yiziton.dataimport.waybill.service;

import com.yiziton.commons.utils.OID;
import com.yiziton.commons.utils.bean.Beans;
import com.yiziton.commons.vo.enums.*;
import com.yiziton.commons.vo.waybill.BillingInfoVO;
import com.yiziton.commons.vo.waybill.Message;
import com.yiziton.commons.vo.waybill.MessageBody;
import com.yiziton.dataimport.waybill.bean.BillingInfo;
import com.yiziton.dataimport.waybill.bean.FeeInfo;
import com.yiziton.dataimport.waybill.dao.*;
import com.yiziton.dataimport.waybill.dao.ext.BillingInfoExtMapper;
import com.yiziton.dataimport.waybill.dao.ext.FeeInfoExtMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
@Component("billingMessageService")
@Slf4j
public class BillingMessageService extends BaseService {

    @Autowired
    BillingInfoExtMapper billingInfoExtMapper;
    @Autowired
    ConsignorInfoMapper consignorInfoMapper;
    @Autowired
    ReceviceInfoMapper receviceInfoMapper;
    @Autowired
    ExceptionInfoMapper exceptionInfoMapper;
    @Autowired
    FeeInfoExtMapper feeInfoExtMapper;
    @Autowired
    GoodsInfoMapper goodsInfoMapper;
    @Autowired
    MilestoneInfoMapper milestoneInfoMapper;
    @Autowired
    OperationDetailMapper operationDetailMapper;
    @Autowired
    WaybillInfoMapper waybillInfoMapper;

    /**
     * 流水处理接口
     *
     * @param msg
     * @param dataType
     * @throws Exception
     */
    public void billing(Message msg, int dataType) throws Exception {
        log.info("调用流水接口开始");
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String waybillId = msg.getWaybillId();
        String operationClass = msg.getOperationClass();

        MessageBody messageBody = msg.getMessageBody();
        if (messageBody == null) {
            return;
        }

        BillingInfoVO billingInfoVO = messageBody.getBillingInfoVO();
        if (billingInfoVO == null) {
            return;
        }

        Map<String, Object> feeInfoVO = billingInfoVO.getFeeInfoVO();
        if (feeInfoVO == null) {
            return;
        }

        Set<String> keySet = feeInfoVO.keySet();
        String relatedBillId = billingInfoVO.getRelatedBillId();
        if (StringUtils.isEmpty(relatedBillId)) {
            relatedBillId = null;
            billingInfoVO.setRelatedBillId(relatedBillId);
        }
        BillingObject billingObject = BillingObject.getEnumByEnumNameString(billingInfoVO.getBillingObject());
        BillingType billingType = BillingType.getEnumByEnumNameString(billingInfoVO.getBillingType());
        Accounting accounting = Accounting.getEnumByEnumNameString(billingInfoVO.getAccounting());
        switch (operationClass) {
            case "C"://新增流水：C
                //正常流水： 发货人、承运商、服务商只有一条数据，避免多条新增的相同流水，先到库里面查询; 其他流水都累加
                if (billingType.equals(BillingType.NORMAL) && billingObject.equals(BillingObject.CARRIER)) {//解决承运商流水发两次的问题：确认外发/干结
                    BillingInfo billingInfo2 = new BillingInfo();
                    billingInfo2.setWaybillId(waybillId);
                    billingInfo2.setBillingObject(billingObject.getCode());
                    billingInfo2.setBillingType(billingType.getCode());
                    billingInfo2.setBillingName(billingInfoVO.getBillingName());
                    billingInfo2.setStatus(DeleteStatus.ENABLE.getCode());
                    billingInfo2.setRelatedBillId(relatedBillId);
                    BillingInfo billingInfo3 = billingInfoExtMapper.selectByParams(billingInfo2);
                    if (billingInfo3 != null) {//更新
                        updateBilling(ts, waybillId, billingInfoVO, feeInfoVO, keySet, billingInfo2, billingInfo3, dataType);
                        break;
                    }
                }
                saveBilling(msg, dataType, ts, waybillId, billingInfoVO, feeInfoVO, keySet, relatedBillId, billingObject, billingType, accounting);
                break;
            case "U"://更新流水：U//要修改，异常流水不用update
                log.info("Billing Message 更新流水 " + waybillId + " --> 耗时:" + (new Timestamp(System.currentTimeMillis()).getTime() - ts.getTime()));
                if (billingObject.equals(BillingObject.MASTER)) {
                    saveBilling(msg, dataType, ts, waybillId, billingInfoVO, feeInfoVO, keySet, relatedBillId, billingObject, billingType, accounting);
                } else {
                    BillingInfo billingInfo4 = new BillingInfo();
                    billingInfo4.setWaybillId(waybillId);
                    if(billingObject.equals(BillingObject.CUSTOMER) && billingType.equals(BillingType.NORMAL) && StringUtils.isBlank(relatedBillId)){
                        billingInfo4.setBillingObject(billingObject.getCode());
                        billingInfo4.setBillingType(billingType.getCode());
                        //billingInfo4.setBillingName(billingInfoVO.getBillingName());//改单时会出现修改发货人的情况，先删除流水明细，再更新
                        billingInfo4.setStatus(DeleteStatus.ENABLE.getCode());
                        billingInfo4.setRelatedBillId(relatedBillId);
                        BillingInfo billingInfo5 = billingInfoExtMapper.selectByParams(billingInfo4);
                        log.info("Billing Message 更新流水 " + waybillId + " 查询流水对象 --> 耗时:" + (new Timestamp(System.currentTimeMillis()).getTime() - ts.getTime()));
                        if (billingInfo5 != null) {//更新
                            updateCustomerBilling(ts, waybillId, billingInfoVO, feeInfoVO, keySet, billingInfo4, billingInfo5, dataType);
                        }
                    }else {
                        billingInfo4.setBillingObject(billingObject.getCode());
                        billingInfo4.setBillingType(billingType.getCode());
                        billingInfo4.setBillingName(billingInfoVO.getBillingName());
                        billingInfo4.setStatus(DeleteStatus.ENABLE.getCode());
                        billingInfo4.setRelatedBillId(relatedBillId);
                        BillingInfo billingInfo5 = billingInfoExtMapper.selectByParams(billingInfo4);
                        log.info("Billing Message 更新流水 " + waybillId + " 查询流水对象 --> 耗时:" + (new Timestamp(System.currentTimeMillis()).getTime() - ts.getTime()));
                        if (billingInfo5 != null) {//更新
                            updateBilling(ts, waybillId, billingInfoVO, feeInfoVO, keySet, billingInfo4, billingInfo5, dataType);
                        }
                    }
                }
                break;
            case "D"://作废了的流水：D
                BillingInfo billingInfo6 = new BillingInfo();
                billingInfo6.setWaybillId(waybillId);
                billingInfo6.setBillingObject(billingObject.getCode());
                billingInfo6.setBillingType(billingType.getCode());
                billingInfo6.setBillingName(billingInfoVO.getBillingName());
                billingInfo6.setStatus(DeleteStatus.ENABLE.getCode());
                billingInfo6.setRelatedBillId(relatedBillId);

                List<FeeInfo> feeInfoList = feeInfoExtMapper.selectListByParams(waybillId, billingObject.getCode(), billingType.getCode(), billingInfoVO.getBillingName(), relatedBillId, new ArrayList<String>(keySet));
                for (FeeInfo feeInfo1 : feeInfoList) {
                    feeInfo1.setStatus(DeleteStatus.DISABLE.getCode());
                    this.feeInfoExtMapper.updateByPrimaryKeySelective(feeInfo1);
                }
                List<BillingInfo> billingInfos2 = billingInfoExtMapper.selectListByParams(billingInfo6);
                if (billingInfos2 != null && billingInfos2.size() > 0) {
                    for (BillingInfo billingInfo1 : billingInfos2) {
                        billingInfo1.setStatus(DeleteStatus.DISABLE.getCode());
                        billingInfoExtMapper.updateByPrimaryKeySelective(billingInfo1);
                    }
                }
                break;
        }
        log.info("调用流水接口结束");
    }

    private void saveBilling(Message msg, int dataType, Timestamp ts, String waybillId, BillingInfoVO billingInfoVO, Map<String, Object> feeInfoVO, Set<String> keySet, String relatedBillId, BillingObject billingObject, BillingType billingType, Accounting accounting) {
        BillingInfo billingInfo = new BillingInfo();
        billingInfo.setId(OID.get().toString());
        billingInfo.setWaybillId(waybillId);
        billingInfo.setSentTime(msg.getSentTime());
        //对象copy，将后一个对象中属性的值赋予给前一个
        Beans.from(billingInfoVO).to(billingInfo);
        billingInfo.setStatus(DeleteStatus.ENABLE.getCode());
        billingInfo.setCreateTime(ts);
        billingInfo.setUpdateTime(ts);
        if (billingObject != null) {
            billingInfo.setBillingObject(billingObject.getCode());
        }
        if (billingType != null) {
            billingInfo.setBillingType(billingType.getCode());
        }
        if (accounting != null) {
            billingInfo.setAccounting(accounting.getCode());//应收应付
            if (accounting.equals(Accounting.PAY)) {
                billingInfo.setTotalPrice(billingInfo.getTotalPrice() == null ? 0.00 : (-billingInfo.getTotalPrice()));//应付时将钱取反
            }
        }
        SettlementType sttlementType = SettlementType.getEnumByEnumNameString(billingInfoVO.getSettlementType());
        if (sttlementType != null) {
            billingInfo.setSettlementType(sttlementType.getCode());//代收款、到货付款
        } else {
            billingInfo.setSettlementType(null);//代收款、到货付款
        }
        billingInfo.setDataType(dataType);
        billingInfo.setRelatedBillId(relatedBillId);
        this.billingInfoExtMapper.insert(billingInfo);

        FeeInfo feeInfo = null;
        String billingId = billingInfo.getId();
        for (String key : keySet) {
            feeInfo = new FeeInfo();
            feeInfo.setId(OID.get().toString());
            feeInfo.setWaybillId(waybillId);
            feeInfo.setBillingId(billingId);
            feeInfo.setFeeType(key);
            Object object = feeInfoVO.get(key);
            double tmp = 0.00;
            if (object instanceof Integer) {
                tmp = ((Integer) object).intValue();
            }
            if (object instanceof BigDecimal) {
                tmp = ((BigDecimal) object).doubleValue();
            }
            if (object instanceof Double) {
                tmp = (double) object;
            }
            feeInfo.setFee(tmp);
            feeInfo.setStatus(DeleteStatus.ENABLE.getCode());
            feeInfo.setCreateTime(ts);
            feeInfo.setUpdateTime(ts);
            feeInfo.setDataType(dataType);
            if (accounting != null) {
                feeInfo.setAccounting(accounting.getCode());//应收应付
                if (accounting.equals(Accounting.PAY)) {
                    feeInfo.setFee(-tmp);//应付时将钱取反
                }
            }
            this.feeInfoExtMapper.insert(feeInfo);
        }
    }

    /**
     * 更新流水
     *
     * @param ts
     * @param waybillId
     * @param billingInfoVO
     * @param feeInfoVO
     * @param keySet
     * @param billingInfo4
     * @param billingInfo5
     * @param dataType
     */
    private void updateBilling(Timestamp ts, String waybillId, BillingInfoVO billingInfoVO, Map<String, Object> feeInfoVO, Set<String> keySet, BillingInfo billingInfo4, BillingInfo billingInfo5, int dataType) {
        if (StringUtils.isNotBlank(billingInfo5.getId())) {
            billingInfo5.setTotalPrice(billingInfoVO.getTotalPrice());
            billingInfo5.setUpdateTime(ts);
            Accounting accounting = Accounting.getEnumByEnumNameString(billingInfoVO.getAccounting());
            if (accounting != null) {
                billingInfo5.setAccounting(accounting.getCode());//应收应付
                if (accounting.equals(Accounting.PAY)) {
                    billingInfo5.setTotalPrice(billingInfo5.getTotalPrice() == null ? 0.00 : (-billingInfo5.getTotalPrice()));//应付时将钱取反
                }
            }
            billingInfoExtMapper.updateByPrimaryKeySelective(billingInfo5);
            log.info("Billing Message 更新流水 " + waybillId + " 更新流水对象 --> 耗时:" + (new Timestamp(System.currentTimeMillis()).getTime() - ts.getTime()));

            List<String> feeTypeList = new ArrayList<String>(keySet);
            //keySet
            List<FeeInfo> list = feeInfoExtMapper.selectListByParams(waybillId, billingInfo4.getBillingObject(), billingInfo4.getBillingType(), billingInfoVO.getBillingName(), billingInfo4.getRelatedBillId(), feeTypeList);
            log.info("Billing Message 更新流水 " + waybillId + " 查询费用明细列表 --> 耗时:" + (new Timestamp(System.currentTimeMillis()).getTime() - ts.getTime()));
            for (String key : feeTypeList) {
                boolean exist = false;
                for (FeeInfo feeInfo : list) {
                    String feeType = feeInfo.getFeeType();
                    if (key.contains(feeType)) {
                        Object object = feeInfoVO.get(feeType);
                        double tmp = 0.00;
                        if (object instanceof Integer) {
                            tmp = ((Integer) object).intValue();
                        }
                        if (object instanceof BigDecimal) {
                            tmp = ((BigDecimal) object).doubleValue();
                        }
                        if (object instanceof Double) {
                            tmp = (double) object;
                        }
                        feeInfo.setFee(tmp);
                        if (accounting != null) {
                            feeInfo.setAccounting(accounting.getCode());//应收应付
                            if (accounting.equals(Accounting.PAY)) {
                                feeInfo.setFee(-tmp);//应付时将钱取反
                            }
                        }
                        this.feeInfoExtMapper.updateByPrimaryKeySelective(feeInfo);
                        exist = true;
                    }
                }
                log.info("Billing Message 更新流水 " + waybillId + " 存在明细更细明细 --> 耗时:" + (new Timestamp(System.currentTimeMillis()).getTime() - ts.getTime()));
                if (!exist) {
                    FeeInfo feeInfo = new FeeInfo();
                    feeInfo.setId(OID.get().toString());
                    feeInfo.setWaybillId(waybillId);
                    feeInfo.setBillingId(billingInfo5.getId());
                    feeInfo.setFeeType(key);
                    Object object = feeInfoVO.get(key);
                    double tmp = 0.00;
                    if (object instanceof Integer) {
                        tmp = ((Integer) object).intValue();
                    }
                    if (object instanceof BigDecimal) {
                        tmp = ((BigDecimal) object).doubleValue();
                    }
                    if (object instanceof Double) {
                        tmp = (double) object;
                    }
                    feeInfo.setFee(tmp);
                    feeInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    feeInfo.setCreateTime(ts);
                    feeInfo.setUpdateTime(ts);
                    feeInfo.setDataType(dataType);
                    if (accounting != null) {
                        feeInfo.setAccounting(accounting.getCode());//应收应付
                        if (accounting.equals(Accounting.PAY)) {
                            feeInfo.setFee(-tmp);//应付时将钱取反
                        }
                    }
                    this.feeInfoExtMapper.insert(feeInfo);
                }
                log.info("Billing Message 更新流水 " + waybillId + " 不存在明细更细明细 --> 耗时:" + (new Timestamp(System.currentTimeMillis()).getTime() - ts.getTime()));
            }
            log.info("Billing Message 更新流水 " + waybillId + " 结束 --> 耗时:" + (new Timestamp(System.currentTimeMillis()).getTime() - ts.getTime()));
        } else {
            log.debug("没有历史流水");
        }
    }

    /**
     * 更新流水 改单
     *
     * @param ts
     * @param waybillId
     * @param billingInfoVO
     * @param feeInfoVO
     * @param keySet
     * @param billingInfo4
     * @param billingInfo5
     * @param dataType
     */
    private void updateCustomerBilling(Timestamp ts, String waybillId, BillingInfoVO billingInfoVO, Map<String, Object> feeInfoVO, Set<String> keySet, BillingInfo billingInfo4, BillingInfo billingInfo5, int dataType) {
        if (StringUtils.isNotBlank(billingInfo5.getId())) {
            billingInfo5.setBillingName(billingInfoVO.getBillingName());// 有可能修改发货人
            billingInfo5.setTotalPrice(billingInfoVO.getTotalPrice());
            billingInfo5.setUpdateTime(ts);
            Accounting accounting = Accounting.getEnumByEnumNameString(billingInfoVO.getAccounting());
            if (accounting != null) {
                billingInfo5.setAccounting(accounting.getCode());//应收应付
                if (accounting.equals(Accounting.PAY)) {
                    billingInfo5.setTotalPrice(billingInfo5.getTotalPrice() == null ? 0.00 : (-billingInfo5.getTotalPrice()));//应付时将钱取反
                }
            }
            billingInfoExtMapper.updateByPrimaryKeySelective(billingInfo5);
            log.info("Billing Message 更新流水 " + waybillId + " 更新流水对象 --> 耗时:" + (new Timestamp(System.currentTimeMillis()).getTime() - ts.getTime()));

            //先删除原有费用明细
            List<String> arrayList = new ArrayList<>();
            arrayList.add(billingInfo5.getId());
            feeInfoExtMapper.updateByBillingIds(arrayList);

            List<String> feeTypeList = new ArrayList<String>(keySet);
            //keySet
            List<FeeInfo> list = feeInfoExtMapper.selectListByParams(waybillId, billingInfo4.getBillingObject(), billingInfo4.getBillingType(), billingInfoVO.getBillingName(), billingInfo4.getRelatedBillId(), feeTypeList);
            log.info("Billing Message 更新流水 " + waybillId + " 查询费用明细列表 --> 耗时:" + (new Timestamp(System.currentTimeMillis()).getTime() - ts.getTime()));
            for (String key : feeTypeList) {
                boolean exist = false;
                for (FeeInfo feeInfo : list) {
                    String feeType = feeInfo.getFeeType();
                    if (key.contains(feeType)) {
                        Object object = feeInfoVO.get(feeType);
                        double tmp = 0.00;
                        if (object instanceof Integer) {
                            tmp = ((Integer) object).intValue();
                        }
                        if (object instanceof BigDecimal) {
                            tmp = ((BigDecimal) object).doubleValue();
                        }
                        if (object instanceof Double) {
                            tmp = (double) object;
                        }
                        feeInfo.setFee(tmp);
                        if (accounting != null) {
                            feeInfo.setAccounting(accounting.getCode());//应收应付
                            if (accounting.equals(Accounting.PAY)) {
                                feeInfo.setFee(-tmp);//应付时将钱取反
                            }
                        }
                        this.feeInfoExtMapper.updateByPrimaryKeySelective(feeInfo);
                        exist = true;
                    }
                }
                log.info("Billing Message 更新流水 " + waybillId + " 存在明细更细明细 --> 耗时:" + (new Timestamp(System.currentTimeMillis()).getTime() - ts.getTime()));
                if (!exist) {
                    FeeInfo feeInfo = new FeeInfo();
                    feeInfo.setId(OID.get().toString());
                    feeInfo.setWaybillId(waybillId);
                    feeInfo.setBillingId(billingInfo5.getId());
                    feeInfo.setFeeType(key);
                    Object object = feeInfoVO.get(key);
                    double tmp = 0.00;
                    if (object instanceof Integer) {
                        tmp = ((Integer) object).intValue();
                    }
                    if (object instanceof BigDecimal) {
                        tmp = ((BigDecimal) object).doubleValue();
                    }
                    if (object instanceof Double) {
                        tmp = (double) object;
                    }
                    feeInfo.setFee(tmp);
                    feeInfo.setStatus(DeleteStatus.ENABLE.getCode());
                    feeInfo.setCreateTime(ts);
                    feeInfo.setUpdateTime(ts);
                    feeInfo.setDataType(dataType);
                    if (accounting != null) {
                        feeInfo.setAccounting(accounting.getCode());//应收应付
                        if (accounting.equals(Accounting.PAY)) {
                            feeInfo.setFee(-tmp);//应付时将钱取反
                        }
                    }
                    this.feeInfoExtMapper.insert(feeInfo);
                }
                log.info("Billing Message 更新流水 " + waybillId + " 不存在明细更细明细 --> 耗时:" + (new Timestamp(System.currentTimeMillis()).getTime() - ts.getTime()));
            }
            log.info("Billing Message 更新流水 " + waybillId + " 结束 --> 耗时:" + (new Timestamp(System.currentTimeMillis()).getTime() - ts.getTime()));
        } else {
            log.debug("没有历史流水");
        }
    }

}
