package com.yiziton.dataweb.salesindex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 简单常用工具,定义为不需要依赖额外jar包(或者常用的如spring apache common),如果需要依赖额外jar包,应该放在ServiceUtil中<br>
 * 在这个类中,你应该导入的都是jdk自带的类
 *
 * @author HuangHuai on 2019-06-27 11:30
 **/

public class CommonUtil implements Serializable {

    private static final long   serialVersionUID = 7816069471546717762L;
    private static       Logger log              = LoggerFactory.getLogger(CommonUtil.class);

    /**
     * 仅仅用来：json和对象的互转 其他的，如需要设置忽略以及设置json格式等操作，统统不允许使用此对象，应当自己new一个<br/>
     */
    public static final ObjectMapper jsonMapper = new ObjectMapper().setDateFormat(DateUtil.LONG_FORMAT)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * 把json字符串转为指定对象
     */
    public static <T> T json2Obj(String jsonString, Class<T> desClass) {
        try {
            return jsonMapper.readValue(jsonString, desClass);
        } catch (Exception e) {
            String msg = formatBraces("Deserialize json to Type:{} failed. Cause:{}\n{}",
                    desClass.getName(), e.getMessage(), jsonString);
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * usage: <br>
     * <blockquote><pre>
     *         String jsonString = "{...}";
     *
     *         TypeReference typeReference = new TypeReference&lt;ResultView&lt;User&gt;&gt;(){};
     *
     *         ResultView&lt;User&gt; rv = json2Obj(jsonString, typeReference);
     *     </pre></blockquote>
     */
    public static <T> T json2Obj(String jsonString, TypeReference<T> typeReference) {
        try {
            JavaType javaType = TypeFactory.defaultInstance().constructType(typeReference);
            return jsonMapper.readValue(jsonString, javaType);
        } catch (Exception e) {
            String msg = formatBraces("Deserialize json to Type:{} failed. Cause:{}\n{}",
                    typeReference.getType().getTypeName(), e.getMessage(), jsonString);
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * 把对象转为json字符串
     */
    public static String obj2Json(Object obj) {
        assertOneIsEmpties("Object to json, param obj", obj);
        try {
            return jsonMapper.writeValueAsString(obj);
        } catch (Exception e) {
            String msg = formatBraces("Serialize Type:{} to json failed. Cause:{}",
                    obj.getClass().getName(), e.getMessage());
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * 把json字符串转为JsonNode
     */
    public static JsonNode readTree(String nodeString) {
        try {
            return jsonMapper.readTree(nodeString);
        } catch (Exception e) {
            throw new RuntimeException("Read json to JsonNode error,please check your Json String-->"
                    + nodeString, e);
        }
    }

    /**
     * json漂亮格式
     */
    public static String prettyJson(Object obj) {
        try {
            return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            String msg = formatBraces("Serialize Type:{} to json failed. Cause:{}",
                    obj.getClass().getName(), e.getMessage());
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * e.g:
     * ""    return   true  <br>
     * " "    return   true  <br>
     * " a"    return   false  <br>
     * "null"    return   true  考虑到前端如果为null,有时候他并不用空表示,而是null字符串<br>
     * <p>
     * 对于集合, Collection.isEmpty <br>
     * 如果判断null这里不提供方法，使用  if(obj ==null ) 更加方便
     */
    public static boolean isEmpty(Object o) {
        boolean flag = false;
        if (o == null) {
            flag = true; // null is empty
        } else if (o instanceof String || o instanceof StringBuilder || o instanceof StringBuffer) { // 字符串
            flag = isStringEmpty(o.toString());
        } else if (o instanceof Collection) { // 集合
            flag = ((Collection<?>) o).isEmpty();
        } else if (o instanceof Map) { // map
            flag = ((Map<?, ?>) o).isEmpty();
        } else if (o.getClass().isArray()) { // 数组
            if (Array.getLength(o) == 0) {
                flag = true;
            }
            Object[] arr = (Object[]) o;
            if (arr.length == 1 && arr[0] == null) {
                flag = true; // 当java启动时,第一次调用可变参数,在传递过程中 null ==> Object[]{null}
            }
        } else if (o instanceof Boolean) {
            return (Boolean) o;
        }
        return flag;
    }

    /**
     * 判断是否不为空<br>
     */
    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    /**
     * 有一个为空  return true
     */
    public static boolean oneIsEmpties(Object... objs) {
        if (objs == null || objs.length == 0) {
            return true; // null or length=0 is empty
        }

        if ((objs.length == 1 && objs[0] == null)) {
            return true; // 当java启动时,第一次调用可变参数,在传递过程中 null ==> Object[]{null}
        }

        for (Object o : objs) {
            if (isEmpty(o)) { // 只要有一条是空，这个多参数数组就是空
                return true;
            }
        }
        return false;
    }

    /**
     * 至少有一个不为空 return true <br>
     * = !allIsEmpties (全为空的反义词)
     */
    public static boolean atLeastOneIsNotEmpties(Object... objs) {
        return !allIsEmpties(objs);
    }

    /**
     * 是否全部为空
     */
    public static boolean allIsEmpties(Object... objs) {
        if (objs == null || objs.length == 0) {
            return true; // null or length=0 is empty
        }
        for (Object o : objs) {
            if (isNotEmpty(o)) { // 有一个非空，就是不全为空
                return false;
            }
        }
        return true;
    }

    /**
     * 所有的参数都不为空 return true <br>
     * = !oneIsEmpties (只要有一个为空的反义词)
     */
    public static boolean allIsNotEmpties(Object... objs) {
        return !oneIsEmpties(objs); // 只要有一个非空，false
    }

    /**
     * ""    return   true  <br>
     * " "    return   true  <br>
     * " a"    return   false  <br>
     * "null"    return   true  考虑到前端如果为null,有时候他并不用空表示,而是null字符串<br>
     * 如果不接受 “null” 字符串为空，请使用isStringEmpty(String s, false)
     */
    public static boolean isStringEmpty(String s) {
        return isStringEmpty(s, true);
    }

    /**
     * isStringEmpty("null",true) return false (忽略了null字符串，非空) <br>
     * isStringEmpty("null",false) return true (不忽略null字符串，空) <br>
     *
     * @param ignoreNullStr 是否忽略 “null”
     */
    public static boolean isStringEmpty(String s, boolean ignoreNullStr) {
        if (s == null) {
            return true; // null is empty
        }
        String str = s.trim();

        // length=0 is empty "null" is empty
        if (str.length() == 0) {
            return false;
        }

        if (str.equalsIgnoreCase("null") && !ignoreNullStr) {
            // 不忽略 “null”
            return true;
        }

        return false; // really is not empty return false
    }


    /**
     * 判断字符串是否不为空
     */
    public static boolean isStringNotEmpty(String s) {
        return !isStringEmpty(s);
    }


    /**
     * 只要有一个为null 返回 true <br>
     * 注意，他的反义词是   所有都不为null  而不是 oneIsNotNull
     */
    public static boolean oneIsNull(Object... params) {
        if (params == null) {
            return true;
        }

        for (Object param : params) {
            if (param == null) {
                return true; //有一个为null 断言成功
            }
        }
        return false;
    }

    /**
     * 至少有一个非空 == !allIsNulls()
     */
    public static boolean atLeastOneIsNotNull(Object... params) {
        return !allIsNulls(params);
    }

    /**
     * 所有参数均为null 返回 true
     */
    public static boolean allIsNulls(Object... params) {
        if (params == null) {
            return true;
        }

        for (Object param : params) {
            if (param != null) { //有一个不为null assert失败
                return false;
            }
        }
        return true;
    }

    /**
     * 所有参数都为null return true<br>
     * 只要有一个为空断言失败, 语义 = !oneIsNull (没有一个为空)
     */
    public static boolean allIsNotNulls(Object... params) {
        return !oneIsNull(params);
    }


    /**
     * parent=http://xxxx/ <br>
     * sub = /abc/def <br>
     * result:http://xxxx/abc/def <br>
     */
    public static String appendUrl(String parent, String sub) {
        int parentLastIdx = parent.lastIndexOf('/');
        int subFirstIdx = sub.indexOf('/');
        if (parentLastIdx == parent.length() - 1) { // parent 以 /结尾
            if (subFirstIdx == 0) {
                // sub 以 /开头 去掉一个
                sub = sub.substring(1);
            }
        } else {
            if (subFirstIdx != 0) {
                // sub 也没有 /开头
                parent = parent + "/";
            }
        }
        return parent + sub;
    }

    /**
     * 拼接成get传参的queryString <br>
     * eg:<br>
     * <blockquote>
     * concatQueryString(2, "name","hh","age",18) <br>
     * return ?name=hh&age=18 <br>
     * </blockquote>
     *
     * <blockquote>
     * concatQueryString(1, "name","hh","age",18) <br>
     * return ?name=hh <br>
     * </blockquote>
     *
     * @param numsOfKey 多少个key
     */
    public static String concatQueryString(int numsOfKey, Object... args) {
        if (numsOfKey <= 0 || isEmpty(args)) {
            // 如果指定了0个key,或者arg为空返回空的queryString
            return "";
        }

        // TODO encode ?

        if (args[0] instanceof Map) {
            // TODO
        }

        StringBuilder queryString = new StringBuilder();

        if (numsOfKey > args.length / 2) {
            numsOfKey = args.length / 2;
        }

        boolean init = false;

        for (int i = 0; i < numsOfKey * 2; i++) {

            // key = null or value = null
            if (args[i] == null || args[i + 1] == null) {
                i++;
                continue;
            }

            String key = args[i].toString();
            String value = args[i + 1].toString();

            if (!init) {
                queryString.append("?");
                init = true;
            } else {
                queryString.append("&");
            }
            queryString.append(key).append("=").append(value);
            i++;
        }

        return queryString.toString();
    }

    /**
     * 把异常信息中的root cause和cause by信息返回 给前端浏览
     */
    public static String getCauseToBrowser(Exception e) {
        if (e != null) {
            StringBuilder sb = new StringBuilder();
            e.printStackTrace();
            sb.append(e.getClass().getName() + ":" + e.getMessage());
            Throwable cause = e.getCause();
            for (int i = 0; i < 10; i++) {
                if (cause == null) {
                    break;
                }
                String message = cause.getMessage();
                sb.append(" <br>    cause by:").append(cause.getClass().getName() + ":" + message);
                cause = cause.getCause();
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * @see #mkString(String, Object...)
     */
    public static String mkString(Object... arr) {
        return mkString(",", arr);
    }

    /**
     * <blockquote><pre>
     *     mkString("[", ",", "]","张三","李雷","韩梅梅") ==> [张三,李雷,韩梅梅]
     * </pre></blockquote>
     */
    public static String mkString(String pre, String middle, String suffix, Object... arr) {
        if (isStringEmpty(middle)) {
            middle = ",";
        }
        if (isStringEmpty(pre)) {
            pre = "[";
        }
        if (isStringEmpty(suffix)) {
            suffix = "]";
        }
        StringBuilder sb = new StringBuilder(100);
        sb.append(pre);
        sb.append(mkString(middle, arr));
        return sb.append(suffix).toString();
    }

    /**
     * <blockquote><pre>
     *     mkString("," , "张三", "李四","王五") ==> 张三,李四,王五
     * </pre></blockquote>
     */
    public static String mkString(String split, Object... arr) {
        if (isEmpty(arr)) {
            return "";
        }
        StringBuilder sb = new StringBuilder(100);
        if (split == null) {
            split = ",";
        }
        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                if (i != 0) {
                    sb.append(split);
                }
                sb.append(arr[i]);
            }
        }
        return sb.toString();
    }


    /**
     * 主要是为了得到当前方法名和类名相关信息，以及行号
     */
    public static StackTraceElement getClassInfo() {
        return new Throwable().getStackTrace()[1];
    }

    /**
     * 本方法是第0层,StackTraceElement[]是倒着来的，main方法属于最底层，方法调用的终点属于第0层
     */
    public static StackTraceElement getClassInfo(int depth) {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (depth <= 0) {
            depth = 0;
        } else if (depth >= stackTrace.length) {
            depth = stackTrace.length - 1;
        }
        return stackTrace[depth];
    }

    public static String getClassAndMethod() {
        StackTraceElement classInfo = getClassInfo(2);
        return classInfo.getClassName() + "." + classInfo.getMethodName();
    }

    /**
     * 本方法属于第一层<br>
     * 返回诸如: at ....util.CommonUtil.main(CommonUtil.java:283)  的字符串<br>
     * <blockquote><pre>
     * 		内部调用：getLineInfo() 应该传入参数是0, 因为,getLineInfo方法本身所处的是1,其他方法调用,当然是0了
     * 	外部调用....util.CommonUtil#getLineInfo(0) 也是传入参数0,然后返回的是调用类的行号信息
     * </pre></blockquote>
     *
     * @param depth 获取哪个类的StackTraceElement
     */
    public static String getLineInfo(int depth) {
        StackTraceElement classInfo = getClassInfo(depth);
        return classInfo.getClassName() + "." + classInfo.getMethodName()
                + "(" + classInfo.getFileName() + ":" + classInfo.getLineNumber() + ")";
    }

    /**
     * 关流
     */
    public static void closeQuietly(AutoCloseable... closeables) {
        if (closeables != null && closeables.length > 0) {
            for (AutoCloseable closeable : closeables) {
                if (closeable != null) {
                    try {
                        closeable.close();
                        log.info("Closed {} at {}",
                                closeable.getClass().getName(), getLineInfo(3));//本方法属于第二层
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }
    }


    /**
     * 计算耗时 格式： Takes time: xxx s xxx ms;
     */
    public static String countTime(long startTime) {
        long countTime = System.currentTimeMillis() - startTime;
        return formatBraces("Takes time: {}s {}ms;", countTime / 1000, countTime % 1000);
    }

    public static final Pattern sPattern = Pattern.compile("\\$\\{\\}|\\$|\\{\\}|%|\\?");

    /**
     * 模拟scala的字符串插值 s"name is $name" <br>
     * java编译器不支持所以只能 format("name is $",name) <br>
     * e.g: <br>
     * <blockquote><pre>
     * 			format("My name is $","jack");
     * 			format("My name is %","jack");
     * 			format("My name is ?","jack");
     * 			format("My name is {}","jack");
     * 			format("My name is ${}","jack");
     *
     * 			return "My name is jack"; <br>
     *         </blockquote></pre>
     * <p>
     * 占位符可以是: $ % {} ${} ? 最好只有一种并且不支持{name}这种<br>
     * 如果要增加占位符种类请修改{@link #sPattern} <br>
     */
    public static String format(String s, Object... args) {
        if (isEmpty(args)) {
            return s;
        }
        Matcher m = sPattern.matcher(s);
        StringBuilder sb = new StringBuilder();
        int left = 0;
        int argIndex = 0;
        while (m.find()) {
            if (argIndex >= args.length) {
                break;
            }
            sb.append(s, left, m.start());
            sb.append(args[argIndex++]); // append arg
            left = m.end();
        }
        if (left != s.length()) {
            sb.append(s, left, s.length());
        }
        return sb.toString();
    }


    /**
     * 目的是如果我这样写   ? ? ?  不知道这三个问号代表什么，因为上下文不存在没法推断 <br>
     * 但是我这么写   ${field} ${operation} ${value}  意思就明确很多： field=value
     * <blockquote><pre>
     *     format$("select * from ${table} as t where t.${field} = 11","user","age")
     *     ==>  select * from user as t where t.age=11
     * </pre></blockquote>
     *
     * <br>
     * format$ 意味着参数占位符是 ${}
     */
    public static String format$(String messagePattern, Object... argArray) {
        return formatPlaceholder(messagePattern, "${", "}", argArray);
    }

    /**
     * Braces means { <br>
     * e.g: <br>
     * <blockquote><pre>
     *    formatBraces("select * from {} where {} = {}" , "user", "age", 18);
     *    result:  select * from user where age = 18;
     *  </blockquote></pre>
     *
     * <blockquote><pre>
     *    formatBraces("select * from {table} where {field} = {value}" , "user", "age", 18);
     *    result:  select * from user where age = 18;
     *  </blockquote></pre>
     *
     * <br>
     * formatBraces意味着参数占位符是大括号  {}
     */
    public static String formatBraces(String messagePattern, Object... argArray) {
        return formatPlaceholder(messagePattern, "{", "}", argArray);
    }


    /**
     * Q(question) means ? <br>
     * formatQ意味着参数占位符是问号 ?
     */
    public static String formatQ(String messagePattern, Object... argArray) {
        return formatPlaceholder(messagePattern, "?", "?", argArray);
    }

    /**
     * <blockquote>
     * formatPlaceholder("select * from ? where ? ? ?", "?", "?", "user", "age", "=", "18");
     * ==> select * from user where age = 18
     * </blockquote>
     * <blockquote><pre>
     *     formatPlaceholder("select * from ${table} where ${field} ${operation} ${value}",
     *     "${", "}", "user", "age", "=", "18") ;
     *     ==> select * from user where age = 18
     * </pre></blockquote>
     *
     * @param messagePattern 带占位符的string字符串
     * @param preDelim       可以是是 ${ , abc, def, #, $之类的单个多个字符
     * @param endDelim       同上
     * @param argArray       参数列表
     */
    public static String formatPlaceholder(String messagePattern, String preDelim, String endDelim, Object... argArray) {
        if (isEmpty(argArray)) {
            return messagePattern;
        }

        if (preDelim == null || preDelim.length() == 0) {
            preDelim = "${";
        }
        if (endDelim == null || endDelim.length() == 0) {
            endDelim = "}";
        }

        int preDelimLen = preDelim.length();
        int endDelimLen = endDelim.length();
        boolean preEqualEnd = false;
        if (preDelim.equals(endDelim)) { //如果是相同的  ? & $ $$ 等pre==sufix
            preEqualEnd = true;
        }
        int i = 0; // 第几个{}占位符的开始索引
        int j; // 占位符{}的索引位置
        StringBuilder sbuf = new StringBuilder(50);
        for (int l = 0; l < argArray.length; l++) {
            j = messagePattern.indexOf(preDelim, i);
            if (j == -1) { //如果没有占位符，直接返回
                if (l == 0) {
                    return messagePattern;
                }
                break;
            } else {
                // indexOf函数包含fromIndex
                int endIdx;
                if (preEqualEnd) {
                    endIdx = j;
                } else {
                    endIdx = messagePattern.indexOf(endDelim, j + preDelimLen);
                }

                if (endIdx == -1) {
                    //说明只有  ${ 后面一半没有了，不把他当作参数占位
                    break;
                }

                int middlePreDelimIdx = messagePattern.indexOf(preDelim, j + preDelimLen);
                if (middlePreDelimIdx < endIdx && middlePreDelimIdx > j) {
                    j = middlePreDelimIdx;
                    //${ ${} 避免  跳过那个不全的占位符
                }

                sbuf.append(messagePattern, i, j); //append 非参数部分. 不包含j号索引
                sbuf.append(argArray[l]); //append参数部分
                i = endIdx + endDelimLen; //下一个非占位符索引位置, 是}号索引+endDelimLen
            }
        }

        //最后 没有发现占位需要把尾部append进去
        if (i != messagePattern.length()) {
            sbuf.append(messagePattern, i, messagePattern.length());
        }
        return sbuf.toString();
    }


    public static void main(String[] args) {
        System.out.println("-----------------format$ -------------------");

        System.out.println(format$("${abc}${}", "1"));
        ;
        System.out.println(format$("${abc}${222} ${we}", 2));
        ;
        System.out.println(format$("${abc}${123", 2));
        System.out.println(format$("${abc} ${} ${123}", "a", "=", "b"));

        System.out.println("-----------------formatPlaceholder -------------------");

        System.out.println(formatPlaceholder("${{abc}}} ${{} ${{123}}}", "${{", "}}}", "a", "=", "b"));
        System.out.println(formatPlaceholder("select * from {{ where {{ {{ {{", "{{", "{{", "user", "a", "=", "b"));
        System.out.println(formatPlaceholder("select {{ from bb { {{ {{", "{{", "{{", "user", "a", "=", "b"));

        System.out.println("-----------------arrayFormat -------------------");

        System.out.println();
        System.out.println(mkString("{\"", "\",\"", "\"}", "张三", "李四", "王五"));


        System.out.println("null,abc one is null ? " + oneIsNull(null, "abc"));
        System.out.println("abc,def one is null ? " + oneIsNull(1, "def"));
        System.out.println("null,null one is null ? " + oneIsNull(null, null));

        System.out.println();


        System.out.println();

        System.out.println("---------------------------------------");


        String command = CommonUtil.format("cmd /c cd ? && install-all.bat 0 -P??",
                CommonUtil.appendDir("asd", "cigna\\bat"), "sit");
        System.out.println(command);

    }


    /**
     * is string matchRegex regex ?
     */
    public static boolean matchRegex(String s, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
        return m.find();
    }


    /**
     * 在数组中，是否包含了指定字符串？对大小写敏感
     *
     * @param arr
     * @param s
     * @return
     */
    public static boolean contains(String[] arr, String s) {
        return contains(Arrays.asList(arr), s, false);
    }


    public static boolean contains(List<String> list, String s) {
        return contains(list, s, false);
    }

    private static boolean contains(List<String> list, String value, boolean ignoreCase) {
        if (isEmpty(list)) {
            return false;
        } else {
            for (String s1 : list) {
                if (s1 != null) {
                    if (ignoreCase && s1.equalsIgnoreCase(value)) {
                        return true;
                    } else if (s1.equals(value)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean containsIgnoreCase(String[] arr, String s) {
        return contains(Arrays.asList(arr), s, true);
    }

    public static boolean containsIgnoreCase(List<String> list, String s) {
        return contains(list, s, true);

    }

//	-------------------- 以下方法快速创建一个集合, 省去了new 操作 以及add put操作 -------------------------------------------

    public static <T> List<T> wrapList(T... values) {
        List<T> list = new ArrayList<>();
        if (isEmpty(values)) {
            return list;
        }

        for (T value : values) {
            list.add(value);
        }
        return list;
    }

    public static <T> Set<T> wrapSet(T... values) {
        Set<T> set = new HashSet<>();

        if (isEmpty(values)) {
            return set;
        }

        for (T value : values) {
            set.add(value);
        }
        return set;
    }

    public static <K, V> Map<K, V> wrapMap(K key, V value) {
        Map<K, V> map = new HashMap<>(3);

        if (isEmpty(key)) { //key不能为null ""
            return map;
        }

        map.put(key, value);
        return map;
    }

    /**
     * 将目录进行拼接
     */
    public static String appendDir(String firstDir, String... subDirs) {
        if (CommonUtil.isEmpty(subDirs)) {
            return firstDir;
        }

        String replaceMent = "/";
        String fileSep = File.separator;
        if (firstDir.contains("/")) {
            fileSep = "/";
        } else {
            replaceMent = "\\\\";
        }

        firstDir = firstDir.replaceAll("[/|\\\\]+", replaceMent);
        StringBuilder sb = new StringBuilder(firstDir);
        String last = firstDir;
        for (String subDir : subDirs) {
            if (CommonUtil.isStringEmpty(subDir)) {
                continue;
            }
            subDir = subDir.replaceAll("[/|\\\\]+", replaceMent);
            if (last.lastIndexOf(fileSep) == firstDir.length() - 1) {// 以 /\结尾
                if (subDir.indexOf(fileSep) == 0) {
                    // 以 /\开头
                    subDir = subDir.substring(1);
                }
            } else {
                if (subDir.indexOf(fileSep) != 0) {
                    // 不以 /\开头
                    subDir = fileSep + subDir;
                }
            }
            sb.append(subDir);
            last = subDir;
        }
        return sb.toString();
    }

    // ------------------- assert 断言空 抛出异常 -------------------------

    /**
     * assert开头的就是会抛出异常
     */
    public static void assertOneIsEmpties(String msg, Object... param) {
        if (oneIsEmpties(param)) {
            throw new IllegalArgumentException(matchRegex(msg, "empty|null|空") ? msg : msg
                    + " can not be empty!!");
        }
    }

    /***/
    public static void assertAllIsEmpties(String msg, Object... param) {
        if (allIsEmpties(param)) {
            throw new IllegalArgumentException(matchRegex(msg, "empty|null|空") ? msg : msg
                    + " can not be empty!!");
        }
    }

    public static <T> T require(Function01 f, Class<T> clazz) {
        Object v = null;
        try {
            v = f.apply();
            if (clazz == Integer.class) {
                return (T) (Integer) Integer.parseInt(v.toString());
            }
        } catch (Exception e) {
            String msg = CommonUtil.format("将value:[{}] 转化为:{} 出错. {}", v, clazz.getName(), CommonUtil.getLineInfo(3));
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return null;

    }


}
