package com.yiziton.dataimport.waybill.service;

import com.yiziton.commons.vo.waybill.Message;
import com.yiziton.commons.vo.MessageRedisCacheInfo;
import com.yiziton.commons.vo.enums.CacheMsgTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DealMsgCenterService {

   /* @Autowired
    RedisService redisService;

    public boolean deal(String orderNo, MessageRedisCacheInfo info) {
        List<MessageRedisCacheInfo> infoList = redisService.getShouldHandleMsg(orderNo, info);
        if(infoList != null) {
            infoList.forEach(i -> {
                if(i.getType() == CacheMsgTypeEnum.BILLING_MSG ) {
                    Message message = (Message) i.getMessage();

                } else if(i.getType() == CacheMsgTypeEnum.OLD_DATA_MSG ) {
                } else if(i.getType() == CacheMsgTypeEnum.WAY_BILL_MSG ) {

                }
            });
        }
        return false;
    }*/
}
