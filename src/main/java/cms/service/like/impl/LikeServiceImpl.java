package cms.service.like.impl;


import cms.component.JsonComponent;
import cms.component.like.LikeComponent;
import cms.component.user.UserCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.like.Like;
import cms.model.question.Question;
import cms.model.topic.Topic;
import cms.model.user.User;
import cms.repository.like.LikeRepository;
import cms.repository.question.QuestionRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.topic.TopicRepository;
import cms.repository.user.UserRepository;
import cms.service.like.LikeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 点赞服务
 */
@Service
public class LikeServiceImpl implements LikeService {

    @Resource
    LikeRepository likeRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    LikeComponent likeComponent;
    @Resource
    JsonComponent jsonComponent;
    @Resource UserCacheManager userCacheManager;
    @Resource
    TopicRepository topicRepository;
    @Resource
    QuestionRepository questionRepository;
    @Resource
    UserRepository userRepository;

    /**
     * 获取点赞列表
     * @param page 页码
     * @param id 用户Id
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String,Object> getLikeList(int page,Long id,String userName,String fileServerAddress){
        if(id == null || id <=0){
            throw new BusinessException(Map.of("userId", "用户Id不能为空"));
        }
        if(userName == null || userName.trim().isEmpty()){
            throw new BusinessException(Map.of("userName", "用户名称不能为空"));
        }

        Map<String,Object> returnValue = new HashMap<String,Object>();
        //调用分页算法代码
        PageView<Like> pageView = new PageView<Like>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        QueryResult<Like> qr = likeRepository.findLikeByUserId(id,userName,firstIndex,pageView.getMaxresult());
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Long> topicIdList = new ArrayList<Long>();
            List<Long> questionIdList = new ArrayList<Long>();
            for(Like like : qr.getResultlist()){
                if(like.getModule().equals(10) || like.getModule().equals(20) || like.getModule().equals(30)){
                    topicIdList.add(like.getTopicId());
                }
                if(like.getModule().equals(40) || like.getModule().equals(50) || like.getModule().equals(60)){
                    questionIdList.add(like.getQuestionId());
                }

            }
            if(topicIdList.size() >0){
                List<Topic> topicList = topicRepository.findByIdList(topicIdList);
                if(topicList != null && topicList.size() >0){
                    for(Like like : qr.getResultlist()){
                        for(Topic topic : topicList){
                            if((like.getModule().equals(10) || like.getModule().equals(20) || like.getModule().equals(30)) && like.getTopicId().equals(topic.getId())){
                                like.setTopicTitle(topic.getTitle());
                                break;
                            }
                        }
                    }
                }
            }
            if(questionIdList.size() >0){
                List<Question> questionList = questionRepository.findByIdList(questionIdList);
                if(questionList != null && questionList.size() >0){
                    for(Like like : qr.getResultlist()){
                        for(Question question : questionList){
                            if((like.getModule().equals(40) || like.getModule().equals(50) || like.getModule().equals(60)) && like.getQuestionId().equals(question.getId())){
                                like.setQuestionTitle(question.getTitle());
                                break;
                            }
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
     * 获取话题点赞列表
     * @param page 页码
     * @param itemId 项目Id 例如：话题Id
     * @param topicId 话题Id
     * @param likeModule 点赞模块 10:话题 20:评论 30:回复
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String,Object> getTopicLikes(int page,Long itemId,Long topicId,Integer likeModule,String fileServerAddress){
        if(itemId == null || itemId <=0){
            throw new BusinessException(Map.of("itemId", "项目Id不能为空"));
        }
        if(topicId == null || topicId <=0){
            throw new BusinessException(Map.of("topicId", "话题Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();

        Topic topic = topicRepository.findById(topicId);
        if(topic != null){
            returnValue.put("currentTopic", topic);
        }

        //调用分页算法代码
        PageView<Like> pageView = new PageView<Like>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();
        QueryResult<Like> qr = null;
        if(likeModule.equals(10)){
            qr = likeRepository.findLikePageByTopicId(firstIndex,pageView.getMaxresult(),itemId);
            //将查询结果集传给分页List
            pageView.setQueryResult(qr);
        }else if(likeModule.equals(20)){
            qr = likeRepository.findLikePageByCommentId(firstIndex,pageView.getMaxresult(),itemId);
            //将查询结果集传给分页List
            pageView.setQueryResult(qr);
        }else if(likeModule.equals(30)){
            qr = likeRepository.findLikePageByCommentReplyId(firstIndex,pageView.getMaxresult(),itemId);
            //将查询结果集传给分页List
            pageView.setQueryResult(qr);
        }



        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(Like like : qr.getResultlist()){
                User user = userCacheManager.query_cache_findUserByUserName(like.getUserName());
                if(user != null){
                    like.setAccount(user.getAccount());
                    like.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        like.setAvatarPath(fileServerAddress+user.getAvatarPath());
                        like.setAvatarName(user.getAvatarName());
                    }
                }
            }
        }

        returnValue.put("pageView", pageView);

        return returnValue;
    }

    /**
     * 获取问题点赞列表
     * @param page 页码
     * @param itemId 项目Id 例如：问题Id
     * @param questionId 问题Id
     * @param likeModule 点赞模块 40:问题 50:评论 60:回复
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String,Object> getQuestionLikes(int page,Long itemId,Long questionId,Integer likeModule,String fileServerAddress){
        if(itemId == null || itemId <=0){
            throw new BusinessException(Map.of("itemId", "项目Id不能为空"));
        }
        if(questionId == null || questionId <=0){
            throw new BusinessException(Map.of("questionId", "问题Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Question question = questionRepository.findById(questionId);
        if(question != null){
            returnValue.put("currentQuestion", question);
        }

        //调用分页算法代码
        PageView<Like> pageView = new PageView<Like>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();
        QueryResult<Like> qr = null;
        if(likeModule.equals(40)){
            qr = likeRepository.findLikePageByQuestionId(firstIndex,pageView.getMaxresult(),itemId);
            //将查询结果集传给分页List
            pageView.setQueryResult(qr);
        }else if(likeModule.equals(50)){
            qr = likeRepository.findLikePageByAnswerId(firstIndex,pageView.getMaxresult(),itemId);
            //将查询结果集传给分页List
            pageView.setQueryResult(qr);
        }else if(likeModule.equals(60)){
            qr = likeRepository.findLikePageByAnswerReplyId(firstIndex,pageView.getMaxresult(),itemId);
            //将查询结果集传给分页List
            pageView.setQueryResult(qr);
        }



        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(Like like : qr.getResultlist()){
                User user = userCacheManager.query_cache_findUserByUserName(like.getUserName());
                if(user != null){
                    like.setAccount(user.getAccount());
                    like.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        like.setAvatarPath(fileServerAddress+user.getAvatarPath());
                        like.setAvatarName(user.getAvatarName());
                    }
                }
            }
        }

        returnValue.put("pageView", pageView);
        return returnValue;
    }
}
