package cms.web.action.common;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.ErrorView;
import cms.bean.setting.SystemSetting;
import cms.bean.thirdParty.WeiXinOpenId;
import cms.bean.user.AccessUser;
import cms.bean.user.DisableUserName;
import cms.bean.user.FormCaptcha;
import cms.bean.user.RefreshUser;
import cms.bean.user.User;
import cms.bean.user.UserCustom;
import cms.bean.user.UserGrade;
import cms.bean.user.UserInputValue;
import cms.bean.user.UserLoginLog;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.service.user.UserCustomService;
import cms.service.user.UserGradeService;
import cms.service.user.UserService;
import cms.utils.Base64;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.utils.RefererCompare;
import cms.utils.SHA;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.CSRFTokenManage;
import cms.web.action.setting.SettingManage;
import cms.web.action.thirdParty.ThirdPartyManage;
import cms.web.action.user.UserLoginLogManage;
import cms.web.action.user.UserManage;
import cms.web.taglib.Configuration;




/**
 * 用户登录/登出管理
 *
 */
@Controller
public class UserFormManageAction {
	@Resource TemplateService templateService;
	@Resource UserService userService;

	@Resource(name="userCustomServiceBean")
	private UserCustomService userCustomService;
	@Resource UserGradeService userGradeService;
	
	@Resource UserLoginLogManage userLoginLogManage;
	
	@Resource SettingService settingService;
	@Resource CaptchaManage captchaManage;
	@Resource SettingManage settingManage;
	
	
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource UserManage userManage;
	
	@Resource CSRFTokenManage csrfTokenManage;
	
	@Resource OAuthManage oAuthManage;

