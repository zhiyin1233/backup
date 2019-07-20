/*
 * Copyright 2018 1ziton.com.
 **/
package com.yiziton.dataweb.core.oauth2;



import com.yiziton.dataweb.core.action.CrossProcessFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

/**
 * 资源认证应用
 *
 * @author xiaoHong
 * @create 2018-01-02
 **/
@Configuration
@EnableResourceServer
public class ResourceServerConfigurer extends ResourceServerConfigurerAdapter {
	private static final Log logger = LogFactory.getLog(ResourceServerConfigurer.class);

	private static boolean enableFlag = true;

	public static void setEnableFlag(boolean flag) {
		enableFlag = flag;
	}

	@Autowired
	private ResourceAuthenticationManager resourceAuthenticationManager;

	SimpleAuthenticationEntryPoint simpleAuthenticationEntryPoint = new SimpleAuthenticationEntryPoint();

	@Override
	public void configure(HttpSecurity http) throws Exception {

		http.exceptionHandling().authenticationEntryPoint(simpleAuthenticationEntryPoint);
		ExceptionHandleFilter exceptionHandleFilter = new ExceptionHandleFilter();
		CrossProcessFilter crossProcessFilter = new CrossProcessFilter();



		if(enableFlag){
			http.authorizeRequests().antMatchers("/monitor/**","/actuator/health","/action/order/**","/action/sysExceptDeal/**","/favicon.ico").permitAll();
			http.authorizeRequests()
					.anyRequest().authenticated()
					.and()
					.addFilterAfter(crossProcessFilter, AbstractPreAuthenticatedProcessingFilter.class)
					.addFilterAfter(exceptionHandleFilter, ExceptionTranslationFilter.class);

		}else{
			http.authorizeRequests().antMatchers("/**").permitAll();
		}
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.authenticationEntryPoint(simpleAuthenticationEntryPoint);
		resources.authenticationManager(resourceAuthenticationManager);

	}

}

