package cms.web.action.template.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cms.bean.favorite.QuestionFavorite;
import cms.bean.favorite.TopicFavorite;
import cms.bean.template.Forum;
import cms.bean.user.AccessUser;
import cms.service.favorite.FavoriteService;
import cms.utils.Verification;
import cms.web.action.favorite.FavoriteManage;

/**
 * 收藏夹 -- 模板方法实现
 *
 */
@Component("favorite_TemplateManage")
public class Favorite_TemplateManage {
	@Resource FavoriteService favoriteService; 
	@Resource FavoriteManage favoriteManage;
	
	/**
	 * 话题用户收藏总数  -- 实体对象
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public Long favoriteCount_entityBean(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){	
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
			return favoriteManage.query_cache_findFavoriteCountByTopicId(topicId);
		}else{
			return 0L;
		}
	}
	
	/**
	 * 用户是否已经收藏话题  -- 实体对象
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public Boolean alreadyCollected_entityBean(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){	
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
		if(accessUser != null && topicId != null && topicId >0L){
			//话题收藏Id
		  	String topicFavoriteId = favoriteManage.createTopicFavoriteId(topicId, accessUser.getUserId());
		  
		  	TopicFavorite topicFavorite = favoriteManage.query_cache_findTopicFavoriteById(topicFavoriteId);
	  		
	  		if(topicFavorite != null){
	  			return true;
		  	}
			
			
		}
		return false;
	}
	
	
	/**
	 * 问题用户收藏总数  -- 实体对象
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public Long questionFavoriteCount_entityBean(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){	
		Long questionId = null;
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("questionId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							questionId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}
			}
		}
		if(questionId != null && questionId >0L){
			
			//根据问题Id查询被收藏数量
			return favoriteManage.query_cache_findFavoriteCountByQuestionId(questionId);
		}else{
			return 0L;
		}
	}
	
	/**
	 * 用户是否已经收藏问题  -- 实体对象
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public Boolean alreadyFavoriteQuestion_entityBean(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){	
		Long questionId = null;
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("questionId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							questionId = Long.parseLong(paramIter.getValue().toString());	
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
		if(accessUser != null && questionId != null && questionId >0L){
			//问题收藏Id
		  	String questionFavoriteId = favoriteManage.createQuestionFavoriteId(questionId, accessUser.getUserId());
		  
		  	QuestionFavorite questionFavorite = favoriteManage.query_cache_findQuestionFavoriteById(questionFavoriteId);
	  		
	  		if(questionFavorite != null){
	  			return true;
		  	}
			
			
		}
		return false;
	}
	
	/**
	 * 加入收藏夹
	 * @param forum
	 */
	public Map<String,Object> addFavorite_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		return value;
	}
}
