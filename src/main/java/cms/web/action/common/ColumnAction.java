package cms.web.action.common;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.template.Layout;
import cms.service.template.TemplateService;
import cms.web.action.AccessSourceDeviceManage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 站点栏目
 *
 */
@Controller
public class ColumnAction {
	@Resource TemplateService templateService;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	
	@RequestMapping(value="/column/{columnId}",method = RequestMethod.GET) 
	public String execute(ModelMap model,@PathVariable Integer columnId,
			HttpServletRequest request,HttpServletResponse response)throws Exception {
		//当前模板使用的目录
		String dirName = templateService.findTemplateDir_cache();
		if(columnId != null && columnId >0){
			
			Layout layout = templateService.findLayoutByReferenceCode_cache(dirName,7,"column_"+columnId);
			if(layout != null){
				
				
				
				return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/public/column_"+columnId;
			}
		}
		
		return null;
	}
}
