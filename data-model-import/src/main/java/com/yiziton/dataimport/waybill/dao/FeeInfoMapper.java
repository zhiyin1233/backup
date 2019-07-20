package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.FeeInfo;

public interface FeeInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(FeeInfo record);

    int insertSelective(FeeInfo record);

    FeeInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(FeeInfo record);

    int updateByPrimaryKey(FeeInfo record);
}