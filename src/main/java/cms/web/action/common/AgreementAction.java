package cms.web.action.common;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import cms.service.template.TemplateService;
import cms.web.action.AccessSourceDeviceManage;

/**
 * 用户协议
 *
 */
@Controller
public class AgreementAction {
	//注入业务bean
		@Resource(name="templateServiceBean")
		private TemplateService templateService;//通过接口引用代理返回的对象
		@Resource AccessSourceDeviceManage accessSourceDeviceManage;
		
		
		@RequestMapping("/agreement")
		public String execute(ModelMap model,HttpServletRequest request)throws Exception {
	
			//当前模板使用的目录
			String dirName = templateService.findTemplateDir_cache();
			
		    return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/agreement";
		}
}
