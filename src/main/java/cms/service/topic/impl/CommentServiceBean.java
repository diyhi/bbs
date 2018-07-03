package cms.service.topic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.topic.Comment;
import cms.bean.topic.Reply;
import cms.service.besa.DaoSupport;
import cms.service.topic.CommentService;
import cms.service.topic.TopicService;


/**
 * 评论
 *
 */
@Service
@Transactional
public class CommentServiceBean extends DaoSupport<Comment> implements CommentService {

	@Resource TopicService topicService;

	
	/**--------------------------------------- 评论 ---------------------------------------**/
	
	/**
	 * 根据评论Id查询评论
	 * @param commentId 评论Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Comment findByCommentId(Long commentId){
		Query query = em.createQuery("select o from Comment o where o.id =?1");
		//给SQL语句设置参数
		query.setParameter(1, commentId);
		
		List<Comment> commentList = query.getResultList();
		if(commentList != null && commentList.size() >0){
			for(Comment comment : commentList){
				return comment;
			}
		}
		return null;
	}
	/**
	 * 根据评论Id集合查询评论
	 * @param commentIdList 评论Id集合
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Comment> findByCommentIdList(List<Long> commentIdList){
		Query query = em.createQuery("select o from Comment o where o.id in(:commentIdList)");
		//给SQL语句设置参数
		query.setParameter("commentIdList", commentIdList);
		
		return query.getResultList();
	}
	
	/**
	 * 根据评论Id查询评论在表的第几行
	 * @param commentId 评论Id
	 * @param topicId 话题Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Long findRowByCommentId(Long commentId,Long topicId){
		Query query = em.createQuery("select count(o.id) from Comment o where o.id <=?1 and o.topicId= ?2 order by o.postTime asc");
		//给SQL语句设置参数
		query.setParameter(1, commentId);
		query.setParameter(2, topicId);
		return (Long)query.getSingleResult();		
	}
	/**
	 * 根据评论Id查询评论在表的第几行
	 * @param commentId 评论Id
	 * @param topicId 话题Id
	 * @param status 状态
	 * @param sort 按发表时间排序 1.desc 2.asc 
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Long findRowByCommentId(Long commentId,Long topicId,Integer status,Integer sort){
		String commentId_sql = "";
		String sort_sql = "";
		if(sort.equals(1)){
			commentId_sql = " o.id >=?1";
			sort_sql = " desc";
		}else{
			commentId_sql = " o.id <=?1";
			sort_sql = " asc";
		}
		Query query = em.createQuery("select count(o.id) from Comment o where "+commentId_sql+" and o.topicId= ?2 and o.status= ?3 order by o.postTime"+sort_sql);
		//给SQL语句设置参数
		query.setParameter(1, commentId);
		query.setParameter(2, topicId);
		query.setParameter(3, status);
		return (Long)query.getSingleResult();		
	}
	
	
	/**
	 * 分页查询评论内容
	 * @param firstIndex
	 * @param maxResult
	 * @param userName 用户名称
	 * @param isStaff 是否为员工
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<String> findCommentContentByPage(int firstIndex, int maxResult,String userName,boolean isStaff){
		List<String> topicContentList = new ArrayList<String>();//key:话题Id  value:话题内容
		
		String sql = "select o.content from Comment o where o.userName=?1 and o.isStaff=?2";
		Query query = em.createQuery(sql);	
		query.setParameter(1, userName);
		query.setParameter(2, isStaff);
		//索引开始,即从哪条记录开始
		query.setFirstResult(firstIndex);
		//获取多少条数据
		query.setMaxResults(maxResult);
		
		List<Object> objectList = query.getResultList();
		if(objectList != null && objectList.size() >0){
			for(int i = 0; i<objectList.size(); i++){
				String content = (String)objectList.get(i);
				topicContentList.add(content);
			}
		}
		
		return topicContentList;
	}
	
	
	/**
	 * 保存评论
	 * @param comment
	 */
	public void saveComment(Comment comment){
		this.save(comment);
		topicService.addCommentTotal(comment.getTopicId(), 1L);
	}
	
	/**
	 * 修改评论
	 * @param commentId 评论Id
	 * @param content 内容
	 * @param status 状态
	 * @param lowerQuoteIdGroup 下级引用Id组
	 * @param userName 用户名称
	 * @return
	 */
	public Integer updateComment(Long commentId,String content,Integer status,String lowerQuoteIdGroup,String userName){
		Query query = em.createQuery("update Comment o set o.content=?1,o.userName=?2,o.status=?3 where o.id=?4")
		.setParameter(1, content)
		.setParameter(2, userName)
		.setParameter(3, status)
		.setParameter(4, commentId);
		int i = query.executeUpdate();
		if(i >0){
			Query query2 = em.createQuery("update Comment o set o.quoteUpdateId=CONCAT(o.quoteUpdateId,?1) where o.quoteIdGroup like ?2")
			.setParameter(1, commentId+",")
			
			.setParameter(2, lowerQuoteIdGroup+ '%');
			query2.executeUpdate();
		}
		
		return i;
	}
	
