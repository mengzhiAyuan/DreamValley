package com.mengzhiayuan.naruto.dao;

import com.mengzhiayuan.naruto.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * @Auther: 梦致A远
 * @Date: 2021/8/21 15:46
 * @Description:
 */
@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void selectById() {
        User user = userMapper.selectById(101);
        System.out.println(user);
    }

    @Test
    void selectByName() {
        User user = userMapper.selectByName("liubei");
        System.out.println(user);
    }

    @Test
    void selectByEmail() {
        User user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    void insertUser() {
        User user=new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.baidu.com/404.jpg");
        user.setCreateTime(new Date());
        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    void updateUser() {
        int i = userMapper.updateStatus(150, 1);
        System.out.println(i);
        i = userMapper.updateHeader(150, "http://naruto.com/naruto.png");
        System.out.println(i);
        i = userMapper.updatePassword(150, "666666");
        System.out.println(i);
    }

}