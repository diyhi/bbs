package cms.web.action.template;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.service.template.TemplateService;
import cms.utils.Coding;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.PathUtil;
import cms.utils.WebUtil;
import cms.web.action.SystemException;
import cms.web.action.fileSystem.localImpl.LocalFileManage;

import org.apache.commons.io.FileUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


/**
 * 资源管理
 *
 */
@Controller
@RequestMapping("/control/resource/manage") 
public class ResourceManageAction {
	@Resource TemplateService templateService;
	@Resource LocalFileManage localFileManage;
	
	/**
	 * 资源管理 文件查看
	 * @param resourceId 资源Id
	 * @param dirName 模板目录
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=showFileUI", method=RequestMethod.GET)
	public String showFileUI(String resourceId,String dirName,
			ModelMap model,HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		if(resourceId != null && !"".equals(resourceId.trim()) && dirName != null && !"".equals(dirName.trim())){

			String path = PathUtil.path()+File.separator+"common"+File.separator+FileUtil.toRelativePath(dirName)+File.separator+FileUtil.toRelativePath(FileUtil.toSystemPath(resourceId));
			
			
			String suffix = FileUtil.getExtension(path);
			if(suffix != null && !"".equals(suffix.trim())){//如果是js,css后缀文件
				if("js".equalsIgnoreCase(suffix) || "css".equalsIgnoreCase(suffix)){
					
					StringBuffer sb = new StringBuffer();
					File file = new File(path); 
					if(file.exists()){
						
						//调用文件编码判断类
						String coding = Coding.detection(file);
						InputStreamReader read = new InputStreamReader (new FileInputStream(file),coding); 
						BufferedReader br = new BufferedReader(read);
						String row;
						while((row = br.readLine())!=null){
							sb.append(row).append("\n");
						}
					}else{
						throw new SystemException("系统找不到指定的文件");
					}	
					model.addAttribute("fileContent",sb.toString());
					model.addAttribute("fileType",suffix.toLowerCase());
					
				}
			}	
		}
		
		
		return "jsp/template/resource_showFile";
	}
	
	/**
	 * 资源管理 编辑文件
	 * @param resourceId 资源Id
	 * @param dirName 模板目录
	 * @param content 文件内容
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=editFile", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String editFile(String resourceId,String dirName,String content,
			ModelMap model,HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String,Object> returnJson = new HashMap<String,Object>();
		Map<String,String> error = new HashMap<String,String>();
		if(resourceId != null && !"".equals(resourceId.trim()) && dirName != null && !"".equals(dirName.trim())){

			String pathName = "common"+File.separator+FileUtil.toRelativePath(dirName)+File.separator+FileUtil.toRelativePath(FileUtil.toSystemPath(resourceId));
			String fullPathName = PathUtil.path()+File.separator+pathName;
			
			
			String suffix = FileUtil.getExtension(fullPathName);
			if(suffix != null && !"".equals(suffix.trim())){//如果是js,css后缀文件
				if("js".equalsIgnoreCase(suffix) || "css".equalsIgnoreCase(suffix)){
					
					File file = new File(fullPathName);
					if(file.exists() && !file.isDirectory()){//如果是文件
						FileUtil.writeStringToFile(pathName,content,"utf-8", false);
						
					}else{
						error.put("resource", "文件不存在");
					}
				}else{
					error.put("resource", "不是js或css后缀的文件");
				}
			}else{
				error.put("resource", "文件后缀不能为空");
			}
		}else{
			error.put("resource", "资源Id或模板目录不能为空");
		}
		
		
		if(error.size() >0){
			//上传失败
			returnJson.put("error", error);
			returnJson.put("success", false);
		}else{
			returnJson.put("success", true);
		}
		
		return JsonUtils.toJSONString(returnJson);
	}
	/**
	 * 资源管理 下载
	 * @param model
	 * @param resourceId 资源Id
	 * @param dirName 模板目录
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	*/
	@RequestMapping(params="method=download", method=RequestMethod.GET)
	public ResponseEntity<byte[]> download(ModelMap model,String resourceId,String dirName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		if(resourceId != null && !"".equals(resourceId.trim()) && dirName != null && !"".equals(dirName.trim())){
			String path = PathUtil.path()+File.separator+"common"+File.separator+FileUtil.toRelativePath(dirName)+File.separator+FileUtil.toRelativePath(FileUtil.toSystemPath(resourceId));
			
			String fileName = FileUtil.getName(path);//获取文件名,含后缀
			
			File file = new File(path);
			return WebUtil.downloadResponse(FileUtils.readFileToByteArray(file), fileName,request);

			
			
		}else{
			throw new SystemException("文件不名称不能为空！");
		}
	}
	
	
	/**
	 * 资源管理 新建文件夹
	 * @param model
	 * @param resourceId 资源Id
	 * @param dirName 模板目录
	 * @param folderName 新文件夹名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=newFolder", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String newFolder(ModelMap model,String resourceId,String dirName,String folderName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String,Object> returnJson = new HashMap<String,Object>();
		Map<String,String> error = new HashMap<String,String>();

		if(dirName != null && !"".equals(dirName.trim())){
			String path = PathUtil.path()+File.separator+"common"+File.separator+FileUtil.toRelativePath(dirName)+File.separator+FileUtil.toRelativePath(FileUtil.toSystemPath(resourceId));
			
			boolean validateFolderName = FileUtil.validateFileName(folderName);
			if(validateFolderName == false){
				error.put("folderName", "名称不能含有特殊字符");
				
			}
			
			File file = new File(path);
			if (!file.exists()) {//如果目录不存在
				 file.mkdirs();//生成目录
			}
			if(file.isDirectory()){//如果是目录
				if(validateFolderName == true){
					String newFolder = "common"+File.separator+FileUtil.toRelativePath(dirName)+File.separator+FileUtil.toRelativePath(FileUtil.toSystemPath(resourceId)+File.separator+FileUtil.toRelativePath(FileUtil.toSystemPath(folderName)));
					boolean flag = FileUtil.createFolder(newFolder);
					if(!flag){
						error.put("folderName", "新建文件夹错误");
					}
				}
							
			}else{
				error.put("folderName", "路径不是文件夹");
			}
		}
		
		if(error.size() >0){
			//上传失败
			returnJson.put("error", error);
			returnJson.put("success", false);
		}else{
			returnJson.put("success", true);
		}
		
		return JsonUtils.toJSONString(returnJson);
	}
	
	/**
	 * 资源管理  文件上传
	 * @param resourceId 资源Id
	 * @param dirName 模板目录
	 */
	@RequestMapping(params="method=upload",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String upload(ModelMap model,String resourceId,String dirName,
			MultipartFile uploadFile,
			HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		Map<String,Object> returnJson = new HashMap<String,Object>();
		Map<String,String> error = new HashMap<String,String>();
		if(dirName != null && !"".equals(dirName.trim())){
			String path = PathUtil.path()+File.separator+"common"+File.separator+FileUtil.toRelativePath(dirName)+(resourceId == null || "".equals(resourceId.trim()) ? "" :File.separator+FileUtil.toRelativePath(FileUtil.toSystemPath(resourceId)));
			
			File file = new File(path);
			
			
		   if (!file.exists()) {//如果目录不存在
			   file.mkdirs();//生成目录
		   }
			
			if(file.isDirectory()){//如果是目录
				
				//文件大小
				Long size = uploadFile.getSize();
				//允许上传格式
				List<String> formatList = new ArrayList<String>();
				formatList.add("gif");
				formatList.add("jpg");
				formatList.add("bmp");
				formatList.add("png");
				formatList.add("css");
				formatList.add("js");
				//允许上传大小
				long uploadSize = 2048000L;//单位为字节  例1024000为1M
				
				//验证文件后缀
				boolean authentication = FileUtil.validateFileSuffix(uploadFile.getOriginalFilename(),formatList);
				if(authentication == false){
					error.put("uploadFile", "当前文件格式不允许上传");
				}
				if(size > uploadSize){
					error.put("uploadFile", "文件大小超出2M");
				}
				
				
				if(authentication && size <= uploadSize){
					
					FileOutputStream fileoutstream = null;
					try {
					
						//文件输出流
						fileoutstream = new FileOutputStream(new File(path, FileUtil.toRelativePath(FileUtil.toSystemPath(uploadFile.getOriginalFilename()))));
						//写入硬盘
						fileoutstream.write(uploadFile.getBytes());
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
					//	e.printStackTrace();
						error.put("uploadFile", "文件上传出错");
					}finally{
						if(fileoutstream != null){
							fileoutstream.close();
						}
					}
					
				}
				
			}
		}

		if(error.size() >0){
			//上传失败
			returnJson.put("error", error);
			returnJson.put("success", false);
		}else{
			returnJson.put("success", true);
		}
		return JsonUtils.toJSONString(returnJson);
	}
	/**
	 * 资源管理  重命名
	 * @param resourceId 资源Id
	 * @param rename 重命名
	 * @param dirName 模板目录
	 */
	@RequestMapping(params="method=rename",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String rename(ModelMap model,String resourceId,String rename,String dirName,
			HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		Map<String,Object> returnJson = new HashMap<String,Object>();
		Map<String,String> error = new HashMap<String,String>();
		if(resourceId != null && !"".equals(resourceId.trim()) && dirName != null && !"".equals(dirName.trim())){
			if(rename != null && !"".equals(rename.trim())){
				
				String path = PathUtil.path()+File.separator+"common"+File.separator+FileUtil.toRelativePath(dirName)+File.separator+FileUtil.toRelativePath(FileUtil.toSystemPath(resourceId));
				String suffix = FileUtil.getExtension(path);//获取后缀
				boolean validateFolderName = FileUtil.validateFileName(rename);
				if(validateFolderName == false){
					error.put("rename", "名称不能含有特殊字符");
				}
				
				File file = new File(path);
				if(file.isFile()){//文件
					if(validateFolderName == true){
						String resPath = "common"+File.separator+FileUtil.toRelativePath(dirName)+File.separator+FileUtil.toRelativePath(FileUtil.toSystemPath(resourceId));
						boolean flag = FileUtil.renameFile(resPath,rename+"."+suffix);
						if(!flag){
							error.put("rename", "重命名错误");
						}else{
							 //源文件父路径
					        String resParentPath = file.getParent();   
					        File newFile = new File(resParentPath+File.separator+rename+"."+suffix);   
							
							//将当前节点作结果返回
					        cms.bean.template.Resource resource =  new cms.bean.template.Resource();
							if(resourceId == null || "".equals(resourceId.trim())){
								resource.setId(newFile.getName());
							}else{
								resource.setId(resourceId+"/"+newFile.getName());
							}
					    	resource.setLastModified(new Date(newFile.lastModified()));
							if(newFile.isDirectory() == true){//是目录
								resource.setLeaf(false);//不是叶子节点
							}else{
								resource.setLeaf(true);//是叶子节点
							}
							resource.setName(newFile.getName());
							returnJson.put("resource", resource);
							
						}
					}
								
				}else if(file .isDirectory()){//目录
					if(validateFolderName == true){
						
						
						String resPath = "common"+File.separator+FileUtil.toRelativePath(dirName)+File.separator+FileUtil.toRelativePath(FileUtil.toSystemPath(resourceId));
						boolean flag = FileUtil.renameFile(resPath,rename);
						if(!flag){
							error.put("rename", "重命名错误");
						}else{
							 //源文件父路径
					        String resParentPath = file.getParent();   
					        File newFile = new File(resParentPath+File.separator+rename);
							
							//将当前节点作结果返回
					        cms.bean.template.Resource resource =  new cms.bean.template.Resource();
							if(resourceId == null || "".equals(resourceId.trim())){
								resource.setId(newFile.getName());
							}else{
								resource.setId(resourceId+"/"+newFile.getName());
							}
					    	resource.setLastModified(new Date(newFile.lastModified()));
							if(newFile.isDirectory() == true){//是目录
								resource.setLeaf(false);//不是叶子节点
							}else{
								resource.setLeaf(true);//是叶子节点
							}
							resource.setName(newFile.getName());
							returnJson.put("resource", resource);
						}
					}
								
				}else{
					error.put("rename", "文件或文件夹不存在");
				}
			}else{
				error.put("rename", "请填写目录名称");
			}
			
		}else{
			error.put("rename", "参数错误");
		}
		if(error.size() >0){
			//上传失败
			returnJson.put("error", error);
			returnJson.put("success", false);
		}else{
			returnJson.put("success", true);
		}
		return JsonUtils.toJSONString(returnJson);
	}
	/**
	 * 资源管理  删除
	 * @param resourceId 资源Id
	 * @param dirName 模板目录
	 */
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,String resourceId,String dirName,
			HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		if(resourceId != null && !"".equals(resourceId.trim()) && dirName != null && !"".equals(dirName.trim())){
			
			String path = "common"+File.separator+FileUtil.toRelativePath(dirName)+File.separator+FileUtil.toRelativePath(FileUtil.toSystemPath(resourceId));
			
			File file = new File(PathUtil.path()+File.separator+path);
			if(file.isFile()){//文件
				
				localFileManage.deleteFile(path);
				return "1";
			}else if(file .isDirectory()){//目录
				localFileManage.removeDirectory(path);
				return "1";
			}
			
		
		}
		return "0";
	}
}
