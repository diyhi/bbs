package cms.web.action.lucene;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.question.AppendQuestionItem;
import cms.bean.question.Question;
import cms.bean.question.QuestionIndex;
import cms.bean.question.QuestionTag;
import cms.bean.question.QuestionTagAssociation;
import cms.service.question.QuestionIndexService;
import cms.service.question.QuestionService;
import cms.service.question.QuestionTagService;
import cms.utils.JsonUtils;

/**
 * 问题全文索引定时索引管理
 *
 */
@Component("questionIndexManage")
public class QuestionIndexManage {
	private static final Logger logger = LogManager.getLogger(QuestionIndexManage.class);
	
	@Resource QuestionService questionService;
	@Resource QuestionIndexService questionIndexService;
	@Resource QuestionLuceneManage questionLuceneManage;
	@Resource CacheManager cacheManager;
	
	@Resource QuestionIndexManage questionIndexManage;
	@Resource QuestionTagService questionTagService;
	
	/**
	 * 更新问题索引(由定时器触发)
	 */
	public void updateQuestionIndex(){
		int firstindex = 0;//起始页
		int maxresult = 100;// 每页显示记录数
		
		if(!QuestionLuceneInit.INSTANCE.allowCreateIndexWriter()){
			return;
		}
		
		
		try {
			QuestionLuceneInit.INSTANCE.createIndexWriter();//创建IndexWriter
			
			while(true){			
				//查询问题索引
				List<QuestionIndex> questionIndexList = questionIndexService.findQuestionIndex(firstindex, maxresult);
				
				
				if(questionIndexList == null || questionIndexList.size() == 0){
					break;
				}
				//添加问题Id集合
				Set<Long> add_questionIdList = new LinkedHashSet<Long>();
				//修改问题Id
				Set<Long> update_questionIdList = new LinkedHashSet<Long>();
				//删除问题Id
				Set<Long> delete_questionIdList = new LinkedHashSet<Long>();
				//删除用户名称
				Set<String> delete_userNameList = new LinkedHashSet<String>();
				
				List<Long> indexIdList = new ArrayList<Long>();
				for(QuestionIndex p : questionIndexList){
					indexIdList.add(p.getId());
					if(p.getIndexState().equals(1)){//1:添加
						add_questionIdList.add(Long.parseLong(p.getDataId()));
						
					}else if(p.getIndexState().equals(2)){//2:修改
						update_questionIdList.add(Long.parseLong(p.getDataId()));
					}else if(p.getIndexState().equals(3)){//3:删除 
						Long id = Long.parseLong(p.getDataId());
						delete_questionIdList.add(id);
						
						//删除添加和修改Id
						add_questionIdList.remove(id);
						update_questionIdList.remove(id);
					}else if(p.getIndexState().equals(4)){//4:删除用户发表的问题
						delete_userNameList.add(p.getDataId());
					}
				}

				//删除问题索引变化标记
				questionIndexService.deleteQuestionIndex(indexIdList);
				
				
				//查询问题Id集合
				Set<Long> questionIdList = new LinkedHashSet<Long>();
				questionIdList.addAll(add_questionIdList);
				questionIdList.addAll(update_questionIdList);
				
				//删除问题Id索引集合
				Set<Long> questionIdIndexList = new LinkedHashSet<Long>();
				questionIdIndexList.addAll(update_questionIdList);
				questionIdIndexList.addAll(delete_questionIdList);
				
				
				if(questionIdIndexList != null && questionIdIndexList.size() >0){
					//删除当前Id索引
					questionLuceneManage.deleteIndex(new ArrayList<Long>(questionIdIndexList));
				}
				
				//根据用户名称删除问题集合
				if(delete_userNameList != null && delete_userNameList.size() >0){
					//删除用户名称下的索引
					questionLuceneManage.deleteUserNameIndex(new ArrayList<String>(delete_userNameList));
				}

				if(questionIdList !=null && questionIdList.size() >0){	
					//根据问题Id集合查询问题
					List<Question> questionList = questionService.findQuestionByQuestionIdList(new ArrayList<Long>(questionIdList));
					if(questionList != null && questionList.size() >0){
						
						List<QuestionTag> questionTagList = questionTagService.findAllQuestionTag();
						
						if(questionTagList != null && questionTagList.size() >0){
							for(Question question : questionList){
								//删除最后一个逗号
								String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

								List<AppendQuestionItem> appendQuestionItemList = JsonUtils.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
								question.setAppendQuestionItemList(appendQuestionItemList);
								
								
								
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
						//写入索引
						questionLuceneManage.addIndex(questionList);
						
					}
				}

				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("更新问题索引(由定时器触发)",e);
	        }
		}finally {
			QuestionLuceneInit.INSTANCE.closeIndexWriter();//关闭IndexWriter
		}	
	}
	/**
	 * 添加全部问题索引
	 */
	public void addAllQuestionIndex(){
		long count = 0;
		int page = 1;//分页 当前页
		int maxresult = 200;// 每页显示记录数
		
		questionIndexManage.taskRunMark_delete();
		questionIndexManage.taskRunMark_add(count);
		

		boolean allow = QuestionLuceneInit.INSTANCE.allowCreateIndexWriter();//是否允许创建IndexWriter
		if(allow){
			//删除所有问题索引变化标记
			questionIndexService.deleteAllIndex();
			
			try {
				QuestionLuceneInit.INSTANCE.createIndexWriter();//创建IndexWriter
				
				questionLuceneManage.deleteAllIndex();//删除所有索引
				
				
				while(true){
					count++;
					questionIndexManage.taskRunMark_delete();
					questionIndexManage.taskRunMark_add(count);
					
					//当前页
					int firstindex = (page-1)*maxresult;
					//查询问题
					List<Question> questionList = questionService.findQuestionByPage(firstindex, maxresult);
					
					if(questionList == null || questionList.size() == 0){
						break;
					}
					

					List<QuestionTag> questionTagList = questionTagService.findAllQuestionTag();
					
					if(questionTagList != null && questionTagList.size() >0){
						for(Question question : questionList){
							//删除最后一个逗号
							String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

							List<AppendQuestionItem> appendQuestionItemList = JsonUtils.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
							question.setAppendQuestionItemList(appendQuestionItemList);
							
							
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
					
					
					//写入索引
					questionLuceneManage.addIndex(questionList);
					page++;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("添加全部问题索引",e);
		        }
			}finally {
				QuestionLuceneInit.INSTANCE.closeIndexWriter();//关闭IndexWriter
			}			
		}
		
		questionIndexManage.taskRunMark_delete();
	}


	/**
	 * 查询/添加任务运行标记
	 * @param count 次数  -1为查询方式
	 * @return
	 */
	@Cacheable(value="questionIndexManage_cache_taskRunMark",key="'taskRunMark'")
	public Long taskRunMark_add(Long count){
		return count;
	}
	/**
	 * 删除任务运行标记
	 * @return
	 */
	@CacheEvict(value="questionIndexManage_cache_taskRunMark",key="'taskRunMark'")
	public void taskRunMark_delete(){
	}
	
	
	/**
	 * 定时刷新本地索引(20秒)
	 */
	@Scheduled(fixedDelay=20000)
	public void refreshIndex(){
		QuestionLuceneInit.INSTANCE.refreshSearcher();
	}
	
	
}
