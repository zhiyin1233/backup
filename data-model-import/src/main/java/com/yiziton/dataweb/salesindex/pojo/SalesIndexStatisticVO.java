package com.yiziton.dataweb.salesindex.pojo;

import lombok.Data;

import java.util.List;

/**
 * 父类的是当前查询时间以及部门的统计，不代表全年
 *
 * @author HuangHuai on 2019-07-11 15:54
 */
@Data
public class SalesIndexStatisticVO {


    public SalesIndexDimension.IncomeDimension income_dimension = new SalesIndexDimension.IncomeDimension();//收入分析


    public List<SalesIndexDimension.DepartmentDimension> department_dimension;//部门维度
    public SalesIndexDimension.NewCustomerDimension      new_customer_dimension;//新客户


    public List<SalesIndexDimension.CustomerDimension> customer_dimension; //客户维度
    public List<SalesIndexDimension.CustomerDimension> new_customer_grade_dimension; //新客户等级，就是合作了n个月划分


    public List<SalesIndexDimension.ServiceTypeDimension>   service_type_dimension;//服务类型维度
    public List<SalesIndexDimension.CustomerGradeDimension> customer_grade_dimension;//客户等级维度

    public List<SalesIndexDimension.SettlementTypeDimension> settlement_type_dimensions;//结算方式及账期


}
