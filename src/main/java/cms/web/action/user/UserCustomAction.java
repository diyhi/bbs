package cms.web.action.user;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.user.UserCustom;
import cms.service.user.UserCustomService;
import cms.utils.JsonUtils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户自定义注册功能项
 *
 */
@Controller
public class UserCustomAction {
	//注入业务bean
	@Resource(name="userCustomServiceBean")
	private UserCustomService userCustomService;

	@ResponseBody
	@RequestMapping("/control/userCustom/list") 
	public String execute(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<UserCustom> userCustom = userCustomService.findAllUserCustom();

		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,userCustom));
	}
	
	
}
