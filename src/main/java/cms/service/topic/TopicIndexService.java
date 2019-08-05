package cms.service.topic;

import java.util.List;

import cms.bean.topic.TopicIndex;
import cms.service.besa.DAO;


/**
 * 话题Lucene索引
 *
 */
public interface TopicIndexService extends DAO<TopicIndex>{
	/**
	 * 查询话题索引变化标记
	 */
	public List<TopicIndex> findTopicIndex(int firstIndex, int maxResult);
	
	/**
	 * 添加话题索引变化标记
	 * @param ProductInfoIndex 索引变化标记
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
