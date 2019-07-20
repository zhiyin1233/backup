package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * @program: big-data-platform
 * @description: 运营指标，有关系的属性不换行
 * @author: kdh
 * @create: 2019-05-24 18:26
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@Data
public class IndicatorVO {

    public int serialNumber;//序号

    public String primaryIndicator;//一级指标

    public String assessmentDepartment;//考核部门

    public String assessmentMethod;//考核方式

    public String target;//目标值

    public String actualCompletion;//实际完成值（%）

    public String circleCompletion;//环比完成值（%）

}
