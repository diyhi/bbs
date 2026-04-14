package cms.component.follow;



import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;


import cms.component.message.RemindCacheManager;
import cms.component.message.RemindComponent;
import cms.component.message.RemindConfig;
import cms.component.user.UserCacheManager;
import cms.model.follow.Follow;
import cms.model.message.Remind;
import cms.model.question.Answer;
import cms.model.question.AnswerReply;
import cms.model.question.Question;
import cms.model.topic.Comment;
import cms.model.topic.Reply;
import cms.model.topic.Topic;
import cms.model.user.User;
import cms.repository.message.RemindRepository;
import cms.repository.question.AnswerRepository;
import cms.repository.question.QuestionRepository;
import cms.repository.topic.CommentRepository;
import cms.repository.topic.TopicRepository;
import cms.utils.UUIDUtil;
import jakarta.annotation.Resource;
import org.springframework.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 关注组件
 *
 */
@Component("followComponent")
public class FollowComponent {
	private static final Logger logger = LogManager.getLogger(FollowComponent.class);
	
    @Resource FollowConfig followConfig;

    @Resource UserCacheManager userCacheManager;
    @Resource RemindRepository remindRepository;
    @Resource TopicRepository topicRepository;
    @Resource RemindComponent remindComponent;
    @Resource CommentRepository commentRepository;
    @Resource FollowCacheManager followCacheManager;
    @Resource QuestionRepository questionRepository;
    @Resource AnswerRepository answerRepository;
    @Resource RemindConfig remindConfig;
    @Resource RemindCacheManager remindCacheManager;
    
