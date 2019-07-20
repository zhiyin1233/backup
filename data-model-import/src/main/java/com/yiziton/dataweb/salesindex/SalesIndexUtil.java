package com.yiziton.dataweb.salesindex;

import com.yiziton.dataweb.salesindex.pojo.OrganizeDO;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author HuangHuai on 2019-07-17 17:45
 */

public class SalesIndexUtil {
    public static String getCacheKey(SalesIndexQueryVO vo) {
        StringBuilder sb = new StringBuilder();
        recursionAppendFieldVal(sb, vo,vo.getClass());
        return Md5Util.getMD5String(sb.toString());
    }

    private static void recursionAppendFieldVal(StringBuilder sb, Object vo, Class aClass) {
        if (aClass.getName().startsWith("com.yiziton")) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object v = field.get(vo);
                    if (v != null) {
                        sb.append(v);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            recursionAppendFieldVal(sb,vo,aClass.getSuperclass());
        }


    }

    /**
     * 将list的OrganizeDO.code拼接成sql in的条件,如：  ('180816000001','180816000002','180816000003')
     * @param list
     * @return
     */
    public static String getOrganizeCodeInClause(List<OrganizeDO> list) {
        String s = CommonUtil.mkString("('", "','", "')", list.stream().map(e -> e.code).toArray());
        if (s.length() == 4) {
            return null;
        }
        return s;
    }
}
