package com.mengzhiayuan.naruto.util;


import com.mengzhiayuan.naruto.NarutoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @Auther: 梦致A远
 * @Date: 2021/8/24 11:14
 * @Description:
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NarutoApplication.class)
class MailClientTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    void sendMail() {
        mailClient.sendMail("mengzhiayuan@sina.com","TEST","naruto I LOVE YOU ！！！");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username","naruto ! I LOVE YOU");
        String result = templateEngine.process("/mail/demo",context);
        System.out.println(result);
        mailClient.sendMail("mengzhiayuan@sina.com","至最爱的你",result);
    }
}