package cms.repository.topic;

import cms.model.topic.TopicIndex;
import cms.repository.besa.DAO;

import java.util.List;



/**
 * 话题Lucene索引接口
 *
 */
public interface TopicIndexRepository extends DAO<TopicIndex> {
	/**
	 * 查询话题索引变化标记
	 */
	public List<TopicIndex> findTopicIndex(int firstIndex, int maxResult);
	
	/**
	 * 添加话题索引变化标记
	 * @param topicIndex 索引变化标记
	 */
	public void addTopicIndex(TopicIndex topicIndex);
	/**
	 * 删除话题索引变化标记
	 * @param indexIdList 索引变化标记Id集合
	 */
	public void deleteTopicIndex(List<Long> indexIdList);
	/**
	 * 删除所有话题索引变化标记
	 */
	public Integer deleteAllIndex();
}
