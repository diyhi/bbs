package cms.web.action.follow;


import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.follow.Follow;
import cms.bean.follow.Follower;
import cms.bean.user.User;
import cms.service.follow.FollowService;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.utils.JsonUtils;
import cms.web.action.fileSystem.FileManage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 关注
 *
 */
@Controller
public class FollowAction {
	@Resource FollowService followService;
	@Resource SettingService settingService;
	@Resource UserService userService;
	@Resource FileManage fileManage;
	
	/**
	 * 关注列表
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
	@RequestMapping("/control/follow/list") 
	public String followList(ModelMap model,PageForm pageForm,Long id,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(id != null && id >0L && userName != null && !"".equals(userName.trim())){
			
			//调用分页算法代码
			PageView<Follow> pageView = new PageView<Follow>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			
			QueryResult<Follow> qr = followService.findFollowByUserName(id,userName,firstIndex,pageView.getMaxresult());
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(Follow follow : qr.getResultlist()){
					User user = userService.findUserByUserName(follow.getFriendUserName());
					if(user != null){
						follow.setFriendAccount(user.getAccount());
						follow.setFriendNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							follow.setFriendAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
							follow.setFriendAvatarName(user.getAvatarName());
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
	 * 粉丝列表
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
	@RequestMapping("/control/follower/list") 
	public String followerList(ModelMap model,PageForm pageForm,Long id,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(id != null && id >0L && userName != null && !"".equals(userName.trim())){
			
			//调用分页算法代码
			PageView<Follower> pageView = new PageView<Follower>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			
			QueryResult<Follower> qr = followService.findFollowerByUserName(id,userName,firstIndex,pageView.getMaxresult());
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(Follower follower : qr.getResultlist()){
					User user = userService.findUserByUserName(follower.getFriendUserName());
					if(user != null){
						follower.setFriendAccount(user.getAccount());
						follower.setFriendNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							follower.setFriendAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
							follower.setFriendAvatarName(user.getAvatarName());
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
}
