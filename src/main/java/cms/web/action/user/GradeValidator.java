package cms.web.action.user;


import javax.annotation.Resource;

import cms.bean.user.UserGrade;
import cms.service.user.UserGradeService;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 等级校验器
 *
 */
@Component("gradeValidator")
public class GradeValidator implements Validator{
	@Resource UserGradeService userGradeService;
	
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(UserGrade.class); 
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
		UserGrade userGrade = (UserGrade) obj;
		if(userGrade.getName() != null && !"".equals(userGrade.getName().trim())){
			if(userGrade.getName().length() >50){
				errors.rejectValue("name","errors.required", new String[]{"等级名称不能大于50个字符"},"");
			}
		}else{
			errors.rejectValue("name","errors.required", new String[]{"等级名称不能为空"},"");
		}
		if(userGrade.getNeedPoint() != null){
			if(userGrade.getNeedPoint() < 0L){
				errors.rejectValue("needPoint","errors.required", new String[]{"需要积分不能小于0"},"");
			}
			if(userGrade.getNeedPoint() >999999999999999L){
				errors.rejectValue("needPoint","errors.required", new String[]{"需要积分不能超过999999999999999"},"");
			}
			UserGrade _userGrade = userGradeService.findGradeByNeedPoint(userGrade.getNeedPoint());
			if(_userGrade != null){
				errors.rejectValue("needPoint","errors.required", new String[]{"需要积分已存在"},"");
			}
		}else{
			errors.rejectValue("needPoint","errors.required", new String[]{"需要积分不能为空"},"");
		}
		
		
	}
}
