package cms.web.action.like;



import javax.annotation.Resource;

import cms.bean.like.AnswerLike;
import cms.bean.like.AnswerReplyLike;
import cms.bean.like.CommentLike;
import cms.bean.like.CommentReplyLike;
import cms.bean.like.Like;
import cms.bean.like.QuestionLike;
import cms.bean.like.TopicLike;
import cms.service.like.LikeService;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import net.sf.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 点赞组件
 *
 */
@Component("likeManage")
public class LikeManage {
	private static final Logger logger = LogManager.getLogger(LikeManage.class);
	
    @Resource LikeConfig likeConfig;
    @Resource LikeService likeService;
    @Resource TopicLikeConfig topicLikeConfig;
    @Resource CommentLikeConfig commentLikeConfig;
    @Resource CommentReplyLikeConfig commentReplyLikeConfig;
    @Resource AnswerLikeConfig answerLikeConfig;
    @Resource AnswerReplyLikeConfig answerReplyLikeConfig;
    @Resource QuestionLikeConfig questionLikeConfig;
    
	/**
	 * 取得点赞Id的用户Id(后N位)
	 * @param likeId 点赞Id
	 * @return
	 */
    public int getLikeId(String likeId){
    	String after_userId = likeId.substring(likeId.length()-4, likeId.length());
    	return Integer.parseInt(after_userId);
    } 
    
    /**
     * 生成点赞Id
     * 点赞Id由32位uuid+1位用户Id后4位组成
     * @param userId 用户Id
     * @return
     */
    public String createLikeId(Long userId){
    	//选取得后N位用户Id
    	String afterUserId = String.format("%04d", userId%10000);
    	String id = UUIDUtil.getUUID32()+afterUserId;
    	return id;
    }
    /**
     * 校验点赞Id
     * 点赞Id要先判断是否36位并且最后4位是数字
     * @param likeId 点赞Id
     * @return
     */
    public boolean verificationLikeId(String likeId){
    	if(likeId != null && !"".equals(likeId.trim())){
    		if(likeId.length() == 36){
    			String after_userId = likeId.substring(likeId.length()-4, likeId.length());		
    			boolean verification = Verification.isPositiveIntegerZero(after_userId);//数字
				if(verification){
					return true;
				}
    		}
    	}
    	return false;
    }
    
