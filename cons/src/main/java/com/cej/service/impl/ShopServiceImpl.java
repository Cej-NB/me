package com.cej.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cej.commons.contents.Content;
import com.cej.commons.vo.RedisData;
import com.cej.commons.vo.ResResult;
import com.cej.entity.Shop;
import com.cej.mapper.ShopMapper;
import com.cej.service.ShopService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements ShopService, Content {

    private static final ExecutorService cache_rebuild_executor = Executors.newFixedThreadPool(10);


    @Resource
    private StringRedisTemplate template;

    @Override
    public ResResult queryById(String id) {

        String redisKey = SHOP + ":" + id;
        //1、查询缓存
        String shopJson = template.opsForValue().get(redisKey);
        if(StrUtil.isNotBlank(shopJson)){
            Shop shop = JSONUtil.toBean(shopJson,Shop.class);
            return ResResult.success(shop);
        }

        if(shopJson !=null){
            return ResResult.fail("不存在");
        }

        //2、查询数据库
        Shop shop = getById(id);
        if(shop == null){
            //将空值写入redis,防止穿透
            template.opsForValue().set(redisKey, "", REDIS_NULL_TTL, TimeUnit.MINUTES);
            return ResResult.fail("不存在");
        }
        //3、保存缓存
        template.opsForValue().set(redisKey, JSONUtil.toJsonStr(shop), 30L, TimeUnit.MINUTES);
        return ResResult.success(shop);
    }

    @Override
    public ResResult updateById(String id) {

        //1、更新数据库

        //2、删除缓存
        return ResResult.success();
    }



    //基于互斥锁的方式，解决高并发缓存击穿问题
    private Shop queryByIdMutex(String id) {

        String redisKey = SHOP + ":" + id;
        //1、查询缓存
        String shopJson = template.opsForValue().get(redisKey);
        if (StrUtil.isNotBlank(shopJson)) {
            return JSONUtil.toBean(shopJson, Shop.class);
        }
        if (shopJson != null) {
            return null;
        }
        Shop shop = null;

        // 2、未命中,获取互斥锁
        String lockKey = "lock:shop:" + id;
        try {
            boolean isLock = tryLock(lockKey);
            // 3、是否获取成功
            if (!isLock) {
                // 4、失败则休眠充实
                sleep(50);
                return queryByIdMutex(id);
            }

            //2、查询数据库
            shop = getById(id);
            if (shop == null) {
                //将空值写入redis,防止穿透
                template.opsForValue().set(redisKey, "", REDIS_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            //3、保存缓存
            template.opsForValue().set(redisKey, JSONUtil.toJsonStr(shop), 30L, TimeUnit.MINUTES);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            unLock(lockKey);
        }
        return shop;
    }

    //基于逻辑过期的方式
    private Shop queryByIdLogic(String id){
        Shop shop = null;

        String redisKey = SHOP + ":" + id;
        //1、查询缓存
        String shopJson = template.opsForValue().get(redisKey);
        if (StrUtil.isNotBlank(shopJson)) {
            RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
            shop = JSONUtil.toBean((JSONObject)redisData.getObject(),Shop.class);
            LocalDateTime expire = redisData.getExpire();

            //判断是否过期
            if(expire.isAfter(LocalDateTime.now())){
                //未过期，直接返回
                return shop;
            }

            String lockKey = "lock:shop:" + id;
            boolean isLock = tryLock(lockKey);
            if(isLock){
                //获取锁成功，开启新线程重建缓存
                cache_rebuild_executor.submit(() ->{
                    try {
                        saveShop(id, 30L);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        unLock(lockKey);
                    }
                });
            }

            return shop;
        }

        return null;
    }

    private boolean tryLock(String key){
        Boolean flag = template.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }
    private void unLock(String key){
        template.delete(key);
    }
    private void saveShop(String id, Long expireTime){
        Shop shop = getById(id);

        RedisData redisData = new RedisData();
        redisData.setObject(shop);
        redisData.setExpire(LocalDateTime.now().plusSeconds(expireTime));

        template.opsForValue().set(REDIS_SHOP_KEY + id, JSONUtil.toJsonStr(redisData));
    }


    private void sleep(int time){
        try {
            Thread.sleep(time);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
