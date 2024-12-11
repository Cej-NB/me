package com.cej.nc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@TableName("seed")
@Builder
public class Seed {

    @TableId(type = IdType.AUTO)
    private Long id;

    @JsonProperty("sName")
    private String sName;

    private Long pid;

    private Long star;

    private Long pos;
    @JsonProperty("sType")
    private Long sType;

    @TableField(exist = false)
    private List<Seed> children;
}
