package com.yiziton.dataimport.waybill.action;

import com.yiziton.commons.utils.ExecutorUtil;
import com.yiziton.commons.utils.OID;
import com.yiziton.commons.vo.MessageRedisCacheInfo;
import com.yiziton.commons.vo.enums.DataType;
import com.yiziton.commons.vo.waybill.SysExceptionVO;
import com.yiziton.dataimport.commons.OrderProcessor;
import com.yiziton.dataimport.exception.bean.SysExceptionInfo;
import com.yiziton.dataimport.exception.dao.SysExceptionMapper;
import com.yiziton.dataimport.waybill.job.CacheExpireJob;
import com.yiziton.dataimport.waybill.vo.HandleTimeVO;
import com.yiziton.dataimport.waybill.vo.SysExceptionWayBillVO;
import com.yiziton.dataweb.core.annotation.Action;
import com.yiziton.dataweb.waybill.bean.WayBillInfo;
import com.yiziton.dataweb.waybill.utils.GridRequest;
import com.yiziton.dataweb.waybill.vo.ImportConditionVO;
import com.yiziton.dataweb.waybill.vo.WayBillConditionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/12/5
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Action("order")
@Slf4j
public class OrderAction {

    @Autowired
    OrderProcessor orderProcessor;

    @Autowired
    SysExceptionMapper sysExceptionMapper;

    @Autowired
    CacheExpireJob cacheExpireJob;

    @Autowired
    HistoryDataImportAction historyDataImportAction;

    /**
     * 获取序号
     * @param wayBillNo
     * @return
     */
    public Long getIncrNo(String wayBillNo){
        String[] wayBillNos = null;
        Long serialNo = null;
        if(wayBillNo.contains(":")){
            wayBillNos = wayBillNo.split(":");
            serialNo = orderProcessor.getIncrNo(wayBillNos[0]);
        }else{
            serialNo = orderProcessor.getIncrNo(wayBillNo);
        }
        log.info("serialNo:"+serialNo+",getIncrNo:"+wayBillNo);
        return serialNo;
    }

    /**
     * 减少编号
     * @param wayBillNo
     * @return
     */
    public Long decrNo(String wayBillNo){
        String[] wayBillNos = null;
        Long serialNo = null;
        if(wayBillNo.contains(":")){
            wayBillNos = wayBillNo.split(":");
            orderProcessor.decrNo(wayBillNos[0]);
        }else{
            orderProcessor.decrNo(wayBillNo);
        }
        log.info("serialNo:"+serialNo+",decrNo:"+wayBillNo);
        return serialNo;
    }
    /**
     * 插入异常数据
     * @param sysExceptionVO
     */
    public void insertSysException(SysExceptionVO sysExceptionVO){
        String exceptionInfo = sysExceptionVO.getExceptionInfo();
        SysExceptionInfo sysExceptionInfo = new SysExceptionInfo();
        sysExceptionInfo.setId(OID.get().toString());
        sysExceptionInfo.setNo(sysExceptionVO.getWaybillId());
        sysExceptionInfo.setNoType("producer");
        sysExceptionInfo.setSource(sysExceptionVO.getSource());
        sysExceptionInfo.setDataType(DataType.REAL_TIME_DATA.getCode());
        sysExceptionInfo.setSentTime(new Timestamp(System.currentTimeMillis()));
        sysExceptionInfo.setExceptionBody(sysExceptionVO.getOperation());
        sysExceptionInfo.setExceptionInfo(exceptionInfo.length()<3000? exceptionInfo : exceptionInfo.substring(0, 3000));
        sysExceptionMapper.insert(sysExceptionInfo);
    }

    /**
     * 获取所有生产者获取序号错误（为0）的运单,包含处理时间
     * @param handleTimeVO
     * @return
     */
    public SysExceptionWayBillVO getErrorIncrNoWaybillNoByHandleTime(HandleTimeVO handleTimeVO) throws Exception{
        List<String> noKeys = orderProcessor.getOrderNoKeys(handleTimeVO.getStartTime(),handleTimeVO.getEndTime());
        String[] noKeyArray = noKeys.toArray(new String[noKeys.size()]);
        List<String> exceptionNos = new ArrayList<>();
        List<List<String>> groupKeys = new ArrayList<>();
        List<String> noKeyList = new ArrayList<>();
        boolean addflag = true;
        for(int i=0; i<noKeyArray.length; i++){
            noKeyList.add(noKeyArray[i]);
            addflag = true;
            if((i+1)%1000 == 0){
                groupKeys.add(noKeyList);
                noKeyList = new ArrayList<>();
                addflag = false;
            }
        }
        if(addflag){
            groupKeys.add(noKeyList);
        }

        groupKeys.stream().parallel().forEach((keyList)->{
            for(String noKey : keyList){
                String wayBillNo = noKey.substring((OrderProcessor.ORDER_QUEUE_ZSET+":yyyy-MM-dd_").length(),noKey.length());
                if(!orderProcessor.checkMessageSerializeNo(wayBillNo)){
                    exceptionNos.add(wayBillNo);
                    log.info("exception handle:"+wayBillNo);
                }
            }
        });

        SysExceptionWayBillVO sysExceptionWayBillVO = new SysExceptionWayBillVO();
        sysExceptionWayBillVO.setWayBillNos(exceptionNos);
        return sysExceptionWayBillVO;
    }