    /**
     * 生成关注对象
     * @return
     */
    public Object createFollowObject(Follow follow){
    	//表编号
		int tableNumber = followConfig.followIdRemainder(follow.getId());
		if(tableNumber == 0){//默认对象
			return follow;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.model.follow.Follow_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
				BeanCopier copier = BeanCopier.create(Follow.class,object.getClass(), false); 
			
				copier.copy(follow,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成关注对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成关注对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成关注对象",e);
		        }
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成关注对象",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成关注对象",e);
                }
            }
		}
		return null;
    }
    
    /**
	 * 异步拉取关注的用户消息
	 * @param userId 用户Id
	 * @param userName 用户名称
	 */
    @Async
	public void pullFollow(Long userId,String userName){
		//查询我关注的用户
		List<Follow> followList = followCacheManager.query_cache_findAllFollow(userId, userName);

		for(Follow follow :followList){
			//标记Id
			String uuid = UUIDUtil.getUUID22();
			String flagId = followCacheManager.query_cache_userUpdateFlag(follow.getFriendUserName(), uuid);
			if(flagId.equals(uuid)){
				
				//最近话题发送时间

                LocalDateTime topic_sendTime = follow.getAddtime();
				//最近评论发送时间

                LocalDateTime comment_sendTime = follow.getAddtime();
				//最近回复发送时间

                LocalDateTime reply_sendTime = follow.getAddtime();
				
				//最近问题发送时间

                LocalDateTime question_sendTime = follow.getAddtime();
				//最近答案发送时间

                LocalDateTime answer_sendTime = follow.getAddtime();
				//最近答案回复发送时间

                LocalDateTime answerReply_sendTime = follow.getAddtime();

				User user = userCacheManager.query_cache_findUserByUserName(follow.getFriendUserName());
				if(user != null){
					Remind remind_90 =  remindRepository.findNewRemindByUserId(userId,90);//90.我关注的人发表了话题
					if(remind_90 != null){
						topic_sendTime = Instant.ofEpochMilli(remind_90.getSendTimeFormat()).atZone(ZoneId.systemDefault()).toLocalDateTime();
					}
					//发送话题提醒
					this.sendTopic(userId,userName,user.getId(),follow.getFriendUserName(),topic_sendTime);

					
					Remind remind_100 =  remindRepository.findNewRemindByUserId(userId,100);//100.我关注的人发表了评论
					if(remind_100 != null){
						comment_sendTime = Instant.ofEpochMilli(remind_100.getSendTimeFormat()).atZone(ZoneId.systemDefault()).toLocalDateTime();
					}
					
					//发送评论提醒
					this.sendComment(userId,userName,user.getId(),follow.getFriendUserName(),comment_sendTime);
					
					Remind remind_110 =  remindRepository.findNewRemindByUserId(userId,110);//110.我关注的人发表了回复
					if(remind_110 != null){
						reply_sendTime = Instant.ofEpochMilli(remind_110.getSendTimeFormat()).atZone(ZoneId.systemDefault()).toLocalDateTime();
					}
					//发送回复提醒
					this.sendReply(userId,userName,user.getId(),follow.getFriendUserName(),reply_sendTime);
					
					
					Remind remind_170 =  remindRepository.findNewRemindByUserId(userId,170);//170:我关注的人提了问题
					if(remind_170 != null){
						question_sendTime = Instant.ofEpochMilli(remind_170.getSendTimeFormat()).atZone(ZoneId.systemDefault()).toLocalDateTime();
					}
					//发送问题提醒
					this.sendQuestion(userId,userName,user.getId(),follow.getFriendUserName(),question_sendTime);
					
					Remind remind_180 =  remindRepository.findNewRemindByUserId(userId,180);//180.我关注的人回答了问题
					if(remind_180 != null){
						answer_sendTime = Instant.ofEpochMilli(remind_180.getSendTimeFormat()).atZone(ZoneId.systemDefault()).toLocalDateTime();
					}
					//发送答案提醒
					this.sendAnswer(userId,userName,user.getId(),follow.getFriendUserName(),answer_sendTime);
					
					
					
					Remind remind_190 =  remindRepository.findNewRemindByUserId(userId,190);//190.我关注的人发表了答案回复
					if(remind_190 != null){
						answerReply_sendTime = Instant.ofEpochMilli(remind_190.getSendTimeFormat()).atZone(ZoneId.systemDefault()).toLocalDateTime();
					}
					//发送答案回复提醒
					this.sendAnswerReply(userId,userName,user.getId(),follow.getFriendUserName(),answerReply_sendTime);
					
				}
			}
		}
	}
	
	/**
	 * 发送话题提醒
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param friendUserId 对方用户Id
	 * @param friendUserName 对方用户名称
	 * @param postTime 话题发表时间
	 */
	private void sendTopic(Long userId,String userName,Long friendUserId,String friendUserName,LocalDateTime postTime){
		int page = 1;//分页 当前页
		int maxresult = 200;// 每页显示记录数

				
		while(true){
			//当前页
			int firstindex = (page-1)*maxresult;
			//查询话题
			List<Topic> topicList = topicRepository.findTopicByPage(friendUserName,postTime,firstindex, maxresult);
			
			if(topicList == null || topicList.size() == 0){
				break;
			}
			
			
			
			//提交提醒
			for(Topic topic : topicList){
				Remind remind = new Remind();
				remind.setId(remindConfig.createRemindId(userId));
				remind.setReceiverUserId(userId);//接收提醒用户Id
				remind.setSenderUserId(friendUserId);//发送用户Id
				remind.setTypeCode(90);//90.我关注的人发表了话题
				remind.setSendTimeFormat(topic.getPostTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
				remind.setTopicId(topic.getId());//话题Id
				
				Object remind_object = remindComponent.createRemindObject(remind);
				remindRepository.saveRemind(remind_object);
				
				//删除提醒缓存
				remindCacheManager.delete_cache_findUnreadRemindByUserId(userId);
			}

			page++;
		}
	}
	
	/**
	 * 发送评论提醒
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param friendUserId 对方用户Id
	 * @param friendUserName 对方用户名称
	 * @param postTime 评论发表时间
	 */
	private void sendComment(Long userId,String userName,Long friendUserId,String friendUserName,LocalDateTime postTime){
		int page = 1;//分页 当前页
		int maxresult = 200;// 每页显示记录数
		
		while(true){
			//当前页
			int firstindex = (page-1)*maxresult;
			//查询话题
			List<Comment> commentList = commentRepository.findCommentByPage(friendUserName,postTime,firstindex, maxresult);
			if(commentList == null || commentList.size() == 0){
				break;
			}
			
			
			
			//提交提醒
			for(Comment comment : commentList){
				Remind remind = new Remind();
				remind.setId(remindConfig.createRemindId(userId));
				remind.setReceiverUserId(userId);//接收提醒用户Id
				remind.setSenderUserId(friendUserId);//发送用户Id
				remind.setTypeCode(100);//100.我关注的人发表了评论
				remind.setSendTimeFormat(comment.getPostTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
				remind.setTopicId(comment.getTopicId());//话题Id
				remind.setFriendTopicCommentId(comment.getId());//对方的话题评论Id
				
				Object remind_object = remindComponent.createRemindObject(remind);
				remindRepository.saveRemind(remind_object);
				
				//删除提醒缓存
                remindCacheManager.delete_cache_findUnreadRemindByUserId(userId);
				
			}

			page++;
		}
	}
	
	/**
	 * 发送回复提醒
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param friendUserId 对方用户Id
	 * @param friendUserName 对方用户名称
	 * @param postTime 评论回复发表时间
	 */
	private void sendReply(Long userId,String userName,Long friendUserId,String friendUserName,LocalDateTime postTime){
		
		int page = 1;//分页 当前页
		int maxresult = 200;// 每页显示记录数
		
		while(true){
			//当前页
			int firstindex = (page-1)*maxresult;
			//查询话题
			List<Reply> replyList = commentRepository.findReplyByPage(friendUserName,postTime,firstindex, maxresult);
			if(replyList == null || replyList.size() == 0){
				break;
			}
			
			
			
			//提交提醒
			for(Reply reply : replyList){
				Remind remind = new Remind();
				remind.setId(remindConfig.createRemindId(userId));
				remind.setReceiverUserId(userId);//接收提醒用户Id
				remind.setSenderUserId(friendUserId);//发送用户Id
				remind.setTypeCode(110);//110.我关注的人发表了回复
				remind.setSendTimeFormat(reply.getPostTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
				remind.setTopicId(reply.getTopicId());//话题Id
				
				
				remind.setFriendTopicCommentId(reply.getCommentId());//对方的话题评论Id
				remind.setFriendTopicReplyId(reply.getId());//对方的话题回复Id
				
				
				Object remind_object = remindComponent.createRemindObject(remind);
				remindRepository.saveRemind(remind_object);
				
				//删除提醒缓存
                remindCacheManager.delete_cache_findUnreadRemindByUserId(userId);
				
			}

			page++;
		}
	}
	
	
	
	
	/**
	 * 发送问题提醒
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param friendUserId 对方用户Id
	 * @param friendUserName 对方用户名称
	 * @param postTime 问题发表时间
	 */
	private void sendQuestion(Long userId,String userName,Long friendUserId,String friendUserName,LocalDateTime postTime){
		int page = 1;//分页 当前页
		int maxresult = 200;// 每页显示记录数

				
		while(true){
			//当前页
			int firstindex = (page-1)*maxresult;
			//查询问题
			List<Question> questionList = questionRepository.findQuestionByPage(friendUserName,postTime,firstindex, maxresult);
			
			if(questionList == null || questionList.size() == 0){
				break;
			}
			
			
			
			//提交提醒
			for(Question question : questionList){
				Remind remind = new Remind();
				remind.setId(remindConfig.createRemindId(userId));
				remind.setReceiverUserId(userId);//接收提醒用户Id
				remind.setSenderUserId(friendUserId);//发送用户Id
				remind.setTypeCode(170);//170:我关注的人提了问题
				remind.setSendTimeFormat(question.getPostTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
				remind.setQuestionId(question.getId());//问题Id
				
				Object remind_object = remindComponent.createRemindObject(remind);
				remindRepository.saveRemind(remind_object);
				
				//删除提醒缓存
                remindCacheManager.delete_cache_findUnreadRemindByUserId(userId);
			}

			page++;
		}
	}
	
	
	
	/**
	 * 发送答案提醒
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param friendUserId 对方用户Id
	 * @param friendUserName 对方用户名称
	 * @param postTime 答案发表时间
	 */
	private void sendAnswer(Long userId,String userName,Long friendUserId,String friendUserName,LocalDateTime postTime){
		int page = 1;//分页 当前页
		int maxresult = 200;// 每页显示记录数
		
		while(true){
			//当前页
			int firstindex = (page-1)*maxresult;
			//查询答案
			List<Answer> answerList = answerRepository.findAnswerByPage(friendUserName,postTime,firstindex, maxresult);
			if(answerList == null || answerList.size() == 0){
				break;
			}
			
			
			
			//提交提醒
			for(Answer answer : answerList){
				Remind remind = new Remind();
				remind.setId(remindConfig.createRemindId(userId));
				remind.setReceiverUserId(userId);//接收提醒用户Id
				remind.setSenderUserId(friendUserId);//发送用户Id
				remind.setTypeCode(180);//180.我关注的人回答了问题
				remind.setSendTimeFormat(answer.getPostTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
				remind.setQuestionId(answer.getQuestionId());//答案Id
				remind.setFriendQuestionAnswerId(answer.getId());//对方的问题答案Id
				
				Object remind_object = remindComponent.createRemindObject(remind);
				remindRepository.saveRemind(remind_object);
				
				//删除提醒缓存
                remindCacheManager.delete_cache_findUnreadRemindByUserId(userId);
				
			}

			page++;
		}
	}
	
	/**
	 * 发送答案回复提醒
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param friendUserId 对方用户Id
	 * @param friendUserName 对方用户名称
	 * @param postTime 答案回复发表时间
	 */
	private void sendAnswerReply(Long userId,String userName,Long friendUserId,String friendUserName,LocalDateTime postTime){
		
		int page = 1;//分页 当前页
		int maxresult = 200;// 每页显示记录数
		
		while(true){
			//当前页
			int firstindex = (page-1)*maxresult;
			//查询答案回复
			List<AnswerReply> replyList = answerRepository.findReplyByPage(friendUserName,postTime,firstindex, maxresult);
			if(replyList == null || replyList.size() == 0){
				break;
			}
			
			
			
			//提交提醒
			for(AnswerReply reply : replyList){
				Remind remind = new Remind();
				remind.setId(remindConfig.createRemindId(userId));
				remind.setReceiverUserId(userId);//接收提醒用户Id
				remind.setSenderUserId(friendUserId);//发送用户Id
				remind.setTypeCode(190);//190.我关注的人发表了答案回复
				remind.setSendTimeFormat(reply.getPostTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
				remind.setQuestionId(reply.getQuestionId());//问题Id
				
				
				remind.setFriendQuestionAnswerId(reply.getAnswerId());//对方的问题答案Id
				remind.setFriendQuestionReplyId(reply.getId());//对方的问题回复Id
				
				
				Object remind_object = remindComponent.createRemindObject(remind);
				remindRepository.saveRemind(remind_object);
				
				//删除提醒缓存
                remindCacheManager.delete_cache_findUnreadRemindByUserId(userId);
				
			}

			page++;
		}
	}

	
}
