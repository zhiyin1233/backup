package com.yiziton.dataweb.salesindex;

import com.yiziton.dataweb.salesindex.pojo.*;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author HuangHuai on 2019-07-17 10:06
 */

public class IncomeAnalysisUtil {
    static Logger log = LoggerFactory.getLogger(IncomeAnalysisUtil.class);



    /**
     * vo赋值
     *
     * @param dimension
     * @param indexDO
     */
    public static void assignment(SalesIndexTotalDimension dimension, SalesIndexDO indexDO) {
        if (dimension instanceof SalesIndexDimension.CustomerDimension) {
            SalesIndexDimension.CustomerDimension d = (SalesIndexDimension.CustomerDimension) dimension;
            d.salesman_name = indexDO.salesman;
            d.customer_type = indexDO.settlement_type;
            d.customer_grade = indexDO.client_grade;
            d.customer_name = indexDO.customer_name;
            d.customer_code = indexDO.customer_code;

        }

        if (dimension instanceof SalesIndexDimension.ServiceTypeDimension) {
            SalesIndexDimension.ServiceTypeDimension d = (SalesIndexDimension.ServiceTypeDimension) dimension;
            d.service_type = indexDO.service_type;
        }

        if (dimension instanceof SalesIndexDimension.CustomerGradeDimension) {
            SalesIndexDimension.CustomerGradeDimension d = (SalesIndexDimension.CustomerGradeDimension) dimension;
            d.customer_grade = indexDO.client_grade;
        }

        if (dimension instanceof SalesIndexDimension.SettlementTypeDimension) {
            SalesIndexDimension.SettlementTypeDimension d = (SalesIndexDimension.SettlementTypeDimension) dimension;
            d.settlement_type = indexDO.settlement_type;
        }

        if (dimension instanceof SalesIndexDimension.SalesmanDimension) {
            SalesIndexDimension.SalesmanDimension d = (SalesIndexDimension.SalesmanDimension) dimension;
            d.salesman_name = indexDO.salesman;
        }

        if (dimension instanceof SalesIndexDimension.DepartmentDimension) {
            SalesIndexDimension.DepartmentDimension d = (SalesIndexDimension.DepartmentDimension) dimension;
            d.department_name = indexDO.sales_departments;
            d.parent_code = indexDO.parent_code;
            d.department_code = indexDO.organize_code;
            d.department_grade = indexDO.departments_grade;
        }
    }

    //累加各种税
    public static void accumulation(SalesIndexDimension vo, SalesIndexDO indexDO) {
        //累加所有费用

        SalesIndexController.nullSafeSetField(() -> vo.take_goods_fee += indexDO.take_goods_fee);
        SalesIndexController.nullSafeSetField(() -> vo.transport_fee += indexDO.transport_fee);
        SalesIndexController.nullSafeSetField(() -> vo.delivery_fee += indexDO.delivery_fee);
        SalesIndexController.nullSafeSetField(() -> vo.install_fee += indexDO.install_fee);
        SalesIndexController.nullSafeSetField(() -> vo.insurance_fee += indexDO.insurance_fee);
        SalesIndexController.nullSafeSetField(() -> vo.upstairs_fee += indexDO.upstairs_fee);
        SalesIndexController.nullSafeSetField(() -> vo.entry_home_fee += indexDO.entry_home_fee);
        SalesIndexController.nullSafeSetField(() -> vo.door_fee += indexDO.door_fee);
        SalesIndexController.nullSafeSetField(() -> vo.exceed_square_fee += indexDO.exceed_square_fee);
        SalesIndexController.nullSafeSetField(() -> vo.wooden_fee += indexDO.wooden_fee);
        SalesIndexController.nullSafeSetField(() -> vo.exceed_area_fee += indexDO.exceed_area_fee);
        SalesIndexController.nullSafeSetField(() -> vo.large_extra_fee += indexDO.large_extra_fee);
        SalesIndexController.nullSafeSetField(() -> vo.marble_fee += indexDO.marble_fee);
        SalesIndexController.nullSafeSetField(() -> vo.other_fee += indexDO.other_fee);
        SalesIndexController.nullSafeSetField(() -> vo.increment_service_fee += indexDO.increment_service_fee);
        SalesIndexController.nullSafeSetField(() -> vo.coupon_relief += indexDO.coupon_relief);
        SalesIndexController.nullSafeSetField(() -> vo.gift_relief += indexDO.gift_relief);
        SalesIndexController.nullSafeSetField(() -> vo.cash_relief += indexDO.cash_relief);
        SalesIndexController.nullSafeSetField(() -> vo.client_fee += indexDO.client_fee);
        SalesIndexController.nullSafeSetField(() -> vo.total_num_of_waybills++);
        SalesIndexController.nullSafeSetField(() -> vo.total_billing_income += indexDO.billing_income);
    }

