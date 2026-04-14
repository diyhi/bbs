package cms.validator.link;

import cms.dto.link.LinkRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 友情链接校验器
 *
 */
@Component("linkValidator")
public class LinkValidator implements Validator{
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(LinkRequest.class);
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
        LinkRequest links= (LinkRequest) obj;
		
		if(links.getName() == null || links.getName().trim().isEmpty()){
			errors.rejectValue("name","errors.common", new String[]{"名称不能为空"},"");
		}else{
			if(links.getName().length() >190){
				errors.rejectValue("name","errors.common", new String[]{"不能大于190个字符"},"");
			}
		}
		if(links.getWebsite() == null || links.getWebsite().trim().isEmpty()){
			errors.rejectValue("website","errors.common", new String[]{"网址不能为空"},"");
			
		}else{
			if(links.getWebsite().length() >190){
				errors.rejectValue("website","errors.common", new String[]{"不能大于190个字符"},"");
			}
		}
		if(links.getSort() == null || links.getSort() <0){
			errors.rejectValue("sort","errors.common", new String[]{"排序必须为大于或等于0的数字"},"");
		}

	}
}
