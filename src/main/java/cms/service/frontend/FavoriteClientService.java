package cms.service.frontend;

import cms.dto.PageView;
import cms.dto.frontendModule.FavoriteCountDTO;
import cms.dto.frontendModule.FavoritedStatusDTO;
import cms.model.favorite.Favorites;

import java.util.Map;

/**
 * 前台收藏夹服务接口
 */
public interface FavoriteClientService {

    /**
     * 获取话题收藏总数
     * @param topicId 话题Id
     * @return
     */
    public FavoriteCountDTO getTopicFavoriteCount(Long topicId);

    /**
     * 用户是否已经收藏该话题
     * @param topicId 话题Id
     */
    public FavoritedStatusDTO isTopicFavorited(Long topicId);
    /**
     * 问题收藏总数
     * @param questionId 问题Id
     */
    public FavoriteCountDTO getQuestionFavoriteCount(Long questionId);
    /**
     * 用户是否已经收藏该问题
     * @param questionId 问题Id
     */
    public FavoritedStatusDTO isQuestionFavorited(Long questionId);

    /**
     * 添加收藏
     * @param topicId 话题Id
     * @param questionId 问题Id
     */
    public Map<String,Object> addFavorite(Long topicId,Long questionId);

    /**
     * 删除收藏
     * @param favoriteId 收藏Id  favoriteId、topicId、questionId这3个参数任选一个
     * @param topicId 话题Id  favoriteId、topicId、questionId这3个参数任选一个
     * @param questionId 问题Id  favoriteId、topicId、questionId这3个参数任选一个
     * @return
     */
    public Map<String,Object> deleteFavorite(String favoriteId,Long topicId,Long questionId);

    /**
     * 获取话题收藏列表
     * @param page 页码
     * @param topicId 话题Id
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Favorites> getTopicFavoriteList(int page, Long topicId, String fileServerAddress);
    /**
     * 获取问题收藏列表
     * @param page 页码
     * @param questionId 问题Id
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Favorites> getQuestionFavoriteList(int page,Long questionId, String fileServerAddress);
    /**
     * 获取收藏列表
     * @param page 页码
     * @return
     */
    public PageView<Favorites> getFavoriteList(int page);

}
