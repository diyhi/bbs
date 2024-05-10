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
import cms.bean.like.AnswerLike;
import cms.bean.like.AnswerReplyLike;
import cms.bean.like.CommentLike;
import cms.bean.like.CommentReplyLike;
import cms.bean.like.Like;
import cms.bean.like.QuestionLike;
import cms.bean.like.TopicLike;
import cms.service.besa.DaoSupport;
import cms.service.like.LikeService;
import cms.web.action.like.AnswerLikeConfig;
import cms.web.action.like.AnswerReplyLikeConfig;
import cms.web.action.like.CommentLikeConfig;
import cms.web.action.like.CommentReplyLikeConfig;
import cms.web.action.like.LikeConfig;
import cms.web.action.like.QuestionLikeConfig;
import cms.web.action.like.TopicLikeConfig;
import net.sf.cglib.beans.BeanCopier;

/**
 * 点赞管理接口实现类
 *
 */
@Service
@Transactional
public class LikeServiceBean extends DaoSupport<Like> implements LikeService{
	private static final Logger logger = LogManager.getLogger(LikeServiceBean.class);
	
	@Resource LikeConfig likeConfig;
	@Resource TopicLikeConfig topicLikeConfig;
	@Resource CommentReplyLikeConfig commentReplyLikeConfig;
	@Resource CommentLikeConfig commentLikeConfig;
	@Resource QuestionLikeConfig questionLikeConfig;
	@Resource AnswerLikeConfig answerLikeConfig;
	@Resource AnswerReplyLikeConfig answerReplyLikeConfig;
	
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
	 * 保存项目点赞
	 * @param like 点赞
	 * @param itemLike 项目点赞 例如:topicLike话题点赞
	 */
	public void saveLike(Object like,Object itemLike){
		this.save(like);
		this.save(itemLike);
	}
	
	
	
	/**
	 * 删除点赞
	 * @param likeId 点赞Id
	 * @param itemLikeId 例如：话题点赞Id
	 * @param module 模块
	 */
	public Integer deleteLike(String likeId,String itemLikeId,Integer module){
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
		
		if(module.equals(10)){//话题
			int topicLike_tableNumber = topicLikeConfig.topicLikeIdRemainder(itemLikeId);
			if(topicLike_tableNumber == 0){//默认对象
				Query delete = em.createQuery("delete from TopicLike o where o.id=?1")
				.setParameter(1,itemLikeId);
				i += delete.executeUpdate();
			}else{//带下划线对象
				Query delete = em.createQuery("delete from TopicLike_"+topicLike_tableNumber+" o where o.id=?1")
						.setParameter(1,itemLikeId);
						i += delete.executeUpdate();
			}
			
		}else if(module.equals(20)){//评论
			int commentLike_tableNumber = commentLikeConfig.commentLikeIdRemainder(itemLikeId);
			if(commentLike_tableNumber == 0){//默认对象
				Query delete = em.createQuery("delete from CommentLike o where o.id=?1")
				.setParameter(1,itemLikeId);
				i += delete.executeUpdate();
			}else{//带下划线对象
				Query delete = em.createQuery("delete from CommentLike_"+commentLike_tableNumber+" o where o.id=?1")
						.setParameter(1,itemLikeId);
						i += delete.executeUpdate();
			}
			
		}else if(module.equals(30)){//评论回复
			int commentReplyLike_tableNumber = commentReplyLikeConfig.commentReplyLikeIdRemainder(itemLikeId);
			if(commentReplyLike_tableNumber == 0){//默认对象
				Query delete = em.createQuery("delete from CommentReplyLike o where o.id=?1")
				.setParameter(1,itemLikeId);
				i += delete.executeUpdate();
			}else{//带下划线对象
				Query delete = em.createQuery("delete from CommentReplyLike_"+commentReplyLike_tableNumber+" o where o.id=?1")
						.setParameter(1,itemLikeId);
						i += delete.executeUpdate();
			}
			
		}else if(module.equals(40)){//问题
			int questionLike_tableNumber = questionLikeConfig.questionLikeIdRemainder(itemLikeId);
			if(questionLike_tableNumber == 0){//默认对象
				Query delete = em.createQuery("delete from QuestionLike o where o.id=?1")
				.setParameter(1,itemLikeId);
				i += delete.executeUpdate();
			}else{//带下划线对象
				Query delete = em.createQuery("delete from QuestionLike_"+questionLike_tableNumber+" o where o.id=?1")
						.setParameter(1,itemLikeId);
						i += delete.executeUpdate();
			}
		}else if(module.equals(50)){//答案
			int answerLike_tableNumber = answerLikeConfig.answerLikeIdRemainder(itemLikeId);
			if(answerLike_tableNumber == 0){//默认对象
				Query delete = em.createQuery("delete from AnswerLike o where o.id=?1")
				.setParameter(1,itemLikeId);
				i += delete.executeUpdate();
			}else{//带下划线对象
				Query delete = em.createQuery("delete from AnswerLike_"+answerLike_tableNumber+" o where o.id=?1")
						.setParameter(1,itemLikeId);
						i += delete.executeUpdate();
			}
		}else if(module.equals(60)){//答案回复
			int answerReplyLike_tableNumber = answerReplyLikeConfig.answerReplyLikeIdRemainder(itemLikeId);
			if(answerReplyLike_tableNumber == 0){//默认对象
				Query delete = em.createQuery("delete from AnswerReplyLike o where o.id=?1")
				.setParameter(1,itemLikeId);
				i += delete.executeUpdate();
			}else{//带下划线对象
				Query delete = em.createQuery("delete from AnswerReplyLike_"+answerReplyLike_tableNumber+" o where o.id=?1")
						.setParameter(1,itemLikeId);
						i += delete.executeUpdate();
			}
		}
		
		
		
		return i;
	}
	
