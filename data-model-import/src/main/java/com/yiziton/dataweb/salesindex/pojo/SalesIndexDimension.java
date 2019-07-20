package com.yiziton.dataweb.salesindex.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 销售指标基础维度
 *
 * @author HuangHuai on 2019-07-12 16:23
 */
@Data
public class SalesIndexDimension extends SalesIndexTotalDimension implements Serializable {
    /**
     * 揽货费
     */
    public double take_goods_fee;

    /**
     * 运输费
     */
    public double transport_fee;

    /**
     * 基础送货费
     */
    public double delivery_fee;

    /**
     * 安装费
     */
    public double install_fee;

    /**
     * 保价费
     */
    public double insurance_fee;

    /**
     * 上楼费
     */
    public double upstairs_fee;

    /**
     * 入户费
     */
    public double entry_home_fee;

    /**
     * 上门服务费
     */
    public double door_fee;

    /**
     * 超方费
     */
    public double exceed_square_fee;

    /**
     * 木架费
     */
    public double wooden_fee;

    /**
     * 超区费
     */
    public double exceed_area_fee;

    /**
     * 大票货附加费
     */
    public double large_extra_fee;

    /**
     * 大理石费
     */
    public double marble_fee;

    /**
     * 其他费
     */
    public double other_fee;

    /**
     * 增值服务费
     */
    public double increment_service_fee;

    /**
     * 优惠券
     */
    public double coupon_relief;

    /**
     * 赠送金
     */
    public double gift_relief;

    /**
     * 返现金
     */
    public double cash_relief;

    /**
     * 客户物流费
     */
    public double client_fee;


    /**
     * 1.部门维度 <br>
     */
    public static class DepartmentDimension extends NewCustomerDimension {
        public String department_name; //部门名称
        public String department_code; //部门代码
        public String parent_code; //父部门代码
        public int    department_grade;//部门是一级、二级、三级。。。
    }

    /**
     * 2.客户维度（发货人，客户）--》4.top10客户维度
     */
    public static class CustomerDimension extends DepartmentDimension {
        public String salesman_name;    //业务员名
        public String customer_type;     //客户类型
        public String customer_grade;     //客户等级
        public String customer_code;     //客户编码

        public String customer_name;     //客户名
        public int    is_new_customer = -1;    // 是否是当月新客户，  -1代表不是新客户   0代表当月  1代表合作一个月...
    }


    /**
     * 2.客户维度（发货人，客户）--》4.top10客户维度
     */
    public static class NewCustomerDimension extends SalesIndexTotalDimension {
        public long new_customer_count; //当前查询日期及条件内的新用户数
        public Long current_month_customer_count;//当月新用户
    }


    /**
     * 3.收入项维度（总）放在vo中即可统计<br>
     */


    /**
     * 5.服务类型维度
     */
    public static class ServiceTypeDimension extends SalesIndexTotalDimension {
        public String service_type; //服务类型
    }

    /**
     * 6.客户等级维度 <br>
     */
    public static class CustomerGradeDimension extends SalesIndexTotalDimension {
        public String customer_grade; //客户等级
    }

    /**
     * 7.结算方式维度 <br>
     */
    public static class SettlementTypeDimension extends SalesIndexTotalDimension {
        public String                         settlement_type; //结算类型
        public Map<String, MonthlySettlement> monthly_settlements;

    }


    //月结客户，有账期需另算
    public static class MonthlySettlement extends SalesIndexTotalDimension {
        public String payment_period;//账期
    }

    /**
     * 0.收入分析 <br>
     */
    public static class IncomeDimension extends SalesIndexDimension {
        public double month_income; //当月收入
        public double year_income; //本年度收入

        public long new_customer_count; //新客户数
        public long customer_count;     //客户数
    }

    /**
     * 销售人员维度 <br>
     */
    public static class SalesmanDimension extends DepartmentDimension {
        public String salesman_name; //销售人员名称
        //public String[]                       muti_department = new String[5];//多级部门 0号元素代表公司级别,1号代表一级部门,2号代表二级部门最多有四级部门
        //public Map<String, CustomerDimension> shippers        = new HashMap<>();  //该销售人员负责的客户

    }


}
