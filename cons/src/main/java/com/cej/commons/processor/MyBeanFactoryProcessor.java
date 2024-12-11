package com.cej.commons.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.stereotype.Component;


/**
 * Bean工厂后处理器
 *
 * */
//@Component
public class MyBeanFactoryProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        //该方法在注入对象实例化之前调用

        DefaultListableBeanFactory factory = (DefaultListableBeanFactory) beanFactory;
        System.out.println("MyBeanFactoryProcessor执行了...");
    }
}
