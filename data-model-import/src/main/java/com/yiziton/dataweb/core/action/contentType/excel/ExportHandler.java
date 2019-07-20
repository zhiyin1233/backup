package com.yiziton.dataweb.core.action.contentType.excel;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description:
 * @Author: 菠菜头
 * @Date: 2018-08-17 16:01
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public interface ExportHandler<T> {

    void doExport(T t, String fileName, HttpServletResponse response) throws Exception;
}
