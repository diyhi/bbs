package cms.web.action.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.topic.Tag;
import cms.bean.topic.Topic;
import cms.bean.user.User;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.service.topic.TagService;
import cms.service.topic.TopicService;
import cms.utils.JsonUtils;
import cms.utils.WebUtil;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.lucene.TopicLuceneManage;
import cms.web.action.user.UserManage;

/**
 * 搜索
 *
 */
@Controller
public class SearchAction {
	@Resource TemplateService templateService;
	@Resource TopicService topicService;
	@Resource TopicLuceneManage topicLuceneManage;
	@Resource TagService tagService;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource SettingService settingService;
	@Resource UserManage userManage;
	
	
	/**
	 * 搜索
	 * @param model
	 * @param keyword 关键词
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/search",method = RequestMethod.GET) 
	public String execute(ModelMap model,String keyword,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,String> error = new HashMap<String,String>();//错误
	    Map<String,Object> returnValue = new HashMap<String,Object>();//返回值

		if((keyword == null || "".equals(keyword.trim()))){
			error.put("message", "搜索关键词不能为空");
		}
		PageView<Topic> pageView = new PageView<Topic>(settingService.findSystemSetting_cache().getForestagePageNumber(), pageForm.getPage(), 10,request.getRequestURI(),request.getQueryString());
		List<Topic> new_topicList = new ArrayList<Topic>();
		QueryResult<Topic> _qr = new QueryResult<Topic>();
		if(error.size() == 0){
			QueryResult<Topic> qr  = topicLuceneManage.findIndexByCondition(pageView.getCurrentpage(),pageView.getMaxresult(), keyword.trim(), null, null, null, null, 20, 1,false);
			
			
			if(qr.getResultlist() != null && qr.getResultlist().size() >0){
				List<Long> topicIdList =  new ArrayList<Long>();//话题Id集合
				
				
				for(Topic topic : qr.getResultlist()){
					topicIdList.add(topic.getId());
					
					
					
				}

				if(topicIdList != null && topicIdList.size() >0){
					List<Topic> topicList = topicService.findByIdList(topicIdList);
					if(topicList != null && topicList.size() >0){
						for(Topic old_t : qr.getResultlist()){
							for(Topic pi : topicList){
								if(pi.getId().equals(old_t.getId())){
									pi.setTitle(old_t.getTitle());
									pi.setContent(old_t.getContent());
									pi.setIp(null);//IP不显示
									if(pi.getIsStaff() == false){//会员
										User user = userManage.query_cache_findUserByUserName(pi.getUserName());
										pi.setNickname(user.getNickname());
										pi.setAvatarPath(user.getAvatarPath());
										pi.setAvatarName(user.getAvatarName());
										
									}
									
									new_topicList.add(pi);
									break;
								}
							}
						}
						
						List<Tag> tagList = tagService.findAllTag_cache();
						if(tagList != null && tagList.size() >0){
							for(Topic pi : topicList){
								for(Tag tag :tagList){
									if(pi.getTagId().equals(tag.getId())){
										pi.setTagName(tag.getName());
										break;
									}
									
								}
							}
						}
						
						
					}
				}			
				
				
				
				
			}
			_qr.setTotalrecord(qr.getTotalrecord());
			 //把查询结果设进去
			_qr.setResultlist(new_topicList);
			pageView.setQueryResult(_qr);
		}

		
		
		
		
		if(isAjax == true){
			returnValue.put("topicPage",pageView);
			
    		if(error != null && error.size() >0){
    			returnValue.put("success", "false");
    			returnValue.put("error", error);
    		}else{
    			returnValue.put("success", "true");
    			
    		}
    		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			model.addAttribute("keyword",keyword);
			model.addAttribute("topicPage",pageView);
			
			String dirName = templateService.findTemplateDir_cache();
			
			String accessPath = accessSourceDeviceManage.accessDevices(request);
			
			if(error != null && error.size() >0){//如果有错误	
				for (Map.Entry<String,String> entry : error.entrySet()) {	 
					model.addAttribute("message",entry.getValue());//提示
		  			return "templates/"+dirName+"/"+accessPath+"/message";
		  			
				}		
			}
			return "templates/"+dirName+"/"+accessPath+"/search";
		}	
	}
}
