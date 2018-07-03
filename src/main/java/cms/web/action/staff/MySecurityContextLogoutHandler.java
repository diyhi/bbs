package cms.web.action.staff;

import javax.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户退出时处理
 *
 */
public class MySecurityContextLogoutHandler extends SecurityContextLogoutHandler{
	@Resource StaffManage staffManage;
	
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		if (super.isInvalidateHttpSession()) {
			HttpSession session = request.getSession(false);
	        if (session != null) {
	        	staffManage.delete_staffSecurityDigest(authentication.getName());
	        }
		}
		super.logout(request, response, authentication);
    }
}
