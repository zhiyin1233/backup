package com.yiziton.dataimport.waybill.service;

import com.yiziton.dataimport.waybill.bean.BillingInfo;
import com.yiziton.dataimport.waybill.dao.ext.BillingInfoExtMapper;
import com.yiziton.dataimport.waybill.dao.ext.FeeInfoExtMapper;
import com.yiziton.dataimport.waybill.dao.ext.WaybillInfoExtMapper;
import com.yiziton.dataweb.waybill.vo.ImportConditionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: kdh
 * @Date: 2019/1/29
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@Component("historyDataImportService")
@Slf4j
public class HistoryDataImportService {

    @Autowired
    BillingInfoExtMapper billingInfoExtMapper;

    @Autowired
    FeeInfoExtMapper feeInfoExtMapper;

    @Autowired
    WaybillInfoExtMapper waybillInfoExtMapper;

    public void updateBillingAndFee(ImportConditionVO importConditionVO) throws Exception {
        if (importConditionVO.getWaybillIds() != null && importConditionVO.getWaybillIds().size() > 0) {
            billingInfoExtMapper.updateByPrimaryKeys(importConditionVO.getWaybillIds());
            feeInfoExtMapper.updateByPrimaryKeys(importConditionVO.getWaybillIds());
        } else if (importConditionVO.getOpenBillTimeBegin() != null && importConditionVO.getOpenBillTimeEnd() != null) {
            billingInfoExtMapper.updateByOpenBillTime(importConditionVO.getOpenBillTimeBegin(), importConditionVO.getOpenBillTimeEnd());
            feeInfoExtMapper.updateByOpenBillTime(importConditionVO.getOpenBillTimeBegin(), importConditionVO.getOpenBillTimeEnd());
        }
    }

    public List<String> selectAllId(ImportConditionVO importConditionVO) throws Exception {
        if (importConditionVO.getOpenBillTimeBegin() != null && importConditionVO.getOpenBillTimeEnd() != null) {
            List<String> strings = waybillInfoExtMapper.selectAllId(importConditionVO.getOpenBillTimeBegin(), importConditionVO.getOpenBillTimeEnd());
            if (strings!=null && strings.size()>0){
                return strings;
            }
        }
        return Collections.emptyList();
    }

    public Map selectTotalPrice(List<String> subList) throws Exception {
        List list = billingInfoExtMapper.selectTotalPrice(subList);
        Map map = getHashMap(list);
        return map;
    }

    /**
     * 拼装返回结果 HashMap
     *
     * @param list
     * @return
     * @throws Exception
     */
    private HashMap getHashMap(List<BillingInfo> list) throws Exception {
        HashMap map = new HashMap(16);
        if (list != null && list.size() > 0) {
            BillingInfo tmp = null;
            for (int i = 0; i < list.size(); i++) {
                tmp = (BillingInfo) list.get(i);
                String key = tmp.getWaybillId() == null ? "" : tmp.getWaybillId();
                Double value = tmp.getTotalPrice() == null ? 0.00D : tmp.getTotalPrice();
                map.put(key, value);
            }
        }
        return map;
    }
}
