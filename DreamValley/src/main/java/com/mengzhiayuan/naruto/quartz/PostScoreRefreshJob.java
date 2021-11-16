package com.mengzhiayuan.naruto.quartz;

import com.mengzhiayuan.naruto.entity.DiscussPost;
import com.mengzhiayuan.naruto.service.DiscussPostService;
import com.mengzhiayuan.naruto.service.ElasticsearchService;
import com.mengzhiayuan.naruto.service.LikeService;
import com.mengzhiayuan.naruto.util.CommunityConstant;
import com.mengzhiayuan.naruto.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @Auther: 梦致A远
 * @Date: 2021/9/19 16:21
 * @Description:
 */
//定时对有改动（评论，点赞，发帖）的帖子刷新*分数*
@Slf4j
public class PostScoreRefreshJob implements Job, CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    //鸣人纪元
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2001-10-10 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化鸣人纪元失败！",e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if(operations.size() == 0){
            log.info("[任务取消] 没有需要刷新的帖子！");
            return;
        }
        log.info("[任务开始] 开始刷新帖子分数：" + operations.size());
        while (operations.size() > 0){
            this.refresh((Integer)operations.pop());
        }
        log.info("[任务结束] 帖子分数刷新完毕！");
    }

    private void refresh(int postId){
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if(post==null){
            log.error("帖子不存在： id ="+postId);
            return;
        }

        //是否精华
        boolean wonderful = post.getStatus()==1;
        //评论数量
        int communtCount = post.getCommentCount();
        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,postId);
        //计算权重
        double w = (wonderful ? 99 : 0) +communtCount * 10 +likeCount * 2;
        //分数 = 帖子权重+距离天数
        double score = Math.log10(Math.max(w,1)+(post.getCreateTime().getTime()-epoch.getTime())/1000*3600*24);
        //更新帖子分数
        discussPostService.updateScore(postId,score);
        //同步搜索数据
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);
    }
}
