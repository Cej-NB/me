package com.cej.service;

import com.cej.commons.vo.ResResult;
import com.cej.dto.LoginFormDTO;

import javax.servlet.http.HttpSession;

public interface UserService {

    //发送验证
    public ResResult sendCode(String phone, HttpSession session);
    //验证登录
    public ResResult login(LoginFormDTO loginFormDTO,HttpSession session);
}
