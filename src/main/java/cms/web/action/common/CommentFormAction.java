package cms.web.action.common;



import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.ErrorView;
import cms.bean.setting.EditorTag;
import cms.bean.setting.SystemSetting;
import cms.bean.topic.Comment;
import cms.bean.topic.Quote;
import cms.bean.topic.Reply;
import cms.bean.topic.Topic;
import cms.bean.user.AccessUser;
import cms.bean.user.PointLog;
import cms.bean.user.User;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.service.topic.CommentService;
import cms.service.topic.TopicService;
import cms.service.user.UserService;
import cms.utils.Base64;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.utils.RefererCompare;
import cms.utils.UUIDUtil;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.CSRFTokenManage;
import cms.web.action.FileManage;
import cms.web.action.TextFilterManage;
import cms.web.action.filterWord.SensitiveWordFilterManage;
import cms.web.action.setting.SettingManage;
import cms.web.action.topic.TopicManage;
import cms.web.action.user.PointManage;
import cms.web.taglib.Configuration;

/**
 * 评论接收表单
 *
 */
@Controller
@RequestMapping("user/control/comment") 
public class CommentFormAction {
	@Resource TemplateService templateService;
	
	@Resource CaptchaManage captchaManage;
	@Resource FileManage fileManage;
	@Resource CommentService commentService;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	
	@Resource TextFilterManage textFilterManage;
	@Resource SettingManage settingManage;
	@Resource SettingService settingService;
	@Resource UserService userService;
	@Resource TopicService topicService;
	@Resource PointManage pointManage;
	
	@Resource CSRFTokenManage csrfTokenManage;
	
	@Resource TopicManage topicManage;
	@Resource SensitiveWordFilterManage sensitiveWordFilterManage;
	
