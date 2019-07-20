package com.yiziton.dataweb.waybill.utils.excel;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/11/27
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class Header {

    private String name;

    private String title;

    private String type;

    private int width;

    public Header(String name,String title,String type,int width){
        this.name = name;
        this.title = title;
        this.type = type;
        this.width = width;
    }

    public String getName() {
        return name;
    }

    public Header setName(String name) {
        this.name = name;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Header setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getType() {
        return type;
    }

    public Header setType(String type) {
        this.type = type;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public Header setWidth(int width) {
        this.width = width;
        return this;
    }

    public Map toMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("name",this.getName());
        map.put("title",this.getTitle());
        map.put("type",this.getType());
        map.put("width",this.getWidth());
        return map;
    }
}
