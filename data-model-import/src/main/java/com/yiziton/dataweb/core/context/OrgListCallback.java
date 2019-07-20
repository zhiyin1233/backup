package com.yiziton.dataweb.core.context;

import com.alibaba.fastjson.JSONArray;
import com.yiziton.dataweb.core.invoker.RemoteInvoker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 加载所属组织下3层级别信息
 * @Author: xiaoHong
 * @Date: 2018-05-09 16:40
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class OrgListCallback implements Callback{

    //todo :获取对应组织信息需要优化
    @Override
    public void loadData(String key) {
        System.out.println("--->延迟加载组织信息");
        Map orgUser = (Map) Context.getContext().get("orgUser");
        if(orgUser!=null && !orgUser.isEmpty()){
            String organizeCode = (String)orgUser.get("organizeCode");
            Map param = new HashMap();
            param.put("code",organizeCode);
            param.put("level",3);
            JSONArray organizeList = (JSONArray)RemoteInvoker.invoke(RemoteInvoker.SCM_URL+"/api/organize/getOrganizesByCodeAndLevel",param);
            List<Map> organizes = organizeList.toJavaList(Map.class);
            List<String> simpleOrgs = new ArrayList<>();
            for(Map organize : organizes){
                simpleOrgs.add((String)organize.get("code"));
            }
            Context.put(key,organizeList.toJavaList(Map.class));
            Context.put("organizeCodes",simpleOrgs);
        }else{
//            throw new BusinessException("获取组织编码失败");
        }
    }
}