	/**
	 * 评论   添加
	 * @param model
	 * @param topicId 话题Id
	 * @param content 评论内容
	 * @param jumpUrl 跳转地址
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(ModelMap model,Long topicId,String content,
			String token,String captchaKey,String captchaValue,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		
		Map<String,String> error = new HashMap<String,String>();
		
			
		
		//判断令牌
		if(token != null && !"".equals(token.trim())){	
			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
				if(!token_sessionid.equals(token)){
					error.put("token", ErrorView._13.name());//令牌错误
				}
			}else{
				error.put("token", ErrorView._12.name());//令牌过期
			}
		}else{
			error.put("token", ErrorView._11.name());//令牌为空
		}
		
		//验证码
		boolean isCaptcha = captchaManage.comment_isCaptcha(accessUser.getUserName());
		if(isCaptcha){//如果需要验证码
			//验证验证码
			if(captchaKey != null && !"".equals(captchaKey.trim())){
				//增加验证码重试次数
				//统计每分钟原来提交次数
				int quantity = settingManage.submitQuantity_add("captcha", captchaKey.trim(), 0);
				//删除每分钟原来提交次数
				settingManage.submitQuantity_delete("captcha", captchaKey.trim());
				//刷新每分钟原来提交次数
				settingManage.submitQuantity_add("captcha", captchaKey.trim(), quantity+1);
				
				String _captcha = captchaManage.captcha_generate(captchaKey.trim(),"");
				if(captchaValue != null && !"".equals(captchaValue.trim())){
					if(_captcha != null && !"".equals(_captcha.trim())){
						if(!_captcha.equals(captchaValue)){
							error.put("captchaValue",ErrorView._15.name());//验证码错误
						}
					}else{
						error.put("captchaValue",ErrorView._17.name());//验证码过期
					}
				}else{
					error.put("captchaValue",ErrorView._16.name());//请输入验证码
				}
				//删除验证码
				captchaManage.captcha_delete(captchaKey.trim());	
			}else{
				error.put("captchaValue", ErrorView._14.name());//验证码参数错误
			}
			
		}
		
		Comment comment = new Comment();
		List<String> imageNameList = null;
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		//前台发表评论默认状态
		if(systemSetting.getComment_defaultState() != null){
			if(systemSetting.getComment_defaultState().equals(10)){
				comment.setStatus(10);
			}else if(systemSetting.getComment_defaultState().equals(20)){
				comment.setStatus(20);
			}
		}
		
		
		if(topicId != null && topicId >0L){
			Topic topic = topicService.findById(topicId);
			if(topic != null){
				comment.setTopicId(topicId);
				//是否全局允许评论
				if(systemSetting.isAllowComment() == false){
					error.put("comment", ErrorView._106.name());//禁止评论
				}
				if(topic.isAllow() == false){
					error.put("comment", ErrorView._106.name());//禁止评论	
				}
				if(!topic.getStatus().equals(20)){//已发布
					error.put("comment", ErrorView._111.name());//已发布话题才允许评论	
				}
			}else{
				error.put("comment",ErrorView._107.name());//话题不存在
				
			}
		}else{
			error.put("comment",ErrorView._103.name());//话题Id不能为空
		}
		
		
		//如果实名用户才允许评论
		if(systemSetting.isRealNameUserAllowComment() == true){
			User _user = userService.findUserByUserName(accessUser.getUserName());
			if(_user.isRealNameAuthentication() == false){
				error.put("comment", ErrorView._108.name());//实名用户才允许评论
			}
			
		}
		
		
		
		if(content != null && !"".equals(content.trim())){
			//过滤标签
			content = textFilterManage.filterTag(request,content,settingManage.readEditorTag());
			Object[] object = textFilterManage.filterHtml(request,content,"comment",settingManage.readEditorTag());
			String value = (String)object[0];
			imageNameList = (List<String>)object[1];
			boolean isImage = (Boolean)object[2];//是否含有图片
			//不含标签内容
			String text = textFilterManage.filterText(content);
			//清除空格&nbsp;
			String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
			
			if(isImage == true || (!"".equals(text.trim()) && !"".equals(trimSpace))){
				if(systemSetting.isAllowFilterWord()){
					String wordReplace = "";
					if(systemSetting.getFilterWordReplace() != null){
						wordReplace = systemSetting.getFilterWordReplace();
					}
					value = sensitiveWordFilterManage.filterSensitiveWord(value, wordReplace);
				}
				comment.setContent(value);
				comment.setIsStaff(false);
				comment.setUserName(accessUser.getUserName());
			}else{
				error.put("content",ErrorView._101.name());//内容不能为空
			}
		}else{
			error.put("content",ErrorView._101.name());//内容不能为空
		}

		if(error.size() == 0){
			comment.setIp(IpAddress.getClientIpAddress(request));
			//保存评论
			commentService.saveComment(comment);
			
			
			PointLog pointLog = new PointLog();
			pointLog.setId(pointManage.createPointLogId(accessUser.getUserId()));
			pointLog.setModule(200);//200.评论
			pointLog.setParameterId(comment.getId());//参数Id 
			pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
			pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称
			
			pointLog.setPoint(systemSetting.getComment_rewardPoint());//积分
			pointLog.setUserName(accessUser.getUserName());//用户名称
			pointLog.setRemark("");
			
			//增加用户积分
			userService.addUserPoint(accessUser.getUserName(),systemSetting.getComment_rewardPoint(), pointManage.createPointLogObject(pointLog));
			
			
			String fileNumber = "b"+ accessUser.getUserId();
			
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
			//统计每分钟原来提交次数
			int quantity = settingManage.submitQuantity_add("comment", accessUser.getUserName(), 0);
			//删除每分钟原来提交次数
			settingManage.submitQuantity_delete("comment", accessUser.getUserName());
			//刷新每分钟原来提交次数
			settingManage.submitQuantity_add("comment", accessUser.getUserName(), quantity+1);
			
		}

		Map<String,String> returnError = new HashMap<String,String>();//错误
		if(error.size() >0){
			//将枚举数据转为错误提示字符
    		for (Map.Entry<String,String> entry : error.entrySet()) {		 
    			if(ErrorView.get(entry.getValue()) != null){
    				returnError.put(entry.getKey(),  ErrorView.get(entry.getValue()));
    			}else{
    				returnError.put(entry.getKey(),  entry.getValue());
    			}
			}
		}
		if(isAjax == true){
			
    		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
    		
    		if(error != null && error.size() >0){
    			returnValue.put("success", "false");
    			returnValue.put("error", returnError);
    			if(isCaptcha){
    				returnValue.put("captchaKey", UUIDUtil.getUUID32());
    			}
    			
    		}else{
    			returnValue.put("success", "true");
    		}
    		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			
			
			if(error != null && error.size() >0){//如果有错误
				redirectAttrs.addFlashAttribute("error", returnError);//重定向传参
				redirectAttrs.addFlashAttribute("comment", comment);
				

				String referer = request.getHeader("referer");	
				
				
				referer = StringUtils.removeStartIgnoreCase(referer,Configuration.getUrl(request));//移除开始部分的相同的字符,不区分大小写
				referer = StringUtils.substringBefore(referer, ".");//截取到等于第二个参数的字符串为止
				referer = StringUtils.substringBefore(referer, "?");//截取到等于第二个参数的字符串为止
				
				String queryString = request.getQueryString() != null && !"".equals(request.getQueryString().trim()) ? "?"+request.getQueryString() :"";

				return "redirect:/"+referer+queryString;
	
			}
			
			
			if(jumpUrl != null && !"".equals(jumpUrl.trim())){
				String url = Base64.decodeBase64URL(jumpUrl.trim());
				
				return "redirect:"+url;
			}else{//默认跳转
				model.addAttribute("message", "保存评论成功");
				String referer = request.getHeader("referer");
				if(RefererCompare.compare(request, "login")){//如果是登录页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				model.addAttribute("urlAddress", referer);
				
				String dirName = templateService.findTemplateDir_cache();
				
				
				return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/jump";	
			}
		}
	}
	
	

	/**
	 * 评论  图片上传
	 * @param model
	 * @param topicId 话题Id
	 * @param imgFile
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/uploadImage", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String uploadImage(ModelMap model,Long topicId,
			MultipartFile imgFile, HttpServletRequest request,HttpServletResponse response) throws Exception {

		Map<String,Object> returnJson = new HashMap<String,Object>();
		String errorMessage  = "";
		
		//文件上传
		if(imgFile != null && !imgFile.isEmpty()){
			if(topicId != null && topicId >0L){
				SystemSetting systemSetting = settingService.findSystemSetting_cache();
				
				EditorTag editorSiteObject = settingManage.readEditorTag();
				if(editorSiteObject != null && systemSetting.isAllowComment()){//是否全局允许评论
					if(editorSiteObject.isImage()){//允许上传图片
						//获取登录用户
					  	AccessUser accessUser = AccessUserThreadLocal.get();
						
						//上传文件编号
						String fileNumber = "b"+accessUser.getUserId();
						
						
						//当前图片文件名称
						String fileName = imgFile.getOriginalFilename();
						//当前图片类型
					//	String imgType = file.getContentType();
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
						
						if(authentication ){
							if(size/1024 <= imageSize){
								//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
								String pathDir = "file"+File.separator+"comment"+File.separator + topicId + File.separator;
								//文件锁目录
								String lockPathDir = "file"+File.separator+"comment"+File.separator+"lock"+File.separator;
								//构建文件名称
								String newFileName = UUIDUtil.getUUID32()+fileNumber+ "." + suffix;
								
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
							}else{
								errorMessage = "文件超出允许上传大小";
							}
							
						}else{
							errorMessage = "当前文件类型不允许上传";
						}
					}else{
						errorMessage = "不允许上传图片";
					}
				}else{
					errorMessage = "不允许评论";
				}
			}else{
				errorMessage = "话题Id不能为空";
			}
		}else{
			errorMessage = "文件内容不能为空";
		}
		
		
		//上传失败
		returnJson.put("error", 1);
		returnJson.put("message", errorMessage);
		return JsonUtils.toJSONString(returnJson);
	}
	
	
	
	/**
	 * 引用  添加
	 * @param pageForm
	 * @param model
	 * @param commentId 评论Id
	 */
	@RequestMapping(value="/addQuote", method=RequestMethod.POST)
	public String addQuote(ModelMap model,Long commentId,String content,
			String token,String captchaKey,String captchaValue,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		Map<String,String> error = new HashMap<String,String>();
		
		//判断令牌
		if(token != null && !"".equals(token.trim())){	
			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
				if(!token_sessionid.equals(token)){
					error.put("token", ErrorView._13.name());//令牌错误
				}
			}else{
				error.put("token", ErrorView._12.name());//令牌过期
			}
		}else{
			error.put("token", ErrorView._11.name());//令牌为空
		}
		//验证码
		boolean isCaptcha = captchaManage.comment_isCaptcha(accessUser.getUserName());
		if(isCaptcha){//如果需要验证码
			//验证验证码
			if(captchaKey != null && !"".equals(captchaKey.trim())){
				//增加验证码重试次数
				//统计每分钟原来提交次数
				int quantity = settingManage.submitQuantity_add("captcha", captchaKey.trim(), 0);
				//删除每分钟原来提交次数
				settingManage.submitQuantity_delete("captcha", captchaKey.trim());
				//刷新每分钟原来提交次数
				settingManage.submitQuantity_add("captcha", captchaKey.trim(), quantity+1);
				
				String _captcha = captchaManage.captcha_generate(captchaKey.trim(),"");
				if(captchaValue != null && !"".equals(captchaValue.trim())){
					if(_captcha != null && !"".equals(_captcha.trim())){
						if(!_captcha.equals(captchaValue)){
							error.put("captchaValue",ErrorView._15.name());//验证码错误
						}
					}else{
						error.put("captchaValue",ErrorView._17.name());//验证码过期
					}
				}else{
					error.put("captchaValue",ErrorView._16.name());//请输入验证码
				}
				//删除验证码
				captchaManage.captcha_delete(captchaKey.trim());
			}else{
				error.put("captchaValue", ErrorView._14.name());//验证码参数错误
			}
				
		}
		
		
		Comment comment = null;
		
