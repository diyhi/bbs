package cms.web.action.common;


import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import cms.bean.ErrorView;
import cms.bean.setting.AllowRegisterAccount;
import cms.bean.setting.SystemSetting;
import cms.bean.thirdParty.WeChatConfig;
import cms.bean.thirdParty.WeiXinOpenId;
import cms.bean.thirdParty.WeiXinUserInfo;
import cms.bean.user.AccessUser;
import cms.bean.user.RefreshUser;
import cms.bean.user.User;
import cms.bean.user.UserAuthorization;
import cms.bean.user.UserLoginLog;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.service.user.UserService;
import cms.utils.Base64;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.CSRFTokenManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.membershipCard.MembershipCardGiftTaskManage;
import cms.web.action.setting.SettingManage;
import cms.web.action.template.TemplateMain;
import cms.web.action.thirdParty.ThirdPartyManage;
import cms.web.action.user.UserLoginLogManage;
import cms.web.action.user.UserManage;
import cms.web.taglib.Configuration;


/**
 * 第三方服务管理
 *
 */
@Controller
public class ThirdPartyFormAction {
	
	@Resource CSRFTokenManage csrfTokenManage;
	@Resource TemplateService templateService;//通过接口引用代理返回的对象
	@Resource TemplateMain templateMain;
	@Resource ThirdPartyManage thirdPartyManage;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource SettingService settingService;
	@Resource UserService userService;
	@Resource UserManage userManage;
	@Resource UserLoginLogManage userLoginLogManage;
	@Resource OAuthManage oAuthManage;
	@Resource SettingManage settingManage;
	@Resource FileManage fileManage;
	@Resource MembershipCardGiftTaskManage membershipCardGiftTaskManage;
	
