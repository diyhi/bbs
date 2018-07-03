package cms.web.action.template;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import cms.bean.PageForm;
import cms.bean.template.Templates;
import cms.service.template.TemplateService;
import cms.utils.JsonUtils;
import cms.utils.PathUtil;
import cms.web.action.FileManage;
import cms.web.action.SystemException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 资源管理 分页显示
 *
 */
@Controller
public class ResourceAction {
	@Resource FileManage fileManage;
	@Resource TemplateService templateService;
	
	@RequestMapping("/control/resource/list") 
	public String execute(@RequestParam("dirName") String dirName,PageForm pageForm,ModelMap model){
		
		if(dirName == null || "".equals(dirName.trim())){
			throw new SystemException("目录名称不能为空");
		}
		//根据模板目录名称查询模板
		if(dirName != null && !"".equals(dirName.trim())){
			Templates templates = templateService.findTemplatebyDirName(dirName);
			model.addAttribute("templates", templates);
		}
		return "jsp/template/resourceList";
	}
	
	/**
	 * 查询资源子节点
	 * @param dirName
	 * @param parentId 父Id
	 * @param model @RequestParam("dirName") String dirName,
	 * @return
	 */
	@RequestMapping(value="/control/resource/query",method=RequestMethod.GET) 
	@ResponseBody//方式来做ajax,直接返回字符串
	public String queryChildNode(String dirName,String parentId,ModelMap model,
			HttpServletRequest request){
		
		if(dirName == null || "".equals(dirName.trim())){
			return "";
		}
		List<cms.bean.template.Resource> resourceList = new ArrayList<cms.bean.template.Resource>();

		String path = PathUtil.path()+File.separator+"common"+File.separator+fileManage.toRelativePath(dirName)+(parentId == null || "".equals(parentId.trim()) ? "" :File.separator+fileManage.toRelativePath(fileManage.toSystemPath(parentId)));
		
		File dir = new File(path);
		if(dir.isDirectory()){
			
			File[] fs=dir.listFiles(); 
			for(File file : fs){
				cms.bean.template.Resource resource =  new cms.bean.template.Resource();
				if(parentId == null || "".equals(parentId.trim())){
					resource.setId(file.getName());
				}else{
					resource.setId(parentId+"/"+file.getName());
				}
		    	resource.setLastModified(new Date(file.lastModified()));
				if(file.isDirectory() == true){//是目录
					resource.setLeaf(false);//不是叶子节点
				}else{
					resource.setLeaf(true);//是叶子节点
				}
				resource.setName(file.getName());
				resourceList.add(resource);
			}
		}else{
			return "";
		}

		return JsonUtils.toJSONString(resourceList);
	}
}
