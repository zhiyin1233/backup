package com.yiziton.dataimport.waybill.dao.ext;

import com.yiziton.dataimport.waybill.bean.SysConfigInfo;
import com.yiziton.dataimport.waybill.dao.ReceviceInfoMapper;
import org.apache.ibatis.annotations.Param;

public interface SysConfigInfoExtMapper extends ReceviceInfoMapper {

    SysConfigInfo selectByConfigKey(@Param("configKey") String configKey);

}