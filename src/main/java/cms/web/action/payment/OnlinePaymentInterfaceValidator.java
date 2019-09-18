package cms.web.action.payment;

import cms.bean.payment.OnlinePaymentInterface;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 在线支付接口校验器
 * @author Gao
 *
 */
@Component("onlinePaymentInterfaceValidator")
public class OnlinePaymentInterfaceValidator implements Validator{
	
	
	
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(OnlinePaymentInterface.class); 
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
		OnlinePaymentInterface onlinePaymentInterface = (OnlinePaymentInterface) obj;
		
		if(onlinePaymentInterface.getName() == null || "".equals(onlinePaymentInterface.getName().trim())){
			errors.rejectValue("name","errors.required", new String[]{"支付名称不能为空"},"");
		}
		
		if(onlinePaymentInterface.getSort() == null || onlinePaymentInterface.getSort() <0){
			errors.rejectValue("sort","errors.required", new String[]{"排序必须为大于或等于0的数字"},"");
		}
		

	}
}
