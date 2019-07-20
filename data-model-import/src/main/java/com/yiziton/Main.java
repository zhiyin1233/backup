package com.yiziton;


import com.yiziton.dataweb.waybill.config.Constants;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import java.util.Collections;

/**
 * 主类
 */
@SpringBootApplication
@EnableTransactionManagement
public class Main {
    /**
     * 根据指定的基础包名启动
     * @param basePackage
     */
    public static void run(String basePackage, int port){

        System.setProperty(Constants.PLATFORM_BASE_PACKAGE,basePackage);

        // 设置基础包名
        new SpringApplicationBuilder(Main.class)
                .properties(Collections.singletonMap("server.port", port))
                .web(WebApplicationType.SERVLET).run();
    }

    /**
     * 自动获取基础包名并启动
     */
    public static void run(int port,String[] args){
        try {

            // 获取启动类名
            String baseClassName = Thread.currentThread().getStackTrace()[2].getClassName();
            Class baseClass = Class.forName(baseClassName);
            String basePackage = baseClass.getPackage().getName();
            if(StringUtils.isEmpty(basePackage)){
                basePackage = "com.yiziton";
            }
            run(basePackage,port);
        } catch (Throwable e) {
            throw e instanceof RuntimeException ?(RuntimeException)e:new RuntimeException(e);
        }
    }

    /**
     * 使用默认端口8080启动
     */
    public static void run(){
        try {
            // 获取启动类名
            String baseClassName = Thread.currentThread().getStackTrace()[2].getClassName();
            Class baseClass = Class.forName(baseClassName);
            String basePackage = baseClass.getPackage().getName();
            run(basePackage,8080);
        } catch (Throwable e) {
            throw e instanceof RuntimeException ?(RuntimeException)e:new RuntimeException(e);
        }
    }
}
