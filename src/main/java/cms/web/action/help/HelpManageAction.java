package cms.web.action.help;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.help.Help;
import cms.bean.help.HelpType;
import cms.service.help.HelpService;
import cms.service.help.HelpTypeService;
import cms.service.setting.SettingService;
import cms.utils.CommentedProperties;
import cms.utils.FileType;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.FileManage;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


/**
 * 帮助管理
 *
 */
@Controller
@RequestMapping("/control/help/manage") 
public class HelpManageAction {
	private static final Logger logger = LogManager.getLogger(HelpManageAction.class);
	
	@Resource HelpService helpService; 
	@Resource HelpTypeService helpTypeService;
	@Resource TextFilterManage textFilterManage;
	@Resource SettingService settingService;
	@Resource FileManage fileManage;
	
	/**
	 * 帮助   查看
	 */
	@ResponseBody
	@RequestMapping(params="method=view",method=RequestMethod.GET)
	public String view(Long helpId,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
				
		if(helpId != null && helpId >0L){
			Help help = helpService.findById(helpId);
			if(help != null){
				//处理富文本路径
				help.setContent(fileManage.processRichTextFilePath(help.getContent(),"help"));
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,help));	
			}else{
				error.put("helpId", "帮助不存在");
			}
			
			
		}else{
			error.put("helpId", "帮助Id不存在");
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 帮助   添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(Help help,Long helpTypeId,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
	}
	
	/**
	 * 帮助  添加
	 * @param model
	 * @param helpTypeId
	 * @param helpTypeName
	 * @param name
	 * @param content
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,Long helpTypeId,String helpTypeName, String name,String content,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		Help help = new Help();
		List<String> imageNameList = null;
		boolean isImage = false;//是否含有图片
		List<String> flashNameList = null;
		boolean isFlash = false;//是否含有Flash
		List<String> mediaNameList = null;
		boolean isMedia = false;//是否含有音视频
		List<String> fileNameList = null;
		boolean isFile = false;//是否含有文件
		boolean isMap = false;//是否含有地图
		
		//复制文件锁
		List<String> fileLock_list = new ArrayList<String>();
		List<String> oldPathFileList = new ArrayList<String>();//旧路径文件
		
		help.setHelpTypeId(helpTypeId);
		help.setHelpTypeName(helpTypeName);
		
		if(helpTypeId == null || helpTypeId <=0L){
			error.put("helpTypeId", "帮助分类不能为空");
		}else{
			
			if(name != null && !"".equals(name.trim())){
				help.setName(name);
				if(name.length() >50){
					error.put("name", "不能大于100个字符");
				}
			}else{
				error.put("name", "帮助名称不能为空");
			}
			if(content != null && !"".equals(content.trim())){
				//过滤标签
				content = textFilterManage.filterTag(request,content);
				Object[] object = textFilterManage.filterHtml(request,content,"help",null);
				String value = (String)object[0];
				imageNameList = (List<String>)object[1];
				isImage = (Boolean)object[2];//是否含有图片
				flashNameList = (List<String>)object[3];
				isFlash = (Boolean)object[4];//是否含有Flash
				mediaNameList = (List<String>)object[5];
				isMedia = (Boolean)object[6];//是否含有音视频
				fileNameList = (List<String>)object[7];
				isFile = (Boolean)object[8];//是否含有文件
				isMap = (Boolean)object[9];//是否含有地图
				
				//修改文件路径
				Object[] path_object = textFilterManage.updateTypePath(value,"help",helpTypeId);
				String new_value = (String)path_object[0];
				Map<String,String> file_path = (Map<String,String>)path_object[1];//key:旧文件路径  value:新文件路径
				
				if(file_path != null && file_path.size() >0){
					for(Map.Entry<String,String> entry : file_path.entrySet()) {//key:旧文件路径  value:新文件路径
						
						//旧路径 左斜杆路径转为系统路径
						String old_path_Delimiter = FileUtil.toSystemPath(entry.getKey());
						
						//新路径 左斜杆路径转为系统路径
						String new_path_Delimiter = FileUtil.toSystemPath(entry.getValue());
							
						//替换路径中的..号
						old_path_Delimiter = FileUtil.toRelativePath(old_path_Delimiter);
						new_path_Delimiter = FileUtil.toRelativePath(new_path_Delimiter);
						
						//复制文件到新路径
						fileManage.copyFile(old_path_Delimiter, new_path_Delimiter);
						//取得文件名称
						String fileName = FileUtil.getName(entry.getKey());
						
						//新建文件锁到新路径
						 //生成锁文件名称
						String lockFileName = StringUtils.replaceOnce(entry.getValue(), "file/help/", "")+"/"+fileName;
						lockFileName = lockFileName.replaceAll("/", "_");
						
						fileLock_list.add(lockFileName);
						//添加文件锁
						fileManage.addLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,lockFileName);
						//旧路径文件
						oldPathFileList.add(old_path_Delimiter);
					}
				}
				
				//不含标签内容
				String text = textFilterManage.filterText(content);
				//清除空格&nbsp;
				String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
				
				if(isImage == true || isFlash == true || isMedia == true || isFile==true ||isMap== true || (!"".equals(text.trim()) && !"".equals(trimSpace))){
					String username = "";//用户名称
					
					Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
					if(obj instanceof UserDetails){
						username =((UserDetails)obj).getUsername();	
					}
					help.setUserName(username);
					help.setContent(new_value);
				}else{
					error.put("content", "帮助内容不能为空");
				}	
			}else{
				error.put("content", "帮助内容不能为空");
			}
		}
		
		if(error.size() ==0){
			int i = helpService.saveHelp(help);
			
			if(i == 0){
				error.put("content", "帮助添加失败");
				model.addAttribute("error", error);
			}else{
				//删除图片锁
				if(imageNameList != null && imageNameList.size() >0){
					for(String imageName :imageNameList){
				
						 if(imageName != null && !"".equals(imageName.trim())){
							 fileManage.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
	
						 }
					}
				}
				//falsh
				if(flashNameList != null && flashNameList.size() >0){
					for(String flashName :flashNameList){
						
						 if(flashName != null && !"".equals(flashName.trim())){
							fileManage.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,flashName.replaceAll("/","_"));
		
						 }
					}
				}
				//音视频
				if(mediaNameList != null && mediaNameList.size() >0){
					for(String mediaName :mediaNameList){
						if(mediaName != null && !"".equals(mediaName.trim())){
							fileManage.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));
						
						}
					}
				}
				//文件
				if(fileNameList != null && fileNameList.size() >0){
					for(String fileName :fileNameList){
						if(fileName != null && !"".equals(fileName.trim())){
							fileManage.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
						
						}
					}
				}
				//删除复制文件锁
				if(fileLock_list != null && fileLock_list.size() >0){
					for(String fileName :fileLock_list){
						if(fileName != null && !"".equals(fileName.trim())){
							fileManage.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
							
						}
					}
				}
				
				//删除旧路径文件
				if(oldPathFileList != null && oldPathFileList.size() >0){
					for(String oldPathFile :oldPathFileList){
						//替换路径中的..号
						oldPathFile = FileUtil.toRelativePath(oldPathFile);
						Boolean state = fileManage.deleteFile(oldPathFile);
						if(state != null && state == false){
							//替换指定的字符，只替换第一次出现的
							oldPathFile = StringUtils.replaceOnce(oldPathFile, "file"+File.separator+"help"+File.separator, "");
							
							//创建删除失败文件
							fileManage.failedStateFile("file"+File.separator+"help"+File.separator+"lock"+File.separator+FileUtil.toUnderline(oldPathFile));
						
						}
					}
				}
				
			}
		};
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	/**
	 * 文件上传
	 * @param dir: 上传类型，分别为image、flash、media、file 
	 * @param fileName 文件名称 预签名时有值
	 * 
	 */
	@ResponseBody
	@RequestMapping(params="method=upload",method=RequestMethod.POST)
	public String upload(ModelMap model,Long helpTypeId,String dir,String fileName,
			MultipartFile file, HttpServletResponse response) throws Exception {

		Map<String,Object> returnJson = new HashMap<String,Object>();
		String errorMessage  = "";
		
		
		if(dir != null){
			DateTime dateTime = new DateTime();     
		     
			String date = dateTime.toString("yyyy-MM-dd");
			
			
			int fileSystem = fileManage.getFileSystem();
			if(fileSystem ==10 || fileSystem == 20 || fileSystem == 30){//10.SeaweedFS 20.MinIO 30.阿里云OSS
				if(fileName != null && !"".equals(fileName.trim())){
					//取得文件后缀
					String suffix = FileUtil.getExtension(fileName.trim()).toLowerCase();
					
					if(dir.equals("image")){
						//允许上传格式
						List<String> formatList = CommentedProperties.readRichTextAllowImageUploadFormat();
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(fileName.trim(),formatList);
						if(authentication){
							
							//文件锁目录
							String lockPathDir = "file"+File.separator+"help"+File.separator+"lock"+File.separator;
							
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							
							
							//生成锁文件
							fileManage.addLock(lockPathDir,helpTypeId+"_"+date +"_image_"+newFileName);
							String presigne = fileManage.createPresigned("file/help/"+helpTypeId+"/"+date+"/image/"+newFileName,null);//创建预签名
	
							
							//返回预签名URL
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", presigne);
							returnJson.put("title", fileName);//旧文件名称
							return JsonUtils.toJSONString(returnJson);
							
						}else{
							errorMessage = "文件格式不允许上传";
						}
						
						
					}else if(dir.equals("flash")){
						//允许上传格式
						List<String> formatList = new ArrayList<String>();
						formatList.add("swf");
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(fileName.trim(),formatList);
						if(authentication){
							//文件锁目录
							String lockPathDir = "file"+File.separator+"help"+File.separator+"lock"+File.separator;
							
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							
							
							//生成锁文件
							fileManage.addLock(lockPathDir,helpTypeId+"_"+date +"_flash_"+newFileName);
							String presigne = fileManage.createPresigned("file/help/"+helpTypeId+"/"+date+"/flash/"+newFileName,null);//创建预签名
	
							
							//返回预签名URL
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", presigne);
							returnJson.put("title", fileName);//旧文件名称
							return JsonUtils.toJSONString(returnJson);
							
						}else{
							errorMessage = "文件格式不允许上传";
						}
						
					}else if(dir.equals("media")){
						//允许上传格式
						List<String> formatList = CommentedProperties.readRichTextAllowVideoUploadFormat();
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(fileName.trim(),formatList);
						if(authentication){
							//文件锁目录
							String lockPathDir = "file"+File.separator+"help"+File.separator+"lock"+File.separator;
							
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							
							
							//生成锁文件
							fileManage.addLock(lockPathDir,helpTypeId+"_"+date +"_media_"+newFileName);
							String presigne = fileManage.createPresigned("file/help/"+helpTypeId+"/"+date+"/media/"+newFileName,null);//创建预签名
	
							
							//返回预签名URL
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", presigne);
							returnJson.put("title", fileName);//旧文件名称
							return JsonUtils.toJSONString(returnJson);

						}else{
							errorMessage = "文件格式不允许上传";
						}
					}else if(dir.equals("file")){
						//允许上传格式
						List<String> formatList = CommentedProperties.readRichTextAllowFileUploadFormat();
						
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(fileName.trim(),formatList);
						if(authentication){
							//文件锁目录
							String lockPathDir = "file"+File.separator+"help"+File.separator+"lock"+File.separator;
							
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							
							
							//生成锁文件
							fileManage.addLock(lockPathDir,helpTypeId+"_"+date +"_file_"+newFileName);
							String presigne = fileManage.createPresigned("file/help/"+helpTypeId+"/"+date+"/file/"+newFileName,null);//创建预签名
	
							
							//返回预签名URL
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", presigne);
							returnJson.put("title", fileName);//旧文件名称
							return JsonUtils.toJSONString(returnJson);
						}else{
							errorMessage = "文件格式不允许上传";
						}
					}else{
						errorMessage = "缺少dir参数";
					}
				}else{
					errorMessage = "文件名称不能为空";
				}
				
				
				
			
			}else{//0.本地系统
				if(file != null && !file.isEmpty()){
					//当前文件名称
					String sourceFileName = file.getOriginalFilename();
					
					String suffix = FileUtil.getExtension(sourceFileName).toLowerCase();

					if(dir.equals("image")){
						//允许上传图片格式
						List<String> formatList = CommentedProperties.readRichTextAllowImageUploadFormat();
					
						//验证文件类型
						boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
						
						//如果用flash控件上传
						if(file.getContentType().equalsIgnoreCase("application/octet-stream")){
							String fileType = FileType.getType(file.getInputStream());
							for (String format :formatList) {
								if(format.equalsIgnoreCase(fileType)){
									authentication = true;
									break;
								}
							}
						}
						
						if(authentication){
							//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
							String pathDir = "file"+File.separator+"help"+File.separator + helpTypeId + File.separator + date +File.separator +"image"+ File.separator;
							//文件锁目录
							String lockPathDir = "file"+File.separator+"help"+File.separator+"lock"+File.separator;
							
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							
							//生成文件保存目录
							FileUtil.createFolder(pathDir);
							//生成锁文件保存目录
							FileUtil.createFolder(lockPathDir);
							//生成锁文件
							fileManage.addLock(lockPathDir,helpTypeId+"_"+date +"_image_"+newFileName);
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());
							
				
							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", fileManage.fileServerAddress()+"file/help/"+helpTypeId+"/"+date+"/image/"+newFileName);
							return JsonUtils.toJSONString(returnJson);
							
						}else{
							errorMessage = "当前文件类型不允许上传";
						}
					}else if(dir.equals("flash")){
					
						List<String> flashFormatList = new ArrayList<String>();
						flashFormatList.add("swf");
						
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),flashFormatList);

						if(authentication){
							
							String pathDir = "file"+File.separator+"help"+File.separator + helpTypeId + File.separator + date+ File.separator +"flash"+ File.separator;
							//文件锁目录
							String lockPathDir = "file"+File.separator+"help"+File.separator+"lock"+File.separator;
							 //构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							
							//生成文件保存目录
							FileUtil.createFolder(pathDir);
							//生成锁文件保存目录
							FileUtil.createFolder(lockPathDir);
							//生成锁文件
							fileManage.addLock(lockPathDir,helpTypeId+"_"+date +"_flash_"+newFileName);
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());
							
							
							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", fileManage.fileServerAddress()+"file/help/"+helpTypeId+"/"+date+"/flash/"+newFileName);
							return JsonUtils.toJSONString(returnJson);
						}else{
							errorMessage = "当前文件类型不允许上传";
						}
						
						
						
					}else if(dir.equals("media")){	
						//允许上传视音频格式
						List<String> formatList = CommentedProperties.readRichTextAllowVideoUploadFormat();
						
						
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
						
						if(authentication){	
							//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
							String pathDir = "file"+File.separator+"help"+File.separator + helpTypeId + File.separator + date+ File.separator +"media"+ File.separator;
							//文件锁目录
							String lockPathDir = "file"+File.separator+"help"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							//生成文件保存目录
							FileUtil.createFolder(pathDir);
							//生成锁文件保存目录
							FileUtil.createFolder(lockPathDir);
							//生成锁文件
							fileManage.addLock(lockPathDir,helpTypeId+"_"+date +"_media_"+newFileName);
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());

							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", fileManage.fileServerAddress()+"file/help/"+helpTypeId+"/"+date+"/media/"+newFileName);
							return JsonUtils.toJSONString(returnJson);
						}else{
							errorMessage = "当前文件类型不允许上传";
						}
					}else if(dir.equals("file")){
						//允许上传文件格式
						List<String> formatList = CommentedProperties.readRichTextAllowFileUploadFormat();
						//验证文件后缀
						boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
						if(authentication){
							//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
							String pathDir = "file"+File.separator+"help"+File.separator + helpTypeId + File.separator + date+ File.separator +"file"+ File.separator;
							//文件锁目录
							String lockPathDir = "file"+File.separator+"help"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ "." + suffix;
							
							//生成文件保存目录
							FileUtil.createFolder(pathDir);
							//生成锁文件保存目录
							FileUtil.createFolder(lockPathDir);
							//生成锁文件
							fileManage.addLock(lockPathDir,helpTypeId+"_"+date +"_file_"+newFileName);
							//保存文件
							fileManage.writeFile(pathDir, newFileName,file.getBytes());
							
							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", fileManage.fileServerAddress()+"file/help/"+helpTypeId+"/"+date+"/file/"+newFileName);
							returnJson.put("title", file.getOriginalFilename());//旧文件名称
							return JsonUtils.toJSONString(returnJson);
						}else{
							errorMessage = "当前文件类型不允许上传";
						}
					}else{
						errorMessage = "缺少dir参数";
					}
				
				}else{
					errorMessage = "文件不能为空";
				}

			}

		}else{
			errorMessage = "参数不能为空";
		}
		
		//上传失败
		returnJson.put("error", 1);
		returnJson.put("message", errorMessage);
		return JsonUtils.toJSONString(returnJson);
	}
	/**
	 * 帮助   修改界面显示
	 * 
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.GET)
	public String editUI(ModelMap model,Long helpId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
				
		if(helpId != null && helpId >0L){
			Help help = helpService.findById(helpId);
			if(help != null){
				HelpType helpType = helpTypeService.findById(help.getHelpTypeId());
				if(helpType != null){
					help.setHelpTypeName(helpType.getName());
				}
				//处理富文本路径
				help.setContent(fileManage.processRichTextFilePath(help.getContent(),"help"));
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,help));
			}else{
				error.put("helpId", "帮助不存在");
			}
		}else{
			error.put("helpId", "帮助Id不能为空");
		}

		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	/**
	 * 帮助   修改
	 * @param model
	 * @param helpId
	 * @param helpTypeId
	 * @param name
	 * @param content
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.POST)
	public String edit(ModelMap model,Long helpId,Long helpTypeId,String name,String content,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		Help help = null;
		List<String> imageNameList = null;
		boolean isImage = false;//是否含有图片
		List<String> flashNameList = null;
		boolean isFlash = false;//是否含有Flash
		List<String> mediaNameList = null;
		boolean isMedia = false;//是否含有音视频
		List<String> fileNameList = null;
		boolean isFile = false;//是否含有文件
		boolean isMap = false;//是否含有地图
		
		
		//复制文件锁
		List<String> fileLock_list = new ArrayList<String>();
		List<String> oldPathFileList = new ArrayList<String>();//旧路径文件

		
		String old_content = "";
		if(helpId != null && helpId >0L){
			help = helpService.findById(helpId);
			if(help != null){
				if(helpTypeId != null && helpTypeId >0L){
					HelpType helpType = helpTypeService.findById(helpTypeId);
					if(helpType != null){
						help.setHelpTypeId(helpType.getId());
						help.setHelpTypeName(helpType.getName());
						
					}else{
						error.put("helpTypeId", "帮助不存在");
					}
				}else{
					error.put("helpTypeId", "帮助Id不能为空");
				}
				
				
				old_content = help.getContent();
				if(name != null && !"".equals(name.trim())){
					help.setName(name);
					if(name.length() >100){
						error.put("name", "不能大于200个字符");
					}
				}else{
					error.put("name", "帮助名称不能为空");
				}
				if(content != null && !"".equals(content.trim())){
					//过滤标签
					content = textFilterManage.filterTag(request,content);
					Object[] object = textFilterManage.filterHtml(request,content,"help",null);
					String value = (String)object[0];
					imageNameList = (List<String>)object[1];
					isImage = (Boolean)object[2];//是否含有图片
					flashNameList = (List<String>)object[3];
					isFlash = (Boolean)object[4];//是否含有Flash
					mediaNameList = (List<String>)object[5];
					isMedia = (Boolean)object[6];//是否含有音视频
					fileNameList = (List<String>)object[7];
					isFile = (Boolean)object[8];//是否含有文件
					isMap = (Boolean)object[9];//是否含有地图
					
					//修改文件路径
					Object[] path_object = textFilterManage.updateTypePath(value,"help",helpTypeId);
					String new_value = (String)path_object[0];
					Map<String,String> file_path = (Map<String,String>)path_object[1];//key:旧文件路径  value:新文件路径
					
					if(file_path != null && file_path.size() >0){
						for(Map.Entry<String,String> entry : file_path.entrySet()) {//key:旧文件路径  value:新文件路径
							
							//旧路径 左斜杆路径转为系统路径
							String old_path_Delimiter = FileUtil.toSystemPath(entry.getKey());
							
							//新路径 左斜杆路径转为系统路径
							String new_path_Delimiter = FileUtil.toSystemPath(entry.getValue());
								
							//替换路径中的..号
							old_path_Delimiter = FileUtil.toRelativePath(old_path_Delimiter);
							new_path_Delimiter = FileUtil.toRelativePath(new_path_Delimiter);
							
							//复制文件到新路径
							fileManage.copyFile(old_path_Delimiter, new_path_Delimiter);
							//取得文件名称
							String fileName = FileUtil.getName(entry.getKey());
							
							//新建文件锁到新路径
							 //生成锁文件名称
							String lockFileName = StringUtils.replaceOnce(entry.getValue(), "file/help/", "")+"/"+fileName;
							lockFileName = lockFileName.replaceAll("/", "_");
							
							fileLock_list.add(lockFileName);
							//添加文件锁
							fileManage.addLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,lockFileName);
							//旧路径文件
							oldPathFileList.add(old_path_Delimiter);
						}
					}
					//不含标签内容
					String text = textFilterManage.filterText(content);
					//清除空格&nbsp;
					String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
					
					if(isImage == true || isFlash == true || isMedia == true || isFile==true ||isMap== true || (!"".equals(text.trim()) && !"".equals(trimSpace))){
						String username = "";//用户名称
						
						Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
						if(obj instanceof UserDetails){
							username =((UserDetails)obj).getUsername();	
						}
						help.setUserName(username);
						help.setContent(new_value);
					}else{
						error.put("content", "帮助内容不能为空");
					}	
				}else{
					error.put("content", "帮助内容不能为空");
				}
			}else{
				error.put("helpId", "帮助不存在");
			}
		}else{
			error.put("helpId", "帮助Id不能为空");
		}

		
		if(error.size() ==0){
			helpService.updateHelp(help);
			//分隔符
			String separator = "";
			if("\\".equals(File.separator)){
				separator = "\\\\";
			}else{
				separator = "/";
			}
			
			Object[] obj = textFilterManage.readPathName(old_content,"help");
			if(obj != null && obj.length >0){
				//旧图片
				List<String> old_imageNameList = (List<String>)obj[0];
				if(old_imageNameList != null && old_imageNameList.size() >0){
					
			        Iterator<String> iter = old_imageNameList.iterator();
			        while (iter.hasNext()) {
			        	String imageName = iter.next();  
						for(String new_imageName : imageNameList){
							if(imageName.equals("file/help/"+new_imageName)){
								iter.remove();
								break;
							}
						}
					}
					if(old_imageNameList != null && old_imageNameList.size() >0){
						for(String imageName : old_imageNameList){
							oldPathFileList.add(FileUtil.toSystemPath(imageName));
						}
						
					}
				}
				
				//旧Flash
				List<String> old_flashNameList = (List<String>)obj[1];		
				if(old_flashNameList != null && old_flashNameList.size() >0){		
			        Iterator<String> iter = old_flashNameList.iterator();
			        while (iter.hasNext()) {
			        	String flashName = iter.next();  
						for(String new_flashName : flashNameList){
							if(flashName.equals("file/help/"+new_flashName)){
								iter.remove();
								break;
							}
						}
					}
					if(old_flashNameList != null && old_flashNameList.size() >0){
						for(String flashName : old_flashNameList){
							oldPathFileList.add(FileUtil.toSystemPath(flashName));
						}
						
					}
				}

				//旧影音
				List<String> old_mediaNameList = (List<String>)obj[2];	
				if(old_mediaNameList != null && old_mediaNameList.size() >0){		
			        Iterator<String> iter = old_mediaNameList.iterator();
			        while (iter.hasNext()) {
			        	String mediaName = iter.next();  
						for(String new_mediaName : mediaNameList){
							if(mediaName.equals("file/help/"+new_mediaName)){
								iter.remove();
								break;
							}
						}
					}
					if(old_mediaNameList != null && old_mediaNameList.size() >0){
						for(String mediaName : old_mediaNameList){
							oldPathFileList.add(FileUtil.toSystemPath(mediaName));
						}
						
					}
				}
				
				//旧文件
				List<String> old_fileNameList = (List<String>)obj[3];		
				if(old_fileNameList != null && old_fileNameList.size() >0){		
			        Iterator<String> iter = old_fileNameList.iterator();
			        while (iter.hasNext()) {
			        	String fileName = iter.next();  
						for(String new_fileName : fileNameList){
							if(fileName.equals("file/help/"+new_fileName)){
								iter.remove();
								break;
							}
						}
					}
					if(old_fileNameList != null && old_fileNameList.size() >0){
						for(String fileName : old_fileNameList){
							oldPathFileList.add(FileUtil.toSystemPath(fileName));
						}
						
					}
				}
			}
			
			
			//删除图片锁
			if(imageNameList != null && imageNameList.size() >0){
				for(String imageName :imageNameList){
					 if(imageName != null && !"".equals(imageName.trim())){
						 fileManage.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
					 }
				}
			}
			//删除Falsh锁
			if(flashNameList != null && flashNameList.size() >0){
				for(String flashName :flashNameList){
					 if(flashName != null && !"".equals(flashName.trim())){
						 fileManage.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,flashName.replaceAll("/","_"));
					 }
				}
			}
			//删除音视频锁
			if(mediaNameList != null && mediaNameList.size() >0){
				for(String mediaName :mediaNameList){
					if(mediaName != null && !"".equals(mediaName.trim())){
						fileManage.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));
						
					}
				}
			}
			//删除文件锁
			if(fileNameList != null && fileNameList.size() >0){
				for(String fileName :fileNameList){
					if(fileName != null && !"".equals(fileName.trim())){
						fileManage.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
						
					}
				}
			}
			//删除复制文件锁
			if(fileLock_list != null && fileLock_list.size() >0){
				for(String fileName :fileLock_list){
					if(fileName != null && !"".equals(fileName.trim())){
						fileManage.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
						
					}
				}
			}
			
			//删除旧路径文件
			if(oldPathFileList != null && oldPathFileList.size() >0){
				for(String oldPathFile :oldPathFileList){
					//替换路径中的..号
					oldPathFile = FileUtil.toRelativePath(oldPathFile);
					
					//删除旧路径文件
					Boolean state = fileManage.deleteFile(oldPathFile);
					if(state != null && state == false){
						//替换指定的字符，只替换第一次出现的
						oldPathFile = StringUtils.replaceOnce(oldPathFile, "file"+File.separator+"help"+File.separator, "");
						
						//创建删除失败文件
						fileManage.failedStateFile("file"+File.separator+"help"+File.separator+"lock"+File.separator+FileUtil.toUnderline(oldPathFile));

					}
				}
			}
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
			
			
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	/**
	 * 帮助   删除
	 * @param model
	 * @param helpId
	 */
	@ResponseBody
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	public String delete(ModelMap model,Long[] helpId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		if(helpId != null && helpId.length >0){
			List<Long> helpIdList = new ArrayList<Long>();
			for(Long l :helpId){
				if(l != null && l >0L){
					helpIdList .add(l);
				}
			}
			if(helpIdList != null && helpIdList.size() >0){

				List<Help> helpList = helpService.findByIdList(helpIdList);
				if(helpList != null && helpList.size() >0){
					for(Help help : helpList){
						if(help.isVisible()){//标记删除
							int i = helpService.markDelete(help.getHelpTypeId(),help.getId());
						}else{//物理删除
							int i = helpService.deleteHelp(help.getHelpTypeId(),help.getId(),0L);
							
							Object[] obj = textFilterManage.readPathName(help.getContent(),"help");
							if(obj != null && obj.length >0){
								List<String> filePathList = new ArrayList<String>();
								
								//删除图片
								List<String> imageNameList = (List<String>)obj[0];		
								for(String imageName :imageNameList){
									filePathList.add(imageName);
									
								}
								//删除Flash
								List<String> flashNameList = (List<String>)obj[1];		
								for(String flashName :flashNameList){
									filePathList.add(flashName);
								}
								//删除影音
								List<String> mediaNameList = (List<String>)obj[2];		
								for(String mediaName :mediaNameList){
									filePathList.add(mediaName);
								}
								//删除文件
								List<String> fileNameList = (List<String>)obj[3];		
								for(String fileName :fileNameList){
									filePathList.add(fileName);
								}
								
								for(String filePath :filePathList){
									//替换路径中的..号
									filePath = FileUtil.toRelativePath(filePath);
									filePath = FileUtil.toSystemPath(filePath);
									//删除旧路径文件
									Boolean state = fileManage.deleteFile(filePath);
									if(state != null && state == false){
										 //替换指定的字符，只替换第一次出现的
										filePath = StringUtils.replaceOnce(filePath, "file"+File.separator+"help"+File.separator, "");
										
										//创建删除失败文件
										fileManage.failedStateFile("file"+File.separator+"help"+File.separator+"lock"+File.separator+FileUtil.toUnderline(filePath));
									}
								}
								
							}
						}
					}
					return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
				}else{
					error.put("helpId", "帮助不存在");
				}
			}
		}else{
			error.put("helpId", "帮助Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 还原
	 * @param model
	 * @param helpId 帮助Id集合
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=reduction",method=RequestMethod.POST)
	public String reduction(ModelMap model,Long[] helpId,
			HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(helpId != null && helpId.length>0){
			List<Help> helpList = helpService.findByIdList(Arrays.asList(helpId));
			if(helpList != null && helpList.size() >0){
				helpService.reductionHelp(helpList);
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
			}else{
				error.put("helpId", "帮助不存在");
			}
		}else{
			error.put("helpId", "帮助Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 移动
	 * @param model
	 * @param helpId 帮助Id集合
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=move",method=RequestMethod.POST)
	public String move(ModelMap model,Long[] helpId,Long helpTypeId,
			HttpServletResponse response) throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		if(helpId != null && helpId.length>0){
			
			List<Help> helpList = helpService.findByIdList(Arrays.asList(helpId));
			
			//复制文件锁
			List<String> fileLock_list = new ArrayList<String>();
			List<String> pathFileList = new ArrayList<String>();//旧路径文件
			
			if(helpList != null && helpList.size() >0){
				for(Help help : helpList){
					//修改文件路径
					Object[] path_object = textFilterManage.updateTypePath(help.getContent(),"help",helpTypeId);
					String new_value = (String)path_object[0];
					
					help.setContent(new_value);
					Map<String,String> file_path = (Map<String,String>)path_object[1];//key:旧文件路径  value:新文件路径
					
					if(file_path != null && file_path.size() >0){
						for(Map.Entry<String,String> entry : file_path.entrySet()) {//key:旧文件路径  value:新文件路径
							
							//旧路径 左斜杆路径转为系统路径
							String old_path_Delimiter = FileUtil.toSystemPath(entry.getKey());
							
							//新路径 左斜杆路径转为系统路径
							String new_path_Delimiter = FileUtil.toSystemPath(entry.getValue());
								
							//替换路径中的..号
							old_path_Delimiter = FileUtil.toRelativePath(old_path_Delimiter);
							new_path_Delimiter = FileUtil.toRelativePath(new_path_Delimiter);
							
							//复制文件到新路径
							fileManage.copyFile(old_path_Delimiter, new_path_Delimiter);
							//取得文件名称
							String fileName = FileUtil.getName(entry.getKey());
							
							//新建文件锁到新路径
							 //生成锁文件名称
							String lockFileName = StringUtils.replaceOnce(entry.getValue(), "file/help/", "")+"/"+fileName;
							lockFileName = lockFileName.replaceAll("/", "_");
							
							fileLock_list.add(lockFileName);
							//添加文件锁
							fileManage.addLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,lockFileName);
							//旧路径文件
							pathFileList.add(old_path_Delimiter);
						}
					}
					
				}
			}else{
				error.put("helpId", "帮助不存在");
			}
			
			if(error.size() ==0){
				helpService.updateHelp(helpList,helpTypeId);
				
				
				//删除复制文件锁
				if(fileLock_list != null && fileLock_list.size() >0){
					for(String fileName :fileLock_list){
						if(fileName != null && !"".equals(fileName.trim())){
							fileManage.deleteLock("file"+File.separator+"help"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
							
						}
					}
				}
				//删除旧路径文件
				if(pathFileList != null && pathFileList.size() >0){
					for(String pathFile :pathFileList){
						//替换路径中的..号
						pathFile = FileUtil.toRelativePath(pathFile);
						
						//删除旧路径文件
						Boolean state = fileManage.deleteFile(pathFile);
						if(state != null && state == false){
							//替换指定的字符，只替换第一次出现的
							pathFile = StringUtils.replaceOnce(pathFile, "file"+File.separator+"help"+File.separator, "");
							
							//创建删除失败文件
							fileManage.failedStateFile("file"+File.separator+"help"+File.separator+"lock"+File.separator+FileUtil.toUnderline(pathFile));
						
						
						}
					}
				}
				
			}
			

			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("helpId", "帮助Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 搜索帮助分页
	 * @param keyword 关键词
	 * @return
	 */
	@ResponseBody
	@RequestMapping(params="method=ajax_searchHelpPage", method=RequestMethod.GET)
	public String searchHelpPage(ModelMap model,PageForm pageForm,
			String keyword) {
		
		StringBuffer jpql = new StringBuffer("");
		String sql = "";
		List<Object> params = new ArrayList<Object>();
		//调用分页算法代码
		PageView<Help> pageView = new PageView<Help>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		if(keyword != null && !"".equals(keyword.trim())){
			String searchName_utf8 = "";
			try {
				searchName_utf8 = URLDecoder.decode(keyword.trim(),"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("搜索帮助名称编码错误",e);
		        }
			}
			jpql.append(" and o.name like ?").append((params.size()+1)).append(" escape '/' ");
			params.add("%/"+ searchName_utf8.trim()+"%" );//加上查询参数
			
		}
		jpql.append(" and o.visible=?").append((params.size()+1));//and o.code=?1
		params.add(true);//设置o.visible=?1是否可见
		//删除第一个and
		sql = StringUtils.difference(" and", jpql.toString());
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
	//	orderby.put("sell", "desc");//先按是否在售排序
	//	orderby.put("id", "desc");//根据code字段降序排序
		QueryResult<Help> qr = helpService.getScrollData(Help.class,firstindex, pageView.getMaxresult(), sql, params.toArray(),orderby);
		
		
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
	}
}
