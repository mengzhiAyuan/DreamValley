package com.mengzhiayuan.naruto.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Auther: 梦致A远
 * @Date: 2021/8/25 12:36
 * @Description:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginTicket {

    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;

}
