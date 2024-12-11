package com.cej.shardingspherejdbcdemo;

import com.cej.shardingspherejdbcdemo.entity.User;
import com.cej.shardingspherejdbcdemo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringTest {

    @Autowired
    private UserMapper userMapper;



    @Test
    void DoSomething(){
        User user = new User();
        user.setName("陈恩杰");
        userMapper.insert(user);
    }


}
