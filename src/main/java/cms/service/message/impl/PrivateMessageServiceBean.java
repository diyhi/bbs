package cms.service.message.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.QueryResult;
import cms.bean.message.PrivateMessage;
import cms.service.besa.DaoSupport;
import cms.service.message.PrivateMessageService;
import cms.utils.ObjectConversion;
import cms.utils.VersionCompare;
import cms.web.action.data.MySqlDataManage;
import cms.web.action.message.PrivateMessageConfig;
import net.sf.cglib.beans.BeanCopier;

/**
 * 私信
 *
 */
@Service
@Transactional
public class PrivateMessageServiceBean extends DaoSupport<PrivateMessage> implements PrivateMessageService{
	private static final Logger logger = LogManager.getLogger(PrivateMessageServiceBean.class);
	 
	
	@Resource PrivateMessageConfig privateMessageConfig;
	@Resource MySqlDataManage mySqlDataManage;
	
	/**
	 * 保存私信
	 * 先由privateMessageManage.createPrivateMessageObject();方法生成对象再保存
	 * @param sender_privateMessage 私信发送者
	 * @param receiver_privateMessage 私信接收者
	 */
	public void savePrivateMessage(Object sender_privateMessage,Object receiver_privateMessage){
		this.save(sender_privateMessage);
		
		this.save(receiver_privateMessage);
	}
	
	
	
