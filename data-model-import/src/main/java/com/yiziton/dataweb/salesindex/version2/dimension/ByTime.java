package com.yiziton.dataweb.salesindex.version2.dimension;

import com.yiziton.dataweb.salesindex.CommonUtil;
import com.yiziton.dataweb.salesindex.DateUtil;
import com.yiziton.dataweb.salesindex.pojo.SalesIndexDO;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 按时间统计所有，时间可以是日 月 年
 *
 * @author HuangHuai on 2019-07-20 17:11
 */
@Component
public class ByTime extends BaseDimension {
    private static Logger log = LoggerFactory.getLogger(ByTime.class);
    Map<String, Numeric> everyDayNumericStatistic = new HashMap<>();


    @Override
    public Object compute(SalesIndexDO indexDO) {
        if (indexDO.open_bill_time == null) {
            log.error("没有开单时间：" + indexDO.waybill_id);
            return null;
        }

        String yearMonthDay = DateUtil.SHORT_FORMAT2.format(indexDO.open_bill_time);
        Numeric numeric = everyDayNumericStatistic.get(yearMonthDay);
        if (numeric == null) {
            everyDayNumericStatistic.put(yearMonthDay, numeric = new Numeric());
        }
        numeric.compute(indexDO);
        return null;
    }
}
