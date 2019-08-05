package cms.service.like.impl;

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
import cms.bean.like.Like;
import cms.bean.like.TopicLike;
import cms.service.besa.DaoSupport;
import cms.service.like.LikeService;
import cms.web.action.like.LikeConfig;
import cms.web.action.like.TopicLikeConfig;
import net.sf.cglib.beans.BeanCopier;

/**
 * 点赞
 *
 */
@Service
@Transactional
public class LikeServiceBean extends DaoSupport<Like> implements LikeService{
	private static final Logger logger = LogManager.getLogger(LikeServiceBean.class);
	
	@Resource LikeConfig likeConfig;
	@Resource TopicLikeConfig topicLikeConfig;
	
	
	/**
	 * 根据Id查询点赞
	 * @param likeId 点赞Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Like findById(String likeId){
		//表编号
		int tableNumber = likeConfig.likeIdRemainder(likeId);
		
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("select o from Like o where o.id=?1")
			.setParameter(1, likeId);
			List<Like> list = query.getResultList();
			for(Like p : list){
				return p;
			}
			
		}else{//带下划线对象
			Query query = em.createQuery("select o from Like_"+tableNumber+" o where o.id=?1")
			.setParameter(1, likeId);
			List<?> like_List= query.getResultList();
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.like.Like_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Like.class, false); 
				
				for(int j = 0;j< like_List.size(); j++) {  
					Object obj = like_List.get(j);
					Like like = new Like();
					copier.copy(obj,like, null);
					return like;
				}
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询点赞",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询点赞",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询点赞",e);
		        }
			}
		}
		return null;
	}
	
	/**
	 * 根据用户名称查询点赞分页
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<Like> findLikeByUserId(Long userId,String userName,int firstIndex, int maxResult){
		QueryResult<Like> qr = new QueryResult<Like>();
		
		//表编号
		int tableNumber = likeConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			
			query = em.createQuery("select o from Like o where o.userName=?1 ORDER BY o.addtime desc");
			query.setParameter(1, userName);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<Like> likeList= query.getResultList();
			qr.setResultlist(likeList);
			
			query = em.createQuery("select count(o) from Like o where o.userName=?1");
			query.setParameter(1, userName);
			qr.setTotalrecord((Long)query.getSingleResult());
		}else{//带下划线对象
			
			query = em.createQuery("select o from Like_"+tableNumber+" o where o.userName=?1 ORDER BY o.addtime desc");
			query.setParameter(1, userName);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> like_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.like.Like_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Like.class, false); 
				List<Like> likeList= new ArrayList<Like>();
				for(int j = 0;j< like_List.size(); j++) {  
					Object obj = like_List.get(j);
					Like like = new Like();
					copier.copy(obj,like, null);
					likeList.add(like);
				}
				qr.setResultlist(likeList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户名称查询点赞分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户名称查询点赞分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户名称查询点赞分页",e);
		        }
			}
			
			query = em.createQuery("select count(o) from Like_"+tableNumber+" o where o.userName=?1");
			query.setParameter(1, userName);
			qr.setTotalrecord((Long)query.getSingleResult());
			
		}
		
		return qr;
	}
	
	
	/**
	 * 保存点赞
	 * @param like 点赞
	 * @param topicLike 话题点赞
	 */
	public void saveLike(Object like,Object topicLike){
		this.save(like);
		this.save(topicLike);
	}
	
	/**
	 * 删除点赞
	 * @param likeId 点赞Id
	 * @param topicLikeId 话题点赞Id
	 */
	public Integer deleteLike(String likeId,String topicLikeId){
		//表编号
		int like_tableNumber = likeConfig.likeIdRemainder(likeId);
		int i = 0;
		if(like_tableNumber == 0){//默认对象
			Query delete = em.createQuery("delete from Like o where o.id=?1")
			.setParameter(1,likeId);
			i += delete.executeUpdate();
		}else{//带下划线对象
			Query delete = em.createQuery("delete from Like_"+like_tableNumber+" o where o.id=?1")
					.setParameter(1,likeId);
					i += delete.executeUpdate();
		}
		
		
		int topicLike_tableNumber = topicLikeConfig.topicLikeIdRemainder(topicLikeId);
		if(topicLike_tableNumber == 0){//默认对象
			Query delete = em.createQuery("delete from TopicLike o where o.id=?1")
			.setParameter(1,topicLikeId);
			i += delete.executeUpdate();
		}else{//带下划线对象
			Query delete = em.createQuery("delete from TopicLike_"+topicLike_tableNumber+" o where o.id=?1")
					.setParameter(1,topicLikeId);
					i += delete.executeUpdate();
		}
		
		
		return i;
	}
	
	/**
	 * 根据话题Id删除点赞
	 * @param topicId 话题Id
	 */
	public Integer deleteLikeByTopicId(Long topicId){
		int j = 0;
		
		//表编号
		int like_tableNumber = likeConfig.getTableQuantity();
		for(int i = 0; i<like_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from Like o where o.topicId=?1")
						.setParameter(1, topicId);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from Like_"+i+" o where o.topicId=?1")
						.setParameter(1, topicId);
				j += query.executeUpdate();
			}
		}
		
