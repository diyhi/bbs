package cms.web.action.common;


import java.util.Date;
import java.util.HashMap;
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
import cms.bean.like.Like;
import cms.bean.like.TopicLike;
import cms.bean.message.Remind;
import cms.bean.topic.Topic;
import cms.bean.user.AccessUser;
import cms.bean.user.User;
import cms.service.like.LikeService;
import cms.service.message.RemindService;
import cms.service.template.TemplateService;
import cms.utils.Base64;
import cms.utils.JsonUtils;
import cms.utils.RefererCompare;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.CSRFTokenManage;
import cms.web.action.like.LikeManage;
import cms.web.action.message.RemindManage;
import cms.web.action.topic.TopicManage;
import cms.web.action.user.UserManage;
import cms.web.taglib.Configuration;

/**
 * 点赞接收表单
 *
 */
@Controller
@RequestMapping("user/control/like") 
public class LikeFormAction {
	@Resource TemplateService templateService;
	
	@Resource RemindService remindService;
	@Resource RemindManage remindManage;
	@Resource UserManage userManage;
	@Resource LikeService likeService;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource LikeManage likeManage;
	
	@Resource CSRFTokenManage csrfTokenManage;
	@Resource TopicManage topicManage;
	/**
	 * 点赞   添加
	 * @param model
	 * @param topicId 话题表单Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(ModelMap model,Long topicId,String token,String jumpUrl,
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
	  	if(topicId != null && topicId >0L){
	  		
	  		topic = topicManage.queryTopicCache(topicId);//查询缓存

	  		
	  	}else{
	  		error.put("topicLike", ErrorView._1710.name());//话题点赞Id不能为空
	  	}
	  	
	  	if(topic != null){
	  		//话题点赞Id
		  	String topicLikeId = likeManage.createTopicLikeId(topicId, accessUser.getUserId());
		  
		  	TopicLike topicLike = likeManage.query_cache_findTopicLikeById(topicLikeId);
	  		
	  		if(topicLike != null){
		  		error.put("topicLike", ErrorView._1720.name());//当前话题已经点赞
		  	}
		}
	  	
	  	

		if(error.size() == 0){
			Date time = new Date();
			Like like = new Like();
			like.setId(likeManage.createLikeId(accessUser.getUserId()));
			like.setAddtime(time);
			like.setTopicId(topicId);
			like.setUserName(accessUser.getUserName());
			like.setPostUserName(topic.getUserName());
			
			TopicLike topicLike = new TopicLike();
			String topicLikeId = likeManage.createTopicLikeId(topicId, accessUser.getUserId());
			topicLike.setId(topicLikeId);
			topicLike.setAddtime(time);
			topicLike.setTopicId(topicId);
			topicLike.setUserName(accessUser.getUserName());
			topicLike.setPostUserName(topic.getUserName());
			try {
				//保存点赞
				likeService.saveLike(likeManage.createLikeObject(like),likeManage.createTopicLikeObject(topicLike));
				
				//删除点赞缓存
				likeManage.delete_cache_findTopicLikeById(topicLikeId);
				likeManage.delete_cache_findLikeCountByTopicId(like.getTopicId());
				
				
				
				User _user = userManage.query_cache_findUserByUserName(topic.getUserName());
				if(_user != null && !_user.getId().equals(accessUser.getUserId())){//楼主点赞不发提醒给自己
					
					//提醒楼主
					Remind remind = new Remind();
					remind.setId(remindManage.createRemindId(_user.getId()));
					remind.setReceiverUserId(_user.getId());//接收提醒用户Id
					remind.setSenderUserId(accessUser.getUserId());//发送用户Id
					remind.setTypeCode(70);//70.别人点赞了我的话题
					remind.setSendTimeFormat(new Date().getTime());//发送时间格式化
					remind.setTopicId(topic.getId());//话题Id
					
					Object remind_object = remindManage.createRemindObject(remind);
					remindService.saveRemind(remind_object);
					
					//删除提醒缓存
					remindManage.delete_cache_findUnreadRemindByUserId(_user.getId());
				}
				
			} catch (org.springframework.orm.jpa.JpaSystemException e) {
				error.put("like", ErrorView._1700.name());//重复点赞
				
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
				model.addAttribute("message", "给话题点赞成功");
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
