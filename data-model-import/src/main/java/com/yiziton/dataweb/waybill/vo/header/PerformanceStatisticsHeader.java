package com.yiziton.dataweb.waybill.vo.header;

import com.yiziton.dataweb.waybill.utils.excel.Header;

/**
 * Created by ouweijian on 2018-12-06 19:00:32.
 */
public class PerformanceStatisticsHeader extends InfoHeader {

    public PerformanceStatisticsHeader() {
        headerMap.put("waybillId",new Header("waybillId","运单号","string",12));
        headerMap.put("openBillTime",new Header("openBillTime","开单时间","string",12));
        headerMap.put("openBillNode",new Header("openBillNode","开单网点","string",12));
        headerMap.put("productType",new Header("productType","产品类型","string",12));
        headerMap.put("merchantType",new Header("merchantType","商家类型","string",12));
        headerMap.put("secondMerchantName",new Header("secondMerchantName","商家名称","string",12));
        headerMap.put("consignorName",new Header("consignorName","发货人","string",12));
        headerMap.put("consignorMobile",new Header("consignorMobile","发货人手机","string",12));
        headerMap.put("receiveName",new Header("receiveName","收货人","string",12));
        headerMap.put("receiveMobile",new Header("receiveMobile","收货人手机","string",12));
        headerMap.put("goodsType",new Header("goodsType","货物类别","string",12));
        headerMap.put("goodsName",new Header("goodsName","货物名称","string",12));
        headerMap.put("goodsPacking",new Header("goodsPacking","包装","string",12));
        headerMap.put("packingTotalCount",new Header("packingTotalCount","件数","string",12));
        headerMap.put("packingTotalVolume",new Header("packingTotalVolume","体积","string",12));
        headerMap.put("packingTotalWeight",new Header("packingTotalWeight","重量","string",12));
        headerMap.put("province",new Header("province","目的省","string",12));
        headerMap.put("city",new Header("city","目的市","string",12));
        headerMap.put("area",new Header("area","目的区","string",12));
        headerMap.put("street",new Header("street","目的镇","string",12));
        headerMap.put("receiveAddress",new Header("receiveAddress","收货人地址","string",12));
        headerMap.put("serviceType",new Header("serviceType","服务类型","string",12));
        headerMap.put("totalFee",new Header("totalFee","合计运费","string",12));
        headerMap.put("paymentType",new Header("paymentType","付款方式","string",12));
        headerMap.put("openBillOperator",new Header("openBillOperator","开单员","string",12));
        headerMap.put("waybillStatus",new Header("waybillStatus","运单状态","string",12));
        headerMap.put("channelSource",new Header("channelSource","运单来源","string",12));
        headerMap.put("transportFee",new Header("transportFee","运费","string",12));
        headerMap.put("deliveryFee",new Header("deliveryFee","基础送货费","string",12));
        headerMap.put("statementValue",new Header("statementValue","声明价值","string",12));
        headerMap.put("saleTotalPrice",new Header("saleTotalPrice","销售价","string",12));
        headerMap.put("insuranceFee",new Header("insuranceFee","保价费","string",12));
        headerMap.put("installFee",new Header("installFee","安装费","string",12));
        headerMap.put("upstairsFee",new Header("upstairsFee","上楼费","string",12));
        headerMap.put("otherFee",new Header("otherFee","其他费","string",12));
        headerMap.put("openBillRemark",new Header("openBillRemark","开单备注","string",12));
        headerMap.put("signName",new Header("signName","签收人","string",12));
        headerMap.put("signTime",new Header("signTime","签收时间","string",12));

    }
}
