package cms.service.question;

import java.util.List;
import cms.bean.question.QuestionIndex;
import cms.service.besa.DAO;


/**
 * 问题Lucene索引
 *
 */
public interface QuestionIndexService extends DAO<QuestionIndex>{
	/**
	 * 查询问题索引变化标记
	 * @param firstIndex
	 * @param maxResult
	 * @return
	 */
	public List<QuestionIndex> findQuestionIndex(int firstIndex, int maxResult);
	
	/**
	 * 添加问题索引变化标记
	 * @param questionIndex 索引变化标记
	 */
	public void addQuestionIndex(QuestionIndex questionIndex);
	/**
	 * 删除问题索引变化标记
	 * @param indexIdList 索引变化标记Id集合
	 */
	public void deleteQuestionIndex(List<Long> indexIdList);
	/**
	 * 删除所有问题索引变化标记
	 */
	public Integer deleteAllIndex();
}
