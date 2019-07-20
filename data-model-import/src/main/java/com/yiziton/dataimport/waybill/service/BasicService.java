package com.yiziton.dataimport.waybill.service;

import com.yiziton.commons.utils.OID;
import com.yiziton.commons.utils.bean.Beans;
import com.yiziton.commons.vo.enums.*;
import com.yiziton.commons.vo.waybill.ConsignorVO;
import com.yiziton.commons.vo.waybill.OrganizeUserVO;
import com.yiziton.dataimport.waybill.bean.Consignor;
import com.yiziton.dataimport.waybill.dao.ext.ConsignorExtMapper;
import com.yiziton.dataimport.waybill.dao.ext.OrganizeUserExtMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Description: 基础消息消费: 发货客户 customer.consignor，用户权限 organize.orgUser
 * @Author: kdh
 * @Date: 2019/7/19
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@Slf4j
@Transactional
@Component("basicService")
public class BasicService {

    @Autowired
    ConsignorExtMapper consignorExtMapper;

    @Autowired
    OrganizeUserExtMapper organizeUserExtMapper;


    /**
     * @description 保存或更新
     * @author kdh
     * @create 2019/7/19 14:54
     * @param consignorVO
     * @return
     */
    public void saveOrUpdateConsignor(ConsignorVO consignorVO) {
        // 以客户编号为唯一标识查询
        String code = consignorVO.getCode();
        if(StringUtils.isNotEmpty(code)){
            Consignor consignor = consignorExtMapper.selectByCode(code);
            if(consignor == null){
                consignor = new Consignor();
                //对象copy，将后一个对象中属性的值赋予给前一个
                Beans.from(consignor).to(consignorVO);
                if(StringUtils.isEmpty(consignor.getId())){
                    consignor.setId(OID.get().toString());
                }
                consignor.setCreateTime(new Date());
                consignor.setUpdateTime(new Date());
                consignorExtMapper.insert(consignor);
            }else{
                if(StringUtils.isNotEmpty(consignorVO.getName())){
                    consignor.setName(consignorVO.getName());
                }
                if(StringUtils.isNotEmpty(consignorVO.getTelephone())){
                    consignor.setTelephone(consignorVO.getTelephone());
                }
                if(StringUtils.isNotEmpty(consignorVO.getType())){
                    SettlementType settlementType = SettlementType.getEnumByEnumNameString(consignorVO.getType());
                    if (settlementType != null){
                        consignor.setType(settlementType.getCode());
                        consignor.setTypeName(consignorVO.getType());
                    }
                }
                if(StringUtils.isNotEmpty(consignorVO.getBusinessContact())){
                    consignor.setBusinessContact(consignorVO.getBusinessContact());
                }
                if(StringUtils.isNotEmpty(consignorVO.getBusinessContactJobNum())){
                    consignor.setBusinessContactJobNum(consignorVO.getBusinessContactJobNum());
                }
                if(StringUtils.isNotEmpty(consignorVO.getBusinessContactTelephone())){
                    consignor.setBusinessContactTelephone(consignorVO.getBusinessContactTelephone());
                }
                if(StringUtils.isNotEmpty(consignorVO.getShippingMethod())){
                    ShippingMethod shippingMethod = ShippingMethod.getEnumByEnumNameString(consignorVO.getShippingMethod());
                    if(shippingMethod != null){
                        consignor.setShippingMethod(shippingMethod.getCode());
                        consignor.setShippingMethodName(consignorVO.getShippingMethod());
                    }
                }
                if(StringUtils.isNotEmpty(consignorVO.getStatus())){
                    consignor.setName(consignorVO.getName());
                }
                if(StringUtils.isNotEmpty(consignorVO.getLevel())){
                    Level level = Level.getEnumByEnumNameString(consignorVO.getLevel());
                    if(level != null){
                        consignor.setLevel(level.getCode());
                        consignor.setLevelName(consignorVO.getLevel());
                    }
                }
                if(StringUtils.isNotEmpty(consignorVO.getLabel())){
                    Label label = Label.getEnumByEnumNameString(consignorVO.getLabel());
                    if(label != null){
                        consignor.setLabel(label.getCode());
                        consignor.setLabelName(consignorVO.getLabel());
                    }
                }
                if(StringUtils.isNotEmpty(consignorVO.getAccountPeriod())){
                    AccountPeriod accountPeriod = AccountPeriod.getEnumByEnumNameString(consignorVO.getAccountPeriod());
                    if(accountPeriod != null){
                        consignor.setAccountPeriod(accountPeriod.getCode());
                        consignor.setAccountPeriodName(consignorVO.getAccountPeriod());
                    }
                }
                if(StringUtils.isNotEmpty(consignorVO.getLogisticsCost())){
                    consignor.setLogisticsCost(Double.valueOf(consignorVO.getLogisticsCost()));
                }
                consignorExtMapper.updateByPrimaryKeySelective(consignor);

            }
        }else{
            throw new RuntimeException("发货人编号不能为空");
        }
    }

    /**
     * @description 保存或更新
     * @author kdh
     * @create 2019/7/19 14:54
     * @param organizeUserVO
     * @return
     */
    public void saveOrUpdateOrganizeUser(OrganizeUserVO organizeUserVO) {
    }
}
