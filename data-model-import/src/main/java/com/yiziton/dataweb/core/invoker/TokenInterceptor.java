package com.yiziton.dataweb.core.invoker;

import com.yiziton.dataweb.core.oauth2.ThreadToken;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/9/18
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class TokenInterceptor implements ClientHttpRequestInterceptor
{
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException
    {

        String token = "bearer "+ThreadToken.token.get();
        //将令牌放入请求header中
        request.getHeaders().add("Authorization",token);

        return execution.execute(request, body);
    }
}