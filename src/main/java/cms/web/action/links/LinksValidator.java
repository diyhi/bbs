package cms.web.action.links;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import cms.bean.links.Links;
/**
 * 友情链接校验器
 *
 */
@Component("linksValidator")
public class LinksValidator implements Validator{
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(Links.class); 
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
		Links links= (Links) obj;
		
		if(links.getName() == null || "".equals(links.getName().trim())){
			errors.rejectValue("name","errors.required", new String[]{"名称不能为空"},"");
		}else{
			if(links.getName().length() >190){
				errors.rejectValue("name","errors.required", new String[]{"不能大于190个字符"},"");
			}
		}
		if(links.getWebsite() == null || "".equals(links.getWebsite().trim())){
			errors.rejectValue("website","errors.required", new String[]{"网址不能为空"},"");
			
		}else{
			if(links.getWebsite().length() >190){
				errors.rejectValue("website","errors.required", new String[]{"不能大于190个字符"},"");
			}
		}
		
		
		if(links.getSort() == null || links.getSort() <0){
			errors.rejectValue("sort","errors.required", new String[]{"排序必须为大于或等于0的数字"},"");
		}

	}
}