    //累加单数以及收入
    public static void accumulation(SalesIndexTotalDimension vo, SalesIndexDO indexDO) {
        vo.total_billing_income += indexDO.billing_income == null ? 0 : indexDO.billing_income;
        vo.total_num_of_waybills++;
    }

    public static void computeBySalesDepartment(SalesIndexStatistic vo, SalesIndexDO indexDO) {
        if (indexDO.sales_departments != null) {
            SalesIndexDimension.DepartmentDimension dimension = vo.departmentDimensions.get(indexDO.sales_departments);
            if (dimension == null) {
                vo.departmentDimensions.put(indexDO.sales_departments, dimension = new SalesIndexDimension.DepartmentDimension());
                assignment(dimension, indexDO);
            }
            accumulation(dimension, indexDO);
        }
    }

    public static void computeByCustomerGrade(SalesIndexStatistic vo, SalesIndexDO indexDO) {
        if (indexDO.client_grade != null) {
            SalesIndexDimension.CustomerGradeDimension dimension = vo.customerGradeDimensions.get(indexDO.client_grade);
            if (dimension == null) {
                vo.customerGradeDimensions.put(indexDO.client_grade, dimension = new SalesIndexDimension.CustomerGradeDimension());
                assignment(dimension, indexDO);
            }
            accumulation(dimension, indexDO);
        }
    }

    public static void computeByCustomer(SalesIndexStatistic vo, SalesIndexDO indexDO) {
        if (indexDO.customer_name != null) {
            SalesIndexDimension.CustomerDimension dimension = vo.customerDimensions.get(indexDO.customer_name);
            if (dimension == null) {
                vo.customerDimensions.put(indexDO.customer_name, dimension = new SalesIndexDimension.CustomerDimension());
                assignment(dimension, indexDO);
            }
            accumulation(dimension, indexDO);
        }
    }

    public static void computeByServiceType(SalesIndexStatistic vo, SalesIndexDO indexDO) {
        if (indexDO.service_type != null) {
            SalesIndexDimension.ServiceTypeDimension dimension = vo.serviceTypeDimensions.get(indexDO.service_type);
            if (dimension == null) {
                vo.serviceTypeDimensions.put(indexDO.service_type, dimension = new SalesIndexDimension.ServiceTypeDimension());
                assignment(dimension, indexDO);
            }
            accumulation(dimension, indexDO);
        }
    }

    public static void computeBySalesman(SalesIndexStatistic vo, SalesIndexDO indexDO) {
        if (indexDO.salesman != null) {
            SalesIndexDimension.SalesmanDimension dimension = vo.salesmanDimensions.get(indexDO.salesman);
            if (dimension == null) {
                vo.salesmanDimensions.put(indexDO.salesman, dimension = new SalesIndexDimension.SalesmanDimension());
                assignment(dimension, indexDO);
            }
            accumulation(dimension, indexDO);
        }
    }

    /**
     * 按部门维度
     *
     * @param vo
     */
    public static List<SalesIndexDimension.DepartmentDimension> getDeptStatistic(SalesIndexStatistic vo) {
        ArrayList<SalesIndexDimension.DepartmentDimension> list = new ArrayList<>(vo.departmentDimensions.values());
        Map<String, List<SalesIndexDimension.CustomerDimension>> groupByDept =
                vo.customerDimensions.values().stream().filter(e -> e.department_code != null).collect(Collectors.groupingBy(e -> e.department_code));
        Collections.sort(list, (e1, e2) -> (int) (e2.total_billing_income - e1.total_billing_income));
        list.forEach(e -> {
            List<SalesIndexDimension.CustomerDimension> customerDimensions = groupByDept.get(e.department_code);
            e.new_customer_count = customerDimensions == null ? 0 : customerDimensions.size();
        });
        return list.size() > 10 ? list.subList(0, 10) : list;
    }

