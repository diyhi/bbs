package cms.web.action.common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.ErrorView;
import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.favorite.Favorites;
import cms.bean.follow.Follow;
import cms.bean.follow.Follower;
import cms.bean.like.Like;
import cms.bean.membershipCard.MembershipCardOrder;
import cms.bean.message.PrivateMessage;
import cms.bean.message.Remind;
import cms.bean.message.SubscriptionSystemNotify;
import cms.bean.message.SystemNotify;
import cms.bean.message.UnreadMessage;
import cms.bean.payment.PaymentLog;
import cms.bean.question.Answer;
import cms.bean.question.AnswerReply;
import cms.bean.question.Question;
import cms.bean.question.QuestionTag;
import cms.bean.question.QuestionTagAssociation;
import cms.bean.redEnvelope.GiveRedEnvelope;
import cms.bean.redEnvelope.ReceiveRedEnvelope;
import cms.bean.setting.SystemSetting;
import cms.bean.topic.Comment;
import cms.bean.topic.HideTagType;
import cms.bean.topic.Reply;
import cms.bean.topic.Tag;
import cms.bean.topic.Topic;
import cms.bean.topic.TopicUnhide;
import cms.bean.user.AccessUser;
import cms.bean.user.DisableUserName;
import cms.bean.user.FormCaptcha;
import cms.bean.user.PointLog;
import cms.bean.user.RefreshUser;
import cms.bean.user.ResourceEnum;
import cms.bean.user.User;
import cms.bean.user.UserCustom;
import cms.bean.user.UserDynamic;
import cms.bean.user.UserGrade;
import cms.bean.user.UserInputValue;
import cms.bean.user.UserLoginLog;
import cms.bean.user.UserRole;
import cms.bean.user.UserRoleGroup;
import cms.service.favorite.FavoriteService;
import cms.service.follow.FollowService;
import cms.service.like.LikeService;
import cms.service.membershipCard.MembershipCardService;
import cms.service.message.PrivateMessageService;
import cms.service.message.RemindService;
import cms.service.message.SystemNotifyService;
import cms.service.payment.PaymentService;
import cms.service.question.AnswerService;
import cms.service.question.QuestionService;
import cms.service.question.QuestionTagService;
import cms.service.redEnvelope.RedEnvelopeService;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.service.topic.CommentService;
import cms.service.topic.TagService;
import cms.service.topic.TopicService;
import cms.service.user.UserCustomService;
import cms.service.user.UserGradeService;
import cms.service.user.UserRoleService;
import cms.service.user.UserService;
import cms.utils.Base64;
import cms.utils.FileUtil;
import cms.utils.HtmlEscape;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.utils.PathUtil;
import cms.utils.RefererCompare;
import cms.utils.SHA;
import cms.utils.SecureLink;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.CSRFTokenManage;
import cms.web.action.TextFilterManage;
import cms.web.action.favorite.FavoriteManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.follow.FollowManage;
import cms.web.action.follow.FollowerManage;
import cms.web.action.like.LikeManage;
import cms.web.action.mediaProcess.MediaProcessQueueManage;
import cms.web.action.message.PrivateMessageManage;
import cms.web.action.message.RemindManage;
import cms.web.action.message.SubscriptionSystemNotifyManage;
import cms.web.action.message.SystemNotifyManage;
import cms.web.action.question.AnswerManage;
import cms.web.action.question.QuestionManage;
import cms.web.action.redEnvelope.RedEnvelopeManage;
import cms.web.action.setting.SettingManage;
import cms.web.action.sms.SmsManage;
import cms.web.action.topic.CommentManage;
import cms.web.action.topic.TopicManage;
import cms.web.action.user.RoleAnnotation;
import cms.web.action.user.UserManage;
import cms.web.action.user.UserRoleManage;
import cms.web.taglib.Configuration;

/**
 * 后台管理
 *
 */
@Controller
public class HomeManageAction {

	@Resource TemplateService templateService;
	@Resource UserService userService;
	@Resource UserGradeService userGradeService;
	
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource FileManage fileManage;
	
	@Resource CaptchaManage captchaManage;
	
	
	@Resource SettingService settingService;
	@Resource UserCustomService userCustomService;
	@Resource CommentService commentService;
	
	@Resource TagService tagService;
	@Resource TopicService topicService;
	@Resource TextFilterManage textFilterManage;
	
	
	@Resource SettingManage settingManage;
	
	@Resource SmsManage smsManage;
	
	@Resource CSRFTokenManage csrfTokenManage;
	@Resource UserManage userManage;
	@Resource PrivateMessageManage privateMessageManage;
	@Resource PrivateMessageService privateMessageService;
	@Resource SystemNotifyService systemNotifyService;
	@Resource SubscriptionSystemNotifyManage subscriptionSystemNotifyManage;
	@Resource SystemNotifyManage systemNotifyManage;
	@Resource RemindService remindService;
	@Resource TopicManage topicManage;
	@Resource RemindManage remindManage;
	
	@Resource OAuthManage oAuthManage;
	
	
	@Resource FavoriteService favoriteService;
	
	@Resource FavoriteManage favoriteManage;
	
	@Resource CommentManage commentManage;
	
	@Resource LikeService likeService;
	@Resource LikeManage likeManage;
	
	@Resource FollowService followService;
	@Resource FollowManage followManage;
	@Resource FollowerManage followerManage;
	
	@Resource UserRoleManage userRoleManage;
	@Resource PaymentService paymentService;
	@Resource MembershipCardService membershipCardService;
	
	@Resource UserRoleService userRoleService;
	
	@Resource QuestionManage questionManage;
	@Resource AnswerManage answerManage;
	
