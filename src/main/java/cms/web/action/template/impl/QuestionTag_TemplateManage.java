package cms.web.action.template.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cms.bean.question.QuestionTag;
import cms.bean.template.Forum;
import cms.service.question.QuestionTagService;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.question.QuestionTagManage;

/**
 * 问题标签 -- 模板方法实现
 *
 */
@Component("questionTag_TemplateManage")
public class QuestionTag_TemplateManage {
	@Resource QuestionTagService questionTagService; 
	@Resource QuestionTagManage questionTagManage;
	@Resource FileManage fileManage;
	
	
	/**
	 * 标签列表 -- 集合
	 * @param forum
	 */
	public List<QuestionTag> questionTag_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		List<QuestionTag> questionTagList =  questionTagService.findAllQuestionTag_cache();
		List<QuestionTag> new_questionTagList = new ArrayList<QuestionTag>();//排序后标签
		
		if(questionTagList != null && questionTagList.size() >0){
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();  
			
			//组成排序数据
			Iterator<QuestionTag> questionTag_iter = questionTagList.iterator();   
			while(questionTag_iter.hasNext()){   
				QuestionTag questionTag = questionTag_iter.next();
				
				
				if(questionTag.getImage() != null && !"".equals(questionTag.getImage().trim())){
					questionTag.setImage(fileManage.fileServerAddress(request)+questionTag.getImage());
				}
				
				//如果是根节点
				if(questionTag.getParentId().equals(0L)){
					
					new_questionTagList.add(questionTag);
					questionTag_iter.remove();   
			    }  
			}
			//组合子标签
			for(QuestionTag questionTag :new_questionTagList){
				questionTagManage.childQuestionTag(questionTag,questionTagList);
			}
			//排序
			questionTagManage.questionTagSort(new_questionTagList);

		}
		return new_questionTagList;
	}
}
