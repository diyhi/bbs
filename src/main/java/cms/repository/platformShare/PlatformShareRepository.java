package cms.repository.platformShare;


import cms.model.platformShare.QuestionRewardPlatformShare;
import cms.model.platformShare.TopicUnhidePlatformShare;
import cms.repository.besa.DAO;

/**
 * 平台分成管理接口
 *
 */
public interface PlatformShareRepository extends DAO<TopicUnhidePlatformShare> {
	
	/**
	 * 根据问题Id和回答问题的用户名称查询问答悬赏平台分成
	 * @param questionId 问题Id
	 * @param answerUserName 回答问题的用户名称
	 * @return
	 */
	public QuestionRewardPlatformShare findQuestionRewardPlatformShareByQuestionIdAndAnswerUserName(Long questionId, String answerUserName);
	/**
	 * 删除问答悬赏平台分成
	 * @param questionId 问题Id
	 * @param answerUserName 回答问题的用户名称
	 * @return
	 */
	public int deleteQuestionRewardPlatformShare(Long questionId,String answerUserName);
	
}
