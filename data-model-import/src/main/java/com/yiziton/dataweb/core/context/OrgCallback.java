package com.yiziton.dataweb.core.context;


import com.yiziton.dataweb.core.invoker.RemoteInvoker;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 加载组织信息
 * @Author: xiaoHong
 * @Date: 2018-05-09 16:40
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class OrgCallback implements Callback{

    @Override
    public void loadData(String key) {
        System.out.println("--->延迟加载组织信息");
        Map orgUser = (Map) Context.getContext().get("orgUser");
        if(orgUser!=null && !orgUser.isEmpty()){
            String organizeCode = (String)orgUser.get("organizeCode");
            Map param = new HashMap();
            param.put("code",organizeCode);
            Map organize = (Map)RemoteInvoker.invoke(RemoteInvoker.SCM_URL+"/api/organize/getOrganizeByCode",param);
            Context.put(key,organize);

            //网点
            Map outlet = (Map)RemoteInvoker.invoke(RemoteInvoker.SCM_URL+"/api/organize/getCurrentOutletsByOrganizeCode",organizeCode);
            Context.put("outlet",outlet);
        }else{
//            throw new BusinessException("获取组织编码失败");
        }
    }
}
