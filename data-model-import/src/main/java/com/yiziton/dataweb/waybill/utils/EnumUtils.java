package com.yiziton.dataweb.waybill.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Description: 枚举反射工具类</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: YZT</p>
 *
 * @author TY
 * @version 1.0
 * @date 2019/07/18 10:28
 */
public class EnumUtils {
    /**
     * 根据枚举的字符串获取枚举的值
     *
     * @param className 包名+类名
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> getAllEnum(String className) throws Exception {
        // 得到枚举类对象
        Class<Enum> clazz = (Class<Enum>) Class.forName(className);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        //获取所有枚举实例
        Enum[] enumConstants = clazz.getEnumConstants();
        //根据方法名获取方法
        Method getCode = clazz.getMethod("getCode");
        Method getMessage = clazz.getMethod("getMessage");
        Map<String, Object> map = null;
        for (Enum enum1 : enumConstants) {
            map = new HashMap<String, Object>();
            //执行枚举方法获得枚举实例对应的值
            map.put("code", getCode.invoke(enum1));
            map.put("name", enum1);
            map.put("message", getMessage.invoke(enum1));
            list.add(map);
        }
        return list;
    }
}
