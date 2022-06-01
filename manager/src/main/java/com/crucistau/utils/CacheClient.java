package com.crucistau.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crucistau.commons.R;
import com.crucistau.commons.Result;
import com.crucistau.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.Function2Arg;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.crucistau.commons.RedisConstants.*;

@Slf4j
@Component
public class CacheClient {
    private StringRedisTemplate stringRedisTemplate;

    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void set(String key, Object value, Long time, TimeUnit unit){
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }
    public String get(String key, Object value, Long time, TimeUnit unit){
        return stringRedisTemplate.opsForValue().get(key);
    }


    public <T, ID> T queryWithPassThrough(
            String keyPrefix, ID id, Class<T> type, Function<ID, T> dbFallback, Long time, TimeUnit unit){

        String key = keyPrefix + id;
        //1、从redis中查询学生缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        //2、判断是否存在
        if (StrUtil.isNotBlank(json)) {
            //3、存在，直接返回
            return JSONUtil.toBean(json, type);
        }
        //判断是否是空值,缓存穿透设置的reids
        if (json != null) {
            //返回错误信息
            return null;
        }

        //4、不存在 根据ID查询数据库
        T t = dbFallback.apply(id);
        //5、数据库中没有 返回错误
        if (t == null) {
            //将空值写入Redis
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            //返回错误信息
            return null;
        }
        //6、数据库中查询成功，则写入redis然后返回
        this.set(key, t, time, unit);
        return t;
    }


    /**
     * 针对模糊查询所定义缓存
     * @param keyPrefix  部分主键
     * @param page  分页查询
     * @param q   模糊查询条件
     * @param type  返回类别
     * @param dbFallback  调用方法
     * @param time 定时
     * @param unit  时间单位
     * @param <T>
     * @return
     */
    public <T> Page queryListWithPassThrough(
            String keyPrefix, Page<T> page, T t,LambdaQueryWrapper<T> q, Class<Page> type, BiFunction<Page, LambdaQueryWrapper, Page> dbFallback, Long time, TimeUnit unit){

        //定义主键

        String key = keyPrefix + page.getCurrent() + "—" +page.getSize() + "//" + t.toString();
        //1、从redis中查询学生缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        //2、判断是否存在
        if (StrUtil.isNotBlank(json)) {
            //3、存在，直接返回
            return JSONUtil.toBean(json, type);
        }
        //判断是否是空值,缓存穿透设置的reids
        if (json != null) {
            //返回错误信息
            return null;
        }

        //4、不存在 根据ID查询数据库
        Page page1 = dbFallback.apply(page,q);
        //5、数据库中没有 返回错误
        if (page1 == null) {
            //将空值写入Redis
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            //返回错误信息
            return null;
        }
        //6、数据库中查询成功，则写入redis然后返回
        this.set(key, page1, time, unit);
        return page1;
    }

}
