package cms.service.message.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.QueryResult;
import cms.bean.message.SubscriptionSystemNotify;
import cms.bean.message.SystemNotify;
import cms.service.besa.DaoSupport;
import cms.service.message.SystemNotifyService;
import cms.utils.ObjectConversion;
import cms.web.action.message.SubscriptionSystemNotifyConfig;
import net.sf.cglib.beans.BeanCopier;

/**
 * 系统通知
 *
 */
@Service
@Transactional
public class SystemNotifyServiceBean extends DaoSupport<SystemNotify> implements SystemNotifyService{
	private static final Logger logger = LogManager.getLogger(SystemNotifyServiceBean.class);
	 
	
	@Resource SubscriptionSystemNotifyConfig subscriptionSystemNotifyConfig;
	
	/**
	 * 根据Id查询系统通知
	 * @param systemNotifyId 系统通知Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public SystemNotify findById(Long systemNotifyId){
		Query query = em.createQuery("select o from SystemNotify o where o.id=?1")
		.setParameter(1, systemNotifyId);
		List<SystemNotify> list = query.getResultList();
		for(SystemNotify p : list){
			return p;
		}
		return null;
	}
	
	
	/**
	 * 根据用户Id查询订阅系统通知分页
	 * @param userId 用户Id
	 * @param status 状态
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<SubscriptionSystemNotify> findSubscriptionSystemNotifyByUserId(Long userId,Integer status,int firstIndex, int maxResult){
		QueryResult<SubscriptionSystemNotify> qr = new QueryResult<SubscriptionSystemNotify>();
		
		String status_sql = "";
		if(status != null){
			status_sql = " and o.status <?2 ";
		}
		
		//表编号
		int tableNumber = subscriptionSystemNotifyConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			
			query = em.createQuery("select o from SubscriptionSystemNotify o where o.userId=?1 "+status_sql+" ORDER BY o.systemNotifyId desc");
			query.setParameter(1, userId);
			if(status != null){
				query.setParameter(2, status);
			}
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<SubscriptionSystemNotify> subscriptionSystemNotifyList= query.getResultList();
			qr.setResultlist(subscriptionSystemNotifyList);
			
			query = em.createQuery("select count(o) from SubscriptionSystemNotify o where o.userId=?1 "+status_sql+"");
			query.setParameter(1, userId);
			if(status != null){
				query.setParameter(2, status);
			}
			qr.setTotalrecord((Long)query.getSingleResult());
		}else{//带下划线对象
			
			query = em.createQuery("select o from SubscriptionSystemNotify_"+tableNumber+" o where o.userId=?1 "+status_sql+" ORDER BY o.systemNotifyId desc");
			query.setParameter(1, userId);
			if(status != null){
				query.setParameter(2, status);
			}
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> subscriptionSystemNotify_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.message.SubscriptionSystemNotify_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),SubscriptionSystemNotify.class, false); 
				List<SubscriptionSystemNotify> subscriptionSystemNotifyList= new ArrayList<SubscriptionSystemNotify>();
				for(int j = 0;j< subscriptionSystemNotify_List.size(); j++) {  
					Object obj = subscriptionSystemNotify_List.get(j);
					SubscriptionSystemNotify subscriptionSystemNotify = new SubscriptionSystemNotify();
					copier.copy(obj,subscriptionSystemNotify, null);
					subscriptionSystemNotifyList.add(subscriptionSystemNotify);
				}
				qr.setResultlist(subscriptionSystemNotifyList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户Id查询订阅系统通知分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户Id查询订阅系统通知分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户Id查询订阅系统通知分页",e);
		        }
			}
			
			query = em.createQuery("select count(o) from SubscriptionSystemNotify_"+tableNumber+" o where o.userId=?1 "+status_sql+"");
			query.setParameter(1, userId);
			if(status != null){
				query.setParameter(2, status);
			}
			qr.setTotalrecord((Long)query.getSingleResult());
			
		}
		
		return qr;
	}
	
	
	
	/**
	 * 查询用户最新订阅系统通知Id
	 * @param userId 用户Id
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findMaxSystemNotifyIdByUserId(Long userId){
		Long maxSystemNotifyId = null;
		//表编号
		int tableNumber = subscriptionSystemNotifyConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			
			query = em.createQuery("select o.systemNotifyId from SubscriptionSystemNotify o where o.userId=?1 ORDER BY o.systemNotifyId desc");
			query.setParameter(1, userId);
			//索引开始,即从哪条记录开始
			query.setFirstResult(0);
			//获取多少条数据
			query.setMaxResults(1);
			List<Object> objectList = query.getResultList();
			
			if(objectList != null && objectList.size() >0){
				for(int o = 0; o<objectList.size(); o++){
					maxSystemNotifyId = ObjectConversion.conversion(objectList.get(o), ObjectConversion.LONG);
				}
			}

		}else{//带下划线对象
			
			query = em.createQuery("select o.systemNotifyId from SubscriptionSystemNotify_"+tableNumber+" o where o.userId=?1 ORDER BY o.systemNotifyId desc");
			query.setParameter(1, userId);
			//索引开始,即从哪条记录开始
			query.setFirstResult(0);
			//获取多少条数据
			query.setMaxResults(1);
			List<Object> objectList = query.getResultList();
			
			if(objectList != null && objectList.size() >0){
				for(int o = 0; o<objectList.size(); o++){
					maxSystemNotifyId = ObjectConversion.conversion(objectList.get(o), ObjectConversion.LONG);
				}
			}
		}
		
		return maxSystemNotifyId;
	}
	
	
	
	/**
	 * 根据发送时间查询系统通知(仅返回id和sendTime字段)
	 * @param sendTime 发送时间
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Map<Long,Date> findSystemNotifyBySendTime(Date sendTime){
		Query query = em.createQuery("select o.id,o.sendTime from SystemNotify o where o.sendTime>=?1")
			.setParameter(1, sendTime);

		Map<Long,Date> map = new HashMap<Long,Date>();
		List<Object[]> objectList = query.getResultList();
		
		if(objectList != null && objectList.size() >0){
			for(int o = 0; o<objectList.size(); o++){
				Object[] object = objectList.get(o);

				map.put(ObjectConversion.conversion(object[0], ObjectConversion.LONG), ObjectConversion.conversion(object[1], ObjectConversion.TIMESTAMP));

			}
		}
		return map;
	}
	
	/**
	 * 根据系统通知Id查询系统通知(仅返回id和sendTime字段)
	 * @param systemNotifyId 系统通知Id
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Map<Long,Date> findSystemNotifyById(Long systemNotifyId){
		Query query = em.createQuery("select o.id,o.sendTime from SystemNotify o where o.id>?1")
				.setParameter(1, systemNotifyId);

			Map<Long,Date> map = new HashMap<Long,Date>();
			List<Object[]> objectList = query.getResultList();
			
			if(objectList != null && objectList.size() >0){
				for(int o = 0; o<objectList.size(); o++){
					Object[] object = objectList.get(o);

					map.put(ObjectConversion.conversion(object[0], ObjectConversion.LONG), ObjectConversion.conversion(object[1], ObjectConversion.TIMESTAMP));

				}
			}
			return map;
		
	}
	
	
	/**
	 * 设置订阅系统通知状态为已读
	 * @param userId 用户Id
	 * @param subscriptionSystemNotifyIdList 订阅系统通知Id集合
	 */
	public Integer updateSubscriptionSystemNotifyStatus(Long userId,List<String> subscriptionSystemNotifyIdList){
		int i = 0;
		Date time = new Date();
		//表编号
		int tableNumber = subscriptionSystemNotifyConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update SubscriptionSystemNotify o set o.status=:status, o.readTime=:readTime where o.id in(:id) and o.status <:status2")
					.setParameter("status", 20)
					.setParameter("readTime", time)
					.setParameter("id", subscriptionSystemNotifyIdList)
					.setParameter("status2", 20);
					i += query.executeUpdate();
			
		}else{//带下划线对象
			Query query = em.createQuery("update SubscriptionSystemNotify_"+tableNumber+" o set o.status=:status, o.readTime=:readTime where o.id in(:id) and o.status <:status2")
					.setParameter("status", 20)
					.setParameter("readTime", time)
					.setParameter("id", subscriptionSystemNotifyIdList)
					.setParameter("status2", 20);
					i += query.executeUpdate();
		}

