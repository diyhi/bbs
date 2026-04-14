package cms.validator.thirdParty;


import cms.dto.thirdParty.ThirdPartyLoginInterfaceRequest;
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
		return clazz.equals(ThirdPartyLoginInterfaceRequest.class);
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
        ThirdPartyLoginInterfaceRequest thirdPartyLoginInterface = (ThirdPartyLoginInterfaceRequest) obj;
		
		if(thirdPartyLoginInterface.getName() == null || thirdPartyLoginInterface.getName().trim().isEmpty()){
			errors.rejectValue("name","errors.common", new String[]{"名称不能为空"},"");
		}
		
		if(thirdPartyLoginInterface.getSort() == null || thirdPartyLoginInterface.getSort() <0){
			errors.rejectValue("sort","errors.common", new String[]{"排序必须为大于或等于0的数字"},"");
		}
		

	}
}
