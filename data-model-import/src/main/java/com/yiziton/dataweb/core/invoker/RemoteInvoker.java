package com.yiziton.dataweb.core.invoker;

import com.alibaba.fastjson.JSONObject;

import com.yiziton.dataimport.commons.SpringUtils;
import com.yiziton.dataweb.waybill.config.InvokerConfig;
import com.yiziton.dataweb.core.action.ActionResponse;
import com.yiziton.dataweb.core.exception.DomainException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.json.JSONArray;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/9/18
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class RemoteInvoker {

    public static String SCM_URL;

    public static Object invoke (String url, Object arg){
        try{
            String path = url;
            JSONArray array;
            if(!arg.getClass().isArray() && !(arg instanceof List)){
                Object[] args = new Object[]{arg};
                array = new JSONArray(parse(args));
            }else {
                array = new JSONArray(parse(arg));
            }
            String param = array.toString();
            //HttpHeaders requestHeaders = new HttpHeaders();
            InvokerConfig invokerConfig = (InvokerConfig) SpringUtils.getApplicationContext().getBean("invokerConfig");
            RestTemplate restTemplate = invokerConfig.getRestTemplate();
            //requestHeaders.add("Authorization", "bearer "+ThreadToken.token.get());
            //RestTemplate restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(new TokenInterceptor());
            //设置编码
            List<HttpMessageConverter<?>> httpMessageConverters = new ArrayList<>();
            StringHttpMessageConverter stringHttpMessageConverter=new StringHttpMessageConverter(Charset.forName("UTF-8"));
            httpMessageConverters.add(stringHttpMessageConverter);
            restTemplate.setMessageConverters(httpMessageConverters);

            ResponseEntity<String> response = restTemplate.postForEntity(path,param,String.class);
            String body = response.getBody();
            ActionResponse actionResponse = JSONObject.parseObject(body,ActionResponse.class);
            if("200".endsWith(actionResponse.getResultCode())){
                Object result = actionResponse.getContent();
                return result;
            }
            throw new DomainException(500,"远程访问调用异常");
        }catch(Exception e){
            e.printStackTrace();
            throw new DomainException(500,"远程访问调用异常");
        }
    }

    private static Object parse(Object object) {
        if (object == null ) {
            return null;
        }
        if (object instanceof Map) {
            Map m = new HashMap();
            Map values = (Map) object;
            if (values instanceof ScriptObjectMirror
                    &&  (((ScriptObjectMirror) values).getClassName().equals("Array")
                    ||((ScriptObjectMirror) values).getClassName().equals("Arguments") )
                    ) {
                List l = new ArrayList();
                ((ScriptObjectMirror) values).entrySet().forEach(e -> {
                    l.add(parse(e.getValue()));
                });
                return l.toArray();
            }

            values.entrySet().forEach(e -> {
                Object key = ((Map.Entry) e).getKey();
                Object value = ((Map.Entry) e).getValue();
                m.put(key.toString(), parse(value));
            });
            return m;
        } else if (object.getClass().isArray()) {
            int len = Array.getLength(object);
//            com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray(len);
            Object[] array = new Object[len];
            for (int i = 0; i < len; ++i) {
                Object item = Array.get(object, i);
                array[i] = (parse(item));
            }
            return array;
        } else if (object instanceof List) {
            List l = new ArrayList();
            ((List) object).forEach(e -> {
                l.add(parse(e));
            });
            return l;
        } else {
            return object;
        }
    }
}
