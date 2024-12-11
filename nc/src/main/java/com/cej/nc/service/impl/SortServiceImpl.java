package com.cej.nc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cej.base.commons.vo.ResResult;
import com.cej.nc.entity.Sort;
import com.cej.nc.mapper.SortMapper;
import com.cej.nc.service.SortService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SortServiceImpl extends ServiceImpl<SortMapper,Sort> implements SortService {

    @Resource
    private SortMapper sortMapper;

    @Override
    public ResResult saveSort(Sort sort) {
        sortMapper.insert(sort);
        return ResResult.success();
    }

    @Override
    public ResResult updateSort(Sort sort) {
        return null;
    }
}
