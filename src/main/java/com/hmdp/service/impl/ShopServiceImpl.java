package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryById(Long id) {

        //1. 从redis中查询商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get("cache:shop:" + id);

//        2. 判断是否存在
        if(StrUtil.isNotBlank(shopJson)){
            //        3. 存在，直接返回
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }

//        4. 不存在，根据id查询数据库
        Shop shop = getById(id);

//        5. 不存在，返回错误
        if(Objects.isNull(shop))
            return Result.fail("店铺不存在！");

//        6. 存在，写入redis
        stringRedisTemplate.opsForValue().set("cache:shop:"+id,JSONUtil.toJsonStr(shop));

        return Result.ok(shop);

    }
}