		//表编号
		int topicLike_tableNumber = topicLikeConfig.getTableQuantity();
		for(int i = 0; i<topicLike_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from TopicLike o where o.topicId=?1")
						.setParameter(1, topicId);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from TopicLike_"+i+" o where o.topicId=?1")
						.setParameter(1, topicId);
				j += query.executeUpdate();
			}
		}
		return j;
	}
	
	/**
	 * 根据点赞用户名称删除点赞
	 * @param userNameList 用户名称集合
	 */
	public Integer deleteLikeByUserName(List<String> userNameList){
		int j = 0;
		//表编号
		int like_tableNumber = likeConfig.getTableQuantity();
		for(int i = 0; i<like_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from Like o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from Like_"+i+" o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		
		//表编号
		int topicLike_tableNumber = topicLikeConfig.getTableQuantity();
		for(int i = 0; i<topicLike_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from TopicLike o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from TopicLike_"+i+" o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		
		return j;
	}
	
	/**
	 * 根据发布话题的用户名称删除点赞
	 * @param userNameList 发布话题的用户名称集合
	 */
	public Integer deleteLikeByPostUserName(List<String> userNameList){
		int j = 0;
		//表编号
		int like_tableNumber = likeConfig.getTableQuantity();
		for(int i = 0; i<like_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from Like o where o.postUserName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from Like_"+i+" o where o.postUserName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		
		//表编号
		int topicLike_tableNumber = topicLikeConfig.getTableQuantity();
		for(int i = 0; i<topicLike_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from TopicLike o where o.postUserName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from TopicLike_"+i+" o where o.postUserName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		
		return j;
	}
	
	
	/**
	 * 根据Id查询话题点赞
	 * @param topicLikeId 话题点赞Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public TopicLike findTopicLikeById(String topicLikeId){
		
		//表编号
		int tableNumber = topicLikeConfig.topicLikeIdRemainder(topicLikeId);
		
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("select o from TopicLike o where o.id=?1")
			.setParameter(1, topicLikeId);
			List<TopicLike> list = query.getResultList();
			for(TopicLike p : list){
				return p;
			}
			
		}else{//带下划线对象
			Query query = em.createQuery("select o from TopicLike_"+tableNumber+" o where o.id=?1")
			.setParameter(1, topicLikeId);
			List<?> topicLike_List= query.getResultList();
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.like.TopicLike_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),TopicLike.class, false); 
				
				for(int j = 0;j< topicLike_List.size(); j++) {  
					Object obj = topicLike_List.get(j);
					TopicLike topicLike = new TopicLike();
					copier.copy(obj,topicLike, null);
					return topicLike;
				}
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询话题点赞",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询话题点赞",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询话题点赞",e);
		        }
			}
		}
		return null;
	}
	
	
	/**
	 * 根据话题Id查询点赞分页
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param topicId 话题Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<Like> findLikePageByTopicId(int firstIndex, int maxResult,Long topicId){
		QueryResult<Like> qr = new QueryResult<Like>();
		Query query  = null;
		
		//表编号
		int tableNumber = topicLikeConfig.topicIdRemainder(topicId);
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select o from TopicLike o where o.topicId=?1 ORDER BY o.addtime desc");
			query.setParameter(1, topicId);

			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<TopicLike> topicLikeList= query.getResultList();
			List<Like> likeList= new ArrayList<Like>();
			if(topicLikeList != null && topicLikeList.size() >0){
				BeanCopier copier = BeanCopier.create(TopicLike.class,Like.class, false); 
				for(TopicLike topicLike : topicLikeList){
					Like like = new Like();
					copier.copy(topicLike,like, null);
					likeList.add(like);
				}
			}
			qr.setResultlist(likeList);
			
			query = em.createQuery("select count(o) from TopicLike o where o.topicId=?1");
			query.setParameter(1, topicId);
			qr.setTotalrecord((Long)query.getSingleResult());
			
		}else{//带下划线对象
			
			query = em.createQuery("select o from TopicLike_"+tableNumber+" o where o.topicId=?1 ORDER BY o.addtime desc");
			query.setParameter(1, topicId);

			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> topicLike_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.like.TopicLike_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Like.class, false); 
				List<Like> likeList= new ArrayList<Like>();
				for(int j = 0;j< topicLike_List.size(); j++) {  
					Object obj = topicLike_List.get(j);
					Like like = new Like();
					copier.copy(obj,like, null);
					likeList.add(like);
				}
				qr.setResultlist(likeList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据话题Id查询点赞分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据话题Id查询点赞分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据话题Id查询点赞分页",e);
		        }
			}
			
			
			
			query = em.createQuery("select count(o) from TopicLike_"+tableNumber+" o where o.topicId=?1");
			query.setParameter(1, topicId);
			qr.setTotalrecord((Long)query.getSingleResult());
		}
		return qr;
	}
	
	
	/**
	 * 根据话题Id查询被点赞数量
	 * @param topicId 话题Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findLikeCountByTopicId(Long topicId){
		Long count = 0L;
		//表编号
		int tableNumber = topicLikeConfig.topicIdRemainder(topicId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select count(o) from TopicLike o where o.topicId=?1");
			query.setParameter(1, topicId);
			count = (Long)query.getSingleResult();
			
		}else{//带下划线对象
			query = em.createQuery("select count(o) from TopicLike_"+tableNumber+" o where o.topicId=?1");
			query.setParameter(1, topicId);
			count = (Long)query.getSingleResult();
		}
		return count;
	}
	
	
}
