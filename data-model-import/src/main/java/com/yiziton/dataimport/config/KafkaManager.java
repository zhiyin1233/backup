package com.yiziton.dataimport.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

/**
 * <p>Description: Kafka工具类</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: YZT</p>
 *
 * @author TY
 * @version 1.0
 * @date 2019/07/03 11:06
 */
@Component
@Slf4j
public class KafkaManager {

    //public static final String KAFKA_LISTENER_ID = TopicType.NODE_TOPIC_ID;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    public void stop(String TopicID) {
        log.info(TopicID + " Kafkalistener stop...");
        registry.getListenerContainer(TopicID).stop();
    }
    public void start(String TopicID) {
        log.info(TopicID + " Kafkalistener start...");
        registry.getListenerContainer(TopicID).start();
    }
}
