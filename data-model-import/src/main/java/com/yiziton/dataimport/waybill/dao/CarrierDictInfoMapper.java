package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.CarrierDictInfo;

public interface CarrierDictInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(CarrierDictInfo record);

    int insertSelective(CarrierDictInfo record);

    CarrierDictInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(CarrierDictInfo record);

    int updateByPrimaryKey(CarrierDictInfo record);
}