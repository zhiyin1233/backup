package com.yiziton.dataimport.commons;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2019/1/9
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
public class ErrorUtil {

    public static String parseError(Throwable e){
        String error = "error[%s];file[%s];method[%s];lineNumber[%s]";
        StackTraceElement[] elements = e.getStackTrace();
        StringBuilder sb = new StringBuilder();
        for(StackTraceElement element : elements){
            String className = element.getClassName();
            if(className.indexOf(".")>0){
                className = className.substring(className.lastIndexOf(".")+1);
            }
            sb.append(String.format(error,e.getClass().getSimpleName(),className,element.getMethodName(),element.getLineNumber())+";");
        }
        return sb.toString();
    }
}
