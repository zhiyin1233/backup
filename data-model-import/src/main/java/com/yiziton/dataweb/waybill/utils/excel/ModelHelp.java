package com.yiziton.dataweb.waybill.utils.excel;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 模型辅助类,负责将旧版的model转成新版的model
 * @Author: 菠菜头
 * @Date: 2018-07-30 17:52
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class ModelHelp {

    public static Map modelConvertor(Map<String,Map> model){
        //System.out.println("-->"+JSONObject.toJSONString(model, SerializerFeature.PrettyFormat));
        Map schema = new HashMap();
        Map properties = new HashMap();
        schema.put("type","object");
        schema.put("properties",properties);
        model.forEach((key,value)->{
            String property = (String) value.get("name");
            Map<String,Object> attributeProperties = new HashMap<>();
            Map<String,Object> importParam = new HashMap<>();
            Map<String,Object> exportParam = new HashMap<>();
            attributeProperties.put("type",value.get("type")==null?"string":value.get("type"));
            attributeProperties.put("title",value.get("title"));
            attributeProperties.put("importParam",importParam);
            attributeProperties.put("exportParam",exportParam);
            importParam.put("required",value.get("required"));
            exportParam.put("order",key);
            exportParam.put("width",value.get("width"));
            properties.put(property,attributeProperties);
        });
        //System.out.println("==>"+JSONObject.toJSONString(schema, SerializerFeature.PrettyFormat));

        return schema;
    }
}
