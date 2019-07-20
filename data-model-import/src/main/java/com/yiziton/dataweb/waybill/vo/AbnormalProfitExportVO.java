package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * @Description: 异常利润VO
 * @Author: kdh
 * @Date: 2018/12/5
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Data
public class AbnormalProfitExportVO {

    //运单号	签收时间	目的省	目的市	目的区	发货人名称	商家名称
    private String waybillId;//运单号
    private String openBillTime;//开单时间
    private String openBillNode;//开单网点
    private String signTime;//签收时间
    private String province;//目的省
    private String city;//目的市
    private String area;//目的区
    private String consignorName;//发货人名称
    private String secondCustomer;//商家名称
    private String customerType;//商家类型
    private String settlementType;//结算方式
    private String serviceType;//服务类型
    private String salesman;//业务员
    private String waybillStatus;//运单状态

    /* 流水对象是发货人，流水是负数扣师傅流水 */
    private Double consignorDeliveryFee = 0.00;//发货人送货费
    private Double consignorInstallFee2 = 0.00;//发货人安装费,无,2018.06前旧数据可能有
    private Double consignorAdvanceFee = 0.00;//发货人垫付费
    private Double consignorUpstairsFee2 = 0.00;//发货人上楼费,无,2018.06前旧数据可能有
    private Double consignorExceedSquareFee = 0.00;//发货人超方费
    private Double consignorExceedAreaFee = 0.00;//发货人超区费
    private Double consignorDismountingFee = 0.00;//发货人拆装费
    private Double consignorHoistingBuildFee = 0.00;//发货人吊楼费
    private Double consignorTranslationalFee = 0.00;//发货人平移费
    private Double consignorEmptyRunFee = 0.00;//发货人空跑费
    private Double consignorSpecialAreaFee = 0.00;//发货人特殊区域费
    private Double consignorStorageFee = 0.00;//发货人仓储费
    private Double consignorUrgentFee = 0.00;//发货人加急费
    private Double consignorSecondDoorFee = 0.00;//发货人二次上门费
    private Double consignorMinUpstairsFee = 0.00;//发货人＞7楼搬楼费
    private Double consignorMaxUpstairsFee = 0.00;//发货人≤7楼搬楼费
    private Double consignorPickUpGoodsFee = 0.00;//发货人提货费
    private Double consignorInsuranceFee = 0.00;//发货人保价费
    private Double consignorServiceDebit = 0.00;//发货人的服务商扣款
    private Double consignorReservationRate = 0.00;//发货人预约超时费
    private Double consignorInstallationRate = 0.00;//发货人安装超时费
    private Double consignorInstallFee = 0.00;//发货人异常安装费
    private Double consignorUpstairsFee = 0.00;//发货人异常上楼费
    private Double consignorDropGoodFee = 0.00;//发货人弃货费
    private Double consignorRepairFee = 0.00;//发货人维修费
    private Double consignorAbnRepairFee = 0.00;//发货人异常维修费
    private Double consignorBackFee = 0.00;//发货人返货费
    private Double consignorAbnBackFee = 0.00;//发货人异常返货费
    private Double consignorPartFee = 0.00;//发货人补件费
    private Double consignorAbnRecoveryFee = 0.00;//发货人异常追偿责任费
    private Double consignorAbnCollectionServiceFee = 0.00;//发货人异常代收货款手续费
    private Double consignorAbnWoodenFee = 0.00;//发货人异常打木架费
    private Double consignorAbnOtherFee = 0.00;//发货人异常其他费
    private Double totalConsignorAbnFee = 0.00;//发货人异常合计

