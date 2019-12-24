# 巡云轻论坛系统

<br>

#### 项目介绍
巡云轻论坛系统包含论坛、问答模块。采用JAVA+MYSQL架构，自适应手机端和电脑端，界面简洁，性能高效。数据库表结构设计使用分表方案，提高系统的负载能力。 后台数据库备份/还原、全站指定目录打包、一键自动升级等功能使维护简单方便。系统拥有强大的模板管理功能，布局版块支持设置输出条件，让前端页面展示方便快捷。
<br>
为了提高论坛性能，部分功能使用数据库分表设计,默认分为4个表,表中有数据后不建议再调整分表数量。程序带有图形安装界面，第一次使用需执行安装操作。前台所有页面均支持返回HTML和JSON格式数据,API接口文档可在【页面管理】 - 【模板列表】 - 【布局】 - 【版块】 中查看。前台各模块模板和
资源均可在线编辑和管理。前台电脑版和手机版各有一套模板,默认页面使用的模板技术:电脑版使用FreeMarker标签，手机版使用Vue2.0,当然也可以使用其它前端模板框架设计页面进行展示。官网发布新版本后用户可以下载升级包上传到后台进行升级。


  
  <br><br>



#### 技术选型
Spring 5.0 + SpringMVC + JPA + Ehcache(可选Memcached) + Lucene


官方网站：[http://www.diyhi.com/](http://www.diyhi.com/)



演示网站：[http://www.diyhi.com/cms.html](http://www.diyhi.com/cms.html) 页面可获取前后台演示地址、登录账号和密码



安装环境配置参考：[http://www.diyhi.com/hostConfig.html](http://www.diyhi.com/hostConfig.html)



编译好的安装包下载页面：[http://www.diyhi.com/cms.html](http://www.diyhi.com/cms.html)



编译好的安装包安装参考：[http://www.diyhi.com/forumInstall.html](http://www.diyhi.com/forumInstall.html)


<br><br>
#### 使用平台
JDK 1.8及以上 + Tomcat 8.0及以上 + MySQL 5.5.3及以上

<br><br>
#### 源码运行教程

1.将源代码导入到Eclipse中(基于Maven)


2.修改数据库配置文件:修改项目下src\main\resources\druid.properties文件，请自行替换数据库信息
![输入图片说明](https://raw.githubusercontent.com/diyhi/bbs/master/image/100.png "100.png")

3.然后启动Tomcat


4.在Eclipse中打开src\test\java\forum\Init.java执行main方法，将SQL导入到数据库,然后重启Tomcat即可正常运行。管理员初始账号admin 密码1234567(可自行修改)
![输入图片说明](https://raw.githubusercontent.com/diyhi/bbs/master/image/200.png "200.png")



<br><br>
#### 主要功能
（1）话题管理（话题列表、标签列表、全部待审核话题、全部待审核评论、全部待审核回复、话题搜索）
 
（2）问答管理（问题列表、问题标签列表、全部待审核问题、全部待审核答案、全部待审核答案回复、问题搜索） 

（3）留言管理（留言列表） 

（4）友情链接管理(友情链接列表) 

（5）会员管理(会员列表、会员等级、会员注册项、会员角色、会员注册禁止用户名称、会员搜索、登录日志、更换头像、私信、系统通知、提醒、收藏夹、点赞、关注、粉丝) 

（6）会员卡管理(会员卡列表、会员卡订单) 

（7）员工管理(员工列表、角色列表、登录日志) 

（8）在线帮助管理(在线帮助分类、合并分类、在线帮助列表) 

（9）模板管理(模板列表、导出模板、导入模板、版块代码管理、资源管理、布局管理、栏目管理、代码编辑) 

（10）浏览量管理(浏览量列表) 

（11）文件打包管理(压缩文件列表、打包文件) 

（12）系统通知管理(系统通知列表) 

（13）平台收益管理(解锁话题隐藏内容分成、问答悬赏平台分成) 

（14）全站设置(基本设置、维护数据、敏感词、数据库备份/还原、服务器节点参数、升级) 

（15）支付管理(在线支付接口) 

（16）短信管理(短信接口列表、短信发送错误日志) 

（17）缩略图管理(缩略图列表) 



<br><br>
#### 前端界面(电脑版)
![输入图片说明](https://raw.githubusercontent.com/diyhi/bbs/master/image/1.jpg "1.jpg")
<br><br>

![输入图片说明](https://raw.githubusercontent.com/diyhi/bbs/master/image/2.jpg "2.jpg")

<br><br>
![输入图片说明](https://raw.githubusercontent.com/diyhi/bbs/master/image/3.jpg "3.jpg")

<br><br>
![输入图片说明](https://raw.githubusercontent.com/diyhi/bbs/master/image/4.jpg "4.jpg")

<br><br>
![输入图片说明](https://raw.githubusercontent.com/diyhi/bbs/master/image/5.jpg "5.jpg")

<br><br>
![输入图片说明](https://raw.githubusercontent.com/diyhi/bbs/master/image/6.jpg "6.jpg")

<br><br>
![输入图片说明](https://raw.githubusercontent.com/diyhi/bbs/master/image/7.jpg "7.jpg")

<br><br>
![输入图片说明](https://raw.githubusercontent.com/diyhi/bbs/master/image/8.jpg "8.jpg")
<br><br>
![输入图片说明](https://raw.githubusercontent.com/diyhi/bbs/master/image/9.jpg "9.jpg")
<br><br>

#### 前端界面(手机版)
![输入图片说明](https://raw.githubusercontent.com/diyhi/bbs/master/image/m1.jpg "m1.jpg")

<br><br>
![输入图片说明](https://raw.githubusercontent.com/diyhi/bbs/master/image/m2.jpg "m2.jpg")

<br><br>
![输入图片说明](https://raw.githubusercontent.com/diyhi/bbs/master/image/m3.jpg "m3.jpg")

<br><br>
#### 安装界面

![输入图片说明](https://raw.githubusercontent.com/diyhi/bbs/master/image/600.png "600.png")






