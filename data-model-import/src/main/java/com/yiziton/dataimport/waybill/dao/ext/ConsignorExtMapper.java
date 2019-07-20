package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.bean.Consignor;
import com.yiziton.dataimport.waybill.dao.ConsignorMapper;

public interface ConsignorExtMapper extends ConsignorMapper {

    Consignor selectByCode(String code);

    int update(Consignor record);

}