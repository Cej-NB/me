package com.cej.nc.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("series")
public class Series {

    @TableId
    private Long id;

    private String seriesName;
}
