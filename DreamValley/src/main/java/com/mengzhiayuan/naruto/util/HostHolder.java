package com.mengzhiayuan.naruto.util;

import com.mengzhiayuan.naruto.entity.User;
import org.springframework.stereotype.Component;

/**
 * @Auther: 梦致A远
 * @Date: 2021/8/26 11:34
 * @Description:
 */

/**
 * 持有用户信息,用于代替session对象.
 */

@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}

