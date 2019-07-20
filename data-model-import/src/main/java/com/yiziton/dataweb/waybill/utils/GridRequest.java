package com.yiziton.dataweb.waybill.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaoHong on 2018/12/3.
 */
public class GridRequest {
    private Map<String,Object> filters;
    private int first;
    private int rows = 10;
    private int page ;
    private int size = 10;
    private String sortField;
    private int sortOrder;
    private Map<String,String> mappingFieldMap = new HashMap<>();

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public GridRequest mapping(String aname, String name){
        this.mappingFieldMap.put(aname,name);
        return this;
    }

    public PageRequest toPageRequest(){
        PageRequest pageRequest;
        if(sortField!=null){
            if(mappingFieldMap.containsKey(sortField)){
                sortField = mappingFieldMap.get(sortField);
            }
            pageRequest = new PageRequest(first/rows,rows,new Sort(new Sort.Order(sortOrder<0? Sort.Direction.DESC: Sort.Direction.ASC,sortField)));
        }
        else {
            pageRequest = new PageRequest(first/rows,rows);
        }
        return pageRequest;
    }
    public <T> T toExample(Class<T> type){
        try{
            T obj = type.newInstance();
            if(filters==null){
                return obj;
            }
            PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(type).getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor:propertyDescriptors){
                String name = propertyDescriptor.getName();
                if(filters.containsKey(name)){
                    Method writeMethod = propertyDescriptor.getWriteMethod();
                    if(writeMethod!=null){
                        JSONObject jsonObject = (JSONObject) filters.get(name);
                        Object value = jsonObject.getObject("value",propertyDescriptor.getPropertyType());
                        writeMethod.invoke(obj,value);
                    }
                }
            }
            return obj;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
