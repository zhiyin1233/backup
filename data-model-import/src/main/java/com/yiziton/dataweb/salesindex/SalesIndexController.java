package com.yiziton.dataweb.salesindex;

import com.yiziton.dataweb.core.annotation.Action;
import com.yiziton.dataweb.salesindex.pojo.*;
import com.yiziton.dataweb.waybill.dao.ConsignorOpentimeMapper;
import com.yiziton.dataweb.waybill.utils.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 销售指标查询controller
 *
 * @author HuangHuai on 2019-07-11 15:21
 */
@Action("salesindex")
public class SalesIndexController {
    Logger log = LoggerFactory.getLogger(SalesIndexController.class);

    @Autowired
    SalesIndexService       salesIndexService;
    @Autowired
    ConsignorOpentimeMapper consignorOpentimeMapper;
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");

    public static final int newCustomerDefineInterval = 6; //新用户定义间隔月份
    @Autowired
    OrganizeService organizeService;

//    -------------------------------------------- 销售指标-配置start-----------------------------------------------

    /**
     * 配置界面服务类型
     *
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getServerType() throws Exception {
        return EnumUtils.getAllEnum("com.yiziton.commons.vo.enums.ServiceType");
    }

    /**
     * 配置界面始发网点
     *
     * @return
     */
    public List<String> getOriginatingNode() {
        return salesIndexService.getOriginatingNode();
    }

    /**
     * 主页-销售简报-新客户增长趋势
     * 获取新客户增长趋势
     *
     * @return
     */
    public List<Map<String, String>> getNewConsignorTrend(String organizeCode) {
        List<String> organizeCodes = organizeService.getAllDescendantCodes(organizeCode, true).stream().map(x -> x.code).collect(Collectors.toList());
        return salesIndexService.getNewConsignorTrend(organizeCodes);
    }

    /**
     * 销售简报-收入分析-新客户
     * @return
     */
    public List<NewUserVO> getNewConsignor(SalesIndexQueryVO salesIndexQueryVO) {
        List<String> organizeCodes = organizeService.getAllDescendantCodes(salesIndexQueryVO.organize_code, true).stream().map(x -> x.code).collect(Collectors.toList());
        return salesIndexService.getNewConsignor(organizeCodes);
    }
    /**
     * 组织
     * @return
     */
    public List<OrganizeVO> getOrganize() {
        return salesIndexService.getOrganize();
    }

//    -------------------------------------------- 销售指标-配置end-------------------------------------------------

//    -------------------------------------------- 销售指标-收入分析start-----------------------------------------------

    /**
     * 对外查询销售收入接口 <br>
     * 按照部门以及时间
     */
    public Object byDeptAndDate(SalesIndexQueryVO queryVO) {
        // 统计当前条件累加值：运单数，运单收入....
        SalesIndexStatistic statistic = salesIndexService.compute(queryVO);
        SalesIndexStatisticVO vo = new SalesIndexStatisticVO();
        vo.income_dimension.total_billing_income = statistic.total_billing_income;
        vo.income_dimension.total_num_of_waybills = statistic.total_num_of_waybills;
        vo.income_dimension.year_income = salesIndexService.getYearIncome();
        vo.income_dimension.month_income = salesIndexService.getMonthIncome();

        BeanUtils.copyProperties(statistic, vo.income_dimension);
        //统计新客户
        vo.new_customer_dimension = IncomeAnalysisUtil.getNewCusDimension(statistic);
        vo.income_dimension.new_customer_count = vo.new_customer_dimension.new_customer_count;
        vo.income_dimension.customer_count = statistic.customerDimensions.size();


        // 按合作n个月group by 然后sum，最终结果是，所有当月客户聚合求开单数，收入，所有合作1个月的聚合求开单数，收入....
        Collection<List<SalesIndexDimension.CustomerDimension>> values = statistic.customerDimensions.values()
                .stream().filter(e -> e.is_new_customer >= 0)
                .collect(Collectors.groupingBy(m -> m.is_new_customer)).values();
        vo.new_customer_grade_dimension = values.stream().map(l -> {
            SalesIndexDimension.CustomerDimension c = new SalesIndexDimension.CustomerDimension();
            for (SalesIndexDimension.CustomerDimension c1 : l) {
                c.total_num_of_waybills += c1.total_num_of_waybills;
                c.total_billing_income += c1.total_billing_income;
            }
            return c;
        }).collect(Collectors.toList());
        //按部门统计
        vo.department_dimension = IncomeAnalysisUtil.getDeptStatistic(statistic);
        //按top10客户
        vo.customer_dimension = IncomeAnalysisUtil.getCustomerDimension(statistic);
        //按服务类型
        vo.service_type_dimension = IncomeAnalysisUtil.getServiceTypeDimension(statistic);
        //按客户等级
        vo.customer_grade_dimension = IncomeAnalysisUtil.getCustomerGradeDimension(statistic);
        //按结算方式
        vo.settlement_type_dimensions = IncomeAnalysisUtil.getSettlementTypeDimension(statistic);

        return vo;
    }

