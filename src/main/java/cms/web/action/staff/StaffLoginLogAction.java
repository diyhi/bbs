package cms.web.action.staff;


import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.staff.StaffLoginLog;
import cms.bean.staff.SysUsers;
import cms.service.setting.SettingService;
import cms.service.staff.StaffService;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 员工登录日志
 *
 */
@Controller
public class StaffLoginLogAction {

	@Resource StaffService staffService;
	@Resource SettingService settingService;
	/**
	 * 员工登录日志列表
	 * @param userId 员工Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/staffLoginLog/list") 
	public String execute(ModelMap model,String userId,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		String _userId = "";//用户Id
		boolean issys = false;//是否是超级用户
		Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(obj instanceof SysUsers){
			issys = ((SysUsers)obj).isIssys();
			_userId =((SysUsers)obj).getUserId();
		}
		//调用分页算法代码
		PageView<StaffLoginLog> pageView = new PageView<StaffLoginLog>(settingService.findSystemSetting().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		if(userId != null && !"".equals(userId.trim())){
			if(issys == false && !_userId.equals(userId)){
				error.put("userId", "非超级管理员不允许查看其他成员登录记录");
			}else{
				QueryResult<StaffLoginLog> qr = staffService.findStaffLoginLogPage(userId, firstIndex, pageView.getMaxresult());
				if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
					for(StaffLoginLog staffLoginLog : qr.getResultlist()){
						if(staffLoginLog.getIp() != null && !"".equals(staffLoginLog.getIp().trim())){
							staffLoginLog.setIpAddress(IpAddress.queryAddress(staffLoginLog.getIp()));
						}
					}
				}
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);	
				returnValue.put("pageView", pageView);
				SysUsers sysUsers = (SysUsers)staffService.find(SysUsers.class, userId);
				if(sysUsers != null){
					returnValue.put("currentStaff", sysUsers);
				}
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
			}
		}else{//如果接收到所属用户为空
			error.put("userId", "用户Id不能为空");
		}

		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
}
