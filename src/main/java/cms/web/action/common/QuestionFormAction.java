package cms.web.action.common;



import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
import cms.bean.question.Answer;
import cms.bean.question.AppendQuestionItem;
import cms.bean.question.Question;
import cms.bean.question.QuestionIndex;
import cms.bean.question.QuestionTag;
import cms.bean.question.QuestionTagAssociation;
import cms.bean.setting.EditorTag;
import cms.bean.setting.SystemSetting;
import cms.bean.user.AccessUser;
import cms.bean.user.PointLog;
import cms.bean.user.ResourceEnum;
import cms.bean.user.User;
import cms.bean.user.UserDynamic;
import cms.service.question.AnswerService;
import cms.service.question.QuestionIndexService;
import cms.service.question.QuestionService;
import cms.service.question.QuestionTagService;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
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
import cms.web.action.question.AnswerManage;
import cms.web.action.question.QuestionManage;
import cms.web.action.setting.SettingManage;
import cms.web.action.user.PointManage;
import cms.web.action.user.UserDynamicManage;
import cms.web.action.user.UserManage;
import cms.web.action.user.UserRoleManage;
import cms.web.taglib.Configuration;

/**
 * 问题接收表单
 *
 */
@Controller
@RequestMapping("user/control/question") 
public class QuestionFormAction {
	@Resource TemplateService templateService;
	
	@Resource CaptchaManage captchaManage;
	@Resource FileManage fileManage;
	@Resource QuestionTagService questionTagService;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource TextFilterManage textFilterManage;
	@Resource SettingManage settingManage;
	@Resource SettingService settingService;
	@Resource UserService userService;
	@Resource CSRFTokenManage csrfTokenManage;
	@Resource SensitiveWordFilterManage sensitiveWordFilterManage;
	@Resource UserManage userManage;
	@Resource UserDynamicManage userDynamicManage;
	@Resource UserRoleManage userRoleManage;
	@Resource QuestionManage questionManage;
	@Resource QuestionService questionService;
	@Resource QuestionIndexService questionIndexService;
	@Resource PointManage pointManage;
	@Resource AnswerManage answerManage;
	@Resource AnswerService answerService;
	
