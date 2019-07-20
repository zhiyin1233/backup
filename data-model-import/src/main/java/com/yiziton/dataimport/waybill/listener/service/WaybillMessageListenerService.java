package com.yiziton.dataimport.waybill.listener.service;

import com.alibaba.fastjson.JSON;
import com.yiziton.commons.serialize.BeanJsonDeserializer;
import com.yiziton.commons.utils.OID;
import com.yiziton.commons.vo.MessageRedisCacheInfo;
import com.yiziton.commons.vo.enums.DataType;
import com.yiziton.commons.vo.enums.Operation;
import com.yiziton.commons.vo.enums.OperationProcessMethod;
import com.yiziton.commons.vo.waybill.Message;
import com.yiziton.dataimport.commons.OrderProcessor;
import com.yiziton.dataimport.commons.SpringUtils;
import com.yiziton.dataimport.exception.bean.SysExceptionInfo;
import com.yiziton.dataimport.exception.dao.SysExceptionMapper;
import com.yiziton.dataimport.waybill.bean.MilestoneInfo;
import com.yiziton.dataimport.waybill.bean.WaybillInfo;
import com.yiziton.dataimport.waybill.dao.ext.MilestoneInfoExtMapper;
import com.yiziton.dataimport.waybill.dao.ext.WaybillInfoExtMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2019/1/2
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@Component
@Slf4j
public class WaybillMessageListenerService {

    @Autowired
    OrderProcessor orderProcessor;

    @Autowired
    SysExceptionMapper sysExceptionMapper;

    @Autowired
    BillingMessageListenerService billingMessageListenerService;

    @Autowired
    MilestoneInfoExtMapper milestoneInfoExtMapper;

    @Autowired
    WaybillInfoExtMapper waybillInfoExtMapper;

    @Value("${orderno.sychronized:}")
    private boolean sychronized = false;

    private String messageService = "messageService";

    public void listen(String data, Acknowledgment ack) throws Exception {
        Object messageObject = BeanJsonDeserializer.deserialize(data);
        if(messageObject instanceof Message) {
            Message message = (Message) messageObject;
            deal(message);
        }else if(messageObject instanceof MessageRedisCacheInfo){
            MessageRedisCacheInfo messageCacheInfo = (MessageRedisCacheInfo) BeanJsonDeserializer.deserialize(data);
            if(sychronized){
                SysExceptionInfo sysExceptionInfo = sysExceptionMapper.selectByNoForGetAll(messageCacheInfo.getMessage().getWaybillId());
                List<MessageRedisCacheInfo> messageCacheInfoList = null;
                if(sysExceptionInfo != null){
                    messageCacheInfoList = orderProcessor.getAllShouldHandleMsg(messageCacheInfo.getMessage().getWaybillId(),messageCacheInfo);
                }else{
                    messageCacheInfoList = orderProcessor.getShouldHandleMsg(messageCacheInfo.getMessage().getWaybillId(),messageCacheInfo);
                }
                if(messageCacheInfoList != null && messageCacheInfoList.size() > 0){
                    for(MessageRedisCacheInfo msgInfo : messageCacheInfoList){
                        Message message = msgInfo.getMessage();
                        if(StringUtils.isEmpty(message.getOperation())){
                            billingMessageListenerService.deal(message, DataType.REAL_TIME_DATA.getCode());
                        }else{
                            deal(message);
                        }
                    }
                }
            }else{
                Message message = messageCacheInfo.getMessage();
                deal(message);
            }
        }
    }

    public void deal(Message message)throws Exception{
        MilestoneInfo milestoneInfo = new MilestoneInfo();
        milestoneInfo.setWaybillId(message.getWaybillId());
        milestoneInfo.setSentTime(message.getSentTime());
        milestoneInfo.setOperation(Operation.getEnumByEnumNameString(message.getOperation()).getCode());
        if (milestoneInfoExtMapper.selectCountForSame(milestoneInfo) > 0) {
            log.error("已经消费的 Waybill Message : " + message);
        } else {
            String operation = message.getOperation();
            if (StringUtils.isEmpty(operation)) {
                log.error("operation 不能为空");
                throw new RuntimeException("operation 不能为空");
            } else {
                String waybillId = message.getWaybillId();
                String[] split = waybillId.split(",");
                for (String id : split) {
                    message.setWaybillId(id);
                    OperationProcessMethod operationProcessMethod = OperationProcessMethod.getEnumByEnumNameString(operation);
                    if (operationProcessMethod != null) {
                        log.info("调用" + messageService + "的方法 : " + operationProcessMethod.getMessage() + "开始");
                        WaybillInfo waybillInfo = waybillInfoExtMapper.selectByPrimaryKey(id);
                        if (waybillInfo == null && !operation.equals(Operation.WAYBILL_OPEN.name())) {
                            if(operation.equals(Operation.TRACKING_ADD_NOTES.name())){
                                //预开单的运单暂时没有传到数据平台
                                log.error("添加跟踪备注接口:预开单的运单暂时没有传到数据平台, 不做处理");
                                return;
                            }
                            SysExceptionInfo sysExceptionInfo = new SysExceptionInfo();
                            sysExceptionInfo.setNo(id);
                            sysExceptionInfo.setId(OID.get().toString());
                            sysExceptionInfo.setSource(message.getMessageFrom());
                            String json = JSON.toJSONString(message);
                            json += "&spilt:"+message.getClass().getName();
                            sysExceptionInfo.setExceptionBody(json.length() < 8000 ? json : json.substring(0, 8000));
                            sysExceptionInfo.setExceptionInfo("运单不存在 : " + id);
                            sysExceptionInfo.setNoType("waybill");
                            sysExceptionInfo.setSentTime(message.getSentTime());
                            sysExceptionInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
                            sysExceptionMapper.insert(sysExceptionInfo);
                        }else{
                            Object service = SpringUtils.getApplicationContext().getBean(messageService);
                            Method method = service.getClass().getMethod(operationProcessMethod.getMessage(), Message.class);
                            method.invoke(service, message);
                        }
                        log.info("调用" + messageService + "的方法 : " + operationProcessMethod.getMessage() + "结束");
                    } else {
                        throw new RuntimeException("OperationProcessMethod 找不到对应枚举");
                    }
                }
            }
        }
    }
}
