package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.BillRelationInfo;

public interface BillRelationInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(BillRelationInfo record);

    int insertSelective(BillRelationInfo record);

    BillRelationInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BillRelationInfo record);

    int updateByPrimaryKey(BillRelationInfo record);
}