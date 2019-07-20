package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.OrganizeUser;

public interface OrganizeUserMapper {
    int deleteByPrimaryKey(String id);

    int insert(OrganizeUser record);

    int insertSelective(OrganizeUser record);

    OrganizeUser selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OrganizeUser record);

    int updateByPrimaryKey(OrganizeUser record);
}