    /* 流水对象是承运商，费用为负数，扣承运商的钱*/
    private Double carrierDeliveryFee = 0.00;//承运商送货费
    private Double carrierInstallFee2 = 0.00;//承运商安装费,无,2018.06前旧数据可能有
    private Double carrierAdvanceFee = 0.00;//承运商垫付费
    private Double carrierUpstairsFee2 = 0.00;//承运商上楼费,无,2018.06前旧数据可能有
    private Double carrierExceedSquareFee = 0.00;//承运商超方费
    private Double carrierExceedAreaFee = 0.00;//承运商超区费
    private Double carrierDismountingFee = 0.00;//承运商拆装费
    private Double carrierHoistingBuildFee = 0.00;//承运商吊楼费
    private Double carrierTranslationalFee = 0.00;//承运商平移费
    private Double carrierEmptyRunFee = 0.00;//承运商空跑费
    private Double carrierSpecialAreaFee = 0.00;//承运商特殊区域费
    private Double carrierStorageFee = 0.00;//承运商仓储费
    private Double carrierUrgentFee = 0.00;//承运商加急费
    private Double carrierSecondDoorFee = 0.00;//承运商二次上门费
    private Double carrierMinUpstairsFee = 0.00;//承运商＞7楼搬楼费
    private Double carrierMaxUpstairsFee = 0.00;//承运商≤7楼搬楼费
    private Double carrierPickUpGoodsFee = 0.00;//承运商提货费
    private Double carrierInsuranceFee = 0.00;//承运商保价费
    private Double carrierServiceDebit = 0.00;//承运商的服务商扣款
    private Double carrierReservationRate = 0.00;//承运商预约超时费
    private Double carrierInstallationRate = 0.00;//承运商安装超时费
    private Double carrierInstallFee = 0.00;//承运商异常安装费
    private Double carrierUpstairsFee = 0.00;//承运商异常上楼费
    private Double carrierDropGoodFee = 0.00;//承运商弃货费
    private Double carrierRepairFee = 0.00;//承运商维修费
    private Double carrierAbnRepairFee = 0.00;//承运商异常维修费
    private Double carrierBackFee = 0.00;//承运商返货费
    private Double carrierAbnBackFee = 0.00;//承运商异常返货费
    private Double carrierPartFee = 0.00;//承运商补件费
    private Double carrierAbnRecoveryFee = 0.00;//承运商异常追偿责任费
    private Double carrierAbnCollectionServiceFee = 0.00;//承运商异常代收货款手续成本
    private Double carrierAbnWoodenFee = 0.00;//承运商异常打木架费
    private Double carrierAbnOtherFee = 0.00;//承运商异常其他费
    private Double totalCarrierAbnFee = 0.00;//承运商异常合计

    /* 流水对象是服务商，流水是负数，扣师傅钱 */
    private Double masterDeliveryFee = 0.00;//服务商送货费
    private Double masterAdvanceFee = 0.00;//服务商垫付费
    private Double masterExceedSquareFee = 0.00;//服务商超方费
    private Double masterExceedAreaFee = 0.00;//服务商超区费
    private Double masterDismountingFee = 0.00;//服务商拆装费
    private Double masterHoistingBuildFee = 0.00;//服务商吊楼费
    private Double masterTranslationalFee = 0.00;//服务商平移费
    private Double masterEmptyRunFee = 0.00;//服务商空跑费
    private Double masterSpecialAreaFee = 0.00;//服务商特殊区域费
    private Double masterStorageFee = 0.00;//服务商仓储费
    private Double masterUrgentFee = 0.00;//服务商加急费
    private Double masterSecondDoorFee = 0.00;//服务商二次上门费
    private Double masterMinUpstairsFee = 0.00;//服务商＞7楼搬楼费
    private Double masterMaxUpstairsFee = 0.00;//服务商≤7楼搬楼费
    private Double masterPickUpGoodsFee = 0.00;//服务商提货费
    private Double masterInsuranceFee = 0.00;//服务商保价费
    private Double masterServiceDebit = 0.00;//服务商的服务商扣款
    private Double masterReservationRate = 0.00;//服务商预约超时费
    private Double masterInstallationRate = 0.00;//服务商安装超时费
    private Double masterInstallFee = 0.00;//服务商异常责任安装费
    private Double masterUpstairsFee = 0.00;//服务商异常责任上楼费
    private Double masterDropGoodFee = 0.00;//服务商弃货费
    private Double masterRepairFee = 0.00;//服务商维修费
    private Double masterAbnRepairFee = 0.00;//服务商异常维修费
    private Double masterBackFee = 0.00;//服务商返货费
    private Double masterAbnBackFee = 0.00;//服务商异常返货费
    private Double masterPartFee = 0.00;//服务商补件费
    private Double masterAbnRecoveryFee = 0.00;//服务商异常追偿责任费
    private Double masterAbnCollectionServiceFee = 0.00;//服务商异常代收货款手续费
    private Double masterWoodenFee = 0.00;//服务商打木架成费
    private Double masterOtherFee = 0.00;//服务商其他费
    private Double totalMasterAbnFee = 0.00;//服务商异常合计

