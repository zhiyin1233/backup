package com.yiziton.dataweb.core.action.contentType;

import com.yiziton.dataweb.core.action.contentType.json.JsonRequest;
import com.yiziton.dataweb.core.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @Description: 文件上传请求处理
 * @Author: xiaoHong
 * @Date: 2018-06-01 15:53
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Component
public class MultipartRequest extends RequestHandler {

    private Logger logger = LoggerFactory.getLogger(JsonRequest.class);

    @Override
    public String getType() {
        return "multipart";
    }

    @Override
    public Object doRequest(HttpServletRequest request, Class actionType, String actionMethod, ApplicationContext context) throws Exception {
        Object action = context.getBean(actionType);
        Object arg = null;
        String threadId = (String)Context.getContext().get("threadId");

        if(request.getParameterMap() != null && request.getParameterMap().get("uploadDomain") != null){
            arg = request.getParameterMap().get("uploadDomain")[0];
        }
        logger.info("multi action request start,threadId:"+threadId+",action:" + actionMethod + ",arg:" + arg);

        MultipartHttpServletRequest multipartRequest =
                WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
        List<MultipartFile> list = multipartRequest.getFiles("file");
        Object result = null;
        if(arg != null){
            Method method = actionType.getMethod(actionMethod,List.class,String.class);
            result = method.invoke(action,list,arg);
        }else{
            Method method = actionType.getMethod(actionMethod,List.class);
            result = method.invoke(action,list);
        }
        logger.info("multi action request end,threadId:"+threadId);
        return result;
    }

}
