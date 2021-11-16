package com.mengzhiayuan.naruto.dao;

import com.mengzhiayuan.naruto.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @Auther: 梦致A远
 * @Date: 2021/8/25 14:27
 * @Description:
 */

@Mapper
@Repository
@Deprecated
public interface LoginTicketMapper {

    @Insert({
            "insert into community.login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from community.login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Update({
            "update community.login_ticket set status=#{status} where ticket=#{ticket}"
    })
    int updateStatus(String ticket,int status);

}
