package cms.web.action.follow;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.follow.Follow;
import cms.bean.follow.Follower;
import cms.bean.user.User;
import cms.service.follow.FollowService;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.web.action.fileSystem.FileManage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

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
	@RequestMapping("/control/follow/list") 
	public String followList(ModelMap model,PageForm pageForm,Long id,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if(userName != null && !"".equals(userName.trim())){
			
			//调用分页算法代码
			PageView<Follow> pageView = new PageView<Follow>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			
			QueryResult<Follow> qr = followService.findFollowByUserName(id,userName,firstIndex,pageView.getMaxresult());
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(Follow follow : qr.getResultlist()){
					User user = userService.findUserByUserName(follow.getFriendUserName());
					if(user != null){
						follow.setFriendNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							follow.setFriendAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
							follow.setFriendAvatarName(user.getAvatarName());
						}		
					}
				}
			}
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			model.addAttribute("pageView", pageView);
		}
		return "jsp/follow/followList";
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
	@RequestMapping("/control/follower/list") 
	public String followerList(ModelMap model,PageForm pageForm,Long id,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if(userName != null && !"".equals(userName.trim())){
			
			//调用分页算法代码
			PageView<Follower> pageView = new PageView<Follower>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			
			QueryResult<Follower> qr = followService.findFollowerByUserName(id,userName,firstIndex,pageView.getMaxresult());
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(Follower follower : qr.getResultlist()){
					User user = userService.findUserByUserName(follower.getFriendUserName());
					if(user != null){
						follower.setFriendNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							follower.setFriendAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
							follower.setFriendAvatarName(user.getAvatarName());
						}		
					}
				}
			}
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			model.addAttribute("pageView", pageView);
		}
		return "jsp/follow/followerList";
	}
}
