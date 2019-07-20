package com.yiziton;

import com.yiziton.dataweb.core.oauth2.ResourceServerConfigurer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/10/9
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@SpringBootApplication
@ComponentScan("com.yiziton")
@MapperScan("com.yiziton.dataweb.waybill.dao")
public class DataModelImportApplication {
    public static void main(String[] args) {
        ResourceServerConfigurer.setEnableFlag(false);
        Main.run(9801,args);
    }
}
