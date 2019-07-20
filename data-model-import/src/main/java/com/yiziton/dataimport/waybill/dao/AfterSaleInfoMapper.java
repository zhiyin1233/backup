package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.AfterSaleInfo;

public interface AfterSaleInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(AfterSaleInfo record);

    int insertSelective(AfterSaleInfo record);

    AfterSaleInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AfterSaleInfo record);

    int updateByPrimaryKey(AfterSaleInfo record);
}