	@Resource ThirdPartyManage thirdPartyManage;
	
	
	//?  匹配任何单字符
	//*  匹配0或者任意数量的字符
	//** 匹配0或者更多的目录
	private PathMatcher matcher = new AntPathMatcher(); 

	
	/**
	 * 会员注册页面显示
	 * @param jumpUrl 跳转URL
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/register",method=RequestMethod.GET) 
	public String registerUI(ModelMap model,String jumpUrl,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
		
		 //判断是否错误回显
	  	boolean errorDisplay = false; 	
	  	if(model != null && model.get("error") != null){
	    	errorDisplay = true;
	    }
		
		if(jumpUrl == null || "".equals(jumpUrl.trim()) && isAjax == false){
			String referer= request.getHeader("referer");  
			if(referer != null && !"".equals(referer.trim())){
				
				//分离URL
				//移除开始部分的相同的字符,不区分大小写
				String uri = StringUtils.removeStartIgnoreCase(referer,Configuration.getUrl(request));
			
				
				//是否为默认注册页面
				boolean isRegister = false;//false:不是  
				
				if(uri != null && !"".equals(uri.trim())){
					//截取到等于第二个参数的字符串为止,从左往右
					String before_a = StringUtils.substringBefore(uri.trim(), "?");
					//截取到等于第二个参数的字符串为止,从左往右
					String before_b = StringUtils.substringBefore(uri.trim(), ".");
					//截取到等于第二个参数的字符串为止,从左往右
					String before_c = StringUtils.substringBefore(uri.trim(), "/");
					
					if(before_a.equalsIgnoreCase("register")){
						isRegister = true;//标记为默认登录页面
					}
					if(before_b.equalsIgnoreCase("register")){
						isRegister = true;
					}
					if(before_c.equalsIgnoreCase("register")){
						isRegister = true;
					}
				}else{
					//如果没有参数,则跳到首页
					uri = "index";
				}
				if(isRegister == false){//如果来源不是登录页面，则写入跳转参数
					String encodedRedirectURL = response.encodeRedirectURL("register?jumpUrl="+Base64.encodeBase64URL(uri));
					response.sendRedirect(encodedRedirectURL);
					
					return null;
				}
			}
		}

		List<UserCustom> userCustomList = userCustomService.findAllUserCustom_cache();
		if(userCustomList != null && userCustomList.size() >0){		
			Iterator <UserCustom> it = userCustomList.iterator();  
			while(it.hasNext()){  
				UserCustom userCustom = it.next();
				if(userCustom.isVisible() == false){//如果不显示
					it.remove();  
					continue;
				}
				if(userCustom.getValue() != null && !"".equals(userCustom.getValue().trim())){
					LinkedHashMap<String,String> itemValue = JsonUtils.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});
					userCustom.setItemValue(itemValue);
				}
				
			}
		}
		
		if(errorDisplay == false){//如果不是错误回显
			model.addAttribute("userCustomList", userCustomList);
		}
		returnValue.put("userCustomList", userCustomList);
		
		
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.isRegisterCaptcha()){//如果注册需要验证码
			String captchaKey = UUIDUtil.getUUID32();
			model.addAttribute("captchaKey",captchaKey);
			returnValue.put("captchaKey", captchaKey);
		}
		
		
		if(isAjax == true){
    		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
			return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/register";		
		}
	}
	
	/**
	 * 会员注册
	 * @param thirdPartyOpenId 第三方用户获取唯一标识  例如微信公众号openid
	 * @param jumpUrl 跳转URL
	 * @param token 令牌
	 * @param captchaKey 验证Key
	 * @param captchaValue 验证码
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/register",method=RequestMethod.POST) 
	public String register(ModelMap model,User formbean,
			String captchaKey,String captchaValue,String thirdPartyOpenId,
			String jumpUrl,String token,RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据

	    String dirName = templateService.findTemplateDir_cache();
		
		
		
		User user = new User();
		
		Map<String,String> error = new HashMap<String,String>();
		
		//判断令牌
		if(token != null && !"".equals(token.trim())){	
			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
				if(!token_sessionid.equals(token)){
					error.put("token", ErrorView._13.name());
				}
			}else{
				error.put("token", ErrorView._12.name());
			}
		}else{
			error.put("token", ErrorView._11.name());
		}
		
		boolean isCaptcha = false;
		//用户自定义注册功能项参数
		List<UserCustom> userCustomList = userCustomService.findAllUserCustom_cache();
		
		//验证码
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		//如果不允许注册
		if(systemSetting.isAllowRegister() == false){
			error.put("register", ErrorView._862.name());//不允许注册
		}else{
			
			
			if(systemSetting.isRegisterCaptcha()){//如果注册需要验证码
				isCaptcha = true;
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
			}

			if(userCustomList != null && userCustomList.size() >0){	
				for(UserCustom userCustom : userCustomList){
					//用户自定义注册功能项用户输入值集合
					List<UserInputValue> userInputValueList = new ArrayList<UserInputValue>();
					
					if(userCustom.isVisible() == true){//显示
						if(userCustom.getValue() != null && !"".equals(userCustom.getValue().trim())){
							LinkedHashMap<String,String> itemValue = JsonUtils.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});
							userCustom.setItemValue(itemValue);
						}
						if(userCustom.getChooseType().equals(1)){//1.输入框
							String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());
							
							if(userCustom_value != null && !"".equals(userCustom_value.trim())){
								UserInputValue userInputValue = new UserInputValue();
								userInputValue.setUserCustomId(userCustom.getId());
								userInputValue.setContent(userCustom_value.trim());
								userInputValueList.add(userInputValue);
								
								
								if(userCustom.getMaxlength() != null && userCustom_value.length() > userCustom.getMaxlength()){
									error.put("userCustom_"+userCustom.getId(), "长度超过"+userCustom_value.length()+"个字符");
								}
								
								int fieldFilter = userCustom.getFieldFilter();//字段过滤方式    0.无  1.只允许输入数字  2.只允许输入字母  3.只允许输入数字和字母  4.只允许输入汉字  5.正则表达式过滤
								switch(fieldFilter){
									case 1 : //输入数字
										if(Verification.isPositiveIntegerZero(userCustom_value.trim()) == false){
											error.put("userCustom_"+userCustom.getId(), ErrorView._804.name());//只允许输入数字
										}
									  break; 
									case 2 : //输入字母
										if(Verification.isLetter(userCustom_value.trim()) == false){
											error.put("userCustom_"+userCustom.getId(), ErrorView._805.name());//只允许输入字母
										}
									  break;
									case 3 : //只能输入数字和字母
										if(Verification.isNumericLetters(userCustom_value.trim()) == false){
											error.put("userCustom_"+userCustom.getId(), ErrorView._806.name());//只允许输入数字和字母
										}
									  break;
									case 4 : //只能输入汉字
										if(Verification.isChineseCharacter(userCustom_value.trim())== false){
											error.put("userCustom_"+userCustom.getId(), ErrorView._807.name());//只允许输入汉字
										}
									  break;
									case 5 : //正则表达式过滤
										if(userCustom_value.matches(userCustom.getRegular())== false){
											error.put("userCustom_"+userCustom.getId(), ErrorView._808.name());//输入错误
										}
									  break;
								//	default:
								}
							}else{
								if(userCustom.isRequired() == true){//是否必填	
									error.put("userCustom_"+userCustom.getId(), ErrorView._809.name());//必填项
								}
								
							}	
							userCustom.setUserInputValueList(userInputValueList);
						}else if(userCustom.getChooseType().equals(2)){//2.单选按钮
							String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());

							if(userCustom_value != null && !"".equals(userCustom_value.trim())){
								
								String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
								if(itemValue != null ){
									UserInputValue userInputValue = new UserInputValue();
									userInputValue.setUserCustomId(userCustom.getId());
									userInputValue.setOptions(userCustom_value.trim());
									userInputValueList.add(userInputValue);
									
								}else{
									if(userCustom.isRequired() == true){//是否必填	
										error.put("userCustom_"+userCustom.getId(), ErrorView._809.name());//必填项
									}
								}
								
							}else{
								if(userCustom.isRequired() == true){//是否必填	
									error.put("userCustom_"+userCustom.getId(), ErrorView._809.name());//必填项
								}
							}
							userCustom.setUserInputValueList(userInputValueList);	
							
						}else if(userCustom.getChooseType().equals(3)){//3.多选按钮
							String[] userCustom_value_arr = request.getParameterValues("userCustom_"+userCustom.getId());
		
							if(userCustom_value_arr != null && userCustom_value_arr.length >0){
								for(String userCustom_value : userCustom_value_arr){
									
									if(userCustom_value != null && !"".equals(userCustom_value.trim())){
										
										String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
										if(itemValue != null ){
											UserInputValue userInputValue = new UserInputValue();
											userInputValue.setUserCustomId(userCustom.getId());
											userInputValue.setOptions(userCustom_value.trim());
											userInputValueList.add(userInputValue);
										}
										
										
									}
								}
							}else{
								if(userCustom.isRequired() == true){//是否必填	
									error.put("userCustom_"+userCustom.getId(), ErrorView._809.name());//必填项
								}
							}
							if(userInputValueList.size() == 0){
								if(userCustom.isRequired() == true){//是否必填	
									error.put("userCustom_"+userCustom.getId(), ErrorView._809.name());//必填项
								}
							}
							userCustom.setUserInputValueList(userInputValueList);	
							
						}else if(userCustom.getChooseType().equals(4)){//4.下拉列表
							String[] userCustom_value_arr = request.getParameterValues("userCustom_"+userCustom.getId());
			
							if(userCustom_value_arr != null && userCustom_value_arr.length >0){
								for(String userCustom_value : userCustom_value_arr){
									
									if(userCustom_value != null && !"".equals(userCustom_value.trim())){
										
										String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
										if(itemValue != null ){
											UserInputValue userInputValue = new UserInputValue();
											userInputValue.setUserCustomId(userCustom.getId());
											userInputValue.setOptions(userCustom_value.trim());
											userInputValueList.add(userInputValue);
										}
										
										
									}
								}
							}else{
								if(userCustom.isRequired() == true){//是否必填	
									error.put("userCustom_"+userCustom.getId(), ErrorView._809.name());//必填项
								}
							}
							if(userInputValueList.size() == 0){
								if(userCustom.isRequired() == true){//是否必填	
									error.put("userCustom_"+userCustom.getId(), ErrorView._809.name());//必填项
								}
							}
							userCustom.setUserInputValueList(userInputValueList);	
						}else if(userCustom.getChooseType().equals(5)){// 5.文本域
							String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());

							if(userCustom_value != null && !"".equals(userCustom_value.trim())){
								UserInputValue userInputValue = new UserInputValue();
								userInputValue.setUserCustomId(userCustom.getId());
								userInputValue.setContent(userCustom_value);
							
								userInputValueList.add(userInputValue);
								
							}else{
								if(userCustom.isRequired() == true){//是否必填	
									error.put("userCustom_"+userCustom.getId(), ErrorView._809.name());//必填项
								}
							}
							userCustom.setUserInputValueList(userInputValueList);
						}
					}
				}
				
			}
			
			if(formbean.getUserName() != null && !"".equals(formbean.getUserName().trim())){
				if(formbean.getUserName().trim().length() < 3){
					error.put("userName", ErrorView._811.name());//用户名称小于3个字符
				}
				if(formbean.getUserName().trim().length() > 25){
					error.put("userName", ErrorView._812.name());//用户名称大于25个字符
				}
				if(Verification.isNumericLettersUnderscore(formbean.getUserName().trim()) == false){
					error.put("userName", ErrorView._813.name());//用户名只能输入由数字、26个英文字母或者下划线组成
				}
				List<DisableUserName> disableUserNameList = userService.findAllDisableUserName_cache();
				if(disableUserNameList != null && disableUserNameList.size() >0){
					for(DisableUserName disableUserName : disableUserNameList){
						boolean flag = matcher.match(disableUserName.getName(), formbean.getUserName().trim());  //参数一: ant匹配风格   参数二:输入URL
						if(flag){
							error.put("userName", ErrorView._863.name());//该用户名不允许注册
						}
					}
				}
				
				
				User u = userService.findUserByUserName(formbean.getUserName().trim());
				if(u != null){
					error.put("userName", ErrorView._814.name());//该用户名已注册
				}	
				user.setUserName(formbean.getUserName().trim());
			}else{
				error.put("userName", ErrorView._815.name());//用户名称不能为空
			}

			//盐值
			user.setSalt(UUIDUtil.getUUID32());
			
			if(formbean.getPassword() != null && !"".equals(formbean.getPassword().trim())){
				if(formbean.getPassword().trim().length() != 64){//判断是否是64位SHA256
					error.put("password", ErrorView._801.name());//密码长度错误
				}else{
					user.setPassword(SHA.sha256Hex(formbean.getPassword().trim()+"["+user.getSalt()+"]"));
				}
			}else{
				error.put("password", ErrorView._816.name());//密码不能为空
			}

			if(formbean.getIssue() != null && !"".equals(formbean.getIssue().trim())){//密码提示问题
				if(formbean.getIssue().length()>50){
					error.put("issue", ErrorView._817.name());//密码提示问题不能超过50个字符
				}
				user.setIssue(formbean.getIssue().trim());
			}else{
				error.put("issue", ErrorView._818.name());//密码提示问题不能为空
			}
			if(formbean.getAnswer() != null && !"".equals(formbean.getAnswer().trim())){//密码提示答案
				if(formbean.getAnswer().trim().length() != 64){//判断是否是64位SHA256
					error.put("answer", ErrorView._819.name());//密码提示答案长度错误
				}else{
					//密码提示答案由  密码提示答案原文sha256  进行sha256组成
					user.setAnswer(SHA.sha256Hex(formbean.getAnswer().trim()));
				}
			}else{
				error.put("answer", ErrorView._820.name());//密码提示答案不能为空
			}
			if(formbean.getEmail() != null && !"".equals(formbean.getEmail().trim())){//邮箱
				if(Verification.isEmail(formbean.getEmail().trim()) == false){
					error.put("email", ErrorView._821.name());//Email地址不正确
				}
				if(formbean.getEmail().length()>60){
					error.put("email", ErrorView._822.name());//Email地址不能超过60个字符
				}
				user.setEmail(formbean.getEmail().trim());
			}
			
			user.setRegistrationDate(new Date());	
			
		}

		if(error.size() == 0){
			user.setType(10);
			user.setPlatformUserId(user.getUserName());
			
			//用户自定义注册功能项用户输入值集合
			List<UserInputValue> all_userInputValueList = new ArrayList<UserInputValue>();
		
			if(userCustomList != null && userCustomList.size() >0){	
				for(UserCustom userCustom : userCustomList){
					all_userInputValueList.addAll(userCustom.getUserInputValueList());
				}
			}
			user.setSecurityDigest(new Date().getTime());
			try {
				userService.saveUser(user,all_userInputValueList,null);
			} catch (Exception e) {
				error.put("register", ErrorView._823.name());//注册会员出错
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			}
			//自动登录
			if(error.size() == 0){
				//写入登录日志
				UserLoginLog userLoginLog = new UserLoginLog();
				userLoginLog.setId(userLoginLogManage.createUserLoginLogId(user.getId()));
				userLoginLog.setIp(IpAddress.getClientIpAddress(request));
				userLoginLog.setUserId(user.getId());
				userLoginLog.setTypeNumber(10);//登录
				userLoginLog.setLogonTime(new Date());
				Object new_userLoginLog = userLoginLogManage.createUserLoginLogObject(userLoginLog);
				userService.saveUserLoginLog(new_userLoginLog);
	
				//自动登录
				
				//访问令牌
				String accessToken = UUIDUtil.getUUID32();
				//刷新令牌
				String refreshToken = UUIDUtil.getUUID32();
	
				String openId = "";//第三方openId
				if(thirdPartyOpenId != null && !"".equals(thirdPartyOpenId.trim())){
					openId = thirdPartyOpenId;
					oAuthManage.addOpenId(openId,refreshToken);
				}
				
				oAuthManage.addAccessToken(accessToken, new AccessUser(user.getId(),user.getUserName(),user.getNickname(),user.getAvatarPath(),user.getAvatarName(), user.getSecurityDigest(),false,openId));
				oAuthManage.addRefreshToken(refreshToken, new RefreshUser(accessToken,user.getId(),user.getUserName(),user.getNickname(),user.getAvatarPath(),user.getAvatarName(),user.getSecurityDigest(),false,openId));
	
				//将访问令牌添加到Cookie
				WebUtil.addCookie(response, "cms_accessToken", accessToken, 0);
				//将刷新令牌添加到Cookie
				WebUtil.addCookie(response, "cms_refreshToken", refreshToken, 0);
				AccessUserThreadLocal.set(new AccessUser(user.getId(),user.getUserName(),user.getNickname(),user.getAvatarPath(),user.getAvatarName(),user.getSecurityDigest(),false,openId));
				
				//删除缓存
				userManage.delete_cache_findUserById(user.getId());
				userManage.delete_cache_findUserByUserName(user.getUserName());
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
    			
    			if(isCaptcha){
    				returnValue.put("captchaKey", UUIDUtil.getUUID32());
    			}
    			
    		}else{
    			
    			//跳转URL
    			String _jumpUrl = "";
    			if(jumpUrl != null && !"".equals(jumpUrl.trim())){
    				//Base64解码后参数进行URL编码
    				String parameter = WebUtil.parameterEncoded(Base64.decodeBase64URL(jumpUrl.trim()));
    				
    				String encodedRedirectURL = response.encodeRedirectURL(parameter);
    				_jumpUrl = encodedRedirectURL;
    			}else{
    				_jumpUrl = "index";
    			}
    			
    			returnValue.put("success", "true");
    			returnValue.put("jumpUrl", _jumpUrl);
    			returnValue.put("systemUser", new AccessUser(user.getId(),user.getUserName(),user.getNickname(),user.getAvatarPath(),user.getAvatarName(),null,false,""));//登录用户
    			
    		}
    		
    		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			if(error != null && error.size() >0){//如果有错误
				
				redirectAttrs.addFlashAttribute("error", returnError);//重定向传参
				redirectAttrs.addFlashAttribute("user",formbean);
				redirectAttrs.addFlashAttribute("userCustomList",userCustomList);
				
				
				
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
				model.addAttribute("message", "注册成功");
				String referer = request.getHeader("referer");
				if(RefererCompare.compare(request, "login")){//如果是登录页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				if(RefererCompare.compare(request, "register")){//如果是注册页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				
				
				model.addAttribute("urlAddress", referer);
				
				return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/jump";	
			}
		}
	}
	
	
	/**
	 * 会员注册校验/验证码校验
	 * @param userName 会员用户名
	 * @param response
	 * @return true 禁止  false 允许
	 * @throws Exception 
	 */
	@RequestMapping(value="/userVerification",method=RequestMethod.GET) 
	@ResponseBody//方式来做ajax,直接返回字符串
	public String verification(ModelMap model,String userName,String captchaKey,String captchaValue,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(userName != null && !"".equals(userName.trim())){
			List<DisableUserName> disableUserNameList = userService.findAllDisableUserName_cache();
			if(disableUserNameList != null && disableUserNameList.size() >0){
				for(DisableUserName disableUserName : disableUserNameList){
					boolean flag = matcher.match(disableUserName.getName(), userName.trim());  //参数一: ant匹配风格   参数二:输入URL
					if(flag){
						return "true";
					}
				}
			}

			User u = userService.findUserByUserName(userName.trim());
			if(u != null){
				return "true";
			}
		}
		if(captchaKey != null && !"".equals(captchaKey.trim()) && captchaValue != null && !"".equals(captchaValue.trim())){
			//增加验证码重试次数
			//统计每分钟原来提交次数
			Integer original = settingManage.getSubmitQuantity("captcha", captchaKey.trim());
    		if(original != null){
    			settingManage.addSubmitQuantity("captcha", captchaKey.trim(),original+1);//刷新每分钟原来提交次数
    		}else{
    			settingManage.addSubmitQuantity("captcha", captchaKey.trim(),1);//刷新每分钟原来提交次数
    		}
			
			String _captcha = captchaManage.captcha_generate(captchaKey.trim(),"");
			if(_captcha != null && _captcha.equalsIgnoreCase(captchaValue)){
				return "true";
			}
		}
		
		
		return "false";
	}
	
	
	
	
	
