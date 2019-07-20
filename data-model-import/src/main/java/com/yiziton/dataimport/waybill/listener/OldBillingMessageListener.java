package com.yiziton.dataimport.waybill.listener;

import com.yiziton.commons.serialize.BeanJsonDeserializer;
import com.yiziton.commons.vo.enums.DataType;
import com.yiziton.commons.vo.enums.TopicType;
import com.yiziton.commons.vo.waybill.Message;
import com.yiziton.dataimport.waybill.service.BillingMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: 历史流水消息消费
 * @Author: kdh
 * @Date: 2018/10/31
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Component
@Slf4j
public class OldBillingMessageListener {

    @Autowired
    BillingMessageService billingMessageService;

    @Value("${oldFinanceData.poolNum}")
    private int poolNum;

    //创建一个可重用固定线程数的线程池
    private static ExecutorService pool = null;

    @PostConstruct
    public void init() {
        pool = Executors.newFixedThreadPool(poolNum);
    }

    /**
     * 消费topic
     *
     * @param data
     * @throws Exception
     */
    @KafkaListener(id = TopicType.OLD_FINANCE_TOPIC_ID, groupId = TopicType.OLD_FINANCE_TOPIC_GROUP, topics = TopicType.OLD_FINANCE_TOPIC, containerFactory = "kafkaManualAckListenerContainerFactory")
    public void listen(String data, Acknowledgment ack) throws Exception {
        Message message = (Message) BeanJsonDeserializer.deserialize(data);
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    billingMessageService.billing(message, DataType.HISTORICAL_DATA.getCode());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("处理历史流水数据错误出现IllegalAccessException异常!",e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("处理历史流水数据错误出现InvocationTargetException异常!",e);
                } catch (Exception e) {
                    throw new RuntimeException("处理历史流水数据错误出现异常!",e);
                }
            }
        });

    }

}
