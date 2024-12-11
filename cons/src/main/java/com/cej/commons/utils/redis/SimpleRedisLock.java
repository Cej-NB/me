package com.cej.commons.utils.redis;

import cn.hutool.core.lang.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;


import java.util.concurrent.TimeUnit;
/**
 * redis分布锁的简单实现
 * */
public class SimpleRedisLock implements ILock {

    private String name;

    private static final String KEY_PRE = "lock:";
    private static final String ID_PRE = UUID.randomUUID().toString(true) + "-";

    private StringRedisTemplate template;

    public SimpleRedisLock(String name,StringRedisTemplate template){
        this.name = name;
        this.template = template;
    }


    @Override
    public boolean tryLock(long timeSec) {

        //获取线程id,拼接id_pre作为线程标识，在释放锁时判断释放的是不是获取到的锁,避免锁误删
        String threadId = ID_PRE + Thread.currentThread().getId();
        //获取锁
        Boolean flag = template.opsForValue().setIfAbsent(KEY_PRE + name, threadId , timeSec, TimeUnit.SECONDS);
        //避免自动装箱拆箱发生异常
        return Boolean.TRUE.equals(flag);
    }

    @Override
    public void unlock() {
        String threadId = ID_PRE + Thread.currentThread().getId();
        String id = template.opsForValue().get(KEY_PRE + name);
        if(threadId.equals(id)){
            template.delete(KEY_PRE + name);
        }
    }
}
