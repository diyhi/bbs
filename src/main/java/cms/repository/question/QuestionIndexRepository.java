package cms.repository.question;

import cms.model.question.QuestionIndex;
import cms.repository.besa.DAO;

import java.util.List;


/**
 * 问题Lucene索引接口
 *
 */
public interface QuestionIndexRepository extends DAO<QuestionIndex> {
	/**
	 * 查询问题索引变化标记
     * @param firstIndex 索引开始
     * @param maxResult 获取数量
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
