package cms.web.action.user;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.user.UserLoginLog;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.utils.IpAddress;
import cms.web.action.SystemException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户登录日志
 *
 */
@Controller
public class UserLoginLogAction {
	
	@Resource UserService userService;
	@Resource SettingService settingService;
	/**
	 * 用户登录日志列表
	 * @param userId 用户Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/control/userLoginLog/list") 
	public String execute(ModelMap model,Long id,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		//调用分页算法代码
		PageView<UserLoginLog> pageView = new PageView<UserLoginLog>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		
		if(id != null && id >0L){

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
		}else{//如果接收到所属用户为空
			throw new SystemException("参数错误！");
		}
		model.addAttribute("pageView", pageView);

		return "jsp/user/loginLogList";
	}
}
