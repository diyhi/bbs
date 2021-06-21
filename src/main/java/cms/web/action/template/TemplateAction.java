package cms.web.action.template;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.template.Templates;
import cms.service.template.TemplateService;
import cms.utils.JsonUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 模板目录显示
 *
 */
@Controller
public class TemplateAction {
	//注入业务bean
	@Resource(name="templateServiceBean")
	private TemplateService templateService;//通过接口引用代理返回的对象
	
	
	@ResponseBody
	@RequestMapping("/control/template/list") 
	public String execute(ModelMap model,HttpServletRequest request)
			throws Exception {

		List<Templates> templatesList =  templateService.findAllTemplates();

		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,templatesList));
	}
}
