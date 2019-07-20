package com.yiziton.dataweb.salesindex.pojo;

import java.io.Serializable;

/**
 * 分页接口
 *
 * @author HuangHuai on 2019-07-18 15:19
 */

public interface Page extends Serializable {
    public int getPage();

    public void setPage(int page);

    public int getPageSize();

    public void setPageSize(int pageSize);

    public int getTotalPage();

    public void setTotalPage(int totalPage);
}
