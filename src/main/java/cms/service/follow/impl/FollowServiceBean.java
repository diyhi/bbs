package cms.service.follow.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.QueryResult;
import cms.bean.follow.Follow;
import cms.bean.follow.Follower;
import cms.service.besa.DaoSupport;
import cms.service.follow.FollowService;
import cms.web.action.follow.FollowConfig;
import cms.web.action.follow.FollowerConfig;
import net.sf.cglib.beans.BeanCopier;

/**
 * 关注
 *
 */
@Service
@Transactional
public class FollowServiceBean extends DaoSupport<Follow> implements FollowService{
	private static final Logger logger = LogManager.getLogger(FollowServiceBean.class);
	
	@Resource FollowConfig followConfig;
	@Resource FollowerConfig followerConfig;
	
	
	/**
	 * 根据Id查询关注
	 * @param followId 关注Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Follow findById(String followId){
		//表编号
		int tableNumber = followConfig.followIdRemainder(followId);
		
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("select o from Follow o where o.id=?1")
			.setParameter(1, followId);
			List<Follow> list = query.getResultList();
			for(Follow p : list){
				return p;
			}
			
		}else{//带下划线对象
			Query query = em.createQuery("select o from Follow_"+tableNumber+" o where o.id=?1")
			.setParameter(1, followId);
			List<?> follow_List= query.getResultList();
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.follow.Follow_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Follow.class, false); 
				
				for(int j = 0;j< follow_List.size(); j++) {  
					Object obj = follow_List.get(j);
					Follow follow = new Follow();
					copier.copy(obj,follow, null);
					return follow;
				}
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询关注",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询关注",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询关注",e);
		        }
			}
		}
		return null;
	}
	
	/**
	 * 根据用户名称查询所有关注
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<Follow> findAllFollow(Long userId,String userName){
		//表编号
		int tableNumber = followConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select o from Follow o where o.userName=?1");
			query.setParameter(1, userName);
			List<Follow> followList= query.getResultList();
			return followList;
		}else{//带下划线对象
			
			query = em.createQuery("select o from Follow_"+tableNumber+" o where o.userName=?1");
			query.setParameter(1, userName);
			List<?> follow_List= query.getResultList();
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.follow.Follow_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Follow.class, false); 
				List<Follow> followList= new ArrayList<Follow>();
				for(int j = 0;j< follow_List.size(); j++) {  
					Object obj = follow_List.get(j);
					Follow follow = new Follow();
					copier.copy(obj,follow, null);
					followList.add(follow);
				}
				return followList;
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户名称查询所有关注",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户名称查询所有关注",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户名称查询所有关注",e);
		        }
			}

			
		}
		
		return null;
		
	}
	
	/**
	 * 根据用户名称查询关注分页
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<Follow> findFollowByUserName(Long userId,String userName,int firstIndex, int maxResult){
		QueryResult<Follow> qr = new QueryResult<Follow>();
		
		//表编号
		int tableNumber = followConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			
			query = em.createQuery("select o from Follow o where o.userName=?1 ORDER BY o.addtime desc");
			query.setParameter(1, userName);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<Follow> followList= query.getResultList();
			qr.setResultlist(followList);
			
			query = em.createQuery("select count(o) from Follow o where o.userName=?1");
			query.setParameter(1, userName);
			qr.setTotalrecord((Long)query.getSingleResult());
		}else{//带下划线对象
			
			query = em.createQuery("select o from Follow_"+tableNumber+" o where o.userName=?1 ORDER BY o.addtime desc");
			query.setParameter(1, userName);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> follow_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.follow.Follow_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Follow.class, false); 
				List<Follow> followList= new ArrayList<Follow>();
				for(int j = 0;j< follow_List.size(); j++) {  
					Object obj = follow_List.get(j);
					Follow follow = new Follow();
					copier.copy(obj,follow, null);
					followList.add(follow);
				}
				qr.setResultlist(followList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户名称查询关注分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户名称查询关注分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户名称查询关注分页",e);
		        }
			}
			
			query = em.createQuery("select count(o) from Follow_"+tableNumber+" o where o.userName=?1");
			query.setParameter(1, userName);
			qr.setTotalrecord((Long)query.getSingleResult());
			
		}
		
		return qr;
	}
	
	/**
	 * 根据用户名称查询粉丝分页
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<Follower> findFollowerByUserName(Long userId,String userName,int firstIndex, int maxResult){
		QueryResult<Follower> qr = new QueryResult<Follower>();
		
		//表编号
		int tableNumber = followConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			
			query = em.createQuery("select o from Follower o where o.userName=?1 ORDER BY o.addtime desc");
			query.setParameter(1, userName);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<Follower> followerList= query.getResultList();
			qr.setResultlist(followerList);
			
			query = em.createQuery("select count(o) from Follower o where o.userName=?1");
			query.setParameter(1, userName);
			qr.setTotalrecord((Long)query.getSingleResult());
		}else{//带下划线对象
			
			query = em.createQuery("select o from Follower_"+tableNumber+" o where o.userName=?1 ORDER BY o.addtime desc");
			query.setParameter(1, userName);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> follower_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.follow.Follower_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Follower.class, false); 
				List<Follower> followerList= new ArrayList<Follower>();
				for(int j = 0;j< follower_List.size(); j++) {  
					Object obj = follower_List.get(j);
					Follower follower = new Follower();
					copier.copy(obj,follower, null);
					followerList.add(follower);
				}
				qr.setResultlist(followerList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户名称查询粉丝分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户名称查询粉丝分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户名称查询粉丝分页",e);
		        }
			}
			
			query = em.createQuery("select count(o) from Follower_"+tableNumber+" o where o.userName=?1");
			query.setParameter(1, userName);
			qr.setTotalrecord((Long)query.getSingleResult());
			
		}
		
		return qr;
	}
	
	
	/**
	 * 保存关注
	 * @param follow 关注
	 * @param follower 粉丝
	 */
	public void saveFollow(Object follow,Object follower){
		this.save(follow);
		this.save(follower);
	}
	
