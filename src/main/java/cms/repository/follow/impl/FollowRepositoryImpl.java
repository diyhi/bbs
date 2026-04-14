package cms.repository.follow.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


import cms.component.follow.FollowConfig;
import cms.component.follow.FollowerConfig;
import cms.dto.QueryResult;
import cms.model.follow.Follow;
import cms.model.follow.Follower;
import cms.repository.besa.DaoSupport;
import cms.repository.follow.FollowRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.cglib.beans.BeanCopier;

/**
 * 关注管理接口实现类
 *
 */
@Repository
@Transactional
public class FollowRepositoryImpl extends DaoSupport<Follow> implements FollowRepository {
	private static final Logger logger = LogManager.getLogger(FollowRepositoryImpl.class);
	
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
				Class<?> c = Class.forName("cms.model.follow.Follow_"+tableNumber);
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
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
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("根据Id查询关注",e);
                }
            } catch (NoSuchMethodException e) {
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
				Class<?> c = Class.forName("cms.model.follow.Follow_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
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
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("根据用户名称查询所有关注",e);
                }
            } catch (NoSuchMethodException e) {
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
				Class<?> c = Class.forName("cms.model.follow.Follow_"+tableNumber);
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
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
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("根据用户名称查询关注分页",e);
                }
            } catch (NoSuchMethodException e) {
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
	public QueryResult<Follower> findFollowerByUserName(Long userId, String userName, int firstIndex, int maxResult){
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
				Class<?> c = Class.forName("cms.model.follow.Follower_"+tableNumber);
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
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
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("根据用户名称查询粉丝分页",e);
                }
            } catch (NoSuchMethodException e) {
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
