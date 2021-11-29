package cms.web.action.user;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cms.bean.payment.PaymentLog;
import cms.bean.payment.PaymentVerificationLog;
import cms.bean.question.Answer;
import cms.bean.question.AnswerReply;
import cms.bean.question.Question;
import cms.bean.question.QuestionIndex;
import cms.bean.question.QuestionTag;
import cms.bean.question.QuestionTagAssociation;
import cms.bean.user.PointLog;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.topic.Comment;
import cms.bean.topic.Reply;
import cms.bean.topic.Tag;
import cms.bean.topic.Topic;
import cms.bean.topic.TopicIndex;
import cms.bean.user.User;
import cms.bean.user.UserCustom;
import cms.bean.user.UserGrade;
import cms.bean.user.UserInputValue;
import cms.bean.user.UserRole;
import cms.bean.user.UserRoleGroup;
import cms.service.payment.PaymentService;
import cms.service.question.AnswerService;
import cms.service.question.QuestionIndexService;
import cms.service.question.QuestionService;
import cms.service.question.QuestionTagService;
import cms.service.setting.SettingService;
import cms.service.topic.CommentService;
import cms.service.topic.TagService;
import cms.service.topic.TopicIndexService;
import cms.service.topic.TopicService;
import cms.service.user.UserCustomService;
import cms.service.user.UserGradeService;
import cms.service.user.UserRoleService;
import cms.service.user.UserService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.SHA;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.lucene.TopicLuceneManage;
import cms.web.action.membershipCard.MembershipCardGiftTaskManage;
import cms.web.action.payment.PaymentManage;
import cms.web.action.question.QuestionManage;
import cms.web.action.topic.TopicManage;

/**
 * 用户管理
 *
 */
@Controller
@RequestMapping("/control/user/manage") 
public class UserManageAction {
	private static final Logger logger = LogManager.getLogger(UserManageAction.class);
	
	
	@Resource(name="userServiceBean")
	private UserService userService;
	//注入业务bean
	@Resource(name="userCustomServiceBean")
	private UserCustomService userCustomService;
	@Resource PointManage pointManage;
	@Resource SettingService settingService;
	
	@Resource(name = "userValidator") 
	private Validator validator; 
	@Resource UserManage userManage;
	
	@Resource UserGradeService userGradeService;
	
	@Resource TopicService topicService;
	@Resource CommentService commentService;
	@Resource TextFilterManage textFilterManage;
	@Resource TagService tagService;
	@Resource TopicManage topicManage;
	
	
	@Resource TopicLuceneManage topicLuceneManage;
	@Resource TopicIndexService topicIndexService;
	
	@Resource UserRoleService userRoleService;
	@Resource UserRoleManage userRoleManage;
	@Resource PaymentService paymentService;
	@Resource PaymentManage paymentManage;
	@Resource QuestionManage questionManage;
	
