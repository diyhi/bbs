package cms.service.question.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.question.Answer;
import cms.bean.question.AnswerReply;
import cms.service.besa.DaoSupport;
import cms.service.question.AnswerService;
import cms.service.question.QuestionService;


/**
 * 答案管理实现类
 *
 */
@Service
@Transactional
public class AnswerServiceBean extends DaoSupport<Answer> implements AnswerService {

	@Resource QuestionService questionService;

	
	
	/**
	 * 根据答案Id查询答案
	 * @param answerId 答案Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Answer findByAnswerId(Long answerId){
		Query query = em.createQuery("select o from Answer o where o.id =?1");
		//给SQL语句设置参数
		query.setParameter(1, answerId);
		
		List<Answer> answerList = query.getResultList();
		if(answerList != null && answerList.size() >0){
			for(Answer answer : answerList){
				return answer;
			}
		}
		return null;
	}
	/**
	 * 根据答案Id集合查询答案
	 * @param answerIdList 答案Id集合
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Answer> findByCommentIdList(List<Long> answerIdList){
		Query query = em.createQuery("select o from Answer o where o.id in(:answerIdList)");
		//给SQL语句设置参数
		query.setParameter("answerIdList", answerIdList);
		
		return query.getResultList();
	}
	
	/**
	 * 根据用户名称查询回答数量
	 * @param userName 用户名称
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long findAnswerCountByUserName(String userName){
		Query query = em.createQuery("select count(o) from Answer o where o.userName=?1 and o.status=?2");
		query.setParameter(1, userName);
		query.setParameter(2, 20);
		return (Long)query.getSingleResult();
	}
	
	/**
	 * 根据答案Id查询答案在表的第几行
	 * @param answerId 答案Id
	 * @param questionId 问题Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Long findRowByAnswerId(Long answerId,Long questionId){
		Query query = em.createQuery("select count(o.id) from Answer o where o.id <=?1 and o.questionId= ?2 order by o.postTime asc");
		//给SQL语句设置参数
		query.setParameter(1, answerId);
		query.setParameter(2, questionId);
		return (Long)query.getSingleResult();		
	}
	/**
	 * 根据答案Id查询答案在表的第几行
	 * @param answerId 答案Id
	 * @param questionId 问题Id
	 * @param status 状态
	 * @param sort 按发表时间排序 1.desc 2.asc 
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Long findRowByAnswerId(Long answerId,Long questionId,Integer status,Integer sort){
		String answerId_sql = "";
		String sort_sql = "";
		if(sort.equals(1)){
			answerId_sql = " o.id >=?1";
			sort_sql = " desc";
		}else{
			answerId_sql = " o.id <=?1";
			sort_sql = " asc";
		}
		Query query = em.createQuery("select count(o.id) from Answer o where "+answerId_sql+" and o.questionId= ?2 and o.status= ?3 order by o.postTime"+sort_sql);
		//给SQL语句设置参数
		query.setParameter(1, answerId);
		query.setParameter(2, questionId);
		query.setParameter(3, status);
		return (Long)query.getSingleResult();		
	}
	
	/**
	 * 分页查询答案内容
	 * @param firstIndex
	 * @param maxResult
	 * @param userName 用户名称
	 * @param isStaff 是否为员工
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<String> findAnswerContentByPage(int firstIndex, int maxResult,String userName,boolean isStaff){
		List<String> contentList = new ArrayList<String>();//key:话题Id  value:话题内容
		
		String sql = "select o.content from Answer o where o.userName=?1 and o.isStaff=?2";
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
				contentList.add(content);
			}
		}
		
		return contentList;
	}
	
	
	
	/**
	 * 保存答案
	 * @param answer
	 */
	public void saveAnswer(Answer answer){
		this.save(answer);
		questionService.addAnswerTotal(answer.getQuestionId(), 1L);
	}
	
	
	/**
	 * 修改答案
	 * @param answerId 答案Id
	 * @param content 内容
	 * @param status 状态
	 * @param userName 用户名称
	 * @return
	 */
	public Integer updateAnswer(Long answerId,String content,Integer status,String userName){
		Query query = em.createQuery("update Answer o set o.content=?1,o.userName=?2,o.status=?3 where o.id=?4")
		.setParameter(1, content)
		.setParameter(2, userName)
		.setParameter(3, status)
		.setParameter(4, answerId);
		return query.executeUpdate();
	}
	
