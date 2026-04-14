package cms.service.favorite;



import java.util.Map;

/**
 * 收藏夹服务
 */
public interface FavoriteService {

    /**
     * 获取收藏夹列表
     * @param page 页码
     * @param id 用户Id
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String,Object> getFavoriteList(int page,Long id,String userName,String fileServerAddress);
    /**
     * 获取话题收藏列表
     * @param page 页码
     * @param topicId 话题Id
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String,Object> getTopicFavorites(int page,Long topicId,String fileServerAddress);

    /**
     * 获取问题收藏列表
     * @param page 页码
     * @param questionId 问题Id
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String,Object> getQuestionFavorites(int page,Long questionId,String fileServerAddress);

}
