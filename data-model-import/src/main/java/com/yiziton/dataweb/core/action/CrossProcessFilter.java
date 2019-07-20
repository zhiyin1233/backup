/*
 * Copyright 2018 1ziton.com.
 **/
package com.yiziton.dataweb.core.action;

import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 跨域处理拦截器
 *
 * @author xiaoHong
 * @create 2018-09-10
 **/
@Component
@WebFilter(urlPatterns = {"/action/*","/websocket/*"},filterName = "CrossProcessFilter")
public class CrossProcessFilter implements Filter {

    private final List<String> allowedOrigins = Arrays.asList("http://localhost:18006","http://localhost:8081","http://localhost:18004","http://192.168.100.20:8002","https://uatwms.1ziton.com","https://wms.1ziton.com");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse){
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            HttpServletRequest request = (HttpServletRequest) servletRequest;

            String method = request.getMethod();
            String requestUri = request.getRequestURI();

            if(requestUri.startsWith("/action/")){
                if(((HttpServletRequest) servletRequest).getHeader("zuul-user-id") == null){
                    response.setHeader("Access-Control-Allow-Origin", "*");
                    response.setHeader("Access-Control-Allow-Methods", "*");
                    response.setHeader("Access-Control-Max-Age", "1728000");
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept,Authorization");
                }

                if("OPTIONS".equalsIgnoreCase(method)){
                    return;
                }
            }else if(requestUri.startsWith("/websocket/")){
                // Access-Control-Allow-Origin
                String origin = request.getHeader("Origin");
                response.setHeader("Access-Control-Allow-Origin", allowedOrigins.contains(origin) ? origin : "");
                response.setHeader("Vary", "Origin");

                // Access-Control-Max-Age
                response.setHeader("Access-Control-Max-Age", "1728000");

                // Access-Control-Allow-Credentials
                response.setHeader("Access-Control-Allow-Credentials", "true");

                // Access-Control-Allow-Methods
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");

                // Access-Control-Allow-Headers
                response.setHeader("Access-Control-Allow-Headers",
                        "Origin, X-Requested-With, Content-Type, Accept, " + "X-CSRF-TOKEN");

            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }
}