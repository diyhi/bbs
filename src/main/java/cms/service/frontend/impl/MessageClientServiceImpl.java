package cms.service.frontend.impl;

import cms.component.CaptchaComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.filterWord.SensitiveWordFilterComponent;
import cms.component.follow.FollowComponent;
import cms.component.message.*;
import cms.component.question.AnswerCacheManager;
import cms.component.question.QuestionCacheManager;
import cms.component.setting.SettingCacheManager;
import cms.component.topic.CommentCacheManager;
import cms.component.topic.TopicCacheManager;
import cms.component.user.UserCacheManager;
import cms.component.user.UserRoleComponent;
import cms.config.BusinessException;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.message.UnreadMessage;
import cms.dto.riskControl.BlockedFeaturesEnum;
import cms.dto.user.AccessUser;
import cms.dto.user.FormCaptcha;
import cms.dto.user.ResourceEnum;
import cms.model.message.PrivateMessage;
import cms.model.message.Remind;
import cms.model.message.SubscriptionSystemNotify;
import cms.model.message.SystemNotify;
import cms.model.question.Answer;
import cms.model.question.AnswerReply;
import cms.model.question.Question;
import cms.model.setting.SystemSetting;
import cms.model.topic.Comment;
import cms.model.topic.Reply;
import cms.model.topic.Tag;
import cms.model.topic.Topic;
import cms.model.user.User;
import cms.model.user.UserGrade;
import cms.repository.message.PrivateMessageRepository;
import cms.repository.message.RemindRepository;
import cms.repository.message.SystemNotifyRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.topic.TagRepository;
import cms.repository.user.UserGradeRepository;
import cms.repository.user.UserRepository;
import cms.service.frontend.MessageClientService;
import cms.utils.AccessUserThreadLocal;
import cms.utils.HtmlEscape;
import cms.utils.UUIDUtil;
import cms.utils.WebUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

/**
 * 前台消息服务
 */
@Service
public class MessageClientServiceImpl implements MessageClientService {
    @Resource
    SettingRepository settingRepository;
    @Resource
    FileComponent fileComponent;
    @Resource
    CaptchaComponent captchaComponent;
    @Resource
    UserRepository userRepository;
    @Resource
    FollowComponent followComponent;
    @Resource RemindCacheManager remindCacheManager;
    @Resource SystemNotifyCacheManager systemNotifyCacheManager;
    @Resource PrivateMessageCacheManager privateMessageCacheManager;
    @Resource UserCacheManager userCacheManager;
    @Resource
    PrivateMessageRepository privateMessageRepository;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    UserGradeRepository userGradeRepository;
    @Resource SettingCacheManager settingCacheManager;
    @Resource
    PrivateMessageComponent privateMessageComponent;
    @Resource
    PrivateMessageConfig privateMessageConfig;
    @Resource
    SystemNotifyRepository systemNotifyRepository;
    @Resource
    SubscriptionSystemNotifyComponent subscriptionSystemNotifyComponent;
    @Resource SubscriptionSystemNotifyConfig subscriptionSystemNotifyConfig;
    @Resource
    RemindRepository remindRepository;
    @Resource
    TagRepository tagRepository;
    @Resource TopicCacheManager topicCacheManager;
    @Resource CommentCacheManager commentCacheManager;
    @Resource QuestionCacheManager questionCacheManager;
    @Resource AnswerCacheManager answerCacheManager;
    @Resource
    UserRoleComponent userRoleComponent;
    @Resource
    SensitiveWordFilterComponent sensitiveWordFilterComponent;


