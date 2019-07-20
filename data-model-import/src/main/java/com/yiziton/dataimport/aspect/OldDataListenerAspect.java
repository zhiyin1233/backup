package com.yiziton.dataimport.aspect;

import com.yiziton.commons.serialize.BeanJsonDeserializer;
import com.yiziton.commons.utils.OID;
import com.yiziton.commons.vo.enums.DataType;
import com.yiziton.commons.vo.enums.OperationProcessMethod;
import com.yiziton.commons.vo.waybill.OldDataMessage;
import com.yiziton.dataimport.commons.ErrorUtil;
import com.yiziton.dataimport.commons.FileUtils;
import com.yiziton.dataimport.exception.bean.SysExceptionInfo;
import com.yiziton.dataimport.exception.dao.SysExceptionMapper;
import com.yiziton.dataimport.waybill.bean.MilestoneInfo;
import com.yiziton.dataimport.waybill.dao.ext.MilestoneInfoExtMapper;
import com.yiziton.dataimport.waybill.dao.ext.WaybillInfoExtMapper;
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
 * <p>Description: 历史数据异常处理类</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: YZT</p>
 *
 * @author TY
 * @version 1.0
 * @date 2018/11/24 14:30
 */

@Aspect
@Component
@Slf4j
public class OldDataListenerAspect {

    @Autowired
    SysExceptionMapper sysExceptionMapper;

    @Autowired
    MilestoneInfoExtMapper milestoneInfoExtMapper;

    @Autowired
    FileUtils fileUtils;

    //@Autowired
    //WaybillInfoExtMapper waybillInfoExtMapper;


    @Around("execution(* com.yiziton.dataimport.waybill.listener.OldDataMessageListener.listen(..))")
    public void invoke(ProceedingJoinPoint joinPoint) throws Throwable {
        //Long startTime = Calendar.getInstance().getTimeInMillis();
        String data = new String();
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof String) {
                data = (String) arg;
                break;
            }
        }
        //log.info("OldDataMessage 开始, 参数:" + data);

        fileUtils.saveFile(data,new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        OldDataMessage message = null;
        Acknowledgment ack = null;
        try {
            message = (OldDataMessage) BeanJsonDeserializer.deserialize(data);
            ack = (Acknowledgment) joinPoint.getArgs()[1];
            int count = sysExceptionMapper.selectCountByNoAndNoTypeAndDataType(message.getWaybillId(),"olddata", DataType.HISTORICAL_DATA.getCode());
            //异常信息处理
            if (count > 0) {
                throw new RuntimeException("该运单号已在异常表中有记录!");
            }
            if (null == message.getWaybillId()) {
                throw new RuntimeException("运单号不能为空!");
            }
            if (null == message.getSentTime()) {
                throw new RuntimeException("消息发送时间不能为空!");
            }
            //业务幂等性判断
            //MilestoneInfo milestoneInfo = new MilestoneInfo();
            //milestoneInfo.setWaybillId(message.getWaybillId());
            //milestoneInfo.setSentTime(message.getSentTime());
            if (milestoneInfoExtMapper.selectCountByWaybillIdAndSentTime(message.getWaybillId(), message.getSentTime()) > 0) {
                log.error("已经消费的message:" + data);
            } else {
                log.info("OldDataMessage 开始处理订单:" + message.getWaybillId());
                Long startTime = Calendar.getInstance().getTimeInMillis();
                joinPoint.proceed();
                Long endTime = Calendar.getInstance().getTimeInMillis();
                log.info("OldDataMessage 完成处理订单:" + message.getWaybillId() + "--> 耗时:" + (endTime - startTime));
            }
            //有问题，如果是SCM、CMP发过来应该为更新
            /*if (0 < waybillInfoExtMapper.selectCountById(message.getWaybillId())) {
                log.error("已经消费的OldDataMessage:" + data);
            } else {
                joinPoint.proceed();
            }*/
            ack.acknowledge();
        } catch (CommitFailedException e1){
            //log.error("错误的消费OldDataMessage:" + data);
            log.error("错误OldDataMessage:" + e1.getMessage() + ",cause:" + e1.getCause());
            ack.acknowledge();
        } catch (Exception e) {
            //log.error("错误的消费OldDataMessage:" + data);
            log.error("错误OldDataMessage:" + e.getMessage() + ",cause:" + e.getCause());
            e.printStackTrace();
            try {
                message = (OldDataMessage) BeanJsonDeserializer.deserialize(data);
                SysExceptionInfo sysExceptionInfo = new SysExceptionInfo();
                String waybillId = message.getWaybillId();
                sysExceptionInfo.setNo(waybillId.length() < 500 ? waybillId: waybillId.substring(0,500));
                sysExceptionInfo.setSource(message.getMessageFrom());
                sysExceptionInfo.setId(OID.get().toString());
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
                sysExceptionInfo.setNoType("olddata");
                sysExceptionInfo.setSentTime(message.getSentTime());
                sysExceptionInfo.setDataType(DataType.HISTORICAL_DATA.getCode());
                sysExceptionMapper.insert(sysExceptionInfo);
                ack.acknowledge();
            } catch (Exception e2) {
                //log.error("错误的保存异常OldDataMessage:" + data);
                log.error("错误的异常OldDataMessage:" + e2.getMessage() + ",cause:" + e2.getCause());
            }
        }

        //Long endTime = Calendar.getInstance().getTimeInMillis();
        //log.info("OldDataMessage 结束, 参数:" + data + "--> 耗时:" + (endTime - startTime));


    }
}
