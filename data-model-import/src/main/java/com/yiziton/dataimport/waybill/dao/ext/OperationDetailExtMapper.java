package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.bean.OperationDetail;
import com.yiziton.dataimport.waybill.dao.OperationDetailMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OperationDetailExtMapper extends OperationDetailMapper {

    @Select("<script>" +
            " select a.* from operation_detail a LEFT JOIN milestone_info b on b.id=a.milestone_id " +
            " where a.waybill_id ='${id}' " +
            "<if test='keys != null'> and b.operation in <foreach item=\"item\" index=\"index\" collection=\"keys\" open=\"(\" separator=\",\" close=\")\">${item}</foreach></if>" +
            "</script>")
    List<OperationDetail> selectByWaybillIdAndOparation(@Param("id") String id, @Param("keys") List<Integer> keys);

    @Select("<script>" +
            " select a.waybill_id, a.milestone_id, a.field, a.val" +
            " from operation_detail a" +
            " where a.`status`=1 " +
            " and a.milestone_id in" +
            " <foreach collection=\"milestoneIds\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            " #{item}" +
            " </foreach>" +
            "</script>")
    List<OperationDetail> selectByMilestoneIds(@Param("milestoneIds") List<String> milestoneIds);

    //@Delete("DELETE FROM operation_detail WHERE waybill_id = #{waybillId}")
    @Delete("DELETE FROM operation_detail WHERE waybill_id = #{waybillId} AND milestone_id in (SELECT id FROM milestone_info WHERE waybill_id = #{waybillId} AND message_from = #{messageFrom})")
    int deleteByWaybillIdAndMessageFrom(@Param("waybillId") String waybillId, @Param("messageFrom") String messageFrom);
}