	@Resource QuestionService questionService;
	@Resource QuestionTagService questionTagService;
	@Resource AnswerService answerService;
	@Resource MediaProcessQueueManage mediaProcessQueueManage;
	@Resource RedEnvelopeService redEnvelopeService;
	@Resource RedEnvelopeManage redEnvelopeManage;
	
	
	//?  匹配任何单字符
	//*  匹配0或者任意数量的字符
	//** 匹配0或者更多的目录
	private PathMatcher matcher = new AntPathMatcher(); 
	
	
	/**--------------------------------- 首页 -----------------------------------**/
	/**
	 * 用户中心页
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/home",method=RequestMethod.GET) 
	public String homeUI(ModelMap model,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		

	    Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
	    
	    //获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();

	  	String _userName = accessUser.getUserName();
	  	//是否为会员自身
	  	boolean flag = true;
	  	if(userName != null && !"".equals(userName.trim())){
	  		if(!userName.trim().equals(accessUser.getUserName())){
	  			_userName = userName.trim();
	  			flag = false;
	  		}
	  	}
	  	
	  	
	  	
	  	
	    //获取登录用户
  		User new_user = userManage.query_cache_findUserByUserName(_userName);
  		if(new_user != null){
  			List<UserGrade> userGradeList = userGradeService.findAllGrade_cache();//取得用户所有等级
			if(userGradeList != null && userGradeList.size() >0){
				for(UserGrade userGrade : userGradeList){
					if(new_user.getPoint() >= userGrade.getNeedPoint()){
						new_user.setGradeId(userGrade.getId());
						new_user.setGradeName(userGrade.getName());//将等级值设进等级参数里
						break;
					}
				} 
					
				
			}
			
			List<String> userRoleNameList = userRoleManage.queryUserRoleName(new_user.getUserName());
			
			
      		if(flag){
      			//仅显示指定的字段
    			User viewUser = new User();
    			viewUser.setId(new_user.getId());
    			viewUser.setUserName(new_user.getUserName());//会员用户名
    			viewUser.setNickname(new_user.getNickname());//呢称
    			viewUser.setState(new_user.getState());//用户状态
    			viewUser.setEmail(new_user.getEmail());//邮箱地址
    			viewUser.setIssue(new_user.getIssue());//密码提示问题
    			viewUser.setRegistrationDate(new_user.getRegistrationDate());//注册日期
    			viewUser.setPoint(new_user.getPoint());//当前积分
    			viewUser.setGradeId(new_user.getGradeId());//等级Id
    			viewUser.setGradeName(new_user.getGradeName());//等级名称
    			viewUser.setMobile(new_user.getMobile());//绑定手机
    			viewUser.setRealNameAuthentication(new_user.isRealNameAuthentication());//是否实名认证
    			viewUser.setAvatarPath(new_user.getAvatarPath());//头像路径
    			viewUser.setAvatarName(new_user.getAvatarName());//头像名称
    			
    			if(userRoleNameList != null && userRoleNameList.size() >0){
    				viewUser.setUserRoleNameList(userRoleNameList);//话题允许查看的角色名称集合
    			}
    			
      			model.addAttribute("user", viewUser);
          		returnValue.put("user", viewUser);
      		}else{//如果不是登录会员自身，则仅显示指定字段
      			User other_user = new User();
      			other_user.setId(new_user.getId());//Id
      			other_user.setUserName(new_user.getUserName());//会员用户名
      			other_user.setNickname(new_user.getNickname());//呢称
      			other_user.setState(new_user.getState());//用户状态
      			other_user.setPoint(new_user.getPoint());//当前积分
      			other_user.setGradeId(new_user.getGradeId());//等级Id
      			other_user.setGradeName(new_user.getGradeName());//等级名称
      			other_user.setAvatarPath(new_user.getAvatarPath());//头像路径
      			other_user.setAvatarName(new_user.getAvatarName());//头像名称
      			if(userRoleNameList != null && userRoleNameList.size() >0){
      				other_user.setUserRoleNameList(userRoleNameList);//话题允许查看的角色名称集合
    			}
      			model.addAttribute("user", other_user);
          		returnValue.put("user", other_user);
      		}
      		
      		
      	}
     	if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
			return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/home";	
		}
	}	
	
	/**
	 * 我的话题列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/topicList",method=RequestMethod.GET) 
	public String topicListUI(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		
		//调用分页算法代码
		PageView<Topic> pageView = new PageView<Topic>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		 //获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
    	
    		
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		jpql.append(" and o.userName=?"+ (params.size()+1));
		params.add(accessUser.getUserName());
		
		jpql.append(" and o.status<?"+ (params.size()+1));
		params.add(100);
		
		jpql.append(" and o.isStaff=?"+ (params.size()+1));
		params.add(false);
		
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
	
		
		QueryResult<Topic> qr = topicService.getScrollData(Topic.class, firstindex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);
		if(qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(Topic o :qr.getResultlist()){
    			o.setIpAddress(null);//IP地址不显示
    		}
			List<Tag> tagList = tagService.findAllTag_cache();
			if(tagList != null && tagList.size() >0){
				for(Topic topic : qr.getResultlist()){
					for(Tag tag : tagList){
						if(topic.getTagId().equals(tag.getId())){
							topic.setTagName(tag.getName());
							break;
						}
					}
					
				}
			}	
		}
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);		
    	
		model.addAttribute("pageView", pageView);
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
		    return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/topicList";	
		}
	}
	
	/**
	 * 我的评论列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/commentList",method=RequestMethod.GET) 
	public String commentListUI(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		
		//调用分页算法代码
		PageView<Comment> pageView = new PageView<Comment>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
    		
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		jpql.append(" and o.userName=?"+ (params.size()+1));
		params.add(accessUser.getUserName());
		
		jpql.append(" and o.isStaff=?"+ (params.size()+1));
		params.add(false);
		
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
	
		
		QueryResult<Comment> qr = commentService.getScrollData(Comment.class, firstindex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);
		if(qr.getResultlist() != null && qr.getResultlist().size() >0){
			List<Long> topicIdList = new ArrayList<Long>();
			for(Comment o :qr.getResultlist()){
    			o.setIpAddress(null);//IP地址不显示
    			
    			o.setContent(textFilterManage.filterText(o.getContent()));
    			if(!topicIdList.contains(o.getTopicId())){
    				topicIdList.add(o.getTopicId());
    			}
    		}
			List<Topic> topicList = topicService.findTitleByIdList(topicIdList);
			if(topicList != null && topicList.size() >0){
				for(Comment o :qr.getResultlist()){
					for(Topic topic : topicList){
						if(topic.getId().equals(o.getTopicId())){
							o.setTopicTitle(topic.getTitle());
							break;
						}
					}
					
				}
			}	
		}
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);		
    	
		model.addAttribute("pageView", pageView);
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
		    return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/commentList";	
		}
	}
	
	/**
	 * 我的回复列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/replyList",method=RequestMethod.GET) 
	public String replyListUI(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		
		//调用分页算法代码
		PageView<Reply> pageView = new PageView<Reply>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
    		
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		jpql.append(" and o.userName=?"+ (params.size()+1));
		params.add(accessUser.getUserName());
		
		jpql.append(" and o.isStaff=?"+ (params.size()+1));
		params.add(false);
		
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
	
		
		QueryResult<Reply> qr = topicService.getScrollData(Reply.class, firstindex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);
		
		if(qr.getResultlist() != null && qr.getResultlist().size() >0){
			List<Long> topicIdList = new ArrayList<Long>();
			for(Reply o :qr.getResultlist()){
    			o.setIpAddress(null);//IP地址不显示
    			
    			if(!topicIdList.contains(o.getTopicId())){
    				topicIdList.add(o.getTopicId());
    			}
    		}
			List<Topic> topicList = topicService.findTitleByIdList(topicIdList);
			if(topicList != null && topicList.size() >0){
				for(Reply o :qr.getResultlist()){
					for(Topic topic : topicList){
						if(topic.getId().equals(o.getTopicId())){
							o.setTopicTitle(topic.getTitle());
							break;
						}
					}
					
				}
			}	
		}
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);		
    	
		model.addAttribute("pageView", pageView);
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
		    return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/replyList";	
		}
	}
	
	
	/**
	 * 积分
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/point",method=RequestMethod.GET) 
	public String pointUI(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值

		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		    		
    	
    	//调用分页算法代码
		PageView<PointLog> pageView = new PageView<PointLog>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		
		User user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
		if(user != null){
			QueryResult<PointLog> qr =  userService.findPointLogPage(user.getId(),user.getUserName(),firstIndex, pageView.getMaxresult());
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			request.setAttribute("pageView", pageView);
			returnValue.put("pageView", pageView);
			List<UserGrade> userGradeList = userGradeService.findAllGrade_cache();//取得用户所有等级
			if(userGradeList != null && userGradeList.size() >0){
				for(UserGrade userGrade : userGradeList){
					if(user.getPoint() >= userGrade.getNeedPoint()){
						user.setGradeId(userGrade.getId());
						user.setGradeName(userGrade.getName());//将等级值设进等级参数里
						break;
					}
				} 
			}
			
			//仅显示指定的字段
			User viewUser = new User();
			viewUser.setId(user.getId());
			viewUser.setUserName(user.getUserName());//会员用户名
			viewUser.setNickname(user.getNickname());//呢称
			viewUser.setEmail(user.getEmail());//邮箱地址
			viewUser.setIssue(user.getIssue());//密码提示问题
			viewUser.setRegistrationDate(user.getRegistrationDate());//注册日期
			viewUser.setPoint(user.getPoint());//当前积分
			viewUser.setGradeId(user.getGradeId());//等级Id
			viewUser.setGradeName(user.getGradeName());//将等级值设进等级参数里
			
			
			model.addAttribute("user",viewUser);
			returnValue.put("user",viewUser);
		}
		
		
		if(isAjax == true){
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
			
			return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/point";	
		}
		
		
	}
	
	
	

	
	/**----------------------------------- 修改用户 ----------------------------------**/
	/**
	 * 会员修改 显示
	 * @param encryptionData 加密数据
	 * @param secretKey 密钥
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/editUser",method=RequestMethod.GET) 
	public String editUserUI(ModelMap model,User formbean,String jumpUrl,
			String encryptionData,String secretKey,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
		 
		String dirName = templateService.findTemplateDir_cache();
		
		String accessPath = accessSourceDeviceManage.accessDevices(request);
	    
	    
	    
	    //判断是否错误回显
	  	boolean errorDisplay = false; 	
	  	if(model != null && model.get("error") != null){
	    	errorDisplay = true;
	    }

	  	//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  		
		User user = userService.findUserByUserName(accessUser.getUserName());
		List<UserGrade> userGradeList = userGradeService.findAllGrade();//取得用户所有等级
		if(userGradeList != null && userGradeList.size() >0){
			for(UserGrade userGrade : userGradeList){
				if(user.getPoint() >= userGrade.getNeedPoint()){
					user.setGradeId(userGrade.getId());
					user.setGradeName(userGrade.getName());//将等级值设进等级参数里
					break;
				}
			} 
		}
		
		List<UserCustom> userCustomList = userCustomService.findAllUserCustom_cache();
		if(userCustomList != null && userCustomList.size() >0){		
			Iterator <UserCustom> it = userCustomList.iterator();  
			while(it.hasNext()){  
				UserCustom userCustom = it.next();
				if(userCustom.isVisible() == false){//如果不显示
					it.remove();  
					continue;
				}
				if(userCustom.getValue() != null && !"".equals(userCustom.getValue().trim())){
					LinkedHashMap<String,String> itemValue = JsonUtils.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});
					userCustom.setItemValue(itemValue);
				}
				
			}
		}
		
		List<UserInputValue> userInputValueList= userCustomService.findUserInputValueByUserName(user.getId());
		if(userInputValueList != null && userInputValueList.size() >0){
			for(UserCustom userCustom : userCustomList){
				for(UserInputValue userInputValue : userInputValueList){
					if(userCustom.getId().equals(userInputValue.getUserCustomId())){
						userCustom.addUserInputValue(userInputValue);
					}
				}
			}
		}
		
		if(errorDisplay == false){//如果不是错误回显
			model.addAttribute("userCustomList", userCustomList);
		}
		
		
		//仅显示指定的字段
		User viewUser = new User();
		viewUser.setId(user.getId());
		viewUser.setUserName(user.getUserName());//会员用户名
		viewUser.setNickname(user.getNickname());//呢称
		viewUser.setAllowUserDynamic(user.getAllowUserDynamic());//是否允许显示用户动态
		viewUser.setEmail(user.getEmail());//邮箱地址
		viewUser.setIssue(user.getIssue());//密码提示问题
		viewUser.setRegistrationDate(user.getRegistrationDate());//注册日期
		viewUser.setPoint(user.getPoint());//当前积分
		viewUser.setGradeId(user.getGradeId());//等级Id
		viewUser.setGradeName(user.getGradeName());//将等级值设进等级参数里
		viewUser.setAvatarPath(user.getAvatarPath());//头像路径
		viewUser.setAvatarName(user.getAvatarName());//头像名称
		viewUser.setType(user.getType());
		viewUser.setPlatformUserId(user.getPlatformUserId());
		
		model.addAttribute("user",viewUser);

		
		//有效的用户角色
		List<UserRole> validUserRoleList = new ArrayList<UserRole>();
		
		//查询所有角色
		List<UserRole> userRoleList = userRoleService.findAllRole_cache();
		if(userRoleList != null && userRoleList.size() >0){
			List<UserRoleGroup> userRoleGroupList = userRoleManage.query_cache_findRoleGroupByUserName(user.getUserName());
			
			
			for(UserRole userRole : userRoleList){
				if(userRole.getDefaultRole()){//如果是默认角色
					continue;
				}else{
					//默认时间  年,月,日,时,分,秒,毫秒    
	                DateTime defaultTime = new DateTime(2999, 1, 1, 0, 0);// 2999年1月1日0点0分
	                Date validPeriodEnd = defaultTime.toDate();
					userRole.setValidPeriodEnd(validPeriodEnd);
				}
				
				if(userRoleGroupList != null && userRoleGroupList.size() >0){
					for(UserRoleGroup userRoleGroup : userRoleGroupList){
						if(userRole.getId().equals(userRoleGroup.getUserRoleId())){
							UserRole validUserRole = new UserRole();
							validUserRole.setId(userRole.getId());
							validUserRole.setName(userRole.getName());
							validUserRole.setValidPeriodEnd(userRoleGroup.getValidPeriodEnd());
							validUserRoleList.add(validUserRole);
						}
					}
				}
			}
		}
		model.addAttribute("userRoleList", validUserRoleList);
		
		
		
		
		
		if(isAjax){
			returnValue.put("user", viewUser);
			returnValue.put("userCustomList", userCustomList);
			returnValue.put("userRoleList", validUserRoleList);

			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			return "templates/"+dirName+"/"+accessPath+"/editUser";	
		}
		
	}
	
	
	/**
	 * 会员修改 
	 * @param oldPassword 旧密码
	 * @param token 令牌
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/editUser",method=RequestMethod.POST) 
	public String editUser(ModelMap model,User formbean,String jumpUrl,
			String oldPassword,String token,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		
		
	   
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,String> error = new HashMap<String,String>();//错误
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("user", ErrorView._21.name());//只读模式不允许提交数据
		}
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		User user = userService.findUserByUserName(accessUser.getUserName());
		

		User new_user = new User();
		
		
		//判断令牌
		if(token != null && !"".equals(token.trim())){	
			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
				if(!token_sessionid.equals(token)){
					error.put("token", ErrorView._13.name());
				}
			}else{
				error.put("token", ErrorView._12.name());
			}
		}else{
			error.put("token", ErrorView._11.name());
		}
		
		
		List<UserCustom> userCustomList = userCustomService.findAllUserCustom_cache();

		
		//用户自定义注册功能项参数
		if(userCustomList != null && userCustomList.size() >0){	
			for(UserCustom userCustom : userCustomList){
				//用户自定义注册功能项用户输入值集合
				List<UserInputValue> userInputValueList = new ArrayList<UserInputValue>();
				
				if(userCustom.isVisible() == true){//显示
					if(userCustom.getValue() != null && !"".equals(userCustom.getValue().trim())){
						LinkedHashMap<String,String> itemValue = JsonUtils.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});
						userCustom.setItemValue(itemValue);
					}
					if(userCustom.getChooseType().equals(1)){//1.输入框
						
						String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());
						if(userCustom_value != null && !"".equals(userCustom_value.trim())){
							UserInputValue userInputValue = new UserInputValue();
							userInputValue.setUserCustomId(userCustom.getId());
							userInputValue.setContent(userCustom_value.trim());
							userInputValueList.add(userInputValue);
							
							
							
							if(userCustom.getMaxlength() != null && userCustom_value.length() > userCustom.getMaxlength()){
								error.put("userCustom_"+userCustom.getId(), "长度超过"+userCustom_value.length()+"个字符");
							}
							
							int fieldFilter = userCustom.getFieldFilter();//字段过滤方式    0.无  1.只允许输入数字  2.只允许输入字母  3.只允许输入数字和字母  4.只允许输入汉字  5.正则表达式过滤
							switch(fieldFilter){
								case 1 : //输入数字
									if(Verification.isPositiveIntegerZero(userCustom_value.trim()) == false){
										error.put("userCustom_"+userCustom.getId(),ErrorView._804.name());//只允许输入数字
									}
								  break; 
								case 2 : //输入字母
									if(Verification.isLetter(userCustom_value.trim()) == false){
										error.put("userCustom_"+userCustom.getId(), ErrorView._805.name());//只允许输入字母
									}
								  break;
								case 3 : //只能输入数字和字母
									if(Verification.isNumericLetters(userCustom_value.trim()) == false){
										error.put("userCustom_"+userCustom.getId(), ErrorView._806.name());//只允许输入数字和字母
									}
								  break;
								case 4 : //只能输入汉字
									if(Verification.isChineseCharacter(userCustom_value.trim()) == false){
										error.put("userCustom_"+userCustom.getId(), ErrorView._807.name());//只允许输入汉字
									}
								  break;
								case 5 : //正则表达式过滤
									if(userCustom_value.trim().matches(userCustom.getRegular())== false){
										error.put("userCustom_"+userCustom.getId(), ErrorView._808.name());//输入错误
									}
								  break;
							//	default:
							}
						}else{
							if(userCustom.isRequired() == true){//是否必填	
								error.put("userCustom_"+userCustom.getId(), ErrorView._809.name());//必填项
							}
							
						}	
						userCustom.setUserInputValueList(userInputValueList);
					}else if(userCustom.getChooseType().equals(2)){//2.单选框
						
						String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());
	
						if(userCustom_value != null && !"".equals(userCustom_value.trim())){
							
							String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
							if(itemValue != null ){
								UserInputValue userInputValue = new UserInputValue();
								userInputValue.setUserCustomId(userCustom.getId());
								userInputValue.setOptions(userCustom_value.trim());
								userInputValueList.add(userInputValue);
								
							}else{
								if(userCustom.isRequired() == true){//是否必填	
									error.put("userCustom_"+userCustom.getId(), ErrorView._809.name());//必填项
								}
							}
							
						}else{
							if(userCustom.isRequired() == true){//是否必填	
								error.put("userCustom_"+userCustom.getId(), ErrorView._809.name());//必填项
							}
						}
						userCustom.setUserInputValueList(userInputValueList);	
						
					}else if(userCustom.getChooseType().equals(3)){//3.多选框
						
						String[] userCustom_value_arr = request.getParameterValues("userCustom_"+userCustom.getId());
	
						if(userCustom_value_arr != null && userCustom_value_arr.length >0){
							for(String userCustom_value : userCustom_value_arr){
								
								if(userCustom_value != null && !"".equals(userCustom_value.trim())){
									
									String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
									if(itemValue != null ){
										UserInputValue userInputValue = new UserInputValue();
										userInputValue.setUserCustomId(userCustom.getId());
										userInputValue.setOptions(userCustom_value.trim());
										userInputValueList.add(userInputValue);
									}
									
									
								}
							}
						}else{
							if(userCustom.isRequired() == true){//是否必填	
								error.put("userCustom_"+userCustom.getId(), ErrorView._809.name());//必填项
							}
						}
						if(userInputValueList.size() == 0){
							if(userCustom.isRequired() == true){//是否必填	
								error.put("userCustom_"+userCustom.getId(), ErrorView._809.name());//必填项
							}
						}
						userCustom.setUserInputValueList(userInputValueList);	
						
					}else if(userCustom.getChooseType().equals(4)){//4.下拉列表
						String[] userCustom_value_arr = request.getParameterValues("userCustom_"+userCustom.getId());

						if(userCustom_value_arr != null && userCustom_value_arr.length >0){
							for(String userCustom_value : userCustom_value_arr){
								
								if(userCustom_value != null && !"".equals(userCustom_value.trim())){
									
									String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
									if(itemValue != null ){
										UserInputValue userInputValue = new UserInputValue();
										userInputValue.setUserCustomId(userCustom.getId());
										userInputValue.setOptions(userCustom_value.trim());
										userInputValueList.add(userInputValue);
									}
									
									
								}
							}
						}else{
							if(userCustom.isRequired() == true){//是否必填	
								error.put("userCustom_"+userCustom.getId(), ErrorView._809.name());//必填项
							}
						}
						if(userInputValueList.size() == 0){
							if(userCustom.isRequired() == true){//是否必填	
								error.put("userCustom_"+userCustom.getId(), ErrorView._809.name());//必填项
							}
						}
						userCustom.setUserInputValueList(userInputValueList);	
					}else if(userCustom.getChooseType().equals(5)){// 5.文本域
						String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());

						if(userCustom_value != null && !"".equals(userCustom_value.trim())){
							UserInputValue userInputValue = new UserInputValue();
							userInputValue.setUserCustomId(userCustom.getId());
							userInputValue.setContent(userCustom_value);
							userInputValueList.add(userInputValue);
							
						}else{
							if(userCustom.isRequired() == true){//是否必填	
								error.put("userCustom_"+userCustom.getId(), ErrorView._809.name());//必填项
							}
						}
						userCustom.setUserInputValueList(userInputValueList);
					}
				}
			}
		}
		
		//验证数据
		if(user.getType() <=30){//本地账户才允许设置的参数
			if(formbean.getPassword() != null && !"".equals(formbean.getPassword().trim())){
				if(formbean.getPassword().trim().length() != 64){//判断是否是64位SHA256
					error.put("password", ErrorView._801.name());//密码长度错误
				}else{
					new_user.setPassword(SHA.sha256Hex(formbean.getPassword().trim()+"["+user.getSalt()+"]"));
					new_user.setSecurityDigest(new Date().getTime());
					
					
				}
				
				//比较旧密码
				if(oldPassword != null && !"".equals(oldPassword.trim())){
					if(!user.getPassword().equals(SHA.sha256Hex(oldPassword.trim()+"["+user.getSalt()+"]"))){
						error.put("oldPassword", ErrorView._802.name());//旧密码错误
					}
				}else{
					error.put("oldPassword", ErrorView._803.name());//旧密码不能为空
				}
			}else{
				new_user.setPassword(user.getPassword());
				new_user.setSecurityDigest(user.getSecurityDigest());
			}
		}else{
			new_user.setSecurityDigest(user.getSecurityDigest());
		}
		
		
		if(formbean.getNickname() != null && !"".equals(formbean.getNickname().trim())){
			if(user.getNickname() == null || "".equals(user.getNickname().trim())){
				if(formbean.getNickname().length()>15){
					error.put("nickname", ErrorView._829.name());//呢称不能超过15个字符
				}
				User u = userService.findUserByNickname(formbean.getNickname().trim());
				if(u != null){
					error.put("nickname", ErrorView._830.name());//该呢称已存在
				}
			}else{
				error.put("nickname", ErrorView._831.name());//不允许修改呢称
			}
			
			User u1 = userService.findUserByUserName(formbean.getNickname().trim());
			if(u1 != null){
				error.put("nickname", ErrorView._833.name());//呢称不能和其他用户名相同
			}
			
			List<DisableUserName> disableUserNameList = userService.findAllDisableUserName_cache();
			if(disableUserNameList != null && disableUserNameList.size() >0){
				for(DisableUserName disableUserName : disableUserNameList){
					boolean flag = matcher.match(disableUserName.getName(), formbean.getNickname().trim());  //参数一: ant匹配风格   参数二:输入URL
					if(flag){
						error.put("nickname", ErrorView._832.name());//该呢称不允许使用
					}
				}
			}

			User u = userService.findUserByNickname(formbean.getNickname().trim());
			if(u != null){
				if(user.getNickname() == null || "".equals(user.getNickname()) || !formbean.getNickname().trim().equals(user.getNickname())){
					error.put("nickname",ErrorView._830.name());//该呢称已存在
				}
				
			}
			new_user.setNickname(formbean.getNickname().trim());
		}

		new_user.setId(user.getId());
		new_user.setUserName(user.getUserName());
		if(new_user.getNickname() == null || "".equals(new_user.getNickname().trim())){
			new_user.setNickname(user.getNickname());
		}
		if(formbean.getAllowUserDynamic() != null){
			new_user.setAllowUserDynamic(formbean.getAllowUserDynamic());//允许显示用户动态 
		}
		
		
		new_user.setUserVersion(user.getUserVersion());
		
		
		//提交
		if(error.size() == 0){
			List<UserInputValue> userInputValueList= userCustomService.findUserInputValueByUserName(user.getId());
			
			//添加注册功能项用户输入值集合
			List<UserInputValue> add_userInputValue = new ArrayList<UserInputValue>();
			//删除注册功能项用户输入值Id集合
			List<Long> delete_userInputValueIdList = new ArrayList<Long>();
			if(userCustomList != null && userCustomList.size() >0){	
				for(UserCustom userCustom : userCustomList){
					List<UserInputValue> new_userInputValueList = userCustom.getUserInputValueList();
					if(new_userInputValueList != null && new_userInputValueList.size() >0){
						A:for(UserInputValue new_userInputValue : new_userInputValueList){
							if(userInputValueList != null && userInputValueList.size() >0){
								for(UserInputValue old_userInputValue : userInputValueList){
									if(new_userInputValue.getUserCustomId().equals(old_userInputValue.getUserCustomId())){
										
										
										if(new_userInputValue.getOptions().equals("-1")){
											
											if(new_userInputValue.getContent() == null){
												if(old_userInputValue.getContent() == null){
													userInputValueList.remove(old_userInputValue);
													continue A;
												}
											}else{
												if(new_userInputValue.getContent().equals(old_userInputValue.getContent())){
													userInputValueList.remove(old_userInputValue);
													continue A;
												}
											}
											
										}else{
											if(new_userInputValue.getOptions().equals(old_userInputValue.getOptions())){
												userInputValueList.remove(old_userInputValue);
												continue A;
											}
										}
									}	
								}
							}
							add_userInputValue.add(new_userInputValue);
						}
					}
				}
			}
			if(userInputValueList != null && userInputValueList.size() >0){
				for(UserInputValue old_userInputValue : userInputValueList){
					delete_userInputValueIdList.add(old_userInputValue.getId());
					
				}
			}
			
			
			int i = userService.updateUser2(new_user,add_userInputValue,delete_userInputValueIdList);
			userManage.delete_userState(new_user.getUserName());
			//删除缓存
			userManage.delete_cache_findUserById(new_user.getId());
			userManage.delete_cache_findUserByUserName(new_user.getUserName());

			if(i == 0){
				error.put("user", ErrorView._810.name());//修改用户失败
			}else{
				//有修改密码的情况要执行更新OAuth令牌操作
				if((formbean.getPassword() != null && !"".equals(formbean.getPassword().trim())) ||
						(formbean.getNickname() != null && !"".equals(formbean.getNickname().trim()))){

					User _user = userService.findUserByUserName(accessUser.getUserName());
					
					String _accessToken = WebUtil.getCookieByName(request, "cms_accessToken");
					if(_accessToken != null && !"".equals(_accessToken.trim())){
						//删除访问令牌
		    			oAuthManage.deleteAccessToken(_accessToken.trim());
					}
					String _refreshToken = WebUtil.getCookieByName(request, "cms_refreshToken");
					if(_refreshToken != null && !"".equals(_refreshToken.trim())){
						//删除刷新令牌
		    			oAuthManage.deleteRefreshToken(_refreshToken);
					}
					
					//访问令牌
					String accessToken = UUIDUtil.getUUID32();
					//刷新令牌
					String refreshToken = UUIDUtil.getUUID32();

					//获取cookie的存活时间
					int maxAge = WebUtil.getCookieMaxAge(request, "cms_accessToken"); //存放时间 单位/秒
					boolean rememberMe = maxAge >0 ? true :false;
					
					
					oAuthManage.addAccessToken(accessToken, new AccessUser(_user.getId(),_user.getUserName(),_user.getNickname(),_user.getAvatarPath(),_user.getAvatarName(),_user.getSecurityDigest(),rememberMe,accessUser.getOpenId()));
					oAuthManage.addRefreshToken(refreshToken, new RefreshUser(accessToken,_user.getId(),_user.getUserName(),_user.getNickname(),_user.getAvatarPath(),_user.getAvatarName(),_user.getSecurityDigest(),rememberMe,accessUser.getOpenId()));
					
					//第三方openId
					oAuthManage.addOpenId(accessUser.getOpenId(),refreshToken);
					
					//将访问令牌添加到Cookie
					WebUtil.addCookie(response, "cms_accessToken", accessToken, maxAge);
					//将刷新令牌添加到Cookie
					WebUtil.addCookie(response, "cms_refreshToken", refreshToken, maxAge);
					AccessUserThreadLocal.set(new AccessUser(_user.getId(),_user.getUserName(),_user.getNickname(),_user.getAvatarPath(),_user.getAvatarName(),_user.getSecurityDigest(),rememberMe,accessUser.getOpenId()));
					
					
					
					
					
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
				redirectAttrs.addFlashAttribute("userCustomList", userCustomList);
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
				model.addAttribute("message", "修改用户成功");
				String referer = request.getHeader("referer");
				if(RefererCompare.compare(request, "login")){//如果是登录页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				model.addAttribute("urlAddress", referer);
				String dirName = templateService.findTemplateDir_cache();
				
				String accessPath = accessSourceDeviceManage.accessDevices(request);
				return "templates/"+dirName+"/"+accessPath+"/jump";	
			}
		}
		
		
		
		
	}
	

	
	/**
	 * 更新头像
	 * @param model
	 * @param imgFile
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/updateAvatar",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	@RoleAnnotation(resourceCode=ResourceEnum._2001000)
	public String updateAvatar(ModelMap model,MultipartFile imgFile,
			HttpServletRequest request,HttpServletResponse response)
			throws Exception {	
				
				
		Map<String,String> error = new HashMap<String,String>();//错误
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("imgFile", ErrorView._21.name());//只读模式不允许提交数据
		}
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		User user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
		
		String _width = request.getParameter("width");
		String _height = request.getParameter("height");
		String _x = request.getParameter("x");
		String _y = request.getParameter("y");
		
		
		Integer width = null;//宽
		Integer height = null;//高
		Integer x = 0;//坐标X轴
		Integer y = 0;//坐标Y轴
		
		Integer maxWidth = 200;//最大宽度
		Integer maxHeight = 200;//最大高度
		
		
		if(_width != null && !"".equals(_width.trim())){
			if(Verification.isPositiveInteger(_width.trim())){
				if(_width.trim().length() >=8){
					error.put("width", ErrorView._1200.name());//不能超过8位数字
				}else{
					width = Integer.parseInt(_width.trim());
				}
				
				
			}else{
				error.put("width", ErrorView._1210.name());//宽度必须大于0
			}
			
		}
		if(_height != null && !"".equals(_height.trim())){
			if(Verification.isPositiveInteger(_height.trim())){
				if(_height.trim().length() >=8){
					error.put("height", ErrorView._1200.name());//不能超过8位数字
				}else{
					height = Integer.parseInt(_height.trim());
				}
				
			}else{
				error.put("height", ErrorView._1230.name());//高度必须大于0 
			}
		}
		
		if(_x != null && !"".equals(_x.trim())){
			if(Verification.isPositiveIntegerZero(_x.trim())){
				if(_x.trim().length() >=8){
					error.put("x", ErrorView._1200.name());//不能超过8位数字
				}else{
					x = Integer.parseInt(_x.trim());
				}
				
			}else{
				error.put("x", ErrorView._1250.name());//X轴必须大于或等于0
			}
			
		}
		
		if(_y != null && !"".equals(_y.trim())){
			if(Verification.isPositiveIntegerZero(_y.trim())){
				if(_y.trim().length() >=8){
					error.put("y", ErrorView._1200.name());//不能超过8位数字
				}else{
					y = Integer.parseInt(_y.trim());
				}
				
			}else{
				error.put("y", ErrorView._1270.name());//Y轴必须大于或等于0
			}
			
		}
		
		
		//构建文件名称
		String newFileName = "";
		DateTime dateTime = new DateTime(user.getRegistrationDate());     
		String date = dateTime.toString("yyyy-MM-dd");
		
		if(error.size()==0 && imgFile != null && !imgFile.isEmpty()){
			//当前文件名称
			String fileName = imgFile.getOriginalFilename();
			
			//文件大小
			Long size = imgFile.getSize();
			

			
			//允许上传图片格式
			List<String> formatList = new ArrayList<String>();
			formatList.add("gif");
			formatList.add("jpg");
			formatList.add("jpeg");
			formatList.add("bmp");
			formatList.add("png");
			//允许上传图片大小 单位KB
			long imageSize = 3*1024L;
			
			if(size/1024 <= imageSize){
				
				//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
				String pathDir = "file"+File.separator+"avatar"+File.separator + date +File.separator ;
				//生成文件保存目录
				fileManage.createFolder(pathDir);
				//100*100目录
				String pathDir_100 = "file"+File.separator+"avatar"+File.separator + date +File.separator +"100x100" +File.separator;
				//生成文件保存目录
				fileManage.createFolder(pathDir_100);
				
				
				if("blob".equalsIgnoreCase(imgFile.getOriginalFilename())){//Blob类型
					
					newFileName = UUIDUtil.getUUID32()+ ".png";
					
					BufferedImage bufferImage = ImageIO.read(imgFile.getInputStream());  
		            //获取图片的宽和高  
		            int srcWidth = bufferImage.getWidth();  
		            int srcHeight = bufferImage.getHeight();  
					if(srcWidth > maxWidth){
						error.put("imgFile",ErrorView._1290.name());//超出最大宽度
					}
					if(srcHeight > maxHeight){
						error.put("imgFile",ErrorView._1300.name());//超出最大高度
					}
					if(error.size() == 0){
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							String oldPathFile = pathDir +user.getAvatarName();
							//删除旧头像
							fileManage.deleteFile(oldPathFile);
							String oldPathFile_100 = pathDir_100+user.getAvatarName();
							//删除旧头像100*100
							fileManage.deleteFile(oldPathFile_100);
						}
						
						//保存文件
						fileManage.writeFile(pathDir, newFileName,imgFile.getBytes());

						//生成100*100缩略图
						fileManage.createImage(imgFile.getInputStream(),PathUtil.path()+File.separator+pathDir_100+newFileName,"png",100,100);
						
					}
				}else{//图片类型
					//验证文件类型
					boolean authentication = FileUtil.validateFileSuffix(imgFile.getOriginalFilename(),formatList);
			
					if(authentication){
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							String oldPathFile = pathDir +user.getAvatarName();
							//删除旧头像
							fileManage.deleteFile(oldPathFile);
							String oldPathFile_100 = pathDir_100+user.getAvatarName();
							//删除旧头像100*100
							fileManage.deleteFile(oldPathFile_100);
						}
						
						
						BufferedImage bufferImage = ImageIO.read(imgFile.getInputStream());  
			            //获取图片的宽和高  
			            int srcWidth = bufferImage.getWidth();  
			            int srcHeight = bufferImage.getHeight();  
						
						//取得文件后缀
						String suffix = FileUtil.getExtension(fileName).toLowerCase();
						
						//构建文件名称
						newFileName = UUIDUtil.getUUID32()+ "." + suffix;
						
						if(srcWidth <=200 && srcHeight <=200){	
							//保存文件
							fileManage.writeFile(pathDir, newFileName,imgFile.getBytes());
							
							if(srcWidth <=100 && srcHeight <=100){
								//保存文件
								fileManage.writeFile(pathDir_100, newFileName,imgFile.getBytes());
							}else{
								//生成100*100缩略图
								fileManage.createImage(imgFile.getInputStream(),PathUtil.path()+File.separator+pathDir_100+newFileName,suffix,100,100);
								
							}
						}else{
							//生成200*200缩略图
							fileManage.createImage(imgFile.getInputStream(),PathUtil.path()+File.separator+pathDir+newFileName,suffix,x,y,width,height,200,200);

							//生成100*100缩略图
							fileManage.createImage(imgFile.getInputStream(),PathUtil.path()+File.separator+pathDir_100+newFileName,suffix,x,y,width,height,100,100);   
						}		
						
					}else{
						error.put("imgFile",ErrorView._1310.name());//当前文件类型不允许上传
					}
				}	
			}else{
				error.put("imgFile",ErrorView._1320.name());//文件超出允许上传大小
			}	
		}else{
			error.put("imgFile",ErrorView._1330.name());//文件不能为空
		}
		

		if(error.size() ==0){
			userService.updateUserAvatar(accessUser.getUserName(), newFileName);
			//删除缓存
			userManage.delete_cache_findUserById(user.getId());
			userManage.delete_cache_findUserByUserName(user.getUserName());
			
			
			String accessToken = WebUtil.getCookieByName(request, "cms_accessToken");
			if(accessToken != null && !"".equals(accessToken.trim())){
				//删除访问令牌
    			oAuthManage.deleteAccessToken(accessToken.trim());
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
		
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值

		if(error != null && error.size() >0){
			returnValue.put("success", "false");
			returnValue.put("error", returnError);
		}else{
			returnValue.put("success", "true");
		}
		return JsonUtils.toJSONString(returnValue);
	}
	
	
	
	/**-------------------------------------------- 实名认证 ----------------------------------------------------**/
	/**
	 * 实名认证
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/realNameAuthentication",method=RequestMethod.GET) 
	public String realNameAuthenticationUI(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		User user = userService.findUserByUserName(accessUser.getUserName());
	    if(user != null){
	    	//仅显示指定的字段
			User viewUser = new User();
			viewUser.setId(user.getId());
			viewUser.setUserName(user.getUserName());//会员用户名
			viewUser.setNickname(user.getNickname());//呢称
			viewUser.setMobile(user.getMobile());
			viewUser.setRealNameAuthentication(user.isRealNameAuthentication());
	    	
	    	model.addAttribute("user",viewUser);
		    returnValue.put("user",viewUser);
	    } 
		

		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
			String accessPath = accessSourceDeviceManage.accessDevices(request);
			return "templates/"+dirName+"/"+accessPath+"/realNameAuthentication";	
		}
	}
	

	/**
	 * 手机绑定
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/phoneBinding",method=RequestMethod.GET) 
	public String phoneBindingUI(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
	    String captchaKey = UUIDUtil.getUUID32();
	    model.addAttribute("captchaKey",captchaKey);
	    returnValue.put("captchaKey",captchaKey);

	    if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
	    }else{	
			String dirName = templateService.findTemplateDir_cache();   
		    return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/phoneBinding";	
		}
	}
	
	/**
	 * 手机绑定
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/phoneBinding",method=RequestMethod.POST) 
	public String phoneBinding(ModelMap model,String mobile,String smsCode,
			String captchaKey,String captchaValue,String jumpUrl,
			String token,RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
	
	    Map<String,String> error = new HashMap<String,String>();
	    SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("smsCode", ErrorView._21.name());//只读模式不允许提交数据
		}
	    //获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	   
	    //判断令牌
	    if(token != null && !"".equals(token.trim())){	
  			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
  			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
  				if(!token_sessionid.equals(token)){
  					error.put("token", ErrorView._13.name());
  				}
  			}else{
  				error.put("token", ErrorView._12.name());
  			}
  		}else{
  			error.put("token", ErrorView._11.name());
  		}
  		
	    User new_user = userService.findUserByUserName(accessUser.getUserName());;
	    
	    if(mobile != null && !"".equals(mobile.trim())){
	    	if(mobile.trim().length() >18){
				error.put("mobile", ErrorView._854.name());//手机号码超长
			}else{
				boolean mobile_verification = Verification.isPositiveInteger(mobile.trim());//正整数
				if(!mobile_verification){
					error.put("mobile", ErrorView._853.name());//手机号码不正确
				}else{
					
		      		if(new_user != null){
		      			if(new_user.getMobile() != null && !"".equals(new_user.getMobile())){
		      				error.put("mobile", ErrorView._857.name());//手机号码不能重复绑定
		      			}
		      			
		      		}else{
		      			error.put("mobile", ErrorView._859.name());//用户不存在
		      		}
				}
			}
	    }else{
	    	error.put("mobile", ErrorView._851.name());//手机号不能为空
	    }
	    
	    
	    if(smsCode != null && !"".equals(smsCode.trim())){
	    	if(smsCode.trim().length() >6){
				error.put("smsCode", ErrorView._855.name());//手机验证码超长
			}else{
			    if(error.size() ==0){
			    	
			    	//生成绑定手机验证码标记
		    		String numeric = smsManage.smsCode_generate(1,new_user.getId(), mobile.trim(),null);
		    		if(numeric != null){
		    			if(!numeric.equals(smsCode)){
		    				error.put("smsCode", ErrorView._850.name());//手机验证码错误
		    			}
		    			
		    		}else{
		    			error.put("smsCode", ErrorView._856.name());//手机验证码不存在或已过期
		    		}
			    }
			}
	    }else{
	    	error.put("smsCode", ErrorView._852.name());//手机验证码不能为空
	    }
	    
	    if(mobile != null && !"".equals(mobile.trim())){
	    	 //删除绑定手机验证码标记
		    smsManage.smsCode_delete(1,new_user.getId(), mobile.trim());
		   
	    }
	    
	    if(error.size() ==0){
	    	userService.updateUserMobile(new_user.getUserName(),mobile.trim(),true);
	    	//删除缓存
			userManage.delete_cache_findUserById(new_user.getId());
			userManage.delete_cache_findUserByUserName(new_user.getUserName());
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
    			returnValue.put("captchaKey", UUIDUtil.getUUID32());
    		}else{
    			returnValue.put("success", "true");
    		}
    		
    		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
			
			String accessPath = accessSourceDeviceManage.accessDevices(request);
			if(error != null && error.size() >0){//如果有错误
				for (Map.Entry<String,String> entry : returnError.entrySet()) {		 
					model.addAttribute("message",entry.getValue());//提示
		  			return "templates/"+dirName+"/"+accessPath+"/message";
				}
			}
			
			
			if(jumpUrl != null && !"".equals(jumpUrl.trim())){
				String url = Base64.decodeBase64URL(jumpUrl.trim());
				return "redirect:"+url;
			}else{//默认跳转
				model.addAttribute("message", "绑定手机成功");
				String referer = request.getHeader("referer");
				if(RefererCompare.compare(request, "login")){//如果是登录页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				model.addAttribute("urlAddress", referer);
				
				return "templates/"+dirName+"/"+accessPath+"/jump";	
			}
		}
	}
	
	/**
	 * 获取短信验证码
	 * @param model
	 * @param mobile 手机
	 * @param module 模块  1.绑定手机  2.更换绑定手机第一步  3.更换绑定手机第二步
	 * @param captchaKey
	 * @param captchaValue
	 * @param token
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/getSmsCode",method=RequestMethod.POST) 
	@ResponseBody//方式来做ajax,直接返回字符串
	public String SMS_verificationCode(ModelMap model,String mobile,Integer module,
			String captchaKey,String captchaValue,String token,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
	    
	    //判断令牌
	    if(token != null && !"".equals(token.trim())){	
  			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
  			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
  				if(!token_sessionid.equals(token)){
  					error.put("token", ErrorView._13.name());
  				}
  			}else{
  				error.put("token", ErrorView._12.name());
  			}
  		}else{
  			error.put("token", ErrorView._11.name());
  		}
	    
	    if(module == null || module <1 || module >3){
	    	error.put("message", "模块错误");
	    }
	    SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("message", ErrorView._21.name());//只读模式不允许提交数据
		}
	    
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
  		
  		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	User new_user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
	  	
		if(module.equals(1) || module.equals(3)){//1.绑定手机  3.更换绑定手机第二步
  			if(mobile != null && !"".equals(mobile.trim())){
  				if(mobile.trim().length() >18){
  					error.put("mobile", "手机号码超长");
  				}else{
  					boolean mobile_verification = Verification.isPositiveInteger(mobile.trim());//正整数
  					if(!mobile_verification){
  						error.put("mobile", "手机号码不正确");
  					}
  				}
  			}else{
  				error.put("mobile", ErrorView._851.name());//手机号不能为空
  			}
  		}else{//2.更换绑定手机第一步
  	  		if(new_user == null){
  	  			error.put("message", "用户不存在");
  	  		}else{
  	  			if(new_user.getMobile() != null && !"".equals(new_user.getMobile())){
  	  				mobile = new_user.getMobile();
  	  			}else{
  	  				error.put("message", "用户还没绑定手机");
  	  			}
  	  		}
  		}
	    if(error.size() == 0){
	    	String randomNumeric = RandomStringUtils.randomNumeric(6);
	    	String errorInfo = smsManage.sendSms_code(new_user.getId(),new_user.getUserName(),mobile,randomNumeric);//6位随机数
	    	if(errorInfo != null){
	    		error.put("smsCode", errorInfo);
	    	}else{
	    		//删除绑定手机验证码标记
	    	    smsManage.smsCode_delete(module,new_user.getId(), mobile.trim());
	    		//生成绑定手机验证码标记
	    		smsManage.smsCode_generate(module,new_user.getId(), mobile.trim(),randomNumeric);
	    		
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
		if(error != null && error.size() >0){
			returnValue.put("success", "false");
			returnValue.put("error", returnError);
			returnValue.put("captchaKey", UUIDUtil.getUUID32());
		}else{
			returnValue.put("success", "true");
		}
		
		return JsonUtils.toJSONString(returnValue);
	}
	
	
	
	
	/**
	 * 修改手机绑定 第一步验证旧手机界面
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/updatePhoneBinding/step1",method=RequestMethod.GET) 
	public String updatePhoneBinding_1UI(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
	    String captchaKey = UUIDUtil.getUUID32();
	    model.addAttribute("captchaKey",captchaKey);
	    returnValue.put("captchaKey",captchaKey);
	    
	    //获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	    
	    User user = userService.findUserByUserName(accessUser.getUserName());
	    if(user != null){
	    	model.addAttribute("mobile",user.getMobile());
		    returnValue.put("mobile",user.getMobile());
	    } 
   
	    if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
	    }else{	
			String dirName = templateService.findTemplateDir_cache();   
		    return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/updatePhoneBinding_step1";	
		}
	}
	
	/**
	 * 修改手机绑定 第一步验证旧手机
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/updatePhoneBinding/step1",method=RequestMethod.POST) 
	public String updatePhoneBinding_1(ModelMap model,String smsCode,
			String captchaKey,String captchaValue,String jumpUrl,
			String token,RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
	    
	    Map<String,String> error = new HashMap<String,String>();
	    SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("smsCode", ErrorView._21.name());//只读模式不允许提交数据
		}
	    //获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	   
	    //判断令牌
	    if(token != null && !"".equals(token.trim())){	
  			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
  			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
  				if(!token_sessionid.equals(token)){
  					error.put("token", ErrorView._13.name());
  				}
  			}else{
  				error.put("token", ErrorView._12.name());
  			}
  		}else{
  			error.put("token", ErrorView._11.name());
  		}
  		
	    User new_user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
  		if(new_user != null){
  			if(new_user.getMobile() == null || "".equals(new_user.getMobile())){
  				error.put("smsCode", ErrorView._858.name());//你还没有绑定手机
  			}
  			
  			
  			if(error.size() ==0 && smsCode != null && !"".equals(smsCode.trim())){
  		    	if(smsCode.trim().length() >6){
  					error.put("smsCode", ErrorView._855.name());//手机验证码超长
  				}else{
  				    if(error.size() ==0){
  				    	
  				    	//生成绑定手机验证码标记
  			    		String numeric = smsManage.smsCode_generate(2,new_user.getId(), new_user.getMobile(),null);
  			    		if(numeric != null){
  			    			if(!numeric.equals(smsCode)){
  			    				error.put("smsCode", ErrorView._850.name());//手机验证码错误
  			    			}
  			    			
  			    		}else{
  			    			error.put("smsCode", ErrorView._856.name());//手机验证码不存在或已过期
  			    		}
  				    }
  				}
  		    }else{
  		    	error.put("smsCode", ErrorView._852.name());//手机验证码不能为空
  		    }
  	  			
  			 //删除绑定手机验证码标记
  		    smsManage.smsCode_delete(2,new_user.getId(), new_user.getMobile());	
  		}else{
  			error.put("smsCode", ErrorView._859.name());//用户不存在
  		}

  		if(error.size() ==0){
	    	smsManage.replaceCode_delete(new_user.getId(), new_user.getMobile());
	    	smsManage.replaceCode_generate(new_user.getId(), new_user.getMobile(),true);//标记可以进行第二步验证
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
    			returnValue.put("captchaKey", UUIDUtil.getUUID32());
    		}else{
    			returnValue.put("success", "true");
    			returnValue.put("jumpUrl", "user/control/updatePhoneBinding/step2");
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
			
			return "redirect:/user/control/updatePhoneBinding/step2";

		}
		
	}
	
	/**
	 * 修改手机绑定 第二步验证新手机界面
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/updatePhoneBinding/step2",method=RequestMethod.GET) 
	public String updatePhoneBinding_2UI(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
	    String captchaKey = UUIDUtil.getUUID32();
	    model.addAttribute("captchaKey",captchaKey);
	    returnValue.put("captchaKey",captchaKey);

	    if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
	    }else{	
			String dirName = templateService.findTemplateDir_cache();   
		    return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/updatePhoneBinding_step2";	
		}
	}
	
	/**
	 * 修改手机绑定 第二步验证新手机
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/updatePhoneBinding/step2",method=RequestMethod.POST) 
	public String updatePhoneBinding_2UI(ModelMap model,String mobile,String smsCode,
			String captchaKey,String captchaValue,String jumpUrl,
			String token,RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
	   
		
		
		
	    Map<String,String> error = new HashMap<String,String>();
	    SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("smsCode", ErrorView._21.name());//只读模式不允许提交数据
		}
	    //获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	   
	    //判断令牌
	    if(token != null && !"".equals(token.trim())){	
  			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
  			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
  				if(!token_sessionid.equals(token)){
  					error.put("token", ErrorView._13.name());
  				}
  			}else{
  				error.put("token", ErrorView._12.name());
  			}
  		}else{
  			error.put("token", ErrorView._11.name());
  		}
  		
	    User new_user = userService.findUserByUserName(accessUser.getUserName());
  		
	    
	    if(mobile != null && !"".equals(mobile.trim())){
	    	if(mobile.trim().length() >18){
				error.put("mobile", ErrorView._854.name());//手机号码超长
			}else{
				boolean mobile_verification = Verification.isPositiveInteger(mobile.trim());//正整数
				if(!mobile_verification){
					error.put("mobile", ErrorView._853.name());//手机号码不正确
				}else{
					
					if(new_user != null){
		      			if(new_user.getMobile() != null && !"".equals(new_user.getMobile())){
		      				if(new_user.getMobile().equals(mobile.trim())){
		      					error.put("mobile", ErrorView._860.name());//新手机号码不能和旧用机号码相同
		      				}
		      			}
		      		}
				}
			}
	    }else{
	    	error.put("mobile", ErrorView._851.name());//手机号不能为空
	    }
	    
	    
	    if(smsCode != null && !"".equals(smsCode.trim())){
	    	if(smsCode.trim().length() >6){
				error.put("smsCode", ErrorView._855.name());//手机验证码超长
			}else{
			    if(error.size() ==0){
			    	
			    	//生成绑定手机验证码标记
		    		String numeric = smsManage.smsCode_generate(3,new_user.getId(), mobile.trim(),null);
		    		if(numeric != null){
		    			if(!numeric.equals(smsCode)){
		    				error.put("smsCode", ErrorView._850.name());//手机验证码错误
		    			}
		    			
		    		}else{
		    			error.put("smsCode", ErrorView._856.name());//手机验证码不存在或已过期
		    		}
			    }
			}
	    }else{
	    	error.put("smsCode", ErrorView._852.name());//手机验证码不能为空
	    }
	    
	    //删除绑定手机验证码标记
	    smsManage.smsCode_delete(3,new_user.getId(), mobile.trim());
	    
	    if(error.size() ==0){
	    	if(new_user != null){
	    		boolean allow = smsManage.replaceCode_generate(new_user.getId(), new_user.getMobile(),false);//查询是否验证第一步成功
		    	if(allow == false){
		    		error.put("smsCode", ErrorView._861.name());//旧手机号码校验失败
		    	}
		    	//删除
		    	smsManage.replaceCode_delete(new_user.getId(), new_user.getMobile());
	    	}
	    	
	    	
	    }
	    
	    if(error.size() ==0){
	    	userService.updateUserMobile(new_user.getUserName(),mobile.trim(),true);
	    	//删除缓存
			userManage.delete_cache_findUserById(new_user.getId());
			userManage.delete_cache_findUserByUserName(new_user.getUserName());
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
    			returnValue.put("captchaKey", UUIDUtil.getUUID32());
    		}else{
    			returnValue.put("success", "true");
    		}
    		
    		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
			
			String accessPath = accessSourceDeviceManage.accessDevices(request);
			if(error != null && error.size() >0){//如果有错误
				for (Map.Entry<String,String> entry : returnError.entrySet()) {		 
					model.addAttribute("message",entry.getValue());//提示
		  			return "templates/"+dirName+"/"+accessPath+"/message";
				}
			}
			
			
			if(jumpUrl != null && !"".equals(jumpUrl.trim())){
				String url = Base64.decodeBase64URL(jumpUrl.trim());
				return "redirect:"+url;
			}else{//默认跳转
				model.addAttribute("message", "更换绑定手机成功");
				String referer = request.getHeader("referer");
				if(RefererCompare.compare(request, "login")){//如果是登录页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				model.addAttribute("urlAddress", referer);
				
				return "templates/"+dirName+"/"+accessPath+"/jump";	
			}
		}
		
	}
	
	
	
	/**----------------------------------- 登录日志 ----------------------------------**/


