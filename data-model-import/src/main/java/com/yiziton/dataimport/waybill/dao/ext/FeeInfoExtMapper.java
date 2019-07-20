package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.bean.FeeInfo;
import com.yiziton.dataimport.waybill.dao.FeeInfoMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface FeeInfoExtMapper extends FeeInfoMapper {

    @Select("<script>" +
            " select a.id, a.billing_id billingId, a.fee, a.fee_type feeType, a.status, a.create_time createTime, a.update_time updateTime, a.data_type dataType " +
            " from fee_info a left join billing_info b on b.id=a.billing_id " +
            " where a.`status`=1 " +
            " and b.waybill_id = '${waybillId}' " +
            " and b.billing_object = ${billingObject} " +
            " and b.billing_type = ${billingType} " +
            " and b.billing_name = '${billingName}' " +
            " <if test='relatedBillId == null'> and b.related_bill_id is null</if> " +
            " <if test='relatedBillId != null'> and b.related_bill_id = '${relatedBillId}'</if>" +
            " <if test='feeTypeList != null'> and a.fee_type in <foreach item=\"item\" index=\"index\" collection=\"feeTypeList\" open=\"(\" separator=\",\" close=\")\">'${item}'</foreach></if>" +
            "</script>")
    List<FeeInfo> selectListByParams(@Param("waybillId") String waybillId, @Param("billingObject") Integer billingObject,
                                     @Param("billingType") Integer billingType, @Param("billingName") String billingName,
                                     @Param("relatedBillId") String relatedBillId, @Param("feeTypeList") List<String> feeTypeList);

    @Select("<script>" +
            " select a.waybill_id, a.fee, a.fee_type" +
            " from fee_info a " +
            " where a.`status`=1 " +
            " and a.waybill_id in" +
            " <foreach collection=\"waybillIds\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "   #{item}" +
            " </foreach>" +
            "</script>")
    List<FeeInfo> selectByWaybillIds(@Param("waybillIds") List<String> waybillIds);

    @Update("<script>" +
            " update fee_info a set a.status=2 " +
            " where 1=1 " +
            " and a.waybill_id in" +
            " <foreach collection=\"waybillIds\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "   #{item}" +
            " </foreach>" +
            "</script>")
    void updateByPrimaryKeys(@Param("waybillIds") List<String> waybillIds);

    @Update("update fee_info a left join waybill_info b on b.id=a.waybill_id set a.status=2 " +
            " where 1=1 and a.status=1 " +
            " and b.open_bill_time >= '${startTime}' and b.open_bill_time < '${endTime}'")
    void updateByOpenBillTime(@Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime);

    @Update("<script>" +
            " update fee_info a set a.status=2 " +
            " where 1=1 " +
            " and a.billing_id in" +
            " <foreach collection=\"billingIds\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "   #{item}" +
            " </foreach>" +
            "</script>")
    void updateByBillingIds(@Param("billingIds") List<String> billingIds);
}