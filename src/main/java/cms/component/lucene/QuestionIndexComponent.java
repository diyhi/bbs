package cms.component.lucene;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cms.component.JsonComponent;
import cms.model.question.*;
import cms.repository.question.QuestionIndexRepository;
import cms.repository.question.QuestionRepository;
import cms.repository.question.QuestionTagRepository;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import tools.jackson.core.type.TypeReference;

/**
 * 问题全文索引定时索引组件
 *
 */
@Component("questionIndexComponent")
public class QuestionIndexComponent {
	private static final Logger logger = LogManager.getLogger(QuestionIndexComponent.class);
	
	@Resource QuestionRepository questionRepository;
	@Resource QuestionIndexRepository questionIndexRepository;
    @Resource QuestionIndexCacheManager questionIndexCacheManager;
	@Resource QuestionLuceneComponent questionLuceneComponent;
	@Resource QuestionTagRepository questionTagRepository;
    @Resource JsonComponent jsonComponent;

	/**
	 * 更新问题索引(由定时器触发)
	 */
	public void updateQuestionIndex(){
		int firstindex = 0;//起始页
		int maxresult = 100;// 每页显示记录数

		
		try {
			while(true){			
				//查询问题索引
				List<QuestionIndex> questionIndexList = questionIndexRepository.findQuestionIndex(firstindex, maxresult);
				
				
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
				questionIndexRepository.deleteQuestionIndex(indexIdList);
				
				
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
					questionLuceneComponent.deleteIndex(new ArrayList<Long>(questionIdIndexList));
				}
				
				//根据用户名称删除问题集合
				if(delete_userNameList != null && delete_userNameList.size() >0){
					//删除用户名称下的索引
					questionLuceneComponent.deleteUserNameIndex(new ArrayList<String>(delete_userNameList));
				}

				if(questionIdList !=null && questionIdList.size() >0){	
					//根据问题Id集合查询问题
					List<Question> questionList = questionRepository.findQuestionByQuestionIdList(new ArrayList<Long>(questionIdList));
					if(questionList != null && questionList.size() >0){
						
						List<QuestionTag> questionTagList = questionTagRepository.findAllQuestionTag();
						
						if(questionTagList != null && questionTagList.size() >0){
							for(Question question : questionList){
								//删除最后一个逗号
								String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

								List<AppendQuestionItem> appendQuestionItemList = jsonComponent.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
								question.setAppendQuestionItemList(appendQuestionItemList);
								
								
								
								List<QuestionTagAssociation> questionTagAssociationList = questionRepository.findQuestionTagAssociationByQuestionId(question.getId());
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
						questionLuceneComponent.addIndex(questionList);
						
					}
				}

				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("更新问题索引(由定时器触发)",e);
	        }
		}
	}
	/**
	 * 添加全部问题索引
	 */
	public void addAllQuestionIndex(){
		long count = 0;
		int page = 1;//分页 当前页
		int maxresult = 200;// 每页显示记录数

        questionIndexCacheManager.taskRunMark_delete();
        questionIndexCacheManager.taskRunMark_add(count);


        //删除所有问题索引变化标记
        questionIndexRepository.deleteAllIndex();

        try {
            questionLuceneComponent.deleteAllIndex();//删除所有索引


            while(true){
                count++;
                questionIndexCacheManager.taskRunMark_delete();
                questionIndexCacheManager.taskRunMark_add(count);

                //当前页
                int firstindex = (page-1)*maxresult;
                //查询问题
                List<Question> questionList = questionRepository.findQuestionByPage(firstindex, maxresult);

                if(questionList == null || questionList.size() == 0){
                    break;
                }


                List<QuestionTag> questionTagList = questionTagRepository.findAllQuestionTag();

                if(questionTagList != null && questionTagList.size() >0){
                    for(Question question : questionList){
                        //删除最后一个逗号
                        String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

                        List<AppendQuestionItem> appendQuestionItemList = jsonComponent.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
                        question.setAppendQuestionItemList(appendQuestionItemList);


                        List<QuestionTagAssociation> questionTagAssociationList = questionRepository.findQuestionTagAssociationByQuestionId(question.getId());
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
                questionLuceneComponent.addIndex(questionList);
                page++;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
        //	e.printStackTrace();
            if (logger.isErrorEnabled()) {
                logger.error("添加全部问题索引",e);
            }
        }

        questionIndexCacheManager.taskRunMark_delete();
	}

}
