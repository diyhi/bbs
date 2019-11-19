package cms.web.action.question;

import java.io.File;
import java.util.Date;
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

import cms.bean.PageForm;
import cms.bean.question.Answer;
import cms.bean.question.AnswerReply;
import cms.bean.setting.EditorTag;
import cms.bean.staff.SysUsers;
import cms.bean.user.User;
import cms.service.question.AnswerService;
import cms.service.question.QuestionService;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.web.action.FileManage;
import cms.web.action.TextFilterManage;
import cms.web.action.filterWord.SensitiveWordFilterManage;
import cms.web.action.setting.SettingManage;
import cms.web.action.user.UserManage;

/**
 * 答案
 *
 */
@Controller
@RequestMapping("/control/answer/manage") 
public class AnswerManageAction {

	@Resource AnswerService answerService;//通过接口引用代理返回的对象
	@Resource SettingManage settingManage;
	@Resource AnswerManage answerManage;
	@Resource FileManage fileManage;
	
	@Resource TextFilterManage textFilterManage;
	
	@Resource SensitiveWordFilterManage sensitiveWordFilterManage;
	
	@Resource SettingService settingService;
	
	@Resource UserService userService;
	@Resource UserManage userManage;
	@Resource QuestionManage questionManage;
	@Resource QuestionService questionService;
	
