package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

import java.util.List;

/**
 * Created by ouweijian on 2018-12-08 14:00:56.
 */
@Data
public class SalesmanVO {

    /**
     * 业务员
     */
    private String salesman;

    private List<String> salesmanList;
}
