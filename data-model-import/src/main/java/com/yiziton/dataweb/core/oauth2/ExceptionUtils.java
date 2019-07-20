/*
 * Copyright 2018 1ziton.com.
 **/
package com.yiziton.dataweb.core.oauth2;

import org.apache.commons.lang3.StringUtils;

/**
 * @author xiaoHong
 * @create 2018-06-08
 **/
public class ExceptionUtils {

    public static String gainCauseText(String cause, int len){
        if (StringUtils.isEmpty(cause)){
            return "";
        }
        if(cause.length()<len){
            return ".原因:"+cause;
        }else {
            return ".原因:"+cause.substring(0,len)+"..";
        }
    }

    public static String builtErrorText(String msg, String cause){
        String subCause = gainCauseText(cause,30);
        return msg + subCause;
    }


}