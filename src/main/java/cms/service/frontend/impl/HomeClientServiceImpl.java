package cms.service.frontend.impl;

import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.mediaProcess.MediaProcessQueueCacheManager;
import cms.component.mediaProcess.MediaProcessQueueComponent;
import cms.component.question.AnswerCacheManager;
import cms.component.question.QuestionCacheManager;
import cms.component.staff.StaffCacheManager;
import cms.component.topic.CommentCacheManager;
import cms.component.topic.TopicCacheManager;
import cms.component.topic.TopicComponent;
import cms.component.topic.TopicUnhideConfig;
import cms.component.user.UserCacheManager;
import cms.component.user.UserRoleComponent;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.topic.HideTagType;
import cms.dto.user.AccessUser;
import cms.dto.user.ResourceEnum;
import cms.model.question.Answer;
import cms.model.question.AnswerReply;
import cms.model.question.Question;
import cms.model.setting.SystemSetting;
import cms.model.staff.SysUsers;
import cms.model.topic.Comment;
import cms.model.topic.Reply;
import cms.model.topic.Topic;
import cms.model.topic.TopicUnhide;
import cms.model.user.User;
import cms.model.user.UserDynamic;
import cms.model.user.UserGrade;
import cms.model.user.UserLoginLog;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserGradeRepository;
import cms.repository.user.UserRepository;
import cms.service.frontend.HomeClientService;
import cms.utils.AccessUserThreadLocal;
import cms.utils.IpAddress;
import cms.utils.SecureLink;
import cms.utils.WebUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 前台用户中心服务
 */
@Service
public class HomeClientServiceImpl implements HomeClientService {
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    FileComponent fileComponent;
    @Resource
    UserRepository userRepository;
    @Resource
    UserGradeRepository userGradeRepository;
    @Resource UserCacheManager userCacheManager;
    @Resource
    UserRoleComponent userRoleComponent;
    @Resource StaffCacheManager staffCacheManager;
    @Resource TopicCacheManager topicCacheManager;
    @Resource CommentCacheManager commentCacheManager;
    @Resource QuestionCacheManager questionCacheManager;
    @Resource AnswerCacheManager answerCacheManager;
    @Resource
    MediaProcessQueueComponent mediaProcessQueueComponent;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    TopicComponent topicComponent;
    @Resource
    TopicUnhideConfig topicUnhideConfig;
    @Resource MediaProcessQueueCacheManager mediaProcessQueueCacheManager;


