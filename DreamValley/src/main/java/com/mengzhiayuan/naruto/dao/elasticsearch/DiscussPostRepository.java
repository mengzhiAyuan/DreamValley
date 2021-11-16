package com.mengzhiayuan.naruto.dao.elasticsearch;

import com.mengzhiayuan.naruto.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Auther: 梦致A远
 * @Date: 2021/9/14 15:31
 * @Description:
 */
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {

}
