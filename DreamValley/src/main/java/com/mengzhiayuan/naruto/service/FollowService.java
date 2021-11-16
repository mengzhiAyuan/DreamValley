package com.mengzhiayuan.naruto.service;

import com.mengzhiayuan.naruto.util.RedisKeyUtil;

import java.util.List;
import java.util.Map;

/**
 * @Auther: 梦致A远
 * @Date: 2021/9/2 10:57
 * @Description:
 */

public interface FollowService {

    void follow(int userId,int entityType,int entityId);

    void unfollow(int userId,int entityType,int entityId);

    // 查询关注的实体的数量
    long findFolloweeCount(int userId, int entityType);

    // 查询实体的粉丝的数量
    long findFollowerCount(int entityType, int entityId);

    // 查询当前用户是否已关注该实体
    boolean hasFollowed(int userId, int entityType, int entityId);

    //查询用户关注的人
    List<Map<String,Object>> findFollowees(int userId, int offset, int limit);

    //查询用户的粉丝
    List<Map<String,Object>> findFollowers(int userId,int offset,int limit);


}
