package cms.web.action.common;



import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.setting.SystemSetting;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.utils.WebUtil;
import cms.web.action.AccessSourceDeviceManage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 默认消息页
 *
 */
@Controller
public class MessageAction {
	//注入业务bean
	@Resource(name="templateServiceBean")
	private TemplateService templateService;//通过接口引用代理返回的对象
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource SettingService settingService;
	
	
	@RequestMapping("/message")
	public String execute(ModelMap model,
			HttpServletRequest request,HttpServletResponse response)throws Exception {
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据

		if(isAjax){
			if(systemSetting.getCloseSite() >1){//如果关闭网站
				response.setStatus(503);
			}
			WebUtil.writeToWeb(systemSetting.getCloseSitePrompt(), "html", response);
			return null;
		}else{
			if(systemSetting.getCloseSite() >1){//如果关闭网站
				model.addAttribute("message",systemSetting.getCloseSitePrompt());
			}
			
			//当前模板使用的目录
			String dirName = templateService.findTemplateDir_cache();
		    return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/message";
		}
		
	}
}