	/**
	 * 用户登录日志列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/userLoginLogList",method=RequestMethod.GET) 
	public String accountLogList(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		String dirName = templateService.findTemplateDir_cache();
		
		
		String accessPath = accessSourceDeviceManage.accessDevices(request);
	   
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		//调用分页算法代码
		PageView<UserLoginLog> pageView = new PageView<UserLoginLog>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();

    	
		QueryResult<UserLoginLog> qr = userService.findUserLoginLogPage(accessUser.getUserId(),firstIndex,pageView.getMaxresult());
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(UserLoginLog userLoginLog : qr.getResultlist()){
				userLoginLog.setIpAddress(IpAddress.queryAddress(userLoginLog.getIp()));
			}
		}	
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);

		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			model.addAttribute("pageView", pageView);
			return "templates/"+dirName+"/"+accessPath+"/userLoginLogList";	
		}
	}
	
	
	
	/**----------------------------------- 私信 ----------------------------------**/
	
	
	/**
	 * 私信列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/privateMessageList",method=RequestMethod.GET) 
	public String privateMessageList(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		String dirName = templateService.findTemplateDir_cache();
		
		
		String accessPath = accessSourceDeviceManage.accessDevices(request);
	   
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		//调用分页算法代码
		PageView<PrivateMessage> pageView = new PageView<PrivateMessage>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();

		//用户Id集合
		Set<Long> userIdList = new HashSet<Long>();
		//用户集合
		Map<Long,User> userMap = new HashMap<Long,User>();
		
		QueryResult<PrivateMessage> qr = privateMessageService.findPrivateMessageByUserId(accessUser.getUserId(),100,firstIndex,pageView.getMaxresult());
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(PrivateMessage privateMessage : qr.getResultlist()){
				userIdList.add(privateMessage.getSenderUserId());//发送者用户Id 
				userIdList.add(privateMessage.getReceiverUserId());//接受者用户Id

				privateMessage.setSendTime(new Timestamp(privateMessage.getSendTimeFormat()));
				if(privateMessage.getReadTimeFormat() != null){
					privateMessage.setReadTime(new Timestamp(privateMessage.getReadTimeFormat()));
				}
				privateMessage.setMessageContent(textFilterManage.filterText(privateMessage.getMessageContent()));
				
				
				
				
				
			}
		}
		
		if(userIdList != null && userIdList.size() >0){
			for(Long userId : userIdList){
				User user = userManage.query_cache_findUserById(userId);
				if(user != null){
					userMap.put(userId, user);
				}
			}
		}
		if(userMap != null && userMap.size() >0){
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(PrivateMessage privateMessage : qr.getResultlist()){
					User friend_user = userMap.get(privateMessage.getFriendUserId());
					if(friend_user != null){
						privateMessage.setFriendUserName(friend_user.getUserName());//私信对方用户名称
						privateMessage.setFriendNickname(friend_user.getNickname());
						if(friend_user.getAvatarName() != null && !"".equals(friend_user.getAvatarName().trim())){
							privateMessage.setFriendAvatarPath(friend_user.getAvatarPath());//私信对方头像路径
							privateMessage.setFriendAvatarName(friend_user.getAvatarName());//私信对方头像名称
						}			
					}
					User sender_user = userMap.get(privateMessage.getSenderUserId());
					if(sender_user != null){
						privateMessage.setSenderUserName(sender_user.getUserName());//私信发送者用户名称
						privateMessage.setSenderNickname(sender_user.getNickname());
						if(sender_user.getAvatarName() != null && !"".equals(sender_user.getAvatarName().trim())){
							privateMessage.setSenderAvatarPath(sender_user.getAvatarPath());//发送者头像路径
							privateMessage.setSenderAvatarName(sender_user.getAvatarName());//发送者头像名称
						}
					}
					
				}
			}
			
			
			
		}
		
		
		
		

		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			model.addAttribute("pageView", pageView);
			return "templates/"+dirName+"/"+accessPath+"/privateMessageList";	
		}
	}
	
	
	/**
	 * 私信对话列表
	 * @param model
	 * @param page 页码
	 * @param friendUserName 对方用户名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/privateMessageChatList",method=RequestMethod.GET) 
	public String privateMessageChatList(ModelMap model,Integer page,String friendUserName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		String dirName = templateService.findTemplateDir_cache();
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		String accessPath = accessSourceDeviceManage.accessDevices(request);
		PageForm pageForm = new PageForm();
		pageForm.setPage(page);

		//调用分页算法代码
		PageView<PrivateMessage> pageView = new PageView<PrivateMessage>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		
		
		//对话用户
		User chatUser = null;
		
		//用户Id集合
		Set<Long> userIdList = new HashSet<Long>();
		//用户集合
		Map<Long,User> userMap = new HashMap<Long,User>();
		
		//未读私信Id集合
		List<String> unreadPrivateMessageIdList = new ArrayList<String>();
		
		if(friendUserName != null && !"".equals(friendUserName.trim())){
			chatUser = userManage.query_cache_findUserByUserName(friendUserName.trim());
			if(chatUser != null){
				if(page == null){//页数为空时显示最后一页数据
					//根据用户Id查询私信对话分页总数
					Long count = privateMessageService .findPrivateMessageChatCountByUserId(accessUser.getUserId(),chatUser.getId(),100);
					//计算总页数
					pageView.setTotalrecord(count);
					pageForm.setPage((int)pageView.getTotalpage());
					pageView = new PageView<PrivateMessage>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
					
				}
				//当前页
				int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
				
				QueryResult<PrivateMessage> qr = privateMessageService.findPrivateMessageChatByUserId(accessUser.getUserId(),chatUser.getId(),100,firstIndex,pageView.getMaxresult(),1);
				if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
					for(PrivateMessage privateMessage : qr.getResultlist()){
						userIdList.add(privateMessage.getSenderUserId());//发送者用户Id 
						userIdList.add(privateMessage.getReceiverUserId());//接受者用户Id

						privateMessage.setSendTime(new Timestamp(privateMessage.getSendTimeFormat()));
						if(privateMessage.getReadTimeFormat() != null){
							privateMessage.setReadTime(new Timestamp(privateMessage.getReadTimeFormat()));
						}
						
						if(privateMessage.getStatus().equals(10)){
							unreadPrivateMessageIdList.add(privateMessage.getId());
						}
					}
				}
				
				if(userIdList != null && userIdList.size() >0){
					for(Long userId : userIdList){
						User user = userManage.query_cache_findUserById(userId);
						if(user != null){
							userMap.put(userId, user);
						}
					}
				}
				if(userMap != null && userMap.size() >0){
					if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
						for(PrivateMessage privateMessage : qr.getResultlist()){
							User friend_user = userMap.get(privateMessage.getFriendUserId());
							if(friend_user != null){
								privateMessage.setFriendUserName(friend_user.getUserName());//私信对方用户名称
								privateMessage.setFriendNickname(friend_user.getNickname());
								if(friend_user.getAvatarName() != null && !"".equals(friend_user.getAvatarName().trim())){
									privateMessage.setFriendAvatarPath(friend_user.getAvatarPath());//私信对方头像路径
									privateMessage.setFriendAvatarName(friend_user.getAvatarName());//私信对方头像名称
								}
							}
							User sender_user = userMap.get(privateMessage.getSenderUserId());
							if(sender_user != null){
								privateMessage.setSenderUserName(sender_user.getUserName());//私信发送者用户名称
								privateMessage.setSenderNickname(sender_user.getNickname());
								if(sender_user.getAvatarName() != null && !"".equals(sender_user.getAvatarName().trim())){
									privateMessage.setSenderAvatarPath(sender_user.getAvatarPath());//发送者头像路径
									privateMessage.setSenderAvatarName(sender_user.getAvatarName());//发送者头像名称
								}
							}
							
						}
					}
					
					
					
				}
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);
				
				if(unreadPrivateMessageIdList != null && unreadPrivateMessageIdList.size() >0){
					
					//将未读私信设置为已读
					privateMessageService.updatePrivateMessageStatus(accessUser.getUserId(), unreadPrivateMessageIdList);
					//删除私信缓存
					privateMessageManage.delete_cache_findUnreadPrivateMessageByUserId(accessUser.getUserId());
				}
				
				
			}
		}
		
		if(chatUser != null){
			//仅显示指定的字段
			User viewUser = new User();
			viewUser.setId(chatUser.getId());
			viewUser.setUserName(chatUser.getUserName());//会员用户名
			viewUser.setNickname(chatUser.getNickname());//呢称
			viewUser.setRegistrationDate(chatUser.getRegistrationDate());//注册日期

			List<UserGrade> userGradeList = userGradeService.findAllGrade_cache();//取得用户所有等级
			if(userGradeList != null && userGradeList.size() >0){
				for(UserGrade userGrade : userGradeList){
					if(chatUser.getPoint() >= userGrade.getNeedPoint()){
						viewUser.setGradeId(userGrade.getId());//等级Id
						viewUser.setGradeName(userGrade.getName());//将等级值设进等级参数里
						break;
					}
				} 
			}

			chatUser = viewUser;
		}
		
		
		if(isAjax){
			Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
			returnValue.put("chatUser", chatUser);
			returnValue.put("pageView", pageView);
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			model.addAttribute("chatUser", chatUser);//对话用户
			model.addAttribute("pageView", pageView);
			return "templates/"+dirName+"/"+accessPath+"/privateMessageChatList";	
		}
	}
	
	/**
	 * 添加私信界面
	 * @param friendUserName 对方用户名称
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/addPrivateMessage",method=RequestMethod.GET) 
	public String addPrivateMessageUI(ModelMap model,String friendUserName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	Map<String,Object> returnValue = new HashMap<String,Object>();
	  	FormCaptcha formCaptcha = new FormCaptcha();
		if(accessUser != null){
			boolean captchaKey = captchaManage.privateMessage_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				formCaptcha.setShowCaptcha(true);
				formCaptcha.setCaptchaKey(UUIDUtil.getUUID32());
			}
		}
		
		if(friendUserName != null && !"".equals(friendUserName.trim())){		
			User user = userManage.query_cache_findUserByUserName(friendUserName.trim());
			if(user != null){
				returnValue.put("allowSendPrivateMessage",true);//允许发私信
			}else{
				returnValue.put("allowSendPrivateMessage",false);//不允许发私信
			}
		}else{
			returnValue.put("allowSendPrivateMessage",false);//不允许发私信
		}
		
		if(isAjax){
			returnValue.put("formCaptcha", formCaptcha);
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			model.addAttribute("formCaptcha", formCaptcha);
			model.addAttribute("allowSendPrivateMessage", returnValue.get("allowSendPrivateMessage"));
			String dirName = templateService.findTemplateDir_cache();
			String accessPath = accessSourceDeviceManage.accessDevices(request);		
			return "templates/"+dirName+"/"+accessPath+"/addPrivateMessage";	
		}
	}

	
	/**
	 * 私信  添加
	 * @param model
	 * @param friendUserName 对方用户名称
	 * @param messageContent 消息内容
	 * @param jumpUrl 跳转地址
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/addPrivateMessage", method=RequestMethod.POST)
	@RoleAnnotation(resourceCode=ResourceEnum._6001000)
	public String addPrivateMessage(ModelMap model,String friendUserName,String messageContent,
			String token,String captchaKey,String captchaValue,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		
		Map<String,String> error = new HashMap<String,String>();
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("privateMessage", ErrorView._21.name());//只读模式不允许提交数据
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
		boolean isCaptcha = captchaManage.privateMessage_isCaptcha(accessUser.getUserName());
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
		
		User friendUser = null;
		
		
		if(friendUserName != null && !"".equals(friendUserName.trim())){		
			friendUser = userManage.query_cache_findUserByUserName(friendUserName.trim());
			if(friendUser != null){
				if(friendUser.getState() >1){
					error.put("privateMessage", ErrorView._1000.name());//不允许给当前用户发私信
				}
				if(friendUser.getUserName().equals(accessUser.getUserName())){
					error.put("privateMessage", ErrorView._1010.name());//不允许给自己发私信
				}
			}else{
				error.put("privateMessage", ErrorView._910.name());//用户不存在
			}
		}else{
			error.put("privateMessage", ErrorView._1020.name());//对方用户名称不能为空
		}
		
		if(messageContent != null && !"".equals(messageContent.trim())){
			if(messageContent.length() >1000){
				error.put("messageContent", ErrorView._1030.name());//私信内容不能超过1000个字符
			}
		}else{
			error.put("messageContent", ErrorView._1040.name());//私信内容不能为空
		}
		
		if(error.size() ==0){
			Long time = new Date().getTime();
			String content = WebUtil.urlToHyperlink(HtmlEscape.escape(messageContent.trim()));
			
			
			//保存发送者私信
			PrivateMessage sender_privateMessage = new PrivateMessage();
			sender_privateMessage.setId(privateMessageManage.createPrivateMessageId(accessUser.getUserId()));
			sender_privateMessage.setUserId(accessUser.getUserId());//私信用户Id
			sender_privateMessage.setFriendUserId(friendUser.getId());//私信对方用户Id
			sender_privateMessage.setSenderUserId(accessUser.getUserId());//发送者用户Id
			sender_privateMessage.setReceiverUserId(friendUser.getId());//接受者用户Id
			sender_privateMessage.setMessageContent(content);//消息内容
			sender_privateMessage.setStatus(20);//消息状态 20:已读
			sender_privateMessage.setSendTimeFormat(time);//发送时间格式化
			sender_privateMessage.setReadTimeFormat(time);//阅读时间格式化
			Object sender_privateMessage_object = privateMessageManage.createPrivateMessageObject(sender_privateMessage);
			
			
			//保存接收者私信
			PrivateMessage receiver_privateMessage = new PrivateMessage();
			receiver_privateMessage.setId(privateMessageManage.createPrivateMessageId(friendUser.getId()));
			receiver_privateMessage.setUserId(friendUser.getId());//私信用户Id
			receiver_privateMessage.setFriendUserId(accessUser.getUserId());//私信对方用户Id
			receiver_privateMessage.setSenderUserId(accessUser.getUserId());//发送者用户Id
			receiver_privateMessage.setReceiverUserId(friendUser.getId());//接受者用户Id
			receiver_privateMessage.setMessageContent(content);//消息内容
			receiver_privateMessage.setStatus(10);//消息状态 10:未读
			receiver_privateMessage.setSendTimeFormat(time);//发送时间格式化
			Object receiver_privateMessage_object = privateMessageManage.createPrivateMessageObject(receiver_privateMessage);
			
			privateMessageService.savePrivateMessage(sender_privateMessage_object, receiver_privateMessage_object);
			
			//删除私信缓存
			privateMessageManage.delete_cache_findUnreadPrivateMessageByUserId(accessUser.getUserId());
			privateMessageManage.delete_cache_findUnreadPrivateMessageByUserId(friendUser.getId());
			
			
			//统计每分钟原来提交次数
			Integer original = settingManage.getSubmitQuantity("privateMessage", accessUser.getUserName());
    		if(original != null){
    			settingManage.addSubmitQuantity("privateMessage", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
    		}else{
    			settingManage.addSubmitQuantity("privateMessage", accessUser.getUserName(),1);//刷新每分钟原来提交次数
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
			String dirName = templateService.findTemplateDir_cache();
			
			String accessPath = accessSourceDeviceManage.accessDevices(request);

			
			if(error != null && error.size() >0){//如果有错误
				
				redirectAttrs.addFlashAttribute("error", returnError);//重定向传参
				
				PrivateMessage privateMessage = new PrivateMessage();
				privateMessage.setFriendUserName(friendUserName);
				privateMessage.setMessageContent(messageContent);
				redirectAttrs.addFlashAttribute("privateMessage", privateMessage);
				
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
				model.addAttribute("message", "提交私信成功");
				String referer = request.getHeader("referer");
				if(RefererCompare.compare(request, "login")){//如果是登录页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				model.addAttribute("urlAddress", referer);
				
				return "templates/"+dirName+"/"+accessPath+"/jump";	
			}
		}
		
	}
	
	
	/**
	 * 删除私信
	 * @param model
	 * @param jumpUrl 跳转地址   页面post方式提交有效
	 * @param token 令牌标记
	 * @param friendUserName 对方用户名称
	 */
	@RequestMapping(value="/user/control/deletePrivateMessage", method=RequestMethod.POST)
	@RoleAnnotation(resourceCode=ResourceEnum._6002000)
	public String deletePrivateMessage(ModelMap model,String jumpUrl,String token,String friendUserName,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,String> error = new HashMap<String,String>();//错误
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("privateMessage", ErrorView._21.name());//只读模式不允许提交数据
		}
		
