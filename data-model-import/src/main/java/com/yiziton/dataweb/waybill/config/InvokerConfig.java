package com.yiziton.dataweb.waybill.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/9/26
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Configuration
public class InvokerConfig {
    @Value("${remote.requestTimeOut}")
    private int requestTimeOut;
    @Value("${remote.connectTimeOut}")
    private int connectTimeOut;
    @Value("${remote.readTimeOut}")
    private int readTimeOut;


    public RestTemplate getRestTemplate(){
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(requestTimeOut);
        httpRequestFactory.setConnectTimeout(connectTimeOut);
        httpRequestFactory.setReadTimeout(readTimeOut);
        return new RestTemplate(httpRequestFactory);
    }
}