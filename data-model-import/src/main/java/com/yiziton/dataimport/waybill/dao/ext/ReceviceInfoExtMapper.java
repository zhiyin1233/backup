package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.bean.ReceviceInfo;
import com.yiziton.dataimport.waybill.dao.ReceviceInfoMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ReceviceInfoExtMapper extends ReceviceInfoMapper {

    @Select("select a.* from recevice_info a where 1=1 and ${column} = '${value}'")
    ReceviceInfo selectByParams(@Param("column") String column, @Param("value") String value);

    @Select("select a.* from recevice_info a where 1=1 and ${column} = '${value}'")
    List<ReceviceInfo> selectListByParams(@Param("column") String column, @Param("value") String value);

    @Select("<script>" +
            " select a.* from recevice_info a " +
            " where a.`status`=1 " +
            " and a.waybill_id in" +
            " <foreach collection=\"waybillIds\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "   #{item}" +
            " </foreach>" +
            "</script>")
    List<ReceviceInfo> selectByWaybillIds(@Param("waybillIds") List<String> waybillIds);

    @Delete("DELETE FROM recevice_info WHERE waybill_id = #{waybillId}")
    int deleteByWaybillId(@Param("waybillId") String waybillId);
}