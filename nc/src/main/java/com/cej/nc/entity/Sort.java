package com.cej.nc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sort")
public class Sort {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sortName;

    private Long typeId;

    private Long pos;
}
