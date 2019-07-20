package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.bean.BillRelationInfo;
import com.yiziton.dataimport.waybill.dao.BillRelationInfoMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BillRelationInfoExtMapper extends BillRelationInfoMapper {

    @Select("<script>" +
            " select a.id, a.waybill_id waybillId, a.customer_bill_id customerBillId, a.order_bill_id orderBillId, a.status, a.create_time createTime, a.update_time updateTime, a.data_type dataType " +
            " from bill_relation_info a " +
            " where a.`status`=1 " +
            " and a.waybill_id in" +
            " <foreach collection=\"waybillIds\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "   #{item}" +
            " </foreach>" +
            "</script>")
    List<BillRelationInfo> selectByWaybillIds(@Param("waybillIds") List<String> waybillIds);

    @Delete("DELETE FROM bill_relation_info WHERE waybill_id = #{waybillId}")
    int deleteByWaybillId(@Param("waybillId") String waybillId);

}