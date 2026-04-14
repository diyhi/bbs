package cms.component.like;

import cms.model.like.*;
import cms.repository.like.LikeRepository;
import jakarta.annotation.Resource;
import org.springframework.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 点赞组件
 *
 */
@Component("likeComponent")
public class LikeComponent {
	private static final Logger logger = LogManager.getLogger(LikeComponent.class);
	
    @Resource LikeConfig likeConfig;

    @Resource TopicLikeConfig topicLikeConfig;
    @Resource CommentLikeConfig commentLikeConfig;
    @Resource CommentReplyLikeConfig commentReplyLikeConfig;
    @Resource AnswerLikeConfig answerLikeConfig;
    @Resource AnswerReplyLikeConfig answerReplyLikeConfig;
    @Resource QuestionLikeConfig questionLikeConfig;
    

    
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
				c = Class.forName("cms.model.like.Like_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
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
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成点赞对象",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成点赞对象",e);
                }
            }
		}
		return null;
    }
    
    
    
    /**---------------------------------------------- 话题点赞 ----------------------------------------------**/

    
    
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
				c = Class.forName("cms.model.like.TopicLike_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
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
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成话题点赞对象",e);
                }
            } catch (NoSuchMethodException e) {
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
				c = Class.forName("cms.model.like.CommentLike_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
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
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成评论点赞对象",e);
                }
            } catch (NoSuchMethodException e) {
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
				c = Class.forName("cms.model.like.CommentReplyLike_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
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
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成评论回复点赞对象",e);
                }
            } catch (NoSuchMethodException e) {
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
				c = Class.forName("cms.model.like.QuestionLike_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
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
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成问题点赞对象",e);
                }
            } catch (NoSuchMethodException e) {
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
				c = Class.forName("cms.model.like.AnswerLike_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
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
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成答案点赞对象",e);
                }
            } catch (NoSuchMethodException e) {
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
				c = Class.forName("cms.model.like.AnswerReplyLike_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
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
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成答案回复点赞对象",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成答案回复点赞对象",e);
                }
            }
		}
		return null;
    }
    


}
