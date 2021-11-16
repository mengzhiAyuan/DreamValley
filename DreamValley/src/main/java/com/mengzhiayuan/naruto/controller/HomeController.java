package com.mengzhiayuan.naruto.controller;


import com.mengzhiayuan.naruto.entity.DiscussPost;
import com.mengzhiayuan.naruto.entity.Page;
import com.mengzhiayuan.naruto.entity.User;
import com.mengzhiayuan.naruto.service.DiscussPostService;
import com.mengzhiayuan.naruto.service.LikeService;
import com.mengzhiayuan.naruto.service.UserService;
import com.mengzhiayuan.naruto.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: 梦致A远
 * @Date: 2021/8/21 16:57
 * @Description:
 */

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/")
    public String root(){
        return "forward:/index";
    }

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode",defaultValue = "0")int orderMode){ //0:是最新排行  1:是最热排行
        // 方法调用前,SpringMVC会自动实例化Model和Page,并将Page注入Model.
        // 所以,在thymeleaf中可以直接访问Page对象中的数据.
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode="+ orderMode);

        List<DiscussPost> discussPostList = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(),orderMode);
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        if(discussPostList!=null){
            for(DiscussPost post : discussPostList){
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);

                //当前帖子有多少赞
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode",orderMode);
        return "/index";
    }

    @GetMapping("/error")
    public String getErrorPage(){
        return "/error/500";
    }

    @GetMapping("/denied")
    public String getDeniedPage(){
        return "/error/404";
    }


}
