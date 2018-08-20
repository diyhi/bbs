package cms.web.action.message;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import cms.bean.message.SystemNotify;

/**
 * 系统通知校验器
 *
 */
@Component("systemNotifyValidator")
public class SystemNotifyValidator implements Validator{
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(SystemNotify.class); 
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
		SystemNotify systemNotify = (SystemNotify) obj;
		
		if(systemNotify.getContent() == null || "".equals(systemNotify.getContent().trim())){
			errors.rejectValue("content","errors.required", new String[]{"内容不能为空"},"");
		}else{
			if(systemNotify.getContent().length() >3000){
				errors.rejectValue("content","errors.required", new String[]{"不能超过3000个字符"},"");
			}
		}
	}
}
