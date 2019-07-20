package com.yiziton.dataimport.waybill.listener;

import com.yiziton.commons.serialize.BeanJsonDeserializer;
import com.yiziton.commons.vo.enums.OperationProcessMethod;
import com.yiziton.commons.vo.enums.TopicType;
import com.yiziton.commons.vo.waybill.Message;
import com.yiziton.commons.vo.waybill.OldDataMessage;
import com.yiziton.dataimport.commons.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: 历史运单消息消费
 * @Author: kdh
 * @Date: 2018/10/31
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Component
@Slf4j
//@Transactional(propagation = Propagation.REQUIRED)
public class OldDataMessageListener {

    private String oldMessageService = "oldMessageService";

    @Value("${olddata.poolNum}")
    private int poolNum;

    //创建一个可重用固定线程数的线程池
    private static ExecutorService pool = null;

    @PostConstruct
    public void init() {
        pool = Executors.newFixedThreadPool(poolNum);
    }


    /**
     * topic消费
     *
     * @param data
     * @throws Exception
     */
    @KafkaListener(id = TopicType.OLD_DATA_TOPIC_ID, groupId = TopicType.OLD_DATA_TOPIC_GROUP, topics = TopicType.OLD_DATA_TOPIC, containerFactory = "kafkaManualAckListenerContainerFactory")
    public void listen(String data, Acknowledgment ack) throws Exception {
        OldDataMessage oldDataMessage = (OldDataMessage) BeanJsonDeserializer.deserialize(data);

        String dealMethod = oldDataMessage.getDealMethod();
        if (StringUtils.isEmpty(dealMethod)) {
            log.error("dealMethod不能为空");
            throw new RuntimeException("dealMethod不能为空");
        } else {
            OperationProcessMethod operationProcessMethod = OperationProcessMethod.getEnumByEnumNameString(dealMethod);
            if (operationProcessMethod != null) {
                log.info("调用" + oldMessageService + "的方法 : " + operationProcessMethod.getMessage());
                Object service = SpringUtils.getApplicationContext().getBean(oldMessageService);
                Method method = service.getClass().getMethod(operationProcessMethod.getMessage(), OldDataMessage.class);
                method.invoke(service, oldDataMessage);
                /*pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            method.invoke(service, oldDataMessage);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("处理历史数据错误:处理历史数据错误出现IllegalAccessException异常!",e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException("处理历史数据错误:处理历史数据错误出现InvocationTargetException异常!",e);
                        }
                    }
                });*/

            } else {
                throw new RuntimeException("dealMethod 找不到对应枚举");
            }
        }
    }

}
