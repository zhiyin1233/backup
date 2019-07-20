package com.yiziton.dataweb.waybill.utils.excel;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * @Description:
 * @Author: 菠菜头
 * @Date: 2018-07-23 15:54
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class WriteService {

    private Logger logger = LoggerFactory.getLogger(WriteService.class);
    private Workbook wb;
    private BaseCellStyleFactory styleFactory;
    private int titleRowStart;
    private int titleRowDepth;
    private int curCellColumn;
    private List<String> cellNames = new ArrayList<>();
    private Map exportParam;
    private WriteDataService writeDataService;
    public WriteService writeExcel(Map exportParam){
        wb = new SXSSFWorkbook(500);
        styleFactory = new BaseCellStyleFactory(wb);
        createSheet();
        this.exportParam = exportParam;
        return this;
    }

    public  WriteService createSheet(){
        Sheet sheet = wb.createSheet();
        return this;
    }


    public WriteService createHeaderForSimple(Map<String,Map> model){
        model = ModelHelp.modelConvertor(model);
        List<String> names = getSortNames(model);
        int column = names.size();
        Sheet sheet = wb.getSheetAt(0);
        //标题
        createCaption(sheet,exportParam,column);

        //表头
        createTitle(sheet,names,model);
        writeDataService = new WriteDataService(wb,sheet,model,exportParam);
        return this;
    }

    public WriteService createHeaderForComplex(Map<String,Map> model){
        List<String> names = getSortNames(model);
        int column = names.size();
        Sheet sheet = wb.getSheetAt(0);
        //标题
        createCaption(sheet,exportParam,column);

        //表头
        createTitle(sheet,names,model);
        writeDataService = new WriteDataService(wb,sheet,model,exportParam);
        return this;
    }

    public WriteService createContents(List<Map<String,Object>> dataList){
        writeDataService.createContents(cellNames,dataList);
        return this;
    }

    public Workbook build() {
        Object summary =  Optional.ofNullable(exportParam).map(param->param.get("summary")).orElse(null);
        if(summary!=null){
            writeDataService.createSummary();
        }
        return wb;
    }

    public  Workbook createSheet(Map<String,Object> model, List<Map<String,Object>> dataList, Map<String,Object> exportParam) throws Exception{
        Sheet sheet = wb.createSheet();
        List<String> names = getSortNames(model);
        int column = names.size();
        //标题
        createCaption(sheet,exportParam,column);

        //表头
        createTitle(sheet,names,model);

        //内容
        new WriteDataService(wb,sheet,model,exportParam).createContents(cellNames,dataList);
        return wb;
    }


    /**
     * 标题栏
     * @param sheet
     * @param exportParam
     * @param colNumber
     */
    public  void createCaption(Sheet sheet, Map<String,Object> exportParam, int colNumber){
        if(exportParam==null){
            return ;
        }
        String caption = (String)exportParam.get("caption");
        if(StringUtils.isBlank(caption)){
            return;
        }

        sheet.addMergedRegion(new CellRangeAddress(0,0,0,colNumber));

        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(caption);
        cell.setCellStyle(styleFactory.getCellStyle(BaseCellStyleFactory.BASE_CAPTION_STYLE));

        String subCaption = (String)exportParam.get("subCaption");
        String author = (String)exportParam.get("author");
        if(StringUtils.isBlank(subCaption) && StringUtils.isBlank(author)){
            return;
        }
        String text = (StringUtils.isBlank(subCaption)?"":subCaption)+"        " + (StringUtils.isBlank(author)?"":"出表人:"+author);
        sheet.addMergedRegion(new CellRangeAddress(1,1,0,colNumber));
        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue(text);
        cell.setCellStyle(styleFactory.getCellStyle(BaseCellStyleFactory.BASE_SUB_CAPTION_STYLE));
    }

    /**
     * 标题区
     * @param sheet
     * @param names
     * @param schema
     */
    public void createTitle(Sheet sheet,List<String> names,Map schema){
        int startRowNum = sheet.getLastRowNum();
        if(startRowNum==0 && sheet.getRow(startRowNum) == null){
        }else{
            startRowNum = startRowNum+1;
        }
        List<String> list = new ArrayList<>();
        for (String name:names){
            list.add(name.replace(".*",""));
        }
//        Set<Map> set = getTitleStruct(list,schema);
//        for (Map map:set){
//            System.out.println("key-->"+map.get("key"));
//            System.out.println("title-->"+((Map)(map.get("schema"))).get("title"));
//            System.out.println("width-->"+map.get("width"));
//            System.out.println("properties-->"+map.get("properties"));
//        }
        //整个标题栏跨的行数
        int depth = calculateTitleDepth(list);

        titleRowStart = startRowNum;
        titleRowDepth = depth;
        for (int i=0;i<depth;i++){
           sheet.createRow(startRowNum+i);
        }

        Map sortSchema = sortSchemaProperties(schema);
        Set s = sortSchema.keySet();
        for (String n:(Set<String>)s){
            Map propertySchema = (Map)(sortSchema.get(n));
            buildTitleCell(n,propertySchema,0,sheet,"");
        }
    }

    public int buildTitleCell(String name,Map propertySchema,int curDepth,Sheet sheet,String fathName){
        String title = (String) propertySchema.get("title");
        boolean isArray = false;
        if (propertySchema.get("type").equals("array")){
            propertySchema = (Map) propertySchema.get("items");
            isArray = true;
        }
        if (propertySchema.get("type").equals("object")){
            int width = 0;
            if (StringUtils.isBlank(fathName)){
                fathName = name;
            }else {
                fathName = fathName+"."+name;
            }
            if(isArray){
                fathName = fathName+".*";
            }

           // Map properties = (Map) propertySchema.get("properties");
            Map sortSchema = sortSchemaProperties(propertySchema);
            Set s = sortSchema.keySet();
            for (String n:(Set<String>)s){
                Map propertySchema2 = (Map)(sortSchema.get(n));
                width += buildTitleCell(n,propertySchema2,curDepth+1,sheet,fathName);
//                cellNames.set(cellNames.size()-1,name+(isArray?".*.":".")+n);
                //System.out.println(n+"的宽度是:"+width);
//                System.out.println(n+"的name是:"+name+(isArray?".*.":".")+n);
            }

            Map region = new HashMap();
            region.put("firstRow",titleRowStart+curDepth);
            region.put("lastRow",titleRowStart+curDepth);
            region.put("leftColumn",curCellColumn-width);
            region.put("rightColumn",curCellColumn-1);
//            curCellColumn = width+curCellColumn;
            createTitleCell(sheet,title,region,propertySchema);
            curDepth++;
            return width;
        }else{
            Map region = new HashMap();
            region.put("firstRow",titleRowStart+curDepth);
            region.put("lastRow",titleRowStart+titleRowDepth-1);
            region.put("leftColumn",curCellColumn);
            region.put("rightColumn",curCellColumn);
            curCellColumn++;
            createTitleCell(sheet,title,region,propertySchema);
            String n = (StringUtils.isBlank(fathName)?"":(fathName+"."))+name+(isArray?".*":"");
            cellNames.add(n);
            return 1;
        }
    }

    public void createTitleCell(Sheet sheet,String title,Map region,Map schema){
        int firstRow = (int) region.get("firstRow");
        int lastRow = (int) region.get("lastRow");
        int leftColumn = (int) region.get("leftColumn");
        int rightColumn = (int) region.get("rightColumn");

        Row row = sheet.getRow(firstRow);
        Cell cell = row.createCell(leftColumn);
        cell.setCellValue(title);

        //System.out.println("标题【"+title+"】的区域是:"+firstRow+"-"+lastRow+"-"+leftColumn+"-"+rightColumn);
        if(firstRow<lastRow || leftColumn<rightColumn){
            CellRangeAddress cra =new CellRangeAddress(firstRow,lastRow,leftColumn,rightColumn);
            sheet.addMergedRegion(cra);
        }

        CellStyle style = styleFactory.getCellStyle(BaseCellStyleFactory.TITLE_STYLE);
        cell.setCellStyle(style);

        Integer width = (Integer) ModelSchemaUtils.getExportParam(schema,"width");
        if(width != null && width>0) {
            sheet.setColumnWidth(leftColumn, 256 * width);
        }
    }

    public int calculateTitleDepth(List<String> titles){
        List<Integer> list = new ArrayList<>();
        for (String name:titles){
            list.add(name.split("\\.").length);
        }
        Collections.sort(list);
        return list.get(list.size()-1);
    }





    public List<String> names(Map schema){
        List<String> names = new ArrayList<>();
        Map properties = (Map)schema.get("properties");
        properties.forEach((name,property)->{
            if(((Map)property).get("type").equals("object")){
                List<String> list = names(((Map)property));
                String fatherName = (String) name;
                for (String subName:list){
                    names.add(fatherName+"."+subName);
                }
            }else if(((Map)property).get("type").equals("array")){
                Map item =(Map) ((Map)property).get("items");
                String itemType = (String) item.get("type");
                if(itemType.equals("object")){
                    List<String> list = names(item);
                    String fatherName = (String) name;
                    for (String subName:list){
                        names.add(fatherName+".*."+subName);
                    }
                }else{
                    String fatherName = (String) name;
                    names.add(fatherName+".*");
                }
            }else{
                names.add((String) name);
            }
        });

        return names;
    }


    public List<String> sorts(Map schema){
        List<String> sorts = new ArrayList<>();
        Map properties = (Map)schema.get("properties");
        properties.forEach((name,property)->{
            if(((Map)property).get("type").equals("object")){
                List<String> list = sorts(((Map)property));
                int order = getOrder((Map) ((Map) property).get("exportParam"));
                for (String subOrder:list){
                    sorts.add(order+"."+subOrder);
                }
            }else if(((Map)property).get("type").equals("array")){
                int fatherOrder = getOrder((Map) ((Map) property).get("exportParam"));
                Map item =(Map) ((Map)property).get("items");
                String itemType = (String) item.get("type");
                if(itemType.equals("object")){
                    List<String> list = sorts(item);
                    for (String subOrder:list){
                        sorts.add(fatherOrder+"."+subOrder);
                    }
                }else{
                    sorts.add(fatherOrder+"");
                }
            }else{
                sorts.add(getOrder((Map) ((Map) property).get("exportParam"))+"");
            }
        });

        return sorts;
    }

     private int getOrder(Map model){
        Object order =  ModelSchemaUtils.getExportParam(model,"order");
        return Optional.ofNullable(order).map(o->o.toString()).map(o-> Integer.valueOf(o)).orElse(Integer.MAX_VALUE);
    }

    public  List<String> sortNames(List<String> sorts,List<String> names){
        if(sorts.size() != names.size()){
            logger.error("解析异常");
        }
        List<Map<String,String>> list = new ArrayList<>();
        for (int i=0;i<sorts.size();i++){
            Map<String,String> map = new HashMap<>();
            map.put("sort",sorts.get(i));
            map.put("name",names.get(i));
            list.add(map);
        }
        Collections.sort(list, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                int i = o1.get("sort").compareTo(o2.get("sort"));
                if(i == 0){
                    return 0;
                }
                return i;
            }
        });
        List<String> sortNames = new ArrayList<>();
        list.forEach(map->{
            sortNames.add(map.get("name"));
        });
        return sortNames;
    }


    public List<String> getSortNames(Map model){
        List names = names(model);
        System.out.println(JSON.toJSONString(names));
        List map = sorts(model);
        System.out.println(JSON.toJSONString(map));
        return sortNames(map,names);
    }

