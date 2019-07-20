package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataimport.waybill.bean.MilestoneInfo;

public interface MilestoneInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(MilestoneInfo record);

    int insertSelective(MilestoneInfo record);

    MilestoneInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(MilestoneInfo record);

    int updateByPrimaryKey(MilestoneInfo record);
}