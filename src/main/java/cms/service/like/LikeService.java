package cms.service.like;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.QueryResult;
import cms.bean.like.AnswerLike;
import cms.bean.like.AnswerReplyLike;
import cms.bean.like.CommentLike;
import cms.bean.like.CommentReplyLike;
import cms.bean.like.Like;
import cms.bean.like.QuestionLike;
import cms.bean.like.TopicLike;
import cms.service.besa.DAO;
import net.sf.cglib.beans.BeanCopier;

/**
 * 点赞管理接口
 *
 */
public interface LikeService extends DAO<Like>{
	/**
	 * 根据Id查询点赞
	 * @param likeId 点赞Id
	 * @return
	 */
	public Like findById(String likeId);
	/**
	 * 根据用户名称查询点赞分页
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	public QueryResult<Like> findLikeByUserId(Long userId,String userName,int firstIndex, int maxResult);
	
	/**
	 * 保存项目点赞
	 * @param like 点赞
	 * @param itemLike 项目点赞 例如:topicLike话题点赞
	 */
	public void saveLike(Object like,Object itemLike);
	
	/**
	 * 删除点赞
	 * @param likeId 点赞Id
	 * @param itemLikeId 例如：话题点赞Id
	 * @param module 模块
	 */
	public Integer deleteLike(String likeId,String itemLikeId,Integer module);
	
	/**
	 * 根据话题Id删除点赞（暂时没用上）
	 * @param topicId 话题Id
	 */
	public Integer deleteLikeByTopicId(Long topicId);
	
	/**
	 * 根据点赞用户名称删除点赞
	 * @param userNameList 用户名称集合
	 */
	public Integer deleteLikeByUserName(List<String> userNameList);
	
	/**
	 * 根据发布话题的用户名称删除点赞（暂时没用上）
	 * @param userNameList 发布话题的用户名称集合
	 */
	public Integer deleteLikeByPostUserName(List<String> userNameList);
	
	/**
	 * 根据Id查询话题点赞
	 * @param topicLikeId 话题点赞Id
	 * @return
	 */
	public TopicLike findTopicLikeById(String topicLikeId);
	
	
	/**
	 * 根据话题Id查询点赞分页
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param topicId 话题Id
	 * @return
	 */
	public QueryResult<Like> findLikePageByTopicId(int firstIndex, int maxResult,Long topicId);
	
	
	/**
	 * 根据Id查询评论点赞
	 * @param commentLikeId 评论点赞Id
	 * @return
	 */
	public CommentLike findCommentLikeById(String commentLikeId);
	/**
	 * 根据话题Id查询被点赞数量
	 * @param topicId 话题Id
	 * @return
	 */
	public Long findLikeCountByTopicId(Long topicId);
	
	/**
	 * 根据评论Id查询被点赞数量
	 * @param commentId 评论Id
	 * @return
	 */
	public Long findLikeCountByCommentId(Long commentId);
	/**
	 * 删除评论点赞
	 * @param likeId 点赞Id
	 * @param commentLikeId 评论点赞Id
	 */
	public Integer deleteCommentLike(String likeId,String commentLikeId);
	
	/**
	 * 根据评论Id查询点赞分页
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param commentId 评论Id
	 * @return
	 */
	public QueryResult<Like> findLikePageByCommentId(int firstIndex, int maxResult,Long commentId);
	/**
	 * 根据Id查询评论回复点赞
	 * @param commentReplyLikeId 评论回复点赞Id
	 * @return
	 */
	public CommentReplyLike findCommentReplyLikeById(String commentReplyLikeId);
	/**
	 * 根据评论回复Id查询被点赞数量
	 * @param commentId 评论回复Id
	 * @return
	 */
	public Long findLikeCountByCommentReplyId(Long commentReplyId);
	/**
	 * 删除评论回复点赞
	 * @param likeId 点赞Id
	 * @param commentLikeId 评论点赞Id
	 */
	public Integer deleteCommentReplyLike(String likeId,String commentReplyLikeId);
	
	/**
	 * 根据评论回复Id查询点赞分页
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param replyId 回复Id
	 * @return
	 */
	public QueryResult<Like> findLikePageByCommentReplyId(int firstIndex, int maxResult,Long replyId);
	
	/**
	 * 根据Id查询问题点赞
	 * @param questionLikeId 问题点赞Id
	 * @return
	 */
	public QuestionLike findQuestionLikeById(String questionLikeId);
	
	
	/**
	 * 根据问题Id查询点赞分页
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param questionId 问题Id
	 * @return
	 */
	public QueryResult<Like> findLikePageByQuestionId(int firstIndex, int maxResult,Long questionId);
	
	
	/**
	 * 根据问题Id查询被点赞数量
	 * @param questionId 问题Id
	 * @return
	 */
	public Long findLikeCountByQuestionId(Long questionId);
	
		
	/**
	 * 根据Id查询答案点赞
	 * @param answerLikeId 答案点赞Id
	 * @return
	 */
	public AnswerLike findAnswerLikeById(String answerLikeId);
	
	/**
	 * 根据答案Id查询被点赞数量
	 * @param answerId 答案Id
	 * @return
	 */
	public Long findLikeCountByAnswerId(Long answerId);
	
	/**
	 * 删除答案点赞
	 * @param likeId 点赞Id
	 * @param answerLikeId 答案点赞Id
	 */
	public Integer deleteAnswerLike(String likeId,String answerLikeId);
	/**
	 * 根据答案Id查询点赞分页
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param answerId 答案Id
	 * @return
	 */
	public QueryResult<Like> findLikePageByAnswerId(int firstIndex, int maxResult,Long answerId);
	/**
	 * 根据Id查询答案回复点赞
	 * @param answerReplyLikeId 答案回复点赞Id
	 * @return
	 */
	public AnswerReplyLike findAnswerReplyLikeById(String answerReplyLikeId);
	
	/**
	 * 根据答案回复Id查询被点赞数量
	 * @param answerId 答案回复Id
	 * @return
	 */
	public Long findLikeCountByAnswerReplyId(Long answerReplyId);

	/**
	 * 删除答案回复点赞
	 * @param likeId 点赞Id
	 * @param answerLikeId 答案点赞Id
	 */
	public Integer deleteAnswerReplyLike(String likeId,String answerReplyLikeId);
	/**
	 * 根据答案回复Id查询点赞分页
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param replyId 回复Id
	 * @return
	 */
	public QueryResult<Like> findLikePageByAnswerReplyId(int firstIndex, int maxResult,Long replyId);
}
