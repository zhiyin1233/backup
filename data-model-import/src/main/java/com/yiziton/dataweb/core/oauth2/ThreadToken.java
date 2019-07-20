package com.yiziton.dataweb.core.oauth2;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/9/18
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class ThreadToken {

    public static ThreadLocal<String> token = new ThreadLocal<>();
}
