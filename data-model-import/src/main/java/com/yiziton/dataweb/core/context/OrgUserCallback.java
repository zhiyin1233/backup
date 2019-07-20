package com.yiziton.dataweb.core.context;



import com.yiziton.dataweb.core.exception.DomainException;
import com.yiziton.dataweb.core.invoker.RemoteInvoker;
import com.yiziton.dataweb.core.oauth2.User;

import java.util.Map;
import java.util.Optional;

/**
 * @Description: 加载组织用户信息
 * @Author: xiaoHong
 * @Date: 2018-05-09 16:40
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class OrgUserCallback implements Callback{

    @Override
    public void loadData(String key) {
        System.out.println("--->延迟加载组织用户信息");

        String userCode = (String) Optional.ofNullable(((User)Context.getContext().get(Context.USER)).getUserCode()).orElseThrow(()-> new DomainException(500,"获取用户信息失败"));
        //初始化组织信息
        Map orgUser = (Map) RemoteInvoker.invoke(RemoteInvoker.SCM_URL+"/api/organize/queryNormalOrgUserByUserCode",userCode);
        if(orgUser!=null && !orgUser.isEmpty()){
            Context.put(key,orgUser);
        }else{
            System.out.println("获取组织用户失败");
        }
    }
}
