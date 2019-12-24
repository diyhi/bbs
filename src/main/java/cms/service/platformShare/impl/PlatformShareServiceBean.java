package cms.service.platformShare.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.platformShare.QuestionRewardPlatformShare;
import cms.bean.platformShare.TopicUnhidePlatformShare;
import cms.service.besa.DaoSupport;
import cms.service.platformShare.PlatformShareService;

/**
 * 平台分成
 *
 */
@Service
@Transactional
public class PlatformShareServiceBean extends DaoSupport<TopicUnhidePlatformShare> implements PlatformShareService{
	
	/**
	 * 根据问题Id和回答问题的用户名称查询问答悬赏平台分成
	 * @param questionId 问题Id
	 * @param answerUserName 回答问题的用户名称
	 * @return
	 */
	public QuestionRewardPlatformShare findQuestionRewardPlatformShareByQuestionIdAndAnswerUserName(Long questionId,String answerUserName){
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