		if(commentId == null || commentId <=0){
			error.put("comment", ErrorView._14.name());//引用评论不能为空
		}else{
			comment = commentService.findByCommentId(commentId);
		}
		
		
		if(comment != null){
			Topic topic = topicService.findById(comment.getTopicId());
			if(topic != null){
				SystemSetting systemSetting = settingService.findSystemSetting_cache();
				//是否全局允许评论
				if(systemSetting.isAllowComment() == false){
					error.put("comment", ErrorView._106.name());//禁止评论
				}
				if(topic.isAllow() == false){
					error.put("comment", ErrorView._106.name());//禁止评论	
				}
				if(!topic.getStatus().equals(20)){//已发布
					error.put("comment", ErrorView._111.name());//已发布话题才允许评论	
				}
			}else{
				error.put("comment",ErrorView._107.name());//话题不存在
				
			}
			
		}
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		//如果实名用户才允许评论
		if(systemSetting.isRealNameUserAllowComment() == true){
			User _user = userService.findUserByUserName(accessUser.getUserName());
			if(_user.isRealNameAuthentication() == false){
				error.put("comment", ErrorView._108.name());//实名用户才允许评论
			}
			
		}
		
		Comment newComment = new Comment();
		List<String> imageNameList = null;
		
		
		//前台发表评论默认状态
		if(systemSetting.getComment_defaultState() != null){
			if(systemSetting.getComment_defaultState().equals(10)){
				newComment.setStatus(10);
			}else if(systemSetting.getComment_defaultState().equals(20)){
				newComment.setStatus(20);
			}
		}
		