    /**
     * 获取所有生产者获取序号错误（为0）的运单
     * @return
     */
    public SysExceptionWayBillVO getAllErrorIncrNoWaybillNo(String param){
        Set<String> noKeys = orderProcessor.getAllOrderNoKeys();
        String[] noKeyArray = noKeys.toArray(new String[noKeys.size()]);
        List<String> exceptionNos = new ArrayList<>();
        List<List<String>> groupKeys = new ArrayList<>();
        List<String> noKeyList = new ArrayList<>();
        boolean addflag = true;
        for(int i=0; i<noKeyArray.length; i++){
            noKeyList.add(noKeyArray[i]);
            addflag = true;
            if((i+1)%600 == 0){
                groupKeys.add(noKeyList);
                noKeyList = new ArrayList<>();
                addflag = false;
            }
        }
        if(addflag){
            groupKeys.add(noKeyList);
        }

        groupKeys.stream().parallel().forEach((keyList)->{
            for(String noKey : keyList){
                String wayBillNo = noKey.substring(OrderProcessor.ORDER_NO_FIX.length(),noKey.length());
                if(!orderProcessor.checkMessageSerializeNo(wayBillNo)){
                    exceptionNos.add(wayBillNo);
                    log.info("exception handle:"+wayBillNo);
                }
            }
        });

        SysExceptionWayBillVO sysExceptionWayBillVO = new SysExceptionWayBillVO();
        sysExceptionWayBillVO.setWayBillNos(exceptionNos);
        return sysExceptionWayBillVO;
    }

    /**
     * 获取所有异常数据
     * @param
     */
    public SysExceptionWayBillVO getAllExceptionWaybillNo(String param){
        Set<String> noKeys = orderProcessor.getAllOrderNoKeys();
        String[] noKeyArray = noKeys.toArray(new String[noKeys.size()]);
        List<String> exceptionNos = new ArrayList<>();
        List<List<String>> groupKeys = new ArrayList<>();
        List<String> noKeyList = new ArrayList<>();
        boolean addflag = true;
        for(int i=0;i<noKeyArray.length; i++){
            noKeyList.add(noKeyArray[i]);
            addflag = true;
            if((i+1)%500 == 0){
                groupKeys.add(noKeyList);
                noKeyList = new ArrayList<>();
                addflag = false;
            }
        }
        if(addflag){
            groupKeys.add(noKeyList);
        }

        groupKeys.stream().parallel().forEach((keyList)->{
            for(String noKey : keyList){
                String wayBillNo = noKey.substring(OrderProcessor.ORDER_NO_FIX.length(),noKey.length());
                if(!orderProcessor.checkOrderDealSame(wayBillNo)){
                    exceptionNos.add(wayBillNo);
                    log.info("exception handle:"+wayBillNo);
                }
            }
        });

        SysExceptionWayBillVO sysExceptionWayBillVO = new SysExceptionWayBillVO();
        sysExceptionWayBillVO.setWayBillNos(exceptionNos);
        return sysExceptionWayBillVO;
    }

    /**
     * 获取所有异常数据
     * @param
     */
    public String dealAllExceptionWaybillNoByHandleTime(HandleTimeVO handleTimeVO) throws Exception{
        List<String> noKeys = orderProcessor.getOrderNoKeys(handleTimeVO.getStartTime(),handleTimeVO.getEndTime());
        String[] noKeyArray = noKeys.toArray(new String[noKeys.size()]);
        List<String> exceptionNos = new ArrayList<>();
        List<List<String>> groupKeys = new ArrayList<>();
        List<String> noKeyList = new ArrayList<>();
        boolean addflag = true;
        for(int i=0; i<noKeyArray.length; i++){
            noKeyList.add(noKeyArray[i]);
            addflag = true;
            if((i+1)%1000 == 0){
                groupKeys.add(noKeyList);
                noKeyList = new ArrayList<>();
                addflag = false;
            }
        }
        if(addflag){
            groupKeys.add(noKeyList);
        }

        groupKeys.stream().parallel().forEach((keyList)->{
            for(String noKey : keyList){
                String wayBillNo = noKey.substring((OrderProcessor.ORDER_QUEUE_ZSET+":yyyy-MM-dd_").length(),noKey.length());
                if(!orderProcessor.checkOrderDealSame(wayBillNo)){
                    exceptionNos.add(wayBillNo);
                    log.info("exception handle:"+wayBillNo);
                }
            }
        });

        ImportConditionVO importConditionVO = new ImportConditionVO();
        importConditionVO.setWaybillIds(exceptionNos);
        String result = historyDataImportAction.dataImport(importConditionVO);

        return result;
    }



    /**
     * redis缓存续期
     * @param param
     */
    public void expireCacheExpire(String param){
        cacheExpireJob.execute(param);
    }

}
