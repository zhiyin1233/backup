package com.yiziton.dataweb.waybill.utils.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author: 菠菜头
 * @Date: 2018-08-06 10:52
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class WriteDataService {

    private Logger logger = LoggerFactory.getLogger(WriteDataService.class);

    private Workbook wb;
    private Sheet sheet;
    private Map schema;
    private Map exportParam;
    private Map summaryParam;
    private List<String> names;
    private String pattern = "\\{.*\\}";
    //汇总项
    private Map summaryData;
    private BaseCellStyleFactory styleFactory;
    public WriteDataService(Workbook wb, Sheet sheet, Map schema, Map exportParam) {
        this.wb = wb;
        this.sheet = sheet;
        this.schema = schema;
        this.exportParam = exportParam;
        styleFactory = new BaseCellStyleFactory(wb);
        summaryParam = Optional.ofNullable(exportParam).map(param->(Map)param.get("summary")).orElse(null);
        if(summaryParam !=null){
            summaryData = new HashMap();
        }
    }

    /**
     * 内容区
     * @param
     * @param dataList
     */
    public void createContents(List<String> names,List<Map<String,Object>> dataList){
        this.names = names;
        //内容
        int i =0;
        for (Map<String,Object> data:dataList){
//            System.out.println("开始insert第"+i+"条记录");
            insertDataRecord(names,data);
        }
    }

    /**
     * 汇总区
     */
    public void createSummary(){
        String pattern = "\\{.*\\}";
        Map<String,Object> summaryItems = Optional.ofNullable(summaryParam).map(param->(Map)param.get("items")).orElse(null);
        if(summaryItems!=null){
            Map<String,Object> summaryRowData = new HashMap<>();

            summaryItems.forEach((key,value)->{
                if(Pattern.matches(pattern,(String)value)){
                    summaryRowData.put(key,summaryData.get(key));
                }else{
                    summaryRowData.put(key,value);
                }
            });
            if(summaryRowData.get(names.get(0))==null){
                summaryRowData.put(names.get(0),"汇总行:");
            }
            insertSummaryDataRecord(names,summaryRowData);
        }
    }

    /**
     * 写入单条记录(允许占用多行)
     * @param names
     * @param data
     */
    public void insertDataRecord(List<String> names,Map<String,Object> data) {
        Row row = createRow(sheet);
        int startRow = row.getRowNum();
        Map<String,Map> cellSchemas = cellSchemas(names);
        String name ;
        int cellNum = 0;
        Iterator<String> it = names.iterator();
        while (it.hasNext()){
            name = it.next();
            Object value = getDataValue(data,name);
            Map cellSchema = cellSchemas.get(name);
            Map map = new HashMap();
            map.put("sheet",sheet);
            map.put("row",startRow);
            map.put("cell",cellNum);
//            System.out.println(name+"--->"+cellNum+"-->"+value);
            if(value instanceof List){
                int index =0;
                for (Object v:(List)value){
                    map.put("row",startRow+index);
                    singleCellData(v,name,cellSchema,data,map,index);
                    index ++;
                }
            }else{
                singleCellData(value,name,cellSchema,data,map,0);
            }
            cellNum++;
        }
    }

    /**
     * 在sheet的末尾继续追加一行
     * @param sheet
     * @return
     */
    public static Row createRow(Sheet sheet){
//        System.out.println("开始创建行号:"+(sheet.getLastRowNum()+1));
        return sheet.createRow(sheet.getLastRowNum()+1);
    }


    /**
     * 对单个数据进行处理并写如cell中
     * @param value
     * @param name
     * @param cellSchema
     * @param data
     * @param coordinate
     */
    private void singleCellData(Object value,String name,Map cellSchema,Map data,Map coordinate,int index){
        Object cellValue = dataResolver(value,cellSchema);

        List<String> failFields = null;
        Object fails =data.get("failFields");
        if(fails instanceof Array){
            failFields = Arrays.asList((String[])data.get("failFields"));
        }else if (fails instanceof List){
            failFields = (List<String>) fails;
        }else{
            failFields = new ArrayList<>();
        }

        boolean errorFlag = isErrorData(failFields,name,index);
        String contentCellStyle = this.getContentStyle(errorFlag,value);
        createCell(cellValue,cellSchema,contentCellStyle,coordinate);

        //追加求和
        String summary = Optional.ofNullable(summaryParam).map(param->(Map)param.get("items")).map(item->(String)item.get(name)).orElse(null);
        /*if(summary!=null && Pattern.matches(pattern, summary)){
            Object obj = summaryData.get(name);
            if(obj==null){
                obj=0;
            }
            Map<String,Object> bnlParam = new HashMap<>();
            bnlParam.put("a",obj);
            bnlParam.put("b",value);
            BNL bnl = new BNL();
            Object summaryValue = bnl.eval("a+b",bnlParam);
            summaryData.put(name,summaryValue);
        }*/
    }


    /**
     * 对单个数据进行处理并写如cell中
     * @param value
     * @param name
     * @param cellSchema
     * @param data
     * @param coordinate
     */
    private void summarySingleCellData(Object value,String name,Map cellSchema,Map data,Map coordinate,int index){
        String contentCellStyle = BaseCellStyleFactory.BASE_SUMMARY_STYLE;
        createCell(value,cellSchema,contentCellStyle,coordinate);
    }

    /**
     * 写入单条记录(允许占用多行)
     * @param names
     * @param summaryData
     */
    public void insertSummaryDataRecord(List<String> names,Map<String,Object> summaryData) {
        Row row = createRow(sheet);
        int startRow = row.getRowNum();
        Map<String,Map> cellSchemas = cellSchemas(names);
        String name ;
        int cellNum = 0;
        Iterator<String> it = names.iterator();
        while (it.hasNext()){
            name = it.next();
            Object value = getDataValue(summaryData,name);
            Map cellSchema = cellSchemas.get(name);
            Map map = new HashMap();
            map.put("sheet",sheet);
            map.put("row",startRow);
            map.put("cell",cellNum);
            if(value instanceof List){
                int index =0;
                for (Object v:(List)value){
                    map.put("row",startRow+index);
                    summarySingleCellData(v,name,cellSchema,summaryData,map,index);
                    index ++;
                }
            }else{
                summarySingleCellData(value,name,cellSchema,summaryData,map,0);
            }
            cellNum++;
        }
    }
    private String getContentStyle(boolean isError,Object value){
        if(value instanceof Date){
            return isError?BaseCellStyleFactory.DATE_ERROR_CONTENT_STYLE:BaseCellStyleFactory.DATE_CONTENT_STYLE;
        }else{
           return isError?BaseCellStyleFactory.BASE_ERROR_CONTENT_STYLE:BaseCellStyleFactory.BASE_CONTENT_STYLE;
        }
    }

    /**
     * 获取属性的schema
     * @param name
     * @return
     */
    private Map<String,Object> getCellSchema(String name){
        String[] keys =name.split("\\.");
        Map map = schema;
        for (int i=0;i<keys.length;i++){
            String key = keys[i];
            if(key.equals("*")){
                map = (Map) map.get("items");
                continue;
            }
            map = (Map) ((Map) map.get("properties")).get(key);
        }
        return map;
    }

    public boolean isErrorData(List<String> errorFields,String name,int index){
        String replace = name.replace(".*", "[" + index + "]");
        boolean b = Optional.ofNullable(errorFields).map(list->list.contains(replace)).orElse(false);
//        System.out.println(replace+"的正确属性:"+!b);
        return b;
    }

    /**
     * 数据的美化
     * 如:日期的格式化/值的替代
     * @param value
     * @param cellSchema
     * @return
     */
    public Object dataResolver(Object value,Map cellSchema){
        if(value instanceof Timestamp){
            value = new Date(((Timestamp) value).getTime());
        }
        if(value instanceof Date){
            String format = (String) ModelSchemaUtils.getExportParam(cellSchema,"format");
            if(StringUtils.isNotBlank(format)){
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                return sdf.format(value);
            }
        }
        return value;
    }

    public Map<String,Map> cellSchemas(List<String> names){
        Map map = new HashMap();
        Iterator<String> iterator = names.iterator();
        while (iterator.hasNext()){
            String name = iterator.next();
            map.put(name,getCellSchema(name));
        }
        return map;
    }

    /**
     * 创建一个cell
     * @param value
     * @param cellSchema
     * @param cellStyle
     * @param coordinate
     */
    public void createCell(Object value,Map cellSchema,String cellStyle,Map coordinate){
        Sheet sheet = (Sheet) coordinate.get("sheet");
        int rowNumber = (Integer) coordinate.get("row");
        int columnNumber = (Integer) coordinate.get("cell");
        Row row = sheet.getRow(rowNumber);
        if(row == null){
            row = sheet.createRow(rowNumber);
        }
        Cell cell = row.createCell(columnNumber);
        if(value==null || value instanceof String){
            cell.setCellValue((String)value);
        }else if(value instanceof Number){
            cell.setCellValue(numberConvertToDouble((Number) value));
        }else if(value instanceof Boolean){
            cell.setCellValue((boolean) value);
        }else if(value instanceof Date){
            cell.setCellValue((Date) value);
        }else{
            cell.setCellValue(value.toString());
        }
        cell.setCellStyle(styleFactory.getCellStyle(cellStyle));
    }

    private static double numberConvertToDouble(Number number){
        Double result = new Double(number.toString());
        return result.doubleValue();
    }

    /**
     * 从data中获取某个属性的值
     * @param data
     * @param name
     * @return
     */
     public  Object getDataValue(Map data,String name){
        int index =  name.split("\\.")[0].length();
        String head = name.substring(0,index);
        String tail = index == name.length()?"":name.substring(index+1,name.length());

        Object obj = data.get(head);
        if(obj ==null){
            return null;
        }
        if(obj instanceof Map){
            return getDataValue((Map) obj,tail);
        }else if (obj instanceof List ||obj instanceof Set){
            List list = new ArrayList((Collection) obj);
            Object o = list.get(0);
            if(o instanceof Map){
                tail  = tail.startsWith("*.")?tail.replaceFirst("\\*\\.",""):tail;
                List itemList = new ArrayList();
                for (Map m:(List<Map>) list){
                    itemList.add(getDataValue( m,tail));
                }
                return itemList;
            }else{
                return obj;
            }
        }else{
            return obj;
        }
    }
}