    /* 给师傅加钱的流水，也就是服务商，流水是正数，的异常费用 */
    private Double masterAbnDeliveryFee = 0.00;//服务商异常送货成本
    private Double masterAbnAdvanceFee = 0.00;//服务商异常垫付成本
    private Double masterAbnExceedSquareFee = 0.00;//服务商异常超方成本
    private Double masterAbnExceedAreaFee = 0.00;//服务商异常超区成本
    private Double masterAbnDismountingFee = 0.00;//服务商异常拆装成本
    private Double masterAbnHoistingBuildFee = 0.00;//服务商异常吊楼成本
    private Double masterAbnTranslationalFee = 0.00;//服务商异常平移成本
    private Double masterAbnEmptyRunFee = 0.00;//服务商异常空跑成本
    private Double masterAbnSpecialAreaFee = 0.00;//服务商异常特殊区域成本
    private Double masterAbnStorageFee = 0.00;//服务商异常仓储成本
    private Double masterAbnUrgentFee = 0.00;//服务商异常加急成本
    private Double masterAbnSecondDoorFee = 0.00;//服务商异常二次上门成本
    private Double masterAbnMinUpstairsFee = 0.00;//服务商异常＞7楼搬楼成本
    private Double masterAbnMaxUpstairsFee = 0.00;//服务商异常≤7楼搬楼成本
    private Double masterAbnPickUpGoodsFee = 0.00;//服务商异常提货成本
    private Double masterAbnInsuranceFee = 0.00;//服务商异常保价成本
    private Double masterAbnServiceDebit = 0.00;//服务商异常的服务商异常扣款
    private Double masterAbnReservationRate = 0.00;//服务商异常预约超时成本
    private Double masterAbnInstallationRate = 0.00;//服务商异常安装超时成本
    private Double masterAbnInstallFee = 0.00;//服务商异常安装成本
    private Double masterAbnUpstairsFee = 0.00;//服务商异常上楼成本
    private Double masterAbnDropGoodFee = 0.00;//服务商异常弃货成本
    private Double masterRepairFee2 = 0.00;//服务维修成本
    private Double masterAbnRepairFee2 = 0.00;//服务商异常维修成本
    private Double masterBackFee2 = 0.00;//服务商返货成本
    private Double masterAbnBackFee2 = 0.00;//服务商异常返货成本
    private Double masterAbnPartFee = 0.00;//服务商异常补件成本
    private Double masterAbnCollectionServiceFee2 = 0.00;//发货人异常代收货款手续成本
    private Double masterAbnWoodenFee = 0.00;//服务商异常打木架成本
    private Double masterAbnOtherFee = 0.00;//服务商异常其他成本
    private Double totalMasterAbnCompensationFee = 0.00;//服务商异常补偿合计

    private Double abnPraiseFee = 0.00;//师傅好评费
    private Double abnPartialRefundFee = 0.00;//商家部分退款

    /* 异常费毛利 */
    private Double abnGrossProfit = 0.00;//异常费毛利
}
