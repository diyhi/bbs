package cms.service.favorite.impl;

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
import cms.bean.favorite.Favorites;
import cms.bean.favorite.TopicFavorite;
import cms.service.besa.DaoSupport;
import cms.service.favorite.FavoriteService;
import cms.web.action.favorite.FavoritesConfig;
import cms.web.action.favorite.TopicFavoriteConfig;
import net.sf.cglib.beans.BeanCopier;

/**
 * 收藏夹
 *
 */
@Service
@Transactional
public class FavoriteServiceBean extends DaoSupport<Favorites> implements FavoriteService{
	private static final Logger logger = LogManager.getLogger(FavoriteServiceBean.class);
	
	@Resource FavoritesConfig favoritesConfig;
	@Resource TopicFavoriteConfig topicFavoriteConfig;
	
	
	/**
	 * 根据Id查询收藏夹
	 * @param favoriteId 收藏夹Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Favorites findById(String favoriteId){
		//表编号
		int tableNumber = favoritesConfig.favoriteIdRemainder(favoriteId);
		
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("select o from Favorites o where o.id=?1")
			.setParameter(1, favoriteId);
			List<Favorites> list = query.getResultList();
			for(Favorites p : list){
				return p;
			}
			
		}else{//带下划线对象
			Query query = em.createQuery("select o from Favorites_"+tableNumber+" o where o.id=?1")
			.setParameter(1, favoriteId);
			List<?> favorites_List= query.getResultList();
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.favorite.Favorites_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Favorites.class, false); 
				
				for(int j = 0;j< favorites_List.size(); j++) {  
					Object obj = favorites_List.get(j);
					Favorites favorites = new Favorites();
					copier.copy(obj,favorites, null);
					return favorites;
				}
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询收藏夹",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询收藏夹",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询收藏夹",e);
		        }
			}
		}
		return null;
	}
	
	/**
	 * 根据用户名称查询收藏夹分页
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<Favorites> findFavoriteByUserId(Long userId,String userName,int firstIndex, int maxResult){
		QueryResult<Favorites> qr = new QueryResult<Favorites>();
		
		//表编号
		int tableNumber = favoritesConfig.userIdRemainder(userId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			
			query = em.createQuery("select o from Favorites o where o.userName=?1 ORDER BY o.addtime desc");
			query.setParameter(1, userName);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<Favorites> favoritesList= query.getResultList();
			qr.setResultlist(favoritesList);
			
			query = em.createQuery("select count(o) from Favorites o where o.userName=?1");
			query.setParameter(1, userName);
			qr.setTotalrecord((Long)query.getSingleResult());
		}else{//带下划线对象
			
			query = em.createQuery("select o from Favorites_"+tableNumber+" o where o.userName=?1 ORDER BY o.addtime desc");
			query.setParameter(1, userName);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> favorites_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.favorite.Favorites_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Favorites.class, false); 
				List<Favorites> favoritesList= new ArrayList<Favorites>();
				for(int j = 0;j< favorites_List.size(); j++) {  
					Object obj = favorites_List.get(j);
					Favorites favorites = new Favorites();
					copier.copy(obj,favorites, null);
					favoritesList.add(favorites);
				}
				qr.setResultlist(favoritesList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户名称查询收藏夹分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户名称查询收藏夹分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户名称查询收藏夹分页",e);
		        }
			}
			
			query = em.createQuery("select count(o) from Favorites_"+tableNumber+" o where o.userName=?1");
			query.setParameter(1, userName);
			qr.setTotalrecord((Long)query.getSingleResult());
			
		}
		
		return qr;
	}
	
	
	/**
	 * 保存收藏
	 * @param favorites 收藏夹
	 * @param topicFavorite 话题收藏
	 */
	public void saveFavorite(Object favorites,Object topicFavorite){
		this.save(favorites);
		this.save(topicFavorite);
	}
	