	/**
	 * 根据用户Id查询私信列表分页(后台原生SQL查询)
	 * @param userId 用户Id
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<PrivateMessage> findPrivateMessageByUserId(Long userId,int firstIndex, int maxResult){
		QueryResult<PrivateMessage> qr = new QueryResult<PrivateMessage>();

		//表编号
		int tableNumber = privateMessageConfig.userIdRemainder(userId);
		
		
		
		
		//MySQL5.7之后版本分组出现this is incompatible with sql_mode=only_full_group_by错误.查询的列必须在group by后面出现否则就会报错，或者这个字段出现在聚合函数里面。
		
		//在mysql 5.7.5之前的版本，ONLY_FULL_GROUP_BY sql mode默认不开启。在5.7.5或之后的版本默认开启。
		
		//mysql 5.7之后的版本对子查询排序做了优化，子查询全表排序失效
		
		//当前MySQL版本
		String currentVersion = mySqlDataManage.showVersion();

		int v = VersionCompare.compare(currentVersion, "5.7.5");

		//原始SQL，因为兼容问题，下面两条SQL按本SQL查询实现效果修改
		//sql = "select t1.id, t1.userId, t1.friendUserId, t1.senderUserId, t1.receiverUserId, t1.messageContent, t1.status, t1.sendTimeFormat, t1.readTimeFormat from (select * from privatemessage_"+tableNumber+" o where o.userId=? order by o.sendTimeFormat desc) as t1 GROUP BY t1.friendUserId ORDER BY t1.sendTimeFormat desc";
		

		String sql = "";
		//注意t2 必须使用 GROUP BY分组，因为sendTimeFormat字段值可能一样，必须去重
		if(v >=0){
			//MySQL 5.7.5及之后版本  在不需要group by的字段上使用any_value()函数
			sql = "select ANY_VALUE(t1.id) as id, ANY_VALUE(t1.userId) as userId, ANY_VALUE(t1.friendUserId) as friendUserId, ANY_VALUE(t1.senderUserId) as senderUserId, ANY_VALUE(t1.receiverUserId) as receiverUserId, ANY_VALUE(t1.messageContent) as messageContent, ANY_VALUE(t1.status) as status, ANY_VALUE(t1.sendTimeFormat) as sendTimeFormat, ANY_VALUE(t1.readTimeFormat) as readTimeFormat from privatemessage_"+tableNumber+" t1, (select o.friendUserId as friendUserId, max(o.sendTimeFormat) as max_sendTimeFormat from privatemessage_"+tableNumber+" o where o.userId=? GROUP BY o.friendUserId) as t2 where t1.friendUserId=t2.friendUserId and t1.sendTimeFormat=t2.max_sendTimeFormat GROUP BY t1.friendUserId ORDER BY ANY_VALUE(t1.sendTimeFormat) desc";
			
		}else{
			//MySQL 5.7.5之前版本
			sql = "select t1.id as id, t1.userId, t1.friendUserId, t1.senderUserId, t1.receiverUserId, t1.messageContent, t1.status, t1.sendTimeFormat as sendTimeFormat, t1.readTimeFormat from privatemessage_"+tableNumber+" t1, (select o.friendUserId as friendUserId, max(o.sendTimeFormat) as max_sendTimeFormat from privatemessage_"+tableNumber+" o where o.userId=? GROUP BY o.friendUserId) as t2 where t1.friendUserId=t2.friendUserId and t1.sendTimeFormat=t2.max_sendTimeFormat GROUP BY t1.friendUserId ORDER BY t1.sendTimeFormat desc";
		}
		
		Query query = em.createNativeQuery(sql);
		query.setParameter(1,userId);
		
		
		//索引开始,即从哪条记录开始
		query.setFirstResult(firstIndex);
		//获取多少条数据
		query.setMaxResults(maxResult);

		List<PrivateMessage> privateMessageList = new ArrayList<PrivateMessage>();
		List<Object[]> objectList = query.getResultList();
		
		if(objectList != null && objectList.size() >0){
			for(int o = 0; o<objectList.size(); o++){
				Object[] object = objectList.get(o);
				PrivateMessage privateMessage = new PrivateMessage();
				privateMessage.setId(ObjectConversion.conversion(object[0], ObjectConversion.STRING));
				privateMessage.setUserId(ObjectConversion.conversion(object[1], ObjectConversion.LONG));
				privateMessage.setFriendUserId(ObjectConversion.conversion(object[2], ObjectConversion.LONG));
				privateMessage.setSenderUserId(ObjectConversion.conversion(object[3], ObjectConversion.LONG));
				privateMessage.setReceiverUserId(ObjectConversion.conversion(object[4], ObjectConversion.LONG));
				privateMessage.setMessageContent(ObjectConversion.conversion(object[5], ObjectConversion.STRING));
				privateMessage.setStatus(ObjectConversion.conversion(object[6], ObjectConversion.INTEGER));
				privateMessage.setSendTimeFormat(ObjectConversion.conversion(object[7], ObjectConversion.LONG));
				privateMessage.setReadTimeFormat(ObjectConversion.conversion(object[8], ObjectConversion.LONG));
				privateMessageList.add(privateMessage);
			}
		}
		
		//把查询结果设进去
		qr.setResultlist(privateMessageList);

		query = em.createNativeQuery("select count(1) c from (select count(1) from privatemessage_"+tableNumber+" o where o.userId=? GROUP BY o.friendUserId ) c1");
		query.setParameter(1,userId);
		Long lCount = 0L;
		for(Object o :query.getResultList()){
			lCount +=Long.parseLong(o.toString());
		}
		
		//获取总记录数
		qr.setTotalrecord(lCount);

		return qr;
	}
	/**
	 * 根据用户Id查询私信列表分页(前台原生SQL查询)
	 * @param userId 用户Id
	 * @param status 状态
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<PrivateMessage> findPrivateMessageByUserId(Long userId,Integer status,int firstIndex, int maxResult){
		QueryResult<PrivateMessage> qr = new QueryResult<PrivateMessage>();

		//表编号
		int tableNumber = privateMessageConfig.userIdRemainder(userId);
		
		
		
		//MySQL5.7之后版本分组出现this is incompatible with sql_mode=only_full_group_by错误.查询的列必须在group by后面出现否则就会报错，或者这个字段出现在聚合函数里面。
		
		//在mysql 5.7.5之前的版本，ONLY_FULL_GROUP_BY sql mode默认不开启。在5.7.5或之后的版本默认开启。
		
		//mysql 5.7之后的版本对子查询排序做了优化，子查询全表排序失效
		
		//当前MySQL版本
		String currentVersion = mySqlDataManage.showVersion();

		int v = VersionCompare.compare(currentVersion, "5.7.5");

		//原始SQL，因为兼容问题，下面两条SQL按本SQL查询实现效果修改
		//sql = "select t1.id, t1.userId, t1.friendUserId, t1.senderUserId, t1.receiverUserId, t1.messageContent, t1.status, t1.sendTimeFormat, t1.readTimeFormat from (select * from privatemessage_"+tableNumber+" o where o.userId=? and o.status <? order by o.status asc,o.sendTimeFormat desc) as t1 GROUP BY t1.friendUserId ORDER BY t1.sendTimeFormat desc";
		

		String sql = "";
		//注意t2 必须使用 GROUP BY分组，因为sendTimeFormat字段值可能一样，必须去重
		if(v >=0){
			//MySQL 5.7.5及之后版本  在不需要group by的字段上使用any_value()函数
			sql = "select ANY_VALUE(t1.id) as id, ANY_VALUE(t1.userId) as userId, ANY_VALUE(t1.friendUserId) as friendUserId, ANY_VALUE(t1.senderUserId) as senderUserId, ANY_VALUE(t1.receiverUserId) as receiverUserId, ANY_VALUE(t1.messageContent) as messageContent, ANY_VALUE(t1.status) as status, ANY_VALUE(t1.sendTimeFormat) as sendTimeFormat, ANY_VALUE(t1.readTimeFormat) as readTimeFormat from privatemessage_"+tableNumber+" t1, (select o.friendUserId as friendUserId, max(o.sendTimeFormat) as max_sendTimeFormat from privatemessage_"+tableNumber+" o where o.userId=? and o.status <?  GROUP BY o.friendUserId) as t2 where t1.friendUserId=t2.friendUserId and t1.sendTimeFormat=t2.max_sendTimeFormat GROUP BY t1.friendUserId ORDER BY ANY_VALUE(t1.status) asc, ANY_VALUE(t1.sendTimeFormat) desc";
			
		}else{
			//MySQL 5.7.5之前版本
			sql = "select t1.id as id, t1.userId, t1.friendUserId, t1.senderUserId, t1.receiverUserId, t1.messageContent, t1.status as status, t1.sendTimeFormat as sendTimeFormat, t1.readTimeFormat from privatemessage_"+tableNumber+" t1, (select o.friendUserId as friendUserId, max(o.sendTimeFormat) as max_sendTimeFormat from privatemessage_"+tableNumber+" o where o.userId=? and o.status <?  GROUP BY o.friendUserId) as t2 where t1.friendUserId=t2.friendUserId and t1.sendTimeFormat=t2.max_sendTimeFormat GROUP BY t1.friendUserId ORDER BY t1.status asc,t1.sendTimeFormat desc";
		}
		
		
		Query query = em.createNativeQuery(sql);
		
		query.setParameter(1,userId);
		query.setParameter(2,status);
		
		
		//索引开始,即从哪条记录开始
		query.setFirstResult(firstIndex);
		//获取多少条数据
		query.setMaxResults(maxResult);

		List<PrivateMessage> privateMessageList = new ArrayList<PrivateMessage>();
		List<Object[]> objectList = query.getResultList();
		
		if(objectList != null && objectList.size() >0){
			for(int o = 0; o<objectList.size(); o++){
				Object[] object = objectList.get(o);
				PrivateMessage privateMessage = new PrivateMessage();
				privateMessage.setId(ObjectConversion.conversion(object[0], ObjectConversion.STRING));
				privateMessage.setUserId(ObjectConversion.conversion(object[1], ObjectConversion.LONG));
				privateMessage.setFriendUserId(ObjectConversion.conversion(object[2], ObjectConversion.LONG));
				privateMessage.setSenderUserId(ObjectConversion.conversion(object[3], ObjectConversion.LONG));
				privateMessage.setReceiverUserId(ObjectConversion.conversion(object[4], ObjectConversion.LONG));
				privateMessage.setMessageContent(ObjectConversion.conversion(object[5], ObjectConversion.STRING));
				privateMessage.setStatus(ObjectConversion.conversion(object[6], ObjectConversion.INTEGER));
				privateMessage.setSendTimeFormat(ObjectConversion.conversion(object[7], ObjectConversion.LONG));
				privateMessage.setReadTimeFormat(ObjectConversion.conversion(object[8], ObjectConversion.LONG));
				privateMessageList.add(privateMessage);
			}
		}
		
		//把查询结果设进去
		qr.setResultlist(privateMessageList);

		query = em.createNativeQuery("select count(1) c from (select count(1) from privatemessage_"+tableNumber+" o where o.userId=? and o.status <? GROUP BY o.friendUserId ) c1");
		query.setParameter(1,userId);
		query.setParameter(2,status);
		Long lCount = 0L;
		for(Object o :query.getResultList()){
			lCount +=Long.parseLong(o.toString());
		}
		
		//获取总记录数
		qr.setTotalrecord(lCount);

		return qr;
	}
	
	
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
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<PrivateMessage> findPrivateMessageChatByUserId(Long userId,Long friendUserId,Integer status,int firstIndex, int maxResult,Integer sort){
		QueryResult<PrivateMessage> qr = new QueryResult<PrivateMessage>();
		
		String status_sql = "";
		if(status != null){
			status_sql = " and o.status <?3 ";
		}
		String sort_sql = "";
		if(sort.equals(1)){
			sort_sql = "asc";
		}else{
			sort_sql = "desc";
		}
		//表编号
		int tableNumber = privateMessageConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			
			query = em.createQuery("select o from PrivateMessage o where o.userId=?1 and o.friendUserId=?2 "+status_sql+" ORDER BY o.sendTimeFormat "+sort_sql);
			query.setParameter(1, userId);
			query.setParameter(2, friendUserId);
			if(status != null){
				query.setParameter(3, 100);
			}
			
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<PrivateMessage> privateMessageList= query.getResultList();
			qr.setResultlist(privateMessageList);
			
			query = em.createQuery("select count(o) from PrivateMessage o where o.userId=?1 and o.friendUserId=?2 "+status_sql+"");
			query.setParameter(1, userId);
			query.setParameter(2, friendUserId);
			if(status != null){
				query.setParameter(3, 100);
			}
			qr.setTotalrecord((Long)query.getSingleResult());
		}else{//带下划线对象
			query = em.createQuery("select o from PrivateMessage_"+tableNumber+" o where o.userId=?1 and o.friendUserId=?2 "+status_sql+" ORDER BY o.sendTimeFormat "+sort_sql);
			query.setParameter(1, userId);
			query.setParameter(2, friendUserId);
			if(status != null){
				query.setParameter(3, 100);
			}
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> privateMessage_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.message.PrivateMessage_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),PrivateMessage.class, false); 
				List<PrivateMessage> privateMessageList= new ArrayList<PrivateMessage>();
				for(int j = 0;j< privateMessage_List.size(); j++) {  
					Object obj = privateMessage_List.get(j);
					PrivateMessage privateMessage = new PrivateMessage();
					copier.copy(obj,privateMessage, null);
					privateMessageList.add(privateMessage);
				}
				qr.setResultlist(privateMessageList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户Id查询私信对话分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户Id查询私信对话分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户Id查询私信对话分页",e);
		        }
			}
			
			query = em.createQuery("select count(o) from PrivateMessage_"+tableNumber+" o where o.userId=?1 and o.friendUserId=?2 "+status_sql+"");
			query.setParameter(1, userId);
			query.setParameter(2, friendUserId);
			if(status != null){
				query.setParameter(3, 100);
			}
			qr.setTotalrecord((Long)query.getSingleResult());
		}
		
		return qr;
	}
	
	/**
	 * 根据用户Id查询私信对话分页总数
	 * @param userId 用户Id
	 * @param friendUserId 对方用户Id
	 * @param status 状态
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findPrivateMessageChatCountByUserId(Long userId,Long friendUserId,Integer status){
		String status_sql = "";
		if(status != null){
			status_sql = " and o.status <?3 ";
		}
		
		//表编号
		int tableNumber = privateMessageConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象	
			query = em.createQuery("select count(o) from PrivateMessage o where o.userId=?1 and o.friendUserId=?2 "+status_sql+"");
			query.setParameter(1, userId);
			query.setParameter(2, friendUserId);
			if(status != null){
				query.setParameter(3, 100);
			}
			return (Long)query.getSingleResult();
		}else{//带下划线对象
			query = em.createQuery("select count(o) from PrivateMessage_"+tableNumber+" o where o.userId=?1 and o.friendUserId=?2 "+status_sql+"");
			query.setParameter(1, userId);
			query.setParameter(2, friendUserId);
			if(status != null){
				query.setParameter(3, 100);
			}
			return (Long)query.getSingleResult();
		}
	}
	/**
	 * 根据用户Id查询未读私信数量
	 * @param userId 用户Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findUnreadPrivateMessageByUserId(Long userId){
		Long count = 0L;
		//表编号
		int tableNumber = privateMessageConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select count(o) from PrivateMessage o where o.userId=?1 and o.status =?2 ");
			query.setParameter(1, userId);
			query.setParameter(2, 10);
			count = (Long)query.getSingleResult();
			
		}else{//带下划线对象
			query = em.createQuery("select count(o) from PrivateMessage_"+tableNumber+" o where o.userId=?1 and o.status =?2 ");
			query.setParameter(1, userId);
			query.setParameter(2, 10);
			count = (Long)query.getSingleResult();
		}
		return count;
	}
	
	
	
	
	/**
	 * 设置私信状态为已读
	 * @param userId 用户Id
	 * @param privateMessageIdList 私信Id集合
	 */
	public Integer updatePrivateMessageStatus(Long userId,List<String> privateMessageIdList){
	
		int i = 0;
		Long time = new Date().getTime();
		
		//表编号
		int tableNumber = privateMessageConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update PrivateMessage o set o.status=:status, o.readTimeFormat=:readTimeFormat where o.id in(:id) and o.status <:status2")
					.setParameter("status", 20)
					.setParameter("readTimeFormat", time)
					.setParameter("id", privateMessageIdList)
					.setParameter("status2", 20);
					i += query.executeUpdate();
			
		}else{//带下划线对象
			Query query = em.createQuery("update PrivateMessage_"+tableNumber+" o set o.status=:status, o.readTimeFormat=:readTimeFormat where o.id in(:id) and o.status <:status2")
					.setParameter("status", 20)
					.setParameter("readTimeFormat", time)
					.setParameter("id", privateMessageIdList)
					.setParameter("status2", 20);
					i += query.executeUpdate();
		}

