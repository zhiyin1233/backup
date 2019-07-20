package com.yiziton.dataimport.waybill.dao;


import com.github.pagehelper.Page;
import com.yiziton.dataweb.waybill.bean.WayBillInfo;
import com.yiziton.dataweb.waybill.vo.WayBillConditionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WayBillExportInfoMapper {

    List<Map<String,Object>> selectWayBillInfoByCondition(WayBillConditionVO wayBillConditionVo);

    Page<WayBillInfo> selectWayBillInfoPageByCondition(@Param("pageNumKey") int pageNum,
                                                       @Param("pageSizeKey") int pageSize);

}