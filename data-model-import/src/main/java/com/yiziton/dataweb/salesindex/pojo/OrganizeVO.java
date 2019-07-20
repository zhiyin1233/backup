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
public class OrganizeVO {
    /**
     * ID
     */
    public String id;
    /**
     * CODE
     */
    public String code;
    /**
     * 父CODE
     */
    public String parentCode;
    /**
     * 组织名
     */
    public String name;
    /**
     * 排序号
     */
    public String sort;
    /**
     * 层级
     */
    public String level;
}
