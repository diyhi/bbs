package cms.web.action.topic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.staff.SysUsers;
import cms.bean.topic.Comment;
import cms.bean.topic.Reply;
import cms.bean.topic.Tag;
import cms.bean.topic.Topic;
import cms.bean.topic.TopicUnhide;
import cms.bean.user.User;
import cms.service.setting.SettingService;
import cms.service.topic.CommentService;
import cms.service.topic.TagService;
import cms.service.topic.TopicService;
import cms.service.user.UserService;
import cms.utils.HtmlEscape;
import cms.utils.JsonUtils;
import cms.utils.Verification;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.lucene.TopicLuceneManage;
import cms.web.action.staff.StaffManage;
import cms.web.action.user.UserManage;

/**
 * 话题
 *
 */
@Controller
public class TopicAction {
	@Resource SettingService settingService;
	@Resource TopicService topicService;
	@Resource CommentService commentService;
	@Resource TagService tagService;
	@Resource TopicLuceneManage topicLuceneManage;
	@Resource TextFilterManage textFilterManage;
	@Resource FileManage fileManage;
	@Resource UserManage userManage;
	@Resource TopicManage topicManage;
	@Resource UserService userService;
	@Resource StaffManage staffManage;
	
	@ResponseBody
	@RequestMapping("/control/topic/list") 
	public String execute(PageForm pageForm,ModelMap model,Boolean visible,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();

		if(visible != null && visible == false){
			jpql.append(" and o.status>?"+ (params.size()+1));
			params.add(100);
		}else{
			jpql.append(" and o.status<?"+ (params.size()+1));
			params.add(100);
		}
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		PageView<Topic> pageView = new PageView<Topic>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
		
		
		//调用分页算法类
		QueryResult<Topic> qr = topicService.getScrollData(Topic.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			List<Tag> tagList = tagService.findAllTag();
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
			
			
			for(Topic topic : qr.getResultlist()){
				if(topic.getIsStaff()){//如果为员工

					SysUsers sysUsers = staffManage.query_cache_findByUserAccount(topic.getUserName());
					if(sysUsers != null){
						topic.setNickname(sysUsers.getNickname());
						if(sysUsers.getAvatarName() != null && !"".equals(sysUsers.getAvatarName().trim())){
							topic.setAvatarPath(fileManage.fileServerAddress(request)+sysUsers.getAvatarPath());
							topic.setAvatarName(sysUsers.getAvatarName());
						}	
					}
					
					
					topic.setAccount(topic.getUserName());//员工用户名和账号是同一个
				}else{
//					User user = userService.findUserByUserName(topic.getUserName());
					User user = userManage.query_cache_findUserByUserName(topic.getUserName());
					if(user != null){
						topic.setAccount(user.getAccount());
						topic.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							topic.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
							topic.setAvatarName(user.getAvatarName());
						}		
					}
				}
			}
			
			
		}

