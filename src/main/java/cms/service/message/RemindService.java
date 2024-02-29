package cms.service.message;


import java.util.List;

import cms.bean.QueryResult;
import cms.bean.message.Remind;
import cms.service.besa.DAO;

/**
 * 提醒
 *
 */
public interface RemindService extends DAO<Remind>{

	/**
	 * 保存提醒
	 * @param remind
	 */
	public void saveRemind(Object remind);
	/**
	 * 设置提醒设状态为已读
	 * @param userId 用户Id
	 * @param unreadRemindIdList 提醒设Id集合
	 */
	public Integer updateRemindStatus(Long userId,List<String> unreadRemindIdList);
	/**
	 * 设置全部提醒状态为已读
	 * @param userId 用户Id
	 */
	public Integer updateAllRemindStatus(Long userId);
	/**
	 * 软删除提醒
	 * @param userId 用户Id
	 * @param remindId 提醒Id
	 */
	public Integer softDeleteRemind(Long userId,String remindId);
	/**
	 * 根据用户Id集合删除提醒
	 * @param userIdList 用户Id集合
	 */
	public Integer deleteRemindByUserId(List<Long> userIdList);
	
	/**
	 * 还原被用户删除的提醒
	 * @param remindId 提醒Id
	 */
	public Integer reductionRemind(String remindId);
	/**
	 * 根据话题Id删除提醒
	 * @param topicId 话题Id
	 * @return
	 */
	public Integer deleteRemindByTopicId(Long topicId);
	/**
	 * 根据问题Id删除提醒
	 * @param questionId 问题Id
	 * @return
	 */
	public Integer deleteRemindByQuestionId(Long questionId);
	/**
	 * 根据用户Id查询提醒分页
	 * @param userId 用户Id
	 * @param status 状态
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	public QueryResult<Remind> findRemindByUserId(Long userId,Integer status,int firstIndex, int maxResult);
	/**
	 * 根据用户Id和提醒类型代码编号查询最新一条提醒
	 * @param userId 用户Id
	 * @param typeCode 提醒类型代码编号
	 * @return
	 */
	public Remind findNewRemindByUserId(Long userId,Integer typeCode);
	/**
	 * 根据用户Id查询未读提醒数量
	 * @param userId 用户Id
	 * @return
	 */
	public Long findUnreadRemindByUserId(Long userId);
}
