package cms.web.action.topic;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.PageForm;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.setting.EditorTag;
import cms.bean.setting.SystemSetting;
import cms.bean.staff.SysUsers;
import cms.bean.topic.Comment;
import cms.bean.topic.Quote;
import cms.bean.topic.Reply;
import cms.bean.user.User;
import cms.service.setting.SettingService;
import cms.service.topic.CommentService;
import cms.service.topic.TopicService;
import cms.service.user.UserService;
import cms.utils.FileUtil;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.filterWord.SensitiveWordFilterManage;
import cms.web.action.setting.SettingManage;
import cms.web.action.user.UserManage;

/**
 * 评论
 *
 */
@Controller
@RequestMapping("/control/comment/manage") 
public class CommentManageAction {

	@Resource CommentService commentService;//通过接口引用代理返回的对象
	@Resource SettingManage settingManage;
	@Resource CommentManage commentManage;
	
	@Resource TextFilterManage textFilterManage;
	
	@Resource SensitiveWordFilterManage sensitiveWordFilterManage;
	
	@Resource SettingService settingService;
	@Resource TopicManage topicManage;
	@Resource TopicService topicService;
	@Resource FileManage fileManage;
	@Resource UserService userService;
	@Resource UserManage userManage;
	
	
	/**
	 * 评论  添加
	 */
	@ResponseBody
	@RequestMapping(params="method=addComment",method=RequestMethod.POST)
	public String addComment(ModelMap model,Long topicId,String content,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();
		if(topicId == null || topicId <=0L){
			error.put("topicId", "话题Id不能为空");
		}
		if(error.size() == 0 && content != null && !"".equals(content.trim())){
			
			//过滤标签
			content = textFilterManage.filterTag(request,content,settingManage.readEditorTag());
			Object[] object = textFilterManage.filterHtml(request,content,"comment",settingManage.readEditorTag());
			String value = (String)object[0];
			List<String> imageNameList = (List<String>)object[1];
			boolean isImage = (Boolean)object[2];//是否含有图片
			//不含标签内容
			String text = textFilterManage.filterText(content);
			//清除空格&nbsp;
			String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
			
			if(isImage == true || (!"".equals(text.trim()) && !"".equals(trimSpace))){
				String username = "";//用户名称
				String userId = "";//用户Id
				Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
				if(obj instanceof SysUsers){
					userId =((SysUsers)obj).getUserId();
					username =((SysUsers)obj).getUserAccount();
				}
				
				Comment comment = new Comment();
				comment.setTopicId(topicId);
				comment.setContent(value);
				comment.setIsMarkdown(false);
				comment.setIsStaff(true);
				comment.setUserName(username);
				comment.setIp(IpAddress.getClientIpAddress(request));
				comment.setStatus(20);
				//保存评论
				commentService.saveComment(comment);
				
				//修改话题最后回复时间
				topicService.updateTopicReplyTime(topicId,new Date());
				
				
				//上传文件编号
				String fileNumber = "a"+userId;
				
				//删除图片锁
				if(imageNameList != null && imageNameList.size() >0){
					for(String imageName :imageNameList){
						if(imageName != null && !"".equals(imageName.trim())){
							//如果验证不是当前用户上传的文件，则不删除锁
							 if(!topicManage.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
								 continue;
							 }
							 fileManage.deleteLock("file"+File.separator+"comment"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
						 }
					
					}
				}
			}	
		}else{
			error.put("content", "评论内容不能为空");
		}
		
		if(error.size()==0){
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	/**
	 * 评论  修改页面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=editComment",method=RequestMethod.GET)
	public String editCommentUI(ModelMap model,Long commentId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		
		returnValue.put("availableTag",commentManage.availableTag());
		
		if(commentId != null && commentId >0L){
			Comment comment = commentService.findByCommentId(commentId);
			if(comment != null){
				if(comment.getContent() != null && !"".equals(comment.getContent().trim())){
					//处理富文本路径
					comment.setContent(fileManage.processRichTextFilePath(comment.getContent(),"comment"));
				}
				if(comment.getQuote() != null && !"".equals(comment.getQuote().trim())){
					//引用
					List<Quote> customQuoteList = JsonUtils.toGenericObject(comment.getQuote(), new TypeReference< List<Quote> >(){});
					comment.setQuoteList(customQuoteList);
				}
				returnValue.put("comment", comment);
				String username = "";
				Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
				if(obj instanceof UserDetails){
					username =((UserDetails)obj).getUsername();	
				}
				returnValue.put("userName", username);
				SystemSetting systemSetting = settingService.findSystemSetting_cache();
				returnValue.put("supportEditor", systemSetting.getSupportEditor());
			}else{
				error.put("commentId", "评论不存在");
			}
			
		}else{
			error.put("commentId", "评论Id不能为空");
		}
		
		
		if(error.size()==0){
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	/**
	 * 评论  修改
	 */
	@ResponseBody
	@RequestMapping(params="method=editComment",method=RequestMethod.POST)
	public String editComment(PageForm pageForm,ModelMap model,Long commentId,String content,Integer status,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Comment comment = null;
		Integer old_status = -1;
		Map<String,String> error = new HashMap<String,String>();
		if(commentId == null || commentId <=0){
			error.put("commentId", "评论Id不能为空");
		}else{
			comment = commentService.findByCommentId(commentId);
		}
		
		if(status == null){
			error.put("status", "状态参数不能为空");
		}
	//	List<Integer> numbers = Arrays.asList(10,20); 
	//	if(!numbers.contains(status)){
	//		error.put("status", "状态参数错误");
	//	}
		
		
		if(error.size()==0 && content != null && !"".equals(content.trim())){
			if(comment != null){
				old_status = comment.getStatus();
				comment.setStatus(status);
				
				
				//过滤标签
				content = textFilterManage.filterTag(request,content,settingManage.readEditorTag());
				Object[] object = textFilterManage.filterHtml(request,content,"comment",settingManage.readEditorTag());
				String value = (String)object[0];
				List<String> imageNameList = (List<String>)object[1];
				boolean isImage = (Boolean)object[2];//是否含有图片
				//不含标签内容
				String text = textFilterManage.filterText(content);
				//清除空格&nbsp;
				String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
				
				if(isImage == true || (!"".equals(text.trim()) && !"".equals(trimSpace))){
					
					//下级引用Id组
					String lowerQuoteIdGroup = ","+comment.getId()+comment.getQuoteIdGroup();
					
					String username = comment.getUserName();//用户名称
					
					//修改评论
					int i = commentService.updateComment(comment.getId(),value,status,new Date(),lowerQuoteIdGroup,username);
					
					
					if(i >0 && !old_status.equals(status)){
						User user = userManage.query_cache_findUserByUserName(comment.getUserName());
						if(user != null){
							//修改评论状态
							userService.updateUserDynamicCommentStatus(user.getId(),comment.getUserName(),comment.getTopicId(),comment.getId(),comment.getStatus());
						}
						
					}
					
					
					
					//删除缓存
					commentManage.delete_cache_findByCommentId(comment.getId());
					
					//上传文件编号
					String fileNumber = topicManage.generateFileNumber(comment.getUserName(), comment.getIsStaff());

						
					if(imageNameList != null && imageNameList.size() >0){
						for(String imageName :imageNameList){
							if(imageName != null && !"".equals(imageName.trim())){
								 //如果验证不是当前用户上传的文件，则不删除
								 if(!topicManage.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
										continue;
								 }
								 fileManage.deleteLock("file"+File.separator+"comment"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
							 }
						
						}
					}

					//旧图片名称
					List<String> old_ImageName = textFilterManage.readImageName(comment.getContent(),"comment");
					if(old_ImageName != null && old_ImageName.size() >0){		
				        Iterator<String> iter = old_ImageName.iterator();
				        while (iter.hasNext()) {
				        	String imageName = iter.next();//含有路径
				        	String old_name = FileUtil.getName(imageName);
				        	
							for(String new_imageName : imageNameList){
								String new_name = FileUtil.getName(new_imageName);
								if(old_name.equals(new_name)){
									iter.remove();
									break;
								}
							}
						}
						if(old_ImageName != null && old_ImageName.size() >0){
							for(String imagePath : old_ImageName){
								 //如果验证不是当前用户上传的文件，则不删除
								 if(!topicManage.getFileNumber(FileUtil.getBaseName(imagePath.trim())).equals(fileNumber)){
										continue;
								 }
								//替换路径中的..号
								imagePath = FileUtil.toRelativePath(imagePath);
								//替换路径分割符
								imagePath = StringUtils.replace(imagePath, "/", File.separator);
								
								Boolean state = fileManage.deleteFile(imagePath);
								if(state != null && state == false){	
									//替换指定的字符，只替换第一次出现的
									imagePath = StringUtils.replaceOnce(imagePath, "file"+File.separator+"comment"+File.separator, "");
									imagePath = StringUtils.replace(imagePath, File.separator, "_");//替换所有出现过的字符
									
									//创建删除失败文件
									fileManage.failedStateFile("file"+File.separator+"comment"+File.separator+"lock"+File.separator+imagePath);
								
								}
							}
							
						}
					}	
				}else{	
					error.put("content", "评论内容不能为空");
				}	
			}else{
				error.put("commentId", "评论不能为空");
			}
		}else{
			error.put("content", "评论内容不能为空");
		}

		
		if(error.size()==0){
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	/**
	 * 评论  删除
	 * @param model
	 * @param commentId 评论Id
	 */
	@ResponseBody
	@RequestMapping(params="method=deleteComment",method=RequestMethod.POST)
	public String deleteComment(ModelMap model,Long[] commentId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();
		
		
		if(commentId != null && commentId.length >0){
			List<Long> commentIdList = new ArrayList<Long>();
			for(Long l :commentId){
				if(l != null && l >0L){
					commentIdList .add(l);
				}
			}
			if(commentIdList != null && commentIdList.size() >0){
				List<Comment> commentList = commentService.findByCommentIdList(commentIdList);
				if(commentList != null && commentList.size() >0){
					for(Comment comment : commentList){
						if(comment.getStatus() <100){//标记删除
							Integer constant = 100000;
							int i = commentService.markDeleteComment(comment.getId(),constant);
							
							if(i >0){
								User user = userManage.query_cache_findUserByUserName(comment.getUserName());
								if(user != null){
									//修改评论状态
									userService.updateUserDynamicCommentStatus(user.getId(),comment.getUserName(),comment.getTopicId(),comment.getId(),comment.getStatus()+constant);
								}
								//删除缓存
								commentManage.delete_cache_findByCommentId(comment.getId());
							}
							
						}else{//物理删除
							int i = commentService.deleteComment(comment.getTopicId(),comment.getId());
							if(i >0){
								//根据评论Id删除用户动态(评论下的回复也同时删除)
								userService.deleteUserDynamicByCommentId(comment.getTopicId(),comment.getId());
								
								
								//删除缓存
								commentManage.delete_cache_findByCommentId(comment.getId());
								
								String fileNumber = topicManage.generateFileNumber(comment.getUserName(), comment.getIsStaff());
								
								//删除图片
								List<String> imageNameList = textFilterManage.readImageName(comment.getContent(),"comment");
								if(imageNameList != null && imageNameList.size() >0){
									for(String imagePath : imageNameList){
										//如果验证不是当前用户上传的文件，则不删除锁
										 if(!topicManage.getFileNumber(FileUtil.getBaseName(imagePath.trim())).equals(fileNumber)){
											 continue;
										 }
										
										
										//替换路径中的..号
										imagePath = FileUtil.toRelativePath(imagePath);
										//替换路径分割符
										imagePath = StringUtils.replace(imagePath, "/", File.separator);
										
										Boolean state = fileManage.deleteFile(imagePath);
										
										if(state != null && state == false){	
											//替换指定的字符，只替换第一次出现的
											imagePath = StringUtils.replaceOnce(imagePath, "file"+File.separator+"comment"+File.separator, "");
											imagePath = StringUtils.replace(imagePath, File.separator, "_");//替换所有出现过的字符
											
											//创建删除失败文件
											fileManage.failedStateFile("file"+File.separator+"comment"+File.separator+"lock"+File.separator+imagePath);
										
										}
									}
								}
							}
							
						}
					}
				}
			}
		}else{
			error.put("commentId", "评论Id不能为空");
		}

		if(error.size()==0){
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	/**
	 * 评论  恢复
	 * @param model
	 * @param commentId 评论Id
	 */
	@ResponseBody
	@RequestMapping(params="method=recoveryComment",method=RequestMethod.POST)
	public String recoveryComment(ModelMap model,Long commentId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();
		if(commentId != null && commentId >0L){
			Comment comment = commentService.findByCommentId(commentId);
			if(comment != null && comment.getStatus() >100){
				int originalState = this.parseInitialValue(comment.getStatus());
				int i = commentService.updateCommentStatus(commentId, originalState);
				if(i >0){
					User user = userManage.query_cache_findUserByUserName(comment.getUserName());
					if(user != null){
						//修改评论状态
						userService.updateUserDynamicCommentStatus(user.getId(),comment.getUserName(),comment.getTopicId(),comment.getId(),originalState);
					}
				}
				
				commentManage.delete_cache_findByCommentId(commentId);
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
			}else{
				error.put("commentId", "评论不存在或未标记删除");
			}
		}else{
			error.put("commentId", "评论Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 解析初始值
	 * @param status 状态
	 * @return
	 */
	private int parseInitialValue(Integer status){
		int tens  = status%100/10;//十位%100/10
        int units  = status%10;//个位直接%10
		
        return Integer.parseInt(tens+""+units);
	}
	
	
	
	/**
	 * 评论  图片上传
	 * 
	 * 员工发话题 上传文件名为UUID + a + 员工Id
	 * 用户发话题 上传文件名为UUID + b + 用户Id
	 * @param topicId 话题Id
	 * @param userName 用户名称
	 * @param isStaff 是否是员工   true:员工   false:会员
	 * @param fileName 文件名称 预签名时有值
	 */
	@RequestMapping(params="method=uploadImage",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String uploadImage(ModelMap model,Long topicId,String userName, Boolean isStaff,String fileName,
			MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String number = topicManage.generateFileNumber(userName, isStaff);
		
		Map<String,Object> returnJson = new HashMap<String,Object>();
		String errorMessage = "";
		
		
		int fileSystem = fileManage.getFileSystem();
		if(fileSystem ==10 || fileSystem == 20 || fileSystem == 30){//10.SeaweedFS 20.MinIO 30.阿里云OSS
			if(fileName != null && !"".equals(fileName.trim()) && topicId != null && topicId >0L && number != null && !"".equals(number.trim())){
				//取得文件后缀
				String suffix = FileUtil.getExtension(fileName.trim()).toLowerCase();
				EditorTag editorSiteObject = settingManage.readEditorTag();
				if(editorSiteObject != null){
					if(editorSiteObject.isImage()){//允许上传图片
						//允许上传图片格式
						List<String> imageFormat = editorSiteObject.getImageFormat();
						//允许上传图片大小
						long imageSize = editorSiteObject.getImageSize();

						//验证文件类型
						boolean authentication = FileUtil.validateFileSuffix(fileName.trim(),imageFormat);
						
						if(authentication){
							
							//文件锁目录
							String lockPathDir = "file"+File.separator+"comment"+File.separator+"lock"+File.separator;
							//构建文件名称
							String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;
							//生成锁文件
							fileManage.addLock(lockPathDir,topicId+"_"+newFileName);
							
							String presigne = fileManage.createPresigned("file/comment/"+topicId+"/"+newFileName,imageSize);//创建预签名
							
							//上传成功
							returnJson.put("error", 0);//0成功  1错误
							returnJson.put("url", presigne);
							
							return JsonUtils.toJSONString(returnJson);
							
						}
						
						
					}else{
						errorMessage = "不允许上传图片";
					}
				}
			}else{
				errorMessage = "参数错误";
			}
		}else{//0.本地系统
			//文件上传
			if(file != null && !file.isEmpty() && topicId != null && topicId >0L && number != null && !"".equals(number.trim())){
				EditorTag editorSiteObject = settingManage.readEditorTag();
				if(editorSiteObject != null){
					if(editorSiteObject.isImage()){//允许上传图片
						//当前图片文件名称
						String sourceFileName = file.getOriginalFilename();
						//文件大小
						Long size = file.getSize();
						//取得文件后缀
						String suffix = sourceFileName.substring(sourceFileName.lastIndexOf('.')+1).toLowerCase();
						
						
						//允许上传图片格式
						List<String> imageFormat = editorSiteObject.getImageFormat();
						//允许上传图片大小
						long imageSize = editorSiteObject.getImageSize();

						//验证文件类型
						boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),imageFormat);
						
						if(authentication){
							if(size/1024 <= imageSize){
								
								//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
								String pathDir = "file"+File.separator+"comment"+File.separator + topicId + File.separator;
								//文件锁目录
								String lockPathDir = "file"+File.separator+"comment"+File.separator+"lock"+File.separator;
								//构建文件名称
								String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;
								
								//生成文件保存目录
								fileManage.createFolder(pathDir);
								//生成锁文件保存目录
								fileManage.createFolder(lockPathDir);
								//生成锁文件
								fileManage.addLock(lockPathDir,topicId+"_"+newFileName);
								//保存文件
								fileManage.writeFile(pathDir, newFileName,file.getBytes());
				
								//上传成功
								returnJson.put("error", 0);//0成功  1错误
								returnJson.put("url", fileManage.fileServerAddress(request)+"file/comment/"+topicId+"/"+newFileName);
								
								return JsonUtils.toJSONString(returnJson);
							}else{
								errorMessage = "图片大小超出"+size/1024+"K";
							}
						}else{
							errorMessage = "不允许上传的图片类型";
						}
					}else{
						errorMessage = "不允许上传图片";
					}
				}
				
			}else{
				errorMessage = "参数错误";
			}
		}
		
		//上传失败
		returnJson.put("error", 1);
		returnJson.put("message", errorMessage);
		return JsonUtils.toJSONString(returnJson);
	}
	/**
	 * 引用  添加页面显示
	 * @param model
	 * @param commentId 评论Id
	 */
	@ResponseBody
	@RequestMapping(params="method=addQuote",method=RequestMethod.GET)
	public String addQuoteUI(ModelMap model,Long commentId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
	
		returnValue.put("availableTag", commentManage.availableTag());
		Comment comment = commentService.findByCommentId(commentId);
		if(comment != null){
			if(comment.getContent() != null && !"".equals(comment.getContent().trim())){
				//处理富文本路径
				comment.setContent(fileManage.processRichTextFilePath(comment.getContent(),"comment"));
			}
			if(comment.getQuote() != null && !"".equals(comment.getQuote().trim())){
				//引用
				List<Quote> customQuoteList = JsonUtils.toGenericObject(comment.getQuote(), new TypeReference< List<Quote> >(){});
				comment.setQuoteList(customQuoteList);
			}
			returnValue.put("comment", comment);
		}else{
			error.put("commentId", "评论Id不能为空");
		}
		String username = "";//用户名称
		
		Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(obj instanceof UserDetails){
			username =((UserDetails)obj).getUsername();	
		}
		returnValue.put("userName", username);
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		returnValue.put("supportEditor", systemSetting.getSupportEditor());
		if(error.size()==0){
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	 }
	/**
	 * 引用  添加
	 * @param model
	 * @param commentId 评论Id
	 */
	@ResponseBody
	@RequestMapping(params="method=addQuote",method=RequestMethod.POST)	
	public String addQuote(ModelMap model,Long commentId,String content,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		Comment comment = null;
		Map<String,String> error = new HashMap<String,String>();
		if(commentId == null || commentId <=0){
			error.put("commentId", "引用评论不能为空");
		}else{
			comment = commentService.findByCommentId(commentId);
		}
		
		
		if(content != null && !"".equals(content.trim())){
			if(comment != null){
				comment.setStatus(20);
				
				//过滤标签
				content = textFilterManage.filterTag(request,content,settingManage.readEditorTag());
				
				Object[] object = textFilterManage.filterHtml(request,content,"comment",settingManage.readEditorTag());
				String value = (String)object[0];
				List<String> imageNameList = (List<String>)object[1];
				boolean isImage = (Boolean)object[2];//是否含有图片
				//不含标签内容
				String text = textFilterManage.filterText(content);
				//清除空格&nbsp;
				String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
				
				if(isImage == true || (!"".equals(text.trim()) && !"".equals(trimSpace))){
					String username = "";//用户名称
					String userId = "";//用户Id
					Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
					if(obj instanceof SysUsers){
						userId =((SysUsers)obj).getUserId();
						username =((SysUsers)obj).getUserAccount();
					}
					//旧引用
					List<Quote> old_quoteList = new ArrayList<Quote>();
					//旧引用Id组
					String old_quoteId = ","+comment.getId()+comment.getQuoteIdGroup();
					if(comment.getQuote() != null && !"".equals(comment.getQuote().trim())){
						//旧引用
						old_quoteList = JsonUtils.toGenericObject(comment.getQuote(), new TypeReference< List<Quote> >(){});
					}
					
					
					//自定义引用
					Quote quote = new Quote();
					quote.setCommentId(comment.getId());
					quote.setIsStaff(comment.getIsStaff());
					quote.setUserName(comment.getUserName());
					quote.setContent(textFilterManage.filterText(textFilterManage.specifyHtmlTagToText(comment.getContent())));
					old_quoteList.add(quote);
					
					//自定义评论
					Comment newComment = new Comment();
					newComment.setTopicId(comment.getTopicId());
					newComment.setContent(value);
					newComment.setIsMarkdown(false);
					newComment.setIsStaff(true);
					newComment.setQuoteIdGroup(old_quoteId);
					newComment.setUserName(username);
					newComment.setIp(IpAddress.getClientIpAddress(request));
					newComment.setQuote(JsonUtils.toJSONString(old_quoteList));
					newComment.setStatus(20);
					//保存评论
					commentService.saveComment(newComment);
					
					//修改话题最后回复时间
					topicService.updateTopicReplyTime(comment.getTopicId(),new Date());
					
					//上传文件编号
					String fileNumber = "a"+userId;
						
					if(imageNameList != null && imageNameList.size() >0){
						for(String imageName :imageNameList){
							if(imageName != null && !"".equals(imageName.trim())){
								//如果验证不是当前用户上传的文件，则不删除锁
								 if(!topicManage.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
									 continue;
								 }
								 fileManage.deleteLock("file"+File.separator+"comment"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
							 }
						
						}
					}	
				}else{	
					error.put("content", "评论内容不能为空");
					
				}	
			}else{
				error.put("commentId", "引用评论不能为空");
			}
		}else{
			error.put("content", "评论内容不能为空");
		}
		
		if(error.size()==0){
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	
	 }
	
	
	/**
	 * 审核评论
	 * @param model
	 * @param commentId 评论Id
	 * @return
	 * @throws Exception
	*/
	@ResponseBody
	@RequestMapping(params="method=auditComment",method=RequestMethod.POST)
	public String auditComment(ModelMap model,Long commentId,
			HttpServletResponse response) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		if(commentId != null && commentId>0L){
			int i = commentService.updateCommentStatus(commentId, 20);
			
			Comment comment = commentManage.query_cache_findByCommentId(commentId);
			if(i >0 && comment != null){
				User user = userManage.query_cache_findUserByUserName(comment.getUserName());
				if(user != null){
					//修改话题状态
					userService.updateUserDynamicCommentStatus(user.getId(),comment.getUserName(),comment.getTopicId(),comment.getId(),20);
				}
			}
			
			//删除缓存
			commentManage.delete_cache_findByCommentId(commentId);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("commentId", "评论Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	
	/**
	 * 回复  添加页面显示
	 * @param pageForm
	 * @param model
	 * @param commentId 自定义评论Id
	 */
	@ResponseBody
	@RequestMapping(params="method=addReply",method=RequestMethod.GET)
	public String addReplyUI(ModelMap model,Long commentId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		
		returnValue.put("availableTag",commentManage.availableTag());

		String username = "";
		Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(obj instanceof UserDetails){
			username =((UserDetails)obj).getUsername();	
		}
		returnValue.put("userName", username);
		
		if(error.size()==0){
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	/**
	 * 回复  添加
	 * @param model
	 * @param commentId 评论Id
	 * @param friendReplyId 对方回复Id
	 */
	@ResponseBody
	@RequestMapping(params="method=addReply",method=RequestMethod.POST)
	public String addReply(ModelMap model,Long commentId,String content,Long friendReplyId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Comment comment = null;
		Reply friendReply = null;
		Reply reply = new Reply();
		Map<String,String> error = new HashMap<String,String>();
		if(commentId == null || commentId <=0){
			error.put("commentId", "评论Id不能为空");
		}else{
			comment = commentService.findByCommentId(commentId);
		}
		
		if(friendReplyId != null && friendReplyId >0){
			friendReply = commentService.findReplyByReplyId(friendReplyId);
			if(friendReply != null){
				if(!friendReply.getCommentId().equals(commentId)){
					error.put("friendReplyId", "对方回复Id和评论Id不对应");
				}
			}else{
				error.put("friendReplyId", "对方回复Id不存在");
			}
		}
		
		
		if(content != null && !"".equals(content.trim())){
			if(comment != null){
				content = textFilterManage.filterReplyTag(request,content.trim(),settingManage.readEditorTag());
				Object[] object = textFilterManage.correctionReplyTag(request,content);
				String replyContent = (String)object[0];
				LinkedHashSet<String> mentionUserNameList = (LinkedHashSet<String>)object[1];//@提及用户名称
				
				boolean isImage = (Boolean)object[2];//是否含有图片
				//不含标签内容
				String text = textFilterManage.filterText(content);
				//清除空格&nbsp;
				String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
				
				if(isImage == true || (!"".equals(text.trim()) && !"".equals(trimSpace))){
					String username = "";//用户名称
					
					Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
					if(obj instanceof UserDetails){
						username =((UserDetails)obj).getUsername();	
					}
					
				
					//回复
					
					reply.setCommentId(comment.getId());
					reply.setIsStaff(true);
					reply.setUserName(username);
					reply.setContent(replyContent);
					reply.setTopicId(comment.getTopicId());
					reply.setStatus(20);
					reply.setIp(IpAddress.getClientIpAddress(request));
					
					
					
				}else{	
					error.put("content", "回复内容不能为空");
					
				}	
			}else{
				error.put("commentId", "评论不存在");
			}
		}else{
			error.put("content", "回复内容不能为空");
		}
		
		if(error.size()==0){
			if(friendReply != null){
				reply.setFriendReplyId(friendReplyId);
				reply.setFriendReplyIdGroup(friendReply.getFriendReplyIdGroup()+friendReplyId+",");
				reply.setIsFriendStaff(friendReply.getIsStaff());
				reply.setFriendUserName(friendReply.getUserName());
			}
			
			//保存评论
			commentService.saveReply(reply);

			
			//修改话题最后回复时间
			topicService.updateTopicReplyTime(comment.getTopicId(),new Date());
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	/**
	 * 回复  修改页面显示
	 * @param pageForm
	 * @param model
	 * @param replyId 回复Id
	 */
	@ResponseBody
	@RequestMapping(params="method=editReply",method=RequestMethod.GET)
	public String editReplyUI(ModelMap model,Long replyId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(replyId != null && replyId >0){
			Reply reply = commentService.findReplyByReplyId(replyId);
			if(reply != null){
				if(reply.getIp() != null && !"".equals(reply.getIp().trim())){
					
					reply.setIpAddress(IpAddress.queryAddress(reply.getIp()));
				}
				returnValue.put("reply", reply);
			}
		}else{
			error.put("replyId", "回复Id不能为空");
		}
		
		returnValue.put("availableTag",commentManage.availableTag());

		String username = "";
		Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(obj instanceof UserDetails){
			username =((UserDetails)obj).getUsername();	
		}
		returnValue.put("userName", username);
		
		if(error.size() ==0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	/**
	 * 回复  修改
	 * @param model
	 * @param replyId 回复Id
	 * @param content 内容
	 * @param status 状态
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=editReply",method=RequestMethod.POST)
	public String editReply(ModelMap model,Long replyId,String content,Integer status,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		Reply reply = null;
		Integer old_status = -1;
		Map<String,String> error = new HashMap<String,String>();
		if(replyId != null && replyId >0){
			reply = commentService.findReplyByReplyId(replyId);
		}else{
			error.put("replyId", "回复Id不能为空");
			
		}
		
		if(status == null){
			error.put("status", "回复状态不能为空");
		}
		
		if(content != null && !"".equals(content.trim())){
			if(reply != null){
				old_status = reply.getStatus();
				reply.setStatus(status);
				
				content = textFilterManage.filterReplyTag(request,content.trim(),settingManage.readEditorTag());
				Object[] object = textFilterManage.correctionReplyTag(request,content);
				String replyContent = (String)object[0];
				LinkedHashSet<String> mentionUserNameList = (LinkedHashSet<String>)object[1];//@提及用户名称
				
				boolean isImage = (Boolean)object[2];//是否含有图片
				//不含标签内容
				String text = textFilterManage.filterText(content);
				//清除空格&nbsp;
				String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
				
				if(isImage == true || (!"".equals(text.trim()) && !"".equals(trimSpace))){
					String username = reply.getUserName();//用户名称
					//修改回复
					int i = commentService.updateReply(replyId,replyContent,username,status,new Date());
					
					if(i >0 && !old_status.equals(status)){
						User user = userManage.query_cache_findUserByUserName(reply.getUserName());
						if(user != null){
							//修改回复状态
							userService.updateUserDynamicReplyStatus(user.getId(),reply.getUserName(),reply.getTopicId(),reply.getCommentId(),reply.getId(),reply.getStatus());
						}
						
					}
					
					
					//删除缓存
					commentManage.delete_cache_findReplyByReplyId(replyId);
				}else{	
					error.put("content", "回复内容不能为空");
					
				}	
			}else{
				error.put("commentId", "回复不存在");
			}
		}else{
			error.put("content", "回复内容不能为空");
		}
		
		if(error.size()==0){
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	/**
	 * 回复  删除
	 * @param model
	 * @param replyId 回复Id
	 */
	@ResponseBody
	@RequestMapping(params="method=deleteReply",method=RequestMethod.POST)
	public String deleteReply(ModelMap model,Long[] replyId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();
		
		if(replyId != null && replyId.length >0){
			List<Long> replyIdList = new ArrayList<Long>();
			for(Long l :replyId){
				if(l != null && l >0L){
					replyIdList .add(l);
				}
			}
			if(replyIdList != null && replyIdList.size() >0){
				List<Reply> replyList = commentService.findByReplyIdList(replyIdList);
				if(replyList != null && replyList.size() >0){
					for(Reply reply : replyList){
						if(reply.getStatus() <100){//标记删除
							Integer constant = 100000;
							int i = commentService.markDeleteReply(reply.getId(),constant);
							
							
							if(i >0){
								User user = userManage.query_cache_findUserByUserName(reply.getUserName());
								if(user != null){
									//修改回复状态
									userService.updateUserDynamicReplyStatus(user.getId(),reply.getUserName(),reply.getTopicId(),reply.getCommentId(),reply.getId(),reply.getStatus()+constant);
								}
								//删除缓存
								commentManage.delete_cache_findReplyByReplyId(reply.getId());
							}
						}else{//物理删除
							int i = commentService.deleteReply(reply.getId());
							if(i >0 && reply != null){
								User user = userManage.query_cache_findUserByUserName(reply.getUserName());
								if(user != null){
									userService.deleteUserDynamicByReplyId(user.getId(),reply.getTopicId(),reply.getCommentId(),reply.getId());
								}
							}

							//删除缓存
							commentManage.delete_cache_findReplyByReplyId(reply.getId());
						}
						
					}
					
				}
				
			}
		}
		
		
		
		if(error.size()==0){
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 回复  恢复
	 * @param model
	 * @param replyId 回复Id
	 */
	@ResponseBody
	@RequestMapping(params="method=recoveryReply",method=RequestMethod.POST)
	public String recoveryReply(ModelMap model,Long replyId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();
		if(replyId != null && replyId >0){
			Reply reply = commentService.findReplyByReplyId(replyId);
			if(reply != null && reply.getStatus() >100){
				int originalState = this.parseInitialValue(reply.getStatus());
				int i = commentService.updateReplyStatus(replyId, originalState);
				
				User user = userManage.query_cache_findUserByUserName(reply.getUserName());
				if(i >0 && user != null){
					//修改回复状态
					userService.updateUserDynamicReplyStatus(user.getId(),reply.getUserName(),reply.getTopicId(),reply.getCommentId(),reply.getId(),originalState);
				}
				//删除缓存
				commentManage.delete_cache_findReplyByReplyId(replyId);
				
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
			}else{
				error.put("replyId", "回复不存在或未标记删除");
			}
			
		}else{
			error.put("replyId", "回复Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	/**
	 * 审核回复
	 * @param model
	 * @param replyId 回复Id
	 * @return
	 * @throws Exception
	*/
	@ResponseBody//方式来做ajax,直接返回字符串
	@RequestMapping(params="method=auditReply",method=RequestMethod.POST)
	public String auditReply(ModelMap model,Long replyId,
			HttpServletResponse response) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		if(replyId != null && replyId>0L){
			int i = commentService.updateReplyStatus(replyId, 20);
			
			Reply reply = commentManage.query_cache_findReplyByReplyId(replyId);
			if(reply != null){
				User user = userManage.query_cache_findUserByUserName(reply.getUserName());
				if(i >0 && user != null){
					//修改回复状态
					userService.updateUserDynamicReplyStatus(user.getId(),reply.getUserName(),reply.getTopicId(),reply.getCommentId(),reply.getId(),20);
				}
			}
			
			//删除缓存
			commentManage.delete_cache_findReplyByReplyId(replyId);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("replyId", "回复Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
}
