package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.ConsignorInfo;

public interface ConsignorInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(ConsignorInfo record);

    int insertSelective(ConsignorInfo record);

    ConsignorInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ConsignorInfo record);

    int updateByPrimaryKey(ConsignorInfo record);

}