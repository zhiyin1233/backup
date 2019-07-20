package com.yiziton.dataimport.waybill.action;

import com.yiziton.commons.serialize.BeanJsonDeserializer;
import com.yiziton.commons.utils.OID;
import com.yiziton.commons.vo.MessageRedisCacheInfo;
import com.yiziton.commons.vo.enums.DataType;
import com.yiziton.commons.vo.enums.OperationProcessMethod;
import com.yiziton.commons.vo.enums.WaybillStatus;
import com.yiziton.commons.vo.waybill.Message;
import com.yiziton.commons.vo.waybill.OldDataMessage;
import com.yiziton.commons.vo.waybill.SysExceptionDealVO;
import com.yiziton.commons.vo.waybill.SysExceptionVO;
import com.yiziton.dataimport.commons.OrderProcessor;
import com.yiziton.dataimport.commons.SpringUtils;
import com.yiziton.dataimport.exception.bean.SysExceptionInfo;
import com.yiziton.dataimport.exception.dao.SysExceptionMapper;
import com.yiziton.dataimport.waybill.dao.ext.WaybillInfoExtMapper;
import com.yiziton.dataimport.waybill.listener.WaybillMessageListener;
import com.yiziton.dataimport.waybill.listener.service.WaybillMessageListenerService;
import com.yiziton.dataimport.waybill.service.BillingMessageService;
import com.yiziton.dataimport.waybill.vo.SysWayBillVO;
import com.yiziton.dataweb.core.annotation.Action;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/12/11
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Action("sysExceptDeal")
@Slf4j
public class SysExceptionDealAction {


    private String oldMessageService = "oldMessageService";


    @Autowired
    SysExceptionMapper sysExceptionMapper;

    @Autowired
    WaybillMessageListenerService waybillMessageListenerService;

    @Autowired
    BillingMessageService billingMessageService;

    @Autowired
    WaybillInfoExtMapper waybillInfoExtMapper;

    @Autowired
    OrderProcessor orderProcessor;

    /**
     * 同步实时数据
     * @param sysExceptionDealVO
     * @return
     */
    public boolean dealRealTime(SysExceptionDealVO sysExceptionDealVO){
        List<SysExceptionInfo> sysExceptionInfos = sysExceptionMapper.selectSysExceptionInfos(sysExceptionDealVO);
        for(SysExceptionInfo sysExceptionInfo  : sysExceptionInfos){
            try{
                boolean dealFlag = false;
                String data = sysExceptionInfo.getExceptionBody();
                Object messageObject = BeanJsonDeserializer.deserialize(data);
                if(messageObject instanceof Message) {
                    Message message = (Message) messageObject;
                    if("waybill".equals(sysExceptionInfo.getNoType())){
                        waybillMessageListenerService.deal(message);
                        dealFlag = true;
                    }
                    else if("billing".equals(sysExceptionInfo.getNoType())){
                        billingMessageService.billing(message, DataType.REAL_TIME_DATA.getCode());
                        dealFlag = true;
                    }
                }else if(messageObject instanceof MessageRedisCacheInfo){
                    MessageRedisCacheInfo messageCacheInfo = (MessageRedisCacheInfo) BeanJsonDeserializer.deserialize(data);
                    Message message = messageCacheInfo.getMessage();
                    if("waybill".equals(sysExceptionInfo.getNoType())){
                        waybillMessageListenerService.deal(message);
                        dealFlag = true;
                    }
                    else if("billing".equals(sysExceptionInfo.getNoType())){
                        billingMessageService.billing(message, DataType.REAL_TIME_DATA.getCode());
                        dealFlag = true;
                    }
                }
                if(dealFlag){
                    SysExceptionInfo sysExceptionInfoup = new SysExceptionInfo();
                    sysExceptionInfoup.setDataType(9999);
                    sysExceptionInfoup.setId(sysExceptionInfo.getId());
                    sysExceptionMapper.updateByPrimaryKeySelective(sysExceptionInfoup);
                }
            }catch (Exception e){
                log.error("no:" + sysExceptionInfo.getNo() + ",sentTime:" + sysExceptionInfo.getSentTime()+",excute error",e);
            }
        }
        return true;
    }

