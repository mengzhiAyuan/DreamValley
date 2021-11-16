package com.mengzhiayuan.naruto.controller;

import com.mengzhiayuan.naruto.annotation.LoginRequired;
import com.mengzhiayuan.naruto.entity.User;
import com.mengzhiayuan.naruto.service.FollowService;
import com.mengzhiayuan.naruto.service.LikeService;
import com.mengzhiayuan.naruto.service.UserService;
import com.mengzhiayuan.naruto.util.CommunityConstant;
import com.mengzhiayuan.naruto.util.CommunityUtil;
import com.mengzhiayuan.naruto.util.HostHolder;
import com.mengzhiayuan.naruto.util.RedisKeyUtil;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * @Auther: 梦致A远
 * @Date: 2021/8/26 16:41
 * @Description:
 */

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${quniu.bucket.header.url}")
    private String headerBucketUrl;


    @Autowired
    private RedisTemplate redisTemplate;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(Model model) {
        // 上传文件名称
        String fileName = CommunityUtil.generateUUID();
        // 设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJSONString(0));
        // 生成上传凭证
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);

        model.addAttribute("uploadToken", uploadToken);
        model.addAttribute("fileName", fileName);

        return "/site/setting";
    }

    // 更新头像路径
    @RequestMapping(path = "/header/url", method = RequestMethod.POST)
    @ResponseBody
    public String updateHeaderUrl(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return CommunityUtil.getJSONString(1, "文件名不能为空!");
        }

        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeader(hostHolder.getUser().getId(), url);

        return CommunityUtil.getJSONString(0);
    }

    //废弃
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }

        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf(".")); //  .png  .jpg 等后缀名
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }

        //生成随机文件名
        filename = CommunityUtil.generateUUID() + suffix;
        //确定文件在服务器存放的路径
        File dest = new File(uploadPath + "/" + filename);
        //存储文件
        try {
            //防止上传图片重复，将图片重命名 UUID
            headerImage.transferTo(dest); //transferTo(File dest) 用来把 MultipartFile 转换换成 File
        } catch (IOException e) {
            log.error("上传文件失败： +" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器出现异常！！！",e);
        }

        //更新当前用户的头像的路径(Web访问路径)
        //http://loaclhost:8080/community/user/header/xxx.jpg
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

     // 废弃，现在直接访问七牛云
//    @GetMapping("/header/{fileName}")
//    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
//        //服务器存放路径
//        fileName = uploadPath + "/" + fileName;
//        //文件后缀
//        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);  // jpg
//        //响应图片 response.setContentType(MIME)的作用是使客户端浏览器，区分不同种类的数据，并根据不同的MIME调用浏览器内不同的程序嵌入模块来处理相应的数据
//        response.setContentType("image/"+suffix);
//
//        FileInputStream fileInputStream= null;
//        OutputStream os = null;
//        try {
//            fileInputStream = new FileInputStream(fileName);
//            os = response.getOutputStream();
//            byte[] buffer = new byte[1024];
//            int b=0;
//            while((b=fileInputStream.read(buffer))!=-1){
//                os.write(buffer,0,b);
//            }
//        } catch (IOException e) {
//            log.error("读取头像失败：" + e.getMessage());
//        } finally {
//            if(fileInputStream!=null){
//                try {
//                    fileInputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if(os!=null){
//                try {
//                    os.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//    }

    //修改密码，model变量用来向页面返回数据
    //@LoginRequired
    @RequestMapping(path = "/changePassword", method = {RequestMethod.GET,RequestMethod.POST})
    public String changePassword(String oldPassword,String newPassword,String confirmPassword, Model model) {
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.changePassword(user,oldPassword, newPassword, confirmPassword);
        if(map == null || map.isEmpty()){
            return "redirect:/index";
        }else {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            log.error(oldPassword);
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            log.error(newPassword);
            model.addAttribute("confirmPasswordMsg", map.get("confirmPasswordMsg"));
            return "/site/setting";
        }
    }

    @RequestMapping(path = "/forgetPassword", method = RequestMethod.GET)
    //忘记密码
    public String forgetPassword() {
        return "/site/forget";
    }

    //个人主页
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user = userService.findUserById(userId);
        if(user==null){
            throw new RuntimeException("该用户不存在");
        }

        //用户
        model.addAttribute("user",user);
        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);
        //是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUser()!=null){   //当前用户是否已经关注该作者
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);


        return "/site/profile";
    }


}
