package cms.repository.platformShare.impl;

import java.util.List;


import cms.model.platformShare.QuestionRewardPlatformShare;
import cms.model.platformShare.TopicUnhidePlatformShare;
import cms.repository.besa.DaoSupport;
import cms.repository.platformShare.PlatformShareRepository;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;



/**
 * 平台分成管理接口实现类
 *
 */
@Repository
@Transactional
public class PlatformShareRepositoryImpl extends DaoSupport<TopicUnhidePlatformShare> implements PlatformShareRepository {
	
	/**
	 * 根据问题Id和回答问题的用户名称查询问答悬赏平台分成
	 * @param questionId 问题Id
	 * @param answerUserName 回答问题的用户名称
	 * @return
	 */
	public QuestionRewardPlatformShare findQuestionRewardPlatformShareByQuestionIdAndAnswerUserName(Long questionId, String answerUserName){
		Query query = em.createQuery("select o from QuestionRewardPlatformShare o where o.questionId=?1 and o.answerUserName=?2");
		//给SQL语句设置参数
		query.setParameter(1, questionId);
		query.setParameter(2, answerUserName);
		List<QuestionRewardPlatformShare> questionRewardPlatformShareList = query.getResultList();
		if(questionRewardPlatformShareList != null && questionRewardPlatformShareList.size() >0){
			for(QuestionRewardPlatformShare questionRewardPlatformShare : questionRewardPlatformShareList){
				return questionRewardPlatformShare;
			}
		}
		return null;
		
	}
	
	
	/**
	 * 删除问答悬赏平台分成
	 * @param questionId 问题Id
	 * @param answerUserName 回答问题的用户名称
	 * @return
	 */
	public int deleteQuestionRewardPlatformShare(Long questionId,String answerUserName){
		//删除答案
		Query delete = em.createQuery("delete from QuestionRewardPlatformShare o where o.questionId=?1 and o.answerUserName=?2");
		delete.setParameter(1, questionId);
		delete.setParameter(2, answerUserName);
		return delete.executeUpdate();
	}
}
