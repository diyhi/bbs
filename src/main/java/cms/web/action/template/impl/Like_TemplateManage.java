package cms.web.action.template.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

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
	 * 话题点赞总数  -- 实体对象
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public Long likeCount_entityBean(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){	
		Long topicId = null;
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("topicId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							topicId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}
			}
		}
		if(topicId != null && topicId >0L){
			
			//根据话题Id查询被收藏数量
			return likeManage.query_cache_findLikeCountByTopicId(topicId);
		}else{
			return 0L;
		}
	}
	
	/**
	 * 用户是否已经点赞该话题  -- 实体对象
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public Boolean alreadyLiked_entityBean(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){	
		Long topicId = null;
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("topicId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							topicId = Long.parseLong(paramIter.getValue().toString());	
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
		if(accessUser != null && topicId != null && topicId>0L){
			//话题收藏Id
		  	String topicLikeId = likeManage.createTopicLikeId(topicId, accessUser.getUserId());
		  
		  	TopicLike topicLike = likeManage.query_cache_findTopicLikeById(topicLikeId);
	  		
	  		if(topicLike != null){
	  			return true;
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
