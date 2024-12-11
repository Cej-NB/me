package com.cej.nc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cej.base.commons.vo.ResResult;
import com.cej.nc.entity.Seed;
import com.cej.nc.mapper.SeedMapper;
import com.cej.nc.service.SeedService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeedServiceImpl extends ServiceImpl<SeedMapper, Seed> implements SeedService {

    @Override
    public ResResult query(Seed seed) {
        QueryWrapper<Seed> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(seed.getSName() != null,Seed::getSName,seed.getSName());
        return ResResult.success(baseMapper.selectList(wrapper));
    }

    @Override
    public ResResult saveSeed(Seed seed) {
        try {
            baseMapper.insert(seed);
            return ResResult.success();
        }catch (Exception e){
            e.printStackTrace();
            return ResResult.fail("保存异常");
        }
    }

    @Override
    public ResResult delete(Seed seed) {
        QueryWrapper<Seed> wrapper = new QueryWrapper<Seed>()
                .eq("pid",seed.getId());
        List<Seed> seeds = baseMapper.selectList(wrapper);
        if(seeds !=null && seeds.size() >0){
            return ResResult.fail("存在子元素");
        }
        baseMapper.deleteById(seed.getId());
        return ResResult.success();
    }

    @Override
    public ResResult getAll() {

        List<Seed> seedAll = baseMapper.selectList(new QueryWrapper<>());
        List<Seed> sortedSeeds = seedAll.stream()
                .filter((seed) -> seed.getPid() == 0)
                .peek((seed) -> setChildren(seed,seedAll))
                .collect(Collectors.toList());
        return ResResult.success(sortedSeeds);
    }

    private void setChildren(Seed seed, List<Seed> seedAll){
        List<Seed> children = seedAll.stream()
                .filter(one -> one.getPid().equals(seed.getId()))
                .peek(one -> setChildren(one,seedAll))
                .collect(Collectors.toList());
        seed.setChildren(children);
    }
}
