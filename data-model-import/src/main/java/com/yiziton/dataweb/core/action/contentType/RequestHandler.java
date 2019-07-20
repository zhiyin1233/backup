package com.yiziton.dataweb.core.action.contentType;

import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 相应请求处理
 * @Author: xiaoHong
 * @Date: 2018-06-01 15:52
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public abstract class RequestHandler {
    public RequestHandler() {
        HttpHandlerFactory.registryRequestHandler(getType(),this);
    }

    public abstract String getType();
    public abstract Object doRequest(HttpServletRequest request, Class actionType, String actionMethod, ApplicationContext context) throws Exception;
}
