package cms.service.frontend;


import cms.dto.PageView;
import cms.dto.frontendModule.LikeCountDTO;
import cms.dto.frontendModule.UserLikeStatusDTO;
import cms.model.like.Like;

import java.util.Map;

/**
 * 前台点赞服务接口
 */
public interface LikeClientService {
    /**
     * 获取点赞总数
     *
     * @param itemId 项Id
     * @param module 模块 10:话题 20:评论 30:评论回复 40:问题 50:答案 60:答案回复
     * @return
     */
    public LikeCountDTO getTotalLikes(Long itemId, Integer module);

    /**
     * 用户是否已经点赞该项
     *
     * @param itemId 项Id
     * @param module 模块 10:话题 20:评论 30:评论回复 40:问题 50:答案 60:答案回复
     * @return
     */
    public UserLikeStatusDTO hasUserLiked(Long itemId, Integer module);
    /**
     * 添加点赞
     * @param itemId   项目Id 例如：话题Id
     * @param module   模块
     * @return
     */
    public Map<String,Object> addLike(Long itemId, Integer module);
    /**
     * 删除点赞
     * @param likeId  点赞Id 只用本参数或下面两个参数
     * @param module  模块  10:话题  20:评论  30:评论回复  40:问题  50:答案  60:答案回复
     * @param itemId  项Id  话题Id、评论Id、评论回复Id、问题Id、答案Id、答案回复Id
     */
    public Map<String,Object> deleteLike(String likeId, Integer module, Long itemId);
    /**
     * 获取话题点赞列表
     * @param page 页码
     * @param topicId  话题Id
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Like> getTopicLikeList(int page, Long topicId, String fileServerAddress);
    /**
     * 获取点赞列表
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Like> getLikeList(int page, String fileServerAddress);
}
