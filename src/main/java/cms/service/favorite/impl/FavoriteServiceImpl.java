package cms.service.favorite.impl;


import cms.component.JsonComponent;
import cms.component.favorite.FavoriteComponent;
import cms.component.user.UserCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.favorite.Favorites;
import cms.model.question.Question;
import cms.model.topic.Topic;
import cms.model.user.User;
import cms.repository.favorite.FavoriteRepository;
import cms.repository.question.QuestionRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.topic.TopicRepository;
import cms.repository.user.UserRepository;
import cms.service.favorite.FavoriteService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 收藏夹服务
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Resource
    FavoriteRepository favoriteRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    FavoriteComponent favoriteComponent;
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
     * 获取收藏夹列表
     * @param page 页码
     * @param id 用户Id
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String,Object> getFavoriteList(int page,Long id,String userName,String fileServerAddress){
        if(id == null || id <=0){
            throw new BusinessException(Map.of("userId", "用户Id不能为空"));
        }
        if(userName == null || userName.trim().isEmpty()){
            throw new BusinessException(Map.of("userName", "用户名称不能为空"));
        }

        Map<String,Object> returnValue = new HashMap<String,Object>();
        //调用分页算法代码
        PageView<Favorites> pageView = new PageView<Favorites>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        QueryResult<Favorites> qr = favoriteRepository.findFavoriteByUserId(id,userName,firstIndex,pageView.getMaxresult());
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Long> topicIdList = new ArrayList<Long>();
            List<Long> questionIdList = new ArrayList<Long>();
            for(Favorites favorites : qr.getResultlist()){
                if(favorites.getModule().equals(10)){//话题
                    topicIdList.add(favorites.getTopicId());
                }else if(favorites.getModule().equals(20)){//问题
                    questionIdList.add(favorites.getQuestionId());
                }

            }
            if(topicIdList.size() >0){
                List<Topic> topicList = topicRepository.findByIdList(topicIdList);
                if(topicList != null && topicList.size() >0){
                    for(Favorites favorites : qr.getResultlist()){
                        for(Topic topic : topicList){
                            if(favorites.getModule().equals(10) && favorites.getTopicId().equals(topic.getId())){
                                favorites.setTopicTitle(topic.getTitle());
                                break;
                            }
                        }
                    }
                }
            }
            if(questionIdList.size() >0){
                List<Question> questionList = questionRepository.findByIdList(questionIdList);
                if(questionList != null && questionList.size() >0){
                    for(Favorites favorites : qr.getResultlist()){
                        for(Question question : questionList){
                            if(favorites.getModule().equals(20) && favorites.getQuestionId().equals(question.getId())){
                                favorites.setQuestionTitle(question.getTitle());
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
     * 获取话题收藏列表
     * @param page 页码
     * @param topicId 话题Id
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String,Object> getTopicFavorites(int page,Long topicId,String fileServerAddress){
        if(topicId == null || topicId <=0){
            throw new BusinessException(Map.of("topicId", "话题Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Topic topic = topicRepository.findById(topicId);
        if(topic != null){
            returnValue.put("currentTopic", topic);
        }


        //调用分页算法代码
        PageView<Favorites> pageView = new PageView<Favorites>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        QueryResult<Favorites> qr = favoriteRepository.findFavoritePageByTopicId(firstIndex,pageView.getMaxresult(),topicId);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(Favorites favorites : qr.getResultlist()){
                User user = userCacheManager.query_cache_findUserByUserName(favorites.getUserName());
                if(user != null){
                    favorites.setAccount(user.getAccount());
                    favorites.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        favorites.setAvatarPath(fileServerAddress+user.getAvatarPath());
                        favorites.setAvatarName(user.getAvatarName());
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
     * 获取问题收藏列表
     * @param page 页码
     * @param questionId 问题Id
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String,Object> getQuestionFavorites(int page,Long questionId,String fileServerAddress){
        if(questionId == null || questionId <=0){
            throw new BusinessException(Map.of("questionId", "问题Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Question question = questionRepository.findById(questionId);
        if(question != null){
            returnValue.put("currentQuestion", question);
        }


        //调用分页算法代码
        PageView<Favorites> pageView = new PageView<Favorites>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        QueryResult<Favorites> qr = favoriteRepository.findFavoritePageByQuestionId(firstIndex,pageView.getMaxresult(),questionId);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(Favorites favorites : qr.getResultlist()){
                User user = userCacheManager.query_cache_findUserByUserName(favorites.getUserName());
                if(user != null){
                    favorites.setAccount(user.getAccount());
                    favorites.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        favorites.setAvatarPath(fileServerAddress+user.getAvatarPath());
                        favorites.setAvatarName(user.getAvatarName());
                    }
                }
            }
        }
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        returnValue.put("pageView", pageView);
        return returnValue;
    }
}
