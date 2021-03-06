package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description: 安装师傅所属节点
 * @Author: kdh
 * @Date: 2018/12/8
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Data
public class MasterNodeVO {

    private String masterNode;

    private List<String> masterNodeList;
}
