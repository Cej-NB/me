package com.cej.nc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cej.base.commons.vo.ResResult;
import com.cej.nc.entity.Series;
import com.cej.nc.mapper.SeriesMapper;
import com.cej.nc.service.SeriesService;
import org.springframework.stereotype.Service;

@Service
public class SeriesServiceImpl extends ServiceImpl<SeriesMapper, Series> implements SeriesService {
    @Override
    public ResResult getAll() {
        return ResResult.success(baseMapper.selectList(new QueryWrapper<>()));
    }
}
