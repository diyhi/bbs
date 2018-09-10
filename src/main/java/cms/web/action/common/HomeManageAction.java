package cms.web.action.common;

import java.awt.image.BufferedImage;
import java.io.File;
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
import cms.bean.message.PrivateMessage;
import cms.bean.message.SubscriptionSystemNotify;
import cms.bean.message.SystemNotify;
import cms.bean.message.UnreadMessage;
import cms.bean.topic.Comment;
import cms.bean.topic.Reply;
import cms.bean.topic.Tag;
import cms.bean.topic.Topic;
import cms.bean.user.AccessUser;
import cms.bean.user.FormCaptcha;
import cms.bean.user.PointLog;
import cms.bean.user.User;
import cms.bean.user.UserCustom;
import cms.bean.user.UserGrade;
import cms.bean.user.UserInputValue;
import cms.bean.user.UserLoginLog;
import cms.service.message.PrivateMessageService;
import cms.service.message.SystemNotifyService;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.service.topic.CommentService;
import cms.service.topic.TagService;
import cms.service.topic.TopicService;
import cms.service.user.UserCustomService;
import cms.service.user.UserGradeService;
import cms.service.user.UserService;
import cms.utils.Base64;
import cms.utils.HtmlEscape;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.utils.PathUtil;
import cms.utils.RefererCompare;
import cms.utils.SHA;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.CSRFTokenManage;
import cms.web.action.FileManage;
import cms.web.action.TextFilterManage;
import cms.web.action.message.PrivateMessageManage;
import cms.web.action.message.SubscriptionSystemNotifyManage;
import cms.web.action.message.SystemNotifyManage;
import cms.web.action.setting.SettingManage;
import cms.web.action.sms.SmsManage;
import cms.web.action.thumbnail.ThumbnailManage;
import cms.web.action.user.UserManage;
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
	
	
	@Resource CaptchaManage captchaManage;
	
	
	@Resource SettingService settingService;
	@Resource UserCustomService userCustomService;
	@Resource CommentService commentService;
	
	@Resource FileManage fileManage;
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
	
	@Resource ThumbnailManage thumbnailManage;
	
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
			
			
			
      		if(flag){
      			//仅显示指定的字段
    			User viewUser = new User();
    			viewUser.setId(new_user.getId());
    			viewUser.setUserName(new_user.getUserName());//会员用户名
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
    			
      			model.addAttribute("user", viewUser);
          		returnValue.put("user", viewUser);
      		}else{//如果不是登录会员自身，则仅显示指定字段
      			User other_user = new User();
      			other_user.setId(new_user.getId());//Id
      			other_user.setUserName(new_user.getUserName());//会员用户名
      			other_user.setState(new_user.getState());//用户状态
      			other_user.setGradeId(new_user.getGradeId());//等级Id
      			other_user.setGradeName(new_user.getGradeName());//等级名称
      			other_user.setAvatarPath(new_user.getAvatarPath());//头像路径
      			other_user.setAvatarName(new_user.getAvatarName());//头像名称
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
		viewUser.setEmail(user.getEmail());//邮箱地址
		viewUser.setIssue(user.getIssue());//密码提示问题
		viewUser.setRegistrationDate(user.getRegistrationDate());//注册日期
		viewUser.setPoint(user.getPoint());//当前积分
		viewUser.setGradeId(user.getGradeId());//等级Id
		viewUser.setGradeName(user.getGradeName());//将等级值设进等级参数里
		viewUser.setAvatarPath(user.getAvatarPath());//头像路径
		viewUser.setAvatarName(user.getAvatarName());//头像名称
		
		model.addAttribute("user",viewUser);

		if(isAjax){
			returnValue.put("user", viewUser);
			returnValue.put("userCustomList", userCustomList);
			
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
		
		
		new_user.setId(user.getId());
		new_user.setUserName(user.getUserName());
		
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
	public String updateAvatar(ModelMap model,MultipartFile imgFile,
			HttpServletRequest request,HttpServletResponse response)
			throws Exception {	
				
				
		Map<String,String> error = new HashMap<String,String>();//错误

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
		
		if(imgFile != null && !imgFile.isEmpty()){
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
					
					newFileName = user.getId()+ ".png";
					
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
						thumbnailManage.createImage(imgFile.getInputStream(),PathUtil.path()+File.separator+pathDir_100+newFileName,"png",100,100);
						
					}
				}else{//图片类型
					//验证文件类型
					boolean authentication = fileManage.validateFileSuffix(imgFile.getOriginalFilename(),formatList);
			
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
						String suffix = fileManage.getExtension(fileName).toLowerCase();
						
						//构建文件名称
						newFileName = user.getId()+ "." + suffix;
						
						if(srcWidth <=200 && srcHeight <=200){	
							//保存文件
							fileManage.writeFile(pathDir, newFileName,imgFile.getBytes());
							
							if(srcWidth <=100 && srcHeight <=100){
								//保存文件
								fileManage.writeFile(pathDir_100, newFileName,imgFile.getBytes());
							}else{
								//生成100*100缩略图
								thumbnailManage.createImage(imgFile.getInputStream(),PathUtil.path()+File.separator+pathDir_100+newFileName,suffix,100,100);
								
							}
						}else{
							//生成200*200缩略图
							thumbnailManage.createImage(imgFile.getInputStream(),PathUtil.path()+File.separator+pathDir+newFileName,suffix,x,y,width,height,200,200);

							//生成100*100缩略图
							thumbnailManage.createImage(imgFile.getInputStream(),PathUtil.path()+File.separator+pathDir_100+newFileName,suffix,x,y,width,height,100,100);   
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
						if(friend_user.getAvatarName() != null && !"".equals(friend_user.getAvatarName().trim())){
							privateMessage.setFriendAvatarPath(friend_user.getAvatarPath());//私信对方头像路径
							privateMessage.setFriendAvatarName(friend_user.getAvatarName());//私信对方头像名称
						}			
					}
					User sender_user = userMap.get(privateMessage.getSenderUserId());
					if(sender_user != null){
						privateMessage.setSenderUserName(sender_user.getUserName());//私信发送者用户名称
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
	 * @param pageForm
	 * @param friendUserName 对方用户名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/privateMessageChatList",method=RequestMethod.GET) 
	public String privateMessageChatList(ModelMap model,PageForm pageForm,String friendUserName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		String dirName = templateService.findTemplateDir_cache();
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		String accessPath = accessSourceDeviceManage.accessDevices(request);
		//调用分页算法代码
		PageView<PrivateMessage> pageView = new PageView<PrivateMessage>(settingService.findSystemSetting_cache().getForestagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
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

				QueryResult<PrivateMessage> qr = privateMessageService.findPrivateMessageChatByUserId(accessUser.getUserId(),chatUser.getId(),100,firstIndex,pageView.getMaxresult());
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
								if(friend_user.getAvatarName() != null && !"".equals(friend_user.getAvatarName().trim())){
									privateMessage.setFriendAvatarPath(friend_user.getAvatarPath());//私信对方头像路径
									privateMessage.setFriendAvatarName(friend_user.getAvatarName());//私信对方头像名称
								}
							}
							User sender_user = userMap.get(privateMessage.getSenderUserId());
							if(sender_user != null){
								privateMessage.setSenderUserName(sender_user.getUserName());//私信发送者用户名称
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
	public String addPrivateMessage(ModelMap model,String friendUserName,String messageContent,
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
		boolean isCaptcha = captchaManage.privateMessage_isCaptcha(accessUser.getUserName());
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
			int quantity = settingManage.submitQuantity_add("privateMessage", accessUser.getUserName(), 0);
			//删除每分钟原来提交次数
			settingManage.submitQuantity_delete("privateMessage", accessUser.getUserName());
			//刷新每分钟原来提交次数
			settingManage.submitQuantity_add("privateMessage", accessUser.getUserName(), quantity+1);
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
	public String deletePrivateMessage(ModelMap model,String jumpUrl,String token,String friendUserName,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,String> error = new HashMap<String,String>();//错误
		
		
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
	  	
	  	return JsonUtils.toJSONString(unreadMessage);
	}
	
}
