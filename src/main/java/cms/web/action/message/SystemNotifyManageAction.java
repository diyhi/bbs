package cms.web.action.message;


import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.message.SubscriptionSystemNotify;
import cms.bean.message.SystemNotify;
import cms.bean.staff.SysUsers;
import cms.service.message.SystemNotifyService;
import cms.service.setting.SettingService;
import cms.utils.HtmlEscape;
import cms.utils.RedirectPath;
import cms.utils.WebUtil;
import cms.web.action.SystemException;
import cms.web.action.TextFilterManage;

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
	
	/**
	 * 系统通知   添加界面显示
	 */
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(SystemNotify systemNotify,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "jsp/message/add_systemNotify";
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
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,SystemNotify formbean,BindingResult result,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//数据校验
		this.validator.validate(formbean, result); 
		if (result.hasErrors()) { 
			model.addAttribute("systemNotify",formbean);//返回消息
			return "jsp/message/add_systemNotify";
		}
		
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
		
		
		model.addAttribute("message","添加系统通知成功");//返回消息
		model.addAttribute("urladdress", RedirectPath.readUrl("control.systemNotify.list"));
		return "jsp/common/message";
	}
	
	
	/**
	 * 系统通知   修改界面显示
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(Long systemNotifyId,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(systemNotifyId != null){//判断ID是否存在;
			SystemNotify systemNotify = systemNotifyService.findById(systemNotifyId);
			if(systemNotify != null){
				systemNotify.setContent(textFilterManage.filterText(systemNotify.getContent()));
				
				
				model.addAttribute("systemNotify",systemNotify);//返回消息
			}
		}
		return "jsp/message/edit_systemNotify";
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
	@RequestMapping(params="method=edit", method=RequestMethod.POST)
	public String edit(ModelMap model,SystemNotify formbean,BindingResult result,Long systemNotifyId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		SystemNotify old_systemNotify = null;
		if(systemNotifyId != null && systemNotifyId >0){
			old_systemNotify = systemNotifyService.findById(systemNotifyId);
			if(old_systemNotify == null){
				throw new SystemException("系统通知不存在！");
			}
		}else{
			throw new SystemException("参数错误！");
		}
		
		
		//数据校验
		this.validator.validate(formbean, result); 
		if (result.hasErrors()) { 
			model.addAttribute("systemNotify",formbean);//返回消息
			return "jsp/message/edit_systemNotify";
		}
		
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
		
		
		model.addAttribute("message","修改系统通知成功");//返回消息
		model.addAttribute("urladdress", RedirectPath.readUrl("control.systemNotify.list"));
		return "jsp/common/message";
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
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Long systemNotifyId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(systemNotifyId != null && systemNotifyId >0L){
			int i = systemNotifyService.deleteSystemNotify(systemNotifyId);	
			
			//删除缓存
			systemNotifyManage.delete_cache_findSystemNotifyCountBySystemNotifyId();
			systemNotifyManage.delete_cache_findSystemNotifyCountBySendTime();
			systemNotifyManage.delete_cache_findById(systemNotifyId);
			return "1";
		}
		return "0";
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
	@RequestMapping(params="method=subscriptionSystemNotifyList",method=RequestMethod.GET)
	public String subscriptionSystemNotifyList(PageForm pageForm,ModelMap model,Long id,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
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
			
			model.addAttribute("pageView", pageView);
		}
		
		
		
		return "jsp/message/subscriptionSystemNotifyList";
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
	@RequestMapping(params="method=reductionSubscriptionSystemNotify", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String reductionSubscriptionSystemNotify(ModelMap model,Long userId,String subscriptionSystemNotifyId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(subscriptionSystemNotifyId != null && !"".equals(subscriptionSystemNotifyId.trim())){
			int i = systemNotifyService.reductionSubscriptionSystemNotify(subscriptionSystemNotifyId);
			
			//删除缓存
			systemNotifyManage.delete_cache_findMinUnreadSystemNotifyIdByUserId(userId);
			systemNotifyManage.delete_cache_findMaxReadSystemNotifyIdByUserId(userId);
			return "1";
		}
		return "0";
	}
	
}
