package com.yiziton.dataweb.core.context;


import java.util.HashMap;
import java.util.Map;


/**
 * @Description: 全局上下文
 * @Author: xiaoHong
 * @Date: 2018/9/4
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class Context {
    static ThreadLocal<Map> currentUser = new ThreadLocal<Map>();

    public static String USER = "user";

    public static void put(String key, Object value) {
        Map map = currentUser.get();
        if (map == null) {

            Map<String,Callback> callbackMap= new HashMap<>();
            callbackMap.put("orgUser", new OrgUserCallback());
            callbackMap.put("organize", new OrgCallback());
            callbackMap.put("outlet", new OrgCallback());
            callbackMap.put("organizeList", new OrgListCallback());
            callbackMap.put("organizeCodes", new OrgListCallback());
            map = new LazyMap(callbackMap);
            map.put(key, value);
            currentUser.set(map);
        } else {
            map.put(key, value);
        }
    }

    public static void clear() {
        currentUser.remove();
    }

    public static Map getContext() {
        return currentUser.get();
    }

}