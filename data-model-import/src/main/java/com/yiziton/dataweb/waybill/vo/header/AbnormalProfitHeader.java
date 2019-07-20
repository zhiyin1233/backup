package com.yiziton.dataweb.waybill.vo.header;

import com.yiziton.dataweb.waybill.utils.excel.Header;

/**
 * @Description:
 * @Author: xujinping
 * @Date: 2018/11/28
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class AbnormalProfitHeader extends InfoHeader{

    public AbnormalProfitHeader(){
        headerMap.put("waybillId",new Header("waybillId","运单号","string",12));
        headerMap.put("openBillTime",new Header("openBillTime","开单时间","string",12));
        headerMap.put("openBillNode",new Header("openBillNode","开单网点","string",12));
        headerMap.put("signTime",new Header("signTime","签收时间","string",12));
        headerMap.put("province",new Header("province","目的省","string",12));
        headerMap.put("city",new Header("city","目的市","string",12));
        headerMap.put("area",new Header("area","目的区","string",12));
        headerMap.put("consignorName",new Header("consignorName","发货人","string",12));
        headerMap.put("secondCustomer",new Header("secondCustomer","商家名称","string",12));
        headerMap.put("customerType",new Header("customerType","商家类型","string",12));
        headerMap.put("settlementType",new Header("settlementType","结算方式","string",12));
        headerMap.put("serviceType",new Header("serviceType","服务类型","string",12));
        headerMap.put("salesman",new Header("salesman","业务员","string",12));
        headerMap.put("waybillStatus",new Header("waybillStatus","运单状态","string",12));

        headerMap.put("consignorDeliveryFee",new Header("consignorDeliveryFee","发货人送货费","string",12));
        headerMap.put("consignorInstallFee2",new Header("consignorInstallFee2","发货人安装费","string",12));//无,2018.06前旧数据可能有
        headerMap.put("consignorAdvanceFee",new Header("consignorAdvanceFee","发货人垫付费","string",12));
        headerMap.put("consignorUpstairsFee2",new Header("consignorUpstairsFee2","发货人上楼费","string",12));//无,2018.06前旧数据可能有
        headerMap.put("consignorExceedSquareFee",new Header("consignorExceedSquareFee","发货人超方费","string",12));
        headerMap.put("consignorExceedAreaFee",new Header("consignorExceedAreaFee","发货人超区费","string",12));
        headerMap.put("consignorDismountingFee",new Header("consignorDismountingFee","发货人拆装费","string",12));
        headerMap.put("consignorHoistingBuildFee",new Header("consignorHoistingBuildFee","发货人吊楼费","string",12));
        headerMap.put("consignorTranslationalFee",new Header("consignorTranslationalFee","发货人平移费","string",12));
        headerMap.put("consignorEmptyRunFee",new Header("consignorEmptyRunFee","发货人空跑费","string",12));
        headerMap.put("consignorSpecialAreaFee",new Header("consignorSpecialAreaFee","发货人特殊区域费","string",12));
        headerMap.put("consignorStorageFee",new Header("consignorStorageFee","发货人仓储费","string",12));
        headerMap.put("consignorUrgentFee",new Header("consignorUrgentFee","发货人加急费","string",12));
        headerMap.put("consignorSecondDoorFee",new Header("consignorSecondDoorFee","发货人二次上门费","string",12));
        headerMap.put("consignorMinUpstairsFee",new Header("consignorMinUpstairsFee","发货人＞7楼搬楼费","string",12));
        headerMap.put("consignorMaxUpstairsFee",new Header("consignorMaxUpstairsFee","发货人≤7楼搬楼费","string",12));
        headerMap.put("consignorPickUpGoodsFee",new Header("consignorPickUpGoodsFee","发货人提货费","string",12));
        headerMap.put("consignorInsuranceFee",new Header("consignorInsuranceFee","发货人保价费","string",12));
        headerMap.put("consignorServiceDebit",new Header("consignorServiceDebit","发货人的服务商扣款","string",12));
        headerMap.put("consignorReservationRate",new Header("consignorReservationRate","发货人预约超时费","string",12));
        headerMap.put("consignorInstallationRate",new Header("consignorInstallationRate","发货人安装超时费","string",12));
        headerMap.put("consignorInstallFee",new Header("consignorInstallFee","发货人异常安装费","string",12));
        headerMap.put("consignorUpstairsFee",new Header("consignorUpstairsFee","发货人异常上楼费","string",12));
        headerMap.put("consignorDropGoodFee",new Header("consignorDropGoodFee","发货人弃货费","string",12));
        headerMap.put("consignorRepairFee",new Header("consignorRepairFee","发货人维修费","string",12));
        headerMap.put("consignorAbnRepairFee",new Header("consignorAbnRepairFee","发货人异常维修费","string",12));
        headerMap.put("consignorBackFee",new Header("consignorBackFee","发货人返货费","string",12));
        headerMap.put("consignorAbnBackFee",new Header("consignorAbnBackFee","发货人异常返货费","string",12));
        headerMap.put("consignorPartFee",new Header("consignorPartFee","发货人补件费","string",12));
        headerMap.put("consignorAbnRecoveryFee",new Header("consignorAbnRecoveryFee","发货人异常追偿责任费","string",12));
        headerMap.put("consignorAbnCollectionServiceFee",new Header("consignorAbnCollectionServiceFee","发货人异常代收货款手续费","string",12));
        headerMap.put("consignorAbnWoodenFee",new Header("consignorAbnWoodenFee","发货人异常打木架费","string",12));
        headerMap.put("consignorAbnOtherFee",new Header("consignorAbnOtherFee","发货人异常其他费","string",12));
        headerMap.put("totalConsignorAbnFee",new Header("totalConsignorAbnFee","发货人异常合计","string",12));

        headerMap.put("carrierDeliveryFee",new Header("carrierDeliveryFee","承运商送货费","string",12));
        headerMap.put("carrierInstallFee2",new Header("carrierInstallFee2","承运商安装费","string",12));//无,2018.06前旧数据可能有
        headerMap.put("carrierAdvanceFee",new Header("carrierAdvanceFee","承运商垫付费","string",12));
        headerMap.put("carrierUpstairsFee2",new Header("carrierUpstairsFee2","承运商上楼费","string",12));//无,2018.06前旧数据可能有
        headerMap.put("carrierExceedSquareFee",new Header("carrierExceedSquareFee","承运商超方费","string",12));
        headerMap.put("carrierExceedAreaFee",new Header("carrierExceedAreaFee","承运商超区费","string",12));
        headerMap.put("carrierDismountingFee",new Header("carrierDismountingFee","承运商拆装费","string",12));
        headerMap.put("carrierHoistingBuildFee",new Header("carrierHoistingBuildFee","承运商吊楼费","string",12));
        headerMap.put("carrierTranslationalFee",new Header("carrierTranslationalFee","承运商平移费","string",12));
        headerMap.put("carrierEmptyRunFee",new Header("carrierEmptyRunFee","承运商空跑费","string",12));
        headerMap.put("carrierSpecialAreaFee",new Header("carrierSpecialAreaFee","承运商特殊区域费","string",12));
        headerMap.put("carrierStorageFee",new Header("carrierStorageFee","承运商仓储费","string",12));
        headerMap.put("carrierUrgentFee",new Header("carrierUrgentFee","承运商加急费","string",12));
        headerMap.put("carrierSecondDoorFee",new Header("carrierSecondDoorFee","承运商二次上门费","string",12));
        headerMap.put("carrierMinUpstairsFee",new Header("carrierMinUpstairsFee","承运商＞7楼搬楼费","string",12));
        headerMap.put("carrierMaxUpstairsFee",new Header("carrierMaxUpstairsFee","承运商≤7楼搬楼费","string",12));
        headerMap.put("carrierPickUpGoodsFee",new Header("carrierPickUpGoodsFee","承运商提货费","string",12));
        headerMap.put("carrierInsuranceFee",new Header("carrierInsuranceFee","承运商保价费","string",12));
        headerMap.put("carrierServiceDebit",new Header("carrierServiceDebit","承运商的服务商扣款","string",12));
        headerMap.put("carrierReservationRate",new Header("carrierReservationRate","承运商预约超时费","string",12));
        headerMap.put("carrierInstallationRate",new Header("carrierInstallationRate","承运商安装超时费","string",12));
        headerMap.put("carrierInstallFee",new Header("carrierInstallFee","承运商异常安装费","string",12));
        headerMap.put("carrierUpstairsFee",new Header("carrierUpstairsFee","承运商异常上楼费","string",12));
        headerMap.put("carrierDropGoodFee",new Header("carrierDropGoodFee","承运商弃货费","string",12));
        headerMap.put("carrierRepairFee",new Header("carrierRepairFee","承运商维修费","string",12));
        headerMap.put("carrierAbnRepairFee",new Header("carrierAbnRepairFee","承运异常商维修费","string",12));
        headerMap.put("carrierBackFee",new Header("carrierBackFee","承运商返货费","string",12));
        headerMap.put("carrierAbnBackFee",new Header("carrierAbnBackFee","承运商异常返货费","string",12));
        headerMap.put("carrierPartFee",new Header("carrierPartFee","承运商补件费","string",12));
        headerMap.put("carrierAbnRecoveryFee",new Header("carrierAbnRecoveryFee","承运商异常追偿责任费","string",12));
        headerMap.put("carrierAbnCollectionServiceFee",new Header("carrierAbnCollectionServiceFee","承运商异常代收货款手续费","string",12));
        headerMap.put("carrierAbnWoodenFee",new Header("carrierAbnWoodenFee","承运商异常打木架费","string",12));
        headerMap.put("carrierAbnOtherFee",new Header("carrierAbnOtherFee","承运商异常其他费","string",12));
        headerMap.put("totalCarrierAbnFee",new Header("totalCarrierAbnFee","承运商异常合计","string",12));

        headerMap.put("masterDeliveryFee",new Header("masterDeliveryFee","服务商送货费","string",12));
        headerMap.put("masterAdvanceFee",new Header("masterAdvanceFee","服务商垫付费","string",12));
        headerMap.put("masterExceedSquareFee",new Header("masterExceedSquareFee","服务商超方费","string",12));
        headerMap.put("masterExceedAreaFee",new Header("masterExceedAreaFee","服务商超区费","string",12));
        headerMap.put("masterDismountingFee",new Header("masterDismountingFee","服务商拆装费","string",12));
        headerMap.put("masterHoistingBuildFee",new Header("masterHoistingBuildFee","服务商吊楼费","string",12));
        headerMap.put("masterTranslationalFee",new Header("masterTranslationalFee","服务商平移费","string",12));
        headerMap.put("masterEmptyRunFee",new Header("masterEmptyRunFee","服务商空跑费","string",12));
        headerMap.put("masterSpecialAreaFee",new Header("masterSpecialAreaFee","服务商特殊区域费","string",12));
        headerMap.put("masterStorageFee",new Header("masterStorageFee","服务商仓储费","string",12));
        headerMap.put("masterUrgentFee",new Header("masterUrgentFee","服务商加急费","string",12));
        headerMap.put("masterSecondDoorFee",new Header("masterSecondDoorFee","服务商二次上门费","string",12));
        headerMap.put("masterMinUpstairsFee",new Header("masterMinUpstairsFee","服务商＞7楼搬楼费","string",12));
        headerMap.put("masterMaxUpstairsFee",new Header("masterMaxUpstairsFee","服务商≤7楼搬楼费","string",12));
        headerMap.put("masterPickUpGoodsFee",new Header("masterPickUpGoodsFee","服务商提货费","string",12));
        headerMap.put("masterInsuranceFee",new Header("masterInsuranceFee","服务商保价费","string",12));
        headerMap.put("masterServiceDebit",new Header("masterServiceDebit","服务商的服务商扣款","string",12));
        headerMap.put("masterReservationRate",new Header("masterReservationRate","服务商预约超时费","string",12));
        headerMap.put("masterInstallationRate",new Header("masterInstallationRate","服务商安装超时费","string",12));
        headerMap.put("masterInstallFee",new Header("masterInstallFee","服务商异常责任安装费","string",12));
        headerMap.put("masterUpstairsFee",new Header("masterUpstairsFee","服务商异常责任上楼费","string",12));
        headerMap.put("masterDropGoodFee",new Header("masterDropGoodFee","服务商弃货费","string",12));
        headerMap.put("masterRepairFee",new Header("masterRepairFee","服务商维修费","string",12));
        headerMap.put("masterAbnRepairFee",new Header("masterAbnRepairFee","服务商异常维修费","string",12));
        headerMap.put("masterBackFee",new Header("masterBackFee","服务商返货费","string",12));
        headerMap.put("masterAbnBackFee",new Header("masterAbnBackFee","服务商异常返货费","string",12));
        headerMap.put("masterPartFee",new Header("masterPartFee","服务商补件费","string",12));
        headerMap.put("masterAbnRecoveryFee",new Header("masterAbnRecoveryFee","服务商异常追偿责任费","string",12));
        headerMap.put("masterAbnCollectionServiceFee",new Header("masterAbnCollectionServiceFee","服务商异常代收货款手续费","string",12));
        headerMap.put("masterWoodenFee",new Header("masterWoodenFee","服务商打木架成费","string",12));
        headerMap.put("masterOtherFee",new Header("masterOtherFee","服务商其他费","string",12));
        headerMap.put("totalMasterAbnFee",new Header("totalMasterAbnFee","服务商异常合计","string",12));

        headerMap.put("masterAbnDeliveryFee",new Header("masterAbnDeliveryFee","服务商异常送货成本","string",12));
        headerMap.put("masterAbnAdvanceFee",new Header("masterAbnAdvanceFee","服务商异常垫付成本","string",12));
        headerMap.put("masterAbnExceedSquareFee",new Header("masterAbnExceedSquareFee","服务商异常超方成本","string",12));
        headerMap.put("masterAbnExceedAreaFee",new Header("masterAbnExceedAreaFee","服务商异常超区成本","string",12));
        headerMap.put("masterAbnDismountingFee",new Header("masterAbnDismountingFee","服务商异常拆装成本","string",12));
        headerMap.put("masterAbnHoistingBuildFee",new Header("masterAbnHoistingBuildFee","服务商异常吊楼成本","string",12));
        headerMap.put("masterAbnTranslationalFee",new Header("masterAbnTranslationalFee","服务商异常平移成本","string",12));
        headerMap.put("masterAbnEmptyRunFee",new Header("masterAbnEmptyRunFee","服务商异常空跑成本","string",12));
        headerMap.put("masterAbnSpecialAreaFee",new Header("masterAbnSpecialAreaFee","服务商异常特殊区域成本","string",12));
        headerMap.put("masterAbnStorageFee",new Header("masterAbnStorageFee","服务商异常仓储成本","string",12));
        headerMap.put("masterAbnUrgentFee",new Header("masterAbnUrgentFee","服务商异常加急成本","string",12));
        headerMap.put("masterAbnSecondDoorFee",new Header("masterAbnSecondDoorFee","服务商异常二次上门成本","string",12));
        headerMap.put("masterAbnMinUpstairsFee",new Header("masterAbnMinUpstairsFee","服务商异常＞7楼搬楼成本","string",12));
        headerMap.put("masterAbnMaxUpstairsFee",new Header("masterAbnMaxUpstairsFee","服务商异常≤7楼搬楼成本","string",12));
        headerMap.put("masterAbnPickUpGoodsFee",new Header("masterAbnPickUpGoodsFee","服务商异常提货成本","string",12));
        headerMap.put("masterAbnInsuranceFee",new Header("masterAbnInsuranceFee","服务商异常保价成本","string",12));
        headerMap.put("masterAbnServiceDebit",new Header("masterAbnServiceDebit","服务商异常的服务商异常扣款","string",12));
        headerMap.put("masterAbnReservationRate",new Header("masterAbnReservationRate","服务商异常预约超时成本","string",12));
        headerMap.put("masterAbnInstallationRate",new Header("masterAbnInstallationRate","服务商异常安装超时成本","string",12));
        headerMap.put("masterAbnInstallFee",new Header("masterAbnInstallFee","服务商异常安装成本","string",12));
        headerMap.put("masterAbnUpstairsFee",new Header("masterAbnUpstairsFee","服务商异常上楼成本","string",12));
        headerMap.put("masterAbnDropGoodFee",new Header("masterAbnDropGoodFee","服务商异常弃货成本","string",12));
        headerMap.put("masterRepairFee2",new Header("masterRepairFee","服务商维修成本","string",12));
        headerMap.put("masterAbnRepairFee2",new Header("masterAbnRepairFee","服务商异常维修成本","string",12));
        headerMap.put("masterBackFee2",new Header("masterBackFee","服务商返货成本","string",12));
        headerMap.put("masterAbnBackFee2",new Header("masterAbnBackFee","服务商异常返货成本","string",12));
        headerMap.put("masterAbnPartFee",new Header("masterAbnPartFee","服务商异常补件成本","string",12));
        headerMap.put("masterAbnCollectionServiceFee2",new Header("masterAbnCollectionServiceFee2","发货人异常代收货款手续成本","string",12));
        headerMap.put("masterAbnWoodenFee",new Header("masterAbnWoodenFee","服务商异常打木架成本","string",12));
        headerMap.put("masterAbnOtherFee",new Header("masterAbnOtherFee","服务商异常其他成本","string",12));
        headerMap.put("totalMasterAbnCompensationFee",new Header("totalMasterAbnCompensationFee","服务商异常补偿合计","string",12));

        headerMap.put("abnPraiseFee",new Header("abnPraiseFee","师傅好评费","string",12));
        headerMap.put("abnPartialRefundFee",new Header("abnPartialRefundFee","商家部分退款费","string",12));

        headerMap.put("abnGrossProfit",new Header("abnGrossProfit","异常费毛利","string",12));
    }

}