	@Resource QuestionService questionService;
	@Resource QuestionTagService questionTagService;
	@Resource AnswerService answerService;
	@Resource QuestionIndexService questionIndexService;
	@Resource FileManage fileManage;
	@Resource MessageSource messageSource;
	@Resource MembershipCardGiftTaskManage membershipCardGiftTaskManage;
	/**
	 * 用户管理 查看
	 */
	@ResponseBody
	@RequestMapping(params="method=show",method=RequestMethod.GET)
	public String show(ModelMap model2,Long id,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		if(id != null){
			User user = userService.findUserById(id);
			if(user != null){
				user.setPassword(null);//密码不显示
				user.setAnswer(null);//密码提示答案不显示
				user.setSalt(null);//盐值不显示
				user.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
				
				
				if(user.getType() >10){
					user.setPlatformUserId(userManage.platformUserIdToThirdPartyUserId(user.getPlatformUserId()));
				}
				
				
				List<UserGrade> userGradeList = userGradeService.findAllGrade();
				if(userGradeList != null && userGradeList.size() >0){
					for(UserGrade userGrade : userGradeList){//取得所有等级 
						if(user.getPoint() >= userGrade.getNeedPoint()){
							user.setGradeName(userGrade.getName());//将等级值设进等级参数里
							break;
						}
					} 
				}
				
				//有效的用户角色
				List<UserRole> validUserRoleList = new ArrayList<UserRole>();
				
				//查询所有角色
				List<UserRole> userRoleList = userRoleService.findAllRole();
				if(userRoleList != null && userRoleList.size() >0){
					List<UserRoleGroup> userRoleGroupList = userRoleService.findRoleGroupByUserName(user.getUserName());
					
					
					for(UserRole userRole : userRoleList){
						if(userRole.getDefaultRole()){//如果是默认角色
							continue;
						}else{
							//默认时间  年,月,日,时,分,秒,毫秒    
			                DateTime defaultTime = new DateTime(2999, 1, 1, 0, 0);// 2999年1月1日0点0分
			                Date validPeriodEnd = defaultTime.toDate();
							userRole.setValidPeriodEnd(validPeriodEnd);
						}
						
						if(userRoleGroupList != null && userRoleGroupList.size() >0){
							for(UserRoleGroup userRoleGroup : userRoleGroupList){
								if(userRole.getId().equals(userRoleGroup.getUserRoleId())){
									UserRole validUserRole = new UserRole();
									validUserRole.setId(userRole.getId());
									validUserRole.setName(userRole.getName());
									validUserRole.setValidPeriodEnd(userRoleGroup.getValidPeriodEnd());
									validUserRoleList.add(validUserRole);
								}
							}
						}
					}
				}
				
				
				List<UserCustom> userCustomList = userCustomService.findAllUserCustom();
				if(userCustomList != null && userCustomList.size() >0){		
					Iterator <UserCustom> it = userCustomList.iterator();  
					while(it.hasNext()){  
						UserCustom userCustom = it.next();
						if(userCustom.isVisible() == false){//如果不显示
							it.remove();  
							continue;
						}
						if(userCustom.getValue() != null && !"".equals(userCustom.getValue().trim())){
							LinkedHashMap<String,String> itemValue = JsonUtils.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});
							userCustom.setItemValue(itemValue);
						}
						
					}
				}
				
				List<UserInputValue> userInputValueList= userCustomService.findUserInputValueByUserName(user.getId());
				if(userInputValueList != null && userInputValueList.size() >0){
					for(UserCustom userCustom : userCustomList){
						for(UserInputValue userInputValue : userInputValueList){
							if(userCustom.getId().equals(userInputValue.getUserCustomId())){
								userCustom.addUserInputValue(userInputValue);
							}
						}
					}
				}
				
				returnValue.put("userRoleList", validUserRoleList);
				returnValue.put("userCustomList", userCustomList);
				returnValue.put("user",user);
			}else{
				error.put("user", "用户不存在");
			}
		}else{
			error.put("id", "Id不能为空");
		}
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
	
	
	/**
	 * 用户管理 添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model,User user,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		List<UserCustom> userCustomList = userCustomService.findAllUserCustom();
		if(userCustomList != null && userCustomList.size() >0){		
			Iterator <UserCustom> it = userCustomList.iterator();  
			while(it.hasNext()){  
				UserCustom userCustom = it.next();
				if(userCustom.isVisible() == false){//如果不显示
					it.remove();  
					continue;
				}
				if(userCustom.getValue() != null && !"".equals(userCustom.getValue().trim())){
					LinkedHashMap<String,String> itemValue = JsonUtils.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});
					userCustom.setItemValue(itemValue);
				}
				
			}
		}
		
		
		//查询所有角色
		List<UserRole> userRoleList = userRoleService.findAllRole();
		if(userRoleList != null && userRoleList.size() >0){
			for(UserRole userRole : userRoleList){
				if(userRole.getDefaultRole()){//如果是默认角色
					userRole.setSelected(true);
				}else{
					//默认时间  年,月,日,时,分,秒,毫秒    
	                DateTime defaultTime = new DateTime(2999, 1, 1, 0, 0);// 2999年1月1日0点0分
	                Date validPeriodEnd = defaultTime.toDate();
					userRole.setValidPeriodEnd(validPeriodEnd);
				}
			}
		}
		
		returnValue.put("userCustomList", userCustomList);
		returnValue.put("userRoleList", userRoleList);
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
	}
	
	
	/**
	 * 用户管理 添加用户(服务端生成参数)
	 * @param formbean
	 * @param userRolesId 角色Id
	 * @param result
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.POST)
	public String add(User formbean,String[] userRolesId,BindingResult result,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		//用户自定义注册功能项参数
		List<UserCustom> userCustomList = userCustomService.findAllUserCustom();
		if(userCustomList != null && userCustomList.size() >0){	
			for(UserCustom userCustom : userCustomList){
				//用户自定义注册功能项用户输入值集合
				List<UserInputValue> userInputValueList = new ArrayList<UserInputValue>();
				
				if(userCustom.isVisible() == true){//显示
					if(userCustom.getValue() != null && !"".equals(userCustom.getValue().trim())){
						LinkedHashMap<String,String> itemValue = JsonUtils.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});
						userCustom.setItemValue(itemValue);
					}
					if(userCustom.getChooseType().equals(1)){//1.输入框
						String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());
						
						if(userCustom_value != null && !"".equals(userCustom_value.trim())){
							UserInputValue userInputValue = new UserInputValue();
							userInputValue.setUserCustomId(userCustom.getId());
							userInputValue.setContent(userCustom_value.trim());
							userInputValueList.add(userInputValue);
							
							if(userCustom.getMaxlength() != null && userCustom_value.length() > userCustom.getMaxlength()){
								error.put("userCustom_"+userCustom.getId(), "长度超过"+userCustom_value.length()+"个字符");
							}
							
							int fieldFilter = userCustom.getFieldFilter();//字段过滤方式    0.无  1.只允许输入数字  2.只允许输入字母  3.只允许输入数字和字母  4.只允许输入汉字  5.正则表达式过滤
							switch(fieldFilter){
								case 1 : //输入数字
									if(Verification.isPositiveIntegerZero(userCustom_value.trim()) == false){
										error.put("userCustom_"+userCustom.getId(), "只允许输入数字");
									}
								  break; 
								case 2 : //输入字母
									if(Verification.isLetter(userCustom_value.trim()) == false){
										error.put("userCustom_"+userCustom.getId(), "只允许输入字母");
									}
								  break;
								case 3 : //只能输入数字和字母
									if(Verification.isNumericLetters(userCustom_value.trim()) == false){
										error.put("userCustom_"+userCustom.getId(), "只允许输入数字和字母");
									}
								  break;
								case 4 : //只能输入汉字
									if(Verification.isChineseCharacter(userCustom_value.trim()) == false){
										error.put("userCustom_"+userCustom.getId(), "只允许输入汉字");
									}
								  break;
								case 5 : //正则表达式过滤
									if(userCustom_value.trim().matches(userCustom.getRegular())== false){
										error.put("userCustom_"+userCustom.getId(), "输入错误");
									}
								  break;
							//	default:
							}
						}else{
							if(userCustom.isRequired() == true){//是否必填	
								error.put("userCustom_"+userCustom.getId(), "必填项");
							}
							
						}	
						userCustom.setUserInputValueList(userInputValueList);
					}else if(userCustom.getChooseType().equals(2)){//2.单选按钮
						String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());
						
						if(userCustom_value != null && !"".equals(userCustom_value.trim())){
							
							String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
							if(itemValue != null ){
								UserInputValue userInputValue = new UserInputValue();
								userInputValue.setUserCustomId(userCustom.getId());
								userInputValue.setOptions(userCustom_value.trim());
								userInputValueList.add(userInputValue);
								
							}else{
								if(userCustom.isRequired() == true){//是否必填	
									error.put("userCustom_"+userCustom.getId(), "必填项");
								}
							}
							
						}else{
							if(userCustom.isRequired() == true){//是否必填	
								error.put("userCustom_"+userCustom.getId(), "必填项");
							}
						}
						userCustom.setUserInputValueList(userInputValueList);	
						
					}else if(userCustom.getChooseType().equals(3)){//3.多选按钮
						String[] userCustom_value_arr = request.getParameterValues("userCustom_"+userCustom.getId());
						
						if(userCustom_value_arr != null && userCustom_value_arr.length >0){
							for(String userCustom_value : userCustom_value_arr){
								
								if(userCustom_value != null && !"".equals(userCustom_value.trim())){
									
									String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
									if(itemValue != null ){
										UserInputValue userInputValue = new UserInputValue();
										userInputValue.setUserCustomId(userCustom.getId());
										userInputValue.setOptions(userCustom_value.trim());
										userInputValueList.add(userInputValue);
									}
									
									
								}
							}
						}else{
							if(userCustom.isRequired() == true){//是否必填	
								error.put("userCustom_"+userCustom.getId(), "必填项");
							}
						}
						if(userInputValueList.size() == 0){
							if(userCustom.isRequired() == true){//是否必填	
								error.put("userCustom_"+userCustom.getId(), "必填项");
							}
						}
						userCustom.setUserInputValueList(userInputValueList);	
						
					}else if(userCustom.getChooseType().equals(4)){//4.下拉列表
						String[] userCustom_value_arr = request.getParameterValues("userCustom_"+userCustom.getId());
						
						if(userCustom_value_arr != null && userCustom_value_arr.length >0){
							for(String userCustom_value : userCustom_value_arr){
								
								if(userCustom_value != null && !"".equals(userCustom_value.trim())){
									
									String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
									if(itemValue != null ){
										UserInputValue userInputValue = new UserInputValue();
										userInputValue.setUserCustomId(userCustom.getId());
										userInputValue.setOptions(userCustom_value.trim());
										userInputValueList.add(userInputValue);
									}
									
									
								}
							}
						}else{
							if(userCustom.isRequired() == true){//是否必填	
								error.put("userCustom_"+userCustom.getId(), "必填项");
							}
						}
						if(userInputValueList.size() == 0){
							if(userCustom.isRequired() == true){//是否必填	
								error.put("userCustom_"+userCustom.getId(), "必填项");
							}
						}
						userCustom.setUserInputValueList(userInputValueList);	
					}else if(userCustom.getChooseType().equals(5)){// 5.文本域
						String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());
						
						if(userCustom_value != null && !"".equals(userCustom_value.trim())){
							UserInputValue userInputValue = new UserInputValue();
							userInputValue.setUserCustomId(userCustom.getId());
							userInputValue.setContent(userCustom_value);
							userInputValueList.add(userInputValue);
							
						}else{
							if(userCustom.isRequired() == true){//是否必填	
								error.put("userCustom_"+userCustom.getId(), "必填项");
							}
						}
						userCustom.setUserInputValueList(userInputValueList);
					}
				}
			}
		}

		List<UserRoleGroup> userRoleGroupList = new ArrayList<UserRoleGroup>();
		List<UserRole> userRoleList = userRoleService.findAllRole();
		if(userRolesId != null && userRolesId.length >0){
			
			if(userRoleList != null && userRoleList.size() >0){
				for(String rolesId : userRolesId){
					if(rolesId != null && !"".equals(rolesId.trim())){
						for(UserRole userRole : userRoleList){
							userRole.setSelected(true);//错误回显需要
							if(!userRole.getDefaultRole() && userRole.getId().equals(rolesId.trim())){//默认角色不保存
								//默认时间  年,月,日,时,分,秒,毫秒    
				                DateTime defaultTime = new DateTime(2999, 1, 1, 0, 0);// 2999年1月1日0点0分
				                Date validPeriodEnd = defaultTime.toDate();
				                
				                String validPeriodEnd_str = request.getParameter("validPeriodEnd_"+userRole.getId());
								
								if(validPeriodEnd_str != null && !"".equals(validPeriodEnd_str.trim())){
									boolean verification = Verification.isTime_minute(validPeriodEnd_str.trim());
									if(verification){
										DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");  
						                //时间解析    
						                DateTime dateTime = DateTime.parse(validPeriodEnd_str.trim(), format);
						                validPeriodEnd = dateTime.toDate();
									}else{
										validPeriodEnd = null;
										error.put("validPeriodEnd_"+userRole.getId(), "请填写正确的日期");
									}
								}
								
								userRole.setValidPeriodEnd(validPeriodEnd);//错误回显需要
								UserRoleGroup userRoleGroup = new UserRoleGroup();
								userRoleGroup.setUserName(formbean.getUserName() != null ?formbean.getUserName().trim() :formbean.getUserName());
								userRoleGroup.setUserRoleId(userRole.getId());
								userRoleGroup.setValidPeriodEnd(validPeriodEnd);
								userRoleGroupList.add(userRoleGroup);
							}
						}
					}
				}
				
				
			}
		}
		
		
		//数据校验
		this.validator.validate(formbean, result); 
		if (result.hasErrors()) { 
			List<FieldError> fieldErrorList = result.getFieldErrors();
			if(fieldErrorList != null && fieldErrorList.size() >0){
				for(FieldError fieldError : fieldErrorList){
					error.put(fieldError.getField(), messageSource.getMessage(fieldError, null));
				}
			}
		}
		if(error.size() == 0){
			User user = new User();
			
			if(formbean.getType().equals(10)){//10:本地账号密码用户
				user.setUserName(formbean.getUserName().trim());
				user.setIssue(formbean.getIssue().trim());
				//密码提示答案由  密码提示答案原文sha256  进行sha256组成
				user.setAnswer(SHA.sha256Hex(SHA.sha256Hex(formbean.getAnswer().trim())));
				user.setPlatformUserId(user.getUserName());
			}else if(formbean.getType().equals(20)){//20: 手机用户
				user.setUserName(userManage.queryUserIdentifier(20)+"-"+UUIDUtil.getUUID22());//会员用户名
				user.setPlatformUserId(userManage.thirdPartyUserIdToPlatformUserId(formbean.getMobile().trim(),20));
			}
			
			
			
			user.setSalt(UUIDUtil.getUUID32());
			
			if(formbean.getNickname() != null && !"".equals(formbean.getNickname().trim())){
				user.setNickname(formbean.getNickname().trim());
			}
			
			//密码
			user.setPassword(SHA.sha256Hex(SHA.sha256Hex(formbean.getPassword().trim())+"["+user.getSalt()+"]"));
			user.setEmail(formbean.getEmail().trim());
			

			user.setRegistrationDate(new Date());
			user.setRemarks(formbean.getRemarks());
			user.setState(formbean.getState());
			
			user.setMobile(formbean.getMobile().trim());
			user.setRealNameAuthentication(formbean.isRealNameAuthentication());
			//允许显示用户动态
			user.setAllowUserDynamic(formbean.getAllowUserDynamic());
			user.setSecurityDigest(new Date().getTime());
			user.setType(formbean.getType());
			
			//用户自定义注册功能项用户输入值集合
			List<UserInputValue> all_userInputValueList = new ArrayList<UserInputValue>();
		
			if(userCustomList != null && userCustomList.size() >0){	
				for(UserCustom userCustom : userCustomList){
					all_userInputValueList.addAll(userCustom.getUserInputValueList());
				}
			}

			try {
				userService.saveUser(user,all_userInputValueList,userRoleGroupList);
			} catch (Exception e) {
				error.put("user", "添加用户错误");
			//	e.printStackTrace();
			}
			//删除缓存
			if(user.getId() != null){
				userManage.delete_cache_findUserById(user.getId());
			}
			userManage.delete_cache_findUserByUserName(user.getUserName());
			userRoleManage.delete_cache_findRoleGroupByUserName(user.getUserName());
		}
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	/**
	 * 用户管理 修改界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(User formbean,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
				
		if(formbean.getId() != null){
			User user = userService.findUserById(formbean.getId());
			if(user != null){
				user.setPassword(null);//密码不显示
				user.setAnswer(null);//密码提示答案不显示
				user.setSalt(null);//盐值不显示
				
				List<UserCustom> userCustomList = userCustomService.findAllUserCustom();
				if(userCustomList != null && userCustomList.size() >0){		
					Iterator <UserCustom> it = userCustomList.iterator();  
					while(it.hasNext()){  
						UserCustom userCustom = it.next();
						if(userCustom.isVisible() == false){//如果不显示
							it.remove();  
							continue;
						}
						if(userCustom.getValue() != null && !"".equals(userCustom.getValue().trim())){
							LinkedHashMap<String,String> itemValue = JsonUtils.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});
							userCustom.setItemValue(itemValue);
						}
						
					}
				}
				
				List<UserInputValue> userInputValueList= userCustomService.findUserInputValueByUserName(user.getId());
				if(userInputValueList != null && userInputValueList.size() >0){
					for(UserCustom userCustom : userCustomList){
						for(UserInputValue userInputValue : userInputValueList){
							if(userCustom.getId().equals(userInputValue.getUserCustomId())){
								userCustom.addUserInputValue(userInputValue);
							}
						}
					}
				}
				//查询所有角色
				List<UserRole> userRoleList = userRoleService.findAllRole();
				if(userRoleList != null && userRoleList.size() >0){
					List<UserRoleGroup> userRoleGroupList = userRoleService.findRoleGroupByUserName(user.getUserName());
					
					
					for(UserRole userRole : userRoleList){
						if(userRole.getDefaultRole()){//如果是默认角色
							userRole.setSelected(true);
						}else{
							//默认时间  年,月,日,时,分,秒,毫秒    
			                DateTime defaultTime = new DateTime(2999, 1, 1, 0, 0);// 2999年1月1日0点0分
			                Date validPeriodEnd = defaultTime.toDate();
							userRole.setValidPeriodEnd(validPeriodEnd);
						}
						
						if(userRoleGroupList != null && userRoleGroupList.size() >0){
							for(UserRoleGroup userRoleGroup : userRoleGroupList){
								if(userRole.getId().equals(userRoleGroup.getUserRoleId())){
									userRole.setSelected(true);
									userRole.setValidPeriodEnd(userRoleGroup.getValidPeriodEnd());
								}
							}
						}
					}
				}
				returnValue.put("userRoleList", userRoleList);
				
				returnValue.put("userCustomList", userCustomList);
			
				
				
				returnValue.put("user",user);
				
			}else{
				error.put("id", "用户不存在");
			}
			
			
		}else{
			error.put("id", "用户Id不能为空");
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}	
	}
	/**
	 * 用户管理 修改
	 * @param formbean
	 * @param userRolesId 角色Id
	 * @param model
	 * @param pageForm
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	public String edit(User formbean,String[] userRolesId,ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		User user = null;
		if(formbean.getId() != null){
			user = userService.findUserById(formbean.getId());
			if(user != null){
				if(!user.getUserVersion().equals(formbean.getUserVersion())){
					error.put("user", "当前数据不是最新");
				}
			}else{
				error.put("id", "用户不存在");
			}
			
		}else{
			error.put("id", "Id不能为空");
		}
		
		if(error.size() ==0){
			User new_user = new User();
			
			
			
			List<UserCustom> userCustomList = userCustomService.findAllUserCustom();
			//用户自定义注册功能项参数
			if(userCustomList != null && userCustomList.size() >0){	
				for(UserCustom userCustom : userCustomList){
					//用户自定义注册功能项用户输入值集合
					List<UserInputValue> userInputValueList = new ArrayList<UserInputValue>();
					
					if(userCustom.isVisible() == true){//显示
						if(userCustom.getValue() != null && !"".equals(userCustom.getValue().trim())){
							LinkedHashMap<String,String> itemValue = JsonUtils.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});
							userCustom.setItemValue(itemValue);
						}
						if(userCustom.getChooseType().equals(1)){//1.输入框
							String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());
							
							if(userCustom_value != null && !"".equals(userCustom_value.trim())){
								UserInputValue userInputValue = new UserInputValue();
								userInputValue.setUserCustomId(userCustom.getId());
								userInputValue.setContent(userCustom_value.trim());
								userInputValueList.add(userInputValue);

								if(userCustom.getMaxlength() != null && userCustom_value.length() > userCustom.getMaxlength()){
									error.put("userCustom_"+userCustom.getId(), "长度超过"+userCustom_value.length()+"个字符");
								}
								

								int fieldFilter = userCustom.getFieldFilter();//字段过滤方式    0.无  1.只允许输入数字  2.只允许输入字母  3.只允许输入数字和字母  4.只允许输入汉字  5.正则表达式过滤
								switch(fieldFilter){
									case 1 : //输入数字
										if(Verification.isPositiveIntegerZero(userCustom_value.trim()) == false){
											error.put("userCustom_"+userCustom.getId(), "只允许输入数字");
										}
									  break; 
									case 2 : //输入字母
										if(Verification.isLetter(userCustom_value.trim()) == false){
											error.put("userCustom_"+userCustom.getId(), "只允许输入字母");
										}
									  break;
									case 3 : //只能输入数字和字母
										if(Verification.isNumericLetters(userCustom_value.trim()) == false){
											error.put("userCustom_"+userCustom.getId(), "只允许输入数字和字母");
										}
									  break;
									case 4 : //只能输入汉字
										if(Verification.isChineseCharacter(userCustom_value.trim()) == false){
											error.put("userCustom_"+userCustom.getId(), "只允许输入汉字");
										}
									  break;
									case 5 : //正则表达式过滤
										if(userCustom_value.trim().matches(userCustom.getRegular())== false){
											error.put("userCustom_"+userCustom.getId(), "输入错误");
										}
									  break;
								//	default:
								}
							}else{
								if(userCustom.isRequired() == true && user.getType() <=30){//是否必填	
									error.put("userCustom_"+userCustom.getId(), "必填项");
								}
								
							}	
							userCustom.setUserInputValueList(userInputValueList);
						}else if(userCustom.getChooseType().equals(2)){//2.单选按钮
							String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());
							
							if(userCustom_value != null && !"".equals(userCustom_value.trim())){
								
								String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
								if(itemValue != null ){
									UserInputValue userInputValue = new UserInputValue();
									userInputValue.setUserCustomId(userCustom.getId());
									userInputValue.setOptions(userCustom_value.trim());
									userInputValueList.add(userInputValue);
									
								}else{
									if(userCustom.isRequired() == true && user.getType() <=30){//是否必填	
										error.put("userCustom_"+userCustom.getId(), "必填项");
									}
								}
								
							}else{
								if(userCustom.isRequired() == true && user.getType() <=30){//是否必填	
									error.put("userCustom_"+userCustom.getId(), "必填项");
								}
							}
							userCustom.setUserInputValueList(userInputValueList);	
							
						}else if(userCustom.getChooseType().equals(3)){//3.多选按钮
							String[] userCustom_value_arr = request.getParameterValues("userCustom_"+userCustom.getId());
							
							if(userCustom_value_arr != null && userCustom_value_arr.length >0){
								for(String userCustom_value : userCustom_value_arr){
									
									if(userCustom_value != null && !"".equals(userCustom_value.trim())){
										
										String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
										if(itemValue != null ){
											UserInputValue userInputValue = new UserInputValue();
											userInputValue.setUserCustomId(userCustom.getId());
											userInputValue.setOptions(userCustom_value.trim());
											userInputValueList.add(userInputValue);
										}	
									}
								}
							}else{
								if(userCustom.isRequired() == true && user.getType() <=30){//是否必填	
									error.put("userCustom_"+userCustom.getId(), "必填项");
								}
							}
							if(userInputValueList.size() == 0){
								if(userCustom.isRequired() == true && user.getType() <=30){//是否必填	
									error.put("userCustom_"+userCustom.getId(), "必填项");
								}
							}
							userCustom.setUserInputValueList(userInputValueList);	
							
						}else if(userCustom.getChooseType().equals(4)){//4.下拉列表
							String[] userCustom_value_arr = request.getParameterValues("userCustom_"+userCustom.getId());
							
							if(userCustom_value_arr != null && userCustom_value_arr.length >0){
								for(String userCustom_value : userCustom_value_arr){
									
									if(userCustom_value != null && !"".equals(userCustom_value.trim())){
										
										String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
										if(itemValue != null ){
											UserInputValue userInputValue = new UserInputValue();
											userInputValue.setUserCustomId(userCustom.getId());
											userInputValue.setOptions(userCustom_value.trim());
											userInputValueList.add(userInputValue);
										}
									}
								}
							}else{
								if(userCustom.isRequired() == true && user.getType() <=30){//是否必填	
									error.put("userCustom_"+userCustom.getId(), "必填项");
								}
							}
							if(userInputValueList.size() == 0){
								if(userCustom.isRequired() == true && user.getType() <=30){//是否必填	
									error.put("userCustom_"+userCustom.getId(), "必填项");
								}
							}
							userCustom.setUserInputValueList(userInputValueList);	
						}else if(userCustom.getChooseType().equals(5)){// 5.文本域
							String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());
							
							if(userCustom_value != null && !"".equals(userCustom_value.trim())){
								UserInputValue userInputValue = new UserInputValue();
								userInputValue.setUserCustomId(userCustom.getId());
								userInputValue.setContent(userCustom_value);
								userInputValueList.add(userInputValue);
								
							}else{
								if(userCustom.isRequired() == true && user.getType() <=30){//是否必填	
									error.put("userCustom_"+userCustom.getId(), "必填项");
								}
							}
							userCustom.setUserInputValueList(userInputValueList);
						}
					}
				}
			}
			
			
			//验证数据
			if(formbean.getNickname() != null && !"".equals(formbean.getNickname().trim())){
				if(formbean.getNickname().length()>15){
					error.put("nickname", "呢称不能超过15个字符");
				}
				
				
				User u = userService.findUserByNickname(formbean.getNickname().trim());
				if(u != null){
					if(user.getNickname() == null || "".equals(user.getNickname()) || !formbean.getNickname().trim().equals(user.getNickname())){
						error.put("nickname", "该呢称已存在");
					}
					
				}
				new_user.setNickname(formbean.getNickname().trim());
			}else{
				new_user.setNickname(null);
			}
			if(user.getType() <=30){//本地账户才允许设置的参数
				if(formbean.getPassword() != null && !"".equals(formbean.getPassword().trim())){//密码
					if(formbean.getPassword().length()>30){
						error.put("password", "密码不能超过30个字符");
					}
					//密码
					new_user.setPassword(SHA.sha256Hex(SHA.sha256Hex(formbean.getPassword().trim())+"["+user.getSalt()+"]"));
					new_user.setSecurityDigest(new Date().getTime());
				}else{
					new_user.setPassword(user.getPassword());
					new_user.setSecurityDigest(user.getSecurityDigest());
				}
				
				if(user.getType().equals(10)){
					if(formbean.getIssue() != null && !"".equals(formbean.getIssue().trim())){//密码提示问题
						if(formbean.getIssue().length()>50){
							error.put("issue", "密码提示问题不能超过50个字符");
						}
						new_user.setIssue(formbean.getIssue().trim());
					}else{
						error.put("issue", "密码提示问题不能为空");
					}
					if(formbean.getAnswer() != null && !"".equals(formbean.getAnswer().trim())){//密码提示答案
						if(formbean.getAnswer().length()>50){
							error.put("answer", "密码提示答案不能超过50个字符");
						}
						//密码提示答案由  密码提示答案原文sha256  进行sha256组成
						new_user.setAnswer(SHA.sha256Hex(SHA.sha256Hex(formbean.getAnswer().trim())));
					}else{
						new_user.setAnswer(user.getAnswer());
					}
					
				}
			}else{
				new_user.setPassword(user.getPassword());
				if(user.getSecurityDigest() != null && !"".equals(user.getSecurityDigest())){
					new_user.setSecurityDigest(user.getSecurityDigest());
				}else{
					new_user.setSecurityDigest(new Date().getTime());
				}
				
				new_user.setIssue(user.getIssue());
				new_user.setAnswer(user.getAnswer());
			}
			
			if(formbean.getEmail() != null && !"".equals(formbean.getEmail().trim())){//邮箱
				if(Verification.isEmail(formbean.getEmail().trim()) == false){
					error.put("email", "Email地址不正确");
				}
				if(formbean.getEmail().trim().length()>60){
					error.put("email", "Email地址不能超过60个字符");
				}
				new_user.setEmail(formbean.getEmail().trim());
			}
			
			//平台用户Id
			new_user.setPlatformUserId(user.getPlatformUserId());
			if(user.getType().equals(10)){//10:本地账号密码用户
				//手机
				if(formbean.getMobile() != null && !"".equals(formbean.getMobile().trim())){
			    	if(formbean.getMobile().trim().length() >18){
						error.put("mobile", "手机号码超长");
					}else{
						boolean mobile_verification = Verification.isPositiveInteger(formbean.getMobile().trim());//正整数
						if(!mobile_verification){
							error.put("mobile", "手机号码不正确");
						}else{
							new_user.setMobile(formbean.getMobile().trim());
						}
					}
			    }
				
			}else if(user.getType().equals(20)){//20: 手机用户
				//手机
				if(formbean.getMobile() != null && !"".equals(formbean.getMobile().trim())){
			    	if(formbean.getMobile().trim().length() >18){
						error.put("mobile", "手机号码超长");
					}else{
						boolean mobile_verification = Verification.isPositiveInteger(formbean.getMobile().trim());//正整数
						if(!mobile_verification){
							error.put("mobile", "手机号码不正确");
						}else{
							
							if(!user.getMobile().equals(formbean.getMobile().trim())){
								String platformUserId = userManage.thirdPartyUserIdToPlatformUserId(formbean.getMobile().trim(),20);
								User mobile_user = userService.findUserByPlatformUserId(platformUserId);
								
					      		if(mobile_user != null){
					      			error.put("mobile", "手机号码已注册");

					      		}
							}
							
							new_user.setPlatformUserId(userManage.thirdPartyUserIdToPlatformUserId(formbean.getMobile().trim(),20));
							new_user.setMobile(formbean.getMobile().trim());
						}
					}
			    }else{
			    	error.put("mobile", "手机号码不能为空");
			    }
			}
			
			
			
			//实名认证
			new_user.setRealNameAuthentication(formbean.isRealNameAuthentication());
			//允许显示用户动态
			new_user.setAllowUserDynamic(formbean.getAllowUserDynamic());

			//用户状态
			if(formbean.getState() == null){
				error.put("state", "用户状态不能为空");
			}else{
				if(formbean.getState() >2 || formbean.getState() <1){
					error.put("state", "用户状态错误");
				}
				new_user.setState(formbean.getState());
			}
			
			
			List<UserRoleGroup> userRoleGroupList = new ArrayList<UserRoleGroup>();
			List<UserRole> userRoleList = userRoleService.findAllRole();
			if(userRolesId != null && userRolesId.length >0){
				
				if(userRoleList != null && userRoleList.size() >0){
					for(String rolesId : userRolesId){
						if(rolesId != null && !"".equals(rolesId.trim())){
							for(UserRole userRole : userRoleList){
								if(!userRole.getDefaultRole() && userRole.getId().equals(rolesId.trim())){//默认角色不保存
									//默认时间  年,月,日,时,分,秒,毫秒    
					                DateTime defaultTime = new DateTime(2999, 1, 1, 0, 0);// 2999年1月1日0点0分
					                Date validPeriodEnd = defaultTime.toDate();
					                
					                String validPeriodEnd_str = request.getParameter("validPeriodEnd_"+userRole.getId());
									
									if(validPeriodEnd_str != null && !"".equals(validPeriodEnd_str.trim())){
										boolean verification = Verification.isTime_minute(validPeriodEnd_str.trim());
										if(verification){
											DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");  
							                //时间解析    
							                DateTime dateTime = DateTime.parse(validPeriodEnd_str.trim(), format);
							                validPeriodEnd = dateTime.toDate();
										}else{
											validPeriodEnd = null;
											error.put("validPeriodEnd_"+userRole.getId(), "请填写正确的日期");
										}
									}
									
									UserRoleGroup userRoleGroup = new UserRoleGroup();
									userRoleGroup.setUserName(user.getUserName());
									userRoleGroup.setUserRoleId(userRole.getId());
									userRoleGroup.setValidPeriodEnd(validPeriodEnd);
									userRoleGroupList.add(userRoleGroup);
								}
							}
						}
					}
					
					
				}
			}
			
			
			
			
			new_user.setId(user.getId());
			new_user.setUserName(user.getUserName());
			//备注
			new_user.setRemarks(formbean.getRemarks());
			new_user.setUserVersion(formbean.getUserVersion());
			if(error.size() ==0){
				List<UserInputValue> userInputValueList= userCustomService.findUserInputValueByUserName(user.getId());
				
				//添加注册功能项用户输入值集合
				List<UserInputValue> add_userInputValue = new ArrayList<UserInputValue>();
				//删除注册功能项用户输入值Id集合
				List<Long> delete_userInputValueIdList = new ArrayList<Long>();
				if(userCustomList != null && userCustomList.size() >0){	
					for(UserCustom userCustom : userCustomList){
						List<UserInputValue> new_userInputValueList = userCustom.getUserInputValueList();
						if(new_userInputValueList != null && new_userInputValueList.size() >0){
							A:for(UserInputValue new_userInputValue : new_userInputValueList){
								if(userInputValueList != null && userInputValueList.size() >0){
									for(UserInputValue old_userInputValue : userInputValueList){
										if(new_userInputValue.getUserCustomId().equals(old_userInputValue.getUserCustomId())){
											if(new_userInputValue.getOptions().equals("-1")){
												
												if(new_userInputValue.getContent() == null){
													if(old_userInputValue.getContent() == null){
														userInputValueList.remove(old_userInputValue);
														continue A;
													}
												}else{
													if(new_userInputValue.getContent().equals(old_userInputValue.getContent())){
														userInputValueList.remove(old_userInputValue);
														continue A;
													}
												}
												
											}else{
												if(new_userInputValue.getOptions().equals(old_userInputValue.getOptions())){
													userInputValueList.remove(old_userInputValue);
													continue A;
												}
											}
										}	
									}
								}
								add_userInputValue.add(new_userInputValue);
							}
						}
					}
				}
				if(userInputValueList != null && userInputValueList.size() >0){
					for(UserInputValue old_userInputValue : userInputValueList){
						delete_userInputValueIdList.add(old_userInputValue.getId());
					}
				}
				
				userService.updateUser(new_user,add_userInputValue,delete_userInputValueIdList,userRoleGroupList);

				userManage.delete_userState(new_user.getUserName());
				
				//删除缓存
				userManage.delete_cache_findUserById(user.getId());
				userManage.delete_cache_findUserByUserName(user.getUserName());
				userRoleManage.delete_cache_findRoleGroupByUserName(user.getUserName());
			}
			
			
		}
		
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}	
	}
	
	/**
	 * 用户  删除
	 * @param userId 用户Id集合
	 */
	@ResponseBody
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	public String delete(ModelMap model,Long[] userId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(userId != null && userId.length >0){
			List<Long> idList = new ArrayList<Long>();
			List<Long> softDelete_userIdList = new ArrayList<Long>();//逻辑删除用户Id集合
			List<User> softDelete_userList = new ArrayList<User>();//逻辑删除用户集合
			List<Long> physicalDelete_userIdList = new ArrayList<Long>();//物理删除用户Id集合
			List<String> physicalDelete_userNameList = new ArrayList<String>();//物理删除用户名称集合
			List<User> physicalDelete_userList = new ArrayList<User>();//物理删除用户集合
			for(Long l :userId){
				if(l != null){
					idList.add(l);
				}
			}
			if(idList != null && idList.size() >0){
				List<User> userList = userService.findUserByUserIdList(idList);
				if(userList != null && userList.size() >0){
					for(User user : userList){		
						if(user.getState() <10){
							softDelete_userIdList.add(user.getId());
							softDelete_userList.add(user);
						}else{
							physicalDelete_userIdList.add(user.getId());
							physicalDelete_userNameList.add(user.getUserName());
							physicalDelete_userList.add(user);
						}
					}
					
					
					if(softDelete_userIdList.size() >0){//逻辑删除
						int i = userService.markDelete(softDelete_userIdList);
						//删除缓存用户状态
						for(User user : softDelete_userList){
							userManage.delete_userState(user.getUserName());
							//删除缓存
							userManage.delete_cache_findUserById(user.getId());
							userManage.delete_cache_findUserByUserName(user.getUserName());
							userRoleManage.delete_cache_findRoleGroupByUserName(user.getUserName());
						}
					}
					if(physicalDelete_userNameList.size() >0){//物理删除
						for(User user : physicalDelete_userList){
							//删除用户话题文件
							topicManage.deleteTopicFile(user.getUserName(), false);
							
							//删除评论文件
							topicManage.deleteCommentFile(user.getUserName(), false);
							
							//删除用户问题文件
							questionManage.deleteQuestionFile(user.getUserName(), false);
							
							//删除答案文件
							questionManage.deleteAnswerFile(user.getUserName(), false);

							if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
								DateTime dateTime = new DateTime(user.getRegistrationDate());     
								String date = dateTime.toString("yyyy-MM-dd");
								
								String pathFile = "file"+File.separator+"avatar"+File.separator + date +File.separator  +user.getAvatarName();
								//删除头像
								fileManage.deleteFile(pathFile);
								
								String pathFile_100 = "file"+File.separator+"avatar"+File.separator + date +File.separator +"100x100" +File.separator+user.getAvatarName();
								//删除头像100*100
								fileManage.deleteFile(pathFile_100);
							}
						}
						
						
						
						int i = userService.delete(physicalDelete_userIdList,physicalDelete_userNameList);
						
						for(User user : physicalDelete_userList){
							//添加删除索引标记
							topicIndexService.addTopicIndex(new TopicIndex(user.getUserName(),4));
							questionIndexService.addQuestionIndex(new QuestionIndex(user.getUserName(),4));
							
							//删除缓存用户状态
							userManage.delete_userState(user.getUserName());
							//删除缓存
							userManage.delete_cache_findUserById(user.getId());
							userManage.delete_cache_findUserByUserName(user.getUserName());
							userRoleManage.delete_cache_findRoleGroupByUserName(user.getUserName());
							
						}

					}
					
				}
				
	
			}else{
				error.put("userId", "用户不存在");
			}
		}else{
			error.put("userId", "用户Id不能为空");
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		
	}
	/**
	 * 还原
	 * @param model
	 * @param userId 用户Id集合
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=reduction",method=RequestMethod.POST)
	public String reduction(ModelMap model,Long[] userId,
			HttpServletResponse response) throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(userId != null && userId.length>0){
			
			List<User> userList = userService.findUserByUserIdList(Arrays.asList(userId));
			if(userList != null && userList.size() >0){
				
				for(User user :userList){
					if(user.getState().equals(11)){ //1:正常用户   2:禁止用户   11: 正常用户删除   12: 禁止用户删除
						user.setState(1);
					}else if(user.getState().equals(12)){
						user.setState(2);
					}
					
				}
				userService.reductionUser(userList);
				
				//删除缓存用户状态
				for(User user :userList){
					userManage.delete_userState(user.getUserName());
					//删除缓存
					userManage.delete_cache_findUserById(user.getId());
					userManage.delete_cache_findUserByUserName(user.getUserName());
					userRoleManage.delete_cache_findRoleGroupByUserName(user.getUserName());
				}
				
				
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
			}else{
				error.put("userId", "用户不存在");
			}
		}else{
			error.put("userId", "用户Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	

	
	/**
	 * 发表的话题
	 * 
	 * @param pageForm
	 * @param model
	 * @param userName 用户名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=allTopic",method=RequestMethod.GET)
	public String allTopic(PageForm pageForm,ModelMap model,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		if(userName != null && !"".equals(userName.trim())){
			StringBuffer jpql = new StringBuffer("");
			//存放参数值
			List<Object> params = new ArrayList<Object>();

			jpql.append(" and o.userName=?"+ (params.size()+1));
			params.add(userName.trim());
			
			//删除第一个and
			String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
			
			PageView<Topic> pageView = new PageView<Topic>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			//当前页
			int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
			//排序
			LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
			
			orderby.put("id", "desc");//根据id字段降序排序
			
			
			//调用分页算法类
			QueryResult<Topic> qr = topicService.getScrollData(Topic.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				List<Tag> tagList = tagService.findAllTag();
				if(tagList != null && tagList.size() >0){
					for(Topic topic : qr.getResultlist()){
						for(Tag tag : tagList){
							if(topic.getTagId().equals(tag.getId())){
								topic.setTagName(tag.getName());
								break;
							}
						}
						
					}
				}
				
				User user = null;
				for(Topic topic : qr.getResultlist()){
					if(user == null){
						user = userManage.query_cache_findUserByUserName(topic.getUserName());
					}
					if(user != null){
						topic.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							topic.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
							topic.setAvatarName(user.getAvatarName());
						}		
					}
				}
				
			}

			pageView.setQueryResult(qr);
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
		}else{
			error.put("userName", "用户名称不能为空");
		}
		
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 发表的评论
	 * @param pageForm
	 * @param model
	 * @param userName 用户名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=allComment",method=RequestMethod.GET)
	public String allComment(PageForm pageForm,ModelMap model,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(userName != null && !"".equals(userName.trim())){
			StringBuffer jpql = new StringBuffer("");
			//存放参数值
			List<Object> params = new ArrayList<Object>();

			jpql.append(" and o.userName=?"+ (params.size()+1));
			params.add(userName.trim());
			
			//删除第一个and
			String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
			
			PageView<Comment> pageView = new PageView<Comment>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			//当前页
			int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
			//排序
			LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
			
			orderby.put("id", "desc");//根据id字段降序排序
			
			
			//调用分页算法类
			QueryResult<Comment> qr = commentService.getScrollData(Comment.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				List<Long> topicIdList = new ArrayList<Long>();
				for(Comment o :qr.getResultlist()){
	    			o.setContent(textFilterManage.filterText(o.getContent()));
	    			if(!topicIdList.contains(o.getTopicId())){
	    				topicIdList.add(o.getTopicId());
	    			}
	    		}
				List<Topic> topicList = topicService.findTitleByIdList(topicIdList);
				if(topicList != null && topicList.size() >0){
					for(Comment o :qr.getResultlist()){
						for(Topic topic : topicList){
							if(topic.getId().equals(o.getTopicId())){
								o.setTopicTitle(topic.getTitle());
								break;
							}
						}
						
					}
				}
				User user = null;
				for(Comment comment : qr.getResultlist()){
					if(user == null){
						user = userManage.query_cache_findUserByUserName(comment.getUserName());
					}
					if(user != null){
						comment.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							comment.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
							comment.setAvatarName(user.getAvatarName());
						}		
					}
				}
				
			}

			pageView.setQueryResult(qr);
			
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
		}else{
			error.put("userName", "用户名称不能为空");
		}
		

		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 发表的回复
	 * @param pageForm
	 * @param model
	 * @param userName 用户名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=allReply",method=RequestMethod.GET)
	public String allReply(PageForm pageForm,ModelMap model,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(userName != null && !"".equals(userName.trim())){
			StringBuffer jpql = new StringBuffer("");
			//存放参数值
			List<Object> params = new ArrayList<Object>();

			jpql.append(" and o.userName=?"+ (params.size()+1));
			params.add(userName.trim());
			
			//删除第一个and
			String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
			
			PageView<Reply> pageView = new PageView<Reply>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			//当前页
			int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
			//排序
			LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
			
			orderby.put("id", "desc");//根据id字段降序排序
			
			
			//调用分页算法类
			QueryResult<Reply> qr = commentService.getScrollData(Reply.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				List<Long> topicIdList = new ArrayList<Long>();
				for(Reply o :qr.getResultlist()){
	    				
	    			o.setContent(textFilterManage.filterText(o.getContent()));
	    			if(!topicIdList.contains(o.getTopicId())){
	    				topicIdList.add(o.getTopicId());
	    			}
	    		}
				List<Topic> topicList = topicService.findTitleByIdList(topicIdList);
				if(topicList != null && topicList.size() >0){
					for(Reply o :qr.getResultlist()){
						for(Topic topic : topicList){
							if(topic.getId().equals(o.getTopicId())){
								o.setTopicTitle(topic.getTitle());
								break;
							}
						}
						
					}
				}
				User user = null;
				for(Reply reply : qr.getResultlist()){
					if(user == null){
						user = userManage.query_cache_findUserByUserName(reply.getUserName());
					}
					if(user != null){
						reply.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							reply.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
							reply.setAvatarName(user.getAvatarName());
						}		
					}
				}
			}

			pageView.setQueryResult(qr);
			
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
		}else{
			error.put("userName", "用户名称不能为空");
		}
		

		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	/**
	 * 发表的问题
	 * 
	 * @param pageForm
	 * @param model
	 * @param userName 用户名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=allQuestion",method=RequestMethod.GET)
	public String allQuestion(PageForm pageForm,ModelMap model,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(userName != null && !"".equals(userName.trim())){
			StringBuffer jpql = new StringBuffer("");
			//存放参数值
			List<Object> params = new ArrayList<Object>();

			jpql.append(" and o.userName=?"+ (params.size()+1));
			params.add(userName.trim());
			
			//删除第一个and
			String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
			
			PageView<Question> pageView = new PageView<Question>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			//当前页
			int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
			//排序
			LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
			
			orderby.put("id", "desc");//根据id字段降序排序
			
			//调用分页算法类
			QueryResult<Question> qr = questionService.getScrollData(Question.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				List<QuestionTag> questionTagList = questionTagService.findAllQuestionTag();
				
				if(questionTagList != null && questionTagList.size() >0){
					for(Question question : qr.getResultlist()){
						List<QuestionTagAssociation> questionTagAssociationList = questionManage.query_cache_findQuestionTagAssociationByQuestionId(question.getId());
						if(questionTagAssociationList != null && questionTagAssociationList.size() >0){
							for(QuestionTag questionTag : questionTagList){
								for(QuestionTagAssociation questionTagAssociation : questionTagAssociationList){
									if(questionTagAssociation.getQuestionTagId().equals(questionTag.getId())){
										questionTagAssociation.setQuestionTagName(questionTag.getName());
										question.addQuestionTagAssociation(questionTagAssociation);
										break;
									}
								}
							}
						}
					}
				}
				User user = null;
				for(Question question : qr.getResultlist()){
					if(user == null){
						user = userManage.query_cache_findUserByUserName(question.getUserName());
					}
					if(user != null){
						question.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							question.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
							question.setAvatarName(user.getAvatarName());
						}		
					}
				}
			}

			pageView.setQueryResult(qr);
			
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
		}else{
			error.put("userName", "用户名称不能为空");
		}
		

		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 发表的答案
	 * @param pageForm
	 * @param model
	 * @param userName 用户名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=allAnswer",method=RequestMethod.GET)
	public String allAuditAnswer(PageForm pageForm,ModelMap model,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(userName != null && !"".equals(userName.trim())){
			StringBuffer jpql = new StringBuffer("");
			//存放参数值
			List<Object> params = new ArrayList<Object>();

			jpql.append(" and o.userName=?"+ (params.size()+1));
			params.add(userName.trim());
			
			//删除第一个and
			String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
			
			PageView<Answer> pageView = new PageView<Answer>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			//当前页
			int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
			//排序
			LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
			
			orderby.put("id", "desc");//根据id字段降序排序
			
			
			//调用分页算法类
			QueryResult<Answer> qr = answerService.getScrollData(Answer.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				List<Long> questionIdList = new ArrayList<Long>();
				for(Answer o :qr.getResultlist()){
	    			o.setContent(textFilterManage.filterText(o.getContent()));
	    			if(!questionIdList.contains(o.getQuestionId())){
	    				questionIdList.add(o.getQuestionId());
	    			}
	    		}
				List<Question> questionList = questionService.findTitleByIdList(questionIdList);
				if(questionList != null && questionList.size() >0){
					for(Answer o :qr.getResultlist()){
						for(Question question : questionList){
							if(question.getId().equals(o.getQuestionId())){
								o.setQuestionTitle(question.getTitle());
								break;
							}
						}
						
					}
				}
				User user = null;
				for(Answer answer : qr.getResultlist()){
					if(user == null){
						user = userManage.query_cache_findUserByUserName(answer.getUserName());
					}
					if(user != null){
						answer.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							answer.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
							answer.setAvatarName(user.getAvatarName());
						}		
					}
				}
			}

			pageView.setQueryResult(qr);
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
		}else{
			error.put("userName", "用户名称不能为空");
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 发表的答案回复
	 * @param pageForm
	 * @param model
	 * @param userName 用户名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=allAnswerReply",method=RequestMethod.GET)
	public String allAuditAnswerReply(PageForm pageForm,ModelMap model,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(userName != null && !"".equals(userName.trim())){
			StringBuffer jpql = new StringBuffer("");
			//存放参数值
			List<Object> params = new ArrayList<Object>();

			jpql.append(" and o.userName=?"+ (params.size()+1));
			params.add(userName.trim());
			
			//删除第一个and
			String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
			
			PageView<AnswerReply> pageView = new PageView<AnswerReply>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			//当前页
			int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
			//排序
			LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
			
			orderby.put("id", "desc");//根据id字段降序排序
			
			
			//调用分页算法类
			QueryResult<AnswerReply> qr = answerService.getScrollData(AnswerReply.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				List<Long> questionIdList = new ArrayList<Long>();
				for(AnswerReply o :qr.getResultlist()){
	    				
	    			o.setContent(textFilterManage.filterText(o.getContent()));
	    			if(!questionIdList.contains(o.getQuestionId())){
	    				questionIdList.add(o.getQuestionId());
	    			}
	    		}
				List<Question> questionList = questionService.findTitleByIdList(questionIdList);
				if(questionList != null && questionList.size() >0){
					for(AnswerReply o :qr.getResultlist()){
						for(Question question : questionList){
							if(question.getId().equals(o.getQuestionId())){
								o.setQuestionTitle(question.getTitle());
								break;
							}
						}
						
					}
				}
				User user = null;
				for(AnswerReply answerReply : qr.getResultlist()){
					if(user == null){
						user = userManage.query_cache_findUserByUserName(answerReply.getUserName());
					}
					if(user != null){
						answerReply.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							answerReply.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
							answerReply.setAvatarName(user.getAvatarName());
						}		
					}
				}
			}

			pageView.setQueryResult(qr);
			
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
		}else{
			error.put("userName", "用户名称不能为空");
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	
	
	
	
	
	/**
	 * 更新头像
	 * @param model
	 * @param file
	 * @param id 用户Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=updateAvatar",method=RequestMethod.POST)
	public String updateAvatar(ModelMap model,MultipartFile file,Long id,
			HttpServletRequest request,HttpServletResponse response)
			throws Exception {	
		
		Map<String,String> error = new HashMap<String,String>();//错误
		if(id ==null){
			error.put("userId", "用户Id不能为空");
		}
		if(file ==null || file.isEmpty()){
			error.put("file", "文件不能为空");
		}
		String _width = request.getParameter("width");
		String _height = request.getParameter("height");
		String _x = request.getParameter("x");
		String _y = request.getParameter("y");
		
		
		Integer width = null;//宽
		Integer height = null;//高
		Integer x = 0;//坐标X轴
		Integer y = 0;//坐标Y轴
		
		
		if(_width != null && !"".equals(_width.trim())){
			if(Verification.isPositiveInteger(_width.trim())){
				if(_width.trim().length() >=8){
					error.put("width", "不能超过8位数字");//不能超过8位数字
				}else{
					width = Integer.parseInt(_width.trim());
				}
				
				
			}else{
				error.put("width", "宽度必须大于0");//宽度必须大于0
			}
			
		}
		if(_height != null && !"".equals(_height.trim())){
			if(Verification.isPositiveInteger(_height.trim())){
				if(_height.trim().length() >=8){
					error.put("height", "不能超过8位数字");//不能超过8位数字
				}else{
					height = Integer.parseInt(_height.trim());
				}
				
			}else{
				error.put("height", "高度必须大于0 ");//高度必须大于0 
			}
		}
		
		if(_x != null && !"".equals(_x.trim())){
			if(Verification.isPositiveIntegerZero(_x.trim())){
				if(_x.trim().length() >=8){
					error.put("x", "不能超过8位数字");//不能超过8位数字
				}else{
					x = Integer.parseInt(_x.trim());
				}
				
			}else{
				error.put("x", "X轴必须大于或等于0");//X轴必须大于或等于0
			}
			
		}
		
		if(_y != null && !"".equals(_y.trim())){
			if(Verification.isPositiveIntegerZero(_y.trim())){
				if(_y.trim().length() >=8){
					error.put("y","不能超过8位数字");//不能超过8位数字
				}else{
					y = Integer.parseInt(_y.trim());
				}
				
			}else{
				error.put("y","Y轴必须大于或等于0");//Y轴必须大于或等于0
			}
			
		}
		
		
		
		String newFileName = "";
	
		User user = userService.findUserById(id);
		if(user != null){
			//当前文件名称
			String fileName = file.getOriginalFilename();
			
			//文件大小
			Long size = file.getSize();
			
			
			
			//允许上传图片大小 单位KB
			long imageSize = 3*1024L;
			
			Integer maxWidth = 200;//最大宽度
			Integer maxHeight = 200;//最大高度
			DateTime dateTime = new DateTime(user.getRegistrationDate());     
			String date = dateTime.toString("yyyy-MM-dd");
			
			//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
			String pathDir = "file"+File.separator+"avatar"+File.separator + date +File.separator ;
			//生成文件保存目录
			fileManage.createFolder(pathDir);
			//100*100目录
			String pathDir_100 = "file"+File.separator+"avatar"+File.separator + date +File.separator +"100x100" +File.separator;
			//生成文件保存目录
			fileManage.createFolder(pathDir_100);
			
			if(size/1024 <= imageSize){
				if("blob".equalsIgnoreCase(fileName)){//Blob类型
					
					newFileName = UUIDUtil.getUUID32()+ ".jpg";
					
					BufferedImage bufferImage = ImageIO.read(file.getInputStream());  
		            //获取图片的宽和高  
		            int srcWidth = bufferImage.getWidth();  
		            int srcHeight = bufferImage.getHeight();  
					if(srcWidth > maxWidth){
						error.put("file","超出最大宽度");
					}
					if(srcHeight > maxHeight){
						error.put("file","超出最大高度");
					}
					if(error.size() == 0){
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							String oldPathFile = pathDir +user.getAvatarName();
							//删除旧头像
							fileManage.deleteFile(oldPathFile);
							String oldPathFile_100 = pathDir_100+user.getAvatarName();
							//删除旧头像100*100
							fileManage.deleteFile(oldPathFile_100);
						}
						
						//保存文件
						fileManage.writeFile(pathDir, newFileName,file.getBytes());

						//生成100*100缩略图
						fileManage.createImage(file.getInputStream(),pathDir_100+newFileName,"jpg",100,100);
					}
				}else{
					
					
					//允许上传图片格式
					List<String> formatList = new ArrayList<String>();
					formatList.add("gif");
					formatList.add("jpg");
					formatList.add("jpeg");
					formatList.add("bmp");
					formatList.add("png");
					
					if(size/1024 <= imageSize){
						
						//验证文件类型
						boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
				
						if(authentication){
							//生成文件保存目录
							fileManage.createFolder(pathDir);
							//生成文件保存目录
							fileManage.createFolder(pathDir_100);
							
							if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
								String oldPathFile = pathDir + user.getAvatarName();
								//删除旧头像
								fileManage.deleteFile(oldPathFile);
								String oldPathFile_100 = pathDir_100 + user.getAvatarName();
								//删除旧头像100*100
								fileManage.deleteFile(oldPathFile_100);
							}

							BufferedImage bufferImage = ImageIO.read(file.getInputStream());  
				            //获取图片的宽和高  
				            int srcWidth = bufferImage.getWidth();  
				            int srcHeight = bufferImage.getHeight();  
							
							//取得文件后缀
							String suffix = FileUtil.getExtension(fileName).toLowerCase();
							//构建文件名称
							newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							
							if(srcWidth <=200 && srcHeight <=200){	
								//保存文件
								fileManage.writeFile(pathDir, newFileName,file.getBytes());
								
								if(srcWidth <=100 && srcHeight <=100){
									//保存文件
									fileManage.writeFile(pathDir_100, newFileName,file.getBytes());
								}else{
									//生成100*100缩略图
									fileManage.createImage(file.getInputStream(),pathDir_100+newFileName,suffix,100,100);
								}
							}else{
								//生成200*200缩略图
								fileManage.createImage(file.getInputStream(),pathDir+newFileName,suffix,x,y,width,height,200,200);

								//生成100*100缩略图
								fileManage.createImage(file.getInputStream(),pathDir_100+newFileName,suffix,x,y,width,height,100,100);
	    
							}	
						}else{
							error.put("file","当前文件类型不允许上传");//当前文件类型不允许上传
						}	
					}else{
						error.put("file","文件超出允许上传大小");//文件超出允许上传大小
					}
				}
				
				
			}else{
				error.put("file", "文件超出允许上传大小");
			}
		}else{
			error.put("user", "用户不存在");
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			userService.updateUserAvatar(user.getUserName(), newFileName);
			//删除缓存
			userManage.delete_cache_findUserById(user.getId());
			userManage.delete_cache_findUserByUserName(user.getUserName());
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		
		/**
		if(user == null){
			error.put("user", "用户不存在");
		}
		
		String _width = request.getParameter("width");
		String _height = request.getParameter("height");
		String _x = request.getParameter("x");
		String _y = request.getParameter("y");
		
		
		Integer width = null;//宽
		Integer height = null;//高
		Integer x = 0;//坐标X轴
		Integer y = 0;//坐标Y轴
		
		
		if(_width != null && !"".equals(_width.trim())){
			if(Verification.isPositiveInteger(_width.trim())){
				if(_width.trim().length() >=8){
					error.put("width", "不能超过8位数字");//不能超过8位数字
				}else{
					width = Integer.parseInt(_width.trim());
				}
				
				
			}else{
				error.put("width", "宽度必须大于0");//宽度必须大于0
			}
			
		}
		if(_height != null && !"".equals(_height.trim())){
			if(Verification.isPositiveInteger(_height.trim())){
				if(_height.trim().length() >=8){
					error.put("height", "不能超过8位数字");//不能超过8位数字
				}else{
					height = Integer.parseInt(_height.trim());
				}
				
			}else{
				error.put("height", "高度必须大于0 ");//高度必须大于0 
			}
		}
		
		if(_x != null && !"".equals(_x.trim())){
			if(Verification.isPositiveIntegerZero(_x.trim())){
				if(_x.trim().length() >=8){
					error.put("x", "不能超过8位数字");//不能超过8位数字
				}else{
					x = Integer.parseInt(_x.trim());
				}
				
			}else{
				error.put("x", "X轴必须大于或等于0");//X轴必须大于或等于0
			}
			
		}
		
		if(_y != null && !"".equals(_y.trim())){
			if(Verification.isPositiveIntegerZero(_y.trim())){
				if(_y.trim().length() >=8){
					error.put("y","不能超过8位数字");//不能超过8位数字
				}else{
					y = Integer.parseInt(_y.trim());
				}
				
			}else{
				error.put("y","Y轴必须大于或等于0");//Y轴必须大于或等于0
			}
			
		}
		//构建文件名称
		String newFileName = "";
		if(error.size() ==0){
			
			DateTime dateTime = new DateTime(user.getRegistrationDate());     
			String date = dateTime.toString("yyyy-MM-dd");
			
			if(file != null && !file.isEmpty()){
				//当前文件名称
				String fileName = file.getOriginalFilename();
				
				//文件大小
				Long size = file.getSize();
				

				
				//允许上传图片格式
				List<String> formatList = new ArrayList<String>();
				formatList.add("gif");
				formatList.add("jpg");
				formatList.add("jpeg");
				formatList.add("bmp");
				formatList.add("png");
				//允许上传图片大小 单位KB
				long imageSize = 3*1024L;
				
				if(size/1024 <= imageSize){
					
					//验证文件类型
					boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
			
					if(authentication){
						//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
						String pathDir = "file"+File.separator+"avatar"+File.separator + date +File.separator ;
						//100*100目录
						String pathDir_100 = "file"+File.separator+"avatar"+File.separator + date +File.separator +"100x100" +File.separator;

						//生成文件保存目录
						fileManage.createFolder(pathDir);
						//生成文件保存目录
						fileManage.createFolder(pathDir_100);
						
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							String oldPathFile = pathDir + user.getAvatarName();
							//删除旧头像
							fileManage.deleteFile(oldPathFile);
							String oldPathFile_100 = pathDir_100 + user.getAvatarName();
							//删除旧头像100*100
							fileManage.deleteFile(oldPathFile_100);
						}

						BufferedImage bufferImage = ImageIO.read(file.getInputStream());  
			            //获取图片的宽和高  
			            int srcWidth = bufferImage.getWidth();  
			            int srcHeight = bufferImage.getHeight();  
						
						//取得文件后缀
						String suffix = FileUtil.getExtension(fileName).toLowerCase();
						//构建文件名称
						newFileName = UUIDUtil.getUUID32()+ "." + suffix;
						
						if(srcWidth <=200 && srcHeight <=200){	
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());
							
							if(srcWidth <=100 && srcHeight <=100){
								//保存文件
								fileManage.writeFile(pathDir_100, newFileName,file.getBytes());
							}else{
								//生成100*100缩略图
								fileManage.createImage(file.getInputStream(),pathDir_100+newFileName,suffix,100,100);
							}
						}else{
							//生成200*200缩略图
							fileManage.createImage(file.getInputStream(),pathDir+newFileName,suffix,x,y,width,height,200,200);

							//生成100*100缩略图
							fileManage.createImage(file.getInputStream(),pathDir_100+newFileName,suffix,x,y,width,height,100,100);
    
						}	
					}else{
						error.put("imgFile","当前文件类型不允许上传");//当前文件类型不允许上传
					}	
				}else{
					error.put("imgFile","文件超出允许上传大小");//文件超出允许上传大小
				}	
			}else{
				error.put("imgFile","文件不能为空");//文件不能为空
			}
		}
		

		if(error.size() ==0){
			userService.updateUserAvatar(user.getUserName(), newFileName);
			//删除缓存
			userManage.delete_cache_findUserById(user.getId());
			userManage.delete_cache_findUserByUserName(user.getUserName());
		}
		
		
		

		
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值

		if(error != null && error.size() >0){
			returnValue.put("success", "false");
			returnValue.put("error", error);
		}else{
			returnValue.put("success", "true");
		}
		return JsonUtils.toJSONString(returnValue);**/
	}
	
	
	
	/**
	 * 用户管理 支付
	 * @param model
	 * @param id 用户Id
	 * @param type 支付类型  1:支付流水号  2:预存款 3:积分
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=payment",method=RequestMethod.POST)
	public String payment(ModelMap model,Long id,Integer type,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();//错误
		
		if(id == null){
			error.put("payment", "参数错误");
		}else{
			User user = userService.findUserById(id);
			if(user == null){
				error.put("payment", "用户不存在");
			}
			String staffName = "";//员工名称	
			Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
			if(obj instanceof UserDetails){
				staffName =((UserDetails)obj).getUsername();
			}
			
			if(type != null){
				if(type.equals(1)){//1:支付流水号
					BigDecimal paymentRunningNumber_amount = new BigDecimal("0");
					String paymentRunningNumber = null;//支付流水号

					String _paymentRunningNumberAmount = request.getParameter("paymentRunningNumberAmount");
					String _paymentRunningNumber = request.getParameter("paymentRunningNumber");
					String paymentRunningNumber_remark = request.getParameter("paymentRunningNumber_remark");//备注
					
					if(_paymentRunningNumberAmount != null && !"".equals(_paymentRunningNumberAmount.trim())){
						if(_paymentRunningNumberAmount.trim().length() > 12){
							error.put("paymentRunningNumberAmount", "金额不能超过12位数字");
						}else{
							boolean paymentRunningNumber_amountVerification = Verification.isAmount(_paymentRunningNumberAmount.trim());//金额
							if(paymentRunningNumber_amountVerification){
								paymentRunningNumber_amount = new BigDecimal(_paymentRunningNumberAmount.trim());
								if(paymentRunningNumber_amount.compareTo(new BigDecimal("0")) <=0){
									error.put("paymentRunningNumberAmount", "金额必须大于0");	
								}
							
							}else{
								error.put("paymentRunningNumberAmount", "请填写金额");	
							}
						}
					}
					if(_paymentRunningNumber != null && !"".equals(_paymentRunningNumber.trim())){
						if(_paymentRunningNumber.trim().length() >64){
							error.put("paymentRunningNumber", "流水号不能超过64位");
						}else{
							paymentRunningNumber = _paymentRunningNumber.trim();
						}
						
						
					}
					
					if(error.size() == 0&& paymentRunningNumber != null){
						PaymentVerificationLog paymentVerificationLog = paymentService.findPaymentVerificationLogById(paymentRunningNumber);
						if(paymentVerificationLog != null){
							if(paymentVerificationLog.getPaymentModule().equals(5)){//5.用户充值
								if(user.getUserName().equals(paymentVerificationLog.getUserName())){
									
									PaymentLog paymentLog = new PaymentLog();
									paymentLog.setPaymentRunningNumber(paymentVerificationLog.getId());//支付流水号
									paymentLog.setPaymentModule(paymentVerificationLog.getPaymentModule());//支付模块 5.用户充值
									paymentLog.setSourceParameterId(String.valueOf(paymentVerificationLog.getParameterId()));//参数Id 
									paymentLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
									paymentLog.setOperationUserName(staffName);
									
									paymentLog.setAmount(paymentRunningNumber_amount);//金额
									paymentLog.setInterfaceProduct(-1);//接口产品
									paymentLog.setTradeNo("");//交易号
									paymentLog.setUserName(paymentVerificationLog.getUserName());//用户名称
									paymentLog.setRemark(paymentRunningNumber_remark);//备注
									paymentLog.setAmountState(1);//金额状态  1:账户存入  2:账户支出 
									Object new_paymentLog = paymentManage.createPaymentLogObject(paymentLog);
									
									userService.onlineRecharge(paymentRunningNumber,paymentVerificationLog.getUserName(),paymentRunningNumber_amount,new_paymentLog);
									
									//删除缓存
									userManage.delete_cache_findUserById(user.getId());
									userManage.delete_cache_findUserByUserName(user.getUserName());
									
									return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
								}else{
									error.put("paymentRunningNumber", "流水号不属于当前用户");
								}
							}else{
								error.put("paymentRunningNumber", "流水号不属于充值模块");
							}
						}else{
							error.put("paymentRunningNumber", "流水号不存在");
						}
					}

					
				}else if(type.equals(2)){//2:预存款
					BigDecimal deposit = new BigDecimal("0");//预存款

					String deposit_symbol = request.getParameter("deposit_symbol");//符号
					String _deposit = request.getParameter("deposit");
					String deposit_remark = request.getParameter("deposit_remark");//备注
					
					
					if("+".equals(deposit_symbol)){//增加
						deposit_symbol = "+";
					}else{//减少
						deposit_symbol = "-";
					}
					
					if(_deposit != null && !"".equals(_deposit.trim())){
						if(_deposit.trim().length() >=10){
							error.put("deposit", "不能超过10位数");
						}
						
						
						boolean deposit_verification = Verification.isAmount(_deposit.trim());
						if(!deposit_verification){
							error.put("deposit", "请填写正确的金额");
						}else{
							deposit = new BigDecimal(_deposit.trim());//预存款
						}
					}
					
					if(error.size() == 0&& deposit_symbol != null && deposit.compareTo(new BigDecimal("0")) > 0){
							
						PaymentLog paymentLog = new PaymentLog();
						paymentLog.setPaymentRunningNumber(paymentManage.createRunningNumber(user.getId()));//支付流水号
						paymentLog.setPaymentModule(5);//支付模块 5.用户充值
						paymentLog.setSourceParameterId(String.valueOf(user.getId()));//参数Id 
						paymentLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
						paymentLog.setOperationUserName(staffName);
						
						paymentLog.setAmount(deposit);//金额
						paymentLog.setInterfaceProduct(-1);//接口产品
						paymentLog.setTradeNo("");//交易号
						paymentLog.setUserName(user.getUserName());//用户名称
						paymentLog.setRemark(deposit_remark);//备注
						
						if("+".equals(deposit_symbol)){//增加预存款
							paymentLog.setAmountState(1);//金额状态  1:账户存入  2:账户支出 
							Object new_paymentLog = paymentManage.createPaymentLogObject(paymentLog);
							userService.addUserDeposit(user.getUserName(),deposit,new_paymentLog);
						}else{//减少预存款
							paymentLog.setAmountState(2);//金额状态  1:账户存入  2:账户支出 
							Object new_paymentLog = paymentManage.createPaymentLogObject(paymentLog);
							userService.subtractUserDeposit(user.getUserName(),deposit,new_paymentLog);
						}
						
						//删除缓存
						userManage.delete_cache_findUserById(user.getId());
						userManage.delete_cache_findUserByUserName(user.getUserName());
						return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
					}
					
					
				}else if(type.equals(3)){//3:积分
					
					Long point = 0L;//积分

					String point_symbol = request.getParameter("point_symbol");//符号
					String _point = request.getParameter("point");//积分
					String point_remark = request.getParameter("point_remark");//备注
						
					if("+".equals(point_symbol)){//增加
						point_symbol = "+";
					}else{//减少
						point_symbol = "-";
					}
					
					if(_point != null && !"".equals(_point.trim())){
						if(_point.trim().length() >=10){
							error.put("point", "不能超过10位数");
						}
						boolean point_verification = Verification.isPositiveInteger(_point.trim());//正整数
						if(!point_verification){
							error.put("point", "请填写正整数");
						}else{
							point = Long.parseLong(_point.trim());
						}
						
					}
					
					if(error.size() == 0&& point_symbol != null && point > 0){
						
						PointLog pointLog = new PointLog();
						pointLog.setId(pointManage.createPointLogId(user.getId()));
						pointLog.setModule(600);//模块  600.账户充值
						pointLog.setParameterId(user.getId());//参数Id 
						pointLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
						pointLog.setOperationUserName(staffName);//操作用户名称
						
						pointLog.setPoint(point);//积分
						pointLog.setUserName(user.getUserName());//用户名称
						pointLog.setRemark(point_remark);
						
						
						if("+".equals(point_symbol)){//增加积分
							pointLog.setPointState(1);//积分状态 1:账户存入 
							Object new_pointLog = pointManage.createPointLogObject(pointLog);
							userService.addUserPoint(user.getUserName(),point,new_pointLog);
						}else{//减少积分
							pointLog.setPointState(2);//积分状态  1:账户存入  2:账户支出 
							Object new_pointLog = pointManage.createPointLogObject(pointLog);
							userService.subtractUserPoint(user.getUserName(),point,new_pointLog);
						}
						//删除缓存
						userManage.delete_cache_findUserById(user.getId());
						userManage.delete_cache_findUserByUserName(user.getUserName());
						
						//异步执行会员卡赠送任务(长期任务类型)
						membershipCardGiftTaskManage.async_triggerMembershipCardGiftTask(user.getUserName());
						
						return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
					}
				}
			}
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
}
