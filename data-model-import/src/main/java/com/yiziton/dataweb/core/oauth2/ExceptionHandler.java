/*
 * Copyright 2018 1ziton.com.
 **/
package com.yiziton.dataweb.core.oauth2;

import com.alibaba.fastjson.JSON;
import com.yiziton.dataweb.core.action.ActionResponse;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统一异常处理
 *
 * @author xiaoHong
 * @create 2018-03-09
 **/
public class ExceptionHandler {
    public void handle(HttpServletRequest request,
                         HttpServletResponse response,
                         Exception e){
        try{
            ActionResponse actionResponse = new ActionResponse();
            if(e instanceof AccessDeniedException){
                actionResponse.setResultCode("401");
                actionResponse.setMsg(ExceptionUtils.builtErrorText("认证异常",e.getMessage()));
                actionResponse.setCause(e.getMessage());
            }else if(e instanceof AuthenticationException){
                actionResponse.setResultCode("401");
                actionResponse.setMsg(ExceptionUtils.builtErrorText("认证异常",e.getMessage()));
                actionResponse.setCause(e.getMessage());
            }else if(e instanceof InsufficientAuthenticationException){
                actionResponse.setResultCode("401");
                actionResponse.setMsg(ExceptionUtils.builtErrorText("认证异常",e.getMessage()));
                actionResponse.setCause(e.getMessage());
            }else if(e instanceof MultipartException){
                System.out.println("文件上传异常了.");
                String msg =null;
                if(e.getCause()!=null && e.getCause().getCause() instanceof FileUploadBase.FileSizeLimitExceededException){
                    msg = "文件上传超过限制大小:10M";
                }else{
                    msg = "文件上传异常";
                }
                actionResponse.setResultCode("502");
                actionResponse.setMsg(ExceptionUtils.builtErrorText(msg,e.getMessage()));
                actionResponse.setCause(e.getMessage());
            }else{
                actionResponse.setResultCode("502");
                actionResponse.setMsg(ExceptionUtils.builtErrorText("调用异常",e.getMessage()));
                actionResponse.setCause(e.getMessage());
            }

            String responseJSON = JSON.toJSONString(actionResponse);

            if(response.getHeader("Access-Control-Allow-Origin")==null){
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Methods", "*");
                response.setHeader("Access-Control-Max-Age", "1700000");
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept,Authorization");
            }

            response.setContentType("application/javascript;charset=utf-8");
            response.setHeader("Pragma","No-cache");
            response.setHeader("Cache-Control","no-cache");
            response.setDateHeader("Expires", 0);
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().write(responseJSON);
            response.getWriter().flush();
        }catch(Exception ex){ex.printStackTrace();}
    }
}