//    public Set<Map> getTitleStruct(List<String> names,Map schema){
//        Set<Map> list = new LinkedHashSet<>();
//        for (String name:names){
//            if(name.split("\\.").length>=3) {
//                list.add(getTitleStruct2(name, schema));
//            }
//        }
//
//        return list;
//    }
//    public Map getTitleStruct2(String name,Map schema){
//        System.out.println("<== "+ name +" ==>");
//        String key  = name.split("\\.")[0];
//        Map keySchema = getCellSchema(key,schema);
//        Map map =  new HashMap();
//        map.put("key",key);
//        map.put("schema",keySchema);
//
//        if(!name.equals(key)){
//            String subString = name.substring(key.length()+1,name.length());
//            Map properties = getTitleStruct2(subString,keySchema);
//            map.put("properties",properties);
//            int width = map.get("width")==null?1:(int)map.get("width");
//            map.put("width",width+1);
//        }else{
//            map.put("width",1);
//        }
//        return map;
//    }

    private Map<String,Object> getCellSchema(String name,Map schema){
        String[] keys =name.split("\\.");
        Map map = schema;
        for (int i=0;i<keys.length;i++){
            String key = keys[i];
            if(map.get("properties")==null || ((Map) map.get("properties")).get(key) == null){
                map = (Map) map.get("items");
                map = (Map) ((Map) map.get("properties")).get(key);
            }else{
                map = (Map) ((Map) map.get("properties")).get(key);
            }
        }
        return map;
    }

    public Map sortSchemaProperties(Map schema){
        Map map = new LinkedHashMap();
        Map properties = (Map)schema.get("properties");

        ListMultimap<Integer,String> multiMap =  MultimapBuilder.treeKeys().arrayListValues().build();
        properties.forEach((name,property)->{
            multiMap.put(getOrder((Map) property),(String) name);
        });
        TreeMap treeMap = new TreeMap();
        treeMap.putAll(multiMap.asMap());

        treeMap.forEach((key,value)->{
            if(value instanceof List){
                for (Object v:(List)value){
                    map.put(v,getCellSchema((String)v,schema));
                }
            }else{
                map.put(value,getCellSchema((String) value,schema));
            }
        });
        return map;
    }



}
