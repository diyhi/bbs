package cms.web.action.template.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cms.bean.like.AnswerLike;
import cms.bean.like.AnswerReplyLike;
import cms.bean.like.CommentLike;
import cms.bean.like.CommentReplyLike;
import cms.bean.like.QuestionLike;
import cms.bean.like.TopicLike;
import cms.bean.template.Forum;
import cms.bean.user.AccessUser;
import cms.service.like.LikeService;
import cms.utils.Verification;
import cms.web.action.like.LikeManage;

/**
 * 点赞 -- 模板方法实现
 *
 */
@Component("like_TemplateManage")
public class Like_TemplateManage {
	@Resource LikeService likeService; 
	@Resource LikeManage likeManage;
	
	/**
	 * 点赞总数  -- 实体对象
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public Long likeCount_entityBean(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){	
		Long itemId = null;
		Integer module = null;
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("itemId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							itemId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}else if("module".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=2){
							module = Integer.parseInt(paramIter.getValue().toString());
						}
					}
				}
			}
		}
		if(itemId != null && itemId >0L && module != null){
			
			//根据话题Id查询被点赞数量
			if(module.equals(10)){
				return likeManage.query_cache_findLikeCountByTopicId(itemId);
		  	}else if(module.equals(20)){
		  		return likeManage.query_cache_findLikeCountByCommentId(itemId);
		  	}else if(module.equals(30)){
		  		return likeManage.query_cache_findLikeCountByCommentReplyId(itemId);
		  	}else if(module.equals(40)){
				return likeManage.query_cache_findLikeCountByQuestionId(itemId);
		  	}else if(module.equals(50)){
		  		return likeManage.query_cache_findLikeCountByAnswerId(itemId);
		  	}else if(module.equals(60)){
		  		return likeManage.query_cache_findLikeCountByAnswerReplyId(itemId);
		  	}
		}
			
		return 0L;
		
	}
	
	/**
	 * 用户是否已经点赞该项  -- 实体对象
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public Boolean alreadyLiked_entityBean(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){	
		Long itemId = null;
		Integer module = null;
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("itemId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							itemId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}else if("module".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=2){
							module = Integer.parseInt(paramIter.getValue().toString());
						}
					}
				}
			}
		}
		
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		if(accessUser != null && module != null && itemId != null && itemId>0L){
			//项目收藏Id
		  	String itemLikeId = likeManage.createItemLikeId(itemId, accessUser.getUserId());
		  
		  	if(module.equals(10)){
		  		TopicLike topicLike = likeManage.query_cache_findTopicLikeById(itemLikeId);
		  		if(topicLike != null){
		  			return true;
			  	}
		  	}else if(module.equals(20)){
		  		CommentLike commentLike = likeManage.query_cache_findCommentLikeById(itemLikeId);
		  		if(commentLike != null){
		  			return true;
			  	}
		  	}else if(module.equals(30)){
		  		CommentReplyLike commentReplyLike = likeManage.query_cache_findCommentReplyLikeById(itemLikeId);
		  		if(commentReplyLike != null){
		  			return true;
			  	}
		  	}else if(module.equals(40)){
		  		QuestionLike questionLike = likeManage.query_cache_findQuestionLikeById(itemLikeId);
		  		if(questionLike != null){
		  			return true;
			  	}
		  	}else if(module.equals(50)){
		  		AnswerLike answerLike = likeManage.query_cache_findAnswerLikeById(itemLikeId);
		  		if(answerLike != null){
		  			return true;
			  	}
		  	}else if(module.equals(60)){
		  		AnswerReplyLike answerReplyLike = likeManage.query_cache_findAnswerReplyLikeById(itemLikeId);
		  		if(answerReplyLike != null){
		  			return true;
			  	}
		  	}
		  	
		}
		return false;
	}
	
	
	/**
	 * 给话题点赞
	 * @param forum
	 */
	public Map<String,Object> addLike_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		return value;
	}
}
