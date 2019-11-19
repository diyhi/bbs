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
import cms.bean.message.Remind;
import cms.service.besa.DaoSupport;
import cms.service.message.RemindService;
import cms.web.action.message.RemindConfig;
import net.sf.cglib.beans.BeanCopier;

/**
 * 提醒通知
 *
 */
@Service
@Transactional
public class RemindServiceBean extends DaoSupport<Remind> implements RemindService{
	private static final Logger logger = LogManager.getLogger(RemindServiceBean.class);
	 
	
	@Resource RemindConfig remindConfig;
	
	
	/**
	 * 保存提醒
	 * @param remind
	 */
	public void saveRemind(Object remind){
		this.save(remind);
	}
	
	/**
	 * 设置提醒设状态为已读
	 * @param userId 用户Id
	 * @param unreadRemindIdList 提醒设Id集合
	 */
	public Integer updateRemindStatus(Long userId,List<String> unreadRemindIdList){
		int i = 0;
		Long time = new Date().getTime();
		
		//表编号
		int tableNumber = remindConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update Remind o set o.status=:status, o.readTimeFormat=:readTimeFormat where o.id in(:id) and o.status <:status2")
					.setParameter("status", 20)
					.setParameter("readTimeFormat", time)
					.setParameter("id", unreadRemindIdList)
					.setParameter("status2", 20);
					i += query.executeUpdate();
			
		}else{//带下划线对象
			Query query = em.createQuery("update Remind_"+tableNumber+" o set o.status=:status, o.readTimeFormat=:readTimeFormat where o.id in(:id) and o.status <:status2")
					.setParameter("status", 20)
					.setParameter("readTimeFormat", time)
					.setParameter("id", unreadRemindIdList)
					.setParameter("status2", 20);
					i += query.executeUpdate();
		}

