package smartbix.datamining.engine.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.DoubleType;
import org.apache.spark.sql.types.IntegerType;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StringType;
import org.apache.spark.sql.types.StructField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import au.com.bytecode.opencsv.CSVParser;
import smartbix.datamining.engine.execute.node.bean.Feature;
import smartbix.datamining.engine.execute.node.bean.Features;

/**
 * 与node节点相关的一些简化操作
 *
 * @author HuangHuai
 * @since 2018年7月31日
 */
@SuppressWarnings("all")
public class NodeUtil implements Serializable {
    private static Logger log = LoggerFactory.getLogger(NodeUtil.class);
    /**
     * 字符串拼接，带参数，默认参数占位符号{}
     */
    public static final String DEFAULT_DELIM_STR = "{}";
    public static final String SQL_DELIM_STR = "?"; // sql的参数占位符

    /**
     * 从前端的features json字符串中获取其中的values数组，也就是所谓的features
     */
    public static String[] getFeaturesArray(String featureString) {
        return getFeatures(featureString).toArray();
    }

    /**
     * 将json 的feature字符串解析为Features对象
     */
    public static Features getFeatures(String featureString) {
        return new Features(getFeatureList(featureString));
    }

    /**
     */
    public static List<Feature> getFeatureList(String featureString) {
        return new Gson().fromJson(featureString, new TypeToken<List<Feature>>() {
        }.getType());
    }

    /**
     * 判断是否为空: 支持集合,字符串<br>
     * 集合: null,size=0均为空<br>
     * 字符串:"null",null,"  " 均判断为空
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
        }
        return flag;
    }

    /**
     * 判断是否不为空<br>
     * 用法参考{@link #isEmpty(Object)}
     */
    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    /**
     * 判断是否为空,支持多个参数判断,只要有一个参数是空,即判断为空
     *
     * @see #isEmpty(Object)
     */
    public static boolean isEmpties(Object... objs) {
        if (objs == null || objs.length == 0) {
            return true; // null or length=0 is empty
        }
        for (Object o : objs) {
            if (isEmpty(o)) { // 只要有一条是空，这个多参数数组就是空
                return true;
            }
        }
        return false;
    }

    /**
     * 判断多个参数是否非空
     *
     * @see #isEmpties(Object...)
     * @see #isEmpty(Object)
     */
    public static boolean isNotEmpties(Object... objs) {
        return !isEmpties(objs);
    }

    /**
     * 判断字符串是否为空<br>
     * 定义：null "null" "" "  " 这四种情况都判断为空
     */
    public static boolean isStringEmpty(String s) {
        if (s == null) {
            return true; // null is empty
        }
        String str = s.trim();

        // length=0 is empty "null" is empty
        if (str.length() == 0 || str.equalsIgnoreCase("null")) {
            return true;
        }
        return false; // really is not empty return false
    }

    /**
     * 判断字符串是否不为空
     *
     * @see #isStringEmpty(Object)
     */
    public static boolean isStringNotEmpty(String s) {
        return !isStringEmpty(s);
    }

    /**
     * 补齐sql语句的参数 如： select * from table1 where id=? 就会将问号替换成argArray中的参数
     */
    public static String sqlParamFormat(String messagePattern, Object... argArray) {
        return arrayFormat(messagePattern, SQL_DELIM_STR, argArray);
    }

    public static String arrayFormat(String messagePattern, Object... argArray) {
        return arrayFormat(messagePattern, DEFAULT_DELIM_STR, argArray);
    }

    /**
     * @param messagePattern messagePattern
     * @param delim          delim
     * @param argArray       argArray
     * @return String
     */
    public static String arrayFormat(String messagePattern, String delim, Object... argArray) {
        if (isEmpty(argArray)) {
            return messagePattern;
        }

        if (delim == null) {
            delim = DEFAULT_DELIM_STR;
        }

        int i = 0; // 第几个{}占位符的开始索引
        int j; // 占位符{}的索引位置
        int delimLen = delim.length();
        StringBuilder sbuf = new StringBuilder(getArrayFormatCapacity(messagePattern, argArray));
        for (int l = 0; l < argArray.length; l++) {
            j = messagePattern.indexOf(delim, i);
            if (j == -1) {
                if (l == 0) {
                    return messagePattern;
                }
                break;
            } else {
                sbuf.append(messagePattern, i, j);
                sbuf.append(argArray[l]);
                i = j + delimLen;
            }
        }

        if (i != messagePattern.length()) {
            sbuf.append(messagePattern, i, messagePattern.length());
        }
        return sbuf.toString();
    }

