package cms.web.action.favorite;


import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.favorite.Favorites;
import cms.bean.question.Question;
import cms.bean.topic.Topic;
import cms.service.favorite.FavoriteService;
import cms.service.question.QuestionService;
import cms.service.setting.SettingService;
import cms.service.topic.TopicService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

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
	@RequestMapping("/control/favorite/list") 
	public String favoriteList(ModelMap model,PageForm pageForm,Long id,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if(userName != null && !"".equals(userName.trim())){
			
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
			model.addAttribute("pageView", pageView);
		}
		return "jsp/favorite/favoriteList";
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
	@RequestMapping("/control/topicFavorite/list") 
	public String topicFavoriteList(ModelMap model,PageForm pageForm,Long topicId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if(topicId != null && topicId >0L){
			
			//调用分页算法代码
			PageView<Favorites> pageView = new PageView<Favorites>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			
			QueryResult<Favorites> qr = favoriteService.findFavoritePageByTopicId(firstIndex,pageView.getMaxresult(),topicId);
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				List<Long> topicIdList = new ArrayList<Long>();
				for(Favorites favorites : qr.getResultlist()){
					topicIdList.add(favorites.getTopicId());
				}
				List<Topic> topicList = topicService.findByIdList(topicIdList);
				if(topicList != null && topicList.size() >0){
					for(Favorites favorites : qr.getResultlist()){
						for(Topic topic : topicList){
							if(favorites.getTopicId().equals(topic.getId())){
								favorites.setTopicTitle(topic.getTitle());
								break;
							}
						}
					}
				}
			}
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			model.addAttribute("pageView", pageView);
		}
		return "jsp/favorite/topicFavoriteList";
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
	@RequestMapping("/control/questionFavorite/list") 
	public String questionFavoriteList(ModelMap model,PageForm pageForm,Long questionId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if(questionId != null && questionId >0L){
			
			//调用分页算法代码
			PageView<Favorites> pageView = new PageView<Favorites>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			
			QueryResult<Favorites> qr = favoriteService.findFavoritePageByQuestionId(firstIndex,pageView.getMaxresult(),questionId);
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				List<Long> questionIdList = new ArrayList<Long>();
				for(Favorites favorites : qr.getResultlist()){
					questionIdList.add(favorites.getQuestionId());
				}
				List<Question> questionList = questionService.findByIdList(questionIdList);
				if(questionList != null && questionList.size() >0){
					for(Favorites favorites : qr.getResultlist()){
						for(Question question : questionList){
							if(favorites.getQuestionId().equals(question.getId())){
								favorites.setQuestionTitle(question.getTitle());
								break;
							}
						}
					}
				}
			}
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			model.addAttribute("pageView", pageView);
		}
		return "jsp/favorite/questionFavoriteList";
	}
}