		return i;
		
		
	}
	/**
	 * 软删除提醒
	 * @param userId 用户Id
	 * @param remindId 提醒Id
	 */
	public Integer softDeleteRemind(Long userId,String remindId){
		int i = 0;
		
		//表编号
		int tableNumber = remindConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update Remind o set o.status=o.status+?1 where o.id=?2 and o.receiverUserId=?3 and o.status <?4")
					.setParameter(1, 100)
					.setParameter(2, remindId)
					.setParameter(3, userId)
					.setParameter(4, 100);
					i = query.executeUpdate();
			
		}else{//带下划线对象
			Query query = em.createQuery("update Remind_"+tableNumber+" o set o.status=o.status+?1 where o.id=?2 and o.receiverUserId=?3 and o.status <?4")
					.setParameter(1, 100)
					.setParameter(2, remindId)
					.setParameter(3, userId)
					.setParameter(4, 100);
					i = query.executeUpdate();
		}

		return i;
	}
	
	
	/**
	 * 根据用户Id集合删除提醒
	 * @param userIdList 用户Id集合
	 */
	public Integer deleteRemindByUserId(List<Long> userIdList){
		int j = 0;
		
		//表编号
		int tableNumber = remindConfig.getTableQuantity();
		for(int i = 0; i<tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from Remind o where o.receiverUserId in(:userId)")
						.setParameter("userId", userIdList);
				j += query.executeUpdate();		
			}else{//带下划线对象
				Query query = em.createQuery("delete from Remind_"+i+" o where o.receiverUserId in(:userId)")
						.setParameter("userId", userIdList);
				j += query.executeUpdate();
			}
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from Remind o where o.senderUserId in(:userId)")
						.setParameter("userId", userIdList);
				j += query.executeUpdate();		
			}else{//带下划线对象
				Query query = em.createQuery("delete from Remind_"+i+" o where o.senderUserId in(:userId)")
						.setParameter("userId", userIdList);
				j += query.executeUpdate();
			}
		}
		
		return j;
	}
	
	/**
	 * 还原被用户删除的提醒
	 * @param remindId 提醒Id
	 */
	public Integer reductionRemind(String remindId){
		
		//表编号
		int tableNumber = remindConfig.remindIdRemainder(remindId);
		int i = 0;
		Query query = null;
		if(tableNumber == 0){//默认对象
			query = em.createQuery("update Remind o set o.status=o.status-?1 where o.id=?2 and o.status >?3")
			.setParameter(1, 100)
			.setParameter(2, remindId)
			.setParameter(3, 100);
			i = query.executeUpdate();
		}else{//带下划线对象
			query = em.createQuery("update Remind_"+tableNumber+" o set o.status=o.status-?1 where o.id=?2 and o.status >?3")
			.setParameter(1, 100)
			.setParameter(2, remindId)
			.setParameter(3, 100);
			i = query.executeUpdate();
		}
		return i;
	}
	
	/**
	 * 根据话题Id删除提醒
	 * @param topicId 话题Id
	 * @return
	 */
	public Integer deleteRemindByTopicId(Long topicId){
		int j = 0;
		
		//表编号
		int tableNumber = remindConfig.getTableQuantity();
		for(int i = 0; i<tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from Remind o where o.topicId=?1")
						.setParameter(1, topicId);
				j += query.executeUpdate();

			}else{//带下划线对象
				Query query = em.createQuery("delete from Remind_"+i+" o where o.topicId=?1")
						.setParameter(1, topicId);
				j += query.executeUpdate();

			}
		}
		

		return j;
		
		
	}
	
	/**
	 * 根据问题Id删除提醒
	 * @param questionId 问题Id
	 * @return
	 */
	public Integer deleteRemindByQuestionId(Long questionId){
		int j = 0;
		
		//表编号
		int tableNumber = remindConfig.getTableQuantity();
		for(int i = 0; i<tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from Remind o where o.questionId=?1")
						.setParameter(1, questionId);
				j += query.executeUpdate();

			}else{//带下划线对象
				Query query = em.createQuery("delete from Remind_"+i+" o where o.questionId=?1")
						.setParameter(1, questionId);
				j += query.executeUpdate();

			}
		}
		

		return j;
		
		
	}
	
	/**
	 * 根据用户Id查询提醒分页
	 * @param userId 用户Id
	 * @param status 状态
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<Remind> findRemindByUserId(Long userId,Integer status,int firstIndex, int maxResult){
		QueryResult<Remind> qr = new QueryResult<Remind>();
		
		String status_sql = "";
		if(status != null){
			status_sql = " and o.status <?2 ";
		}
		
		//表编号
		int tableNumber = remindConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			
			query = em.createQuery("select o from Remind o where o.receiverUserId=?1 "+status_sql+" ORDER BY o.sendTimeFormat desc");
			query.setParameter(1, userId);
			if(status != null){
				query.setParameter(2, status);
			}
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<Remind> remindList= query.getResultList();
			qr.setResultlist(remindList);
			
			query = em.createQuery("select count(o) from Remind o where o.receiverUserId=?1 "+status_sql+"");
			query.setParameter(1, userId);
			if(status != null){
				query.setParameter(2, status);
			}
			qr.setTotalrecord((Long)query.getSingleResult());
		}else{//带下划线对象
			
			query = em.createQuery("select o from Remind_"+tableNumber+" o where o.receiverUserId=?1 "+status_sql+" ORDER BY o.sendTimeFormat desc");
			query.setParameter(1, userId);
			if(status != null){
				query.setParameter(2, status);
			}
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> remind_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.message.Remind_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Remind.class, false); 
				List<Remind> remindList= new ArrayList<Remind>();
				for(int j = 0;j< remind_List.size(); j++) {  
					Object obj = remind_List.get(j);
					Remind remind = new Remind();
					copier.copy(obj,remind, null);
					remindList.add(remind);
				}
				qr.setResultlist(remindList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户Id查询提醒分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户Id查询提醒分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户Id查询提醒分页",e);
		        }
			}
			
			query = em.createQuery("select count(o) from Remind_"+tableNumber+" o where o.receiverUserId=?1 "+status_sql+"");
			query.setParameter(1, userId);
			if(status != null){
				query.setParameter(2, status);
			}
			qr.setTotalrecord((Long)query.getSingleResult());
			
		}
		
		return qr;
	}
	
	/**
	 * 根据用户Id和提醒类型代码编号查询最新一条提醒
	 * @param userId 用户Id
	 * @param typeCode 提醒类型代码编号
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Remind findNewRemindByUserId(Long userId,Integer typeCode){
		//表编号
		int tableNumber = remindConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select o from Remind o where o.receiverUserId=?1 and o.typeCode=?2 ORDER BY o.sendTimeFormat desc");
			query.setParameter(1, userId);
			query.setParameter(2, typeCode);
			//索引开始,即从哪条记录开始
			query.setFirstResult(0);
			//获取多少条数据
			query.setMaxResults(1);
			List<Remind> remindList= query.getResultList();
			if(remindList != null && remindList.size() >0){
				for(Remind remind : remindList){
					return remind;
				}
			}

		}else{//带下划线对象
			
			query = em.createQuery("select o from Remind_"+tableNumber+" o where o.receiverUserId=?1 and o.typeCode=?2 ORDER BY o.sendTimeFormat desc");
			query.setParameter(1, userId);
			query.setParameter(2, typeCode);
			//索引开始,即从哪条记录开始
			query.setFirstResult(0);
			//获取多少条数据
			query.setMaxResults(1);
			List<?> remind_List= query.getResultList();

			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.message.Remind_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Remind.class, false); 
				for(int j = 0;j< remind_List.size(); j++) {  
					Object obj = remind_List.get(j);
					Remind remind = new Remind();
					copier.copy(obj,remind, null);
					
					return remind;
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户Id和提醒类型代码编号查询最新一条提醒",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户Id和提醒类型代码编号查询最新一条提醒",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户Id和提醒类型代码编号查询最新一条提醒",e);
		        }
			}

			
		}
		
		return null;
	}
	
	/**
	 * 根据用户Id查询未读提醒数量
	 * @param userId 用户Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findUnreadRemindByUserId(Long userId){
		Long count = 0L;
		//表编号
		int tableNumber = remindConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select count(o) from Remind o where o.receiverUserId=?1 and o.status =?2 ");
			query.setParameter(1, userId);
			query.setParameter(2, 10);
			count = (Long)query.getSingleResult();
			
		}else{//带下划线对象
			query = em.createQuery("select count(o) from Remind_"+tableNumber+" o where o.receiverUserId=?1 and o.status =?2 ");
			query.setParameter(1, userId);
			query.setParameter(2, 10);
			count = (Long)query.getSingleResult();
		}
		return count;
	}
}
