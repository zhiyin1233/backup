package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.ReceviceDictInfo;

public interface ReceviceDictInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(ReceviceDictInfo record);

    int insertSelective(ReceviceDictInfo record);

    ReceviceDictInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ReceviceDictInfo record);

    int updateByPrimaryKey(ReceviceDictInfo record);
}