		if(content != null && !"".equals(content.trim())){ 
			if(comment != null){
				//过滤标签
				content = textFilterManage.filterTag(request,content,settingManage.readEditorTag());
				
				Object[] object = textFilterManage.filterHtml(request,content,"comment",settingManage.readEditorTag());
				String value = (String)object[0];
				imageNameList = (List<String>)object[1];
				boolean isImage = (Boolean)object[2];//是否含有图片
				//不含标签内容
				String text = textFilterManage.filterText(content);
				//清除空格&nbsp;
				String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
				
				if(isImage == true || (!"".equals(text.trim()) && !"".equals(trimSpace))){
					
					//旧引用
					List<Quote> old_customQuoteList = new ArrayList<Quote>();
					//旧引用Id组
					String old_customQuoteId = ","+comment.getId()+comment.getQuoteIdGroup();
					if(comment.getQuote() != null && !"".equals(comment.getQuote().trim())){
						//旧引用
						old_customQuoteList = JsonUtils.toGenericObject(comment.getQuote(), new TypeReference< List<Quote> >(){});
					}
					
					
					//自定义引用
					Quote customQuote = new Quote();
					customQuote.setCommentId(comment.getId());
					customQuote.setIsStaff(comment.getIsStaff());
					customQuote.setUserName(comment.getUserName());
					customQuote.setContent(textFilterManage.filterText(comment.getContent()));
					old_customQuoteList.add(customQuote);
					
					//评论
					newComment.setTopicId(comment.getTopicId());
					newComment.setIp(IpAddress.getClientIpAddress(request));
					if(systemSetting.isAllowFilterWord()){
						String wordReplace = "";
						if(systemSetting.getFilterWordReplace() != null){
							wordReplace = systemSetting.getFilterWordReplace();
						}
						value = sensitiveWordFilterManage.filterSensitiveWord(value, wordReplace);
					}
					newComment.setContent(value);
					newComment.setIsStaff(false);
					newComment.setQuoteIdGroup(old_customQuoteId);
					newComment.setUserName(accessUser.getUserName());
					newComment.setQuote(JsonUtils.toJSONString(old_customQuoteList));
					
					

				}else{	
					error.put("content", ErrorView._101.name());//内容不能为空
				}	
			}else{
				error.put("comment", ErrorView._104.name());//引用评论不能为空
			}
		}else{
			error.put("content", ErrorView._101.name());//内容不能为空
		}

