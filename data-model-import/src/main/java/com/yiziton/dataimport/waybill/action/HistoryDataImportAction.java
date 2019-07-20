package com.yiziton.dataimport.waybill.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiziton.dataimport.waybill.bean.SysConfigInfo;
import com.yiziton.dataimport.waybill.dao.ext.SysConfigInfoExtMapper;
import com.yiziton.dataimport.waybill.service.HistoryDataImportService;
import com.yiziton.dataweb.core.annotation.Action;
import com.yiziton.dataweb.waybill.vo.ImportConditionVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 导入历史数据
 *
 * @author kdh
 * @version 1.0
 * @date 2019年01月18日
 */
@Action("historyDataImport")
@Slf4j
public class HistoryDataImportAction {

    @Value("${historyDataImport.scmPassport}")
    String scmPassport;
    @Value("${historyDataImport.scmDomain}")
    String scmDomain;
    @Value("${historyDataImport.cmpAuthorization}")
    String cmpAuthorization;
    @Value("${historyDataImport.cmp}")
    String cmp;
    @Value("${historyDataImport.ips}")
    String ips;

    String scmAuthorzization, user = "", password = "";
    boolean newConfig = false;
    long sessionStart = 0L, sessionInterval = 2 * 60 * 60 * 1000L;

    @Autowired
    HistoryDataImportService historyDataImportService;
    @Autowired
    SysConfigInfoExtMapper sysConfigInfoExtMapper;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 封装HTTP GET方法
     * 有参数的Get请求
     *
     * @param
     * @param
     * @return
     * @throws ClientProtocolException
     * @throws java.io.IOException
     */
    public static String get(String url, Map<String, Object> map) throws ClientProtocolException, IOException {
        String body = "";
        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet();
        //装填参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
            }
        }
        //设置参数到请求对象中
        String param = URLEncodedUtils.format(nvps, "UTF-8");
        httpGet.setURI(URI.create(url + "?" + param));
        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpGet);
        //获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, "utf-8");
        }
        EntityUtils.consume(entity);
        //释放链接
        response.close();
        return body;
    }

    /**
     * 获得响应HTTP实体内容
     *
     * @param response
     * @return
     * @throws java.io.IOException
     * @throws java.io.UnsupportedEncodingException
     */
    private static String getHttpEntityContent(HttpResponse response) throws IOException, UnsupportedEncodingException {
        //通过HttpResponse 的getEntity()方法获取返回信息
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream is = entity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line + "\n");
                line = br.readLine();
            }
            br.close();
            is.close();
            return sb.toString();
        }
        return "";
    }

    /**
     * 封装HTTP POST方法
     * 有参数的Post请求
     *
     * @param url      资源地址
     * @param map      参数列表
     * @param encoding 编码
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static String post(String url, Map<String, Object> map, String encoding, String contentType, String authorization) throws ParseException, IOException {
        String body = "";

        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        //装填参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
            }
        }
        //设置参数到请求对象中
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));//Content-type = "application/x-www-form-urlencoded";

        log.info("请求地址：" + url + "；请求参数：" + nvps.toString() + "；请求体：" + httpPost.getEntity().toString());

        //设置header信息
        //指定报文头【Content-type】、【Authorization】
        if (contentType != null) {
            httpPost.setHeader("Content-type", contentType);
        }
        if (authorization != null) {
            httpPost.setHeader("Authorization", authorization);
        }
        log.info("Content-type：" + contentType + "；Authorization：" + authorization + "；请求头：" + httpPost.getAllHeaders().toString());
        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        //获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, encoding);
        }
        EntityUtils.consume(entity);
        //释放链接
        response.close();
        return body;
    }

    /**
     * 模拟请求
     *
     * @param url           资源地址
     * @param string        参数列表
     * @param encoding      编码
     * @param contentType
     * @param authorization
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static String post(String url, String string, String encoding, String contentType, String authorization) throws ParseException, IOException {
        String body = "";

        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        //装填参数
        StringEntity stringEntity = null;
        if (string != null) {
            // 构建消息实体
            stringEntity = new StringEntity(string, Charset.forName("UTF-8"));
            stringEntity.setContentEncoding("UTF-8");
            // 发送Json格式的数据请求
            stringEntity.setContentType("application/json");
        }

        //设置参数到请求对象中
        httpPost.setEntity(stringEntity);

        log.info("请求地址：" + url + "；请求参数：" + stringEntity.toString());

        //设置header信息
        //指定报文头【Content-type】、【Authorization】
        if (contentType != null) {
            httpPost.setHeader("Content-type", contentType);
        }
        if (authorization != null) {
            httpPost.setHeader("Authorization", authorization);
        }
        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        //获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, encoding);
        }
        EntityUtils.consume(entity);
        //释放链接
        response.close();
        return body;
    }

    /**
     * 模拟请求
     *
     * @param url      资源地址
     * @param map      参数列表
     * @param encoding 编码
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static String send(String url, Map<String, String> map, String encoding) throws ParseException, IOException {
        String body = "";

        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        //装填参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        //设置参数到请求对象中
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));

        log.info("请求地址：" + url + "；请求参数：" + nvps.toString());

        //设置header信息
        //指定报文头【Content-type】、【Authorization】
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Authorization", "Basic V01TOmFjbWVzZWNyZXQ=");

        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        //获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, encoding);
        }
        EntityUtils.consume(entity);
        //释放链接
        response.close();
        return body;
    }

    public static void main(String[] args) throws ParseException, IOException {
        String url = "http://192.168.100.121:17800/oauth/token";
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", "18000000000");
        map.put("password", "888888");
        map.put("scope", "openid");
        map.put("grant_type", "password");
        String body = send(url, map, "utf-8");
        if (body != null) {
            Map parse = (Map) JSON.parse(body);
            Object accessToken = parse.get("access_token");
            log.info((String) accessToken);
        }
        log.info("交易响应结果：" + body);
    }

    /**
     * end 比 start 多的天数
     *
     * @param start
     * @param end
     * @return
     */
    public static int differentDays(Timestamp start, Timestamp end) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(start);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(end);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {//同一年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {//闰年
                    timeDistance += 366;
                } else {//不是闰年
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else {//不同年
            log.info("判断 day2 - day1 : " + (day2 - day1));
            return day2 - day1;
        }
    }

    /**
     * 将List<String>集合转化为以逗号隔开的String
     *
     * @param list
     * @return
     */
    public static String convertListToString(List<String> list) {
        StringBuffer sb = new StringBuffer();
        if (CollectionUtils.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    sb.append(list.get(i));
                } else {
                    sb.append(",").append(list.get(i));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 触发各系统导数据的接口
     *
     * @param importConditionVO
     * @throws Exception
     */
    public String dataImport(ImportConditionVO importConditionVO) throws Exception {
        if (importConditionVO.getOpenBillTimeBegin() == null && importConditionVO.getOpenBillTimeEnd() == null
                && (importConditionVO.getWaybillIds() == null || (importConditionVO.getWaybillIds() != null && importConditionVO.getWaybillIds().size() == 0))) {
            throw new RuntimeException("两个维度导数据：1、运单：数组形式，一个或多个；2、时间：开单开始时间和开单结束时间");
        }
        if (importConditionVO.getOpenBillTimeBegin() != null && importConditionVO.getOpenBillTimeEnd() != null
                && (importConditionVO.getWaybillIds() != null && importConditionVO.getWaybillIds().size() > 0)) {
            throw new RuntimeException("只能通过一个维度导数据");
        }
        if ((importConditionVO.getOpenBillTimeBegin() != null && importConditionVO.getOpenBillTimeEnd() == null)
                || (importConditionVO.getOpenBillTimeBegin() == null && importConditionVO.getOpenBillTimeEnd() != null)) {
            throw new RuntimeException("开单开始时间和开单结束时间两个条件要同时填写");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long start = System.currentTimeMillis();
        //SCM 获取授权
        getAuthorzization();

        /* 多线程获取数据开始 */
        log.info("多线程获取历史数据开始 : " + sdf.format(new Date()));
        ExecutorService executor = Executors.newCachedThreadPool();
        /* 获取SCM历史数据 */
        String scmString = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return scmDataImport(importConditionVO);
            }
        }).get();
        //log.info("获取 SCM 历史数据响应结果：" + scmString);//{"resultCode":"200"}
        /* 获取CMP历史数据 */
        String cmpString = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return cmpDataImport(importConditionVO);
            }
        }).get();
        //log.info("获取 CMP 历史数据响应结果：" + cmpString);//{}
        /* 获取IPS历史数据 */
        String ipsString = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return ipsDataImport(importConditionVO);
            }
        }).get();
        //log.info("获取 IPS 历史数据响应结果：" + ipsString);//"触发成功"
        /* 获取SCM历史流水数据 */
        String scmBillingString = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return billingDataImport(importConditionVO);
            }
        }).get();
        //log.info("获取 BILLING 历史流水数据响应结果：" + scmBillingString);
        /**************************************************************************************************************/
        executor.shutdown();
        // awaitTermination返回false即超时会继续循环，返回true即线程池中的线程执行完成主线程跳出循环往下执行，每隔10秒循环一次
        //while (!executor.awaitTermination(60, TimeUnit.SECONDS));//二选一
        while (!executor.isTerminated()) ;//二选一
        long end = System.currentTimeMillis();
        log.info("多线程获取历史数据结束 : " + sdf.format(new Date()) + "; 时 : " + (end - start));/* 多线程获取数据结束 */
        String result = "获取 SCM 历史数据响应结果: " + scmString + ";    " +
                "获取 CMP 历史数据响应结果: " + cmpString + ";    " +
                "获取 IPS 历史数据响应结果: " + ipsString + ";    " +
                "获取 BILLING 历史数据响应结果: " + scmBillingString;
        log.info(result);
        return result;
    }

    private void getAuthorzization() throws IOException {
        // 获取配置表中的配置
        SysConfigInfo userConfig = sysConfigInfoExtMapper.selectByConfigKey("user");
        SysConfigInfo passwordConfig = sysConfigInfoExtMapper.selectByConfigKey("password");
        if (userConfig != null && passwordConfig != null) {
            String userTemp = userConfig.getConfigValue();
            String passwordTemp = passwordConfig.getConfigValue();
            if (!(user.equals(userTemp) && password.equals(passwordTemp))) {
                user = userTemp;
                password = passwordTemp;
                newConfig = true;
            }
        }
        if((StringUtils.isBlank(user) || StringUtils.isBlank(password))){
            throw new RuntimeException("用户名或密码不能为空");
        }
        //SCM 获取授权
        String urlScmPassport = scmPassport + "/oauth/token";
        if (scmAuthorzization == null || (System.currentTimeMillis() - sessionStart) > sessionInterval || newConfig) {
            Map scmAuthorization = getScmAuthorization();
            String contentType = "application/x-www-form-urlencoded";
            String authorization = "Basic V01TOmFjbWVzZWNyZXQ=";//"Basic V01TOmFjbWVzZWNyZXQ=";
            String scmBody = post(urlScmPassport, scmAuthorization, "utf-8", contentType, authorization);
            log.info("获取 SCM 授权响应结果：" + scmBody);
            if (scmBody != null) {
                Map parse = (Map) JSON.parse(scmBody);
                scmAuthorzization = parse.get("access_token") != null ? ("Bearer " + (String) parse.get("access_token")) : null;
                if(scmAuthorzization == null){
                    throw new RuntimeException(parse.get("error_description") != null ? (String) parse.get("error_description") : null);
                }
            }
            log.info("SCM 授权 : " + scmAuthorzization);
            //{"error":"unauthorized","error_description":"用户名和密码不能为空"}
            //{"error":"unauthorized","error_description":"该用户没有使用该系统的权限"}
            sessionStart = System.currentTimeMillis();
            newConfig = false;
        }
    }

    private String scmDataImport(ImportConditionVO importConditionVO) throws IOException {
        //SCM 获取授权
        getAuthorzization();

        String contentType = "application/json";
        // SCM 获取历史流水数据
        String urlScm = scmDomain + "/api/order/sysDisWorkerOldData";
        String scm = scm(importConditionVO);
        return post(urlScm, scm, "utf-8", contentType, scmAuthorzization);
    }

    private String cmpDataImport(ImportConditionVO importConditionVO) throws IOException {
        String contentType = "application/json";
        //CMP 获取授权
        //String urlCmpLogin = cmp + "/login";
        //CMP 获取历史数据
        String urlCmp = cmp + "/api.do";
        /*String cmpAuthorization = getCmpAuthorization();
        contentType = "application/json";
        String cmpBody = post(urlCmpLogin, cmpAuthorization, "utf-8", contentType, null);
        log.info("获取 CMP 授权响应结果：" + cmpBody);
        String cmpAuthorization = null;
        if (cmpBody != null) {
            Map parse = (Map) JSON.parse(cmpBody);
            cmpAuthorization = parse.get("jwt") != null ? ("Bearer " + (String) parse.get("jwt")) : null;
        }
        log.info("CMP 授权 : " + cmpAuthorization);*/
        String cmp = cmp(importConditionVO);
        return post(urlCmp, cmp, "utf-8", contentType, cmpAuthorization);
    }

    private String ipsDataImport(ImportConditionVO importConditionVO) throws IOException {
        String contentType = "application/x-www-form-urlencoded";
        // IPS 获取历史数据
        String urlIpsByTime = ips + "/taskToDataTriggerController/trigger";
        String urlIpsByWaybillId = ips + "/oldTaskToDataController/transferDataByTitle";
        Map ips = ips(importConditionVO);
        if (importConditionVO.getWaybillIds() != null && importConditionVO.getWaybillIds().size() > 0) {
            //return get(urlIpsByWaybillId, ips);
            return post(urlIpsByWaybillId, ips, "utf-8", contentType, null);
        } else {
            //return get(urlIpsByTime, ips);
            return post(urlIpsByTime, ips, "utf-8", contentType, null);
        }
    }

    public String billingDataImport(ImportConditionVO importConditionVO) throws Exception {
        //SCM 获取授权
        getAuthorzization();

        // SCM 获取历史流水数据
        String urlScmBilling = scmDomain + "/api/finance/sendFlowToKafkaByOrderAddTime";
        String contentType = "application/json";
        //String scmBilling = scmBilling(importConditionVO);
        //return post(urlScmBilling, scmBilling, "utf-8", finalContentType, finalAuthorization);
        //流水一次性不能导太多，需要分割，暂时一次时间间隔控制在半个月
        if (importConditionVO.getOpenBillTimeBegin() != null && importConditionVO.getOpenBillTimeEnd() != null) {
            Timestamp openBillTimeBegin = importConditionVO.getOpenBillTimeBegin();
            Timestamp openBillTimeEnd = importConditionVO.getOpenBillTimeEnd();
            long openBillTimeBeginTimes = openBillTimeBegin.getTime(), beginTime = 0L, endTime = 0L;
            int interval = 15, days = differentDays(openBillTimeBegin, openBillTimeEnd), length = days / interval + (days % interval == 0 ? 0 : 1), cnt = 0;
            String msg = "";
            ImportConditionVO importConditionVO1 = new ImportConditionVO();
            for (int i = 0; i < length; i++) {
                beginTime = openBillTimeBeginTimes + (interval * 24 * 60 * 60 * 1000L * i);
                importConditionVO1.setOpenBillTimeBegin(new Timestamp(beginTime));
                if (i == length - 1) {
                    importConditionVO1.setOpenBillTimeEnd(openBillTimeEnd);
                } else {
                    endTime = openBillTimeBeginTimes + (interval * 24 * 60 * 60 * 1000L * (i + 1));
                    importConditionVO1.setOpenBillTimeEnd(new Timestamp(endTime));
                }
                // 逻辑删除流水
                log.info("按照时间导流水数据前把流水相关数据逻辑删除 开始");
                updateBillingAndFee(importConditionVO);
                log.info("按照时间导流水数据前把流水相关数据逻辑删除 结束");
                String region = simpleDateFormat.format(importConditionVO1.getOpenBillTimeBegin()) + " ~ " + simpleDateFormat.format(importConditionVO1.getOpenBillTimeEnd());
                String billing = billing(importConditionVO1);
                String result = post(urlScmBilling, billing, "utf-8", contentType, scmAuthorzization);//{"resultCode":"200"}
                if (!result.equals("{\"resultCode\":\"200\"}")) {
                    msg += region + " 报错信息为 : " + result + " \n";
                    log.info("流水报错" + msg);
                    cnt++;
                }
            }
            if (cnt > 0) {
                return "{\"resultCode\":\"200\",\"msg\":\"部分时间段导入流水报错：" + msg + "\"}";
            }
            return "{\"resultCode\":\"200\"}";
        } else {
            // 逻辑删除流水
            log.info("按照运单号导流水数据前把流水相关数据逻辑删除 开始");
            updateBillingAndFee(importConditionVO);
            log.info("按照运单号导流水数据前把流水相关数据逻辑删除 结束");
            String billing = billing(importConditionVO);
            return post(urlScmBilling, billing, "utf-8", contentType, scmAuthorzization);
        }
    }

    /**
     * 逻辑删除流水
     *
     * @param importConditionVO
     */
    private void updateBillingAndFee(ImportConditionVO importConditionVO) throws Exception {
        historyDataImportService.updateBillingAndFee(importConditionVO);
    }

    /**
     * 固定值，不需要修改
     *
     * @return
     */
    public Map getScmAuthorization() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", user);//用户名
        map.put("password", password);//密码
        map.put("scope", "openid");//权限
        map.put("grant_type", "password");//获取token模式
        log.info("scm 授权参数 : " + map.toString());
        return map;
    }

    /**
     * 固定值，不需要修改
     *
     * @return
     */
    public String getCmpAuthorization() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", user);//用户名
        jsonObject.put("password", password);//密码
        log.info(jsonObject.toString());
        log.info("cmp 授权参数 : " + jsonObject.toString());
        return jsonObject.toString();
    }

    /**
     * 拼接参数
     *
     * @param importConditionVO
     * @return
     */
    public String scm(ImportConditionVO importConditionVO) {
        JSONObject jsonObject = new JSONObject();
        if (importConditionVO.getWaybillIds() != null && importConditionVO.getWaybillIds().size() > 0) {
            jsonObject.put("waybillIds", importConditionVO.getWaybillIds());//多运单号
        }
        if (importConditionVO.getOpenBillTimeBegin() != null) {
            jsonObject.put("startTime", simpleDateFormat.format(importConditionVO.getOpenBillTimeBegin()));//起始日期
        }
        if (importConditionVO.getOpenBillTimeEnd() != null) {
            jsonObject.put("endTime", simpleDateFormat.format(importConditionVO.getOpenBillTimeEnd()));//结束日期
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject);
        log.info("scm 导数参数 : " + jsonArray.toString());
        return jsonArray.toString();
    }

    /**
     * 拼接参数
     *
     * @param importConditionVO
     * @return
     */
    public String cmp(ImportConditionVO importConditionVO) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "batchHandleDataController.batchSyncWaybillNodeData");//api名称
        jsonObject.put("token", "rt_1548407811000");//token
        JSONArray jsonArray = new JSONArray();
        Map<String, Object> argsMap = new HashMap<String, Object>();
        if (importConditionVO.getAgentCode() != null) {
            argsMap.put("agentCode", importConditionVO.getAgentCode());//承运商code
        }
        if (importConditionVO.getWaybillIds() != null && importConditionVO.getWaybillIds().size() > 0) {
            argsMap.put("waybillIdList", importConditionVO.getWaybillIds());//运单号
        }
        if (importConditionVO.getOpenBillTimeBegin() != null) {
            argsMap.put("beginDate", simpleDateFormat.format(importConditionVO.getOpenBillTimeBegin()));//起始日期
        }
        if (importConditionVO.getOpenBillTimeEnd() != null) {
            argsMap.put("endDate", simpleDateFormat.format(importConditionVO.getOpenBillTimeEnd()));//结束日期
        }
        jsonArray.add(argsMap);
        jsonObject.put("args", jsonArray);//起始日期
        log.info("cmp 导数参数 : " + jsonObject.toString());
        return jsonObject.toString();
    }

    /**
     * 拼接参数
     *
     * @param importConditionVO
     * @return
     */
    public JSONObject ips(ImportConditionVO importConditionVO) {
        JSONObject jsonObject = new JSONObject();
        if (importConditionVO.getWaybillIds() != null && importConditionVO.getWaybillIds().size() > 0) {
            jsonObject.put("title", convertListToString(importConditionVO.getWaybillIds()));//多运单号
        }
        if (importConditionVO.getOpenBillTimeBegin() != null) {
            jsonObject.put("startTime", simpleDateFormat.format(importConditionVO.getOpenBillTimeBegin()));//起始日期
        }
        if (importConditionVO.getOpenBillTimeEnd() != null) {
            jsonObject.put("endTime", simpleDateFormat.format(importConditionVO.getOpenBillTimeEnd()));//结束日期
        }
        log.info("ips 导数参数 : " + jsonObject.toString());
        return jsonObject;
    }

    /**
     * 拼接参数
     *
     * @param importConditionVO
     * @return
     */
    public String billing(ImportConditionVO importConditionVO) {
        JSONObject jsonObject = new JSONObject();
        if (importConditionVO.getWaybillIds() != null && importConditionVO.getWaybillIds().size() > 0) {
            jsonObject.put("waybillIds", importConditionVO.getWaybillIds());//多运单号
        }
        if (importConditionVO.getOpenBillTimeBegin() != null) {
            jsonObject.put("createBeginTime", simpleDateFormat.format(importConditionVO.getOpenBillTimeBegin()));//起始日期
        }
        if (importConditionVO.getOpenBillTimeEnd() != null) {
            jsonObject.put("createEndTime", simpleDateFormat.format(importConditionVO.getOpenBillTimeEnd()));//结束日期
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject);
        log.info("billing 导数参数 : " + jsonArray.toString());
        return jsonArray.toString();
    }

    public String compareBilling(ImportConditionVO importConditionVO) throws Exception {

        //SCM 获取授权
        getAuthorzization();

        //获取当前开单时间区域内所有的运单
        List<String> list = null;
        if (importConditionVO.getWaybillIds() != null && importConditionVO.getWaybillIds().size() > 0) {
            list = importConditionVO.getWaybillIds();
        } else {
            list = historyDataImportService.selectAllId(importConditionVO);
        }
        if (list != null && list.size() > 0) {
            int size = list.size(), interval = 5000, length = size / interval + (size % interval > 0 ? 1 : 0), start = 0, end = 0;
            List<String> subList = null;
            Map total = null, selectTotal = null;
            ImportConditionVO importConditionVO1 = null;
            String totalString = null;
            String msg = "";
            int cnt = 0;
            for (int i = 0; i < length; i++) {
                start = i * interval;
                if (i == length - 1) {
                    end = size;
                } else {
                    end = (i + 1) * interval;
                }
                subList = list.subList(start, end);
                selectTotal = selectTotalPrice(subList);
                List<String> waybillIds = new ArrayList<String>();
                if (selectTotal == null || selectTotal.size() == 0) {
                    waybillIds.addAll(subList);
                } else {
                    importConditionVO1 = new ImportConditionVO();
                    importConditionVO1.setWaybillIds(subList);
                    totalString = getBillingTotal(importConditionVO1);
                    log.info(totalString);
                    log.info(totalString.indexOf("\"resultCode\":\"200\"") + "");
                    if (totalString.indexOf("\"resultCode\":\"200\"") > 0) {
                        total = dealTotal(totalString);
                        for (String key : subList) {
                            if (total.get(key) == null) {
                                waybillIds.add(key);
                                continue;
                            }
                            if (selectTotal.get(key) == null) {
                                waybillIds.add(key);
                                continue;
                            }
                            Double t1 = (Double) total.get(key);
                            Double t2 = (Double) selectTotal.get(key);
                            if (!t1.equals(t2)) {
                                waybillIds.add(key);
                                continue;
                            }
                        }
                    } else {
                        return "BOSS 流水数据核查接口报错" + totalString;
                    }
                }
                if (waybillIds.size() > 0) {
                    log.info("流水金额对应不上,重新导数据 : " + waybillIds.size() + "个运单; " + waybillIds.toString());
                    importConditionVO1 = new ImportConditionVO();
                    importConditionVO1.setWaybillIds(waybillIds);
                    String result = billingDataImport(importConditionVO1);
                    if (!result.equals("{\"resultCode\":\"200\"}")) {
                        msg += waybillIds.toString() + " 报错信息为 : " + result + " \n";
                        log.info("流水金额对应不上,重新导数据结果 : " + waybillIds.toString() + " 报错信息为 : " + result);
                        cnt++;
                    }
                }
            }
            if (cnt > 0) {
                return "{\"resultCode\":\"200\",\"msg\":\"流水金额对应不上,重新导数据报错：" + msg + "\"}";
            }
        }
        return "流水数据核查完毕";
    }

    private Map selectTotalPrice(List<String> subList) throws Exception {
        Map map = historyDataImportService.selectTotalPrice(subList);
        return map;
    }

    private Map dealTotal(String total) {
        Map parse = (Map) JSON.parse(total);
        Map content = (Map) parse.get("content");
        if (content != null) {
            Set<String> keySet = content.keySet();
            for (String key : keySet) {
                Map map = (Map) content.get(key);
                Object consignor = map.get("consignor");
                Object carrier = map.get("carrier");
                Object serviceProvider = map.get("serviceProvider");
                Object collectionOnDeliveryFee = map.get("collectionOnDeliveryFee");//COLLECTION_ON_DELIVERY(5,"代收货款流水");
                Object foreightAtDestinationFee = map.get("foreightAtDestinationFee");//FOREIGHT_AT_DESTINATION(4,"到付运费流水"),
                double consignor1 = 0.00d, carrier1 = 0.00d, serviceProvider1 = 0.00d, collectionOnDeliveryFee1 = 0.00d, foreightAtDestinationFee1 = 0.00d;
                if (consignor instanceof Integer) {
                    consignor1 = ((Integer) consignor).intValue();
                } else if (consignor instanceof BigDecimal) {
                    consignor1 = ((BigDecimal) consignor).doubleValue();
                } else if (consignor instanceof Double) {
                    consignor1 = (double) consignor;
                }
                if (carrier instanceof Integer) {
                    carrier1 = ((Integer) carrier).intValue();
                } else if (carrier instanceof BigDecimal) {
                    carrier1 = ((BigDecimal) carrier).doubleValue();
                } else if (carrier instanceof Double) {
                    carrier1 = (double) carrier;
                }
                if (serviceProvider instanceof Integer) {
                    serviceProvider1 = ((Integer) serviceProvider).intValue();
                } else if (serviceProvider instanceof BigDecimal) {
                    serviceProvider1 = ((BigDecimal) serviceProvider).doubleValue();
                } else if (serviceProvider instanceof Double) {
                    serviceProvider1 = (double) serviceProvider;
                }
                if (collectionOnDeliveryFee instanceof Integer) {
                    collectionOnDeliveryFee1 = ((Integer) collectionOnDeliveryFee).intValue();
                } else if (collectionOnDeliveryFee instanceof BigDecimal) {
                    collectionOnDeliveryFee1 = ((BigDecimal) collectionOnDeliveryFee).doubleValue();
                } else if (collectionOnDeliveryFee instanceof Double) {
                    collectionOnDeliveryFee1 = (double) collectionOnDeliveryFee;
                }
                if (foreightAtDestinationFee instanceof Integer) {
                    foreightAtDestinationFee1 = ((Integer) foreightAtDestinationFee).intValue();
                } else if (foreightAtDestinationFee instanceof BigDecimal) {
                    foreightAtDestinationFee1 = ((BigDecimal) foreightAtDestinationFee).doubleValue();
                } else if (foreightAtDestinationFee instanceof Double) {
                    foreightAtDestinationFee1 = (double) foreightAtDestinationFee;
                }
                double v = consignor1 + carrier1 + serviceProvider1 - collectionOnDeliveryFee1 - foreightAtDestinationFee1;
                BigDecimal bg = new BigDecimal(v);
                double v1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                content.put(key, v1);
            }
        }
        return content;
    }

    private String getBillingTotal(ImportConditionVO importConditionVO) throws Exception {
        //SCM 获取授权
        getAuthorzization();

        // SCM 获取历史流水数据
        String urlScmBilling = scmDomain + "/api/finance/findFlowTotalToDataFire";
        String contentType = "application/json";
        String billing = billing(importConditionVO);
        return post(urlScmBilling, billing, "utf-8", contentType, scmAuthorzization);
    }

}