	/**
	 * 修改评论状态
	 * @param commentId 评论Id
	 * @param status 状态
	 * @return
	 */
	public int updateCommentStatus(Long commentId,Integer status){
		Query query = em.createQuery("update Comment o set o.status=?1 where o.id=?2")
				.setParameter(1, status)
				.setParameter(2, commentId);
		return query.executeUpdate();
	}
	
	/**
	 * 删除评论
	 * @param topicId 话题Id
	 * @param commentId 评论Id
	 * @return
	*/
	public Integer deleteComment(Long topicId,Long commentId){
		//删除自定义评论
		Query delete = em.createQuery("delete from Comment o where o.id=?1");
		delete.setParameter(1, commentId);
		int i = delete.executeUpdate();
		
		//删除自定义评论回复
		Query delete_customReply = em.createQuery("delete from Reply o where o.commentId=?1");
		delete_customReply.setParameter(1, commentId);
		delete_customReply.executeUpdate();
		
		topicService.subtractCommentTotal(topicId,1L);
		return i;
	} 
	/**--------------------------------------回复------------------------------------**/
	
	/**
	 * 添加回复
	 * @param reply
	*/
	public void saveReply(Reply reply){
		this.save(reply);
	} 
	/**
	 * 根据评论Id查询回复
	 * @param commentId 评论Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Reply> findReplyByCommentId(Long commentId){
		Query query = em.createQuery("select o from Reply o where o.commentId=?1");
		//给SQL语句设置参数
		query.setParameter(1, commentId);
		
		return query.getResultList();
	}
	/**
	 * 根据评论Id集合查询回复
	 * @param commentIdList 评论Id集合
	 * @return
	*/
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Reply> findReplyByCommentId(List<Long> commentIdList){
		Query query = em.createQuery("select o from Reply o where o.commentId in(:commentIdList)");
		//给SQL语句设置参数
		query.setParameter("commentIdList", commentIdList);
		
		return query.getResultList();
	}
	/**
	 * 根据评论Id集合查询回复
	 * @param commentIdList 评论Id集合
	 * @param status 状态
	 * @return
	*/
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Reply> findReplyByCommentId(List<Long> commentIdList,Integer status){
		Query query = em.createQuery("select o from Reply o where o.commentId in(:commentIdList) and o.status= :status");
		//给SQL语句设置参数
		query.setParameter("commentIdList", commentIdList);
		query.setParameter("status", status);
		return query.getResultList();
	}
	
	
	
	
	/**
	 * 根据回复Id查询评论回复
	 * @param replyId 回复Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Reply findReplyByReplyId(Long replyId){
		Query query = em.createQuery("select o from Reply o where o.id =?1");
		//给SQL语句设置参数
		query.setParameter(1, replyId);
		
		List<Reply> replyList = query.getResultList();
		if(replyList != null && replyList.size() >0){
			for(Reply reply : replyList){
				return reply;
			}
		}
		return null;
	}
	/**
	 * 修改回复
	 * @param replyId 回复Id
	 * @param content 回复内容
	 * @param userName 用户名称
	 * @param status 状态
	 * @return
	*/
	public Integer updateReply(Long replyId,String content,String userName,Integer status){
		Query query = em.createQuery("update Reply o set o.content=?1,o.userName=?2,o.status=?3 where o.id=?4")
		.setParameter(1, content)
		.setParameter(2, userName)
		.setParameter(3, status)
		.setParameter(4, replyId);
		int i = query.executeUpdate();
		return i;
	} 
	/**
	 * 修改回复状态
	 * @param replyId 回复Id
	 * @param status 状态
	 * @return
	 */
	public int updateReplyStatus(Long replyId,Integer status){
		Query query = em.createQuery("update Reply o set o.status=?1 where o.id=?2")
				.setParameter(1, status)
				.setParameter(2, replyId);
		return query.executeUpdate();
	}
	/**
	 * 删除回复
	 * @param replyId 回复Id
	 * @return
	 */
	public Integer deleteReply(Long replyId){
		//删除订单超卖产品
		Query delete = em.createQuery("delete from Reply o where o.id=?1")
		.setParameter(1, replyId);
		return delete.executeUpdate();
	}
}
