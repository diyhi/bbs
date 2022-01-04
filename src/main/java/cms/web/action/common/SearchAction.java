package cms.web.action.common;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.wltea.analyzer.lucene.IKAnalyzer;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.fulltext.SearchResult;
import cms.bean.question.Question;
import cms.bean.topic.ImageInfo;
import cms.bean.topic.Tag;
import cms.bean.topic.Topic;
import cms.bean.user.ResourceEnum;
import cms.bean.user.User;
import cms.service.question.QuestionService;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.service.topic.TagService;
import cms.service.topic.TopicService;
import cms.utils.HtmlEscape;
import cms.utils.JsonUtils;
import cms.utils.WebUtil;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.lucene.QuestionLuceneInit;
import cms.web.action.lucene.TopicLuceneInit;
import cms.web.action.lucene.TopicLuceneManage;
import cms.web.action.user.UserManage;
import cms.web.action.user.UserRoleManage;

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
	@Resource UserRoleManage userRoleManage;
	@Resource TextFilterManage textFilterManage;
	@Resource QuestionService questionService;
	@Resource FileManage fileManage;
	
	
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
		PageView<SearchResult> pageView = new PageView<SearchResult>(settingService.findSystemSetting_cache().getForestagePageNumber(), pageForm.getPage(), 10,request.getRequestURI(),request.getQueryString());
		
		
		if(error.size() == 0){
			QueryResult<SearchResult> qr = this.findIndexByCondition(pageView.getCurrentpage(),pageView.getMaxresult(), keyword.trim(),20, 1,false);
			
			if(qr.getResultlist() != null && qr.getResultlist().size() >0){
				List<Long> topicIdList =  new ArrayList<Long>();//话题Id集合
				Map<Long,List<String>> tagRoleNameMap = new HashMap<Long,List<String>>();//标签角色名称 key:标签Id 角色名称集合
				Map<String,List<String>> userRoleNameMap = new HashMap<String,List<String>>();//用户角色名称 key:用户名称Id 角色名称集合
				Map<Long,Boolean> userViewPermissionMap = new HashMap<Long,Boolean>();//用户如果对话题项是否有查看权限  key:标签Id value:是否有查看权限
				List<Long> questionIdList =  new ArrayList<Long>();//问题Id集合
				

				for(SearchResult searchResult : qr.getResultlist()){
					if(searchResult.getIndexModule().equals(10)){//话题模块
						topicIdList.add(searchResult.getTopic().getId());
					}else if(searchResult.getIndexModule().equals(20)){//问题模块
						questionIdList.add(searchResult.getQuestion().getId());
					}
				}
				
				List<Topic> topicList = null;
				if(topicIdList != null && topicIdList.size() >0){
					topicList = topicService.findByIdList(topicIdList);
				}
				List<Question> questionList = null;
				//问题
				if(questionIdList != null && questionIdList.size() >0){
					questionList = questionService.findByIdList(questionIdList);
				}
				
				Iterator<SearchResult> iter = qr.getResultlist().iterator();
				A:while (iter.hasNext()) {
					SearchResult searchResult = iter.next();
					if(searchResult.getIndexModule().equals(10)){//话题模块
						if(topicIdList != null && topicIdList.size() >0){
							Topic old_topic = searchResult.getTopic();
							for(Topic t : topicList){
								if(old_topic.getId().equals(t.getId())){
									t.setTitle(old_topic.getTitle());
									t.setContent(old_topic.getContent());
									t.setIp(null);//IP不显示
									if(t.getPostTime().equals(t.getLastReplyTime())){//如果发贴时间等于回复时间，则不显示回复时间
										t.setLastReplyTime(null);
									}
									if(t.getIsStaff() == false){//会员
										User user = userManage.query_cache_findUserByUserName(t.getUserName());
										t.setAccount(user.getAccount());
										t.setNickname(user.getNickname());
										t.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
										t.setAvatarName(user.getAvatarName());
										
										
										userRoleNameMap.put(t.getUserName(), null);
										
									}else{
										t.setAccount(t.getUserName());//员工用户名和账号是同一个
									}
									searchResult.setTopic(t);
									continue A;
								}
							}
						}
					}else if(searchResult.getIndexModule().equals(20)){//问题模块
						if(questionList != null && questionList.size() >0){
							Question old_question = searchResult.getQuestion();
							for(Question t : questionList){
								if(old_question.getId().equals(t.getId())){
									t.setTitle(old_question.getTitle());
									t.setContent(old_question.getContent());
									t.setIp(null);//IP不显示
									if(t.getPostTime().equals(t.getLastAnswerTime())){//如果发贴时间等于回复时间，则不显示回复时间
										t.setLastAnswerTime(null);
									}
									if(t.getIsStaff() == false){//会员
										User user = userManage.query_cache_findUserByUserName(t.getUserName());
										t.setAccount(user.getAccount());
										t.setNickname(user.getNickname());
										t.setAvatarPath(fileManage.fileServerAddress()+user.getAvatarPath());
										t.setAvatarName(user.getAvatarName());
										
										
										userRoleNameMap.put(t.getUserName(), null);
										
									}else{
										t.setAccount(t.getUserName());//员工用户名和账号是同一个
									}
									searchResult.setQuestion(t);
									continue A;
								}
							}
						}
					}

					//如果SQL查询不存在则删除本条记录
					iter.remove();
					
				}
				
				List<Tag> tagList = tagService.findAllTag_cache();
				if(tagList != null && tagList.size() >0 && topicIdList != null && topicIdList.size() >0){
					for(Topic t : topicList){
						for(Tag tag :tagList){
							if(t.getTagId().equals(tag.getId())){
								t.setTagName(tag.getName());
								tagRoleNameMap.put(t.getTagId(), null);
								userViewPermissionMap.put(t.getTagId(), null);
								break;
							}
							
						}
					}
				}
				
				
				/**
				//话题
				if(topicIdList != null && topicIdList.size() >0){
					List<Topic> topicList = topicService.findByIdList(topicIdList);
					if(topicList != null && topicList.size() >0){
						for(SearchResult searchResult : qr.getResultlist()){
							if(searchResult.getIndexModule().equals(10)){//话题模块
							
								Topic old_t = searchResult.getTopic();
								for(Topic pi : topicList){
									if(pi.getId().equals(old_t.getId())){
										pi.setTitle(old_t.getTitle());
										pi.setContent(old_t.getContent());
										pi.setIp(null);//IP不显示
										if(pi.getPostTime().equals(pi.getLastReplyTime())){//如果发贴时间等于回复时间，则不显示回复时间
											pi.setLastReplyTime(null);
										}
										if(pi.getIsStaff() == false){//会员
											User user = userManage.query_cache_findUserByUserName(pi.getUserName());
											pi.setNickname(user.getNickname());
											pi.setAvatarPath(user.getAvatarPath());
											pi.setAvatarName(user.getAvatarName());
											
											
											userRoleNameMap.put(pi.getUserName(), null);
											
										}
										
										searchResult.setTopic(pi);
										break;
									}
								}
							}
						}
						
						List<Tag> tagList = tagService.findAllTag_cache();
						if(tagList != null && tagList.size() >0){
							for(Topic pi : topicList){
								for(Tag tag :tagList){
									if(pi.getTagId().equals(tag.getId())){
										pi.setTagName(tag.getName());
										tagRoleNameMap.put(pi.getTagId(), null);
										userViewPermissionMap.put(pi.getTagId(), null);
										break;
									}
									
								}
							}
						}			
					}	
				}			
				//问题
				if(questionIdList != null && questionIdList.size() >0){
					List<Question> questionList = questionService.findByIdList(questionIdList);
					if(questionList != null && questionList.size() >0){
						for(SearchResult searchResult : qr.getResultlist()){
							if(searchResult.getIndexModule().equals(20)){//问题模块
								Question old_t = searchResult.getQuestion();
								for(Question pi : questionList){
									if(pi.getId().equals(old_t.getId())){
										pi.setTitle(old_t.getTitle());
										pi.setContent(old_t.getContent());
										pi.setIp(null);//IP不显示
										if(pi.getPostTime().equals(pi.getLastAnswerTime())){//如果发贴时间等于回复时间，则不显示回复时间
											pi.setLastAnswerTime(null);
										}
										if(pi.getIsStaff() == false){//会员
											User user = userManage.query_cache_findUserByUserName(pi.getUserName());
											pi.setNickname(user.getNickname());
											pi.setAvatarPath(user.getAvatarPath());
											pi.setAvatarName(user.getAvatarName());
											
											
											userRoleNameMap.put(pi.getUserName(), null);
											
										}
										searchResult.setQuestion(pi);
										break;
									}
								}
							}
						}
					}
				
				}
				**/
				
				if(tagRoleNameMap != null && tagRoleNameMap.size() >0){
					for (Map.Entry<Long, List<String>> entry : tagRoleNameMap.entrySet()) {
						List<String> roleNameList = userRoleManage.queryAllowViewTopicRoleName(entry.getKey());
						entry.setValue(roleNameList);
					}
				}
				
				if(userRoleNameMap != null && userRoleNameMap.size() >0){
					for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
						List<String> roleNameList = userRoleManage.queryUserRoleName(entry.getKey());
						entry.setValue(roleNameList);
					}
				}
				if(userViewPermissionMap != null && userViewPermissionMap.size()>0){
					for (Map.Entry<Long,Boolean> entry : userViewPermissionMap.entrySet()) {
						//是否有当前功能操作权限
						boolean flag = userRoleManage.isPermission(ResourceEnum._1001000,entry.getKey());
						entry.setValue(flag);
					}
				}
				
				for(SearchResult searchResult : qr.getResultlist()){
					if(searchResult.getIndexModule().equals(10)){//话题模块
						Topic topic = searchResult.getTopic();
						//话题允许查看的角色名称集合
						for (Map.Entry<Long, List<String>> entry : tagRoleNameMap.entrySet()) {
							if(entry.getKey().equals(topic.getTagId())){
								List<String> roleNameList = entry.getValue();
								if(roleNameList != null && roleNameList.size() >0){
									topic.setAllowRoleViewList(roleNameList);
								}
								break;
							}
							
						}
						//用户角色名称集合
						for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
							if(entry.getKey().equals(topic.getUserName())){
								List<String> roleNameList = entry.getValue();
								if(roleNameList != null && roleNameList.size() >0){
									topic.setUserRoleNameList(roleNameList);
								}
								break;
							}
						}
						
						//用户如果对话题项无查看权限，则不显示摘要和图片
						for (Map.Entry<Long,Boolean> entry : userViewPermissionMap.entrySet()) {
							if(entry.getKey().equals(topic.getTagId())){
								if(entry.getValue() != null && !entry.getValue()){
									topic.setImage(null);
									topic.setImageInfoList(new ArrayList<ImageInfo>());
									topic.setSummary("");
									topic.setContent("");
								}
								break;
							}
							
						}
					}else if(searchResult.getIndexModule().equals(20)){//问题模块
						Question question = searchResult.getQuestion();
						//用户角色名称集合
						for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
							if(entry.getKey().equals(question.getUserName())){
								List<String> roleNameList = entry.getValue();
								if(roleNameList != null && roleNameList.size() >0){
									question.setUserRoleNameList(roleNameList);
								}
								break;
							}
						}
					}
				}
				
			}
			
			pageView.setQueryResult(qr);
		}

		
		
		
		
		if(isAjax == true){
			returnValue.put("searchResultPage",pageView);
			
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
			model.addAttribute("searchResultPage",pageView);
			
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
	
	
	/**
	 * 根据条件查询索引
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 * @param keyword 关键词
	 * @param status 状态
	 * @param sortCondition 排序条件
	 * @param isHide 是否显示隐藏标签内容
	 * @return
	 */
	private QueryResult<SearchResult> findIndexByCondition(int firstIndex, int maxResult,String keyword,Integer status,int sortCondition,boolean isHide){
		QueryResult<SearchResult> qr = new QueryResult<SearchResult>();
		//存储符合条件的记录   
	    List<SearchResult> searchResultList = new ArrayList<SearchResult>();  
		
		IndexSearcher searcher_topic =  TopicLuceneInit.INSTANCE.getSearcher();
		IndexSearcher searcher_question =  QuestionLuceneInit.INSTANCE.getSearcher();
		IndexSearcher indexSearcher = null;
		if(searcher_topic != null && searcher_question != null){
			Analyzer analyzer_keyword = new IKAnalyzer(); 
			MultiReader multiReader = null;
			try {
				multiReader = new MultiReader(searcher_topic.getIndexReader(),searcher_question.getIndexReader());
				indexSearcher = new IndexSearcher(multiReader);
				
				//要搜索的字段  
				//	String[] fieldName = {"name","sellprice","createdate"}; //"path"字段不查询
				// BooleanClause.Occur[]数组,它表示多个条件之间的关系,     
				// BooleanClause.Occur.MUST表示 and,     
				// BooleanClause.Occur.MUST_NOT表示not,     
				// BooleanClause.Occur.SHOULD表示or. 
				BooleanQuery.Builder query = new BooleanQuery.Builder();//组合查询
				
				if(keyword != null && !"".equals(keyword.trim())){
					BooleanClause.Occur[] clauses = { BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD }; 
					Query keyword_parser = MultiFieldQueryParser.parse(new String[] {QueryParser.escape(keyword), QueryParser.escape(keyword)}, new String[] {"title", "content"}, clauses,analyzer_keyword);
					query.add(keyword_parser,BooleanClause.Occur.MUST);
				}
			
				
				if(status != null){
		        	//是否可见 精确查询
			        Query status_query = IntPoint.newExactQuery("status", status);
			        query.add(status_query,BooleanClause.Occur.MUST);
		        }

		        //分页起始索引  
		        int startIndex = firstIndex <= 1? 0 : (firstIndex-1) * maxResult;
				//分页结束索引
				int endIndex = startIndex+maxResult;
				
				//排序   
				SortField[] sortFields = new SortField[2]; 
				//type对应的值分别为： 
				//SortField.Type.SCORE 按相关度(积分)排序 
				//SortField.Type.DOC 按文档排序 
				//SortField.Type.AUTO 域的值为int、long、float都有效 
				//SortField.Type.STRING 域按STRING排序 
				//SortField.Type.FLOAT 
				//SortField.Type.LONG 
				//SortField.Type.DOUBLE 
				//SortField.Type.SHORT 
				//SortField.Type.CUSTOM 通过比较器排序 
				//SortField.Type.BYTE
				sortFields[0] = new SortField("title",SortField.Type.SCORE);   
				if(sortCondition == 1){
					sortFields[1] = new SortField("postTime",SortField.Type.LONG,true);//false升序，true降序 //发布时间排序   新-->旧
				}else if(sortCondition == 2){
					sortFields[1] = new SortField("postTime",SortField.Type.LONG,false);//false升序，true降序//发布时间排序  旧-->新
				}else{
					//和1相同
					sortFields[1] = new SortField("postTime",SortField.Type.LONG,true);//false升序，true降序 //发布时间排序   新-->旧
				}
				Sort sort = new Sort(sortFields);  

				
				 //高亮设置    
				SimpleHTMLFormatter simpleHtmlFormatter = new SimpleHTMLFormatter("<B>","</B>");//设定高亮显示的格式，也就是对高亮显示的词组加上前缀后缀   
				
				Highlighter highlighter = new Highlighter(simpleHtmlFormatter,new QueryScorer(query.build()));   
				highlighter.setTextFragmenter(new SimpleFragmenter(190));//设置每次返回的字符数.想必大家在使用搜索引擎的时候也没有一并把全部数据展示出来吧，当然这里也是设定只展示部分数据   
				
				
				
			
				TopDocs topDocs = indexSearcher.search(query.build() ,endIndex,sort); 
				ScoreDoc[] scoreDocs = topDocs.scoreDocs;  
				
			    //查询所得记录 
			    for (int i = startIndex;i < endIndex && i<topDocs.totalHits; i++) {   
			    	
					Document targetDoc = indexSearcher.doc(scoreDocs[i].doc); //根据文档编号取出相应的文档   
					String _indexModule = targetDoc.get("indexModule");
					String _id = targetDoc.get("id");
					String _title = targetDoc.get("title");
					String _content = targetDoc.get("content");
					
					Long id = Long.parseLong(_id);
					String title = "";
					String content = "";

					if (_title != null && !"".equals(_title)) {   
						//执行转义
						_title = HtmlEscape.escape(_title);
		                TokenStream tokenStream = analyzer_keyword.tokenStream("title",new StringReader(_title));    
		                String highLightText = highlighter.getBestFragment(tokenStream, _title);//高亮显示  
		                if(highLightText != null && !"".equals(highLightText)){
		                	title = highLightText;
		                }else{
		                	title = _title;
		                }
		            }
					
					
					if (_content != null && !"".equals(_content)) { 
						if(isHide){//如果显示隐藏内容
							_content = textFilterManage.filterText(_content);
						}else{
							_content = textFilterManage.filterHideText(_content);
						}
						
		                TokenStream tokenStream = analyzer_keyword.tokenStream("content",new StringReader(_content));    
		                String highLightText = highlighter.getBestFragment(tokenStream, _content);
						//高亮显示  
		                if(highLightText != null && !"".equals(highLightText)){
		                	content = highLightText;
		                }else{
		                	if(_content.length() >190){
		                		content = _content.substring(0, 190);
		                	}else{
		                		content = _content;
		                	}
		                	
		                }
		            }
					
					if(_indexModule != null && "20".equals(_indexModule)){//问题模块
						SearchResult searchResult = new SearchResult();
						searchResult.setIndexModule(20);
						Question question = new Question();
						question.setId(id);
						question.setTitle(title);
						question.setContent(content);
						searchResult.setQuestion(question);
						searchResultList.add(searchResult);
						
					}else{//话题模块
						SearchResult searchResult = new SearchResult();
						searchResult.setIndexModule(10);
						Topic topic = new Topic();
						topic.setId(id);
						topic.setTitle(title);
						topic.setContent(content);
						searchResult.setTopic(topic);
						searchResultList.add(searchResult);
					}
					
			    }
			    //把查询结果设进去
				qr.setResultlist(searchResultList);
				qr.setTotalrecord(topDocs.totalHits);
			} catch (InvalidTokenOffsetsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();	
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}finally{ 
				//关闭资源   
				TopicLuceneInit.INSTANCE.closeSearcher(searcher_topic);
				TopicLuceneInit.INSTANCE.closeSearcher(searcher_question);
			}
			
			
		
		}
		
		return qr;
	}
	
	
	
	
	
	
	
	
	
}
