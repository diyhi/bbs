package cms.web.action.common;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import cms.bean.ErrorView;
import cms.bean.favorite.Favorites;
import cms.bean.favorite.QuestionFavorite;
import cms.bean.favorite.TopicFavorite;
import cms.bean.question.Question;
import cms.bean.topic.Topic;
import cms.bean.user.AccessUser;
import cms.bean.user.ResourceEnum;
import cms.service.favorite.FavoriteService;
import cms.service.template.TemplateService;
import cms.utils.Base64;
import cms.utils.JsonUtils;
import cms.utils.RefererCompare;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.CSRFTokenManage;
import cms.web.action.favorite.FavoriteManage;
import cms.web.action.question.QuestionManage;
import cms.web.action.topic.TopicManage;
import cms.web.action.user.RoleAnnotation;
import cms.web.taglib.Configuration;

/**
 * 收藏夹接收表单
 *
 */
@Controller
@RequestMapping("user/control/favorite") 
public class FavoriteFormAction {
	@Resource TemplateService templateService;
	
	
	@Resource FavoriteService favoriteService;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource FavoriteManage favoriteManage;
	
	@Resource CSRFTokenManage csrfTokenManage;
	@Resource TopicManage topicManage;
	@Resource QuestionManage questionManage;
	/**
	 * 收藏夹   添加
	 * @param model
	 * @param topicId 话题Id
	 * @param questionId 问题Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/add", method=RequestMethod.POST)
	@RoleAnnotation(resourceCode=ResourceEnum._3001000)
	public String add(ModelMap model,Long topicId,Long questionId,String token,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据

		
		Map<String,String> error = new HashMap<String,String>();
		
			
		
		//判断令牌
		if(token != null && !"".equals(token.trim())){	
			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
				if(!token_sessionid.equals(token)){
					error.put("token", ErrorView._13.name());//令牌错误
				}
			}else{
				error.put("token", ErrorView._12.name());//令牌过期
			}
		}else{
			error.put("token", ErrorView._11.name());//令牌为空
		}


		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	Topic topic = null;
	  	Question question = null;
  		if(topicId != null && topicId >0L){
	  		topic = topicManage.queryTopicCache(topicId);//查询缓存	
	  	}
	  	
	  	
  		if(questionId != null && questionId >0L){
  			question = questionManage.query_cache_findById(questionId);//查询缓存

	  	}
	  	
  		if(topic == null && question == null){
  			error.put("favorites", ErrorView._1580.name());//待收藏数据不存在
  		}
  		if(topic != null && question != null){
  			error.put("favorites", ErrorView._1590.name());//不允许同时收藏多项数据
  		}
	  	

	  	
	  	if(topic != null){
	  		//话题收藏Id
		  	String topicFavoriteId = favoriteManage.createTopicFavoriteId(topicId, accessUser.getUserId());
		  
		  	TopicFavorite topicFavorite = favoriteManage.query_cache_findTopicFavoriteById(topicFavoriteId);
	  		
	  		if(topicFavorite != null){
		  		error.put("topicFavorite", ErrorView._1520.name());//当前话题已经收藏
		  	}
		}
		if(question != null){
			//问题收藏Id
		  	String questionFavoriteId = favoriteManage.createQuestionFavoriteId(questionId, accessUser.getUserId());
		  
		  	QuestionFavorite questionFavorite = favoriteManage.query_cache_findQuestionFavoriteById(questionFavoriteId);
	  		
	  		if(questionFavorite != null){
		  		error.put("questionFavorite", ErrorView._1521.name());//当前问题已经收藏
		  	}
		}
	  	

		if(error.size() == 0){
			Date time = new Date();
			Favorites favorites = new Favorites();
			favorites.setId(favoriteManage.createFavoriteId(accessUser.getUserId()));
			favorites.setAddtime(time);
			favorites.setUserName(accessUser.getUserName());
			
			
			TopicFavorite topicFavorite = null;
			String topicFavoriteId = null;
			if(topic != null){
				favorites.setPostUserName(topic.getUserName());
				favorites.setModule(10);
				favorites.setTopicId(topicId);
				topicFavorite = new TopicFavorite();
				topicFavoriteId = favoriteManage.createTopicFavoriteId(topicId, accessUser.getUserId());
				topicFavorite.setId(topicFavoriteId);
				topicFavorite.setAddtime(time);
				topicFavorite.setTopicId(topicId);
				topicFavorite.setUserName(accessUser.getUserName());
				topicFavorite.setPostUserName(topic.getUserName());
			}
			
			QuestionFavorite questionFavorite = null;
			String questionFavoriteId = null;
			if(question != null){
				favorites.setPostUserName(question.getUserName());
				favorites.setModule(20);
				favorites.setQuestionId(questionId);
				questionFavorite = new QuestionFavorite();
				questionFavoriteId = favoriteManage.createQuestionFavoriteId(questionId, accessUser.getUserId());
				questionFavorite.setId(questionFavoriteId);
				questionFavorite.setAddtime(time);
				questionFavorite.setQuestionId(questionId);
				questionFavorite.setUserName(accessUser.getUserName());
				questionFavorite.setPostUserName(question.getUserName());
			}
			
			
			try {
				
				//删除收藏缓存
				if(topic != null){
					//保存收藏
					favoriteService.saveFavorite(favoriteManage.createFavoriteObject(favorites),favoriteManage.createTopicFavoriteObject(topicFavorite),null);
					
					favoriteManage.delete_cache_findTopicFavoriteById(topicFavoriteId);
					favoriteManage.delete_cache_findFavoriteCountByTopicId(favorites.getTopicId());
				}
				if(question != null){
					//保存收藏
					favoriteService.saveFavorite(favoriteManage.createFavoriteObject(favorites),null,favoriteManage.createQuestionFavoriteObject(questionFavorite));
					
					favoriteManage.delete_cache_findQuestionFavoriteById(questionFavoriteId);
					favoriteManage.delete_cache_findFavoriteCountByQuestionId(favorites.getQuestionId());
				}
			} catch (org.springframework.orm.jpa.JpaSystemException e) {
				error.put("favorites", ErrorView._1500.name());//重复收藏
				
			}
	
		}
		Map<String,String> returnError = new HashMap<String,String>();//错误
		if(error.size() >0){
			//将枚举数据转为错误提示字符
    		for (Map.Entry<String,String> entry : error.entrySet()) {		 
    			if(ErrorView.get(entry.getValue()) != null){
    				returnError.put(entry.getKey(),  ErrorView.get(entry.getValue()));
    			}else{
    				returnError.put(entry.getKey(),  entry.getValue());
    			}
			}
		}
		if(isAjax == true){
			
    		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
    		
    		if(error != null && error.size() >0){
    			returnValue.put("success", "false");
    			returnValue.put("error", returnError);	
    		}else{
    			returnValue.put("success", "true");
    		}
    		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			
			
			if(error != null && error.size() >0){//如果有错误
				redirectAttrs.addFlashAttribute("error", returnError);//重定向传参
				

				String referer = request.getHeader("referer");	
				
				
				referer = StringUtils.removeStartIgnoreCase(referer,Configuration.getUrl(request));//移除开始部分的相同的字符,不区分大小写
				referer = StringUtils.substringBefore(referer, ".");//截取到等于第二个参数的字符串为止
				referer = StringUtils.substringBefore(referer, "?");//截取到等于第二个参数的字符串为止
				
				String queryString = request.getQueryString() != null && !"".equals(request.getQueryString().trim()) ? "?"+request.getQueryString() :"";

				return "redirect:/"+referer+queryString;
	
			}
			
			
			if(jumpUrl != null && !"".equals(jumpUrl.trim())){
				String url = Base64.decodeBase64URL(jumpUrl.trim());
				
				return "redirect:"+url;
			}else{//默认跳转
				model.addAttribute("message", "加入收藏夹成功");
				String referer = request.getHeader("referer");
				if(RefererCompare.compare(request, "login")){//如果是登录页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				model.addAttribute("urlAddress", referer);
				
				String dirName = templateService.findTemplateDir_cache();
				
				
				return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/jump";	
			}
		}
	}
	
	

	
	
}
