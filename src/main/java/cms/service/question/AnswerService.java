package cms.service.question;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import cms.bean.platformShare.QuestionRewardPlatformShare;
import cms.bean.question.Answer;
import cms.bean.question.AnswerReply;
import cms.service.besa.DAO;

/**
 * 答案管理接口
 *
 */
public interface AnswerService  extends DAO<Answer>{
	/**
	 * 根据答案Id查询答案
	 * @param answerId 答案Id
	 * @return
	 */
	public Answer findByAnswerId(Long answerId);
	/**
	 * 根据答案Id集合查询答案
	 * @param answerIdList 答案Id集合
	 * @return
	 */
	public List<Answer> findByAnswerIdList(List<Long> answerIdList);
	/**
	 * 根据用户名称查询回答数量
	 * @param userName 用户名称
	 * @return
	 */
	public Long findAnswerCountByUserName(String userName);
	/**
	 * 根据答案Id查询答案在表的第几行
	 * @param answerId 答案Id
	 * @param questionId 问题Id
	 * @return
	 */
	public Long findRowByAnswerId(Long answerId,Long questionId);
	/**
	 * 根据答案Id查询答案在表的第几行
	 * @param answerId 答案Id
	 * @param questionId 问题Id
	 * @param status 状态
	 * @param sort 按发表时间排序 1.desc 2.asc 
	 * @return
	 */
	public Long findRowByAnswerId(Long answerId,Long questionId,Integer status,Integer sort);
	/**
	 * 分页查询答案内容
	 * @param firstIndex
	 * @param maxResult
	 * @param userName 用户名称
	 * @param isStaff 是否为员工
	 * @return
	 */
	public List<String> findAnswerContentByPage(int firstIndex, int maxResult,String userName,boolean isStaff);
	/**
	 * 分页查询答案
	 * @param userName 用户名称
	 * @param postTime 评论发表时间
	 * @param firstIndex
	 * @param maxResult
	 * @return
	 */
	public List<Answer> findAnswerByPage(String userName,Date postTime,int firstIndex, int maxResult);
	/**
	 * 保存答案
	 * @param answer
	 */
	public void saveAnswer(Answer answer);
	
	/**
	 * 修改答案
	 * @param answerId 答案Id
	 * @param content 内容
	 * @param status 状态
	 * @param userName 用户名称
	 * @return
	 */
	public Integer updateAnswer(Long answerId,String content,Integer status,String userName);
	/**
	 * 修改答案
	 * @param answerId 答案Id
	 * @param content 内容
	 * @param status 状态
	 * @param lastUpdateTime 最后修改时间
	 * @param userName 用户名称
	 * @return
	 */
	public Integer updateAnswer(Long answerId,String content,Integer status,Date lastUpdateTime,String userName);
	/**
	 * 采纳答案
	 * @param questionId 问题Id
	 * @param answerId 答案Id
	 * @param changeAdoption 是否更改采纳答案
	 * @param cancelAdoptionUserName 取消采纳用户名称
	 * @param cancelAdoptionPointLogObject 取消采纳用户退还悬赏积分日志
	 * @param cancelAdoptionUserNameShareAmount 取消采纳用户退还分成金额
	 * @param cancelAdoptionPaymentLogObject 取消采纳用户退还悬赏金额日志
	 * @param userName 回答的用户名称
	 * @param point 扣减用户积分
	 * @param pointLogObject 积分日志
	 * @param amount 扣减用户预存款
	 * @param paymentLogObject 支付日志
	 * @param questionRewardPlatformShare 平台分成
	 * @return
	 */
	public int updateAdoptionAnswer(Long questionId, Long answerId,boolean changeAdoption,String cancelAdoptionUserName,Object cancelAdoptionPointLogObject,BigDecimal cancelAdoptionUserNameShareAmount,Object cancelAdoptionPaymentLogObject,
			String userName,Long point,Object pointLogObject,BigDecimal amount,Object paymentLogObject,QuestionRewardPlatformShare questionRewardPlatformShare);
	/**
	 * 取消采纳答案
	 * @param questionId 问题Id
	 * @param cancelAdoptionUserName 取消采纳用户名称
	 * @param cancelAdoptionPointLogObject 取消采纳用户退还悬赏积分日志
	 * @param cancelAdoptionUserNameShareAmount 取消采纳用户退还分成金额
	 * @param cancelAdoptionPaymentLogObject 取消采纳用户退还悬赏金额日志
	 * @param point 扣减用户积分
	 * @return
	 */
	public int updateCancelAdoptionAnswer(Long questionId,String cancelAdoptionUserName,Object cancelAdoptionPointLogObject,BigDecimal cancelAdoptionUserNameShareAmount,Object cancelAdoptionPaymentLogObject,Long point);
	/**
	 * 修改答案状态
	 * @param answerId 答案Id
	 * @param status 状态
	 * @return
	 */
	public int updateAnswerStatus(Long answerId,Integer status);
	
