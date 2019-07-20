package com.yiziton.dataimport.waybill.listener.service;

import com.yiziton.commons.serialize.BeanJsonDeserializer;
import com.yiziton.commons.vo.waybill.ConsignorVO;
import com.yiziton.commons.vo.waybill.OrganizeUserVO;
import com.yiziton.dataimport.waybill.service.BasicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * @Description: 基础消息消费: 发货客户 customer.consignor，用户权限 organize.orgUser
 * @Author: kdh
 * @Date: 2019/7/19
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@Component
@Slf4j
public class BasicListenerService {

    @Autowired
    BasicService basicService;

    public void listen(String data, Acknowledgment ack) throws Exception {
        Object messageObject = BeanJsonDeserializer.deserialize(data);
        if(messageObject instanceof ConsignorVO) {
            ConsignorVO consignorVO = (ConsignorVO) messageObject;
            dealConsignor(consignorVO);
        }else if(messageObject instanceof OrganizeUserVO){
            OrganizeUserVO organizeUserVO = (OrganizeUserVO) messageObject;
            dealOrganizeUser(organizeUserVO);
        }
    }

    /**
     * @description 保存或更新
     * @author kdh
     * @create 2019/7/19 14:54
     * @param consignorVO
     * @return
     */
    private void dealConsignor(ConsignorVO consignorVO) {
        basicService.saveOrUpdateConsignor(consignorVO);
    }

    /**
     * @description 保存或更新
     * @author kdh
     * @create 2019/7/19 14:55
     * @param organizeUserVO
     * @return
     */
    private void dealOrganizeUser(OrganizeUserVO organizeUserVO) {
        basicService.saveOrUpdateOrganizeUser(organizeUserVO);
    }

}
