package com.cej.commons.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RedisData {
    private LocalDateTime expire;

    private Object object;
}
