package com.yiziton.dataweb.core.action.contentType.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yiziton.dataweb.core.action.contentType.RequestHandler;
import com.yiziton.dataweb.core.context.Context;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description: Json请求处理
 * @Author: xiaoHong
 * @Date: 2018-06-01 15:53
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Component
public class JsonRequest extends RequestHandler {

    private Logger logger = LoggerFactory.getLogger(JsonRequest.class);

    @Override
    public String getType() {
        return "json";
    }

    @Override
    public Object doRequest(HttpServletRequest request, Class actionType, String actionMethod, ApplicationContext context) throws Exception {

        Object action = context.getBean(actionType);
        String argStr = IOUtils.toString(request.getInputStream(), "UTF-8");
        String threadId = (String)Context.getContext().get("threadId");
        logger.info("action request start,threadId:"+threadId+",action:" + actionMethod + ",args:" + argStr);
        JSONArray args = JSON.parseArray(argStr);
        Stream<Method> methodStream = Arrays.asList(actionType.getMethods()).stream().filter((method) -> {
            return method.getName().equals(actionMethod);
        }).filter((method) -> {
            return method.getParameterCount() == args.size();
        });
        List<Method> methods = methodStream.collect(Collectors.toList());
        for(Method method:methods){
            Object[] methodArgs = new Object[method.getParameterCount()];
            try{
                for (int i = 0; i < args.size(); i++) {
                    Object arg = args.get(i);
                    methodArgs[i] = JSON.parseObject(JSON.toJSONString(arg),method.getParameterTypes()[i]);
                }
            }catch (Throwable e){
                continue;
            }

            Object result = method.invoke(action, methodArgs);
            logger.info("action request end,threadId:" + threadId);
            return result;
        }
        throw new IllegalStateException("No matching action methods can be executed");
    }
}