	/**
	 * 根据话题Id删除点赞（暂时没用上）
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
	 * ----------------------------------------------- 话题点赞 -----------------------------------------------
	 */
	
	
	
	
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
		
		
		//表编号
		int commentLike_tableNumber = commentLikeConfig.getTableQuantity();
		for(int i = 0; i<commentLike_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from CommentLike o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from CommentLike_"+i+" o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		
		//表编号
		int commentReplyLike_tableNumber = commentReplyLikeConfig.getTableQuantity();
		for(int i = 0; i<commentReplyLike_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from CommentReplyLike o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from CommentReplyLike_"+i+" o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		
		
		
		
		
		
		
		//表编号
		int questionLike_tableNumber = questionLikeConfig.getTableQuantity();
		for(int i = 0; i<questionLike_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from QuestionLike o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from QuestionLike_"+i+" o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		
		
		//表编号
		int answerLike_tableNumber = answerLikeConfig.getTableQuantity();
		for(int i = 0; i<answerLike_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from AnswerLike o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from AnswerLike_"+i+" o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		
		//表编号
		int answerReplyLike_tableNumber = answerReplyLikeConfig.getTableQuantity();
		for(int i = 0; i<answerReplyLike_tableNumber; i++){
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from AnswerReplyLike o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
				
			}else{//带下划线对象
				Query query = em.createQuery("delete from AnswerReplyLike_"+i+" o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		
		return j;
	}
	
	/**
	 * 根据发布话题的用户名称删除点赞（暂时没用上）
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
	
	
	/**
	 * ----------------------------------------------- 评论点赞 -----------------------------------------------
	 */
	
	/**
	 * 根据Id查询评论点赞
	 * @param commentLikeId 评论点赞Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public CommentLike findCommentLikeById(String commentLikeId){
		
		//表编号
		int tableNumber = commentLikeConfig.commentLikeIdRemainder(commentLikeId);
		
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("select o from CommentLike o where o.id=?1")
			.setParameter(1, commentLikeId);
			List<CommentLike> list = query.getResultList();
			for(CommentLike p : list){
				return p;
			}
			
		}else{//带下划线对象
			Query query = em.createQuery("select o from CommentLike_"+tableNumber+" o where o.id=?1")
			.setParameter(1, commentLikeId);
			List<?> commentLike_List= query.getResultList();
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.like.CommentLike_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),CommentLike.class, false); 
				
				for(int j = 0;j< commentLike_List.size(); j++) {  
					Object obj = commentLike_List.get(j);
					CommentLike commentLike = new CommentLike();
					copier.copy(obj,commentLike, null);
					return commentLike;
				}
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询评论点赞",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询评论点赞",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询评论点赞",e);
		        }
			}
		}
		return null;
	}
	
	/**
	 * 根据评论Id查询被点赞数量
	 * @param commentId 评论Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findLikeCountByCommentId(Long commentId){
		Long count = 0L;
		//表编号
		int tableNumber = commentLikeConfig.commentIdRemainder(commentId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select count(o) from CommentLike o where o.commentId=?1");
			query.setParameter(1, commentId);
			count = (Long)query.getSingleResult();
			
		}else{//带下划线对象
			query = em.createQuery("select count(o) from CommentLike_"+tableNumber+" o where o.commentId=?1");
			query.setParameter(1, commentId);
			count = (Long)query.getSingleResult();
		}
		return count;
	}
	
	/**
	 * 删除评论点赞
	 * @param likeId 点赞Id
	 * @param commentLikeId 评论点赞Id
	 */
	public Integer deleteCommentLike(String likeId,String commentLikeId){
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
		
		
		int commentLike_tableNumber = commentLikeConfig.commentLikeIdRemainder(commentLikeId);
		if(commentLike_tableNumber == 0){//默认对象
			Query delete = em.createQuery("delete from CommentLike o where o.id=?1")
			.setParameter(1,commentLikeId);
			i += delete.executeUpdate();
		}else{//带下划线对象
			Query delete = em.createQuery("delete from CommentLike_"+commentLike_tableNumber+" o where o.id=?1")
					.setParameter(1,commentLikeId);
					i += delete.executeUpdate();
		}
		
		
		return i;
	}
	
	
	/**
	 * 根据评论Id查询点赞分页
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param commentId 评论Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<Like> findLikePageByCommentId(int firstIndex, int maxResult,Long commentId){
		QueryResult<Like> qr = new QueryResult<Like>();
		Query query  = null;
		
		//表编号
		int tableNumber = commentLikeConfig.commentIdRemainder(commentId);
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select o from CommentLike o where o.commentId=?1 ORDER BY o.addtime desc");
			query.setParameter(1, commentId);

			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<CommentLike> commentLikeList= query.getResultList();
			List<Like> likeList= new ArrayList<Like>();
			if(commentLikeList != null && commentLikeList.size() >0){
				BeanCopier copier = BeanCopier.create(CommentLike.class,Like.class, false); 
				for(CommentLike commentLike : commentLikeList){
					Like like = new Like();
					copier.copy(commentLike,like, null);
					likeList.add(like);
				}
			}
			qr.setResultlist(likeList);
			
			query = em.createQuery("select count(o) from CommentLike o where o.commentId=?1");
			query.setParameter(1, commentId);
			qr.setTotalrecord((Long)query.getSingleResult());
			
		}else{//带下划线对象
			query = em.createQuery("select o from CommentLike_"+tableNumber+" o where o.commentId=?1 ORDER BY o.addtime desc");
			query.setParameter(1, commentId);

			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> commentLike_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.like.CommentLike_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Like.class, false); 
				List<Like> likeList= new ArrayList<Like>();
				for(int j = 0;j< commentLike_List.size(); j++) {  
					Object obj = commentLike_List.get(j);
					Like like = new Like();
					copier.copy(obj,like, null);
					likeList.add(like);
				}
				qr.setResultlist(likeList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据评论Id查询点赞分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据评论Id查询点赞分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据评论Id查询点赞分页",e);
		        }
			}
			
			
			
			query = em.createQuery("select count(o) from CommentLike_"+tableNumber+" o where o.commentId=?1");
			query.setParameter(1, commentId);
			qr.setTotalrecord((Long)query.getSingleResult());
		}
		return qr;
	}
	
	/**
	 * ----------------------------------------------- 评论回复点赞 -----------------------------------------------
	 */
	
	/**
	 * 根据Id查询评论回复点赞
	 * @param commentReplyLikeId 评论回复点赞Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public CommentReplyLike findCommentReplyLikeById(String commentReplyLikeId){
		
		//表编号
		int tableNumber = commentReplyLikeConfig.commentReplyLikeIdRemainder(commentReplyLikeId);
		
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("select o from CommentReplyLike o where o.id=?1")
			.setParameter(1, commentReplyLikeId);
			List<CommentReplyLike> list = query.getResultList();
			for(CommentReplyLike p : list){
				return p;
			}
			
		}else{//带下划线对象
			Query query = em.createQuery("select o from CommentReplyLike_"+tableNumber+" o where o.id=?1")
			.setParameter(1, commentReplyLikeId);
			List<?> commentReplyLike_List= query.getResultList();
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.like.CommentReplyLike_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),CommentReplyLike.class, false); 
				
				for(int j = 0;j< commentReplyLike_List.size(); j++) {  
					Object obj = commentReplyLike_List.get(j);
					CommentReplyLike commentReplyLike = new CommentReplyLike();
					copier.copy(obj,commentReplyLike, null);
					return commentReplyLike;
				}
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询评论回复点赞",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询评论回复点赞",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询评论回复点赞",e);
		        }
			}
		}
		return null;
	}
	
	/**
	 * 根据评论回复Id查询被点赞数量
	 * @param commentId 评论回复Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findLikeCountByCommentReplyId(Long commentReplyId){
		Long count = 0L;
		//表编号
		int tableNumber = commentReplyLikeConfig.commentReplyIdRemainder(commentReplyId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select count(o) from CommentReplyLike o where o.replyId=?1");
			query.setParameter(1, commentReplyId);
			count = (Long)query.getSingleResult();
			
		}else{//带下划线对象
			query = em.createQuery("select count(o) from CommentReplyLike_"+tableNumber+" o where o.replyId=?1");
			query.setParameter(1, commentReplyId);
			count = (Long)query.getSingleResult();
		}
		return count;
	}

	/**
	 * 删除评论回复点赞
	 * @param likeId 点赞Id
	 * @param commentLikeId 评论点赞Id
	 */
	public Integer deleteCommentReplyLike(String likeId,String commentReplyLikeId){
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
		
		
		int commentReplyLike_tableNumber = commentReplyLikeConfig.commentReplyLikeIdRemainder(commentReplyLikeId);
		if(commentReplyLike_tableNumber == 0){//默认对象
			Query delete = em.createQuery("delete from CommentReplyLike o where o.id=?1")
			.setParameter(1,commentReplyLikeId);
			i += delete.executeUpdate();
		}else{//带下划线对象
			Query delete = em.createQuery("delete from CommentReplyLike_"+commentReplyLike_tableNumber+" o where o.id=?1")
					.setParameter(1,commentReplyLikeId);
					i += delete.executeUpdate();
		}
		
		
		return i;
	}
	
	
	
	/**
	 * 根据评论回复Id查询点赞分页
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param replyId 回复Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<Like> findLikePageByCommentReplyId(int firstIndex, int maxResult,Long replyId){
		QueryResult<Like> qr = new QueryResult<Like>();
		Query query  = null;
		
		//表编号
		int tableNumber = commentReplyLikeConfig.commentReplyIdRemainder(replyId);
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select o from CommentReplyLike o where o.replyId=?1 ORDER BY o.addtime desc");
			query.setParameter(1, replyId);

			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<CommentReplyLike> commentReplyLikeList= query.getResultList();
			List<Like> likeList= new ArrayList<Like>();
			if(commentReplyLikeList != null && commentReplyLikeList.size() >0){
				BeanCopier copier = BeanCopier.create(CommentReplyLike.class,Like.class, false); 
				for(CommentReplyLike commentReplyLike : commentReplyLikeList){
					Like like = new Like();
					copier.copy(commentReplyLike,like, null);
					likeList.add(like);
				}
			}
			qr.setResultlist(likeList);
			
			query = em.createQuery("select count(o) from CommentReplyLike o where o.replyId=?1");
			query.setParameter(1, replyId);
			qr.setTotalrecord((Long)query.getSingleResult());
			
		}else{//带下划线对象
			query = em.createQuery("select o from CommentReplyLike_"+tableNumber+" o where o.replyId=?1 ORDER BY o.addtime desc");
			query.setParameter(1, replyId);

			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> commentReplyLike_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.like.CommentReplyLike_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Like.class, false); 
				List<Like> likeList= new ArrayList<Like>();
				for(int j = 0;j< commentReplyLike_List.size(); j++) {  
					Object obj = commentReplyLike_List.get(j);
					Like like = new Like();
					copier.copy(obj,like, null);
					likeList.add(like);
				}
				qr.setResultlist(likeList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据评论回复Id查询点赞分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据评论回复Id查询点赞分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据评论回复Id查询点赞分页",e);
		        }
			}
			
			
			
			query = em.createQuery("select count(o) from CommentReplyLike_"+tableNumber+" o where o.replyId=?1");
			query.setParameter(1, replyId);
			qr.setTotalrecord((Long)query.getSingleResult());
		}
		return qr;
	}
	
	/**
	 * ----------------------------------------------- 问题点赞 -----------------------------------------------
	 */
	
	
	
	
	
	/**
	 * 根据Id查询问题点赞
	 * @param questionLikeId 问题点赞Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QuestionLike findQuestionLikeById(String questionLikeId){
		
		//表编号
		int tableNumber = questionLikeConfig.questionLikeIdRemainder(questionLikeId);
		
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("select o from QuestionLike o where o.id=?1")
			.setParameter(1, questionLikeId);
			List<QuestionLike> list = query.getResultList();
			for(QuestionLike p : list){
				return p;
			}
			
		}else{//带下划线对象
			Query query = em.createQuery("select o from QuestionLike_"+tableNumber+" o where o.id=?1")
			.setParameter(1, questionLikeId);
			List<?> questionLike_List= query.getResultList();
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.like.QuestionLike_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),QuestionLike.class, false); 
				
				for(int j = 0;j< questionLike_List.size(); j++) {  
					Object obj = questionLike_List.get(j);
					QuestionLike questionLike = new QuestionLike();
					copier.copy(obj,questionLike, null);
					return questionLike;
				}
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询问题点赞",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询问题点赞",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询问题点赞",e);
		        }
			}
		}
		return null;
	}
	
	
	/**
	 * 根据问题Id查询点赞分页
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param questionId 问题Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<Like> findLikePageByQuestionId(int firstIndex, int maxResult,Long questionId){
		QueryResult<Like> qr = new QueryResult<Like>();
		Query query  = null;
		
		//表编号
		int tableNumber = questionLikeConfig.questionIdRemainder(questionId);
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select o from QuestionLike o where o.questionId=?1 ORDER BY o.addtime desc");
			query.setParameter(1, questionId);

			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<QuestionLike> questionLikeList= query.getResultList();
			List<Like> likeList= new ArrayList<Like>();
			if(questionLikeList != null && questionLikeList.size() >0){
				BeanCopier copier = BeanCopier.create(QuestionLike.class,Like.class, false); 
				for(QuestionLike questionLike : questionLikeList){
					Like like = new Like();
					copier.copy(questionLike,like, null);
					likeList.add(like);
				}
			}
			qr.setResultlist(likeList);
			
			query = em.createQuery("select count(o) from QuestionLike o where o.questionId=?1");
			query.setParameter(1, questionId);
			qr.setTotalrecord((Long)query.getSingleResult());
			
		}else{//带下划线对象
			query = em.createQuery("select o from QuestionLike_"+tableNumber+" o where o.questionId=?1 ORDER BY o.addtime desc");
			query.setParameter(1, questionId);

			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> questionLike_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.like.QuestionLike_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Like.class, false); 
				List<Like> likeList= new ArrayList<Like>();
				for(int j = 0;j< questionLike_List.size(); j++) {  
					Object obj = questionLike_List.get(j);
					Like like = new Like();
					copier.copy(obj,like, null);
					likeList.add(like);
				}
				qr.setResultlist(likeList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据问题Id查询点赞分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据问题Id查询点赞分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据问题Id查询点赞分页",e);
		        }
			}
			
			
			
			query = em.createQuery("select count(o) from QuestionLike_"+tableNumber+" o where o.questionId=?1");
			query.setParameter(1, questionId);
			qr.setTotalrecord((Long)query.getSingleResult());
		}
		return qr;
	}
	
	
	/**
	 * 根据问题Id查询被点赞数量
	 * @param questionId 问题Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findLikeCountByQuestionId(Long questionId){
		Long count = 0L;
		//表编号
		int tableNumber = questionLikeConfig.questionIdRemainder(questionId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select count(o) from QuestionLike o where o.questionId=?1");
			query.setParameter(1, questionId);
			count = (Long)query.getSingleResult();
			
		}else{//带下划线对象
			query = em.createQuery("select count(o) from QuestionLike_"+tableNumber+" o where o.questionId=?1");
			query.setParameter(1, questionId);
			count = (Long)query.getSingleResult();
		}
		return count;
	}
	
	
	/**
	 * ----------------------------------------------- 答案点赞 -----------------------------------------------
	 */
	
	/**
	 * 根据Id查询答案点赞
	 * @param answerLikeId 答案点赞Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public AnswerLike findAnswerLikeById(String answerLikeId){
		
		//表编号
		int tableNumber = answerLikeConfig.answerLikeIdRemainder(answerLikeId);
		
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("select o from AnswerLike o where o.id=?1")
			.setParameter(1, answerLikeId);
			List<AnswerLike> list = query.getResultList();
			for(AnswerLike p : list){
				return p;
			}
			
		}else{//带下划线对象
			Query query = em.createQuery("select o from AnswerLike_"+tableNumber+" o where o.id=?1")
			.setParameter(1, answerLikeId);
			List<?> answerLike_List= query.getResultList();
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.like.AnswerLike_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),AnswerLike.class, false); 
				
				for(int j = 0;j< answerLike_List.size(); j++) {  
					Object obj = answerLike_List.get(j);
					AnswerLike answerLike = new AnswerLike();
					copier.copy(obj,answerLike, null);
					return answerLike;
				}
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询答案点赞",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询答案点赞",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询答案点赞",e);
		        }
			}
		}
		return null;
	}
	
	/**
	 * 根据答案Id查询被点赞数量
	 * @param answerId 答案Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findLikeCountByAnswerId(Long answerId){
		Long count = 0L;
		//表编号
		int tableNumber = answerLikeConfig.answerIdRemainder(answerId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select count(o) from AnswerLike o where o.answerId=?1");
			query.setParameter(1, answerId);
			count = (Long)query.getSingleResult();
			
		}else{//带下划线对象
			query = em.createQuery("select count(o) from AnswerLike_"+tableNumber+" o where o.answerId=?1");
			query.setParameter(1, answerId);
			count = (Long)query.getSingleResult();
		}
		return count;
	}
	
	/**
	 * 删除答案点赞
	 * @param likeId 点赞Id
	 * @param answerLikeId 答案点赞Id
	 */
	public Integer deleteAnswerLike(String likeId,String answerLikeId){
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
		
		
		int answerLike_tableNumber = answerLikeConfig.answerLikeIdRemainder(answerLikeId);
		if(answerLike_tableNumber == 0){//默认对象
			Query delete = em.createQuery("delete from AnswerLike o where o.id=?1")
			.setParameter(1,answerLikeId);
			i += delete.executeUpdate();
		}else{//带下划线对象
			Query delete = em.createQuery("delete from AnswerLike_"+answerLike_tableNumber+" o where o.id=?1")
					.setParameter(1,answerLikeId);
					i += delete.executeUpdate();
		}
		
		
		return i;
	}
	
	/**
	 * 根据答案Id查询点赞分页
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param answerId 答案Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<Like> findLikePageByAnswerId(int firstIndex, int maxResult,Long answerId){
		QueryResult<Like> qr = new QueryResult<Like>();
		Query query  = null;
		
		//表编号
		int tableNumber = answerLikeConfig.answerIdRemainder(answerId);
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select o from AnswerLike o where o.answerId=?1 ORDER BY o.addtime desc");
			query.setParameter(1, answerId);

			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<AnswerLike> answerLikeList= query.getResultList();
			List<Like> likeList= new ArrayList<Like>();
			if(answerLikeList != null && answerLikeList.size() >0){
				BeanCopier copier = BeanCopier.create(AnswerLike.class,Like.class, false); 
				for(AnswerLike answerLike : answerLikeList){
					Like like = new Like();
					copier.copy(answerLike,like, null);
					likeList.add(like);
				}
			}
			qr.setResultlist(likeList);
			
			query = em.createQuery("select count(o) from AnswerLike o where o.answerId=?1");
			query.setParameter(1, answerId);
			qr.setTotalrecord((Long)query.getSingleResult());
			
		}else{//带下划线对象
			query = em.createQuery("select o from AnswerLike_"+tableNumber+" o where o.answerId=?1 ORDER BY o.addtime desc");
			query.setParameter(1, answerId);

			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> answerLike_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.like.AnswerLike_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Like.class, false); 
				List<Like> likeList= new ArrayList<Like>();
				for(int j = 0;j< answerLike_List.size(); j++) {  
					Object obj = answerLike_List.get(j);
					Like like = new Like();
					copier.copy(obj,like, null);
					likeList.add(like);
				}
				qr.setResultlist(likeList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据答案Id查询点赞分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据答案Id查询点赞分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据答案Id查询点赞分页",e);
		        }
			}
			
			
			
			query = em.createQuery("select count(o) from AnswerLike_"+tableNumber+" o where o.answerId=?1");
			query.setParameter(1, answerId);
			qr.setTotalrecord((Long)query.getSingleResult());
		}
		return qr;
	}
	
	/**
	 * ----------------------------------------------- 答案回复点赞 -----------------------------------------------
	 */
	
	/**
	 * 根据Id查询答案回复点赞
	 * @param answerReplyLikeId 答案回复点赞Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public AnswerReplyLike findAnswerReplyLikeById(String answerReplyLikeId){
		
		//表编号
		int tableNumber = answerReplyLikeConfig.answerReplyLikeIdRemainder(answerReplyLikeId);
		
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("select o from AnswerReplyLike o where o.id=?1")
			.setParameter(1, answerReplyLikeId);
			List<AnswerReplyLike> list = query.getResultList();
			for(AnswerReplyLike p : list){
				return p;
			}
			
		}else{//带下划线对象
			Query query = em.createQuery("select o from AnswerReplyLike_"+tableNumber+" o where o.id=?1")
			.setParameter(1, answerReplyLikeId);
			List<?> answerReplyLike_List= query.getResultList();
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.like.AnswerReplyLike_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),AnswerReplyLike.class, false); 
				
				for(int j = 0;j< answerReplyLike_List.size(); j++) {  
					Object obj = answerReplyLike_List.get(j);
					AnswerReplyLike answerReplyLike = new AnswerReplyLike();
					copier.copy(obj,answerReplyLike, null);
					return answerReplyLike;
				}
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询答案回复点赞",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询答案回复点赞",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询答案回复点赞",e);
		        }
			}
		}
		return null;
	}
	
	/**
	 * 根据答案回复Id查询被点赞数量
	 * @param answerId 答案回复Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findLikeCountByAnswerReplyId(Long answerReplyId){
		Long count = 0L;
		//表编号
		int tableNumber = answerReplyLikeConfig.answerReplyIdRemainder(answerReplyId);
		Query query  = null;
		
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select count(o) from AnswerReplyLike o where o.replyId=?1");
			query.setParameter(1, answerReplyId);
			count = (Long)query.getSingleResult();
			
		}else{//带下划线对象
			query = em.createQuery("select count(o) from AnswerReplyLike_"+tableNumber+" o where o.replyId=?1");
			query.setParameter(1, answerReplyId);
			count = (Long)query.getSingleResult();
		}
		return count;
	}

	/**
	 * 删除答案回复点赞
	 * @param likeId 点赞Id
	 * @param answerLikeId 答案点赞Id
	 */
	public Integer deleteAnswerReplyLike(String likeId,String answerReplyLikeId){
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
		
		
		int answerReplyLike_tableNumber = answerReplyLikeConfig.answerReplyLikeIdRemainder(answerReplyLikeId);
		if(answerReplyLike_tableNumber == 0){//默认对象
			Query delete = em.createQuery("delete from AnswerReplyLike o where o.id=?1")
			.setParameter(1,answerReplyLikeId);
			i += delete.executeUpdate();
		}else{//带下划线对象
			Query delete = em.createQuery("delete from AnswerReplyLike_"+answerReplyLike_tableNumber+" o where o.id=?1")
					.setParameter(1,answerReplyLikeId);
					i += delete.executeUpdate();
		}
		
		
		return i;
	}
	
	
	/**
	 * 根据答案回复Id查询点赞分页
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param replyId 回复Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<Like> findLikePageByAnswerReplyId(int firstIndex, int maxResult,Long replyId){
		QueryResult<Like> qr = new QueryResult<Like>();
		Query query  = null;
		
		//表编号
		int tableNumber = answerReplyLikeConfig.answerReplyIdRemainder(replyId);
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select o from AnswerReplyLike o where o.replyId=?1 ORDER BY o.addtime desc");
			query.setParameter(1, replyId);

			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<AnswerReplyLike> answerReplyLikeList= query.getResultList();
			List<Like> likeList= new ArrayList<Like>();
			if(answerReplyLikeList != null && answerReplyLikeList.size() >0){
				BeanCopier copier = BeanCopier.create(AnswerReplyLike.class,Like.class, false); 
				for(AnswerReplyLike answerReplyLike : answerReplyLikeList){
					Like like = new Like();
					copier.copy(answerReplyLike,like, null);
					likeList.add(like);
				}
			}
			qr.setResultlist(likeList);
			
			query = em.createQuery("select count(o) from AnswerReplyLike o where o.replyId=?1");
			query.setParameter(1, replyId);
			qr.setTotalrecord((Long)query.getSingleResult());
			
		}else{//带下划线对象
			query = em.createQuery("select o from AnswerReplyLike_"+tableNumber+" o where o.replyId=?1 ORDER BY o.addtime desc");
			query.setParameter(1, replyId);

			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> answerReplyLike_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.like.AnswerReplyLike_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),Like.class, false); 
				List<Like> likeList= new ArrayList<Like>();
				for(int j = 0;j< answerReplyLike_List.size(); j++) {  
					Object obj = answerReplyLike_List.get(j);
					Like like = new Like();
					copier.copy(obj,like, null);
					likeList.add(like);
				}
				qr.setResultlist(likeList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据答案回复Id查询点赞分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据答案回复Id查询点赞分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据答案回复Id查询点赞分页",e);
		        }
			}
			
			
			
			query = em.createQuery("select count(o) from AnswerReplyLike_"+tableNumber+" o where o.replyId=?1");
			query.setParameter(1, replyId);
			qr.setTotalrecord((Long)query.getSingleResult());
		}
		return qr;
	}
	
}
