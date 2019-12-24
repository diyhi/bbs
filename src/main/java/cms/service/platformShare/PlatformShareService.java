package cms.service.platformShare;

import cms.bean.platformShare.QuestionRewardPlatformShare;
import cms.bean.platformShare.TopicUnhidePlatformShare;
import cms.service.besa.DAO;

/**
 * 平台分成
 *
 */
public interface PlatformShareService extends DAO<TopicUnhidePlatformShare>{
	
	/**
	 * 根据问题Id和回答问题的用户名称查询问答悬赏平台分成
	 * @param questionId 问题Id
	 * @param answerUserName 回答问题的用户名称
	 * @return
	 */
	public QuestionRewardPlatformShare findQuestionRewardPlatformShareByQuestionIdAndAnswerUserName(Long questionId,String answerUserName);
	/**
	 * 删除问答悬赏平台分成
	 * @param questionId 问题Id
	 * @param answerUserName 回答问题的用户名称
	 * @return
	 */
	public int deleteQuestionRewardPlatformShare(Long questionId,String answerUserName);
	
}