	/**
	 * 问题  添加
	 * @param model
	 * @param tagId 标签Id
	 * @param title 标题
	 * @param content 内容
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(ModelMap model,Long[] tagId,String title,String content,
			String token,String captchaKey,String captchaValue,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		
		//是否有当前功能操作权限
		boolean permission = userRoleManage.isPermission(ResourceEnum._10002000,null);
		if(permission == false){
			if(isAjax == true){
				response.setStatus(403);//设置状态码
	    		
				WebUtil.writeToWeb("", "json", response);
				return null;
			}else{ 
				String dirName = templateService.findTemplateDir_cache();
					
				String accessPath = accessSourceDeviceManage.accessDevices(request);	
				request.setAttribute("message","权限不足"); 	
				return "templates/"+dirName+"/"+accessPath+"/message";
			}
		}
		
		
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
		boolean isCaptcha = captchaManage.question_isCaptcha(accessUser.getUserName());
		if(isCaptcha){//如果需要验证码
			//验证验证码
			if(captchaKey != null && !"".equals(captchaKey.trim())){
				//增加验证码重试次数
				//统计每分钟原来提交次数
				Integer original = settingManage.getSubmitQuantity("captcha", captchaKey.trim());
	    		if(original != null){
	    			settingManage.addSubmitQuantity("captcha", captchaKey.trim(),original+1);//刷新每分钟原来提交次数
	    		}else{
	    			settingManage.addSubmitQuantity("captcha", captchaKey.trim(),1);//刷新每分钟原来提交次数
	    		}
				
				
				String _captcha = captchaManage.captcha_generate(captchaKey.trim(),"");
				if(captchaValue != null && !"".equals(captchaValue.trim())){
					if(_captcha != null && !"".equals(_captcha.trim())){
						if(!_captcha.equalsIgnoreCase(captchaValue)){
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
		
		
		//如果全局不允许提交问题
		if(systemSetting.isAllowQuestion() == false){
			error.put("question", ErrorView._210.name());//不允许提交问题
		}
		
		//如果实名用户才允许提交问题
		if(systemSetting.isRealNameUserAllowQuestion() == true){
			User _user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
			if(_user.isRealNameAuthentication() == false){
				error.put("question", ErrorView._209.name());//实名用户才允许提交问题
			}
			
		}
	
		
		Question question = new Question();
		Date d = new Date();
		question.setPostTime(d);
		question.setLastAnswerTime(d);
		
		
		List<String> imageNameList = null;
		boolean isImage = false;//是否含有图片
		List<String> flashNameList = null;
		boolean isFlash = false;//是否含有Flash
		List<String> mediaNameList = null;
		boolean isMedia = false;//是否含有音视频
		List<String> fileNameList = null;
		boolean isFile = false;//是否含有文件
		boolean isMap = false;//是否含有地图
		
		
		//前台发表话题审核状态
		if(systemSetting.getQuestion_review().equals(10)){//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
			question.setStatus(10);//10.待审核 	
		}else if(systemSetting.getQuestion_review().equals(30)){
			//是否有当前功能操作权限
			boolean flag_permission = userRoleManage.isPermission(ResourceEnum._10006000,null);
			if(flag_permission){
				question.setStatus(20);//20.已发布
			}else{
				question.setStatus(10);//10.待审核 
			}
		}else{
			question.setStatus(20);//20.已发布
		}
		
		
		
		
		LinkedHashSet <QuestionTagAssociation> questionTagAssociationList = new LinkedHashSet<QuestionTagAssociation>();
		if(tagId != null && tagId.length >0){
			if(tagId.length > systemSetting.getMaxQuestionTagQuantity()){
				error.put("tagId", "标签不能超过"+systemSetting.getMaxQuestionTagQuantity()+"个");
			}
			
			List<QuestionTag> questionTagList =  questionTagService.findAllQuestionTag_cache();
			for(Long id :tagId){
				if(id != null && id >0L){
					QuestionTagAssociation questionTagAssociation = new QuestionTagAssociation();
					questionTagAssociation.setQuestionTagId(id);
					for(QuestionTag questionTag : questionTagList){
						if(questionTag.getId().equals(id) && questionTag.getChildNodeNumber().equals(0)){
							questionTagAssociation.setQuestionTagName(questionTag.getName());
							questionTagAssociation.setUserName(accessUser.getUserName());
							questionTagAssociationList.add(questionTagAssociation);
							break;
						}
						
					}	
				}
			}
			question.setQuestionTagAssociationList(new ArrayList<QuestionTagAssociation>(questionTagAssociationList));
		}else{
			error.put("tagId", "标签不能为空");
		}
		
		if(title != null && !"".equals(title.trim())){
			if(systemSetting.isAllowFilterWord()){
				String wordReplace = "";
				if(systemSetting.getFilterWordReplace() != null){
					wordReplace = systemSetting.getFilterWordReplace();
				}
				title = sensitiveWordFilterManage.filterSensitiveWord(title, wordReplace);
			}
			
			question.setTitle(title);
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
			
			EditorTag editorTag = settingManage.readQuestionEditorTag();
			//过滤标签
			content = textFilterManage.filterTag(request,content,editorTag);
			Object[] object = textFilterManage.filterHtml(request,content,"question",editorTag);
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
			String text = textFilterManage.filterText(value);
			//清除空格&nbsp;
			String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();
			//摘要
			if(trimSpace != null && !"".equals(trimSpace)){
				if(systemSetting.isAllowFilterWord()){
					String wordReplace = "";
					if(systemSetting.getFilterWordReplace() != null){
						wordReplace = systemSetting.getFilterWordReplace();
					}
					trimSpace = sensitiveWordFilterManage.filterSensitiveWord(trimSpace, wordReplace);
				}
				if(trimSpace.length() >180){
					question.setSummary(trimSpace.substring(0, 180)+"..");
				}else{
					question.setSummary(trimSpace+"..");
				}
			}
			
			//不含标签内容
			String source_text = textFilterManage.filterText(content);
			//清除空格&nbsp;
			String source_trimSpace = cms.utils.StringUtil.replaceSpace(source_text).trim();
			
			if(isImage == true || isFlash == true || isMedia == true || isFile==true ||isMap== true || (!"".equals(source_text.trim()) && !"".equals(source_trimSpace))){
				if(systemSetting.isAllowFilterWord()){
					String wordReplace = "";
					if(systemSetting.getFilterWordReplace() != null){
						wordReplace = systemSetting.getFilterWordReplace();
					}
					value = sensitiveWordFilterManage.filterSensitiveWord(value, wordReplace);
				}
				
				
				
				
				question.setIp(IpAddress.getClientIpAddress(request));
				question.setUserName(accessUser.getUserName());
				question.setIsStaff(false);
				question.setContent(value);
			}else{
				error.put("content", "问题内容不能为空");
			}	

		}else{
			error.put("content", "问题内容不能为空");
		}
		
		
		
		if(error.size() == 0){
			
			//保存问题
			questionService.saveQuestion(question,new ArrayList<QuestionTagAssociation>(questionTagAssociationList));
			//更新索引
			questionIndexService.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),1));
			
			PointLog pointLog = new PointLog();
			pointLog.setId(pointManage.createPointLogId(accessUser.getUserId()));
			pointLog.setModule(700);//700.问题
			pointLog.setParameterId(question.getId());//参数Id 
			pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
			pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称
			
			pointLog.setPoint(systemSetting.getQuestion_rewardPoint());//积分
			pointLog.setUserName(accessUser.getUserName());//用户名称
			pointLog.setRemark("");
			
			//增加用户积分
			userService.addUserPoint(accessUser.getUserName(),systemSetting.getQuestion_rewardPoint(), pointManage.createPointLogObject(pointLog));
			
			
			//用户动态
			UserDynamic userDynamic = new UserDynamic();
			userDynamic.setId(userDynamicManage.createUserDynamicId(accessUser.getUserId()));
			userDynamic.setUserName(accessUser.getUserName());
			userDynamic.setModule(500);//模块 500.问题
			userDynamic.setQuestionId(question.getId());
			userDynamic.setPostTime(question.getPostTime());
			userDynamic.setStatus(question.getStatus());
			userDynamic.setFunctionIdGroup(","+question.getId()+",");
			Object new_userDynamic = userDynamicManage.createUserDynamicObject(userDynamic);
			userService.saveUserDynamic(new_userDynamic);
		
			
			//删除缓存
			userManage.delete_cache_findUserById(accessUser.getUserId());
			userManage.delete_cache_findUserByUserName(accessUser.getUserName());
			
			String fileNumber = "b"+ accessUser.getUserId();
			
			//删除图片锁
			if(imageNameList != null && imageNameList.size() >0){
				for(String imageName :imageNameList){
			
					 if(imageName != null && !"".equals(imageName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						 if(!questionManage.getFileNumber(fileManage.getBaseName(imageName.trim())).equals(fileNumber)){
							 continue;
						 }
						 
						 
						 fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
					
					 }
				}
			}
			//falsh
			if(flashNameList != null && flashNameList.size() >0){
				for(String flashName :flashNameList){
					 if(flashName != null && !"".equals(flashName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						if(!questionManage.getFileNumber(fileManage.getBaseName(flashName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,flashName.replaceAll("/","_"));
					
					 }
				}
			}
			//音视频
			if(mediaNameList != null && mediaNameList.size() >0){
				for(String mediaName :mediaNameList){
					if(mediaName != null && !"".equals(mediaName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						if(!questionManage.getFileNumber(fileManage.getBaseName(mediaName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));
					
					}
				}
			}
			//文件
			if(fileNameList != null && fileNameList.size() >0){
				for(String fileName :fileNameList){
					if(fileName != null && !"".equals(fileName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						if(!questionManage.getFileNumber(fileManage.getBaseName(fileName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
					
					}
				}
			}
			
			
			
			//统计每分钟原来提交次数
			Integer original = settingManage.getSubmitQuantity("question", accessUser.getUserName());
    		if(original != null){
    			settingManage.addSubmitQuantity("question", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
    		}else{
    			settingManage.addSubmitQuantity("question", accessUser.getUserName(),1);//刷新每分钟原来提交次数
    		}

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
				redirectAttrs.addFlashAttribute("question", question);
				

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
				model.addAttribute("message", "保存问题成功");
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
	 * 问题  追加
	 * @param model
	 * @param questionId 问题Id
	 * @param content 内容
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/appendQuestion", method=RequestMethod.POST)
	public String appendQuestion(ModelMap model,Long questionId,String content,
			String token,String captchaKey,String captchaValue,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		
		//是否有当前功能操作权限
		boolean permission = userRoleManage.isPermission(ResourceEnum._10003000,null);
		if(permission == false){
			if(isAjax == true){
				response.setStatus(403);//设置状态码
	    		
				WebUtil.writeToWeb("", "json", response);
				return null;
			}else{ 
				String dirName = templateService.findTemplateDir_cache();
					
				String accessPath = accessSourceDeviceManage.accessDevices(request);	
				request.setAttribute("message","权限不足"); 	
				return "templates/"+dirName+"/"+accessPath+"/message";
			}
		}
		
		
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
		boolean isCaptcha = captchaManage.question_isCaptcha(accessUser.getUserName());
		if(isCaptcha){//如果需要验证码
			//验证验证码
			if(captchaKey != null && !"".equals(captchaKey.trim())){
				//增加验证码重试次数
				//统计每分钟原来提交次数
				Integer original = settingManage.getSubmitQuantity("captcha", captchaKey.trim());
	    		if(original != null){
	    			settingManage.addSubmitQuantity("captcha", captchaKey.trim(),original+1);//刷新每分钟原来提交次数
	    		}else{
	    			settingManage.addSubmitQuantity("captcha", captchaKey.trim(),1);//刷新每分钟原来提交次数
	    		}
				
				
				String _captcha = captchaManage.captcha_generate(captchaKey.trim(),"");
				if(captchaValue != null && !"".equals(captchaValue.trim())){
					if(_captcha != null && !"".equals(_captcha.trim())){
						if(!_captcha.equalsIgnoreCase(captchaValue)){
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
		
		
		//如果全局不允许提交问题
		if(systemSetting.isAllowQuestion() == false){
			error.put("question", ErrorView._210.name());//不允许提交问题
		}
		
		//如果实名用户才允许提交问题
		if(systemSetting.isRealNameUserAllowQuestion() == true){
			User _user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
			if(_user.isRealNameAuthentication() == false){
				error.put("question", ErrorView._209.name());//实名用户才允许提交问题
			}
			
		}
		
		
		
		
		
		List<String> imageNameList = null;
		boolean isImage = false;//是否含有图片
		List<String> flashNameList = null;
		boolean isFlash = false;//是否含有Flash
		List<String> mediaNameList = null;
		boolean isMedia = false;//是否含有音视频
		List<String> fileNameList = null;
		boolean isFile = false;//是否含有文件
		boolean isMap = false;//是否含有地图
		
		String appendContent = "";
		Question question = null;
		if(questionId != null && questionId >0L){
			question = questionService.findById(questionId);
			if(question != null){
				
				if(!question.getUserName().equals(accessUser.getUserName())){
					error.put("question", ErrorView._219.name());//不是提交该问题的用户不允许追加提问
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
						
						EditorTag editorTag = settingManage.readQuestionEditorTag();
						//过滤标签
						content = textFilterManage.filterTag(request,content,editorTag);
						Object[] object = textFilterManage.filterHtml(request,content,"question",editorTag);
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
						String source_text = textFilterManage.filterText(content);
						//清除空格&nbsp;
						String source_trimSpace = cms.utils.StringUtil.replaceSpace(source_text).trim();
						
						if(isImage == true || isFlash == true || isMedia == true || isFile==true ||isMap== true || (!"".equals(source_text.trim()) && !"".equals(source_trimSpace))){
							if(systemSetting.isAllowFilterWord()){
								String wordReplace = "";
								if(systemSetting.getFilterWordReplace() != null){
									wordReplace = systemSetting.getFilterWordReplace();
								}
								value = sensitiveWordFilterManage.filterSensitiveWord(value, wordReplace);
							}
							
							appendContent = value;
						}else{
							error.put("content", ErrorView._218.name());//追加内容不能为空
						}	

					}else{
						error.put("content", ErrorView._218.name());//追加内容不能为空
					}
			}else{
				error.put("question", ErrorView._207.name());//问题不存在
			}
			
		}else{
			error.put("question", ErrorView._203.name());//问题Id不能为空
		}
		
		String appendContent_json = "";
		if(appendContent != null && !"".equals(appendContent.trim())){
			AppendQuestionItem appendQuestionItem = new AppendQuestionItem();
			appendQuestionItem.setId(UUIDUtil.getUUID32());
			appendQuestionItem.setContent(appendContent.trim());
			appendQuestionItem.setPostTime(new Date());
			appendContent_json = JsonUtils.toJSONString(appendQuestionItem);
		}else{
			error.put("content", ErrorView._218.name());//追加内容不能为空
		}
		
		
		
		if(error.size() == 0){
			
			//追加问题
			questionService.saveAppendQuestion(questionId, appendContent_json+",");
			//更新索引
			questionIndexService.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),2));

			//删除缓存
			questionManage.delete_cache_findById(questionId);//删除问题缓存
			
			String fileNumber = "b"+ accessUser.getUserId();
			
			//删除图片锁
			if(imageNameList != null && imageNameList.size() >0){
				for(String imageName :imageNameList){
			
					 if(imageName != null && !"".equals(imageName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						 if(!questionManage.getFileNumber(fileManage.getBaseName(imageName.trim())).equals(fileNumber)){
							 continue;
						 }
						 
						 
						 fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
					
					 }
				}
			}
			//falsh
			if(flashNameList != null && flashNameList.size() >0){
				for(String flashName :flashNameList){
					 if(flashName != null && !"".equals(flashName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						if(!questionManage.getFileNumber(fileManage.getBaseName(flashName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,flashName.replaceAll("/","_"));
					
					 }
				}
			}
			//音视频
			if(mediaNameList != null && mediaNameList.size() >0){
				for(String mediaName :mediaNameList){
					if(mediaName != null && !"".equals(mediaName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						if(!questionManage.getFileNumber(fileManage.getBaseName(mediaName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));
					
					}
				}
			}
			//文件
			if(fileNameList != null && fileNameList.size() >0){
				for(String fileName :fileNameList){
					if(fileName != null && !"".equals(fileName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						if(!questionManage.getFileNumber(fileManage.getBaseName(fileName.trim())).equals(fileNumber)){
							continue;
						}
						fileManage.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));
					
					}
				}
			}
			
			
			
			//统计每分钟原来提交次数
			Integer original = settingManage.getSubmitQuantity("question", accessUser.getUserName());
    		if(original != null){
    			settingManage.addSubmitQuantity("question", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
    		}else{
    			settingManage.addSubmitQuantity("question", accessUser.getUserName(),1);//刷新每分钟原来提交次数
    		}

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
				redirectAttrs.addFlashAttribute("content", content);
				

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
				model.addAttribute("message", "追加问题成功");
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
	 * dir: 上传类型，分别为image、file 
	 * 
	 * 员工发话题 上传文件名为UUID + a + 员工Id
	 * 用户发话题 上传文件名为UUID + b + 用户Id
	 */
	@RequestMapping(value="/upload", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String upload(ModelMap model,String dir,
			MultipartFile file,HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,Object> returnJson = new HashMap<String,Object>();
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		String errorMessage  = "";
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		boolean flag = true;
		
		//如果全局不允许提交问题
		if(systemSetting.isAllowQuestion() == false){
			flag = false;
		}
		
		//如果实名用户才允许提交问题
		if(systemSetting.isRealNameUserAllowQuestion() == true){
			User _user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
			if(_user.isRealNameAuthentication() == false){
				flag = false;
			}
		}
		
		if(flag){
			DateTime dateTime = new DateTime();     
			String date = dateTime.toString("yyyy-MM-dd");

			if(file != null && !file.isEmpty()){
				EditorTag editorSiteObject = settingManage.readQuestionEditorTag();
				if(editorSiteObject != null){
					if(dir.equals("image")){
						//是否有当前功能操作权限
						boolean flag_permission = userRoleManage.isPermission(ResourceEnum._2002000,null);
						if(flag_permission){
							if(editorSiteObject.isImage()){//允许上传图片
								//上传文件编号
								String fileNumber = "b"+accessUser.getUserId();
								
								//当前文件名称
								String fileName = file.getOriginalFilename();
								
								//文件大小
								Long size = file.getSize();
								//取得文件后缀
								String suffix = fileManage.getExtension(fileName).toLowerCase();
								
								//允许上传图片格式
								List<String> imageFormat = editorSiteObject.getImageFormat();
								//允许上传图片大小
								long imageSize = editorSiteObject.getImageSize();
								
								//验证文件类型
								boolean authentication = fileManage.validateFileSuffix(file.getOriginalFilename(),imageFormat);
								
								if(authentication ){
									if(size/1024 <= imageSize){
										//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
										String pathDir = "file"+File.separator+"question"+File.separator + date +File.separator +"image"+ File.separator;
										//文件锁目录
										String lockPathDir = "file"+File.separator+"question"+File.separator+"lock"+File.separator;
										//构建文件名称
										String newFileName = UUIDUtil.getUUID32()+ fileNumber+"." + suffix;
										
										//生成文件保存目录
										fileManage.createFolder(pathDir);
										//生成锁文件保存目录
										fileManage.createFolder(lockPathDir);
										//生成锁文件
										fileManage.newFile(lockPathDir+date +"_image_"+newFileName);
										//保存文件
										fileManage.writeFile(pathDir, newFileName,file.getBytes());
										//上传成功
										returnJson.put("error", 0);//0成功  1错误
										returnJson.put("url", "file/question/"+date+"/image/"+newFileName);
										return JsonUtils.toJSONString(returnJson);
									}else{
										errorMessage = "文件超出允许上传大小";
									}
								}else{
									errorMessage = "当前文件类型不允许上传";
								}
							}else{
								errorMessage = "不允许上传文件";
							}
						}else{
							errorMessage = "权限不足";
						}
						
						
						
					}else{
						errorMessage = "缺少dir参数";
					}
				}else{
					errorMessage = "读取话题编辑器允许使用标签失败";
				}	
			}else{
				errorMessage = "文件内容不能为空";
			}
		}else{
			errorMessage = "不允许发表问题";
		}
		

		
		//上传失败
		returnJson.put("error", 1);
		returnJson.put("message", errorMessage);
		return JsonUtils.toJSONString(returnJson);
	}
	
	
	
	/**
	 * 采纳答案 
	 * @param model
	 * @param answerId 答案Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/adoptionAnswer", method=RequestMethod.POST)
	public String adoptionAnswer(ModelMap model,Long answerId,String token,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		
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
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	Answer answer = null;
	  	if(answerId != null && answerId >0L){
	  		answer = answerManage.query_cache_findByAnswerId(answerId);
			if(answer != null){
				Question  question = questionManage.query_cache_findById(answer.getQuestionId());
				if(question != null){
					if(question.getAdoptionAnswerId() >0L){
						error.put("adoptionAnswer", ErrorView._216.name());//该问题已经采纳答案
					}
					
					if(!accessUser.getUserName().equals(question.getUserName())){
						error.put("adoptionAnswer", ErrorView._217.name());//不是提交该问题的用户不允许采纳答案
					}
					
				}else{
					error.put("adoptionAnswer", ErrorView._207.name());//问题不存在
				}
			}else{
				error.put("adoptionAnswer", ErrorView._205.name());//答案不存在
			}
	  		
	  		
	  	}else{
	  		error.put("adoptionAnswer", ErrorView._215.name());//答案Id不能为空
	  	}
	  	

		if(error.size() == 0){
			
			int i = answerService.updateAdoptionAnswer(answer.getQuestionId(), answerId);
			//删除缓存
			answerManage.delete_cache_findByAnswerId(answerId);
			questionManage.delete_cache_findById(answer.getQuestionId());
			answerManage.delete_cache_answerCount(answer.getUserName());

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
    		}else{
    			returnValue.put("success", "true");
    		}
    		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			
			
			if(error != null && error.size() >0){//如果有错误
				redirectAttrs.addFlashAttribute("error", returnError);//重定向传参
				

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
				model.addAttribute("message", "采纳答案成功");
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
