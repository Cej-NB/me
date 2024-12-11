package com.itheima.mp;

import com.itheima.mp.domain.po.User;
import com.itheima.mp.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;

@SpringBootTest
class MpDemoApplicationTests {

    @Resource
    private UserMapper userMapper;
    @Test
    void contextLoads() {
        User user = new User();
        user.setInfo("{\"good\":\"good\"}");
        user.setUsername("goodd");
        user.setPassword("goo");
        userMapper.saveUser(user);
    }

}
