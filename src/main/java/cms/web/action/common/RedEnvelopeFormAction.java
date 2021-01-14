package cms.web.action.common;


import java.math.BigDecimal;
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
import cms.bean.redEnvelope.GiveRedEnvelope;
import cms.bean.redEnvelope.ReceiveRedEnvelope;
import cms.bean.setting.SystemSetting;
import cms.bean.topic.Topic;
import cms.bean.user.AccessUser;
import cms.bean.user.ResourceEnum;
import cms.service.redEnvelope.RedEnvelopeService;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.service.user.UserRoleService;
import cms.service.user.UserService;
import cms.utils.Base64;
import cms.utils.JsonUtils;
import cms.utils.RateLimiterUtil;
import cms.utils.RefererCompare;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.CSRFTokenManage;
import cms.web.action.redEnvelope.RedEnvelopeManage;
import cms.web.action.topic.TopicManage;
import cms.web.action.user.UserRoleManage;
import cms.web.taglib.Configuration;

/**
 * 红包接收表单
 *
 */
@Controller
@RequestMapping("user/control/redEnvelope") 
public class RedEnvelopeFormAction {
	@Resource TemplateService templateService;
	@Resource RedEnvelopeService redEnvelopeService;
	@Resource RedEnvelopeManage redEnvelopeManage;
	
	
	@Resource UserRoleService userRoleService;
	@Resource UserRoleManage userRoleManage;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource CSRFTokenManage csrfTokenManage;
	@Resource UserService userService;
	@Resource SettingService settingService;
	@Resource TopicManage topicManage;
	
	/**
	 * 收红包 
	 * @param model
	 * @param giveRedEnvelopeId 发红包Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/addReceiveRedEnvelope", method=RequestMethod.POST)
	public String add(ModelMap model,String giveRedEnvelopeId,String token,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
			
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		
		Map<String,String> error = new HashMap<String,String>();
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("receiveRedEnvelope", ErrorView._21.name());//只读模式不允许提交数据
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
		
		
		//拆红包获得金额
		BigDecimal receiveRedEnvelopeAmount = null;
		
		if(RateLimiterUtil.apply("giveRedEnvelope-"+giveRedEnvelopeId, 50)){//限流
			//获取登录用户
		  	AccessUser accessUser = AccessUserThreadLocal.get();
		  	GiveRedEnvelope giveRedEnvelope = null;
		  	if(giveRedEnvelopeId != null){
		  		giveRedEnvelope = redEnvelopeService.findById(giveRedEnvelopeId);
		  		if(giveRedEnvelope != null){
		  			
		  			
		  		
		  			
		  			
		  			
		  			if(giveRedEnvelope.getRemainingQuantity() <=0){
		  				error.put("receiveRedEnvelope", ErrorView._3050.name());//红包已被抢光
		  				
		  			}else{
		  				ReceiveRedEnvelope receiveRedEnvelope = redEnvelopeService.findByReceiveRedEnvelopeId(redEnvelopeManage.createReceiveRedEnvelopeId(giveRedEnvelopeId, accessUser.getUserId()));
			  			if(receiveRedEnvelope != null){
			  				error.put("receiveRedEnvelope", ErrorView._3040.name());//每个红包只能领取一次
			  			}
		  			}
		  			
		  			Topic topic = topicManage.queryTopicCache(giveRedEnvelope.getBindTopicId());//查询缓存
					if(topic != null){
						if(!topic.getStatus().equals(20)){//20.已发布
							error.put("receiveRedEnvelope", ErrorView._3060.name());//话题未发布不允许领取红包
						}else{
							//检查权限
							boolean isPermission = userRoleManage.checkPermission(ResourceEnum._1001000,topic.getTagId());
				  			if(!isPermission){
				  				error.put("receiveRedEnvelope", ErrorView._3090.name());//没有领取红包权限
				  			}
						}
						
					}else{
						error.put("receiveRedEnvelope", ErrorView._3070.name());//话题不存在不允许领取红包
					}
					
					if(giveRedEnvelope.getRefundAmount() != null && giveRedEnvelope.getRefundAmount().compareTo(new BigDecimal("0")) >0){
						
						error.put("receiveRedEnvelope", ErrorView._3080.name());//红包已原路返还用户
					}
		  			
		  		}else{
		  			error.put("receiveRedEnvelope", ErrorView._3010.name());//没有发红包
		  		}
		  	}else{
		  		error.put("receiveRedEnvelope", ErrorView._3020.name());//发红包Id不能为空
		  	}
		 
			if(error.size() == 0){
				
				
				
				
				//收红包
				ReceiveRedEnvelope receiveRedEnvelope = new ReceiveRedEnvelope();
				receiveRedEnvelope.setId(redEnvelopeManage.createReceiveRedEnvelopeId(giveRedEnvelopeId, accessUser.getUserId()));
				receiveRedEnvelope.setReceiveUserId(accessUser.getUserId());//收红包的用户Id
				
				receiveRedEnvelope.setGiveRedEnvelopeId(giveRedEnvelopeId);//发红包Id
				receiveRedEnvelope.setGiveUserId(giveRedEnvelope.getUserId());
				receiveRedEnvelope.setReceiveTime(new Date());

				Object receiveRedEnvelope_obj = redEnvelopeManage.createReceiveRedEnvelopeObject(receiveRedEnvelope);
				String grabRedEnvelopeUserId = JsonUtils.toJSONString(accessUser.getUserId())+",";
				try {
					int i = redEnvelopeService.saveReceiveRedEnvelope(receiveRedEnvelope_obj, giveRedEnvelopeId, grabRedEnvelopeUserId);
					
					if(i >0){
						giveRedEnvelope = redEnvelopeService.findById(giveRedEnvelopeId);
						//查询用户领取到的红包金额
						BigDecimal amount = redEnvelopeManage.queryReceiveRedEnvelopeAmount(giveRedEnvelope,accessUser.getUserId());
						if(amount != null && amount.compareTo(new BigDecimal("0")) >0){
							ReceiveRedEnvelope new_receiveRedEnvelope =  redEnvelopeService.findByReceiveRedEnvelopeId(receiveRedEnvelope.getId());
							if(new_receiveRedEnvelope != null && new_receiveRedEnvelope.getAmount() != null && new_receiveRedEnvelope.getAmount().compareTo(new BigDecimal("0")) ==0 ){
								//拆红包--调用的方法包含删除收红包缓存
								Integer j = redEnvelopeManage.unwrapRedEnvelope(new_receiveRedEnvelope,amount,accessUser.getUserId(),accessUser.getUserName());
								if(j >0){//如果拆红包成功
									receiveRedEnvelopeAmount = amount;
								}
							}
						}
						
					}else{
						//删除缓存
				    	redEnvelopeManage.delete_cache_findByReceiveRedEnvelopeId(receiveRedEnvelope.getId());
					}
					//删除缓存
					redEnvelopeManage.delete_cache_findById(giveRedEnvelopeId);
					
				} catch (org.springframework.orm.jpa.JpaSystemException e) {
					error.put("receiveRedEnvelope", ErrorView._3030.name());//收红包错误
				}
		
			}
			
			
			
		}else{
			error.put("systemInfo", ErrorView._22.name());//系统繁忙
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
    			if(receiveRedEnvelopeAmount != null){
    				returnValue.put("receiveRedEnvelopeAmount", receiveRedEnvelopeAmount);
    			}
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
				model.addAttribute("message", "收红包成功");
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