	/**
	 * 查询微信openid
	 * @param code code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
	 */
	@RequestMapping(value="/thirdParty/queryWeiXinOpenId",method = RequestMethod.POST) 
	@ResponseBody//方式来做ajax,直接返回字符串
	public String queryWeiXinOpenId(ModelMap model,String code,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(code != null && !"".equals(code.trim())){
			
			WeiXinOpenId oldWeiXinOpenId = thirdPartyManage.getWeiXinOpenId(code.trim());
			if(oldWeiXinOpenId != null){
				//只允许查一次
				thirdPartyManage.deleteWeiXinOpenId(code.trim());
				return JsonUtils.toJSONString(oldWeiXinOpenId);
			}else{
				WeiXinOpenId weiXinOpenId = thirdPartyManage.queryWeiXinOpenId(code.trim());
				if(weiXinOpenId != null){
					return JsonUtils.toJSONString(weiXinOpenId);
				}
			}
		}
		return "";
	}
	
	
	/**
	 * 第三方登录链接
	 * @param model
	 * @param interfaceProduct 接口产品
	 * @param jumpUrl 重定向参数
	 * @param redirectAttrs
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/thirdParty/loginLink",method = RequestMethod.GET)
	public String loginLink(ModelMap model,Integer interfaceProduct,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		if(isAjax){
			
			
			Map<String,Object> returnValue = new HashMap<String,Object>();
			String domain = WebUtil.getOriginDomain(request);
			
			if(domain != null && !"".equals(domain.trim())){
				if(interfaceProduct != null){
					if(interfaceProduct.equals(10)){//微信
						if(accessSourceDeviceManage.accessDevices(request).equals("pc")){//电脑端
							WeChatConfig weChatConfig = thirdPartyManage.queryWeChatConfig();
					    	if(weChatConfig != null){
								String appid = weChatConfig.getOp_appID();//开放平台唯一标识
								String redirect_uri = domain+"thirdParty/loginRedirect";
								
								if(jumpUrl != null && !"".equals(jumpUrl.trim())){
									redirect_uri = domain+"thirdParty/loginRedirect?jumpUrl="+jumpUrl.trim();//Base64安全编码;
								}
								redirect_uri = java.net.URLEncoder.encode(redirect_uri,"utf-8");   
								String csrfToken = csrfTokenManage.getToken(request);//获取CSRF令牌;
								
								String state = interfaceProduct+"_"+(csrfToken != null ? csrfToken : "");
								
								//授权接口
								returnValue.put("redirectUrl", "https://open.weixin.qq.com/connect/qrconnect?appid="+appid+"&redirect_uri="+redirect_uri+"&response_type=code&scope=snsapi_login&state="+state+"#wechat_redirect");
								
					    	}
						}else if(accessSourceDeviceManage.accessDevices(request).equals("wap")){//手机端
							WeChatConfig weChatConfig = thirdPartyManage.queryWeChatConfig();
					    	if(weChatConfig != null){
								String appid = weChatConfig.getOa_appID();//公众号唯一标识
								
								String redirect_uri = domain+"thirdParty/loginRedirect";
								
								if(jumpUrl != null && !"".equals(jumpUrl.trim())){
									redirect_uri = domain+"thirdParty/loginRedirect?jumpUrl="+jumpUrl.trim();//Base64安全编码;
								}
								redirect_uri = java.net.URLEncoder.encode(redirect_uri,"utf-8");   
								
								String csrfToken = csrfTokenManage.getToken(request);//获取CSRF令牌;
								
								String state = interfaceProduct+"_"+(csrfToken != null ? csrfToken : "");
								
								//授权接口
								returnValue.put("redirectUrl", "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+redirect_uri+"&response_type=code&scope=snsapi_userinfo&state="+state+"&connect_redirect=1#wechat_redirect");
								
					    	}
						}
					}
				}
			}
			
			
			
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}
		
		
		
		if(interfaceProduct != null && interfaceProduct.equals(10)){//微信
			if(accessSourceDeviceManage.accessDevices(request).equals("pc")){//电脑端
				WeChatConfig weChatConfig = thirdPartyManage.queryWeChatConfig();
		    	if(weChatConfig != null){
					String appid = weChatConfig.getOp_appID();//开放平台唯一标识
					
					String redirect_uri = Configuration.getUrl(request)+"thirdParty/loginRedirect";
					
					if(jumpUrl != null && !"".equals(jumpUrl.trim())){
						redirect_uri = Configuration.getUrl(request)+"thirdParty/loginRedirect?jumpUrl="+jumpUrl.trim();//Base64安全编码;
					}
					redirect_uri = java.net.URLEncoder.encode(redirect_uri,"utf-8");   
					String csrfToken = csrfTokenManage.getToken(request);//获取CSRF令牌;
					
					String state = interfaceProduct+"_"+csrfToken;
					
					
					
					return "redirect:https://open.weixin.qq.com/connect/qrconnect?appid="+appid+"&redirect_uri="+redirect_uri+"&response_type=code&scope=snsapi_login&state="+state+"#wechat_redirect";
					
		    	}
			}else if(accessSourceDeviceManage.accessDevices(request).equals("wap")){//手机端
				WeChatConfig weChatConfig = thirdPartyManage.queryWeChatConfig();
		    	if(weChatConfig != null){
					String appid = weChatConfig.getOa_appID();//公众号唯一标识
					
					String redirect_uri = Configuration.getUrl(request)+"thirdParty/loginRedirect";
					
					if(jumpUrl != null && !"".equals(jumpUrl.trim())){
						redirect_uri = Configuration.getUrl(request)+"thirdParty/loginRedirect?jumpUrl="+jumpUrl.trim();//Base64安全编码;
					}
					redirect_uri = java.net.URLEncoder.encode(redirect_uri,"utf-8");   
					
					String csrfToken = csrfTokenManage.getToken(request);//获取CSRF令牌;
					
					String state = interfaceProduct+"_"+csrfToken;
					
					return "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+redirect_uri+"&response_type=code&scope=snsapi_userinfo&state="+state+"&connect_redirect=1#wechat_redirect";
					
		    	}
			}
			
		}
		
		
		
		//当前模板使用的目录
		String dirName = templateService.findTemplateDir_cache();
		
		model.addAttribute("message","第三方登录链接错误");//返回消息  
		return "/templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/message";
	}
	
	
	
	
	/**
	 * 第三方登录重定向
	 * @param model
	 * @param code 微信公众号code
	 * @param state 自定义参数   微信公众号state 存放csrf令牌
	 * @param jumpUrl 重定向参数
	 * @param redirectAttrs
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/thirdParty/loginRedirect")
	public String loginRedirect(ModelMap model,String code,String state,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();
		
		Integer interfaceProduct = -1;//接口产品
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		UserAuthorization userAuthorization = null;
		
		//判断令牌
		if(state != null && !"".equals(state.trim())){	
			String[] param_arr = state.trim().split("_");
			interfaceProduct = Integer.parseInt(param_arr[0]);
			String csrfToken = null;
			if(param_arr.length ==2){
				csrfToken = param_arr[1];
			}
			
			csrfTokenManage.processCsrfToken(request, csrfToken,error);
			
			
		}else{
			error.put("state", "自定义参数不能为空");
		}

		if(error.size() ==0){
			if(interfaceProduct.equals(10)){//微信
				WeChatConfig weChatConfig = thirdPartyManage.queryWeChatConfig();
				if(weChatConfig != null){
					String appid = "";//应用唯一标识
					String secret = "";//应用密钥
					
					if(accessSourceDeviceManage.accessDevices(request).equals("pc")){//电脑端
						appid = weChatConfig.getOp_appID();
						secret = weChatConfig.getOp_appSecret();
					}else if(accessSourceDeviceManage.accessDevices(request).equals("wap")){//手机端
						//微信浏览器端
						appid = weChatConfig.getOa_appID();
						secret = weChatConfig.getOa_appSecret();
					}
					
					if(appid != null && !"".equals(appid.trim()) && secret != null && !"".equals(secret.trim())){
						WeiXinUserInfo weiXinUserInfo = thirdPartyManage.queryWeiXinUserInfo(code,appid,secret);
						if(weiXinUserInfo != null){
							if(weiXinUserInfo.getErrorCode() == null || "".equals(weiXinUserInfo.getErrorCode())){
								
		
								if(weiXinUserInfo.getUnionId() != null && !"".equals(weiXinUserInfo.getUnionId())){
									userAuthorization = this.createWeiXinUserInfo(weiXinUserInfo.getUnionId(),weiXinUserInfo.getOpenId(), error,request, response);
									
									
									if(error.size() ==0 && !isAjax){
										//重定向到
										if(jumpUrl != null && !"".equals(jumpUrl.trim())){

											//Base64解码后参数进行URL编码
											String parameter = WebUtil.parameterEncoded(Base64.decodeBase64URL(jumpUrl));

											String encodedRedirectURL = response.encodeRedirectURL(parameter);
											
											if("login".equalsIgnoreCase(encodedRedirectURL)){
												return "redirect:/index";
											}else{
												response.sendRedirect((Configuration.getPath() != null && !"".equals(Configuration.getPath()) ?Configuration.getPath()+"/" : "/")+encodedRedirectURL);
												return null;
											}
										}else{
											return "redirect:/index";
											
										}
									}
									
									
								}else{
									error.put("weiXinUserInfo", "微信unionid为空，请将公众号绑定到微信开放平台");
								}
							}else{
								error.put("weiXinUserInfo", weiXinUserInfo.getErrorCode()+" -- "+weiXinUserInfo.getErrorMessage());
							}
						}else{
							
							error.put("weiXinUserInfo", "查询微信用户基本信息为空");
						}
					}
					
					
				}else{
					error.put("weChatConfig", "微信配置信息不存在");
				}
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
		
		if(isAjax){
			Map<String,Object> returnValue = new HashMap<String,Object>();
			
			if(error != null && error.size() >0){
    			returnValue.put("success", "false");
    			returnValue.put("error", returnError);
    			
    		}else{
    			returnValue.put("success", "true");
    			returnValue.put("userAuthorization", userAuthorization);
    		}
			
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}
		
		
		if(error.size() >0){
    		if(accessSourceDeviceManage.accessDevices(request).equals("wap")){//单页使用
    			String htmlContent = "<html><head><meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0'><style type='text/css'>html,body{height: 100%; margin: 0;padding: 0;}</style></head>"
    					+ "<body><div style='width: 100%;height: 100%;display:flex; justify-content:center;align-items:center; font-size: 16px;color: #666;'>"
    					+ returnError.entrySet().iterator().next().getValue()
    					+ "</div></body></html>";

    			WebUtil.writeToWeb(htmlContent, "html", response);
    			return null;
    			
    		}
		}
		
		model.addAttribute("message","登录重定向失败");//返回消息  
		if(error.size() >0){
			model.addAttribute("message",returnError.entrySet().iterator().next().getValue());//返回消息
		}
		//当前模板使用的目录
		String dirName = templateService.findTemplateDir_cache();
		return "/templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/message";
		
	}
	
	/**
	 * 生成微信用户信息
	 * @param unionId 第三方用户信息唯一凭证
	 * @param openId 用户的唯一标识
	 * @param error 错误信息集合
	 * @param request
	 * @param response
	 * @return
	 */
	private UserAuthorization createWeiXinUserInfo(String unionId,String openId,Map<String,String> error,
			HttpServletRequest request, HttpServletResponse response){

		String platformUserId = userManage.thirdPartyUserIdToPlatformUserId(unionId,40);
		User user = userService.findUserByPlatformUserId(platformUserId);
		
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("weiXinUserInfo", ErrorView._21.name());//只读模式不允许提交数据
		}else{
			if(user == null){//用户不存在，执行注册
				
				//读取允许注册账号类型
				AllowRegisterAccount allowRegisterAccount =  settingManage.readAllowRegisterAccount();

				if(allowRegisterAccount != null && allowRegisterAccount.isWeChat()){
					user = new User();
					String id = UUIDUtil.getUUID22();
					user.setUserName(id);//会员用户名
					user.setAccount(userManage.queryUserIdentifier(40)+"-"+id);//账号
					user.setSalt(UUIDUtil.getUUID32());//盐值
					user.setSecurityDigest(new Date().getTime());//安全摘要
					user.setAllowUserDynamic(true);//是否允许显示用户动态
					user.setRealNameAuthentication(false);//是否实名认证
					user.setRegistrationDate(new Date());//注册日期
					user.setPoint(0L);//当前积分
					user.setDeposit(new BigDecimal("0"));//当前预存款
					user.setType(40);//用户类型 40:微信用户
					user.setPlatformUserId(platformUserId);//平台用户Id 
					user.setState(1);//用户状态    1:正常用户
					user.setUserVersion(0);//版本号
					
					
					try {
						userService.saveUser(user,null,null);
					} catch (Exception e) {
						error.put("register", ErrorView._823.name());//注册会员出错
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}else{//如果不允许注册
					error.put("register", ErrorView._862.name());//不允许注册
					
					
				}
				
				
			}
		}
		
		
			
		
		//自动登录
		if(error.size() == 0 && user != null){
			
			if(user.getState().equals(1)){
				//写入登录日志
				UserLoginLog userLoginLog = new UserLoginLog();
				userLoginLog.setId(userLoginLogManage.createUserLoginLogId(user.getId()));
				userLoginLog.setIp(IpAddress.getClientIpAddress(request));
				userLoginLog.setUserId(user.getId());
				userLoginLog.setTypeNumber(10);//登录
				userLoginLog.setLogonTime(new Date());
				Object new_userLoginLog = userLoginLogManage.createUserLoginLogObject(userLoginLog);
				userService.saveUserLoginLog(new_userLoginLog);

				
				//访问令牌
				String accessToken = UUIDUtil.getUUID32();
				//刷新令牌
				String refreshToken = UUIDUtil.getUUID32();

				if(openId != null && !"".equals(openId.trim())){
					oAuthManage.addOpenId(openId,refreshToken);
				}
				
				AccessUser accessUser = new AccessUser(user.getId(),user.getUserName(),user.getAccount(),user.getNickname(),fileManage.fileServerAddress(request)+user.getAvatarPath(),user.getAvatarName(), user.getSecurityDigest(),false,openId);
				oAuthManage.addAccessToken(accessToken, accessUser);
				oAuthManage.addRefreshToken(refreshToken, new RefreshUser(accessToken,user.getId(),user.getUserName(),user.getAccount(),user.getNickname(),fileManage.fileServerAddress(request)+user.getAvatarPath(),user.getAvatarName(),user.getSecurityDigest(),false,openId));

				//将访问令牌添加到Cookie
				WebUtil.addCookie(response, "cms_accessToken", accessToken, 0);
				//将刷新令牌添加到Cookie
				WebUtil.addCookie(response, "cms_refreshToken", refreshToken, 0);
				AccessUserThreadLocal.set(new AccessUser(user.getId(),user.getUserName(),user.getAccount(),user.getNickname(),fileManage.fileServerAddress(request)+user.getAvatarPath(),user.getAvatarName(),user.getSecurityDigest(),false,openId));
				
				//删除缓存
				userManage.delete_cache_findUserById(user.getId());
				userManage.delete_cache_findUserByUserName(user.getUserName());
				
				//异步执行会员卡赠送任务(长期任务类型)
				membershipCardGiftTaskManage.async_triggerMembershipCardGiftTask(user.getUserName());
				
				return new UserAuthorization(accessToken,refreshToken, accessUser);
			}else{
				error.put("register", ErrorView._824.name());//禁止用户
			}
			
			
		}
		return null;
	}
	
	
}
