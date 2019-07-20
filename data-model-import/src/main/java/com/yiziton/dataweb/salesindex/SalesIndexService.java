package com.yiziton.dataweb.salesindex;


import com.yiziton.dataweb.salesindex.pojo.*;
import com.yiziton.dataweb.waybill.dao.ConsignorOpentimeMapper;
import com.yiziton.dataweb.waybill.dao.SalesIndexMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 销售指标service
 */
@Service
public class SalesIndexService implements InitializingBean {
    static Logger log = LoggerFactory.getLogger(SalesIndexService.class);
    Map<String, List<SalesIndexDO>> cache = new ConcurrentHashMap<>();
    @Autowired
    SalesIndexMapper        salesIndexMapper;
    @Autowired
    SalesIndexService       salesIndexService;
    @Autowired
    ConsignorOpentimeMapper consignorOpentimeMapper;
    @Autowired
    OrganizeService         organizeService;

    public static Map<String, List<ConsignorOpentimeDO>> customerCodeToConsignorOpentime = new HashMap<>();//客户编码 对应 ConsignorOpentime表
    public static List<ConsignorOpentimeDO>              consignorOpentime               = new ArrayList<>(0);//ConsignorOpentime表


    /**
     * 获取配置界面始发网点
     * @return
     */
    public List<String> getOriginatingNode() {
        return salesIndexMapper.querOriginatingNode();
    }

    public List<Map<String, String>> getNewConsignorTrend(List<String> organizeCodes) {
        return  salesIndexMapper.querNewConsignorTrend(organizeCodes);
    }

    public List<NewUserVO> getNewConsignor(List<String> organizeCodes) {
        return  salesIndexMapper.querNewConsignor(organizeCodes);
    }
    public List<OrganizeVO> getOrganize() {
        return  salesIndexMapper.getOrganize();
    }

    /**
     * 销售收入分析
     *
     * @param query
     */
    public SalesIndexStatistic compute(SalesIndexQueryVO query) {
        if (query == null) {
            query = new SalesIndexQueryVO();
            query.openBillTimeStart = DateUtil.todayDateFromHMS(0, 0, 1); //00:00:01
            query.openBillTimeEnd = DateUtil.todayDateFromHMS(24);//明天0点
        }
        SalesIndexStatistic vo = new SalesIndexStatistic();

        long s1 = System.currentTimeMillis();

        Calendar c = Calendar.getInstance();
        c.setTime(query.openBillTimeEnd);
        c.add(Calendar.MONTH, -(SalesIndexController.newCustomerDefineInterval + 1));
        //新客户查询月
        int newCusStartYearMonth = Integer.parseInt(SalesIndexController.dateFormat.format(query.openBillTimeEnd).replace("-", ""));
        //新客户终止月,默认6个月前
        int newCusEndYearMonth = Integer.parseInt(SalesIndexController.dateFormat.format(new Date(c.getTimeInMillis())).replace("-", ""));

        List<SalesIndexDO> all = salesIndexService.queryByDateAndDept(query);

        System.out.println("查询到条数：" + all.size() + " 条件：" + query);

        //开单总收入,开单总票数, 各种费用汇总 sum
        long s2 = System.currentTimeMillis();
        System.out.println("取数耗时：" + (s2 - s1) / 1000d);

        all.forEach(e -> IncomeAnalysisUtil.statisticAll(vo, e));
        long s3 = System.currentTimeMillis();
        System.out.println("计算耗时：" + (s3 - s2) / 1000d);
        computeNewCust(vo.customerDimensions, newCusStartYearMonth, newCusEndYearMonth);
        return vo;
    }


    /**
     * 此cache仅允许查询，绝不允许对里面的对象set值，如果需要set并计算，请不要使用cache另起方法
     *
     * @param vo
     * @return
     */
    public List<SalesIndexDO> queryByDateAndDept(SalesIndexQueryVO vo) {
        List<SalesIndexDO> list = this.cache.get(SalesIndexUtil.getCacheKey(vo));
        if (list == null || list.isEmpty()) {
            synchronized (cache) {
                List<OrganizeDO> descendants = organizeService.getAllDescendantCodes(vo.organize_code, true);
                list = salesIndexMapper.queryByDateAndDept(vo.openBillTimeStart, vo.openBillTimeEnd,
                        SalesIndexUtil.getOrganizeCodeInClause(descendants));
                cache.putIfAbsent(SalesIndexUtil.getCacheKey(vo), list);
            }
        }
        return list;
    }