	/**
	 * 删除收藏
	 * @param favoriteId 收藏夹Id
	 * @param topicFavoriteId 话题收藏Id
	 */
	public Integer deleteFavorite(String favoriteId,String topicFavoriteId){
		//表编号
		int favorites_tableNumber = favoritesConfig.favoriteIdRemainder(favoriteId);
		int i = 0;
		if(favorites_tableNumber == 0){//默认对象
			Query delete = em.createQuery("delete from Favorites o where o.id=?1")
			.setParameter(1,favoriteId);
			i += delete.executeUpdate();
		}else{//带下划线对象
			Query delete = em.createQuery("delete from Favorites_"+favorites_tableNumber+" o where o.id=?1")
					.setParameter(1,favoriteId);
					i += delete.executeUpdate();
		}
		
		
		int topicFavorite_tableNumber = topicFavoriteConfig.topicFavoriteIdRemainder(topicFavoriteId);
		if(topicFavorite_tableNumber == 0){//默认对象
			Query delete = em.createQuery("delete from TopicFavorite o where o.id=?1")
			.setParameter(1,topicFavoriteId);
			i += delete.executeUpdate();
		}else{//带下划线对象
			Query delete = em.createQuery("delete from TopicFavorite_"+topicFavorite_tableNumber+" o where o.id=?1")
					.setParameter(1,topicFavoriteId);
					i += delete.executeUpdate();
		}
		
		
		return i;
	}
	
	/**
	 * 根据话题Id删除收藏
	 * @param topicId 话题Id
	 */
	public Integer deleteFavoriteByTopicId(Long topicId){
		int j = 0;
		
		//表编号
		int favorites_tableNumber = favoritesConfig.getTableQuantity();
		for(int i = 0; i<favorites_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from Favorites o where o.topicId=?1")
						.setParameter(1, topicId);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from Favorites_"+i+" o where o.topicId=?1")
						.setParameter(1, topicId);
				j += query.executeUpdate();
			}
		}
		
