package cms.web.action.common;



import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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


import cms.bean.ErrorView;
import cms.bean.message.Remind;
import cms.bean.question.Answer;
import cms.bean.question.AnswerReply;
import cms.bean.question.Question;
import cms.bean.setting.EditorTag;
import cms.bean.setting.SystemSetting;
import cms.bean.user.AccessUser;
import cms.bean.user.PointLog;
import cms.bean.user.ResourceEnum;
import cms.bean.user.User;
import cms.bean.user.UserDynamic;
import cms.service.message.RemindService;
import cms.service.question.AnswerService;
import cms.service.question.QuestionService;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.service.user.UserService;
import cms.utils.Base64;
import cms.utils.FileUtil;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.utils.RefererCompare;
import cms.utils.UUIDUtil;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.CSRFTokenManage;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.filterWord.SensitiveWordFilterManage;
import cms.web.action.follow.FollowManage;
import cms.web.action.message.RemindManage;
import cms.web.action.question.AnswerManage;
import cms.web.action.question.QuestionManage;
import cms.web.action.setting.SettingManage;
import cms.web.action.user.PointManage;
import cms.web.action.user.UserDynamicManage;
import cms.web.action.user.UserManage;
import cms.web.action.user.UserRoleManage;
import cms.web.taglib.Configuration;

/**
 * 答案接收表单
 *
 */
@Controller
@RequestMapping("user/control/answer") 
public class AnswerFormAction {
	@Resource TemplateService templateService;
	@Resource CaptchaManage captchaManage;
	
	@Resource UserRoleManage userRoleManage;
	
	
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	
	@Resource TextFilterManage textFilterManage;
	@Resource SettingManage settingManage;
	@Resource SettingService settingService;
	@Resource UserService userService;
	@Resource FileManage fileManage;
	
