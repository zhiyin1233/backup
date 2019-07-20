package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.bean.OrganizeUser;
import com.yiziton.dataimport.waybill.dao.OrganizeUserMapper;

public interface OrganizeUserExtMapper extends OrganizeUserMapper {

    OrganizeUser selectByCode(String code);
}