package cms.web.action.follow;



import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import cms.bean.follow.Follow;
import cms.bean.message.Remind;
import cms.bean.topic.Comment;
import cms.bean.topic.Reply;
import cms.bean.topic.Topic;
import cms.bean.user.User;
import cms.service.follow.FollowService;
import cms.service.message.RemindService;
import cms.service.topic.CommentService;
import cms.service.topic.TopicService;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.web.action.message.RemindManage;
import cms.web.action.user.UserManage;
import net.sf.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 关注管理
 *
 */
@Component("followManage")
public class FollowManage {
	private static final Logger logger = LogManager.getLogger(FollowManage.class);
	
    @Resource FollowConfig followConfig;
    @Resource FollowService followService;
    @Resource UserManage userManage;
    @Resource RemindService  remindService;
    @Resource TopicService topicService;
    @Resource RemindManage remindManage;
    @Resource CommentService commentService;
    @Lazy @Resource FollowManage followManage;
    
	/**
	 * 取得关注Id的用户Id(后N位)
	 * @param followId 关注Id
	 * @return
	 */
    public int getFollowAfterId(String followId){
    	String[] idGroup = followId.split("-");
    	Long userId = Long.parseLong(idGroup[1]);
    	String afterUserId = String.format("%04d", userId%10000);
    	return Integer.parseInt(afterUserId);
    } 
    
    /**
     * 生成关注Id
     * 关注Id由(对方用户Id-关注的用户Id)组成
     * @param userId 用户Id
     * @param friendUserId 对方用户Id
     * @return
     */
    public String createFollowId(Long userId,Long friendUserId){
    	return String.valueOf(friendUserId)+"-"+String.valueOf(userId);
    }
    /**
     * 校验关注Id
     * 关注Id要判断按横杆分割后是数字
     * @param followId 关注Id
     * @return
     */
    public boolean verificationFollowId(String followId){
    	if(followId != null && !"".equals(followId.trim())){
    		String[] idGroup = followId.split("-");
    		if(idGroup.length ==2){
    			boolean verification_1 = Verification.isPositiveIntegerZero(idGroup[0]);//数字
    			boolean verification_2 = Verification.isPositiveIntegerZero(idGroup[1]);//数字
    			if(verification_1 && verification_2){
					return true;
				}
    		}
    	}
    	return false;
    }
    
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
				c = Class.forName("cms.bean.follow.Follow_"+tableNumber);
				Object object = c.newInstance();
				
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
		List<Follow> followList = followManage.query_cache_findAllFollow(userId, userName);

