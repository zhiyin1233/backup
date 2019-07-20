package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.bean.BillingInfo;
import com.yiziton.dataimport.waybill.dao.BillingInfoMapper;
import com.yiziton.dataweb.waybill.vo.FeeVO;
import com.yiziton.dataweb.waybill.vo.ReportConditionVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.sql.Timestamp;
import java.util.List;

public interface BillingInfoExtMapper extends BillingInfoMapper {

    BillingInfo selectByParams(BillingInfo billingInfo);

    List<BillingInfo> selectListByParams(BillingInfo billingInfo);

    /**
     *
     * @param conditionVO 查询条件
     * @param columnList 要查询的属性
     * @param total 是否统计总数，是：传值“total”，否：传null
     * @param waybillIdList 运单号列表，用于in查询
     * @param billingObject 流水对象类型 YZT：0,一智通；CUSTOMER：1,商家；MASTER：2,服务商；CARRIER：3,承运商
     * @param billingType 流水类型 NORMAL：0,正常流水；ABNORMAL：1,异常流水；COMPENSATE：2,赔偿流水
     * @param feeTypeList 费用类型，用于in查询
     * @param fee
     * @return
     */
    @Select("<script>" +
            " <if test='feeTypeList != null'> select fee.*,<foreach item=\"item\" index=\"index\" collection=\"feeTypeList\" open=\"SUM(\" separator=\"+\" close=\")\">${item}</foreach> AS total from (</if>" +
            " select " +
            " a.waybill_id as waybillId " +
            " <if test='columnList != null'> ${columnList} </if>" +
            " from fee_info a " +
            " left join billing_info b on a.billing_id=b.id " +
            //" from waybill_info c" +
            //" left join billing_info b on b.waybill_id=c.id " +
            //" left join fee_info a on a.waybill_id=b.waybill_id " +
            " <if test='billingObject != null'> and b.billing_object =${billingObject}</if>" +
            " <if test='billingType != null'> and b.billing_type =${billingType}</if>" +
            " where 1=1 and b.waybill_id is not null and a.status=1 and b.status=1 " +
            //" <if test=\"conditionVO != null\"><![CDATA[and c.open_bill_time >= '${conditionVO.openBillStartTime}']]></if>" +
            //" <if test=\"conditionVO != null\"><![CDATA[and c.open_bill_time <= '${conditionVO.openBillEndTime}']]></if>"+
            " <if test='waybillIdList != null'> and a.waybill_id in <foreach item=\"item\" index=\"index\" collection=\"waybillIdList\" open=\"(\" separator=\",\" close=\")\">'${item}'</foreach></if>" +
            //" <if test='feeTypeList != null'> and a.fee_type in <foreach item=\"item\" index=\"index\" collection=\"feeTypeList\" open=\"(\" separator=\",\" close=\")\">'${item}'</foreach></if>" +
            " <if test='fee != null'><![CDATA[and a.fee${fee}0.00]]></if>" +
            " group by a.waybill_id" +
            " <if test='feeTypeList != null'>)fee group by fee.waybillId</if>" +
            "</script>")
    List<FeeVO> getFeeList(@Param("conditionVO") ReportConditionVO conditionVO, @Param("columnList") String columnList,
                           @Param("total") String total, @Param("waybillIdList") List<String> waybillIdList,
                           @Param("billingObject") Integer billingObject, @Param("billingType") Integer billingType,
                           @Param("feeTypeList") List<String> feeTypeList, @Param("fee") String fee);

    int selectCountForSame(BillingInfo billingInfo);

    @Select("<script>" +
            " select a.waybill_id, a.total_price" +
            " from billing_info a " +
            " where a.`status`=1 " +
            " and a.billing_object=1 and a.billing_type=1" +
            " and a.waybill_id in" +
            " <foreach collection=\"waybillIds\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "   #{item}" +
            " </foreach>" +
            "</script>")
    List<BillingInfo> select4WaybillAging(@Param("waybillIds") List<String> waybillIds);

    @Select("select a.total_price from billing_info a " +
            "where a.`status`=1 and a.waybill_id=#{waybillId} and a.billing_object=1 and a.billing_type=1")
    Double selectTotalPriceByWaybillId(@Param("waybillId") String waybillId);

    @Select("<script>" +
            " select a.waybill_id waybillId, sum(a.total_price) as totalPrice" +
            " from billing_info a " +
            " where a.`status`=1 " +
            " and a.waybill_id in" +
            " <foreach collection=\"waybillIds\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "   #{item}" +
            " </foreach>" +
            " GROUP BY a.waybill_id " +
            "</script>")
    List<BillingInfo> selectTotalPrice(@Param("waybillIds") List<String> waybillIds);

    @Update("<script>" +
            " update billing_info a set a.status=2 " +
            " where 1=1 " +
            " and a.waybill_id in" +
            " <foreach collection=\"waybillIds\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "   #{item}" +
            " </foreach>" +
            "</script>")
    void updateByPrimaryKeys(@Param("waybillIds") List<String> waybillIds);

    @Update(" update billing_info a left join waybill_info b on b.id=a.waybill_id set a.status=2 " +
            " where 1=1 and a.status=1 " +
            " and b.open_bill_time >= '${startTime}' and b.open_bill_time < '${endTime}' ")
    void updateByOpenBillTime(@Param("startTime") Timestamp startTime, @Param("endTime")Timestamp endTime);
}