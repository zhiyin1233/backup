package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.ReceviceInfo;

public interface ReceviceInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(ReceviceInfo record);

    int insertSelective(ReceviceInfo record);

    ReceviceInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ReceviceInfo record);

    int updateByPrimaryKey(ReceviceInfo record);

}