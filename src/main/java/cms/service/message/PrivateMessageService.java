package cms.service.message;


import java.util.List;

import cms.bean.QueryResult;
import cms.bean.message.PrivateMessage;
import cms.service.besa.DAO;

/**
 * 私信
 *
 */
public interface PrivateMessageService extends DAO<PrivateMessage>{
	/**
	 * 保存私信
	 * 先由privateMessageManage.createPrivateMessageObject();方法生成对象再保存
	 * @param sender_privateMessage 私信发送者
	 * @param receiver_privateMessage 私信接收者
	 */
	public void savePrivateMessage(Object sender_privateMessage,Object receiver_privateMessage);
	
	/**
	 * 根据用户Id查询私信列表分页(原生SQL查询)
	 * @param userId 用户Id
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	public QueryResult<PrivateMessage> findPrivateMessageByUserId(Long userId,int firstIndex, int maxResult);
	
	/**
	 * 根据用户Id查询私信列表分页(原生SQL查询)
	 * @param userId 用户Id
	 * @param status 状态
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	public QueryResult<PrivateMessage> findPrivateMessageByUserId(Long userId,Integer status,int firstIndex, int maxResult);
	
	
	/**
	 * 根据用户Id查询私信对话分页
	 * @param userId 用户Id
	 * @param friendUserId 对方用户Id
	 * @param status 状态
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param sort 排序 1.aes 2.desc
	 * @return
	 */
	public QueryResult<PrivateMessage> findPrivateMessageChatByUserId(Long userId,Long friendUserId,Integer status,int firstIndex, int maxResult,Integer sort);
	/**
	 * 根据用户Id查询私信对话分页总数
	 * @param userId 用户Id
	 * @param friendUserId 对方用户Id
	 * @param status 状态
	 * @return
	 */
	public Long findPrivateMessageChatCountByUserId(Long userId,Long friendUserId,Integer status);
	/**
	 * 根据用户Id查询未读私信数量
	 * @param userId 用户Id
	 * @return
	 */
	public Long findUnreadPrivateMessageByUserId(Long userId);
	/**
	 * 设置私信状态为已读
	 * @param userId 用户Id
	 * @param privateMessageIdList 私信Id集合
	 */
	public Integer updatePrivateMessageStatus(Long userId,List<String> privateMessageIdList);
	
	/**
	 * 软删除私信
	 * @param userId 用户Id
	 * @param friendUserId 对方用户Id
	 */
	public Integer softDeletePrivateMessage(Long userId,Long friendUserId);
	/**
	 * 删除私信
	 * @param userId 用户Id
	 * @param friendUserId 对方用户Id
	 */
	public Integer deletePrivateMessage(Long userId,Long friendUserId);
	/**
	 * 删除用户所有私信
	 * @param userIdList 用户Id集合
	 */
	public Integer deleteUserPrivateMessage(List<Long> userIdList);
	
	/**
	 * 还原被用户删除的私信
	 * @param privateMessageId 私信Id
	 */
	public Integer reductionPrivateMessage(String privateMessageId);
}