		//判断令牌
		if(token != null && !"".equals(token.trim())){	
			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
				if(!token_sessionid.equals(token)){
					error.put("token", ErrorView._13.name());
				}
			}else{
				error.put("token", ErrorView._12.name());
			}
		}else{
			error.put("token", ErrorView._11.name());
		}
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	
	  	//对话用户
  		User chatUser = null;
  	
  		if(friendUserName != null && !"".equals(friendUserName.trim())){
  			chatUser = userService.findUserByUserName(friendUserName.trim());
  			if(chatUser == null){
  				error.put("privateMessage", ErrorView._910.name());//用户不存在
  			}
  			
  		}else{
  			error.put("privateMessage", ErrorView._1020.name());//对方用户名称不能为空
  		}

  		
		if(error.size() == 0){
			int i = privateMessageService.softDeletePrivateMessage(accessUser.getUserId(),chatUser.getId());
			if(i == 0){
				error.put("privateMessage", ErrorView._1050.name());//删除私信失败
			}
			
			//删除私信缓存
			privateMessageManage.delete_cache_findUnreadPrivateMessageByUserId(accessUser.getUserId());
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
			String dirName = templateService.findTemplateDir_cache();
			String accessPath = accessSourceDeviceManage.accessDevices(request);
			if(error != null && error.size() >0){//如果有错误
				
				for (Map.Entry<String,String> entry : returnError.entrySet()) {		 
					model.addAttribute("message",entry.getValue());//提示
		  			return "templates/"+dirName+"/"+accessPath+"/message";
				}
					
			}
			
			
			if(jumpUrl != null && !"".equals(jumpUrl.trim())){
				String url = Base64.decodeBase64URL(jumpUrl.trim());
				return "redirect:"+url;
			}else{//默认跳转
				model.addAttribute("message", "删除私信成功");
				String referer = request.getHeader("referer");
				if(RefererCompare.compare(request, "login")){//如果是登录页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				model.addAttribute("urlAddress", referer);
				
				return "templates/"+dirName+"/"+accessPath+"/jump";	
			}
		}
		
	}
	

	/**----------------------------------- 系统通知 ----------------------------------**/
	
	/**
	 * 系统通知列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/systemNotifyList",method=RequestMethod.GET) 
	public String systemNotifyList(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		String dirName = templateService.findTemplateDir_cache();
		
		
		String accessPath = accessSourceDeviceManage.accessDevices(request);
	   
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	User user = userManage.query_cache_findUserById(accessUser.getUserId());
	  	
	  	//拉取系统通知
	  	this.pullSystemNotify(user.getId(),user.getRegistrationDate());
	  	
	  	
		//调用分页算法代码
		PageView<SubscriptionSystemNotify> pageView = new PageView<SubscriptionSystemNotify>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();

		//系统通知Id集合
		Set<Long> systemNotifyIdList = new HashSet<Long>();
		//系统通知内容集合
		Map<Long,String> systemNotifyMap = new HashMap<Long,String>();
		//未读订阅系统通知Id集合
		List<String> unreadSystemNotifyIdList = new ArrayList<String>();	
				
		QueryResult<SubscriptionSystemNotify> qr = systemNotifyService.findSubscriptionSystemNotifyByUserId(accessUser.getUserId(),100,firstIndex,pageView.getMaxresult());
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(SubscriptionSystemNotify subscriptionSystemNotify : qr.getResultlist()){
				systemNotifyIdList.add(subscriptionSystemNotify.getSystemNotifyId());
				
				if(subscriptionSystemNotify.getStatus().equals(10)){
					unreadSystemNotifyIdList.add(subscriptionSystemNotify.getId());
				}
			}
		}
		
	
		if(systemNotifyIdList != null && systemNotifyIdList.size() >0){
			for(Long systemNotifyId : systemNotifyIdList){
				SystemNotify systemNotify = systemNotifyManage.query_cache_findById(systemNotifyId);
				if(systemNotify != null){
					systemNotifyMap.put(systemNotifyId, systemNotify.getContent());
				}
			}
		}
		if(systemNotifyIdList != null && systemNotifyIdList.size() >0){
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(SubscriptionSystemNotify subscriptionSystemNotify : qr.getResultlist()){
					String content = systemNotifyMap.get(subscriptionSystemNotify.getSystemNotifyId());
					if(content != null){
						subscriptionSystemNotify.setContent(content);
					}
					
				}
			}
		}
		
		if(unreadSystemNotifyIdList != null && unreadSystemNotifyIdList.size() >0){
			//将未读订阅系统通知设置为已读
			systemNotifyService.updateSubscriptionSystemNotifyStatus(accessUser.getUserId(), unreadSystemNotifyIdList);
		}
		

		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			model.addAttribute("pageView", pageView);
			return "templates/"+dirName+"/"+accessPath+"/systemNotifyList";	
		}
	}
	
	/**
	 * 拉取系统通知
	 * @param userId 用户Id
	 * @param registrationDate 用户注册时间
	 */
	private void pullSystemNotify(Long userId,Date registrationDate){
		List<Object> subscriptionSystemNotifyList = new ArrayList<Object>();
		//查询用户订阅系统通知最新订阅系统Id
		Long maxSystemNotifyId = systemNotifyService.findMaxSystemNotifyIdByUserId(userId);
		Map<Long,Date> systemNotifyMap = null;
		if(maxSystemNotifyId == null){//如果还没有拉取系统通知
			systemNotifyMap = systemNotifyService.findSystemNotifyBySendTime(registrationDate);
		}else{
			systemNotifyMap = systemNotifyService.findSystemNotifyById(maxSystemNotifyId);
		}
		
		if(systemNotifyMap != null && systemNotifyMap.size() >0){
			for(Map.Entry<Long,Date> entry : systemNotifyMap.entrySet()){
				//系统通知Id
				Long systemNotifyId = entry.getKey();
				SubscriptionSystemNotify subscriptionSystemNotify = new SubscriptionSystemNotify();
				subscriptionSystemNotify.setId(subscriptionSystemNotifyManage.createSubscriptionSystemNotifyId(systemNotifyId, userId));
				subscriptionSystemNotify.setSystemNotifyId(systemNotifyId);
				subscriptionSystemNotify.setUserId(userId);
				subscriptionSystemNotify.setStatus(10);
				subscriptionSystemNotify.setSendTime(entry.getValue());
				
				Object subscriptionSystemNotify_object = subscriptionSystemNotifyManage.createSubscriptionSystemNotifyObject(subscriptionSystemNotify);
				subscriptionSystemNotifyList.add(subscriptionSystemNotify_object);
			}
		}
		
		
		
		if(subscriptionSystemNotifyList != null && subscriptionSystemNotifyList.size() >0){
			try {
				systemNotifyService.saveSubscriptionSystemNotify(subscriptionSystemNotifyList);
			} catch (Exception e) {
				//e.printStackTrace();
			}
			
			//删除缓存
			systemNotifyManage.delete_cache_findMinUnreadSystemNotifyIdByUserId(userId);
			systemNotifyManage.delete_cache_findMaxReadSystemNotifyIdByUserId(userId);
		}
	}
	
	
	/**
	 * 删除系统通知
	 * @param model
	 * @param jumpUrl 跳转地址   页面post方式提交有效
	 * @param token 令牌标记
	 * @param subscriptionSystemNotifyId 订阅系统通知Id
	 */
	@RequestMapping(value="/user/control/deleteSystemNotify", method=RequestMethod.POST)
	public String deleteSystemNotify(ModelMap model,String jumpUrl,String token,String subscriptionSystemNotifyId,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,String> error = new HashMap<String,String>();//错误
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("systemNotify", ErrorView._21.name());//只读模式不允许提交数据
		}
		
		//判断令牌
		if(token != null && !"".equals(token.trim())){	
			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
				if(!token_sessionid.equals(token)){
					error.put("token", ErrorView._13.name());
				}
			}else{
				error.put("token", ErrorView._12.name());
			}
		}else{
			error.put("token", ErrorView._11.name());
		}
		
		if(subscriptionSystemNotifyId == null || "".equals(subscriptionSystemNotifyId.trim())){
			error.put("systemNotify", ErrorView._1100.name());//订阅系统通知Id不能为空
		}
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	
	 

		if(error.size() == 0){
			int i = systemNotifyService.softDeleteSubscriptionSystemNotify(accessUser.getUserId(),subscriptionSystemNotifyId);
			
			//删除缓存
			systemNotifyManage.delete_cache_findMinUnreadSystemNotifyIdByUserId(accessUser.getUserId());
			systemNotifyManage.delete_cache_findMaxReadSystemNotifyIdByUserId(accessUser.getUserId());
			if(i == 0){
				error.put("systemNotify", ErrorView._1110.name());//删除系统通知失败
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
			String dirName = templateService.findTemplateDir_cache();
			String accessPath = accessSourceDeviceManage.accessDevices(request);
			if(error != null && error.size() >0){//如果有错误
				
				for (Map.Entry<String,String> entry : returnError.entrySet()) {		 
					model.addAttribute("message",entry.getValue());//提示
		  			return "templates/"+dirName+"/"+accessPath+"/message";
				}
					
			}
			
			
			if(jumpUrl != null && !"".equals(jumpUrl.trim())){
				String url = Base64.decodeBase64URL(jumpUrl.trim());
				return "redirect:"+url;
			}else{//默认跳转
				model.addAttribute("message", "删除系统通知成功");
				String referer = request.getHeader("referer");
				if(RefererCompare.compare(request, "login")){//如果是登录页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				model.addAttribute("urlAddress", referer);
				
				return "templates/"+dirName+"/"+accessPath+"/jump";	
			}
		}
		
	}
	
	
	/**
	 * 未读消息数量
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/unreadMessageCount",method=RequestMethod.GET) 
	@ResponseBody//方式来做ajax,直接返回字符串
	public String unreadMessageCount(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	
	  	//拉取关注的用户消息
	  	followManage.pullFollow(accessUser.getUserId(),accessUser.getUserName());
	  	
	  	UnreadMessage unreadMessage = new UnreadMessage();
	  	
	  	//查询未读私信数量
	  	Long unreadPrivateMessageCount = privateMessageManage.query_cache_findUnreadPrivateMessageByUserId(accessUser.getUserId());
	  	unreadMessage.setPrivateMessageCount(unreadPrivateMessageCount);
	  	
	  	//起始系统通知Id
	  	Long start_systemNotifyId = 0L;
	  	//起始系统通知发送时间
	  	Date start_systemNotifySendTime = null;
	  	
	  	//最早的未读系统通知Id
	  	Long minUnreadSystemNotifyId = systemNotifyManage.query_cache_findMinUnreadSystemNotifyIdByUserId(accessUser.getUserId());
	  	if(minUnreadSystemNotifyId == null){
	  		
	  		//最大的已读系统通知Id
	  		Long maxReadSystemNotifyId = systemNotifyManage.query_cache_findMaxReadSystemNotifyIdByUserId(accessUser.getUserId());
	  		if(maxReadSystemNotifyId != null){
	  			start_systemNotifyId = maxReadSystemNotifyId;
	  		}else{
	  			//获取用户
	  	  		User user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
	  	  		start_systemNotifySendTime = user.getRegistrationDate();
	  	  		
	  		}
	  		
	  		
	  	}else{
	  		start_systemNotifyId = minUnreadSystemNotifyId -1L;//-1是为了SQL查询时包含起始系统通知Id
	  	}
	  	if(start_systemNotifySendTime == null){
	  		//查询未读系统通知数量(如果顺序已读Id之间含有未读Id,则计数从最小的未读Id算起)
		  	Long unreadSystemNotifyCount = systemNotifyManage.query_cache_findSystemNotifyCountBySystemNotifyId(start_systemNotifyId);
		  	unreadMessage.setSystemNotifyCount(unreadSystemNotifyCount);
	  	}else{
	  		Long unreadSystemNotifyCount = systemNotifyManage.query_cache_findSystemNotifyCountBySendTime(start_systemNotifySendTime);
	  		unreadMessage.setSystemNotifyCount(unreadSystemNotifyCount);
	  	}
	  	
	  	//未读提醒数量
	  	Long unreadRemindCount = remindManage.query_cache_findUnreadRemindByUserId(accessUser.getUserId());
	  	unreadMessage.setRemindCount(unreadRemindCount);
	  	
	  	

		
	  	
	  	return JsonUtils.toJSONString(unreadMessage);
	}
	
	
	/**
	 * 提醒列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/remindList",method=RequestMethod.GET) 
	public String remindList(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		String dirName = templateService.findTemplateDir_cache();
		
		
		String accessPath = accessSourceDeviceManage.accessDevices(request);
	   
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	
	  	
		//调用分页算法代码
		PageView<Remind> pageView = new PageView<Remind>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		//用户Id集合
		Set<Long> userIdList = new HashSet<Long>();
		//用户集合
		Map<Long,User> userMap = new HashMap<Long,User>();
		
		//未读提醒Id集合
		List<String> unreadRemindIdList = new ArrayList<String>();
		
		
		QueryResult<Remind> qr = remindService.findRemindByUserId(accessUser.getUserId(),100,firstIndex,pageView.getMaxresult());
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(Remind remind : qr.getResultlist()){
				userIdList.add(remind.getSenderUserId());//发送用户Id
				
				if(remind .getStatus().equals(10)){
					unreadRemindIdList.add(remind.getId());
				}
				
				remind.setSendTime(new Timestamp(remind.getSendTimeFormat()));
				if(remind.getReadTimeFormat() != null){
					remind.setReadTime(new Timestamp(remind.getReadTimeFormat()));
				}
				
				if(remind.getTopicId() != null && remind.getTopicId() >0L){
					Topic topic = topicManage.queryTopicCache(remind.getTopicId());//查询缓存
					if(topic != null){
						remind.setTopicTitle(topic.getTitle());
					}
					
				}
				if(remind.getQuestionId() != null && remind.getQuestionId() >0L){
					Question question = questionManage.query_cache_findById(remind.getQuestionId());//查询缓存
					if(question != null){
						remind.setQuestionTitle(question.getTitle());
					}
					
				}
				
				
			}

			if(userIdList != null && userIdList.size() >0){
				for(Long userId : userIdList){
					User user = userManage.query_cache_findUserById(userId);
					if(user != null){
						userMap.put(userId, user);
					}
				}
			}
			if(userMap != null && userMap.size() >0){
				if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
					for(Remind remind : qr.getResultlist()){
						
						User sender_user = userMap.get(remind.getSenderUserId());
						if(sender_user != null){
							remind.setSenderUserName(sender_user.getUserName());//发送者用户名称
							remind.setSenderNickname(sender_user.getNickname());
							if(sender_user.getAvatarName() != null && !"".equals(sender_user.getAvatarName().trim())){
								remind.setSenderAvatarPath(sender_user.getAvatarPath());//发送者头像路径
								remind.setSenderAvatarName(sender_user.getAvatarName());//发送者头像名称
							}
						}
						
					}
				}
			}
		}
		
		if(unreadRemindIdList != null && unreadRemindIdList.size() >0){
			//将未读提醒设置为已读
			remindService.updateRemindStatus(accessUser.getUserId(), unreadRemindIdList);
			//删除提醒缓存
			remindManage.delete_cache_findUnreadRemindByUserId(accessUser.getUserId());
		}
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			model.addAttribute("pageView", pageView);
			return "templates/"+dirName+"/"+accessPath+"/remindList";	
		}
	}
	
	/**
	 * 删除提醒
	 * @param model
	 * @param jumpUrl 跳转地址   页面post方式提交有效
	 * @param token 令牌标记
	 * @param remindId 提醒Id
	 */
	@RequestMapping(value="/user/control/deleteRemind", method=RequestMethod.POST)
	public String deleteRemind(ModelMap model,String jumpUrl,String token,String remindId,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,String> error = new HashMap<String,String>();//错误
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("remind", ErrorView._21.name());//只读模式不允许提交数据
		}
		
		//判断令牌
		if(token != null && !"".equals(token.trim())){	
			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
				if(!token_sessionid.equals(token)){
					error.put("token", ErrorView._13.name());
				}
			}else{
				error.put("token", ErrorView._12.name());
			}
		}else{
			error.put("token", ErrorView._11.name());
		}
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	
	  	
  		if(remindId == null || "".equals(remindId.trim())){
  			error.put("remind", ErrorView._1400.name());//提醒不存在
  		}
  		
		if(error.size() == 0){
			int i = remindService.softDeleteRemind(accessUser.getUserId(),remindId.trim());
			if(i == 0){
				error.put("remind", ErrorView._1050.name());//删除提醒失败
			}
			//删除提醒缓存
			remindManage.delete_cache_findUnreadRemindByUserId(accessUser.getUserId());
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
			String dirName = templateService.findTemplateDir_cache();
			String accessPath = accessSourceDeviceManage.accessDevices(request);
			if(error != null && error.size() >0){//如果有错误
				
				for (Map.Entry<String,String> entry : returnError.entrySet()) {		 
					model.addAttribute("message",entry.getValue());//提示
		  			return "templates/"+dirName+"/"+accessPath+"/message";
				}
					
			}
			
			
			if(jumpUrl != null && !"".equals(jumpUrl.trim())){
				String url = Base64.decodeBase64URL(jumpUrl.trim());
				return "redirect:"+url;
			}else{//默认跳转
				model.addAttribute("message", "删除提醒成功");
				String referer = request.getHeader("referer");
				if(RefererCompare.compare(request, "login")){//如果是登录页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				model.addAttribute("urlAddress", referer);
				
				return "templates/"+dirName+"/"+accessPath+"/jump";	
			}
		}
		
	}
	
	/**
	 * 话题收藏列表
	 * @param model
	 * @param pageForm
	 * @param topicId 话题Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/topicFavoriteList",method=RequestMethod.GET) 
	public String topicFavoriteList(ModelMap model,PageForm pageForm,Long topicId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		String dirName = templateService.findTemplateDir_cache();
		
		
		String accessPath = accessSourceDeviceManage.accessDevices(request);
	   
		//调用分页算法代码
		PageView<Favorites> pageView = new PageView<Favorites>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	if(topicId != null && topicId > 0L){
	  		Topic topicInfo = topicManage.queryTopicCache(topicId);//查询缓存
	  		if(topicInfo != null && topicInfo.getUserName().equals(accessUser.getUserName())){
	  			//当前页
	  			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
	  			
	  			
	  			QueryResult<Favorites> qr = favoriteService.findFavoritePageByTopicId(firstIndex,pageView.getMaxresult(),topicId);
	  			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
	  				for(Favorites favorites : qr.getResultlist()){
	  					Topic topic = topicManage.queryTopicCache(favorites.getTopicId());//查询缓存
	  					if(topic != null){
	  						favorites.setTopicTitle(topic.getTitle());
	  					}
	  				}
	  			}
	  			//将查询结果集传给分页List
	  			pageView.setQueryResult(qr);
	  		}
	  	}
	  	
	  	
		
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			model.addAttribute("pageView", pageView);
			return "templates/"+dirName+"/"+accessPath+"/topicFavoriteList";	
		}
	}
	
	/**
	 * 问题收藏列表
	 * @param model
	 * @param pageForm
	 * @param questionId 问题Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/questionFavoriteList",method=RequestMethod.GET) 
	public String questionFavoriteList(ModelMap model,PageForm pageForm,Long questionId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		String dirName = templateService.findTemplateDir_cache();
		
		
		String accessPath = accessSourceDeviceManage.accessDevices(request);
	   
		//调用分页算法代码
		PageView<Favorites> pageView = new PageView<Favorites>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	if(questionId != null && questionId > 0L){
	  		Question questionInfo = questionManage.query_cache_findById(questionId);//查询缓存
	  		if(questionInfo != null && questionInfo.getUserName().equals(accessUser.getUserName())){
	  			//当前页
	  			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
	  			
	  			
	  			QueryResult<Favorites> qr = favoriteService.findFavoritePageByQuestionId(firstIndex,pageView.getMaxresult(),questionId);
	  			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
	  				for(Favorites favorites : qr.getResultlist()){
	  					Question question = questionManage.query_cache_findById(favorites.getQuestionId());//查询缓存
	  					if(question != null){
	  						favorites.setQuestionTitle(question.getTitle());
	  					}
	  				}
	  			}
	  			//将查询结果集传给分页List
	  			pageView.setQueryResult(qr);
	  		}
	  	}
	  	
	  	
		
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			model.addAttribute("pageView", pageView);
			return "templates/"+dirName+"/"+accessPath+"/questionFavoriteList";	
		}
	}
	
	
	/**
	 * 收藏夹列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/favoriteList",method=RequestMethod.GET) 
	public String favoriteList(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		String dirName = templateService.findTemplateDir_cache();
		
		
		String accessPath = accessSourceDeviceManage.accessDevices(request);
	   
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	
	  	
		//调用分页算法代码
		PageView<Favorites> pageView = new PageView<Favorites>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		QueryResult<Favorites> qr = favoriteService.findFavoriteByUserId(accessUser.getUserId(),accessUser.getUserName(),firstIndex,pageView.getMaxresult());
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(Favorites favorites : qr.getResultlist()){
				if(favorites.getModule().equals(10)){//话题
					Topic topic = topicManage.queryTopicCache(favorites.getTopicId());//查询缓存
					if(topic != null){
						favorites.setTopicTitle(topic.getTitle());
					}
				}else if(favorites.getModule().equals(20)){//问题
					Question question = questionManage.query_cache_findById(favorites.getQuestionId());//查询缓存
					if(question != null){
						favorites.setQuestionTitle(question.getTitle());
					}
				}
				
			}
		}
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			model.addAttribute("pageView", pageView);
			return "templates/"+dirName+"/"+accessPath+"/favoriteList";	
		}
	}
	
	
	/**
	 * 删除收藏
	 * @param model
	 * @param jumpUrl 跳转地址   页面post方式提交有效
	 * @param token 令牌标记
	 * @param remindId 收藏Id
	 */
	@RequestMapping(value="/user/control/deleteFavorite", method=RequestMethod.POST)
	@RoleAnnotation(resourceCode=ResourceEnum._3002000)
	public String deleteFavorite(ModelMap model,String jumpUrl,String token,String favoriteId,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,String> error = new HashMap<String,String>();//错误
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("favorite", ErrorView._21.name());//只读模式不允许提交数据
		}
		
		//判断令牌
		if(token != null && !"".equals(token.trim())){	
			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
				if(!token_sessionid.equals(token)){
					error.put("token", ErrorView._13.name());
				}
			}else{
				error.put("token", ErrorView._12.name());
			}
		}else{
			error.put("token", ErrorView._11.name());
		}
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	Favorites favorites = null;
	  	//话题收藏Id
	  	String topicFavoriteId = null;
	  	//问题收藏Id
	  	String questionFavoriteId = null;
  		if(favoriteId == null || "".equals(favoriteId.trim())){
  			error.put("favorite", ErrorView._1530.name());//收藏Id不存在
  		}else{
  			favorites  = favoriteService.findById(favoriteId.trim());
  			if(favorites != null){
  				if(favorites.getUserName().equals(accessUser.getUserName())){
  					if(favorites.getModule().equals(10)){//话题模块
  						topicFavoriteId = favoriteManage.createTopicFavoriteId(favorites.getTopicId(), accessUser.getUserId());
  					}else if(favorites.getModule().equals(20)){//问题模块
  						questionFavoriteId = favoriteManage.createQuestionFavoriteId(favorites.getQuestionId(), accessUser.getUserId());
  					}
  					
  				}else{
  					error.put("favorite", ErrorView._1560.name());//本收藏不属于当前用户
  				}
  			}else{
  				error.put("favorite", ErrorView._1550.name());//收藏不存在
  			}
  		}
  		
		if(error.size() == 0){
			if(favorites.getModule().equals(10)){//话题模块
				int i = favoriteService.deleteFavorite(favoriteId.trim(),topicFavoriteId, null);
				if(i == 0){
					error.put("favorite", ErrorView._1540.name());//删除收藏失败
				}
				//删除收藏缓存
				favoriteManage.delete_cache_findTopicFavoriteById(topicFavoriteId);
				favoriteManage.delete_cache_findFavoriteCountByTopicId(favorites.getTopicId());
			}else if(favorites.getModule().equals(20)){//问题模块
				int i = favoriteService.deleteFavorite(favoriteId.trim(),null,questionFavoriteId);
				if(i == 0){
					error.put("favorite", ErrorView._1540.name());//删除收藏失败
				}
				//删除收藏缓存
				favoriteManage.delete_cache_findQuestionFavoriteById(questionFavoriteId);
				favoriteManage.delete_cache_findFavoriteCountByQuestionId(favorites.getQuestionId());
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
			String dirName = templateService.findTemplateDir_cache();
			String accessPath = accessSourceDeviceManage.accessDevices(request);
			if(error != null && error.size() >0){//如果有错误
				
				for (Map.Entry<String,String> entry : returnError.entrySet()) {		 
					model.addAttribute("message",entry.getValue());//提示
		  			return "templates/"+dirName+"/"+accessPath+"/message";
				}
					
			}
			
			
			if(jumpUrl != null && !"".equals(jumpUrl.trim())){
				String url = Base64.decodeBase64URL(jumpUrl.trim());
				return "redirect:"+url;
			}else{//默认跳转
				model.addAttribute("message", "删除收藏成功");
				String referer = request.getHeader("referer");
				if(RefererCompare.compare(request, "login")){//如果是登录页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				model.addAttribute("urlAddress", referer);
				
				return "templates/"+dirName+"/"+accessPath+"/jump";	
			}
		}
		
	}
	
	
	
	/**
	 * 话题取消隐藏用户列表(只记录'输入密码可见','积分购买可见','余额购买可见'的用户)
	 * @param model
	 * @param pageForm
	 * @param topicId 话题Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/topicUnhideList",method=RequestMethod.GET) 
	public String topicUnhideList(ModelMap model,PageForm pageForm,Long topicId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		String dirName = templateService.findTemplateDir_cache();
		
		
		String accessPath = accessSourceDeviceManage.accessDevices(request);
	   
		//调用分页算法代码
		PageView<TopicUnhide> pageView = new PageView<TopicUnhide>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	if(topicId != null && topicId > 0L){
	  		Topic topicInfo = topicManage.queryTopicCache(topicId);//查询缓存
	  		if(topicInfo != null && topicInfo.getUserName().equals(accessUser.getUserName())){
	  			//当前页
	  			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
	  			
	  			
	  			QueryResult<TopicUnhide> qr = topicService.findTopicUnhidePageByTopicId(firstIndex,pageView.getMaxresult(),topicId);

	  			//将查询结果集传给分页List
	  			pageView.setQueryResult(qr);
	  		}
	  	}
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			model.addAttribute("pageView", pageView);
			return "templates/"+dirName+"/"+accessPath+"/topicUnhideList";	
		}
	}
	
	/**
	 * 用户动态列表
	 * @param model
	 * @param pageForm
	 * @param topicId 话题Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/userDynamicList",method=RequestMethod.GET) 
	public String userDynamicList(ModelMap model,PageForm pageForm,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		String dirName = templateService.findTemplateDir_cache();
		
		
		String accessPath = accessSourceDeviceManage.accessDevices(request);
	   
		
		
		//调用分页算法代码
		PageView<UserDynamic> pageView = new PageView<UserDynamic>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	
	  	
	  	Long _userId = null;
	  	String _userName = null;
	  	if(userName != null && !"".equals(userName.trim())){
	  		if(userName.equals(accessUser.getUserName())){//自身
	  			_userId = accessUser.getUserId();
	  			_userName = accessUser.getUserName();
	  		}else{//其它用户
	  			User user = userManage.query_cache_findUserByUserName(userName);
	  			if(user != null && user.getAllowUserDynamic() != null && user.getAllowUserDynamic()){//允许显示
	  				_userId = user.getId();
	  				_userName = user.getUserName();
	  			}
	  		}
	  	}else{
	  		_userId = accessUser.getUserId();
	  		_userName = accessUser.getUserName();
	  	}

	  	if(_userId != null){
	  		//当前页
  			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
	  		QueryResult<UserDynamic> qr = userService.findUserDynamicPage(_userId,_userName,firstIndex,pageView.getMaxresult());
	  		
	  		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(UserDynamic userDynamic : qr.getResultlist()){
					User user = userManage.query_cache_findUserByUserName(userDynamic.getUserName());
					if(user != null){
						userDynamic.setNickname(user.getNickname());
						userDynamic.setAvatarPath(user.getAvatarPath());
						userDynamic.setAvatarName(user.getAvatarName());
					}
					
				
					
					
					if(userDynamic.getTopicId() >0L){
						Topic topicInfo = topicManage.queryTopicCache(userDynamic.getTopicId());//查询缓存
						if(topicInfo != null){
							userDynamic.setTopicTitle(topicInfo.getTitle());
							userDynamic.setTopicViewTotal(topicInfo.getViewTotal());
							userDynamic.setTopicCommentTotal(topicInfo.getCommentTotal());
							
							List<String> topicRoleNameList = userRoleManage.queryAllowViewTopicRoleName(topicInfo.getTagId());
							
							
							if(topicRoleNameList != null && topicRoleNameList.size() >0){
								userDynamic.setAllowRoleViewList(topicRoleNameList);//话题允许查看的角色名称集合
							}
							SystemSetting systemSetting = settingService.findSystemSetting_cache();
							//话题内容摘要MD5
							String topicContentDigest_link = "";
							String topicContentDigest_video = "";
							
							//处理文件防盗链
							if(topicInfo.getContent() != null && !"".equals(topicInfo.getContent().trim()) && systemSetting.getFileSecureLinkSecret() != null && !"".equals(systemSetting.getFileSecureLinkSecret().trim())){
								//解析上传的文件完整路径名称
								Map<String,String> analysisFullFileNameMap = topicManage.query_cache_analysisFullFileName(topicInfo.getContent(),topicInfo.getId());
								if(analysisFullFileNameMap != null && analysisFullFileNameMap.size() >0){
									
									
									Map<String,String> newFullFileNameMap = new HashMap<String,String>();//新的完整路径名称 key: 完整路径名称 value: 重定向接口
									for (Map.Entry<String,String> entry : analysisFullFileNameMap.entrySet()) {

										newFullFileNameMap.put(entry.getKey(), SecureLink.createDownloadRedirectLink(entry.getKey(),entry.getValue(),topicInfo.getTagId(),systemSetting.getFileSecureLinkSecret()));
									}
									
									Integer topicContentUpdateMark = topicManage.query_cache_markUpdateTopicStatus(topicInfo.getId(), Integer.parseInt(RandomStringUtils.randomNumeric(8)));
									//生成处理'上传的文件完整路径名称'Id
									String processFullFileNameId = topicManage.createProcessFullFileNameId(topicInfo.getId(),topicContentUpdateMark,newFullFileNameMap);
									
									topicInfo.setContent(topicManage.query_cache_processFullFileName(topicInfo.getContent(),"topic",newFullFileNameMap,processFullFileNameId));
									
									topicContentDigest_link = cms.utils.MD5.getMD5(processFullFileNameId);
								}
								
								
							}
							
							
							//处理视频播放器标签
							if(topicInfo.getContent() != null && !"".equals(topicInfo.getContent().trim())){
								Integer topicContentUpdateMark = topicManage.query_cache_markUpdateTopicStatus(topicInfo.getId(), Integer.parseInt(RandomStringUtils.randomNumeric(8)));
								
								//生成处理'视频播放器'Id
								String processVideoPlayerId = mediaProcessQueueManage.createProcessVideoPlayerId(topicInfo.getId(),topicContentUpdateMark);
								
								//处理视频播放器标签
								String content = mediaProcessQueueManage.query_cache_processVideoPlayer(topicInfo.getContent(),processVideoPlayerId+"|"+topicContentDigest_link,topicInfo.getTagId(),systemSetting.getFileSecureLinkSecret());
								topicInfo.setContent(content);
								
								topicContentDigest_video = cms.utils.MD5.getMD5(processVideoPlayerId);
							}

							
							//处理隐藏标签
							if(userDynamic.getModule().equals(100) && topicInfo.getContent() != null && !"".equals(topicInfo.getContent().trim())){
								//允许可见的隐藏标签
								List<Integer> visibleTagList = new ArrayList<Integer>();
								if(accessUser != null){
									//如果话题由当前用户发表，则显示全部隐藏内容
									if(topicInfo.getIsStaff() == false && topicInfo.getUserName().equals(accessUser.getUserName())){
										visibleTagList.add(HideTagType.PASSWORD.getName());
										visibleTagList.add(HideTagType.COMMENT.getName());
										visibleTagList.add(HideTagType.GRADE.getName());
										visibleTagList.add(HideTagType.POINT.getName());
										visibleTagList.add(HideTagType.AMOUNT.getName());
									}else{
										//解析隐藏标签
										Map<Integer,Object> analysisHiddenTagMap = topicManage.query_cache_analysisHiddenTag(topicInfo.getContent(),topicInfo.getId());
										if(analysisHiddenTagMap != null && analysisHiddenTagMap.size() >0){
											for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {
												
												if(entry.getKey().equals(HideTagType.PASSWORD.getName())){//输入密码可见
													//话题取消隐藏Id
												  	String topicUnhideId = topicManage.createTopicUnhideId(accessUser.getUserId(), HideTagType.PASSWORD.getName(), userDynamic.getTopicId());
												  
													TopicUnhide topicUnhide = topicManage.query_cache_findTopicUnhideById(topicUnhideId);
											  		
											  		if(topicUnhide != null){
											  			visibleTagList.add(HideTagType.PASSWORD.getName());//当前话题已经取消隐藏
												  	}
												}else if(entry.getKey().equals(HideTagType.COMMENT.getName())){//评论话题可见
													Boolean isUnhide = topicManage.query_cache_findWhetherCommentTopic(userDynamic.getTopicId(),accessUser.getUserName());
													if(isUnhide){
														visibleTagList.add(HideTagType.COMMENT.getName());//当前话题已经取消隐藏
													}
												}else if(entry.getKey().equals(HideTagType.GRADE.getName())){//超过等级可见
													User _user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
													if(_user != null){
														List<UserGrade> userGradeList = userGradeService.findAllGrade_cache();//取得用户所有等级
														if(userGradeList != null && userGradeList.size() >0){
															for(UserGrade userGrade : userGradeList){
																if(_user.getPoint() >= userGrade.getNeedPoint() && (Long)entry.getValue() <=userGrade.getNeedPoint()){
																	visibleTagList.add(HideTagType.GRADE.getName());//当前话题已经取消隐藏
																	
																	break;
																}
															} 
																
															
														}
													}
													
												}else if(entry.getKey().equals(HideTagType.POINT.getName())){//积分购买可见
													//话题取消隐藏Id
												  	String topicUnhideId = topicManage.createTopicUnhideId(accessUser.getUserId(), HideTagType.POINT.getName(), userDynamic.getTopicId());
												  
													TopicUnhide topicUnhide = topicManage.query_cache_findTopicUnhideById(topicUnhideId);
											  		
											  		if(topicUnhide != null){
											  			visibleTagList.add(HideTagType.POINT.getName());//当前话题已经取消隐藏
												  	}
												}else if(entry.getKey().equals(HideTagType.AMOUNT.getName())){//余额购买可见
													//话题取消隐藏Id
												  	String topicUnhideId = topicManage.createTopicUnhideId(accessUser.getUserId(), HideTagType.AMOUNT.getName(), userDynamic.getTopicId());
												  	TopicUnhide topicUnhide = topicManage.query_cache_findTopicUnhideById(topicUnhideId);
											  		
											  		if(topicUnhide != null){
											  			visibleTagList.add(HideTagType.AMOUNT.getName());//当前话题已经取消隐藏
												  	}
												}
												
											}
										
										
										
											//内容含有隐藏标签类型
											LinkedHashMap<Integer,Boolean> hideTagTypeMap = new LinkedHashMap<Integer,Boolean>();//key:内容含有隐藏标签类型  10.输入密码可见  20.评论话题可见  30.达到等级可见 40.积分购买可见 50.余额购买可见  value:当前用户是否已对隐藏内容解锁	
											for (HideTagType hideTagType : HideTagType.values()) {//按枚举类的顺序排序
									            for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {
													if(entry.getKey().equals(hideTagType.getName())){
														if(visibleTagList.contains(entry.getKey())){
															hideTagTypeMap.put(entry.getKey(), true);
															
														}else{
															hideTagTypeMap.put(entry.getKey(), false);
														}
														break;
													}
												}
									        }
											userDynamic.setHideTagTypeMap(hideTagTypeMap);
											
										}
										
										
										
									}
								}
								
								Integer topicContentUpdateMark = topicManage.query_cache_markUpdateTopicStatus(userDynamic.getTopicId(), Integer.parseInt(RandomStringUtils.randomNumeric(8)));
								
								//生成处理'隐藏标签'Id
								String processHideTagId = topicManage.createProcessHideTagId(userDynamic.getTopicId(),topicContentUpdateMark, visibleTagList);
								
								//处理隐藏标签
								String content = topicManage.query_cache_processHiddenTag(topicInfo.getContent(),visibleTagList,processHideTagId+"|"+topicContentDigest_link+"|"+ topicContentDigest_video);
								userDynamic.setTopicContent(content);
								
								//如果话题不是当前用户发表的，则检查用户对话题的查看权限
								if(topicInfo.getIsStaff() == false && !topicInfo.getUserName().equals(accessUser.getUserName())){
									//是否有当前功能操作权限
									boolean flag = userRoleManage.isPermission(ResourceEnum._1001000,topicInfo.getTagId());
									if(!flag){//如果没有查看权限
										userDynamic.setTopicContent("");
									}
								}
								
							}
							
							
						}
					}
					

					if(userDynamic.getModule().equals(200)){//评论
						Comment comment = commentManage.query_cache_findByCommentId(userDynamic.getCommentId());
						if(comment != null){
							userDynamic.setCommentContent(comment.getContent());
						}
						
					}
					if(userDynamic.getModule().equals(300)){//引用评论
						Comment comment = commentManage.query_cache_findByCommentId(userDynamic.getCommentId());
						if(comment != null){
							userDynamic.setCommentContent(comment.getContent());
						}
						Comment quoteComment = commentManage.query_cache_findByCommentId(userDynamic.getQuoteCommentId());
						if(quoteComment != null && quoteComment.getStatus().equals(20)){
							userDynamic.setQuoteCommentContent(textFilterManage.filterText(textFilterManage.specifyHtmlTagToText(quoteComment.getContent())));
						}
					}
					if(userDynamic.getModule().equals(400)){//回复
						Reply reply = commentManage.query_cache_findReplyByReplyId(userDynamic.getReplyId());
						if(reply != null){
							userDynamic.setReplyContent(reply.getContent());
						}
					}
					
					//问题
					if(userDynamic.getQuestionId() >0L){
						Question questionInfo = questionManage.query_cache_findById(userDynamic.getQuestionId());//查询缓存
						if(questionInfo != null){
							userDynamic.setQuestionTitle(questionInfo.getTitle());
							userDynamic.setQuestionViewTotal(questionInfo.getViewTotal());
							userDynamic.setQuestionAnswerTotal(questionInfo.getAnswerTotal());
							userDynamic.setQuestionContent(questionInfo.getContent());
						}
						
					}
					if(userDynamic.getModule().equals(600)){//600.答案
						Answer answer = answerManage.query_cache_findByAnswerId(userDynamic.getAnswerId());
						if(answer != null){
							userDynamic.setAnswerContent(answer.getContent());
						}
						
					}
					if(userDynamic.getModule().equals(700)){//700.答案回复
						AnswerReply answerReply = answerManage.query_cache_findReplyByReplyId(userDynamic.getAnswerReplyId());
						if(answerReply != null){
							userDynamic.setAnswerReplyContent(answerReply.getContent());
						}
					}
					
					
					
					
				}
			}

	  		//将查询结果集传给分页List
  			pageView.setQueryResult(qr);
	  		
	  	}
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			model.addAttribute("pageView", pageView);
			return "templates/"+dirName+"/"+accessPath+"/userDynamicList";	
		}
	}
	
	/**
	 * 话题点赞列表
	 * @param model
	 * @param pageForm
	 * @param topicId 话题Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/topicLikeList",method=RequestMethod.GET) 
	public String topicLikeList(ModelMap model,PageForm pageForm,Long topicId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		String dirName = templateService.findTemplateDir_cache();
		
		
		String accessPath = accessSourceDeviceManage.accessDevices(request);
	   
		//调用分页算法代码
		PageView<Like> pageView = new PageView<Like>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	if(topicId != null && topicId > 0L){
	  		Topic topicInfo = topicManage.queryTopicCache(topicId);//查询缓存
	  		if(topicInfo != null && topicInfo.getUserName().equals(accessUser.getUserName())){
	  			//当前页
	  			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
	  			
	  			
	  			QueryResult<Like> qr = likeService.findLikePageByTopicId(firstIndex,pageView.getMaxresult(),topicId);
	  			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
	  				for(Like like : qr.getResultlist()){
	  					Topic topic = topicManage.queryTopicCache(like.getTopicId());//查询缓存
	  					if(topic != null){
	  						like.setTopicTitle(topic.getTitle());
	  					}
	  				}
	  			}
	  			//将查询结果集传给分页List
	  			pageView.setQueryResult(qr);
	  		}
	  	}
	  	
	  	
		
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			model.addAttribute("pageView", pageView);
			return "templates/"+dirName+"/"+accessPath+"/topicLikeList";	
		}
	}
	
	
	/**
	 * 点赞列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/likeList",method=RequestMethod.GET) 
	public String likeList(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		String dirName = templateService.findTemplateDir_cache();
		
		
		String accessPath = accessSourceDeviceManage.accessDevices(request);
	   
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	
	  	
		//调用分页算法代码
		PageView<Like> pageView = new PageView<Like>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		QueryResult<Like> qr = likeService.findLikeByUserId(accessUser.getUserId(),accessUser.getUserName(),firstIndex,pageView.getMaxresult());
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(Like like : qr.getResultlist()){
				Topic topic = topicManage.queryTopicCache(like.getTopicId());//查询缓存
				if(topic != null){
					like.setTopicTitle(topic.getTitle());
				}
			}
		}
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			model.addAttribute("pageView", pageView);
			return "templates/"+dirName+"/"+accessPath+"/likeList";	
		}
	}
	
	
	/**
	 * 删除点赞
	 * @param model
	 * @param jumpUrl 跳转地址   页面post方式提交有效
	 * @param token 令牌标记
	 * @param likeId 点赞Id
	 */
	@RequestMapping(value="/user/control/deleteLike", method=RequestMethod.POST)
	@RoleAnnotation(resourceCode=ResourceEnum._4002000)
	public String deleteLike(ModelMap model,String jumpUrl,String token,String likeId,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,String> error = new HashMap<String,String>();//错误
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("like", ErrorView._21.name());//只读模式不允许提交数据
		}
		
		//判断令牌
		if(token != null && !"".equals(token.trim())){	
			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
				if(!token_sessionid.equals(token)){
					error.put("token", ErrorView._13.name());
				}
			}else{
				error.put("token", ErrorView._12.name());
			}
		}else{
			error.put("token", ErrorView._11.name());
		}
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	Like like = null;
	  	//话题点赞Id
	  	String topicLikeId = null;
  		if(likeId == null || "".equals(likeId.trim())){
  			error.put("like", ErrorView._1730.name());//点赞Id不存在
  		}else{
  			like  = likeService.findById(likeId.trim());
  			if(like != null){
  				if(like.getUserName().equals(accessUser.getUserName())){
  					topicLikeId = likeManage.createTopicLikeId(like.getTopicId(), accessUser.getUserId());
  				}else{
  					error.put("like", ErrorView._1760.name());//本点赞不属于当前用户
  				}
  			}else{
  				error.put("like", ErrorView._1750.name());//点赞不存在
  			}
  		}
  		
		if(error.size() == 0){
			int i = likeService.deleteLike(likeId.trim(),topicLikeId);
			if(i == 0){
				error.put("like", ErrorView._1740.name());//删除点赞失败
			}
			//删除点赞缓存
			likeManage.delete_cache_findTopicLikeById(topicLikeId);
			likeManage.delete_cache_findLikeCountByTopicId(like.getTopicId());
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
			String dirName = templateService.findTemplateDir_cache();
			String accessPath = accessSourceDeviceManage.accessDevices(request);
			if(error != null && error.size() >0){//如果有错误
				
				for (Map.Entry<String,String> entry : returnError.entrySet()) {		 
					model.addAttribute("message",entry.getValue());//提示
		  			return "templates/"+dirName+"/"+accessPath+"/message";
				}
					
			}
			
			
			if(jumpUrl != null && !"".equals(jumpUrl.trim())){
				String url = Base64.decodeBase64URL(jumpUrl.trim());
				return "redirect:"+url;
			}else{//默认跳转
				model.addAttribute("message", "删除点赞成功");
				String referer = request.getHeader("referer");
				if(RefererCompare.compare(request, "login")){//如果是登录页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				model.addAttribute("urlAddress", referer);
				
				return "templates/"+dirName+"/"+accessPath+"/jump";	
			}
		}
		
	}
	/**
	 * 关注列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/followList",method=RequestMethod.GET) 
	public String followList(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		String dirName = templateService.findTemplateDir_cache();
		
		
		String accessPath = accessSourceDeviceManage.accessDevices(request);
	   
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	
	  	
		//调用分页算法代码
		PageView<Follow> pageView = new PageView<Follow>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		QueryResult<Follow> qr = followService.findFollowByUserName(accessUser.getUserId(),accessUser.getUserName(),firstIndex,pageView.getMaxresult());
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(Follow follow : qr.getResultlist()){
				User user = userManage.query_cache_findUserByUserName(follow.getFriendUserName());//查询缓存
				if(user != null){
					follow.setFriendNickname(user.getNickname());
					if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
						follow.setFriendAvatarPath(user.getAvatarPath());
						follow.setFriendAvatarName(user.getAvatarName());
					}		
				}
			}
		}
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			model.addAttribute("pageView", pageView);
			return "templates/"+dirName+"/"+accessPath+"/followList";	
		}
	}
	
	
	/**
	 * 粉丝列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/followerList",method=RequestMethod.GET) 
	public String followerList(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		String dirName = templateService.findTemplateDir_cache();
		
		
		String accessPath = accessSourceDeviceManage.accessDevices(request);
	   
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	
	  	
		//调用分页算法代码
		PageView<Follower> pageView = new PageView<Follower>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		QueryResult<Follower> qr = followService.findFollowerByUserName(accessUser.getUserId(),accessUser.getUserName(),firstIndex,pageView.getMaxresult());
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(Follower follower : qr.getResultlist()){
				User user = userManage.query_cache_findUserByUserName(follower.getFriendUserName());//查询缓存
				if(user != null){
					follower.setFriendNickname(user.getNickname());
					if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
						follower.setFriendAvatarPath(user.getAvatarPath());
						follower.setFriendAvatarName(user.getAvatarName());
					}		
				}
			}
		}
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			model.addAttribute("pageView", pageView);
			return "templates/"+dirName+"/"+accessPath+"/followerList";	
		}
	}
	
	/**
	 * 删除关注
	 * @param model
	 * @param jumpUrl 跳转地址   页面post方式提交有效
	 * @param token 令牌标记
	 * @param followId 关注Id
	 */
	@RequestMapping(value="/user/control/deleteFollow", method=RequestMethod.POST)
	@RoleAnnotation(resourceCode=ResourceEnum._5002000)
	public String deleteFollow(ModelMap model,String jumpUrl,String token,String followId,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,String> error = new HashMap<String,String>();//错误
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("follow", ErrorView._21.name());//只读模式不允许提交数据
		}
		
		//判断令牌
		if(token != null && !"".equals(token.trim())){	
			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
				if(!token_sessionid.equals(token)){
					error.put("token", ErrorView._13.name());
				}
			}else{
				error.put("token", ErrorView._12.name());
			}
		}else{
			error.put("token", ErrorView._11.name());
		}
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	Follow follow = null;
	  	
	  	
	  	
  		if(followId == null || "".equals(followId.trim())){
  			error.put("followId", ErrorView._1830.name());//关注Id不存在
  		}else{
  			follow  = followService.findById(followId.trim());
  			if(follow != null){
  				if(!follow.getUserName().equals(accessUser.getUserName())){
  					error.put("follow", ErrorView._1860.name());//本关注不属于当前用户
  				}
  			}else{
  				error.put("follow", ErrorView._1850.name());//关注不存在
  			}
  		}
  		
		if(error.size() == 0){
			String[] idGroup = followId.trim().split("-");
			//粉丝Id
		  	String followerId = idGroup[1]+"-"+idGroup[0];
			
			int i = followService.deleteFollow(followId.trim(),followerId);
			if(i == 0){
				error.put("follow", ErrorView._1840.name());//删除关注失败
			}
			//删除关注缓存
			followManage.delete_cache_findById(followId.trim());
			followerManage.delete_cache_followerCount(follow.getFriendUserName());
			
			followManage.delete_cache_findAllFollow(accessUser.getUserName());
			followManage.delete_cache_followCount(accessUser.getUserName());
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
			String dirName = templateService.findTemplateDir_cache();
			String accessPath = accessSourceDeviceManage.accessDevices(request);
			if(error != null && error.size() >0){//如果有错误
				
				for (Map.Entry<String,String> entry : returnError.entrySet()) {		 
					model.addAttribute("message",entry.getValue());//提示
		  			return "templates/"+dirName+"/"+accessPath+"/message";
				}
					
			}
			
			
			if(jumpUrl != null && !"".equals(jumpUrl.trim())){
				String url = Base64.decodeBase64URL(jumpUrl.trim());
				return "redirect:"+url;
			}else{//默认跳转
				model.addAttribute("message", "删除关注成功");
				String referer = request.getHeader("referer");
				if(RefererCompare.compare(request, "login")){//如果是登录页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				model.addAttribute("urlAddress", referer);
				
				return "templates/"+dirName+"/"+accessPath+"/jump";	
			}
		}
		
	}
	
	/**
	 * 账户余额
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/balance",method=RequestMethod.GET) 
	public String balanceUI(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
		
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();

		
		User user = userService.findUserByUserName(accessUser.getUserName());

		model.addAttribute("deposit",user.getDeposit());//当前预存款
		returnValue.put("deposit",user.getDeposit());
		if(user != null){
			//调用分页算法代码
			PageView<PaymentLog> pageView = new PageView<PaymentLog>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			
			if(user != null){
				QueryResult<PaymentLog> qr =  paymentService.findPaymentLogPage(user.getId(),user.getUserName(),firstIndex, pageView.getMaxresult());
				
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);
				model.addAttribute("pageView", pageView);
				returnValue.put("pageView", pageView);
			}
		}
		if(isAjax == true){
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
			
			String accessPath = accessSourceDeviceManage.accessDevices(request);
		   
			
			return "templates/"+dirName+"/"+accessPath+"/balance";	
		}
		
		
	}
	
	/**
	 * 会员卡订单列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/membershipCardOrderList",method=RequestMethod.GET) 
	public String membershipCardOrderUI(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
			
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();

	
		//调用分页算法代码
		PageView<MembershipCardOrder> pageView = new PageView<MembershipCardOrder>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		
		QueryResult<MembershipCardOrder> qr =  membershipCardService.findMembershipCardOrderByUserName(accessUser.getUserName(),firstIndex, pageView.getMaxresult());
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		model.addAttribute("pageView", pageView);
		
		
		if(isAjax == true){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
			
			String accessPath = accessSourceDeviceManage.accessDevices(request);
		   
			
			return "templates/"+dirName+"/"+accessPath+"/membershipCardOrderList";	
		}
		
		
	}
	
	
	
	
	/**
	 * 我的问题列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/questionList",method=RequestMethod.GET) 
	public String questionListUI(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		
		//调用分页算法代码
		PageView<Question> pageView = new PageView<Question>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		 //获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
    	
    		
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		jpql.append(" and o.userName=?"+ (params.size()+1));
		params.add(accessUser.getUserName());
		
		jpql.append(" and o.status<?"+ (params.size()+1));
		params.add(100);
		
		jpql.append(" and o.isStaff=?"+ (params.size()+1));
		params.add(false);
		
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
	
		
		QueryResult<Question> qr = questionService.getScrollData(Question.class, firstindex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);
		if(qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(Question o :qr.getResultlist()){
    			o.setIpAddress(null);//IP地址不显示
    		}
			
			List<QuestionTag> questionTagList = questionTagService.findAllQuestionTag_cache();
			
			if(questionTagList != null && questionTagList.size() >0){
				for(Question question : qr.getResultlist()){
					List<QuestionTagAssociation> questionTagAssociationList = questionManage.query_cache_findQuestionTagAssociationByQuestionId(question.getId());
					if(questionTagAssociationList != null && questionTagAssociationList.size() >0){
						for(QuestionTag questionTag : questionTagList){
							for(QuestionTagAssociation questionTagAssociation : questionTagAssociationList){
								if(questionTagAssociation.getQuestionTagId().equals(questionTag.getId())){
									questionTagAssociation.setQuestionTagName(questionTag.getName());
									question.addQuestionTagAssociation(questionTagAssociation);
									break;
								}
							}
						}
					}
				}
			}
			
			
			
			
		}
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);		
    	
		model.addAttribute("pageView", pageView);
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
		    return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/questionList";	
		}
	}
	
	
	
	
	/**
	 * 我的答案列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/answerList",method=RequestMethod.GET) 
	public String answerListUI(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		
		//调用分页算法代码
		PageView<Answer> pageView = new PageView<Answer>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
    		
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		jpql.append(" and o.userName=?"+ (params.size()+1));
		params.add(accessUser.getUserName());
		
		jpql.append(" and o.isStaff=?"+ (params.size()+1));
		params.add(false);
		
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
	
		
		QueryResult<Answer> qr = answerService.getScrollData(Answer.class, firstindex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);
		if(qr.getResultlist() != null && qr.getResultlist().size() >0){
			List<Long> questionIdList = new ArrayList<Long>();
			for(Answer o :qr.getResultlist()){
    			o.setIpAddress(null);//IP地址不显示
    			
    			o.setContent(textFilterManage.filterText(o.getContent()));
    			if(!questionIdList.contains(o.getQuestionId())){
    				questionIdList.add(o.getQuestionId());
    			}
    		}
			List<Question> questionList = questionService.findTitleByIdList(questionIdList);
			if(questionList != null && questionList.size() >0){
				for(Answer o :qr.getResultlist()){
					for(Question question : questionList){
						if(question.getId().equals(o.getQuestionId())){
							o.setQuestionTitle(question.getTitle());
							break;
						}
					}
					
				}
			}	
		}
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);		
    	
		model.addAttribute("pageView", pageView);
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
		    return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/answerList";	
		}
	}
	
	/**
	 * 我的答案回复列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/answerReplyList",method=RequestMethod.GET) 
	public String answerReplyListUI(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		
		//调用分页算法代码
		PageView<AnswerReply> pageView = new PageView<AnswerReply>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
    		
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		jpql.append(" and o.userName=?"+ (params.size()+1));
		params.add(accessUser.getUserName());
		
		jpql.append(" and o.isStaff=?"+ (params.size()+1));
		params.add(false);
		
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
	
		
		QueryResult<AnswerReply> qr = questionService.getScrollData(AnswerReply.class, firstindex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);
		
		if(qr.getResultlist() != null && qr.getResultlist().size() >0){
			List<Long> questionIdList = new ArrayList<Long>();
			for(AnswerReply o :qr.getResultlist()){
    			o.setIpAddress(null);//IP地址不显示
    			
    			if(!questionIdList.contains(o.getQuestionId())){
    				questionIdList.add(o.getQuestionId());
    			}
    		}
			List<Question> questionList = questionService.findTitleByIdList(questionIdList);
			if(questionList != null && questionList.size() >0){
				for(AnswerReply o :qr.getResultlist()){
					for(Question question : questionList){
						if(question.getId().equals(o.getQuestionId())){
							o.setQuestionTitle(question.getTitle());
							break;
						}
					}
					
				}
			}	
		}
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);		
    	
		model.addAttribute("pageView", pageView);
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
		    return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/answerReplyList";	
		}
	}
	
	
	
	
	/**
	 * 发红包列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/giveRedEnvelopeList",method=RequestMethod.GET) 
	public String giveRedEnvelopeListUI(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	
	  	
		//调用分页算法代码
		PageView<GiveRedEnvelope> pageView = new PageView<GiveRedEnvelope>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();

		
		jpql.append(" and o.userId=?"+ (params.size()+1));
		params.add(accessUser.getUserId());
		
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("giveTime", "desc");//根据giveTime字段降序排序
		QueryResult<GiveRedEnvelope> qr = redEnvelopeService.getScrollData(GiveRedEnvelope.class,firstIndex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);
		
		if(qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(GiveRedEnvelope giveRedEnvelope : qr.getResultlist()){
				if(giveRedEnvelope.getBindTopicId() != null && giveRedEnvelope.getBindTopicId() >0L){
					Topic topic = topicManage.queryTopicCache(giveRedEnvelope.getBindTopicId());//查询缓存
					if(topic != null){
						giveRedEnvelope.setBindTopicTitle(topic.getTitle());
					}
					
				}
				
			}
			
		}
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);		
    	
		model.addAttribute("pageView", pageView);
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
		    return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/giveRedEnvelopeList";	
		}
	}
	
	
	/**
	 * 发红包金额分配列表
	 * @param model
	 * @param pageForm
	 * @param giveRedEnvelopeId 发红包Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/redEnvelopeAmountDistributionList",method=RequestMethod.GET) 
	public String redEnvelopeAmountDistributionUI(ModelMap model,PageForm pageForm,String giveRedEnvelopeId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
	    
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	
		//调用分页算法代码
		PageView<ReceiveRedEnvelope> pageView = new PageView<ReceiveRedEnvelope>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
		if(giveRedEnvelopeId != null && !"".equals(giveRedEnvelopeId.trim())){
			GiveRedEnvelope giveRedEnvelope = redEnvelopeService.findById(giveRedEnvelopeId);
			if(giveRedEnvelope != null && giveRedEnvelope.getUserId().equals(accessUser.getUserId())){
				//排序
				boolean sort = false;//true:正序 false:倒序
				
				QueryResult<ReceiveRedEnvelope> qr = redEnvelopeManage.queryReceiveRedEnvelopeByCondition(giveRedEnvelope,false,firstindex, pageView.getMaxresult(),sort);
				    
				if(giveRedEnvelope.getBindTopicId() != null && giveRedEnvelope.getBindTopicId() >0L){
					Topic topic = topicManage.queryTopicCache(giveRedEnvelope.getBindTopicId());//查询缓存
					if(topic != null){
						giveRedEnvelope.setBindTopicTitle(topic.getTitle());
					}
					
				}
		
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);
				model.addAttribute("pageView", pageView);	
				model.addAttribute("giveRedEnvelope", giveRedEnvelope);
				
				returnValue.put("pageView", pageView);
				returnValue.put("giveRedEnvelope", giveRedEnvelope);
			}
		}
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
		    return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/redEnvelopeAmountDistributionList";	
		}
	}
	
	
	/**
	 * 收红包列表
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/receiveRedEnvelopeList",method=RequestMethod.GET) 
	public String receiveRedEnvelopeUI(ModelMap model,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
	  	
	  	
	  //调用分页算法代码
  		PageView<ReceiveRedEnvelope> pageView = new PageView<ReceiveRedEnvelope>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
  		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
  		
  		
		QueryResult<ReceiveRedEnvelope> qr = redEnvelopeService.findReceiveRedEnvelopeByUserId(accessUser.getUserId(), firstIndex, pageView.getMaxresult());
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		
		if(qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(ReceiveRedEnvelope receiveRedEnvelope : qr.getResultlist()){
				User user = userManage.query_cache_findUserById(receiveRedEnvelope.getGiveUserId());
        		if(user != null){
        			receiveRedEnvelope.setGiveNickname(user.getNickname());
        			receiveRedEnvelope.setGiveUserName(user.getUserName());
        			if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
        				receiveRedEnvelope.setGiveAvatarPath(user.getAvatarPath());
        				receiveRedEnvelope.setGiveAvatarName(user.getAvatarName());
        			}		
        		}
        		
        		//如果红包还没拆，则执行拆红包
  				if(receiveRedEnvelope.getAmount() != null && receiveRedEnvelope.getAmount().compareTo(new BigDecimal("0")) ==0 && user != null){
  					GiveRedEnvelope giveRedEnvelope = redEnvelopeService.findById(receiveRedEnvelope.getGiveRedEnvelopeId());
  					if(giveRedEnvelope != null){
  						//查询用户领取到的红包金额
      				    BigDecimal amount = redEnvelopeManage.queryReceiveRedEnvelopeAmount(giveRedEnvelope,user.getId());
      					if(amount != null && amount.compareTo(new BigDecimal("0")) >0 ){
      						redEnvelopeManage.unwrapRedEnvelope(receiveRedEnvelope,amount,user.getId(),user.getUserName());
      					}
  					}
  					
  				}
        		
			}
		}
  		
		
		
		model.addAttribute("pageView", pageView);
		
		if(isAjax){
			WebUtil.writeToWeb(JsonUtils.toJSONString(pageView), "json", response);
			return null;
		}else{
			String dirName = templateService.findTemplateDir_cache();
		    return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/receiveRedEnvelopeList";	
		}
	}
	
}
