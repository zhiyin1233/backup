package com.yiziton.dataimport.aspect;

import com.yiziton.commons.serialize.BeanJsonDeserializer;
import com.yiziton.commons.utils.OID;
import com.yiziton.commons.utils.bean.Beans;
import com.yiziton.commons.vo.MessageRedisCacheInfo;
import com.yiziton.commons.vo.enums.BillingObject;
import com.yiziton.commons.vo.enums.BillingType;
import com.yiziton.commons.vo.enums.DataType;
import com.yiziton.commons.vo.waybill.BillingInfoVO;
import com.yiziton.commons.vo.waybill.Message;
import com.yiziton.commons.vo.waybill.MessageBody;
import com.yiziton.dataimport.commons.ErrorUtil;
import com.yiziton.dataimport.commons.FileUtils;
import com.yiziton.dataimport.commons.OrderProcessor;
import com.yiziton.dataimport.exception.bean.SysExceptionInfo;
import com.yiziton.dataimport.exception.dao.SysExceptionMapper;
import com.yiziton.dataimport.waybill.bean.BillingInfo;
import com.yiziton.dataimport.waybill.dao.ext.BillingInfoExtMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @Description:
 * @Author: kdh
 * @Date: 2018/11/16
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Aspect
@Component
@Slf4j
public class BillingListenerAspect {

    @Autowired
    OrderProcessor orderProcessor;

    @Autowired
    SysExceptionMapper sysExceptionMapper;

    @Autowired
    BillingInfoExtMapper billingInfoExtMapper;

    @Autowired
    FileUtils fileUtils;

    @Around("execution(* com.yiziton.dataimport.waybill.listener.service.BillingMessageListenerService.listen(..))")
    public void invoke(ProceedingJoinPoint joinPoint) throws Throwable{
        Long startTime = Calendar.getInstance().getTimeInMillis();
        String data = new String();

        for(Object arg : joinPoint.getArgs()){
            if(arg instanceof String){
                data = (String)arg;
                break;
            }
        }

        log.debug("Billing Message 开始, 参数:"+data);

        fileUtils.saveFile(data,new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        Message message = null;
        Acknowledgment acknowledgment = null;
        try {
            Object messageObject =  BeanJsonDeserializer.deserialize(data);
            if(messageObject instanceof Message){
                message = (Message) messageObject;
            }else {
                message = ((MessageRedisCacheInfo) BeanJsonDeserializer.deserialize(data)).getMessage();
            }
            acknowledgment = (Acknowledgment) joinPoint.getArgs()[1];
            if (null == message.getWaybillId()) {
                throw new RuntimeException("运单号不能为空!");
            }
            //业务幂等性判断
            MessageBody messageBody = message.getMessageBody();
            if (messageBody == null) throw new RuntimeException("流水消息体不能为空!");

            BillingInfoVO billingInfoVO = messageBody.getBillingInfoVO();
            if (billingInfoVO == null) throw new RuntimeException("流水对象不能为空!");

            Map<String, Object> feeInfo = billingInfoVO.getFeeInfoVO();
            if (feeInfo == null) throw new RuntimeException("流水详细不能为空!");

            BillingInfo billingInfo = new BillingInfo();
            Beans.from(billingInfoVO).to(billingInfo);
            BillingObject billingObject = BillingObject.getEnumByEnumNameString(billingInfoVO.getBillingObject());
            if(billingObject != null){
                billingInfo.setBillingObject(billingObject.getCode());
            }else{
                throw new RuntimeException("BillingObject 找不到对应枚举");
            }
            BillingType billingType = BillingType.getEnumByEnumNameString(billingInfoVO.getBillingType());
            if(billingType != null){
                billingInfo.setBillingType(billingType.getCode());
            }else{
                throw new RuntimeException("BillingType 找不到对应枚举");
            }
            billingInfo.setWaybillId(message.getWaybillId());
            billingInfo.setSentTime(message.getSentTime());
            billingInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
            if (billingInfoExtMapper.selectCountForSame(billingInfo) > 0) {
                log.info("已经消费的 Billing Message:" + data);
                if(messageObject instanceof MessageRedisCacheInfo)
                    orderProcessor.putUnhandleMsgAndDealOrderFixNo(message.getWaybillId(), (MessageRedisCacheInfo) messageObject);
            } else {
                log.info("消息进入执行 Billing Message:" + data);
                joinPoint.proceed();
            }
            acknowledgment.acknowledge();
        }catch (CommitFailedException e1){
            log.debug("错误的消费 Billing Message:" + data);
            log.error("错误 Billing Message:" + e1.getMessage() + ",cause:" + e1.getCause());
            acknowledgment.acknowledge();
        }   catch (Exception e) {
            log.debug("错误的消费 Billing Message:" + data);
            log.error("错误 Billing Message:" + e.getMessage() + ",cause:" + e.getCause());
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
                sysExceptionInfo.setNoType("billing");
                sysExceptionInfo.setSentTime(message.getSentTime());
                sysExceptionInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
                sysExceptionMapper.insert(sysExceptionInfo);

                if(messageObject instanceof MessageRedisCacheInfo)
                    orderProcessor.putUnhandleMsgAndDealOrderFixNo(message.getWaybillId(), (MessageRedisCacheInfo) messageObject);

                acknowledgment.acknowledge();
            } catch (Exception e2) {
                log.debug("错误的保存异常 Billing Message:" + data);
                log.error("错误的异常 Billing Message:" + e2.getMessage() + ",cause:" + e2.getCause());
                acknowledgment.acknowledge();
            }
        }

        Long endTime = Calendar.getInstance().getTimeInMillis();
        log.info("Billing Message 结束, 参数:"+data+"--> 耗时:" + (endTime-startTime));
    }
}
