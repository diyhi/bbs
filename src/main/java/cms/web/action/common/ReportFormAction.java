package cms.web.action.common;



import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cms.bean.ErrorView;
import cms.bean.question.Answer;
import cms.bean.question.AnswerReply;
import cms.bean.question.Question;
import cms.bean.report.Report;
import cms.bean.report.ReportType;
import cms.bean.setting.SystemSetting;
import cms.bean.topic.Comment;
import cms.bean.topic.ImageInfo;
import cms.bean.topic.Reply;
import cms.bean.topic.Topic;
import cms.bean.user.AccessUser;
import cms.service.question.AnswerService;
import cms.service.question.QuestionService;
import cms.service.report.ReportService;
import cms.service.report.ReportTypeService;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.service.topic.CommentService;
import cms.service.topic.TopicService;
import cms.utils.Base64;
import cms.utils.FileUtil;
import cms.utils.HtmlEscape;
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
import cms.web.action.setting.SettingManage;
import cms.web.taglib.Configuration;

/**
 * 举报接收表单
 *
 */
@Controller
@RequestMapping("user/control/report") 
public class ReportFormAction {
	@Resource TemplateService templateService;
	@Resource FileManage fileManage;
	@Resource CaptchaManage captchaManage;
	@Resource ReportTypeService reportTypeService;
	@Resource ReportService reportService; 
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource TextFilterManage textFilterManage;
	@Resource SettingManage settingManage;
	@Resource SettingService settingService;
	@Resource CSRFTokenManage csrfTokenManage;
	@Resource TopicService topicService;
	@Resource CommentService commentService;
	@Resource QuestionService questionService; 
	@Resource AnswerService answerService;
	
	//模块参数
	private List<Integer> modules = Arrays.asList(10,20,30,40,50,60); 
	