		//表编号
		int topicFavorite_tableNumber = topicFavoriteConfig.getTableQuantity();
		for(int i = 0; i<topicFavorite_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from TopicFavorite o where o.topicId=?1")
						.setParameter(1, topicId);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from TopicFavorite_"+i+" o where o.topicId=?1")
						.setParameter(1, topicId);
				j += query.executeUpdate();
			}
		}
		return j;
	}
	
	/**
	 * 根据收藏夹用户名称删除收藏
	 * @param userNameList 用户名称集合
	 */
	public Integer deleteFavoriteByUserName(List<String> userNameList){
		int j = 0;
		//表编号
		int favorites_tableNumber = favoritesConfig.getTableQuantity();
		for(int i = 0; i<favorites_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from Favorites o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from Favorites_"+i+" o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		
		//表编号
		int topicFavorite_tableNumber = topicFavoriteConfig.getTableQuantity();
		for(int i = 0; i<topicFavorite_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from TopicFavorite o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from TopicFavorite_"+i+" o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		
		return j;
	}
	
	/**
	 * 根据发布话题的用户名称删除收藏
	 * @param userNameList 发布话题的用户名称集合
	 */
	public Integer deleteFavoriteByPostUserName(List<String> userNameList){
		int j = 0;
		//表编号
		int favorites_tableNumber = favoritesConfig.getTableQuantity();
		for(int i = 0; i<favorites_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from Favorites o where o.postUserName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from Favorites_"+i+" o where o.postUserName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		
		//表编号
		int topicFavorite_tableNumber = topicFavoriteConfig.getTableQuantity();
		for(int i = 0; i<topicFavorite_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from TopicFavorite o where o.postUserName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from TopicFavorite_"+i+" o where o.postUserName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		
		return j;
	}
	
	
	/**
	 * 根据Id查询话题收藏
	 * @param topicFavoriteId 话题收藏Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public TopicFavorite findTopicFavoriteById(String topicFavoriteId){
		
		//表编号
		int tableNumber = topicFavoriteConfig.topicFavoriteIdRemainder(topicFavoriteId);
		
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("select o from TopicFavorite o where o.id=?1")
			.setParameter(1, topicFavoriteId);
			List<TopicFavorite> list = query.getResultList();
			for(TopicFavorite p : list){
				return p;
			}
			
		}else{//带下划线对象
			Query query = em.createQuery("select o from TopicFavorite_"+tableNumber+" o where o.id=?1")
			.setParameter(1, topicFavoriteId);
			List<?> topicFavorite_List= query.getResultList();
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.favorite.TopicFavorite_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),TopicFavorite.class, false); 
				
				for(int j = 0;j< topicFavorite_List.size(); j++) {  
					Object obj = topicFavorite_List.get(j);
					TopicFavorite topicFavorite = new TopicFavorite();
					copier.copy(obj,topicFavorite, null);
					return topicFavorite;
				}
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询话题收藏",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询话题收藏",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询话题收藏",e);
		        }
			}
		}
		return null;
	}
	
	
	/**
	 * 根据话题Id查询收藏夹分页
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param topicId 话题Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<Favorites> findFavoritePageByTopicId(int firstIndex, int maxResult,Long topicId){
		QueryResult<Favorites> qr = new QueryResult<Favorites>();
		Query query  = null;
		
		//表编号
		int tableNumber = topicFavoriteConfig.topicIdRemainder(topicId);
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select o from TopicFavorite o where o.topicId=?1 ORDER BY o.addtime desc");
			query.setParameter(1, topicId);

			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<TopicFavorite> topicFavoriteList= query.getResultList();
			List<Favorites> favoritesList= new ArrayList<Favorites>();
			if(topicFavoriteList != null && topicFavoriteList.size() >0){
				BeanCopier copier = BeanCopier.create(TopicFavorite.class,Favorites.class, false); 
				for(TopicFavorite topicFavorite : topicFavoriteList){
					Favorites favorites = new Favorites();
					copier.copy(topicFavorite,favorites, null);
					favoritesList.add(favorites);
				}
			}
			qr.setResultlist(favoritesList);
			
			query = em.createQuery("select count(o) from TopicFavorite o where o.topicId=?1");
			query.setParameter(1, topicId);
			qr.setTotalrecord((Long)query.getSingleResult());
			
		}else{//带下划线对象
			
			query = em.createQuery("select o from TopicFavorite_"+tableNumber+" o where o.topicId=?1 ORDER BY o.addtime desc");
			query.setParameter(1, topicId);

			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> topicFavorite_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.favorite.TopicFavorite_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Favorites.class, false); 
				List<Favorites> favoritesList= new ArrayList<Favorites>();
				for(int j = 0;j< topicFavorite_List.size(); j++) {  
					Object obj = topicFavorite_List.get(j);
					Favorites favorites = new Favorites();
					copier.copy(obj,favorites, null);
					favoritesList.add(favorites);
				}
				qr.setResultlist(favoritesList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据话题Id查询收藏夹分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据话题Id查询收藏夹分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据话题Id查询收藏夹分页",e);
		        }
			}
			
			
			
			query = em.createQuery("select count(o) from TopicFavorite_"+tableNumber+" o where o.topicId=?1");
			query.setParameter(1, topicId);
			qr.setTotalrecord((Long)query.getSingleResult());
		}
		return qr;
	}
	
	
	/**
	 * 根据话题Id查询被收藏数量
	 * @param topicId 话题Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findFavoriteCountByTopicId(Long topicId){
		Long count = 0L;
		//表编号
		int tableNumber = topicFavoriteConfig.topicIdRemainder(topicId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select count(o) from TopicFavorite o where o.topicId=?1");
			query.setParameter(1, topicId);
			count = (Long)query.getSingleResult();
			
		}else{//带下划线对象
			query = em.createQuery("select count(o) from TopicFavorite_"+tableNumber+" o where o.topicId=?1");
			query.setParameter(1, topicId);
			count = (Long)query.getSingleResult();
		}
		return count;
	}
	
	
}