    /**
     * 获取私信列表
     * @param page 页码
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public PageView<PrivateMessage> getPrivateMessageList(int page, String fileServerAddress){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        //调用分页算法代码
        PageView<PrivateMessage> pageView = new PageView<PrivateMessage>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        //用户Id集合
        Set<Long> userIdList = new HashSet<Long>();
        //用户集合
        Map<Long,User> userMap = new HashMap<Long,User>();

        QueryResult<PrivateMessage> qr = privateMessageRepository.findPrivateMessageByUserId(accessUser.getUserId(),100,firstIndex,pageView.getMaxresult());
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(PrivateMessage privateMessage : qr.getResultlist()){
                userIdList.add(privateMessage.getSenderUserId());//发送者用户Id
                userIdList.add(privateMessage.getReceiverUserId());//接受者用户Id

                privateMessage.setSendTime(LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(privateMessage.getSendTimeFormat()),
                        ZoneId.systemDefault()
                ));
                if(privateMessage.getReadTimeFormat() != null){
                    privateMessage.setReadTime(LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(privateMessage.getReadTimeFormat()),
                            ZoneId.systemDefault()
                    ));
                }
                privateMessage.setMessageContent(textFilterComponent.filterText(privateMessage.getMessageContent()));
            }
        }

        if( userIdList.size() >0){
            for(Long userId : userIdList){
                User user = userCacheManager.query_cache_findUserById(userId);
                if(user != null){
                    userMap.put(userId, user);
                }
            }
        }
        if(userMap.size() >0){
            if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
                for(PrivateMessage privateMessage : qr.getResultlist()){
                    User friend_user = userMap.get(privateMessage.getFriendUserId());
                    if(friend_user != null){
                        privateMessage.setFriendUserName(friend_user.getUserName());//私信对方用户名称
                        if(friend_user.getCancelAccountTime().equals(-1L)){
                            privateMessage.setFriendAccount(friend_user.getAccount());
                            privateMessage.setFriendNickname(friend_user.getNickname());
                            if(friend_user.getAvatarName() != null && !friend_user.getAvatarName().trim().isEmpty()){
                                privateMessage.setFriendAvatarPath(fileServerAddress+friend_user.getAvatarPath());//私信对方头像路径
                                privateMessage.setFriendAvatarName(friend_user.getAvatarName());//私信对方头像名称
                            }
                        }
                    }
                    User sender_user = userMap.get(privateMessage.getSenderUserId());
                    if(sender_user != null){
                        privateMessage.setSenderUserName(sender_user.getUserName());//私信发送者用户名称
                        privateMessage.setSenderAccount(sender_user.getAccount());
                        privateMessage.setSenderNickname(sender_user.getNickname());
                        if(sender_user.getAvatarName() != null && !sender_user.getAvatarName().trim().isEmpty()){
                            privateMessage.setSenderAvatarPath(fileServerAddress+sender_user.getAvatarPath());//发送者头像路径
                            privateMessage.setSenderAvatarName(sender_user.getAvatarName());//发送者头像名称
                        }
                    }

                }
            }
        }
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }
    /**
     * 获取私信对话列表
     * @param page 页码
     * @param friendUserName 对方用户名称
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getPrivateMessageChatList(Integer page, String friendUserName,String fileServerAddress){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        PageForm pageForm = new PageForm();
        pageForm.setPage(page);

        //调用分页算法代码
        PageView<PrivateMessage> pageView = new PageView<PrivateMessage>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10);


        //对话用户
        User chatUser = null;

        //用户Id集合
        Set<Long> userIdList = new HashSet<Long>();
        //用户集合
        Map<Long,User> userMap = new HashMap<Long,User>();

        //未读私信Id集合
        List<String> unreadPrivateMessageIdList = new ArrayList<String>();

        if(friendUserName != null && !friendUserName.trim().isEmpty()){
            chatUser = userCacheManager.query_cache_findUserByUserName(friendUserName.trim());
            if(chatUser != null){
                if(page == null){//页数为空时显示最后一页数据
                    //根据用户Id查询私信对话分页总数
                    Long count = privateMessageRepository.findPrivateMessageChatCountByUserId(accessUser.getUserId(),chatUser.getId(),100);
                    //计算总页数
                    pageView.setTotalrecord(count);
                    pageForm.setPage((int)pageView.getTotalpage());
                    pageView = new PageView<PrivateMessage>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10);

                }
                //当前页
                int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();

                QueryResult<PrivateMessage> qr = privateMessageRepository.findPrivateMessageChatByUserId(accessUser.getUserId(),chatUser.getId(),100,firstIndex,pageView.getMaxresult(),1);
                if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
                    for(PrivateMessage privateMessage : qr.getResultlist()){
                        userIdList.add(privateMessage.getSenderUserId());//发送者用户Id
                        userIdList.add(privateMessage.getReceiverUserId());//接受者用户Id

                        privateMessage.setSendTime(LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(privateMessage.getSendTimeFormat()),
                                ZoneId.systemDefault()
                        ));
                        if(privateMessage.getReadTimeFormat() != null){
                            privateMessage.setReadTime(LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(privateMessage.getReadTimeFormat()),
                                    ZoneId.systemDefault()
                            ));
                        }

                        if(privateMessage.getStatus().equals(10)){
                            unreadPrivateMessageIdList.add(privateMessage.getId());
                        }
                    }
                }

                if(userIdList.size() >0){
                    for(Long userId : userIdList){
                        User user = userCacheManager.query_cache_findUserById(userId);
                        if(user != null){
                            userMap.put(userId, user);
                        }
                    }
                }
                if(userMap.size() >0){
                    if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
                        for(PrivateMessage privateMessage : qr.getResultlist()){
                            User friend_user = userMap.get(privateMessage.getFriendUserId());
                            if(friend_user != null){
                                privateMessage.setFriendUserName(friend_user.getUserName());//私信对方用户名称
                                privateMessage.setFriendAccount(friend_user.getAccount());
                                privateMessage.setFriendNickname(friend_user.getNickname());
                                if(friend_user.getAvatarName() != null && !friend_user.getAvatarName().trim().isEmpty()){
                                    privateMessage.setFriendAvatarPath(fileServerAddress+friend_user.getAvatarPath());//私信对方头像路径
                                    privateMessage.setFriendAvatarName(friend_user.getAvatarName());//私信对方头像名称
                                }
                            }
                            User sender_user = userMap.get(privateMessage.getSenderUserId());
                            if(sender_user != null){
                                privateMessage.setSenderUserName(sender_user.getUserName());//私信发送者用户名称
                                if(sender_user.getCancelAccountTime().equals(-1L)){
                                    privateMessage.setSenderAccount(sender_user.getAccount());
                                    privateMessage.setSenderNickname(sender_user.getNickname());
                                    if(sender_user.getAvatarName() != null && !sender_user.getAvatarName().trim().isEmpty()){
                                        privateMessage.setSenderAvatarPath(fileServerAddress+sender_user.getAvatarPath());//发送者头像路径
                                        privateMessage.setSenderAvatarName(sender_user.getAvatarName());//发送者头像名称
                                    }
                                }
                            }

                        }
                    }



                }
                //将查询结果集传给分页List
                pageView.setQueryResult(qr);

                if(unreadPrivateMessageIdList.size() >0){

                    //将未读私信设置为已读
                    privateMessageRepository.updatePrivateMessageStatus(accessUser.getUserId(), unreadPrivateMessageIdList);
                    //删除私信缓存
                    privateMessageCacheManager.delete_cache_findUnreadPrivateMessageByUserId(accessUser.getUserId());
                }


            }
        }

        if(chatUser != null){
            if(chatUser.getCancelAccountTime().equals(-1L)){
                //仅显示指定的字段
                User viewUser = new User();
                viewUser.setId(chatUser.getId());
                viewUser.setUserName(chatUser.getUserName());//会员用户名
                viewUser.setAccount(chatUser.getAccount());//账号
                viewUser.setNickname(chatUser.getNickname());//呢称
                viewUser.setRegistrationDate(chatUser.getRegistrationDate());//注册日期

                List<UserGrade> userGradeList = userGradeRepository.findAllGrade_cache();//取得用户所有等级
                if(userGradeList != null && userGradeList.size() >0){
                    for(UserGrade userGrade : userGradeList){
                        if(chatUser.getPoint() >= userGrade.getNeedPoint()){
                            viewUser.setGradeId(userGrade.getId());//等级Id
                            viewUser.setGradeName(userGrade.getName());//将等级值设进等级参数里
                            break;
                        }
                    }
                }

                chatUser = viewUser;
            }else{
                chatUser = null;
            }
        }

        returnValue.put("chatUser", chatUser);
        returnValue.put("pageView", pageView);
        return returnValue;
    }
    /**
     * 获取添加私信界面信息
     * @param friendUserName 对方用户名称
     * @return
     */
    public Map<String,Object> getAddPrivateMessageViewModel(String friendUserName){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        Map<String,Object> returnValue = new HashMap<String,Object>();
        FormCaptcha formCaptcha = new FormCaptcha();
        if(accessUser != null){
            boolean captchaKey = captchaComponent.privateMessage_isCaptcha(accessUser.getUserName());//验证码标记
            if(captchaKey){
                formCaptcha.setShowCaptcha(true);
                formCaptcha.setCaptchaKey(UUIDUtil.getUUID32());
            }
        }

        if(friendUserName != null && !friendUserName.trim().isEmpty()){
            User user = userCacheManager.query_cache_findUserByUserName(friendUserName.trim());
            if(user != null){
                returnValue.put("allowSendPrivateMessage",true);//允许发私信
            }else{
                returnValue.put("allowSendPrivateMessage",false);//不允许发私信
            }
        }else{
            returnValue.put("allowSendPrivateMessage",false);//不允许发私信
        }
        returnValue.put("formCaptcha", formCaptcha);
        return returnValue;
    }
    /**
     * 添加私信
     * @param friendUserName 对方用户名称
     * @param messageContent 消息内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     */
    public void addPrivateMessage(String friendUserName, String messageContent,String captchaKey, String captchaValue){
        AccessUser accessUser = AccessUserThreadLocal.get();
        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("privateMessage", "只读模式不允许提交数据"));
        }


        //验证码
        boolean isCaptcha = captchaComponent.privateMessage_isCaptcha(accessUser.getUserName());
        if(isCaptcha) {//如果需要验证码
            //校验验证码
            captchaComponent.validateCaptcha(captchaKey, captchaValue, errors);
        }

        User friendUser = null;


        if(friendUserName != null && !friendUserName.trim().isEmpty()){
            friendUser = userCacheManager.query_cache_findUserByUserName(friendUserName.trim());
            if(friendUser != null){
                if(friendUser.getState() >1){
                    errors.put("privateMessage", "不允许给当前用户发私信");
                }
                if(friendUser.getUserName().equals(accessUser.getUserName())){
                    errors.put("privateMessage", "不允许给自己发私信");
                }
            }else{
                errors.put("privateMessage", "用户不存在");
            }
        }else{
            errors.put("privateMessage", "对方用户名称不能为空");
        }

        if(messageContent != null && !messageContent.trim().isEmpty()){
            if(messageContent.length() >1000){
                errors.put("messageContent", "私信内容不能超过1000个字符");
            }
        }else{
            errors.put("messageContent", "私信内容不能为空");
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors)
                    .addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }
        Long time = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
        String content = WebUtil.urlToHyperlink(HtmlEscape.escape(messageContent.trim()));


        //保存发送者私信
        PrivateMessage sender_privateMessage = new PrivateMessage();
        sender_privateMessage.setId(privateMessageConfig.createPrivateMessageId(accessUser.getUserId()));
        sender_privateMessage.setUserId(accessUser.getUserId());//私信用户Id
        sender_privateMessage.setFriendUserId(friendUser.getId());//私信对方用户Id
        sender_privateMessage.setSenderUserId(accessUser.getUserId());//发送者用户Id
        sender_privateMessage.setReceiverUserId(friendUser.getId());//接受者用户Id
        sender_privateMessage.setMessageContent(content);//消息内容
        sender_privateMessage.setStatus(20);//消息状态 20:已读
        sender_privateMessage.setSendTimeFormat(time);//发送时间格式化
        sender_privateMessage.setReadTimeFormat(time);//阅读时间格式化
        Object sender_privateMessage_object = privateMessageComponent.createPrivateMessageObject(sender_privateMessage);


        //保存接收者私信
        PrivateMessage receiver_privateMessage = new PrivateMessage();
        receiver_privateMessage.setId(privateMessageConfig.createPrivateMessageId(friendUser.getId()));
        receiver_privateMessage.setUserId(friendUser.getId());//私信用户Id
        receiver_privateMessage.setFriendUserId(accessUser.getUserId());//私信对方用户Id
        receiver_privateMessage.setSenderUserId(accessUser.getUserId());//发送者用户Id
        receiver_privateMessage.setReceiverUserId(friendUser.getId());//接受者用户Id
        receiver_privateMessage.setMessageContent(content);//消息内容
        receiver_privateMessage.setStatus(10);//消息状态 10:未读
        receiver_privateMessage.setSendTimeFormat(time);//发送时间格式化
        Object receiver_privateMessage_object = privateMessageComponent.createPrivateMessageObject(receiver_privateMessage);

        privateMessageRepository.savePrivateMessage(sender_privateMessage_object, receiver_privateMessage_object);

        //删除私信缓存
        privateMessageCacheManager.delete_cache_findUnreadPrivateMessageByUserId(accessUser.getUserId());
        privateMessageCacheManager.delete_cache_findUnreadPrivateMessageByUserId(friendUser.getId());


        //统计每分钟原来提交次数
        Integer original = settingCacheManager.getSubmitQuantity("privateMessage", accessUser.getUserName());
        if(original != null){
            settingCacheManager.addSubmitQuantity("privateMessage", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
        }else{
            settingCacheManager.addSubmitQuantity("privateMessage", accessUser.getUserName(),1);//刷新每分钟原来提交次数
        }
    }
    /**
     * 删除私信
     * @param friendUserName 对方用户名称
     */
    public void deletePrivateMessage(String friendUserName){
        AccessUser accessUser = AccessUserThreadLocal.get();
        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("privateMessage", "只读模式不允许提交数据"));
        }


        if(friendUserName == null || friendUserName.trim().isEmpty()){
            throw new BusinessException(Map.of("privateMessage", "对方用户名称不能为空"));
        }

        //对话用户
        User chatUser = userRepository.findUserByUserName(friendUserName.trim());
        if(chatUser == null){
            throw new BusinessException(Map.of("privateMessage", "用户不存在"));
        }

        int i = privateMessageRepository.softDeletePrivateMessage(accessUser.getUserId(),chatUser.getId());
        if(i == 0){
            throw new BusinessException(Map.of("privateMessage", "删除私信失败"));
        }

        //删除私信缓存
        privateMessageCacheManager.delete_cache_findUnreadPrivateMessageByUserId(accessUser.getUserId());
    }
    /**
     * 获取系统通知列表
     * @param page 页码
     * @return
     */
    public PageView<SubscriptionSystemNotify> getSystemNotifyList(int page){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        User user = userCacheManager.query_cache_findUserById(accessUser.getUserId());

        //拉取系统通知
        this.pullSystemNotify(user.getId(),user.getRegistrationDate());


        //调用分页算法代码
        PageView<SubscriptionSystemNotify> pageView = new PageView<SubscriptionSystemNotify>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        //系统通知Id集合
        Set<Long> systemNotifyIdList = new HashSet<Long>();
        //系统通知内容集合
        Map<Long,String> systemNotifyMap = new HashMap<Long,String>();
        //未读订阅系统通知Id集合
        List<String> unreadSystemNotifyIdList = new ArrayList<String>();

        QueryResult<SubscriptionSystemNotify> qr = systemNotifyRepository.findSubscriptionSystemNotifyByUserId(accessUser.getUserId(),100,firstIndex,pageView.getMaxresult());
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(SubscriptionSystemNotify subscriptionSystemNotify : qr.getResultlist()){
                systemNotifyIdList.add(subscriptionSystemNotify.getSystemNotifyId());

                if(subscriptionSystemNotify.getStatus().equals(10)){
                    unreadSystemNotifyIdList.add(subscriptionSystemNotify.getId());
                }
            }
        }


        if(systemNotifyIdList.size() >0){
            for(Long systemNotifyId : systemNotifyIdList){
                SystemNotify systemNotify = systemNotifyCacheManager.query_cache_findById(systemNotifyId);
                if(systemNotify != null){
                    systemNotifyMap.put(systemNotifyId, systemNotify.getContent());
                }
            }
        }
        if(systemNotifyIdList.size() >0){
            if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
                for(SubscriptionSystemNotify subscriptionSystemNotify : qr.getResultlist()){
                    String content = systemNotifyMap.get(subscriptionSystemNotify.getSystemNotifyId());
                    if(content != null){
                        subscriptionSystemNotify.setContent(content);
                    }

                }
            }
        }

        if(unreadSystemNotifyIdList.size() >0){
            //将未读订阅系统通知设置为已读
            systemNotifyRepository.updateSubscriptionSystemNotifyStatus(accessUser.getUserId(), unreadSystemNotifyIdList);

            //删除缓存
            systemNotifyCacheManager.delete_cache_findMinUnreadSystemNotifyIdByUserId(user.getId());
            systemNotifyCacheManager.delete_cache_findMaxReadSystemNotifyIdByUserId(user.getId());
        }


        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }
    /**
     * 删除系统通知
     * @param subscriptionSystemNotifyId 订阅系统通知Id
     */
    public void deleteSystemNotify(String subscriptionSystemNotifyId){
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("systemNotify", "只读模式不允许提交数据"));
        }

        if(subscriptionSystemNotifyId == null || subscriptionSystemNotifyId.trim().isEmpty()){
            throw new BusinessException(Map.of("systemNotify", "订阅系统通知Id不能为空"));
        }

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        int i = systemNotifyRepository.softDeleteSubscriptionSystemNotify(accessUser.getUserId(),subscriptionSystemNotifyId);
        if(i == 0){
            throw new BusinessException(Map.of("systemNotify", "删除系统通知失败"));
        }
        //删除缓存
        systemNotifyCacheManager.delete_cache_findMinUnreadSystemNotifyIdByUserId(accessUser.getUserId());
        systemNotifyCacheManager.delete_cache_findMaxReadSystemNotifyIdByUserId(accessUser.getUserId());

    }
    /**
     * 全部系统通知标记为已读
     */
    public void markAllSystemNotifyAsRead(){
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("systemNotify", "只读模式不允许提交数据"));
        }
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        int i = systemNotifyRepository.updateAllSubscriptionSystemNotifyStatus(accessUser.getUserId());

        //删除缓存
        systemNotifyCacheManager.delete_cache_findMinUnreadSystemNotifyIdByUserId(accessUser.getUserId());
        systemNotifyCacheManager.delete_cache_findMaxReadSystemNotifyIdByUserId(accessUser.getUserId());
    }
    /**
     * 获取未读消息数量
     * @return
     */
    public UnreadMessage getUnreadMessageCount(){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        //拉取关注的用户消息
        followComponent.pullFollow(accessUser.getUserId(),accessUser.getUserName());

        UnreadMessage unreadMessage = new UnreadMessage();

        //查询未读私信数量
        Long unreadPrivateMessageCount = privateMessageCacheManager.query_cache_findUnreadPrivateMessageByUserId(accessUser.getUserId());
        unreadMessage.setPrivateMessageCount(unreadPrivateMessageCount);

        //起始系统通知Id
        Long start_systemNotifyId = 0L;
        //起始系统通知发送时间
        LocalDateTime start_systemNotifySendTime = null;

        //最早的未读系统通知Id
        Long minUnreadSystemNotifyId = systemNotifyCacheManager.query_cache_findMinUnreadSystemNotifyIdByUserId(accessUser.getUserId());
        if(minUnreadSystemNotifyId == null){

            //最大的已读系统通知Id
            Long maxReadSystemNotifyId = systemNotifyCacheManager.query_cache_findMaxReadSystemNotifyIdByUserId(accessUser.getUserId());
            if(maxReadSystemNotifyId != null){
                start_systemNotifyId = maxReadSystemNotifyId;
            }else{
                //获取用户
                User user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
                start_systemNotifySendTime = user.getRegistrationDate();

            }


        }else{
            start_systemNotifyId = minUnreadSystemNotifyId -1L;//-1是为了SQL查询时包含起始系统通知Id
        }
        if(start_systemNotifySendTime == null){
            //查询未读系统通知数量(如果顺序已读Id之间含有未读Id,则计数从最小的未读Id算起)
            Long unreadSystemNotifyCount = systemNotifyCacheManager.query_cache_findSystemNotifyCountBySystemNotifyId(start_systemNotifyId);
            unreadMessage.setSystemNotifyCount(unreadSystemNotifyCount);
        }else{
            Long unreadSystemNotifyCount = systemNotifyCacheManager.query_cache_findSystemNotifyCountBySendTime(start_systemNotifySendTime);
            unreadMessage.setSystemNotifyCount(unreadSystemNotifyCount);
        }

        //未读提醒数量
        Long unreadRemindCount = remindCacheManager.query_cache_findUnreadRemindByUserId(accessUser.getUserId());
        unreadMessage.setRemindCount(unreadRemindCount);

        return unreadMessage;
    }

    /**
     * 获取提醒列表
     * @param page 页码
     * @param request 请求信息
     * @return
     */
    public PageView<Remind> getRemindList(int page, HttpServletRequest request){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();


        //调用分页算法代码
        PageView<Remind> pageView = new PageView<Remind>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        //用户Id集合
        Set<Long> userIdList = new HashSet<Long>();
        //用户集合
        Map<Long,User> userMap = new HashMap<Long,User>();

        //未读提醒Id集合
        List<String> unreadRemindIdList = new ArrayList<String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();


        List<Tag> tagList = tagRepository.findAllTag_cache();
        Map<Long,List<String>> tagRoleNameMap = new HashMap<Long,List<String>>();//标签角色名称 key:标签Id 角色名称集合
        Map<Long,Boolean> userViewPermissionMap = new HashMap<Long,Boolean>();//用户如果对话题项是否有查看权限  key:标签Id value:是否有查看权限
        Map<Long,Long> tagMap = new HashMap<Long,Long>();//话题标签  key:话题Id value:标签Id

        QueryResult<Remind> qr = remindRepository.findRemindByUserId(accessUser.getUserId(),100,firstIndex,pageView.getMaxresult());
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(Remind remind : qr.getResultlist()){
                userIdList.add(remind.getSenderUserId());//发送用户Id

                if(remind .getStatus().equals(10)){
                    unreadRemindIdList.add(remind.getId());
                }

                remind.setSendTime(LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(remind.getSendTimeFormat()),
                        ZoneId.systemDefault()));
                if(remind.getReadTimeFormat() != null){
                    remind.setReadTime(LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(remind.getReadTimeFormat()),
                            ZoneId.systemDefault()));
                }




                if(remind.getTopicId() != null && remind.getTopicId() >0L){
                    Topic topic = topicCacheManager.queryTopicCache(remind.getTopicId());//查询缓存
                    if(topic != null){
                        remind.setTopicTitle(topic.getTitle());
                        if(topic.getStatus().equals(20)){

                            if(remind.getTypeCode().equals(60)//60:别人解锁了我的话题
                                    || remind.getTypeCode().equals(70)//70.别人点赞了我的话题
                                    || remind.getTypeCode().equals(200)//200:别人在话题提到我
                            ){
                                remind.setSummary(topic.getSummary());
                            }

                            if(remind.getTypeCode().equals(90)){//90.我关注的人发表了话题
                                remind.setSummary(topic.getSummary());
                                if(tagList != null && tagList.size() >0){
                                    for(Tag tag :tagList){
                                        if(topic.getTagId().equals(tag.getId())){
                                            tagRoleNameMap.put(topic.getTagId(), null);
                                            userViewPermissionMap.put(topic.getTagId(), null);
                                            tagMap.put(topic.getId(), topic.getTagId());
                                            break;
                                        }

                                    }
                                }
                            }
                        }
                    }

                }
                if(remind.getFriendTopicCommentId() != null && remind.getFriendTopicCommentId() >0L){
                    Comment comment = commentCacheManager.query_cache_findByCommentId(remind.getFriendTopicCommentId());//查询缓存
                    if(comment != null
                            && comment.getStatus().equals(20)
                            && (remind.getTypeCode().equals(10)//10:别人评论了我的话题
                            || remind.getTypeCode().equals(30)//30:别人引用了我的评论
                            || remind.getTypeCode().equals(100)//100.我关注的人发表了评论
                            || remind.getTypeCode().equals(210)//210:别人在评论提到我
                    )){
                        //不含标签内容
                        String text = textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(comment.getContent()));
                        //清除空格&nbsp;
                        String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
                        //摘要
                        if(trimSpace != null && !trimSpace.isEmpty()){
                            if(systemSetting.isAllowFilterWord()){
                                String wordReplace = "";
                                if(systemSetting.getFilterWordReplace() != null){
                                    wordReplace = systemSetting.getFilterWordReplace();
                                }
                                trimSpace = sensitiveWordFilterComponent.filterSensitiveWord(trimSpace, wordReplace);
                            }
                            if(trimSpace.length() >180){
                                remind.setSummary(trimSpace.substring(0, 180)+"..");
                            }else{
                                remind.setSummary(trimSpace+"..");
                            }
                        }
                    }
                }
                if(remind.getFriendTopicReplyId() != null && remind.getFriendTopicReplyId() >0L){
                    Reply reply = commentCacheManager.query_cache_findReplyByReplyId(remind.getFriendTopicReplyId());//查询缓存
                    if(reply != null
                            && reply.getStatus().equals(20)
                            && (remind.getTypeCode().equals(20)//20:别人回复了我的话题
                            || remind.getTypeCode().equals(40)//40:别人回复了我的评论
                            || remind.getTypeCode().equals(50)//50:别人回复了我回复过的评论
                            || remind.getTypeCode().equals(55)//55:别人回复了我的评论回复
                            || remind.getTypeCode().equals(110)//110.我关注的人发表了回复
                            || remind.getTypeCode().equals(220)//220:别人在评论回复提到我
                    )){
                        //清除空格&nbsp;
                        String trimSpace = cms.utils.StringUtil.replaceSpace(reply.getContent()).trim();
                        //摘要
                        if(trimSpace != null && !trimSpace.isEmpty()){
                            if(systemSetting.isAllowFilterWord()){
                                String wordReplace = "";
                                if(systemSetting.getFilterWordReplace() != null){
                                    wordReplace = systemSetting.getFilterWordReplace();
                                }
                                trimSpace = sensitiveWordFilterComponent.filterSensitiveWord(trimSpace, wordReplace);
                            }
                            if(trimSpace.length() >180){
                                remind.setSummary(trimSpace.substring(0, 180)+"..");
                            }else{
                                remind.setSummary(trimSpace+"..");
                            }
                        }
                    }
                }


                if(remind.getQuestionId() != null && remind.getQuestionId() >0L){
                    Question question = questionCacheManager.query_cache_findById(remind.getQuestionId());//查询缓存
                    if(question != null){
                        remind.setQuestionTitle(question.getTitle());
                        if(question.getStatus().equals(20)
                                && (remind.getTypeCode().equals(170) //170:我关注的人提了问题
                                || remind.getTypeCode().equals(230)//230:别人在问题提到我
                        )){
                            remind.setSummary(question.getSummary());
                        }
                    }
                }
                if(remind.getFriendQuestionAnswerId() != null && remind.getFriendQuestionAnswerId() >0L){
                    Answer answer = answerCacheManager.query_cache_findByAnswerId(remind.getFriendQuestionAnswerId());//查询缓存
                    if(answer != null
                            && answer.getStatus().equals(20)
                            && (remind.getTypeCode().equals(120)//120:别人回答了我的问题
                            || remind.getTypeCode().equals(180)//180.我关注的人回答了问题
                            || remind.getTypeCode().equals(240)//240:别人在答案提到我
                    )){
                        //不含标签内容
                        String text = textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(answer.getContent()));
                        //清除空格&nbsp;
                        String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
                        //摘要
                        if(trimSpace != null && !trimSpace.isEmpty()){
                            if(systemSetting.isAllowFilterWord()){
                                String wordReplace = "";
                                if(systemSetting.getFilterWordReplace() != null){
                                    wordReplace = systemSetting.getFilterWordReplace();
                                }
                                trimSpace = sensitiveWordFilterComponent.filterSensitiveWord(trimSpace, wordReplace);
                            }
                            if(trimSpace.length() >180){
                                remind.setSummary(trimSpace.substring(0, 180)+"..");
                            }else{
                                remind.setSummary(trimSpace+"..");
                            }
                        }
                    }
                }
                if(remind.getFriendQuestionReplyId() != null && remind.getFriendQuestionReplyId() >0L){
                    AnswerReply answerReply = answerCacheManager.query_cache_findReplyByReplyId(remind.getFriendQuestionReplyId());//查询缓存
                    if(answerReply != null
                            && answerReply.getStatus().equals(20)
                            && (remind.getTypeCode().equals(130)//130:别人回复了我的问题
                            || remind.getTypeCode().equals(140)//140:别人回复了我的答案
                            || remind.getTypeCode().equals(150)//150:别人回复了我回复过的答案
                            || remind.getTypeCode().equals(160)//160:别人回复了我的答案回复
                            || remind.getTypeCode().equals(190)//190.我关注的人发表了答案回复
                            || remind.getTypeCode().equals(250)//250:别人在答案回复提到我
                    )){
                        //清除空格&nbsp;
                        String trimSpace = cms.utils.StringUtil.replaceSpace(answerReply.getContent()).trim();
                        //摘要
                        if(trimSpace != null && !trimSpace.isEmpty()){
                            if(systemSetting.isAllowFilterWord()){
                                String wordReplace = "";
                                if(systemSetting.getFilterWordReplace() != null){
                                    wordReplace = systemSetting.getFilterWordReplace();
                                }
                                trimSpace = sensitiveWordFilterComponent.filterSensitiveWord(trimSpace, wordReplace);
                            }
                            if(trimSpace.length() >180){
                                remind.setSummary(trimSpace.substring(0, 180)+"..");
                            }else{
                                remind.setSummary(trimSpace+"..");
                            }
                        }
                    }
                }

            }


            if(tagRoleNameMap.size() >0){
                for (Map.Entry<Long, List<String>> entry : tagRoleNameMap.entrySet()) {
                    List<String> roleNameList = userRoleComponent.queryAllowViewTopicRoleName(entry.getKey(),request);
                    entry.setValue(roleNameList);
                }
            }
            if(userViewPermissionMap.size()>0){
                for (Map.Entry<Long,Boolean> entry : userViewPermissionMap.entrySet()) {
                    //是否有当前功能操作权限
                    boolean flag = userRoleComponent.isPermission(ResourceEnum._1001000,entry.getKey());
                    entry.setValue(flag);
                }
            }


            if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
                for(Remind remind : qr.getResultlist()){
                    //用户如果对话题项无查看权限，则不显示摘要
                    for (Map.Entry<Long,Boolean> entry : userViewPermissionMap.entrySet()) {
                        if(entry.getKey().equals(tagMap.get(remind.getTopicId()))
                                && remind.getTypeCode().equals(90)//90.我关注的人发表了话题
                        ){
                            if(entry.getValue() != null && !entry.getValue()){
                                remind.setSummary("");
                            }
                            break;
                        }

                    }
                }
            }


            if(userIdList.size() >0){
                for(Long userId : userIdList){
                    User user = userCacheManager.query_cache_findUserById(userId);
                    if(user != null){
                        userMap.put(userId, user);
                    }
                }
            }
            if(userMap.size() >0){
                if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
                    for(Remind remind : qr.getResultlist()){

                        User sender_user = userMap.get(remind.getSenderUserId());
                        if(sender_user != null && sender_user.getCancelAccountTime().equals(-1L)){
                            remind.setSenderUserName(sender_user.getUserName());//发送者用户名称
                            remind.setSenderAccount(sender_user.getAccount());//发送者账号
                            remind.setSenderNickname(sender_user.getNickname());
                            if(sender_user.getAvatarName() != null && !sender_user.getAvatarName().trim().isEmpty()){
                                remind.setSenderAvatarPath(fileComponent.fileServerAddress(request)+sender_user.getAvatarPath());//发送者头像路径
                                remind.setSenderAvatarName(sender_user.getAvatarName());//发送者头像名称
                            }
                        }

                    }
                }
            }
        }

        if(unreadRemindIdList.size() >0){
            //将未读提醒设置为已读
            remindRepository.updateRemindStatus(accessUser.getUserId(), unreadRemindIdList);
            //删除提醒缓存
            remindCacheManager.delete_cache_findUnreadRemindByUserId(accessUser.getUserId());
        }

        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 全部提醒状态标记为已读
     */
    public void markAllRemindersAsRead(){
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("remind", "只读模式不允许提交数据"));
        }
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        int i = remindRepository.updateAllRemindStatus(accessUser.getUserId());
        //删除提醒缓存
        remindCacheManager.delete_cache_findUnreadRemindByUserId(accessUser.getUserId());
    }

    /**
     * 删除提醒
     * @param remindId 提醒Id
     */
    public void deleteRemind(String remindId){
        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("remind", "只读模式不允许提交数据"));
        }
        AccessUser accessUser = AccessUserThreadLocal.get();
        if(remindId == null || remindId.trim().isEmpty()){
            throw new BusinessException(Map.of("remind", "提醒不存在"));
        }
        int i = remindRepository.softDeleteRemind(accessUser.getUserId(),remindId.trim());
        if(i == 0){
            throw new BusinessException(Map.of("remind", "删除提醒失败"));
        }
        //删除提醒缓存
        remindCacheManager.delete_cache_findUnreadRemindByUserId(accessUser.getUserId());
    }

    /**
     * 拉取系统通知
     * @param userId 用户Id
     * @param registrationDate 用户注册时间
     */
    private void pullSystemNotify(Long userId, LocalDateTime registrationDate){
        List<Object> subscriptionSystemNotifyList = new ArrayList<Object>();
        //查询用户订阅系统通知最新订阅系统Id
        Long maxSystemNotifyId = systemNotifyRepository.findMaxSystemNotifyIdByUserId(userId);
        Map<Long, LocalDateTime> systemNotifyMap = null;
        if(maxSystemNotifyId == null){//如果还没有拉取系统通知
            systemNotifyMap = systemNotifyRepository.findSystemNotifyBySendTime(registrationDate);
        }else{
            systemNotifyMap = systemNotifyRepository.findSystemNotifyById(maxSystemNotifyId);
        }

        if(systemNotifyMap != null && systemNotifyMap.size() >0){
            for(Map.Entry<Long, LocalDateTime> entry : systemNotifyMap.entrySet()){
                //系统通知Id
                Long systemNotifyId = entry.getKey();
                SubscriptionSystemNotify subscriptionSystemNotify = new SubscriptionSystemNotify();
                subscriptionSystemNotify.setId(subscriptionSystemNotifyConfig.createSubscriptionSystemNotifyId(systemNotifyId, userId));
                subscriptionSystemNotify.setSystemNotifyId(systemNotifyId);
                subscriptionSystemNotify.setUserId(userId);
                subscriptionSystemNotify.setStatus(10);
                subscriptionSystemNotify.setSendTime(entry.getValue());

                Object subscriptionSystemNotify_object = subscriptionSystemNotifyComponent.createSubscriptionSystemNotifyObject(subscriptionSystemNotify);
                subscriptionSystemNotifyList.add(subscriptionSystemNotify_object);
            }
        }



        if(subscriptionSystemNotifyList.size() >0){
            try {
                systemNotifyRepository.saveSubscriptionSystemNotify(subscriptionSystemNotifyList);
            } catch (Exception e) {
                //e.printStackTrace();
            }

            //删除缓存
            systemNotifyCacheManager.delete_cache_findMinUnreadSystemNotifyIdByUserId(userId);
            systemNotifyCacheManager.delete_cache_findMaxReadSystemNotifyIdByUserId(userId);
        }
    }
}
