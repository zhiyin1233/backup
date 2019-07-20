package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.ExceptionInfo;

public interface ExceptionInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(ExceptionInfo record);

    int insertSelective(ExceptionInfo record);

    ExceptionInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ExceptionInfo record);

    int updateByPrimaryKey(ExceptionInfo record);
}