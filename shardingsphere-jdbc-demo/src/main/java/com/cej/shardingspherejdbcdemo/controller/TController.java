package com.cej.shardingspherejdbcdemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cej.shardingspherejdbcdemo.entity.User;
import com.cej.shardingspherejdbcdemo.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/")
public class TController {

    @Resource
    private UserMapper userMapper;

    @GetMapping("/getUser")
    public User getUser(){
        return userMapper.selectList(new QueryWrapper<User>().select("id","name")).get(0);
    }

    @PostMapping("/insertUser")
    public String insertUser(@RequestBody User user){
        userMapper.insert(user);
        return "OK";
    }

}
