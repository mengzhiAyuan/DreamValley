package com.mengzhiayuan.naruto.controller;

import com.mengzhiayuan.naruto.entity.Comment;
import com.mengzhiayuan.naruto.entity.DiscussPost;
import com.mengzhiayuan.naruto.entity.Event;
import com.mengzhiayuan.naruto.event.EventProducer;
import com.mengzhiayuan.naruto.service.CommentService;
import com.mengzhiayuan.naruto.service.DiscussPostService;
import com.mengzhiayuan.naruto.util.CommunityConstant;
import com.mengzhiayuan.naruto.util.HostHolder;
import com.mengzhiayuan.naruto.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @Auther: 梦致A远
 * @Date: 2021/8/29 15:40
 * @Description:
 */

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        //!!! 不从comment中取userId，因为前端没有传入这个参数也无法传入，因为只知道帖子作者的userId，针对帖子下的每个评论者，前端是不知道的
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",discussPostId);
        if(comment.getEntityType()==ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());

        }else if(comment.getEntityType()==ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());

        }
        eventProducer.pushEvent(event); //异步

        //添加评论的话,如果评论实体是帖子,帖子评论数量会变化,而且帖子的评论回复列表也是会变化的，所以应该更新es中帖子的数据,触发发帖事件
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            //触发发帖事件 ：通知帖子已经发生改变 es数据要变更
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.pushEvent(event);

            // 计算帖子分数  （只有对帖子做出评论才会计分）
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, discussPostId);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
