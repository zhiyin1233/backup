package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.BillingInfo;

public interface BillingInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(BillingInfo record);

    int insertSelective(BillingInfo record);

    BillingInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BillingInfo record);

    int updateByPrimaryKey(BillingInfo record);
}