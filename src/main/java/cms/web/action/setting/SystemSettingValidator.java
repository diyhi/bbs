package cms.web.action.setting;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import cms.bean.setting.SystemSetting;

@Component("systemSettingValidator")
public class SystemSettingValidator implements Validator{
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(SystemSetting.class); 
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
		SystemSetting systemSetting = (SystemSetting)obj;
		
		//站点名称
		if(systemSetting.getTitle() != null && !"".equals(systemSetting.getTitle())){
			if(systemSetting.getTitle().length() >200){
				errors.rejectValue("title","errors.required", new String[]{"不能超过200个字符"},"");
			}
		}
		//站点关键词
		if(systemSetting.getKeywords() != null && !"".equals(systemSetting.getKeywords())){
			if(systemSetting.getKeywords().length() >200){
				errors.rejectValue("keywords","errors.required", new String[]{"不能超过200个字符"},"");
			}
		}
		//站点描述
		if(systemSetting.getDescription() != null && !"".equals(systemSetting.getDescription())){
			if(systemSetting.getDescription().length() >200){
				errors.rejectValue("description","errors.required", new String[]{"不能超过200个字符"},"");
			}
		}

		//登录密码每分钟连续错误
		if(systemSetting.getLogin_submitQuantity() != null){
			if(systemSetting.getLogin_submitQuantity() <0){
				errors.rejectValue("login_submitQuantity","errors.required", new String[]{"不能小于0"},"");
			}
		}else{
			errors.rejectValue("login_submitQuantity","errors.required", new String[]{"不能为空"},"");
		}
		//发表话题每分钟提交超过
		if(systemSetting.getTopic_submitQuantity() != null){
			if(systemSetting.getTopic_submitQuantity() <0){
				errors.rejectValue("topic_submitQuantity","errors.required", new String[]{"不能小于0"},"");
			}
		}else{
			errors.rejectValue("topic_submitQuantity","errors.required", new String[]{"不能为空"},"");
		}
		//发表评论每分钟提交超过
		if(systemSetting.getComment_submitQuantity() != null){
			if(systemSetting.getComment_submitQuantity() <0){
				errors.rejectValue("comment_submitQuantity","errors.required", new String[]{"不能小于0"},"");
			}
		}else{
			errors.rejectValue("comment_submitQuantity","errors.required", new String[]{"不能为空"},"");
		}
		//发表私信每分钟提交超过
		if(systemSetting.getPrivateMessage_submitQuantity() != null){
			if(systemSetting.getPrivateMessage_submitQuantity() <0){
				errors.rejectValue("privateMessage_submitQuantity","errors.required", new String[]{"不能小于0"},"");
			}
		}else{
			errors.rejectValue("privateMessage_submitQuantity","errors.required", new String[]{"不能为空"},"");
		}
		
		//发表话题奖励积分
		if(systemSetting.getTopic_rewardPoint() != null){
			if(systemSetting.getTopic_rewardPoint() <0){
				errors.rejectValue("topic_rewardPoint","errors.required", new String[]{"必须大于或等于0"},"");
			}
		}else{
			errors.rejectValue("topic_rewardPoint","errors.required", new String[]{"不能为空"},"");
		}
		//发表评论奖励积分
		if(systemSetting.getComment_rewardPoint() != null){
			if(systemSetting.getComment_rewardPoint() <0){
				errors.rejectValue("comment_rewardPoint","errors.required", new String[]{"必须大于或等于0"},"");
			}
		}else{
			errors.rejectValue("comment_rewardPoint","errors.required", new String[]{"不能为空"},"");
		}
		//发表回复奖励积分
		if(systemSetting.getReply_rewardPoint() != null){
			if(systemSetting.getReply_rewardPoint() <0){
				errors.rejectValue("reply_rewardPoint","errors.required", new String[]{"必须大于或等于0"},"");
			}
		}else{
			errors.rejectValue("reply_rewardPoint","errors.required", new String[]{"不能为空"},"");
		}
		
		//前台分页数量
		if(systemSetting.getForestagePageNumber() != null){
			if(systemSetting.getForestagePageNumber() <=0){
				errors.rejectValue("forestagePageNumber","errors.required", new String[]{"不能小于或等于0"},"");
			}
		}else{
			errors.rejectValue("forestagePageNumber","errors.required", new String[]{"不能为空"},"");
		}
		
		//后台分页数量
		if(systemSetting.getBackstagePageNumber() != null){
			if(systemSetting.getBackstagePageNumber() <=0){
				errors.rejectValue("backstagePageNumber","errors.required", new String[]{"不能小于或等于0"},"");
			}
		}
	
		
		//上传临时文件有效期
		if(systemSetting.getTemporaryFileValidPeriod() != null){
			if(systemSetting.getTemporaryFileValidPeriod() <=0){
				errors.rejectValue("temporaryFileValidPeriod","errors.required", new String[]{"必须大于0"},"");
			}
		}
		if(systemSetting.getUserSentSmsCount() != null){
			if(systemSetting.getUserSentSmsCount() <=0){
				errors.rejectValue("userSentSmsCount","errors.required", new String[]{"必须大于0"},"");
			}
		}
		
		
		
	}
}
