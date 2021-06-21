package cms.web.action.user;


import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.user.UserRole;
import cms.service.user.UserRoleService;
import cms.utils.JsonUtils;

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
	@ResponseBody
	@RequestMapping("/control/userRole/list") 
	public String execute(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		
		List<UserRole> userRoleList = userRoleService.findAllRole();
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,userRoleList));
	}
	
	
	
}
