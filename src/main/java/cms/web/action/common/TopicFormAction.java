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
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import cms.bean.ErrorView;
import cms.bean.setting.SystemSetting;
import cms.bean.thumbnail.Thumbnail;
import cms.bean.topic.ImageInfo;
import cms.bean.topic.Tag;
import cms.bean.topic.Topic;
import cms.bean.topic.TopicIndex;
import cms.bean.user.AccessUser;
import cms.bean.user.PointLog;
import cms.bean.user.User;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.service.thumbnail.ThumbnailService;
import cms.service.topic.CommentService;
import cms.service.topic.TagService;
import cms.service.topic.TopicIndexService;
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
import cms.web.action.thumbnail.ThumbnailManage;
import cms.web.action.topic.TopicManage;
import cms.web.action.user.PointManage;
import cms.web.action.user.UserManage;
import cms.web.taglib.Configuration;

/**
 * 话题接收表单
 *
 */
@Controller
@RequestMapping("user/control/topic") 
public class TopicFormAction {
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
	@Resource TagService tagService;
	@Resource PointManage pointManage;
	
	@Resource TopicIndexService topicIndexService;

	@Resource CSRFTokenManage csrfTokenManage;
	@Resource TopicManage topicManage;
	@Resource SensitiveWordFilterManage sensitiveWordFilterManage;
	@Resource ThumbnailService thumbnailService;
	@Resource ThumbnailManage thumbnailManage;
	@Resource UserManage userManage;
	
	/**
	 * 话题  添加
	 * @param model
	 * @param tagId 标签Id
	 * @param tagName 标签名称
	 * @param title 标题
	 * @param content 内容
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(ModelMap model,Long tagId,String title,String content,
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
		boolean isCaptcha = captchaManage.topic_isCaptcha(accessUser.getUserName());
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
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		
		//如果全局不允许提交话题
		if(systemSetting.isAllowTopic() == false){
			error.put("topic", ErrorView._110.name());//不允许提交话题
		}
		
		//如果实名用户才允许提交话题
		if(systemSetting.isRealNameUserAllowTopic() == true){
			User _user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
			if(_user.isRealNameAuthentication() == false){
				error.put("topic", ErrorView._109.name());//实名用户才允许提交话题
			}
			
		}
		
		
		//前3张图片地址
		List<ImageInfo> beforeImageList = new ArrayList<ImageInfo>();
				
		
		Topic topic = new Topic();
		List<String> imageNameList = null;
		boolean isImage = false;//是否含有图片
		List<String> flashNameList = null;
		boolean isFlash = false;//是否含有Flash
		List<String> mediaNameList = null;
		boolean isMedia = false;//是否含有音视频
		List<String> fileNameList = null;
		boolean isFile = false;//是否含有文件
		boolean isMap = false;//是否含有地图
		
		//前台发表话题默认状态
		if(systemSetting.getTopic_defaultState() != null){
			if(systemSetting.getTopic_defaultState().equals(10)){
				topic.setStatus(10);
			}else if(systemSetting.getTopic_defaultState().equals(20)){
				topic.setStatus(20);
			}
		}
		
		if(tagId == null || tagId <=0L){
			error.put("tagId", "标签不能为空");
		}else{
			List<Tag> tagList = tagService.findAllTag_cache();
			if(tagList != null && tagList.size() >0){
				for(Tag tag : tagList){
					if(tag.getId().equals(tagId)){
						topic.setTagId(tag.getId());
						topic.setTagName(tag.getName());
						break;
					}
					
				}
				if(topic.getTagId() == null){
					error.put("tagId", "标签不存在");
				}
			}
		}
		if(title != null && !"".equals(title.trim())){
			if(systemSetting.isAllowFilterWord()){
				String wordReplace = "";
				if(systemSetting.getFilterWordReplace() != null){
					wordReplace = systemSetting.getFilterWordReplace();
				}
				title = sensitiveWordFilterManage.filterSensitiveWord(title, wordReplace);
			}
			
			topic.setTitle(title);
			if(title.length() >150){
				error.put("title", "不能大于150个字符");
			}
		}else{
			error.put("title", "标题不能为空");
		}
		
		
		if(content != null && !"".equals(content.trim())){
			
		//	content = "<div style=\"text-align: center; height: expression(alert('test &ss')); while: expression(alert('test xss'));\">fdfd</div>";
		//	content = "<img src=\"java script:alert(/XSS/)\" width = 100>";
		//	content = "<div style=\"background-image: url(javasc \n\t ript:alert('XSS'))\">";
		//	content = "<img src=\"java\nscript:alert(/XSS/)\" width = 100>";
			
		//	content = "<div style=\"background-color: #123456;color: expr\65ssion(alert('testss'));\"><a href=\"javascript:alert('XSS');\"><a href=\"http://127.0.0.1:8080/cms/javascript:alert('XSS');\"><a href=\"aaa/ggh.htm\">";
			
		//	content = "<div style=\"background-image: url(javascript:alert('XSS'));background-image: url(javascript&colon;alert('XSSW'));width: expression(alert&#40;'testxss'));\">";
		//	content = "<div style=\"background-image: url(javasc\n\tript:alert('XSS'))\">";
			
		//	content = "<div style=\"color:express/**/ion(eval('\\x69\\x66\\x28\\x77\\x69\\x6e\\x64\\x6f\\x77\\x2e\\x61\\x21\\x3d\\x31\\x29\\x7b\\x61\\x6c\\x65\\x72\\x74\\x28\\x2f\\x73\\x74\\x79\\x6c\\x65\\x5f\\x38\\x2f\\x29\\x3b\\x77\\x69\\x6e\\x64\\x6f\\x77\\x2e\\x61\\x3d\\x31\\x3b\\x7d'))\">";
			
			
			//过滤标签
			content = textFilterManage.filterTag(request,content);
			Object[] object = textFilterManage.filterHtml(request,content,"topic",settingManage.readEditorTag());
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
			
			
			//不含标签内容
			String text = textFilterManage.filterText(content);
			//清除空格&nbsp;
			String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
			//摘要
			if(trimSpace != null && !"".equals(trimSpace)){
				if(trimSpace.length() >150){
					topic.setSummary(trimSpace.substring(0, 150)+"..");
				}else{
					topic.setSummary(trimSpace+"..");
				}
			}
			if(isImage == true || isFlash == true || isMedia == true || isFile==true ||isMap== true || (!"".equals(text.trim()) && !"".equals(trimSpace))){
				if(systemSetting.isAllowFilterWord()){
					String wordReplace = "";
					if(systemSetting.getFilterWordReplace() != null){
						wordReplace = systemSetting.getFilterWordReplace();
					}
					value = sensitiveWordFilterManage.filterSensitiveWord(value, wordReplace);
				}
				
				
				
				
				topic.setIp(IpAddress.getClientIpAddress(request));
				topic.setUserName(accessUser.getUserName());
				topic.setIsStaff(false);
				topic.setContent(value);
			}else{
				error.put("content", "话题内容不能为空");
			}	
			
