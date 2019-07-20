package com.yiziton.dataweb.salesindex.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 销售指标基础维度
 *
 * @author HuangHuai on 2019-07-12 16:23
 */
@Data
public class SalesIndexTotalDimension implements Serializable {
    public double total_billing_income;         //开单总收入
    public long   total_num_of_waybills;        //运单总数
}
