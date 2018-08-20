package cms.web.action.sms;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import cms.bean.sms.SmsInterface;

/**
 * 短信接口校验器
 *
 */
@Component("smsInterfaceValidator")
public class SmsInterfaceValidator implements Validator{
	
	
	
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(SmsInterface.class); 
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
		SmsInterface smsInterface = (SmsInterface) obj;
		
		if(smsInterface.getName() == null || "".equals(smsInterface.getName().trim())){
			errors.rejectValue("name","errors.required", new String[]{"名称不能为空"},"");
		}
		
		if(smsInterface.getSort() == null || smsInterface.getSort() <0){
			errors.rejectValue("sort","errors.required", new String[]{"排序必须为大于或等于0的数字"},"");
		}
		

	}
}
