package cms.web.action.template;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.template.Forum;
import cms.bean.template.Forum_CustomForumRelated_CustomHTML;
import cms.bean.template.Layout;
import cms.bean.template.TemplateData;
import cms.bean.template.Templates;
import cms.service.template.TemplateService;
import cms.utils.JsonUtils;
import cms.utils.PathUtil;
import cms.utils.RedirectPath;
import cms.utils.UUIDUtil;
import cms.utils.WebUtil;
import cms.utils.ZipUtil;
import cms.web.action.FileManage;
import cms.web.action.SystemException;
import cms.web.action.TextFilterManage;

/**
 * 模板目录管理
 *
 */
@Controller
@RequestMapping("/control/template/manage")  
public class TemplateManageAction {
	private static final Logger logger = LogManager.getLogger(TemplateManageAction.class);
	
	//注入业务bean
	@Resource(name="templateServiceBean")
	private TemplateService templateService;//通过接口引用代理返回的对象
	@Resource(name = "templateValidator") 
	private Validator validator; 
	@Resource TemplateManage templateManage;
	@Resource FileManage fileManage;
	
	@Resource TextFilterManage textFilterManage;
	
	/**
	 * 模板管理 添加模板显示
	 */
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(Templates templates)throws Exception {
		
		return "jsp/template/add_templates";
	}
	
