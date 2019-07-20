package com.yiziton.dataweb.waybill.utils.excel;

import java.util.Map;
import java.util.Optional;

/**
 * @Description:
 * @Author: 菠菜头
 * @Date: 2018-08-10 10:22
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
public class ModelSchemaUtils {
    public static Object getExportParam(Map schemaMap, String param){
        return Optional.ofNullable(schemaMap).map(m->m.get("exportParam")).map(m->((Map)m).get(param)).orElse(null);
    }
    public static Object getImportParam(Map schemaMap, String param){
        return Optional.ofNullable(schemaMap).map(m->m.get("importParam")).map(m->((Map)m).get(param)).orElse(null);
    }
    public static Object getProperty(Map schemaMap, String key){
        return Optional.ofNullable(schemaMap).map(m->m.get("properties")).map(m->((Map)m).get(key)).orElse(null);
    }
}
