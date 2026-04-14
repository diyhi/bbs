package cms.service.frontend.impl;

import cms.component.favorite.*;
import cms.component.question.QuestionCacheManager;
import cms.component.topic.TopicCacheManager;
import cms.component.user.UserCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.frontendModule.*;
import cms.dto.riskControl.BlockedFeaturesEnum;
import cms.dto.user.AccessUser;
import cms.model.favorite.Favorites;
import cms.model.favorite.QuestionFavorite;
import cms.model.favorite.TopicFavorite;
import cms.model.question.Question;
import cms.model.setting.SystemSetting;
import cms.model.topic.Topic;
import cms.model.user.User;
import cms.repository.favorite.FavoriteRepository;
import cms.repository.setting.SettingRepository;
import cms.service.frontend.FavoriteClientService;
import cms.utils.AccessUserThreadLocal;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * 前台收藏夹服务
 */
@Service
public class FavoriteClientServiceImpl implements FavoriteClientService {
    @Resource
    FavoriteCacheManager favoriteCacheManager;
    @Resource
    TopicFavoriteConfig topicFavoriteConfig;
    @Resource
    QuestionFavoriteConfig questionFavoriteConfig;
    @Resource
    SettingRepository settingRepository;
    @Resource TopicCacheManager topicCacheManager;
    @Resource QuestionCacheManager questionCacheManager;
    @Resource
    FavoriteComponent favoriteComponent;
    @Resource
    FavoritesConfig favoritesConfig;
    @Resource
    FavoriteRepository favoriteRepository;
    @Resource UserCacheManager userCacheManager;

    /**
     * 获取话题收藏总数
     * @param topicId 话题Id
     * @return
     */
    public FavoriteCountDTO getTopicFavoriteCount(Long topicId){
        if(topicId != null && topicId >0L){
            //根据话题Id查询被收藏数量
            Long count = favoriteCacheManager.query_cache_findFavoriteCountByTopicId(topicId);
            if(count !=null){
                return new FavoriteCountDTO(count);
            }

        }
        return new FavoriteCountDTO(0L);
    }

    /**
     * 用户是否已经收藏该话题
     * @param topicId 话题Id
     */
    public FavoritedStatusDTO isTopicFavorited(Long topicId){
        AccessUser accessUser = AccessUserThreadLocal.get();
        if(accessUser != null && topicId != null && topicId >0L){
            //话题收藏Id
            String topicFavoriteId = topicFavoriteConfig.createTopicFavoriteId(topicId, accessUser.getUserId());
            TopicFavorite topicFavorite = favoriteCacheManager.query_cache_findTopicFavoriteById(topicFavoriteId);
            if(topicFavorite != null){
                return new FavoritedStatusDTO(true);
            }
        }
        return new FavoritedStatusDTO(false);
    }

    /**
     * 问题收藏总数
     * @param questionId 问题Id
     */
    public FavoriteCountDTO getQuestionFavoriteCount(Long questionId){
        if(questionId != null && questionId >0L){
            //根据问题Id查询被收藏数量
            Long count = favoriteCacheManager.query_cache_findFavoriteCountByQuestionId(questionId);
            if (count != null) {
                return new FavoriteCountDTO(count);
            }
        }
        return new FavoriteCountDTO(0L);
    }
    /**
     * 用户是否已经收藏该问题
     * @param questionId 问题Id
     */
    public FavoritedStatusDTO isQuestionFavorited(Long questionId){
        AccessUser accessUser = AccessUserThreadLocal.get();
        if(accessUser != null && questionId != null && questionId >0L){
            //问题收藏Id
            String questionFavoriteId = questionFavoriteConfig.createQuestionFavoriteId(questionId, accessUser.getUserId());

            QuestionFavorite questionFavorite = favoriteCacheManager.query_cache_findQuestionFavoriteById(questionFavoriteId);

            if(questionFavorite != null){
                return new FavoritedStatusDTO(true);
            }
        }
        return new FavoritedStatusDTO(false);
    }

