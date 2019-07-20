package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.MasterDictInfo;

public interface MasterDictInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(MasterDictInfo record);

    int insertSelective(MasterDictInfo record);

    MasterDictInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(MasterDictInfo record);

    int updateByPrimaryKey(MasterDictInfo record);
}