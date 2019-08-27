package cms.web.action.user;


import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import cms.bean.user.UserRole;
import cms.service.user.UserRoleService;

/**
 * 用户角色 
 *
 */
@Controller
public class UserRoleAction {
	//注入业务bean
	@Resource UserRoleService userRoleService;
	
	
	/**
	 * 用户角色列表
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/control/userRole/list") 
	public String execute(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		
		List<UserRole> userRoleList = userRoleService.findAllRole();
		model.addAttribute("userRoleList", userRoleList);
		return "jsp/user/userRoleList";
	}
	
	
	
}
