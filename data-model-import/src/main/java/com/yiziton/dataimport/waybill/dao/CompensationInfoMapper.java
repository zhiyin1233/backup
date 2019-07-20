package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.CompensationInfo;

public interface CompensationInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(CompensationInfo record);

    int insertSelective(CompensationInfo record);

    CompensationInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(CompensationInfo record);

    int updateByPrimaryKey(CompensationInfo record);
}