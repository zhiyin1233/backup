package com.yiziton.dataweb.waybill.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiziton.commons.utils.DateUtil;
import com.yiziton.dataweb.core.annotation.Action;
import com.yiziton.dataweb.waybill.service.OperationIndicatorService;
import com.yiziton.dataweb.waybill.utils.GridRequest;
import com.yiziton.dataweb.waybill.vo.CommonVO;
import com.yiziton.dataweb.waybill.vo.IndicatorConditionVO;
import com.yiziton.dataweb.waybill.vo.IndicatorVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.*;

/**
 * @program: bigdataplatform
 * @description: 运营管理-运营指标
 * @author: kdh
 * @create: 2019-05-24 17:47
 * @Copyright © 2019 www.1ziton.com Inc. All Rights Reserved.
 */
@Action("operationIndicator")
@Slf4j
public class OperationIndicatorAction {

    @Value("${historyDataImport.ips}")
    String ips;
    @Value("${historyDataImport.ipsLogin}")
    String ipsCore;
    String ipsAuthorization;
    String token;
    long sessionStart = 0L, sessionInterval = 10 * 24 * 60 * 60 * 1000L;//ips token 14天才失效，这里设置10天

    @Autowired
    OperationIndicatorService operationIndicatorService;

    @Autowired
    HttpServletResponse httpServletResponse;

