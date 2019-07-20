package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.dao.AfterSaleInfoMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * <p>Description: 售后扩展类</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: YZT</p>
 *
 * @author TY
 * @version 1.0
 * @date 2018/11/25 15:06
 */
public interface AfterSaleInfoExtMapper extends AfterSaleInfoMapper {
    @Delete("DELETE FROM after_sale_info WHERE waybill_id = #{waybillId}")
    int deleteByWaybillId(@Param("waybillId") String waybillId);
}