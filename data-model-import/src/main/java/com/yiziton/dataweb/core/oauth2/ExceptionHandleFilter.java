/*
 * Copyright 2018 1ziton.com.
 **/
package com.yiziton.dataweb.core.oauth2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 异常处理
 *
 * @author xiaoHong
 * @create 2018-03-09
 **/
public class ExceptionHandleFilter implements Filter {
    private static final Log logger = LogFactory.getLog(ExceptionHandleFilter.class);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.debug(String.format("ExceptionHandleFilter start "));
        try{
            filterChain.doFilter(servletRequest,servletResponse);
        }catch(Exception e){
            logger.error(((HttpServletRequest)servletRequest).getRequestURI()+" 出现异常：{}",e);
            ExceptionHandler exceptionHandler = new ExceptionHandler();
            exceptionHandler.handle((HttpServletRequest)servletRequest,(HttpServletResponse)servletResponse,e);
        }
        logger.debug(String.format("ExceptionHandleFilter end "));

    }

    @Override
    public void destroy() {

    }
}