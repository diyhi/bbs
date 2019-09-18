package cms.web.action.user;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cms.bean.payment.PaymentLog;
import cms.bean.payment.PaymentVerificationLog;
import cms.bean.user.PointLog;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
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
import cms.service.setting.SettingService;
import cms.service.topic.CommentService;
import cms.service.topic.TagService;
import cms.service.topic.TopicIndexService;
import cms.service.topic.TopicService;
import cms.service.user.UserCustomService;
import cms.service.user.UserGradeService;
import cms.service.user.UserRoleService;
import cms.service.user.UserService;
import cms.utils.JsonUtils;
import cms.utils.PathUtil;
import cms.utils.RedirectPath;
import cms.utils.SHA;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.web.action.FileManage;
import cms.web.action.SystemException;
import cms.web.action.TextFilterManage;
import cms.web.action.lucene.TopicLuceneManage;
import cms.web.action.payment.PaymentManage;
import cms.web.action.thumbnail.ThumbnailManage;
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
	
	
	@Resource FileManage fileManage;
	@Resource ThumbnailManage thumbnailManage;
	
	@Resource TopicLuceneManage topicLuceneManage;
	@Resource TopicIndexService topicIndexService;
	
	@Resource UserRoleService userRoleService;
	@Resource UserRoleManage userRoleManage;
	@Resource PaymentService paymentService;
	@Resource PaymentManage paymentManage;
	
	/**
	 * 用户管理 查看
	 */
	@RequestMapping(params="method=show",method=RequestMethod.GET)
	public String show(ModelMap model,Long id,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		if(id == null){
			throw new SystemException("参数错误");
		}
		
		User user = userService.findUserById(id);
		if(user == null){
			throw new SystemException("用户不存在");
		}	
		user.setPassword(null);//密码不显示
		user.setAnswer(null);//密码提示答案不显示
		user.setSalt(null);//盐值不显示
		
		List<UserGrade> userGradeList = userGradeService.findAllGrade();
		if(userGradeList != null && userGradeList.size() >0){
			for(UserGrade userGrade : userGradeList){//取得所有等级 
				if(user.getPoint() >= userGrade.getNeedPoint()){
					user.setGradeName(userGrade.getName());//将等级值设进等级参数里
					break;
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
		
		
		model.addAttribute("userCustomList", userCustomList);
		model.addAttribute("user",user);
		
		return "jsp/user/show_user";
	}
	
	
	
	/**
	 * 用户管理 添加界面显示
	 */
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model,User user,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
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
		
		model.addAttribute("userCustomList", userCustomList);
		model.addAttribute("userRoleList", userRoleList);
		return "jsp/user/add_user";
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
	@RequestMapping(params="method=add",method=RequestMethod.POST)
	public String add(User formbean,String[] userRolesId,BindingResult result,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
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
		if (result.hasErrors() || error.size() >0) { 
			model.addAttribute("user",formbean);
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
			
			model.addAttribute("userRoleList", userRoleList);
		
			model.addAttribute("userCustomList", userCustomList);
			model.addAttribute("error", error);
			return "jsp/user/add_user";
		} 
		
		
		User user = new User();
		user.setSalt(UUIDUtil.getUUID32());
		user.setUserName(formbean.getUserName().trim());
		if(formbean.getNickname() != null && !"".equals(formbean.getNickname().trim())){
			user.setNickname(formbean.getNickname().trim());
		}
		
		//密码
		user.setPassword(SHA.sha256Hex(SHA.sha256Hex(formbean.getPassword().trim())+"["+user.getSalt()+"]"));
		user.setEmail(formbean.getEmail().trim());
		user.setIssue(formbean.getIssue().trim());
		//密码提示答案由  密码提示答案原文sha256  进行sha256组成
		user.setAnswer(SHA.sha256Hex(SHA.sha256Hex(formbean.getAnswer().trim())));

		user.setRegistrationDate(new Date());
		user.setRemarks(formbean.getRemarks());
		user.setState(formbean.getState());
		
		user.setMobile(formbean.getMobile().trim());
		user.setRealNameAuthentication(formbean.isRealNameAuthentication());
		//允许显示用户动态
		user.setAllowUserDynamic(formbean.getAllowUserDynamic());
		user.setSecurityDigest(new Date().getTime());
		
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
			throw new SystemException("添加用户错误");
		//	e.printStackTrace();
		}
		//删除缓存
		userManage.delete_cache_findUserById(user.getId());
		userManage.delete_cache_findUserByUserName(user.getUserName());
		userRoleManage.delete_cache_findRoleGroupByUserName(user.getUserName());
		
		request.setAttribute("message", "添加用户成功");
		request.setAttribute("urladdress", RedirectPath.readUrl("control.user.list"));
		return "jsp/common/message";
	}
	
	/**
	 * 用户管理 修改界面显示
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(User formbean,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(formbean.getId() == null){
			throw new SystemException("参数错误");
		}
		
		User user = userService.findUserById(formbean.getId());
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
		model.addAttribute("userRoleList", userRoleList);
		
		model.addAttribute("userCustomList", userCustomList);
	
		
		
		model.addAttribute("user",user);
		
		return "jsp/user/edit_user";	
	}
	/**
	 * 用户管理 修改
	 * @param formbean
	 * @param userRolesId 角色Id
	 * @param model
	 * @param pageForm
	 * @param jumpStatus 跳转流程  如果值小于或等于-10，则返回空串(页面判断用[-10：不刷新  -12:刷新上一页]) -1：来自退款页面跳转
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	public String edit(User formbean,String[] userRolesId,ModelMap model,PageForm pageForm,Integer jumpStatus,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(formbean.getId() == null){
			throw new SystemException("参数错误");
		}
		User user = userService.findUserById(formbean.getId());
		
		if(!user.getUserVersion().equals(formbean.getUserVersion())){
			throw new SystemException("当前数据不是最新");
		}
		User new_user = new User();
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		
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
		if(formbean.getEmail() != null && !"".equals(formbean.getEmail().trim())){//邮箱
			if(Verification.isEmail(formbean.getEmail().trim()) == false){
				error.put("email", "Email地址不正确");
			}
			if(formbean.getEmail().trim().length()>60){
				error.put("email", "Email地址不能超过60个字符");
			}
			new_user.setEmail(formbean.getEmail().trim());
		}
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
		if(error.size() >0){
			model.addAttribute("error",error);
			formbean.setUserName(user.getUserName());
			formbean.setUserVersion(user.getUserVersion());
			model.addAttribute("user",formbean);
			
			
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
			model.addAttribute("userRoleList", userRoleList);
			model.addAttribute("userCustomList", userCustomList);
			return "jsp/user/edit_user";	
		}
		
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
		
		if(jumpStatus != null && jumpStatus<= -10){
			model.addAttribute("jumpStatus",jumpStatus);//返回消息
			//跳转到框架回调页面
			return "jsp/admin/frameCallback";
		}
		
		
		request.setAttribute("message", "修改用户成功");
		request.setAttribute("urladdress", RedirectPath.readUrl("control.user.list")+"?page="+pageForm.getPage());
		return "jsp/common/message";
	}
	
	/**
	 * 用户  删除
	 * @param userId 用户Id集合
	 * @param queryState 查询状态  null或true:正常页面  false:回收站
	 */
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Long[] userId,Boolean queryState,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(userId != null && userId.length >0){
			List<Long> idList = new ArrayList<Long>();
			List<String> userNameList = new ArrayList<String>();//用户名称集合
			for(Long l :userId){
				if(l != null){
					idList.add(l);
				}
			}
			if(idList != null && idList.size() >0){
				List<User> userList = userService.findUserByUserIdList(idList);
				if(userList != null && userList.size() >0){
					for(User user : userList){
						userNameList.add(user.getUserName());
						
					}
					if(queryState != null && queryState != true){//物理删除
						for(User user : userList){
							//删除用户话题文件
							topicManage.deleteTopicFile(user.getUserName(), false);
							
							//删除评论文件
							topicManage.deleteCommentFile(user.getUserName(), false);

							DateTime dateTime = new DateTime(user.getRegistrationDate());     
							String date = dateTime.toString("yyyy-MM-dd");
							
							String pathFile = "file"+File.separator+"avatar"+File.separator + date +File.separator  +user.getAvatarName();
							//删除头像
							fileManage.deleteFile(pathFile);
							
							String pathFile_100 = "file"+File.separator+"avatar"+File.separator + date +File.separator +"100x100" +File.separator+user.getAvatarName();
							//删除头像100*100
							fileManage.deleteFile(pathFile_100);
							
						}
						
						
						
						int i = userService.delete(idList,userNameList);
						
						for(User user : userList){
							//添加删除索引标记
							topicIndexService.addTopicIndex(new TopicIndex(user.getUserName(),4));
							
							
							//删除缓存用户状态
							userManage.delete_userState(user.getUserName());
							//删除缓存
							userManage.delete_cache_findUserById(user.getId());
							userManage.delete_cache_findUserByUserName(user.getUserName());
							userRoleManage.delete_cache_findRoleGroupByUserName(user.getUserName());
							
						}

						
						if(i >0){
							return "1";
						}
					}else{//标记删除
						int i = userService.markDelete(idList);
						//删除缓存用户状态
						for(User user : userList){
							userManage.delete_userState(user.getUserName());
							//删除缓存
							userManage.delete_cache_findUserById(user.getId());
							userManage.delete_cache_findUserByUserName(user.getUserName());
							userRoleManage.delete_cache_findRoleGroupByUserName(user.getUserName());
						}
						if(i >0){
							return "1";
						}
					}
				}
				
	
			}
		}
		return "0";
	}
	/**
	 * 还原
	 * @param model
	 * @param userId 用户Id集合
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=reduction",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String reduction(ModelMap model,Long[] userId,
			HttpServletResponse response) throws Exception {
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
				
				
				return "1";
			}	
		}
		return "0";
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
	@RequestMapping(params="method=allTopic",method=RequestMethod.GET)
	public String allTopic(PageForm pageForm,ModelMap model,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
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
				
			}

			pageView.setQueryResult(qr);
			
			
			model.addAttribute("pageView", pageView);
		}
		
		
		

		return "jsp/user/allTopicList";
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
	@RequestMapping(params="method=allComment",method=RequestMethod.GET)
	public String allAuditComment(PageForm pageForm,ModelMap model,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

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
				
			}

			pageView.setQueryResult(qr);
			
			
			model.addAttribute("pageView", pageView);
		}
		

		return "jsp/user/allCommentList";
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
	@RequestMapping(params="method=allReply",method=RequestMethod.GET)
	public String allAuditReply(PageForm pageForm,ModelMap model,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
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
				
			}

			pageView.setQueryResult(qr);
			
			
			model.addAttribute("pageView", pageView);
		}
		

		return "jsp/user/allReplyList";
	}
	
	
	/**
	 * 更新头像
	 * @param model
	 * @param imgFile
	 * @param id 用户Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=updateAvatar",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String updateAvatar(ModelMap model,MultipartFile imgFile,Long id,
			HttpServletRequest request,HttpServletResponse response)
			throws Exception {	
		
		Map<String,String> error = new HashMap<String,String>();//错误

	
		User user = userService.findUserById(id);
		
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
			
			if(imgFile != null && !imgFile.isEmpty()){
				//当前文件名称
				String fileName = imgFile.getOriginalFilename();
				
				//文件大小
				Long size = imgFile.getSize();
				

				
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
					boolean authentication = fileManage.validateFileSuffix(imgFile.getOriginalFilename(),formatList);
			
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

						BufferedImage bufferImage = ImageIO.read(imgFile.getInputStream());  
			            //获取图片的宽和高  
			            int srcWidth = bufferImage.getWidth();  
			            int srcHeight = bufferImage.getHeight();  
						
						//取得文件后缀
						String suffix = fileManage.getExtension(fileName).toLowerCase();
						//构建文件名称
						newFileName = UUIDUtil.getUUID32()+ "." + suffix;
						
						if(srcWidth <=200 && srcHeight <=200){	
							//保存文件
							fileManage.writeFile(pathDir, newFileName,imgFile.getBytes());
							
							if(srcWidth <=100 && srcHeight <=100){
								//保存文件
								fileManage.writeFile(pathDir_100, newFileName,imgFile.getBytes());
							}else{
								//生成100*100缩略图
								thumbnailManage.createImage(imgFile.getInputStream(),PathUtil.path()+File.separator+pathDir_100+newFileName,suffix,100,100);
							}
						}else{
							//生成200*200缩略图
							thumbnailManage.createImage(imgFile.getInputStream(),PathUtil.path()+File.separator+pathDir+newFileName,suffix,x,y,width,height,200,200);

							//生成100*100缩略图
							thumbnailManage.createImage(imgFile.getInputStream(),PathUtil.path()+File.separator+pathDir_100+newFileName,suffix,x,y,width,height,100,100);
    
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
		return JsonUtils.toJSONString(returnValue);
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
	@RequestMapping(params="method=payment",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String payment(ModelMap model,Long id,Integer type,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
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
					String paymentRunningNumber_remark = "";//备注

					String _paymentRunningNumberAmount = request.getParameter("paymentRunningNumberAmount");
					String _paymentRunningNumber = request.getParameter("paymentRunningNumber");
					String _paymentRunningNumber_remark = request.getParameter("paymentRunningNumber_remark");
					
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
					if(_paymentRunningNumber_remark != null){
						try {	
							paymentRunningNumber_remark = URLDecoder.decode(_paymentRunningNumber_remark,"utf-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
						//	e.printStackTrace();
							if (logger.isErrorEnabled()) {
					            logger.error("备注编码错误",e);
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
									paymentLog.setParameterId(paymentVerificationLog.getParameterId());//参数Id 
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
									
									
									
									returnValue.put("success", "true");
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
					String deposit_symbol = null;//符号
					BigDecimal deposit = new BigDecimal("0");//预存款
					String deposit_remark = "";//备注

					String _deposit_symbol = request.getParameter("deposit_symbol");
					String _deposit = request.getParameter("deposit");
					String _deposit_remark = request.getParameter("deposit_remark");
					
					if(_deposit_symbol != null){
						try {
							deposit_symbol = URLDecoder.decode(_deposit_symbol,"utf-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
						//	e.printStackTrace();
							if (logger.isErrorEnabled()) {
					            logger.error("预存款符号编码错误",e);
					        }
						}
						if("+".equals(deposit_symbol)){//增加
							deposit_symbol = "+";
						}else{//减少
							deposit_symbol = "-";
						}
					}
					if(_deposit_remark != null){
						try {	
							deposit_remark = URLDecoder.decode(_deposit_remark,"utf-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
						//	e.printStackTrace();
							if (logger.isErrorEnabled()) {
					            logger.error("预存款编码错误",e);
					        }
						}
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
						paymentLog.setParameterId(user.getId());//参数Id 
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
						
						
						returnValue.put("success", "true");
					}
					
					
				}else if(type.equals(3)){//3:积分
					
					String point_symbol = null;//符号
					Long point = 0L;//积分
					String point_remark = "";//备注

					String _point_symbol = request.getParameter("point_symbol");
					String _point = request.getParameter("point");
					String _point_remark = request.getParameter("point_remark");
					
					if(_point_symbol != null){
						try {
							point_symbol = URLDecoder.decode(_point_symbol,"utf-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
						//	e.printStackTrace();
							if (logger.isErrorEnabled()) {
					            logger.error("积分符号编码错误",e);
					        }
						}
						if("+".equals(point_symbol)){//增加
							point_symbol = "+";
						}else{//减少
							point_symbol = "-";
						}
					}
					if(_point_remark != null){
						try {	
							point_remark = URLDecoder.decode(_point_remark,"utf-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
						//	e.printStackTrace();
							if (logger.isErrorEnabled()) {
					            logger.error("积分备注编码错误",e);
					        }
						}
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
						
						
						returnValue.put("success", "true");
					}
				}
			}
		}
		
		
		
		returnValue.put("error", error);
		return JsonUtils.toJSONString(returnValue);
	}
}
