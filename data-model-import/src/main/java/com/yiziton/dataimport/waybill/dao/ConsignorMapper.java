package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.Consignor;

public interface ConsignorMapper {
    int deleteByPrimaryKey(String id);

    int insert(Consignor record);

    int insertSelective(Consignor record);

    Consignor selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Consignor record);

    int updateByPrimaryKey(Consignor record);
}