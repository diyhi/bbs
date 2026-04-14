package cms.repository.question;

import cms.model.payment.PaymentLog;
import cms.model.question.Question;
import cms.model.question.QuestionTagAssociation;
import cms.model.user.PointLog;
import cms.model.vote.VoteOption;
import cms.model.vote.VoteTheme;
import cms.repository.besa.DAO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 问题管理接口
 *
 */
public interface QuestionRepository extends DAO<Question> {
	/**
	 * 根据Id查询问题
	 * @param questionId 问题Id
	 * @return
	 */
	public Question findById(Long questionId);
	/**
	 * 根据Id集合查询问题
	 * @param questionIdList 问题Id集合
	 * @return
	 */
	public List<Question> findByIdList(List<Long> questionIdList);
	/**
	 * 根据Id集合查询问题标题
	 * @param questionIdList 问题Id集合
	 * @return
	 */
	public List<Question> findTitleByIdList(List<Long> questionIdList);
	/**
	 * 根据问题Id集合查询问题
	 * @param questionIdList 话题Id集合
	 * @return
	 */
	public List<Question> findQuestionByQuestionIdList(List<Long> questionIdList);
	/**
	 * 分页查询问题内容
     * @param firstIndex 索引开始
     * @param maxResult 获取数量
	 * @param userName 用户名称
	 * @param isStaff 是否为员工
	 * @return
	 */
	public List<Question> findQuestionContentByPage(int firstIndex, int maxResult,String userName,boolean isStaff);
	/**
	 * 分页查询问题
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 * @return
	 */
	public List<Question> findQuestionByPage(int firstIndex, int maxResult);
	/**
	 * 分页查询问题
	 * @param userName 用户名称
	 * @param postTime 问题发表时间
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 * @return
	 */
	public List<Question> findQuestionByPage(String userName, LocalDateTime postTime, int firstIndex, int maxResult);
	/**
	 * 根据问题Id查询问题标签关联
	 * @param questionId 问题Id
	 * @return
	 */
	public List<QuestionTagAssociation> findQuestionTagAssociationByQuestionId(Long questionId);
	/**
	 * 保存问题
	 * @param question 问题
	 * @param questionTagAssociationList 问题标签关联集合
	 * @param point 扣减用户积分
	 * @param pointLog 积分日志
	 * @param amount 扣减用户预存款
	 * @param paymentLog 支付日志
	 * @param voteTheme 投票主题
	 */
	public void saveQuestion(Question question, List<QuestionTagAssociation> questionTagAssociationList, Long point, PointLog pointLog, BigDecimal amount, PaymentLog paymentLog, VoteTheme voteTheme);
	/**
	 * 追加问题
	 * @param questionId 问题Id
	 * @param appendContent 追加问题内容 AppendQuestionItem对象的JSON格式加上逗号
	 * @return
	 */
	public Integer saveAppendQuestion(Long questionId,String appendContent);
	/**
	 * 修改追加问题
	 * @param questionId 问题Id
	 * @param appendContent 追加问题内容
	 * @return
	 */
	public Integer updateAppendQuestion(Long questionId,String appendContent);
	/**
	 * 增加展示次数
	 * @param countMap key: 话题Id value:展示次数
	 * @return
	 */
	public int addViewCount(Map<Long,Long> countMap);
	/**
	 * 修改采纳答案Id
	 * @param questionId 问题Id
	 * @param answerId 答案Id
	 * @return
	 */
	public int updateAdoptionAnswerId(Long questionId, Long answerId);
	/**
	 * 修改问题
	 * @param question 问题
	 * @param questionTagAssociationList 问题标签关联集合
	 * @param changePointSymbol 变更积分符号 true：问题增加积分  false：问题减少积分
	 * @param changePoint 变更积分
	 * @param changeAmountSymbol 变更金额符号 true：问题增加金额  false：问题减少金额
	 * @param changeAmount 变更金额
	 * @param pointLogObject 用户悬赏积分日志
	 * @param paymentLogObject 用户悬赏金额日志
	 * @param addVoteTheme 添加投票主题
	 * @param editVoteTheme 修改投票主题
	 * @param deleteVoteThemeId 删除投票主题Id
	 * @param addVoteOptionList 添加投票选项集合
	 * @param editVoteOptionList 修改投票选项集合
	 * @param deleteVoteOptionIdList 删除投票选项Id集合
	 * @return
	 */
	public Integer updateQuestion(Question question, List<QuestionTagAssociation> questionTagAssociationList,
                                  boolean changePointSymbol, Long changePoint, boolean changeAmountSymbol, BigDecimal changeAmount, Object pointLogObject, Object paymentLogObject,
                                  VoteTheme addVoteTheme, VoteTheme editVoteTheme, String deleteVoteThemeId, List<VoteOption> addVoteOptionList, List<VoteOption> editVoteOptionList, List<String> deleteVoteOptionIdList);
	/**
	 * 修改问题最后回答时间
	 * @param questionId 问题Id
	 * @param lastAnswerTime 最后回答时间
	 * @return
	 */
	public Integer updateQuestionAnswerTime(Long questionId,LocalDateTime lastAnswerTime);
	/**
	 * 修改问题状态
	 * @param questionId 问题Id
	 * @param status 状态
	 * @return
	 */
	public int updateQuestionStatus(Long questionId,Integer status);
	/**
	 * 修改问题状态为待审核
	 * @param questionId 问题Id
	 * @return
	 */
	public int updateQuestionPendingReview(Long questionId);
	/**
	 * 修改问题标签Id
	 * @param old_tagId 旧标签Id
	 * @param new_tagId 新标签Id
	 * @return
	 */
	public Integer updateTagId(Long old_tagId,Long new_tagId);
	/**
	 * 根据标签Id删除问题标签关联
	 * @param questionTagId 问题标签Id
	 * @return
	 */
	public Integer deleteQuestionTagAssociation(Long questionTagId);
	/**
	 * 根据用户名称集合删除问题标签关联
	 * @param userNameList 用户名称集合
	 * @return
	 */
	public Integer deleteQuestionTagAssociationByUserId(List<String> userNameList);
	/**
	 * 还原问题
	 * @param questionList 问题集合
	 * @return
	 */
	public Integer reductionQuestion(List<Question> questionList);
	
	/**
	 * 标记删除问题
	 * @param questionId 问题Id
	 * @return
	 */
	public Integer markDelete(Long questionId);
	/**
	 * 删除问题
	 * @param questionId 问题Id
	 * @param userName 用户名称
	 * @param point 扣减用户积分
	 * @param pointLogObject 积分日志
	 * @param amount 扣减用户预存款
	 * @param paymentLogObject 支付日志
	 * @return
	 */
	public Integer deleteQuestion(Long questionId,String userName,Long point,Object pointLogObject,BigDecimal amount,Object paymentLogObject);
	/**
	 * 根据用户名称集合删除问题
	 * @param userNameList 用户名称集合
	 * @return
	 */
	public Integer deleteQuestion(List<String> userNameList);
	/**
	 * 增加总答案数
	 * @param questionId 问题Id
	 * @param quantity 数量
	 * @return
	 */
	public int addAnswerTotal(Long questionId,Long quantity);
	/**
	 * 减少总答案数
	 * @param questionId 问题Id
	 * @param quantity 数量
	 * @return
	 */
	public int subtractAnswerTotal(Long questionId,Long quantity);
	/**
	 * 查询待审核问题数量
	 * @return
	 */
	public Long auditQuestionCount();
}
