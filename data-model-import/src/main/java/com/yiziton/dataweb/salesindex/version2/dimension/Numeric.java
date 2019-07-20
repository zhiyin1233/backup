package com.yiziton.dataweb.salesindex.version2.dimension;

import com.yiziton.dataweb.salesindex.SalesIndexController;
import com.yiziton.dataweb.salesindex.pojo.SalesIndexDO;
import com.yiziton.dataweb.salesindex.pojo.SalesIndexDimension;

/**
 * sum数字(费用,单数)
 *
 * @author HuangHuai on 2019-07-20 17:11
 */

public class Numeric extends BaseDimension {

    SalesIndexDimension salesIndexDimension = new SalesIndexDimension();

    @Override
   public Object compute(SalesIndexDO indexDO) {
        salesIndexDimension.take_goods_fee += (indexDO.take_goods_fee == null ? 0 : indexDO.take_goods_fee);
        salesIndexDimension.transport_fee += (indexDO.transport_fee == null ? 0 : indexDO.transport_fee);
        salesIndexDimension.delivery_fee += (indexDO.delivery_fee == null ? 0 : indexDO.delivery_fee);
        salesIndexDimension.install_fee += (indexDO.install_fee == null ? 0 : indexDO.install_fee);
        salesIndexDimension.insurance_fee += (indexDO.insurance_fee == null ? 0 : indexDO.insurance_fee);
        salesIndexDimension.upstairs_fee += (indexDO.upstairs_fee == null ? 0 : indexDO.upstairs_fee);
        salesIndexDimension.entry_home_fee += (indexDO.entry_home_fee == null ? 0 : indexDO.entry_home_fee);
        salesIndexDimension.door_fee += (indexDO.door_fee == null ? 0 : indexDO.door_fee);
        salesIndexDimension.exceed_square_fee += (indexDO.exceed_square_fee == null ? 0 : indexDO.exceed_square_fee);
        salesIndexDimension.wooden_fee += (indexDO.wooden_fee == null ? 0 : indexDO.wooden_fee);
        salesIndexDimension.exceed_area_fee += (indexDO.exceed_area_fee == null ? 0 : indexDO.exceed_area_fee);
        salesIndexDimension.large_extra_fee += (indexDO.large_extra_fee == null ? 0 : indexDO.large_extra_fee);
        salesIndexDimension.marble_fee += (indexDO.marble_fee == null ? 0 : indexDO.marble_fee);
        salesIndexDimension.other_fee += (indexDO.other_fee == null ? 0 : indexDO.other_fee);
        salesIndexDimension.increment_service_fee += (indexDO.increment_service_fee == null ? 0 : indexDO.increment_service_fee);
        salesIndexDimension.coupon_relief += (indexDO.coupon_relief == null ? 0 : indexDO.coupon_relief);
        salesIndexDimension.gift_relief += (indexDO.gift_relief == null ? 0 : indexDO.gift_relief);
        salesIndexDimension.cash_relief += (indexDO.cash_relief == null ? 0 : indexDO.cash_relief);
        salesIndexDimension.client_fee += (indexDO.client_fee == null ? 0 : indexDO.client_fee);
        salesIndexDimension.total_num_of_waybills++;
        salesIndexDimension.total_billing_income += (indexDO.billing_income == null ? 0 : indexDO.billing_income);
        return null;
    }
}