    public static void nullSafeSetField(Function00 f) {
        try {
            f.apply();
        } catch (Exception e) {

        }
    }

    //    -------------------------------------------- 销售指标-收入分析end-----------------------------------------------


    //    -------------------------------------------- 销售指标-月结钻取start-----------------------------------------------


    //    -------------------------------------------- 销售指标-月结钻取end-----------------------------------------------

    /**
     * 新客户留存数量
     *
     * @param consignorOpentimeDO
     * @return
     */
    public List<Map<String, List<Map<String, Object>>>> queryRetainInfo(ConsignorOpentimeDO consignorOpentimeDO) {
        String clientGrade = consignorOpentimeDO.getClientGrade();
        String organizeCode = consignorOpentimeDO.getOrganizeCode();
        List organizeCodeList = null;
        if (StringUtils.isNotEmpty(organizeCode)) {
            organizeCodeList = organizeService.getAllDescendantCodes(organizeCode, true);
        } else {
            organizeCodeList = null;
        }
        List<Map<String, List<Map<String, Object>>>> retainData = salesIndexService.queryRetainInfo(clientGrade, organizeCodeList);
        return retainData;
    }


    /**
     * 计算是否是新用户
     */
    public int computeIsNewCust(String customer_code, Date yearMonth) {
        int s = Integer.parseInt(DateUtil.SHORT_FORMAT.format(yearMonth)) / 100;
        int e = Integer.parseInt(DateUtil.SHORT_FORMAT.format(DateUtil.getRelativeMonthToCurrent(-7))) / 100;
        return IncomeAnalysisUtil.computeIsNewCust(customer_code, s, e);
    }

    /**
     * 查询出一天的数据，对所有数字类型进行sum 如:销售简报-昨日数据
     *
     * @return
     */
    public Object computeOneDaySum(SalesIndexQueryVO o) {
        SalesIndexQueryVO vo = new SalesIndexQueryVO();
        vo.openBillTimeStart = DateUtil.getOneDayBeginning(o.openBillTimeStart);
        vo.openBillTimeEnd = DateUtil.getOneDayEnding(o.openBillTimeStart);
        List<SalesIndexDO> list = salesIndexService.waybillQuery(vo);
        SalesIndexTotalDimension dimension = new SalesIndexTotalDimension();
        list.forEach(e -> IncomeAnalysisUtil.accumulation(dimension, e));
        return dimension;

    }