	/**
	 * 删除关注
	 * @param followId 关注Id
	 * @param followerId 粉丝Id
	 */
	public Integer deleteFollow(String followId,String followerId){
		//表编号
		int follow_tableNumber = followConfig.followIdRemainder(followId);
		int i = 0;
		if(follow_tableNumber == 0){//默认对象
			Query delete = em.createQuery("delete from Follow o where o.id=?1")
			.setParameter(1,followId);
			i += delete.executeUpdate();
		}else{//带下划线对象
			Query delete = em.createQuery("delete from Follow_"+follow_tableNumber+" o where o.id=?1")
					.setParameter(1,followId);
					i += delete.executeUpdate();
		}
		
		
		int follower_tableNumber = followerConfig.followerIdRemainder(followerId);
		if(follower_tableNumber == 0){//默认对象
			Query delete = em.createQuery("delete from Follower o where o.id=?1")
			.setParameter(1,followerId);
			i += delete.executeUpdate();
		}else{//带下划线对象
			Query delete = em.createQuery("delete from Follower_"+follower_tableNumber+" o where o.id=?1")
					.setParameter(1,followerId);
					i += delete.executeUpdate();
		}
		
		
		return i;
	}
	
	/**
	 * 根据用户名称删除关注
	 * @param userNameList 用户名称集合
	 */
	public Integer deleteFollowByUserName(List<String> userNameList){
		int j = 0;
		//表编号
		int follow_tableNumber = followConfig.getTableQuantity();
		for(int i = 0; i<follow_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from Follow o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from Follow_"+i+" o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		
		//表编号
		int follower_tableNumber = followerConfig.getTableQuantity();
		for(int i = 0; i<follower_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from Follower o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from Follower_"+i+" o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		
		return j;
	}
	

	/**
	 * 根据用户名称查询粉丝数量
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findFollowerCountByUserName(Long userId,String userName){
		Long count = 0L;
		//表编号
		int tableNumber = followerConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select count(o) from Follower o where o.userName=?1");
			query.setParameter(1, userName);
			count = (Long)query.getSingleResult();
			
		}else{//带下划线对象
			query = em.createQuery("select count(o) from Follower_"+tableNumber+" o where o.userName=?1");
			query.setParameter(1, userName);
			count = (Long)query.getSingleResult();
		}
		return count;
	}
	
	/**
	 * 根据用户名称查询关注数量
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findFollowCountByUserName(Long userId,String userName){
		Long count = 0L;
		//表编号
		int tableNumber = followConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select count(o) from Follow o where o.userName=?1");
			query.setParameter(1, userName);
			count = (Long)query.getSingleResult();
			
		}else{//带下划线对象
			query = em.createQuery("select count(o) from Follow_"+tableNumber+" o where o.userName=?1");
			query.setParameter(1, userName);
			count = (Long)query.getSingleResult();
		}
		return count;
	}
}
