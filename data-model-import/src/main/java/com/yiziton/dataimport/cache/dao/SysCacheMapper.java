package com.yiziton.dataimport.cache.dao;

import com.yiziton.commons.vo.waybill.SysExceptionDealVO;
import com.yiziton.dataimport.exception.bean.SysExceptionInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysCacheMapper {

    @Insert("insert into sys_cache_deal_info(id,no,deal)values('${id}','${no}','${deal}')")
    int insert(@Param("id") String id,@Param("no") String no,@Param("deal") Integer deal);
}