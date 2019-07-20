package com.yiziton.dataweb.waybill.utils.excel;

import com.sun.jersey.client.impl.CopyOnWriteHashMap;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Map;

/**
 * @Description: 基础CellStyle工厂
 * @Author: 菠菜头
 * @Date: 2018-08-09 18:11
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class BaseCellStyleFactory {
    private  Map<String,CellStyle> cellStyleMap;
    private Workbook wb;
    public final static String TITLE_STYLE = "titleStyle";
    public final static String BASE_CONTENT_STYLE = "baseContentStyle";
    public final static String BASE_CAPTION_STYLE = "baseCaptionStyle";
    public final static String BASE_SUB_CAPTION_STYLE = "baseSubCaptionStyle";
    public final static String BASE_ERROR_CONTENT_STYLE = "baseErrorContentStyle";
    public final static String DATE_CONTENT_STYLE = "dateContentStyle";
    public final static String DATE_ERROR_CONTENT_STYLE = "dateErrorContentStyle";
    public final static String BASE_SUMMARY_STYLE = "baseSummaryStyle";

    public BaseCellStyleFactory(Workbook wb) {
        this.cellStyleMap = new CopyOnWriteHashMap<>();
        this.wb = wb;
    }

    public CellStyle getCellStyle(String styleName){
        if(cellStyleMap.get(styleName)==null){
            if(styleName.equals(TITLE_STYLE)){
                CellStyle style = createTitleStyle();
                cellStyleMap.put(styleName,style);
            }else if(styleName.equals(BASE_CAPTION_STYLE)){
                CellStyle style = createBaseCaptionStyle();
                cellStyleMap.put(styleName,style);
            }else if(styleName.equals(BASE_SUB_CAPTION_STYLE)){
                CellStyle style = createBaseSubCaptionStyle();
                cellStyleMap.put(styleName,style);
            }else if(styleName.equals(BASE_CONTENT_STYLE)){
                CellStyle style = createBaseContentStyle();
                cellStyleMap.put(styleName,style);
            }else if (styleName.equals(BASE_ERROR_CONTENT_STYLE)){
                CellStyle style = createBaseErrorContentStyle();
                cellStyleMap.put(styleName,style);
            }else if(styleName.equals(DATE_CONTENT_STYLE)){
                CellStyle style = createDateContentStyle();
                cellStyleMap.put(styleName,style);
            }else if (styleName.equals(DATE_ERROR_CONTENT_STYLE)){
                CellStyle style = createDateErrorContentStyle();
                cellStyleMap.put(styleName,style);
            }else if (styleName.equals(BASE_SUMMARY_STYLE)){
                CellStyle style = createBaseSummaryStyle();
                cellStyleMap.put(styleName,style);
            }
        }
        return cellStyleMap.get(styleName);
    }

    private CellStyle createBaseCaptionStyle() {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) (26));
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER_SELECTION);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }

    private CellStyle createTitleStyle() {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // Create a new font and alter it.
        Font font = wb.createFont();
        font.setBold(true);
//        font.setColor(IndexedColors.RED.getIndex());
        style.setFont(font);

        return style;
    }
    private CellStyle createBaseContentStyle() {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // Create a new font and alter it.
        Font font = wb.createFont();
        //font.setBold(true);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        return style;
    }

    private CellStyle createBaseSubCaptionStyle() {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) (18));
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }

    private CellStyle createBaseErrorContentStyle(){
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // Create a new font and alter it.
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.RED.getIndex());
        style.setFont(font);
        return style;
    }

    private CellStyle createDateContentStyle() {
        DataFormat format= wb.createDataFormat();
        CellStyle style = createBaseContentStyle();
        style.setDataFormat(format.getFormat("yyyy-m-d hh:mm:ss"));
        return style;
    }

    private CellStyle createDateErrorContentStyle() {
        DataFormat format= wb.createDataFormat();
        CellStyle style = createBaseContentStyle();
        style.setDataFormat(format.getFormat("yyyy-m-d hh:mm:ss"));
        return style;
    }
    private CellStyle createBaseSummaryStyle() {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // Create a new font and alter it.
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.BLUE.getIndex());
        style.setFont(font);
        return style;
    }
}