	/**
	 * 模板管理 添加
	 */
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,Templates formbean,BindingResult result, 
			MultipartHttpServletRequest request, HttpServletResponse response)throws Exception {
		
		//数据校验
		this.validator.validate(formbean, result); 
		
		Templates templates = new Templates();
		templates.setName(formbean.getName().trim());
		templates.setDirName(formbean.getDirName().trim());
		templates.setIntroduction(formbean.getIntroduction());
		
		
		if (!result.hasErrors()) {  
			//图片上传
			List<MultipartFile> files = request.getFiles("uploadImage"); 
			for(MultipartFile file : files) {
				if(!file.isEmpty()){	
					//验证文件类型
					List<String> formatList = new ArrayList<String>();
					formatList.add("gif");
					formatList.add("jpg");
					formatList.add("jpeg");
					formatList.add("bmp");
					formatList.add("png");
					boolean authentication = fileManage.validateFileSuffix(file.getOriginalFilename(),formatList);
					if(authentication){
						//取得文件后缀		
						String ext = fileManage.getExtension(file.getOriginalFilename());
						//文件保存目录
						String pathDir = "common"+File.separator+formbean.getDirName()+File.separator;
						//构建文件名称
						String fileName = "templates." + ext;
						templates.setThumbnailSuffix(ext);
						
						//生成文件保存目录
						fileManage.createFolder(pathDir);
						//保存文件
						fileManage.writeFile(pathDir, fileName,file.getBytes());
				   }else{
					   result.rejectValue("thumbnailSuffix","errors.required", new String[]{"图片格式错误"},"");
				   }
				}
				
				break;//只有一个文件上传框
			}
		}

		if (result.hasErrors()) {  
			return "jsp/template/add_templates";
		} 
		
		
		
		List<Layout> layoutList = templateManage.newTemplate(templates.getDirName());
		templateService.saveTemplate(templates,layoutList);
		
		model.addAttribute("message","添加模板成功");//返回消息
		model.addAttribute("urladdress", RedirectPath.readUrl("control.template.list"));//返回消息//返回转向地址
		return "jsp/common/message";
	}
	
	/**
	 * 模板管理 修改模板显示
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(ModelMap model,String dirName)throws Exception {
		Templates templates = templateService.findTemplatebyDirName(dirName);
		if(templates.getThumbnailSuffix() != null && !"".equals(templates.getThumbnailSuffix().trim())){
			String imagePath = "common/"+ templates.getDirName() +"/templates."+templates.getThumbnailSuffix();
			model.addAttribute("imagePath",imagePath);
		}
		
		model.addAttribute("templates",templates);//返回消息
		
		return "jsp/template/edit_templates";
	}
	/**
	 * 模板管理 修改
	 */
	@RequestMapping(params="method=edit", method=RequestMethod.POST)
	public String edit(ModelMap model,Templates formbean,BindingResult result,
			MultipartHttpServletRequest request, HttpServletResponse response)throws Exception {
		
		Templates old_templates = templateService.findTemplatebyDirName(formbean.getDirName().trim());
		
		if(formbean.getName() == null || "".equals(formbean.getName().trim())){
			result.rejectValue("name","errors.required", new String[]{"模板名称不能为空"},"");
		}
		
		Templates templates = new Templates();
		templates.setName(formbean.getName().trim());
		templates.setDirName(formbean.getDirName().trim());
		templates.setIntroduction(formbean.getIntroduction());
		
		//标记是否删除旧图片文件
		boolean flag = false;
		
		if (!result.hasErrors()) {  
			//图片上传
			List<MultipartFile> files = request.getFiles("uploadImage"); 
			for(MultipartFile file : files) {
				if(file.isEmpty()){//如果图片已上传
					String imagePathName = request.getParameter("imagePath_1");//图片路径
					if(imagePathName != null && !"".equals(imagePathName)){
						//取得文件后缀
						String ext = fileManage.getExtension(imagePathName);
						templates.setThumbnailSuffix(ext);
						
					}else{
						flag = true;
						templates.setThumbnailSuffix(null);
					}
				}
				
				if(!file.isEmpty()){	
					
					//验证文件类型
					List<String> formatList = new ArrayList<String>();
					formatList.add("gif");
					formatList.add("jpg");
					formatList.add("jpeg");
					formatList.add("bmp");
					formatList.add("png");
					boolean authentication = fileManage.validateFileSuffix(file.getOriginalFilename(),formatList);
					if(authentication){
						//取得文件后缀		
						String ext = fileManage.getExtension(file.getOriginalFilename());
						//文件保存目录
						String pathDir = "common"+File.separator+formbean.getDirName()+File.separator;
						//构建文件名称
						String fileName = "templates." + ext;
						if(old_templates.getThumbnailSuffix() != null && !old_templates.getThumbnailSuffix().equalsIgnoreCase(ext)){
							flag = true;
						}
						templates.setThumbnailSuffix(ext);
						//生成文件保存目录
						fileManage.createFolder(pathDir);
						//保存文件
						fileManage.writeFile(pathDir, fileName,file.getBytes());
						
						
				   }else{
					   result.rejectValue("thumbnailSuffix","errors.required", new String[]{"图片格式错误"},"");
				   }
				}
				
				break;//只有一个文件上传框
			}
		}
		
		
		
		

		if (result.hasErrors()) {  
			if(old_templates.getThumbnailSuffix() != null && !"".equals(old_templates.getThumbnailSuffix().trim())){
				String imagePath = "common/"+ old_templates.getDirName() +"/templates."+old_templates.getThumbnailSuffix();
				model.addAttribute("imagePath",imagePath);
			}
			return "jsp/template/edit_templates";
		} 
		
		templateService.updateTemplate(templates);
		
		
		if(flag){//删除旧文件
			 String pathDir = "common"+File.separator+old_templates.getDirName()+File.separator+"templates."+old_templates.getThumbnailSuffix();
				
			//删除旧路径文件
			fileManage.deleteFile(pathDir);
		}
		
		model.addAttribute("message","修改模板成功");//返回消息
		model.addAttribute("urladdress", RedirectPath.readUrl("control.template.list"));//返回消息//返回转向地址
		return "jsp/common/message";
	}
	
	/**
	 * 模板管理 删除模板
	 */
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,String dirName)throws Exception {
		
		if(dirName != null && !"".equals(dirName.trim())){
			Templates templates = templateService.findTemplatebyDirName(dirName.trim());
			if(templates != null){
				//替换路径中的..号
				dirName = fileManage.toRelativePath(dirName);
				templateService.deleteTemplate(dirName.trim());
				//删除资源文件
				fileManage.removeDirectory("common"+File.separator+dirName.trim()+File.separator);
				//删除模板图片文件
				fileManage.removeDirectory("file"+File.separator+"template"+File.separator+dirName.trim()+File.separator);
				//删除模板文件
				fileManage.removeDirectory("WEB-INF"+File.separator+"templates"+File.separator + dirName.trim() + File.separator);
				return "1";
			}
		}
		
		return "0";
	}
	
	/**
	 * 模板管理 导出模板
	 * @param model
	 * @param dirName 模板目录名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=export", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String export(ModelMap model,String dirName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(dirName == null || "".equals(dirName.trim())){
			return "0";
		}
		//替换路径中的..号
		dirName = fileManage.toRelativePath(dirName);
		
		//构建文件名称
		String tempDirName = UUIDUtil.getUUID32();
		
		//创建临时目录
		String pathDir = "WEB-INF"+File.separator+"data"+File.separator + "temp" + File.separator+ tempDirName +File.separator+dirName+ File.separator;
				
		//取得文件保存目录的真实路径
		String realpathDir = PathUtil.path()+File.separator+pathDir;
		File saveDir = new File(realpathDir);//生成目录
		if (!saveDir.exists()) {//如果目录不存在
			 saveDir.mkdirs();//生成目录
		}
		
		TemplateData templateData = new TemplateData();
		//读取模板数据
		Templates templates = templateService.findTemplatebyDirName(dirName);
		//读取布局数据
		List<Layout> layoutList = templateService.findLayout(dirName);
		//读取版块数据
		List<Forum> forumList = templateService.findForumByDirName(dirName);
		templateData.setTemplates(templates);
		templateData.setLayoutList(layoutList);
		templateData.setForumList(forumList);
		//备份到临时目录
		fileManage.writeStringToFile(pathDir+"templateData.data",JsonUtils.toJSONString(templateData),"UTF-8", false);
		
		//创建模板目录
		String template_dir_path = realpathDir+"templates"+File.separator;
		File template_dir = new File(template_dir_path);//生成目录
		if (!template_dir.exists()) {//如果目录不存在
			template_dir.mkdirs();//生成目录
		}
		//创建资源文件目录
		String file_dir_path = realpathDir+"common"+File.separator;
		File file_dir = new File(file_dir_path);//生成目录
		if (!file_dir.exists()) {//如果目录不存在
			file_dir.mkdirs();//生成目录
		}
		//创建模板上传文件目录
		String upload_dir_path = realpathDir+"uploadFile"+File.separator;
		File upload_dir = new File(upload_dir_path);//生成目录
		if (!upload_dir.exists()) {//如果目录不存在
			upload_dir.mkdirs();//生成目录
		}
		
		//模板源目录
		String template_source = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator;	
		//复制模板文件到临时目录
		fileManage.copyDirectory(template_source, pathDir+"templates"+File.separator);
			
		//资源文件源目录
		String file_source = "common"+File.separator+dirName+File.separator;
		//复制模板文件到临时目录
		fileManage.copyDirectory(file_source, pathDir+"common"+File.separator);
		
		//模板上传文件源目录
		String upload_source = "file"+File.separator+"template"+File.separator+dirName+File.separator;
		//复制模板文件到临时目录
		fileManage.copyDirectory(upload_source, pathDir+"uploadFile"+File.separator);
		
		
		SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		
		//压缩文件名称
		String zipName = dirName+"_"+dateformat.format(new Date())+".zip";
		
		//压缩文件
		
	
		ZipUtil.pack(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+File.separator + "temp" + File.separator+ tempDirName +File.separator, 
				PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+ File.separator+"templateBackup"+ File.separator+zipName);//第一个参数：待压缩目录  第二个参数：输出文件
	
		//删除临时文件
		fileManage.removeDirectory("WEB-INF"+File.separator+"data"+File.separator + "temp" + File.separator+ tempDirName +File.separator);
		
		return "1";
	}
	

	/**
	 * 模板管理 删除导出模板
	 * @param model
	 * @param fileName 模板备份文件名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=deleteExport", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String deleteExport(ModelMap model,String fileName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(fileName != null && !"".equals(fileName.trim())){
			//替换路径中的..号
			fileName = fileManage.toRelativePath(fileName);
			//模板文件路径
			String templateFile_path = "WEB-INF"+File.separator+"data"+ File.separator+"templateBackup"+ File.separator+fileName;
			fileManage.deleteFile(templateFile_path);
			return "1";
		}
		return "0";
	}
	
	/**
	 * 模板管理 下载导出模板
	 * @param model
	 * @param fileName 模板备份文件名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	*/
	@RequestMapping(params="method=templateDownload", method=RequestMethod.GET)
	public ResponseEntity<byte[]> templateDownload(ModelMap model,String fileName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if(fileName == null || "".equals(fileName.trim())){
			throw new SystemException("文件不名称不能为空！");
		}
		//替换路径中的..号
		fileName = fileManage.toRelativePath(fileName); 
		
		String templateFile_path = "WEB-INF"+File.separator+"data"+ File.separator+"templateBackup"+ File.separator+fileName;

	    File file = new File(PathUtil.path()+File.separator+templateFile_path);
	    
	    
	  
        return WebUtil.downloadResponse(FileUtils.readFileToByteArray(file), fileName,request);
	}
	
	
	
	/**
	 * 模板管理  导入模板
	 * @param model
	 * @param templateFileName 模板文件名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=import", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String importTemplate(ModelMap model,String templateFileName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(templateFileName == null || "".equals(templateFileName.trim())){
			return "-1";
		}
		//替换路径中的..号
		templateFileName = fileManage.toRelativePath(templateFileName);
		
		//模板文件路径
		String templateFile_path = "WEB-INF"+File.separator+"data"+ File.separator+"templateBackup"+ File.separator+templateFileName;
		//临时目录名称
		String tempDirName = UUIDUtil.getUUID32();
		//临时目录路径
		String temp_dir_path = "WEB-INF"+File.separator+"data"+File.separator + "temp" + File.separator+ tempDirName + File.separator;
		
		
		
		//读取模板文件
		File templateFile = new File(PathUtil.path()+File.separator+templateFile_path);
		
		if (templateFile.isFile()) {//如果文件存在
			
			//解压到临时目录
			ZipUtil.unZip(PathUtil.path()+File.separator+templateFile_path, PathUtil.path()+File.separator+temp_dir_path);
			//模板目录名称
			String templateDirName = "";
			
			File temp_dir_path_file = new File(PathUtil.path()+File.separator+temp_dir_path);
			File[] dirs = temp_dir_path_file.listFiles(); 
			for (File f : dirs) {
				if (f.isDirectory()) {
					templateDirName = f.getName();
					break;
				} 
			}
			
			if(templateDirName != null && !"".equals(templateDirName.trim())){
				//替换路径中的..号
				templateDirName = fileManage.toRelativePath(templateDirName);
				//验证目录是否重复
				Templates t = templateService.findTemplatebyDirName(templateDirName.trim());
				if(t != null){
					//删除临时文件
					fileManage.removeDirectory("WEB-INF"+File.separator+"data"+File.separator + "temp" + File.separator+ tempDirName +File.separator);
					
					return "-3";
				}
				
				//读取模板数据
				String templateData_str = fileManage.readFileToString(temp_dir_path+templateDirName+File.separator+"templateData.data","UTF-8");
				
				if(templateData_str != null && !"".equals(templateData_str.trim())){
					TemplateData templateData = JsonUtils.toGenericObject(templateData_str, new TypeReference<TemplateData>(){});		
					Map<String,String> newPrimaryKey = new HashMap<String,String>();//布局数据新主键
					Templates templates = templateData.getTemplates();
					//旧模板目录名称
					String oldDirName = "";
					
					if(templates != null){
						//重设主键
						templates.setId(null);
						oldDirName = templates.getDirName();
						//重设模板目录名称
						templates.setDirName(templateDirName);
						//设置模板没选中
						templates.setUses(false);
					}
					List<Layout> layoutList = templateData.getLayoutList();
					if(layoutList != null && layoutList.size() >0){
						for(Layout layout : layoutList){
							String layoutId = UUIDUtil.getUUID32();
							newPrimaryKey.put(layout.getId(), layoutId);
							//重设布局主键
							layout.setId(layoutId);
							//重设模板目录名称
							layout.setDirName(templateDirName);
							
							     
						}
					}
					List<Forum> forumList = templateData.getForumList();
					if(forumList != null && forumList.size() >0){
						for(Forum forum : forumList){
							//重设版块主键
							forum.setId(null);
							//重设模板目录名称
							forum.setDirName(templateDirName);
							//重设布局Id
							forum.setLayoutId(newPrimaryKey.get(forum.getLayoutId()));
							
							//更改用户自定义HTML上传文件的模板目录
							if("自定义版块".equals(forum.getForumType()) && "用户自定义HTML".equals(forum.getForumChildType())){
								
								String formValueJSON = forum.getFormValue();//表单值
								if(formValueJSON != null && !"".equals(formValueJSON)){
									
									Forum_CustomForumRelated_CustomHTML forum_CustomForumRelated_CustomHTML = JsonUtils.toObject(formValueJSON, Forum_CustomForumRelated_CustomHTML.class);
									
									
									if(forum_CustomForumRelated_CustomHTML != null){
										if(forum_CustomForumRelated_CustomHTML.getHtml_content() != null && !"".equals(forum_CustomForumRelated_CustomHTML.getHtml_content().trim())){
											
										}
										String content = textFilterManage.updatePathName(forum_CustomForumRelated_CustomHTML.getHtml_content(),oldDirName,templateDirName);
										
										forum_CustomForumRelated_CustomHTML.setHtml_content(content);
										
										forum.setFormValue(JsonUtils.toJSONString(forum_CustomForumRelated_CustomHTML));//加入表单值
										
									}
								}
							}
							
							
						}
					}
					templateService.saveTemplateData(templateData);
				}
				
				
				//templates目录的下一级目录名称
				String next_templates_dirName = "";
				File next_templates_temp_dir_path_file = new File(PathUtil.path()+File.separator+temp_dir_path+templateDirName+File.separator+"templates"+File.separator);
				File[] next_templates_dirs = next_templates_temp_dir_path_file.listFiles(); 
				for (File f : next_templates_dirs) {
					if (f.isDirectory()) {
						next_templates_dirName = f.getName();
						break;
					} 
				}
				
				//common目录的下一级目录名称
				String next_common_dirName = "";
				File next_common_temp_dir_path_file = new File(PathUtil.path()+File.separator+temp_dir_path+templateDirName+File.separator+"common"+File.separator);
				File[] next_common_dirs = next_common_temp_dir_path_file.listFiles(); 
				for (File f : next_common_dirs) {
					if (f.isDirectory()) {
						next_common_dirName = f.getName();
						break;
					} 
				}
				//uploadFile目录的下一级目录名称
				String next_uploadFile_dirName = "";
				File next_uploadFile_temp_dir_path_file = new File(PathUtil.path()+File.separator+temp_dir_path+templateDirName+File.separator+"uploadFile"+File.separator);
				File[] next_uploadFile_dirs = next_uploadFile_temp_dir_path_file.listFiles(); 
				for (File f : next_uploadFile_dirs) {
					if (f.isDirectory()) {
						next_uploadFile_dirName = f.getName();
						break;
					} 
				}
				
				if(next_templates_dirName != null && !"".equals(next_templates_dirName.trim())){
					//模板源目录
					String template_source = temp_dir_path+templateDirName+File.separator+"templates"+File.separator+next_templates_dirName+File.separator;	
					
					//重命名文件夹名称,和模板名称一致
					fileManage.renameFile(template_source, templateDirName);
					
					//复制模板文件到模板目录
					fileManage.copyDirectory(temp_dir_path+templateDirName+File.separator+"templates"+File.separator+templateDirName+File.separator, "WEB-INF"+File.separator+"templates");
				}
				if(next_common_dirName != null && !"".equals(next_common_dirName.trim())){
					String file_source = temp_dir_path+templateDirName+File.separator+"common"+File.separator+next_common_dirName+File.separator;
					//重命名文件夹名称,和模板名称一致
					fileManage.renameFile(file_source, templateDirName);
					//复制模板资源文件到文件目录
					fileManage.copyDirectory(temp_dir_path+templateDirName+File.separator+"common"+File.separator+templateDirName+File.separator, "common");
					
				}
				if(next_uploadFile_dirName != null && !"".equals(next_uploadFile_dirName.trim())){
					String upload_source = temp_dir_path+templateDirName+File.separator+"uploadFile"+File.separator+next_common_dirName+File.separator;
					//重命名文件夹名称,和模板名称一致
					fileManage.renameFile(upload_source, templateDirName);
					//复制模板上传文件到文件目录
					fileManage.copyDirectory(temp_dir_path+templateDirName+File.separator+"uploadFile"+File.separator+templateDirName+File.separator, "file"+File.separator+"template");
					
				}
				
				
				
				//删除临时文件
				fileManage.removeDirectory("WEB-INF"+File.separator+"data"+File.separator + "temp" + File.separator+ tempDirName +File.separator);
				
				
			}
		}else{	
			return "-2";
		}
		
		
		return "1";
	}
	/**
	 * 模板管理 导入模板列表
	 */
	@RequestMapping(params="method=importTemplateList",method=RequestMethod.GET)
	public String importTemplateList(ModelMap model
			)throws Exception {
		List<Templates> templatesList = new ArrayList<Templates>();
		
		//模板目录
		String pathDir = "WEB-INF"+File.separator+"data"+ File.separator+"templateBackup"+ File.separator;
		
		String[] extensions = null;//后缀名{"doc", "pdf"}
		boolean recursive = false;//是否递归
		Collection<File> files = FileUtils.listFiles(new File(PathUtil.path()+File.separator+pathDir), extensions, recursive);
		
		
		// 迭代输出
		for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
		    File file = iterator.next();
		   
		    //读取压缩文件
		    ZipFile zip = null;
			try {
				zip = new ZipFile(file);
				Templates templates = new Templates();
				
				templates.setFileName(file.getName());
				Enumeration<ZipArchiveEntry> entry = zip.getEntries();
				A:while(entry.hasMoreElements()){//依次访问各条目
					ZipArchiveEntry ze = entry.nextElement();  
					String fileName = fileManage.getName(ze.getName());//文件名称
					
					if(templates.getDirName() == null || "".equals(templates.getDirName())){		
						//截取到等于第二个参数的字符串为止
						templates.setDirName(StringUtils.substringBefore(ze.getName(), "/"));	
					}
					
					 //读取配置文件
				    if("templateData.data".equals(fileName)){
				    		
						InputStream in = zip.getInputStream(ze);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			    			int i;  
			    			while ((i = in.read()) != -1) {  
			    			    baos.write(i);  
			    			}  
			    			String str = baos.toString("utf-8"); 
			    			if(str != null && !"".equals(str)){
			    				
			    				
			    				
			    				TemplateData templateData = JsonUtils.toGenericObject(str, new TypeReference<TemplateData>(){});
			    				if(templateData != null){
			    					if(templateData.getTemplates() != null){
			    						templates.setName(templateData.getTemplates().getName());
			    		    			templates.setIntroduction(templateData.getTemplates().getIntroduction());
			    					}
			    				}
			    			
			    			}	
							
						break A;
				    }
				}
				templatesList.add(templates);
			} catch (Exception e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("导入模板列表",e);
		        }
			}finally{
				if(zip != null){
					zip.close();
				}
				
			}
			
		}
		
		
		
		
		
		model.addAttribute("templatesList",templatesList);
		return "jsp/template/importTemplateList";
	}
	
	/**
	 * 目录重命名
	 * @param model
	 * @param fileName 文件名称
	 * @param directoryName 新目录名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=directoryRename",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String directoryRename(ModelMap model,String fileName,String directoryName,
			HttpServletRequest request, HttpServletResponse response)throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();//错误
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
		
		if(directoryName != null && !"".equals(directoryName.trim())){
			if(directoryName.length() >40){
				error.put("directoryName", "目录名称过长");
			}else{
				//只能输入字母或下划线
				if(!directoryName.matches("^[a-zA-Z][a-zA-Z0-9|_]{2,40}$")){
					error.put("directoryName", "只能由字母或数字或下划线组成,并且开头为字母，总长度大于两个字符");
				}else{
					//验证目录是否重复
					Templates t = templateService.findTemplatebyDirName(directoryName.trim());
					if(t != null){
						error.put("directoryName", "模板目录不能重复");
					}
				}
			}
		}else{
			error.put("directoryName", "新目录名称不能为空");
		}
		
		if(fileName != null && !"".equals(fileName.trim())){
			if(error.size() == 0){

				//替换路径中的..号
				fileName = fileManage.toRelativePath(fileName.trim());
				
				//模板文件路径
				String templateFile_path = "WEB-INF"+File.separator+"data"+ File.separator+"templateBackup"+ File.separator+fileName;
				//临时目录名称
		    	String tempDirName = UUIDUtil.getUUID32();
		    	//临时目录路径
		    	String temp_dir_path = "WEB-INF"+File.separator+"data"+File.separator + "temp" + File.separator+ tempDirName + File.separator;
		    	
				//读取模板文件
				File templateFile = new File(PathUtil.path()+File.separator+templateFile_path);
				
				if (templateFile.isFile()) {//如果文件存在
					//解压到临时目录
					ZipUtil.unZip(PathUtil.path()+File.separator+templateFile_path, PathUtil.path()+File.separator+temp_dir_path);
					
					
					//临时目录的下一级目录名称
					String next_temp_dirName = "";
					File next_temp_dir_path_file = new File(PathUtil.path()+File.separator+temp_dir_path+File.separator);
					File[] next_templates_dirs = next_temp_dir_path_file.listFiles(); 
					for (File f : next_templates_dirs) {
						if (f.isDirectory()) {
							next_temp_dirName = f.getName();
							break;
						} 
					}
					//想命名的原文件夹的路径  
			        File sourceFolder = new File(PathUtil.path()+File.separator+temp_dir_path+next_temp_dirName+File.separator);  
			        
			        //将原文件夹名称更改新文件夹名称
			        sourceFolder.renameTo(new File(PathUtil.path()+File.separator+temp_dir_path+directoryName+File.separator)); 
	
					//压缩文件
					ZipUtil.pack(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+File.separator + "temp" + File.separator+ tempDirName +File.separator, 
							PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+ File.separator+"templateBackup"+ File.separator+fileName);//第一个参数：待压缩目录  第二个参数：输出文件
								
		
					//删除临时文件
					fileManage.removeDirectory("WEB-INF"+File.separator+"data"+File.separator + "temp" + File.separator+ tempDirName +File.separator);
				}
				
			}
		}else{
			error.put("directoryRename", "文件不存在");
		}
		
		if(error != null && error.size() >0){
			returnValue.put("success", "false");
			returnValue.put("error", error);
		}else{
			returnValue.put("success", "true");
			
		}

		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
		return null;
	}
	
	/**
	 * 模板文件上传
	 * @param resourceId 资源Id
	 * @param dirName 模板目录
	 */
	@RequestMapping(params="method=upload",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String upload(ModelMap model,
			MultipartFile uploadFile,
			HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		Map<String,Object> returnJson = new HashMap<String,Object>();
		Map<String,String> error = new HashMap<String,String>();
		
		String path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+ File.separator+"templateBackup";
		
		File file = new File(path);
		
		
	   if (!file.exists()) {//如果目录不存在
		   file.mkdirs();//生成目录
	   }
		if(file.isDirectory() && uploadFile != null && !uploadFile.isEmpty()){//如果是目录
			
			//文件大小
			Long size = uploadFile.getSize();
			//允许上传格式
			List<String> formatList = new ArrayList<String>();
			formatList.add("zip");
			//允许上传大小
			long uploadSize = 204800000L;//单位为字节  例1024000为1M
			
			//验证文件后缀
			boolean authentication = fileManage.validateFileSuffix(uploadFile.getOriginalFilename(),formatList);
			if(authentication == false){
				error.put("uploadFile", "当前文件格式不允许上传");
			}
			if(size > uploadSize){
				error.put("uploadFile", "文件大小超出200M");
			}
			
			String newFilename = "";
			
			//判断文件是否存在
			File templateFile = new File(path+File.separator+uploadFile.getOriginalFilename());
			
			if (templateFile.isFile()) {//如果文件存在
				
				//
				char[] character = { '1', '2', '3', '4', '5', '6', '7', '8', '9','0',
						'a','b','c','d','e','f','g','h','j','k','m','n','p','q','r','s','t','u','v','w','x','y'};
				//使用指定的字符生成1位长度的随机字符串
		        String text = RandomStringUtils.random(2, character);
		       
				newFilename =  fileManage.getBaseName(uploadFile.getOriginalFilename())+"_"+text+"."+fileManage.getExtension(uploadFile.getOriginalFilename());
				
				File _templateFile = new File(path+File.separator+newFilename);
				if(_templateFile.isFile()){
					error.put("uploadFile", "文件名称重复,请重命名再上传");
				}
				if(newFilename.length()>50){
					error.put("uploadFile", "文件名称过长");
				}
			}else{
				newFilename = uploadFile.getOriginalFilename();
			}
			
			if(error.size()==0 && authentication && size <= uploadSize){
				
				FileOutputStream fileoutstream = null;
				try {
				
					//文件输出流
					fileoutstream = new FileOutputStream(new File(path, fileManage.toRelativePath(fileManage.toSystemPath(newFilename))));
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
	 * 模板管理 设置当前使用的模板
	 * @param model
	 * @param dirName 模板目录名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=setTemplate",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String setTemplate(ModelMap model,String dirName,
			HttpServletRequest request, HttpServletResponse response)throws Exception {
		
		if(dirName != null && !"".equals(dirName.trim())){
			templateService.useTemplate(dirName.trim());
			return "1";
		}
		return "0";
	}
}
