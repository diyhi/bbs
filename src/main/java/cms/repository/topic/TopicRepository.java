package cms.repository.topic;

import cms.dto.QueryResult;
import cms.model.payment.PaymentLog;
import cms.model.platformShare.TopicUnhidePlatformShare;
import cms.model.redEnvelope.GiveRedEnvelope;
import cms.model.topic.Topic;
import cms.model.topic.TopicUnhide;
import cms.model.vote.VoteOption;
import cms.model.vote.VoteTheme;
import cms.repository.besa.DAO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;



/**
 * 话题管理接口
 *
 */
public interface TopicRepository extends DAO<Topic> {
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
     * @param firstIndex 索引开始
     * @param maxResult 获取数量
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
	 * 分页查询话题
	 * @param userName 用户名称
	 * @param postTime 话题发表时间
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 * @return
	 */
	public List<Topic> findTopicByPage(String userName, LocalDateTime postTime, int firstIndex, int maxResult);
	/**
	 * 查询热门话题
	 * @param maxResult 需要获取的记录数
	 * @return
	 */
	public List<Topic> findHotTopic(int maxResult);
	/**
	 * 查询热门话题 - 缓存
	 * @return
	 */
	public List<Topic> findHotTopic_cache(int maxResult);
	/**
	 * 保存话题
	 * @param topic 话题
	 * @param giveRedEnvelope 发红包
	 * @param userName 用户名称
	 * @param amount 扣减用户预存款
	 * @param paymentLog 支付日志
	 * @param voteTheme 投票主题
	 */
	public void saveTopic(Topic topic, GiveRedEnvelope giveRedEnvelope, String userName, BigDecimal amount, PaymentLog paymentLog, VoteTheme voteTheme);
	/**
	 * 修改话题
	 * @param topic 话题
	 * @param addVoteTheme 添加投票主题
	 * @param editVoteTheme 修改投票主题
	 * @param deleteVoteThemeId 删除投票主题Id
	 * @param addVoteOptionList 添加投票选项集合
	 * @param editVoteOptionList 修改投票选项集合
	 * @param deleteVoteOptionIdList 删除投票选项Id集合
	 * @return
	 */
	public Integer updateTopic(Topic topic, VoteTheme addVoteTheme, VoteTheme editVoteTheme, String deleteVoteThemeId, List<VoteOption> addVoteOptionList, List<VoteOption> editVoteOptionList, List<String> deleteVoteOptionIdList);
	
	/**
	 * 前台用户修改话题
	 * @param topic 话题
	 * @param addVoteTheme 添加投票主题
	 * @param editVoteTheme 修改投票主题
	 * @param deleteVoteThemeId 删除投票主题Id
	 * @param addVoteOptionList 添加投票选项集合
	 * @param editVoteOptionList 修改投票选项集合
	 * @param deleteVoteOptionIdList 删除投票选项Id集合
	 * @return
	 */
	public Integer updateTopic2(Topic topic,VoteTheme addVoteTheme,VoteTheme editVoteTheme,String deleteVoteThemeId,List<VoteOption> addVoteOptionList,List<VoteOption> editVoteOptionList,List<String> deleteVoteOptionIdList);
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
	 * 修改话题最后回复时间
	 * @param topicId 话题Id
	 * @param lastReplyTime 最后回复时间
	 * @return
	 */
	public Integer updateTopicReplyTime(Long topicId,LocalDateTime lastReplyTime);
	/**
	 * 删除话题
	 * @param topicId 话题Id
	 * @param giveRedEnvelope 发红包
	 * @param userName 用户名称
	 * @param amount 返还用户金额
	 * @param paymentLogObject 支付日志
	 * @return
	 */
	public Integer deleteTopic(Long topicId,GiveRedEnvelope giveRedEnvelope,String userName,BigDecimal amount,Object paymentLogObject);
	/**
	 * 查询待审核话题数量
	 * @return
	 */
	public Long auditTopicCount();
	/**
	 * 增加展示次数
	 * @param countMap key: 话题Id value:展示次数
	 * @return
	 */
	public int addViewCount(Map<Long,Long> countMap);
	/**
	 * 增加权重
	 * @param countMap key: 话题Id value:权重
	 * @return
	 */
	public int addWeightCount(Map<Long,Double> countMap);
	/**
	 * 修改话题状态
	 * @param topicId 话题Id
	 * @param status 状态
	 * @return
	 */
	public int updateTopicStatus(Long topicId,Integer status);
	/**
	 * 修改话题状态为待审核
	 * @param topicId 话题Id
	 * @return
	 */
	public int updateTopicPendingReview(Long topicId);
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
	
	/**
	 * 保存'话题取消隐藏'
	 * @param topicUnhide 话题取消隐藏
	 * @param hideTagType 隐藏标签类型
	 * @param point 积分
	 * @param consumption_userName 消费用户名称
	 * @param consumption_pointLogObject 消费积分日志
	 * @param income_userName 收入用户名称
	 * @param income_pointLogObject 收入积分日志
	 * @param consumption_amount 消费金额
     * @param userShare_amount 用户分成金额
	 * @param consumption_paymentLogObject 用户消费金额日志
	 * @param income_paymentLogObject 用户收入金额日志
	 * @param topicUnhidePlatformShare 平台分成
	 */
	public void saveTopicUnhide(Object topicUnhide, Integer hideTagType, Long point, String consumption_userName, Object consumption_pointLogObject, String income_userName, Object income_pointLogObject,
                                BigDecimal consumption_amount, BigDecimal userShare_amount, Object consumption_paymentLogObject, Object income_paymentLogObject, TopicUnhidePlatformShare topicUnhidePlatformShare);
	/**
	 * 根据Id查询'话题取消隐藏'
	 * @param topicUnhideId 话题取消隐藏Id
	 * @return
	 */
	public TopicUnhide findTopicUnhideById(String topicUnhideId);
	/**
	 * 根据话题Id查询话题取消隐藏用户列表
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param topicId 话题Id
	 * @return
	 */
	public QueryResult<TopicUnhide> findTopicUnhidePageByTopicId(int firstIndex, int maxResult, Long topicId);
}
