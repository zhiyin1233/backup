package com.yiziton.dataweb.waybill.vo.header;

import com.yiziton.dataweb.waybill.utils.excel.Header;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/11/28
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class WayBillInfoHeader extends InfoHeader{

    public WayBillInfoHeader(){
        headerMap.put("waybill_id",new Header("waybill_id","运单号","string",12));
        headerMap.put("operation",new Header("operation","操作","string",12));
    }

}
