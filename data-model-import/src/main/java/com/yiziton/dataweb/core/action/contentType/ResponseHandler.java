package com.yiziton.dataweb.core.action.contentType;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description:
 * @Author: 菠菜头
 * @Date: 2018-06-01 15:50
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public abstract class ResponseHandler {
    public ResponseHandler() {
        HttpHandlerFactory.registryResponseHandler(getType(),this);
    }

    public abstract String getType();
    public abstract Object doResponse(HttpServletResponse response, Object result) throws Exception;
}