    public void computeNewCust(Map<String, SalesIndexDimension.CustomerDimension> customerDimensions,
                               int newCusStartYearMonth, int newCusEndYearMonth) {
        customerDimensions.values().forEach(d -> d.is_new_customer =
                IncomeAnalysisUtil.computeIsNewCust(d.customer_code, newCusStartYearMonth, newCusEndYearMonth));
    }








    public List<SalesIndexDO> waybillQuery(SalesIndexQueryVO vo) {
        return salesIndexMapper.waybillQuery(getOrganizeCodeVO(vo));
    }

    public List<SalesIndexDO> customerQuery(SalesIndexQueryVO vo) {
        SalesIndexDO copy = getOrganizeCodeVO(vo);
        return salesIndexMapper.customerQuery(copy);
    }

    /**
     * 避免污染原来的vo的organize_code值
     *
     * @param vo
     * @return
     */
    private SalesIndexDO getOrganizeCodeVO(SalesIndexQueryVO vo) {
        List<OrganizeDO> list = organizeService.getAllDescendantCodes(vo.organize_code, true);
        SalesIndexDO copy = BeanUtil.copy(vo, SalesIndexQueryVO.class);
        copy.organize_code = SalesIndexUtil.getOrganizeCodeInClause(list);
        return copy;
    }

    /**
     * 查询留存新客户数量及留存率接口
     *
     * @param "clientGrade"
     * @param "departmentList"
     * @return 月份，留存客户数，留存率
     */

