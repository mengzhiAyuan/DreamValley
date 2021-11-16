package com.mengzhiayuan.naruto.dao;



import com.mengzhiayuan.naruto.NarutoApplication;
import com.mengzhiayuan.naruto.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;


/**
 * @Auther: 梦致A远
 * @Date: 2021/8/21 11:38
 * @Description:
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NarutoApplication.class)
class DiscussPostMapperTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    void selectDiscussPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10,0);
        for (DiscussPost e : discussPosts){
            System.out.println(e);
        }
    }

    @Test
    void selectDiscussPostRows() {
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }

    @Test
    void insertDiscussPost(){
        DiscussPost post = new DiscussPost();
        post.setUserId(333);
        post.setCreateTime(new Date());
        post.setStatus(2);
        post.setType(0);
        discussPostMapper.insertDiscussPost(post);
        System.out.println(post);
    }
}