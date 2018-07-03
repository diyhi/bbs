package cms.web.action.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.user.PointLog;
import cms.service.user.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 积分日志管理
 *
 */
@Controller
@RequestMapping("/control/pointLog/manage") 
public class PointLogManageAction{
	@Resource PointManage pointManage;
	@Resource UserService userService;//通过接口引用代理返回的对象
	/**
	 * 积分日志管理 详细显示
	 * @param model
	 * @param pointLogId
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=show",method=RequestMethod.GET)
	public String addUI(ModelMap model,String pointLogId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		if(pointLogId != null && !"".equals(pointLogId.trim())){
			if(pointLogId.trim().length() == 36 && pointManage.verificationPointLogId(pointLogId)){
				PointLog pointLog = userService.findPointLogById(pointLogId);
				model.addAttribute("pointLog", pointLog);
			}
		}
		return "jsp/user/show_pointLog";
	}
	
}

