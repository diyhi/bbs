package cms.web.action.common;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import cms.bean.ErrorView;
import cms.bean.like.AnswerLike;
import cms.bean.like.AnswerReplyLike;
import cms.bean.like.CommentLike;
import cms.bean.like.CommentReplyLike;
import cms.bean.like.Like;
import cms.bean.like.QuestionLike;
import cms.bean.like.TopicLike;
import cms.bean.message.Remind;
import cms.bean.question.Answer;
import cms.bean.question.AnswerReply;
import cms.bean.question.Question;
import cms.bean.setting.SystemSetting;
import cms.bean.topic.Comment;
import cms.bean.topic.Reply;
import cms.bean.topic.Topic;
import cms.bean.user.AccessUser;
import cms.bean.user.ResourceEnum;
import cms.bean.user.User;
import cms.service.like.LikeService;
import cms.service.message.RemindService;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.utils.Base64;
import cms.utils.JsonUtils;
import cms.utils.RefererCompare;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.CSRFTokenManage;
import cms.web.action.like.LikeManage;
import cms.web.action.message.RemindManage;
import cms.web.action.question.AnswerManage;
import cms.web.action.question.QuestionManage;
import cms.web.action.topic.CommentManage;
import cms.web.action.topic.HotTopicManage;
import cms.web.action.topic.TopicManage;
import cms.web.action.user.RoleAnnotation;
import cms.web.action.user.UserManage;
import cms.web.taglib.Configuration;

/**
 * 点赞接收表单
 *
 */
@Controller
@RequestMapping("user/control/like") 
public class LikeFormAction {
	@Resource TemplateService templateService;
	
	@Resource RemindService remindService;
	@Resource RemindManage remindManage;
	@Resource UserManage userManage;
	@Resource LikeService likeService;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource LikeManage likeManage;
	