    /**
     * 按客户维度
     *
     * @param vo
     */
    public static List<SalesIndexDimension.CustomerDimension> getCustomerDimension(SalesIndexStatistic vo) {
        ArrayList<SalesIndexDimension.CustomerDimension> list = new ArrayList<>(vo.customerDimensions.values());
        Collections.sort(list, (e1, e2) -> (int) (e2.total_billing_income - e1.total_billing_income));
        return list.size() > 10 ? list.subList(0, 10) : list;
    }

    /**
     * 按服务类型
     *
     * @param vo
     */
    public static List<SalesIndexDimension.ServiceTypeDimension> getServiceTypeDimension(SalesIndexStatistic vo) {
        ArrayList<SalesIndexDimension.ServiceTypeDimension> list = new ArrayList<>(vo.serviceTypeDimensions.values());
        Collections.sort(list, (e1, e2) -> (int) (e2.total_billing_income - e1.total_billing_income));
        return list.size() > 10 ? list.subList(0, 10) : list;
    }

    /**
     * 按客户等级
     *
     * @param vo
     */
    public static List<SalesIndexDimension.CustomerGradeDimension> getCustomerGradeDimension(SalesIndexStatistic vo) {
        ArrayList<SalesIndexDimension.CustomerGradeDimension> list = new ArrayList<>(vo.customerGradeDimensions.values());
        Collections.sort(list, (e1, e2) -> (int) (e2.total_billing_income - e1.total_billing_income));
        return list.size() > 10 ? list.subList(0, 10) : list;
    }

    /**
     * 按结算方式
     *
     * @param statistic
     * @return
     */
    public static List<SalesIndexDimension.SettlementTypeDimension> getSettlementTypeDimension(SalesIndexStatistic statistic) {
        ArrayList<SalesIndexDimension.SettlementTypeDimension> list = new ArrayList<>(statistic.settlementTypeDimensions.values());
        return list.size() > 10 ? list.subList(0, 10) : list;
    }

    public static SalesIndexDimension.NewCustomerDimension getNewCusDimension(SalesIndexStatistic statistic) {
        SalesIndexDimension.NewCustomerDimension newCustomerDimension = new SalesIndexDimension.NewCustomerDimension();
        for (SalesIndexDimension.CustomerDimension customerDimension : statistic.customerDimensions.values()) {
            if (customerDimension.is_new_customer >= 0) {
                newCustomerDimension.total_billing_income += customerDimension.total_billing_income;
                newCustomerDimension.total_num_of_waybills += customerDimension.total_num_of_waybills;
                newCustomerDimension.new_customer_count++;
            }
        }
        newCustomerDimension.current_month_customer_count = computeCurrentMonthNewCustNum();
        return newCustomerDimension;
    }

