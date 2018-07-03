package cms.web.action.topic;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import cms.bean.topic.Tag;

/**
 * 标签校验器
 *
 */
@Component("tagValidator")
public class TagValidator implements Validator{
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(Tag.class); 
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
		Tag tag = (Tag) obj;
		
		if(tag.getName() == null || "".equals(tag.getName().trim())){
			errors.rejectValue("name","errors.required", new String[]{"标签名称不能为空"},"");
		}else{
			if(tag.getName().length() >190){
				errors.rejectValue("name","errors.required", new String[]{"不能大于190个字符"},"");
			}
		}
		if(tag.getSort() == null || tag.getSort() <0){
			errors.rejectValue("sort","errors.required", new String[]{"排序必须为大于或等于0的数字"},"");
		}

	}
}
