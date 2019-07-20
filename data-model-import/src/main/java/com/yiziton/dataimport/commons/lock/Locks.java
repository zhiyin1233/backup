package com.yiziton.dataimport.commons.lock;

import com.yiziton.dataimport.commons.SpringUtils;

import java.util.concurrent.Callable;

/**
 * 分布式事务锁
 * @author lujijiang
 */
public class Locks {
    /**
     * 尝试锁
     * @param key 锁定键
     * @param callable 处理逻辑
     * @param <T> 返回处理结果
     * @return
     */
    public static <T> T tryLock(Object key, Callable<T> callable){
        RedisLock redisLock = (RedisLock)SpringUtils.getApplicationContext().getBean("redisLock");
        return redisLock.tryLock(key,callable);
    }
}