    /**
     * 运营指标
     *
     * @param indicatorConditionVO
     * @return
     */
    public List<IndicatorVO> getIndicators(IndicatorConditionVO indicatorConditionVO) {
        log.info("获取运营指标开始");
        try {
            if (indicatorConditionVO.getAssessTimeBegin() == null || indicatorConditionVO.getAssessTimeEnd() == null
                    || indicatorConditionVO.getCircleAssessTimeBegin() == null || indicatorConditionVO.getCircleAssessTimeEnd() == null) {
                log.info("没有选择考核时间和环比考核时间");
            } else {
                Timestamp assessTimeEnd = indicatorConditionVO.getAssessTimeEnd();
                if (assessTimeEnd != null) {
                    indicatorConditionVO.setAssessTimeEnd(new Timestamp(assessTimeEnd.getTime() + 86400000L));//加上一天
                }
                Timestamp circleAssessTimeEnd = indicatorConditionVO.getCircleAssessTimeEnd();
                if (circleAssessTimeEnd != null) {
                    indicatorConditionVO.setCircleAssessTimeEnd(new Timestamp(circleAssessTimeEnd.getTime() + 86400000L));//加上一天
                }
            }
            List<IndicatorVO> indicatorVOList = operationIndicatorService.getIndicators(indicatorConditionVO);
            log.info("获取运营指标结束");
            return indicatorVOList;
        } catch (Exception e) {
            log.info("获取运营指标异常");
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 运营指标报表
     *
     * @param indicatorConditionVO
     * @throws Exception
     */
    public void exportIndicator(IndicatorConditionVO indicatorConditionVO) {
        log.info("获取运营指标报表开始");
        try {
            if (indicatorConditionVO.getAssessTimeBegin() == null || indicatorConditionVO.getAssessTimeEnd() == null
                    || indicatorConditionVO.getCircleAssessTimeBegin() == null || indicatorConditionVO.getCircleAssessTimeEnd() == null) {
                throw new RuntimeException("没有选择考核时间和环比考核时间");
            }
            Timestamp assessTimeEnd = indicatorConditionVO.getAssessTimeEnd();
            if (assessTimeEnd != null) {
                indicatorConditionVO.setAssessTimeEnd(new Timestamp(assessTimeEnd.getTime() + 86400000L));//加上一天
            }
            Timestamp circleAssessTimeEnd = indicatorConditionVO.getCircleAssessTimeEnd();
            if (circleAssessTimeEnd != null) {
                indicatorConditionVO.setCircleAssessTimeEnd(new Timestamp(circleAssessTimeEnd.getTime() + 86400000L));//加上一天
            }
            Workbook workbook = operationIndicatorService.exportIndicator(indicatorConditionVO);
            exportExcel("OperationIndicatorReport " + DateUtil.fromDateH(), workbook);
        } catch (Exception e) {
            log.info("获取运营指标报表异常");
            e.printStackTrace();
        }
        log.info("获取运营指标报表结束");
    }

    /**
     * 运营指标报表本地
     *
     * @param indicatorConditionVO
     * @throws Exception
     */
    public void exportIndicatorLocal(IndicatorConditionVO indicatorConditionVO) {
        log.info("获取运营指标报表开始");
        try {
            if (indicatorConditionVO.getAssessTimeBegin() == null || indicatorConditionVO.getAssessTimeEnd() == null
                    || indicatorConditionVO.getCircleAssessTimeBegin() == null || indicatorConditionVO.getCircleAssessTimeEnd() == null) {
                throw new RuntimeException("没有选择考核时间和环比考核时间");
            }
            Timestamp assessTimeEnd = indicatorConditionVO.getAssessTimeEnd();
            if (assessTimeEnd != null) {
                indicatorConditionVO.setAssessTimeEnd(new Timestamp(assessTimeEnd.getTime() + 86400000L));//加上一天
            }
            Timestamp circleAssessTimeEnd = indicatorConditionVO.getCircleAssessTimeEnd();
            if (circleAssessTimeEnd != null) {
                indicatorConditionVO.setCircleAssessTimeEnd(new Timestamp(circleAssessTimeEnd.getTime() + 86400000L));//加上一天
            }
            Workbook workbook = null;
            workbook = operationIndicatorService.exportIndicator(indicatorConditionVO);
            File savefile = new File("D:/");
            if (!savefile.exists()) {
                savefile.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream("D:/运营指标报表 " + DateUtil.fromDateH() + ".xls");
            workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            log.info("获取运营指标报表异常");
            e.printStackTrace();
        }
        log.info("获取运营指标报表结束");
    }

    /**
     * 获取 运营管理-运营指标 承运商下拉选择列表
     *
     * @param commonVO
     * @return
     */
    public Page<CommonVO> findCarrierCodeList(GridRequest gridRequest, CommonVO commonVO) throws Exception {

        if (ipsAuthorization == null || (System.currentTimeMillis() - sessionStart) > sessionInterval) {
            String ipsLoginUrl = ipsCore + "/login";
            String spliceNameValuePair = getIpsAuthorization();
            String contentType = "application/json";
            String scmBody = post(ipsLoginUrl, spliceNameValuePair, "utf-8", contentType, null);
            log.info("获取 IPS 授权响应结果：" + scmBody);
            if (scmBody != null) {
                Map parse = (Map) JSON.parse(scmBody);
                ipsAuthorization = parse.get("jwt") != null ? ("Bearer " + (String) parse.get("jwt")) : null;
            }
            log.info("SCM 授权 : " + ipsAuthorization);
            sessionStart = System.currentTimeMillis();
        }

        String ipsCarrierUrl = ipsCore + "/api.do";
        String spliceNameValuePair = carrier(gridRequest, commonVO);
        String contentType = "application/json";
        String carrierBody = post(ipsCarrierUrl, spliceNameValuePair, "utf-8", contentType, ipsAuthorization);
        log.info("获取 IPS 承运商响应结果：" + carrierBody);
        if (carrierBody != null) {
            Map parse = (Map) JSON.parse(carrierBody);
            String resultStr = parse.get("result") != null ? parse.get("result").toString() : null;
            Map result = (Map) JSON.parse(resultStr);
            String content = result.get("content") != null ? result.get("content").toString() : null;
            String totalElementsStr = result.get("totalElements") != null ? result.get("totalElements").toString() : "0";
            String firstStr = result.get("first") != null ? result.get("first").toString() : "false";
            JSONArray jsonArray = JSON.parseArray(content);
            List<CommonVO> data = null;
            Long total = 0L;
            Long totalElements = Long.valueOf(totalElementsStr);
            if (totalElements < 0) {//超出页数会返回，"totalElements":-1
                return new PageImpl(Collections.emptyList(), new PageRequest(gridRequest.getPage() - 1, gridRequest.getSize()), 0);
            }
            boolean first = Boolean.parseBoolean(firstStr);
            if (first) {
                total = totalElements;
            }
            if (jsonArray.size() > 0) {
                data = new ArrayList<CommonVO>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    Map map = (Map) jsonArray.get(i);
                    String agentId;
                    String agentName;
                    CommonVO common = new CommonVO();
                    if (map.get("agentId") == null) {
                        continue;
                    } else {
                        agentId = map.get("agentId").toString();
                        common.setCode(agentId);
                    }
                    if (map.get("agentName") == null) {
                        continue;
                    } else {
                        String companyDepartmentName = map.get("companyDepartmentName") == null ? "" : map.get("companyDepartmentName").toString();
                        agentName = map.get("agentName").toString() + (companyDepartmentName.length() == 0 ? "" : " | " + companyDepartmentName);
                        common.setValue(agentName);
                    }
                    data.add(common);
                }
            }
            return new PageImpl(data, new PageRequest(gridRequest.getPage() - 1, gridRequest.getSize()), total);
        }
        return new PageImpl(null, new PageRequest(gridRequest.getPage() - 1, gridRequest.getSize()), 0);
    }

    /**
     * 固定值，不需要修改
     *
     * @return
     */
    public String getIpsAuthorization() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("source", "core");//用户名
        jsonObject.put("mobile", "18000000000");//用户名
        jsonObject.put("password", "888888");//密码
        log.info(jsonObject.toString());
        log.info("IPS 授权参数 : " + jsonObject.toString());
        return jsonObject.toString();
    }

    /**
     * 拼接参数
     *
     * @param gridRequest
     * @param commonVO
     * @return
     */
    public String carrier(GridRequest gridRequest, CommonVO commonVO) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "cmpRelayAgentController.findRelayAgentList");//api名称
        jsonObject.put("token", "rt_1558951627770");//token
        JSONArray jsonArray = new JSONArray();
        Map<String, Object> pageMap = new HashMap<String, Object>();
        pageMap.put("first", (gridRequest.getPage() - 1) * gridRequest.getSize());//offset
        if (gridRequest.getSize() == 0) {
            pageMap.put("rows", 10);//每页10条
        } else {
            pageMap.put("rows", gridRequest.getSize());//每页条数
        }
        jsonArray.add(pageMap);
        Map<String, Object> argsMap = new HashMap<String, Object>();
        if (commonVO.getValue() != null) {
            argsMap.put("agentName", commonVO.getValue());//承运商名，模糊查找
        }
        argsMap.put("accountType", "PARENT");//过滤子账号
        jsonArray.add(argsMap);
        jsonObject.put("args", jsonArray);//起始日期
        log.info("IPS 获取承运商参数 : " + jsonObject.toString());
        return jsonObject.toString();
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
        org.apache.http.HttpEntity entity = response.getEntity();
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
        org.apache.http.HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, encoding);
        }
        EntityUtils.consume(entity);
        //释放链接
        response.close();
        return body;
    }

    public void exportExcel(String fileName, Workbook workbook) throws Exception {

        if (StringUtils.isBlank(fileName)) {
            fileName = System.currentTimeMillis() + "";
        }

        httpServletResponse.setHeader("Content-disposition", "attachment;filename=\"" + new String(fileName.getBytes("gb2312"), "ISO8859-1") + ".xlsx" + "\"");
        httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        OutputStream os = null;
        try {
            os = httpServletResponse.getOutputStream();
            workbook.write(os);
            os.flush();
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }
}
