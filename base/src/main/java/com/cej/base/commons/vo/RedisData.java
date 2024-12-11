package com.cej.base.commons.vo;


import java.time.LocalDateTime;


public class RedisData {
    private LocalDateTime expire;

    private Object object;

    public LocalDateTime getExpire() {
        return expire;
    }

    public void setExpire(LocalDateTime expire) {
        this.expire = expire;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
