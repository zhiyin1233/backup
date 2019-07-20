package com.yiziton.dataimport.waybill.service;

import com.yiziton.commons.vo.waybill.*;
import com.yiziton.dataimport.waybill.bean.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

/**
 * <p>Description: 历史消息处理服务类</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: YZT</p>
 *
 * @author TY
 * @version 1.0
 * @date 2018/11/24 14:45
 */
@Service("oldMessageService")
@Slf4j
public class OldMessageService extends BaseService {

    //@Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void oldDataMessage(OldDataMessage oldDataMessage) throws Exception {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        if (oldDataMessage.getOperationClass().equals("U") && "IPS".equals(oldDataMessage.getMessageFrom())) {
            //先删除
            deleteCompensationAndMilestoneAndOperationContext(oldDataMessage);
            //插入轨迹表
            instMilestoneAndOperationContext(oldDataMessage, ts);
            //赔(追)偿处理
            instCompensation(oldDataMessage, ts);
        } else if (oldDataMessage.getOperationClass().equals("C")) {
            if (null != oldDataMessage && null != oldDataMessage.getOldMessageBody()) {
                //再次同步时删除原有数据
                deleteOldData(oldDataMessage);
                // 1.取message 到apiName跟milestoneInfo的relatedBillId到operationOganization（可能是list）合并插入到milestone_info,同时产生ID，创建时间，更新时间
                instMilestoneAndOperationContext(oldDataMessage, ts);

                if ("CMP".equals(oldDataMessage.getMessageFrom())) {
                    //插入承运商表
                    CarrierInfoVO carrierInfoVO = oldDataMessage.getOldMessageBody().getCarrierInfoVO();
                    if (null != carrierInfoVO) {
                        CarrierInfo carrierInfo = getCarrierInfo(oldDataMessage.getWaybillId(), ts, carrierInfoVO);
                        carrierInfoExtMapper.insert(carrierInfo);
                    }
                } else {
                    //log.info("====================================info================================================");
                    //log.info("OldMessageService:"+ oldDataMessage.getWaybillId() + oldDataMessage);
                    //if (0 < waybillInfoExtMapper.selectCountById(oldDataMessage.getWaybillId())) {
                    //    throw new RuntimeException(oldmsg + "运单表已存在此运单号!");
                    //}
                    //log.info("====================================================================================");
                    // 3.将waybillInfo里的字段插入运单表，运单号，创建时间，更新时间
                    WaybillInfoVO waybillInfoVO = oldDataMessage.getOldMessageBody().getWaybillInfoVO();
                    if (null != waybillInfoVO) {
                        WaybillInfo waybillInfo = getWaybillInfo(oldDataMessage.getWaybillId(), ts, waybillInfoVO);
                        waybillInfoExtMapper.insert(waybillInfo);
                    }
                    // 4.将consignorInfo里的字段插入发货人表，运单号，创建时间，更新时间
                    ConsignorInfoVO consignorInfoVO = oldDataMessage.getOldMessageBody().getConsignorInfoVO();
                    if (null != consignorInfoVO) {
                        ConsignorInfo consignorInfo = getConsignorInfo(oldDataMessage.getWaybillId(), ts, consignorInfoVO);
                        consignorInfoExtMapper.insert(consignorInfo);
                    }
                    // 5.将receviceInfo里的字段插入收货人表，运单号，创建时间，更新时间
                    ReceviceInfoVO receviceInfoVO = oldDataMessage.getOldMessageBody().getReceviceInfoVO();
                    if (null != receviceInfoVO) {
                        ReceviceInfo receviceInfo = getReceviceInfo(oldDataMessage.getWaybillId(), ts, receviceInfoVO);
                        receviceInfoExtMapper.insert(receviceInfo);
                    }

                    //插入服务商表
                    List<MasterInfoVO> masterInfoVOList = oldDataMessage.getOldMessageBody().getMasterInfoVOList();
                    if (null != masterInfoVOList && 0 < masterInfoVOList.size()) {
                        masterInfoVOList.stream().parallel().forEach(masterInfoVO -> {
                            MasterInfo masterInfo = getMasterInfo(oldDataMessage.getWaybillId(), ts, masterInfoVO);
                            masterInfoExtMapper.insert(masterInfo);
                        });

                    }
                    //插入承运商表
                    CarrierInfoVO carrierInfoVO = oldDataMessage.getOldMessageBody().getCarrierInfoVO();
                    if (null != carrierInfoVO) {
                        CarrierInfo carrierInfo = getCarrierInfo(oldDataMessage.getWaybillId(), ts, carrierInfoVO);
                        carrierInfoExtMapper.insert(carrierInfo);
                    }
                    //插入单号关联表bill_relation_info
                    BillRelationInfoVO billRelationInfoVO = oldDataMessage.getOldMessageBody().getBillRelationInfoVO();
                    if (null != billRelationInfoVO) {
                        String orderBillId = billRelationInfoVO.getOrderBillId();
                        String customerBillId = billRelationInfoVO.getCustomerBillId();
                        if (StringUtils.isNoneBlank(orderBillId)) {
                            String[] orderBillIdArray = orderBillId.split(",");
                            for (int i = 0; i < orderBillIdArray.length; i++) {
                                String obId = orderBillIdArray[i];
                                BillRelationInfo billRelationInfo = getBillRelationInfo(oldDataMessage.getWaybillId(), ts, customerBillId, obId);
                                billRelationInfoExtMapper.insert(billRelationInfo);
                            }
                        }
                    }
                }
                // 6.将goodsInfo里的字段插入商品表，运单号，创建时间，更新时间
                if (null != oldDataMessage.getOldMessageBody().getGoodsInfoVOList() && 0 < oldDataMessage.getOldMessageBody().getGoodsInfoVOList().size()) {
                    oldDataMessage.getOldMessageBody().getGoodsInfoVOList().stream().parallel().forEach(goodsInfoVO -> {
                        GoodsInfo goodsInfo = getGoodsInfo(oldDataMessage.getWaybillId(), ts, goodsInfoVO);
                        goodsInfoExtMapper.insert(goodsInfo);
                    });
                /*for (GoodsInfoVO goodsInfoVO : oldDataMessage.getOldMessageBody().getGoodsInfoVOList()) {
                    GoodsInfo goodsInfo = getGoodsInfo(oldDataMessage.getWaybillId(), ts, goodsInfoVO);
                    goodsInfoExtMapper.insert(goodsInfo);
                }*/
                }
                // 7.将exceptionInfo里除了afterSaleInfo以外的字段插入异常表，运单号，创建时间，更新时间
                if (null != oldDataMessage.getOldMessageBody().getOldExceptionInfoVOList() && 0 < oldDataMessage.getOldMessageBody().getOldExceptionInfoVOList().size()) {
                    oldDataMessage.getOldMessageBody().getOldExceptionInfoVOList().stream().parallel().forEach(oldExceptionInfoVo -> {
                        ExceptionInfo exceptionInfo = getExceptionInfo(oldDataMessage.getWaybillId(), ts, oldExceptionInfoVo);
                        exceptionInfoExtMapper.insert(exceptionInfo);
                        // 8.将afterSaleInfo里的字段，exceptionInfo里的异常编号，运单号，创建时间，更新时间 插入售后表
                        if (null != oldExceptionInfoVo.getAfterSaleInfoVOList() && 0 < oldExceptionInfoVo.getAfterSaleInfoVOList().size()) {
                            oldExceptionInfoVo.getAfterSaleInfoVOList().stream().parallel().forEach(afterSaleInfoVo -> {
                                AfterSaleInfo afterSaleInfo = getAfterSaleInfo(oldDataMessage.getWaybillId(), ts, oldExceptionInfoVo, afterSaleInfoVo);
                                afterSaleInfoExtMapper.insert(afterSaleInfo);
                            });
                        /*for (AfterSaleInfoVO afterSaleInfoVo : oldExceptionInfoVo.getAfterSaleInfoVOList()) {
                            AfterSaleInfo afterSaleInfo = getAfterSaleInfo(oldDataMessage.getWaybillId(), ts, oldExceptionInfoVo, afterSaleInfoVo);
                            afterSaleInfoExtMapper.insert(afterSaleInfo);
                        }*/
                        }
                    });
                /*for (OldExceptionInfoVO oldExceptionInfoVo : oldDataMessage.getOldMessageBody().getOldExceptionInfoVOList()) {
                    ExceptionInfo exceptionInfo = getExceptionInfo(oldDataMessage.getWaybillId(), ts, oldExceptionInfoVo);
                    exceptionInfoExtMapper.insert(exceptionInfo);
                    // 8.将afterSaleInfo里的字段，exceptionInfo里的异常编号，运单号，创建时间，更新时间 插入售后表
                    if (null != oldExceptionInfoVo.getAfterSaleInfoVOList() && 0 < oldExceptionInfoVo.getAfterSaleInfoVOList().size()) {
                        for (AfterSaleInfoVO afterSaleInfoVo : oldExceptionInfoVo.getAfterSaleInfoVOList()) {
                            AfterSaleInfo afterSaleInfo = getAfterSaleInfo(oldDataMessage.getWaybillId(), ts, oldExceptionInfoVo, afterSaleInfoVo);
                            afterSaleInfoExtMapper.insert(afterSaleInfo);
                        }
                    }
                }*/
                }
                // 8.赔(追)偿处理
                instCompensation(oldDataMessage, ts);
            }
        } else {
            throw new RuntimeException(oldmsg + "OperationClass都没传，不知道你想干什么!");

        }
    }

}