    /**
     * 同步历史数据
     * @param sysExceptionDealVO
     * @return
     */
    public boolean dealOld(SysExceptionDealVO sysExceptionDealVO){
        List<SysExceptionInfo> sysExceptionInfos = sysExceptionMapper.selectSysExceptionInfos(sysExceptionDealVO);
        for(SysExceptionInfo sysExceptionInfo  : sysExceptionInfos){
            try{
                String data = sysExceptionInfo.getExceptionBody();
                Object messageObject = BeanJsonDeserializer.deserialize(data);
                boolean dealFlag = false;
                if(messageObject instanceof OldDataMessage) {
                    OldDataMessage oldDataMessage = (OldDataMessage) messageObject;
                    if("olddata".equals(sysExceptionInfo.getNoType())){
                        String dealMethod = oldDataMessage.getDealMethod();
                        if (StringUtils.isEmpty(dealMethod)) {
                            log.error("dealMethod不能为空");
                            throw new RuntimeException("dealMethod不能为空");
                        } else {
                            OperationProcessMethod operationProcessMethod = OperationProcessMethod.getEnumByEnumNameString(dealMethod);
                            if (operationProcessMethod != null) {
                                log.info("调用" + oldMessageService + "的方法 : " + operationProcessMethod.getMessage());
                                Object service = SpringUtils.getApplicationContext().getBean(oldMessageService);
                                Method method = service.getClass().getMethod(operationProcessMethod.getMessage(), OldDataMessage.class);
                                method.invoke(service, oldDataMessage);
                                dealFlag = true;
                            } else {
                                throw new RuntimeException("dealMethod 找不到对应枚举");
                            }
                        }
                    }
                }else if(messageObject instanceof Message){
                    Message message = (Message) BeanJsonDeserializer.deserialize(data);
                    if("oldbilling".equals(sysExceptionInfo.getNoType())){
                        billingMessageService.billing(message, DataType.HISTORICAL_DATA.getCode());
                        dealFlag = true;
                    }
                }
                if(dealFlag) {
                    SysExceptionInfo sysExceptionInfoup = new SysExceptionInfo();
                    sysExceptionInfoup.setDataType(9999);
                    sysExceptionInfoup.setId(sysExceptionInfo.getId());
                    sysExceptionMapper.updateByPrimaryKeySelective(sysExceptionInfoup);
                }
            }catch (Exception e){
                log.error("no:" + sysExceptionInfo.getNo() + ",sentTime:" + sysExceptionInfo.getSentTime()+",excute error",e);
            }
        }
        return true;
    }

    /**
     * 运单信息状态校验
     * @param param
     */
    public void checkWayBillInfoStatus(String param){
        List<Map<String,Object>> listMaps = waybillInfoExtMapper.selectOrderByOpenBillTime();

        Map<String,Integer> mapWayBillStatus = new HashMap<>();

        List<String> wayBillList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<listMaps.size(); i++){
            mapWayBillStatus.put((String)listMaps.get(i).get("id"),(Integer)listMaps.get(i).get("waybill_status"));
            sb.append(listMaps.get(i).get("id")+",");
            if((i+1)%10000 == 0){
                wayBillList.add(sb.toString().substring(0,sb.toString().length()-1));
                sb = new StringBuilder();
            }
        }
        wayBillList.add(sb.toString().substring(0,sb.toString().length()-1));



        for(String waybillIds :wayBillList){
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
            params.add("title", waybillIds);
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
            ResponseEntity<List> responseEntity = restTemplate.exchange("http://134.175.172.242:11501/oldTaskToDataController/queryWaybillStatus", HttpMethod.POST, requestEntity, List.class);
            List<Map<String,String>> resultList = responseEntity.getBody();

            for(Map<String,String> result : resultList){
                String waybillId = (String)result.get("waybillId");
                if(mapWayBillStatus.get(waybillId) != null){
                    WaybillStatus waybillStatus = WaybillStatus.getEnumByCode(mapWayBillStatus.get(waybillId));
                    if(waybillStatus != null){
                        if(!waybillStatus.name().equals(result.get("waybillStatus"))){
                            SysExceptionInfo sysExceptionInfo = new SysExceptionInfo();
                            sysExceptionInfo.setNo(waybillId);
                            sysExceptionInfo.setId(OID.get().toString());
                            sysExceptionInfo.setNoType("checkStat");
                            sysExceptionInfo.setDataType(5);
                            sysExceptionInfo.setSource("IPS");
                            sysExceptionInfo.setExceptionBody("data-import:"+waybillStatus.name()+",IPS:"+result.get("waybillStatus"));
                            sysExceptionInfo.setExceptionInfo("状态不正确");
                            sysExceptionMapper.insert(sysExceptionInfo);
                        }
                    }
                }else{
                    SysExceptionInfo sysExceptionInfo = new SysExceptionInfo();
                    sysExceptionInfo.setNo(waybillId);
                    sysExceptionInfo.setId(OID.get().toString());
                    sysExceptionInfo.setNoType("checkStat");
                    sysExceptionInfo.setDataType(5);
                    sysExceptionInfo.setSource("IPS");
                    sysExceptionInfo.setExceptionBody("IPS:"+result.get("waybillStatus"));

                    sysExceptionInfo.setExceptionInfo("数据平台数据不存在");
                    sysExceptionMapper.insert(sysExceptionInfo);
                }
            }
        }
    }

    /**
     * 清理缓存
     */
    public void deleteWayBillInfoCache(SysWayBillVO sysWayBillVO){
        List<String> waybillIds = sysWayBillVO.getWayBillNos();
        for(String waybillId : waybillIds){
            orderProcessor.deleteOrderCache(waybillId);
        }
    }


}
