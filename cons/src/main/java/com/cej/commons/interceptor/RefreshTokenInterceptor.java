package com.cej.commons.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.cej.commons.UserHolder;
import com.cej.commons.contents.RedisContent;
import com.cej.commons.vo.UserDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RefreshTokenInterceptor implements HandlerInterceptor, RedisContent {

    private StringRedisTemplate template;

    public RefreshTokenInterceptor(StringRedisTemplate template){
        this.template = template;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1、获取或者 token
        String token = request.getHeader("authorization");
        if(StrUtil.isBlank(token)){
            return true;
        }

        //2、从redis获取User
        String tokenKey = LOGIN_TOKEN_PRE + token;
        Map<Object,Object> userMap = template.opsForHash().entries(tokenKey);
        if(userMap.isEmpty()){
            return true;
        }

        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(),false);
        UserHolder.saveUser(userDTO);

        //3、刷新token时间
        template.expire(tokenKey,30, TimeUnit.MINUTES);
        return true;
    }
}
