package com.yiziton.dataweb.salesindex.version2.dimension;

import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;
import com.yiziton.dataweb.salesindex.pojo.SalesIndexDO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HuangHuai on 2019-07-20 17:26
 */
@Component
public class ComputeUtil implements InitializingBean {

    static Map<String,BaseDimension> dimensions = new ConcurrentHashMap<>();


    @Override
    public void afterPropertiesSet() throws Exception {
        SalesIndexDO salesIndexDO=null;

        Iterator<SalesIndexDO> it=null;

        ArrayList<BaseDimension> computeChain = new ArrayList<>(dimensions.values());
        while (it.hasNext()) {
            apply(it.next(),computeChain);
        }



        dimensions.values().forEach(e->e.compute(salesIndexDO));
    }

    private void apply(SalesIndexDO next, ArrayList<BaseDimension> computeChain) {
        for (int i = 0; i < computeChain.size(); i++) {
            computeChain.get(i).compute(next);
        }
    }
}
