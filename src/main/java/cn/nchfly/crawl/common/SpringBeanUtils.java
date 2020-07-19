package cn.nchfly.crawl.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Description: 构造方法中手动注入被@service标记的类
 * @Author: YangYangen
 * @Date: 2020/7/6 18:22
 * Copyright (c) 2020, Samton. All rights reserved
*/
@Component
public class SpringBeanUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtils.applicationContext = applicationContext;
    }

    public static Object getBeanByName(String className) {
        if (applicationContext == null){
            return null;
        }
        return applicationContext.getBean(className);
    }

    public static <T> T getBean(Class<T> type) {
        return applicationContext.getBean(type);
    }

}
