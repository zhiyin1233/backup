package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.bean.CompensationInfo;
import com.yiziton.dataimport.waybill.dao.CompensationInfoMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>Description: 赔偿mapper</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: YZT</p>
 *
 * @author TY
 * @version 1.0
 * @date 2019/04/15 16:43
 */
public interface CompensationInfoExtMapper extends CompensationInfoMapper {

    @Delete("DELETE FROM compensation_info WHERE waybill_id = #{waybillId}")
    int deleteByWaybillId(@Param("waybillId") String waybillId);

    CompensationInfo selectByWaybillIdAndCompensationBillId(@Param("waybillId")String waybillId, @Param("compensationBillId")String compensationBillId);

    List<CompensationInfo> selectByWaybillId(@Param("waybillId")String waybillId);

}