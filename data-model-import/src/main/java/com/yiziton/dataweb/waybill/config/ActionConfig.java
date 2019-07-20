package com.yiziton.dataweb.waybill.config;


import com.yiziton.dataweb.core.action.ActionExporter;
import com.yiziton.dataweb.core.action.ActionScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.yiziton.dataweb.waybill.config.Constants.ACTION_URL_MAPPING;


@Configuration
public class ActionConfig {


    @Bean
    public ActionScanner actionScanner(){
        ActionScanner actionScanner = new ActionScanner();
        actionScanner.setBasePackage(System.getProperty(Constants.PLATFORM_BASE_PACKAGE));
        return actionScanner;
    }
    @Bean(ACTION_URL_MAPPING)
    public ActionExporter actionExporter(){
        ActionExporter actionExporter = new ActionExporter();
        actionExporter.setBasePackage(System.getProperty(Constants.PLATFORM_BASE_PACKAGE));
        return actionExporter;
    }
}
