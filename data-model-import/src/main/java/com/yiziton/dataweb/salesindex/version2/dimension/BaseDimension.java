package com.yiziton.dataweb.salesindex.version2.dimension;

import com.yiziton.dataweb.salesindex.pojo.SalesIndexDO;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author HuangHuai on 2019-07-20 17:26
 */
public abstract class BaseDimension {

    Date start;
    Date end;

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public SalesIndexDO salesIndexDO = new SalesIndexDO();

    public BaseDimension() {
        register(this);
    }

    private synchronized void register(BaseDimension dimension) {
        String key = dimension.getClass().getName();
        if (!ComputeUtil.dimensions.containsKey(key)) {
            ComputeUtil.dimensions.put(key, dimension);
        }
    }

    public abstract Object compute(SalesIndexDO indexDO);
}
