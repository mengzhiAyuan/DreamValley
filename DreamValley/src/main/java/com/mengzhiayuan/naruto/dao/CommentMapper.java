package com.mengzhiayuan.naruto.dao;

import com.mengzhiayuan.naruto.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: 梦致A远
 * @Date: 2021/8/28 19:12
 * @Description:
 */

@Mapper
@Repository
public interface CommentMapper {

    //(实体)：是根据帖子的评论还是通过评论的评论来查
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    //查询评论数量
    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);

}
