package com.yiziton.dataweb.salesindex;

import org.springframework.beans.BeanUtils;

/**
 * @author HuangHuai on 2019-07-18 14:46
 */

public class BeanUtil {


    public static <T> T copy(Object source, T dest) {
        BeanUtils.copyProperties(source, dest);
        return dest;
    }

    public static <T> T copy(Object source, Class<T> destClazz) {
        try {
            T target = destClazz.newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("复制bean出错:" + e.getMessage(), e);
        }
    }
}
