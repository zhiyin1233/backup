package com.yiziton.dataweb.core.action;

import com.alibaba.fastjson.JSON;

import com.yiziton.dataweb.waybill.config.Constants;
import com.yiziton.dataweb.core.action.contentType.HttpHandlerFactory;
import com.yiziton.dataweb.core.action.contentType.RequestHandler;
import com.yiziton.dataweb.core.action.contentType.ResponseHandler;
import com.yiziton.dataweb.core.annotation.Action;
import com.yiziton.dataweb.core.base.Lang;
import com.yiziton.dataweb.core.context.Context;
import com.yiziton.dataweb.core.exception.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.PatternSyntaxException;


public class ActionExporter implements HttpRequestHandler, ApplicationContextAware, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;

    private String basePackage;

    private Map<String,Class> actionMap = new ConcurrentHashMap<>();

    private HttpHandlerFactory httpHandlerFactory = new HttpHandlerFactory();

    private static int max = 5;
    private static AtomicInteger maxConcurrent = new AtomicInteger(max);

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        //全局上下文初始化
        initContext();
        Long startTime = Calendar.getInstance().getTimeInMillis();
        String threadId = (String)Context.getContext().get("threadId");

        boolean exportFlag = false;

        String responseCode = null;
        ActionResponse actionResponse = new ActionResponse();

        try {

            String path = request.getServletPath();
            path = path.substring(Constants.ACTION_URL_MAPPING.lastIndexOf("/"));
            path = path.replaceAll("(^/+)|(/+$)", "");
            String[] paths = path.split("/");
            if (paths.length != 2) {
                throw new IllegalStateException(String.format("The action path(%s) is invalid", request.getServletPath()));
            }
            String actionName = paths[0];
            String actionMethod = paths[1];

            String[] action = actionMethod.split("\\.");
            actionMethod = action[0];
            responseCode = action.length>1?action[action.length-1]:"json";


            if(responseCode.endsWith("excel") || responseCode.endsWith("csv")) {
                int count = maxConcurrent.decrementAndGet();
                exportFlag = true;
                if (count < 0) {
                    maxConcurrent.incrementAndGet();
                    throw new Exception("此刻等待报表导出的人太多,请稍后等下再试");
                }
            }


            Class actionType = actionMap.get(actionName);
            if (actionType == null) {
                throw new IllegalStateException(String.format("The action %s is not exists", actionName));
            }

            RequestHandler requestHandler = httpHandlerFactory.getRequestHandler(request);
            Object result = requestHandler.doRequest(request,actionType,actionMethod,applicationContext);
            actionResponse.setContent(result);
            actionResponse.setResultCode("200");
            ResponseHandler responseHandler = httpHandlerFactory.getResponseHandler(responseCode);
            responseHandler.doResponse(response,actionResponse);

        } catch (Throwable e) {
            logger.error(String.format("ThreadId %s,its action of path %s execute failure",threadId,request.getServletPath()),e);
            try(StringWriter errorStack = new StringWriter()){
                if(e.getCause() instanceof DomainException){
                    e = Lang.getCause(e);
                    e.printStackTrace(new PrintWriter(errorStack));
                    actionResponse.setResultCode(((Integer)((DomainException)e).getErrorCode()).toString());
                    actionResponse.setMsg(e.getMessage());
                    actionResponse.setContent(null);
                    response.getWriter().write(JSON.toJSONString(actionResponse));
                }
                else {
                    e = Lang.getCause(e);
                    if(e instanceof PatternSyntaxException){
                        actionResponse.setMsg("存在特殊字符，如'()*:;$'等，请去掉特殊字符");
                    }else{
                        actionResponse.setMsg(e.getMessage());
                    }
                    e.printStackTrace(new PrintWriter(errorStack));
                    actionResponse.setResultCode("500");
                    actionResponse.setContent(null);
                    actionResponse.setCause(parseError(e));
                    response.getWriter().write(JSON.toJSONString(actionResponse));
                }
            }
        }finally {
            if(exportFlag){
                int count = maxConcurrent.incrementAndGet();
                logger.info("已释放导出令牌,还有【{}】个导出令牌",count);
            }
            Context.clear();
            Long endTime = Calendar.getInstance().getTimeInMillis();
            logger.info("threadId:" + threadId + ",cost time:" + (endTime-startTime));
        }

    }

    private String parseError(Throwable e){
        String error = "error[%s];file[%s];method[%s];lineNumber[%s]";
        StackTraceElement[] elements = e.getStackTrace();
        StackTraceElement top = elements[0];
        String className = top.getClassName();
        if(className.indexOf(".")>0){
            className = className.substring(className.lastIndexOf(".")+1);
        }
        error = String.format(error,e.getClass().getSimpleName(),className,top.getMethodName(),top.getLineNumber());
        return error;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.basePackage, "basePackage should not be null");
        String[] packageNames = StringUtils.tokenizeToStringArray(this.basePackage, ",; \t\n");
        for (String packageName : packageNames) {
            Set<Class> classes = Classes.scanPackage(packageName, (Class<Object> type) -> {
                if (type.getAnnotation(Action.class) != null) {
                    return true;
                }
                return false;
            });
            for (Class<?> type : classes) {
                Action action =  type.getAnnotation(Action.class);
                if(actionMap.containsKey(action.value())){
                    throw new IllegalStateException(String.format("Duplicate Action in %s and %s",actionMap.get(action.value()),type));
                }
                actionMap.put(action.value(),type);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void initContext(){
        Context.put("threadId",UUID.randomUUID().toString());
        /* try{
            User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(user.getUserId()==null){
                user.setUserId("system");
                user.setUserCode("system");
                user.setUsername("系统");
            }
            Context.put(Context.USER,user);
        }catch (Exception e){
            logger.error("初始化USER CONTEXT失败");
        }*/
    }
}
