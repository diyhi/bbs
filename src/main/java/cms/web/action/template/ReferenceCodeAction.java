package cms.web.action.template;

import javax.annotation.Resource;

import cms.bean.template.Forum;
import cms.bean.template.Layout;
import cms.service.template.TemplateService;
import cms.web.action.SystemException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 显示生成模块引用代码
 *
 */
@Controller
public class ReferenceCodeAction {
	//注入业务bean
	@Resource(name="templateServiceBean")
	private TemplateService templateService;//通过接口引用代理返回的对象
	
	/**
	 * 显示引用代码
	 */
	@RequestMapping("/control/template/referenceCode") 
	public String execute(ModelMap model,Integer forumId,String templeteName,String layoutName,Integer returnData) throws Exception {
		if(forumId != null && forumId >0){	
			Forum forum = templateService.find(Forum.class,forumId);	
			
			Layout layout = templateService.findLayoutByLayoutId(forum.getLayoutId());	
			model.addAttribute("forum",forum);
			model.addAttribute("layout",layout);
			
			//代码
			if(forum.getInvokeMethod().equals(2)){
				String code = forum.getModule().substring(0,StringUtils.lastIndexOfIgnoreCase(forum.getModule(), "_"));//含有后缀
				model.addAttribute("code",code);
			}
			
		}else{
			throw new SystemException("【参数不正确】！");
		}	
		return "jsp/template/referenceCode";
	}
}
