package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.OperationDetail;

public interface OperationDetailMapper {
    int deleteByPrimaryKey(String id);

    int insert(OperationDetail record);

    int insertSelective(OperationDetail record);

    OperationDetail selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OperationDetail record);

    int updateByPrimaryKey(OperationDetail record);
}