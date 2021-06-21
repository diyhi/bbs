package cms.web.action.user;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.user.UserGrade;
import cms.service.user.UserGradeService;
import cms.utils.JsonUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *  等级
 *
 */
@Controller
public class UserGradeAction {
	//注入业务bean
	@Resource UserGradeService userGradeService;
	
	/**
	 * 用户等级
	 * @param formbean
	 * @param pageForm
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/userGrade/list") 
	public String execute(PageForm pageForm,ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		List<UserGrade> userGradeList = userGradeService.findAllGrade();
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,userGradeList));
	}
}
