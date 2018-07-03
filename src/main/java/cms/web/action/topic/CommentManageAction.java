package cms.web.action.topic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import cms.bean.setting.EditorTag;
import cms.bean.staff.SysUsers;
import cms.bean.topic.Comment;
import cms.bean.topic.Quote;
import cms.bean.topic.Reply;
import cms.service.setting.SettingService;
import cms.service.topic.CommentService;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.web.action.FileManage;
import cms.web.action.TextFilterManage;
import cms.web.action.filterWord.SensitiveWordFilterManage;
import cms.web.action.setting.SettingManage;

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
	@Resource FileManage fileManage;
	
	@Resource TextFilterManage textFilterManage;
	
	@Resource SensitiveWordFilterManage sensitiveWordFilterManage;
	
	@Resource SettingService settingService;
	@Resource TopicManage topicManage;
	
	/**
	 * 评论  添加
	 */
	@RequestMapping(params="method=add",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String add(ModelMap model,Long topicId,String content,
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
				comment.setIsStaff(true);
				comment.setUserName(username);
				comment.setIp(IpAddress.getClientIpAddress(request));
				comment.setStatus(20);
				//保存评论
				commentService.saveComment(comment);
				
				//上传文件编号
				String fileNumber = "a"+userId;
				
				//删除图片锁
				if(imageNameList != null && imageNameList.size() >0){
					for(String imageName :imageNameList){
						if(imageName != null && !"".equals(imageName.trim())){
							//如果验证不是当前用户上传的文件，则不删除锁
							 if(!topicManage.getFileNumber(fileManage.getBaseName(imageName.trim())).equals(fileNumber)){
								 continue;
							 }
							 fileManage.deleteLock("file"+File.separator+"comment"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
						 }
					
					}
				}
			}	
		}else{
			error.put("content", "评论内容不能不能为空");
		}
		
		
	
		
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
		
		if(error != null && error.size() >0){
			returnValue.put("success", "false");
			returnValue.put("error", error);
		}else{
			returnValue.put("success", "true");
			
		}
		return JsonUtils.toJSONString(returnValue);
		
	}
	
	
	/**
	 * 评论  修改页面显示
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(ModelMap model,Long commentId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		model.addAttribute("availableTag",commentManage.availableTag());
		Comment comment = commentService.findByCommentId(commentId);
		if(comment != null){
			
			if(comment.getQuote() != null && !"".equals(comment.getQuote().trim())){
				//引用
				List<Quote> customQuoteList = JsonUtils.toGenericObject(comment.getQuote(), new TypeReference< List<Quote> >(){});
				comment.setQuoteList(customQuoteList);
			}
			model.addAttribute("comment", comment);
			String username = "";
			Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
			if(obj instanceof UserDetails){
				username =((UserDetails)obj).getUsername();	
			}
			model.addAttribute("userName", username);
		}
		return "jsp/topic/edit_comment";
	}
	/**
	 * 评论  修改
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String edit(PageForm pageForm,ModelMap model,Long commentId,String content,Integer status,
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
					commentService.updateComment(comment.getId(),value,status,lowerQuoteIdGroup,username);
					
					//上传文件编号
					String fileNumber = topicManage.generateFileNumber(comment.getUserName(), comment.getIsStaff());
					
						
					if(imageNameList != null && imageNameList.size() >0){
						for(String imageName :imageNameList){
							if(imageName != null && !"".equals(imageName.trim())){
								 //如果验证不是当前用户上传的文件，则不删除
								 if(!topicManage.getFileNumber(fileManage.getBaseName(imageName.trim())).equals(fileNumber)){
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
				        	String old_name = fileManage.getName(imageName);
				        	
							for(String new_imageName : imageNameList){
								String new_name = fileManage.getName(new_imageName);
								if(old_name.equals(new_name)){
									iter.remove();
									break;
								}
							}
						}
						if(old_ImageName != null && old_ImageName.size() >0){
							for(String imagePath : old_ImageName){
								 //如果验证不是当前用户上传的文件，则不删除
								 if(!topicManage.getFileNumber(fileManage.getBaseName(imagePath.trim())).equals(fileNumber)){
										continue;
								 }
								//替换路径中的..号
								imagePath = fileManage.toRelativePath(imagePath);
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
				error.put("commentId", "引用评论不能为空");
			}
		}else{
			error.put("content", "评论内容不能为空");
		}

		
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
		
		if(error != null && error.size() >0){
			returnValue.put("success", "false");
			returnValue.put("error", error);
		}else{
			returnValue.put("success", "true");
			
		}
		return JsonUtils.toJSONString(returnValue);
	}
	/**
	 * 评论  删除
	 * @param model
	 * @param commentId 评论Id
	 */
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Long commentId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(commentId != null && commentId >0L){
			Comment comment = commentService.findByCommentId(commentId);
			if(comment != null){
				
				int i = commentService.deleteComment(comment.getTopicId(),commentId);
				if(i >0){
					String fileNumber = topicManage.generateFileNumber(comment.getUserName(), comment.getIsStaff());
					
					//删除图片
					List<String> imageNameList = textFilterManage.readImageName(comment.getContent(),"comment");
					if(imageNameList != null && imageNameList.size() >0){
						for(String imagePath : imageNameList){
							//如果验证不是当前用户上传的文件，则不删除锁
							 if(!topicManage.getFileNumber(fileManage.getBaseName(imagePath.trim())).equals(fileNumber)){
								 continue;
							 }
							
							
							//替换路径中的..号
							imagePath = fileManage.toRelativePath(imagePath);
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
					return "1";
				}
			}
		}
		return "0";
	}
	
	/**
	 * 评论  图片上传
	 * 
	 * 员工发话题 上传文件名为UUID + a + 员工Id
	 * 用户发话题 上传文件名为UUID + b + 用户Id
	 * @param topicId 话题Id
	 * @param userName 用户名称
	 * @param isStaff 是否是员工   true:员工   false:会员
	 */
	@RequestMapping(params="method=uploadImage",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String uploadImage(ModelMap model,Long topicId,String userName, Boolean isStaff,
			MultipartFile imgFile, HttpServletResponse response) throws Exception {
		
		String number = topicManage.generateFileNumber(userName, isStaff);
		
		Map<String,Object> returnJson = new HashMap<String,Object>();
		
		//文件上传
		if(imgFile != null && !imgFile.isEmpty() && topicId != null && topicId >0L && number != null && !"".equals(number.trim())){
			EditorTag editorSiteObject = settingManage.readEditorTag();
			if(editorSiteObject != null){
				if(editorSiteObject.isImage()){//允许上传图片
					//当前图片文件名称
					String fileName = imgFile.getOriginalFilename();
					//文件大小
					Long size = imgFile.getSize();
					//取得文件后缀
					String suffix = fileName.substring(fileName.lastIndexOf('.')+1).toLowerCase();
					
					
					//允许上传图片格式
					List<String> imageFormat = editorSiteObject.getImageFormat();
					//允许上传图片大小
					long imageSize = editorSiteObject.getImageSize();

					//验证文件类型
					boolean authentication = fileManage.validateFileSuffix(imgFile.getOriginalFilename(),imageFormat);
					
					if(authentication && size/1024 <= imageSize){
						
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
						fileManage.newFile(lockPathDir+topicId+"_"+newFileName);
						//保存文件
						fileManage.writeFile(pathDir, newFileName,imgFile.getBytes());
		
						//上传成功
						returnJson.put("error", 0);//0成功  1错误
						returnJson.put("url", "file/comment/"+topicId+"/"+newFileName);
						
						return JsonUtils.toJSONString(returnJson);
					}
				}
			}
			
		}
		
		//上传失败
		returnJson.put("error", 1);
		returnJson.put("message", "上传失败");
		return JsonUtils.toJSONString(returnJson);
	}
	/**
	 * 引用  添加页面显示
	 * @param model
	 * @param commentId 评论Id
	 */
	@RequestMapping(params="method=addQuote",method=RequestMethod.GET)
	public String addQuoteUI(ModelMap model,Long commentId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		model.addAttribute("availableTag", commentManage.availableTag());
		Comment comment = commentService.findByCommentId(commentId);
		if(comment != null){
			
			if(comment.getQuote() != null && !"".equals(comment.getQuote().trim())){
				//引用
				List<Quote> customQuoteList = JsonUtils.toGenericObject(comment.getQuote(), new TypeReference< List<Quote> >(){});
				comment.setQuoteList(customQuoteList);
			}
			model.addAttribute("comment", comment);
		}
		String username = "";//用户名称
		
		Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(obj instanceof UserDetails){
			username =((UserDetails)obj).getUsername();	
		}
		model.addAttribute("userName", username);
		return "jsp/topic/add_quote";
	 }
	/**
	 * 引用  添加
	 * @param model
	 * @param commentId 评论Id
	 */
	@RequestMapping(params="method=addQuote",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
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
					quote.setContent(textFilterManage.filterText(comment.getContent()));
					old_quoteList.add(quote);
					
					//自定义评论
					Comment newComment = new Comment();
					newComment.setTopicId(comment.getTopicId());
					newComment.setContent(value);
					newComment.setIsStaff(true);
					newComment.setQuoteIdGroup(old_quoteId);
					newComment.setUserName(username);
					newComment.setQuote(JsonUtils.toJSONString(old_quoteList));
					
					//保存评论
					commentService.saveComment(newComment);
					//上传文件编号
					String fileNumber = "a"+userId;
						
					if(imageNameList != null && imageNameList.size() >0){
						for(String imageName :imageNameList){
							if(imageName != null && !"".equals(imageName.trim())){
								//如果验证不是当前用户上传的文件，则不删除锁
								 if(!topicManage.getFileNumber(fileManage.getBaseName(imageName.trim())).equals(fileNumber)){
									 continue;
								 }
								 fileManage.deleteLock("file"+File.separator+"comment"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
							 }
						
						}
					}	
				}else{	
					error.put("content", "评论内容不能不能为空");
					
				}	
			}else{
				error.put("commentId", "引用评论不能为空");
			}
		}else{
			error.put("content", "评论内容不能不能为空");
		}
		
		
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
		
		if(error != null && error.size() >0){
			returnValue.put("success", "false");
			returnValue.put("error", error);
		}else{
			returnValue.put("success", "true");
			
		}
		return JsonUtils.toJSONString(returnValue);
	 }
	
	
	/**
	 * 审核评论
	 * @param model
	 * @param commentId 评论Id
	 * @return
	 * @throws Exception
	*/
	@RequestMapping(params="method=auditComment",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String auditComment(ModelMap model,Long commentId,
			HttpServletResponse response) throws Exception {
		if(commentId != null && commentId>0L){
			commentService.updateCommentStatus(commentId, 20);
			return "1";
		}
		return "0";
	}
	
	
	
	/**
	 * 回复  添加页面显示
	 * @param pageForm
	 * @param model
	 * @param commentId 自定义评论Id
	 */
	@RequestMapping(params="method=addReply",method=RequestMethod.GET)
	public String addReplyUI(ModelMap model,Long commentId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		return "jsp/topic/add_reply";
	}
	/**
	 * 回复  添加
	 * @param model
	 * @param commentId 评论Id
	 */
	@RequestMapping(params="method=addReply",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String addReply(ModelMap model,Long commentId,String content,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Comment comment = null;
		Map<String,String> error = new HashMap<String,String>();
		if(commentId == null || commentId <=0){
			error.put("commentId", "评论Id不能为空");
		}else{
			comment = commentService.findByCommentId(commentId);
		}
		
		
		if(content != null && !"".equals(content.trim())){
			if(comment != null){
				//不含标签内容
				String text = textFilterManage.filterText(content);
				//清除空格&nbsp;
				String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
				
				if((!"".equals(text.trim()) && !"".equals(trimSpace))){
					String username = "";//用户名称
					
					Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
					if(obj instanceof UserDetails){
						username =((UserDetails)obj).getUsername();	
					}
					
				
					//回复
					Reply reply = new Reply();
					reply.setCommentId(comment.getId());
					reply.setIsStaff(true);
					reply.setUserName(username);
					reply.setContent(text);
					reply.setTopicId(comment.getTopicId());
					reply.setStatus(20);
					reply.setIp(IpAddress.getClientIpAddress(request));
					//保存评论
					commentService.saveReply(reply);

				}else{	
					error.put("content", "回复内容不能为空");
					
				}	
			}else{
				error.put("commentId", "评论不存在");
			}
		}else{
			error.put("content", "回复内容不能为空");
		}
		
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
		
		if(error != null && error.size() >0){
			returnValue.put("success", "false");
			returnValue.put("error", error);
		}else{
			returnValue.put("success", "true");
			
		}
		return JsonUtils.toJSONString(returnValue);
	}
	/**
	 * 回复  修改页面显示
	 * @param pageForm
	 * @param model
	 * @param replyId 回复Id
	 */
	@RequestMapping(params="method=editReply",method=RequestMethod.GET)
	public String editReplyUI(ModelMap model,Long replyId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(replyId != null && replyId >0){
			Reply reply = commentService.findReplyByReplyId(replyId);
			if(reply != null){
				if(reply.getIp() != null && !"".equals(reply.getIp().trim())){
					
					reply.setIpAddress(IpAddress.queryAddress(reply.getIp()));
				}
				model.addAttribute("reply", reply);
			}
		}
		return "jsp/topic/edit_reply";
	}
	/**
	 * 回复  修改页面
	 * @param pageForm
	 * @param model
	 * @param replyId 回复Id
	 */
	@RequestMapping(params="method=editReply",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String editReply(ModelMap model,Long replyId,String content,Integer status,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Reply reply = null;
		Map<String,String> error = new HashMap<String,String>();
		if(replyId != null && replyId >0){
			reply = commentService.findReplyByReplyId(replyId);
		}else{
			error.put("replyId", "回复Id不能为空");
			
		}

		if(content != null && !"".equals(content.trim())){
			if(reply != null){
				reply.setStatus(status);
				
				//不含标签内容
				String text = textFilterManage.filterText(content);
				//清除空格&nbsp;
				String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
				
				if((!"".equals(text.trim()) && !"".equals(trimSpace))){
					String username = reply.getUserName();//用户名称
					//修改回复
					commentService.updateReply(replyId,text,username,status);

				}else{	
					error.put("content", "回复内容不能为空");
					
				}	
			}else{
				error.put("commentId", "回复不存在");
			}
		}else{
			error.put("content", "回复内容不能为空");
		}
		
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
		
		if(error != null && error.size() >0){
			returnValue.put("success", "false");
			returnValue.put("error", error);
		}else{
			returnValue.put("success", "true");
			
		}
		return JsonUtils.toJSONString(returnValue);
	}
	/**
	 * 回复  删除
	 * @param pageForm
	 * @param model
	 * @param replyId 回复Id
	 */
	@RequestMapping(params="method=deleteReply",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String deleteReply(ModelMap model,Long replyId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(replyId != null && replyId >0){
			int i = commentService.deleteReply(replyId);
			if(i >0){
				return "1";
			}
		}
		return "0";
	}
	/**
	 * 审核回复
	 * @param model
	 * @param replyId 回复Id
	 * @return
	 * @throws Exception
	*/
	@RequestMapping(params="method=auditReply",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String auditReply(ModelMap model,Long replyId,
			HttpServletResponse response) throws Exception {
		
		if(replyId != null && replyId>0L){
			commentService.updateReplyStatus(replyId, 20);
			return "1";
		}
		return "0";
	}
	
	
}
