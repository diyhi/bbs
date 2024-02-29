package cms.service.message;


import java.util.Date;
import java.util.List;
import java.util.Map;

import cms.bean.QueryResult;
import cms.bean.message.SubscriptionSystemNotify;
import cms.bean.message.SystemNotify;
import cms.service.besa.DAO;

/**
 * 系统通知
 *
 */
public interface SystemNotifyService extends DAO<SystemNotify>{
	
	/**
	 * 根据Id查询系统通知
	 * @param systemNotifyId 系统通知Id
	 * @return
	 */
	public SystemNotify findById(Long systemNotifyId);
	
	/**
	 * 根据用户Id查询订阅系统通知分页
	 * @param userId 用户Id
	 * @param status 状态
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	public QueryResult<SubscriptionSystemNotify> findSubscriptionSystemNotifyByUserId(Long userId,Integer status,int firstIndex, int maxResult);
	/**
	 * 查询用户最新订阅系统Id
	 * @param userId 用户Id
	 */
	public Long findMaxSystemNotifyIdByUserId(Long userId);
	
	/**
	 * 根据发送时间查询系统通知(仅返回id和sendTime字段)
	 * @param sendTime 发送时间
	 */
	public Map<Long,Date> findSystemNotifyBySendTime(Date sendTime);
	
	/**
	 * 根据系统通知Id查询系统通知(仅返回id和sendTime字段)
	 * @param systemNotifyId 系统通知Id
	 */
	public Map<Long,Date> findSystemNotifyById(Long systemNotifyId);
	/**
	 * 设置订阅系统通知状态为已读
	 * @param userId 用户Id
	 * @param subscriptionSystemNotifyIdList 订阅系统通知Id集合
	 */
	public Integer updateSubscriptionSystemNotifyStatus(Long userId,List<String> subscriptionSystemNotifyIdList);
	/**
	 * 设置全部订阅系统通知状态为已读
	 * @param userId 用户Id
	 */
	public Integer updateAllSubscriptionSystemNotifyStatus(Long userId);
	/**
	 * 根据用户Id查询最早的未读系统通知Id
	 * @param userId 用户Id
	 * @return
	 */
	public Long findMinUnreadSystemNotifyIdByUserId(Long userId);
	/**
	 * 根据用户Id查询最大的已读系统通知Id 
	 * @param userId 用户Id
	 * @return
	 */
	public Long findMaxReadSystemNotifyIdByUserId(Long userId);
	/**
	 * 根据起始系统通知Id查询系统通知数量(不含起始系统通知Id行)
	 * @param start_systemNotifyId 起始系统通知Id
	 * @return
	 */
	public Long findSystemNotifyCountBySystemNotifyId(Long start_systemNotifyId);
	/**
	 * 根据起始系统通知发送时间查询系统通知数量
	 * @param start_sendTime 起始系统通知发送时间
	 * @return
	 */
	public Long findSystemNotifyCountBySendTime(Date start_sendTime);
	/**
	 * 保存订阅系统通知
	 * @param subscriptionSystemNotifyList 订阅系统通知集合
	 */
	public void saveSubscriptionSystemNotify(List<Object> subscriptionSystemNotifyList);
	/**
	 * 软删除订阅系统通知
	 * @param userId 用户Id
	 * @param subscriptionSystemNotifyId 订阅系统通知Id
	 */
	public Integer softDeleteSubscriptionSystemNotify(Long userId,String subscriptionSystemNotifyId);
	/**
	 * 删除用户所有订阅系统通知
	 * @param userIdList 用户Id集合
	 */
	public Integer deleteUserSubscriptionSystemNotify(List<Long> userIdList);
	/**
	 * 还原被用户删除的订阅系统通知
	 * @param subscriptionSystemNotifyId 订阅系统通知Id
	 */
	public Integer reductionSubscriptionSystemNotify(String subscriptionSystemNotifyId);
	
	/**
	 * 保存系统通知
	 * @param systemNotify
	 */
	public void saveSystemNotify(SystemNotify systemNotify);
	/**
	 * 修改系统通知
	 * @param systemNotify
	 * @return
	 */
	public Integer updateSystemNotify(SystemNotify systemNotify);
	/**
	 * 删除系统通知
	 * @param systemNotifyId 系统通知Id
	 */
	public Integer deleteSystemNotify(Long systemNotifyId);
}
