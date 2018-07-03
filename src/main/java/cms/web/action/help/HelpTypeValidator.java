package cms.web.action.help;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import cms.bean.help.HelpType;

/**
 * 帮助分类校验器
 *
 */
@Component("helpTypeValidator")
public class HelpTypeValidator implements Validator{
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(HelpType.class); 
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
		HelpType helpType = (HelpType) obj;
		
		if(helpType.getName() == null || "".equals(helpType.getName().trim())){
			errors.rejectValue("name","errors.required", new String[]{"分类名称不能为空"},"");
		}else{
			if(helpType.getName().length() >200){
				errors.rejectValue("name","errors.required", new String[]{"不能大于200个字符"},"");
			}
		}
		if(helpType.getSort() == null || helpType.getSort() <0){
			errors.rejectValue("sort","errors.required", new String[]{"排序必须为大于或等于0的数字"},"");
		}

	}
}
