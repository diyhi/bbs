package cms.web.action.common;


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
import cms.bean.feedback.Feedback;
import cms.bean.setting.SystemSetting;
import cms.service.feedback.FeedbackService;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.utils.Base64;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.utils.RefererCompare;
import cms.utils.UUIDUtil;
import cms.utils.WebUtil;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.CSRFTokenManage;
import cms.web.action.TextFilterManage;
import cms.web.action.setting.SettingManage;
import cms.web.taglib.Configuration;

/**
 * 在线留言接收表单
 *
 */
@Controller
@RequestMapping("/feedback/control") 
public class FeedbackFormAction {
	@Resource TemplateService templateService;
	
	@Resource CaptchaManage captchaManage;
	@Resource FeedbackService feedbackService;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	
	@Resource TextFilterManage textFilterManage;
	@Resource SettingManage settingManage;
	@Resource SettingService settingService;
	
	@Resource CSRFTokenManage csrfTokenManage;
	
	/**
	 * 在线留言   添加
	 * @param model
	 * @param name 名称
	 * @param contact 联系方式
	 * @param content 内容
	 * @param jumpUrl 跳转地址
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(ModelMap model,String name, String contact,String content,
			String token,String captchaKey,String captchaValue,String jumpUrl,
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
		
		//验证验证码
		if(captchaKey != null && !"".equals(captchaKey.trim())){
			//增加验证码重试次数
			//统计每分钟原来提交次数
			Integer original = settingManage.getSubmitQuantity("captcha", captchaKey.trim());
    		if(original != null){
    			settingManage.addSubmitQuantity("captcha", captchaKey.trim(),original+1);//刷新每分钟原来提交次数
    		}else{
    			settingManage.addSubmitQuantity("captcha", captchaKey.trim(),1);//刷新每分钟原来提交次数
    		}
			
			String _captcha = captchaManage.captcha_generate(captchaKey.trim(),"");
			if(captchaValue != null && !"".equals(captchaValue.trim())){
				if(_captcha != null && !"".equals(_captcha.trim())){
					if(!_captcha.equalsIgnoreCase(captchaValue)){
						error.put("captchaValue",ErrorView._15.name());//验证码错误
					}
				}else{
					error.put("captchaValue",ErrorView._17.name());//验证码过期
				}
			}else{
				error.put("captchaValue",ErrorView._16.name());//请输入验证码
			}
			//删除验证码
			captchaManage.captcha_delete(captchaKey.trim());	
		}else{
			error.put("captchaValue", ErrorView._14.name());//验证码参数错误
		}

		Feedback feedback = new Feedback();
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		
		
		//如果不允许在线留言
		if(!systemSetting.isAllowFeedback()){
			error.put("feedback", ErrorView._305.name());//在线留言已关闭
		}
		
		//名称
		if(name != null && !"".equals(name.trim())){
			if(name.trim().length() >100){
				error.put("name", ErrorView._301.name());//不能超过100个字符
			}else{
				feedback.setName(name.trim());
			}
		}else{
			error.put("name", ErrorView._302.name());//名称不能为空
		}
		//联系方式
		if(contact != null && !"".equals(contact.trim())){
			if(contact.trim().length() >100){
				error.put("contact", ErrorView._301.name());//不能超过100个字符
			}else{
				feedback.setContact(contact.trim());
			}
		}else{
			error.put("contact", ErrorView._303.name());//联系方式不能为空
		}
		if(content != null && !"".equals(content.trim())){
			if(contact.trim().length() >1000){
				error.put("content", ErrorView._306.name());//字符超长
			}else{
				feedback.setContent(content.trim());
			}
			
		}else{
			error.put("content", ErrorView._304.name());//内容不能为空
		}


		if(error.size() == 0){
			feedback.setIp(IpAddress.getClientIpAddress(request));
			//保存评论
			feedbackService.saveFeedback(feedback);
	
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
    			returnValue.put("captchaKey", UUIDUtil.getUUID32());	
    		}else{
    			returnValue.put("success", "true");
    		}
    		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			
			
			if(error != null && error.size() >0){//如果有错误
				redirectAttrs.addFlashAttribute("error", returnError);//重定向传参
				redirectAttrs.addFlashAttribute("feedback", feedback);
				

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
				model.addAttribute("message", "保存留言成功");
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
