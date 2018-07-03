package cms.web.action.common;

import javax.annotation.Resource;

import cms.bean.setting.SystemSetting;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.web.action.setting.SettingManage;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 验证码管理
 *
 */
@Component("captchaManage")
public class CaptchaManage {
	@Resource TemplateService templateService;
	@Resource CaptchaManage captchaManage;
	
	@Resource SettingManage settingManage;
	
	@Resource SettingService settingService;
	/**---------------------------------- 验证码 ---------------------------------------**/
	/**
	 * 生成验证码
	 * @param captchaKey 验证码KEY
	 * @param captcha 验证码
	 * @return
	 */
	@Cacheable(value="captchaManage_cache_captcha",key="#captchaKey")
	public String captcha_generate(String captchaKey,String captcha){
		return captcha;
	}
	/**
	 * 删除验证码
	 * @param captchaKey 验证码KEY
	 * @return
	 */
	@CacheEvict(value="captchaManage_cache_captcha",key="#captchaKey")
	public void captcha_delete(String captchaKey){
	}
	

	
	/**---------------------------------- 令牌 ---------------------------------------**/
	
	/**
	 *  生成员工令牌标记
	 * @param token 令牌标记
	 * @param sessionId
	 * @return
	 
	@Cacheable(value="captchaManage_cache_token",key="#token")
	public String token_generate(String token,String sessionId){
		return sessionId;
	}*/
	/**
	 *  删除员工令牌标记
	 * @param captchaKey 验证码KEY
	 * @return
	 
	@CacheEvict(value="captchaManage_cache_token",key="#token")
	public void token_delete(String token){
	}*/
	
	/**---------------------------------- 话题 ---------------------------------------**/
	/**
	 * 是否显示验证码
	 * @param userName 用户名称
	 * @return 验证码标记
	 */
	public boolean topic_isCaptcha(String userName){
		boolean isCaptcha = false;
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getTopic_submitQuantity() <=0){//为0时每次都出现验证码
			isCaptcha = true;
		}else{
			//用户每分钟提交次数
			int quantity = settingManage.submitQuantity_add("topic", userName, 0);//原来总次数
			
			//如果每用户每分钟提交超过设定次数，则需验证码
			if(quantity >= systemSetting.getTopic_submitQuantity()){
				isCaptcha = true;
			}
			
		}
		return isCaptcha;
	}
	
	
	/**---------------------------------- 评论 ---------------------------------------**/
	/**
	 * 是否显示验证码
	 * @param userName 用户名称
	 * @return 验证码标记
	 */
	public boolean comment_isCaptcha(String userName){
		boolean isCaptcha = false;
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getComment_submitQuantity() <=0){//为0时每次都出现验证码
			isCaptcha = true;
		}else{
			//用户每分钟提交次数
			int quantity = settingManage.submitQuantity_add("comment", userName, 0);//原来总次数
			
			//如果每用户每分钟提交超过设定次数，则需验证码
			if(quantity >= systemSetting.getComment_submitQuantity()){
				isCaptcha = true;
			}
			
		}
		return isCaptcha;
	}
	
	
	
/**---------------------------------- 用户登录 ---------------------------------------**/
	/**
	 * 是否显示验证码
	 * @param userName 用户名称
	 * @return 
	 */
	public boolean login_isCaptcha(String userName){

		//是否需要验证码  true:要  false:不要
		boolean isCaptcha = false;
		
		//用户每分钟提交次数
		int quantity = settingManage.submitQuantity_add("login", userName, 0);//原来总次数
		
		//如果每用户每分钟提交超过设定次数，则需验证码
		if(quantity >= settingService.findSystemSetting_cache().getLogin_submitQuantity()){
			isCaptcha = true;
		}
		return isCaptcha;
	}
	
	
	

}
