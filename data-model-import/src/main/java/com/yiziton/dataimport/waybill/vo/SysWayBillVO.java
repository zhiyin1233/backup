package com.yiziton.dataimport.waybill.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description:  运单号列表入参
 * @Author: xiaoHong
 * @Date: 2019/2/18
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@Data
public class SysWayBillVO {
    private List<String> wayBillNos;
}
