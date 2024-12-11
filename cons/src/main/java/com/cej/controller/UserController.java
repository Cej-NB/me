package com.cej.controller;

import com.cej.commons.BaseController;
import com.cej.commons.vo.ResResult;
import com.cej.dto.LoginFormDTO;
import com.cej.service.MqService;
import org.springframework.web.bind.annotation.*;

import com.cej.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private MqService mqService;

    @PostMapping("code")
    public ResResult sendCode(@RequestParam("phone")String phone, HttpSession httpSession){
        return userService.sendCode(phone,httpSession);
    }

    @PostMapping("/login")
    public ResResult login(@RequestBody LoginFormDTO loginFormDTO,HttpSession httpSession){
        return userService.login(loginFormDTO,httpSession);
    }

    @GetMapping("test")
    public ResResult test(){
        mqService.send();
        return ResResult.success();
    }
}
