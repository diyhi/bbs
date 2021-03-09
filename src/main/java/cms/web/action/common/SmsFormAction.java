package cms.web.action.common;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import cms.bean.ErrorView;
import cms.bean.setting.AllowRegisterAccount;
import cms.bean.setting.SystemSetting;
import cms.bean.user.User;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.utils.WebUtil;
import cms.web.action.CSRFTokenManage;
import cms.web.action.setting.SettingManage;
import cms.web.action.sms.SmsManage;
import cms.web.action.user.UserManage;

/**
 * 短信验证码接收表单
 *
 */
@Controller
public class SmsFormAction {
	@Resource SettingManage settingManage;
	@Resource SettingService settingService;
	@Resource CaptchaManage captchaManage;
	@Resource CSRFTokenManage csrfTokenManage;
	@Resource SmsManage smsManage;
	@Resource UserManage userManage;
	@Resource UserService userService;
	
	
	/**
	 * 获取短信验证码
	 * @param model
	 * @param module 模块  100.注册  200.登录  300.找回密码
	 * @param mobile 手机号
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/smsCode", method=RequestMethod.POST)
	public String add(ModelMap model,Integer module,String mobile,
			String captchaKey,String captchaValue,String token,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		Map<String,String> error = new HashMap<String,String>();
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("smsCode", ErrorView._21.name());//只读模式不允许提交数据
		}
		
		//是否允许发短信
		boolean isAllowSMS = false;
		
		
		if(module != null){
			List<Integer> numbers = Arrays.asList(100, 200,300); 
			
	    	if(!numbers.contains(module)){
	    		error.put("smsCode", ErrorView._23.name());//模块错误
	    	}else{
	    		AllowRegisterAccount allowRegisterAccount =  settingManage.readAllowRegisterAccount();
	    		if(module.equals(100)){//注册
	    			if(allowRegisterAccount != null && allowRegisterAccount.isMobile()){
	    				isAllowSMS = true;
	    			}
				}
	    		if(module.equals(300)){//找回密码
	    			isAllowSMS = true;
				}
	    		
	    		
	    	}
	    }else{
	    	error.put("smsCode", ErrorView._23.name());//模块错误
	    }
		
		if(!isAllowSMS){
			error.put("smsCode", ErrorView._868.name());//不允许发短信
		}
		
		
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
		

  		if(mobile != null && !"".equals(mobile.trim())){
			if(mobile.trim().length() >18){
				error.put("smsCode", "手机号码超长");
			}else{
				boolean mobile_verification = Verification.isPositiveInteger(mobile.trim());//正整数
				if(!mobile_verification){
					error.put("smsCode", "手机号码不正确");
				}else{
					if(error.size() == 0 && module.equals(300)){
						String platformUserId = userManage.thirdPartyUserIdToPlatformUserId(mobile.trim(),20);
						User mobile_user = userService.findUserByPlatformUserId(platformUserId);
						if(mobile_user == null){
				    		 error.put("smsCode", ErrorView._869.name());//手机用户不存在
				    	}
					}
				}
			}
		}else{
			error.put("smsCode", ErrorView._851.name());//手机号不能为空
		}
  		
  		if(error.size() == 0){
  			String platformUserId = userManage.thirdPartyUserIdToPlatformUserId(mobile.trim(),20);
  			
	    	String randomNumeric = RandomStringUtils.randomNumeric(6);
	    	String errorInfo = smsManage.sendSms_code(platformUserId,mobile,randomNumeric);//6位随机数
	    	if(errorInfo != null){
	    		error.put("smsCode", errorInfo);
	    	}else{
	    		//删除绑定手机验证码标记
	    	    smsManage.smsCode_delete(module,platformUserId, mobile.trim());
	    		//生成绑定手机验证码标记
	    		smsManage.smsCode_generate(module,platformUserId, mobile.trim(),randomNumeric);
	    		
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

	}
	
	

	
	
}
