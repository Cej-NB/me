package com.cej.redisdemo;

import com.cej.redisdemo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class SpringRedisTest {



    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    @Test
    public void ConnectRedis(){
        ValueOperations valueOperations = redisTemplate.opsForValue();

        User user = new User();
        user.setName("陈恩杰");
        user.setId("1");
        valueOperations.set("name",user);

        System.out.println(valueOperations.get("name"));
    }

}
