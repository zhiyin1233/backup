package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.WaybillInfo;

public interface WaybillInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(WaybillInfo record);

    int insertSelective(WaybillInfo record);

    WaybillInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(WaybillInfo record);

    int updateByPrimaryKey(WaybillInfo record);
}