    public List<Map<String,List<Map<String,Object>>>>  queryRetainInfo(String clientGrade, List organizeCodeList){
        //返回的最终list
        List<Map<String,List<Map<String,Object>>>> retainDataInfoList = new ArrayList<>();
        //查找所有符合条件的新客户code及开单月份
        List<QueryRetainDO> retainDataList = salesIndexMapper.queryRetain(clientGrade,organizeCodeList);
        //查找所有符合条件的所有客户code及开单月份
        List<QueryRetainDO> retainDataAllList = salesIndexMapper.queryRetainAll(clientGrade,organizeCodeList);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        //获取当前月,循环11次补全新客户数据
        if(retainDataList.size() != 12){
            for(int s1 =11; s1>=0; s1--){
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH,-s1);
                String yearMonth = sdf.format(cal.getTime());
                QueryRetainDO queryRetainItem = new QueryRetainDO();
                queryRetainItem.setOpenBillMonth(yearMonth);
                queryRetainItem.setCustomerCode(null);
                queryRetainItem.setConsignorCount(0);
                if(retainDataList.size()<=11-s1){
                    retainDataList.add(11-s1,queryRetainItem);
                }else{
                    if (!retainDataList.get(11-s1).getOpenBillMonth().equals(yearMonth) ){
                        retainDataList.add(11-s1,queryRetainItem);
                    }
                }
            }
        }
        //获取当前月，循环10次补全所有开单客户数据
        if(retainDataAllList.size() != 11){
            for(int s2 =10; s2>=0; s2--){
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH,-s2);
                String yearMonth = sdf.format(cal.getTime());
                QueryRetainDO queryRetainItem = new QueryRetainDO();
                queryRetainItem.setOpenBillMonth(yearMonth);
                queryRetainItem.setCustomerCode(null);
                if(retainDataAllList.size()<=11-s2){
                    retainDataAllList.add(11-s2,queryRetainItem);
                }else{
                    if (!retainDataAllList.get(11-s2).getOpenBillMonth().equals(yearMonth) ){
                        retainDataAllList.add(11-s2,queryRetainItem);
                    }
                }
            }
        }

        for(int i =0;i<retainDataList.size();i++){
            Map<String,List<Map<String,Object>>> retainDataMap = new HashMap<>();
            List<Map<String,Object>> retainDataItemList = new ArrayList<>();
            Map<String,Object> retainDataItemMap0 = new HashMap<>();
            int count0 = retainDataList.get(i).getConsignorCount();
            retainDataItemMap0.put("M"+0,String.valueOf(count0));
            List<String> relist0 =null;
            if(retainDataList.get(i).getCustomerCode()!=null){
                relist0 = Arrays.asList(retainDataList.get(i).getCustomerCode().split(","));
            }
            retainDataItemMap0.put("codes",relist0);
            retainDataItemMap0.put("rate"+0,String.valueOf(count0));
            retainDataItemList.add(retainDataItemMap0);
            int  k =1;

            for(int j =i; j<= 10;j++){
                List<String> relist = new ArrayList<>();
                Map<String,Object> retainDataItemMapj = new HashMap<>();
                if(relist0 == null){
                    retainDataItemMapj.put("M"+k,"0");
                    retainDataItemMapj.put("codes",null);
                    retainDataItemMapj.put("rate"+k,"0%");
                }else{
                    if(retainDataAllList.get(j).getCustomerCode()!= null){
                        List<String> relistj = Arrays.asList(retainDataAllList.get(j).getCustomerCode().split(","));
                        //取与第一个月新客户的交集
                        relist = relist0.stream().filter(item -> relistj.contains(item)).collect(Collectors.toList());
                    }
                    retainDataItemMapj.put("M"+k,String.valueOf(relist.size()));
                    retainDataItemMapj.put("codes",relist);
                    String rate = String.valueOf(Math.round(relist.size()*100/count0))+"%";
                    retainDataItemMapj.put("rate"+k,rate);
                }
                retainDataItemList.add(retainDataItemMapj);
                k++;
            }
            retainDataMap.put(retainDataList.get(i).getOpenBillMonth(),retainDataItemList);
            retainDataInfoList.add(retainDataMap);
        }
        System.out.println(retainDataInfoList);
        return retainDataInfoList;

    }

    /**
     * 钻取客户明细
     * @param customerCodeList
     * @return
     */
    public List queryRetainDetailInfo(List customerCodeList){
        List queryRetainDetailList = salesIndexMapper.queryRetainDetail(customerCodeList);
        return queryRetainDetailList;

    }

    /**
     * 销售简报-渗透分析
     * @param queryVO
     * @param organizeDOS
     * @return
     * @throws Exception
     */
    public PenetrationAnalysisVO queryBySalesIndexQueryVO(PenetrationAnalysisVO queryVO,List organizeDOS) throws Exception{

        PenetrationAnalysisDO all = salesIndexMapper.queryPenetrationAnalysis(queryVO, organizeDOS);
        List<PenetrationAnalysisDO> customerGrades = salesIndexMapper.queryPenetrationAnalysisCustomerGrade(queryVO, organizeDOS);
        List<PenetrationAnalysisDO> settlementTypes = salesIndexMapper.queryPenetrationAnalysisSettlementType(queryVO, organizeDOS);

        PenetrationAnalysisVO penetrationAnalysisVO = new PenetrationAnalysisVO();
        penetrationAnalysisVO.setAll(all);
        penetrationAnalysisVO.setCustomerGrades(customerGrades);
        penetrationAnalysisVO.setSettlementTypes(settlementTypes);
        return penetrationAnalysisVO;
    }
    public List<SalesIndexDO> sumFee(Date start, Date end) {
        return salesIndexMapper.sumFee(start, end);
    }

    static Double currentMonthIncome;
    static Double currentYearIncome;
    synchronized double getYearIncome( ) {
        if (currentMonthIncome == null) {
            computeCurrentYearAndMonthTotalIncome();
        }
        return currentYearIncome;
    }

    synchronized double getMonthIncome( ) {
        if (currentMonthIncome == null) {
            computeCurrentYearAndMonthTotalIncome();
        }
        return currentMonthIncome;
    }

    private void computeCurrentYearAndMonthTotalIncome() {
        Calendar c = Calendar.getInstance();
        int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1, 0, 0, 0);
        Date start = c.getTime();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), maxDay, 0, 0, 0);
        Date end = c.getTime();


        //月
        List<SalesIndexDO> list = salesIndexService.sumFee(start, end);
        currentMonthIncome = list.get(0).billing_income;
        BigDecimal b = new BigDecimal(currentMonthIncome);
        currentMonthIncome = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        //年
        c.set(c.get(Calendar.YEAR), 0, 1, 0, 0, 0);
        start = c.getTime();
        c.set(Calendar.MONTH, 12);
        c.set(c.get(Calendar.YEAR), 11, c.getActualMaximum(Calendar.DAY_OF_MONTH), 0, 0, 0);
        end = c.getTime();

        currentYearIncome = salesIndexService.sumFee(start, end).get(0).billing_income;
        b = new BigDecimal(currentYearIncome);
        currentYearIncome = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

    }

    /**
     * 查询所有发货人表信息缓存到map方便计算新客户
     */
    void queryConsignorOpentime( ) {
        consignorOpentime = consignorOpentimeMapper.queryAll();
        customerCodeToConsignorOpentime = consignorOpentime.stream().collect(Collectors.groupingBy(e -> e.customerCode));
        //按时间逆序，大到小
        for (String code : customerCodeToConsignorOpentime.keySet()) {
            Collections.sort(customerCodeToConsignorOpentime.get(code), (e1, e2) -> e2.openBillMonth.compareTo(e1.openBillMonth));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(this::queryConsignorOpentime).start();
    }

    public List<SalesIndexDO> queryIncome(Date startDate, Date endDate) {
        return salesIndexMapper.queryIncome(startDate, endDate);
    }
}
