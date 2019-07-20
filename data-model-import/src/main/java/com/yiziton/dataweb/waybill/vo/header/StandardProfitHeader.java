package com.yiziton.dataweb.waybill.vo.header;

import com.yiziton.dataweb.waybill.utils.excel.Header;

/**
 * @Description:
 * @Author: xujinping
 * @Date: 2018/11/28
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class StandardProfitHeader extends InfoHeader{

    public StandardProfitHeader(){
        headerMap.put("waybillId",new Header("waybillId","运单号","string",12));
        headerMap.put("openBillTime",new Header("openBillTime","开单时间","string",12));
        headerMap.put("openBillNode",new Header("openBillNode","开单网点","string",12));
        headerMap.put("originatingNode",new Header("originatingNode","始发网点","string",12));
        headerMap.put("distributionTime",new Header("distributionTime","分配时间","string",12));
        headerMap.put("consignorName",new Header("consignorName","发货人","string",12));
        headerMap.put("secondCustomer",new Header("secondCustomer","商家名称","string",12));
        headerMap.put("customerType",new Header("customerType","商家类型","string",12));
        headerMap.put("paymentType",new Header("paymentType","付款方式","string",12));
        headerMap.put("settlementType",new Header("settlementType","结算方式","string",12));
        headerMap.put("serviceType",new Header("serviceType","服务类型","string",12));
        headerMap.put("province",new Header("province","目的省","string",12));
        headerMap.put("city",new Header("city","目的市","string",12));
        headerMap.put("area",new Header("area","目的区","string",12));
        headerMap.put("address",new Header("address","详细地址","string",12));
        headerMap.put("goodsType",new Header("goodsType","货物类型","string",12));
        headerMap.put("goodsName",new Header("goodsName","品名","string",12));
        headerMap.put("packingTotalCount",new Header("packingTotalCount","包装总件数","string",12));
        headerMap.put("totalVolume",new Header("totalVolume","总体积","string",12));
        headerMap.put("signTime",new Header("signTime","签收时间","string",12));
        headerMap.put("salesman",new Header("salesman","业务员","string",12));
        headerMap.put("waybillStatus",new Header("waybillStatus","运单状态","string",12));

        headerMap.put("consignorTransportFee",new Header("consignorTransportFee","运费","string",12));
        headerMap.put("deliveryFee",new Header("deliveryFee","基础送货费","string",12));
        headerMap.put("statementValue",new Header("statementValue","声明价值","string",12));
        headerMap.put("insuranceFee",new Header("insuranceFee","保价费","string",12));
        headerMap.put("openBillInstallFee",new Header("openBillInstallFee","安装费","string",12));
        headerMap.put("upstairsFee",new Header("upstairsFee","上楼费","string",12));
        headerMap.put("takeGoodsFee",new Header("takeGoodsFee","揽货费","string",12));
        headerMap.put("otherFee",new Header("otherFee","其他费","string",12));
        headerMap.put("agingServiceFee",new Header("agingServiceFee","时效服务费","string",12));
        headerMap.put("doorFee",new Header("doorFee","上门服务费","string",12));
        headerMap.put("entryHomeFee",new Header("entryHomeFee","入户费","string",12));
        headerMap.put("marbleFee",new Header("marbleFee","大理石费","string",12));
        headerMap.put("promotionFee",new Header("promotionFee","干支促销费","string",12));
        headerMap.put("exceedAreaFee",new Header("exceedAreaFee","超区费","string",12));
        headerMap.put("exceedSquareFee",new Header("exceedSquareFee","超方费","string",12));
        headerMap.put("woodenFee",new Header("woodenFee","打木架费","string",12));
        headerMap.put("largeExtraFee",new Header("largeExtraFee","大票附加费","string",12));
        headerMap.put("couponRelief",new Header("couponRelief","优惠卷减免费用","string",12));
        headerMap.put("giftRelief",new Header("giftRelief","赠送金减免费用","string",12));
        headerMap.put("cashRelief",new Header("cashRelief","返现金减免","string",12));
        headerMap.put("collectionServiceFee",new Header("collectionServiceFee","代收货款手续费","string",12));
        headerMap.put("totalOpenBillFee",new Header("totalOpenBillFee","开单合计","string",12));

        headerMap.put("totalConsignorAbnFee",new Header("totalConsignorAbnFee","商家承担异常费合计","string",12));
        headerMap.put("totalMasterAbnFee",new Header("totalMasterAbnFee","服务商责任异常费合计","string",12));
        headerMap.put("totalCarrierAbnFee",new Header("totalCarrierAbnFee","承运商责任异常费合计","string",12));
        headerMap.put("addFee",new Header("addFee","追赔收入","string",12));
        headerMap.put("totalIncome",new Header("totalIncome","总收入合计","string",12));
        headerMap.put("outDeliverCost",new Header("outDeliverCost","外发成本","string",12));
        headerMap.put("carrierName",new Header("carrierName","承运商","string",12));
        headerMap.put("masterInstallFee",new Header("masterInstallFee","安装费成本","string",12));
        headerMap.put("masterTransportFee",new Header("masterTransportFee","支线费成本","string",12));
        headerMap.put("pickUpNodeFee",new Header("pickUpNodeFee","提点费","string",12));
        headerMap.put("masterName",new Header("masterName","安装师傅","string",12));
        headerMap.put("totalInstallAbnFee",new Header("totalInstallAbnFee","安装异常费合计","string",12));
        headerMap.put("repairFee",new Header("repairFee","维修费","string",12));
        headerMap.put("backFee",new Header("backFee","返货费","string",12));
        headerMap.put("claimFee",new Header("claimFee","理赔费用","string",12));
        headerMap.put("abnPraiseFee",new Header("abnPraiseFee","师傅好评费","string",12));
        headerMap.put("abnPartialRefundFee",new Header("abnPartialRefundFee","商家部分退款费","string",12));
        headerMap.put("totalCost",new Header("totalCost","总成本合计","string",12));

        headerMap.put("grossProfit",new Header("grossProfit","毛利","string",12));
        headerMap.put("saleTotalPrice",new Header("saleTotalPrice","销售总价","string",12));
    }

}