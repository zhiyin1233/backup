package com.yiziton.dataimport.commons;

import com.yiziton.dataimport.commons.lock.Locks;
import com.yiziton.commons.vo.MessageRedisCacheInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class OrderProcessor {

    @Autowired
    private RedisTemplate redisTemplate;

    static final String KEY_TRIFIX = "KEY_TRIFIX_";

    //每个单号已出序号
    public static final String ORDER_NO_FIX = KEY_TRIFIX + "ORDER_NO_FIX_";
    //单号已处理的最大序号
    static final String DEAL_ORDER_NO_FIX = KEY_TRIFIX + "DEAL_ORDER_NO_FIX_";
    //运单号下所有消息集合
    public static final String ORDER_QUEUE_ZSET = KEY_TRIFIX + "ORDER_QUEUE_ZSET_";
    //所有尚有消息未处理的运单号
    static final String UNHANDLE_ORDER_NO_LIST = KEY_TRIFIX + "UNHANDLE_ORDER_NO_LIST";


    /**
     * 判断消息是否可以处理
     * @param orderNo
     * @param serializeNo
     * @return
     */
    @Deprecated
    public boolean isDealMsg(String orderNo, Long serializeNo) {
        long nowSerializeNo = getDealOrderFixNo(orderNo);//获取当前处理了的序号
        return (nowSerializeNo + 1) == serializeNo;
    }


    public Set<String> getAllOrderNoKeys(){
       return redisTemplate.keys(ORDER_NO_FIX+"*");
    }

    public List<String> getOrderNoKeys(String startTime, String endTime) throws Exception{
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.setTime(sdf.parse(startTime));
        List<String> keys = new ArrayList<>();
        while (calendar.getTime().before(sdf.parse(endTime)) || calendar.getTime().equals(sdf.parse(endTime))){
            Set<String> keySet = redisTemplate.keys(ORDER_QUEUE_ZSET+":"+sdf.format(calendar.getTime())+"*");
            keys.addAll(keySet);
            calendar.add(Calendar.DATE,1);
        }
        return keys;
    }

    /**
     * 校验处理需要和已处理序号是否相等
     * @param no
     * @return
     */
    public Boolean checkOrderDealSame(String no){
        Integer getNo = (Integer) redisTemplate.opsForValue().get(ORDER_NO_FIX+no);
        Integer dealNo = (Integer) redisTemplate.opsForValue().get(DEAL_ORDER_NO_FIX+no);
        if(getNo != dealNo){
            return false;
        }
        return true;
    }

    /**
     * 校验消息序号是否正确
     * @param no
     * @return
     */
    public Boolean checkMessageSerializeNo(String no){
        Set<Object> messages = redisTemplate.opsForZSet().range(ORDER_QUEUE_ZSET+no, 0, 0);
        if(messages != null){
            Iterator iterator = messages.iterator();
            if(iterator.hasNext()){
                Object object = iterator.next();
                if(object instanceof MessageRedisCacheInfo){
                    MessageRedisCacheInfo messageRedisCacheInfo = (MessageRedisCacheInfo)Arrays.asList(messages.toArray()).get(0);
                    if(messageRedisCacheInfo.getSerializeNo() == 0L){
                        return false;
                    }
                }else{
                    String result = (String)object;
                    if(result.contains("\"serializeNo\":0")){
                        return false;
                    }
                }
            }

        }
        return true;
    }


    /**
     * 获取发送运单对应编号
     * @param no
     * @return
     */
    public long getIncrNo(String no) {
        String key = ORDER_NO_FIX + no;
        Long ret = redisTemplate.opsForValue().increment(key, 1L);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
        return ret;
    }

    /**
     * 减少编号
     * @param no
     * @return
     */
    public void decrNo(String no) {
        String key = ORDER_NO_FIX + no;
        Locks.tryLock(key,()->{
            Long source = ((Integer)redisTemplate.opsForValue().get(key)).longValue();
            redisTemplate.opsForValue().set(key,source-1L);
            redisTemplate.expire(key, 30, TimeUnit.DAYS);
            return null;
        });

    }


    /**
     * 延长未处理单号的有效时间
     * @param orderNo
     */
    public void expireOrder(String orderNo,int days) {
        String orderKey = ORDER_NO_FIX + orderNo;
        String dealKey = DEAL_ORDER_NO_FIX + orderNo;
        Integer orderValue = (Integer)redisTemplate.opsForValue().get(orderKey);
        Integer dealValue = (Integer)redisTemplate.opsForValue().get(dealKey);
        if(orderValue != null && dealValue != null){
            redisTemplate.expire(orderKey,days,TimeUnit.DAYS);
            redisTemplate.expire(dealKey,days,TimeUnit.DAYS);
            String zsetKey = ORDER_QUEUE_ZSET + orderNo;
            redisTemplate.expire(zsetKey,days,TimeUnit.DAYS);
            String unhandleKey = UNHANDLE_ORDER_NO_LIST + orderNo;
            redisTemplate.expire(unhandleKey,days,TimeUnit.DAYS);
        }
    }
    /**
     * 处理已结束的单号
     * @param orderNo
     */
    public void finishOrder(String orderNo) {
        String orderKey = ORDER_NO_FIX + orderNo;
        String dealKey = DEAL_ORDER_NO_FIX + orderNo;
        Integer orderValue = (Integer)redisTemplate.opsForValue().get(orderKey);
        Integer dealValue = (Integer)redisTemplate.opsForValue().get(dealKey);
        if(orderValue != null && dealValue != null){
            if(orderValue.intValue() == dealValue.intValue()){
                redisTemplate.opsForValue().getOperations().delete(orderKey);
                redisTemplate.opsForValue().getOperations().delete(dealKey);
                String zsetKey = ORDER_QUEUE_ZSET + orderNo;
                redisTemplate.opsForZSet().getOperations().delete(zsetKey);
                String unhandleKey = UNHANDLE_ORDER_NO_LIST + orderNo;
                redisTemplate.opsForValue().getOperations().delete(unhandleKey);
            }
        }
    }

    /**
     * 删除单号缓存信息
     * @param orderNo
     */
    public void deleteOrderCache(String orderNo){
        String orderKey = ORDER_NO_FIX + orderNo;
        String dealKey = DEAL_ORDER_NO_FIX + orderNo;
        String zsetKey = ORDER_QUEUE_ZSET + orderNo;
        String unhandleKey = UNHANDLE_ORDER_NO_LIST + orderNo;

        redisTemplate.opsForValue().getOperations().delete(orderKey);
        redisTemplate.opsForValue().getOperations().delete(dealKey);
        redisTemplate.opsForZSet().getOperations().delete(zsetKey);
        redisTemplate.opsForValue().getOperations().delete(unhandleKey);
    }

    /**
     * 消息处理完成递增已处理序号
     * @param orderNo
     */
    @Deprecated
    public void incrOrderFixNo(String orderNo) {
        String key = DEAL_ORDER_NO_FIX + orderNo;
        redisTemplate.opsForValue().increment(key, 1L);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    /**
     * 修正已处理序号
     * @param orderNo
     * @param serializeNo
     */
    public void dealOrderFixNo(String orderNo, Long serializeNo) {
        String key = DEAL_ORDER_NO_FIX + orderNo;
        redisTemplate.opsForValue().set(key, serializeNo);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }


    /**
     * 获取当前已处理的最大单号
     * @param orderNo
     * @return
     */
    public long getDealOrderFixNo(String orderNo) {
        String key = DEAL_ORDER_NO_FIX + orderNo;
        Long ret = getIncrValue(key);
        if (ret != null) {
            return ret.longValue();
        }
        redisTemplate.opsForValue().set(key, 0L);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
        return 0L;
    }

    /**
     * 消息处理放入队列
     * 缓存全部信息以供做消息回放
     * @param orderNo
     * @param msg
     * @return
     */
    public boolean putUnhandleMsg(String orderNo, MessageRedisCacheInfo msg) {
        String key = ORDER_QUEUE_ZSET + orderNo;
        boolean bool = redisTemplate.opsForZSet().add(key, msg, msg.getSerializeNo());
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        redisTemplate.opsForValue().set(ORDER_QUEUE_ZSET+":"+sdf.format(new Date())+"_"+orderNo,orderNo);
        redisTemplate.expire(ORDER_QUEUE_ZSET+":"+sdf.format(new Date())+"_"+orderNo,30,TimeUnit.DAYS);
        return bool;
    }


    /**
     * 获取运单所有处理的消息集合
     *
     */
    public List<MessageRedisCacheInfo> getAllShouldHandleMsg(String orderNo, MessageRedisCacheInfo currentMsg){
        List<MessageRedisCacheInfo> result = new ArrayList<>();
        Long shouldSerializeNo = getDealOrderFixNo(orderNo) + 1L;
        Set<MessageRedisCacheInfo>  unhandle = getOrderNoMsgsByStart(orderNo, shouldSerializeNo - 1);
        if(unhandle != null && unhandle.size() > 0) {
            for (MessageRedisCacheInfo info : unhandle) {
                    result.add(info);
                    shouldSerializeNo ++;
            }
            putUnhandleMsg(orderNo, currentMsg);
            result.add(currentMsg);
            dealOrderFixNo(orderNo, shouldSerializeNo);
        }else {
            result.add(currentMsg);
        }
        return result;
    }


    /**
     * 获取运单应该处理的消息集合
     * @param orderNo
     * @return
     */
    public List<MessageRedisCacheInfo> getShouldHandleMsg(String orderNo, MessageRedisCacheInfo currentMsg) {
        List<MessageRedisCacheInfo> cacheInfos = null;
        log.info("getShouldHandleMsg 方法进入："+currentMsg.getMessage().getWaybillId()+",序号:"+orderNo);
        cacheInfos = Locks.tryLock(orderNo,()->{
            putUnhandleMsg(orderNo, currentMsg);
            log.info("putUnhandleMsg 添加到redis缓存："+currentMsg.getMessage().getWaybillId()+",序号:"+orderNo);
            List<MessageRedisCacheInfo> result = new ArrayList<>();
            Long shouldSerializeNo = getDealOrderFixNo(orderNo) + 1L;
            Set<MessageRedisCacheInfo>  unhandle = getOrderNoMsgsByStart(orderNo, shouldSerializeNo - 1);
            if(unhandle != null) {
                boolean dealFlag = true;
                for (MessageRedisCacheInfo info : unhandle) {
                    if(info.getSerializeNo() == shouldSerializeNo) {
                        result.add(info);
                        shouldSerializeNo ++;
                    }else {
                        dealFlag = false;
                    }
                }
                if(!dealFlag) {
                    addUnhandleMsgOrderNo(orderNo);
                }else{
                    delUnhandleMsgOrderNo(orderNo);
                }
                dealOrderFixNo(orderNo, shouldSerializeNo - 1L);
            }
            return result;
        });
        return cacheInfos;
    }

    /**
     * 存储未处理的消息并修改序号，用于出现异常序号补偿
     * @param orderNo
     * @param currentMsg
     */
    public void putUnhandleMsgAndDealOrderFixNo(String orderNo, MessageRedisCacheInfo currentMsg){
        if(getDealOrderFixNo(orderNo) + 1L == currentMsg.getSerializeNo()){
            putUnhandleMsg(orderNo, currentMsg);
            dealOrderFixNo(orderNo, currentMsg.getSerializeNo());
        }
    }

    /**
     * 获取订单号所有消息
     * 运单号下面所有消息会保存30天
     * @param orderNo
     * @return
     */
    public Set<MessageRedisCacheInfo> getOrderNoMsgs(String orderNo) {
        String key = ORDER_QUEUE_ZSET + orderNo;
        return redisTemplate.opsForZSet().range(key, 0, -1);
    }

    /**
     * 根据起始点获取订单号所有消息
     * 如果size本身小于start则获取全部
     * @param orderNo
     * @return
     */
    public Set<MessageRedisCacheInfo> getOrderNoMsgsByStart(String orderNo, long start) {
        String key = ORDER_QUEUE_ZSET + orderNo;
        long size = redisTemplate.opsForZSet().size(key);
        if(size >= start)
            return redisTemplate.opsForZSet().range(key, start, -1);
        else
            return redisTemplate.opsForZSet().range(key, 0, -1);
    }

    /**
     * 收集尚有消息未处理的运单号
     * @param orderNo
     * @return
     */
    public boolean addUnhandleMsgOrderNo(String orderNo) {
       Long l = redisTemplate.opsForSet().add(UNHANDLE_ORDER_NO_LIST, orderNo);
       if(l > 0) {
           return true;
       }
       return false;
    }

    /**
     * 删除已正常处理完消息的运单号
     * @param orderNo
     * @return
     */
    public boolean delUnhandleMsgOrderNo(String orderNo) {
        Long l = redisTemplate.opsForSet().remove(UNHANDLE_ORDER_NO_LIST, orderNo);
        if(l > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取所有尚未妥善处理的运单号
     * @return
     */
    public Set<String> getUnhandleMsgOrderNo () {//UNHANDLE_ORDER_NO_LIST
         return redisTemplate.opsForSet().members(UNHANDLE_ORDER_NO_LIST);
    }


    private Long getIncrValue(final String key) {

        return (Long) redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer=redisTemplate.getStringSerializer();
                byte[] rowkey=serializer.serialize(key);
                byte[] rowval=connection.get(rowkey);
                try {
                    String val=serializer.deserialize(rowval);
                    return Long.parseLong(val);
                } catch (Exception e) {
                    return 0L;
                }
            }
        });
    }
}
