package com.yiziton.dataimport.config;

import com.yiziton.commons.serialize.BeanJsonDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/10/31
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${kafka.services}")
    private String services;
    @Value("${kafka.AUTO_COMMIT_INTERVAL_MS_CONFIG:}")
    private int AUTO_COMMIT_INTERVAL_MS_CONFIG;

    @Value("${kafka.SESSION_TIMEOUT_MS_CONFIG:}")
    private int SESSION_TIMEOUT_MS_CONFIG;

    @Value("${kafka.MAX_POLL_INTERVAL_MS_CONFIG:}")
    private int MAX_POLL_INTERVAL_MS_CONFIG;

    @Value("${kafka.MAX_POLL_RECORDS_CONFIG:}")
    private int MAX_POLL_RECORDS_CONFIG;

    @Bean
    ConcurrentKafkaListenerContainerFactory<Integer, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<Integer, String> kafkaManualAckListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);
        //设置5个并发
        factory.setConcurrency(5);
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,services);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);
        //props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,AUTO_COMMIT_INTERVAL_MS_CONFIG);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        if(!StringUtils.isEmpty(SESSION_TIMEOUT_MS_CONFIG)){
            props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,SESSION_TIMEOUT_MS_CONFIG);
        }
        if(!StringUtils.isEmpty(MAX_POLL_INTERVAL_MS_CONFIG)){
            props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,MAX_POLL_INTERVAL_MS_CONFIG);
        }
        if(!StringUtils.isEmpty(MAX_POLL_RECORDS_CONFIG)){
            props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,MAX_POLL_RECORDS_CONFIG);
        }
        return props;
    }

}
