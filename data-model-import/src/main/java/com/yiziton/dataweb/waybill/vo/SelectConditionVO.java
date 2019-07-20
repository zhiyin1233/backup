package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * 下拉查询公共参数
 */
@Data
public class SelectConditionVO {

    //表
    private String table;
    //列
    private String column;
    //值
    private String value;
    //初始化偏移量
    private int offset = 0;
    //初始化查询数量
    private int size = 0;

}