    /**
     * 得到arrayFormat方法初始化容量，防止频繁扩容
     */
    private static int getArrayFormatCapacity(String messagePattern, Object[] argArray) {
        int capacity = messagePattern.length();
        if (argArray != null) {
            for (int i = 0; i < argArray.length; i++) {
                if (argArray[i] != null) {
                    capacity += argArray[i].toString().length();
                }
            }
        }
        return capacity;
    }

    /**
     * 指定分隔符为 ","
     *
     * @see #mkString(String[], String)
     */
    public static String mkString(String[] columns) {
        return mkString(columns, ",");
    }

    /**
     * 遍历数组，用指定分隔符分隔数组每个元素，组合成一个字符串
     */
    public static String mkString(String[] columns, String split) {
        StringBuilder sb = new StringBuilder();

        if (split == null) {
            split = ",";
        }

        for (int i = 0; i < columns.length; i++) {
            if (i != 0) {
                sb.append(split);
            }
            sb.append(columns[i]);
        }

        return sb.toString();
    }

    /**
     * 把array转换为string,目前仅仅用来做测试
     */
    public static String mkString(Object[] o) {
        StringBuilder sb = new StringBuilder();
        if (o != null) {
            for (int i = 0; i < o.length; i++) {
                if (i != 0) {
                    sb.append(",");
                }
                sb.append(o[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 保留指定名字的{@code #Feature},并保存到{@code #Features}中返回
     */
    public static Features saveAssignFeatures(Features features, String[] saveFeatures) {
        List<Feature> list = features.getFeatures();
        List<Feature> listResult = new ArrayList<>();

        for (String f : saveFeatures) {
            Feature feature = searchNameFromFeatureList(list, f);
            if (feature != null) {
                listResult.add(feature);
            }
        }
        return new Features(listResult);
    }

    /**
     * 从List中寻找指定名字的Feature并返回
     */
    private static Feature searchNameFromFeatureList(List<Feature> list, String featureName) {
        for (Feature feature : list) {
            if (feature.getKey().equals(featureName)) {
                return feature;
            }
        }
        return null;
    }

    /**
     * 读取csv文件前n条数据,并将它构造成dataset返回
     *
     * @param url          csv文件地址
     * @param numOfRecords 读取多少条数据
     * @param encoding     读取csv文件使用的编码
     * @param separator    csv文件的内容分隔符
     * @param fields       StructField
     * @return Dataset 返回构造好了的dataset
     */
    public static List<Row> createAssignNumOfDSRow(String url, int numOfRecords, String encoding, String separator,
                                                   List<StructField> fields) {
        CSVParser parser = new CSVParser(separator.charAt(0));
        List<String> dataList = getDataListFromUrl(url, encoding, numOfRecords);
        List<Object[]> tempRowData = new ArrayList<>();
        String[] headerLineArr = null;
        Integer columnSize = null;
        DataType[] dataTypes = null;

        for (String line : dataList) {
            try {
                String[] parseLineArr = parser.parseLine(line);

                // 第一行是csv头,初始化即可
                if (columnSize == null) {
                    columnSize = parseLineArr.length;
                    headerLineArr = parseLineArr;
                    dataTypes = new DataType[columnSize];
                    continue;
                }

                // 如果碰到某一行 split后个数不够，会造成数组越界
                if (parseLineArr.length < columnSize) {
                    String[] tempArr = new String[columnSize];
                    System.arraycopy(parseLineArr, 0, tempArr, 0, parseLineArr.length);
                    parseLineArr = tempArr;
                }

                // put到row中的数据,必须是新数组,不然所有row都共用一个数组了
                Object[] lineArr = new Object[columnSize];

                // 开始解析每个单元格的数据和其数据类型
                for (int i = 0; i < columnSize; i++) {
                    DataType dp = dataTypes[i];

                    String cell = parseLineArr[i];

                    // 1.
                    // 如果dataType==StringType说明已经至少有一个数据解析成的string,根据规则,这里数据类型就是string了
                    // 2.
                    // 不等于null才推断,不然不推断,因为即使只有一个值是integer,其他都是null,这列数据类型还是integer

                    if (!(dp instanceof StringType) && isStringNotEmpty(cell)) {
                        Double d = null;
                        try {
                            d = Double.parseDouble(cell);
                        } catch (Exception e) {
                            // 报错说明是字符串
                            dataTypes[i] = DataTypes.StringType;
                            log.info("Parsing Schema: column-->[index:{},name:{},value:{}] StringType", i,
                                    headerLineArr[i], cell);
                            lineArr[i] = cell;
                            continue;
                        }

                        // 走到这,说明至少是数字类型
                        // 现在需要判断是double还是integer
                        if (dp instanceof DoubleType) {
                            lineArr[i] = d;
                            continue;
                        }

                        if (cell.contains(".")) {
                            dataTypes[i] = DataTypes.DoubleType;
                            log.info("Parsing Schema: column-->[index:{},name:{},value:{}] DoubleType", i,
                                    headerLineArr[i], cell);
                            lineArr[i] = d;
                        } else {

                            // integer
                            if (!(dp instanceof IntegerType)) {
                                // 这个判断只是为了打日志
                                dataTypes[i] = DataTypes.IntegerType;
                                log.info("Parsing Schema: column-->[index:{},name:{},value:{}] IntegerType", i,
                                        headerLineArr[i], cell);
                            }
                            lineArr[i] = d.intValue();
                        }
                    }

                    if (isStringEmpty(cell)) {
                        cell = null; // 防止 空字符串干扰
                    }
                    lineArr[i] = cell;
                }
                tempRowData.add(lineArr);
            } catch (IOException e) {
                log.error("Error when parsing csv line and transform it to Row; Line is:" + line, e);
            }
        }
        log.info("dataTypes=" + mkString(dataTypes));
        for (int i = 0; i < columnSize; i++) {
            String columnName = headerLineArr[i];
            if (dataTypes[i] == null) {
                dataTypes[i] = DataTypes.StringType;
            }
            fields.add(new StructField(columnName, dataTypes[i], true, Metadata.empty()));
        }

        return reformatRowList(tempRowData, dataTypes);
    }

    /**
     * 当某列数据是这样的时候： id： 1,2,3,3.0,"aa" <br>
     * 那么前几位数会被解析成Integer，3.0会被解析成Double，但是最后一位是String <br>
     * 根据数据类型解析规则，只要有一个字符串，就都是字符串，所以最终类型为String <br>
     * 但是前面的数却是Integer，就会有(String)Integer转换错误 <br>
     * 或者(Double)Integer错误
     */
    private static List<Row> reformatRowList(List<Object[]> tempRowData, DataType[] dataTypes) {
        List<Row> rsList = new ArrayList<>();
        for (Object[] rowArr : tempRowData) {
            for (int i = 0; i < dataTypes.length; i++) {
                DataType dp = dataTypes[i];
                if (rowArr[i] != null) {
                    try {
                        if (dp instanceof StringType) {
                            rowArr[i] = rowArr[i].toString();
                        } else if (dp instanceof DoubleType) {
                            if (!(rowArr[i] instanceof Double)) {
                                rowArr[i] = Double.parseDouble(rowArr[i].toString());
                            }
                        } else if (dp instanceof IntegerType) {
                            if (!(rowArr[i] instanceof Integer)) {
                                rowArr[i] = Integer.parseInt(rowArr[i].toString());
                            }
                        }
                    } catch (Exception e) {
                        log.error(arrayFormat("Parsing [{},{}] to {} failed!", rowArr[i],
                                rowArr[i].getClass().getSimpleName(), dp), e);
                    }
                }
            }
            rsList.add(RowFactory.create(rowArr));
        }
        return rsList;
    }

    /**
     * 从url中获取文件流
     *
     * @param url          文件地址
     * @param encoding     读取文件使用的编码
     * @param numOfRecords 读取前多少行
     */
    private static List<String> getDataListFromUrl(String url, String encoding, int numOfRecords) {
        List<String> dataList = new ArrayList<>();
        if (isStringEmpty(url)) {
            return dataList;
        }

        int recordCount = 0;
        FileSystem fs = null;
        FSDataInputStream in = null;
        BufferedReader reader = null;
        try {
            if (isHdfsUrl(url)) {
                fs = HdfsUtil.getHadoopFS(HdfsUtil.getHdfsServiceUrl(url), null);
                in = fs.open(new Path(url));
                String line = null;
                reader = new BufferedReader(new InputStreamReader(in, encoding));
                while ((line = reader.readLine()) != null) {
                    dataList.add(line);
                    if (++recordCount > numOfRecords) {
                        break;
                    }
                }
            } else {
                // local file system
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(url), encoding));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    dataList.add(line);
                    if (++recordCount > numOfRecords) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Read data from url failed!! URL:" + url, e);
        } finally {
            closeQuietly(reader, in, fs);
        }
        return dataList;
    }

    /**
     * 关流
     */
    public static void closeQuietly(Closeable... closeables) {
        if (closeables != null && closeables.length > 0) {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }

            }
        }
    }

    public static final String HDFS_URL_REGEX = "^(((?i)hdfs)://)[\\w]{1,}(\\.[\\w]{1,}){1,}[:]{1}(\\d){1,}.*$";

    /**
     * 判断是否是hdfs的url <br>
     * 参考网址：https://www.cnblogs.com/nizuimeiabc1/p/5869673.html
     * ////这种情况也会判断true，所以要自己替换
     */
    public static boolean isHdfsUrl(String url) {
        boolean matches = url.matches(HDFS_URL_REGEX);
        log.info("IsHdfsUrl:{} url:{}", matches, url);
        return matches;
    }

    /**
     * 把list转换为array newType：为array的类型
     */
    public static <T> T[] listToArray(List<T> fields, Class<T> newType) {
        T[] copy = (newType == Object.class) ? (T[]) new Object[fields.size()]
                : (T[]) Array.newInstance(newType, fields.size());
        for (int i = 0; i < copy.length; i++) {
            copy[i] = fields.get(i);
        }
        return copy;
    }

    /**
     * 将json字符串解析成指定类型的对象
     */
    public static <T> T json2Obj(String jsonStr, Class<T> type) {
        return new Gson().fromJson(jsonStr, type);
    }

    /**
     * 将对象转换为json字符串
     */
    public static String obj2Str(Object obj) {
        return new Gson().toJson(obj);
    }

    /**
     * 从一个字符串解析成double, 本方法不适合大数据量解析
     */
    public static Double parseDouble(String value) {
        if (isStringEmpty(value)) {
            return null;
        }
        value = value.replaceAll("\\s", "");

        double d = 0;
        if (value.contains("%")) {
            d = Double.parseDouble(value.replaceAll("%", "")) / 100.0;
        } else {
            d = Double.parseDouble(value);
        }
        return d;
    }

    /**
     * 数据源: [{field:id,order:asc},{field:name,order:desc},...] <br>
     * 如果我传入: key=field <br>
     * 得到结果: [id,name] <br>
     * <br>
     * 如果我传入: key=order <br>
     * 得到结果: [asc,desc] <br>
     */
    public static List<String> getValuesFromArrayMapByKey(List<Map<String, Object>> source, String key) {
        List<String> list = new ArrayList<>();
        for (Map<String, Object> map : source) {
            Object o = map.get(key);
            list.add(o == null ? null : o.toString());
        }
        return list;
    }

    /***/
    public static void throwIllegalArgsException(String msg) {
        throwIllegalArgsException(msg, null);
    }

    /**
     * 参数判断不符合后抛异常
     */
    public static void throwIllegalArgsException(String msg, Object... args) {
        String message = arrayFormat(msg, args);
        log.error(message);
        throw new IllegalArgumentException(message);
    }

    /**
     * 计算耗时 格式： Takes time: xxx s xxx ms;
     */
    public static String countTime(long startTime) {
        long countTime = System.currentTimeMillis() - startTime;
        return arrayFormat("Takes time: {}s {}ms;", countTime / 1000, countTime % 1000);
    }


    /**
     * 根据数据类型创建clickhouse的inset value sql
     */
    public static String createClickHouseSqlValue(Row r) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        StructField[] fields = r.schema().fields();
        for (int i = 0; i < fields.length; i++) {
            try {
                DataType dataType = fields[i].dataType();
                Object o = r.get(i);
                if (o == null) {
                    o = "";
                }
                if (i != 0) {
                    sb.append(",");
                }
                if (dataType instanceof StringType) {
                    sb.append("'").append(o).append("'");
                } else {
                    sb.append(o);
                }
            } catch (Exception e) {
                log.error("row=" + r.mkString(","), e);
            }
        }
        sb.append(")");
        return sb.toString();

    }

    public static final Pattern sPattern = Pattern.compile("\\$\\{\\}|\\$|\\{\\}|%|\\?");

    /**
     * 模拟scala的字符串插值   s"name is $name"  <br>
     * java编译器不支持所以只能  s("name is $",name) <br>
     * 占位符可以是: $ % {} ${} ?     <br>
     * 如果要增加占位符种类请修改{@link #sPattern)
     */
    public static String s(String s, Object... args) {
        if (isEmpty(args)) {
            return s;
        }
        Matcher m = sPattern.matcher(s);
        StringBuilder sb = new StringBuilder();
        int left = 0;
        int argIndex = 0;
        while (m.find()) {
            sb.append(s, left, m.start());
            sb.append(args[argIndex++]); // append arg
            left = m.end();
        }
        if (left != s.length()) {
            sb.append(s, left, s.length());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String s = "into $.% (?) values ({}) ${}";


        String s1 = s(s, "default", "ontime", "name,age", "huanghuai,18", 1);
        System.out.println(s1);

    }
}