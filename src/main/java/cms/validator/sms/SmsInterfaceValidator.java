package cms.validator.sms;

import cms.dto.sms.SmsInterfaceRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * 短信接口校验器
 *
 */
@Component("smsInterfaceValidator")
public class SmsInterfaceValidator implements Validator{
	
	
	
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(SmsInterfaceRequest.class);
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
        SmsInterfaceRequest smsInterfaceRequest = (SmsInterfaceRequest) obj;
		
		if(smsInterfaceRequest.getName() == null || smsInterfaceRequest.getName().trim().isEmpty()){
			errors.rejectValue("name","errors.common", new String[]{"名称不能为空"},"");
		}
		
		if(smsInterfaceRequest.getSort() == null || smsInterfaceRequest.getSort() <0){
			errors.rejectValue("sort","errors.common", new String[]{"排序必须为大于或等于0的数字"},"");
		}


	}
}
