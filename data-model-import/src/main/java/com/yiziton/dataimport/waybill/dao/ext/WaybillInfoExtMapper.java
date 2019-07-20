package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.dao.WaybillInfoMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * <p>Description: 订单扩展类</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: YZT</p>
 *
 * @author TY
 * @version 1.0
 * @date 2018/11/25 15:06
 */
public interface WaybillInfoExtMapper extends WaybillInfoMapper {
    int selectCountById(String id);

    @Select("select distinct salesman from waybill_info where `status`=1 and salesman is not null and salesman <> '' and salesman like concat('%', '${salesmanName}', '%')")
    List<String> selectSalesmanByName(@Param("salesmanName") String salesmanName);

    @Delete("DELETE FROM waybill_info WHERE id = #{id}")
    int deleteByWaybillId(@Param("id") String id);

    @Select("select a.id from waybill_info a" +
            " where a.waybill_status in (21,25,26) and a.create_time >= '${startTime}' and a.create_time <= '${endTime}'" +
            " and not exists( select 'x' from sys_cache_deal_info b where b.no =a.id and b.deal =1 )")
    List<String> selectDoneWayBill(@Param("startTime")Timestamp startTime,@Param("endTime")Timestamp endTime);

    @Select("select a.id from waybill_info a" +
            " where a.waybill_status not in (21,25,26) and a.create_time >= '${startTime}' and a.create_time <= '${endTime}'" +
            " and not exists( select 'x' from sys_cache_deal_info b where b.no =a.id and b.deal =2 )")
    List<String> selectNoDoneWayBill(@Param("startTime")Timestamp startTime,@Param("endTime")Timestamp endTime);

    @Select("select distinct a.id from waybill_info a where a.`status`=1 and a.open_bill_time >= '${startTime}' and a.open_bill_time < '${endTime}'")
    List<String> selectAllId(@Param("startTime")Timestamp startTime,@Param("endTime")Timestamp endTime);

    @Select("select id,waybill_status from waybill_info order by open_bill_time")
    List<Map<String,Object>> selectOrderByOpenBillTime();
}
