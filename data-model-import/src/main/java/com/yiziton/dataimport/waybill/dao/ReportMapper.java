package com.yiziton.dataimport.waybill.dao;

import com.yiziton.dataweb.waybill.vo.CommonVO;
import com.yiziton.dataweb.waybill.vo.ReportConditionVO;
import com.yiziton.dataweb.waybill.vo.ReportExportVO;
import com.yiziton.dataweb.waybill.vo.SelectConditionVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by ouweijian on 2018-12-04 11:31:38.
 */
public interface ReportMapper {

    List<ReportExportVO> selectWayBillInfoForReport(ReportConditionVO wayBillConditionVo);

    Long countWayBillInfoForReport(ReportConditionVO wayBillConditionVo);

    /**
     *
     * @param table 表
     * @param column 列
     * @param value 值
     * @return
     */
    @Select("select distinct ${column} from ${table} where status=1 and ${column} like '%${value}%' order by ${column} desc")
    List<String> selectByParams(@Param("table") String table, @Param("column") String column, @Param("value") String value);

    List<CommonVO> selectForCommon(SelectConditionVO conditionVO);

    Long countForCommon(SelectConditionVO conditionVO);
}
