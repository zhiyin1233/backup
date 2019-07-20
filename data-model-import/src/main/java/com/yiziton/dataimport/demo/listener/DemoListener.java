package com.yiziton.dataimport.demo.listener;

import com.yiziton.commons.vo.demo.DemoBean;
import com.yiziton.dataimport.demo.dao.DemoMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/10/31
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Component
public class DemoListener {

    private Logger log = LoggerFactory.getLogger(DemoListener.class);

    @Autowired
    DemoMapper demoMapper;


    @KafkaListener(id = "demo",topics = "mytopic2")
    /*public void consumer(String message) {
        log.info("message = " + message);
    }*/
    public void listen(ConsumerRecord<?, ?> cr) throws Exception {
        DemoBean value = (DemoBean)cr.value();
        log.info(value.getName());
        demoMapper.insert(value);
    }
}
