package cms.web.action.question;

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
import cms.bean.question.Answer;
import cms.bean.question.AnswerReply;
import cms.bean.question.Question;
import cms.bean.question.QuestionTag;
import cms.bean.question.QuestionTagAssociation;
import cms.bean.user.User;
import cms.service.question.AnswerService;
import cms.service.question.QuestionService;
import cms.service.question.QuestionTagService;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.utils.HtmlEscape;
import cms.utils.JsonUtils;
import cms.utils.Verification;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.lucene.QuestionLuceneManage;
import cms.web.action.user.UserManage;

/**
 * 问题
 *
 */
@Controller
public class QuestionAction {
	@Resource SettingService settingService;
	@Resource QuestionService questionService;
	@Resource QuestionTagService questionTagService;
	@Resource AnswerService answerService;
	@Resource TextFilterManage textFilterManage;
	
	@Resource QuestionLuceneManage questionLuceneManage;
	@Resource FileManage fileManage;
	@Resource UserManage userManage;
	@Resource UserService userService;
	
	
	@ResponseBody
	@RequestMapping("/control/question/list") 
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
		
		PageView<Question> pageView = new PageView<Question>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
		
		
		//调用分页算法类
		QueryResult<Question> qr = questionService.getScrollData(Question.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
		
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			List<QuestionTag> questionTagList = questionTagService.findAllQuestionTag();
			
			if(questionTagList != null && questionTagList.size() >0){
				for(Question question : qr.getResultlist()){
					List<QuestionTagAssociation> questionTagAssociationList = questionService.findQuestionTagAssociationByQuestionId(question.getId());
					if(questionTagAssociationList != null && questionTagAssociationList.size() >0){
						for(QuestionTag questionTag : questionTagList){
							for(QuestionTagAssociation questionTagAssociation : questionTagAssociationList){
								if(questionTagAssociation.getQuestionTagId().equals(questionTag.getId())){
									questionTagAssociation.setQuestionTagName(questionTag.getName());
									question.addQuestionTagAssociation(questionTagAssociation);
									break;
								}
							}
						}
					}
				}
			}
			
			for(Question question : qr.getResultlist()){
				User user = userManage.query_cache_findUserByUserName(question.getUserName());
				if(user != null){
					question.setAccount(user.getAccount());
					question.setNickname(user.getNickname());
					if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
						question.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
						question.setAvatarName(user.getAvatarName());
					}		
				}
				if(question.getIsStaff()){//如果为员工
					question.setAccount(question.getUserName());//员工用户名和账号是同一个
				}
			}
		}

		pageView.setQueryResult(qr);
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
	}
	
	/**
	 * 搜索问题列表
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
	@RequestMapping("/control/question/search") 
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
		PageView<Question> pageView = new PageView<Question>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
	
		if(dataSource.equals(1)){//lucene索引
			
			QueryResult<Question> qr = questionLuceneManage.findIndexByCondition(pageView.getCurrentpage(), pageView.getMaxresult(), _keyword, _tagId, _userName, _start_postTime, _end_postTime, null, 1);

			if(qr.getResultlist() != null && qr.getResultlist().size() >0){
				List<Long> questionIdList =  new ArrayList<Long>();//话题Id集合
				
				List<Question> new_questionList = new ArrayList<Question>();
				for(Question question : qr.getResultlist()){
					questionIdList.add(question.getId());
				}
				if(questionIdList != null && questionIdList.size() >0){
					List<Question> questionList = questionService.findByIdList(questionIdList);
					if(questionList != null && questionList.size() >0){
						for(Question old_t : qr.getResultlist()){
							for(Question pi : questionList){
								if(pi.getId().equals(old_t.getId())){
									pi.setTitle(old_t.getTitle());
									pi.setContent(old_t.getContent());
									
									
									User user = userManage.query_cache_findUserByUserName(pi.getUserName());
									if(user != null){
										pi.setAccount(user.getAccount());
										pi.setNickname(user.getNickname());
										if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
											pi.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
											pi.setAvatarName(user.getAvatarName());
										}		
									}
									if(pi.getIsStaff()){//如果为员工
										pi.setAccount(pi.getUserName());//员工用户名和账号是同一个
									}
									
									new_questionList.add(pi);
									break;
								}
							}
						}
					}
				}
				
				QueryResult<Question> _qr = new QueryResult<Question>();
				 //把查询结果设进去
				_qr.setResultlist(new_questionList);
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
				param += " and exists(select q.questionId from QuestionTagAssociation q where q.questionTagId = ?"+(paramValue.size()+1)+" and q.questionId=o.id) ";
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
			QueryResult<Question> qr = questionService.getScrollData(Question.class,firstindex, pageView.getMaxresult(), param, paramValue.toArray(),orderby);
			
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(Question t :qr.getResultlist()){
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
					
					User user = userManage.query_cache_findUserByUserName(t.getUserName());
					if(user != null){
						t.setAccount(user.getAccount());
						t.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							t.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
							t.setAvatarName(user.getAvatarName());
						}		
					}
					if(t.getIsStaff()){//如果为员工
						t.setAccount(t.getUserName());//员工用户名和账号是同一个
					}
				}
			}
			
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
		}

		
		if(pageView.getRecords() != null && pageView.getRecords().size() >0){
			List<QuestionTag> questionTagList = questionTagService.findAllQuestionTag();
			
			if(questionTagList != null && questionTagList.size() >0){
				for(Question question : pageView.getRecords()){
					List<QuestionTagAssociation> questionTagAssociationList = questionService.findQuestionTagAssociationByQuestionId(question.getId());
					if(questionTagAssociationList != null && questionTagAssociationList.size() >0){
						for(QuestionTag questionTag : questionTagList){
							for(QuestionTagAssociation questionTagAssociation : questionTagAssociationList){
								if(questionTagAssociation.getQuestionTagId().equals(questionTag.getId())){
									questionTagAssociation.setQuestionTagName(questionTag.getName());
									question.addQuestionTagAssociation(questionTagAssociation);
									break;
								}
							}
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
	 * 全部待审核问题
	 * @param pageForm
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/question/allAuditQuestion") 
	public String allAuditQuestion(PageForm pageForm,ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();

		jpql.append(" and o.status=?"+ (params.size()+1));
		params.add(10);
		
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		PageView<Question> pageView = new PageView<Question>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
		
		
		//调用分页算法类
		QueryResult<Question> qr = questionService.getScrollData(Question.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			List<QuestionTag> questionTagList = questionTagService.findAllQuestionTag();
			
			if(questionTagList != null && questionTagList.size() >0){
				for(Question question : qr.getResultlist()){
					List<QuestionTagAssociation> questionTagAssociationList = questionService.findQuestionTagAssociationByQuestionId(question.getId());
					if(questionTagAssociationList != null && questionTagAssociationList.size() >0){
						for(QuestionTag questionTag : questionTagList){
							for(QuestionTagAssociation questionTagAssociation : questionTagAssociationList){
								if(questionTagAssociation.getQuestionTagId().equals(questionTag.getId())){
									questionTagAssociation.setQuestionTagName(questionTag.getName());
									question.addQuestionTagAssociation(questionTagAssociation);
									break;
								}
							}
						}
					}
				}
			}
			for(Question question : qr.getResultlist()){
				User user = userManage.query_cache_findUserByUserName(question.getUserName());
				if(user != null){
					question.setAccount(user.getAccount());
					question.setNickname(user.getNickname());
					if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
						question.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
						question.setAvatarName(user.getAvatarName());
					}		
				}
				if(question.getIsStaff()){//如果为员工
					question.setAccount(question.getUserName());//员工用户名和账号是同一个
				}
			}
		}

		pageView.setQueryResult(qr);
		
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
	}
	
	/**
	 * 全部待审核答案
	 * @param pageForm
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/question/allAuditAnswer") 
	public String allAuditAnswer(PageForm pageForm,ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();

		jpql.append(" and o.status=?"+ (params.size()+1));
		params.add(10);
		
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		PageView<Answer> pageView = new PageView<Answer>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
		
		
		//调用分页算法类
		QueryResult<Answer> qr = answerService.getScrollData(Answer.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			List<Long> questionIdList = new ArrayList<Long>();
			for(Answer o :qr.getResultlist()){
    			o.setContent(textFilterManage.filterText(o.getContent()));
    			if(!questionIdList.contains(o.getQuestionId())){
    				questionIdList.add(o.getQuestionId());
    			}
    			
    			
    		
				User user = userManage.query_cache_findUserByUserName(o.getUserName());
				if(user != null){
					o.setAccount(user.getAccount());
					o.setNickname(user.getNickname());
					if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
						o.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
						o.setAvatarName(user.getAvatarName());
					}		
				}
				if(o.getIsStaff()){//如果为员工
					o.setAccount(o.getUserName());//员工用户名和账号是同一个
				}
    			
    		}
			List<Question> questionList = questionService.findTitleByIdList(questionIdList);
			if(questionList != null && questionList.size() >0){
				for(Answer o :qr.getResultlist()){
					for(Question question : questionList){
						if(question.getId().equals(o.getQuestionId())){
							o.setQuestionTitle(question.getTitle());
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
	@RequestMapping("/control/question/allAuditAnswerReply") 
	public String allAuditAnswerReply(PageForm pageForm,ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();

		jpql.append(" and o.status=?"+ (params.size()+1));
		params.add(10);
		
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		PageView<AnswerReply> pageView = new PageView<AnswerReply>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
		
		
		//调用分页算法类
		QueryResult<AnswerReply> qr = answerService.getScrollData(AnswerReply.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			List<Long> questionIdList = new ArrayList<Long>();
			for(AnswerReply o :qr.getResultlist()){
    				
    			o.setContent(textFilterManage.filterText(o.getContent()));
    			if(!questionIdList.contains(o.getQuestionId())){
    				questionIdList.add(o.getQuestionId());
    			}
    			
    			User user = userManage.query_cache_findUserByUserName(o.getUserName());
				if(user != null){
					o.setAccount(user.getAccount());
					o.setNickname(user.getNickname());
					if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
						o.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
						o.setAvatarName(user.getAvatarName());
					}		
				}
				if(o.getIsStaff()){//如果为员工
					o.setAccount(o.getUserName());//员工用户名和账号是同一个
				}
    		}
			List<Question> questionList = questionService.findTitleByIdList(questionIdList);
			if(questionList != null && questionList.size() >0){
				for(AnswerReply o :qr.getResultlist()){
					for(Question question : questionList){
						if(question.getId().equals(o.getQuestionId())){
							o.setQuestionTitle(question.getTitle());
							break;
						}
					}
					
				}
			}
			
		}

		pageView.setQueryResult(qr);
		
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
	}
	
}