	/**
	 * 答案  添加
	 */
	@RequestMapping(params="method=add",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String add(ModelMap model,Long questionId,String content,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();
		if(questionId == null || questionId <=0L){
			error.put("questionId", "问题Id不能为空");
		}
		if(error.size() == 0 && content != null && !"".equals(content.trim())){
			
			//过滤标签
			content = textFilterManage.filterTag(request,content,settingManage.readAnswerEditorTag());
			Object[] object = textFilterManage.filterHtml(request,content,"answer",settingManage.readAnswerEditorTag());
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
				
				Answer answer= new Answer();
				answer.setQuestionId(questionId);
				answer.setContent(value);
				answer.setIsStaff(true);
				answer.setUserName(username);
				answer.setIp(IpAddress.getClientIpAddress(request));
				answer.setStatus(20);
				//保存答案
				answerService.saveAnswer(answer);
				
				//修改问题最后回答时间
				questionService.updateQuestionAnswerTime(questionId,new Date());
				
				
				//上传文件编号
				String fileNumber = "a"+userId;
				
				//删除图片锁
				if(imageNameList != null && imageNameList.size() >0){
					for(String imageName :imageNameList){
						if(imageName != null && !"".equals(imageName.trim())){
							//如果验证不是当前用户上传的文件，则不删除锁
							 if(!questionManage.getFileNumber(fileManage.getBaseName(imageName.trim())).equals(fileNumber)){
								 continue;
							 }
							 fileManage.deleteLock("file"+File.separator+"answer"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
						 }
					
					}
				}
			}	
		}else{
			error.put("content", "回答内容不能为空");
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
	 * 答案  修改页面显示
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(ModelMap model,Long answerId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		model.addAttribute("availableTag",answerManage.availableTag());
		Answer answer = answerService.findByAnswerId(answerId);
		if(answer != null){
			model.addAttribute("answer", answer);
			String username = "";
			Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
			if(obj instanceof UserDetails){
				username =((UserDetails)obj).getUsername();	
			}
			model.addAttribute("userName", username);
		}
		return "jsp/question/edit_answer";
	}
	/**
	 * 答案  修改
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String edit(PageForm pageForm,ModelMap model,Long answerId,String content,Integer status,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Answer answer = null;
		Integer old_status = -1;
		Map<String,String> error = new HashMap<String,String>();
		if(answerId == null || answerId <=0){
			error.put("answerId", "答案Id不能为空");
		}else{
			answer = answerService.findByAnswerId(answerId);
		}
		if(content != null && !"".equals(content.trim())){
			if(answer != null){
				old_status = answer.getStatus();
				answer.setStatus(status);
				
				
				//过滤标签
				content = textFilterManage.filterTag(request,content,settingManage.readAnswerEditorTag());
				Object[] object = textFilterManage.filterHtml(request,content,"answer",settingManage.readAnswerEditorTag());
				String value = (String)object[0];
				List<String> imageNameList = (List<String>)object[1];
				boolean isImage = (Boolean)object[2];//是否含有图片
				//不含标签内容
				String text = textFilterManage.filterText(content);
				//清除空格&nbsp;
				String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
				
				if(isImage == true || (!"".equals(text.trim()) && !"".equals(trimSpace))){

					String username = answer.getUserName();//用户名称
					
					//修改答案
					int i = answerService.updateAnswer(answer.getId(),value,status,username);
					
					
					if(i >0 && !old_status.equals(status)){
						User user = userManage.query_cache_findUserByUserName(answer.getUserName());
						if(user != null){
							//修改答案状态
							userService.updateUserDynamicAnswerStatus(user.getId(),answer.getUserName(),answer.getQuestionId(),answer.getId(),answer.getStatus());
						}
						
					}
					
					
					
					//删除缓存
					answerManage.delete_cache_findByAnswerId(answer.getId());
					answerManage.delete_cache_answerCount(answer.getUserName());
					//上传文件编号
					String fileNumber = questionManage.generateFileNumber(answer.getUserName(), answer.getIsStaff());

						
					if(imageNameList != null && imageNameList.size() >0){
						for(String imageName :imageNameList){
							if(imageName != null && !"".equals(imageName.trim())){
								 //如果验证不是当前用户上传的文件，则不删除
								 if(!questionManage.getFileNumber(fileManage.getBaseName(imageName.trim())).equals(fileNumber)){
										continue;
								 }
								 fileManage.deleteLock("file"+File.separator+"answer"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
							 }
						
						}
					}

					//旧图片名称
					List<String> old_ImageName = textFilterManage.readImageName(answer.getContent(),"answer");
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
								 if(!questionManage.getFileNumber(fileManage.getBaseName(imagePath.trim())).equals(fileNumber)){
										continue;
								 }
								//替换路径中的..号
								imagePath = fileManage.toRelativePath(imagePath);
								//替换路径分割符
								imagePath = StringUtils.replace(imagePath, "/", File.separator);
								
								Boolean state = fileManage.deleteFile(imagePath);
								if(state != null && state == false){	
									//替换指定的字符，只替换第一次出现的
									imagePath = StringUtils.replaceOnce(imagePath, "file"+File.separator+"answer"+File.separator, "");
									imagePath = StringUtils.replace(imagePath, File.separator, "_");//替换所有出现过的字符
									
									//创建删除失败文件
									fileManage.failedStateFile("file"+File.separator+"answer"+File.separator+"lock"+File.separator+imagePath);
								
								}
							}
							
						}
					}	
				}else{	
					error.put("content", "答案内容不能为空");
				}	
			}else{
				error.put("answerId", "答案不能为空");
			}
		}else{
			error.put("content", "答案内容不能为空");
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
	 * 答案  删除
	 * @param model
	 * @param answerId 答案Id
	*/
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Long answerId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(answerId != null && answerId >0L){
			Answer answer = answerService.findByAnswerId(answerId);
			if(answer != null){
				
				int i = answerService.deleteAnswer(answer.getQuestionId(),answerId);
				if(i >0){
					//根据答案Id删除用户动态(答案下的回复也同时删除)
					userService.deleteUserDynamicByAnswerId(answer.getQuestionId(),answerId);
					
					
					//删除缓存
					answerManage.delete_cache_findByAnswerId(answer.getId());
					answerManage.delete_cache_answerCount(answer.getUserName());
					String fileNumber = questionManage.generateFileNumber(answer.getUserName(), answer.getIsStaff());
					
					//删除图片
					List<String> imageNameList = textFilterManage.readImageName(answer.getContent(),"answer");
					if(imageNameList != null && imageNameList.size() >0){
						for(String imagePath : imageNameList){
							//如果验证不是当前用户上传的文件，则不删除锁
							 if(!questionManage.getFileNumber(fileManage.getBaseName(imagePath.trim())).equals(fileNumber)){
								 continue;
							 }
							
							
							//替换路径中的..号
							imagePath = fileManage.toRelativePath(imagePath);
							//替换路径分割符
							imagePath = StringUtils.replace(imagePath, "/", File.separator);
							
							Boolean state = fileManage.deleteFile(imagePath);
							
							if(state != null && state == false){	
								//替换指定的字符，只替换第一次出现的
								imagePath = StringUtils.replaceOnce(imagePath, "file"+File.separator+"answer"+File.separator, "");
								imagePath = StringUtils.replace(imagePath, File.separator, "_");//替换所有出现过的字符
								
								//创建删除失败文件
								fileManage.failedStateFile("file"+File.separator+"answer"+File.separator+"lock"+File.separator+imagePath);
							
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
	 * 答案  图片上传
	 * 
	 * 员工发话题 上传文件名为UUID + a + 员工Id
	 * 用户发话题 上传文件名为UUID + b + 用户Id
	 * @param topicId 话题Id
	 * @param userName 用户名称
	 * @param isStaff 是否是员工   true:员工   false:会员
	 */
	@RequestMapping(params="method=uploadImage",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String uploadImage(ModelMap model,Long questionId,String userName, Boolean isStaff,
			MultipartFile imgFile, HttpServletResponse response) throws Exception {
		
		String number = questionManage.generateFileNumber(userName, isStaff);
		
		Map<String,Object> returnJson = new HashMap<String,Object>();
		
		//文件上传
		if(imgFile != null && !imgFile.isEmpty() && questionId != null && questionId >0L && number != null && !"".equals(number.trim())){
			EditorTag editorSiteObject = settingManage.readAnswerEditorTag();
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
						String pathDir = "file"+File.separator+"answer"+File.separator + questionId + File.separator;
						//文件锁目录
						String lockPathDir = "file"+File.separator+"answer"+File.separator+"lock"+File.separator;
						//构建文件名称
						String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;
						
						//生成文件保存目录
						fileManage.createFolder(pathDir);
						//生成锁文件保存目录
						fileManage.createFolder(lockPathDir);
						//生成锁文件
						fileManage.newFile(lockPathDir+questionId+"_"+newFileName);
						//保存文件
						fileManage.writeFile(pathDir, newFileName,imgFile.getBytes());
		
						//上传成功
						returnJson.put("error", 0);//0成功  1错误
						returnJson.put("url", "file/answer/"+questionId+"/"+newFileName);
						
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
	 * 审核答案
	 * @param model
	 * @param answerId 答案Id
	 * @return
	 * @throws Exception
	*/
	@RequestMapping(params="method=auditAnswer",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String auditAnswer(ModelMap model,Long answerId,
			HttpServletResponse response) throws Exception {
		if(answerId != null && answerId>0L){
			int i = answerService.updateAnswerStatus(answerId, 20);
			
			Answer answer = answerManage.query_cache_findByAnswerId(answerId);
			if(i >0 && answer != null){
				User user = userManage.query_cache_findUserByUserName(answer.getUserName());
				if(user != null){
					//修改答案状态
					userService.updateUserDynamicAnswerStatus(user.getId(),answer.getUserName(),answer.getQuestionId(),answer.getId(),20);
				}
				//删除缓存
				answerManage.delete_cache_findByAnswerId(answerId);
				answerManage.delete_cache_answerCount(answer.getUserName());
			}
			
			
			return "1";
		}
		return "0";
	}
	
	
	
	/**
	 * 答案回复  添加页面显示
	 * @param pageForm
	 * @param model
	 * @param answerId 答案Id
	 */
	@RequestMapping(params="method=addAnswerReply",method=RequestMethod.GET)
	public String addAnswerReplyUI(ModelMap model,Long answerId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		return "jsp/question/add_answerReply";
	}
	/**
	 * 答案回复  添加
	 * @param model
	 * @param answerId 答案Id
	 */
	@RequestMapping(params="method=addAnswerReply",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String addAnswerReply(ModelMap model,Long answerId,String content,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Answer answer = null;
		Map<String,String> error = new HashMap<String,String>();
		if(answerId == null || answerId <=0){
			error.put("answerId", "答案Id不能为空");
		}else{
			answer = answerService.findByAnswerId(answerId);
		}
		
		
		if(content != null && !"".equals(content.trim())){
			if(answer != null){
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
					AnswerReply answerReply = new AnswerReply();
					answerReply.setAnswerId(answer.getId());
					answerReply.setIsStaff(true);
					answerReply.setUserName(username);
					answerReply.setContent(text);
					answerReply.setQuestionId(answer.getQuestionId());
					answerReply.setStatus(20);
					answerReply.setIp(IpAddress.getClientIpAddress(request));
					//保存答案回复
					answerService.saveReply(answerReply);

					
					//修改问题最后回答时间
					questionService.updateQuestionAnswerTime(answer.getQuestionId(),new Date());
				}else{	
					error.put("content", "回复内容不能为空");
					
				}	
			}else{
				error.put("answerId", "答案不存在");
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
	 * 答案回复  修改页面显示
	 * @param pageForm
	 * @param model
	 * @param answerReplyId 答案回复Id
	 */
	@RequestMapping(params="method=editAnswerReply",method=RequestMethod.GET)
	public String editAnswerReplyUI(ModelMap model,Long answerReplyId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(answerReplyId != null && answerReplyId >0){
			AnswerReply answerReply = answerService.findReplyByReplyId(answerReplyId);
			if(answerReply != null){
				if(answerReply.getIp() != null && !"".equals(answerReply.getIp().trim())){
					
					answerReply.setIpAddress(IpAddress.queryAddress(answerReply.getIp()));
				}
				model.addAttribute("answerReply", answerReply);
			}
		}
		return "jsp/question/edit_answerReply";
	}
	/**
	 * 答案回复  修改页面
	 * @param pageForm
	 * @param model
	 * @param answerReplyId 答案回复Id
	*/
	@RequestMapping(params="method=editAnswerReply",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String editAnswerReply(ModelMap model,Long answerReplyId,String content,Integer status,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		AnswerReply answerReply = null;
		Integer old_status = -1;
		Map<String,String> error = new HashMap<String,String>();
		if(answerReplyId != null && answerReplyId >0){
			answerReply = answerService.findReplyByReplyId(answerReplyId);
		}else{
			error.put("answerReplyId", "回复Id不能为空");
			
		}

		if(content != null && !"".equals(content.trim())){
			if(answerReply != null){
				old_status = answerReply.getStatus();
				answerReply.setStatus(status);
				
				//不含标签内容
				String text = textFilterManage.filterText(content);
				//清除空格&nbsp;
				String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
				
				if((!"".equals(text.trim()) && !"".equals(trimSpace))){
					String username = answerReply.getUserName();//用户名称
					//修改回复
					int i = answerService.updateReply(answerReplyId,text,username,status);
					
					if(i >0 && !old_status.equals(status)){
						User user = userManage.query_cache_findUserByUserName(answerReply.getUserName());
						if(user != null){
							//修改答案回复状态
							userService.updateUserDynamicAnswerReplyStatus(user.getId(),answerReply.getUserName(),answerReply.getQuestionId(),answerReply.getAnswerId(),answerReply.getId(),answerReply.getStatus());
						}
						
					}
					
					
					//删除缓存
					answerManage.delete_cache_findReplyByReplyId(answerReplyId);
				}else{	
					error.put("content", "回复内容不能为空");
					
				}	
			}else{
				error.put("answerReplyId", "回复不存在");
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
	 * @param answerReplyId 回复Id
	 */
	@RequestMapping(params="method=deleteAnswerReply",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String deleteAnswerReply(ModelMap model,Long answerReplyId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(answerReplyId != null && answerReplyId >0){
			
			AnswerReply answerReply = answerManage.query_cache_findReplyByReplyId(answerReplyId);
			int i = answerService.deleteReply(answerReplyId);
			if(i >0 && answerReply != null){
				User user = userManage.query_cache_findUserByUserName(answerReply.getUserName());
				if(user != null){
					userService.deleteUserDynamicByAnswerReplyId(user.getId(),answerReply.getQuestionId(),answerReply.getAnswerId(),answerReplyId);
				}
			}

			//删除缓存
			answerManage.delete_cache_findReplyByReplyId(answerReplyId);
			if(i >0){
				return "1";
			}
		}
		return "0";
	}
	/**
	 * 审核回复
	 * @param model
	 * @param answerReplyId 回复Id
	 * @return
	 * @throws Exception
	*/
	@RequestMapping(params="method=auditAnswerReply",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String auditAnswerReply(ModelMap model,Long answerReplyId,
			HttpServletResponse response) throws Exception {
		
		if(answerReplyId != null && answerReplyId>0L){
			int i = answerService.updateReplyStatus(answerReplyId, 20);
			
			AnswerReply answerReply = answerManage.query_cache_findReplyByReplyId(answerReplyId);
			if(answerReply != null){
				User user = userManage.query_cache_findUserByUserName(answerReply.getUserName());
				if(i >0 && user != null){
					//修改答案回复状态
					userService.updateUserDynamicAnswerReplyStatus(user.getId(),answerReply.getUserName(),answerReply.getQuestionId(),answerReply.getAnswerId(),answerReply.getId(),20);
				}
			}
			
			//删除缓存
			answerManage.delete_cache_findReplyByReplyId(answerReplyId);
			return "1";
		}
		return "0";
	}
	
	
	/**
	 * 采纳答案
	 * @param model
	 * @param answerId 答案Id
	 * @return
	 * @throws Exception
	*/
	@RequestMapping(params="method=adoptionAnswer",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String adoptionAnswer(ModelMap model,Long answerId,
			HttpServletResponse response) throws Exception {
		
		if(answerId != null && answerId>0L){
			Answer answer = answerManage.query_cache_findByAnswerId(answerId);
			if(answer != null){
				int i = answerService.updateAdoptionAnswer(answer.getQuestionId(), answerId);
				//删除缓存
				answerManage.delete_cache_findByAnswerId(answerId);
				questionManage.delete_cache_findById(answer.getQuestionId());
				answerManage.delete_cache_answerCount(answer.getUserName());
			}
			return "1";
		}
		return "0";
	}
	/**
	 * 取消采纳答案
	 * @param model
	 * @param answerId 答案Id
	 * @return
	 * @throws Exception
	*/
	@RequestMapping(params="method=cancelAdoptionAnswer",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String cancelAdoptionAnswer(ModelMap model,Long answerId,
			HttpServletResponse response) throws Exception {
		
		if(answerId != null && answerId>0L){
			Answer answer = answerManage.query_cache_findByAnswerId(answerId);
			if(answer != null){
				int i = answerService.updateCancelAdoptionAnswer(answer.getQuestionId());
				//删除缓存
				answerManage.delete_cache_findByAnswerId(answerId);
				questionManage.delete_cache_findById(answer.getQuestionId());
				answerManage.delete_cache_answerCount(answer.getUserName());
			}
			return "1";
		}
		return "0";
	}
	
	
	
}
