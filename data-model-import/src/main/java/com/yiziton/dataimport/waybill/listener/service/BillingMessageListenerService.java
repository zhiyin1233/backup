package com.yiziton.dataimport.waybill.listener.service;

import com.yiziton.commons.serialize.BeanJsonDeserializer;
import com.yiziton.commons.utils.bean.Beans;
import com.yiziton.commons.vo.MessageRedisCacheInfo;
import com.yiziton.commons.vo.enums.BillingObject;
import com.yiziton.commons.vo.enums.BillingType;
import com.yiziton.commons.vo.enums.DataType;
import com.yiziton.commons.vo.waybill.BillingInfoVO;
import com.yiziton.commons.vo.waybill.Message;
import com.yiziton.dataimport.commons.OrderProcessor;
import com.yiziton.dataimport.exception.bean.SysExceptionInfo;
import com.yiziton.dataimport.exception.dao.SysExceptionMapper;
import com.yiziton.dataimport.waybill.bean.BillingInfo;
import com.yiziton.dataimport.waybill.dao.ext.BillingInfoExtMapper;
import com.yiziton.dataimport.waybill.listener.WaybillMessageListener;
import com.yiziton.dataimport.waybill.service.BillingMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2019/1/2
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@Component
@Slf4j
public class BillingMessageListenerService {

    @Autowired
    BillingMessageService billingMessageService;

    @Autowired
    SysExceptionMapper sysExceptionMapper;

    @Autowired
    WaybillMessageListenerService waybillMessageListenerService;

    @Autowired
    BillingInfoExtMapper billingInfoExtMapper;


    @Autowired
    OrderProcessor orderProcessor;
    @Value("${orderno.sychronized:}")
    private boolean sychronized = false;

    public void listen(String data, Acknowledgment ack) throws Exception {
        Object messageObject = BeanJsonDeserializer.deserialize(data);
        if (messageObject instanceof Message) {
            Message message = (Message) messageObject;
            billingMessageService.billing(message, DataType.REAL_TIME_DATA.getCode());
        } else if (messageObject instanceof MessageRedisCacheInfo) {
            MessageRedisCacheInfo messageCacheInfo = (MessageRedisCacheInfo) messageObject;
            if (sychronized) {
                SysExceptionInfo sysExceptionInfo = sysExceptionMapper.selectByNoForGetAll(messageCacheInfo.getMessage().getWaybillId());
                List<MessageRedisCacheInfo> messageCacheInfoList = null;
                if (sysExceptionInfo != null) {
                    messageCacheInfoList = orderProcessor.getAllShouldHandleMsg(messageCacheInfo.getMessage().getWaybillId(), messageCacheInfo);
                } else {
                    messageCacheInfoList = orderProcessor.getShouldHandleMsg(messageCacheInfo.getMessage().getWaybillId(), messageCacheInfo);
                }
                if (messageCacheInfoList != null && messageCacheInfoList.size() > 0) {
                    for (MessageRedisCacheInfo msgInfo : messageCacheInfoList) {
                        Message message = msgInfo.getMessage();
                        if (StringUtils.isEmpty(message.getOperation())) {
                            deal(message, DataType.REAL_TIME_DATA.getCode());
                        } else {
                            waybillMessageListenerService.deal(message);
                        }
                    }
                }
            } else {
                Message message = messageCacheInfo.getMessage();
                deal(message, DataType.REAL_TIME_DATA.getCode());
            }
        }
    }

    public void deal(Message message, Integer code) throws Exception {
        BillingInfoVO billingInfoVO = message.getMessageBody().getBillingInfoVO();
        BillingInfo billingInfo = new BillingInfo();
        Beans.from(billingInfoVO).to(billingInfo);
        billingInfo.setWaybillId(message.getWaybillId());
        billingInfo.setSentTime(message.getSentTime());
        billingInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
        BillingObject billingObject = BillingObject.getEnumByEnumNameString(billingInfoVO.getBillingObject());
        if (billingObject != null) {
            billingInfo.setBillingObject(billingObject.getCode());
        } else {
            throw new RuntimeException("BillingObject 找不到对应枚举");
        }
        BillingType billingType = BillingType.getEnumByEnumNameString(billingInfoVO.getBillingType());
        if (billingType != null) {
            billingInfo.setBillingType(billingType.getCode());
        } else {
            throw new RuntimeException("BillingType 找不到对应枚举");
        }
        if (billingInfoExtMapper.selectCountForSame(billingInfo) > 0) {
            log.error("已经消费的Billing Message:" + message);
        } else {
            billingMessageService.billing(message, code);
        }
    }
}
