package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.MasterInfo;

public interface MasterInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(MasterInfo record);

    int insertSelective(MasterInfo record);

    MasterInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(MasterInfo record);

    int updateByPrimaryKey(MasterInfo record);
}