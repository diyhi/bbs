package cms.service.topic;

import java.util.Date;
import java.util.List;


import cms.bean.topic.Comment;
import cms.bean.topic.Reply;
import cms.service.besa.DAO;

/**
 * 评论
 *
 */
public interface CommentService  extends DAO<Comment>{
	/**
	 * 根据评论Id查询评论
	 * @param commentId 评论Id
	 * @return
	 */
	public Comment findByCommentId(Long commentId);
	/**
	 * 根据评论Id集合查询评论
	 * @param commentIdList 评论Id集合
	 * @return
	 */
	public List<Comment> findByCommentIdList(List<Long> commentIdList);
	/**
	 * 根据评论Id查询评论在表的第几行
	 * @param commentId 评论Id
	 * @param topicId 话题Id
	 * @return
	 */
	public Long findRowByCommentId(Long commentId,Long topicId);
	/**
	 * 根据评论Id查询评论在表的第几行
	 * @param commentId 评论Id
	 * @param topicId 话题Id
	 * @param status 状态
	 * @param sort 按发表时间排序  1.desc 2.asc 
	 * @return
	 */
	public Long findRowByCommentId(Long commentId,Long topicId,Integer status,Integer sort);
	/**
	 * 分页查询评论内容
	 * @param firstIndex
	 * @param maxResult
	 * @param userName 用户名称
	 * @param isStaff 是否为员工
	 * @return
	 */
	public List<String> findCommentContentByPage(int firstIndex, int maxResult,String userName,boolean isStaff);
	/**
	 * 分页查询评论
	 * @param userName 用户名称
	 * @param postTime 评论发表时间
	 * @param firstIndex
	 * @param maxResult
	 * @return
	 */
	public List<Comment> findCommentByPage(String userName,Date postTime,int firstIndex, int maxResult);
	/**
	 * 查询用户是否评论话题
	 * @param topicId 话题Id
	 * @param userName 用户名称
	 * @return
	 */
	public Boolean findWhetherCommentTopic(Long topicId,String userName);
	/**
	 * 保存评论
	 * @param comment
	 */
	public void saveComment(Comment comment);
	/**
	 * 修改评论
	 * @param commentId 评论Id
	 * @param content 内容
	 * @param status 状态
	 * @param lastUpdateTime 最后修改时间
	 * @param lowerQuoteIdGroup 下级引用Id组
	 * @param userName 用户名称
	 * @return
	 */
	public Integer updateComment(Long commentId,String content,Integer status,Date lastUpdateTime,String lowerQuoteIdGroup,String userName);
	/**
	 * 修改评论
	 * @param commentId 评论Id
	 * @param content 内容
	 * @param status 状态
	 * @param lastUpdateTime 最后修改时间
	 * @param userName 用户名称
	 * @return
	 */
	public Integer updateComment(Long commentId,String content,Integer status,Date lastUpdateTime,String userName);
	/**
	 * 修改评论状态
	 * @param commentId 评论Id
	 * @param status 状态
	 * @return
	 */
	public int updateCommentStatus(Long commentId,Integer status);
	/**
	 * 删除评论
	 * @param topicId 话题Id
	 * @param commentId 评论Id
	 * @return
	*/
	public Integer deleteComment(Long topicId,Long commentId);
	/**
	 * 标记删除评论
	 * @param commentId 评论Id
	 * @param constant 常数 例如 "110.待审核用户删除" 则加上100
	 * @return
	 */
	public Integer markDeleteComment(Long commentId,Integer constant);
	/**
	 * 查询待审核评论数量
	 * @return
	 */
	public Long auditCommentCount();
	/**
	 * 添加回复
	 * @param reply
	*/
	public void saveReply(Reply reply); 
	/**
	 * 根据评论Id查询回复
	 * @param commentId 评论Id
	 * @return
	 */
	public List<Reply> findReplyByCommentId(Long commentId);
	/**
	 * 根据评论Id集合查询回复
	 * @param commentIdList 评论Id集合
	 * @return
	*/
	public List<Reply> findReplyByCommentId(List<Long> commentIdList);
	/**
	 * 根据评论Id集合查询回复
	 * @param commentIdList 评论Id集合
	 * @param status 状态
	 * @return
	*/
	public List<Reply> findReplyByCommentId(List<Long> commentIdList,Integer status);
	/**
	 * 分页查询回复
	 * @param userName 用户名称
	 * @param postTime 回复发表时间
	 * @param firstIndex
	 * @param maxResult
	 * @return
	 */
	public List<Reply> findReplyByPage(String userName,Date postTime,int firstIndex, int maxResult);
	/**
	 * 根据回复Id查询评论回复
	 * @param replyId 回复Id
	 * @return
	 */
	public Reply findReplyByReplyId(Long replyId);
	/**
	 * 根据回复Id集合查询回复
	 * @param replyIdList 回复Id集合
	 * @return
	 */
	public List<Reply> findByReplyIdList(List<Long> replyIdList);
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
	 * @param replyId 回复Id
	 * @param status 状态
	 * @return
	 */
	public int updateReplyStatus(Long replyId,Integer status);
	/**
	 * 删除回复
	 * @param replyId 回复Id
	 * @return
	 */
	public Integer deleteReply(Long replyId);
	/**
	 * 标记删除回复
	 * @param replyId 回复Id
	 * @param constant 常数 例如 "110.待审核用户删除" 则加上100
	 * @return
	 */
	public Integer markDeleteReply(Long replyId,Integer constant);
	/**
	 * 查询待审核回复数量
	 * @return
	 */
	public Long auditReplyCount();
}