    /**
     * 获取用户信息
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getUserInfo(String userName, HttpServletRequest request){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        String _userName = accessUser.getUserName();
        //是否为会员自身
        boolean flag = true;
        if(userName != null && !userName.trim().isEmpty()){
            if(!userName.trim().equals(accessUser.getUserName())){
                _userName = userName.trim();
                flag = false;
            }
        }


        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        //获取登录用户
        User new_user = userCacheManager.query_cache_findUserByUserName(_userName);
        if(new_user != null && new_user.getState().equals(1) && new_user.getCancelAccountTime().equals(-1L)){
            List<UserGrade> userGradeList = userGradeRepository.findAllGrade_cache();//取得用户所有等级
            if(userGradeList != null && userGradeList.size() >0){
                for(UserGrade userGrade : userGradeList){
                    if(new_user.getPoint() >= userGrade.getNeedPoint()){
                        new_user.setGradeId(userGrade.getId());
                        new_user.setGradeName(userGrade.getName());//将等级值设进等级参数里
                        break;
                    }
                }


            }

            List<String> userRoleNameList = userRoleComponent.queryUserRoleName(new_user.getUserName(),request);


            if(flag){
                //仅显示指定的字段
                User viewUser = new User();
                viewUser.setId(new_user.getId());
                viewUser.setUserName(new_user.getUserName());//会员用户名
                viewUser.setAccount(new_user.getAccount());//账号
                viewUser.setNickname(new_user.getNickname());//呢称
                viewUser.setState(new_user.getState());//用户状态
                viewUser.setIssue(new_user.getIssue());//密码提示问题
                viewUser.setRegistrationDate(new_user.getRegistrationDate());//注册日期
                viewUser.setPoint(new_user.getPoint());//当前积分
                viewUser.setGradeId(new_user.getGradeId());//等级Id
                viewUser.setGradeName(new_user.getGradeName());//等级名称
                viewUser.setMobile(new_user.getMobile());//绑定手机
                viewUser.setRealNameAuthentication(new_user.isRealNameAuthentication());//是否实名认证
                viewUser.setAvatarPath(fileComponent.fileServerAddress(request)+new_user.getAvatarPath());//头像路径
                viewUser.setAvatarName(new_user.getAvatarName());//头像名称
                //只有中国地区简体中文语言才显示IP归属地
                if(systemSetting.isShowIpAddress() && new_user.getId() != null && IpAddress.isChinaRegion(IpAddress.getClientIpAddress(request))){
                    UserLoginLog userLoginLog = userRepository.findFirstUserLoginLog(new_user.getId());
                    if(userLoginLog != null && IpAddress.isChinaRegion(userLoginLog.getIp())){
                        viewUser.setIpAddress(IpAddress.queryProvinceAddress(userLoginLog.getIp()));
                    }
                }
                if(userRoleNameList != null && userRoleNameList.size() >0){
                    viewUser.setUserRoleNameList(userRoleNameList);//话题允许查看的角色名称集合
                }

                returnValue.put("user", viewUser);
            }else{//如果不是登录会员自身，则仅显示指定字段
                User other_user = new User();
                other_user.setId(new_user.getId());//Id
                other_user.setUserName(new_user.getUserName());//会员用户名
                other_user.setAccount(new_user.getAccount());//账号
                other_user.setNickname(new_user.getNickname());//呢称
                other_user.setState(new_user.getState());//用户状态
                other_user.setPoint(new_user.getPoint());//当前积分
                other_user.setGradeId(new_user.getGradeId());//等级Id
                other_user.setGradeName(new_user.getGradeName());//等级名称
                other_user.setAvatarPath(fileComponent.fileServerAddress(request)+new_user.getAvatarPath());//头像路径
                other_user.setAvatarName(new_user.getAvatarName());//头像名称
                if(userRoleNameList != null && userRoleNameList.size() >0){
                    other_user.setUserRoleNameList(userRoleNameList);//话题允许查看的角色名称集合
                }
                //只有中国地区简体中文语言才显示IP归属地
                if(systemSetting.isShowIpAddress() && new_user.getId() != null && IpAddress.isChinaRegion(IpAddress.getClientIpAddress(request))){
                    UserLoginLog userLoginLog = userRepository.findFirstUserLoginLog(new_user.getId());
                    if(userLoginLog != null){
                        other_user.setIpAddress(IpAddress.queryProvinceAddress(userLoginLog.getIp()));
                    }
                }
                returnValue.put("user", other_user);
            }


        }

        if(!returnValue.containsKey("user")){
            SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(_userName);
            if(sysUsers != null){//显示员工信息
                User admin_user = new User();
                admin_user.setId(-1L);
                admin_user.setAccount(sysUsers.getUserAccount());//账号
                admin_user.setNickname(sysUsers.getNickname());//呢称
                admin_user.setAvatarPath(fileComponent.fileServerAddress(request)+sysUsers.getAvatarPath());//头像路径
                admin_user.setAvatarName(sysUsers.getAvatarName());//头像名称
                returnValue.put("user", admin_user);
            }
        }

        return returnValue;
    }

    /**
     * 获取用户动态列表
     * @param userName 用户名称
     * @param page 页码
     * @param request 请求信息
     * @return
     */
    public PageView<UserDynamic> getUserDynamicList(String userName, int page,HttpServletRequest request){

        //调用分页算法代码
        PageView<UserDynamic> pageView = new PageView<UserDynamic>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();


        Long _userId = null;
        String _userName = null;
        if(userName != null && !userName.trim().isEmpty()){
            if(userName.equals(accessUser.getUserName())){//自身
                _userId = accessUser.getUserId();
                _userName = accessUser.getUserName();
            }else{//其它用户
                User user = userCacheManager.query_cache_findUserByUserName(userName);
                if(user != null && user.getAllowUserDynamic() != null && user.getAllowUserDynamic() &&  user.getState().equals(1) && user.getCancelAccountTime().equals(-1L)){//允许显示
                    _userId = user.getId();
                    _userName = user.getUserName();
                }
            }
        }else{
            _userId = accessUser.getUserId();
            _userName = accessUser.getUserName();
        }

        if(_userId != null){
            //当前页
            int firstIndex = (page-1)*pageView.getMaxresult();
            QueryResult<UserDynamic> qr = userRepository.findUserDynamicPage(_userId,_userName,firstIndex,pageView.getMaxresult());

            if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
                for(UserDynamic userDynamic : qr.getResultlist()){
                    User user = userCacheManager.query_cache_findUserByUserName(userDynamic.getUserName());
                    if(user != null){
                        userDynamic.setAccount(user.getAccount());
                        userDynamic.setNickname(user.getNickname());
                        userDynamic.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                        userDynamic.setAvatarName(user.getAvatarName());
                    }




                    if(userDynamic.getTopicId() >0L){
                        Topic topicInfo = topicCacheManager.queryTopicCache(userDynamic.getTopicId());//查询缓存
                        if(topicInfo != null){
                            userDynamic.setTopicTitle(topicInfo.getTitle());
                            userDynamic.setTopicViewTotal(topicInfo.getViewTotal());
                            userDynamic.setTopicCommentTotal(topicInfo.getCommentTotal());
                            userDynamic.setIsMarkdown(topicInfo.getIsMarkdown());
                            List<String> topicRoleNameList = userRoleComponent.queryAllowViewTopicRoleName(topicInfo.getTagId(),request);


                            if(topicRoleNameList != null && topicRoleNameList.size() >0){
                                userDynamic.setAllowRoleViewList(topicRoleNameList);//话题允许查看的角色名称集合
                            }
                            SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
                            //话题内容摘要MD5
                            String topicContentDigest_link = "";
                            String topicContentDigest_video = "";

                            if(topicInfo.getContent() != null && !topicInfo.getContent().trim().isEmpty()){
                                //处理富文本路径
                                topicInfo.setContent(fileComponent.processRichTextFilePath(topicInfo.getContent(),"topic"));
                            }
                            //处理文件防盗链
                            if(topicInfo.getContent() != null && !topicInfo.getContent().trim().isEmpty() && systemSetting.getFileSecureLinkSecret() != null && !systemSetting.getFileSecureLinkSecret().trim().isEmpty()){
                                List<String> serverAddressList = fileComponent.fileServerAllAddress(request);
                                //解析上传的文件完整路径名称
                                Map<String,String> analysisFullFileNameMap = topicCacheManager.query_cache_analysisFullFileName(topicInfo.getContent(),topicInfo.getId(),serverAddressList);
                                if(analysisFullFileNameMap != null && analysisFullFileNameMap.size() >0){


                                    Map<String,String> newFullFileNameMap = new HashMap<String,String>();//新的完整路径名称 key: 完整路径名称 value: 重定向接口
                                    for (Map.Entry<String,String> entry : analysisFullFileNameMap.entrySet()) {

                                        newFullFileNameMap.put(entry.getKey(), WebUtil.getUrl(request)+ SecureLink.createDownloadRedirectLink(entry.getKey(),entry.getValue(),topicInfo.getTagId(),systemSetting.getFileSecureLinkSecret()));
                                    }

                                    Integer topicContentUpdateMark = topicCacheManager.query_cache_markUpdateTopicStatus(topicInfo.getId(), ThreadLocalRandom.current().nextInt(10000000, 100000000));//8位随机数
                                    //生成处理'上传的文件完整路径名称'Id
                                    String processFullFileNameId = topicComponent.createProcessFullFileNameId(topicInfo.getId(),topicContentUpdateMark,newFullFileNameMap);

                                    topicInfo.setContent(topicCacheManager.query_cache_processFullFileName(topicInfo.getContent(),"topic",newFullFileNameMap,processFullFileNameId,serverAddressList));

                                    topicContentDigest_link = cms.utils.MD5.getMD5(processFullFileNameId);
                                }


                            }


                            //处理视频播放器标签
                            if(topicInfo.getContent() != null && !topicInfo.getContent().trim().isEmpty()){
                                Integer topicContentUpdateMark = topicCacheManager.query_cache_markUpdateTopicStatus(topicInfo.getId(), ThreadLocalRandom.current().nextInt(10000000, 100000000));//8位随机数

                                //生成处理'视频播放器'Id
                                String processVideoPlayerId = mediaProcessQueueComponent.createProcessVideoPlayerId(topicInfo.getId(),topicContentUpdateMark);

                                //处理视频播放器标签
                                String content = mediaProcessQueueCacheManager.query_cache_processVideoPlayer(WebUtil.getUrl(request),topicInfo.getContent(),processVideoPlayerId+"|"+topicContentDigest_link,topicInfo.getTagId(),systemSetting.getFileSecureLinkSecret());
                                topicInfo.setContent(content);

                                topicContentDigest_video = cms.utils.MD5.getMD5(processVideoPlayerId);
                            }


                            //处理隐藏标签
                            if(userDynamic.getModule().equals(100) && topicInfo.getContent() != null && !topicInfo.getContent().trim().isEmpty()){
                                //允许可见的隐藏标签
                                List<Integer> visibleTagList = new ArrayList<Integer>();

                                //如果话题由当前用户发表，则显示全部隐藏内容
                                if(!topicInfo.getIsStaff()&& topicInfo.getUserName().equals(accessUser.getUserName())){
                                    visibleTagList.add(HideTagType.PASSWORD.getName());
                                    visibleTagList.add(HideTagType.COMMENT.getName());
                                    visibleTagList.add(HideTagType.GRADE.getName());
                                    visibleTagList.add(HideTagType.POINT.getName());
                                    visibleTagList.add(HideTagType.AMOUNT.getName());
                                }else{
                                    //解析隐藏标签
                                    Map<Integer,Object> analysisHiddenTagMap = topicCacheManager.query_cache_analysisHiddenTag(topicInfo.getContent(),topicInfo.getId());
                                    if(analysisHiddenTagMap != null && analysisHiddenTagMap.size() >0){
                                        for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {

                                            if(entry.getKey().equals(HideTagType.PASSWORD.getName())){//输入密码可见
                                                //话题取消隐藏Id
                                                String topicUnhideId = topicUnhideConfig.createTopicUnhideId(accessUser.getUserId(), HideTagType.PASSWORD.getName(), userDynamic.getTopicId());

                                                TopicUnhide topicUnhide = topicCacheManager.query_cache_findTopicUnhideById(topicUnhideId);

                                                if(topicUnhide != null){
                                                    visibleTagList.add(HideTagType.PASSWORD.getName());//当前话题已经取消隐藏
                                                }
                                            }else if(entry.getKey().equals(HideTagType.COMMENT.getName())){//评论话题可见
                                                Boolean isUnhide = topicCacheManager.query_cache_findWhetherCommentTopic(userDynamic.getTopicId(),accessUser.getUserName());
                                                if(isUnhide){
                                                    visibleTagList.add(HideTagType.COMMENT.getName());//当前话题已经取消隐藏
                                                }
                                            }else if(entry.getKey().equals(HideTagType.GRADE.getName())){//超过等级可见
                                                User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
                                                if(_user != null){
                                                    List<UserGrade> userGradeList = userGradeRepository.findAllGrade_cache();//取得用户所有等级
                                                    if(userGradeList != null && userGradeList.size() >0){
                                                        for(UserGrade userGrade : userGradeList){
                                                            if(_user.getPoint() >= userGrade.getNeedPoint() && (Long)entry.getValue() <=userGrade.getNeedPoint()){
                                                                visibleTagList.add(HideTagType.GRADE.getName());//当前话题已经取消隐藏

                                                                break;
                                                            }
                                                        }


                                                    }
                                                }

                                            }else if(entry.getKey().equals(HideTagType.POINT.getName())){//积分购买可见
                                                //话题取消隐藏Id
                                                String topicUnhideId = topicUnhideConfig.createTopicUnhideId(accessUser.getUserId(), HideTagType.POINT.getName(), userDynamic.getTopicId());

                                                TopicUnhide topicUnhide = topicCacheManager.query_cache_findTopicUnhideById(topicUnhideId);

                                                if(topicUnhide != null){
                                                    visibleTagList.add(HideTagType.POINT.getName());//当前话题已经取消隐藏
                                                }
                                            }else if(entry.getKey().equals(HideTagType.AMOUNT.getName())){//余额购买可见
                                                //话题取消隐藏Id
                                                String topicUnhideId = topicUnhideConfig.createTopicUnhideId(accessUser.getUserId(), HideTagType.AMOUNT.getName(), userDynamic.getTopicId());
                                                TopicUnhide topicUnhide = topicCacheManager.query_cache_findTopicUnhideById(topicUnhideId);

                                                if(topicUnhide != null){
                                                    visibleTagList.add(HideTagType.AMOUNT.getName());//当前话题已经取消隐藏
                                                }
                                            }

                                        }



                                        //内容含有隐藏标签类型
                                        LinkedHashMap<Integer,Boolean> hideTagTypeMap = new LinkedHashMap<Integer,Boolean>();//key:内容含有隐藏标签类型  10.输入密码可见  20.评论话题可见  30.达到等级可见 40.积分购买可见 50.余额购买可见  value:当前用户是否已对隐藏内容解锁
                                        for (HideTagType hideTagType : HideTagType.values()) {//按枚举类的顺序排序
                                            for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {
                                                if(entry.getKey().equals(hideTagType.getName())){
                                                    if(visibleTagList.contains(entry.getKey())){
                                                        hideTagTypeMap.put(entry.getKey(), true);

                                                    }else{
                                                        hideTagTypeMap.put(entry.getKey(), false);
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                        userDynamic.setHideTagTypeMap(hideTagTypeMap);

                                    }



                                }


                                Integer topicContentUpdateMark = topicCacheManager.query_cache_markUpdateTopicStatus(userDynamic.getTopicId(), ThreadLocalRandom.current().nextInt(10000000, 100000000));//8位随机数

                                //生成处理'隐藏标签'Id
                                String processHideTagId = topicComponent.createProcessHideTagId(userDynamic.getTopicId(),topicContentUpdateMark, visibleTagList);

                                //处理隐藏标签
                                String content = topicCacheManager.query_cache_processHiddenTag(topicInfo.getContent(),visibleTagList,processHideTagId+"|"+topicContentDigest_link+"|"+ topicContentDigest_video);
                                userDynamic.setTopicContent(content);

                                //如果话题不是当前用户发表的，则检查用户对话题的查看权限
                                if(topicInfo.getIsStaff() == false && !topicInfo.getUserName().equals(accessUser.getUserName())){
                                    //是否有当前功能操作权限
                                    boolean flag = userRoleComponent.isPermission(ResourceEnum._1001000,topicInfo.getTagId());
                                    if(!flag){//如果没有查看权限
                                        userDynamic.setTopicContent("");
                                    }
                                }

                            }


                        }
                    }


                    if(userDynamic.getModule().equals(200)){//评论
                        Comment comment = commentCacheManager.query_cache_findByCommentId(userDynamic.getCommentId());
                        if(comment != null){
                            if(comment.getContent() != null && !comment.getContent().trim().isEmpty()){
                                //处理富文本路径
                                comment.setContent(fileComponent.processRichTextFilePath(comment.getContent(),"comment"));
                            }
                            userDynamic.setCommentContent(comment.getContent());
                            userDynamic.setIsMarkdown(comment.getIsMarkdown());
                        }

                    }
                    if(userDynamic.getModule().equals(300)){//引用评论
                        Comment comment = commentCacheManager.query_cache_findByCommentId(userDynamic.getCommentId());
                        if(comment != null){
                            if(comment.getContent() != null && !comment.getContent().trim().isEmpty()){
                                //处理富文本路径
                                comment.setContent(fileComponent.processRichTextFilePath(comment.getContent(),"comment"));
                            }
                            userDynamic.setCommentContent(comment.getContent());
                            userDynamic.setIsMarkdown(comment.getIsMarkdown());
                        }
                        Comment quoteComment = commentCacheManager.query_cache_findByCommentId(userDynamic.getQuoteCommentId());
                        if(quoteComment != null && quoteComment.getStatus().equals(20)){
                            userDynamic.setQuoteCommentContent(textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(quoteComment.getContent())));
                        }
                    }
                    if(userDynamic.getModule().equals(400)){//回复
                        Reply reply = commentCacheManager.query_cache_findReplyByReplyId(userDynamic.getReplyId());
                        if(reply != null){
                            userDynamic.setReplyContent(reply.getContent());
                        }
                    }

                    //问题
                    if(userDynamic.getQuestionId() >0L){
                        Question questionInfo = questionCacheManager.query_cache_findById(userDynamic.getQuestionId());//查询缓存
                        if(questionInfo != null){
                            if(questionInfo.getContent() != null && !questionInfo.getContent().trim().isEmpty()){
                                //处理富文本路径
                                questionInfo.setContent(fileComponent.processRichTextFilePath(questionInfo.getContent(),"question"));
                            }
                            userDynamic.setQuestionTitle(questionInfo.getTitle());
                            userDynamic.setQuestionViewTotal(questionInfo.getViewTotal());
                            userDynamic.setQuestionAnswerTotal(questionInfo.getAnswerTotal());
                            userDynamic.setQuestionContent(questionInfo.getContent());
                            userDynamic.setIsMarkdown(questionInfo.getIsMarkdown());
                        }

                    }
                    if(userDynamic.getModule().equals(600)){//600.答案
                        Answer answer = answerCacheManager.query_cache_findByAnswerId(userDynamic.getAnswerId());
                        if(answer != null){
                            if(answer.getContent() != null && !answer.getContent().trim().isEmpty()){
                                //处理富文本路径
                                answer.setContent(fileComponent.processRichTextFilePath(answer.getContent(),"answer"));
                            }
                            userDynamic.setAnswerContent(answer.getContent());
                            userDynamic.setIsMarkdown(answer.getIsMarkdown());
                        }

                    }
                    if(userDynamic.getModule().equals(700)){//700.答案回复
                        AnswerReply answerReply = answerCacheManager.query_cache_findReplyByReplyId(userDynamic.getAnswerReplyId());
                        if(answerReply != null){
                            userDynamic.setAnswerReplyContent(answerReply.getContent());
                        }
                    }
                }
            }

            //将查询结果集传给分页List
            pageView.setQueryResult(qr);

        }
        return pageView;
    }
}
