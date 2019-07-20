package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.bean.GoodsInfo;
import com.yiziton.dataimport.waybill.dao.GoodsInfoMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GoodsInfoExtMapper extends GoodsInfoMapper {

    @Select("select a.* from goods_info a where 1=1 and ${column} = '${value}'")
    GoodsInfo selectByParams(@Param("column") String column, @Param("value") String value);

    @Select("select a.* from goods_info a where 1=1 and ${column} = '${value}'")
    List<GoodsInfo> selectListByParams(@Param("column") String column, @Param("value") String value);

    @Select("<script>" +
            " select a.waybill_id, a.goods_name, a.install_items, a.packing_items," +
            " a.volume, a.weight, a.install_fee, a.delivery_fee, a.packing " +
            " from goods_info a " +
            " where a.`status`=1 " +
            " and a.waybill_id in" +
            " <foreach collection=\"waybillIds\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "   #{item}" +
            " </foreach>" +
            "</script>")
    List<GoodsInfo> selectByWaybillIds(@Param("waybillIds") List<String> waybillIds);

    @Delete("DELETE FROM goods_info WHERE waybill_id = #{waybillId}")
    int deleteByWaybillId(@Param("waybillId") String waybillId);
}