		return i;
	}
	
	
	
	/**
	 * 根据用户Id查询最早的未读系统通知Id
	 * @param userId 用户Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findMinUnreadSystemNotifyIdByUserId(Long userId){
		Long systemNotifyId = null;
		//表编号
		int tableNumber = subscriptionSystemNotifyConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select o.systemNotifyId from SubscriptionSystemNotify o where o.userId=?1 and o.status =?2 ORDER BY o.systemNotifyId asc");
			query.setParameter(1, userId);
			query.setParameter(2, 10);
			//索引开始,即从哪条记录开始
			query.setFirstResult(0);
			//获取多少条数据
			query.setMaxResults(1);
			
			List<Object[]> objectList = query.getResultList();
			
			if(objectList != null && objectList.size() >0){
				for(int o = 0; o<objectList.size(); o++){
					Object object = objectList.get(o);
					systemNotifyId = ObjectConversion.conversion(object, ObjectConversion.LONG);

				}
			}
			
		}else{//带下划线对象
			query = em.createQuery("select o.systemNotifyId from SubscriptionSystemNotify_"+tableNumber+" o where o.userId=?1 and o.status =?2 ORDER BY o.systemNotifyId asc");
			query.setParameter(1, userId);
			query.setParameter(2, 10);
			//索引开始,即从哪条记录开始
			query.setFirstResult(0);
			//获取多少条数据
			query.setMaxResults(1);
			
			List<Object[]> objectList = query.getResultList();
			
			if(objectList != null && objectList.size() >0){
				for(int o = 0; o<objectList.size(); o++){
					Object object = objectList.get(o);
					systemNotifyId = ObjectConversion.conversion(object, ObjectConversion.LONG);

				}
			}
		}
		return systemNotifyId;
	}
	/**
	 * 根据用户Id查询最大的已读系统通知Id 
	 * @param userId 用户Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findMaxReadSystemNotifyIdByUserId(Long userId){
		Long systemNotifyId = null;
		//表编号
		int tableNumber = subscriptionSystemNotifyConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select o.systemNotifyId from SubscriptionSystemNotify o where o.userId=?1 and o.status >=?2 and o.status <=?3 and o.status !=?4 ORDER BY o.systemNotifyId desc");
			query.setParameter(1, userId);
			query.setParameter(2, 20);
			query.setParameter(3, 120);
			query.setParameter(4, 110);
			//索引开始,即从哪条记录开始
			query.setFirstResult(0);
			//获取多少条数据
			query.setMaxResults(1);
			
			List<Object[]> objectList = query.getResultList();
			
			if(objectList != null && objectList.size() >0){
				for(int o = 0; o<objectList.size(); o++){
					Object object = objectList.get(o);
					systemNotifyId = ObjectConversion.conversion(object, ObjectConversion.LONG);

				}
			}
			
		}else{//带下划线对象
			query = em.createQuery("select o.systemNotifyId from SubscriptionSystemNotify_"+tableNumber+" o where o.userId=?1 and o.status >=?2 and o.status <=?3 and o.status !=?4 ORDER BY o.systemNotifyId desc");
			query.setParameter(1, userId);
			query.setParameter(2, 20);
			query.setParameter(3, 120);
			query.setParameter(4, 110);
			//索引开始,即从哪条记录开始
			query.setFirstResult(0);
			//获取多少条数据
			query.setMaxResults(1);
			
			List<Object[]> objectList = query.getResultList();
			
			if(objectList != null && objectList.size() >0){
				for(int o = 0; o<objectList.size(); o++){
					Object object = objectList.get(o);
					systemNotifyId = ObjectConversion.conversion(object, ObjectConversion.LONG);

				}
			}
		}
		return systemNotifyId;
	}
	

	/**
	 * 根据起始系统通知Id查询系统通知数量(不含起始系统通知Id行)
	 * @param start_systemNotifyId 起始系统通知Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findSystemNotifyCountBySystemNotifyId(Long start_systemNotifyId){
		Query query = em.createQuery("select count(o) from SystemNotify o where o.id>?1 ORDER BY o.id asc")
			.setParameter(1, start_systemNotifyId);
		return (Long)query.getSingleResult();
	}
	
	/**
	 * 根据起始系统通知发送时间查询系统通知数量
	 * @param start_sendTime 起始系统通知发送时间
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findSystemNotifyCountBySendTime(Date start_sendTime){
		Query query = em.createQuery("select count(o) from SystemNotify o where o.sendTime>=?1 ORDER BY o.id asc")
			.setParameter(1, start_sendTime);
		return (Long)query.getSingleResult();
	}
	
	
	/**
	 * 保存订阅系统通知
	 * @param subscriptionSystemNotifyList 订阅系统通知集合
	 */
	public void saveSubscriptionSystemNotify(List<Object> subscriptionSystemNotifyList){
		for(Object subscriptionSystemNotify : subscriptionSystemNotifyList){
			this.save(subscriptionSystemNotify);
		}
		
	}
	/**
	 * 软删除订阅系统通知
	 * @param userId 用户Id
	 * @param subscriptionSystemNotifyId 订阅系统通知Id
	 */
	public Integer softDeleteSubscriptionSystemNotify(Long userId,String subscriptionSystemNotifyId){
		int i = 0;
		
		//表编号
		int tableNumber = subscriptionSystemNotifyConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update SubscriptionSystemNotify o set o.status=o.status+?1 where o.id=?2 and o.userId=?3 and o.status <?4")
					.setParameter(1, 100)
					.setParameter(2, subscriptionSystemNotifyId)
					.setParameter(3, userId)
					.setParameter(4, 100);
					i = query.executeUpdate();
			
		}else{//带下划线对象
			Query query = em.createQuery("update SubscriptionSystemNotify_"+tableNumber+" o set o.status=o.status+?1 where o.id=?2 and o.userId=?3 and o.status <?4")
					.setParameter(1, 100)
					.setParameter(2, subscriptionSystemNotifyId)
					.setParameter(3, userId)
					.setParameter(4, 100);
					i = query.executeUpdate();
		}

		return i;
	}
	
	/**
	 * 删除用户所有订阅系统通知
	 * @param userIdList 用户Id集合
	 */
	public Integer deleteUserSubscriptionSystemNotify(List<Long> userIdList){
		int j = 0;
		
		//表编号
		int tableNumber = subscriptionSystemNotifyConfig.getTableQuantity();
		for(int i = 0; i<tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from SubscriptionSystemNotify o where o.userId in(:userId)")
						.setParameter("userId", userIdList);
				j += query.executeUpdate();		
			}else{//带下划线对象
				Query query = em.createQuery("delete from SubscriptionSystemNotify_"+i+" o where o.userId in(:userId)")
						.setParameter("userId", userIdList);
				j += query.executeUpdate();
			}
		}
		
		return j;
	}
	
	/**
	 * 还原被用户删除的订阅系统通知
	 * @param subscriptionSystemNotifyId 订阅系统通知Id
	 */
	public Integer reductionSubscriptionSystemNotify(String subscriptionSystemNotifyId){
		
		//表编号
		int tableNumber = subscriptionSystemNotifyConfig.subscriptionSystemNotifyIdRemainder(subscriptionSystemNotifyId);
		int i = 0;
		Query query = null;
		if(tableNumber == 0){//默认对象
			query = em.createQuery("update SubscriptionSystemNotify o set o.status=o.status-?1 where o.id=?2 and o.status >?3")
			.setParameter(1, 100)
			.setParameter(2, subscriptionSystemNotifyId)
			.setParameter(3, 100);
			i = query.executeUpdate();
		}else{//带下划线对象
			query = em.createQuery("update SubscriptionSystemNotify_"+tableNumber+" o set o.status=o.status-?1 where o.id=?2 and o.status >?3")
			.setParameter(1, 100)
			.setParameter(2, subscriptionSystemNotifyId)
			.setParameter(3, 100);
			i = query.executeUpdate();
		}
		return i;
	}
	
	/**
	 * 保存系统通知
	 * @param systemNotify
	 */
	public void saveSystemNotify(SystemNotify systemNotify){
		this.save(systemNotify);
	}
	
	/**
	 * 修改系统通知
	 * @param systemNotify
	 * @return
	 */
	public Integer updateSystemNotify(SystemNotify systemNotify){
		Query query = em.createQuery("update SystemNotify o set o.staffName=?1,o.content=?2 where o.id=?3")
			.setParameter(1, systemNotify.getStaffName())
			.setParameter(2, systemNotify.getContent())
			.setParameter(3, systemNotify.getId());
			int i = query.executeUpdate();
		return i;
	}
	
	
	
	/**
	 * 删除系统通知
	 * @param systemNotifyId 系统通知Id
	 */
	public Integer deleteSystemNotify(Long systemNotifyId){
		int i = 0;
		Query delete = em.createQuery("delete from SystemNotify o where o.id=?1")
		.setParameter(1,systemNotifyId);
		i = delete.executeUpdate();
		
		if(i >0){
			//删除订阅系统通知
			this.deleteSubscriptionSystemNotify(systemNotifyId);
		}
		
		return i;
	}
	
	/**
	 * /删除订阅系统通知
	 * @param systemNotifyId 系统通知Id
	 * @return
	 */
	private Integer deleteSubscriptionSystemNotify(Long systemNotifyId){
		int j = 0;
		
		//表编号
		int tableNumber = subscriptionSystemNotifyConfig.getTableQuantity();
		for(int i = 0; i<tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from SubscriptionSystemNotify o where o.systemNotifyId=?1")
						.setParameter(1, systemNotifyId);
				j += query.executeUpdate();

			}else{//带下划线对象
				Query query = em.createQuery("delete from SubscriptionSystemNotify_"+i+" o where o.systemNotifyId=?1")
						.setParameter(1, systemNotifyId);
				j += query.executeUpdate();

			}
		}
		

		return j;
		
		
	}
}
