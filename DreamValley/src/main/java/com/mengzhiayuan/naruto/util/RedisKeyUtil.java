package com.mengzhiayuan.naruto.util;

/**
 * @Auther: 梦致A远
 * @Date: 2021/9/1 10:38
 * @Description:
 */

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee"; //关注的实体
    private static final String PREFIX_FOLLOWER = "follower"; //关注的用户
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";
    private static final String PREFIX_POST = "post";
//    private static final String PREFIX_CODE = "code";

    //某个实体的赞
    // like:entity:entityType:entityId -> set(userId) 存userId 功能拓展的时候可以知道给我点赞的用户
    //entityType,entityId唯一确定点赞的实体  entityType 表示是给帖子的评论，还是对评论的回复，entityId确定唯一comment实体
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT +entityType +SPLIT +entityId;
    }

    //某个用户的赞
    // like:user:userId -> int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //某个用户关注的实体
    //followee:userId:entityType -> zset(entityId,now)
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //某个实体（int ENTITY_TYPE_USER = 3 用户实体）拥有的粉丝
    //实体既可是是帖子，也可以是用户，评论(关注用户，关注帖子)，论坛只做用户，统一实体int ，方便以后拓展
    //关注了多少帖子，关注了多少用户，这是两个模块
    //follower:entityType:entityId -> zset(userId,now)
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT +entityId;
    }


//    userId:entityType----------》唯一标识一个用户,
//    entityType:entityId-----------》唯一标识一个实体


    //登录验证码
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    // 登录的凭证
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    // 用户
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    //单日UV unique visitor 某一天的游客（去重后）
    public static String getUVKey(String date){
        return PREFIX_UV + SPLIT + date;
    }

    //区间UV: 某个时间段的游客
    public static String getUVKey(String startDate,String endDate){
        return PREFIX_UV + SPLIT + startDate +SPLIT +endDate;
    }

    //单日活跃用户  day active user
    public static String getDAUKey(String date){
        return PREFIX_DAU + SPLIT +date;
    }

    //区间活跃用户
    public static String getDAUKey(String startDate,String endDate){
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    // 帖子分数
    public static String getPostScoreKey() {
        return PREFIX_POST + SPLIT + "score";
    }

//    public static String getCodeKey(String owner) {
//        return PREFIX_CODE + SPLIT + owner;
//    }

}