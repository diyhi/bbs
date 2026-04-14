package cms.service.message.impl;


import cms.component.JsonComponent;
import cms.component.message.RemindCacheManager;
import cms.component.question.QuestionCacheManager;
import cms.component.topic.TopicCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.message.Remind;
import cms.model.question.Question;
import cms.model.topic.Topic;
import cms.model.user.User;
import cms.repository.message.RemindRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.service.message.RemindService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

/**
 * 提醒服务
 */
@Service
public class RemindServiceImpl implements RemindService {

    @Resource
    RemindRepository remindRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource RemindCacheManager remindCacheManager;
    @Resource
    UserRepository userRepository;
    @Resource QuestionCacheManager questionCacheManager;
    @Resource TopicCacheManager topicCacheManager;

    /**
     * 获取提醒列表
     * @param page 页码
     * @param id 用户Id
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getRemindList(int page,Long id,String fileServerAddress){
        if(id == null || id <=0){
            throw new BusinessException(Map.of("id", "用户Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();

        //调用分页算法代码
        PageView<Remind> pageView = new PageView<Remind>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);


        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        //用户Id集合
        Set<Long> userIdList = new HashSet<Long>();
        //用户集合
        Map<Long,User> userMap = new HashMap<Long, User>();

        QueryResult<Remind> qr = remindRepository.findRemindByUserId(id,null,firstIndex,pageView.getMaxresult());
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(Remind remind : qr.getResultlist()){
                userIdList.add(remind.getSenderUserId());

                remind.setSendTime(Instant.ofEpochMilli(remind.getSendTimeFormat())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());
                if(remind.getReadTimeFormat() != null){
                    remind.setReadTime(Instant.ofEpochMilli(remind.getReadTimeFormat())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime());
                }

                if(remind.getTopicId() != null && remind.getTopicId() >0L){
                    Topic topic = topicCacheManager.queryTopicCache(remind.getTopicId());//查询缓存
                    if(topic != null){
                        remind.setTopicTitle(topic.getTitle());
                    }

                }
                if(remind.getQuestionId() != null && remind.getQuestionId() >0L){
                    Question question = questionCacheManager.query_cache_findById(remind.getQuestionId());//查询缓存
                    if(question != null){
                        remind.setQuestionTitle(question.getTitle());
                    }

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
                for(Remind remind : qr.getResultlist()){
                    User sender_user = userMap.get(remind.getSenderUserId());
                    if(sender_user != null){
                        remind.setSenderUserName(sender_user.getUserName());
                        remind.setSenderAccount(sender_user.getAccount());
                        remind.setSenderNickname(sender_user.getNickname());
                        if(sender_user.getAvatarName() != null && !sender_user.getAvatarName().trim().isEmpty()){
                            remind.setSenderAvatarPath(fileServerAddress+sender_user.getAvatarPath());//发送者头像路径
                            remind.setSenderAvatarName(sender_user.getAvatarName());//发送者头像名称
                        }
                    }

                }
            }



        }
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        User user = userRepository.findUserById(id);
        if(user != null){
            User currentUser = new User();
            currentUser.setId(user.getId());
            currentUser.setAccount(user.getAccount());
            currentUser.setNickname(user.getNickname());
            if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                currentUser.setAvatarPath(fileServerAddress+user.getAvatarPath());
                currentUser.setAvatarName(user.getAvatarName());
            }
            returnValue.put("currentUser", currentUser);
        }

        returnValue.put("pageView", pageView);
        return returnValue;
    }

    /**
     * 还原提醒
     * @param userId 用户Id
     * @param remindId 提醒Id
     */
    public void reductionRemind(Long userId,String remindId){
        if(userId == null || userId <=0){
            throw new BusinessException(Map.of("userId", "用户Id不能为空"));
        }
        if(remindId == null || remindId.trim().isEmpty()){
            throw new BusinessException(Map.of("remindId", "提醒Id不能为空"));
        }
        int i = remindRepository.reductionRemind(remindId);

        //删除提醒缓存
        remindCacheManager.delete_cache_findUnreadRemindByUserId(userId);


    }
}
