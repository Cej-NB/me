package com.cej.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@TableName("tb_user")
@Data
public class User {

    private Long id;

    private String username;

    private String password;

    private String phone;
}