	@Resource CSRFTokenManage csrfTokenManage;
	
	
	@Resource SensitiveWordFilterManage sensitiveWordFilterManage;
	@Resource UserManage userManage;
	@Resource UserDynamicManage userDynamicManage;
	@Resource QuestionManage questionManage;
	@Resource AnswerService answerService;
	@Resource PointManage pointManage;
	@Resource FollowManage followManage;
	@Resource QuestionService questionService;
	@Resource RemindManage remindManage;
	@Resource RemindService remindService;
	@Resource AnswerManage answerManage;
	/**
	 * 答案   添加
	 * @param model
	 * @param questionId 问题Id
	 * @param content 评论内容
	 * @param jumpUrl 跳转地址
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(ModelMap model,Long questionId,String content,
			String token,String captchaKey,String captchaValue,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
	
		Map<String,String> error = new HashMap<String,String>();
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("answer", ErrorView._21.name());//只读模式不允许提交数据
		}
		
		
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
		boolean isCaptcha = captchaManage.answer_isCaptcha(accessUser.getUserName());
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
		
		Answer answer = new Answer();
		Date postTime = new Date();
		answer.setPostTime(postTime);
		List<String> imageNameList = null;
		
		

		
		Question question = null;
		if(questionId != null && questionId >0L){
			question = questionManage.query_cache_findById(questionId);
			if(question != null){
				answer.setQuestionId(questionId);
				//是否全局允许回答
				if(systemSetting.isAllowAnswer() == false){
					error.put("answer", ErrorView._206.name());//禁止回答
				}
				if(question.isAllow() == false){
					error.put("answer", ErrorView._206.name());//禁止回答	
				}
				if(!question.getStatus().equals(20)){//已发布
					error.put("answer", ErrorView._211.name());//已发布问题才允许回答
				}
			}else{
				error.put("answer",ErrorView._207.name());//问题不存在
				
			}
		}else{
			error.put("answer",ErrorView._203.name());//问题Id不能为空
		}
		
		
		
		
		
		if(question != null){
			//前台发表评论审核状态
			if(systemSetting.getAnswer_review().equals(10)){//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
				answer.setStatus(10);//10.待审核 	
			}else if(systemSetting.getAnswer_review().equals(30)){
				if(question != null){
					//是否有当前功能操作权限
					boolean flag_permission = userRoleManage.isPermission(ResourceEnum._10012000,null);
					if(flag_permission){
						answer.setStatus(20);//20.已发布
					}else{
						answer.setStatus(10);//10.待审核 
					}
				}
			}else{
				answer.setStatus(20);//20.已发布
			}
			
			
			
			
			//是否有当前功能操作权限
			boolean flag_permission = userRoleManage.isPermission(ResourceEnum._10007000,null);
			if(flag_permission == false){
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
		}
		
		
		
		
		//如果实名用户才允许回答
		if(systemSetting.isRealNameUserAllowAnswer() == true){
			User _user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
			if(_user.isRealNameAuthentication() == false){
				error.put("answer", ErrorView._208.name());//实名用户才允许回答
			}
			
		}
		
		
		
		if(content != null && !"".equals(content.trim())){
			//过滤标签
			content = textFilterManage.filterTag(request,content,settingManage.readEditorTag());
			Object[] object = textFilterManage.filterHtml(request,content,"answer",settingManage.readEditorTag());
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
				answer.setContent(value);
				answer.setIsStaff(false);
				answer.setUserName(accessUser.getUserName());
			}else{
				error.put("content",ErrorView._201.name());//内容不能为空
			}
		}else{
			error.put("content",ErrorView._201.name());//内容不能为空
		}
		
		if(error.size() == 0){
			answer.setIp(IpAddress.getClientIpAddress(request));
			//保存答案
			answerService.saveAnswer(answer);
			
			
			PointLog pointLog = new PointLog();
			pointLog.setId(pointManage.createPointLogId(accessUser.getUserId()));
			pointLog.setModule(800);//800.答案
			pointLog.setParameterId(answer.getId());//参数Id 
			pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
			pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称
			
			pointLog.setPoint(systemSetting.getAnswer_rewardPoint());//积分
			pointLog.setUserName(accessUser.getUserName());//用户名称
			pointLog.setRemark("");
			
			//增加用户积分
			userService.addUserPoint(accessUser.getUserName(),systemSetting.getAnswer_rewardPoint(), pointManage.createPointLogObject(pointLog));
			
			//用户动态
			UserDynamic userDynamic = new UserDynamic();
			userDynamic.setId(userDynamicManage.createUserDynamicId(accessUser.getUserId()));
			userDynamic.setUserName(accessUser.getUserName());
			userDynamic.setModule(600);//模块 600.答案
			userDynamic.setQuestionId(question.getId());
			userDynamic.setAnswerId(answer.getId());
			userDynamic.setPostTime(postTime);
			userDynamic.setStatus(answer.getStatus());
			userDynamic.setFunctionIdGroup(","+question.getId()+","+answer.getId()+",");
			Object new_userDynamic = userDynamicManage.createUserDynamicObject(userDynamic);
			userService.saveUserDynamic(new_userDynamic);

			
			//删除缓存
			userManage.delete_cache_findUserById(accessUser.getUserId());
			userManage.delete_cache_findUserByUserName(accessUser.getUserName());
			questionManage.delete_cache_findById(questionId);
			answerManage.delete_cache_answerCount(answer.getUserName());
			followManage.delete_cache_userUpdateFlag(accessUser.getUserName());
			
			//修改问题最后回答时间
			questionService.updateQuestionAnswerTime(questionId,new Date());
			
			
			if(!question.getIsStaff()){
				User _user = userManage.query_cache_findUserByUserName(question.getUserName());
				if(_user != null && !_user.getId().equals(accessUser.getUserId())){//作者回答不发提醒给自己
					
					//提醒作者
					Remind remind = new Remind();
					remind.setId(remindManage.createRemindId(_user.getId()));
					remind.setReceiverUserId(_user.getId());//接收提醒用户Id
					remind.setSenderUserId(accessUser.getUserId());//发送用户Id
					remind.setTypeCode(120);//120:别人回答了我的问题
					remind.setSendTimeFormat(postTime.getTime());//发送时间格式化
					remind.setQuestionId(question.getId());//问题Id
					remind.setFriendQuestionAnswerId(answer.getId());//对方的问题答案Id
					
					Object remind_object = remindManage.createRemindObject(remind);
					remindService.saveRemind(remind_object);
					
					//删除提醒缓存
					remindManage.delete_cache_findUnreadRemindByUserId(_user.getId());
				}
			}
			
			
			
			
			
			
			String fileNumber = "b"+ accessUser.getUserId();
			
			//删除图片锁
			if(imageNameList != null && imageNameList.size() >0){
				for(String imageName :imageNameList){
					if(imageName != null && !"".equals(imageName.trim())){
						//如果验证不是当前用户上传的文件，则不删除锁
						 if(!questionManage.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
							 continue;
						 }
						 fileManage.deleteLock("file"+File.separator+"answer"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
					 }
				}
			}	
			//统计每分钟原来提交次数
			Integer original = settingManage.getSubmitQuantity("answer", accessUser.getUserName());
    		if(original != null){
    			settingManage.addSubmitQuantity("answer", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
    		}else{
    			settingManage.addSubmitQuantity("answer", accessUser.getUserName(),1);//刷新每分钟原来提交次数
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
				redirectAttrs.addFlashAttribute("answer", answer);
				

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
				model.addAttribute("message", "保存答案成功");
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
	 * 答案  修改
	 * @param model
	 * @param answerId 答案Id
	 * @param content 内容
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(ModelMap model,Long answerId,String content,
			String token,String captchaKey,String captchaValue,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,String> error = new HashMap<String,String>();
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("answer", ErrorView._21.name());//只读模式不允许提交数据
		}
		
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
		boolean isCaptcha = captchaManage.answer_isCaptcha(accessUser.getUserName());
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
		List<String> imageNameList = null;
		Answer answer = null;
		
		Question question = null;
		//旧状态
		Integer old_status = -1;
		
		String old_content = "";

		if(answerId != null && answerId >0L){
			answer = answerService.findByAnswerId(answerId);
			if(answer != null){
				question = questionService.findById(answer.getQuestionId());
				if(question != null){
					if(!answer.getUserName().equals(accessUser.getUserName())){
						error.put("answer", ErrorView._240.name());//只允许修改自己发布的答案
					}
					if(answer.getAdoption()){
						error.put("answer", ErrorView._244.name());//不允许修改已采纳的答案
					}
					/**
					if(answer.getStatus() > 100){
						error.put("comment", ErrorView._116.name());//答案已删除
					}**/
					
