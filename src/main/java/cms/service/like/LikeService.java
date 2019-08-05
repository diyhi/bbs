package cms.service.like;


import java.util.List;


import cms.bean.QueryResult;
import cms.bean.like.Like;
import cms.bean.like.TopicLike;
import cms.service.besa.DAO;

/**
 * 点赞
 *
 */
public interface LikeService extends DAO<Like>{
	/**
	 * 根据Id查询点赞
	 * @param likeId 点赞Id
	 * @return
	 */
	public Like findById(String likeId);
	
	/**
	 * 根据用户名称查询点赞分页
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	public QueryResult<Like> findLikeByUserId(Long userId,String userName,int firstIndex, int maxResult);
	
	/**
	 * 保存点赞
	 * @param like 点赞
	 * @param topicLike 话题点赞
	 */
	public void saveLike(Object like,Object topicLike);
	/**
	 * 删除点赞
	 * @param likeId 点赞Id
	 * @param topicLikeId 话题点赞Id
	 */
	public Integer deleteLike(String likeId,String topicLikeId);
	/**
	 * 根据话题Id删除点赞
	 * @param topicId 话题Id
	 */
	public Integer deleteLikeByTopicId(Long topicId);
	
	/**
	 * 根据点赞用户名称删除点赞
	 * @param userNameList 用户名称集合
	 */
	public Integer deleteLikeByUserName(List<String> userNameList);
	
	/**
	 * 根据发布话题的用户名称删除点赞
	 * @param userNameList 发布话题的用户名称集合
	 */
	public Integer deleteLikeByPostUserName(List<String> userNameList);
	/**
	 * 根据Id查询话题点赞
	 * @param topicLikeId 话题点赞Id
	 * @return
	 */
	public TopicLike findTopicLikeById(String topicLikeId);
	
	/**
	 * 根据话题Id查询点赞分页
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param topicId 话题Id
	 * @return
	 */
	public QueryResult<Like> findLikePageByTopicId(int firstIndex, int maxResult,Long topicId);
	
	
	/**
	 * 根据话题Id查询被点赞数量
	 * @param topicId 话题Id
	 * @return
	 */
	public Long findLikeCountByTopicId(Long topicId);
}
