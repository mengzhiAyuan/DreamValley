package com.mengzhiayuan.naruto.service;

import com.mengzhiayuan.naruto.entity.Comment;

import java.util.List;

/**
 * @Auther: 梦致A远
 * @Date: 2021/8/28 19:45
 * @Description:
 */

public interface CommentService {

    List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int findCommentCount(int entityType, int entityId);

    int addComment(Comment comment);

    Comment findCommentById(int id);

}
