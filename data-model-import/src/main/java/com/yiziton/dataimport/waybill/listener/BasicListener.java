package com.yiziton.dataimport.waybill.listener;

import com.yiziton.commons.vo.enums.TopicType;
import com.yiziton.dataimport.waybill.listener.service.BasicListenerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * @Description: 基础消息消费: 发货客户 customer.consignor，用户权限 organize.orgUser
 * @Author: kdh
 * @Date: 2019/7/19
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@Component
@Slf4j
public class BasicListener {

    @Autowired
    BasicListenerService basicListenerService;

    /**
     * topic消费
     *
     * @param data
     * @throws Exception
     */
    @KafkaListener(groupId = TopicType.BASIC_TOPIC_GROUP, topics = TopicType.BASIC_TOPIC, containerFactory = "kafkaManualAckListenerContainerFactory")
    public void listen(String data, Acknowledgment ack) throws Exception {
        basicListenerService.listen(data, ack);
    }
}
