package cms.web.action.membershipCard;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.membershipCard.MembershipCardGiftItem;
import cms.bean.membershipCard.MembershipCardGiftTask;
import cms.bean.membershipCard.RestrictionGroup;
import cms.bean.user.User;
import cms.bean.user.UserRole;
import cms.service.membershipCard.MembershipCardGiftTaskService;
import cms.service.setting.SettingService;
import cms.service.user.UserRoleService;
import cms.service.user.UserService;
import cms.utils.JsonUtils;
import cms.utils.Verification;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.user.UserManage;

/**
 * 会员卡赠送任务管理
 *
 */
@Controller
@RequestMapping("/control/membershipCardGiftTask/manage") 
public class MembershipCardGiftTaskManageAction {

	@Resource MembershipCardGiftTaskService membershipCardGiftTaskService;
	@Resource UserRoleService userRoleService;
	@Resource SettingService settingService;
	@Resource FileManage fileManage;
	@Resource UserService userService;
	@Resource UserManage userManage;
	@Resource(name = "membershipCardGiftTaskValidator") 
	private Validator validator; 
	@Resource MessageSource messageSource;
	@Resource MembershipCardGiftTaskManage membershipCardGiftTaskManage;
	
	/**
	 * 查询会员卡赠送项(获赠用户)
	 * @param pageForm
	 * @param model
	 * @param membershipCardGiftTaskId 会员卡赠送任务Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=membershipCardGiftItemList",method=RequestMethod.GET)
	public String queryMembershipCardGiftItem(PageForm pageForm,ModelMap model,Long membershipCardGiftTaskId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		
		if(membershipCardGiftTaskId != null && membershipCardGiftTaskId >0L){
			
			PageView<MembershipCardGiftItem> pageView = new PageView<MembershipCardGiftItem>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			//当前页
			int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
			
			//调用分页算法类
			QueryResult<MembershipCardGiftItem> qr = membershipCardGiftTaskService.findMembershipCardGiftItemPage(membershipCardGiftTaskId, firstindex, pageView.getMaxresult());
			
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(MembershipCardGiftItem membershipCardGiftItem :qr.getResultlist()){
					if(membershipCardGiftItem.getRestriction() != null && !"".equals(membershipCardGiftItem.getRestriction().trim())){
						RestrictionGroup restrictionGroup= JsonUtils.toObject(membershipCardGiftItem.getRestriction(), RestrictionGroup.class);
						if(restrictionGroup != null){
							membershipCardGiftItem.setRestrictionGroup(restrictionGroup);
						}
					}
					User user = userManage.query_cache_findUserByUserName(membershipCardGiftItem.getUserName());
					if(user != null){
						membershipCardGiftItem.setAccount(user.getAccount());
						membershipCardGiftItem.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							membershipCardGiftItem.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
							membershipCardGiftItem.setAvatarName(user.getAvatarName());
						}		
					}
				}
			}
			pageView.setQueryResult(qr);
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
			
		}else{
			error.put("membershipCardGiftTaskId", "会员卡赠送任务Id不能为空");
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	/**
	 * 查询会员卡赠送任务
	 * @param pageForm
	 * @param model
	 * @param membershipCardGiftTaskId 会员卡赠送任务Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=view",method=RequestMethod.GET)
	public String view(PageForm pageForm,ModelMap model,Long membershipCardGiftTaskId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		if(membershipCardGiftTaskId != null && membershipCardGiftTaskId >0L){
			MembershipCardGiftTask membershipCardGiftTask = membershipCardGiftTaskService.findById(membershipCardGiftTaskId);
			if(membershipCardGiftTask != null){
				if(membershipCardGiftTask.getRestriction() != null && !"".equals(membershipCardGiftTask.getRestriction().trim())){
					RestrictionGroup restrictionGroup= JsonUtils.toObject(membershipCardGiftTask.getRestriction(), RestrictionGroup.class);
					if(restrictionGroup != null){
						membershipCardGiftTask.setRestrictionGroup(restrictionGroup);
					}
				}
				
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,membershipCardGiftTask));
			}
			
		}else{
			error.put("membershipCardGiftTaskId", "会员卡赠送任务Id不能为空");
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	
	/**
	 * 会员卡赠送任务管理 添加界面显示
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model,MembershipCardGiftTask membershipCardGiftTask
			) throws Exception {
		//错误
		Map<String,Object> returnValue = new HashMap<String,Object>();
		//查询所有角色
		List<UserRole> userRoleList = userRoleService.findAllRole();
		if(userRoleList != null && userRoleList.size() >0){
	        Iterator<UserRole> iterator = userRoleList.iterator();
	        while (iterator.hasNext()) {
	        	UserRole userRole = iterator.next();
	        	if(userRole.getDefaultRole()){//如果是默认角色
	                iterator.remove();
	            }
	        }
	        for(UserRole userRole : userRoleList){
	        	userRole.setSelected(true);//默认选中第一个
	        	break;
	        }
		}
		
		returnValue.put("userRoleList", userRoleList);
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
	}
	
	
	/**
	 * 会员卡赠送任务管理 添加
	 * @param model
	 * @param formbean
	 * @param result
	 * @param _expirationDate_start 任务有效期范围起始
	 * @param _expirationDate_end 任务有效期范围结束
	 * @param _registrationTime_start 用户注册时间范围起始
	 * @param _registrationTime_end 用户注册时间范围结束
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,MembershipCardGiftTask formbean,BindingResult result,
			String _expirationDate_start,String _expirationDate_end,String _registrationTime_start,String _registrationTime_end,
			HttpServletRequest request) throws Exception {

		
		Map<String,String> error = new HashMap<String,String>();//错误
		MembershipCardGiftTask membershipCardGiftTask = new MembershipCardGiftTask();
		if(formbean.getName() != null && !"".equals(formbean.getName().trim())){
			if(formbean.getName().trim().length() >150){
				error.put("name", "不能大于150个字符");
			}else{
				membershipCardGiftTask.setName(formbean.getName().trim());//名称
			}
		}else{
			error.put("name", "名称不能为空");
		}
		
		if(formbean.getUserRoleId() != null && !"".equals(formbean.getUserRoleId().trim())){
			//查询角色
			UserRole userRole = userRoleService.findRoleById(formbean.getUserRoleId().trim());
			if(userRole != null){
				membershipCardGiftTask.setUserRoleId(userRole.getId());
				membershipCardGiftTask.setUserRoleName(userRole.getName());
			}else{
				error.put("userRoleId", "角色不存在");
			}
		}else{
			error.put("userRoleId", "角色不能为空");
		}
		if(formbean.getType() != null){
			membershipCardGiftTask.setType(formbean.getType());
		}else{
			error.put("type", "任务类型不能为空");
		}
		
		if(formbean.getType().equals(10)){//长期任务
			//任务有效期范围起始
			if(_expirationDate_start != null && !"".equals(_expirationDate_start.trim())){
				boolean start_verification = Verification.isTime_second(_expirationDate_start.trim());
				if(start_verification){
					DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					membershipCardGiftTask.setExpirationDate_start(dd.parse(_expirationDate_start.trim()));
				}else{
					error.put("expirationDate_start", "请填写正确的日期");
				}
			}
			//任务有效期范围结束
			if(_expirationDate_end != null && !"".equals(_expirationDate_end.trim())){
				boolean end_verification = Verification.isTime_second(_expirationDate_end.trim());
				if(end_verification){
					DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					membershipCardGiftTask.setExpirationDate_end(dd.parse(_expirationDate_end.trim()));
				}else{
					error.put("expirationDate_end", "请填写正确的日期");
				}
			}
			//比较时间
			Calendar start=Calendar.getInstance();//发表时间 起始  
	        Calendar end=Calendar.getInstance();//发表时间 结束
	        if(membershipCardGiftTask.getExpirationDate_start() != null){
	        	start.setTime(membershipCardGiftTask.getExpirationDate_start());   
	        }
	        if(membershipCardGiftTask.getExpirationDate_end() != null){
	        	end.setTime(membershipCardGiftTask.getExpirationDate_end());   
	        }
	        
			if(membershipCardGiftTask.getExpirationDate_start() != null && membershipCardGiftTask.getExpirationDate_end() != null){
	        	int results =start.compareTo(end);//起始时间与结束时间比较
	        	if(results > 0 ){//起始时间比结束时间大
	        		error.put("expirationDate_start", "起始时间不能比结束时间大");
	        	}
			}
		}
		if(membershipCardGiftTask.getExpirationDate_start() == null){//空时间则填充默认值
			//默认时间  年,月,日,时,分,秒,毫秒    
            DateTime defaultTime = new DateTime(1970, 1, 1, 0, 0, 0);// 1970年1月1日0点0分0秒
            membershipCardGiftTask.setExpirationDate_start(defaultTime.toDate());
		}
		if(membershipCardGiftTask.getExpirationDate_end() == null){//空时间则填充默认值
			//默认时间  年,月,日,时,分,秒,毫秒    
            DateTime defaultTime = new DateTime(2999, 1, 1, 0, 0, 0);// 2999年1月1日0点0分0秒
            membershipCardGiftTask.setExpirationDate_end(defaultTime.toDate());
		}
		
		
		
		RestrictionGroup restrictionGroup = new RestrictionGroup();
		
		
		//用户注册时间范围起始
		if(_registrationTime_start != null && !"".equals(_registrationTime_start.trim())){
			boolean start_verification = Verification.isTime_second(_registrationTime_start.trim());
			if(start_verification){
				DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				restrictionGroup.setRegistrationTime_start(dd.parse(_registrationTime_start.trim()));
			}else{
				error.put("registrationTime_start", "请填写正确的日期");
			}
		}
		//用户注册时间范围结束
		if(_registrationTime_end != null && !"".equals(_registrationTime_end.trim())){
			boolean end_verification = Verification.isTime_second(_registrationTime_end.trim());
			if(end_verification){
				DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				restrictionGroup.setRegistrationTime_end(dd.parse(_registrationTime_end.trim()));
			}else{
				error.put("registrationTime_end", "请填写正确的日期");
			}
		}
		//比较时间
		Calendar start=Calendar.getInstance();//发表时间 起始  
        Calendar end=Calendar.getInstance();//发表时间 结束
        if(restrictionGroup.getRegistrationTime_start() != null){
        	start.setTime(restrictionGroup.getRegistrationTime_start());   
        }
        if(restrictionGroup.getRegistrationTime_end() != null){
        	end.setTime(restrictionGroup.getRegistrationTime_end());   
        }
        
		if(restrictionGroup.getRegistrationTime_start() != null && restrictionGroup.getRegistrationTime_end() != null){
        	int results =start.compareTo(end);//起始时间与结束时间比较
        	if(results > 0 ){//起始时间比结束时间大
        		error.put("registrationTime_start", "起始时间不能比结束时间大");
        	}
		}
		if(formbean.getRestrictionGroup().getTotalPoint() != null && formbean.getRestrictionGroup().getTotalPoint() >0L){
			restrictionGroup.setTotalPoint(formbean.getRestrictionGroup().getTotalPoint());
		}
		
		membershipCardGiftTask.setRestriction(JsonUtils.toJSONString(restrictionGroup));
		
		
		
		if(formbean.getDuration() != null){
			if(formbean.getDuration() <=0){
				error.put("duration", "时长必须大于0");
			}else{
				if(formbean.getDuration() >99999999){
					error.put("duration", "数字最大为8位");
				}else{
					membershipCardGiftTask.setDuration(formbean.getDuration());
				}
			}
		}else{
			error.put("duration", "时长不能为空");
		}
		
		//时长单位
		if(formbean.getUnit() != null){
			membershipCardGiftTask.setUnit(formbean.getUnit());
		}else{
			error.put("unit", "时长单位不能为空");
		}
		
		membershipCardGiftTask.setCreateDate(new Date());//创建时间
		membershipCardGiftTask.setEnable(formbean.isEnable());
		
		
		
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

		
		if(error.size() ==0){
			membershipCardGiftTaskService.saveMembershipCardGiftTask(membershipCardGiftTask);
			
			if(membershipCardGiftTask.getType().equals(20)){//一次性任务
				membershipCardGiftTask.setRestrictionGroup(restrictionGroup);
				membershipCardGiftTaskManage.async_executionMembershipCardGiftTask(membershipCardGiftTask);
			}
			
			
		}
		
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	
	/**
	 * 会员卡赠送任务管理 修改界面显示
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(ModelMap model,Long membershipCardGiftTaskId
			) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		if(membershipCardGiftTaskId != null && membershipCardGiftTaskId >0L){
			MembershipCardGiftTask membershipCardGiftTask = membershipCardGiftTaskService.findById(membershipCardGiftTaskId);
			if(membershipCardGiftTask != null){
				if(membershipCardGiftTask.getExpirationDate_start() != null){//默认初始时间则设为空
					//默认时间  年,月,日,时,分,秒,毫秒    
		            DateTime defaultTime = new DateTime(1970, 1, 1, 0, 0, 0);// 1970年1月1日0点0分0秒
		            
		            if(defaultTime.toLocalDateTime().isEqual(new DateTime(membershipCardGiftTask.getExpirationDate_start()).toLocalDateTime())){
		            	membershipCardGiftTask.setExpirationDate_start(null);
		            }
				}
				if(membershipCardGiftTask.getExpirationDate_end() != null){//默认初始时间则设为空
					//默认时间  年,月,日,时,分,秒,毫秒    
					 DateTime defaultTime = new DateTime(2999, 1, 1, 0, 0, 0);// 2999年1月1日0点0分0秒
		            
					 if(defaultTime.toLocalDateTime().isEqual(new DateTime(membershipCardGiftTask.getExpirationDate_end()).toLocalDateTime())){
		            	membershipCardGiftTask.setExpirationDate_end(null);
		            }
				}
				
				
				if(membershipCardGiftTask.getRestriction() != null && !"".equals(membershipCardGiftTask.getRestriction().trim())){
					RestrictionGroup restrictionGroup= JsonUtils.toObject(membershipCardGiftTask.getRestriction(), RestrictionGroup.class);
					if(restrictionGroup != null){
						membershipCardGiftTask.setRestrictionGroup(restrictionGroup);
					}
				}
				returnValue.put("membershipCardGiftTask",membershipCardGiftTask);
				
				//查询所有角色
				List<UserRole> userRoleList = userRoleService.findAllRole();
				if(userRoleList != null && userRoleList.size() >0){
			        Iterator<UserRole> iterator = userRoleList.iterator();
			        while (iterator.hasNext()) {
			        	UserRole userRole = iterator.next();
			        	if(membershipCardGiftTask.getUserRoleId() != null && userRole.getId().equals(membershipCardGiftTask.getUserRoleId())){
			        		userRole.setSelected(true);
			        	}
			        	if(userRole.getDefaultRole()){//如果是默认角色
			                iterator.remove();
			            }
			        }
			        
				}
				returnValue.put("userRoleList", userRoleList);
			}else{
				error.put("membershipCardGiftTask", "会员卡赠送任务不存在");
			}
		}else{
			error.put("membershipCardGiftTaskId", "会员卡赠送任务Id不能为空");
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}	
	}
	
	/**
	 * 会员卡赠送任务管理 修改界面显示
	 * @param model
	 * @param membershipCardGiftTaskId 会员卡赠送任务Id
	 * @param _expirationDate_start 任务有效期范围起始
	 * @param _expirationDate_end 任务有效期范围结束
	 * @param _registrationTime_start 用户注册时间范围起始
	 * @param _registrationTime_end 用户注册时间范围结束
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	public String edit(ModelMap model,MembershipCardGiftTask formbean,BindingResult result,Long membershipCardGiftTaskId,
			String _expirationDate_start,String _expirationDate_end,String _registrationTime_start,String _registrationTime_end,
			HttpServletRequest request) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();//错误
		
		MembershipCardGiftTask membershipCardGiftTask = new MembershipCardGiftTask();
		if(membershipCardGiftTaskId != null && membershipCardGiftTaskId >0L){
			MembershipCardGiftTask old_membershipCardGiftTask = membershipCardGiftTaskService.findById(membershipCardGiftTaskId);
			if(old_membershipCardGiftTask != null){
				if(old_membershipCardGiftTask.getType().equals(10)){//10:长期
					
					if(formbean.getName() != null && !"".equals(formbean.getName().trim())){
						if(formbean.getName().trim().length() >150){
							error.put("name", "不能大于150个字符");
						}else{
							membershipCardGiftTask.setName(formbean.getName().trim());//名称
						}
					}else{
						error.put("name", "名称不能为空");
					}
					
					if(formbean.getUserRoleId() != null && !"".equals(formbean.getUserRoleId().trim())){
						//查询角色
						UserRole userRole = userRoleService.findRoleById(formbean.getUserRoleId().trim());
						if(userRole != null){
							membershipCardGiftTask.setUserRoleId(userRole.getId());
							membershipCardGiftTask.setUserRoleName(userRole.getName());
						}else{
							error.put("userRoleId", "角色不存在");
						}
					}else{
						error.put("userRoleId", "角色不能为空");
					}
					
					if(formbean.getType().equals(10)){//长期任务
						//任务有效期范围起始
						if(_expirationDate_start != null && !"".equals(_expirationDate_start.trim())){
							boolean start_verification = Verification.isTime_second(_expirationDate_start.trim());
							if(start_verification){
								DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								membershipCardGiftTask.setExpirationDate_start(dd.parse(_expirationDate_start.trim()));
							}else{
								error.put("expirationDate_start", "请填写正确的日期");
							}
						}
						//任务有效期范围结束
						if(_expirationDate_end != null && !"".equals(_expirationDate_end.trim())){
							boolean end_verification = Verification.isTime_second(_expirationDate_end.trim());
							if(end_verification){
								DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								membershipCardGiftTask.setExpirationDate_end(dd.parse(_expirationDate_end.trim()));
							}else{
								error.put("expirationDate_end", "请填写正确的日期");
							}
						}
						//比较时间
						Calendar start=Calendar.getInstance();//发表时间 起始  
				        Calendar end=Calendar.getInstance();//发表时间 结束
				        if(membershipCardGiftTask.getExpirationDate_start() != null){
				        	start.setTime(membershipCardGiftTask.getExpirationDate_start());   
				        }
				        if(membershipCardGiftTask.getExpirationDate_end() != null){
				        	end.setTime(membershipCardGiftTask.getExpirationDate_end());   
				        }
				        
						if(membershipCardGiftTask.getExpirationDate_start() != null && membershipCardGiftTask.getExpirationDate_end() != null){
				        	int results =start.compareTo(end);//起始时间与结束时间比较
				        	if(results > 0 ){//起始时间比结束时间大
				        		error.put("expirationDate_start", "起始时间不能比结束时间大");
				        	}
						}
					}
					if(membershipCardGiftTask.getExpirationDate_start() == null){//空时间则填充默认值
						//默认时间  年,月,日,时,分,秒,毫秒    
			            DateTime defaultTime = new DateTime(1970, 1, 1, 0, 0, 0);// 1970年1月1日0点0分0秒
			            membershipCardGiftTask.setExpirationDate_start(defaultTime.toDate());
					}
					if(membershipCardGiftTask.getExpirationDate_end() == null){//空时间则填充默认值
						//默认时间  年,月,日,时,分,秒,毫秒    
			            DateTime defaultTime = new DateTime(2999, 1, 1, 0, 0, 0);// 2999年1月1日0点0分0秒
			            membershipCardGiftTask.setExpirationDate_end(defaultTime.toDate());
					}
					
					RestrictionGroup restrictionGroup = new RestrictionGroup();
					
					//用户注册时间范围起始
					if(_registrationTime_start != null && !"".equals(_registrationTime_start.trim())){
						boolean start_verification = Verification.isTime_second(_registrationTime_start.trim());
						if(start_verification){
							DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							restrictionGroup.setRegistrationTime_start(dd.parse(_registrationTime_start.trim()));
						}else{
							error.put("registrationTime_start", "请填写正确的日期");
						}
					}
					//用户注册时间范围结束
					if(_registrationTime_end != null && !"".equals(_registrationTime_end.trim())){
						boolean end_verification = Verification.isTime_second(_registrationTime_end.trim());
						if(end_verification){
							DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							restrictionGroup.setRegistrationTime_end(dd.parse(_registrationTime_end.trim()));
						}else{
							error.put("registrationTime_end", "请填写正确的日期");
						}
					}
					//比较时间
					Calendar start=Calendar.getInstance();//发表时间 起始  
			        Calendar end=Calendar.getInstance();//发表时间 结束
			        if(restrictionGroup.getRegistrationTime_start() != null){
			        	start.setTime(restrictionGroup.getRegistrationTime_start());   
			        }
			        if(restrictionGroup.getRegistrationTime_end() != null){
			        	end.setTime(restrictionGroup.getRegistrationTime_end());   
			        }
			        
					if(restrictionGroup.getRegistrationTime_start() != null && restrictionGroup.getRegistrationTime_end() != null){
			        	int results =start.compareTo(end);//起始时间与结束时间比较
			        	if(results > 0 ){//起始时间比结束时间大
			        		error.put("registrationTime_start", "起始时间不能比结束时间大");
			        	}
					}
					
					if(formbean.getRestrictionGroup().getTotalPoint() != null && formbean.getRestrictionGroup().getTotalPoint() >0L){
						restrictionGroup.setTotalPoint(formbean.getRestrictionGroup().getTotalPoint());
					}
					
					membershipCardGiftTask.setRestriction(JsonUtils.toJSONString(restrictionGroup));
					
					
					
					if(formbean.getDuration() != null){
						if(formbean.getDuration() <=0){
							error.put("duration", "时长必须大于0");
						}else{
							if(formbean.getDuration() >99999999){
								error.put("duration", "数字最大为8位");
							}else{
								membershipCardGiftTask.setDuration(formbean.getDuration());
							}
						}
					}else{
						error.put("duration", "时长不能为空");
					}
					
					//时长单位
					if(formbean.getUnit() != null){
						membershipCardGiftTask.setUnit(formbean.getUnit());
					}else{
						error.put("unit", "时长单位不能为空");
					}
					membershipCardGiftTask.setId(membershipCardGiftTaskId);
					membershipCardGiftTask.setEnable(formbean.isEnable());
					
					
					
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
					
					
					if(error.size() ==0){
						membershipCardGiftTaskService.updateMembershipCardGiftTask(membershipCardGiftTask);
					}
				}else{//一次性
					error.put("membershipCardGiftTask", "一次性类型赠送任务不允许修改");
				}
			}
		}else{
			error.put("membershipCardGiftTaskId", "会员卡赠送任务Id不能为空");
		}
		

		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	/**
	 * 会员卡赠送任务管理   删除
	 * @param model
	 * @param membershipCardGiftTaskId 会员卡赠送任务Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	public String delete(ModelMap model,Long membershipCardGiftTaskId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		if(membershipCardGiftTaskId != null && membershipCardGiftTaskId >0L){
			int i = membershipCardGiftTaskService.deleteMembershipCardGiftTask(membershipCardGiftTaskId);	
			
			//清除缓存
			//membershipCardManage.delete_cache_findById(membershipCardId);
			//membershipCardManage.delete_cache_findSpecificationByMembershipCardId(membershipCardId);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("membershipCardGiftTaskId", "会员卡赠送任务Id不能为空");
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
}
