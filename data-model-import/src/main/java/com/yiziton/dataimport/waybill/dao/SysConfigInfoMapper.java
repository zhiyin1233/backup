package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.SysConfigInfo;

public interface SysConfigInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(SysConfigInfo record);

    int insertSelective(SysConfigInfo record);

    SysConfigInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SysConfigInfo record);

    int updateByPrimaryKey(SysConfigInfo record);
}