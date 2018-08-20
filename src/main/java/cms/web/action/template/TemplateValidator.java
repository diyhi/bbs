package cms.web.action.template;

import javax.annotation.Resource;

import cms.bean.template.Templates;
import cms.service.template.TemplateService;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 模板校验器
 *
 */
@Component("templateValidator")
public class TemplateValidator implements Validator{
	//注入业务bean
	@Resource(name="templateServiceBean")
	private TemplateService templateService;//通过接口引用代理返回的对象
	
	
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(Templates.class); 
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
		Templates templates = (Templates) obj;
		
		if(templates.getName() == null || "".equals(templates.getName().trim())){
			errors.rejectValue("name","errors.required", new String[]{"模板名称不能为空"},"");
		}else{
			if(templates.getName().length() >80){
				errors.rejectValue("name","errors.required", new String[]{"模板名称过长"},"");
				
			}
		}
		if(templates.getDirName() == null || "".equals(templates.getDirName().trim())){
			errors.rejectValue("dirName","errors.required", new String[]{"模板目录不能为空"},"");
		}else{
			if(templates.getDirName().length() >40){
				errors.rejectValue("dirName","errors.required", new String[]{"目录名称过长"},"");
			}else{
				//只能输入字母或下划线
				if(!templates.getDirName().matches("^[a-zA-Z][a-zA-Z0-9|_]{2,40}$")){
					errors.rejectValue("dirName","errors.required", new String[]{"只能由字母或数字或下划线组成,并且开头为字母，总长度大于两个字符"},"");
				}else{
					//验证目录是否重复
					Templates t = templateService.findTemplatebyDirName(templates.getDirName().trim());
					if(t != null){
						errors.rejectValue("dirName","errors.required", new String[]{"模板目录不能重复"},"");
					}
				}
			}
			
			
		}

	}
}
