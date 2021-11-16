package com.mengzhiayuan.naruto.dao;

import com.mengzhiayuan.naruto.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: 梦致A远
 * @Date: 2021/8/20 19:11
 * @Description:
 */
@Repository
@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit,int orderMode);

    //@param注解用于给参数取别名
    //如果只有一个参数，并且在<if>里使用，则必须加别名
    int selectDiscussPostRows(@Param("userId")int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id,int commentCount);

//  type : 0-普通 1-置顶
//  status : 0-正常 1-精华 2-拉黑

    int updateType(int id,int type);

    int updateStatus(int id,int status);

    int updateScore(int id,double score);

}
