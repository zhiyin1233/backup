package com.yiziton.dataimport.waybill.listener;

import com.yiziton.commons.serialize.BeanJsonDeserializer;
import com.yiziton.commons.utils.bean.Beans;
import com.yiziton.commons.vo.MessageRedisCacheInfo;
import com.yiziton.commons.vo.enums.BillingObject;
import com.yiziton.commons.vo.enums.BillingType;
import com.yiziton.commons.vo.enums.DataType;
import com.yiziton.commons.vo.enums.TopicType;
import com.yiziton.commons.vo.waybill.BillingInfoVO;
import com.yiziton.commons.vo.waybill.Message;
import com.yiziton.dataimport.commons.OrderProcessor;
import com.yiziton.dataimport.exception.bean.SysExceptionInfo;
import com.yiziton.dataimport.exception.dao.SysExceptionMapper;
import com.yiziton.dataimport.waybill.bean.BillingInfo;
import com.yiziton.dataimport.waybill.dao.ext.BillingInfoExtMapper;
import com.yiziton.dataimport.waybill.listener.service.BillingMessageListenerService;
import com.yiziton.dataimport.waybill.service.BillingMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Description: 流水消息消费
 * @Author: kdh
 * @Date: 2018/10/31
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Component
@Slf4j
public class BillingMessageListener {

    @Autowired
    BillingMessageListenerService billingMessageListenerService;

    /**
     * 消费topic
     *
     * @param data
     * @throws Exception
     */
    @KafkaListener(id = TopicType.FINANCE_TOPIC_ID, groupId = TopicType.FINANCE_TOPIC_GROUP, topics = TopicType.FINANCE_TOPIC, containerFactory = "kafkaManualAckListenerContainerFactory")
    public void listen(String data, Acknowledgment ack) throws Exception {
        billingMessageListenerService.listen(data,ack);
    }
}
