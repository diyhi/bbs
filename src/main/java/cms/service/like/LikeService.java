package cms.service.like;



import java.util.Map;

/**
 * 点赞服务
 */
public interface LikeService {

    /**
     * 获取点赞列表
     * @param page 页码
     * @param id 用户Id
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String,Object> getLikeList(int page,Long id,String userName,String fileServerAddress);

    /**
     * 获取话题点赞列表
     * @param page 页码
     * @param itemId 项目Id 例如：话题Id
     * @param topicId 话题Id
     * @param likeModule 点赞模块 10:话题 20:评论 30:回复
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String,Object> getTopicLikes(int page,Long itemId,Long topicId,Integer likeModule,String fileServerAddress);
    /**
     * 获取问题点赞列表
     * @param page 页码
     * @param itemId 项目Id 例如：问题Id
     * @param questionId 问题Id
     * @param likeModule 点赞模块 40:问题 50:评论 60:回复
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String,Object> getQuestionLikes(int page,Long itemId,Long questionId,Integer likeModule,String fileServerAddress);
}
