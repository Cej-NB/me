package com.cej.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_shop")
public class Shop {

    @TableId
    private String id;

    private String name;
}
