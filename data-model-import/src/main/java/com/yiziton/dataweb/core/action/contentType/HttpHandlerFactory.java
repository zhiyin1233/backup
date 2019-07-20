package com.yiziton.dataweb.core.action.contentType;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Description:
 * @Author: 菠菜头
 * @Date: 2018-06-01 15:59
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class HttpHandlerFactory {


    private static Map<String,RequestHandler> requestHandlerMap = new HashMap<>();
    private static Map<String,ResponseHandler> responseHandlerMap = new HashMap<>();

    public RequestHandler getRequestHandler(HttpServletRequest request){
        String contentType = request.getContentType();
        String requestHandlerType;
        if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
            requestHandlerType = "multipart";
        }else{
            requestHandlerType = "json";
        }
        return Optional.ofNullable(requestHandlerMap.get(requestHandlerType)).orElse(requestHandlerMap.get("json"));
    }

    public ResponseHandler getResponseHandler(String type){
        return Optional.ofNullable(responseHandlerMap.get(type)).orElse(responseHandlerMap.get("json"));
    }

    public static void registryResponseHandler(String key, ResponseHandler responseHandler){
        responseHandlerMap.put(key,responseHandler);
    }

    public static void registryRequestHandler(String key, RequestHandler requestHandler){
        requestHandlerMap.put(key,requestHandler);
    }
}
