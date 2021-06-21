package cms.web.action.user;

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
import cms.bean.user.PointLog;
import cms.bean.user.User;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.utils.JsonUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 积分日志管理
 *
 */
@Controller
public class PointLogAction{

	@Resource UserService userService;
	@Resource SettingService settingService;
	
	@ResponseBody
	@RequestMapping("/control/pointLog/list")
	public String execute(ModelMap model, PageForm pageForm,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		if(userName != null && !"".equals(userName.trim())){
			//调用分页算法代码
			PageView<PointLog> pageView = new PageView<PointLog>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			User user = userService.findUserByUserName(userName);
			if(user != null){
				QueryResult<PointLog> qr =  userService.findPointLogPage(user.getId(),user.getUserName(),firstIndex, pageView.getMaxresult());
				
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);
				
				returnValue.put("currentUser", user);
				returnValue.put("pageView", pageView);
			}
			
		}else{
			error.put("userName", "用户名称不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
}

