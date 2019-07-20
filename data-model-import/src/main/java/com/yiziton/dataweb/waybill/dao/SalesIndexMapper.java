package com.yiziton.dataweb.waybill.dao;

import com.yiziton.dataweb.salesindex.pojo.*;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author HuangHuai on 2019-07-15 14:27
 */

public interface SalesIndexMapper {


    List<SalesIndexDO> queryAll();

    List<SalesIndexDO> queryLimit(@Param("start") int start, @Param("size") int size);

    List<SalesIndexDO> queryByDateAndDept(@Param("startDate") Date startDate,
                                          @Param("endDate") Date endDate,
                                          @Param("organize_codes") String organize_codes);

    List<QueryRetainDO> queryRetain(@Param("clientGrade") String clientGrade,
                                    @Param("organizeCodeList") List organizeCodeList
    );

    List<QueryRetainDO> queryRetainAll(@Param("clientGrade") String clientGrade,
                                       @Param("organizeCodeList") List organizeCodeList
    );

    List<QueryRetainDetailDO> queryRetainDetail(@Param("customerCodeList") List customerCodeList);



    /*List<SalesIndexDO> queryBySelective(SalesIndexDO salesIndexDO);*/


    List<SalesIndexDO> waybillQuery(SalesIndexDO vo);

    List<SalesIndexDO> customerQuery(SalesIndexDO vo);

    List<String> querOriginatingNode();

    List<Map<String, String>> querNewConsignorTrend(@Param("organizeCodes") List<String> organizeCodes);

    List<NewUserVO> querNewConsignor(@Param("organizeCodes") List<String> organizeCodes);

    List<OrganizeVO> getOrganize();

    PenetrationAnalysisDO queryPenetrationAnalysis(@Param("queryVO") PenetrationAnalysisVO queryVO, @Param("organizeDOS") List<OrganizeDO> organizeDOS);

    List<PenetrationAnalysisDO> queryPenetrationAnalysisCustomerGrade(@Param("queryVO") PenetrationAnalysisVO queryVO, @Param("organizeDOS") List<OrganizeDO> organizeDOS);

    List<PenetrationAnalysisDO> queryPenetrationAnalysisSettlementType(@Param("queryVO") PenetrationAnalysisVO queryVO, @Param("organizeDOS") List<OrganizeDO> organizeDOS);


    @ResultMap("noCamelSalesIndexDO")
    @Select("SELECT sum(billing_income) billing_income FROM salesindex where open_bill_time > #{startDate} and open_bill_time <#{endDate}")
    List<SalesIndexDO> sumFee(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Results({@Result(column = "billing_income", property = "billing_income"), @Result(column = "open_bill_time", property = "open_bill_time")})
    @Select("SELECT billing_income,open_bill_time FROM salesindex where open_bill_time > #{startDate} and open_bill_time <#{endDate}")
    List<SalesIndexDO> queryIncome(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
