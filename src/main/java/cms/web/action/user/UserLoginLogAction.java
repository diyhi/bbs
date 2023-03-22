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
import cms.bean.user.User;
import cms.bean.user.UserLoginLog;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.web.action.fileSystem.FileManage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户登录日志
 *
 */
@Controller
public class UserLoginLogAction {
	
	@Resource UserService userService;
	@Resource SettingService settingService;
	@Resource FileManage fileManage;
	
	/**
	 * 用户登录日志列表
	 * @param userId 用户Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/userLoginLog/list") 
	public String execute(ModelMap model,Long id,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		
		
		if(id != null && id >0L){
			//调用分页算法代码
			PageView<UserLoginLog> pageView = new PageView<UserLoginLog>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
			QueryResult<UserLoginLog> qr = userService.findUserLoginLogPage(id, firstIndex, pageView.getMaxresult());
			
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(UserLoginLog userLoginLog : qr.getResultlist()){
					if(userLoginLog.getIp() != null && !"".equals(userLoginLog.getIp().trim())){
						userLoginLog.setIpAddress(IpAddress.queryAddress(userLoginLog.getIp()));
					}
				}
			}
			
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);	
			User user = userService.findUserById(id);
			if(user != null){
				User currentUser = new User();
				currentUser.setId(user.getId());
				currentUser.setAccount(user.getAccount());
				currentUser.setNickname(user.getNickname());
				if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
					currentUser.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
					currentUser.setAvatarName(user.getAvatarName());
				}
				returnValue.put("currentUser", currentUser);
			}
			returnValue.put("pageView", pageView);
		}else{
			error.put("userId", "用户Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
}
