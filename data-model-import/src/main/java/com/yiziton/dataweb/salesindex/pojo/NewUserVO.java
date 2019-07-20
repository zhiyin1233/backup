package com.yiziton.dataweb.salesindex.pojo;

import lombok.Data;

/**
 * <p>Description: 新用户VO</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: YZT</p>
 *
 * @author TY
 * @version 1.0
 * @date 2019/07/18 21:10
 */
@Data
public class NewUserVO {
    /**
     * 月份
     */
    public String openBillMonth;
    /**
     * 新客户百分比
     */
    public String percentageOfNewCustomers;
    /**
     * 其他客户百分比
     */
    public String percentageOfOtherCustomers;
    /**
     * 新客户数
     */
    public String numberOfNewCustomers;
    /**
     * 其他客户数
     */
    public String numberOfOtherCustomers;
    /**
     * 新客户开单收入
     */
    public String newCustomerBillingIncome;
    /**
     * 其他客户开单收入
     */
    public String revenueFromOtherCustomersBilling;
    /**
     * 新客户开单票数
     */
    public String numberOfInvoicesIssuedByNewCustomers;
    /**
     * 其他客户开单票数
     */
    public String numberOfInvoicesIssuedByOtherCustomers;
    /**
     * 新客户CODE
     */
    public String newUserCode;
}