	@Resource CSRFTokenManage csrfTokenManage;
	@Resource TopicManage topicManage;
	@Resource SettingService settingService;
	@Resource HotTopicManage hotTopicManage;
	@Resource CommentManage commentManage;
	@Resource QuestionManage questionManage;
	@Resource AnswerManage answerManage;
	
	
	/**
	 * 点赞   添加
	 * @param model
	 * @param topicId 话题表单Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/add", method=RequestMethod.POST)
	@RoleAnnotation(resourceCode=ResourceEnum._4001000)
	public String add(ModelMap model,Long itemId,Integer module,String token,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
			
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		
		Map<String,String> error = new HashMap<String,String>();
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("itemLike", ErrorView._21.name());//只读模式不允许提交数据
		}
			
		
		//处理CSRF令牌
		csrfTokenManage.processCsrfToken(request, token,error);
				
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	Topic topic = null;
	  	Comment comment = null;
	  	Reply commentReply = null;
	  	
	  	Question question = null;
	  	Answer answer = null;
	  	AnswerReply answerReply = null;
	  	
	  	Date postTime = new Date();
	  	
	  	if(itemId != null && itemId >0L){
	  		if(module != null){
	  			if(module.equals(10)){//话题
	  				topic = topicManage.queryTopicCache(itemId);//查询缓存
	  				
	  				if(topic != null){
	  			  		//话题点赞Id
	  				  	String topicLikeId = likeManage.createItemLikeId(itemId, accessUser.getUserId());
	  				  
	  				  	TopicLike topicLike = likeManage.query_cache_findTopicLikeById(topicLikeId);
	  			  		
	  			  		if(topicLike != null){
	  				  		error.put("itemLike", ErrorView._1720.name());//当前项目已经点赞
	  				  	}
	  				}
	  			}else if(module.equals(20)){//评论
	  				comment = commentManage.query_cache_findByCommentId(itemId);
	  				if(comment != null){
	  					//评论点赞Id
	  				  	String commentLikeId = likeManage.createItemLikeId(itemId, accessUser.getUserId());
	  				  	
	  				  	CommentLike commentLike = likeManage.query_cache_findCommentLikeById(commentLikeId);
	  			  		
	  			  		if(commentLike != null){
	  				  		error.put("itemLike", ErrorView._1720.name());//当前项目已经点赞
	  				  	}
	  				}
	  			}else if(module.equals(30)){//评论回复
	  				commentReply =  commentManage.query_cache_findReplyByReplyId(itemId);
	  				if(commentReply != null){
	  					//评论回复点赞Id
	  				  	String commentReplyLikeId = likeManage.createItemLikeId(itemId, accessUser.getUserId());
	  				  	
	  				  	CommentReplyLike commentReplyLike = likeManage.query_cache_findCommentReplyLikeById(commentReplyLikeId);
	  			  		
	  			  		if(commentReplyLike != null){
	  				  		error.put("itemLike", ErrorView._1720.name());//当前项目已经点赞
	  				  	}
	  				}
	  			}else if(module.equals(40)){//问题
	  				question = questionManage.query_cache_findById(itemId);//查询缓存
	  				
	  				if(question != null){
	  			  		//问题点赞Id
	  				  	String questionLikeId = likeManage.createItemLikeId(itemId, accessUser.getUserId());
	  				  
	  				  	QuestionLike questionLike = likeManage.query_cache_findQuestionLikeById(questionLikeId);
	  			  		
	  			  		if(questionLike != null){
	  				  		error.put("itemLike", ErrorView._1720.name());//当前项目已经点赞
	  				  	}
	  				}
	  			}else if(module.equals(50)){//答案
	  				answer = answerManage.query_cache_findByAnswerId(itemId);
	  				if(answer != null){
	  					//答案点赞Id
	  				  	String answerLikeId = likeManage.createItemLikeId(itemId, accessUser.getUserId());
	  				  	
	  				  	AnswerLike answerLike = likeManage.query_cache_findAnswerLikeById(answerLikeId);
	  			  		
	  			  		if(answerLike != null){
	  				  		error.put("itemLike", ErrorView._1720.name());//当前项目已经点赞
	  				  	}
	  				}
	  			}else if(module.equals(60)){//答案回复
	  				answerReply =  answerManage.query_cache_findReplyByReplyId(itemId);
	  				if(answerReply != null){
	  					//答案回复点赞Id
	  				  	String answerReplyLikeId = likeManage.createItemLikeId(itemId, accessUser.getUserId());
	  				  	
	  				  	AnswerReplyLike answerReplyLike = likeManage.query_cache_findAnswerReplyLikeById(answerReplyLikeId);
	  			  		
	  			  		if(answerReplyLike != null){
	  				  		error.put("itemLike", ErrorView._1720.name());//当前项目已经点赞
	  				  	}
	  				}
	  			}else{
	  				error.put("likeModule", ErrorView._1726.name());//点赞模块不存在
	  			}
	  		}else{
	  			error.put("likeModule", ErrorView._1725.name());//点赞模块不能为空
	  		}
	  	}else{
	  		error.put("itemLike", ErrorView._1710.name());//项目点赞Id不能为空
	  	}
	  	
	  	
	  	
	  	

		if(error.size() == 0){
			Date time = new Date();
			Like like = new Like();
			like.setId(likeManage.createLikeId(accessUser.getUserId()));
			like.setAddtime(time);
			like.setUserName(accessUser.getUserName());
			like.setModule(module);
			
			if(module.equals(10)){//话题
				like.setTopicId(topic.getId());
				
				like.setPostUserName(topic.getUserName());
				
				TopicLike topicLike = new TopicLike();
				String topicLikeId = likeManage.createItemLikeId(topic.getId(), accessUser.getUserId());
				topicLike.setId(topicLikeId);
				topicLike.setAddtime(time);
				topicLike.setTopicId(topic.getId());
				topicLike.setUserName(accessUser.getUserName());
				topicLike.setPostUserName(topic.getUserName());
				
				try {
					//保存点赞
					likeService.saveLike(likeManage.createLikeObject(like),likeManage.createTopicLikeObject(topicLike));
					
					//删除点赞缓存
					likeManage.delete_cache_findTopicLikeById(topicLikeId);
					likeManage.delete_cache_findLikeCountByTopicId(like.getTopicId());
					

					//添加热门话题
					hotTopicManage.addHotTopic(topic);
					
					User _user = userManage.query_cache_findUserByUserName(topic.getUserName());
					if(_user != null && !_user.getId().equals(accessUser.getUserId())){//楼主点赞不发提醒给自己
						
						//提醒楼主
						Remind remind = new Remind();
						remind.setId(remindManage.createRemindId(_user.getId()));
						remind.setReceiverUserId(_user.getId());//接收提醒用户Id
						remind.setSenderUserId(accessUser.getUserId());//发送用户Id
						remind.setTypeCode(70);//70.别人点赞了我的话题
						remind.setSendTimeFormat(new Date().getTime());//发送时间格式化
						remind.setTopicId(topic.getId());//话题Id
						
						Object remind_object = remindManage.createRemindObject(remind);
						remindService.saveRemind(remind_object);
						
						//删除提醒缓存
						remindManage.delete_cache_findUnreadRemindByUserId(_user.getId());
					}
					
				} catch (org.springframework.orm.jpa.JpaSystemException e) {
					error.put("like", ErrorView._1700.name());//重复点赞
					
				}
  			}else if(module.equals(20)){//评论
  				like.setTopicId(comment.getTopicId());
				like.setCommentId(comment.getId());
				
				like.setPostUserName(comment.getUserName());
				
				CommentLike commentLike = new CommentLike();
				String commentLikeId = likeManage.createItemLikeId(comment.getId(), accessUser.getUserId());
				commentLike.setId(commentLikeId);
				commentLike.setAddtime(time);
				commentLike.setTopicId(comment.getTopicId());
				commentLike.setCommentId(comment.getId()); 
				commentLike.setUserName(accessUser.getUserName());
				commentLike.setPostUserName(comment.getUserName());
				
				try {
					//保存点赞
					likeService.saveLike(likeManage.createLikeObject(like),likeManage.createCommentLikeObject(commentLike));
					
					//删除点赞缓存
					likeManage.delete_cache_findCommentLikeById(commentLikeId);
					likeManage.delete_cache_findLikeCountByCommentId(like.getCommentId());
					
					
					if(!comment.getIsStaff()){
						User _user = userManage.query_cache_findUserByUserName(comment.getUserName());
						if(_user != null && !_user.getId().equals(accessUser.getUserId())){//作者回答不发提醒给自己
							
							//提醒作者
							Remind remind = new Remind();
							remind.setId(remindManage.createRemindId(_user.getId()));
							remind.setReceiverUserId(_user.getId());//接收提醒用户Id
							remind.setSenderUserId(accessUser.getUserId());//发送用户Id
							remind.setTypeCode(260);//260.别人点赞了我的评论
							remind.setSendTimeFormat(postTime.getTime());//发送时间格式化
							remind.setTopicId(comment.getTopicId());//话题Id
							remind.setTopicCommentId(comment.getId());//我的话题评论Id
							
							Object remind_object = remindManage.createRemindObject(remind);
							remindService.saveRemind(remind_object);
							
							//删除提醒缓存
							remindManage.delete_cache_findUnreadRemindByUserId(_user.getId());
						}
					}

				} catch (org.springframework.orm.jpa.JpaSystemException e) {
					error.put("like", ErrorView._1700.name());//重复点赞
					
				}
  			}else if(module.equals(30)){//评论回复
  				like.setTopicId(commentReply.getTopicId());
  				like.setCommentId(commentReply.getCommentId());
				like.setCommentReplyId(commentReply.getId());
				like.setPostUserName(commentReply.getUserName());
				
				CommentReplyLike commentReplyLike = new CommentReplyLike();
				String commentReplyLikeId = likeManage.createItemLikeId(commentReply.getId(), accessUser.getUserId());
				commentReplyLike.setId(commentReplyLikeId);
				commentReplyLike.setAddtime(time);
				commentReplyLike.setTopicId(commentReply.getTopicId());
				commentReplyLike.setReplyId(commentReply.getId()); 
				commentReplyLike.setUserName(accessUser.getUserName());
				commentReplyLike.setPostUserName(commentReply.getUserName());
				
				try {
					//保存点赞
					likeService.saveLike(likeManage.createLikeObject(like),likeManage.createCommentReplyLikeObject(commentReplyLike));
					
					//删除点赞缓存
					likeManage.delete_cache_findCommentReplyLikeById(commentReplyLikeId);
					likeManage.delete_cache_findLikeCountByCommentReplyId(like.getCommentReplyId());
					
					if(!commentReply.getIsStaff()){
						User _user = userManage.query_cache_findUserByUserName(commentReply.getUserName());
						if(_user != null 
								&& !_user.getId().equals(accessUser.getUserId())){//不发提醒给自己
							
							Remind remind = new Remind();
							remind.setId(remindManage.createRemindId(_user.getId()));
							remind.setReceiverUserId(_user.getId());//接收提醒用户Id
							remind.setSenderUserId(accessUser.getUserId());//发送用户Id
							remind.setTypeCode(270);//270.别人点赞了我的评论回复
							remind.setSendTimeFormat(postTime.getTime());//发送时间格式化
							remind.setTopicId(commentReply.getTopicId());//话题Id
							remind.setTopicCommentId(commentReply.getCommentId());//我的话题评论Id
							remind.setTopicReplyId(commentReply.getId());//我的话题回复Id
							
							
							Object remind_object = remindManage.createRemindObject(remind);
							remindService.saveRemind(remind_object);
							
							//删除提醒缓存
							remindManage.delete_cache_findUnreadRemindByUserId(_user.getId());
						}
						
					}
					

				} catch (org.springframework.orm.jpa.JpaSystemException e) {
					error.put("like", ErrorView._1700.name());//重复点赞
					
				}
  			}else if(module.equals(40)){//问题
				like.setQuestionId(question.getId());
				
				like.setPostUserName(question.getUserName());
				
				QuestionLike questionLike = new QuestionLike();
				String questionLikeId = likeManage.createItemLikeId(question.getId(), accessUser.getUserId());
				questionLike.setId(questionLikeId);
				questionLike.setAddtime(time);
				questionLike.setQuestionId(question.getId());
				questionLike.setUserName(accessUser.getUserName());
				questionLike.setPostUserName(question.getUserName());
				
				try {
					//保存点赞
					likeService.saveLike(likeManage.createLikeObject(like),likeManage.createQuestionLikeObject(questionLike));
					
					//删除点赞缓存
					likeManage.delete_cache_findQuestionLikeById(questionLikeId);
					likeManage.delete_cache_findLikeCountByQuestionId(like.getQuestionId());
					

					
					User _user = userManage.query_cache_findUserByUserName(question.getUserName());
					if(_user != null && !_user.getId().equals(accessUser.getUserId())){//楼主点赞不发提醒给自己
						
						//提醒楼主
						Remind remind = new Remind();
						remind.setId(remindManage.createRemindId(_user.getId()));
						remind.setReceiverUserId(_user.getId());//接收提醒用户Id
						remind.setSenderUserId(accessUser.getUserId());//发送用户Id
						remind.setTypeCode(280);// 280.别人点赞了我的问题
						remind.setSendTimeFormat(new Date().getTime());//发送时间格式化
						remind.setQuestionId(question.getId());//问题Id
						
						Object remind_object = remindManage.createRemindObject(remind);
						remindService.saveRemind(remind_object);
						
						//删除提醒缓存
						remindManage.delete_cache_findUnreadRemindByUserId(_user.getId());
					}
					
				} catch (org.springframework.orm.jpa.JpaSystemException e) {
					error.put("like", ErrorView._1700.name());//重复点赞
					
				}
  			}else if(module.equals(50)){//答案
  				like.setQuestionId(answer.getQuestionId());
				like.setAnswerId(answer.getId());
				
				like.setPostUserName(answer.getUserName());
				
				AnswerLike answerLike = new AnswerLike();
				String answerLikeId = likeManage.createItemLikeId(answer.getId(), accessUser.getUserId());
				answerLike.setId(answerLikeId);
				answerLike.setAddtime(time);
				answerLike.setQuestionId(answer.getQuestionId());
				answerLike.setAnswerId(answer.getId()); 
				answerLike.setUserName(accessUser.getUserName());
				answerLike.setPostUserName(answer.getUserName());
				
				try {
					//保存点赞
					likeService.saveLike(likeManage.createLikeObject(like),likeManage.createAnswerLikeObject(answerLike));
					
					//删除点赞缓存
					likeManage.delete_cache_findAnswerLikeById(answerLikeId);
					likeManage.delete_cache_findLikeCountByAnswerId(like.getAnswerId());
					
					
					if(!answer.getIsStaff()){
						User _user = userManage.query_cache_findUserByUserName(answer.getUserName());
						if(_user != null && !_user.getId().equals(accessUser.getUserId())){//作者回答不发提醒给自己
							
							//提醒作者
							Remind remind = new Remind();
							remind.setId(remindManage.createRemindId(_user.getId()));
							remind.setReceiverUserId(_user.getId());//接收提醒用户Id
							remind.setSenderUserId(accessUser.getUserId());//发送用户Id
							remind.setTypeCode(290);//290.别人点赞了我的答案
							remind.setSendTimeFormat(postTime.getTime());//发送时间格式化
							remind.setQuestionId(answer.getQuestionId());//问题Id
							remind.setQuestionAnswerId(answer.getId());//我的问题答案Id
							
							Object remind_object = remindManage.createRemindObject(remind);
							remindService.saveRemind(remind_object);
							
							//删除提醒缓存
							remindManage.delete_cache_findUnreadRemindByUserId(_user.getId());
						}
					}

				} catch (org.springframework.orm.jpa.JpaSystemException e) {
					error.put("like", ErrorView._1700.name());//重复点赞
					
				}
  			}else if(module.equals(60)){//答案回复
  				like.setQuestionId(answerReply.getQuestionId());
  				like.setAnswerId(answerReply.getAnswerId());
				like.setAnswerReplyId(answerReply.getId());
				like.setPostUserName(answerReply.getUserName());
				
				AnswerReplyLike answerReplyLike = new AnswerReplyLike();
				String answerReplyLikeId = likeManage.createItemLikeId(answerReply.getId(), accessUser.getUserId());
				answerReplyLike.setId(answerReplyLikeId);
				answerReplyLike.setAddtime(time);
				answerReplyLike.setQuestionId(answerReply.getQuestionId());
				answerReplyLike.setReplyId(answerReply.getId()); 
				answerReplyLike.setUserName(accessUser.getUserName());
				answerReplyLike.setPostUserName(answerReply.getUserName());
				
				try {
					//保存点赞
					likeService.saveLike(likeManage.createLikeObject(like),likeManage.createAnswerReplyLikeObject(answerReplyLike));
					
					//删除点赞缓存
					likeManage.delete_cache_findAnswerReplyLikeById(answerReplyLikeId);
					likeManage.delete_cache_findLikeCountByAnswerReplyId(like.getAnswerReplyId());
					
					if(!answerReply.getIsStaff()){
						User _user = userManage.query_cache_findUserByUserName(answerReply.getUserName());
						if(_user != null 
								&& !_user.getId().equals(accessUser.getUserId())){//不发提醒给自己
							
							Remind remind = new Remind();
							remind.setId(remindManage.createRemindId(_user.getId()));
							remind.setReceiverUserId(_user.getId());//接收提醒用户Id
							remind.setSenderUserId(accessUser.getUserId());//发送用户Id
							remind.setTypeCode(300);//300.别人点赞了我的答案回复 
							remind.setSendTimeFormat(postTime.getTime());//发送时间格式化
							remind.setQuestionId(answerReply.getQuestionId());//问题Id
							remind.setQuestionAnswerId(answerReply.getAnswerId());//我的答案Id
							remind.setQuestionReplyId(answerReply.getId());//我的问题回复Id
							
							
							Object remind_object = remindManage.createRemindObject(remind);
							remindService.saveRemind(remind_object);
							
							//删除提醒缓存
							remindManage.delete_cache_findUnreadRemindByUserId(_user.getId());
						}
						
					}
					

				} catch (org.springframework.orm.jpa.JpaSystemException e) {
					error.put("like", ErrorView._1700.name());//重复点赞
					
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
				model.addAttribute("message", "给话题点赞成功");
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