	/**
	 * 修改答案状态
	 * @param answerId 答案Id
	 * @param status 状态
	 * @return
	 */
	public int updateAnswerStatus(Long answerId,Integer status){
		Query query = em.createQuery("update Answer o set o.status=?1 where o.id=?2")
				.setParameter(1, status)
				.setParameter(2, answerId);
		return query.executeUpdate();
	}
	
	/**
	 * 采纳答案
	 * @param questionId 问题Id
	 * @param answerId 答案Id
	 * @return
	 */
	public int updateAdoptionAnswer(Long questionId, Long answerId){
		//先设置所有答案为不采纳
		Query query = em.createQuery("update Answer o set o.adoption=?1 where o.questionId=?2")
				.setParameter(1, false)
				.setParameter(2, questionId);
		query.executeUpdate();
		
		Query query_1 = em.createQuery("update Answer o set o.adoption=?1 where o.id=?2")
				.setParameter(1, true)
				.setParameter(2, answerId);
		int i = query_1.executeUpdate();
		if(i >0){
			//修改问题的采纳答案Id
			questionService.updateAdoptionAnswerId(questionId, answerId);
		}
		return i;
	}
	/**
	 * 取消采纳答案
	 * @param questionId 问题Id
	 * @return
	 */
	public int updateCancelAdoptionAnswer(Long questionId){
		//先设置所有答案为不采纳
		Query query = em.createQuery("update Answer o set o.adoption=?1 where o.questionId=?2")
				.setParameter(1, false)
				.setParameter(2, questionId);
		query.executeUpdate();
		//修改问题的采纳答案Id
		return questionService.updateAdoptionAnswerId(questionId, 0L);
	}
	
	
	
