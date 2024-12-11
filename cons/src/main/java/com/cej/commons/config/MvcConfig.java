package com.cej.commons.config;

import com.cej.commons.interceptor.LoginInterceptor;
import com.cej.commons.interceptor.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate template;

//    @Override
//    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//        configurer.enable();
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //添加拦截器,拦截时的redisTemplate从这边注入
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns("/user/**").order(1);

        registry.addInterceptor(new RefreshTokenInterceptor(template))
                .excludePathPatterns("/**").order(0);
    }
}