		return i;
	}
	
	
	/**
	 * 软删除私信
	 * @param userId 用户Id
	 * @param friendUserId 对方用户Id
	 */
	public Integer softDeletePrivateMessage(Long userId,Long friendUserId){
		int i = 0;
		
		//表编号
		int tableNumber = privateMessageConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update PrivateMessage o set o.status=o.status+?1 where o.userId=?2 and o.friendUserId=?3 and o.status <?4")
					.setParameter(1, 100)
					.setParameter(2, userId)
					.setParameter(3, friendUserId)
					.setParameter(4, 100);
					i = query.executeUpdate();
			
		}else{//带下划线对象
			Query query = em.createQuery("update PrivateMessage_"+tableNumber+" o set o.status=o.status+?1 where o.userId=?2  and o.friendUserId=?3 and o.status <?4")
					.setParameter(1, 100)
					.setParameter(2, userId)
					.setParameter(3, friendUserId)
					.setParameter(4, 100);
					i = query.executeUpdate();
		}

		return i;
	}
	
	
	
	
	
	/**
	 * 删除私信
	 * @param userId 用户Id
	 * @param friendUserId 对方用户Id
	 */
	public Integer deletePrivateMessage(Long userId,Long friendUserId){
		int i = 0;
		
		//表编号
		int tableNumber = privateMessageConfig.userIdRemainder(userId);
			
		if(tableNumber == 0){//默认对象
			Query delete = em.createQuery("delete from PrivateMessage o where o.userId=?1 and o.friendUserId=?2")
					.setParameter(1, userId)
					.setParameter(2, friendUserId);
					i += delete.executeUpdate();
		}else{//带下划线对象
			Query delete = em.createQuery("delete from PrivateMessage_"+tableNumber+" o where o.userId=?1 and o.friendUserId=?2")
					.setParameter(1, userId)
					.setParameter(2, friendUserId);
					i += delete.executeUpdate();
			
		}
		
		return i;
	}
	
	/**
	 * 删除用户所有私信
	 * @param userIdList 用户Id集合
	 */
	public Integer deleteUserPrivateMessage(List<Long> userIdList){
		int j = 0;
		
		//表编号
		int tableNumber = privateMessageConfig.getTableQuantity();
		for(int i = 0; i<tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from PrivateMessage o where o.userId in(:userId)")
						.setParameter("userId", userIdList);
				j += query.executeUpdate();
				
				query = em.createQuery("delete from PrivateMessage o where o.friendUserId in(:userId)")
						.setParameter("userId", userIdList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from PrivateMessage_"+i+" o where o.userId in(:userId)")
						.setParameter("userId", userIdList);
				j += query.executeUpdate();
				
				query = em.createQuery("delete from PrivateMessage_"+i+" o where o.friendUserId in(:userId)")
						.setParameter("userId", userIdList);
				j += query.executeUpdate();
			}
		}
		

		return j;
	}
	
	/**
	 * 还原被用户删除的私信
	 * @param privateMessageId 私信Id
	 */
	public Integer reductionPrivateMessage(String privateMessageId){
		
		//表编号
		int tableNumber = privateMessageConfig.privateMessageIdRemainder(privateMessageId);
		int i = 0;
		Query query = null;
		if(tableNumber == 0){//默认对象
			query = em.createQuery("update PrivateMessage o set o.status=o.status-?1 where o.id=?2 and o.status >?3")
			.setParameter(1, 100)
			.setParameter(2, privateMessageId)
			.setParameter(3, 100);
			i = query.executeUpdate();
		}else{//带下划线对象
			query = em.createQuery("update PrivateMessage_"+tableNumber+" o set o.status=o.status-?1 where o.id=?2 and o.status >?3")
			.setParameter(1, 100)
			.setParameter(2, privateMessageId)
			.setParameter(3, 100);
			i = query.executeUpdate();
		}
		return i;
	}
	
}
