package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.bean.MilestoneInfo;
import com.yiziton.dataimport.waybill.dao.MilestoneInfoMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.List;

public interface MilestoneInfoExtMapper extends MilestoneInfoMapper {

    int selectCountForSame(MilestoneInfo record);

    int selectCountByWaybillIdAndSentTime(@Param("waybillId") String waybillId, @Param("sentTime") Timestamp sentTime);

//    @Select("select a.* from milestone_info a where a.`status`=1 and a.waybill_id in (${waybillIds})")
    @Select("<script>" +
            " select a.id, a.waybill_id, a.related_bill_id, a.operation, a.operate_time, a.operator" +
            " from milestone_info a" +
            " where a.`status`=1" +
            " and a.waybill_id in" +
            " <foreach collection=\"waybillIds\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "   #{item}" +
            " </foreach>" +
            " <if test='operations != null and operations.size() gt 0'>" +
            "   and a.operation in " +
            "   <foreach collection=\"operations\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "     #{item}" +
            "   </foreach>" +
            " </if>" +
            "</script>")
    List<MilestoneInfo> selectByWaybillIdsAndOperations(@Param("waybillIds") List<String> waybillIds, @Param("operations") List<Integer> operations);

    @Delete("DELETE FROM milestone_info WHERE waybill_id = #{waybillId} AND message_from = #{messageFrom}")
    int deleteByWaybillIdAndMessageFrom(@Param("waybillId") String waybillId, @Param("messageFrom") String messageFrom);

    @Select("SELECT COUNT(*) FROM milestone_info WHERE waybill_id = #{waybillId} AND message_from = #{messageFrom}")
    int selectCountByWaybillIdAndMessageFrom(@Param("waybillId") String waybillId, @Param("messageFrom") String messageFrom);

    @Delete("DELETE FROM milestone_info WHERE waybill_id = #{waybillId} AND message_from = #{messageFrom} AND operation IN (415, 416, 4150, 418, 419, 420, 4190, 422, 434, 435, 436, 437)")
    int deleteByWaybillIdAndMessageFromAndOperationClass(String waybillId, String messageFrom);
}