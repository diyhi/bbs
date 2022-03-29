package cms.web.action.template.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.question.Answer;
import cms.bean.question.AnswerReply;
import cms.bean.question.AppendQuestionItem;
import cms.bean.question.Question;
import cms.bean.question.QuestionTag;
import cms.bean.question.QuestionTagAssociation;
import cms.bean.setting.SystemSetting;
import cms.bean.template.Forum;
import cms.bean.template.Forum_AnswerRelated_Answer;
import cms.bean.template.Forum_QuestionRelated_LikeQuestion;
import cms.bean.template.Forum_QuestionRelated_Question;
import cms.bean.user.AccessUser;
import cms.bean.user.User;
import cms.service.question.AnswerService;
import cms.service.question.QuestionService;
import cms.service.question.QuestionTagService;
import cms.service.setting.SettingService;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.web.action.TextFilterManage;
import cms.web.action.common.CaptchaManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.lucene.QuestionLuceneManage;
import cms.web.action.question.AnswerManage;
import cms.web.action.question.QuestionManage;
import cms.web.action.setting.SettingManage;
import cms.web.action.user.UserManage;
import cms.web.action.user.UserRoleManage;

/**
 * 问题 -- 模板方法实现
 *
 */
@Component("question_TemplateManage")
public class Question_TemplateManage {
	
	
	@Resource QuestionService questionService; 
	@Resource SettingService settingService;
	@Resource SettingManage settingManage;
	@Resource QuestionTagService questionTagService;
	@Resource UserManage userManage;
	@Resource UserRoleManage userRoleManage;
	@Resource QuestionManage questionManage;
	@Resource TextFilterManage textFilterManage;
	@Resource AnswerService answerService;
	@Resource AnswerManage answerManage;
	@Resource CaptchaManage captchaManage;
	@Resource QuestionLuceneManage questionLuceneManage;
	@Resource FileManage fileManage;
	
	
	/**
	 * 问题列表  -- 分页
	 * @param forum
	 */
	public PageView<Question> question_page(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		
		String formValueJSON = forum.getFormValue();//表单值
		if(formValueJSON != null && !"".equals(formValueJSON)){
			Forum_QuestionRelated_Question forum_QuestionRelated_Question = JsonUtils.toObject(formValueJSON,Forum_QuestionRelated_Question.class);
			if(forum_QuestionRelated_Question != null){
				int maxResult = settingService.findSystemSetting_cache().getForestagePageNumber();
				//每页显示记录数
				if(forum_QuestionRelated_Question.getQuestion_maxResult() != null && forum_QuestionRelated_Question.getQuestion_maxResult() >0){
					maxResult = forum_QuestionRelated_Question.getQuestion_maxResult();
				}
				
				return this.question_SQL_Page(forum_QuestionRelated_Question,parameter,runtimeParameter,maxResult);
			}
			
		}
		return null;
	}
	
	
	/**
	 * 问题SQL分页
	 * @param maxResult 每页显示记录数
	 */
	private PageView<Question> question_SQL_Page(Forum_QuestionRelated_Question forum_QuestionRelated_Question,Map<String,Object> parameter,Map<String,Object> runtimeParameter,int maxResult){
		int page = 1;//分页 当前页
		int pageCount=10;// 页码显示总数
		int sort = 1;//排序
		Long questionTagId = null;//问题标签Id
		Integer filterCondition = null;//条件过滤
		
		String requestURI = "";
		String queryString = "";
		//标签Id
		if(forum_QuestionRelated_Question.getQuestion_tagId() != null && forum_QuestionRelated_Question.getQuestion_tagId() >0){
			questionTagId = forum_QuestionRelated_Question.getQuestion_tagId();
		}
		//排序
		if(forum_QuestionRelated_Question.getQuestion_sort() != null && forum_QuestionRelated_Question.getQuestion_sort() >0){
			sort = forum_QuestionRelated_Question.getQuestion_sort();
		}
		//条件过滤
		if(forum_QuestionRelated_Question.getQuestion_filterCondition() != null && forum_QuestionRelated_Question.getQuestion_filterCondition() >0){
			filterCondition = forum_QuestionRelated_Question.getQuestion_filterCondition();
		}
		//页码显示总数
		if(forum_QuestionRelated_Question.getQuestion_pageCount() != null && forum_QuestionRelated_Question.getQuestion_pageCount() >0){
			pageCount = forum_QuestionRelated_Question.getQuestion_pageCount();
		}
		
		//获取提交参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				
				if("page".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=9){
							page = Integer.parseInt(paramIter.getValue().toString());
						}
					}
				}else if("questionTagId".equals(paramIter.getKey())){
					if(forum_QuestionRelated_Question.isQuestion_tag_transferPrameter()){
						if(Verification.isNumeric(paramIter.getValue().toString())){
							if(paramIter.getValue().toString().length() <=18){
								questionTagId = Long.parseLong(paramIter.getValue().toString());	
							}
						}
					}
				}else if("filterCondition".equals(paramIter.getKey())){
					if(forum_QuestionRelated_Question.isQuestion_filterCondition_transferPrameter()){
						if(Verification.isNumeric(paramIter.getValue().toString())){
							if(paramIter.getValue().toString().length() <=8){
								filterCondition = Integer.parseInt(paramIter.getValue().toString());	
							}
						}
					}
				}
			}
		}
		
		
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("requestURI".equals(paramIter.getKey())){
					requestURI = (String)paramIter.getValue();
				}else if("queryString".equals(paramIter.getKey())){
					queryString = (String)paramIter.getValue();
				}
			}
		}
		
		PageForm pageForm = new PageForm();
		pageForm.setPage(page);
		
		//调用分页算法代码
		PageView<Question> pageView = new PageView<Question>(maxResult,pageForm.getPage(),pageCount,requestURI,queryString);
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();

		//执行查询
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		//标签Id
		if(questionTagId != null && questionTagId >0L){
			
			jpql.append(" and exists(select q.questionId from QuestionTagAssociation q where q.questionTagId = ?"+ (params.size()+1)+" and q.questionId=o.id) ");
			params.add(questionTagId);//加上查询参数	
		}
		
		if(filterCondition != null){
			if(filterCondition.equals(20)){//未解决：20
				jpql.append(" and o.adoptionAnswerId=?"+ (params.size()+1));
				params.add(0L);
			}else if(filterCondition.equals(30)){//已解决：30
				jpql.append(" and o.adoptionAnswerId>?"+ (params.size()+1));
				params.add(0L);
			}else if(filterCondition.equals(40)){//积分悬赏：40
				jpql.append(" and o.point>?"+ (params.size()+1));
				params.add(0L);
			}else if(filterCondition.equals(50)){//现金悬赏：50
				jpql.append(" and o.amount>?"+ (params.size()+1));
				params.add(new BigDecimal("0"));
			}
		}
		
		jpql.append(" and o.status=?"+ (params.size()+1));
		params.add(20);
		
		
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("sort", "desc");//优先级   大-->小
		//排行依据
		if(sort == 1){
			orderby.put("id", "desc");//发布时间排序   新-->旧
		}else if(sort == 2){
			orderby.put("id", "asc");//发布时间排序  旧-->新
		}else if(sort == 3){
			orderby.put("lastAnswerTime", "desc");//最后回答时间排序   新-->旧
		}else if(sort == 4){
			orderby.put("lastAnswerTime", "asc");//最后回答时间排序  旧-->新
		}
		
		
		
		//删除第一个and
		String jpql_str = StringUtils.difference(" and", jpql.toString());
		QueryResult<Question> qr = questionService.getScrollData(Question.class,firstIndex, maxResult, jpql_str, params.toArray(),orderby);

		
		if(qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(Question question : qr.getResultlist()){
				question.setIpAddress(null);//IP地址不显示
				question.setContent(null);//问题内容不显示
				if(question.getPostTime().equals(question.getLastAnswerTime())){//如果发贴时间等于回复时间，则不显示回复时间
					question.setLastAnswerTime(null);
				}
				if(question.getIsStaff() == false){//会员
					User user = userManage.query_cache_findUserByUserName(question.getUserName());
					if(user != null){
						question.setAccount(user.getAccount());
						question.setNickname(user.getNickname());
						question.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
						question.setAvatarName(user.getAvatarName());
						
						List<String> userRoleNameList = userRoleManage.queryUserRoleName(user.getUserName());
						if(userRoleNameList != null && userRoleNameList.size() >0){
							question.setUserRoleNameList(userRoleNameList);//用户角色名称集合
						}
						
						if(user.getCancelAccountTime() != -1L){//账号已注销
							question.setUserInfoStatus(-30);
						}
					}
					
				}else{
					question.setAccount(question.getUserName());//员工用户名和账号是同一个
				}
			}
			
			
			List<QuestionTag> questionTagList = questionTagService.findAllQuestionTag_cache();
			
			if(questionTagList != null && questionTagList.size() >0){
				for(Question question : qr.getResultlist()){
					List<QuestionTagAssociation> questionTagAssociationList = questionManage.query_cache_findQuestionTagAssociationByQuestionId(question.getId());
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
			//非正常状态用户清除显示数据
			for(Question question : qr.getResultlist()){
				if(question.getUserInfoStatus() <0){
					question.setUserName(null);
					question.setAccount(null);
					question.setNickname(null);
					question.setAvatarPath(null);
					question.setAvatarName(null);
					question.setUserRoleNameList(new ArrayList<String>());
					question.setTitle("");
					question.setSummary("");
					question.setContent("");
					question.getAppendQuestionItemList().clear();
				}
			}
		}
		
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		return pageView;
	}
	
	
	/**
	 * 问题-- 相似问题-- 集合
	 * @param forum
	 */
	public List<Question> question_like_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		
		String formValueJSON = forum.getFormValue();//表单值
		if(formValueJSON != null && !"".equals(formValueJSON)){
			Forum_QuestionRelated_LikeQuestion forum_QuestionRelated_LikeQuestion = JsonUtils.toObject(formValueJSON,Forum_QuestionRelated_LikeQuestion.class);
			if(forum_QuestionRelated_LikeQuestion != null){
				Integer  likeQuestion_maxResult = 10;
				if(forum_QuestionRelated_LikeQuestion.getLikeQuestion_maxResult() != null && forum_QuestionRelated_LikeQuestion.getLikeQuestion_maxResult() >0){
					likeQuestion_maxResult = forum_QuestionRelated_LikeQuestion.getLikeQuestion_maxResult();
				}
				Long questionId = null;
				
				if(parameter != null && parameter.size() >0){
					for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
						if("questionId".equals(paramIter.getKey())){
							if(Verification.isNumeric(paramIter.getValue().toString())){
								if(paramIter.getValue().toString().length() <=18){
									questionId = Long.parseLong(paramIter.getValue().toString());	
								}
							}
						}
					}
				}
				
				if(questionId != null && questionId > 0){
					return questionLuceneManage.findLikeQuestion(likeQuestion_maxResult,questionId,20);
				}
			}
		}
		return null;
	}
	
	
	/**
	 * 问题-- 问题内容 -- 实体对象
	 * @param forum 版块对象
	 * @param parameter 参数
	*/
	public Question content_entityBean(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		
		
		Long questionId = null;
		String ip = null;
		
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		if(parameter != null && parameter.size() >0){
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("questionId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							questionId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}
			}
		}
		
		if(runtimeParameter != null && runtimeParameter.size() >0){
			for(Map.Entry<String,Object> runtimeParameIter : runtimeParameter.entrySet()) {
				if("ip".equals(runtimeParameIter.getKey())){
					if(runtimeParameIter.getValue() != null && !"".equals(runtimeParameIter.getValue().toString().trim())){
						ip = runtimeParameIter.getValue().toString().trim();
					}
				}
			}
		}
		if(questionId != null && questionId > 0L){
			Question question = questionManage.query_cache_findById(questionId);//查询缓存
			
			if(question != null){
					
				if(ip != null){
					questionManage.addView(questionId, ip);
				}
				
				if(question.getStatus() >20){//20:已发布
					return null;
					
				}else{
					if(question.getStatus().equals(10) ){
						if(accessUser == null){
							return null;
						}else{
							if(!accessUser.getUserName().equals(question.getUserName())){
								return null;
							}
						}
					}	
				}
				question.setIpAddress(null);//IP地址不显示
				
				if(question.getContent() != null && !"".equals(question.getContent().trim())){
					//处理富文本路径
					question.setContent(fileManage.processRichTextFilePath(question.getContent(),"question"));
				}
				
				List<QuestionTag> questionTagList = questionTagService.findAllQuestionTag_cache();
				
				if(questionTagList != null && questionTagList.size() >0){
					List<QuestionTagAssociation> questionTagAssociationList = questionManage.query_cache_findQuestionTagAssociationByQuestionId(question.getId());
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
				
				//删除最后一个逗号
				String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

				List<AppendQuestionItem> appendQuestionItemList = JsonUtils.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
				if(appendQuestionItemList != null && appendQuestionItemList.size() >0){	
					for(AppendQuestionItem appendQuestionItem : appendQuestionItemList){
						if(appendQuestionItem.getContent() != null && !"".equals(appendQuestionItem.getContent().trim())){
							//处理富文本路径
							appendQuestionItem.setContent(fileManage.processRichTextFilePath(appendQuestionItem.getContent(),"question"));
						}
					}
				}
				
				
				
				question.setAppendQuestionItemList(appendQuestionItemList);
				
				
				User user = null;
				if(question.getIsStaff() == false){//会员
					user = userManage.query_cache_findUserByUserName(question.getUserName());
					if(user != null){
						question.setAccount(user.getAccount());
						question.setNickname(user.getNickname());
						question.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
						question.setAvatarName(user.getAvatarName());
						
						List<String> userRoleNameList = userRoleManage.queryUserRoleName(user.getUserName());
						if(userRoleNameList != null && userRoleNameList.size() >0){
							question.setUserRoleNameList(userRoleNameList);//用户角色名称集合
						}
						
						if(user.getCancelAccountTime() != -1L){//账号已注销
							question.setUserInfoStatus(-30);
						}
					}
					
				}else{
					question.setAccount(question.getUserName());//员工用户名和账号是同一个
				}
				
				//非正常状态用户清除显示数据
				if(question.getUserInfoStatus() <0){
					question.setUserName(null);
					question.setAccount(null);
					question.setNickname(null);
					question.setAvatarPath(null);
					question.setAvatarName(null);
					question.setUserRoleNameList(new ArrayList<String>());
					question.setTitle("");
					question.setContent("");
					question.getAppendQuestionItemList().clear();
				}
				
				return question;
			}
			
		}
		return null;
	}
	
	
	/**
	 * 问题  -- 添加
	 * @param forum
	 */
	public Map<String,Object> addQuestion_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		if(accessUser != null){
			boolean captchaKey = captchaManage.question_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				value.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
			}
			User user = userManage.query_cache_findUserByUserName(accessUser.getUserName());
			if(user != null){
				value.put("maxDeposit",user.getDeposit());//用户共有预存款
				value.put("maxPoint",user.getPoint());//用户共有积分
			}
		}
		
		
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		//如果全局不允许提交问题
		if(systemSetting.isAllowQuestion() == false){
			value.put("allowQuestion",false);//不允许提交问题
		}else{
			value.put("allowQuestion",true);//允许提交问题
		}
		value.put("questionRewardPointMin",systemSetting.getQuestionRewardPointMin());
		value.put("questionRewardPointMax",systemSetting.getQuestionRewardPointMax());
		value.put("questionRewardAmountMin",systemSetting.getQuestionRewardAmountMin());
		value.put("questionRewardAmountMax",systemSetting.getQuestionRewardAmountMax());
		
		
		value.put("maxQuestionTagQuantity", systemSetting.getMaxQuestionTagQuantity());//提交问题最多可选择标签数量
		
		value.put("availableTag", questionManage.availableTag());//问题编辑器允许使用标签
		value.put("fileSystem", fileManage.getFileSystem());
		return value;
	}
	
	/**
	 * 问题  -- 追加
	 * @param forum
	 */
	public Map<String,Object> appendQuestion_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		if(accessUser != null){
			boolean captchaKey = captchaManage.question_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				value.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
			}
		}
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		//如果全局不允许提交问题
		if(systemSetting.isAllowQuestion() == false){
			value.put("allowQuestion",false);//不允许提交问题
		}else{
			value.put("allowQuestion",true);//允许提交问题
		}
		
		value.put("availableTag", questionManage.availableTag());//问题编辑器允许使用标签
		value.put("fileSystem", fileManage.getFileSystem());
		return value;
	}
	
	/**
	 * 答案列表  -- 集合
	 * @param forum
	 */
	public PageView<Answer> answer_page(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		
		String formValueJSON = forum.getFormValue();//表单值
		if(formValueJSON != null && !"".equals(formValueJSON)){
			Forum_AnswerRelated_Answer forum_AnswerRelated_Answer = JsonUtils.toObject(formValueJSON,Forum_AnswerRelated_Answer.class);
			if(forum_AnswerRelated_Answer != null){
				int maxResult = settingService.findSystemSetting_cache().getForestagePageNumber();
				//每页显示记录数
				if(forum_AnswerRelated_Answer.getAnswer_maxResult() != null && forum_AnswerRelated_Answer.getAnswer_maxResult() >0){
					maxResult = forum_AnswerRelated_Answer.getAnswer_maxResult();
				}
				
				return this.answer_SQL_Page(forum_AnswerRelated_Answer,parameter,runtimeParameter,maxResult);

			}
			
		}
		return null;
	}
	
	
	/**
	 * 答案SQL分页
	 * @param maxResult 每页显示记录数
	 */
	private PageView<Answer> answer_SQL_Page(Forum_AnswerRelated_Answer forum_AnswerRelated_Answer,Map<String,Object> parameter,Map<String,Object> runtimeParameter,int maxResult){
		Integer page = null;//分页 当前页
		int pageCount=10;// 页码显示总数
		int sort = 1;//排序
		Long questionId = null;//问题Id
		Long answerId = null;//答案Id
		
		//排序
		if(forum_AnswerRelated_Answer.getAnswer_sort() != null && forum_AnswerRelated_Answer.getAnswer_sort() >0){
			sort = forum_AnswerRelated_Answer.getAnswer_sort();
		}
		
		//页码显示总数
		if(forum_AnswerRelated_Answer.getAnswer_pageCount() != null && forum_AnswerRelated_Answer.getAnswer_pageCount() >0){
			pageCount = forum_AnswerRelated_Answer.getAnswer_pageCount();
		}
		
		
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("page".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=9){
							page = Integer.parseInt(paramIter.getValue().toString());
						}
					}
				}else if("questionId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							questionId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
					
				}else if("answerId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							answerId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
					
				}
			}
		}
		String requestURI = "";
		String queryString = "";
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("requestURI".equals(paramIter.getKey())){
					requestURI = (String)paramIter.getValue();
				}else if("queryString".equals(paramIter.getKey())){
					queryString = (String)paramIter.getValue();
				}
			}
		}
		
		
		
		PageForm pageForm = new PageForm();
		pageForm.setPage(page);
		
		if(answerId != null && answerId >0L && page == null){
			Integer row_sort = 1;
			if(sort == 1){
				row_sort = 1;
			}else if(sort == 2){
				row_sort = 2;
			}
			Answer answer = answerManage.query_cache_findByAnswerId(answerId);//查询缓存
			if(answer != null && answer.getAdoption()){//如果答案为采纳，则显示第一页
				pageForm.setPage(1);
			}else{
				Long row = answerService.findRowByAnswerId(answerId,questionId,20,row_sort);
				if(row != null && row >0L){
					
					Integer _page = Integer.parseInt(String.valueOf(row))/maxResult;
					if(Integer.parseInt(String.valueOf(row))%maxResult >0){//余数大于0要加1
						
						_page = _page+1;
					}
					
					
					pageForm.setPage(_page);
				}
			}
			
			
		}
		
		
		
		
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		PageView<Answer> pageView = new PageView<Answer>(maxResult,pageForm.getPage(),pageCount,requestURI,queryString);
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

		if(questionId != null && questionId >0L){
			jpql.append(" o.questionId=?"+ (params.size()+1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
			params.add(questionId);//设置o.parentId=?2参数
		}else{
			return pageView;
		}
		
		
		
		
		jpql.append(" and o.status=?"+ (params.size()+1));
		params.add(20);
		
		orderby.put("adoption", "desc");//采纳答案时间排序   新-->旧
		//排行依据
		if(sort == 1){
			orderby.put("postTime", "desc");//回答时间排序   新-->旧
		}else if(sort == 2){
			orderby.put("postTime", "asc");//回答时间排序  旧-->新
		}
		//根据sort字段降序排序
		QueryResult<Answer> qr = answerService.getScrollData(Answer.class,firstIndex, pageView.getMaxresult(),jpql.toString(),params.toArray(),orderby);
		
		
		List<Long> answerIdList = new ArrayList<Long>();
		List<Answer> answerList = qr.getResultlist();
		
		
		Map<String,List<String>> userRoleNameMap = new HashMap<String,List<String>>();//用户角色名称 key:用户名称Id 角色名称集合
		if(answerList != null && answerList.size() >0){
			for(Answer answer : answerList){
				if(answer.getContent() != null && !"".equals(answer.getContent().trim())){
					//处理富文本路径
					answer.setContent(fileManage.processRichTextFilePath(answer.getContent(),"answer"));
				}
				answer.setIpAddress(null);//IP地址不显示
				if(answer.getIsStaff() == false){//会员
					User user = userManage.query_cache_findUserByUserName(answer.getUserName());
					if(user != null){
						answer.setAccount(user.getAccount());
						answer.setNickname(user.getNickname());
						answer.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
						answer.setAvatarName(user.getAvatarName());
						userRoleNameMap.put(answer.getUserName(), null);
						
						if(user.getCancelAccountTime() != -1L){//账号已注销
							answer.setUserInfoStatus(-30);
						}
					}
				}else{
					answer.setAccount(answer.getUserName());//员工用户名和账号是同一个
				}
				
				//非正常状态用户清除显示数据
				if(answer.getUserInfoStatus() <0){
					answer.setUserName(null);
					answer.setAccount(null);
					answer.setNickname(null);
					answer.setAvatarPath(null);
					answer.setAvatarName(null);
					answer.setUserRoleNameList(new ArrayList<String>());
					answer.setContent("");
				}
			}
			
		}
		
		if(userRoleNameMap != null && userRoleNameMap.size() >0){
			for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
				List<String> roleNameList = userRoleManage.queryUserRoleName(entry.getKey());
				entry.setValue(roleNameList);
			}
		}
		
		
		
		
		
		if(answerList != null && answerList.size() >0){
			for(Answer answer : answerList){
				answerIdList.add(answer.getId());

				
				//用户角色名称集合
				for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
					if(entry.getKey().equals(answer.getUserName())){
						List<String> roleNameList = entry.getValue();
						if(roleNameList != null && roleNameList.size() >0){
							answer.setUserRoleNameList(roleNameList);
						}
						break;
					}
				}
			}
		}
		
		if(answerIdList != null && answerIdList.size() >0){
			List<AnswerReply> replyList = answerService.findReplyByAnswerId(answerIdList,20);
			if(replyList != null && replyList.size() >0){
				for(Answer answer : answerList){
					for(AnswerReply answerReply : replyList){
						if(answer.getId().equals(answerReply.getAnswerId())){
							answerReply.setIpAddress(null);//IP地址不显示
							if(answerReply.getIsStaff() == false){//会员
								User user = userManage.query_cache_findUserByUserName(answerReply.getUserName());
								if(user != null){
									answerReply.setAccount(user.getAccount());
									answerReply.setNickname(user.getNickname());
									answerReply.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
									answerReply.setAvatarName(user.getAvatarName());
									
									List<String> roleNameList = userRoleManage.queryUserRoleName(answerReply.getUserName());
									answerReply.setUserRoleNameList(roleNameList);
									
									if(user.getCancelAccountTime() != -1L){//账号已注销
										answerReply.setUserInfoStatus(-30);
									}
								}
								
								
							}else{
								answerReply.setAccount(answerReply.getUserName());//员工用户名和账号是同一个
							}
							
							//非正常状态用户清除显示数据
							if(answerReply.getUserInfoStatus() <0){
								answerReply.setUserName(null);
								answerReply.setAccount(null);
								answerReply.setNickname(null);
								answerReply.setAvatarPath(null);
								answerReply.setAvatarName(null);
								answerReply.setUserRoleNameList(new ArrayList<String>());
								answerReply.setContent("");
							}
							
							answer.addAnswerReply(answerReply);
						}
					}
					
				}
			}
		}
		
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		return pageView;
	}
	
	/**
	 * 答案  -- 添加
	 * @param forum
	 */
	public Map<String,Object> addAnswer_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		if(accessUser != null){
			boolean captchaKey = captchaManage.answer_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				value.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
			}
		}
		
		//如果全局不允许提交答案
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.isAllowAnswer()){
			value.put("allowAnswer",true);//允许提交答案
		}else{
			value.put("allowAnswer",false);//不允许提交答案
		}
		
		value.put("availableTag", answerManage.availableTag());//答案编辑器允许使用标签
		value.put("fileSystem", fileManage.getFileSystem());
		return value;
	}
	
	/**
	 * 答案  -- 修改
	 * @param forum
	 */
	public Map<String,Object> editAnswer_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		Long answerId = null;
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		if(accessUser != null){
			boolean captchaKey = captchaManage.answer_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				value.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
			}
		}
		
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("answerId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							answerId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}
			}
		}
		
		if(accessUser != null && answerId != null && answerId >0L){
			Answer answer = answerManage.query_cache_findByAnswerId(answerId);//查询缓存
			if(answer != null && answer.getStatus() <100 && answer.getUserName().equals(accessUser.getUserName())){
				answer.setIpAddress(null);//IP地址不显示
				if(answer.getContent() != null && !"".equals(answer.getContent().trim())){
					//处理富文本路径
					answer.setContent(fileManage.processRichTextFilePath(answer.getContent(),"answer"));
				}
				value.put("answer",answer);
				
				
			}
		}
		

		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		//如果全局不允许提交答案
		if(systemSetting.isAllowAnswer() == false){
			value.put("allowAnswer",false);//不允许提交答案
		}else{
			value.put("allowAnswer",true);//允许提交答案
		}
		value.put("availableTag", answerManage.availableTag());//答案编辑器允许使用标签
		value.put("fileSystem", fileManage.getFileSystem());
		return value;
	}
	
	/**
	 * 答案  -- 回复添加
	 * @param forum
	 */
	public Map<String,Object> replyAnswer_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		
		if(accessUser != null){
			boolean captchaKey = captchaManage.answer_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				value.put("captchaKey",UUIDUtil.getUUID32());//验证码key
			}
		}
		//如果全局不允许提交答案
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.isAllowAnswer()){
			value.put("allowReply",true);//允许提交回复
		}else{
			value.put("allowReply",false);//不允许提交回复
		}
		return value;
	}
	/**
	 * 答案  -- 回复修改
	 * @param forum
	 */
	public Map<String,Object> editAnswerReply_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		Long replyId = null;
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		
		if(accessUser != null){
			boolean captchaKey = captchaManage.answer_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				value.put("captchaKey",UUIDUtil.getUUID32());//验证码key
			}
		}
		
		
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("replyId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							replyId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}
			}
		}
		
		if(accessUser != null && replyId != null && replyId >0L){
			AnswerReply answerReply = answerManage.query_cache_findReplyByReplyId(replyId);//查询缓存
			if(answerReply != null && answerReply.getStatus() <100 && answerReply.getUserName().equals(accessUser.getUserName())){
				answerReply.setIpAddress(null);//IP地址不显示
	
				value.put("reply",answerReply);
				
				
			}
		}
				
		
		
		//如果全局不允许提交答案
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.isAllowAnswer()){
			value.put("allowReply",true);//允许提交回复
		}else{
			value.put("allowReply",false);//不允许提交回复
		}
		return value;
	}
	
	/**
	 * 采纳答案
	 * @param forum
	 */
	public Map<String,Object> adoptionAnswer_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		return value;
	}
	
	/**
	 * 回答总数  -- 实体对象
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public Long answerCount_entityBean(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){	
		String userName = null;
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("userName".equals(paramIter.getKey())){
					userName = paramIter.getValue().toString();
				}
			}
		}
		
		if(userName != null && !"".equals(userName.trim())){
			User user = userManage.query_cache_findUserByUserName(userName.trim());
			if(user != null){
				return answerManage.query_cache_answerCount(user.getUserName());
			}
		}
		return 0L;
	}
}
