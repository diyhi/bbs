package cms.web.action;

import java.util.List;

import javax.annotation.Resource;

import cms.bean.QueryResult;
import cms.bean.staff.StaffLoginLog;
import cms.bean.staff.SysUsers;
import cms.service.staff.StaffService;
import cms.utils.IpAddress;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 后台框架页面
 *
 */
@Controller
public class AdminAction {
	@Resource SessionRegistry sessionRegistry;
	@Resource StaffService staffService;
	/**
	 * 后台管理
	 * @return
	 */
	@RequestMapping("control/center/admin") 
	public String admin(ModelMap model){
		SysUsers sysUsers = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null){
			 Object o = authentication.getPrincipal();
			 if(o instanceof SysUsers){
				 sysUsers = (SysUsers)o;
			 }
		}
		model.addAttribute("sysUsers",sysUsers);//返回消息
		return "jsp/admin/admin";
	}
	/**
	 * 后台首页
	 * @return
	 */
	@RequestMapping("control/center/home") 
	public String mainframe(ModelMap model){
		SysUsers sysUsers = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null){
			 Object o = authentication.getPrincipal();
			 if(o instanceof SysUsers){
				 sysUsers = (SysUsers)o;
			 }
		}
		
		model.addAttribute("sysUsers",sysUsers);//返回消息
		
		QueryResult<StaffLoginLog> qr = staffService.findStaffLoginLogPage(sysUsers.getUserId(), 0, 5);
		List<StaffLoginLog> staffLoginLogList = qr.getResultlist();
		if(staffLoginLogList != null && staffLoginLogList.size() >0){
			for(StaffLoginLog staffLoginLog : staffLoginLogList){
				if(staffLoginLog.getIp() != null && !"".equals(staffLoginLog.getIp().trim())){
					staffLoginLog.setIpAddress(IpAddress.queryAddress(staffLoginLog.getIp()));
				}
			}
		}
		model.addAttribute("staffLoginLogList", staffLoginLogList);
		return "jsp/admin/home";
	}
}
