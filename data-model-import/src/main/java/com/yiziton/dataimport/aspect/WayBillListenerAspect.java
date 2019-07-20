package com.yiziton.dataimport.aspect;

import com.yiziton.commons.serialize.BeanJsonDeserializer;
import com.yiziton.commons.utils.OID;
import com.yiziton.commons.vo.MessageRedisCacheInfo;
import com.yiziton.commons.vo.enums.DataType;
import com.yiziton.commons.vo.enums.Operation;
import com.yiziton.commons.vo.enums.OperationProcessMethod;
import com.yiziton.commons.vo.waybill.Message;
import com.yiziton.dataimport.commons.ErrorUtil;
import com.yiziton.dataimport.commons.FileUtils;
import com.yiziton.dataimport.commons.OrderProcessor;
import com.yiziton.dataimport.exception.bean.SysExceptionInfo;
import com.yiziton.dataimport.exception.dao.SysExceptionMapper;
import com.yiziton.dataimport.waybill.bean.MilestoneInfo;
import com.yiziton.dataimport.waybill.dao.ext.MilestoneInfoExtMapper;
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
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/11/16
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Aspect
@Component
@Slf4j
public class WayBillListenerAspect {

    @Autowired
    SysExceptionMapper sysExceptionMapper;

    @Autowired
    MilestoneInfoExtMapper milestoneInfoExtMapper;

    @Autowired
    FileUtils fileUtils;

    @Autowired
    OrderProcessor orderProcessor;

    @Around("execution(* com.yiziton.dataimport.waybill.listener.service.WaybillMessageListenerService.listen(..))")
    public void invoke(ProceedingJoinPoint joinPoint) throws Throwable {
        Long startTime = Calendar.getInstance().getTimeInMillis();
        String data = new String();
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof String) {
                data = (String) arg;
                break;
            }
        }
        log.info("WaybillMessage 开始, 参数:" + data);

        fileUtils.saveFile(data,new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        Message message = null;
        Acknowledgment ack = null;
        try {
            Object messageObject =  BeanJsonDeserializer.deserialize(data);
            if(messageObject instanceof Message){
                message = (Message) messageObject;
            }else {
                message = ((MessageRedisCacheInfo) BeanJsonDeserializer.deserialize(data)).getMessage();
            }
            ack = (Acknowledgment) joinPoint.getArgs()[1];
            int count = sysExceptionMapper.selectCountByNoAndNoTypeAndDataType(message.getWaybillId(), "waybill", DataType.REAL_TIME_DATA.getCode());
            //异常信息处理
            if (count > 0) {
                throw new RuntimeException("该运单号在采集时存在信息报错");
            }
            if (null == message.getWaybillId()) {
                throw new RuntimeException("运单号不能为空!");
            }
            Operation operation = Operation.getEnumByEnumNameString(message.getOperation());
            if (operation == null) {
                throw new RuntimeException("operation 找不到对应枚举");
            }
            //业务幂等性判断
            MilestoneInfo milestoneInfo = new MilestoneInfo();
            milestoneInfo.setWaybillId(message.getWaybillId());
            milestoneInfo.setSentTime(message.getSentTime());
            milestoneInfo.setOperation(operation.getCode());
            if (milestoneInfoExtMapper.selectCountForSame(milestoneInfo) > 0) {
                log.error("已经消费的 Waybill Message:" + data);
                if(messageObject instanceof MessageRedisCacheInfo)
                    orderProcessor.putUnhandleMsgAndDealOrderFixNo(message.getWaybillId(), (MessageRedisCacheInfo) messageObject);
            } else {
                log.info("消息进入执行 Waybill Message:" + data);
                joinPoint.proceed();
            }
            ack.acknowledge();
        } catch (CommitFailedException e1){
            log.debug("错误的消费 Waybill Message:" + data);
            log.error("错误 Waybill Message:" + e1.getMessage() + ",cause:" + e1.getCause());
            ack.acknowledge();
        } catch (Exception e) {
            log.debug("错误的消费 Waybill Message:" + data);
            log.error("错误 Waybill Message:" + e.getMessage() + ",cause:" + e.getCause());
            e.printStackTrace();
            try {
                Object messageObject =  BeanJsonDeserializer.deserialize(data);
                if(messageObject instanceof Message){
                    message = (Message) messageObject;
                }else {
                    message = ((MessageRedisCacheInfo) BeanJsonDeserializer.deserialize(data)).getMessage();
                }

                SysExceptionInfo sysExceptionInfo = new SysExceptionInfo();
                String waybillId = message.getWaybillId();
                sysExceptionInfo.setNo(waybillId.length() < 500 ? waybillId: waybillId.substring(0,500));
                sysExceptionInfo.setId(OID.get().toString());
                sysExceptionInfo.setSource(message.getMessageFrom());

                sysExceptionInfo.setExceptionBody(data.length() < 7999 ? data : data.substring(0, 7999));
                if (e.getCause() != null) {
                    String errMsg = e.getCause().toString();
                    errMsg = errMsg + ":" + ErrorUtil.parseError(e);
                    sysExceptionInfo.setExceptionInfo(errMsg.length() < 7999 ? errMsg : errMsg.substring(0, 7999));
                } else if (e.getMessage() != null) {
                    String errMsg = e.getMessage();
                    errMsg = errMsg + ":" + ErrorUtil.parseError(e);
                    sysExceptionInfo.setExceptionInfo(errMsg.length() < 7999 ? errMsg : errMsg.substring(0, 7999));
                }
                sysExceptionInfo.setNoType("waybill");
                sysExceptionInfo.setSentTime(message.getSentTime());
                sysExceptionInfo.setDataType(DataType.REAL_TIME_DATA.getCode());

                sysExceptionMapper.insert(sysExceptionInfo);

                if(messageObject instanceof MessageRedisCacheInfo)
                    orderProcessor.putUnhandleMsgAndDealOrderFixNo(message.getWaybillId(), (MessageRedisCacheInfo) messageObject);

                ack.acknowledge();
            } catch (Exception e2) {
                log.debug("错误的保存异常 Waybill Message:" + data);
                log.error("错误的异常 Waybill Message:" + e2.getMessage() + ",cause:" + e2.getCause());
                ack.acknowledge();
            }
        }
        Long endTime = Calendar.getInstance().getTimeInMillis();
        log.info("Waybill Message 结束, 参数:" + data + "--> 耗时:" + (endTime - startTime));
    }



}