	/**
	 * 会员登录页面显示
	 * @param jumpUrl 跳转URL
	 * @param code 微信公众号code
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/login",method=RequestMethod.GET) 
	public String loginUI(ModelMap model,String jumpUrl,String code,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		//处理微信浏览器被清理缓存后公众号自动登录
		if(code != null && !"".equals(code.trim()) && WebUtil.isWeChatBrowser(request)){//如果是微信客户端
			
			WeiXinOpenId weiXinOpenId = thirdPartyManage.queryWeiXinOpenId(code.trim());
			if(weiXinOpenId != null && weiXinOpenId.getOpenId() != null && !"".equals(weiXinOpenId.getOpenId())){

				//添加到缓存
				thirdPartyManage.addWeiXinOpenId(code.trim(), weiXinOpenId);
				
				
				
				//刷新令牌号
				String refreshToken = oAuthManage.getRefreshTokenByOpenId(weiXinOpenId.getOpenId());
				if(refreshToken != null && !"".equals(refreshToken.trim())){

					
					RefreshUser refreshUser = oAuthManage.getRefreshUserByRefreshToken(refreshToken.trim());
					if(refreshUser != null){
						
						//存放时间 单位/秒
						int maxAge = 0;
						if(refreshUser.isRememberMe()){
							maxAge = WebUtil.cookieMaxAge;//默认Cookie有效期
						}
						//将令牌写入Cookie
						
						//将访问令牌添加到Cookie
						WebUtil.addCookie(response, "cms_accessToken", refreshUser.getAccessToken(), maxAge);
						//将刷新令牌添加到Cookie
						WebUtil.addCookie(response, "cms_refreshToken", refreshToken, maxAge);

						
						if(jumpUrl != null && !"".equals(jumpUrl.trim())){
							//Base64解码后参数进行URL编码
							String parameter = WebUtil.parameterEncoded(Base64.decodeBase64URL(jumpUrl));
							
							String encodedRedirectURL = response.encodeRedirectURL(parameter);
							response.sendRedirect((Configuration.getPath() != null && !"".equals(Configuration.getPath()) ?Configuration.getPath()+"/" : "")+encodedRedirectURL);
							return null;
							
						}
							
						
						
						
					}
					
				}
				
			}
		}
		
		FormCaptcha formCaptcha = new FormCaptcha();
		boolean isCaptcha = false;
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getLogin_submitQuantity() <=0){//每分钟连续登录密码错误N次时出现验证码
			isCaptcha = true;
		}else{
			String userName = WebUtil.getCookieByName(request.getCookies(),"cms_userName");
			if(userName != null && !"".equals(userName.trim())){
				//是否需要验证码  true:要  false:不要
				isCaptcha = captchaManage.login_isCaptcha(userName);
			}
		}

		if(isCaptcha){
			formCaptcha.setShowCaptcha(true);
			formCaptcha.setCaptchaKey(UUIDUtil.getUUID32());
		}
		
		
		if(isAjax){
			if(jumpUrl == null){
				jumpUrl = "";
			}
			
		//	response.setHeader("login", "login?jumpUrl="+jumpUrl);//设置登录页面响应http头。用来激活Ajax请求处理方式 Session超时后的跳转
		//	response.setHeader("login", Configuration.getUrl(request)+"login?jumpUrl="+jumpUrl);//设置登录页面响应http头。用来激活Ajax请求处理方式 Session超时后的跳转
			
			WebUtil.writeToWeb(JsonUtils.toJSONString(formCaptcha), "json", response);
			
			return null;
		}else{
			model.addAttribute("formCaptcha", formCaptcha);
			if(jumpUrl == null || "".equals(jumpUrl.trim())){
				String referer= request.getHeader("referer");  
				if(referer != null && !"".equals(referer.trim())){
					//分离URL
					//移除开始部分的相同的字符,不区分大小写
					String uri = StringUtils.removeStartIgnoreCase(referer,Configuration.getUrl(request));
					//是否为默认登录页面
					boolean isLogin = false;//false:不是  
					
					if(uri != null && !"".equals(uri.trim())){
						//截取到等于第二个参数的字符串为止,从左往右
						String before_a = StringUtils.substringBefore(uri.trim(), "?");
						//截取到等于第二个参数的字符串为止,从左往右
						String before_b = StringUtils.substringBefore(uri.trim(), ".");
						//截取到等于第二个参数的字符串为止,从左往右
						String before_c = StringUtils.substringBefore(uri.trim(), "/");
						
						if(before_a.equalsIgnoreCase("login")){
							isLogin = true;//标记为默认登录页面
						}
						if(before_b.equalsIgnoreCase("login")){
							isLogin = true;
						}
						if(before_c.equalsIgnoreCase("login")){
							isLogin = true;
						}
					}else{
						//如果没有参数,则跳到首页
						uri = "index";
					}
					if(isLogin == false){//如果来源不是登录页面，则写入跳转参数
						
						String encodedRedirectURL = response.encodeRedirectURL("login?jumpUrl="+Base64.encodeBase64URL(uri));
						
						response.sendRedirect((Configuration.getPath() != null && !"".equals(Configuration.getPath()) ?Configuration.getPath()+"/" : "")+encodedRedirectURL);
						return null;
					}
				}
			}

			String dirName = templateService.findTemplateDir_cache();
			
			return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/login";
		}	
	}
	/**
	 * 会员登录
	 * @param jumpUrl 跳转URL
	 * @param rememberMe 记住密码
	 * @param thirdPartyOpenId 第三方用户获取唯一标识  例如微信公众号openid
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/login",method=RequestMethod.POST) 
	public String login2(ModelMap model,String userName, String password,Boolean rememberMe,String jumpUrl,
			RedirectAttributes redirectAttrs,
			String token,String captchaKey,String captchaValue,
			String thirdPartyOpenId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据

		
		Map<String,String> error = new HashMap<String,String>();
		
		
		
		if(userName == null || "".equals(userName.trim())){
			//用户名不能为空
			error.put("userName", ErrorView._815.name());//用户名称不能为空
		}else{
			userName = userName.trim();
		}
		if(password == null || "".equals(password.trim())){
			//密码不能为空
			error.put("password", ErrorView._816.name());//密码不能为空
		}else{
			if(password.trim().length() != 64){//判断是否是64位SHA256
				error.put("password", ErrorView._801.name());//密码长度错误
			}
		}
		if(rememberMe == null){
			rememberMe =false;
		}
		
		//判断令牌
		if(token != null && !"".equals(token.trim())){	
			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
				if(!token_sessionid.equals(token)){
					error.put("token", ErrorView._13.name());
				}
			}else{
				error.put("token", ErrorView._12.name());
			}
		}else{
			error.put("token", ErrorView._11.name());
		}

		
		User user = null;
		if(error.size() == 0){
			boolean isCaptcha = captchaManage.login_isCaptcha(userName);
			
			if(isCaptcha){//如果需要验证码		
				
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
				
			}
			
			if(error.size() == 0){
				//验证用户名
				user = userService.findUserByUserName(userName);
				if(user != null){
					List<UserGrade> userGradeList = userGradeService.findAllGrade_cache();	
					if(userGradeList != null && userGradeList.size() >0){
						for(UserGrade userGrade : userGradeList){//取得所有等级 
							if(user.getPoint() >= userGrade.getNeedPoint()){
								user.setGradeId(userGrade.getId());
								user.setGradeName(userGrade.getName());//将等级值设进等级参数里
								break;
							}
						}
					}
					//密码
					password = SHA.sha256Hex(password.trim()+"["+user.getSalt()+"]");
					
					//判断密码
					if(user.getState() >1 ){
						//禁止用户
						error.put("userName", ErrorView._824.name());//禁止用户
					}else if(password.equals(user.getPassword())){
						
						
						//访问令牌
						String accessToken = UUIDUtil.getUUID32();
						//刷新令牌
						String refreshToken = UUIDUtil.getUUID32();

						//删除缓存用户状态
						userManage.delete_userState(user.getUserName());

						//写入登录日志
						UserLoginLog userLoginLog = new UserLoginLog();
						userLoginLog.setId(userLoginLogManage.createUserLoginLogId(user.getId()));
						userLoginLog.setIp(IpAddress.getClientIpAddress(request));
						userLoginLog.setUserId(user.getId());
						userLoginLog.setTypeNumber(10);//登录
						userLoginLog.setLogonTime(new Date());
						Object new_userLoginLog = userLoginLogManage.createUserLoginLogObject(userLoginLog);
						userService.saveUserLoginLog(new_userLoginLog);
						
						
						
						String openId = "";//第三方openId
						if(thirdPartyOpenId != null && !"".equals(thirdPartyOpenId.trim())){
							openId = thirdPartyOpenId.trim();
							oAuthManage.addOpenId(openId,refreshToken);
						}
						
						oAuthManage.addAccessToken(accessToken, new AccessUser(user.getId(),user.getUserName(),user.getNickname(),user.getAvatarPath(),user.getAvatarName(),user.getSecurityDigest(),rememberMe,openId));
						oAuthManage.addRefreshToken(refreshToken, new RefreshUser(accessToken,user.getId(),user.getUserName(),user.getNickname(),user.getAvatarPath(),user.getAvatarName(),user.getSecurityDigest(),rememberMe,openId));
						
						
						
						//存放时间 单位/秒
						int maxAge = 0;
						if(rememberMe == true){
							maxAge = WebUtil.cookieMaxAge;//默认Cookie有效期
						}
						
						//将访问令牌添加到Cookie
						WebUtil.addCookie(response, "cms_accessToken", accessToken, maxAge);
						//将刷新令牌添加到Cookie
						WebUtil.addCookie(response, "cms_refreshToken", refreshToken, maxAge);
						AccessUserThreadLocal.set(new AccessUser(user.getId(),user.getUserName(),user.getNickname(),user.getAvatarPath(),user.getAvatarName(),user.getSecurityDigest(),rememberMe,openId));
						
					}else{
						//密码错误
						error.put("password", ErrorView._826.name());//密码错误
					}
				}else{
					//用户名错误
					error.put("userName",  ErrorView._825.name());//用户名错误
				}
			}
			
		}
		
		//登录失败处理
		if(error.size() >0){
			//统计每分钟原来提交次数
			Integer original = settingManage.getSubmitQuantity("login", userName);
    		if(original != null){
    			settingManage.addSubmitQuantity("login", userName,original+1);//刷新每分钟原来提交次数
    		}else{
    			settingManage.addSubmitQuantity("login", userName,1);//刷新每分钟原来提交次数
    		}

			//添加用户名到Cookie
			WebUtil.addCookie(response, "cms_userName", userName, 60);
		}else{
			//删除每分钟原来提交次数
			settingManage.deleteSubmitQuantity("login", userName);
			WebUtil.deleteCookie(response, "cms_userName");
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
		
		//跳转URL
		String _jumpUrl = "";
		if(jumpUrl != null && !"".equals(jumpUrl.trim())){
			//Base64解码后参数进行URL编码
			String parameter = WebUtil.parameterEncoded(Base64.decodeBase64URL(jumpUrl.trim()));
			
			String encodedRedirectURL = response.encodeRedirectURL(parameter);
			_jumpUrl = encodedRedirectURL;
		}else{
			_jumpUrl = "index";
		}
			
		if(isAjax){//Ajax方式返回数据
    		Map<String,Object> ajax_return = new HashMap<String,Object>();//返回
    		if(error != null && error.size() >0){
    			ajax_return.put("success", "false");
    			ajax_return.put("error", returnError);
    			
    			boolean isCaptcha = captchaManage.login_isCaptcha(userName);

    			if(isCaptcha){
    				ajax_return.put("captchaKey", UUIDUtil.getUUID32());
    			}
    		}else{
    			ajax_return.put("success", "true");
    			ajax_return.put("jumpUrl", _jumpUrl);
    			ajax_return.put("systemUser", new AccessUser(user.getId(),user.getUserName(),user.getNickname(),user.getAvatarPath(),user.getAvatarName(),null,false,""));//登录用户
    		}
    		
    		
    		WebUtil.writeToWeb(JsonUtils.toJSONString(ajax_return), "json", response);
			return null;
		}else{
			
			
			//登录失败处理
			if(error.size() >0){
				redirectAttrs.addFlashAttribute("error", returnError);//重定向传参
				redirectAttrs.addAttribute("jumpUrl", jumpUrl);
				return "redirect:/login"+(jumpUrl != null && !"".equals(jumpUrl.trim()) ? "?jumpUrl={jumpUrl}" : "");
			}else{//登录成功处理
	
				if(jumpUrl != null && !"".equals(jumpUrl.trim())){
					//Base64解码后参数进行URL编码
					String parameter = WebUtil.parameterEncoded(Base64.decodeBase64URL(jumpUrl));
					
					String encodedRedirectURL = response.encodeRedirectURL(parameter);
					response.sendRedirect((Configuration.getPath() != null && !"".equals(Configuration.getPath()) ?Configuration.getPath()+"/" : "")+encodedRedirectURL);
					return null;
					
				}
			}
		}
		//默认跳转
		return "redirect:/index";
	}
	
	
	
	
	
	/**
	 * 会员退出
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/logout",method=RequestMethod.POST) 
	public String logout(ModelMap model,String token,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		Map<String,String> error = new HashMap<String,String>();
		//判断令牌
		if(token != null && !"".equals(token.trim())){	
			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
				if(!token_sessionid.equals(token)){
					error.put("token", ErrorView._13.name());
				}
			}else{
				error.put("token", ErrorView._12.name());
			}
		}else{
			error.put("token", ErrorView._11.name());
		}
		
		
		if(error.size() ==0){
			//获取登录用户
		  	AccessUser accessUser = AccessUserThreadLocal.get();
			if(accessUser != null){
				userManage.delete_userState(accessUser.getUserName());
			}
			

			String refreshToken = WebUtil.getCookieByName(request, "cms_refreshToken");
			String accessToken = WebUtil.getCookieByName(request, "cms_accessToken");
			if(refreshToken != null && !"".equals(refreshToken.trim())){
				//删除刷新令牌
    			oAuthManage.deleteRefreshToken(refreshToken);
			}
			if(accessToken != null && !"".equals(accessToken.trim())){
				//删除访问令牌
    			oAuthManage.deleteAccessToken(accessToken.trim());
			}
			
			WebUtil.deleteCookie(response, "cms_refreshToken");
			WebUtil.deleteCookie(response, "cms_accessToken");
		}
		
		
		if(isAjax){
			Map<String,Object> returnValue = new HashMap<String,Object>();
			
			if(error != null && error.size() >0){
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
				returnValue.put("success", "false");
				returnValue.put("error", returnError);	
    		}else{
    			String jumpUrl = "login";
    			returnValue.put("success", "true");
    			returnValue.put("jumpUrl", jumpUrl);
    		}
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			if(error.size() == 0){
				//跳转到登录页
				return "redirect:/login";
			}
			return "redirect:/index";
			
		}
	}
	
	
	/**
	 * 找回密码 第一步界面
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/findPassWord/step1",method=RequestMethod.GET) 
	public String findPassWord_step1_UI(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据

	    Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
	    String captchaKey = UUIDUtil.getUUID32();
	    model.addAttribute("captchaKey",captchaKey);
	    returnValue.put("captchaKey",captchaKey);
	    if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();   
		    return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/findPassWord_step1";	
		}
	}
	
	/**
	 * 找回密码 第一步
	 * @param jumpUrl 跳转URL
	 * @param encryptionData 加密数据
	 * @param secretKey 密钥
	 * @param token 令牌
	 * @param captchaKey 验证Key
	 * @param captchaValue 验证码
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/findPassWord/step1",method=RequestMethod.POST) 
	public String findPassWord_step1(ModelMap model,String userName,
			String captchaKey,String captchaValue,
			String token,RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
	    
	    Map<String,String> error = new HashMap<String,String>();
	    
	    
	    //判断令牌
	    if(token != null && !"".equals(token.trim())){	
  			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
  			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
  				if(!token_sessionid.equals(token)){
  					error.put("token", ErrorView._13.name());
  				}
  			}else{
  				error.put("token", ErrorView._12.name());
  			}
  		}else{
  			error.put("token", ErrorView._11.name());
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
	    
	    if(userName != null && !"".equals(userName.trim())){
	    	 User user = userManage.query_cache_findUserByUserName(userName.trim());
	    	 if(user == null){
	    		 error.put("userName", ErrorView._910.name());//用户不存在
	    	 }else{
	    		 if(user.getType() >10){
	    			 error.put("userName", ErrorView._920.name());//用户不是本地密码账户
	    		 }
	    		 
	    	 }
	    }else{
	    	error.put("userName", ErrorView._815.name());//用户名称不能为空
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
    			returnValue.put("jumpUrl", "findPassWord/step2"+"?userName="+userName);
    		}
    		
    		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			
			if(error != null && error.size() >0){//如果有错误
				
				redirectAttrs.addFlashAttribute("error", returnError);//重定向传参
				redirectAttrs.addFlashAttribute("userName",userName);
				
				String referer = request.getHeader("referer");	

				referer = StringUtils.removeStartIgnoreCase(referer,Configuration.getUrl(request));//移除开始部分的相同的字符,不区分大小写
				referer = StringUtils.substringBefore(referer, ".");//截取到等于第二个参数的字符串为止
				referer = StringUtils.substringBefore(referer, "?");//截取到等于第二个参数的字符串为止
				
				String queryString = request.getQueryString() != null && !"".equals(request.getQueryString().trim()) ? "?"+request.getQueryString() :"";
				return "redirect:/"+referer+queryString;
					
			}
			
			return "redirect:/findPassWord/step2?userName="+userName;

		}
	}
	
	
	/**
	 * 找回密码 第二步界面
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/findPassWord/step2",method=RequestMethod.GET) 
	public String findPassWord_step2_UI(ModelMap model,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		   
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
		
	    
	    User newUser = new User();
	    
	    Map<String,String> error = new HashMap<String,String>();
	    if(userName != null && !"".equals(userName.trim())){
	    	 User user = userService.findUserByUserName(userName.trim());
	    	 if(user != null){
	    		 newUser.setId(user.getId());
	    		 newUser.setUserName(user.getUserName());
	    		 newUser.setIssue(user.getIssue());
	    		 
	    		 model.addAttribute("user", newUser);
	    		 returnValue.put("user", newUser);
	    	 }else{
	    		 error.put("userName", ErrorView._910.name());//用户不存在
	    	 }
	    }else{
	    	error.put("userName", ErrorView._815.name());//用户名称不能为空
	    }
  
	    //显示验证码
	    String captchaKey = UUIDUtil.getUUID32();
	    model.addAttribute("captchaKey",captchaKey);
	    returnValue.put("captchaKey",captchaKey);

	    if(isAjax == true){
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
		    
    		if(error != null && error.size() >0){
    			returnValue.put("success", "false");
    			returnValue.put("error", returnError);
    		}else{
    			returnValue.put("success", "true");
    		}
    		
    		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			if(error.size() > 0){
		    	return "redirect:/findPassWord/step1";
		    }
			String dirName = templateService.findTemplateDir_cache();  
		    return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/findPassWord_step2";	
		}   
	}
	
	
	
	/**
	 * 找回密码 第二步
	 * @param jumpUrl 跳转URL
	 * @param token 令牌
	 * @param captchaKey 验证Key
	 * @param captchaValue 验证码
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/findPassWord/step2",method=RequestMethod.POST) 
	public String findPassWord_step2(ModelMap model,User formbean,
			String captchaKey,String captchaValue,
			String jumpUrl,String token,RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
	    String dirName = templateService.findTemplateDir_cache();

		
		Map<String,String> error = new HashMap<String,String>();

		//判断令牌
		if(token != null && !"".equals(token.trim())){	
			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
				if(!token_sessionid.equals(token)){
					error.put("token", ErrorView._13.name());
				}
			}else{
				error.put("token", ErrorView._12.name());
			}
		}else{
			error.put("token", ErrorView._11.name());
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
				
			
		
		
		User user = null;
		if(formbean.getUserName() != null && !"".equals(formbean.getUserName().trim())){
			user = userService.findUserByUserName(formbean.getUserName().trim());
			if(user == null){
				error.put("userName", ErrorView._825.name());//用户名错误
			}else{
				 if(user.getType() >10){
	    			 error.put("userName", ErrorView._920.name());//用户不是本地密码账户
	    		 }
			}
		}else{
			error.put("userName", ErrorView._815.name());//用户名称不能为空
		}
		
	
		//新密码
		String newPassword = "";
		
		if(user != null){
			if(formbean.getPassword() != null && !"".equals(formbean.getPassword().trim())){
				if(formbean.getPassword().trim().length() != 64){//判断是否是64位SHA256
					error.put("password", ErrorView._801.name());//密码长度错误
				}else{
					newPassword = SHA.sha256Hex(formbean.getPassword().trim()+"["+user.getSalt()+"]");
				}
			}else{
				error.put("password", ErrorView._816.name());//密码不能为空
			}

			
			if(formbean.getAnswer() != null && !"".equals(formbean.getAnswer().trim())){//密码提示答案
				if(formbean.getAnswer().trim().length() != 64){//判断是否是64位SHA256
					error.put("answer", ErrorView._819.name());//密码提示答案长度错误
				}else{
					
					String answer = SHA.sha256Hex(formbean.getAnswer().trim());
					//比较密码答案
					if(!answer.equals(user.getAnswer())){
						error.put("answer", ErrorView._827.name());//密码提示答案错误
					}
					
					
				}
			}else{
				error.put("answer", ErrorView._820.name());//密码提示答案不能为空
			}
		}
		
		
		if(error.size() == 0){
			//修改密码
			
			int i = userService.updatePassword(formbean.getUserName().trim(), newPassword,new Date().getTime(), user.getUserVersion());
			userManage.delete_userState(formbean.getUserName().trim());
			if(i == 0){
				error.put("user", ErrorView._828.name());//找回密码错误
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
    			
    			returnValue.put("captchaKey", UUIDUtil.getUUID32());
    		}else{
    			returnValue.put("success", "true");
    			
    		}
    		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			if(error != null && error.size() >0){//如果有错误
				
				redirectAttrs.addFlashAttribute("error", returnError);//重定向传参
				redirectAttrs.addFlashAttribute("user",formbean);
				
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
				model.addAttribute("message", "找回密码成功");
				
				String referer = Configuration.getUrl(request)+"login";//默认跳转到登录页
				
				
				model.addAttribute("urlAddress", referer);
				
				return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/jump";	
			}
		}
	}
	
	

}
