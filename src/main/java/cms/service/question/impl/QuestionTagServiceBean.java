package cms.service.question.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.question.QuestionTag;
import cms.service.besa.DaoSupport;
import cms.service.question.QuestionService;
import cms.service.question.QuestionTagService;
import cms.web.action.SystemException;

/**
 * 问题标签实现
 *
 */
@Service
@Transactional
public class QuestionTagServiceBean extends DaoSupport<QuestionTag> implements QuestionTagService{
	@Resource QuestionService questionService; 
	
	/** 
	 * 根据Id查询标签
	 * @param questionTagId 标签Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QuestionTag findById(Long questionTagId){
		Query query = em.createQuery("select o from QuestionTag o where o.id=?1")
		.setParameter(1, questionTagId);
		List<QuestionTag> list = query.getResultList();
		for(QuestionTag p : list){
			return p;
		}
		return null;
	}
	/**
	 * 根据标签查询所有父类标签
	 * @param questionTag 标签
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<QuestionTag> findAllParentById(QuestionTag questionTag){
		List<QuestionTag> tagList = new ArrayList<QuestionTag>();
		//查询所有父类
		if(questionTag.getParentId() >0L){
			List<QuestionTag> list = this.findParentById(questionTag.getParentId(),new ArrayList<QuestionTag>());
			tagList.addAll(list);
		}
		//倒转顺序
		Collections.reverse(tagList);
		return tagList;
	}
	
	/**
	 * 根据ID查询标签父类 (递归)
	 * @param parentId 父标签ID
	 * @param questionTagList 父标签集合
	 * @return
	*/
	private List<QuestionTag> findParentById(Long parentId,List<QuestionTag> questionTagList){
		QuestionTag parentTag =this.findById(parentId);
		if(parentTag != null){
			questionTagList.add(parentTag);
			if(parentTag.getParentId() >0L){
				this.findParentById(parentTag.getParentId(),questionTagList);
			}
		}
		return questionTagList;
	}
	
	/**
	 * 根据标签Id查询子标签(下一节点)
	 * @param questionTagId 标签Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<QuestionTag> findChildTagById(Long questionTagId){
		Query query = em.createQuery("select o from QuestionTag o where o.parentId=?1")
		.setParameter(1,questionTagId);
		List<QuestionTag> list = query.getResultList();
		return list;
	}
	
	/**
	 * 查询所有问题标签
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<QuestionTag> findAllQuestionTag(){
		Query query = em.createQuery("select o from QuestionTag o order by o.sort desc");
		return query.getResultList();
	}
	
	/**
	 * 查询所有问题标签 - 缓存
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="findAllQuestionTag_cache",key="'findAllQuestionTag_default'")
	public List<QuestionTag> findAllQuestionTag_cache(){
		return this.findAllQuestionTag();
	}

	
	/**
	 * 保存标签
	 * @param questionTag
	 */
	@CacheEvict(value="findAllQuestionTag_cache",allEntries=true)
	public void saveQuestionTag(QuestionTag questionTag){
		this.save(questionTag);
		
		if(!questionTag.getParentId().equals(0L)){//如果不是根节点
			//修改父节点叶子节点状态
			Query query = em.createQuery("update QuestionTag o set o.childNodeNumber=o.childNodeNumber+1 where o.id=?1")
			.setParameter(1, questionTag.getParentId());
			int i = query.executeUpdate();
			if(i==0){
				throw new SystemException("父节点不存在");
			}
			
			//验证上级节点原来是根节点才执行
			Query query2 = em.createQuery("update QuestionTag o set o.childNodeNumber=?1 where o.id=?2 and o.childNodeNumber=?3")
			.setParameter(1, 1)
			.setParameter(2, questionTag.getParentId())
			.setParameter(3, 1);
			int j = query2.executeUpdate();
			if(j >0){
				//将父节点下的问题转到本节点
				questionService.updateTagId(questionTag.getParentId(), questionTag.getId());
			}
		}
		
	}
	

	/**
	 * 修改标签
	 * @param questionTag
	 * @return
	 */
	@CacheEvict(value="findAllQuestionTag_cache",allEntries=true)
	public Integer updateQuestionTag(QuestionTag questionTag){
		Query query = em.createQuery("update QuestionTag o set o.name=?1, o.sort=?2 where o.id=?3")
		.setParameter(1, questionTag.getName())
		.setParameter(2, questionTag.getSort())
		.setParameter(3, questionTag.getId());
		int i = query.executeUpdate();
		return i;
	}
	
	/**
	 * 删除标签
	 * @param questionTag 标签
	 */
	@CacheEvict(value="findAllQuestionTag_cache",allEntries=true)
	public Integer deleteQuestionTag(QuestionTag questionTag){
		int i = 0;
		Query delete = em.createQuery("delete from QuestionTag o where o.id=?1")
		.setParameter(1,questionTag.getId());
		i = delete.executeUpdate();
		if(i >0){
			//删除问题标签关联
			questionService.deleteQuestionTagAssociation(questionTag.getId());
			
			if(questionTag.getParentId() >0L){
				//将父节点计数减一
				Query query = em.createQuery("update QuestionTag o set o.childNodeNumber=childNodeNumber-1 where o.id=?1")
				.setParameter(1, questionTag.getParentId());
				query.executeUpdate();
			}
			
			
			this.deleteChildNode(Arrays.asList(questionTag.getId()));
		}
		
		return i;
	}
	
	
	/**
	 * 递归删除所有子节点
	 * @param questionTagIdList
	 */
	private void deleteChildNode(List<Long> questionTagIdList){
		List<Long> idList = new ArrayList<Long>();
		for(Long tagId : questionTagIdList){
			Query query = em.createQuery("select o from QuestionTag o where o.parentId=?1")
			.setParameter(1, tagId);
			List<QuestionTag> tagList = query.getResultList();
			if(tagList != null && tagList.size() >0){

				for(QuestionTag t : tagList){
					//删除当前节点
					Query delete = em.createQuery("delete from QuestionTag o where o.id=?1")
						.setParameter(1,t.getId());
					Integer s = delete.executeUpdate();
					if(s >0){
						//删除问题标签关联
						questionService.deleteQuestionTagAssociation(t.getId());
					}
					
					
					if(t.getChildNodeNumber() >0){
						idList.add(t.getId());
						
					}
					
				}	
			}
		}
		if(idList != null && idList.size() >0){
			this.deleteChildNode(idList);
		}
	}
}

