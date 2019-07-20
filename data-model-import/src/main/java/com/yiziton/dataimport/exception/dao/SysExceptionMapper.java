package com.yiziton.dataimport.exception.dao;

import com.yiziton.commons.vo.waybill.SysExceptionDealVO;
import com.yiziton.commons.vo.waybill.SysExceptionVO;
import com.yiziton.dataimport.exception.bean.SysExceptionInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysExceptionMapper {

    int deleteByPrimaryKey(String id);

    int insert(SysExceptionInfo record);

    List<SysExceptionInfo> selectSysExceptionInfos(SysExceptionDealVO sysExceptionDealVO);

    int insertSelective(SysExceptionInfo record);

    SysExceptionInfo selectByPrimaryKey(String id);

    SysExceptionInfo selectByNoForGetAll(String no);

    int updateByPrimaryKeySelective(SysExceptionInfo record);

    int updateByPrimaryKey(SysExceptionInfo record);

    //int selectCountByNoAndDataType(String no, String dataType);

    int selectCountByNoAndNoTypeAndDataType(@Param("no") String no, @Param("noType") String noType, @Param("dataType") Integer dataType);

    @Delete("DELETE FROM sys_exception_info WHERE no = #{no} AND source = #{source}")
    int deleteByNoAndSource(@Param("no") String no, @Param("source")  String source);
}