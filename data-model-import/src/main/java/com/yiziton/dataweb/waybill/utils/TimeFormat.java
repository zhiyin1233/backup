package com.yiziton.dataweb.waybill.utils;

import java.sql.Timestamp;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/12/6
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class TimeFormat {

    public static String timeStamp2String(Timestamp timestamp){
        return timeStamp2String(timestamp,0);
    }

    public static String timeStamp2String(Timestamp timestamp,int accuracy){
        String time = String.valueOf(timestamp);
        return time.substring(0,time.indexOf("."));
    }
}