					//是否有当前功能操作权限
					boolean flag_permission = userRoleManage.isPermission(ResourceEnum._10008000,null);
					if(flag_permission == false){
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
					
					
					
					
					
					
					//如果全局不允许提交答案
					if(systemSetting.isAllowAnswer() == false){
						error.put("answer", ErrorView._206.name());//禁止回答
					}
					
					//如果实名用户才允许提交回答
					if(systemSetting.isRealNameUserAllowAnswer() == true){
						User _user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
						if(_user.isRealNameAuthentication() == false){
							error.put("answer", ErrorView._208.name());//实名用户才允许提交回答
						}
						
					}
					old_status = answer.getStatus();
					old_content = answer.getContent();
				}else{
					error.put("answer", ErrorView._207.name());//问题不存在
				}
				
			}else{
				error.put("answer", ErrorView._205.name());//答案不存在
			}
		
		}else{
			error.put("answer", ErrorView._243.name());//答案Id不能为空
		}
		
		if(error.size() ==0){
			if(answer.getStatus().equals(20)){//如果已发布，则重新执行发贴审核逻辑
				//前台发表答案审核状态
				if(systemSetting.getAnswer_review().equals(10)){//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
					answer.setStatus(10);//10.待审核 	
				}else if(systemSetting.getAnswer_review().equals(30)){
					if(question != null){
						//是否有当前功能操作权限
						boolean flag_permission = userRoleManage.isPermission(ResourceEnum._10012000,null);
						if(flag_permission){
							answer.setStatus(20);//20.已发布
						}else{
							answer.setStatus(10);//10.待审核 
						}
					}
				}else{
					answer.setStatus(20);//20.已发布
				}
			}
			
			
			
			if(content != null && !"".equals(content.trim())){
				//过滤标签
				content = textFilterManage.filterTag(request,content,settingManage.readEditorTag());
				Object[] object = textFilterManage.filterHtml(request,content,"answer",settingManage.readEditorTag());
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
					answer.setContent(value);
				}else{
					error.put("content",ErrorView._101.name());//内容不能为空
				}
			}else{
				error.put("content",ErrorView._101.name());//内容不能为空
			}
			
			
		
		}
		
		
		if(error.size() == 0){
			
			int i = answerService.updateAnswer(answer.getId(),answer.getContent(),answer.getStatus(),new Date(),answer.getUserName());
			
			if(i >0 && answer.getStatus() < 100 && !old_status.equals(answer.getStatus())){
				User user = userManage.query_cache_findUserByUserName(answer.getUserName());
				if(user != null){
					//修改答案状态
					userService.updateUserDynamicAnswerStatus(user.getId(),answer.getUserName(),answer.getQuestionId(),answer.getId(),answer.getStatus());
				}
				
			}
			 
			if(i >0){
				//删除缓存
				answerManage.delete_cache_findByAnswerId(answer.getId());
				

				

				
				//上传文件编号
				String fileNumber = questionManage.generateFileNumber(answer.getUserName(), answer.getIsStaff());
				
				//删除图片锁
				if(imageNameList != null && imageNameList.size() >0){
					for(String imageName :imageNameList){
						if(imageName != null && !"".equals(imageName.trim())){
							 //如果验证不是当前用户上传的文件，则不删除
							 if(!questionManage.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
									continue;
							 }
							 fileManage.deleteLock("file"+File.separator+"answer"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
						 }
					
					}
				}

				
				//旧图片名称
				List<String> old_ImageName = textFilterManage.readImageName(old_content,"answer");
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
							 if(!questionManage.getFileNumber(FileUtil.getBaseName(imagePath.trim())).equals(fileNumber)){
									continue;
							 }
							//替换路径中的..号
							imagePath = FileUtil.toRelativePath(imagePath);
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
				error.put("answer", ErrorView._242.name());//修改答案失败
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
				redirectAttrs.addFlashAttribute("answer", answer);
				

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
				model.addAttribute("message", "修改答案成功");
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
	 * 答案  删除
	 * @param model
	 * @param answerId 答案Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public String delete(ModelMap model,Long answerId,
			String token,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,String> error = new HashMap<String,String>();
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("answer", ErrorView._21.name());//只读模式不允许提交数据
		}
		
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

		
		Answer answer = null;
		Question question = null;
		

		if(answerId != null && answerId >0L){
			answer = answerService.findByAnswerId(answerId);
			if(answer != null){
				question = questionService.findById(answer.getQuestionId());
				if(question != null){
					if(!answer.getUserName().equals(accessUser.getUserName())){
						error.put("answer", ErrorView._240.name());//只允许删除自己发布的答案
					}
					
					if(answer.getStatus() > 100){
						error.put("answer", ErrorView._241.name());//答案已删除
					}
					
					//是否有当前功能操作权限
					boolean flag_permission = userRoleManage.isPermission(ResourceEnum._10009000,null);
					if(flag_permission == false){
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

				}else{
					error.put("answer", ErrorView._212.name());//问题不存在
				}
				
			}else{
				error.put("answer", ErrorView._205.name());//答案不存在
			}
		
		}else{
			error.put("answer", ErrorView._215.name());//答案Id不能为空
		}
		
		
		
		if(error.size() == 0){
			Integer constant = 100;
			int i = answerService.markDeleteAnswer(answer.getId(),constant);
			
			if(i >0 && answer.getStatus() < 100){
				User user = userManage.query_cache_findUserByUserName(answer.getUserName());
				if(user != null){
					//修改答案状态
					userService.updateUserDynamicAnswerStatus(user.getId(),answer.getUserName(),answer.getQuestionId(),answer.getId(),answer.getStatus()+constant);
				}
				
			}
			 
			if(i >0){
				//删除缓存
				answerManage.delete_cache_findByAnswerId(answer.getId());
				
				
			}else{
				error.put("answer", ErrorView._245.name());//删除答案失败
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
    			
    		}else{
    			returnValue.put("success", "true");
    		}
    		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			
			
			if(error != null && error.size() >0){//如果有错误
				
				redirectAttrs.addFlashAttribute("error", returnError);//重定向传参
				redirectAttrs.addFlashAttribute("answer", answer);
				

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
				model.addAttribute("message", "删除答案成功");
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
	 * 答案  图片上传
	 * @param model
	 * @param questionId 问题Id
	 * @param fileName 文件名称 预签名时有值
	 * @param imgFile
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/uploadImage", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String uploadImage(ModelMap model,Long questionId,String fileName,
			MultipartFile file, HttpServletRequest request,HttpServletResponse response) throws Exception {

		Map<String,Object> returnJson = new HashMap<String,Object>();
		String errorMessage  = "";
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			errorMessage = "只读模式不允许提交数据";
		}else{
			//是否有当前功能操作权限
			boolean flag_permission = userRoleManage.isPermission(ResourceEnum._2002000,null);
			if(flag_permission){
				//文件上传
				int fileSystem = fileManage.getFileSystem();
				if(fileSystem ==10 || fileSystem == 20 || fileSystem == 30){//10.SeaweedFS 20.MinIO 30.阿里云OSS
					if(fileName != null && !"".equals(fileName.trim()) && questionId != null && questionId >0L){
						//取得文件后缀
						String suffix = FileUtil.getExtension(fileName.trim()).toLowerCase();
						EditorTag editorSiteObject = settingManage.readAnswerEditorTag();
						if(editorSiteObject != null && systemSetting.isAllowAnswer()){//是否全局允许回答
							if(editorSiteObject.isImage()){//允许上传图片
								//获取登录用户
							  	AccessUser accessUser = AccessUserThreadLocal.get();
								
								//上传文件编号
								String fileNumber = "b"+accessUser.getUserId();
								
								//允许上传图片格式
								List<String> imageFormat = editorSiteObject.getImageFormat();
								//允许上传图片大小
								long imageSize = editorSiteObject.getImageSize();

								//验证文件类型
								boolean authentication = FileUtil.validateFileSuffix(fileName.trim(),imageFormat);
								
								if(authentication ){
									//文件锁目录
									String lockPathDir = "file"+File.separator+"answer"+File.separator+"lock"+File.separator;
									//构建文件名称
									String newFileName = UUIDUtil.getUUID32()+fileNumber+ "." + suffix;
									
									
									//生成锁文件
									fileManage.addLock(lockPathDir,questionId+"_"+newFileName);
									String presigne = fileManage.createPresigned("file/answer/"+questionId+"/"+newFileName,imageSize);//创建预签名
									
									//上传成功
									returnJson.put("error", 0);//0成功  1错误
									returnJson.put("url", presigne);
									
									return JsonUtils.toJSONString(returnJson);
									
								}else{
									errorMessage = "当前文件类型不允许上传";
								}
							}else{
								errorMessage = "不允许上传图片";
							}
						}else{
							errorMessage = "不允许回答";
						}
					}else{
						errorMessage = "参数错误";
					}
				}else{//0.本地系统
					//文件上传
					if(file != null && !file.isEmpty()){
						if(questionId != null && questionId >0L){
							
							
							
							EditorTag editorSiteObject = settingManage.readAnswerEditorTag();
							if(editorSiteObject != null && systemSetting.isAllowAnswer()){//是否全局允许回答
								if(editorSiteObject.isImage()){//允许上传图片
									//获取登录用户
								  	AccessUser accessUser = AccessUserThreadLocal.get();
									
									//上传文件编号
									String fileNumber = "b"+accessUser.getUserId();
									
									
									//当前图片文件名称
									String sourceFileName = file.getOriginalFilename();
									//当前图片类型
								//	String imgType = file.getContentType();
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
									
									if(authentication ){
										if(size/1024 <= imageSize){
											//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
											String pathDir = "file"+File.separator+"answer"+File.separator + questionId + File.separator;
											//文件锁目录
											String lockPathDir = "file"+File.separator+"answer"+File.separator+"lock"+File.separator;
											//构建文件名称
											String newFileName = UUIDUtil.getUUID32()+fileNumber+ "." + suffix;
											
											//生成文件保存目录
											fileManage.createFolder(pathDir);
											//生成锁文件保存目录
											fileManage.createFolder(lockPathDir);
											//生成锁文件
											fileManage.addLock(lockPathDir,questionId+"_"+newFileName);
											//保存文件
											fileManage.writeFile(pathDir, newFileName,file.getBytes());
											
											//上传成功
											returnJson.put("error", 0);//0成功  1错误
											returnJson.put("url", fileManage.fileServerAddress()+"file/answer/"+questionId+"/"+newFileName);
											
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
								errorMessage = "不允许回答";
							}
						}else{
							errorMessage = "问题Id不能为空";
						}
					}else{
						errorMessage = "文件内容不能为空";
					}
					
				}
			}else{
				errorMessage = "权限不足";
			}
			
			
		}

		//上传失败
		returnJson.put("error", 1);
		returnJson.put("message", errorMessage);
		return JsonUtils.toJSONString(returnJson);
	}
	
	
	
	

	/**
	 * 回复  添加
	 * @param model
	 * @param answerId 答案回复Id
	 * @param content
	 * @param token
	 * @param captchaKey
	 * @param captchaValue
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/addAnswerReply", method=RequestMethod.POST)
	public String addAnswerReply(ModelMap model,Long answerId,String content,
			String token,String captchaKey,String captchaValue,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		//获取登录用户
		AccessUser accessUser = AccessUserThreadLocal.get();
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		Map<String,String> error = new HashMap<String,String>();
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("answerReply", ErrorView._21.name());//只读模式不允许提交数据
		}
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
		boolean isCaptcha = captchaManage.answer_isCaptcha(accessUser.getUserName());
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
		
		Answer answer = null;
		Question question = null;
		if(answerId == null || answerId <=0){
			error.put("answerReply", ErrorView._205.name());//答案不存在
		}else{
			answer = answerService.findByAnswerId(answerId);
			
			if(answer != null){
				
				question = questionService.findById(answer.getQuestionId());
				if(question != null){
					//是否全局允许回答
					if(systemSetting.isAllowAnswer() == false){
						error.put("answerReply", ErrorView._206.name());//禁止回答
					}
					if(question.isAllow() == false){
						error.put("answerReply", ErrorView._206.name());//禁止回答	
					}
					if(!question.getStatus().equals(20)){//已发布
						error.put("answerReply", ErrorView._211.name());//已发布问题才允许回复
					}
				}else{
					error.put("answerReply",ErrorView._207.name());//问题不存在
					
				}
			}else{
				error.put("answerReply", ErrorView._205.name());//答案不存在
			}
			
			
		}
		
		if(question != null){
			//是否有当前功能操作权限
			boolean flag_permission = userRoleManage.isPermission(ResourceEnum._10013000,null);
			if(flag_permission == false){
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
		}
		
		
		
		
		//如果实名用户才允许回答
		if(systemSetting.isRealNameUserAllowAnswer() == true){
			User _user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
			if(_user.isRealNameAuthentication() == false){
				error.put("answerReply", ErrorView._208.name());//实名用户才允许回答
			}
			
		}

		//回复
		AnswerReply answerReply = new AnswerReply();
		Date postTime = new Date();
		answerReply.setPostTime(postTime);
		
		//前台提交答案审核状态
		if(systemSetting.getAnswerReply_review().equals(10)){//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
			answerReply.setStatus(10);//10.待审核 	
		}else if(systemSetting.getAnswerReply_review().equals(30)){
			if(question != null){
				//是否有当前功能操作权限
				boolean flag_permission = userRoleManage.isPermission(ResourceEnum._10016000,null);
				if(flag_permission){
					answerReply.setStatus(20);//20.已发布
				}else{
					answerReply.setStatus(10);//10.待审核 
				}
			}
		}else{
			answerReply.setStatus(20);//20.已发布
		}
		
		
		if(error != null && error.size() ==0){
			if(content != null && !"".equals(content.trim())){
				if(answer != null){
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
						
						answerReply.setAnswerId(answer.getId());
						answerReply.setIsStaff(false);
						answerReply.setUserName(accessUser.getUserName());
						answerReply.setIp(IpAddress.getClientIpAddress(request));
						answerReply.setContent(text);
						answerReply.setQuestionId(answer.getQuestionId());
						
						

					}else{	
						error.put("content", ErrorView._201.name());//内容不能为空
						
					}	
				}else{
					error.put("answerReply", ErrorView._205.name());//答案不存在
				}
			}else{
				error.put("content", ErrorView._201.name());//内容不能为空
			}
			
		}
		
		
		//保存
		if(error.size() ==0){
			//保存回复
			answerService.saveReply(answerReply);
			
			PointLog pointLog = new PointLog();
			pointLog.setId(pointManage.createPointLogId(accessUser.getUserId()));
			pointLog.setModule(900);//900.答案回复
			pointLog.setParameterId(answer.getId());//参数Id 
			pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
			pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称
			
			pointLog.setPoint(systemSetting.getAnswerReply_rewardPoint());//积分
			pointLog.setUserName(accessUser.getUserName());//用户名称
			pointLog.setRemark("");
			
			//增加用户积分
			userService.addUserPoint(accessUser.getUserName(),systemSetting.getAnswerReply_rewardPoint(), pointManage.createPointLogObject(pointLog));
			
			
			//用户动态
			UserDynamic userDynamic = new UserDynamic();
			userDynamic.setId(userDynamicManage.createUserDynamicId(accessUser.getUserId()));
			userDynamic.setUserName(accessUser.getUserName());
			userDynamic.setModule(700);//模块 700.答案回复
			userDynamic.setQuestionId(answerReply.getQuestionId());
			userDynamic.setAnswerId(answerReply.getAnswerId());
			userDynamic.setAnswerReplyId(answerReply.getId());
			userDynamic.setPostTime(answerReply.getPostTime());
			userDynamic.setStatus(answerReply.getStatus());
			userDynamic.setFunctionIdGroup(","+answerReply.getQuestionId()+","+answerReply.getAnswerId()+","+answerReply.getId()+",");
			Object new_userDynamic = userDynamicManage.createUserDynamicObject(userDynamic);
			userService.saveUserDynamic(new_userDynamic);
			
			//删除缓存
			userManage.delete_cache_findUserById(accessUser.getUserId());
			userManage.delete_cache_findUserByUserName(accessUser.getUserName());
			followManage.delete_cache_userUpdateFlag(accessUser.getUserName());
			
			//修改问题最后回复时间
			questionService.updateQuestionAnswerTime(answer.getQuestionId(),new Date());
			
			if(question != null && !question.getIsStaff()){
				User _user = userManage.query_cache_findUserByUserName(question.getUserName());
				//别人回复了我的问题
				if(_user != null && !_user.getId().equals(accessUser.getUserId())){//作者回复不发提醒给自己
					//如果别人回复了问题发布者的答案，则不发本类型提醒给问题发布者
					if(!answer.getUserName().equals(question.getUserName())){
						Remind remind = new Remind();
						remind.setId(remindManage.createRemindId(_user.getId()));
						remind.setReceiverUserId(_user.getId());//接收提醒用户Id
						remind.setSenderUserId(accessUser.getUserId());//发送用户Id
						remind.setTypeCode(130);//130:别人回复了我的问题
						remind.setSendTimeFormat(postTime.getTime());//发送时间格式化
						remind.setQuestionId(answer.getQuestionId());//问题Id
						remind.setFriendQuestionAnswerId(answer.getId());//对方的问题答案Id
						remind.setFriendQuestionReplyId(answerReply.getId());//对方的问题回复Id
						
						Object remind_object = remindManage.createRemindObject(remind);
						remindService.saveRemind(remind_object);
						
						//删除提醒缓存
						remindManage.delete_cache_findUnreadRemindByUserId(_user.getId());
						
					}		
				}
			}
			
			User _user = userManage.query_cache_findUserByUserName(answer.getUserName());
			//别人回复了我的答案
			if(!answer.getIsStaff() && _user != null && !_user.getId().equals(accessUser.getUserId())){//不发提醒给自己

				Remind remind = new Remind();
				remind.setId(remindManage.createRemindId(_user.getId()));
				remind.setReceiverUserId(_user.getId());//接收提醒用户Id
				remind.setSenderUserId(accessUser.getUserId());//发送用户Id
				remind.setTypeCode(140);//140:别人回复了我的答案 
				remind.setSendTimeFormat(postTime.getTime());//发送时间格式化
				remind.setQuestionId(answer.getQuestionId());//问题Id
				remind.setQuestionAnswerId(answer.getId());//我的问题答案Id
				remind.setFriendQuestionReplyId(answerReply.getId());//对方的问题回复Id
				
				
				Object remind_object = remindManage.createRemindObject(remind);
				remindService.saveRemind(remind_object);
				
				//删除提醒缓存
				remindManage.delete_cache_findUnreadRemindByUserId(_user.getId());
			}
			
			Set<String> userNameList = new HashSet<String>();
			List<AnswerReply> replyList = answerService.findReplyByAnswerId(answer.getId());
			if(replyList != null && replyList.size() >0){
				for(AnswerReply _reply : replyList){
					//如果是问题发布者的回复，则不发本类型提醒给问题的发布者
					if(question != null && !question.getIsStaff() && _reply.getUserName().equals(question.getUserName())){
						continue;
					}
					//如果是答案发布者的回复，则不发本类型提醒给答案的发布者
					if(!answer.getIsStaff() && _reply.getUserName().equals(answer.getUserName())){
						continue;
					}
					//员工的回复不发提醒
					if(_reply.getIsStaff()){
						continue;
					}
					
					//如果是自己的回复，则不发本类型提醒给自己
					if(_reply.getUserName().equals(accessUser.getUserName())){
						continue;
					}

					//如果同一个用户有多条回复,只发一条提醒
					if(userNameList.contains(_reply.getUserName())){
						continue;
					}
					
					
					userNameList.add(_reply.getUserName());
					
					_user = userManage.query_cache_findUserByUserName(_reply.getUserName());
					
					//提醒
					Remind remind = new Remind();
					remind.setId(remindManage.createRemindId(_user.getId()));
					remind.setReceiverUserId(_user.getId());//接收提醒用户Id
					remind.setSenderUserId(accessUser.getUserId());//发送用户Id
					remind.setTypeCode(150);//150:别人回复了我回复过的答案
					remind.setSendTimeFormat(postTime.getTime());//发送时间格式化
					remind.setQuestionId(_reply.getQuestionId());//问题Id
					remind.setQuestionReplyId(_reply.getId());//我的问题回复Id
					
					
					remind.setFriendQuestionAnswerId(answer.getId());//对方的问题答案Id
					remind.setFriendQuestionReplyId(answerReply.getId());//对方的问题回复Id
					
					
					Object remind_object = remindManage.createRemindObject(remind);
					remindService.saveRemind(remind_object);
					
					//删除提醒缓存
					remindManage.delete_cache_findUnreadRemindByUserId(_user.getId());
				}
			}
			
			
			
			
			//统计每分钟原来提交次数
			Integer original = settingManage.getSubmitQuantity("answer", accessUser.getUserName());
    		if(original != null){
    			settingManage.addSubmitQuantity("answer", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
    		}else{
    			settingManage.addSubmitQuantity("answer", accessUser.getUserName(),1);//刷新每分钟原来提交次数
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
				redirectAttrs.addFlashAttribute("answerReply", answerReply);
				

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
	
	
	
	
	/**
	 * 答案回复  修改
	 * @param model
	 * @param replyId 回复Id
	 * @param content 内容
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/editReply", method=RequestMethod.POST)
	public String editReply(ModelMap model,Long replyId,String content,
			String token,String captchaKey,String captchaValue,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,String> error = new HashMap<String,String>();
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("reply", ErrorView._21.name());//只读模式不允许提交数据
		}
		
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
		
		
		
		
		Question question = null;
		AnswerReply answerReply = null;
		//旧状态
		Integer old_status = -1;
		

		if(replyId != null && replyId >0L){
			answerReply = answerService.findReplyByReplyId(replyId);
			if(answerReply != null){
				question = questionService.findById(answerReply.getQuestionId());
				if(question != null){
					if(!answerReply.getUserName().equals(accessUser.getUserName())){
						error.put("reply", ErrorView._122.name());//只允许修改自己发布的回复
					}
					/**
					if(reply.getStatus() > 100){
						error.put("reply", ErrorView._116.name());//回复已删除
					}**/
					
					//是否有当前功能操作权限
					boolean flag_permission = userRoleManage.isPermission(ResourceEnum._10014000,null);
					if(flag_permission == false){
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
					
					
					
					
					
					
					//如果全局不允许提交答案
					if(systemSetting.isAllowAnswer() == false){
						error.put("reply", ErrorView._123.name());//禁止回复
					}
					
					//如果实名用户才允许提交答案
					if(systemSetting.isRealNameUserAllowAnswer() == true){
						User _user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
						if(_user.isRealNameAuthentication() == false){
							error.put("reply", ErrorView._124.name());//实名用户才允许提交回复
						}
						
					}
					old_status = answerReply.getStatus();
				}else{
					error.put("reply", ErrorView._207.name());//问题不存在
				}
				
			}else{
				error.put("reply", ErrorView._125.name());//回复不存在
			}
		
		}else{
			error.put("reply", ErrorView._126.name());//回复Id不能为空
		}
		
		
		if(error.size() ==0){
			if(answerReply.getStatus().equals(20)){//如果已发布，则重新执行发贴审核逻辑
				//前台发表评论审核状态
				if(systemSetting.getReply_review().equals(10)){//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
					answerReply.setStatus(10);//10.待审核 	
				}else if(systemSetting.getReply_review().equals(30)){
					if(question != null){
						//是否有当前功能操作权限
						boolean flag_permission = userRoleManage.isPermission(ResourceEnum._10016000,null);
						if(flag_permission){
							answerReply.setStatus(20);//20.已发布
						}else{
							answerReply.setStatus(10);//10.待审核 
						}
					}
				}else{
					answerReply.setStatus(20);//20.已发布
				}
			}
			
			
			
			if(content != null && !"".equals(content.trim())){
				
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
					answerReply.setContent(text);
				}else{	
					error.put("content", ErrorView._101.name());//内容不能为空
					
				}	
				
			}else{
				error.put("content", ErrorView._101.name());//内容不能为空
			}

		}


		
		if(error.size() == 0){
			//修改回复
			int i = answerService.updateReply(answerReply.getId(),answerReply.getContent(),answerReply.getUserName(),answerReply.getStatus(),new Date());
			
			if(i >0 && !old_status.equals(answerReply.getStatus())){
				User user = userManage.query_cache_findUserByUserName(answerReply.getUserName());
				if(user != null){
					//修改回复状态
					userService.updateUserDynamicReplyStatus(user.getId(),answerReply.getUserName(),answerReply.getQuestionId(),answerReply.getAnswerId(),answerReply.getId(),answerReply.getStatus());
				}
				
			}
			

			 
			if(i >0){
				//删除缓存
				answerManage.delete_cache_findReplyByReplyId(answerReply.getId());
			}else{
				error.put("reply", ErrorView._121.name());//修改回复失败
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
				redirectAttrs.addFlashAttribute("reply", answerReply);
				

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
				model.addAttribute("message", "修改回复成功");
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
	 * 答案回复  删除
	 * @param model
	 * @param replyId 回复Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteReply", method=RequestMethod.POST)
	public String deleteReply(ModelMap model,Long replyId,
			String token,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,String> error = new HashMap<String,String>();
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("reply", ErrorView._21.name());//只读模式不允许提交数据
		}
		
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
		
		
		Question question = null;
		AnswerReply reply = null;
		
		if(replyId != null && replyId >0L){
			reply = answerService.findReplyByReplyId(replyId);
			if(reply != null){
				question = questionService.findById(reply.getQuestionId());
				if(question != null){
					if(!reply.getUserName().equals(accessUser.getUserName())){
						error.put("reply", ErrorView._144.name());//只允许删除自己发布的回复
					}
					
					if(reply.getStatus() > 100){
						error.put("reply", ErrorView._116.name());//回复已删除
					}
					
					//是否有当前功能操作权限
					boolean flag_permission = userRoleManage.isPermission(ResourceEnum._10015000,null);
					if(flag_permission == false){
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
					
				}else{
					error.put("reply", ErrorView._107.name());//话题不存在
				}
				
			}else{
				error.put("reply", ErrorView._125.name());//回复不存在
			}
		
		}else{
			error.put("reply", ErrorView._126.name());//回复Id不能为空
		}
		
		
		if(error.size() == 0){
			Integer constant = 100;
			int i = answerService.markDeleteReply(reply.getId(),constant);
			
			if(i >0){
				User user = userManage.query_cache_findUserByUserName(reply.getUserName());
				if(user != null){
					//修改回复状态
					userService.updateUserDynamicAnswerReplyStatus(user.getId(),reply.getUserName(),reply.getQuestionId(),reply.getAnswerId(),reply.getId(),reply.getStatus()+constant);
				}
				
			}
			

			 
			if(i >0){
				//删除缓存
				answerManage.delete_cache_findReplyByReplyId(reply.getId());
			}else{
				error.put("reply", ErrorView._142.name());//删除回复失败
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
				model.addAttribute("message", "删除回复成功");
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
