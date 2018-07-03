package cms.web.action.staff;

import java.io.IOException;
import java.util.Date;

import cms.bean.staff.StaffLoginLog;
import cms.bean.staff.SysUsers;
import cms.service.staff.ACLService;
import cms.service.staff.StaffService;
import cms.utils.IpAddress;
import cms.utils.WebUtil;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * 登录成功后处理
 *
 **/
public class MySavedRequestAwareAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{

	@Resource ACLService aclService;
	@Resource StaffManage staffManage;
	@Resource StaffLoginLogManage staffLoginLogManage;
	@Resource StaffService staffService;
	
  
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
    	super.onAuthenticationSuccess(request, response, authentication);
   
    	//清理登录次数缓存
    	staffManage.deleteLoginFailureCount(authentication.getName());
    	WebUtil.deleteCookie(response, "cms_staffName");
    	
    	Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(obj instanceof SysUsers){
			String userId =((SysUsers)obj).getUserId();
	    	//写入登录日志
			StaffLoginLog staffLoginLog = new StaffLoginLog();
			staffLoginLog.setId(staffLoginLogManage.createStaffLoginLogId(userId));
			staffLoginLog.setIp(IpAddress.getClientIpAddress(request));
			staffLoginLog.setStaffId(userId);
			staffLoginLog.setLogonTime(new Date());
			Object new_staffLoginLog = staffLoginLogManage.createStaffLoginLogObject(staffLoginLog);
			
			staffService.saveStaffLoginLog(new_staffLoginLog);
		}
		
    //	HttpSession session = request.getSession();
  
    	//删除缓存员工安全摘要
    	staffManage.delete_staffSecurityDigest(authentication.getName());
    	
    	
    }

}

