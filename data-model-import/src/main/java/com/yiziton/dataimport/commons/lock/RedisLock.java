package com.yiziton.dataimport.commons.lock;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis实现分布式锁，确保所有应用节点的Redis都连接到同一个Redis库
 *
 * @author lujijiang
 */
@Component
public class RedisLock {

    /**
     * Redis模板
     */
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 加锁尝试次数（默认一次，快速失败）
     */
    private int maxAttempts = 10;
    /**
     * 加锁尝试间隔时间（默认1毫秒）
     */
    private long attemptIntervalMilliseconds = 60l;
    /**
     * 锁定超时时间（秒）
     */
    private long lockTimeoutSeconds = 60l;


    public void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public void setAttemptIntervalMilliseconds(long attemptIntervalMilliseconds) {
        this.attemptIntervalMilliseconds = attemptIntervalMilliseconds;
    }

    public void setLockTimeoutSeconds(long lockTimeoutSeconds) {
        this.lockTimeoutSeconds = lockTimeoutSeconds;
    }

    /**
     * 尝试上锁
     *
     * @param obj      锁定对象
     * @param callable 回调方法
     */
    public <V> V tryLock(Object obj, Callable<V> callable) {
        RedisSerializer redisSerializer = redisTemplate.getStringSerializer();
        byte[] sign = redisSerializer.serialize(obj);
        String key = "_lock_data." + Base64Utils.encodeToString(sign);
        String id = UUID.randomUUID().toString();
        int maxAttempts = this.maxAttempts;
        while (maxAttempts-- > 0) {
            try {
                if (this.redisTemplate.opsForValue().setIfAbsent(key, id)) {
                    this.redisTemplate.expire(key,this.lockTimeoutSeconds, TimeUnit.SECONDS);
                    return callable.call();
                } else {
                    try {
                        TimeUnit.MILLISECONDS.sleep(this.attemptIntervalMilliseconds);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (Exception e) {
                throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
            } finally {
                DefaultRedisScript script = new DefaultRedisScript<Object>();
                script.setResultType(Long.class);
                script.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
                this.redisTemplate.execute(script, Arrays.asList(key), id);
            }
        }
        throw new IllegalStateException("Failed to get distributed lock for:\r\n" + new String(sign, Charset.defaultCharset()));
    }
}
