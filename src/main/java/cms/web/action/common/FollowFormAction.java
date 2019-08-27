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
import cms.bean.follow.Follow;
import cms.bean.follow.Follower;
import cms.bean.message.Remind;
import cms.bean.user.AccessUser;
import cms.bean.user.ResourceEnum;
import cms.bean.user.User;
import cms.service.follow.FollowService;
import cms.service.message.RemindService;
import cms.service.template.TemplateService;
import cms.utils.Base64;
import cms.utils.JsonUtils;
import cms.utils.RefererCompare;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.CSRFTokenManage;
import cms.web.action.follow.FollowManage;
import cms.web.action.follow.FollowerManage;
import cms.web.action.message.RemindManage;
import cms.web.action.user.RoleAnnotation;
import cms.web.action.user.UserManage;
import cms.web.taglib.Configuration;

/**
 * 关注接收表单
 *
 */
@Controller
@RequestMapping("user/control/follow") 
public class FollowFormAction {
	@Resource TemplateService templateService;
	
	@Resource RemindService remindService;
	@Resource RemindManage remindManage;
	@Resource UserManage userManage;
	@Resource FollowService followService;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource FollowManage followManage;
	@Resource FollowerManage followerManage;
	@Resource CSRFTokenManage csrfTokenManage;
	/**
	 * 关注   添加
	 * @param model
	 * @param userName 用户名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/add", method=RequestMethod.POST)
	@RoleAnnotation(resourceCode=ResourceEnum._5001000)
	public String add(ModelMap model,String userName,String token,String jumpUrl,
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
	  	
	  	User user = null;
	  	if(userName != null && !"".equals(userName.trim())){
	  		user = userManage.query_cache_findUserByUserName(userName.trim());//查询缓存
	  	}else{
	  		error.put("follow", ErrorView._815.name());//用户名称不能为空
	  	}
	  	
	  	if(user != null){
	  		//关注Id
		  	String followId = followManage.createFollowId(accessUser.getUserId(), user.getId());
		  
		  	Follow follow = followManage.query_cache_findById(followId);
	  		
	  		if(follow != null){
		  		error.put("follow", ErrorView._1820.name());//当前用户已关注对方
		  	}
	  		
	  		if(user.getId().equals(accessUser.getUserId())){
	  			error.put("follow", ErrorView._1870.name());//不能关注自身
	  		}
	  		
		}else{
			error.put("follow", ErrorView._859.name());//用户不存在
		}
	  	
		if(error.size() == 0){
			Date time = new Date();
			Follow follow = new Follow();
			follow.setId(followManage.createFollowId(accessUser.getUserId(), user.getId()));
			follow.setAddtime(time);
			follow.setUserName(accessUser.getUserName());
			follow.setFriendUserName(user.getUserName());
			
			Follower follower = new Follower();
			follower.setId(followerManage.createFollowerId(user.getId(), accessUser.getUserId()));
			follower.setAddtime(time);
			follower.setUserName(user.getUserName());
			follower.setFriendUserName(accessUser.getUserName());
			try {
				//保存关注
				followService.saveFollow(followManage.createFollowObject(follow),followerManage.createFollowerObject(follower));
				
				//删除关注缓存
				followManage.delete_cache_findById(follow.getId());
				followerManage.delete_cache_followerCount(user.getUserName());
				followManage.delete_cache_findAllFollow(accessUser.getUserName());
				
				if(user != null && !user.getId().equals(accessUser.getUserId())){//会员关注自已不发提醒给自己
					
					//提醒对方
					Remind remind = new Remind();
					remind.setId(remindManage.createRemindId(user.getId()));
					remind.setReceiverUserId(user.getId());//接收提醒用户Id
					remind.setSenderUserId(accessUser.getUserId());//发送用户Id
					remind.setTypeCode(80);//80.别人关注了我
					remind.setSendTimeFormat(time.getTime());//发送时间格式化
					
					
					Object remind_object = remindManage.createRemindObject(remind);
					remindService.saveRemind(remind_object);
					
					//删除提醒缓存
					remindManage.delete_cache_findUnreadRemindByUserId(user.getId());
				}
				
			} catch (org.springframework.orm.jpa.JpaSystemException e) {
				error.put("follow", ErrorView._1800.name());//重复关注
				
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
				model.addAttribute("message", "关注该用户成功");
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
