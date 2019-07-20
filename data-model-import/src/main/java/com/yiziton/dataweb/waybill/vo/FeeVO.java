package com.yiziton.dataweb.waybill.vo;

import lombok.Data;

/**
 * @Description:
 * @Author: kdh
 * @Date: 2018/12/5
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Data
public class FeeVO {

    private String waybillId;//运单号

    /* 开单费用 */
    private Double agingServiceFee;//时效服务费
    private Double cashRelief;//返现金
    private Double couponRelief;//优惠卷
    private Double deliveryFee;//送货费
    private Double doorFee;//上门服务费"
    private Double entryHomeFee;//入户费
    private Double exceedAreaFee;//超区费
    private Double exceedSquareFee;//超方费
    private Double giftRelief;//赠送金
    private Double installFee;//安装费
    private Double insuranceFee;//保价费
    private Double marbleFee;//大理石费
    private Double mediateFee;//调解费
    private Double otherFee;//其他费
    private Double outDeliverCost;//外发成本
    private Double pickUpNodeFee;//提点费
    private Double promotionFee;//干支促销费
    private Double takeGoodsFee;//揽货费
    private Double transportFee;//运费
    private Double upstairsFee;//上楼费
    private Double collectionServiceFee;//代收货款手续费
    private Double woodenFee = 0.00;//打木架费
    private Double largeExtraFee = 0.00;//大票附加费

    /* 正常费用单不是开单时产生 */
    private Double repairFee;//正常维修费
    private Double backFee;//正常返货费

    /* 追赔费用 */
    private Double addFee;//追赔

    /* 理赔费用 */
    private Double accommodation;//通融金额
    private Double claimFee;//理赔
    private Double dribbletAddFee;//小额理赔

    /* 异常处理费用 */
    private Double abnAdvanceFee;//垫付费
    private Double abnAllocateFee;//调货费
    private Double abnAppeaseFee;//安抚费
    private Double abnBackFee;//异常返货费
    private Double abnDeliveryFee;//送货费
    private Double abnDismountingFee;//拆装费
    private Double abnDropGoodFee;//弃货费
    private Double abnEmptyRunFee;//空跑费
    private Double abnExceedAreaFee;//超区费
    private Double abnExceedSquareFee;//超方费
    private Double abnFloorFee;//落地费
    private Double abnHoistingBuildFee;//吊楼费
    private Double abnInstallationRate;//安装超时费
    private Double abnInstallFee;//安装费
    private Double abnInsuranceFee;//保价费
    private Double abnRecoveryFee;//追偿责任费
    private Double abnMaxUpstairsFee;//<=7楼搬楼费
    private Double abnMinUpstairsFee;//>7楼搬楼费
    private Double abnMoveBuildFee;//搬楼费
    private Double abnPackFee;//包装费
    private Double abnPartFee;//补件费
    private Double abnPickUpGoodsFee;//提货费
    private Double abnPutawayFee;//上架费
    private Double abnRepairFee;//异常维修费
    private Double abnReservationRate;//预约超时费
    private Double abnSecondDoorFee;//二次上门费
    private Double abnServiceDebit;//服务商扣款费
    private Double abnSpecialAreaFee;//特殊区域费
    private Double abnStorageFee;//仓储费
    private Double abnTransferFee;//中转费
    private Double abnTranslationalFee;//平移费
    private Double abnTransportFee;//运费
    private Double abnUnloadFee;//卸货费
    private Double abnUpstairsFee;//上楼费
    private Double abnUrgentFee;//加急费
    private Double abnPraiseFee;//异常好评费
    private Double abnPartialRefundFee;//异常部分退款费
    private Double abnCollectionServiceFee;//异常代收货款手续费
    private Double abnWoodenFee = 0.00;//异常打木架费
    private Double abnOtherFee;//异常其他费

    //private Double collectionOnDeliveryFee;//代收货款//该费用不需要展示在明细中
    //private Double foreightAtDestinationFee;//到付运费//该费用不需要展示在明细中

    private Double total;//合计
}