package cms.web.action.message;


import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
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
import cms.bean.message.SubscriptionSystemNotify;
import cms.bean.message.SystemNotify;
import cms.bean.staff.SysUsers;
import cms.bean.user.User;
import cms.service.message.SystemNotifyService;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.utils.HtmlEscape;
import cms.utils.JsonUtils;
import cms.utils.WebUtil;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.FileManage;

/**
 * 系统通知管理
 *
 */
@Controller
@RequestMapping("/control/systemNotify/manage") 
public class SystemNotifyManageAction {

	@Resource SystemNotifyService systemNotifyService;
	@Resource(name = "systemNotifyValidator") 
	private Validator validator; 
	
	@Resource TextFilterManage textFilterManage;
	@Resource SettingService settingService;
	@Resource SystemNotifyManage systemNotifyManage;
	@Resource UserService userService;
	@Resource MessageSource messageSource;
	@Resource FileManage fileManage;
	
	/**
	 * 系统通知   添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(SystemNotify systemNotify,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
	}
	
	/**
	 * 系统通知  添加
	 * @param model
	 * @param content 通知内容
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,SystemNotify formbean,BindingResult result,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
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
			String staffName = "";//员工名称
			Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
			if(obj instanceof SysUsers){
				staffName =((SysUsers)obj).getUserAccount();
			}
			
			SystemNotify systemNotify = new SystemNotify(); 
			String content = WebUtil.urlToHyperlink(HtmlEscape.escape(formbean.getContent().trim()));
			
			systemNotify.setContent(content);
			systemNotify.setSendTime(new Date());
			systemNotify.setStaffName(staffName);
			systemNotifyService.saveSystemNotify(systemNotify);
			
			
			//删除缓存
			systemNotifyManage.delete_cache_findSystemNotifyCountBySystemNotifyId();
			systemNotifyManage.delete_cache_findSystemNotifyCountBySendTime();
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	
	/**
	 * 系统通知   修改界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(Long systemNotifyId,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		if(systemNotifyId != null){
			SystemNotify systemNotify = systemNotifyService.findById(systemNotifyId);
			if(systemNotify != null){
				systemNotify.setContent(textFilterManage.filterText(systemNotify.getContent()));
				
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,systemNotify));
			}else{
				error.put("systemNotifyId", "系统通知Id不存在");
			}
		}else{
			error.put("systemNotifyId", "系统通知Id不能为空");
			
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	/**
	 * 系统通知  修改
	 * @param model
	 * @param content 通知内容
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.POST)
	public String edit(ModelMap model,SystemNotify formbean,BindingResult result,Long systemNotifyId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		SystemNotify old_systemNotify = null;
		if(systemNotifyId != null && systemNotifyId >0){
			old_systemNotify = systemNotifyService.findById(systemNotifyId);
			if(old_systemNotify == null){
				error.put("systemNotifyId", "系统通知Id不存在");
			}
		}else{
			error.put("systemNotifyId", "系统通知Id不能为空");
			
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
		if(error.size() ==0){
			String staffName = "";//员工名称
			Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
			if(obj instanceof SysUsers){
				staffName =((SysUsers)obj).getUserAccount();
			}
			
			SystemNotify systemNotify = new SystemNotify();
			systemNotify.setId(old_systemNotify.getId());
			String content = WebUtil.urlToHyperlink(HtmlEscape.escape(formbean.getContent().trim()));
			
			systemNotify.setContent(content);
			systemNotify.setStaffName(staffName);
			systemNotifyService.updateSystemNotify(systemNotify);
			
			
			//删除缓存
			systemNotifyManage.delete_cache_findSystemNotifyCountBySystemNotifyId();
			systemNotifyManage.delete_cache_findSystemNotifyCountBySendTime();
			systemNotifyManage.delete_cache_findById(systemNotifyId);
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	/**
	 * 系统通知   删除
	 * @param model
	 * @param systemNotifyId 系统通知Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	public String delete(ModelMap model,Long systemNotifyId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		if(systemNotifyId != null && systemNotifyId >0L){
			int i = systemNotifyService.deleteSystemNotify(systemNotifyId);	
			
			//删除缓存
			systemNotifyManage.delete_cache_findSystemNotifyCountBySystemNotifyId();
			systemNotifyManage.delete_cache_findSystemNotifyCountBySendTime();
			systemNotifyManage.delete_cache_findById(systemNotifyId);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("systemNotifyId", "系统通知Id不能为空");
			
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	
	/**
	 * 订阅系统通知列表
	 * @param model
	 * @param id 用户Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=subscriptionSystemNotifyList",method=RequestMethod.GET)
	public String subscriptionSystemNotifyList(PageForm pageForm,ModelMap model,Long id,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(id != null && id >0L){
			//调用分页算法代码
			PageView<SubscriptionSystemNotify> pageView = new PageView<SubscriptionSystemNotify>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			
			
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();

			//系统通知Id集合
			Set<Long> systemNotifyIdList = new HashSet<Long>();
			//系统通知内容集合
			Map<Long,String> systemNotifyMap = new HashMap<Long,String>();
			
			QueryResult<SubscriptionSystemNotify> qr = systemNotifyService.findSubscriptionSystemNotifyByUserId(id,null,firstIndex,pageView.getMaxresult());
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(SubscriptionSystemNotify subscriptionSystemNotify : qr.getResultlist()){
					systemNotifyIdList.add(subscriptionSystemNotify.getSystemNotifyId());
				}
			}
			if(systemNotifyIdList != null && systemNotifyIdList.size() >0){
				for(Long systemNotifyId : systemNotifyIdList){
					SystemNotify systemNotify = systemNotifyService.findById(systemNotifyId);
					if(systemNotify != null){
						systemNotifyMap.put(systemNotifyId, systemNotify.getContent());
					}
				}
			}
			if(systemNotifyIdList != null && systemNotifyIdList.size() >0){
				if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
					for(SubscriptionSystemNotify subscriptionSystemNotify : qr.getResultlist()){
						String content = systemNotifyMap.get(subscriptionSystemNotify.getSystemNotifyId());
						if(content != null){
							subscriptionSystemNotify.setContent(content);
						}
						
					}
				}
			}
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			
			
			
			User user = userService.findUserById(id);
			if(user != null){
				User currentUser = new User();
				currentUser.setId(user.getId());
				currentUser.setAccount(user.getAccount());
				currentUser.setNickname(user.getNickname());
				if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
					currentUser.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
					currentUser.setAvatarName(user.getAvatarName());
				}
				returnValue.put("currentUser", currentUser);
			}
			
			returnValue.put("pageView", pageView);
		}else{
			error.put("userId", "用户Id不能为空");
			
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
		
	}
	/**
	 * 还原订阅系统通知
	 * @param model
	 * @param userId 用户Id
	 * @param subscriptionSystemNotifyId 订阅系统通知Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=reductionSubscriptionSystemNotify", method=RequestMethod.POST)
	public String reductionSubscriptionSystemNotify(ModelMap model,Long userId,String subscriptionSystemNotifyId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		if(subscriptionSystemNotifyId != null && !"".equals(subscriptionSystemNotifyId.trim())){
			int i = systemNotifyService.reductionSubscriptionSystemNotify(subscriptionSystemNotifyId);
			
			//删除缓存
			systemNotifyManage.delete_cache_findMinUnreadSystemNotifyIdByUserId(userId);
			systemNotifyManage.delete_cache_findMaxReadSystemNotifyIdByUserId(userId);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("subscriptionSystemNotifyId", "订阅系统通知Id不能为空");
			
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
}
