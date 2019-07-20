package com.yiziton.dataimport.waybill.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2019/1/29
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@Data
public class SysExceptionWayBillVO {
    private List<String> wayBillNos;

    private Integer count;
}
