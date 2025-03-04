package cms.web.action.user;


import javax.annotation.Resource;

import cms.bean.user.User;
import cms.service.user.UserService;
import cms.utils.Verification;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 用户校验器
 *
 */
@Component("userValidator")
public class UserValidator implements Validator{
	@Resource UserService userService;
	@Resource UserManage userManage;
	
	
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(User.class); 
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
		User user = (User) obj;
		
		if(user.getType() != null){
			if(user.getType().equals(10)){//10:本地账号密码用户
				if(user.getAccount() != null && !"".equals(user.getAccount().trim())){
					if(Verification.isNumericLettersUnderscore(user.getAccount().trim()) == false){
						errors.rejectValue("account","errors.required", new String[]{"账号只能输入由数字、26个英文字母或者下划线组成"},"");
					}
					if(user.getAccount().length()>30){
						errors.rejectValue("account","errors.required", new String[]{"账号不能超过30个字符"},"");
					}
					User u1 = userService.findUserByAccount(user.getAccount().trim());
					if(u1 != null){
						errors.rejectValue("account","errors.required", new String[]{"该账号已注册"},"");
					}
					
					User u2 = userService.findUserByNickname(user.getAccount().trim());
					if(u2 != null){
						errors.rejectValue("account","errors.required", new String[]{"该账号不能和其他用户呢称相同"},"");
					}
				}else{
					errors.rejectValue("account","errors.required", new String[]{"请填写账号"},"");
				}
				if(user.getIssue() == null || "".equals(user.getIssue().trim())){
					errors.rejectValue("issue","errors.required", new String[]{"密码提示问题不能为空"},"");
				}else{
					if(user.getIssue().length()>50){
						errors.rejectValue("issue","errors.required", new String[]{"密码提示问题不能超过50个字符"},"");
					}
					
				}
				if(user.getAnswer() == null || "".equals(user.getAnswer().trim())){
					errors.rejectValue("answer","errors.required", new String[]{"密码提示答案不能为空"},"");
				}else{
					if(user.getAnswer().length()>50){
						errors.rejectValue("answer","errors.required", new String[]{"密码提示答案不能超过50个字符"},"");
					}
				}
				//手机
				if(user.getMobile() != null && !"".equals(user.getMobile().trim())){
			    	if(user.getMobile().trim().length() >18){
						errors.rejectValue("mobile","errors.required", new String[]{"手机号码超长"},"");
					}else{
						boolean mobile_verification = Verification.isPositiveInteger(StringUtils.removeStart(user.getMobile().trim(), "+"));//正整数
						if(!mobile_verification){
							errors.rejectValue("mobile","errors.required", new String[]{"手机号码不正确"},"");
						}
					}
			    }
			}else if(user.getType().equals(20)){//20: 手机用户
				//手机
				if(user.getMobile() != null && !"".equals(user.getMobile().trim())){
			    	if(user.getMobile().trim().length() >18){
						errors.rejectValue("mobile","errors.required", new String[]{"手机号码超长"},"");
					}else{
						boolean mobile_verification = Verification.isPositiveInteger(StringUtils.removeStart(user.getMobile().trim(), "+"));//正整数
						if(!mobile_verification){
							errors.rejectValue("mobile","errors.required", new String[]{"手机号码不正确"},"");
						}else{
							
							String platformUserId = userManage.thirdPartyUserIdToPlatformUserId(user.getMobile().trim(),20);
							User mobile_user = userService.findUserByPlatformUserId(platformUserId);
							
				      		if(mobile_user != null){
				      			errors.rejectValue("mobile","errors.required", new String[]{"手机号码已注册"},"");

				      		}
						}
					}
			    }else{
			    	errors.rejectValue("mobile","errors.required", new String[]{"手机号不能为空"},"");
			    }
			}else{
				errors.rejectValue("type","errors.required", new String[]{"用户类型错误"},"");
			}
		}else{
			errors.rejectValue("type","errors.required", new String[]{"用户类型不能为空"},"");
		}
		
		
		
		if(user.getNickname() != null && !"".equals(user.getNickname().trim())){
			if(user.getNickname().length()>15){
				errors.rejectValue("nickname","errors.required", new String[]{"呢称不能超过15个字符"},"");
			}
			User u = userService.findUserByNickname(user.getNickname().trim());
			if(u != null){
				errors.rejectValue("nickname","errors.required", new String[]{"该呢称已存在"},"");
			}
			
		}
		
		
		if(user.getPassword() == null || "".equals(user.getPassword().trim())){
			errors.rejectValue("password","errors.required", new String[]{"密码不能为空"},"");
		}else{
			if(user.getPassword().length()>30){
				errors.rejectValue("password","errors.required", new String[]{"密码不能超过30个字符"},"");
			}
		}
		if(user.getEmail() != null && !"".equals(user.getEmail().trim())){
			if(Verification.isEmail(user.getEmail().trim()) == false){
				
				errors.rejectValue("email","errors.required", new String[]{"Email地址不正确"},"");
			}
			if(user.getEmail().trim().length()>60){
				errors.rejectValue("password","errors.required", new String[]{"Email地址不能超过60个字符"},"");
			}
		}
		
		
		
		
		
		
		if(user.getState() == null){
			errors.rejectValue("state","errors.required", new String[]{"用户状态不能为空"},"");
		}else{
			if(user.getState() >2 || user.getState() <1){
				errors.rejectValue("state","errors.required", new String[]{"用户状态错误"},"");
			}
		}
		if(user.getPoint() != null){//积分
			if(user.getPoint() <0){
				errors.rejectValue("point","errors.required", new String[]{"积分不能小于0"},"");
			}
		}
	}
}
