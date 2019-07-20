package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.bean.CarrierInfo;
import com.yiziton.dataimport.waybill.dao.CarrierInfoMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CarrierInfoExtMapper extends CarrierInfoMapper {

    CarrierInfo selectByWaybillIdAndStatus(@Param("waybillId") String waybillId, @Param("status") Integer status, @Param("carrierType") Integer carrierType);

    @Select("<script>" +
            " select a.* from carrier_info a " +
            " where a.`status`=1 " +
            " and a.waybill_id in" +
            " <foreach collection=\"waybillIds\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "   #{item}" +
            " </foreach>" +
            "</script>")
    List<CarrierInfo> selectByWaybillIds(@Param("waybillIds") List<String> waybillIds);

    @Select("SELECT COUNT(*) FROM carrier_info WHERE waybill_id = #{waybillId}")
    int selectCountByWaybillId(@Param("waybillId") String waybillId);

    //@Delete("DELETE FROM carrier_info WHERE waybill_id = #{waybillId}")
    @Delete("DELETE FROM carrier_info WHERE waybill_id = #{waybillId} AND carrier_type = #{carrierType}")
    int deleteByWaybillId(@Param("waybillId") String waybillId, @Param("carrierType") Integer carrierType);
}