    /**
     * 销售简报-开单收入趋势
     *
     * @return
     */
    public Object openBillTrend(HashMap<String, String> map) {
        Integer period = CommonUtil.require(() -> map.get("period"), Integer.class);
        Map<String, Double> trend = new TreeMap<>();
        Date start = null;
        Date end = null;
        if (period.equals(7)) {
            //查看7天内情况 效率低下 只有1/6
            // long s1 = System.currentTimeMillis();
            // for (int i = 0; i < 7; i++) {
            //     start = DateUtil.whichDayDateFromHMS(-i, 0, 0, 0, 0);
            //     end = DateUtil.whichDayDateFromHMS(-i, 23, 59, 59);
            //     data.put(DateUtil.SHORT_FORMAT4.format(start), salesIndexService.sumFee(start, end).get(0).billing_income);
            // }
            // long s2 = System.currentTimeMillis();
            // System.out.println(s2 - s1);

            //方案二 针对数据量不大，可以提升效率  800ms vs 3.5s
            start = DateUtil.whichDayDateFromHMS(-6, 0, 0, 0);
            end = DateUtil.whichDayDateFromHMS(0, 23, 59, 59);
            List<SalesIndexDO> incomes = salesIndexService.queryIncome(start, end);
            for (SalesIndexDO income : incomes) {
                if (income.open_bill_time != null) {
                    String format = DateUtil.SHORT_FORMAT4.format(income.open_bill_time);
                    Double v = trend.get(format);
                    Double income1 = income.billing_income;
                    if (income1 == null) income1 = 0D;
                    if (v == null) trend.put(format, income1);
                    else trend.put(format, v + income1);
                }
            }
        } else {
            //查看半年  半年数据量大，为了减小io 用这种方法
            for (int i = 0; i < 6; i++) {
                start = DateUtil.getRelativeMonthToCurrent(-i);
                end = DateUtil.getLastDateOfMonth(start);
                trend.put(DateUtil.SHORT_FORMAT5.format(start), salesIndexService.sumFee(start, end).get(0).billing_income);
            }
        }
        return trend;
    }

    /**
     * 钻取新客户明细
     *
     * @param queryRetainDetailDO
     * @return
     */
    public List queryRetainDetailInfo(QueryRetainDetailDO queryRetainDetailDO) {
        List customerCodeList = queryRetainDetailDO.getCustomerCodeList();
        List queryRetainDetailData = salesIndexService.queryRetainDetailInfo(customerCodeList);
        return queryRetainDetailData;
    }

    // -------------------------------------------- 销售指标-渗透分析 start --------------------------------------------

    /**
     * 销售指标-渗透分析
     * @param queryVO
     * @return
     * @throws Exception
     */
    public PenetrationAnalysisVO penetrationAnalysis(PenetrationAnalysisVO queryVO) throws Exception {
        List organizeDOS = null;
        if (StringUtils.isNotEmpty(queryVO.getDepartmentCode())) {
            organizeDOS = organizeService.getAllDescendantCodes(queryVO.getDepartmentCode(), true);
        }
        Timestamp openBillTimeBegin = queryVO.getOpenBillTimeBegin();
        Timestamp openBillTimeEnd = queryVO.getOpenBillTimeEnd();
        if(openBillTimeEnd != null){
            queryVO.setOpenBillTimeEnd(new Timestamp(openBillTimeEnd.getTime()+ 86400000L));//加上一天
        }
        if(openBillTimeBegin != null && openBillTimeEnd != null){
            int days = getDays(queryVO.getOpenBillTimeBegin(), queryVO.getOpenBillTimeEnd());
            if(days==31){
                days = 30;//按每月30天计算占比
            }
            queryVO.setDays(days);
        }else{//默认获取当月的
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            // 获取前月的第一天
            Calendar cale = Calendar.getInstance();
            cale.add(Calendar.MONTH, 0);
            cale.set(Calendar.DAY_OF_MONTH, 1);
            Timestamp start = new Timestamp(cale.getTimeInMillis());
            cale = Calendar.getInstance();
            cale.add(Calendar.MONTH, 1);
            cale.set(Calendar.DAY_OF_MONTH, 1);
            Timestamp end = new Timestamp(cale.getTimeInMillis());
            queryVO.setOpenBillTimeBegin(start);
            queryVO.setOpenBillTimeEnd(end);
        }
        PenetrationAnalysisVO penetrationAnalysisVO = salesIndexService
                .queryBySalesIndexQueryVO(queryVO, organizeDOS);
        return penetrationAnalysisVO;
    }


    /**
     * 计算两个日期之间相差的天数
     * @param end
     * @param start
     * @return
     */
    public static int getDays(Timestamp end,Timestamp start){
        Calendar aCalendar = Calendar.getInstance();
        Calendar bCalendar = Calendar.getInstance();
        aCalendar.setTime(end);
        bCalendar.setTime(start);
        int days = 0;
        while(aCalendar.before(bCalendar)){
            days++;
            aCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if(days==0){
            aCalendar.setTime(start);
            bCalendar.setTime(end);
            while(aCalendar.before(bCalendar)){
                days++;
                aCalendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
        return days;
    }


    // -------------------------------------------- 销售指标-渗透分析 end ----------------------------------------------



}
