package com.mengzhiayuan.naruto.service.Impl;


import com.mengzhiayuan.naruto.service.DataService;
import com.mengzhiayuan.naruto.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Auther: 梦致A远
 * @Date: 2021/9/17 16:22
 * @Description:
 */

@Service
public class DateServiceImpl implements DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    //将指定的IP计入UV
    @Override
    public void recordUV(String ip) {
        String redisKey = RedisKeyUtil.getUVKey(dateFormat.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey,ip);
    }

    //统计指定日期范围内的UV
    @Override
    public long calculateUV(Date start, Date end) {
        if(start == null || end == null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        //整理该日期范围内的key
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getUVKey(dateFormat.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE,1);
        }

        //合并这些数据
        String rediskey = RedisKeyUtil.getUVKey(dateFormat.format(start),dateFormat.format(end));
        redisTemplate.opsForHyperLogLog().union(rediskey,keyList.toArray());

        //返回统计的结果
        return redisTemplate.opsForHyperLogLog().size(rediskey);
    }

    // 将指定用户计入DAU (日活跃用户)
    @Override
    public void recordDAU(int userId) {
        String redisKey = RedisKeyUtil.getDAUKey(dateFormat.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey,userId,true);
    }

    // 统计指定日期范围内的DAU
    @Override
    public long calculateDAU(Date start, Date end) {
        if(start==null||end==null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        //整理该日期范围内的key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while(!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getDAUKey(dateFormat.format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE,1);
        }

        // 进行OR运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(dateFormat.format(start),dateFormat.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(),keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });

    }

}
