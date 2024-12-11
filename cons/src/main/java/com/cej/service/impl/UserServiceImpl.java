package com.cej.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cej.commons.contents.Content;
import com.cej.commons.contents.RedisContent;
import com.cej.commons.utils.RegexUtils;
import com.cej.commons.vo.ResResult;
import com.cej.commons.vo.UserDTO;
import com.cej.dto.LoginFormDTO;
import com.cej.entity.User;
import com.cej.mapper.UserMapper;
import com.cej.service.UserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService, Content, RedisContent {

    @Resource
    private StringRedisTemplate template;

    @Override
    public ResResult sendCode(String phone, HttpSession session){

        //1、校验手机号
        if(RegexUtils.isPhoneInvalid(phone)){
            return ResResult.fail("手机号验证错误");
        }
        //2、符合生成验证码,保存到session或redis
        String code = RandomUtil.randomNumbers(6);
        template.opsForValue().set(LOGIN_CODE_PRE + phone, code ,1, TimeUnit.MINUTES);
        //session.setAttribute(VALIDATE_CODE + phone,code);
        //3、发送验证码

        return ResResult.success();
    }

    @Override
    public ResResult login(LoginFormDTO loginFormDTO, HttpSession session) {

        String phone = loginFormDTO.getPhone();

        //1、验证手机号
        if(RegexUtils.isPhoneInvalid(loginFormDTO.getPhone())){
            return ResResult.fail("手机号验证错误");
        }

        //2、验证验证码
        String cacheCode = template.opsForValue().get(LOGIN_CODE_PRE + phone);
        String code = loginFormDTO.getCode();

        if(cacheCode == null || !cacheCode.equals(code)){
            return ResResult.fail("验证码错误");
        }

        //3、创建用户
        User user = query().eq(PHONE,phone).one();
        if(user == null){
            user = createUserWithPhone(phone);
        }

        //4、设置redis
        String token = UUID.randomUUID().toString();

        UserDTO userDTO = BeanUtil.copyProperties(user,UserDTO.class);
        Map<String,Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((key,value) -> value.toString()));

        //设置30分钟有效期
        String tokenId = LOGIN_TOKEN_PRE + token;
        template.opsForHash().putAll(tokenId,userMap);
        template.expire(tokenId,30,TimeUnit.MINUTES);

        return ResResult.success(token);
    }



    private User createUserWithPhone(String phone){
        User user = new User();
        save(user);
        return user;
    }

}
