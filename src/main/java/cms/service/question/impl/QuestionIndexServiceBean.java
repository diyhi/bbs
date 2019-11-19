package cms.service.question.impl;


import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.question.QuestionIndex;
import cms.service.besa.DaoSupport;
import cms.service.question.QuestionIndexService;

/**
 * 问题Lucene索引
 *
 */
@Service
@Transactional
public class QuestionIndexServiceBean extends DaoSupport<QuestionIndex> implements QuestionIndexService {
	
	/**
	 * 查询问题索引变化标记
	 * @param firstIndex
	 * @param maxResult
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<QuestionIndex> findQuestionIndex(int firstIndex, int maxResult){
		
		Query query = em.createQuery("select o from QuestionIndex o ORDER BY o.id asc");
		//索引开始,即从哪条记录开始
		query.setFirstResult(firstIndex);
		//获取多少条数据
		query.setMaxResults(maxResult);
		return query.getResultList();
	
	}
	
	/**
	 * 添加问题索引变化标记
	 * @param questionIndex 索引变化标记
	 */
	public void addQuestionIndex(QuestionIndex questionIndex){
		this.save(questionIndex);
	}
	/**
	 * 删除问题索引变化标记
	 * @param indexIdList 索引变化标记Id集合
	 */
	public void deleteQuestionIndex(List<Long> indexIdList){
		Query query = em.createQuery("delete from QuestionIndex o where o.id in(:id)")
		.setParameter("id",indexIdList);
		query.executeUpdate();
	}
	/**
	 * 删除所有问题索引变化标记
	 */
	public Integer deleteAllIndex(){
		Query query = em.createNativeQuery("truncate table questionindex");
		
		return query.executeUpdate();
		
	}
}
