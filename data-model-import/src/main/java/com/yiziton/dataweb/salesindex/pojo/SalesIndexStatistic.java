package com.yiziton.dataweb.salesindex.pojo;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 第二期销售指标实体对象
 *
 * @author HuangHuai on 2019-07-11 15:54
 */
@Data
public class SalesIndexStatistic extends SalesIndexDimension {

    /**
     * 查询日期的开始和结束日期
     */
    public Date                                 start;
    public Date                                 end;
    /**
     * 维度信息
     */
    public Map<String, SalesmanDimension>       salesmanDimensions       = new HashMap<>(); //销售员维度
    public Map<String, DepartmentDimension>     departmentDimensions     = new HashMap<>(); //部门维度
    public Map<String, CustomerGradeDimension>  customerGradeDimensions  = new HashMap<>(); //客户等级维度
    public Map<String, CustomerDimension>       customerDimensions       = new HashMap<>();//商家维度
    public Map<String, ServiceTypeDimension>    serviceTypeDimensions    = new HashMap<>();//服务类型维度
    public Map<String, SettlementTypeDimension> settlementTypeDimensions = new HashMap<>();//结算方式及账期


}
