package com.yiziton.dataweb.salesindex.pojo;

import lombok.Data;

/**
 * @program: bigdataplatform
 * @description: 渗透分析
 * @author: kdh
 * @create: 2019-07-18 11:45
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@Data
public class PenetrationAnalysisDO {

    public double totalBillingIncome;//开单总收入
    public double totalLogisticsCost;//物理体量
    public String customerGrade; //客户等级
    public String settlementType; //结算类型
    public double penetrationRate;//渗透率

}
