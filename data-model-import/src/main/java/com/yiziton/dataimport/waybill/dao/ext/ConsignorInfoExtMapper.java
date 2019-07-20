package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.bean.ConsignorInfo;
import com.yiziton.dataimport.waybill.dao.ConsignorInfoMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ConsignorInfoExtMapper extends ConsignorInfoMapper {

    @Select("select a.* from consignor_info a where 1=1 and status = 1 and ${column} = '${value}'")
    ConsignorInfo selectByParams(@Param("column") String column, @Param("value") String value);

    @Select("select a.* from consignor_info a where 1=1 and status = 1 and ${column} = '${value}'")
    List<ConsignorInfo> selectListByParams(@Param("column") String column, @Param("value") String value);

    @Select("<script>" +
            " select a.* from consignor_info a " +
            " where a.`status`=1 " +
            " and a.waybill_id in" +
            " <foreach collection=\"waybillIds\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "   #{item}" +
            " </foreach>" +
            "</script>")
    List<ConsignorInfo> selectByWaybillIds(@Param("waybillIds") List<String> waybillIds);


    @Delete("DELETE FROM consignor_info WHERE waybill_id = #{waybillId}")
    int deleteByWaybillId(@Param("waybillId") String waybillId);

}