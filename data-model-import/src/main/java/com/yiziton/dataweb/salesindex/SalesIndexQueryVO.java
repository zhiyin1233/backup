package com.yiziton.dataweb.salesindex;

import com.yiziton.dataweb.salesindex.pojo.Page;
import com.yiziton.dataweb.salesindex.pojo.SalesIndexDO;
import lombok.Data;

import java.util.Date;

/**
 * @author HuangHuai on 2019-07-18 15:27
 */
@Data
public class SalesIndexQueryVO extends SalesIndexDO implements Page {
    public int    inNewCustomer;

    public Date sign_time_start;
    public Date sign_time_end;
    public Date openBillTimeStart;
    public Date openBillTimeEnd;

    public int page     = 1;          //
    public int pageSize = 30;  //
    public int totalPage;  //
}
