package com.yiziton.dataweb.core.action.contentType.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * @Description:
 * @Author: 菠菜头
 * @Date: 2018-08-17 16:02
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class ExcelExportHandler implements ExportHandler<Workbook> {

    @Override
    public void doExport(Workbook workbook, String fileName, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(fileName)){
            fileName = System.currentTimeMillis()+"";
        }
        response.setHeader("Content-disposition", "attachment;filename=\""+ new String( fileName.getBytes( "gb2312" ), "ISO8859-1" )+ ".xlsx" + "\"");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            workbook.write(os);
            os.flush();
        }finally {
            if (os!=null){
                os.close();
            }
        }
    }
}
