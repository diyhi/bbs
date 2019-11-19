package cms.service.follow;


import java.util.List;

import cms.bean.QueryResult;
import cms.bean.follow.Follow;
import cms.bean.follow.Follower;
import cms.service.besa.DAO;

/**
 * 关注
 *
 */
public interface FollowService extends DAO<Follow>{
	/**
	 * 根据Id查询关注
	 * @param followId 关注Id
	 * @return
	 */
	public Follow findById(String followId);
	/**
	 * 根据用户名称查询所有关注
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @return
	 */
	public List<Follow> findAllFollow(Long userId,String userName);
	/**
	 * 根据用户名称查询关注分页
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	public QueryResult<Follow> findFollowByUserName(Long userId,String userName,int firstIndex, int maxResult);
	/**
	 * 根据用户名称查询粉丝分页
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	public QueryResult<Follower> findFollowerByUserName(Long userId,String userName,int firstIndex, int maxResult);
	/**
	 * 保存关注
	 * @param follow 关注
	 * @param follower 粉丝
	 */
	public void saveFollow(Object follow,Object follower);
	/**
	 * 删除关注
	 * @param followId 关注Id
	 * @param followerId 粉丝Id
	 */
	public Integer deleteFollow(String followId,String followerId);
	/**
	 * 根据用户名称删除关注
	 * @param userNameList 用户名称集合
	 */
	public Integer deleteFollowByUserName(List<String> userNameList);
	/**
	 * 根据用户名称查询粉丝数量
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @return
	 */
	public Long findFollowerCountByUserName(Long userId,String userName);
	/**
	 * 根据用户名称查询关注数量
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @return
	 */
	public Long findFollowCountByUserName(Long userId,String userName);
}
