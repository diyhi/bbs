package cms.web.action.thirdParty;

import cms.bean.thirdParty.ThirdPartyLoginInterface;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 第三方登录接口校验器
 * @author Gao
 *
 */
@Component("thirdPartyLoginInterfaceValidator")
public class ThirdPartyLoginInterfaceValidator implements Validator{
	
	
	
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(ThirdPartyLoginInterface.class); 
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
		ThirdPartyLoginInterface thirdPartyLoginInterface = (ThirdPartyLoginInterface) obj;
		
		if(thirdPartyLoginInterface.getName() == null || "".equals(thirdPartyLoginInterface.getName().trim())){
			errors.rejectValue("name","errors.required", new String[]{"名称不能为空"},"");
		}
		
		if(thirdPartyLoginInterface.getSort() == null || thirdPartyLoginInterface.getSort() <0){
			errors.rejectValue("sort","errors.required", new String[]{"排序必须为大于或等于0的数字"},"");
		}
		

	}
}