		for(Follow follow :followList){
			//标记Id
			String uuid = UUIDUtil.getUUID22();
			String flagId = followManage.query_cache_userUpdateFlag(follow.getFriendUserName(), uuid);
			if(flagId.equals(uuid)){
				
				//最近话题发送时间
				Date topic_sendTime = follow.getAddtime();
				//最近评论发送时间
				Date comment_sendTime = follow.getAddtime();
				//最近回复发送时间
				Date reply_sendTime = follow.getAddtime();

				User user = userManage.query_cache_findUserByUserName(follow.getFriendUserName());
				if(user != null){
					Remind remind_90 =  remindService.findNewRemindByUserId(userId,90);//90.我关注的人发表了话题
					if(remind_90 != null){
						topic_sendTime = new Timestamp(remind_90.getSendTimeFormat());
					}
					//发送话题提醒
					this.sendTopic(userId,userName,user.getId(),follow.getFriendUserName(),topic_sendTime);

					
					Remind remind_100 =  remindService.findNewRemindByUserId(userId,100);//100.我关注的人发表了评论
					if(remind_100 != null){
						comment_sendTime = new Timestamp(remind_100.getSendTimeFormat());
					}
					
					//发送评论提醒
					this.sendComment(userId,userName,user.getId(),follow.getFriendUserName(),comment_sendTime);
					
					Remind remind_110 =  remindService.findNewRemindByUserId(userId,110);//110.我关注的人发表了回复
					if(remind_110 != null){
						reply_sendTime = new Timestamp(remind_110.getSendTimeFormat());
					}
					//发送回复提醒
					this.sendReply(userId,userName,user.getId(),follow.getFriendUserName(),reply_sendTime);
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
	private void sendTopic(Long userId,String userName,Long friendUserId,String friendUserName,Date postTime){
		int page = 1;//分页 当前页
		int maxresult = 200;// 每页显示记录数

				
		while(true){
			//当前页
			int firstindex = (page-1)*maxresult;
			//查询话题
			List<Topic> topicList = topicService.findTopicByPage(friendUserName,postTime,firstindex, maxresult);
			
			if(topicList == null || topicList.size() == 0){
				break;
			}
			
			
			
			//提交提醒
			for(Topic topic : topicList){
				Remind remind = new Remind();
				remind.setId(remindManage.createRemindId(userId));
				remind.setReceiverUserId(userId);//接收提醒用户Id
				remind.setSenderUserId(friendUserId);//发送用户Id
				remind.setTypeCode(90);//90.我关注的人发表了话题
				remind.setSendTimeFormat(topic.getPostTime().getTime());//发送时间格式化
				remind.setTopicId(topic.getId());//话题Id
				
				Object remind_object = remindManage.createRemindObject(remind);
				remindService.saveRemind(remind_object);
				
				//删除提醒缓存
				remindManage.delete_cache_findUnreadRemindByUserId(userId);
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
	private void sendComment(Long userId,String userName,Long friendUserId,String friendUserName,Date postTime){
		int page = 1;//分页 当前页
		int maxresult = 200;// 每页显示记录数
		
		while(true){
			//当前页
			int firstindex = (page-1)*maxresult;
			//查询话题
			List<Comment> commentList = commentService.findCommentByPage(friendUserName,postTime,firstindex, maxresult);
			if(commentList == null || commentList.size() == 0){
				break;
			}
			
			
			
			//提交提醒
			for(Comment comment : commentList){
				Remind remind = new Remind();
				remind.setId(remindManage.createRemindId(userId));
				remind.setReceiverUserId(userId);//接收提醒用户Id
				remind.setSenderUserId(friendUserId);//发送用户Id
				remind.setTypeCode(100);//100.我关注的人发表了评论
				remind.setSendTimeFormat(comment.getPostTime().getTime());//发送时间格式化
				remind.setTopicId(comment.getTopicId());//话题Id
				remind.setFriendTopicCommentId(comment.getId());//对方的话题评论Id
				
				Object remind_object = remindManage.createRemindObject(remind);
				remindService.saveRemind(remind_object);
				
				//删除提醒缓存
				remindManage.delete_cache_findUnreadRemindByUserId(userId);
				
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
	 * @param postTime 评论发表时间
	 */
	private void sendReply(Long userId,String userName,Long friendUserId,String friendUserName,Date postTime){
		
		int page = 1;//分页 当前页
		int maxresult = 200;// 每页显示记录数
		
		while(true){
			//当前页
			int firstindex = (page-1)*maxresult;
			//查询话题
			List<Reply> replyList = commentService.findReplyByPage(friendUserName,postTime,firstindex, maxresult);
			if(replyList == null || replyList.size() == 0){
				break;
			}
			
			
			
			//提交提醒
			for(Reply reply : replyList){
				Remind remind = new Remind();
				remind.setId(remindManage.createRemindId(userId));
				remind.setReceiverUserId(userId);//接收提醒用户Id
				remind.setSenderUserId(friendUserId);//发送用户Id
				remind.setTypeCode(110);//110.我关注的人发表了回复
				remind.setSendTimeFormat(reply.getPostTime().getTime());//发送时间格式化
				remind.setTopicId(reply.getTopicId());//话题Id
				
				
				remind.setFriendTopicCommentId(reply.getCommentId());//对方的话题评论Id
				remind.setFriendTopicReplyId(reply.getId());//对方的话题回复Id
				
				
				Object remind_object = remindManage.createRemindObject(remind);
				remindService.saveRemind(remind_object);
				
				//删除提醒缓存
				remindManage.delete_cache_findUnreadRemindByUserId(userId);
				
			}

			page++;
		}
	}
    /**
	 * 查询缓存 查询关注
	 * @param followId 关注Id
	 * @return
	 */
	@Cacheable(value="followManage_cache_findById",key="#followId")
	public Follow query_cache_findById(String followId){
		return followService.findById(followId);
	}
	/**
	 * 删除缓存 关注
	 * @param followId 关注Id
	 * @return
	 */
	@CacheEvict(value="followManage_cache_findById",key="#followId")
	public void delete_cache_findById(String followId){
	}
	
	
	/**
	 * 查询缓存 我关注的用户的更新标记
	 * @param userName 用户名称
	 * @param uuid 标记Id
	 * @return
	 */
	@Cacheable(value="followManage_cache_userUpdateFlag",key="#userName")
	public String query_cache_userUpdateFlag(String userName,String uuid){
		return uuid;
	}
	/**
	 * 删除缓存 我关注的用户的更新标记
	 * @param userName 用户名称
	 * @return
	 */
	@CacheEvict(value="followManage_cache_userUpdateFlag",key="#userName")
	public void delete_cache_userUpdateFlag(String userName){
	}
	
	/**
	 * 查询缓存 根据用户名称查询所有关注
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @return
	 */
	@Cacheable(value="followManage_cache_findAllFollow",key="#userName")
	public List<Follow> query_cache_findAllFollow(Long userId,String userName){
		return followService.findAllFollow(userId,userName);
	}
	/**
	 * 删除缓存 根据用户名称查询所有关注
	 * @param userName 用户名称
	 * @return
	 */
	@CacheEvict(value="followManage_cache_findAllFollow",key="#userName")
	public void delete_cache_findAllFollow(String userName){
	}

	 /**
   	 * 查询缓存 查询关注总数
   	 * @param userName 用户名称
   	 * @return
   	 */
   	@Cacheable(value="followManage_cache_followCount",key="#userName")
   	public Long query_cache_followCount(Long userId, String userName){
   		return followService.findFollowCountByUserName(userId, userName);
   	}
   	/**
   	 * 删除缓存 关注总数
   	 * @param userName 用户名称
   	 * @return
   	 */
   	@CacheEvict(value="followManage_cache_followCount",key="#userName")
   	public void delete_cache_followCount(String userName){
   	}
	
}
