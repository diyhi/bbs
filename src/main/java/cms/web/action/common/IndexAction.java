package cms.web.action.common;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.service.template.TemplateService;
import cms.web.action.AccessSourceDeviceManage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页
 *
 */
@Controller
public class IndexAction {
	//注入业务bean
	@Resource(name="templateServiceBean")
	private TemplateService templateService;//通过接口引用代理返回的对象
	
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	
	@RequestMapping("/index")
	public String execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		//当前模板使用的目录
		String dirName = templateService.findTemplateDir_cache();

		
		return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/index";
	}
}
