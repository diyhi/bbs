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
import cms.bean.topic.Topic;
import cms.bean.user.User;
import cms.service.like.LikeService;
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
 * 点赞
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
				for(Like like : qr.getResultlist()){
					topicIdList.add(like.getTopicId());
				}
				List<Topic> topicList = topicService.findByIdList(topicIdList);
				if(topicList != null && topicList.size() >0){
					for(Like like : qr.getResultlist()){
						for(Topic topic : topicList){
							if(like.getTopicId().equals(topic.getId())){
								like.setTopicTitle(topic.getTitle());
								break;
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
	 * @param topicId 话题Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/topicLike/list") 
	public String topicLikeList(ModelMap model,PageForm pageForm,Long topicId,
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
			PageView<Like> pageView = new PageView<Like>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			
			QueryResult<Like> qr = likeService.findLikePageByTopicId(firstIndex,pageView.getMaxresult(),topicId);
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
}
