package com.yiziton.dataweb.waybill.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.yiziton.commons.utils.OID;
import com.yiziton.dataimport.cache.dao.SysCacheMapper;
import com.yiziton.dataimport.commons.OrderProcessor;
import com.yiziton.dataimport.waybill.dao.ext.WaybillInfoExtMapper;
import com.yiziton.dataweb.core.action.contentType.excel.ExcelFile;
import com.yiziton.dataweb.waybill.utils.MailUtil;
import com.yiziton.dataweb.waybill.utils.excel.ExportExcelUtils;
import com.yiziton.dataweb.waybill.utils.excel.Header;
import com.yiziton.dataweb.waybill.utils.excel.WriteService;
import com.yiziton.dataweb.waybill.vo.header.InfoHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.mail.internet.MimeBodyPart;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: demoJob
 * @Author: xiaoHong
 * @Date: 2018/12/20
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@JobHandler(value="demoJob")
@Component
@Slf4j
public class DemoJob extends IJobHandler {


    @Autowired
    MailUtil mailUtil;

    @Value("#{'${spring.mail.to-receiver}'.split(',')}")
    String[] receiver;


    public class TestHeader extends InfoHeader {

        public TestHeader(){
            headerMap.put("waybillId",new Header("waybillId","运单号","string",12));
            headerMap.put("openBillTime",new Header("openBillTime","开单时间","string",12));
        }
    }

    @Override
    public ReturnT<String> execute(String param) throws Exception{
        log.info("demo Start");

        Workbook workbook = null;
        List<Map<String,Object>> list = new ArrayList<>();
        //创建writeService
        WriteService writeService = ExportExcelUtils.getWriteService(new TestHeader(),null);

        //数据准备
        for(int i=0; i<10000; i++){
            Map<String,Object> map = new HashMap<>();
            map.put("waybillId","123"+i);
            map.put("openBillTime",new Date().toString());
            list.add(map);
            if(list.size() == 1000){
                writeService.createContents(list);
                list.clear();
            }
        }
        workbook = writeService.build();

        //创建输出流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        bos.close();
        //将流转未数组作为多媒体附件
        byte[] bytes = bos.toByteArray();
        MimeBodyPart mimeBodyPart = MailUtil.byte2Multpart(bytes,"测试.xlsx",MailUtil.EXCEL_FORMAT);
        List<MimeBodyPart> mimeBodyParts = new ArrayList<>();

        mimeBodyParts.add(mimeBodyPart);
        //邮件发送，tos表示收件人
        mailUtil.sendEmail("头部字段","测试",mimeBodyParts,receiver);

        log.info("sended");
        return null;
    }


}
