package cms.web.action.follow;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.follow.Follow;
import cms.bean.follow.Follower;
import cms.service.follow.FollowService;
import cms.service.setting.SettingService;

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
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			model.addAttribute("pageView", pageView);
		}
		return "jsp/follow/followerList";
	}
}
