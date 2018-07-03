package cms.service.topic;

import java.util.List;
import java.util.Map;

import cms.bean.topic.Topic;
import cms.service.besa.DAO;

/**
 * 话题
 *
 */
public interface TopicService extends DAO<Topic>{
	/**
	 * 根据Id查询话题
	 * @param topicId 话题Id
	 * @return
	 */
	public Topic findById(Long topicId);
	/**
	 * 根据Id集合查询话题
	 * @param topicIdList 话题Id集合
	 * @return
	 */
	public List<Topic> findByIdList(List<Long> topicIdList);
	/**
	 * 根据Id集合查询话题标题
	 * @param topicIdList 话题Id集合
	 * @return
	 */
	public List<Topic> findTitleByIdList(List<Long> topicIdList);
	/**
	 * 根据话题Id集合查询话题
	 * @param topicIdList 话题Id集合
	 * @return
	 */
	public List<Topic> findTopicByTopicIdList(List<Long> topicIdList);
	/**
	 * 分页查询话题内容
	 * @param firstIndex
	 * @param maxResult
	 * @param userName 用户名称
	 * @param isStaff 是否为员工
	 * @return
	 */
	public Map<Long,String> findTopicContentByPage(int firstIndex, int maxResult,String userName,boolean isStaff);
	/**
	 * 分页查询话题
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 * @return
	 */
	public List<Topic> findTopicByPage(int firstIndex, int maxResult);
	/**
	 * 保存话题
	 * @param topic
	 */
	public void saveTopic(Topic topic);
	/**
	 * 修改话题
	 * @param topic
	 * @return
	 */
	public Integer updateTopic(Topic topic);
	/**
	 * 还原话题
	 * @param topicList 话题集合
	 * @return
	 */
	public Integer reductionTopic(List<Topic> topicList);
	/**
	 * 标记删除话题
	 * @param topicId 话题Id
	 * @return
	 */
	public Integer markDelete(Long topicId);
	/**
	 * 删除话题
	 * @param topicId 话题Id
	 * @return
	 */
	public Integer deleteTopic(Long topicId);
	/**
	 * 增加展示次数
	 * @param countMap key: 话题Id value:展示次数
	 * @return
	 */
	public int addViewCount(Map<Long,Long> countMap);
	/**
	 * 修改话题状态
	 * @param topicId 话题Id
	 * @param status 状态
	 * @return
	 */
	public int updateTopicStatus(Long topicId,Integer status);
	/**
	 * 增加总评论数
	 * @param topicId 话题Id
	 * @param quantity 数量
	 * @return
	 */
	public int addCommentTotal(Long topicId,Long quantity);
	/**
	 * 减少总评论数
	 * @param topicId 话题Id
	 * @param quantity 数量
	 * @return
	 */
	public int subtractCommentTotal(Long topicId,Long quantity);
}
