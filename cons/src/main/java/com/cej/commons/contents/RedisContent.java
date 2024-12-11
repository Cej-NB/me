package com.cej.commons.contents;

public interface RedisContent {
    String LOGIN_CODE_PRE = "login:code:";
    String LOGIN_TOKEN_PRE = "login:token:";

    String LOCK_SHOP_KEY = "lock:shop:";
    //redis空对象过期时间
    long CACHE_NULL_TTL = 20L;
}
