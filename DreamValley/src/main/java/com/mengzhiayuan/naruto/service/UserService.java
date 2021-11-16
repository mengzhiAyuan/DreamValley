package com.mengzhiayuan.naruto.service;



import com.mengzhiayuan.naruto.entity.LoginTicket;
import com.mengzhiayuan.naruto.entity.User;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

/**
 * @Auther: 梦致A远
 * @Date: 2021/8/21 12:26
 * @Description:
 */

public interface UserService {

    User findUserById(int id);

    Map<String,Object> register(User user);

    int activation(int userId,String code);

    Map<String,Object> login(String username,String password,int expiredSeconds);

    void logout(String ticket);

    LoginTicket findLoginTicket(String ticket);

//    修改用户头像
    int updateHeader(int userId,String headerUrl);

    User findUserByName(String username);

    Collection<? extends GrantedAuthority> getAuthorities(int userId);

    //修改密码
    Map<String, Object> changePassword(User user, String oldPassword, String newPassword, String confirmPassword);

    User findUserByEmail(String email);

}
