package cms.service.favorite;


import java.util.List;

import cms.bean.QueryResult;
import cms.bean.favorite.Favorites;
import cms.bean.favorite.TopicFavorite;
import cms.service.besa.DAO;

/**
 * 收藏夹
 *
 */
public interface FavoriteService extends DAO<Favorites>{
	/**
	 * 根据Id查询收藏夹
	 * @param favoriteId 收藏夹Id
	 * @return
	 */
	public Favorites findById(String favoriteId);
	/**
	 * 根据用户名称查询收藏夹分页
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	public QueryResult<Favorites> findFavoriteByUserId(Long userId,String userName,int firstIndex, int maxResult);
	/**
	 * 保存收藏夹
	 * @param favorites 收藏夹
	 * @param topicFavorite 话题收藏
	 */
	public void saveFavorite(Object favorites,Object topicFavorite);
	
	/**
	 * 删除收藏
	 * @param favoriteId 收藏夹Id
	 * @param topicFavoriteId 话题收藏Id
	 */
	public Integer deleteFavorite(String favoriteId,String topicFavoriteId);
	/**
	 * 根据话题Id删除收藏
	 * @param topicId 话题Id
	 */
	public Integer deleteFavoriteByTopicId(Long topicId);
	/**
	 * 根据收藏夹用户名称删除收藏
	 * @param userNameList 用户名称集合
	 */
	public Integer deleteFavoriteByUserName(List<String> userNameList);
	/**
	 * 根据发布话题的用户名称删除收藏
	 * @param userNameList 发布话题的用户名称集合
	 */
	public Integer deleteFavoriteByPostUserName(List<String> userNameList);
	/**
	 * 根据Id查询话题收藏
	 * @param topicFavoriteId 话题收藏Id
	 * @return
	 */
	public TopicFavorite findTopicFavoriteById(String topicFavoriteId);
	/**
	 * 根据话题Id查询收藏夹分页
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param topicId 话题Id
	 * @return
	 */
	public QueryResult<Favorites> findFavoritePageByTopicId(int firstIndex, int maxResult,Long topicId);
	/**
	 * 根据话题Id查询被收藏数量
	 * @param topicId 话题Id
	 * @return
	 */
	public Long findFavoriteCountByTopicId(Long topicId);
}
