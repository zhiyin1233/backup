package com.yiziton.dataimport.aspect;

import com.yiziton.dataimport.commons.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description: 基础消息消费: 发货客户 customer.consignor，用户权限 organize.orgUser
 * @Author: kdh
 * @Date: 2018/10/31
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Aspect
@Component
@Slf4j
public class BasicListenerAspect {

    @Autowired
    FileUtils fileUtils;

    @Around("execution(* com.yiziton.dataimport.waybill.listener.BasicListener.listen(..))")
    public void invoke(ProceedingJoinPoint joinPoint) throws Throwable{
        Long startTime = Calendar.getInstance().getTimeInMillis();
        String data = new String();
        for(Object arg : joinPoint.getArgs()){
            if(arg instanceof String){
                data = (String)arg;
                break;
            }
        }
        log.error("Basic Info 开始, 参数:"+data);

        fileUtils.saveFile(data,new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        Acknowledgment acknowledgment = null;
        try {
            acknowledgment = (Acknowledgment) joinPoint.getArgs()[1];
            joinPoint.proceed();
            acknowledgment.acknowledge();
        } catch (CommitFailedException e1){
            log.error("错误的消费 Basic Info:" + data);
            log.error("错误 Basic Info:" + e1.getMessage() + ",cause:" + e1.getCause());
            acknowledgment.acknowledge();
        }  catch (Exception e) {
            log.error("错误的消费 Basic Info:" + data);
            log.error("错误 Basic Info:" + e.getMessage() + ",cause:" + e.getCause());
            e.printStackTrace();
        }

        Long endTime = Calendar.getInstance().getTimeInMillis();
        log.info("OldBilling Message 结束, 参数:"+data+"--> 耗时:" + (endTime-startTime));
    }
}
