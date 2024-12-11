package com.cej.controller;

import com.cej.commons.vo.ResResult;
import com.cej.service.MqService;
import com.cej.service.ShopService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/shop")
public class ShopController {

    @Resource
    private ShopService shopService;

    @Resource
    private MqService mqService;

    @GetMapping
    @RequestMapping("getById")
    public ResResult getById(@RequestParam("id")String id){
        return shopService.queryById(id);
    }

    @GetMapping("test")
    public ResResult test(){
        mqService.send();
        return ResResult.success();
    }
}
