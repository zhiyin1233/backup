package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description: 收货人
 * @Author: kdh
 * @Date: 2018/12/8
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Data
public class ReceviceVO {

    private String recevice;

    private List<String> ReceviceList;
}
