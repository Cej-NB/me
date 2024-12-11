package com.cej.commons.interceptor;

import com.cej.commons.UserHolder;
import com.cej.commons.contents.RedisContent;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录验证拦截器
 *
 * */
public class LoginInterceptor implements HandlerInterceptor, RedisContent {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //1、判断是否需要拦截
        if(UserHolder.getUser() == null){
            response.setStatus(401);
            return false;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
