package cms.web.action.like;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.like.Like;
import cms.bean.question.Question;
import cms.bean.topic.Topic;
import cms.bean.user.User;
import cms.service.like.LikeService;
import cms.service.question.QuestionService;
import cms.service.setting.SettingService;
import cms.service.topic.TopicService;
import cms.service.user.UserService;
import cms.utils.JsonUtils;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.user.UserManage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 点赞管理列表
 *
 */
@Controller
public class LikeAction {
	@Resource LikeService likeService;
	@Resource SettingService settingService;
	@Resource TopicService topicService;
	@Resource UserManage userManage;
	@Resource UserService userService;
	@Resource FileManage fileManage;
	@Resource QuestionService questionService;
	
	
	/**
	 * 点赞列表
	 * @param model
	 * @param pageForm
	 * @param id 用户Id
	 * @param userName 用户名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/like/list") 
	public String likeList(ModelMap model,PageForm pageForm,Long id,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(id != null && id >0L && userName != null && !"".equals(userName.trim())){
			
			//调用分页算法代码
			PageView<Like> pageView = new PageView<Like>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			
			QueryResult<Like> qr = likeService.findLikeByUserId(id,userName,firstIndex,pageView.getMaxresult());
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				List<Long> topicIdList = new ArrayList<Long>();
				List<Long> questionIdList = new ArrayList<Long>();
				for(Like like : qr.getResultlist()){
					if(like.getModule().equals(10) || like.getModule().equals(20) || like.getModule().equals(30)){
						topicIdList.add(like.getTopicId());
					}
					if(like.getModule().equals(40) || like.getModule().equals(50) || like.getModule().equals(60)){
						questionIdList.add(like.getQuestionId());
					}
					
				}
				if(topicIdList != null && topicIdList.size() >0){
					List<Topic> topicList = topicService.findByIdList(topicIdList);
					if(topicList != null && topicList.size() >0){
						for(Like like : qr.getResultlist()){
							for(Topic topic : topicList){
								if((like.getModule().equals(10) || like.getModule().equals(20) || like.getModule().equals(30)) && like.getTopicId().equals(topic.getId())){
									like.setTopicTitle(topic.getTitle());
									break;
								}
							}
						}
					}
				}
				if(questionIdList != null && questionIdList.size() >0){
					List<Question> questionList = questionService.findByIdList(questionIdList);
					if(questionList != null && questionList.size() >0){
						for(Like like : qr.getResultlist()){
							for(Question question : questionList){
								if((like.getModule().equals(40) || like.getModule().equals(50) || like.getModule().equals(60)) && like.getQuestionId().equals(question.getId())){
									like.setQuestionTitle(question.getTitle());
									break;
								}
							}
						}
					}
				}
			}
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			User user = userService.findUserById(id);
			if(user != null){
				User currentUser = new User();
				currentUser.setId(user.getId());
				currentUser.setAccount(user.getAccount());
				currentUser.setNickname(user.getNickname());
				if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
					currentUser.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
					currentUser.setAvatarName(user.getAvatarName());
				}
				returnValue.put("currentUser", currentUser);
			}
			
			returnValue.put("pageView", pageView);
		}else{
			error.put("userId", "用户Id或用户名称不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
	
	/**
	 * 话题点赞列表
	 * @param model
	 * @param pageForm
	 * @param itemId 项目Id 例如：话题Id
	 * @param likeModule 点赞模块 10:话题 20:评论 30:回复
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/topicLike/list") 
	public String topicLikeList(ModelMap model,PageForm pageForm,Long itemId,Long topicId,Integer likeModule,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(itemId != null && itemId >0L){
			Topic topic = topicService.findById(topicId);
			if(topic != null){
				returnValue.put("currentTopic", topic);
			}
			
			//调用分页算法代码
			PageView<Like> pageView = new PageView<Like>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			QueryResult<Like> qr = null;
			if(likeModule.equals(10)){
				qr = likeService.findLikePageByTopicId(firstIndex,pageView.getMaxresult(),itemId);
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);
			}else if(likeModule.equals(20)){
				qr = likeService.findLikePageByCommentId(firstIndex,pageView.getMaxresult(),itemId);
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);
			}else if(likeModule.equals(30)){
				qr = likeService.findLikePageByCommentReplyId(firstIndex,pageView.getMaxresult(),itemId);
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);
			}
			
			
			
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(Like like : qr.getResultlist()){
					User user = userManage.query_cache_findUserByUserName(like.getUserName());
					if(user != null){
						like.setAccount(user.getAccount());
						like.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							like.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
							like.setAvatarName(user.getAvatarName());
						}		
					}
				}
			}
			
			returnValue.put("pageView", pageView);
		}else{
			error.put("itemId", "项目Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
	
	/**
	 * 问题点赞列表
	 * @param model
	 * @param pageForm
	 * @param itemId 项目Id 例如：话题Id
	 * @param likeModule 点赞模块 40:问题 50:评论 60:回复
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/questionLike/list") 
	public String questionLikeList(ModelMap model,PageForm pageForm,Long itemId,Long questionId,Integer likeModule,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(itemId != null && itemId >0L){
			Question question = questionService.findById(questionId);
			if(question != null){
				returnValue.put("currentQuestion", question);
			}
			
			//调用分页算法代码
			PageView<Like> pageView = new PageView<Like>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			QueryResult<Like> qr = null;
			if(likeModule.equals(40)){
				qr = likeService.findLikePageByQuestionId(firstIndex,pageView.getMaxresult(),itemId);
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);
			}else if(likeModule.equals(50)){
				qr = likeService.findLikePageByAnswerId(firstIndex,pageView.getMaxresult(),itemId);
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);
			}else if(likeModule.equals(60)){
				qr = likeService.findLikePageByAnswerReplyId(firstIndex,pageView.getMaxresult(),itemId);
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);
			}
			
			
			
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(Like like : qr.getResultlist()){
					User user = userManage.query_cache_findUserByUserName(like.getUserName());
					if(user != null){
						like.setAccount(user.getAccount());
						like.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							like.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
							like.setAvatarName(user.getAvatarName());
						}		
					}
				}
			}
			
			returnValue.put("pageView", pageView);
		}else{
			error.put("itemId", "项目Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
}