	/**
	 * 删除答案
	 * @param questionId 问题Id
	 * @param answerId 答案Id
	 * @param cancelAdoptionUserName 取消采纳用户名称
	 * @param cancelAdoptionPointLogObject 取消采纳用户退还悬赏积分日志
	 * @param cancelAdoptionUserNameShareAmount 取消采纳用户退还分成金额
	 * @param cancelAdoptionPaymentLogObject 取消采纳用户退还悬赏金额日志
	 * @param point 扣减用户积分
	 * @return
	*/
	public Integer deleteAnswer(Long questionId,Long answerId,String cancelAdoptionUserName,Object cancelAdoptionPointLogObject,BigDecimal cancelAdoptionUserNameShareAmount,Object cancelAdoptionPaymentLogObject,Long point);
	/**
	 * 根据用户名称集合删除答案
	 * @param userNameList 用户名称集合
	 * @return
	 */
	public Integer deleteAnswer(List<String> userNameList);
	/**
	 * 标记删除答案
	 * @param answerIdId 答案Id
	 * @param constant 常数 例如 "110.待审核用户删除" 则加上100
	 * @return
	 */
	public Integer markDeleteAnswer(Long answerId,Integer constant);
	/**
	 * 查询待审核答案数量
	 * @return
	 */
	public Long auditAnswerCount();
	/**
	 * 添加回复
	 * @param answerReply
	*/
	public void saveReply(AnswerReply answerReply);
	/**
	 * 根据答案Id查询回复
	 * @param answerId 答案Id
	 * @return
	 */
	public List<AnswerReply> findReplyByAnswerId(Long answerId);
	/**
	 * 根据答案Id集合查询回复
	 * @param answerIdList 答案Id集合
	 * @param status 状态
	 * @return
	*/
	public List<AnswerReply> findReplyByAnswerId(List<Long> answerIdList,Integer status);
	/**
	 * 根据答案Id集合查询回复
	 * @param answerIdList 答案Id集合
	 * @return
	*/
	public List<AnswerReply> findReplyByAnswerId(List<Long> answerIdList);
	
	/**
	 * 根据答案回复Id查询答案回复
	 * @param answerReplyId 答案回复Id
	 * @return
	 */
	public AnswerReply findReplyByReplyId(Long answerReplyId);
	/**
	 * 根据答案回复Id集合查询答案回复
	 * @param answerReplyIdList 答案Id集合
	 * @return
	 */
	public List<AnswerReply> findByAnswerReplyIdList(List<Long> answerReplyIdList);
	/**
	 * 分页查询回复
	 * @param userName 用户名称
	 * @param postTime 回复发表时间
	 * @param firstIndex
	 * @param maxResult
	 * @return
	 */
	public List<AnswerReply> findReplyByPage(String userName,Date postTime,int firstIndex, int maxResult);
	/**
	 * 修改回复
	 * @param answerReplyId 回复Id
	 * @param content 回复内容
	 * @param userName 用户名称
	 * @param status 状态
	 * @return
	*/
	public Integer updateReply(Long answerReplyId,String content,String userName,Integer status);
	/**
	 * 修改回复
	 * @param replyId 回复Id
	 * @param content 回复内容
	 * @param userName 用户名称
	 * @param status 状态
	 * @param lastUpdateTime 最后修改时间
	 * @return
	*/
	public Integer updateReply(Long replyId,String content,String userName,Integer status,Date lastUpdateTime);
	/**
	 * 修改回复状态
	 * @param answerReplyId 回复Id
	 * @param status 状态
	 * @return
	 */
	public int updateReplyStatus(Long answerReplyId,Integer status);
	/**
	 * 标记删除回复
	 * @param replyId 回复Id
	 * @param constant 常数 例如 "110.待审核用户删除" 则加上100
	 * @return
	 */
	public Integer markDeleteReply(Long replyId,Integer constant);
	/**
	 * 删除回复
	 * @param answerReplyId 回复Id
	 * @return
	 */
	public Integer deleteReply(Long answerReplyId);
	/**
	 * 根据用户名称集合删除答案回复
	 * @param userNameList 用户名称集合
	 * @return
	 */
	public Integer deleteAnswerReply(List<String> userNameList);
	/**
	 * 查询待审核回复数量
	 * @return
	 */
	public Long auditReplyCount();
}