    /**
     * 生成点赞对象
     * @return
     */
    public Object createLikeObject(Like like){
    	//表编号
		int tableNumber = likeConfig.likeIdRemainder(like.getId());
		if(tableNumber == 0){//默认对象
			return like;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.like.Like_"+tableNumber);
				Object object = c.newInstance();
				
				BeanCopier copier = BeanCopier.create(Like.class,object.getClass(), false); 
			
				copier.copy(like,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成点赞对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成点赞对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成点赞对象",e);
		        }
			}	
		}
		return null;
    }
    
    
    
    /**---------------------------------------------- 话题点赞 ----------------------------------------------**/
    /**
	 * 取得项目点赞Id的项目Id(后N位)   例如：取得话题点赞Id的话题Id(后N位)
	 * @param itemLikeId 项目点赞Id  例如：话题点赞Id
	 * @return
	 */
    public int getItemLikeId(String itemLikeId){
    	String[] idGroup = itemLikeId.split("_");
    	Long itemId = Long.parseLong(idGroup[0]);
    	
    	//选取得后N位项目Id
    	String after_itemId = String.format("%04d", Math.abs(itemId)%10000);
    	return Integer.parseInt(after_itemId);
    } 
    
    /**
     * 生成项目点赞Id  例如：生成话题点赞Id  话题点赞Id格式（话题Id_用户Id）
     * @param itemId 项目Id  例如：话题Id
     * @param userId 用户Id
     * @return
     */
    public String createItemLikeId(Long itemId,Long userId){
    	return itemId+"_"+userId;
    }
    /**
     * 校验项目点赞Id  例如：校验话题点赞Id
     * 话题点赞Id要先判断最后4位是不是数字
     * @param itemLikeId 项目点赞Id  例如：话题点赞Id
     * @return
     */
    public boolean verificationItemLikeId(String itemLikeId){
    	if(itemLikeId != null && !"".equals(itemLikeId.trim())){
    		String[] idGroup = itemLikeId.split("_");
    		for(String id : idGroup){
    			boolean verification = Verification.isPositiveIntegerZero(id);//数字
    			if(!verification){
    				return false;
    			}
    			return true;
    		}	
			
    	}
    	return false;
    }
    
    
    /**
     * 生成话题点赞对象
     * @return
     */
    public Object createTopicLikeObject(TopicLike topicLike){
    	//表编号
		int tableNumber = topicLikeConfig.topicLikeIdRemainder(topicLike.getId());
		if(tableNumber == 0){//默认对象
			return topicLike;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.like.TopicLike_"+tableNumber);
				Object object = c.newInstance();
				
				BeanCopier copier = BeanCopier.create(TopicLike.class,object.getClass(), false); 
			
				copier.copy(topicLike,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成话题点赞对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成话题点赞对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成话题点赞对象",e);
		        }
			}	
		}
		return null;
    }
    
    
    
    
    /**
     * 生成评论点赞对象
     * @return
     */
    public Object createCommentLikeObject(CommentLike commentLike){
    	//表编号
		int tableNumber = commentLikeConfig.commentLikeIdRemainder(commentLike.getId());
		if(tableNumber == 0){//默认对象
			return commentLike;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.like.CommentLike_"+tableNumber);
				Object object = c.newInstance();
				
				BeanCopier copier = BeanCopier.create(CommentLike.class,object.getClass(), false); 
			
				copier.copy(commentLike,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成评论点赞对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成评论点赞对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成评论点赞对象",e);
		        }
			}	
		}
		return null;
    }
    
    
    /**
     * 生成评论回复点赞对象
     * @return
     */
    public Object createCommentReplyLikeObject(CommentReplyLike commentReplyLike){
    	//表编号
		int tableNumber = commentReplyLikeConfig.commentReplyLikeIdRemainder(commentReplyLike.getId());
		if(tableNumber == 0){//默认对象
			return commentReplyLike;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.like.CommentReplyLike_"+tableNumber);
				Object object = c.newInstance();
				
				BeanCopier copier = BeanCopier.create(CommentReplyLike.class,object.getClass(), false); 
			
				copier.copy(commentReplyLike,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成评论回复点赞对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成评论回复点赞对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成评论回复点赞对象",e);
		        }
			}	
		}
		return null;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * 生成问题点赞对象
     * @return
     */
    public Object createQuestionLikeObject(QuestionLike questionLike){
    	//表编号
		int tableNumber = questionLikeConfig.questionLikeIdRemainder(questionLike.getId());
		if(tableNumber == 0){//默认对象
			return questionLike;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.like.QuestionLike_"+tableNumber);
				Object object = c.newInstance();
				
				BeanCopier copier = BeanCopier.create(QuestionLike.class,object.getClass(), false); 
			
				copier.copy(questionLike,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成问题点赞对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成问题点赞对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成问题点赞对象",e);
		        }
			}	
		}
		return null;
    }
    
    
    
    
    /**
     * 生成答案点赞对象
     * @return
     */
    public Object createAnswerLikeObject(AnswerLike answerLike){
    	//表编号
		int tableNumber = answerLikeConfig.answerLikeIdRemainder(answerLike.getId());
		if(tableNumber == 0){//默认对象
			return answerLike;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.like.AnswerLike_"+tableNumber);
				Object object = c.newInstance();
				
				BeanCopier copier = BeanCopier.create(AnswerLike.class,object.getClass(), false); 
			
				copier.copy(answerLike,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成答案点赞对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成答案点赞对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成答案点赞对象",e);
		        }
			}	
		}
		return null;
    }
    
    
    /**
     * 生成答案回复点赞对象
     * @return
     */
    public Object createAnswerReplyLikeObject(AnswerReplyLike answerReplyLike){
    	//表编号
		int tableNumber = answerReplyLikeConfig.answerReplyLikeIdRemainder(answerReplyLike.getId());
		if(tableNumber == 0){//默认对象
			return answerReplyLike;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.like.AnswerReplyLike_"+tableNumber);
				Object object = c.newInstance();
				
				BeanCopier copier = BeanCopier.create(AnswerReplyLike.class,object.getClass(), false); 
			
				copier.copy(answerReplyLike,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成答案回复点赞对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成答案回复点赞对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成答案回复点赞对象",e);
		        }
			}	
		}
		return null;
    }
    

    /**
	 * 查询缓存 查询话题点赞
	 * @param topicLikeId 话题点赞Id
	 * @return
	 */
	@Cacheable(value="likeManage_cache_findTopicLikeById",key="#topicLikeId")
	public TopicLike query_cache_findTopicLikeById(String topicLikeId){
		return likeService.findTopicLikeById(topicLikeId);
	}
	/**
	 * 删除缓存 话题点赞
	 * @param topicLikeId 话题点赞Id
	 * @return
	 */
	@CacheEvict(value="likeManage_cache_findTopicLikeById",key="#topicLikeId")
	public void delete_cache_findTopicLikeById(String topicLikeId){
	}
    
    
	/**
	 * 查询缓存 根据话题Id查询被点赞数量
	 * @param topicId 话题Id
	 * @return
	 */
	@Cacheable(value="likeManage_cache_findLikeCountByTopicId",key="#topicId")
	public Long query_cache_findLikeCountByTopicId(Long topicId){
		return likeService.findLikeCountByTopicId(topicId);
	}
	/**
	 * 删除缓存 根据话题Id查询被点赞数量
	 * @param topicId 话题Id
	 * @return
	 */
	@CacheEvict(value="likeManage_cache_findLikeCountByTopicId",key="#topicId")
	public void delete_cache_findLikeCountByTopicId(Long topicId){
	}
    
	
	/**
	 * 查询缓存 查询评论点赞
	 * @param commentLikeId 评论点赞Id
	 * @return
	 */
	@Cacheable(value="likeManage_cache_findCommentLikeById",key="#commentLikeId")
	public CommentLike query_cache_findCommentLikeById(String commentLikeId){
		return likeService.findCommentLikeById(commentLikeId);
	}
	/**
	 * 删除缓存 评论点赞
	 * @param commentLikeId 评论点赞Id
	 * @return
	 */
	@CacheEvict(value="likeManage_cache_findCommentLikeById",key="#commentLikeId")
	public void delete_cache_findCommentLikeById(String commentLikeId){
	}
    
    
	/**
	 * 查询缓存 根据评论Id查询被点赞数量
	 * @param commentId 评论Id
	 * @return
	 */
	@Cacheable(value="likeManage_cache_findLikeCountByCommentId",key="#commentId")
	public Long query_cache_findLikeCountByCommentId(Long commentId){
		return likeService.findLikeCountByCommentId(commentId);
	}
	/**
	 * 删除缓存 根据评论Id查询被点赞数量
	 * @param commentId 评论Id
	 * @return
	 */
	@CacheEvict(value="likeManage_cache_findLikeCountByCommentId",key="#commentId")
	public void delete_cache_findLikeCountByCommentId(Long commentId){
	}
	
	
	/**
	 * 查询缓存 查询评论回复点赞
	 * @param commentReplyLikeId 评论回复点赞Id
	 * @return
	 */
	@Cacheable(value="likeManage_cache_findCommentReplyLikeById",key="#commentReplyLikeId")
	public CommentReplyLike query_cache_findCommentReplyLikeById(String commentReplyLikeId){
		return likeService.findCommentReplyLikeById(commentReplyLikeId);
	}
	/**
	 * 删除缓存 评论回复点赞
	 * @param commentReplyLikeId 评论回复点赞Id
	 * @return
	 */
	@CacheEvict(value="likeManage_cache_findCommentReplyLikeById",key="#commentReplyLikeId")
	public void delete_cache_findCommentReplyLikeById(String commentReplyLikeId){
	}
    
    
	/**
	 * 查询缓存 根据评论回复Id查询被点赞数量
	 * @param commentReplyId 评论回复Id
	 * @return
	 */
	@Cacheable(value="likeManage_cache_findLikeCountByCommentReplyId",key="#commentReplyId")
	public Long query_cache_findLikeCountByCommentReplyId(Long commentReplyId){
		return likeService.findLikeCountByCommentReplyId(commentReplyId);
	}
	/**
	 * 删除缓存 根据评论回复Id查询被点赞数量
	 * @param commentReplyId 评论回复Id
	 * @return
	 */
	@CacheEvict(value="likeManage_cache_findLikeCountByCommentReplyId",key="#commentReplyId")
	public void delete_cache_findLikeCountByCommentReplyId(Long commentReplyId){
	}
	
	
	/**
	 * 查询缓存 查询问题点赞
	 * @param questionLikeId 问题点赞Id
	 * @return
	 */
	@Cacheable(value="likeManage_cache_findQuestionLikeById",key="#questionLikeId")
	public QuestionLike query_cache_findQuestionLikeById(String questionLikeId){
		return likeService.findQuestionLikeById(questionLikeId);
	}
	/**
	 * 删除缓存 问题点赞
	 * @param questionLikeId 问题点赞Id
	 * @return
	 */
	@CacheEvict(value="likeManage_cache_findQuestionLikeById",key="#questionLikeId")
	public void delete_cache_findQuestionLikeById(String questionLikeId){
	}
	
	
	/**
	 * 查询缓存 根据问题Id查询被点赞数量
	 * @param questionId 问题Id
	 * @return
	 */
	@Cacheable(value="likeManage_cache_findLikeCountByQuestionId",key="#questionId")
	public Long query_cache_findLikeCountByQuestionId(Long questionId){
		return likeService.findLikeCountByQuestionId(questionId);
	}
	/**
	 * 删除缓存 根据问题Id查询被点赞数量
	 * @param questionId 问题Id
	 * @return
	 */
	@CacheEvict(value="likeManage_cache_findLikeCountByQuestionId",key="#questionId")
	public void delete_cache_findLikeCountByQuestionId(Long questionId){
	}
    
	
	/**
	 * 查询缓存 查询答案点赞
	 * @param answerLikeId 答案点赞Id
	 * @return
	 */
	@Cacheable(value="likeManage_cache_findAnswerLikeById",key="#answerLikeId")
	public AnswerLike query_cache_findAnswerLikeById(String answerLikeId){
		return likeService.findAnswerLikeById(answerLikeId);
	}
	/**
	 * 删除缓存 答案点赞
	 * @param answerLikeId 答案点赞Id
	 * @return
	 */
	@CacheEvict(value="likeManage_cache_findAnswerLikeById",key="#answerLikeId")
	public void delete_cache_findAnswerLikeById(String answerLikeId){
	}
    
    
	/**
	 * 查询缓存 根据答案Id查询被点赞数量
	 * @param answerId 答案Id
	 * @return
	 */
	@Cacheable(value="likeManage_cache_findLikeCountByAnswerId",key="#answerId")
	public Long query_cache_findLikeCountByAnswerId(Long answerId){
		return likeService.findLikeCountByAnswerId(answerId);
	}
	/**
	 * 删除缓存 根据答案Id查询被点赞数量
	 * @param answerId 答案Id
	 * @return
	 */
	@CacheEvict(value="likeManage_cache_findLikeCountByAnswerId",key="#answerId")
	public void delete_cache_findLikeCountByAnswerId(Long answerId){
	}
	
	
	/**
	 * 查询缓存 查询答案回复点赞
	 * @param answerReplyLikeId 答案回复点赞Id
	 * @return
	 */
	@Cacheable(value="likeManage_cache_findAnswerReplyLikeById",key="#answerReplyLikeId")
	public AnswerReplyLike query_cache_findAnswerReplyLikeById(String answerReplyLikeId){
		return likeService.findAnswerReplyLikeById(answerReplyLikeId);
	}
	/**
	 * 删除缓存 答案回复点赞
	 * @param answerReplyLikeId 答案回复点赞Id
	 * @return
	 */
	@CacheEvict(value="likeManage_cache_findAnswerReplyLikeById",key="#answerReplyLikeId")
	public void delete_cache_findAnswerReplyLikeById(String answerReplyLikeId){
	}
    
    
	/**
	 * 查询缓存 根据答案回复Id查询被点赞数量
	 * @param answerReplyId 答案回复Id
	 * @return
	 */
	@Cacheable(value="likeManage_cache_findLikeCountByAnswerReplyId",key="#answerReplyId")
	public Long query_cache_findLikeCountByAnswerReplyId(Long answerReplyId){
		return likeService.findLikeCountByAnswerReplyId(answerReplyId);
	}
	/**
	 * 删除缓存 根据答案回复Id查询被点赞数量
	 * @param answerReplyId 答案回复Id
	 * @return
	 */
	@CacheEvict(value="likeManage_cache_findLikeCountByAnswerReplyId",key="#answerReplyId")
	public void delete_cache_findLikeCountByAnswerReplyId(Long answerReplyId){
	}
	    
}
