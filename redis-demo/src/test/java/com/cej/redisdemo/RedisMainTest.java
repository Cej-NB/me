package com.cej.redisdemo;

import redis.clients.jedis.Jedis;

import java.util.Map;

public class RedisMainTest {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1",6379);
        jedis.select(0);


        Map map = jedis.hgetAll("id");
        map.forEach((key,value) -> System.out.println(key+":"+value));
    }
}