			if(imageNameList != null && imageNameList.size() >0){
				for(int i=0; i<imageNameList.size(); i++){
					ImageInfo imageInfo = new ImageInfo();
					imageInfo.setName(fileManage.getName(imageNameList.get(i)));
					imageInfo.setPath(fileManage.getFullPath("file/topic/"+imageNameList.get(i)));
					
					beforeImageList.add(imageInfo);
					if(i ==2){//只添加3张图片
						break;
					}
				}
				topic.setImage(JsonUtils.toJSONString(beforeImageList));
				
			}
		}else{
			error.put("content", "话题内容不能为空");
		}
			
		if(error.size() == 0){
			//保存评论
			topicService.saveTopic(topic);
			//更新索引
			topicIndexService.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),1));
			
			PointLog pointLog = new PointLog();
			pointLog.setId(pointManage.createPointLogId(accessUser.getUserId()));
			pointLog.setModule(100);//100.话题
			pointLog.setParameterId(topic.getId());//参数Id 
			pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
			pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称
			
			pointLog.setPoint(systemSetting.getTopic_rewardPoint());//积分
			pointLog.setUserName(accessUser.getUserName());//用户名称
			pointLog.setRemark("");
			
			//增加用户积分
			userService.addUserPoint(accessUser.getUserName(),systemSetting.getTopic_rewardPoint(), pointManage.createPointLogObject(pointLog));
			//删除缓存
			userManage.delete_cache_findUserById(accessUser.getUserId());
			userManage.delete_cache_findUserByUserName(accessUser.getUserName());
			
			String fileNumber = "b"+ accessUser.getUserId();
			
			//删除图片锁
			if(imageNameList != null && imageNameList.size() >0){
				for(String imageName :imageNameList){
			
					 if(imageName != null && !"".equals(imageName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						 if(!topicManage.getFileNumber(fileManage.getBaseName(imageName.trim())).equals(fileNumber)){
							 continue;
						 }
						 
						 
						 fileManage.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
					
					 }
				}
			}
			//falsh
			if(flashNameList != null && flashNameList.size() >0){
				for(String flashName :flashNameList){
					 if(flashName != null && !"".equals(flashName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						if(!topicManage.getFileNumber(fileManage.getBaseName(flashName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,flashName.replaceAll("/","_"));
					
					 }
				}
			}
			//音视频
			if(mediaNameList != null && mediaNameList.size() >0){
				for(String mediaName :mediaNameList){
					if(mediaName != null && !"".equals(mediaName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						if(!topicManage.getFileNumber(fileManage.getBaseName(mediaName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));
					
					}
				}
			}
			//文件
			if(fileNameList != null && fileNameList.size() >0){
				for(String fileName :fileNameList){
					if(fileName != null && !"".equals(fileName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						if(!topicManage.getFileNumber(fileManage.getBaseName(fileName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
					
					}
				}
			}
			
			List<Thumbnail> thumbnailList = thumbnailService.findAllThumbnail_cache();
			if(thumbnailList != null && thumbnailList.size() >0){
				//异步增加缩略图
				if(beforeImageList != null && beforeImageList.size() >0){
					thumbnailManage.addThumbnail(thumbnailList,beforeImageList);
				}
			}
			
			
			//统计每分钟原来提交次数
			int quantity = settingManage.submitQuantity_add("topic", accessUser.getUserName(), 0);
			//删除每分钟原来提交次数
			settingManage.submitQuantity_delete("topic", accessUser.getUserName());
			//刷新每分钟原来提交次数
			settingManage.submitQuantity_add("topic", accessUser.getUserName(), quantity+1);
			
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
				redirectAttrs.addFlashAttribute("topic", topic);
				

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
				model.addAttribute("message", "保存话题成功");
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
	 * 文件上传
	 * dir: 上传类型，分别为image、flash、media、file 
	 * 
	 * 员工发话题 上传文件名为UUID + a + 员工Id
	 * 用户发话题 上传文件名为UUID + b + 用户Id
	 */
	@RequestMapping(value="/upload", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String upload(ModelMap model,
			MultipartFile imgFile,HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		
		
		Map<String,Object> returnJson = new HashMap<String,Object>();
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		String errorMessage  = "";
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		boolean flag = true;
		
		//如果全局不允许提交话题
		if(systemSetting.isAllowTopic() == false){
			flag = false;
		}
		
		//如果实名用户才允许提交话题
		if(systemSetting.isRealNameUserAllowTopic() == true){
			User _user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
			if(_user.isRealNameAuthentication() == false){
				flag = false;
			}
		}
		if(flag){
			DateTime dateTime = new DateTime();     
			String date = dateTime.toString("yyyy-MM-dd");
			
			if(imgFile != null && !imgFile.isEmpty()){
				//上传文件编号
				String fileNumber = "b"+accessUser.getUserId();
				
				//当前文件名称
				String fileName = imgFile.getOriginalFilename();
				
				//文件大小
				Long size = imgFile.getSize();
				//取得文件后缀
				String suffix = fileManage.getExtension(fileName).toLowerCase();

				
				//允许上传图片格式
				List<String> formatList = new ArrayList<String>();
				formatList.add("gif");
				formatList.add("jpg");
				formatList.add("jpeg");
				formatList.add("bmp");
				formatList.add("png");
				
				//允许上传图片大小
				long imageSize = 200000L;

				//验证文件类型
				boolean authentication = fileManage.validateFileSuffix(imgFile.getOriginalFilename(),formatList);
				
				if(authentication ){
					if(size/1024 <= imageSize){
						//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
						String pathDir = "file"+File.separator+"topic"+File.separator + date +File.separator +"image"+ File.separator;
						//文件锁目录
						String lockPathDir = "file"+File.separator+"topic"+File.separator+"lock"+File.separator;
						//构建文件名称
						String newFileName = UUIDUtil.getUUID32()+ fileNumber+"." + suffix;
						
						//生成文件保存目录
						fileManage.createFolder(pathDir);
						//生成锁文件保存目录
						fileManage.createFolder(lockPathDir);
						//生成锁文件
						fileManage.newFile(lockPathDir+date +"_image_"+newFileName);
						//保存文件
						fileManage.writeFile(pathDir, newFileName,imgFile.getBytes());
						//上传成功
						returnJson.put("error", 0);//0成功  1错误
						returnJson.put("url", "file/topic/"+date+"/image/"+newFileName);
						return JsonUtils.toJSONString(returnJson);
					}else{
						errorMessage = "文件超出允许上传大小";
					}
				}else{
					errorMessage = "当前文件类型不允许上传";
				}
			}else{
				errorMessage = "文件内容不能为空";
			}
		}else{
			errorMessage = "不允许发表话题";
		}
		
		//上传失败
		returnJson.put("error", 1);
		returnJson.put("message", errorMessage);
		return JsonUtils.toJSONString(returnJson);
	}
	
	
	
	
	
}
