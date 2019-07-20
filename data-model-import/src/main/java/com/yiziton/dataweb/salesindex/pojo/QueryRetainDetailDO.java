package com.yiziton.dataweb.salesindex.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author xujinping
 * @date 2019/7/17 19:35
 */
@Data
public class QueryRetainDetailDO {
    public String customerName;
    public String secondName;
    public String settlementType;
    public String clientType;
    public String salesman;
    public String openBillMonth;
    public List customerCodeList;
}
