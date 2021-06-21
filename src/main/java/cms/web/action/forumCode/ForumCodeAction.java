package cms.web.action.forumCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.forumCode.ForumCodeNode;
import cms.bean.template.Templates;
import cms.service.template.TemplateService;
import cms.utils.JsonUtils;

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
	
	@ResponseBody
	@RequestMapping("/control/forumCode/list") 
	public String execute(ModelMap model,@RequestParam("dirName") String dirName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
				
		if(dirName != null && !"".equals(dirName.trim())){
			//根据模板目录名称查询模板
			Templates templates = templateService.findTemplatebyDirName(dirName);
			if(templates != null){
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,templates));
			}else{
				error.put("templates", "模板不存在");
			}
		}else{
			error.put("dirName", "目录名称不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
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
	@ResponseBody
	@RequestMapping(value="/control/forumCode/query",params="method=directory",method=RequestMethod.GET)
	public String queryForumDirectory(ModelMap model,@RequestParam("dirName") String dirName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
				
		if(dirName != null && !"".equals(dirName.trim())){
			List<ForumCodeNode> forumCodeNodeList = forumCodeManage.forumCodeNodeList(dirName);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,forumCodeNodeList));
		}else{
			error.put("dirName", "目录名称不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
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
	@ResponseBody
	@RequestMapping(value="/control/forumCode/query",params="method=forumCode",method=RequestMethod.GET)
	public String queryForumCode(ModelMap model,@RequestParam("dirName") String dirName,
			String childNodeName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
				
		if(dirName != null && !"".equals(dirName.trim())){
			List<ForumCodeNode> forumCodeNodeList = forumCodeManage.getForumCodeNode(dirName,childNodeName);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,forumCodeNodeList));
		}else{
			error.put("dirName", "目录名称不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		
	}
}
