package cms.component.favorite;

import cms.model.favorite.QuestionFavorite;
import cms.model.favorite.TopicFavorite;
import cms.repository.favorite.FavoriteRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 收藏缓存管理
 */
@Component("favoriteCacheManager")
public class FavoriteCacheManager {
    @Resource FavoriteRepository favoriteRepository;


    /**
     * 查询缓存 查询话题收藏
     * @param topicFavoriteId 话题收藏Id
     * @return
     */
    @Cacheable(value="favoriteCacheManager_cache_findTopicFavoriteById",key="#topicFavoriteId")
    public TopicFavorite query_cache_findTopicFavoriteById(String topicFavoriteId){
        return favoriteRepository.findTopicFavoriteById(topicFavoriteId);
    }
    /**
     * 删除缓存 话题收藏
     * @param topicFavoriteId 话题收藏Id
     * @return
     */
    @CacheEvict(value="favoriteCacheManager_cache_findTopicFavoriteById",key="#topicFavoriteId")
    public void delete_cache_findTopicFavoriteById(String topicFavoriteId){
    }


    /**
     * 查询缓存 根据话题Id查询被收藏数量
     * @param topicId 话题Id
     * @return
     */
    @Cacheable(value="favoriteCacheManager_cache_findFavoriteCountByTopicId",key="#topicId")
    public Long query_cache_findFavoriteCountByTopicId(Long topicId){
        return favoriteRepository.findFavoriteCountByTopicId(topicId);
    }
    /**
     * 删除缓存 根据话题Id查询被收藏数量
     * @param topicId 话题Id
     * @return
     */
    @CacheEvict(value="favoriteCacheManager_cache_findFavoriteCountByTopicId",key="#topicId")
    public void delete_cache_findFavoriteCountByTopicId(Long topicId){
    }


    /**
     * 查询缓存 查询问题收藏
     * @param questionFavoriteId 问题收藏Id
     * @return
     */
    @Cacheable(value="favoriteCacheManager_cache_findQuestionFavoriteById",key="#questionFavoriteId")
    public QuestionFavorite query_cache_findQuestionFavoriteById(String questionFavoriteId){
        return favoriteRepository.findQuestionFavoriteById(questionFavoriteId);
    }
    /**
     * 删除缓存 问题收藏
     * @param questionFavoriteId 问题收藏Id
     * @return
     */
    @CacheEvict(value="favoriteCacheManager_cache_findQuestionFavoriteById",key="#questionFavoriteId")
    public void delete_cache_findQuestionFavoriteById(String questionFavoriteId){
    }


    /**
     * 查询缓存 根据问题Id查询被收藏数量
     * @param questionId 问题Id
     * @return
     */
    @Cacheable(value="favoriteCacheManager_cache_findFavoriteCountByQuestionId",key="#questionId")
    public Long query_cache_findFavoriteCountByQuestionId(Long questionId){
        return favoriteRepository.findFavoriteCountByQuestionId(questionId);
    }
    /**
     * 删除缓存 根据问题Id查询被收藏数量
     * @param questionId 问题Id
     * @return
     */
    @CacheEvict(value="favoriteCacheManager_cache_findFavoriteCountByQuestionId",key="#questionId")
    public void delete_cache_findFavoriteCountByQuestionId(Long questionId){
    }

}
