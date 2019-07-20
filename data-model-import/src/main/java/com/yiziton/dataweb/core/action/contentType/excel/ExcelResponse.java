package com.yiziton.dataweb.core.action.contentType.excel;

import com.yiziton.dataweb.core.action.ActionResponse;
import com.yiziton.dataweb.core.action.contentType.ResponseHandler;
import com.yiziton.dataweb.core.context.Context;
import com.yiziton.dataweb.core.exception.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * @Description:
 * @Author: 菠菜头
 * @Date: 2018-06-01 15:51
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Component
public class ExcelResponse extends ResponseHandler {

    private Logger logger = LoggerFactory.getLogger(ExcelResponse.class);


    @Override
    public String getType() {
        return "excel";
    }

    @Override
    public Object doResponse(HttpServletResponse response, Object data) throws Exception {
        String threadId = (String)Context.getContext().get("threadId");
        logger.info("excel action response start,threadId:"+threadId);
        Object result = ((ActionResponse)data).getContent();
        Optional.ofNullable(result).orElseThrow(() -> new IllegalArgumentException("导出数据不能为空"));
        if(result instanceof ExcelFile){
            doWorkbook(response,(ExcelFile)result);
        }else{
            throw new DomainException(500,"该导出请求不支持");
        }
        logger.info("excel action response end,threadId:"+threadId);
        return null;

    }

    private void doWorkbook(HttpServletResponse response, ExcelFile result) throws Exception {
        new ExcelExportHandler().doExport(result.getWorkbook(),result.getFileName(),response);
    }

}
