package com.yiziton.dataweb.waybill.vo.header;

import com.yiziton.dataweb.waybill.utils.excel.Header;

/**
 * Created by ouweijian on 2018-12-04 18:16:40.
 */
public class WaybillAgingHeader extends InfoHeader {

    public WaybillAgingHeader(){
        headerMap.put("waybillId",new Header("waybillId","运单号","string",12));
        headerMap.put("openBillTime",new Header("openBillTime","开单时间","string",12));
        headerMap.put("openBillNode",new Header("openBillNode","开单网点","string",12));
        headerMap.put("warehousingTime",new Header("warehousingTime","入库时间","string",12));
        headerMap.put("pickingTime",new Header("pickingTime","拣货时间","string",12));
        headerMap.put("originatingNode",new Header("originatingNode","始发网点","string",12));
        headerMap.put("waybillStatus",new Header("waybillStatus","运单状态","string",12));
        headerMap.put("consignorName",new Header("consignorName","发货人","string",12));
        headerMap.put("consignorMobile",new Header("consignorMobile","发货人手机","string",12));
        headerMap.put("settlementType",new Header("settlementType","结算方式","string",12));
        headerMap.put("receiveName",new Header("receiveName","收货人","string",12));
        headerMap.put("receiveMobile",new Header("receiveMobile","收货人手机","string",12));
        headerMap.put("destination",new Header("destination","目的地","string",12));
        headerMap.put("province",new Header("province","目的省","string",12));
        headerMap.put("city",new Header("city","目的市","string",12));
        headerMap.put("area",new Header("area","目的区","string",12));
        headerMap.put("receiveAddress",new Header("receiveAddress","详细地址","string",12));
        headerMap.put("serviceType",new Header("serviceType","服务类型","string",12));
        headerMap.put("merchantType",new Header("merchantType","商家标识","string",12));
        headerMap.put("goodsName",new Header("goodsName","品名","string",12));
        headerMap.put("goodsPacking",new Header("goodsPacking","包装","string",12));
        headerMap.put("packingTotalCount",new Header("packingTotalCount","包装总件数","string",12));
        headerMap.put("packingTotalVolume",new Header("packingTotalVolume","总体积","string",12));
        headerMap.put("totalFee",new Header("totalFee","总费用","string",12));
        headerMap.put("openBillOperator",new Header("openBillOperator","制单人","string",12));
        headerMap.put("openBillRemark",new Header("openBillRemark","备注","string",12));
        headerMap.put("carrier",new Header("carrier","承运商","string",12));
        headerMap.put("logisticsBillId",new Header("logisticsBillId","物流单号","string",12));
        headerMap.put("outDeliverTime",new Header("outDeliverTime","外发交接时间","string",12));
        headerMap.put("carrierDepartureTime",new Header("carrierDepartureTime","承运商发车时间","string",12));
        headerMap.put("carrierArriveTime",new Header("carrierArriveTime","承运商内部到达中转时间","string",12));
        headerMap.put("carrierTransferTime",new Header("carrierTransferTime","承运商内部中转发车时间","string",12));
        headerMap.put("arrivalDestinationTime",new Header("arrivalDestinationTime","承运商到达时间","string",12));
        headerMap.put("directTransferTime",new Header("directTransferTime","直营外发时间","string",12));
        headerMap.put("departmentOutDeliverBillNo",new Header("departmentOutDeliverBillNo","直营外发单号","string",12));
        headerMap.put("trunkEndTime",new Header("trunkEndTime","干线结束时间","string",12));
        headerMap.put("distributionTime",new Header("distributionTime","客服中心派单时间","string",12));
        headerMap.put("distributionMaster",new Header("distributionMaster","客服中心派单师傅","string",12));
        headerMap.put("firstAppointmentTime",new Header("firstAppointmentTime","首次预约操作时间","string",12));
        headerMap.put("firstVisitTime",new Header("firstVisitTime","首次预约上门服务时间","string",12));
        headerMap.put("appointmentCount",new Header("appointmentCount","预约次数","string",12));
        headerMap.put("lastAppointmentTime",new Header("lastAppointmentTime","最新一次预约操作时间","string",12));
        headerMap.put("lastVisitTime",new Header("lastVisitTime","最新一次预约上门服务时间","string",12));
        headerMap.put("masterPhone",new Header("masterPhone","安装师傅账号","string",12));
        headerMap.put("masterName",new Header("masterName","安装师傅","string",12));
        headerMap.put("homeCheckTime",new Header("homeCheckTime","上门打卡时间","string",12));
        headerMap.put("signTime",new Header("signTime","签收时间","string",12));
        headerMap.put("checkType",new Header("checkType","核销类型","string",12));
        headerMap.put("checkMethod",new Header("checkMethod","核销方式","string",12));
        headerMap.put("checkBillId",new Header("checkBillId","核销单号","string",12));
        headerMap.put("checkStatus",new Header("checkStatus","是否核销","string",12));
        headerMap.put("masterNode",new Header("masterNode","末端网点","string",12));
        headerMap.put("secondMerchantName",new Header("secondMerchantName","二级商家名称","string",12));
        headerMap.put("waybillType",new Header("waybillType","运单类型","string",12));
        headerMap.put("orderBillId",new Header("orderBillId","订单号","string",12));
        headerMap.put("customerBillId",new Header("customerBillId","客户单号","string",12));
    }
}