		pageView.setQueryResult(qr);
		
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
	}
	
	/**
	 * 搜索话题列表
	 * @param pageForm
	 * @param model
	 * @param dataSource 数据源
	 * @param keyword 关键词
	 * @param tagId 标签Id
	 * @param tagName 标签名称
	 * @param account 账号
	 * @param start_postTime 起始发表时间
	 * @param end_postTime 结束发表时间
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/topic/search") 
	public String search(ModelMap model,PageForm pageForm,
			Integer dataSource,String keyword,String tagId,String tagName,String account,
			String start_postTime,String end_postTime,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if(dataSource == null){//如果数据源为空，则默认为lucene方式查询
			dataSource = 1;
		}
		//错误
		Map<String,String> error = new HashMap<String,String>();

		String _keyword = null;//关键词
		Long _tagId = null;//标签
		String _userName = null;//用户名
		Date _start_postTime = null;//发表时间 起始
		Date _end_postTime= null;//发表时间	结束
		
		
		if(keyword != null && !"".equals(keyword.trim())){
			_keyword = keyword.trim();
		}
		//标签
		if(tagId != null && !"".equals(tagId.trim())){
			boolean tagId_verification = Verification.isPositiveInteger(tagId.trim());//正整数
			if(tagId_verification){
				_tagId = Long.parseLong(tagId.trim());
			}else{
				error.put("tagId", "请选择标签");
			}
		}
		//账号
		if(account != null && !"".equals(account.trim())){
			User user = userService.findUserByAccount(account.trim());
			if(user != null){
				_userName = user.getUserName();
			}else{
				_userName = account.trim();
			}
		}
		//起始发表时间	
		if(start_postTime != null && !"".equals(start_postTime.trim())){
			boolean start_postTimeVerification = Verification.isTime_minute(start_postTime.trim());
			if(start_postTimeVerification){
				DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm");		
				_start_postTime = dd.parse(start_postTime.trim());
			}else{
				error.put("start_postTime", "请填写正确的日期");
			}
		}
		//结束发表时间	
		if(end_postTime != null && !"".equals(end_postTime.trim())){
			boolean end_postTimeVerification = Verification.isTime_minute(end_postTime.trim());
			if(end_postTimeVerification){
				DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm");		
				_end_postTime = dd.parse(end_postTime.trim());
			}else{
				error.put("end_postTime", "请填写正确的日期");
			}
		}
		//比较时间
		Calendar start=Calendar.getInstance();//发表时间 起始  
        Calendar end=Calendar.getInstance();//发表时间 结束
        if(_start_postTime != null){
        	start.setTime(_start_postTime);   
        }
        if(_end_postTime != null){
        	end.setTime(_end_postTime);   
        }
		if(_start_postTime != null && _end_postTime != null){
        	int result =start.compareTo(end);//起始时间与结束时间比较
        	if(result > 0 ){//起始时间比结束时间大
        		error.put("start_postTime", "起始时间不能比结束时间大");
        	}
		}
		
			
		
		//调用分页算法代码
		PageView<Topic> pageView = new PageView<Topic>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
	
		if(dataSource.equals(1)){//lucene索引
			QueryResult<Topic> qr = topicLuceneManage.findIndexByCondition(pageView.getCurrentpage(), pageView.getMaxresult(), _keyword, _tagId, _userName, _start_postTime, _end_postTime, null, 1);

			if(qr.getResultlist() != null && qr.getResultlist().size() >0){
				List<Long> topicIdList =  new ArrayList<Long>();//话题Id集合
				
				List<Topic> new_topicList = new ArrayList<Topic>();
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
									
									if(pi.getIsStaff()){//如果为员工
										SysUsers sysUsers = staffManage.query_cache_findByUserAccount(pi.getUserName());
										if(sysUsers != null){
											pi.setNickname(sysUsers.getNickname());
											if(sysUsers.getAvatarName() != null && !"".equals(sysUsers.getAvatarName().trim())){
												pi.setAvatarPath(fileManage.fileServerAddress(request)+sysUsers.getAvatarPath());
												pi.setAvatarName(sysUsers.getAvatarName());
											}	
										}
										pi.setAccount(pi.getUserName());//员工用户名和账号是同一个
									}else{
										User user = userManage.query_cache_findUserByUserName(pi.getUserName());
										if(user != null){
											pi.setAccount(user.getAccount());
											pi.setNickname(user.getNickname());
											if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
												pi.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
												pi.setAvatarName(user.getAvatarName());
											}		
										}
									}
									new_topicList.add(pi);
									break;
								}
							}
						}
					}
				}
				
				QueryResult<Topic> _qr = new QueryResult<Topic>();
				 //把查询结果设进去
				_qr.setResultlist(new_topicList);
				_qr.setTotalrecord(qr.getTotalrecord());
				pageView.setQueryResult(_qr);
			}	
		}else{//数据库
			String param = "";//sql参数
			List<Object> paramValue = new ArrayList<Object>();//sql参数值
			
			if(_keyword != null){//标题
				param += " and (o.title like ?"+(paramValue.size()+1)+" escape '/' ";
				paramValue.add("%/"+ _keyword+"%");	
				
				//内容
				param += " or o.content like ?"+(paramValue.size()+1)+" escape '/' )";
				paramValue.add("%/"+ _keyword+"%");	
			}
			if(_tagId != null && _tagId >0){//标签
				param += " and o.tagId =?"+(paramValue.size()+1);
				paramValue.add(_tagId);	
			}
			if(_userName != null && !"".equals(_userName)){//用户
				param += " and o.userName =?"+(paramValue.size()+1);
				paramValue.add(_userName);	
			}
			if(_start_postTime != null){//起始发表时间
				param += " and o.postTime >= ?"+(paramValue.size()+1);
				
				paramValue.add(_start_postTime);
			}
			if(_end_postTime != null){//结束发表时间
				param += " and o.postTime <= ?"+(paramValue.size()+1);
				paramValue.add(_end_postTime);
			}
			//删除第一个and
			param = StringUtils.difference(" and", param);
			
			
			//排序
			LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
			orderby.put("id", "desc");//排序
			QueryResult<Topic> qr = topicService.getScrollData(Topic.class,firstindex, pageView.getMaxresult(), param, paramValue.toArray(),orderby);
			
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(Topic t :qr.getResultlist()){
					if(t.getTitle() != null && !"".equals(t.getTitle().trim())){
						//转义
						t.setTitle(HtmlEscape.escape(t.getTitle()));
					}
					if(t.getContent() != null && !"".equals(t.getContent().trim())){
						t.setContent(textFilterManage.filterText(t.getContent()));
						if(t.getContent().length() > 190){
							
							t.setContent(t.getContent().substring(0, 190));
						}
					}
					
					if(t.getIsStaff()){//如果为员工
						SysUsers sysUsers = staffManage.query_cache_findByUserAccount(t.getUserName());
						if(sysUsers != null){
							t.setNickname(sysUsers.getNickname());
							if(sysUsers.getAvatarName() != null && !"".equals(sysUsers.getAvatarName().trim())){
								t.setAvatarPath(fileManage.fileServerAddress(request)+sysUsers.getAvatarPath());
								t.setAvatarName(sysUsers.getAvatarName());
							}	
						}
						t.setAccount(t.getUserName());//员工用户名和账号是同一个
					}else{
						User user = userManage.query_cache_findUserByUserName(t.getUserName());
						if(user != null){
							t.setAccount(user.getAccount());
							t.setNickname(user.getNickname());
							if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
								t.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
								t.setAvatarName(user.getAvatarName());
							}		
						}
					}
				}
			}
			
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
		}

		
		
		if(pageView.getRecords() != null && pageView.getRecords().size() >0){
			List<Tag> tagList = tagService.findAllTag_cache();
			if(tagList != null && tagList.size() >0){
				for(Topic t :pageView.getRecords()){
					for(Tag tag :tagList){
						if(t.getTagId().equals(tag.getId())){
							t.setTagName(tag.getName());
							break;
						}
						
					}
					
				}
			}
			
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
		}
	}
	
	/**
	 * 全部待审核话题
	 * @param pageForm
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/topic/allAuditTopic") 
	public String allAuditTopic(PageForm pageForm,ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();

		jpql.append(" and o.status=?"+ (params.size()+1));
		params.add(10);
		
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		PageView<Topic> pageView = new PageView<Topic>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
		
		
		//调用分页算法类
		QueryResult<Topic> qr = topicService.getScrollData(Topic.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			List<Tag> tagList = tagService.findAllTag();
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
			for(Topic topic : qr.getResultlist()){
				if(topic.getIsStaff()){//如果为员工
					SysUsers sysUsers = staffManage.query_cache_findByUserAccount(topic.getUserName());
					if(sysUsers != null){
						topic.setNickname(sysUsers.getNickname());
						if(sysUsers.getAvatarName() != null && !"".equals(sysUsers.getAvatarName().trim())){
							topic.setAvatarPath(fileManage.fileServerAddress(request)+sysUsers.getAvatarPath());
							topic.setAvatarName(sysUsers.getAvatarName());
						}	
					}
					topic.setAccount(topic.getUserName());//员工用户名和账号是同一个
				}else{
					User user = userManage.query_cache_findUserByUserName(topic.getUserName());
					if(user != null){
						topic.setAccount(user.getAccount());
						topic.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							topic.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
							topic.setAvatarName(user.getAvatarName());
						}		
					}
				}
			}
		}

		pageView.setQueryResult(qr);
		
		
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
	}
	
	/**
	 * 全部待审核评论
	 * @param pageForm
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/topic/allAuditComment") 
	public String allAuditComment(PageForm pageForm,ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();

		jpql.append(" and o.status=?"+ (params.size()+1));
		params.add(10);
		
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		PageView<Comment> pageView = new PageView<Comment>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
		
		
		//调用分页算法类
		QueryResult<Comment> qr = commentService.getScrollData(Comment.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			List<Long> topicIdList = new ArrayList<Long>();
			for(Comment o :qr.getResultlist()){
				o.setContent(textFilterManage.filterText(textFilterManage.specifyHtmlTagToText(o.getContent())));
    			if(!topicIdList.contains(o.getTopicId())){
    				topicIdList.add(o.getTopicId());
    			}
    		}
			List<Topic> topicList = topicService.findTitleByIdList(topicIdList);
			if(topicList != null && topicList.size() >0){
				for(Comment o :qr.getResultlist()){
					if(o.getIsStaff()){//如果为员工
						SysUsers sysUsers = staffManage.query_cache_findByUserAccount(o.getUserName());
						if(sysUsers != null){
							o.setNickname(sysUsers.getNickname());
							if(sysUsers.getAvatarName() != null && !"".equals(sysUsers.getAvatarName().trim())){
								o.setAvatarPath(fileManage.fileServerAddress(request)+sysUsers.getAvatarPath());
								o.setAvatarName(sysUsers.getAvatarName());
							}	
						}
						o.setAccount(o.getUserName());//员工用户名和账号是同一个
					}else{
						User user = userManage.query_cache_findUserByUserName(o.getUserName());
						if(user != null){
							o.setAccount(user.getAccount());
							o.setNickname(user.getNickname());
							if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
								o.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
								o.setAvatarName(user.getAvatarName());
							}		
						}
					}
					for(Topic topic : topicList){
						if(topic.getId().equals(o.getTopicId())){
							o.setTopicTitle(topic.getTitle());
							break;
						}
					}
					
				}
			}
			
		}

		pageView.setQueryResult(qr);
		
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
	}
	
	/**
	 * 全部待审核回复
	 * @param pageForm
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/topic/allAuditReply") 
	public String allAuditReply(PageForm pageForm,ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();

		jpql.append(" and o.status=?"+ (params.size()+1));
		params.add(10);
		
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		PageView<Reply> pageView = new PageView<Reply>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
		
		
		//调用分页算法类
		QueryResult<Reply> qr = commentService.getScrollData(Reply.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			List<Long> topicIdList = new ArrayList<Long>();
			for(Reply o :qr.getResultlist()){
    				
				o.setContent(textFilterManage.filterText(textFilterManage.specifyHtmlTagToText(o.getContent())));
    			if(!topicIdList.contains(o.getTopicId())){
    				topicIdList.add(o.getTopicId());
    			}
    			
    			
    			
    			if(o.getIsStaff()){//如果为员工
					SysUsers sysUsers = staffManage.query_cache_findByUserAccount(o.getUserName());
					if(sysUsers != null){
						o.setNickname(sysUsers.getNickname());
						if(sysUsers.getAvatarName() != null && !"".equals(sysUsers.getAvatarName().trim())){
							o.setAvatarPath(fileManage.fileServerAddress(request)+sysUsers.getAvatarPath());
							o.setAvatarName(sysUsers.getAvatarName());
						}	
					}
					o.setAccount(o.getUserName());//员工用户名和账号是同一个
				}else{
					User user = userManage.query_cache_findUserByUserName(o.getUserName());
					if(user != null){
						o.setAccount(user.getAccount());
						o.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							o.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
							o.setAvatarName(user.getAvatarName());
						}		
					}
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

		pageView.setQueryResult(qr);
		
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
	}
	
	
	/**
	 * 话题取消隐藏列表
	 * @param model
	 * @param pageForm
	 * @param topicId 话题Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/topic/topicUnhideList") 
	public String dynamicImageFavoriteList(ModelMap model,PageForm pageForm,Long topicId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		
		if(topicId != null && topicId >0L){
			
			//调用分页算法代码
			PageView<TopicUnhide> pageView = new PageView<TopicUnhide>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10,request.getRequestURI(),request.getQueryString());
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			
			QueryResult<TopicUnhide> qr = topicService.findTopicUnhidePageByTopicId(firstIndex,pageView.getMaxresult(),topicId);
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(TopicUnhide o :qr.getResultlist()){

					User user = userManage.query_cache_findUserByUserName(o.getUserName());
					if(user != null){
						o.setAccount(user.getAccount());
						o.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							o.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
							o.setAvatarName(user.getAvatarName());
						}		
					}
	    			
	    			
	    		}
				
			}
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			if(topicId != null){
				Topic topic = topicManage.queryTopicCache(topicId);
				if(topic != null){
					returnValue.put("currentTopic", topic);
				}
			}
			returnValue.put("pageView", pageView);
		}else{
			error.put("topicId", " 话题Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
}
