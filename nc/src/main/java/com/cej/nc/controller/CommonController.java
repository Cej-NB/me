package com.cej.nc.controller;

import com.cej.base.commons.vo.ResResult;
import com.cej.nc.entity.Seed;
import com.cej.nc.entity.Sort;
import com.cej.nc.service.SeedService;
import com.cej.nc.service.SortService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/nc")
public class CommonController {

    @Resource
    private SortService sortService;
    @Resource
    private SeedService seedService;


    @PostMapping("seed/query")
    public ResResult querySeed(@RequestBody Seed seed){
        return seedService.query(seed);
    }

    @PostMapping("saveSort")
    public ResResult save(@RequestBody Sort sort){
        return sortService.saveSort(sort);
    }
    @PostMapping("delete")
    public ResResult delete(@RequestBody Seed seed){
        return seedService.delete(seed);
    }

    @PostMapping("saveSeed")
    public ResResult saveSeed(@RequestBody Seed seed){
        return seedService.saveSeed(seed);
    }


    @GetMapping("get")
    public ResResult getSeries(){
        return seedService.getAll();
    }
}
