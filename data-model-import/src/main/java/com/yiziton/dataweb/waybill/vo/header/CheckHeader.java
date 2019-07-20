package com.yiziton.dataweb.waybill.vo.header;

import com.yiziton.dataweb.waybill.utils.excel.Header;

/**
 * Created by ouweijian on 2018-12-06 18:22:22.
 */
public class CheckHeader extends InfoHeader {

    public CheckHeader() {
        headerMap.put("waybillId",new Header("waybillId","运单号","string",12));
        headerMap.put("openBillNode",new Header("openBillNode","开单网点","string",12));
        headerMap.put("checkBillId",new Header("checkBillId","核销单号","string",12));
        headerMap.put("customerBillId",new Header("customerBillId","客户单号","string",12));
        headerMap.put("openBillTime",new Header("openBillTime","开单日期","string",12));
        headerMap.put("originatingNode",new Header("originatingNode","始发网点","string",12));
        headerMap.put("orderBillId",new Header("orderBillId","订单号","string",12));
        headerMap.put("serviceType",new Header("serviceType","服务类型","string",12));
        headerMap.put("distributionOperator",new Header("distributionOperator","分配人","string",12));
        headerMap.put("distributionTime",new Header("distributionTime","分配时间","string",12));
        headerMap.put("masterNode",new Header("masterNode","末端网点","string",12));
        headerMap.put("masterName",new Header("masterName","安装师傅姓名","string",12));
        headerMap.put("waybillStatus",new Header("waybillStatus","运单状态","string",12));
        headerMap.put("checkStatus",new Header("checkStatus","核销状态","string",12));
        headerMap.put("checkWaybillTime",new Header("checkWaybillTime","核销时间","string",12));
        headerMap.put("checkType",new Header("checkType","核销类型","string",12));
        headerMap.put("checkMethod",new Header("checkMethod","核销方式","string",12));
        headerMap.put("trunkStartTime",new Header("trunkStartTime","干线开始时间","string",12));
        headerMap.put("appointmentTime",new Header("appointmentTime","预约时间","string",12));
        headerMap.put("pickUpTime",new Header("pickUpTime","提货时间","string",12));
        headerMap.put("signTime",new Header("signTime","签收时间","string",12));
        headerMap.put("consignorName",new Header("consignorName","发货人","string",12));
        headerMap.put("consignorMobile",new Header("consignorMobile","发货人手机","string",12));
        headerMap.put("secondMerchantName",new Header("secondMerchantName","商家名称","string",12));
        headerMap.put("receiveName",new Header("receiveName","收货人","string",12));
        headerMap.put("receiveMobile",new Header("receiveMobile","收货人手机","string",12));
        headerMap.put("receiveAddress",new Header("receiveAddress","收货人地址","string",12));
        headerMap.put("province",new Header("province","目的省","string",12));
        headerMap.put("city",new Header("city","目的市","string",12));
        headerMap.put("area",new Header("area","目的区县","string",12));
        headerMap.put("street",new Header("street","目的镇","string",12));
        headerMap.put("channelSource",new Header("channelSource","运单来源","string",12));
        headerMap.put("goodsName",new Header("goodsName","品名","string",12));
        headerMap.put("goodsPacking",new Header("goodsPacking","包装","string",12));
        headerMap.put("packingTotalCount",new Header("packingTotalCount","总件数","string",12));
        headerMap.put("packingTotalWeight",new Header("packingTotalWeight","总重量","string",12));
        headerMap.put("packingTotalVolume",new Header("packingTotalVolume","总体积","string",12));
        headerMap.put("pickUpGoodsPhone",new Header("pickUpGoodsPhone","提货电话","string",12));
        headerMap.put("arrivalAddress",new Header("arrivalAddress","提货地址","string",12));
        headerMap.put("tmallOrderNoStatus",new Header("tmallOrderNoStatus","天猫订单号状态","string",12));
    }
}
