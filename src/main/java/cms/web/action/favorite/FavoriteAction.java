package cms.web.action.favorite;


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
import cms.bean.favorite.Favorites;
import cms.bean.question.Question;
import cms.bean.topic.Topic;
import cms.bean.user.User;
import cms.service.favorite.FavoriteService;
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
 * 收藏夹
 *
 */
@Controller
public class FavoriteAction {
	@Resource FavoriteService favoriteService;
	@Resource SettingService settingService;
	@Resource TopicService topicService;
	@Resource QuestionService questionService;
	@Resource UserService userService;
	@Resource UserManage userManage;
	@Resource FileManage fileManage;
	
	
	/**
	 * 收藏夹列表
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
	@RequestMapping("/control/favorite/list") 
	public String favoriteList(ModelMap model,PageForm pageForm,Long id,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(id != null && id >0L && userName != null && !"".equals(userName.trim())){
			
			//调用分页算法代码
			PageView<Favorites> pageView = new PageView<Favorites>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			
			QueryResult<Favorites> qr = favoriteService.findFavoriteByUserId(id,userName,firstIndex,pageView.getMaxresult());
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				List<Long> topicIdList = new ArrayList<Long>();
				List<Long> questionIdList = new ArrayList<Long>();
				for(Favorites favorites : qr.getResultlist()){
					if(favorites.getModule().equals(10)){//话题
						topicIdList.add(favorites.getTopicId());
					}else if(favorites.getModule().equals(20)){//问题
						questionIdList.add(favorites.getQuestionId());
					}
					
				}
				if(topicIdList != null && topicIdList.size() >0){
					List<Topic> topicList = topicService.findByIdList(topicIdList);
					if(topicList != null && topicList.size() >0){
						for(Favorites favorites : qr.getResultlist()){
							for(Topic topic : topicList){
								if(favorites.getModule().equals(10) && favorites.getTopicId().equals(topic.getId())){
									favorites.setTopicTitle(topic.getTitle());
									break;
								}
							}
						}
					}
				}
				if(questionIdList != null && questionIdList.size() >0){
					List<Question> questionList = questionService.findByIdList(questionIdList);
					if(questionList != null && questionList.size() >0){
						for(Favorites favorites : qr.getResultlist()){
							for(Question question : questionList){
								if(favorites.getModule().equals(20) && favorites.getQuestionId().equals(question.getId())){
									favorites.setQuestionTitle(question.getTitle());
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
	 * 话题收藏列表
	 * @param model
	 * @param pageForm
	 * @param topicId 话题Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/topicFavorite/list") 
	public String topicFavoriteList(ModelMap model,PageForm pageForm,Long topicId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(topicId != null && topicId >0L){
			Topic topic = topicService.findById(topicId);
			if(topic != null){
				returnValue.put("currentTopic", topic);
			}
			
			//调用分页算法代码
			PageView<Favorites> pageView = new PageView<Favorites>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			
			QueryResult<Favorites> qr = favoriteService.findFavoritePageByTopicId(firstIndex,pageView.getMaxresult(),topicId);
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(Favorites favorites : qr.getResultlist()){
					User user = userManage.query_cache_findUserByUserName(favorites.getUserName());
					if(user != null){
						favorites.setAccount(user.getAccount());
						favorites.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							favorites.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
							favorites.setAvatarName(user.getAvatarName());
						}		
					}
				}
			}
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			returnValue.put("pageView", pageView);
		}else{
			error.put("topicId", "话题Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
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
	@ResponseBody
	@RequestMapping("/control/questionFavorite/list") 
	public String questionFavoriteList(ModelMap model,PageForm pageForm,Long questionId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(questionId != null && questionId >0L){
			Question question = questionService.findById(questionId);
			if(question != null){
				returnValue.put("currentQuestion", question);
			}
			
			
			//调用分页算法代码
			PageView<Favorites> pageView = new PageView<Favorites>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			
			QueryResult<Favorites> qr = favoriteService.findFavoritePageByQuestionId(firstIndex,pageView.getMaxresult(),questionId);
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(Favorites favorites : qr.getResultlist()){
					User user = userManage.query_cache_findUserByUserName(favorites.getUserName());
					if(user != null){
						favorites.setAccount(user.getAccount());
						favorites.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							favorites.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
							favorites.setAvatarName(user.getAvatarName());
						}		
					}
				}
			}
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			returnValue.put("pageView", pageView);
		}else{
			error.put("questionId", "问题Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
}
