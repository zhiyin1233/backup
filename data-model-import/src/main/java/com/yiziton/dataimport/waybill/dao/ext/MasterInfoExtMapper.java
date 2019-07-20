package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.bean.MasterInfo;
import com.yiziton.dataimport.waybill.dao.MasterInfoMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MasterInfoExtMapper extends MasterInfoMapper {

    MasterInfo selectByWaybillIdAndStatus(@Param("waybillId") String waybillId, @Param("status") Integer status, @Param("distributionType") int distributionType);

    List<MasterInfo> selectListByWaybillIdAndStatus(@Param("waybillId") List<String> waybillId, @Param("status") Integer status);

    @Delete("DELETE FROM master_info WHERE waybill_id = #{waybillId}")
    int deleteByWaybillId(@Param("waybillId") String waybillId);
}