package cms.web.action.template;

import cms.bean.template.Forum;
import cms.utils.Verification;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("forumValidator")
public class ForumValidator implements Validator{
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(Forum.class); 
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
		Forum forum = (Forum)obj;
		if(forum.getName() == null || "".equals(forum.getName().trim())){
			errors.rejectValue("name","errors.required", new String[]{"版块标题不能为空"},"");
		}
		if(forum.getModule() == null || "".equals(forum.getModule().trim())){
			errors.rejectValue("module","errors.required", new String[]{"请选择版块模板"},"");
		}else{
			//只能输入字母和数字下划线
			if(!Verification.isNumericLettersUnderscore(forum.getModule())){
				errors.rejectValue("module","errors.required", new String[]{"请选择版块模板"},"");
			}
		}
	}
}
