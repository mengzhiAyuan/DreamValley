package com.mengzhiayuan.naruto.service.Impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.mengzhiayuan.naruto.dao.DiscussPostMapper;
import com.mengzhiayuan.naruto.entity.DiscussPost;
import com.mengzhiayuan.naruto.service.DiscussPostService;
import com.mengzhiayuan.naruto.util.SensitiveFilter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @Auther: 梦致A远
 * @Date: 2021/8/21 12:22
 * @Description:
 */

@Service
@Slf4j
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    //本地缓存最大帖子数
    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    //缓存帖子的最大生存时间
    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // Caffeine核心接口： Cache,LoadingCache,AsyncLoadingCache

    //帖子列表缓存
    private LoadingCache<String,List<DiscussPost>> postListCache;

    //帖子总数缓存
    private LoadingCache<Integer,Integer> postRowsCache;

    @PostConstruct
    public void init() {
//        初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(@NonNull String key) throws Exception {
                        if(key==null||key.length()==0){
                            throw new IllegalArgumentException("参数错误！");
                        }

                        String[] params = key.split(":");
                        if(params == null || params.length != 2){
                            throw new IllegalArgumentException("参数错误！");
                        }

                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        //二级缓存 redis --> Mysql

                        log.debug("正在把帖子列表数据从数据库中加载到本地缓存！");
                        return discussPostMapper.selectDiscussPosts(0,offset,limit,1);
                    }
                });
        //初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        log.debug("正在把帖子总数数据从数据库中加载到本地缓存！");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }

    //TODO 本地缓存 缓存帖子列表
    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit,int orderMode) {
        if(userId == 0 && orderMode == 1){
            return postListCache.get(offset + ":" + limit);
        }
        log.debug("正在把帖子列表数据从数据库中加载到本地缓存！");
        return discussPostMapper.selectDiscussPosts(userId, offset, limit,orderMode);
    }

    //TODO 本地缓存 缓存帖子总数
    @Override
    public int findDiscussPostRows(int userId) {
        if (userId == 0){
            return postRowsCache.get(userId);
        }
        log.debug("正在把帖子总数数据从数据库中加载到本地缓存！");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int addDiscussPost(DiscussPost discussPost) {
        if(discussPost==null){
            throw new IllegalArgumentException("讨论帖不能为空！！！");
        }

        //转义HTML标记,不会将标签识别出来。过滤标签
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    @Override
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id,commentCount);
    }

    @Override
    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id,type);
    }

    @Override
    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id,status);
    }

    @Override
    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id,score);
    }


}
