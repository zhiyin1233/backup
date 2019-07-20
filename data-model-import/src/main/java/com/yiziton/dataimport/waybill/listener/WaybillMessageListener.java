package com.yiziton.dataimport.waybill.listener;

import com.yiziton.commons.serialize.BeanJsonDeserializer;
import com.yiziton.commons.vo.MessageRedisCacheInfo;
import com.yiziton.commons.vo.enums.DataType;
import com.yiziton.commons.vo.enums.Operation;
import com.yiziton.commons.vo.enums.OperationProcessMethod;
import com.yiziton.commons.vo.enums.TopicType;
import com.yiziton.dataimport.commons.OrderProcessor;
import com.yiziton.dataimport.commons.SpringUtils;
import com.yiziton.commons.vo.waybill.*;
import com.yiziton.dataimport.exception.bean.SysExceptionInfo;
import com.yiziton.dataimport.exception.dao.SysExceptionMapper;
import com.yiziton.dataimport.waybill.bean.MilestoneInfo;
import com.yiziton.dataimport.waybill.dao.ext.MilestoneInfoExtMapper;
import com.yiziton.dataimport.waybill.listener.service.WaybillMessageListenerService;
import com.yiziton.dataimport.waybill.service.BillingMessageService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Description: 运单消息消费
 * @Author: kdh
 * @Date: 2018/10/31
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Component
@Slf4j
public class WaybillMessageListener {

    @Autowired
    WaybillMessageListenerService waybillMessageListenerService;

    /**
     * topic消费
     *
     * @param data
     * @throws Exception
     */
    @KafkaListener(id = TopicType.NODE_TOPIC_ID, groupId = TopicType.NODE_TOPIC_GROUP, topics = TopicType.NODE_TOPIC, containerFactory = "kafkaManualAckListenerContainerFactory")
    public void listen(String data, Acknowledgment ack) throws Exception {
        waybillMessageListenerService.listen(data,ack);
    }
}
