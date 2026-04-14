package cms.utils;

import java.io.File;
import java.util.Properties;

/**
 * 创建数据库分表实体类文件
 * @author Gao
 *
 */
public class CreateEntityFile {
	/**
     * 创建分表实体类文件
     */
    public static void create(){
    	
    	String path = PathUtil.rootPath()+File.separator+"cms"+File.separator+"model"+File.separator;
    	File file = new File(path);
    	if (!file.exists()) {
    		return;
    	}
    	
    	Properties properties = YmlUtils.getYml("application.yml");
    	
    	
    	
    	
    	//积分日志分表数量
    	int pointLogConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.pointLogConfig_tableQuantity"));
		if(pointLogConfig_tableQuantity >1){
			for(int i =1; i<pointLogConfig_tableQuantity; i++){
				
				//初始化积分日志bean
				CreateBean.createPointLogBean(i);
			}	
		}
		//用户登录日志分表数量
		int userLoginLogConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.userLoginLogConfig_tableQuantity"));
		if(userLoginLogConfig_tableQuantity >1){
			for(int i =1; i<userLoginLogConfig_tableQuantity; i++){
				
				//初始化登录日志bean
				CreateBean.createUserLoginLogBean(i);
			}	
		}
    	
    	//员工登录日志分表数量
		int staffLoginLogConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.staffLoginLogConfig_tableQuantity"));
		if(staffLoginLogConfig_tableQuantity >1){
			for(int i =1; i<staffLoginLogConfig_tableQuantity; i++){
				//初始化登录日志bean
				CreateBean.createStaffLoginLogBean(i);
			}	
		}
		//私信分表数量
		int privateMessageConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.privateMessageConfig_tableQuantity"));
		if(privateMessageConfig_tableQuantity >1){
			for(int i =1; i<privateMessageConfig_tableQuantity; i++){
				
				//初始化私信bean
				CreateBean.createPrivateMessageBean(i);
			}	
		}
		//订阅系统通知分表数量
		int subscriptionSystemNotifyConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.subscriptionSystemNotifyConfig_tableQuantity"));
		if(subscriptionSystemNotifyConfig_tableQuantity >1){
			for(int i =1; i<subscriptionSystemNotifyConfig_tableQuantity; i++){
				
				//初始化订阅系统通知bean
				CreateBean.createSubscriptionSystemNotifyBean(i);
			}	
		}
		//提醒分表数量
		int remindConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.remindConfig_tableQuantity"));
		if(remindConfig_tableQuantity >1){
			for(int i =1; i<remindConfig_tableQuantity; i++){
				
				//初始化提醒bean
				CreateBean.createRemindBean(i);
			}	
		}
		//收藏夹分表数量
		int favoritesConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.favoritesConfig_tableQuantity"));
		if(favoritesConfig_tableQuantity >1){
			for(int i =1; i<favoritesConfig_tableQuantity; i++){
				
				//初始化收藏夹bean
				CreateBean.createFavoritesBean(i);
			}	
		}
		//话题收藏分表数量
		int topicFavoriteConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.topicFavoriteConfig_tableQuantity"));
		if(topicFavoriteConfig_tableQuantity >1){
			for(int i =1; i<topicFavoriteConfig_tableQuantity; i++){
				
				//初始化话题收藏bean
				CreateBean.createTopicFavoriteBean(i);
			}	
		}
		//话题取消隐藏分表数量
		int topicUnhideConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.topicUnhideConfig_tableQuantity"));
		if(topicUnhideConfig_tableQuantity >1){
			for(int i =1; i<topicUnhideConfig_tableQuantity; i++){
				
				//初始化话题取消隐藏bean
				CreateBean.createTopicUnhideBean(i);
			}	
		}
		//用户动态分表数量
		int userDynamicConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.userDynamicConfig_tableQuantity"));
		if(userDynamicConfig_tableQuantity >1){
			for(int i =1; i<userDynamicConfig_tableQuantity; i++){
				
				//初始化用户动态bean
				CreateBean.createUserDynamicBean(i);
			}	
		}
		//点赞分表数量
		int likeConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.likeConfig_tableQuantity"));
		if(likeConfig_tableQuantity >1){
			for(int i =1; i<likeConfig_tableQuantity; i++){
				
				//初始化点赞bean
				CreateBean.createLikeBean(i);
			}	
		}
		//话题点赞分表数量
		int topicLikeConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.topicLikeConfig_tableQuantity"));
		if(topicLikeConfig_tableQuantity >1){
			for(int i =1; i<topicLikeConfig_tableQuantity; i++){
				
				//初始化话题点赞bean
				CreateBean.createTopicLikeBean(i);
			}	
		}
		//评论点赞分表数量
		int commentLikeConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.commentLikeConfig_tableQuantity"));
		if(commentLikeConfig_tableQuantity >1){
			for(int i =1; i<commentLikeConfig_tableQuantity; i++){
				
				//初始化评论点赞bean
				CreateBean.createCommentLikeBean(i);
			}	
		}
		//评论回复点赞分表数量
		int commentReplyLikeConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.commentReplyLikeConfig_tableQuantity"));
		if(commentReplyLikeConfig_tableQuantity >1){
			for(int i =1; i<commentReplyLikeConfig_tableQuantity; i++){
				
				//初始化评论回复点赞bean
				CreateBean.createCommentReplyLikeBean(i);
			}	
		}
		
		//问题点赞分表数量
		int questionLikeConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.questionLikeConfig_tableQuantity"));
		if(questionLikeConfig_tableQuantity >1){
			for(int i =1; i<questionLikeConfig_tableQuantity; i++){
				
				//初始化问题点赞bean
				CreateBean.createQuestionLikeBean(i);
			}	
		}
		//答案点赞分表数量
		int answerLikeConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.answerLikeConfig_tableQuantity"));
		if(answerLikeConfig_tableQuantity >1){
			for(int i =1; i<answerLikeConfig_tableQuantity; i++){
				
				//初始化答案点赞bean
				CreateBean.createAnswerLikeBean(i);
			}	
		}
		//答案回复点赞分表数量
		int answerReplyLikeConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.answerReplyLikeConfig_tableQuantity"));
		if(answerReplyLikeConfig_tableQuantity >1){
			for(int i =1; i<answerReplyLikeConfig_tableQuantity; i++){
				
				//初始化答案回复点赞bean
				CreateBean.createAnswerReplyLikeBean(i);
			}	
		}
		//关注分表数量
		int followConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.followConfig_tableQuantity"));
		if(followConfig_tableQuantity >1){
			for(int i =1; i<followConfig_tableQuantity; i++){
				
				//初始化关注bean
				CreateBean.createFollowBean(i);
			}	
		}
		//粉丝分表数量
		int followerConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.followerConfig_tableQuantity"));
		if(followerConfig_tableQuantity >1){
			for(int i =1; i<followerConfig_tableQuantity; i++){
				
				//初始化粉丝bean
				CreateBean.createFollowerBean(i);
			}	
		}
		//支付日志分表数量
		int paymentLogConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.paymentLogConfig_tableQuantity"));
		if(paymentLogConfig_tableQuantity >1){
			for(int i =1; i<paymentLogConfig_tableQuantity; i++){
				
				//初始化支付日志bean
				CreateBean.createPaymentLogBean(i);
			}	
		}
		//问题收藏分表数量
		int questionFavoriteConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.questionFavoriteConfig_tableQuantity"));
		if(questionFavoriteConfig_tableQuantity >1){
			for(int i =1; i<questionFavoriteConfig_tableQuantity; i++){
				
				//初始化问题收藏bean
				CreateBean.createQuestionFavoriteBean(i);
			}	
		}
		//收红包分表数量
		int receiveRedEnvelopeConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.receiveRedEnvelopeConfig_tableQuantity"));
		if(receiveRedEnvelopeConfig_tableQuantity >1){
			for(int i =1; i<receiveRedEnvelopeConfig_tableQuantity; i++){
				
				//初始化收红包bean
				CreateBean.createReceiveRedEnvelopeBean(i);
			}	
		}
		
		//会员卡赠送项分表数量
		int membershipCardGiftItemConfig_tableQuantity = Integer.parseInt(properties.getProperty("bbs.sharding.membershipCardGiftItemConfig_tableQuantity"));
		if(membershipCardGiftItemConfig_tableQuantity >1){
			for(int i =1; i<membershipCardGiftItemConfig_tableQuantity; i++){
				
				//初始化会员卡赠送项bean
				CreateBean.createMembershipCardGiftItem(i);
			}	
		}
		
    	
    }
}
