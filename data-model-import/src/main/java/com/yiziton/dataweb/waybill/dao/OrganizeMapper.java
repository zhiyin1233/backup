package com.yiziton.dataweb.waybill.dao;

import com.yiziton.dataweb.salesindex.pojo.OrganizeDO;
import com.yiziton.dataweb.salesindex.pojo.SalesIndexDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author HuangHuai on 2019-07-15 14:27
 */

public interface OrganizeMapper {


    List<OrganizeDO> queryAll();



    int deleteByPrimaryKey(String id);

    int insert(OrganizeDO record);

    int insertSelective(OrganizeDO record);

    OrganizeDO selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OrganizeDO record);

    int updateByPrimaryKey(OrganizeDO record);

}
