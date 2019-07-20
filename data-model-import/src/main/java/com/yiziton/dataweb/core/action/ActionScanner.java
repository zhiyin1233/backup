package com.yiziton.dataweb.core.action;


import com.yiziton.dataweb.core.annotation.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 动作扫描器
 * @author lujijiang
 */
public class ActionScanner implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;

    private String basePackage;

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.basePackage, "basePackage should not be null");
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Map<String,Class> actionMap = new HashMap<>();
        String[] packageNames = StringUtils.tokenizeToStringArray(this.basePackage, ",; \t\n");
        for (String packageName : packageNames) {
            Set<Class> classes = Classes.scanPackage(packageName, (Class<Object> type) -> {
                if (type.getAnnotation(Action.class) != null) {
                    return true;
                }
                return false;
            });
            for (Class<?> type : classes) {
                Action action =  type.getAnnotation(Action.class);
                if(actionMap.containsKey(action.value())){
                    throw new IllegalStateException(String.format("Duplicate Action in %s and %s",actionMap.get(action.value()),type));
                }
                actionMap.put(action.value(),type);
                doRegist(registry,type);
            }
        }
    }

    private void doRegist(BeanDefinitionRegistry registry, Class<?> type) {
        logger.debug("Found an action: {}",type.getCanonicalName());
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(type);
        beanDefinitionBuilder.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        beanDefinitionBuilder.setLazyInit(false);
        String name = StringUtils.uncapitalize(type.getSimpleName());
        registry.registerBeanDefinition(name, beanDefinitionBuilder.getBeanDefinition());
        logger.debug("Register an action: {}",type.getCanonicalName());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
