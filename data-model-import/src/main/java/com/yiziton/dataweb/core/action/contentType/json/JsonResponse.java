package com.yiziton.dataweb.core.action.contentType.json;

import com.alibaba.fastjson.JSON;
import com.yiziton.dataweb.core.action.contentType.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description:
 * @Author: 菠菜头
 * @Date: 2018-06-01 15:51
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Component
public class JsonResponse extends ResponseHandler {

    @Override
    public String getType() {
        return "json";
    }

    @Override
    public Object doResponse(HttpServletResponse response, Object result) throws Exception {
        String out = JSON.toJSONString(result);
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(out);
        response.getWriter().flush();

        return null;
    }
}
