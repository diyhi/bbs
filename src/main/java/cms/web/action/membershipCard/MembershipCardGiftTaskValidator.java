package cms.web.action.membershipCard;


import javax.annotation.Resource;

import cms.bean.membershipCard.MembershipCardGiftTask;
import cms.bean.user.User;
import cms.service.user.UserService;
import cms.utils.Verification;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 用户校验器
 *
 */
@Component("membershipCardGiftTaskValidator")
public class MembershipCardGiftTaskValidator implements Validator{
	
	
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(MembershipCardGiftTask.class); 
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
		MembershipCardGiftTask membershipCardGiftTask = (MembershipCardGiftTask) obj;
		
		//if(membershipCardGiftTask.getName() == null || "".equals(membershipCardGiftTask.getName().trim())){
		//	errors.rejectValue("name","errors.required", new String[]{"名称不能为空"},"");
			
		//}
		
	}
}
