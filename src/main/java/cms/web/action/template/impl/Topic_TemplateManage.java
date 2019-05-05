package cms.web.action.template.impl;

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
import cms.bean.setting.SystemSetting;
import cms.bean.template.Forum;
import cms.bean.template.Forum_CommentRelated_Comment;
import cms.bean.template.Forum_TopicRelated_LikeTopic;
import cms.bean.template.Forum_TopicRelated_Topic;
import cms.bean.topic.Comment;
import cms.bean.topic.HideTagType;
import cms.bean.topic.ImageInfo;
import cms.bean.topic.Quote;
import cms.bean.topic.Reply;
import cms.bean.topic.Tag;
import cms.bean.topic.Topic;
import cms.bean.topic.TopicUnhide;
import cms.bean.user.AccessUser;
import cms.bean.user.User;
import cms.service.setting.SettingService;
import cms.service.topic.CommentService;
import cms.service.topic.TagService;
import cms.service.topic.TopicService;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.web.action.TextFilterManage;
import cms.web.action.common.CaptchaManage;
import cms.web.action.lucene.TopicLuceneManage;
import cms.web.action.setting.SettingManage;
import cms.web.action.topic.CommentManage;
import cms.web.action.topic.TopicManage;
import cms.web.action.user.UserManage;

/**
 * 话题 -- 模板方法实现
 *
 */
@Component("topic_TemplateManage")
public class Topic_TemplateManage {
	@Resource TopicService topicService; 
	@Resource TagService tagService;
	@Resource CommentService commentService; 

	@Resource TextFilterManage textFilterManage;
	@Resource CommentManage commentManage;
	@Resource CaptchaManage captchaManage;
	
	@Resource SettingService settingService;
	@Resource SettingManage settingManage;
	@Resource TopicManage topicManage;
	
	@Resource TopicLuceneManage topicLuceneManage;
	
