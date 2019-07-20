package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.bean.ExceptionInfo;
import com.yiziton.dataimport.waybill.dao.ExceptionInfoMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface ExceptionInfoExtMapper extends ExceptionInfoMapper {
    int updateByWaybillIdAndExceptionCodeSelective(ExceptionInfo record);

    ExceptionInfo selectByWaybillIdAndExceptionCode(@Param("waybillId") String waybillId, @Param("exceptionCode") String exceptionCode);

    Integer selectCountForSame(@Param("waybillId") String waybillId, @Param("exceptionCode") String exceptionCode);

    @Delete("DELETE FROM exception_info WHERE waybill_id = #{waybillId}")
    int deleteByWaybillId(@Param("waybillId") String waybillId);
}