    /**
     * 添加收藏
     * @param topicId 话题Id
     * @param questionId 问题Id
     */
    public Map<String,Object> addFavorite(Long topicId, Long questionId){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("favorites", "只读模式不允许提交数据"));
        }

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();


        Topic topic = null;
        Question question = null;
        if(topicId != null && topicId >0L){
            topic = topicCacheManager.queryTopicCache(topicId);//查询缓存
        }

        if(questionId != null && questionId >0L){
            question = questionCacheManager.query_cache_findById(questionId);//查询缓存
        }

        if(topic == null && question == null){
            throw new BusinessException(Map.of("favorites", "待收藏数据不存在"));
        }
        if(topic != null && question != null){
            throw new BusinessException(Map.of("favorites", "不允许同时收藏多项数据"));
        }

        if(topic != null){
            //话题收藏Id
            String topicFavoriteId = topicFavoriteConfig.createTopicFavoriteId(topicId, accessUser.getUserId());

            TopicFavorite topicFavorite = favoriteCacheManager.query_cache_findTopicFavoriteById(topicFavoriteId);

            if(topicFavorite != null){
                errors.put("topicFavorite", "当前话题已经收藏");
            }
        }
        if(question != null){
            //问题收藏Id
            String questionFavoriteId = questionFavoriteConfig.createQuestionFavoriteId(questionId, accessUser.getUserId());

            QuestionFavorite questionFavorite = favoriteCacheManager.query_cache_findQuestionFavoriteById(questionFavoriteId);

            if(questionFavorite != null){
                errors.put("questionFavorite", "当前问题已经收藏");
            }
        }

        if(errors.size() == 0){
            LocalDateTime time = LocalDateTime.now();
            Favorites favorites = new Favorites();
            favorites.setId(favoritesConfig.createFavoriteId(accessUser.getUserId()));
            favorites.setAddtime(time);
            favorites.setUserName(accessUser.getUserName());


            TopicFavorite topicFavorite = null;
            String topicFavoriteId = null;
            if(topic != null){
                favorites.setPostUserName(topic.getUserName());
                favorites.setModule(10);
                favorites.setTopicId(topicId);
                topicFavorite = new TopicFavorite();
                topicFavoriteId = topicFavoriteConfig.createTopicFavoriteId(topicId, accessUser.getUserId());
                topicFavorite.setId(topicFavoriteId);
                topicFavorite.setAddtime(time);
                topicFavorite.setTopicId(topicId);
                topicFavorite.setUserName(accessUser.getUserName());
                topicFavorite.setPostUserName(topic.getUserName());
                topicFavorite.setFavoriteId(favorites.getId());
            }

            QuestionFavorite questionFavorite = null;
            String questionFavoriteId = null;
            if(question != null){
                favorites.setPostUserName(question.getUserName());
                favorites.setModule(20);
                favorites.setQuestionId(questionId);
                questionFavorite = new QuestionFavorite();
                questionFavoriteId = questionFavoriteConfig.createQuestionFavoriteId(questionId, accessUser.getUserId());
                questionFavorite.setId(questionFavoriteId);
                questionFavorite.setAddtime(time);
                questionFavorite.setQuestionId(questionId);
                questionFavorite.setUserName(accessUser.getUserName());
                questionFavorite.setPostUserName(question.getUserName());
                questionFavorite.setFavoriteId(favorites.getId());
            }


            try {

                //删除收藏缓存
                if(topic != null){
                    //保存收藏
                    favoriteRepository.saveFavorite(favoriteComponent.createFavoriteObject(favorites),favoriteComponent.createTopicFavoriteObject(topicFavorite),null);

                    favoriteCacheManager.delete_cache_findTopicFavoriteById(topicFavoriteId);
                    favoriteCacheManager.delete_cache_findFavoriteCountByTopicId(favorites.getTopicId());

                }
                if(question != null){
                    //保存收藏
                    favoriteRepository.saveFavorite(favoriteComponent.createFavoriteObject(favorites),null,favoriteComponent.createQuestionFavoriteObject(questionFavorite));

                    favoriteCacheManager.delete_cache_findQuestionFavoriteById(questionFavoriteId);
                    favoriteCacheManager.delete_cache_findFavoriteCountByQuestionId(favorites.getQuestionId());
                }
            } catch (org.springframework.orm.jpa.JpaSystemException e) {
                errors.put("favorites", "重复收藏");

            }

        }
        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);
        }else{
            returnValue.put("success", true);
        }
        return returnValue;
    }

    /**
     * 删除收藏
     * @param favoriteId 收藏Id  favoriteId、topicId、questionId这3个参数任选一个
     * @param topicId 话题Id  favoriteId、topicId、questionId这3个参数任选一个
     * @param questionId 问题Id  favoriteId、topicId、questionId这3个参数任选一个
     * @return
     */
    public Map<String,Object> deleteFavorite(String favoriteId,Long topicId,Long questionId){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("favorites", "只读模式不允许提交数据"));
        }

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();



        Favorites favorites = null;
        //话题收藏Id
        String topicFavoriteId = null;
        //问题收藏Id
        String questionFavoriteId = null;


        if(topicId != null && topicId >0L){
            topicFavoriteId = topicFavoriteConfig.createTopicFavoriteId(topicId, accessUser.getUserId());

            TopicFavorite topicFavorite = favoriteRepository.findTopicFavoriteById(topicFavoriteId);
            if(topicFavorite != null){
                if(topicFavorite.getFavoriteId() != null && !topicFavorite.getFavoriteId().isEmpty()){
                    favoriteId = topicFavorite.getFavoriteId();
                }else{
                    //兼容6.3及之前版本的数据，6.4及之后的版本topicFavorite和QuestionFavorite增加了favoriteId字段
                    Favorites _favorites = favoriteRepository.findFavoriteByCondition(10, accessUser.getUserId(), accessUser.getUserName(), topicId);
                    if(_favorites != null){
                        favoriteId = _favorites.getId();
                    }
                }
            }

        }



        if(questionId != null && questionId >0L){
            questionFavoriteId = questionFavoriteConfig.createQuestionFavoriteId(questionId, accessUser.getUserId());
            QuestionFavorite questionFavorite = favoriteRepository.findQuestionFavoriteById(questionFavoriteId);
            if(questionFavorite != null){
                if(questionFavorite.getFavoriteId() != null && !questionFavorite.getFavoriteId().isEmpty()){
                    favoriteId = questionFavorite.getFavoriteId();
                }else{
                    //兼容6.3及之前版本的数据，6.4及之后的版本topicFavorite和QuestionFavorite增加了favoriteId字段
                    Favorites _favorites = favoriteRepository.findFavoriteByCondition(20, accessUser.getUserId(), accessUser.getUserName(), questionId);
                    if(_favorites != null){
                        favoriteId = _favorites.getId();
                    }
                }
            }
        }


        if(favoriteId == null || favoriteId.trim().isEmpty()){
            errors.put("favorite", "收藏Id不存在");
        }else{
            favorites  = favoriteRepository.findById(favoriteId.trim());
            if(favorites != null){
                if(favorites.getUserName().equals(accessUser.getUserName())){
                    if(favorites.getModule().equals(10)){//话题模块
                        topicFavoriteId = topicFavoriteConfig.createTopicFavoriteId(favorites.getTopicId(), accessUser.getUserId());
                    }else if(favorites.getModule().equals(20)){//问题模块
                        questionFavoriteId = questionFavoriteConfig.createQuestionFavoriteId(favorites.getQuestionId(), accessUser.getUserId());
                    }

                }else{
                    errors.put("favorite", "本收藏不属于当前用户");
                }
            }else{
                errors.put("favorite", "收藏不存在");
            }
        }

        if(errors.size() == 0){
            if(favorites.getModule().equals(10)){//话题模块
                int i = favoriteRepository.deleteFavorite(favoriteId.trim(),topicFavoriteId, null);
                if(i == 0){
                    errors.put("favorite", "删除收藏失败");
                }
                //删除收藏缓存
                favoriteCacheManager.delete_cache_findTopicFavoriteById(topicFavoriteId);
                favoriteCacheManager.delete_cache_findFavoriteCountByTopicId(favorites.getTopicId());
            }else if(favorites.getModule().equals(20)){//问题模块
                int i = favoriteRepository.deleteFavorite(favoriteId.trim(),null,questionFavoriteId);
                if(i == 0){
                    errors.put("favorite", "删除收藏失败");
                }
                //删除收藏缓存
                favoriteCacheManager.delete_cache_findQuestionFavoriteById(questionFavoriteId);
                favoriteCacheManager.delete_cache_findFavoriteCountByQuestionId(favorites.getQuestionId());
            }

        }


        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);
        }else{
            returnValue.put("success", true);

        }
        return returnValue;
    }

    /**
     * 获取话题收藏列表
     * @param page 页码
     * @param topicId 话题Id
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Favorites> getTopicFavoriteList(int page, Long topicId, String fileServerAddress){
        //调用分页算法代码
        PageView<Favorites> pageView = new PageView<Favorites>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        if(topicId != null && topicId > 0L){
            Topic topicInfo = topicCacheManager.queryTopicCache(topicId);//查询缓存
            if(topicInfo != null && topicInfo.getUserName().equals(accessUser.getUserName())){
                //当前页
                int firstIndex = (page-1)*pageView.getMaxresult();


                QueryResult<Favorites> qr = favoriteRepository.findFavoritePageByTopicId(firstIndex,pageView.getMaxresult(),topicId);
                if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
                    for(Favorites favorites : qr.getResultlist()){
                        Topic topic = topicCacheManager.queryTopicCache(favorites.getTopicId());//查询缓存
                        if(topic != null){
                            favorites.setTopicTitle(topic.getTitle());
                        }
                        User user = userCacheManager.query_cache_findUserByUserName(favorites.getUserName());
                        if(user != null && user.getCancelAccountTime().equals(-1L)){
                            favorites.setAccount(user.getAccount());
                            favorites.setNickname(user.getNickname());
                            if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                                favorites.setAvatarPath(fileServerAddress+user.getAvatarPath());//头像路径
                                favorites.setAvatarName(user.getAvatarName());//头像名称
                            }
                        }else{
                            favorites.setUserName(null);
                        }
                    }
                }
                //将查询结果集传给分页List
                pageView.setQueryResult(qr);
            }
        }
        return pageView;
    }

    /**
     * 获取问题收藏列表
     * @param page 页码
     * @param questionId 问题Id
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Favorites> getQuestionFavoriteList(int page,Long questionId, String fileServerAddress){
        //调用分页算法代码
        PageView<Favorites> pageView = new PageView<Favorites>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        if(questionId != null && questionId > 0L){
            Question questionInfo = questionCacheManager.query_cache_findById(questionId);//查询缓存
            if(questionInfo != null && questionInfo.getUserName().equals(accessUser.getUserName())){
                //当前页
                int firstIndex = (page-1)*pageView.getMaxresult();


                QueryResult<Favorites> qr = favoriteRepository.findFavoritePageByQuestionId(firstIndex,pageView.getMaxresult(),questionId);
                if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
                    for(Favorites favorites : qr.getResultlist()){
                        Question question = questionCacheManager.query_cache_findById(favorites.getQuestionId());//查询缓存
                        if(question != null){
                            favorites.setQuestionTitle(question.getTitle());
                        }
                        User user = userCacheManager.query_cache_findUserByUserName(favorites.getUserName());
                        if(user != null && user.getCancelAccountTime().equals(-1L)){
                            favorites.setAccount(user.getAccount());
                            favorites.setNickname(user.getNickname());
                            if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                                favorites.setAvatarPath(fileServerAddress+user.getAvatarPath());//头像路径
                                favorites.setAvatarName(user.getAvatarName());//头像名称
                            }
                        }else{
                            favorites.setUserName(null);
                        }
                    }
                }
                //将查询结果集传给分页List
                pageView.setQueryResult(qr);
            }
        }
        return pageView;
    }

    /**
     * 获取收藏列表
     * @param page 页码
     * @return
     */
    public PageView<Favorites> getFavoriteList(int page){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();


        //调用分页算法代码
        PageView<Favorites> pageView = new PageView<Favorites>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        QueryResult<Favorites> qr = favoriteRepository.findFavoriteByUserId(accessUser.getUserId(),accessUser.getUserName(),firstIndex,pageView.getMaxresult());
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(Favorites favorites : qr.getResultlist()){
                if(favorites.getModule().equals(10)){//话题
                    Topic topic = topicCacheManager.queryTopicCache(favorites.getTopicId());//查询缓存
                    if(topic != null){
                        favorites.setTopicTitle(topic.getTitle());
                    }
                }else if(favorites.getModule().equals(20)){//问题
                    Question question = questionCacheManager.query_cache_findById(favorites.getQuestionId());//查询缓存
                    if(question != null){
                        favorites.setQuestionTitle(question.getTitle());
                    }
                }

            }
        }
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }
}
