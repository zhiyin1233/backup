package create;

/**
 * @author HuangHuai on 2019-06-27 11:38
 */

public class StringUtil {


    /**
     * 将javaBean驼峰命名的字段转换数据库下划线, 如 serviceId --> service_id
     */
    public static String toUnderlineName(String name) {
        StringBuilder sb = new StringBuilder();
        char[] chars = name.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isUpperCase(chars[i])) {
                if (i == 0) {
                    throw new IllegalStateException("字段名不允许首字母大写：" + name);
                }
                sb.append('_').append(Character.toLowerCase(chars[i]));
            } else {
                sb.append(chars[i]);
            }
        }

        return sb.toString();
    }
}
