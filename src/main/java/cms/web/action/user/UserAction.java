package cms.web.action.user;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.user.User;
import cms.bean.user.UserCustom;
import cms.bean.user.UserGrade;
import cms.bean.user.UserInputValue;
import cms.service.setting.SettingService;
import cms.service.user.UserCustomService;
import cms.service.user.UserGradeService;
import cms.service.user.UserService;
import cms.utils.JsonUtils;
import cms.utils.Verification;
import cms.web.action.fileSystem.FileManage;

/**
 * 用户管理 分页显示 查询结果显示
 *
 */
@Controller
public class UserAction {
	//注入业务bean
	@Resource(name="userServiceBean")
	private UserService userService;

	//注入业务bean
	@Resource UserGradeService userGradeService;
	
	@Resource UserCustomService userCustomService;
	
	@Resource SettingService settingService;
	@Resource UserManage userManage;
	@Resource FileManage fileManage;
	
	/**
	 * 用户列表
	 * @param formbean
	 * @param pageForm
	 * @param visible  null或true:正常页面  false:回收站
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/user/list") 
	public String execute(User formbean,PageForm pageForm,Boolean visible,ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
	
		
		
		//调用分页算法代码
		PageView<User> pageView = new PageView<User>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		String param = "";//sql参数
		List<Object> paramValue = new ArrayList<Object>();//sql参数值
		
		
		if(visible != null && visible == false){//回收站
			param = " o.state>? ";
			paramValue.add(2);
		}else{//正常页面
			param = " o.state<=? ";
			paramValue.add(2);
		}
		
		QueryResult<User> qr = userService.findUserByCondition(param,paramValue,firstIndex, pageView.getMaxresult(),false);
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		List<UserGrade> userGradeList = userGradeService.findAllGrade();
		if(userGradeList != null && userGradeList.size() >0){
			for(User user : pageView.getRecords()){//取得所有用户		
				for(UserGrade userGrade : userGradeList){//取得所有等级 
					if(user.getPoint() >= userGrade.getNeedPoint()){
						user.setGradeName(userGrade.getName());//将等级值设进等级参数里
						break;
					}
				} 
				
			}
		}
		
		if(pageView.getRecords() != null && pageView.getRecords().size() >0){
			for(User user : pageView.getRecords()){//取得所有用户
				if(user.getType() >10){
					user.setPlatformUserId(userManage.platformUserIdToThirdPartyUserId(user.getPlatformUserId()));
				}
				
			}
			//仅显示指定字段
			List<User> userViewList = new ArrayList<User>();
			for(User user : pageView.getRecords()){//取得所有用户
				User userView = new User();

				userView.setId(user.getId());
				userView.setUserName(user.getUserName());
				userView.setAccount(user.getAccount());
				userView.setNickname(user.getNickname());
				userView.setCancelAccountTime(user.getCancelAccountTime());
				userView.setAllowUserDynamic(user.getAllowUserDynamic());
				userView.setEmail(user.getEmail());
				userView.setIssue(user.getIssue());
				userView.setMobile(user.getMobile());
				userView.setId(user.getId());
				userView.setRealNameAuthentication(user.isRealNameAuthentication());
				userView.setRegistrationDate(user.getRegistrationDate());
				userView.setRemarks(user.getRemarks());
				userView.setPoint(user.getPoint());
				userView.setDeposit(user.getDeposit());
				userView.setType(user.getType());
				userView.setPlatformUserId(user.getPlatformUserId());
				userView.setState(user.getState());
				userView.setUserVersion(user.getUserVersion());
				userView.setUserRoleNameList(user.getUserRoleNameList());
				userView.setGradeId(user.getGradeId());
				userView.setGradeName(user.getGradeName());
				userView.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
				userView.setAvatarName(user.getAvatarName());
				userViewList.add(userView);
			}
			pageView.setRecords(userViewList);
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
	}
	
	
	/**
	 * 搜索用户列表
	 * @param pageForm
	 * @param model
	 * @param queryType 查询类型
	 * @param account 账号
	 * @param start_deposit 起始预存款
	 * @param end_deposit 结束预存款 
	 * @param start_point 起始积分
	 * @param end_point 结束积分
	 * @param start_buyTotalAmount 起始已购买商品总金额
	 * @param end_buyTotalAmount 结束已购买商品总金额
	 * @param start_registrationDate 起始注册日期
	 * @param end_registrationDate 结束注册日期
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/user/search") 
	public String search(ModelMap model,PageForm pageForm,
			Integer searchType,String account,
			String start_point,String end_point,
			String start_registrationDate,String end_registrationDate,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		if(searchType == null){//如果查询类型为空，则默认为用户名查询
			searchType = 1;
		}
		//错误
		Map<String,String> error = new HashMap<String,String>();

		String _userName = null;
		Integer _start_point = null;//积分
		Integer _end_point = null;//积分
		Date _start_registrationDate = null;//注册日期
		Date _end_registrationDate= null;//注册日期
		
		//验证参数
		if(searchType.equals(1)){//用户名
			if(account != null && !"".equals(account.trim())){
				User user = userService.findUserByAccount(account.trim());
				if(user != null){
					_userName = user.getUserName();
				}else{
					error.put("account", "账号不存在");
				}	
			}else{
				error.put("account", "请填写账号");
			}	
		}
		if(searchType.equals(2)){//筛选条件
			
			//积分
			if(start_point != null && !"".equals(start_point.trim())){
				boolean start_point_verification = Verification.isPositiveInteger(start_point.trim());//正整数
				if(start_point_verification){
					_start_point = Integer.parseInt(start_point.trim());
				}else{
					error.put("start_point", "请填写正整数");
				}
			}
			if(end_point != null && !"".equals(end_point.trim())){
				boolean end_point_verification = Verification.isPositiveInteger(end_point.trim());//正整数
				if(end_point_verification){
					_end_point = Integer.parseInt(end_point.trim());
				}else{
					error.put("end_point", "请填写正整数");
				}
			}
			if(_start_point != null && _end_point != null){
				if(_start_point > _end_point){
					error.put("start_point", "起始积分不能大于结束积分");
				}
			}

			
			
			
			if(start_registrationDate != null && !"".equals(start_registrationDate.trim())){
				boolean start_registrationDateVerification = Verification.isTime_minute(start_registrationDate.trim());
				if(start_registrationDateVerification){
					DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm");		
					_start_registrationDate = dd.parse(start_registrationDate.trim());
				}else{
					error.put("start_registrationDate", "请填写正确的日期");
				}
			}
			if(end_registrationDate != null && !"".equals(end_registrationDate.trim())){
				boolean end_registrationDateVerification = Verification.isTime_minute(end_registrationDate.trim());
				if(end_registrationDateVerification){
					DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm");		
					_end_registrationDate = dd.parse(end_registrationDate.trim());
				}else{
					error.put("end_registrationDate", "请填写正确的日期");
				}
			}
			//比较时间
			Calendar start=Calendar.getInstance();//时间 起始  
	        Calendar end=Calendar.getInstance();//时间 结束
	        if(_start_registrationDate != null){
	        	start.setTime(_start_registrationDate);   
	        }
	        if(_end_registrationDate != null){
	        	end.setTime(_end_registrationDate);   
	        }
			if(_start_registrationDate != null && _end_registrationDate != null){
	        	int result =start.compareTo(end);//起始时间与结束时间比较
	        	if(result > 0 ){//起始时间比结束时间大
	        		error.put("start_registrationDate", "起始时间不能比结束时间大");
	        	}
			}
		}
		
		//自定义参数
		List<UserCustom> userCustomList = userCustomService.findAllUserCustom();
		if(userCustomList != null && userCustomList.size() >0){		
			Iterator <UserCustom> it = userCustomList.iterator();  
			while(it.hasNext()){  
				UserCustom userCustom = it.next();
				if(userCustom.isVisible() == false){//如果不显示
					it.remove();  
					continue;
				}
				if(userCustom.isSearch() == false){//如果不可搜索
					it.remove();  
					continue;
				}
				if(userCustom.getChooseType().equals(1) || (userCustom.getChooseType().equals(5))){//1.输入框  5.文本域不可搜索
					it.remove();  
					continue;
				}
				if(userCustom.getValue() != null && !"".equals(userCustom.getValue().trim())){
					LinkedHashMap<String,String> itemValue = JsonUtils.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});
					userCustom.setItemValue(itemValue);
				}
				
			}
		}
		
		if(userCustomList != null && userCustomList.size() >0){	
			for(UserCustom userCustom : userCustomList){
				//用户自定义注册功能项用户输入值集合
				List<UserInputValue> userInputValueList = new ArrayList<UserInputValue>();
				
				
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
						userCustom.setUserInputValueList(userInputValueList);
						
						if(userCustom_value.trim().length() > userCustom.getMaxlength()){
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
					}	
					
				}else if(userCustom.getChooseType().equals(2)){//2.单选按钮
					String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());
					
					if(userCustom_value != null && !"".equals(userCustom_value.trim())){
						
						String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
						if(itemValue != null ){
							UserInputValue userInputValue = new UserInputValue();
							userInputValue.setUserCustomId(userCustom.getId());
							userInputValue.setOptions(userCustom_value.trim());
							userInputValueList.add(userInputValue);
							userCustom.setUserInputValueList(userInputValueList);	
						}
						
					}			
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
					}
					userCustom.setUserInputValueList(userInputValueList);	
				}else if(userCustom.getChooseType().equals(5)){// 5.文本域
					String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());
					
					if(userCustom_value != null && !"".equals(userCustom_value.trim())){
						UserInputValue userInputValue = new UserInputValue();
						userInputValue.setUserCustomId(userCustom.getId());
						userInputValue.setContent(userCustom_value);
						userInputValueList.add(userInputValue);
						userCustom.setUserInputValueList(userInputValueList);
					}
				}
				
			}
		}
		
		//调用分页算法代码
		PageView<User> pageView = new PageView<User>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		if(error.size() == 0){
			if(searchType.equals(1)){//用户名
				User user = userService.findUserByUserName(_userName);
				
				if(user != null){
					QueryResult<User> qr = new QueryResult<User>();
					List<User> userList = new ArrayList<User>();
					userList.add(user);
					qr.setResultlist(userList);
					qr.setTotalrecord(1L);
					pageView.setQueryResult(qr);
				}
				
			}else if(searchType.equals(2)){//筛选条件
				//用户自定义注册功能项用户输入值集合
				List<UserInputValue> all_userInputValueList = new ArrayList<UserInputValue>();
			
				if(userCustomList != null && userCustomList.size() >0){	
					for(UserCustom userCustom : userCustomList){
						all_userInputValueList.addAll(userCustom.getUserInputValueList());
					}
				}
				if(all_userInputValueList.size() >0){//含有自定义项
					String customParam = "";//自定义参数
					String param = "";//sql参数
					List<Object> paramValue = new ArrayList<Object>();//sql参数值
					Integer customParamGroupCount  = 0;//用户自定义注册功能项参数组数量		
					
					for(UserCustom userCustom : userCustomList){
						if(userCustom.getChooseType().equals(2)){//单选按钮
							List<UserInputValue> userInputValueList = userCustom.getUserInputValueList();
							if(userInputValueList != null && userInputValueList.size() >0){
								customParamGroupCount++;
								customParam += " or ( u.userId =o.id and u.options in(";
								String parameters = "";
										
								for(UserInputValue userInputValue : userInputValueList){
									parameters +=",?";
									paramValue.add(userInputValue.getOptions());

								}
								//删除第一个逗号
								parameters = StringUtils.difference(",", parameters);
								customParam += parameters;
								customParam += "))";
							}
							
						}else if(userCustom.getChooseType().equals(3)){//多选按钮
							List<UserInputValue> userInputValueList = userCustom.getUserInputValueList();
							if(userInputValueList != null && userInputValueList.size() >0){
								customParamGroupCount++;
								customParam += " or ( u.userId =o.id and u.options in(";
								String parameters = "";
										
								for(UserInputValue userInputValue : userInputValueList){
									parameters +=",?";
									paramValue.add(userInputValue.getOptions());

								}
								//删除第一个逗号
								parameters = StringUtils.difference(",", parameters);
								customParam += parameters;
								customParam += "))";
							}
						}else if(userCustom.getChooseType().equals(4)){//下拉列表
							
							List<UserInputValue> userInputValueList = userCustom.getUserInputValueList();
							if(userInputValueList != null && userInputValueList.size() >0){
								customParamGroupCount++;
								customParam += " or ( u.userId =o.id and u.options in(";
								String parameters = "";
										
								for(UserInputValue userInputValue : userInputValueList){
									parameters +=",?";
									paramValue.add(userInputValue.getOptions());

								}
								//删除第一个逗号
								parameters = StringUtils.difference(",", parameters);
								customParam += parameters;
								customParam += "))";
							}
						}
					}
					
					if(_start_point != null){//起始积分
						param += " and o.point >= ? ";
						paramValue.add(_start_point);
					}
					if(_end_point != null){//结束积分
						param += " and o.point <= ? ";
						paramValue.add(_end_point);
					}
					
					
					if(_start_registrationDate != null){//起始时间
						param += " and o.registrationDate >= ? ";
						paramValue.add(_start_registrationDate);
					}
					if(_end_registrationDate != null){//结束时间
						param += " and o.registrationDate <= ? ";
						paramValue.add(_end_registrationDate);
					}
					paramValue.add(customParamGroupCount);
					//删除第一个or
					customParam = StringUtils.difference(" or", customParam);
					
					QueryResult<User> qr = userService.findUserByCustomCondition(param,paramValue,customParam,firstIndex, pageView.getMaxresult(),false);
					//将查询结果集传给分页List
					pageView.setQueryResult(qr);
				}else{
					String param = "";//sql参数
					List<Object> paramValue = new ArrayList<Object>();//sql参数值
					if(_start_point != null){//起始积分
						param += " and o.point >= ? ";
						paramValue.add(_start_point);
					}
					if(_end_point != null){//结束积分
						param += " and o.point <= ? ";
						paramValue.add(_end_point);
					}
					
					if(_start_registrationDate != null){//起始时间
						param += " and o.registrationDate >= ? ";
						paramValue.add(_start_registrationDate);
					}
					if(_end_registrationDate != null){//结束时间
						param += " and o.registrationDate <= ? ";
						paramValue.add(_end_registrationDate);
					}
					
					//删除第一个and
					param = StringUtils.difference(" and", param);
					QueryResult<User> qr = userService.findUserByCondition(param,paramValue,firstIndex, pageView.getMaxresult(),false);
					//将查询结果集传给分页List
					pageView.setQueryResult(qr);
				}
			}
		}
		if(pageView.getRecords() != null && pageView.getRecords().size() >0){
			List<UserGrade> userGradeList = userGradeService.findAllGrade();
			for(User user : pageView.getRecords()){//取得所有用户
				if(user.getType() >10){
					user.setPlatformUserId(userManage.platformUserIdToThirdPartyUserId(user.getPlatformUserId()));
				}
				
				if(userGradeList != null && userGradeList.size() >0){
					for(UserGrade userGrade : userGradeList){//取得所有等级 
						if(user.getPoint() >= userGrade.getNeedPoint()){
							user.setGradeName(userGrade.getName());//将等级值设进等级参数里
							break;
						}
					} 
				}
				if(user.getAvatarPath() != null && !"".contentEquals(user.getAvatarPath().trim())){
					user.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
				}
				
			}
		}
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
		}
	}
}
