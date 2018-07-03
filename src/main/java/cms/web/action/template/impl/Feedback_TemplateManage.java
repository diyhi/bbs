package cms.web.action.template.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cms.bean.setting.SystemSetting;
import cms.bean.template.Forum;
import cms.service.setting.SettingService;
import cms.utils.UUIDUtil;

/**
 * 在线留言 -- 模板方法实现
 *
 */
@Component("feedback_TemplateManage")
public class Feedback_TemplateManage {
	@Resource SettingService settingService;
	
	/**
	 * 在线留言  -- 添加
	 * @param forum
	 */
	public Map<String,Object> addFeedback_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();

		value.put("captchaKey",UUIDUtil.getUUID32());//验证码
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		//如果全局不允许提交在线留言
		if(systemSetting.isAllowFeedback() == false){
			value.put("allowFeedback",false);//不允许提交在线留言
		}else{
			value.put("allowFeedback",true);//允许提交在线留言
		}
		
		
		return value;
	}
}