	/**
	 * 举报  添加
	 * @param model
	 * @param reportTypeId 举报分类Id
	 * @param reason 理由
	 * @param parameterId 参数Id
	 * @param module 模块
	 * @param imageFile 图片文件
	 * @param amount 悬赏金额
	 * @param point 悬赏积分
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(ModelMap model,String reportTypeId,String reason,String parameterId,Integer module, MultipartFile[] imageFile,
			String token,String captchaKey,String captchaValue,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		Report report = new Report();
		
		Map<String,String> error = new HashMap<String,String>();
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("report", ErrorView._21.name());//只读模式不允许提交数据
		}
			
		
		//处理CSRF令牌
		csrfTokenManage.processCsrfToken(request, token,error);
				
				
		
		//验证码
		boolean isCaptcha = captchaManage.report_isCaptcha(accessUser.getUserName());
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
		
		
		if(module == null || module <=0){
			error.put("report", ErrorView._4070.name());//模块不能为空
			
		}else{
			if(!modules.contains(module)){
				error.put("report", ErrorView._4080.name());//状态参数错误
			}
		}
		if(error.size()==0 && parameterId != null && !"".equals(parameterId.trim())){
			if(module.equals(10)){//话题
				Topic topic = topicService.findById(Long.parseLong(parameterId.trim()));
				if(topic != null && topic.getStatus().equals(20)){
					report.setParameterId(parameterId.trim());
				}else{
					error.put("report", ErrorView._4090.name());//话题不存在
				}
			}else if(module.equals(20)){//评论
				Comment comment = commentService.findByCommentId(Long.parseLong(parameterId.trim()));
				if(comment != null && comment.getStatus().equals(20)){
					report.setParameterId(parameterId.trim());
					report.setExtraParameterId(comment.getTopicId().toString());
				}else{
					error.put("report", ErrorView._4100.name());//评论不存在
				}
			}else if(module.equals(30)){//评论回复
				Reply reply = commentService.findReplyByReplyId(Long.parseLong(parameterId.trim()));
				if(reply != null && reply.getStatus().equals(20)){
					report.setParameterId(parameterId.trim());
					report.setExtraParameterId(reply.getTopicId()+"-"+reply.getCommentId());
				}else{
					error.put("report", ErrorView._4110.name());//评论回复不存在
				}
			}else if(module.equals(40)){//问题
				Question question = questionService.findById(Long.parseLong(parameterId.trim()));
				if(question != null && question.getStatus().equals(20)){
					report.setParameterId(parameterId.trim());
				}else{
					error.put("report", ErrorView._4120.name());//问题不存在
				}
			}else if(module.equals(50)){//答案
				Answer answer = answerService.findByAnswerId(Long.parseLong(parameterId.trim()));
				if(answer != null && answer.getStatus().equals(20)){
					report.setParameterId(parameterId.trim());
					report.setExtraParameterId(answer.getQuestionId().toString());
				}else{
					error.put("report", ErrorView._4130.name());//答案不存在
				}
			}else if(module.equals(60)){//答案回复
				AnswerReply answerReply = answerService.findReplyByReplyId(Long.parseLong(parameterId.trim()));
				if(answerReply != null && answerReply.getStatus().equals(20)){
					report.setParameterId(parameterId.trim());
					report.setExtraParameterId(answerReply.getQuestionId()+"-"+answerReply.getAnswerId());
				}else{
					error.put("report", ErrorView._4140.name());//答案回复不存在
				}
			}
			//查询用户是否有本模块的待处理状态举报
			boolean isReport = reportService.findByCondition(accessUser.getUserName(),parameterId.trim(), module, 10);
			if(isReport){
				error.put("report", ErrorView._4150.name());//您之前对此内容的举报还在处理中，请不要重复举报
			}
			
		}else{
			error.put("report", ErrorView._4060.name());//参数Id不能为空
		}
		
		
		String format_reason = "";
		
		if(reportTypeId != null && !"".equals(reportTypeId.trim())){
			ReportType reportType = reportTypeService.findById(reportTypeId.trim());
			if(reportType != null){
				if(reportType.getChildNodeNumber() == 0 && reportType.getGiveReason()){
					if(reason != null && !"".equals(reason.trim())){
						format_reason = textFilterManage.filterText(reason.trim());
					}else{
						error.put("reason", ErrorView._4030.name());//请填写理由
					}
				}
				
			}else{
				error.put("reportTypeId", ErrorView._4020.name());//举报分类不存在
			}
		}else{
			error.put("reportTypeId", ErrorView._4010.name());//请选择举报分类
		}
		
		
		//如果全局不允许提交问题
		if(systemSetting.isAllowReport() == false){
			error.put("report", ErrorView._4040.name());//不允许提交举报
		}
		
		DateTime dateTime = new DateTime();     
		String date = dateTime.toString("yyyy-MM-dd");
		List<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();
		
		if(error.size() == 0 && format_reason != null && !"".equals(format_reason.trim()) && systemSetting.getReportMaxImageUpload() >0 && imageFile != null && imageFile.length <= systemSetting.getReportMaxImageUpload()){
			for(MultipartFile image : imageFile) {
				if(!image.isEmpty()){
					//验证文件类型
					List<String> formatList = new ArrayList<String>();
					formatList.add("gif");
					formatList.add("jpg");
					formatList.add("jpeg");
					formatList.add("bmp");
					formatList.add("png");
					boolean authentication = FileUtil.validateFileSuffix(image.getOriginalFilename(),formatList);
					if(authentication){
						//取得文件后缀		
						String ext = FileUtil.getExtension(image.getOriginalFilename());
						ext = HtmlEscape.escape(ext);
						//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
						String pathDir = "file"+File.separator+"report"+File.separator + date +File.separator;;
						//构建文件名称
						String fileName = UUIDUtil.getUUID32()+ "." + ext;
					
						imageInfoList.add(new ImageInfo("file/report/"+ date +"/",fileName));
						
						//生成文件保存目录
						FileUtil.createFolder(pathDir);
						 
						//生成锁文件名称
						String lockFileName = date +"_"+fileName;
						//添加文件锁
						fileManage.addLock("file"+File.separator+"report"+File.separator+"lock"+File.separator,lockFileName);
						
						//保存文件
						fileManage.writeFile(pathDir, fileName,image.getBytes());
						
					}else{
						error.put("imageFile", ErrorView._4050.name());//图片格式错误
					}	
				}
			}
			
		}
		
		report.setUserName(accessUser.getUserName());
		
		report.setModule(module);
		report.setReportTypeId(reportTypeId);
		report.setReason(format_reason);
		report.setPostTime(dateTime.toDate());
		report.setStatus(10);
		report.setIp(IpAddress.getClientIpAddress(request));
		if(imageInfoList != null && imageInfoList.size() >0){
			report.setImageData(JsonUtils.toJSONString(imageInfoList));
		}
		
	
		
		if(error.size() == 0){
			//保存举报
			reportService.saveReport(report);
			
			//统计每分钟原来提交次数
			Integer original = settingManage.getSubmitQuantity("report", accessUser.getUserName());
    		if(original != null){
    			settingManage.addSubmitQuantity("report", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
    		}else{
    			settingManage.addSubmitQuantity("report", accessUser.getUserName(),1);//刷新每分钟原来提交次数
    		}
    		
    		//删除图片锁
    		if(imageInfoList != null && imageInfoList.size() >0){
    			for(ImageInfo imageInfo : imageInfoList){
    				fileManage.deleteLock("file"+File.separator+"report"+File.separator+"lock"+File.separator,date +"_"+imageInfo.getName());
    			}
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
				redirectAttrs.addFlashAttribute("report", report);
				

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
				model.addAttribute("message", "保存举报成功");
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
