package cms.service.message.impl;


import cms.component.JsonComponent;
import cms.component.message.PrivateMessageCacheManager;
import cms.config.BusinessException;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.message.PrivateMessage;
import cms.model.user.User;
import cms.repository.message.PrivateMessageRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.service.message.PrivateMessageService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

/**
 * 私信服务
 */
@Service
public class PrivateMessageServiceImpl implements PrivateMessageService {

    @Resource
    PrivateMessageRepository privateMessageRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    UserRepository userRepository;
    @Resource PrivateMessageCacheManager privateMessageCacheManager;


    /**
     * 获取私信列表
     * @param page 页码
     * @param id 用户Id
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getPrivateMessageList(int page,Long id,String fileServerAddress){
        if(id == null || id <=0){
            throw new BusinessException(Map.of("id", "用户Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        //调用分页算法代码
        PageView<PrivateMessage> pageView = new PageView<PrivateMessage>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);


        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        //用户Id集合
        Set<Long> userIdList = new HashSet<Long>();
        //用户集合
        Map<Long, User> userMap = new HashMap<Long,User>();

        QueryResult<PrivateMessage> qr = privateMessageRepository.findPrivateMessageByUserId(id,firstIndex,pageView.getMaxresult());
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(PrivateMessage privateMessage : qr.getResultlist()){
                userIdList.add(privateMessage.getSenderUserId());//发送者用户Id
                userIdList.add(privateMessage.getReceiverUserId());//接受者用户Id

                privateMessage.setSendTime(Instant.ofEpochMilli(privateMessage.getSendTimeFormat())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());
                if(privateMessage.getReadTimeFormat() != null){
                    privateMessage.setReadTime(Instant.ofEpochMilli(privateMessage.getReadTimeFormat())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime());
                }
            }
        }

        if(userIdList.size() >0){
            for(Long userId : userIdList){
                User user = userRepository.findUserById(userId);
                if(user != null){
                    userMap.put(userId, user);
                }
            }
        }
        if(userMap.size() >0){
            if(qr.getResultlist() != null && qr.getResultlist().size() >0){
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
        User user = userRepository.findUserById(id);
        if(user != null){
            if(user.getAvatarPath() != null && !"".equals(user.getAvatarPath())){
                user.setAvatarPath(fileServerAddress+user.getAvatarPath());
            }
            returnValue.put("currentUser", user);
        }

        returnValue.put("pageView", pageView);
        return returnValue;
    }


    /**
     * 获取私信列表
     * @param page 页码
     * @param id 用户Id
     * @param friendUserId 对方用户Id
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getPrivateMessageChatList(Integer page,Long id,Long friendUserId,String fileServerAddress){
        if(id == null || id <=0){
            throw new BusinessException(Map.of("id", "用户Id不能为空"));
        }
        if(friendUserId == null || friendUserId <=0){
            throw new BusinessException(Map.of("friendUserId", "对方用户Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        PageForm pageForm = new PageForm();
        pageForm.setPage(page);

        if(page == null){
            Long count = privateMessageRepository.findPrivateMessageChatCountByUserId(id,friendUserId,null);
            if(count != null && count >0L){
                Integer _page = Integer.parseInt(String.valueOf(count))/settingRepository.findSystemSetting_cache().getBackstagePageNumber();
                if(Integer.parseInt(String.valueOf(count))%settingRepository.findSystemSetting_cache().getBackstagePageNumber() >0){//余数大于0要加1
                    _page = _page+1;
                }
                pageForm.setPage(_page);
            }
        }

        //调用分页算法代码
        PageView<PrivateMessage> pageView = new PageView<PrivateMessage>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
        //当前页
        int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();


        //用户Id集合
        Set<Long> userIdList = new HashSet<Long>();
        //用户集合
        Map<Long,User> userMap = new HashMap<Long,User>();


        //对话用户
        User chatUser = userRepository.findUserById(friendUserId);
        if(chatUser != null){
            returnValue.put("chatUser", chatUser);
        }


        QueryResult<PrivateMessage> qr = privateMessageRepository.findPrivateMessageChatByUserId(id,friendUserId,null,firstIndex,pageView.getMaxresult(),1);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(PrivateMessage privateMessage : qr.getResultlist()){
                userIdList.add(privateMessage.getSenderUserId());//发送者用户Id
                userIdList.add(privateMessage.getReceiverUserId());//接受者用户Id
                privateMessage.setSendTime(Instant.ofEpochMilli(privateMessage.getSendTimeFormat())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());
                if(privateMessage.getReadTimeFormat() != null){
                    privateMessage.setReadTime(Instant.ofEpochMilli(privateMessage.getReadTimeFormat())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime());
                }
            }
        }

        if(userIdList.size() >0){
            for(Long _userId : userIdList){
                User user = userRepository.findUserById(_userId);
                if(user != null){
                    userMap.put(_userId, user);
                }
            }
        }
        if(userMap.size() >0){
            if(qr.getResultlist() != null && qr.getResultlist().size() >0){
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
        returnValue.put("pageView", pageView);
        return returnValue;
    }

    /**
     * 删除私信对话
     * @param userId 用户Id
     * @param friendUserId 对方用户Id
     */
    public void deletePrivateMessageChat(Long userId,Long friendUserId){
        if(userId == null || userId <=0){
            throw new BusinessException(Map.of("userId", "用户Id不能为空"));
        }
        if(friendUserId == null || friendUserId <=0){
            throw new BusinessException(Map.of("friendUserId", "对方用户Id不能为空"));
        }
        int i = privateMessageRepository.deletePrivateMessage(userId, friendUserId);

        //删除私信缓存
        privateMessageCacheManager.delete_cache_findUnreadPrivateMessageByUserId(userId);
        privateMessageCacheManager.delete_cache_findUnreadPrivateMessageByUserId(friendUserId);
    }

    /**
     * 还原私信
     * @param userId 用户Id
     * @param privateMessageId 私信Id
     */
    public void reductionPrivateMessage(Long userId,String privateMessageId){
        if(userId == null || userId <=0){
            throw new BusinessException(Map.of("userId", "用户Id不能为空"));
        }
        if(privateMessageId == null || privateMessageId.trim().isEmpty()){
            throw new BusinessException(Map.of("privateMessageId", "私信Id不能为空"));
        }
        int i = privateMessageRepository.reductionPrivateMessage(privateMessageId);

        //删除私信缓存
        privateMessageCacheManager.delete_cache_findUnreadPrivateMessageByUserId(userId);

    }

}
