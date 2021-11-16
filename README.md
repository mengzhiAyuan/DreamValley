# DreamValley
该论坛是一个互动交流平台，有讨论，点赞，关注，网站统计UV，DAU等等功能，你可以技术交流或分享有趣的事情，快来加入mengzhiAyuan的奇幻之旅吧

演示地址 ： mengzhiayuan.top

公众号：鸣人哈说java（加入我微信）

![4snimn.jpg](https://z3.ax1x.com/2021/09/25/4snimn.jpg)

开发环境

| 工具          | 版本号        |
| :------------ | ------------- |
| Mysql         | 8.0.1         |
| Redis         | 3.2           |
| Kafka         | 2.13-2.8.0    |
| Elasticsearch | 6.4.3         |
| SpringBoot    | 2.1.5.RELEASE |
| JDK           | 1.8           |

还有我画的流程图/设计图！！！   帮你摸清项目几个模块的思路  

首先来看重构前的登录模块 @mengzhiayuan

 ![验证码 登录模块：重构前 (2)](https://z3.ax1x.com/2021/11/16/IW4s91.png)

验证码登录模块重构后![验证码 登录模块：重构后 (2)](https://z3.ax1x.com/2021/11/16/IW4DhR.png)

![登录凭证（重构前） (1)](https://z3.ax1x.com/2021/11/16/IW41ts.png)

针对凭证这种过期的特性，我考虑到存入redis中

![登录凭证（重构后） (1)](https://z3.ax1x.com/2021/11/16/IW4Gpq.png)

使用Redis存储登录ticket和验证码，解决分布式session问题（以上，而且key过期就无效很符合业务场景）

重构前

![请求拦截器（重构前） (1)](https://z3.ax1x.com/2021/11/16/IW4UnU.png)

重构后

![请求拦截器（重构后） (1)](https://z3.ax1x.com/2021/11/16/IW43hn.png)



注册激活模块  @mengzhiayaun

![注册激活账号 (1)](https://z3.ax1x.com/2021/11/16/IW46c6.png)

l 使用Redis的set实现点赞，zset实现关注，HyperLogLog统计UV，Bitmap统计DAU；

l 使用Kafka处理发送评论、点赞和关注等系统通知，起到解耦和异步调用的作用；

![点赞功能 (1)](https://z3.ax1x.com/2021/11/16/IW4tXT.png)

![关注模块 (1)](https://z3.ax1x.com/2021/11/16/IW4J10.png)

系统的交通中枢  @mengzhiayuan   设计的优雅永不过时

<img src="https://z3.ax1x.com/2021/11/16/IW4BN9.md.png" alt="IW4BN9.png" style="zoom:150%;" />

缓存与数据库系统的设计

![重构&redis缓存用户信息 (1)](https://z3.ax1x.com/2021/11/16/IW4y1x.png)

网站数据统计

![网站数据统计](https://z3.ax1x.com/2021/11/16/IW40AJ.png)

热帖排行

![热帖排名 (4)](https://z3.ax1x.com/2021/11/16/IW4d74.png)

搜索模块

![搜索模块 (1)](https://z3.ax1x.com/2021/11/16/IW4aBF.png)



看设计图能更好的摸清楚这个项目的！

我总结下功能点

- 使用Spring Security 做权限控制，替代拦截器的拦截控制，并使用自己的认证方案替代Security 认证流程，使权限认证和控制更加方便灵活。
- 使用Redis的set实现点赞，zset实现关注，并使用Redis存储登录ticket和验证码，解决分布式session问题。
- 使用Redis高级数据类型HyperLogLog统计UV(Unique Visitor),使用Bitmap统计DAU(Daily Active User)。
- 使用Kafka处理发送评论、点赞和关注等系统通知，并使用事件进行封装，构建了强大的异步消息系统。
- 使用Elasticsearch做全局搜索，并通过事件封装，增加关键词高亮显示等功能。
- 对热帖排行模块，使用分布式缓存Redis和本地缓存Caffeine作为多级缓存，避免了缓存雪崩，将QPS提升了20倍(10-200)，大大提升了网站访问速度。并使用Quartz定时更新热帖排行。

加油！！！！！ @mengzhiayaun  @周津卓

![Screenshot 2021 07 29 225519](https://z3.ax1x.com/2021/07/29/Wqgwzn.jpg)

