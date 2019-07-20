package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.CarrierInfo;

public interface CarrierInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(CarrierInfo record);

    int insertSelective(CarrierInfo record);

    CarrierInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(CarrierInfo record);

    int updateByPrimaryKey(CarrierInfo record);
}