	/**
	 * 删除答案
	 * @param questionId 问题Id
	 * @param answerId 答案Id
	 * @return
	*/
	public Integer deleteAnswer(Long questionId,Long answerId){
		//删除答案
		Query delete = em.createQuery("delete from Answer o where o.id=?1");
		delete.setParameter(1, answerId);
		int i = delete.executeUpdate();
		
		//删除答案回复
		Query delete_reply = em.createQuery("delete from AnswerReply o where o.answerId=?1");
		delete_reply.setParameter(1, answerId);
		delete_reply.executeUpdate();
		
		questionService.subtractAnswerTotal(questionId,1L);
		return i;
	} 
	
	
	/**
	 * 根据用户名称集合删除答案
	 * @param userNameList 用户名称集合
	 * @return
	 */
	public Integer deleteAnswer(List<String> userNameList){
		Query delete = em.createQuery("delete from Answer o where o.userName in(:userName) and o.isStaff=:isStaff")
				.setParameter("userName", userNameList)
				.setParameter("isStaff", false);
		return delete.executeUpdate();
		
	}
	
			
			
		
	
	
	/**--------------------------------------- 回复 ---------------------------------------**/
	
	
	/**
	 * 添加回复
	 * @param answerReply
	*/
	public void saveReply(AnswerReply answerReply){
		this.save(answerReply);
	} 
	/**
	 * 根据答案Id查询回复
	 * @param answerId 答案Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<AnswerReply> findReplyByAnswerId(Long answerId){
		Query query = em.createQuery("select o from AnswerReply o where o.answerId=?1");
		//给SQL语句设置参数
		query.setParameter(1, answerId);
		
		return query.getResultList();
	}
	/**
	 * 根据答案Id集合查询回复
	 * @param answerIdList 答案Id集合
	 * @return
	*/
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<AnswerReply> findReplyByAnswerId(List<Long> answerIdList){
		Query query = em.createQuery("select o from AnswerReply o where o.answerId in(:answerIdList)");
		//给SQL语句设置参数
		query.setParameter("answerIdList", answerIdList);
		
		return query.getResultList();
	}
	/**
	 * 根据答案Id集合查询回复
	 * @param answerIdList 答案Id集合
	 * @param status 状态
	 * @return
	*/
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<AnswerReply> findReplyByAnswerId(List<Long> answerIdList,Integer status){
		Query query = em.createQuery("select o from AnswerReply o where o.answerId in(:answerIdList) and o.status= :status");
		//给SQL语句设置参数
		query.setParameter("answerIdList", answerIdList);
		query.setParameter("status", status);
		return query.getResultList();
	}
	
	
	
	
	/**
	 * 根据答案回复Id查询答案回复
	 * @param answerReplyId 答案回复Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public AnswerReply findReplyByReplyId(Long answerReplyId){
		Query query = em.createQuery("select o from AnswerReply o where o.id =?1");
		//给SQL语句设置参数
		query.setParameter(1, answerReplyId);
		
		List<AnswerReply> replyList = query.getResultList();
		if(replyList != null && replyList.size() >0){
			for(AnswerReply reply : replyList){
				return reply;
			}
		}
		return null;
	}
	
	/**
	 * 分页查询回复
	 * @param userName 用户名称
	 * @param postTime 回复发表时间
	 * @param firstIndex
	 * @param maxResult
	 * @return
	 
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Reply> findReplyByPage(String userName,Date postTime,int firstIndex, int maxResult){
		String sql = "select o from Reply o where o.userName=?1 and o.postTime>?2";
		Query query = em.createQuery(sql);	
		query.setParameter(1, userName);
		query.setParameter(2, postTime);
		//索引开始,即从哪条记录开始
		query.setFirstResult(firstIndex);
		//获取多少条数据
		query.setMaxResults(maxResult);
		
		List<Reply> replyList = query.getResultList();
		return replyList;
	}*/
	
	/**
	 * 修改回复
	 * @param answerReplyId 回复Id
	 * @param content 回复内容
	 * @param userName 用户名称
	 * @param status 状态
	 * @return
	*/
	public Integer updateReply(Long answerReplyId,String content,String userName,Integer status){
		Query query = em.createQuery("update AnswerReply o set o.content=?1,o.userName=?2,o.status=?3 where o.id=?4")
		.setParameter(1, content)
		.setParameter(2, userName)
		.setParameter(3, status)
		.setParameter(4, answerReplyId);
		int i = query.executeUpdate();
		return i;
	} 
	/**
	 * 修改回复状态
	 * @param answerReplyId 回复Id
	 * @param status 状态
	 * @return
	 */
	public int updateReplyStatus(Long answerReplyId,Integer status){
		Query query = em.createQuery("update AnswerReply o set o.status=?1 where o.id=?2")
				.setParameter(1, status)
				.setParameter(2, answerReplyId);
		return query.executeUpdate();
	}
	/**
	 * 删除回复
	 * @param answerReplyId 回复Id
	 * @return
	 */
	public Integer deleteReply(Long answerReplyId){
		Query delete = em.createQuery("delete from AnswerReply o where o.id=?1")
		.setParameter(1, answerReplyId);
		return delete.executeUpdate();
	}
	/**
	 * 根据用户名称集合删除答案回复
	 * @param userNameList 用户名称集合
	 * @return
	 */
	public Integer deleteAnswerReply(List<String> userNameList){
		//删除回复
		Query delete = em.createQuery("delete from AnswerReply o where o.userName in(:userName) and o.isStaff=:isStaff")
				.setParameter("userName", userNameList)
				.setParameter("isStaff", false);
		return delete.executeUpdate();
		
	}
}