		//保存
		if(error.size() ==0){
			//保存评论
			commentService.saveComment(newComment);
			
			PointLog pointLog = new PointLog();
			pointLog.setId(pointManage.createPointLogId(accessUser.getUserId()));
			pointLog.setModule(200);//200.评论
			pointLog.setParameterId(comment.getId());//参数Id 
			pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
			pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称
			
			pointLog.setPoint(systemSetting.getComment_rewardPoint());//积分
			pointLog.setUserName(accessUser.getUserName());//用户名称
			pointLog.setRemark("");
			
			//增加用户积分
			userService.addUserPoint(accessUser.getUserName(),systemSetting.getComment_rewardPoint(), pointManage.createPointLogObject(pointLog));
			String fileNumber = "b"+ accessUser.getUserId();
				
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
			
			//统计每分钟原来提交次数
			int quantity = settingManage.submitQuantity_add("comment", accessUser.getUserName(), 0);
			//删除每分钟原来提交次数
			settingManage.submitQuantity_delete("comment", accessUser.getUserName());
			//刷新每分钟原来提交次数
			settingManage.submitQuantity_add("comment", accessUser.getUserName(), quantity+1);
		}
		
		
		Map<String,String> returnError = new HashMap<String,String>();//错误
		if(error.size() >0){
			//将枚举数据转为错误提示字符
    		for (Map.Entry<String,String> entry : error.entrySet()) {		 
    			if(ErrorView.get(entry.getValue()) != null){
    				returnError.put(entry.getKey(),  ErrorView.get(entry.getValue()));
    			}else{
    				returnError.put(entry.getKey(),  entry.getValue());
    			}
			}
		}
		if(isAjax == true){
    		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
    		
    		if(error != null && error.size() >0){
    			returnValue.put("success", "false");
    			returnValue.put("error", returnError);
    			if(isCaptcha){
    				returnValue.put("captchaKey", UUIDUtil.getUUID32());
    			}
    		}else{
    			returnValue.put("success", "true");
    		}
    		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			if(error != null && error.size() >0){//如果有错误
				redirectAttrs.addFlashAttribute("error", returnError);//重定向传参
				redirectAttrs.addFlashAttribute("comment", newComment);
				

				String referer = request.getHeader("referer");	
				
				
				referer = StringUtils.removeStartIgnoreCase(referer,Configuration.getUrl(request));//移除开始部分的相同的字符,不区分大小写
				referer = StringUtils.substringBefore(referer, ".");//截取到等于第二个参数的字符串为止
				referer = StringUtils.substringBefore(referer, "?");//截取到等于第二个参数的字符串为止
				
				String queryString = request.getQueryString() != null && !"".equals(request.getQueryString().trim()) ? "?"+request.getQueryString() :"";
				return "redirect:/"+referer+queryString;
	
			}
			
			
			if(jumpUrl != null && !"".equals(jumpUrl.trim())){
				String url = Base64.decodeBase64URL(jumpUrl.trim());
				
				return "redirect:"+url;
			}else{//默认跳转
				model.addAttribute("message", "保存评论成功");
				String referer = request.getHeader("referer");
				if(RefererCompare.compare(request, "login")){//如果是登录页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				model.addAttribute("urlAddress", referer);
				String dirName = templateService.findTemplateDir_cache();
				
				return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/jump";	
			}
		}
	 }
	