	@Resource UserManage userManage;
	
	
	/**
	 * 话题列表  -- 分页
	 * @param forum
	 */
	public PageView<Topic> topic_page(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		
		String formValueJSON = forum.getFormValue();//表单值
		if(formValueJSON != null && !"".equals(formValueJSON)){
			Forum_TopicRelated_Topic forum_TopicRelated_Topic = JsonUtils.toObject(formValueJSON,Forum_TopicRelated_Topic.class);
			if(forum_TopicRelated_Topic != null){
				int maxResult = settingService.findSystemSetting_cache().getForestagePageNumber();
				//每页显示记录数
				if(forum_TopicRelated_Topic.getTopic_maxResult() != null && forum_TopicRelated_Topic.getTopic_maxResult() >0){
					maxResult = forum_TopicRelated_Topic.getTopic_maxResult();
				}
				
				return this.topic_SQL_Page(forum_TopicRelated_Topic,parameter,runtimeParameter,maxResult);
			}
			
		}
		return null;
	}
	
	
	/**
	 * 话题SQL分页
	 * @param maxResult 每页显示记录数
	 */
	private PageView<Topic> topic_SQL_Page(Forum_TopicRelated_Topic forum_TopicRelated_Topic,Map<String,Object> parameter,Map<String,Object> runtimeParameter,int maxResult){
		int page = 1;//分页 当前页
		int pageCount=10;// 页码显示总数
		int sort = 1;//排序
		Long tagId = null;//话题Id
		
		String requestURI = "";
		String queryString = "";
		//话题Id
		if(forum_TopicRelated_Topic.getTopic_tagId() != null && forum_TopicRelated_Topic.getTopic_tagId() >0){
			tagId = forum_TopicRelated_Topic.getTopic_tagId();
		}
		//排序
		if(forum_TopicRelated_Topic.getTopic_sort() != null && forum_TopicRelated_Topic.getTopic_sort() >0){
			sort = forum_TopicRelated_Topic.getTopic_sort();
		}
		
		
		
		//页码显示总数
		if(forum_TopicRelated_Topic.getTopic_pageCount() != null && forum_TopicRelated_Topic.getTopic_pageCount() >0){
			pageCount = forum_TopicRelated_Topic.getTopic_pageCount();
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
				}else if("tagId".equals(paramIter.getKey())){
					if(forum_TopicRelated_Topic.isTopic_tag_transferPrameter()){
						if(Verification.isNumeric(paramIter.getValue().toString())){
							if(paramIter.getValue().toString().length() <=18){
								tagId = Long.parseLong(paramIter.getValue().toString());	
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
		
		//调用分页算法代码
		PageView<Topic> pageView = new PageView<Topic>(maxResult,page,pageCount,requestURI,queryString);
		//当前页
		int firstIndex = (page-1)*pageView.getMaxresult();

		//执行查询
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		
		
		
		
		//标签Id
		if(tagId != null){
			jpql.append(" and o.tagId=?"+ (params.size()+1));
			params.add(tagId);//加上查询参数
		}

		jpql.append(" and o.status=?"+ (params.size()+1));
		params.add(20);//设置o.visible=?1是否可见
		
		
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("sort", "desc");//优先级   大-->小
		//排行依据
		if(sort == 1){
			orderby.put("id", "desc");//发布时间排序   新-->旧
		}else if(sort == 2){
			orderby.put("id", "asc");//发布时间排序  旧-->新
		}	
		//删除第一个and
		String jpql_str = StringUtils.difference(" and", jpql.toString());
		QueryResult<Topic> qr = topicService.getScrollData(Topic.class,firstIndex, maxResult, jpql_str, params.toArray(),orderby);

		
		if(qr.getResultlist() != null && qr.getResultlist().size() >0){
			//查询标签名称
			List<Tag> tagList = tagService.findAllTag_cache();
			if(tagList != null && tagList.size() >0){
				for(Topic topic : qr.getResultlist()){
					topic.setIpAddress(null);//IP地址不显示
					topic.setContent(null);//话题内容不显示
					for(Tag tag: tagList){
						if(topic.getTagId().equals(tag.getId())){
							topic.setTagName(tag.getName());
							break;
						}
					}
					
					if(topic.getImage() != null && !"".equals(topic.getImage().trim())){
						List<ImageInfo> imageInfoList = JsonUtils.toGenericObject(topic.getImage().trim(),new TypeReference< List<ImageInfo> >(){});
						if(imageInfoList != null && imageInfoList.size() >0){
							topic.setImageInfoList(imageInfoList);
						}
					}
					
				}
			}
			
			for(Topic topic : qr.getResultlist()){
				if(topic.getIsStaff() == false){//会员
					User user = userManage.query_cache_findUserByUserName(topic.getUserName());
					
					topic.setAvatarPath(user.getAvatarPath());
					topic.setAvatarName(user.getAvatarName());
				}
				
			}
			
		}
		
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		return pageView;
	}
	
	
	/**
	 * 话题-- 相似话题-- 集合
	 * @param forum
	 */
	public List<Topic> topic_like_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		
		String formValueJSON = forum.getFormValue();//表单值
		if(formValueJSON != null && !"".equals(formValueJSON)){
			Forum_TopicRelated_LikeTopic forum_TopicRelated_LikeTopic = JsonUtils.toObject(formValueJSON,Forum_TopicRelated_LikeTopic.class);
			if(forum_TopicRelated_LikeTopic != null){
				Integer  likeTopic_maxResult = 10;
				if(forum_TopicRelated_LikeTopic.getLikeTopic_maxResult() != null && forum_TopicRelated_LikeTopic.getLikeTopic_maxResult() >0){
					likeTopic_maxResult = forum_TopicRelated_LikeTopic.getLikeTopic_maxResult();
				}
				Long topicId = null;
				
				if(parameter != null && parameter.size() >0){
					for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
						if("topicId".equals(paramIter.getKey())){
							if(Verification.isNumeric(paramIter.getValue().toString())){
								if(paramIter.getValue().toString().length() <=18){
									topicId = Long.parseLong(paramIter.getValue().toString());	
								}
							}
						}
					}
				}
				
				
				
				if(topicId != null && topicId > 0){
					return topicLuceneManage.findLikeTopic(likeTopic_maxResult,topicId,20);
				}
			}
		}
		return null;
	}
	
	
	
	/**
	 * 话题-- 话题内容 -- 实体对象
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public Topic content_entityBean(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		
		Long topicId = null;
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
				if("topicId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							topicId = Long.parseLong(paramIter.getValue().toString());	
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
		if(topicId != null && topicId > 0L){
			Topic topic = topicManage.queryTopicCache(topicId);//查询缓存
			
			if(topic != null){
				if(ip != null){
					topicManage.addView(topicId, ip);
				}
				
				if(topic.getStatus() >20){//20:已发布
					return null;
					
				}else{
					if(topic.getStatus().equals(10) ){
						if(accessUser == null){
							return null;
						}else{
							if(!accessUser.getUserName().equals(topic.getUserName())){
								return null;
							}
						}
					}	
				}
				topic.setIpAddress(null);//IP地址不显示
				List<Tag> tagList = tagService.findAllTag_cache();
				if(tagList != null && tagList.size() >0){
					for(Tag tag :tagList){
						if(topic.getTagId().equals(tag.getId())){
							topic.setTagName(tag.getName());
							break;
						}
						
					}
				}
				User user = null;
				if(topic.getIsStaff() == false){//会员
					user = userManage.query_cache_findUserByUserName(topic.getUserName());
					if(user != null){
						topic.setAvatarPath(user.getAvatarPath());
						topic.setAvatarName(user.getAvatarName());
					}
				}
				
				//处理隐藏标签
				if(topic.getContent() != null && !"".equals(topic.getContent().trim())){
					//允许可见的隐藏标签
					List<Integer> visibleTagList = new ArrayList<Integer>();
					if(accessUser != null){
						//如果话题由当前用户发表，则显示全部隐藏内容
						if(topic.getIsStaff() == false && topic.getUserName().equals(accessUser.getUserName())){
							visibleTagList.add(HideTagType.PASSWORD.getName());
							visibleTagList.add(HideTagType.COMMENT.getName());
							visibleTagList.add(HideTagType.GRADE.getName());
							visibleTagList.add(HideTagType.POINT.getName());
							visibleTagList.add(HideTagType.AMOUNT.getName());
						}else{
							//解析隐藏标签
							Map<Integer,Object> analysisHiddenTagMap = topicManage.query_cache_analysisHiddenTag(topic.getContent(),topic.getId());
							for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {
								
								if(entry.getKey().equals(HideTagType.PASSWORD.getName())){//输入密码可见
									//话题取消隐藏Id
								  	String topicUnhideId = topicManage.createTopicUnhideId(accessUser.getUserId(), HideTagType.PASSWORD.getName(), topicId);
								  
									TopicUnhide topicUnhide = topicManage.query_cache_findTopicUnhideById(topicUnhideId);
							  		
							  		if(topicUnhide != null){
							  			visibleTagList.add(HideTagType.PASSWORD.getName());//当前话题已经取消隐藏
								  	}
								}else if(entry.getKey().equals(HideTagType.COMMENT.getName())){//评论话题可见
									Boolean isUnhide = topicManage.query_cache_findTopicUnhideById(topicId,accessUser.getUserName());
									if(isUnhide){
										visibleTagList.add(HideTagType.COMMENT.getName());//当前话题已经取消隐藏
									}
								}else if(entry.getKey().equals(HideTagType.GRADE.getName())){//超过等级可见
									
								}else if(entry.getKey().equals(HideTagType.POINT.getName())){//积分购买可见
									
								}else if(entry.getKey().equals(HideTagType.AMOUNT.getName())){//余额购买可见
									
								}
								
							}
						}
					}
					//生成处理'隐藏标签'Id
					String processHideTagId = topicManage.createProcessHideTagId(topicId,topic.getLastUpdateTime(), visibleTagList);
					//处理隐藏标签
					String content = topicManage.query_cache_processHiddenTag(topic.getContent(),visibleTagList,processHideTagId);
					//String content = textFilterManage.processHiddenTag(topic.getContent(),visibleTagList);
					topic.setContent(content);
				}
				
				return topic;
			}
			
		}
		return null;
	}
	
	/**
	 * 话题  -- 取消隐藏
	 * @param forum
	 */
	public Map<String,Object> topicUnhide_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		

		return value;
	}
	
	/**
	 * 话题  -- 添加
	 * @param forum
	 */
	public Map<String,Object> addTopic_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
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
			boolean captchaKey = captchaManage.topic_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				value.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
			}
		}
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		//如果全局不允许提交话题
		if(systemSetting.isAllowTopic() == false){
			value.put("allowTopic",false);//不允许提交话题
		}else{
			value.put("allowTopic",true);//允许提交话题
		}
		
		
		return value;
	}
	
	/**
	 * 评论列表  -- 集合
	 * @param forum
	 */
	public PageView<Comment> comment_page(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		
		String formValueJSON = forum.getFormValue();//表单值
		if(formValueJSON != null && !"".equals(formValueJSON)){
			Forum_CommentRelated_Comment forum_CommentRelated_Comment = JsonUtils.toObject(formValueJSON,Forum_CommentRelated_Comment.class);
			if(forum_CommentRelated_Comment != null){
				int maxResult = settingService.findSystemSetting_cache().getForestagePageNumber();
				//每页显示记录数
				if(forum_CommentRelated_Comment.getComment_maxResult() != null && forum_CommentRelated_Comment.getComment_maxResult() >0){
					maxResult = forum_CommentRelated_Comment.getComment_maxResult();
				}
				
				return this.comment_SQL_Page(forum_CommentRelated_Comment,parameter,runtimeParameter,maxResult);

			}
			
		}
		return null;
	}
	
	
	/**
	 * 评论SQL分页
	 * @param maxResult 每页显示记录数
	 */
	private PageView<Comment> comment_SQL_Page(Forum_CommentRelated_Comment forum_CommentRelated_Comment,Map<String,Object> parameter,Map<String,Object> runtimeParameter,int maxResult){
		Integer page = null;//分页 当前页
		int pageCount=10;// 页码显示总数
		int sort = 1;//排序
		Long topicId = null;//话题Id
		Long commentId = null;//评论Id
		
		//排序
		if(forum_CommentRelated_Comment.getComment_sort() != null && forum_CommentRelated_Comment.getComment_sort() >0){
			sort = forum_CommentRelated_Comment.getComment_sort();
		}
		
		//页码显示总数
		if(forum_CommentRelated_Comment.getComment_pageCount() != null && forum_CommentRelated_Comment.getComment_pageCount() >0){
			pageCount = forum_CommentRelated_Comment.getComment_pageCount();
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
				}else if("topicId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							topicId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
					
				}else if("commentId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							commentId = Long.parseLong(paramIter.getValue().toString());	
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
		
		if(commentId != null && commentId >0L && page == null){
			Integer row_sort = 1;
			if(sort == 1){
				row_sort = 1;
			}else if(sort == 2){
				row_sort = 2;
			}
			
			
			Long row = commentService.findRowByCommentId(commentId,topicId,20,row_sort);
			if(row != null && row >0L){
				
				
				Integer _page = Integer.parseInt(String.valueOf(row))/maxResult;
				if(Integer.parseInt(String.valueOf(row))%maxResult >0){//余数大于0要加1
					
					_page = _page+1;
				}
				
				
				pageForm.setPage(_page);
				
			}
		}
		
		
		
		
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		PageView<Comment> pageView = new PageView<Comment>(maxResult,pageForm.getPage(),pageCount,requestURI,queryString);
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

		if(topicId != null && topicId >0L){
			jpql.append(" o.topicId=?"+ (params.size()+1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
			params.add(topicId);//设置o.parentId=?2参数
		}
		jpql.append(" and o.status=?"+ (params.size()+1));
		params.add(20);
		
		//排行依据
		if(sort == 1){
			orderby.put("postTime", "desc");//发布时间排序   新-->旧
		}else if(sort == 2){
			orderby.put("postTime", "asc");//发布时间排序  旧-->新
		}
		//根据sort字段降序排序
		QueryResult<Comment> qr = commentService.getScrollData(Comment.class,firstIndex, pageView.getMaxresult(),jpql.toString(),params.toArray(),orderby);
		
		
		List<Long> commentIdList = new ArrayList<Long>();
		List<Comment> commentList = qr.getResultlist();
		
		//引用修改Id集合
		List<Long> quoteUpdateIdList = new ArrayList<Long>();
		//重新查询Id
		List<Long> query_quoteUpdateIdList = new ArrayList<Long>();
		//新引用集合
		Map<Long,String> new_quoteList = new HashMap<Long,String>();//key:自定义评论Id value:自定义评论内容(文本)
		
		if(commentList != null && commentList.size() >0){
			for(Comment comment : commentList){
				comment.setIpAddress(null);//IP地址不显示
				if(comment.getIsStaff() == false){//会员
					User user = userManage.query_cache_findUserByUserName(comment.getUserName());
					
					comment.setAvatarPath(user.getAvatarPath());
					comment.setAvatarName(user.getAvatarName());
				}
				
				
				if(comment.getQuoteUpdateId() != null && comment.getQuoteUpdateId().length() >1){
					String[] quoteUpdateId_arr = comment.getQuoteUpdateId().split(",");
					if(quoteUpdateId_arr != null && quoteUpdateId_arr.length >0){
						for(String quoteUpdateId : quoteUpdateId_arr){
							if(quoteUpdateId != null && !"".equals(quoteUpdateId.trim())){
								Long l = Long.parseLong(quoteUpdateId);
								if(!quoteUpdateIdList.contains(l)){
									quoteUpdateIdList.add(l);
								}
							}
						}
					}
				}
			}
			
			A:for(Long quoteUpdateId : quoteUpdateIdList){
				for(Comment comment : commentList){
					if(comment.getId().equals(quoteUpdateId)){
						new_quoteList.put(comment.getId(), textFilterManage.filterText(comment.getContent()));
						continue A;
					}
				}
				query_quoteUpdateIdList.add(quoteUpdateId);
			}
		}
		
		
		
		
		if(query_quoteUpdateIdList != null && query_quoteUpdateIdList.size() >0){
			List<Comment> quote_commentList = commentService.findByCommentIdList(query_quoteUpdateIdList);
			if(quote_commentList != null && quote_commentList.size() >0){
				for(Comment comment : quote_commentList){
					new_quoteList.put(comment.getId(), textFilterManage.filterText(comment.getContent()));
				}
			}
		}
		
		
		
		
		if(commentList != null && commentList.size() >0){
			for(Comment comment : commentList){
				commentIdList.add(comment.getId());
				if(comment.getQuote() != null && !"".equals(comment.getQuote().trim())){
					//旧引用
					List<Quote> quoteList = JsonUtils.toGenericObject(comment.getQuote(), new TypeReference< List<Quote> >(){});
					if(quoteList != null && quoteList.size() >0){
						for(Quote quote :quoteList){
							if(new_quoteList.containsKey(quote.getCommentId())){
								quote.setContent(new_quoteList.get(quote.getCommentId()));
							}
						}
					}
					comment.setQuoteList(quoteList);
				}
			}
		}
		
		if(commentIdList != null && commentIdList.size() >0){
			List<Reply> replyList = commentService.findReplyByCommentId(commentIdList,20);
			if(replyList != null && replyList.size() >0){
				for(Comment comment : commentList){
					for(Reply reply : replyList){
						if(comment.getId().equals(reply.getCommentId())){
							reply.setIpAddress(null);//IP地址不显示
							if(reply.getIsStaff() == false){//会员
								User user = userManage.query_cache_findUserByUserName(reply.getUserName());
								
								reply.setAvatarPath(user.getAvatarPath());
								reply.setAvatarName(user.getAvatarName());
							}
							
							comment.addReply(reply);
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
	 * 评论  -- 添加
	 * @param forum
	 */
	public Map<String,Object> addComment_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
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
			boolean captchaKey = captchaManage.comment_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				value.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
			}
		}
		
		//如果全局不允许提交评论
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.isAllowComment()){
			value.put("allowComment",true);//允许提交评论
		}else{
			value.put("allowComment",false);//不允许提交评论
		}
		
		value.put("availableTag", commentManage.availableTag());//自定义评论编辑器允许使用标签
		return value;
	}
	
	
	/**
	 * 评论  -- 引用添加
	 * @param forum
	 */
	public Map<String,Object> quoteComment_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		Long commentId = null;
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
			boolean captchaKey = captchaManage.comment_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey == true){
				value.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
			}
		}
		
		
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("commentId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							commentId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}
			}
		}
		
		if(commentId != null && commentId >0L){
			Comment comment = commentService.findByCommentId(commentId);
			value.put("quoteContent", textFilterManage.filterText(comment.getContent()));//引用内容
		}
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		
		if(systemSetting.isAllowComment()){
			value.put("allowComment",true);//允许提交评论
		}else{
			value.put("allowComment",false);//不允许提交评论
		}
		value.put("availableTag", commentManage.availableTag());//评论编辑器允许使用标签
		return value;
	}
	/**
	 * 评论  -- 回复添加
	 * @param forum
	 */
	public Map<String,Object> replyComment_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
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
			boolean captchaKey = captchaManage.comment_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				value.put("captchaKey",UUIDUtil.getUUID32());//验证码key
			}
		}
		//如果全局不允许提交评论
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.isAllowComment()){
			value.put("allowReply",true);//允许提交回复
		}else{
			value.put("allowReply",false);//不允许提交回复
		}
		return value;
	}
}
