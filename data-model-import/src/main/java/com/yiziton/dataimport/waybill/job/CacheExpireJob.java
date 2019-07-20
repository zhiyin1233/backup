package com.yiziton.dataimport.waybill.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.yiziton.commons.utils.OID;
import com.yiziton.dataimport.cache.dao.SysCacheMapper;
import com.yiziton.dataimport.commons.OrderProcessor;
import com.yiziton.dataimport.waybill.dao.ext.WaybillInfoExtMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

/**
 * @Description: redis过期延长线程
 * @Author: xiaoHong
 * @Date: 2018/12/20
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@JobHandler(value="dataCacheExpireJob")
@Component
@Slf4j
public class CacheExpireJob extends IJobHandler {

    @Autowired
    SysCacheMapper sysCacheMapper;
    @Autowired
    WaybillInfoExtMapper waybillInfoExtMapper;

    @Autowired
    OrderProcessor orderProcessor;
    @Override
    public ReturnT<String> execute(String param) {
        log.info("cacheExpire Start");
        Timestamp startTime = null;
        Timestamp endTime = null;
        if(!StringUtils.isEmpty(param)){
            String[] params = param.split(",");
            startTime = Timestamp.valueOf(params[0]);
            endTime = Timestamp.valueOf(params[1]);
        }else{
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.set(Calendar.HOUR_OF_DAY,0);
            startCalendar.set(Calendar.MINUTE,0);
            startCalendar.set(Calendar.SECOND,0);
            startCalendar.set(Calendar.MILLISECOND,0);
            startCalendar.add(Calendar.DAY_OF_YEAR,-40);
            startTime = new Timestamp(startCalendar.getTimeInMillis());
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.set(Calendar.HOUR_OF_DAY,0);
            endCalendar.set(Calendar.MINUTE,0);
            endCalendar.set(Calendar.SECOND,0);
            endCalendar.set(Calendar.MILLISECOND,0);
            endCalendar.add(Calendar.DAY_OF_YEAR,-26);
            endTime = new Timestamp(endCalendar.getTimeInMillis());
        }
        long count = expireTime(startTime,endTime);
        log.info("cacheExpire end,dealCount:"+count);
        return null;
    }

    public long expireTime(Timestamp startTime,Timestamp endTime){
        List<String> wayBillIds = waybillInfoExtMapper.selectNoDoneWayBill(startTime,endTime);
        long count = 0;
        for(String wayBillId : wayBillIds){
            try{
                count++;
                orderProcessor.expireOrder(wayBillId,50);
                sysCacheMapper.insert(OID.get().toString(),wayBillId,2);
            }catch (Exception e){
                log.error("cacheClear error,waybillId:"+wayBillId,e);
            }
        }
        return count;
    }

}
