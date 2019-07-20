package com.yiziton.dataimport.config;

import com.xxl.job.core.executor.XxlJobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/12/27
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Configuration
public class JobConfig {
    private Logger logger = LoggerFactory.getLogger(JobConfig.class);

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.executor.appname}")
    private String appName;

    @Value("${xxl.job.executor.ip:}")
    private String ip;

    @Value("${xxl.job.executor.port}")
    private Integer port;

    @Value("${xxl.job.executor.accessToken:}")
    private String accessToken;

    @Value("${xxl.job.executor.logpath:}")
    private String logPath;

    @Value("${xxl.job.executor.logretentiondays:}")
    private Integer logRetentionDays;


    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobExecutor xxlJobExecutor = new XxlJobExecutor();
        xxlJobExecutor.setAdminAddresses(adminAddresses);
        xxlJobExecutor.setAppName(appName);
        if (!StringUtils.isEmpty(ip)) {
            xxlJobExecutor.setIp(ip);
        }
        if(port != null){
            xxlJobExecutor.setPort(port);
        }
        if(!StringUtils.isEmpty(accessToken)){
            xxlJobExecutor.setAccessToken(accessToken);
        }
        if(!StringUtils.isEmpty(logPath)){
            xxlJobExecutor.setLogPath(logPath);
        }
        if(logRetentionDays != null){
            xxlJobExecutor.setLogRetentionDays(logRetentionDays);
        }

        return xxlJobExecutor;
    }
}