	/**
	 * 回复  添加
	 * @param model
	 * @param commentId 评论Id
	 * @param content
	 * @param token
	 * @param captchaKey
	 * @param captchaValue
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/addReply", method=RequestMethod.POST)
	public String addReply(ModelMap model,Long commentId,String content,
			String token,String captchaKey,String captchaValue,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		//获取登录用户
		AccessUser accessUser = AccessUserThreadLocal.get();
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		Map<String,String> error = new HashMap<String,String>();
		
		//判断令牌
		if(token != null && !"".equals(token.trim())){	
			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
				if(!token_sessionid.equals(token)){
					error.put("token", ErrorView._13.name());//令牌错误
				}
			}else{
				error.put("token", ErrorView._12.name());//令牌过期
			}
		}else{
			error.put("token", ErrorView._11.name());//令牌为空
		}
		//验证码
		boolean isCaptcha = captchaManage.comment_isCaptcha(accessUser.getUserName());
		if(isCaptcha){//如果需要验证码
			//验证验证码
			if(captchaKey != null && !"".equals(captchaKey.trim())){
				//增加验证码重试次数
				//统计每分钟原来提交次数
				int quantity = settingManage.submitQuantity_add("captcha", captchaKey.trim(), 0);
				//删除每分钟原来提交次数
				settingManage.submitQuantity_delete("captcha", captchaKey.trim());
				//刷新每分钟原来提交次数
				settingManage.submitQuantity_add("captcha", captchaKey.trim(), quantity+1);
				
				String _captcha = captchaManage.captcha_generate(captchaKey.trim(),"");
				if(captchaValue != null && !"".equals(captchaValue.trim())){
					if(_captcha != null && !"".equals(_captcha.trim())){
						if(!_captcha.equals(captchaValue)){
							error.put("captchaValue",ErrorView._15.name());//验证码错误
						}
					}else{
						error.put("captchaValue",ErrorView._17.name());//验证码过期
					}
				}else{
					error.put("captchaValue",ErrorView._16.name());//请输入验证码
				}
				//删除验证码
				captchaManage.captcha_delete(captchaKey.trim());	
			}else{
				error.put("captchaValue", ErrorView._14.name());//验证码参数错误
			}
			
		}
		
		Comment comment = null;
		
		if(commentId == null || commentId <=0){
			error.put("reply", ErrorView._105.name());//评论不存在
		}else{
			comment = commentService.findByCommentId(commentId);
			
			if(comment != null){
				
				Topic topic = topicService.findById(comment.getTopicId());
				if(topic != null){
					SystemSetting systemSetting = settingService.findSystemSetting_cache();
					//是否全局允许评论
					if(systemSetting.isAllowComment() == false){
						error.put("reply", ErrorView._106.name());//禁止评论
					}
					if(topic.isAllow() == false){
						error.put("reply", ErrorView._106.name());//禁止评论	
					}
					if(!topic.getStatus().equals(20)){//已发布
						error.put("reply", ErrorView._111.name());//已发布话题才允许回复
					}
				}else{
					error.put("reply",ErrorView._107.name());//话题不存在
					
				}
			}else{
				error.put("reply", ErrorView._105.name());//评论不存在
			}
			
			
		}
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		//如果实名用户才允许评论
		if(systemSetting.isRealNameUserAllowComment() == true){
			User _user = userService.findUserByUserName(accessUser.getUserName());
			if(_user.isRealNameAuthentication() == false){
				error.put("reply", ErrorView._108.name());//实名用户才允许评论
			}
			
		}

		//回复
		Reply reply = new Reply();
		
		//前台发表回复默认状态
		if(systemSetting.getReply_defaultState() != null){
			if(systemSetting.getReply_defaultState().equals(10)){
				reply.setStatus(10);
			}else if(systemSetting.getReply_defaultState().equals(20)){
				reply.setStatus(20);
			}
		}
		
		
		if(error != null && error.size() ==0){
			if(content != null && !"".equals(content.trim())){
				if(comment != null){
					//不含标签内容
					String text = textFilterManage.filterText(content);
					//清除空格&nbsp;
					String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
					
					if((!"".equals(text.trim()) && !"".equals(trimSpace))){
						
						if(systemSetting.isAllowFilterWord()){
							String wordReplace = "";
							if(systemSetting.getFilterWordReplace() != null){
								wordReplace = systemSetting.getFilterWordReplace();
							}
							text = sensitiveWordFilterManage.filterSensitiveWord(text, wordReplace);
						}
						
						reply.setCommentId(comment.getId());
						reply.setIsStaff(false);
						reply.setUserName(accessUser.getUserName());
						reply.setIp(IpAddress.getClientIpAddress(request));
						reply.setContent(text);
						reply.setTopicId(comment.getTopicId());
						
						

					}else{	
						error.put("content", ErrorView._101.name());//内容不能为空
						
					}	
				}else{
					error.put("reply", ErrorView._105.name());//评论不存在
				}
			}else{
				error.put("content", ErrorView._101.name());//内容不能为空
			}
			
		}
		//保存
		if(error.size() ==0){
			//保存回复
			commentService.saveReply(reply);
			
			PointLog pointLog = new PointLog();
			pointLog.setId(pointManage.createPointLogId(accessUser.getUserId()));
			pointLog.setModule(300);//300.回复
			pointLog.setParameterId(comment.getId());//参数Id 
			pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
			pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称
			
			pointLog.setPoint(systemSetting.getReply_rewardPoint());//积分
			pointLog.setUserName(accessUser.getUserName());//用户名称
			pointLog.setRemark("");
			
			//增加用户积分
			userService.addUserPoint(accessUser.getUserName(),systemSetting.getReply_rewardPoint(), pointManage.createPointLogObject(pointLog));
			
			//统计每分钟原来提交次数
			int quantity = settingManage.submitQuantity_add("comment", accessUser.getUserName(), 0);
			//删除每分钟原来提交次数
			settingManage.submitQuantity_delete("comment", accessUser.getUserName());
			//刷新每分钟原来提交次数
			settingManage.submitQuantity_add("comment", accessUser.getUserName(), quantity+1);
		}
		
		
		Map<String,String> returnError = new HashMap<String,String>();//错误
		if(error.size() >0){
			//将枚举数据转为错误提示字符
    		for (Map.Entry<String,String> entry : error.entrySet()) {		 
    			if(ErrorView.get(entry.getValue()) != null){
    				returnError.put(entry.getKey(),  ErrorView.get(entry.getValue()));
    			}else{
    				returnError.put(entry.getKey(),  entry.getValue());
    			}
			}
		}
		if(isAjax == true){
    		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
    		
    		if(error != null && error.size() >0){
    			returnValue.put("success", "false");
    			returnValue.put("error", returnError);
    			if(isCaptcha){
    				returnValue.put("captchaKey", UUIDUtil.getUUID32());
    			}
    		}else{
    			returnValue.put("success", "true");
    		}
    		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			if(error != null && error.size() >0){//如果有错误
				redirectAttrs.addFlashAttribute("error", returnError);//重定向传参
				redirectAttrs.addFlashAttribute("reply", reply);
				

				String referer = request.getHeader("referer");	
				
				
				referer = StringUtils.removeStartIgnoreCase(referer,Configuration.getUrl(request));//移除开始部分的相同的字符,不区分大小写
				referer = StringUtils.substringBefore(referer, ".");//截取到等于第二个参数的字符串为止
				referer = StringUtils.substringBefore(referer, "?");//截取到等于第二个参数的字符串为止
				
				String queryString = request.getQueryString() != null && !"".equals(request.getQueryString().trim()) ? "?"+request.getQueryString() :"";
				return "redirect:/"+referer+queryString;
	
			}
			
			
			if(jumpUrl != null && !"".equals(jumpUrl.trim())){
				String url = Base64.decodeBase64URL(jumpUrl.trim());
				
				return "redirect:"+url;
			}else{//默认跳转
				model.addAttribute("message", "保存回复成功");
				String referer = request.getHeader("referer");
				if(RefererCompare.compare(request, "login")){//如果是登录页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				model.addAttribute("urlAddress", referer);
				String dirName = templateService.findTemplateDir_cache();
				
				return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/jump";	
			}
		}
	}
	
}
