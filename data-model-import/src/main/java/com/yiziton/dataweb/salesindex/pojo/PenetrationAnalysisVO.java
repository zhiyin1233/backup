package com.yiziton.dataweb.salesindex.pojo;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @program: bigdataplatform
 * @description: 渗透分析
 * @author: kdh
 * @create: 2019-07-18 11:45
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@Data
public class PenetrationAnalysisVO {

    private Timestamp openBillTimeBegin;//开单开始日期
    private Timestamp openBillTimeEnd;//开单结束日期
    public String departmentCode;//部门标号
    public String salesmanJobNum; //销售员工号
    public int days; //计算天数，按每月30天计算占比

    public PenetrationAnalysisDO all;
    public List<PenetrationAnalysisDO> customerGrades;
    public List<PenetrationAnalysisDO> settlementTypes;

}
