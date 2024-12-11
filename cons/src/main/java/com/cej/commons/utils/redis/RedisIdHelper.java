package com.cej.commons.utils.redis;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * redis方式 生成全局唯一id
 * */
@Component
public class RedisIdHelper {

    @Resource
    private StringRedisTemplate template;

    private static final long BEGIN_TIMESTAMP = 1672531200L;

    private static final int COUNT_BIT = 32;

    public Long nextId(String keyPre){

        //1、生成时间戳
        LocalDateTime nowTime = LocalDateTime.now();
        long nowSecond = nowTime.toEpochSecond(ZoneOffset.UTC);
        long time = nowSecond - BEGIN_TIMESTAMP;
        //2、生成序列值

        long count = template.opsForValue().increment("icr:" + keyPre + ":" + nowTime.format(DateTimeFormatter.ofPattern("yyyy:MM:dd")));

        //3、拼接并返回     时间戳向高位移动32位，序列号补上
        return (time << 32) | count;
    }

    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.of(2023,1,1,0,0,0);
        long second = localDateTime.toEpochSecond(ZoneOffset.UTC);
        System.out.println(second);
    }
}
