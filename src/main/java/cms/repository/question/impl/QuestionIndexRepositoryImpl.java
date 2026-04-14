package cms.repository.question.impl;


import java.util.List;


import cms.model.question.QuestionIndex;
import cms.repository.besa.DaoSupport;
import cms.repository.question.QuestionIndexRepository;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



/**
 * 问题Lucene索引接口实现类
 *
 */
@Repository
@Transactional
public class QuestionIndexRepositoryImpl extends DaoSupport<QuestionIndex> implements QuestionIndexRepository {
	
	/**
	 * 查询问题索引变化标记
     * @param firstIndex 索引开始
     * @param maxResult 获取数量
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
