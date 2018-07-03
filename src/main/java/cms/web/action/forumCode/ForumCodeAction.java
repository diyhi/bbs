package cms.web.action.forumCode;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.forumCode.ForumCodeNode;
import cms.bean.template.Templates;
import cms.service.template.TemplateService;
import cms.utils.JsonUtils;
import cms.web.action.SystemException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 版块代码
 *
 */
@Controller
public class ForumCodeAction {
	@Resource ForumCodeManage forumCodeManage;
	@Resource(name="templateServiceBean")
	private TemplateService templateService;//通过接口引用代理返回的对象
	
	@RequestMapping("/control/forumCode/list") 
	public String execute(ModelMap model,@RequestParam("dirName") String dirName,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		if(dirName == null || "".equals(dirName.trim())){
			throw new SystemException("目录名称不能为空");
		}
		//根据模板目录名称查询模板
		if(dirName != null && !"".equals(dirName.trim())){
			Templates templates = templateService.findTemplatebyDirName(dirName);
			model.addAttribute("templates", templates);
		}
		return "jsp/forumCode/forumCodeList";
	}
	
	/**
	 * 查询版块目录
	 * @param model
	 * @param dirName
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/control/forumCode/query",params="method=directory",method=RequestMethod.GET)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String queryForumDirectory(ModelMap model,@RequestParam("dirName") String dirName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		List<ForumCodeNode> forumCodeNodeList = forumCodeManage.forumCodeNodeList(dirName);
		
		return JsonUtils.toJSONString(forumCodeNodeList);
	}
	
	/**
	 * 查询版块代码
	 * @param model
	 * @param dirName
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/control/forumCode/query",params="method=forumCode",method=RequestMethod.GET)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String queryForumCode(ModelMap model,@RequestParam("dirName") String dirName,
			String childNodeName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		List<ForumCodeNode> forumCodeNodeList = forumCodeManage.getForumCodeNode(dirName,childNodeName);
		
		return JsonUtils.toJSONString(forumCodeNodeList);
	}
}