    /**
     * 计算当前月总共有多少个新客户
     *
     * @return
     */
    private static Long computeCurrentMonthNewCustNum() {
        long count = 0;
        int s = Integer.parseInt(DateUtil.SHORT_FORMAT.format(new Date())) / 100;
        int e = Integer.parseInt(DateUtil.SHORT_FORMAT.format(DateUtil.getRelativeMonthToCurrent(-7))) / 100;
        List<String> collect = SalesIndexService.consignorOpentime.stream().map(e1 -> e1.customerCode).distinct().collect(Collectors.toList());
        for (String customerCode : collect) {
            if (computeIsNewCust(customerCode, s, e) >= 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * 根据consignor_opentime表数据，根据新客户定义规则，逆推是否是新客户   <br>
     * 0查询当月新客户，1合作一个月新客户... -1不是新客户
     *
     * @param newCusStartYearMonth
     * @param newCusEndYearMonth
     */
    public static int computeIsNewCust(String customer_code, int newCusStartYearMonth,
                                int newCusEndYearMonth) {
        try {
            int isNew = -1;
            int openBillYear = 0;
            int openBillMonth = 0;
            int startYear = newCusStartYearMonth / 100;
            int startMonth = newCusStartYearMonth % startYear;


            //从consignor_opentime找出该客户，然后向上时间遍历6个月初步判断是否是新用户以及合作几个月新用户
            for (ConsignorOpentimeDO opentimeDO : SalesIndexService.customerCodeToConsignorOpentime.get(customer_code)) {
                if (opentimeDO.customerCode.equals(customer_code)) {
                    int openBillYearMonth = Integer.parseInt(opentimeDO.openBillMonth.replace("-", ""));


                    //如果在选择月份以及以前开了单，初步认为是新客户  //说明在当月以及前六个月内
                    if (openBillYearMonth <= newCusStartYearMonth && openBillYearMonth > newCusEndYearMonth) {
                        openBillYear = openBillYearMonth / 100;
                        openBillMonth = openBillYearMonth % openBillYear;
                        //计算是几个月合作用户
                        isNew = (startYear - openBillYear) * 12 + startMonth - openBillMonth;
                    }
                }
            }

            //计算完了,还需要再次校验前六个月有没有开单,如果有,就不是了
            //如查询月是2019.7月，发现2月有开单，应该是合作5个月新用户，但是还需要再向上查6个月就是到2018.11月到2019.5都没开才算合作5个月
            if (isNew > 0) {
                //时间上限
                int yearMonthLimit;
                int i = openBillMonth - SalesIndexController.newCustomerDefineInterval - 1;
                if (i > 0) {
                    //月份足够不需跨年
                    yearMonthLimit = openBillYear * 100 + (openBillMonth - SalesIndexController.newCustomerDefineInterval);
                } else {
                    //月份不够，要跨年
                    yearMonthLimit = (openBillYear - 1) * 100 + (12 + i);
                }


                for (ConsignorOpentimeDO opentimeDO : SalesIndexService.customerCodeToConsignorOpentime.get(customer_code)) {
                    if (opentimeDO.customerCode.equals(customer_code)) {
                        //如果能找到一单在上限时间内，说明不算新客户
                        if (Integer.parseInt(opentimeDO.openBillMonth.replace("-", "")) < yearMonthLimit) {
                            isNew = -1;
                        }
                    }
                }
            }
            return isNew;
        } catch (Exception e) {
            if (e.getMessage() == null || "null".equals(e.getMessage())) {
                log.error("计算是否是新用户失败:" + e.getMessage());
            } else {
                log.error("计算是否是新用户失败:" + e.getMessage(), e);
            }
        }
        return -1;
    }

    private static void computeBySettlementType(SalesIndexStatistic vo, SalesIndexDO indexDO) {
        if (indexDO.settlement_type != null) {
            SalesIndexDimension.SettlementTypeDimension dimension = vo.settlementTypeDimensions.get(indexDO.settlement_type);
            if (dimension == null) {
                vo.settlementTypeDimensions.put(indexDO.settlement_type, dimension = new SalesIndexDimension.SettlementTypeDimension());
                assignment(dimension, indexDO);
            }
            if ("月结".equals(dimension.settlement_type)) {
                if (dimension.monthly_settlements == null) {
                    dimension.monthly_settlements = new HashMap<>();
                }
                SalesIndexDimension.MonthlySettlement monthlySettlement = dimension.monthly_settlements.get(indexDO.payment_period);
                if (monthlySettlement == null) {
                    dimension.monthly_settlements.put(indexDO.payment_period, monthlySettlement = new SalesIndexDimension.MonthlySettlement());
                    monthlySettlement.payment_period = indexDO.payment_period;
                }
                accumulation(monthlySettlement, indexDO);
            }
            accumulation(dimension, indexDO);
        }
    }

    /**
     * 累加所有需要总计的 <br>
     * 这些数据都是需要根据各个维度累加，如果使用不同接口，一个月的数据会查询十遍计算十遍
     *
     * @param vo
     * @param indexDO
     */
    public static void statisticAll(SalesIndexStatistic vo, SalesIndexDO indexDO) {
        accumulation(vo, indexDO);
        //1.累加指定销售人员的所有指标
        computeBySalesman(vo, indexDO);
        //2.累加指定服务类型的所有指标
        computeByServiceType(vo, indexDO);
        //3.累加用户
        computeByCustomer(vo, indexDO);
        //4.客户等级维度
        computeByCustomerGrade(vo, indexDO);
        //5.部门维度
        computeBySalesDepartment(vo, indexDO);
        //6.结算方式及账期
        computeBySettlementType(vo, indexDO);
    }
}
