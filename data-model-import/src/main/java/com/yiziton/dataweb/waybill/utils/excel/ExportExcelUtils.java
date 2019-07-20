package com.yiziton.dataweb.waybill.utils.excel;

import com.yiziton.dataweb.waybill.vo.header.InfoHeader;
import com.yiziton.dataweb.waybill.vo.header.WayBillInfoHeader;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018-11-27 15:52
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Component
public class ExportExcelUtils {

    @Autowired
    HttpServletResponse httpServletResponse;

    /**
     * 导出excel(.xlsx)
     * @param
     * @param dataList
     * @param exportParam
     * @throws Exception
     */
    public static Workbook createExcelUtils(String columns, List<Map<String,Object>> dataList, Map<String,Object> exportParam) throws Exception{
        String[] columnsList = columns.split(",");
        WayBillInfoHeader billInfoHeader = new WayBillInfoHeader();
        Map<String,Map> headers = new HashMap<>();
        for(String column : columnsList){
            headers.put(column,billInfoHeader.headerMap.get(column).toMap());
        }
        return new WriteService().writeExcel(exportParam).createHeaderForSimple(headers).createContents(dataList).build();
    }

    public static WriteService getWriteService(InfoHeader infoHeader, String columns, Map<String,Object> exportParam){
        String[] columnsList = columns.split(",");
        Map<String,Map> headers = new HashMap<>();
        int i=0;
        for(String column : columnsList){
            headers.put(i+"",infoHeader.headerMap.get(column).toMap());
            i++;
        }
        return new WriteService().writeExcel(exportParam).createSheet().createHeaderForSimple(headers);
    }

    public static WriteService getWriteService(InfoHeader infoHeader, Map<String,Object> exportParam){
        Map<String,Map> headers = new HashMap<>();
        int i=0;
        for(String column : infoHeader.headerMap.keySet()){
            headers.put(i+"",infoHeader.headerMap.get(column).toMap());
            i++;
        }
        return new WriteService().writeExcel(exportParam).createSheet().createHeaderForSimple(headers);
    }

    public void exportExcel(String fileName, Workbook workbook) throws Exception{

        if (StringUtils.isBlank(fileName)){
            fileName = System.currentTimeMillis()+"";
        }
        httpServletResponse.setHeader("Content-disposition", "attachment;filename=\""+ new String( fileName.getBytes( "gb2312" ), "ISO8859-1" )+ ".xlsx" + "\"");
        httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        OutputStream os = null;
        try {
            os = httpServletResponse.getOutputStream();
            workbook.write(os);
            os.flush();
        }finally {
            if (os!=null){
                os.close();
            }
        }
    }

}
