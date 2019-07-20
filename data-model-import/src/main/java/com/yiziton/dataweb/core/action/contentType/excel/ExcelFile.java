package com.yiziton.dataweb.core.action.contentType.excel;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * @Description: ExcelFile类
 * @Author: xiaoHong
 * @Date: 2018/9/14
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class ExcelFile {

    private String fileName;

    private Workbook workbook;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }
}
