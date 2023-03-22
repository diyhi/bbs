package cms.web.action;


import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.staff.StaffLoginLog;
import cms.bean.staff.SysUsers;
import cms.service.staff.StaffService;
import cms.utils.CommentedProperties;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.utils.WebUtil;
import cms.web.action.common.CaptchaManage;
import cms.web.action.setting.SettingManage;
import cms.web.action.staff.StaffLoginLogManage;
import cms.web.action.staff.StaffManage;
import cms.web.taglib.Configuration;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * 员工登录 退出管理
 * @author Administrator
 *
 */
@Controller
public class AdminManageAction {
	private static final Logger logger = LogManager.getLogger(AdminManageAction.class);
	
	@Resource StaffService staffService;//通过接口引用代理返回的对象
	@Resource CaptchaManage captchaManage;
	@Resource StaffManage staffManage;
	
	@Resource CloseableHttpClient httpClient;
	@Resource RequestConfig requestConfig;
	@Resource SettingManage settingManage;
	@Resource StaffLoginLogManage staffLoginLogManage;
	@Resource ConsumerTokenServices tokenServices;
	
	

	/**
	 * 员工登录页面显示
	 * @param username 员工账号
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/admin/login",method=RequestMethod.GET) 
	public String loginUI(ModelMap model,String username,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		if(isAjax){//如果是AJAX方式访问
			//返回值
			Map<String,Object> returnValue = new HashMap<String,Object>();
			
			
			//是否需要验证码  true:要  false:不要
			boolean isCaptcha = false;
			
			//登录失败超过N次，出现验证码 0为每次都出现验证码
			int loginFailureNumber = (Integer)CommentedProperties.readOAuth2().get("loginFailureNumber");
			if(loginFailureNumber <=0){//每分钟连续登录密码错误N次时出现验证码
				isCaptcha = true;
			}else{
				if(username != null && !"".equals(username.trim())){
					Integer errorCount = staffManage.getLoginFailureCount(username);//查询错误次数
					if(errorCount != null && errorCount >= loginFailureNumber){
						isCaptcha = true;
					}
				}
				
			}
			if(isCaptcha){
				returnValue.put("isCaptcha", true);
				returnValue.put("captchaKey", UUIDUtil.getUUID32());
			}else{
				returnValue.put("isCaptcha", false);
			}
			WebUtil.writeToWeb(JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue)), "json", response);
			return null;
		}else{
			return "/manage/index";	
		}
	}
	
	/**
	 * 员工登录
	 * @param model
	 * @param username 账号
	 * @param password 密码
	 * @param captchaKey 验证码编号
	 * @param captchaValue 验证码值
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/admin/login",method=RequestMethod.POST) 
	@ResponseBody
	public String login(ModelMap model,String username,String password,String captchaKey,String captchaValue,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	//  resource.setScope(Arrays.asList("all")); 
		
		Map<String,Object> error = new HashMap<String,Object>();
		
        if (username == null || "".equals(username.trim())) {
        	error.put("username", "用户名不能为空");
        }else{
        	username = username.trim();
        }
        
        if (password == null || "".equals(password.trim())) {
        	error.put("password", "密码不能为空");
        }else{
        	password = password.trim();
        	if(password.length() != 64){
        		error.put("password", "密码不是SHA256格式");
        	}
        }
		
        //是否需要验证码  true:要  false:不要
  		boolean isCaptcha = false;
  		//登录失败超过N次，出现验证码 0为每次都出现验证码
		int loginFailureNumber = (Integer)CommentedProperties.readOAuth2().get("loginFailureNumber");
  		if(loginFailureNumber <=0){//每分钟连续登录密码错误N次时出现验证码
  			isCaptcha = true;
  		}else{

  			if(username != null && !"".equals(username.trim())){
  				Integer errorCount = staffManage.getLoginFailureCount(username.trim());//先查询错误次数
  	  			
  				if(errorCount != null && errorCount >= loginFailureNumber){
  					isCaptcha = true;
  				}
  			}
  			
  		}
  		
  		if(isCaptcha){
  			if (captchaKey == null || "".equals(captchaKey.trim())) {
  				error.put("captchaKey","验证码编号不能为空");
  	        }
  	        if (captchaValue == null || "".equals(captchaValue.trim())) {
  	        	error.put("captchaValue","验证码值不能为空");
  	        }
  			
  	        if(captchaKey != null && !"".equals(captchaKey.trim()) && captchaValue != null && !"".equals(captchaValue.trim())){
  	        	//增加验证码重试次数
  				Integer original = settingManage.getSubmitQuantity("captcha", captchaKey.trim());
  	    		if(original != null){
  	    			settingManage.addSubmitQuantity("captcha", captchaKey.trim(),original+1);//刷新每分钟原来提交次数
  	    		}else{
  	    			settingManage.addSubmitQuantity("captcha", captchaKey.trim(),1);//刷新每分钟原来提交次数
  	    		}
  	  			
  	  			String _captcha = captchaManage.captcha_generate(captchaKey.trim(),"");
  	  	        if(_captcha != null && !"".equals(_captcha.trim())){
  	  	        	if(!_captcha.equalsIgnoreCase(captchaValue.trim())){
  	  	        		error.put("captchaValue","验证码错误");
  	  	        	}
  	  			}else{
  	  				error.put("captchaValue","验证码过期");
  	  			}
  	  			//删除验证码
  	  			captchaManage.captcha_delete(captchaKey);
  	        }
  		}
		
			
		//是否登录成功
		boolean success = false;
		//返回内容
		String returnValue = "";
		
		if(error.size()==0){
			HttpPost httpPost = new HttpPost(Configuration.getUrl(request)+"admin/oauth/token");
			httpPost.setHeader("Accept", "application/json");


			ContentType contentType = ContentType.create("text/plain", "UTF-8");
			//添加你的请求体
	        HttpEntity reqEntity = MultipartEntityBuilder.create()
	                .addPart("client_id", new StringBody("bbs", contentType))
	                .addPart("client_secret", new StringBody((String)CommentedProperties.readOAuth2().get("secret"), contentType))
	                .addPart("grant_type", new StringBody("password", contentType))
	                .addPart("username", new StringBody(username, contentType))
	                .addPart("password", new StringBody(password, contentType))
	                .build();
	       
			//将请求实体放入到httpPost中
			httpPost.setEntity(reqEntity);

			
			
	    	//创建httpClient对象
	    	try(CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {	
	    		//{"access_token":"0e2ql02rNxZ199fbTHr4kq9TX_A","token_type":"bearer","refresh_token":"7Q0wEwoHNZtO-zZ6BFzdd-HQVpA","expires_in":49,"scope":"all"}   		
	    		String data = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
	    		
	    		
	    		
	    		if(data != null && !"".equals(data.trim())){
	    			Map<String,Object> content_map = JsonUtils.toObject(data, HashMap.class);
					if(content_map != null && content_map.size() >0){
						
						for(Map.Entry<String, Object> entry : content_map.entrySet()) {
							if(entry.getKey().equalsIgnoreCase("code")){
								if(entry.getValue().equals(500)){//错误
									returnValue =  data;
									break;
								}
							}else if(entry.getKey().equalsIgnoreCase("expires_in")){
								success = true;
								returnValue =  JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,content_map));	
								break;
							}
						}
					}
	    		}
	    		
	    	} catch (IOException e) {
	    		if (logger.isErrorEnabled()) {
		            logger.error("Http请求IO异常",e);
		        }
	        }
		}
		
		 
    	if(success){//登录成功
    		//清理登录次数缓存
        	staffManage.deleteLoginFailureCount(username);
        	WebUtil.deleteCookie(response, "cms_staffName");
        	
        	SysUsers sysUsers = staffService.findByUserAccount(username);
    		if(sysUsers != null){
    	    	//写入登录日志
    			StaffLoginLog staffLoginLog = new StaffLoginLog();
    			staffLoginLog.setId(staffLoginLogManage.createStaffLoginLogId(sysUsers.getUserId()));
    			staffLoginLog.setIp(IpAddress.getClientIpAddress(request));
    			staffLoginLog.setStaffId(sysUsers.getUserId());
    			staffLoginLog.setLogonTime(new Date());
    			Object new_staffLoginLog = staffLoginLogManage.createStaffLoginLogObject(staffLoginLog);
    			
    			staffService.saveStaffLoginLog(new_staffLoginLog);
    		}
        	//删除缓存员工安全摘要
        	staffManage.delete_staffSecurityDigest(username);
    	}else{//登录失败
    		
    		if(username != null && !"".equals(username.trim())){
    			WebUtil.addCookie(response, "cms_staffName", username, 120);
        		Integer original = staffManage.getLoginFailureCount(username);//原来总次数
        		if(original != null){
        			staffManage.addLoginFailureCount(username,original+1);
        		}else{
        			staffManage.addLoginFailureCount(username,1);
        		}
    		}
    		if(returnValue == null || "".equals(returnValue.trim())){
    			error.put("login", "登录错误");
    	        return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));	
    		}
    	}
    	return returnValue;	
	}
	
	
	
	/**
	 * 刷新令牌
	 * @param model
	 * @param refresh_token 刷新令牌
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/admin/refreshToken",method=RequestMethod.POST) 
	@ResponseBody
	public String refreshToken(ModelMap model,String refresh_token,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		
		if(refresh_token == null || "".equals(refresh_token.trim())){
			error.put("refresh_token", "刷新令牌不能为空");
		}else{
			refresh_token = refresh_token.trim();
		}
		
		//是否刷新成功
		boolean success = false;
		//返回内容
		String returnValue = "";
		
		if(error.size() ==0){
			
		
			HttpPost httpPost = new HttpPost(Configuration.getUrl(request)+"admin/oauth/token");
			httpPost.setHeader("Accept", "application/json");
	
	
			ContentType contentType = ContentType.create("text/plain", "UTF-8");
			//添加你的请求体
	        HttpEntity reqEntity = MultipartEntityBuilder.create()
	                .addPart("client_id", new StringBody("bbs", contentType))
	                .addPart("client_secret", new StringBody((String)CommentedProperties.readOAuth2().get("secret"), contentType))
	                .addPart("grant_type", new StringBody("refresh_token", contentType))
	                .addPart("refresh_token", new StringBody(refresh_token, contentType))
	                .build();
	       
			//将请求实体放入到httpPost中
			httpPost.setEntity(reqEntity);
	
			
			
	    	//创建httpClient对象
	    	try(CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {	
	    		//{"access_token":"0e2ql02rNxZ199fbTHr4kq9TX_A","token_type":"bearer","refresh_token":"7Q0wEwoHNZtO-zZ6BFzdd-HQVpA","expires_in":49,"scope":"all"}   		
	    		String data = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
	    		if(data != null && !"".equals(data.trim())){
	    			Map<String,Object> content_map = JsonUtils.toObject(data, HashMap.class);
					if(content_map != null && content_map.size() >0){
						
						for(Map.Entry<String, Object> entry : content_map.entrySet()) {
							if(entry.getKey().equalsIgnoreCase("code")){
								if(entry.getValue().equals(500)){//错误
									returnValue =  data;
									break;
								}
							}else if(entry.getKey().equalsIgnoreCase("expires_in")){
								success = true;
								returnValue =  JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,content_map));	
								break;
							}
						}
					}
	    		}
	    		
	    		
	    	} catch (IOException e) {
	    		if (logger.isErrorEnabled()) {
		            logger.error("Http请求IO异常",e);
		        }
	        }
		}
		
		if(success){//刷新成功
			//写入日志
			
    	}else{//刷新失败
    		if(returnValue == null || "".equals(returnValue.trim())){
    			error.put("refreshToken", "刷新错误");
    	        return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));	
    		}
    	}
		return returnValue;	
	}
	

	/**
	 * 员工退出
	 * @param model
	 * @param access_token 访问令牌
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/admin/logout",method=RequestMethod.POST) 
	@ResponseBody
	public String logout(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String,String> error = new HashMap<String,String>();

		
		String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.contains("Bearer")) {
            String access_token = authorization.replace("Bearer","").trim();

            if(access_token != null && !"".equals(access_token.trim())){
    			String name = SecurityContextHolder.getContext().getAuthentication().getName(); 
    			if(name != null && !"".equals(name.trim())){
    				if(tokenServices.revokeToken(access_token)){
    					staffManage.delete_staffSecurityDigest(name);
    					return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));	
    				}else{
    					error.put("access_token", "退出失败");
    				}
    			}else{
    				error.put("logout", "用户不存在");
    			}
    		}else{
    			error.put("access_token", "访问令牌不能为空");
    		}
        }else{
        	error.put("access_token", "请求头不能为空");
        }
		
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));	
	}
	
	/**
	 * 当前时间
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/admin/currentTime",method=RequestMethod.GET)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String currentTime(ModelMap model
			) throws Exception {
		TimeData time = new TimeData();
		time.setCurrentTime(new Date().getTime());
		
		TimeZone tz = TimeZone.getDefault();
	//	TimeZone tz = TimeZone.getTimeZone("America/New_York");//-5
	
		//获取当前时区的标准时间到GMT的偏移量。相对于“本初子午线”的偏移，单位/毫秒。
		int offset = tz.getRawOffset();
		//时区偏移量 “时间偏移”对应的小时
		int gmt = offset/(3600*1000);
	
		time.setTimezoneOffset(gmt);
		
		return JsonUtils.toJSONString(time);
	}
	
	
	protected class TimeData {
		//系统当前时间戳
        private Long currentTime;
        //系统当前时区偏移
        private Integer timezoneOffset;
        
        
        public TimeData() {}
		public Long getCurrentTime() {
			return currentTime;
		}
		public void setCurrentTime(Long currentTime) {
			this.currentTime = currentTime;
		}
		public Integer getTimezoneOffset() {
			return timezoneOffset;
		}
		public void setTimezoneOffset(Integer timezoneOffset) {
			this.timezoneOffset = timezoneOffset;
		}
    }
	
}
