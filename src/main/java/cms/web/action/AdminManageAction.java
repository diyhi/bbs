package cms.web.action;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cms.bean.setting.SystemSetting;
import cms.service.setting.SettingService;
import cms.service.staff.StaffService;
import cms.utils.UUIDUtil;
import cms.utils.WebUtil;
import cms.web.action.common.CaptchaManage;
import cms.web.action.staff.StaffManage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * 员工登录 退出管理
 * @author Administrator
 *
 */
@Controller
public class AdminManageAction {
	@Resource StaffService staffService;//通过接口引用代理返回的对象
	@Resource CaptchaManage captchaManage;
	@Resource StaffManage staffManage;
	@Resource SettingService settingService;

	/**
	 * 员工登录页面显示
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/admin/login",method=RequestMethod.GET) 
	public String loginUI(ModelMap model,
			HttpServletRequest request, HttpServletResponse response, HttpSession  session)
			throws Exception {
	
		
		//是否需要验证码  true:要  false:不要
		boolean isCaptcha = false;
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getLogin_submitQuantity() <=0){//每分钟连续登录密码错误N次时出现验证码
			isCaptcha = true;
		}else{

			String staffName = WebUtil.getCookieByName(request.getCookies(),"staffName");
			if(staffName != null && !"".equals(staffName.trim())){
				Integer errorCount = staffManage.getLoginFailureCount(staffName);//查询错误次数
				if(errorCount != null && errorCount >= systemSetting.getLogin_submitQuantity()){
					isCaptcha = true;
				}
			}
			
		}
		if(isCaptcha){
			model.addAttribute("isCaptcha", true);
			model.addAttribute("captchaKey", UUIDUtil.getUUID32());
		}else{
			model.addAttribute("isCaptcha", false);
		}
			
		
		return "jsp/admin/login";
	}
	
	

	
	/**
	 * 没有权限
	 */
	@RequestMapping(value="/admin/permission",method={RequestMethod.GET,RequestMethod.POST}) 
	public String permission(ModelMap model,HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		if(isAjax){//如果是AJAX方式访问
			WebUtil.writeToWeb("", "json", response);
			return null;
		}
		
		model.addAttribute("error", "您没有当前页面的权限");
		return "jsp/common/permission";
	}
	
	
	/**
	 * 当前时间
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/admin/currentTime",method=RequestMethod.GET)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String currentTime(ModelMap model
			) throws Exception {
		return String.valueOf(new Date().getTime());
	}
	
}
