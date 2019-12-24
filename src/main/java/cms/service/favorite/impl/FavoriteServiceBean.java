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
import cms.bean.favorite.QuestionFavorite;
import cms.bean.favorite.TopicFavorite;
import cms.service.besa.DaoSupport;
import cms.service.favorite.FavoriteService;
import cms.web.action.favorite.FavoritesConfig;
import cms.web.action.favorite.QuestionFavoriteConfig;
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
	@Resource QuestionFavoriteConfig questionFavoriteConfig;
	
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
	 * @param questionFavorite 问题收藏
	 */
	public void saveFavorite(Object favorites,Object topicFavorite,Object questionFavorite){
		this.save(favorites);
		if(topicFavorite != null){
			this.save(topicFavorite);
		}
		if(questionFavorite != null){
			this.save(questionFavorite);
		}
	}
	
	/**
	 * 删除收藏
	 * @param favoriteId 收藏夹Id
	 * @param topicFavoriteId 话题收藏Id
	 * @param questionFavoriteId 问题收藏Id
	 */
	public Integer deleteFavorite(String favoriteId,String topicFavoriteId,String questionFavoriteId){
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
		
		if(topicFavoriteId != null){
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
		}
		if(questionFavoriteId != null){
			int questionFavorite_tableNumber = questionFavoriteConfig.questionFavoriteIdRemainder(questionFavoriteId);
			if(questionFavorite_tableNumber == 0){//默认对象
				Query delete = em.createQuery("delete from QuestionFavorite o where o.id=?1")
				.setParameter(1,questionFavoriteId);
				i += delete.executeUpdate();
			}else{//带下划线对象
				Query delete = em.createQuery("delete from QuestionFavorite_"+questionFavorite_tableNumber+" o where o.id=?1")
						.setParameter(1,questionFavoriteId);
						i += delete.executeUpdate();
			}
		}
		
		
		return i;
	}
	
	/**------------------------------------------- 话题 ------------------------------------------------**/
	
	
	
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
		
		//表编号
		int questionFavorite_tableNumber = questionFavoriteConfig.getTableQuantity();
		for(int i = 0; i<questionFavorite_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from QuestionFavorite o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from QuestionFavorite_"+i+" o where o.userName in(:userName)")
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
		
		//表编号
		int questionFavorite_tableNumber = questionFavoriteConfig.getTableQuantity();
		for(int i = 0; i<questionFavorite_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from QuestionFavorite o where o.postUserName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from QuestionFavorite_"+i+" o where o.postUserName in(:userName)")
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
	
	
	
	/**------------------------------------------- 问题 ------------------------------------------------**/
	
	/**
	 * 根据问题Id删除收藏
	 * @param questionId 问题Id
	 */
	public Integer deleteFavoriteByQuestionId(Long questionId){
		int j = 0;
		
		//表编号
		int favorites_tableNumber = favoritesConfig.getTableQuantity();
		for(int i = 0; i<favorites_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from Favorites o where o.questionId=?1")
						.setParameter(1, questionId);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from Favorites_"+i+" o where o.questionId=?1")
						.setParameter(1, questionId);
				j += query.executeUpdate();
			}
		}
		
		//表编号
		int questionFavorite_tableNumber = questionFavoriteConfig.getTableQuantity();
		for(int i = 0; i<questionFavorite_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from QuestionFavorite o where o.questionId=?1")
						.setParameter(1, questionId);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from QuestionFavorite_"+i+" o where o.questionId=?1")
						.setParameter(1, questionId);
				j += query.executeUpdate();
			}
		}
		return j;
	}
	
	
	/**
	 * 根据Id查询问题收藏
	 * @param questionFavoriteId 问题收藏Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QuestionFavorite findQuestionFavoriteById(String questionFavoriteId){
		
		//表编号
		int tableNumber = questionFavoriteConfig.questionFavoriteIdRemainder(questionFavoriteId);
		
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("select o from QuestionFavorite o where o.id=?1")
			.setParameter(1, questionFavoriteId);
			List<QuestionFavorite> list = query.getResultList();
			for(QuestionFavorite p : list){
				return p;
			}
			
		}else{//带下划线对象
			Query query = em.createQuery("select o from QuestionFavorite_"+tableNumber+" o where o.id=?1")
			.setParameter(1, questionFavoriteId);
			List<?> questionFavorite_List= query.getResultList();
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.favorite.QuestionFavorite_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),QuestionFavorite.class, false); 
				
				for(int j = 0;j< questionFavorite_List.size(); j++) {  
					Object obj = questionFavorite_List.get(j);
					QuestionFavorite questionFavorite = new QuestionFavorite();
					copier.copy(obj,questionFavorite, null);
					return questionFavorite;
				}
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询问题收藏",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询问题收藏",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询问题收藏",e);
		        }
			}
		}
		return null;
	}
	
	/**
	 * 根据问题Id查询收藏夹分页
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param questionId 问题Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<Favorites> findFavoritePageByQuestionId(int firstIndex, int maxResult,Long questionId){
		QueryResult<Favorites> qr = new QueryResult<Favorites>();
		Query query  = null;
		
		//表编号
		int tableNumber = questionFavoriteConfig.questionIdRemainder(questionId);
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select o from QuestionFavorite o where o.questionId=?1 ORDER BY o.addtime desc");
			query.setParameter(1, questionId);

			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<QuestionFavorite> questionFavoriteList= query.getResultList();
			List<Favorites> favoritesList= new ArrayList<Favorites>();
			if(questionFavoriteList != null && questionFavoriteList.size() >0){
				BeanCopier copier = BeanCopier.create(QuestionFavorite.class,Favorites.class, false); 
				for(QuestionFavorite questionFavorite : questionFavoriteList){
					Favorites favorites = new Favorites();
					copier.copy(questionFavorite,favorites, null);
					favoritesList.add(favorites);
				}
			}
			qr.setResultlist(favoritesList);
			
			query = em.createQuery("select count(o) from QuestionFavorite o where o.questionId=?1");
			query.setParameter(1, questionId);
			qr.setTotalrecord((Long)query.getSingleResult());
			
		}else{//带下划线对象
			
			query = em.createQuery("select o from QuestionFavorite_"+tableNumber+" o where o.questionId=?1 ORDER BY o.addtime desc");
			query.setParameter(1, questionId);

			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> questionFavorite_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.favorite.QuestionFavorite_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Favorites.class, false); 
				List<Favorites> favoritesList= new ArrayList<Favorites>();
				for(int j = 0;j< questionFavorite_List.size(); j++) {  
					Object obj = questionFavorite_List.get(j);
					Favorites favorites = new Favorites();
					copier.copy(obj,favorites, null);
					favoritesList.add(favorites);
				}
				qr.setResultlist(favoritesList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据问题Id查询收藏夹分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据问题Id查询收藏夹分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据问题Id查询收藏夹分页",e);
		        }
			}
			
			
			
			query = em.createQuery("select count(o) from QuestionFavorite_"+tableNumber+" o where o.questionId=?1");
			query.setParameter(1, questionId);
			qr.setTotalrecord((Long)query.getSingleResult());
		}
		return qr;
	}
	
	/**
	 * 根据问题Id查询被收藏数量
	 * @param questionId 问题Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findFavoriteCountByQuestionId(Long questionId){
		Long count = 0L;
		//表编号
		int tableNumber = questionFavoriteConfig.questionIdRemainder(questionId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select count(o) from QuestionFavorite o where o.questionId=?1");
			query.setParameter(1, questionId);
			count = (Long)query.getSingleResult();
			
		}else{//带下划线对象
			query = em.createQuery("select count(o) from QuestionFavorite_"+tableNumber+" o where o.questionId=?1");
			query.setParameter(1, questionId);
			count = (Long)query.getSingleResult();
		}
		return count;
	}
	
}
