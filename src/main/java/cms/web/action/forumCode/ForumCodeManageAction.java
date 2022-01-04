package cms.web.action.forumCode;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.forumCode.ForumCodeNode;
import cms.bean.template.Forum;
import cms.bean.template.Layout;
import cms.bean.template.Templates;
import cms.service.template.TemplateService;
import cms.utils.Coding;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.PathUtil;
import cms.utils.Verification;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import cms.web.action.template.TemplateManage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * 版块代码管理
 *
 */
@Controller
@RequestMapping("/control/forumCode/manage") 
public class ForumCodeManageAction {
	@Resource ForumCodeManage forumCodeManage;
	@Resource TemplateService templateService;//通过接口引用代理返回的对象
	@Resource TemplateManage templateManage;
	@Resource LocalFileManage localFileManage;
	
	/**
	 * 版块代码管理    添加
	 * @param parentId
	 * @param name 模板名称 只能由字母或数字组成
	 * @param displayType 模板显示类型
	 * @param remark 备注
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.POST)
	public String add(ModelMap model, Integer parentId,String name,String displayType,String remark,
			@RequestParam("dirName") String dirName,
			HttpServletRequest request) throws Exception {

		//错误
		Map<String,String> error = new HashMap<String,String>();

		String prefix = "";//版块代码文件名称前缀
		
		//模板显示类型 
		List<String> displayTypeList = new ArrayList<String>();
		
		//根据节点Id查询文件名前缀
		List<ForumCodeNode> forumCodeNodeList = forumCodeManage.forumCodeNodeList(dirName);
		for(int i = 0; i<forumCodeNodeList.size(); i++){
			//二级节点
			List<ForumCodeNode> childNode = forumCodeNodeList.get(i).getChildNode();
			for(int j = 0; j< childNode.size();j++){
				if(childNode.get(j).getNodeId().equals(parentId)){
					prefix = childNode.get(j).getPrefix();
					displayTypeList.addAll(childNode.get(j).getDisplayType());
					break;
				}
			}
		}
			
		String selected_displayType = "";
		if(displayTypeList != null && displayTypeList.size() >0){
			for(int i = 0; i<displayTypeList.size(); i++){
				if(displayTypeList.get(i).equals(displayType)){
					if(displayType.equals("单层")){
						selected_displayType = "monolayer";
					}else if(displayType.equals("多层")){
						selected_displayType = "multilayer";
					}else if(displayType.equals("分页")){
						selected_displayType = "page";
					}else if(displayType.equals("实体对象")){
						selected_displayType = "entityBean";
					}else if(displayType.equals("集合")){
						selected_displayType = "collection";
					}
				}
			}
			if("".equals(selected_displayType)){
				error.put("displayType", "模板显示类型未选择");
			}
		}else{
			error.put("displayType", "模板显示类型不存在");
		}
		
		//验证模板名称
		if(name != null && !"".equals(name.trim())){
			if("monolayer".equalsIgnoreCase(name.trim()) || "multilayer".equalsIgnoreCase(name.trim()) || "page".equalsIgnoreCase(name.trim())|| "entityBean".equalsIgnoreCase(name.trim())|| "collection".equalsIgnoreCase(name.trim())){
				error.put("name", "模板名称不能为  "+name);
			}else{
				
				//验证'只能由字母或数字组成'
				if(!Verification.isNumericLetters(name.trim())){
					error.put("name", "模板名称只能由字母或数字组成 ");
				}else{
					
					//验证文件是否存在
					String path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"forum"+File.separator+prefix+selected_displayType+"_"+name.trim()+".html";
					
					File f = new File(FileUtil.toRelativePath(path)); 
					if(f.isFile()) {
						error.put("name", "模板文件已存在");
					}
					
				}
			}
		}else{
			error.put("name", "模板名称不能为空");
		}
		if(error.size() ==0){
			String fileName = prefix+selected_displayType+"_"+name.trim()+".html";//文件名称
			String pc_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"forum"+File.separator;
			
			//创建文件并将注释写入模板文件
			FileUtil.writeStringToFile(FileUtil.toRelativePath(pc_path+fileName),"<#-- "+(remark != null && remark != "" ? " "+remark+"" :"")+" -->","utf-8",false);
			
			String wap_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+"forum"+File.separator;
			
			//创建文件并将注释写入模板文件
			FileUtil.writeStringToFile(FileUtil.toRelativePath(wap_path+fileName),"<#-- "+(remark != null && remark != "" ? " "+remark+"" :"")+" -->","utf-8",false);

		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}

	/**
	 * 版块代码管理 修改
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	public String edit(ModelMap model,Integer nodeId,Integer parentId,String name,String displayType,
			String remark,String oldFileName,@RequestParam("dirName") String dirName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		//错误
		Map<String,String> error = new HashMap<String,String>();

		String prefix = "";//版块代码文件名称前缀
		
		
		
		//模板显示类型 
		List<String> displayTypeList = new ArrayList<String>();
		
		//根据节点Id查询文件名前缀
		List<ForumCodeNode> forumCodeNodeList = forumCodeManage.forumCodeNodeList(dirName);
		for(int i = 0; i<forumCodeNodeList.size(); i++){
			//二级节点
			List<ForumCodeNode> childNode = forumCodeNodeList.get(i).getChildNode();
			for(int j = 0; j< childNode.size();j++){
				if(childNode.get(j).getNodeId().equals(parentId)){
					prefix = childNode.get(j).getPrefix();
					displayTypeList.addAll(childNode.get(j).getDisplayType());
					break;
				}
			}
		}
			
		String selected_displayType = "";
		if(displayTypeList != null && displayTypeList.size() >0){
			for(int i = 0; i<displayTypeList.size(); i++){
				if(displayTypeList.get(i).equals(displayType)){
					if(displayType.equals("单层")){
						selected_displayType = "monolayer";
					}else if(displayType.equals("多层")){
						selected_displayType = "multilayer";
					}else if(displayType.equals("分页")){
						selected_displayType = "page";
					}else if(displayType.equals("实体对象")){
						selected_displayType = "entityBean";
					}else if(displayType.equals("集合")){
						selected_displayType = "collection";
					}
				}
			}
			if("".equals(selected_displayType)){
				error.put("displayType", "模板显示类型未选择");
			}
		}else{
			error.put("displayType", "模板显示类型不存在");
		}
		
		//验证模板名称
		if(name != null && !"".equals(name.trim())){
			if("monolayer".equalsIgnoreCase(name.trim()) || "multilayer".equalsIgnoreCase(name.trim()) || "page".equalsIgnoreCase(name.trim())|| "entityBean".equalsIgnoreCase(name.trim())|| "collection".equalsIgnoreCase(name.trim())){
				error.put("name", "模板名称不能为  "+name);
			}else{
				
				//验证'只能由字母或数字组成'
				if(!Verification.isNumericLetters(name.trim())){
					error.put("name", "模板名称只能由字母或数字组成 ");
				}else{
					
					if(!oldFileName.equals(prefix+selected_displayType+"_"+name.trim())){
						
						//验证文件是否存在
						String path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"forum"+File.separator+prefix+selected_displayType+"_"+name.trim()+".html";
						
						File f = new File(FileUtil.toRelativePath(path)); 
						if(f.isFile()) {
							error.put("name", "模板文件已存在");
						}
					}
					
					
				}
			}
		}else{
			error.put("name", "模板名称不能为空");
		}
		if(error.size() ==0){
			
			String newFileName = prefix+selected_displayType+"_"+name;//文件名称
			String pc_newPath = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"forum"+File.separator+newFileName+".html";
			File pc_newFile = new File(PathUtil.path()+File.separator+FileUtil.toRelativePath(pc_newPath));
			//旧路径
			String pc_oldPath = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"forum"+File.separator+oldFileName+".html";
			File pc_oldFile =new File(FileUtil.toRelativePath(pc_oldPath));   
				
			//修改文件名称
			if(pc_oldFile.renameTo(pc_newFile)){
			
				//修改备注
				FileUtil.writeStringToFile(FileUtil.toRelativePath(pc_newPath),forumCodeManage.read(PathUtil.path()+File.separator+FileUtil.toRelativePath(pc_newPath), remark),"utf-8", false);
				
				
				//修改移动端
				String wap_newPath = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+"forum"+File.separator+newFileName+".html";
				File wap_newFile = new File(PathUtil.path()+File.separator+FileUtil.toRelativePath(wap_newPath));
				//旧路径
				String wap_oldPath = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+"forum"+File.separator+oldFileName+".html";
				File wap_oldFile =new File(FileUtil.toRelativePath(wap_oldPath));   
				if(wap_oldFile.renameTo(wap_newFile)){
					//修改备注
					FileUtil.writeStringToFile(FileUtil.toRelativePath(wap_newPath),forumCodeManage.read(PathUtil.path()+File.separator+FileUtil.toRelativePath(wap_newPath), remark),"utf-8", false);
					
				}
			}
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	/**
	 * 版块代码管理 删除
	 */
	@ResponseBody
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	public String delete(String nodeName,@RequestParam("dirName") String dirName
			) throws Exception {
		
		//返回值
		Map<String,String> error = new HashMap<String,String>();
		if(dirName != null && !"".equals(dirName.trim())){
			if(nodeName != null && !"".equals(nodeName.trim())){
				String pc_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"forum"+File.separator+nodeName+".html";
				localFileManage.deleteFile(FileUtil.toRelativePath(pc_path));
				String wap_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+"forum"+File.separator+nodeName+".html";
				localFileManage.deleteFile(FileUtil.toRelativePath(wap_path));
				
			}else{
				error.put("nodeName", "节点名称不能为空");
			}
			
		}else{
			error.put("dirName", "目录名称不能为空");
		}
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	
	
	/**
	 * 版块代码管理  版块代码源码  显示
	 * @param dirName 目录
	 * @param nodeName 节点名称
	 */
	@ResponseBody
	@RequestMapping(params="method=forumSource",method=RequestMethod.GET)
	public String forumSourceUI(String dirName,String nodeName,
			ModelMap model,HttpServletRequest request, HttpServletResponse response) throws Exception {

		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(dirName != null && !"".equals(dirName.trim())){
			if(nodeName != null && !"".equals(nodeName.trim())){
				String pc_path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"forum"+File.separator+nodeName+".html";
				String wap_path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+"forum"+File.separator+nodeName+".html";

				StringBuffer pc_sb = new StringBuffer();
				File pc_f = new File(FileUtil.toRelativePath(pc_path)); 
				if(pc_f.exists()){
					
					//调用文件编码判断类
					String coding = Coding.detection(pc_f);
					try (InputStreamReader read = new InputStreamReader (new FileInputStream(pc_f),coding); 
							BufferedReader br = new BufferedReader(read);) {
						String row;
						while((row = br.readLine())!=null){
							pc_sb.append(row).append("\n");
						}
					}
					
					returnValue.put("isPCHtmlExist", true);
				}else{
					returnValue.put("isPCHtmlExist", false);
					
				}
				StringBuffer wap_sb = new StringBuffer();
				File wap_f = new File(FileUtil.toRelativePath(wap_path)); 
				if(wap_f.exists()){
					//调用文件编码判断类
					String coding = Coding.detection(wap_f);
					try (InputStreamReader read = new InputStreamReader (new FileInputStream(wap_f),coding); 
					BufferedReader br = new BufferedReader(read);) {
						String row;
						while((row = br.readLine())!=null){
							wap_sb.append(row).append("\n");
						}
					}
					
					returnValue.put("isWapHtmlExist", true);
				}else{
					returnValue.put("isWapHtmlExist", false);
				}
				
				//显示示例程序
				String example = templateManage.readExample(nodeName);
				//显示公共API
				String common = templateManage.readExample("common");
				returnValue.put("example", example);
				returnValue.put("common", common);
				
				returnValue.put("pc_html", pc_sb.toString());
				returnValue.put("wap_html", wap_sb.toString());
				
			}else{
				error.put("module", "版块模板不能为空");
			}
			
		}else{
			error.put("dirName", "目录名称不能为空");
		}
		
			
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	/**
	 * 版块代码管理  版块源码    编辑 
	 * @param dirName 目录
	 * @param nodeName 节点名称
	 */
	@ResponseBody
	@RequestMapping(params="method=forumSource",method=RequestMethod.POST)
	public String forumSourceEditor(ModelMap model,String pc_code,String wap_code,String dirName,String nodeName,
			PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(nodeName != null && !"".equals(nodeName.trim())){
			

			String pc_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"forum"+File.separator+nodeName+".html";
			String wap_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+"forum"+File.separator+nodeName+".html";
			
			FileUtil.writeStringToFile(FileUtil.toRelativePath(pc_path),pc_code,"utf-8", false);
			FileUtil.writeStringToFile(FileUtil.toRelativePath(wap_path),wap_code,"utf-8", false);
			
		}else{
			error.put("module", "版块模板不能为空");
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	

	/**
	 * 版块代码管理  版块源码显示 (版块列表模块调用)
	 * @param dirName 目录
	 * @param layoutId 布局Id
	 * @param forumId 版块Id
	 */
	@ResponseBody
	@RequestMapping(params="method=source",method=RequestMethod.GET)
	public String sourceUI(String dirName,String layoutId,Integer forumId,
			ModelMap model2,HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(forumId != null && forumId >0){
			Forum forum  = templateService.findForumById(forumId);
			if(layoutId!=null && !"".equals(layoutId)){
				if(layoutId != null && !"".equals(layoutId.trim())){
					Layout layout = templateService.findLayoutByLayoutId(layoutId);
					returnValue.put("layout", layout);
					
					if(forum.getInvokeMethod().equals(2)){//2.调用对象
						
						returnValue.put("message", "("+forum.getName()+"调用方式为\"调用对象\",没有使用当前版块源码)");
					}
				}
			
			}
			returnValue.put("forum", forum);
			
			//根据模板目录名称查询模板
			if(dirName != null && !"".equals(dirName.trim())){
				Templates templates = templateService.findTemplatebyDirName(dirName);
				returnValue.put("templates", templates);
				
				String pc_path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"forum"+File.separator+forum.getModule()+".html";
				String wap_path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+"forum"+File.separator+forum.getModule()+".html";
				
				StringBuffer pc_sb = new StringBuffer();
				File pc_f = new File(FileUtil.toRelativePath(pc_path)); 
				if(pc_f.exists()){
					
					//调用文件编码判断类
					String coding = Coding.detection(pc_f);
					try (InputStreamReader read = new InputStreamReader (new FileInputStream(pc_f),coding); 
					BufferedReader br = new BufferedReader(read);){
						String row;
						while((row = br.readLine())!=null){
							pc_sb.append(row).append("\n");
						}
					}
					
					returnValue.put("isPCHtmlExist", true);//是否存在电脑版html文件
				}else{
					returnValue.put("isPCHtmlExist", false);
				}
				StringBuffer wap_sb = new StringBuffer();
				File wap_f = new File(FileUtil.toRelativePath(wap_path)); 
				if(wap_f.exists()){
					//调用文件编码判断类
					String coding = Coding.detection(wap_f);
					try (InputStreamReader read = new InputStreamReader (new FileInputStream(wap_f),coding); 
					BufferedReader br = new BufferedReader(read);){
						String row;
						while((row = br.readLine())!=null){
							wap_sb.append(row).append("\n");
						}
					}
					
					returnValue.put("isWapHtmlExist", true);//是否存在手机版html文件
				}else{
					returnValue.put("isWapHtmlExist", false);
				}
				
				
				//显示示例程序
				String example = templateManage.readExample(forum.getModule());
				//显示公共API
				String common = templateManage.readExample("common");
				returnValue.put("pc_html", pc_sb.toString());
				returnValue.put("wap_html", wap_sb.toString());
				
				returnValue.put("example",example);
				returnValue.put("common",common);
				
			}else{
				error.put("dirName", "目录名称不能为空");
			}
			
			
			
			
		}else{
			error.put("forumId", "版块Id不能为空");
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	/**
	 * 版块代码管理  版块源码编辑 (版块列表模块调用)
	 * @param dirName 目录
	 * @param forumId 版块Id
	 */
	@ResponseBody
	@RequestMapping(params="method=source",method=RequestMethod.POST)
	public String sourceEditor(ModelMap model,String pc_code,String wap_code,String dirName,Integer forumId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(forumId != null && forumId >0){
			Forum forum = templateService.findForumById(forumId);
			if(forum != null){

				String pc_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"forum"+File.separator+forum.getModule()+".html";
				String wap_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+"forum"+File.separator+forum.getModule()+".html";
				
				FileUtil.writeStringToFile(FileUtil.toRelativePath(pc_path),pc_code,"utf-8", false);
				FileUtil.writeStringToFile(FileUtil.toRelativePath(wap_path),wap_code,"utf-8", false);
			}else{
				error.put("forumId", "版块不存在");
			}
		}else{
			error.put("forumId", "版块Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	
	
}
