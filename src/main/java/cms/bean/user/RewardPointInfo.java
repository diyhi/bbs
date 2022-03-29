package cms.bean.user;

import java.io.Serializable;

/**
 * 奖励积分信息
 * @author Gao
 *
 */
public class RewardPointInfo implements Serializable{

	private static final long serialVersionUID = -4809755688926932271L;

	/** 发表话题奖励积分 **/
	private Long topic_rewardPoint = 0L;
	/** 发表评论奖励积分 **/
	private Long comment_rewardPoint = 0L;
	/** 发表回复奖励积分 **/
	private Long reply_rewardPoint = 0L;
	
	/** 提交问题奖励积分 **/
	private Long question_rewardPoint = 0L;
	/** 提交答案奖励积分 **/
	private Long answer_rewardPoint = 0L;
	/** 提交答案回复奖励积分 **/
	private Long answerReply_rewardPoint = 0L;
	
	public Long getTopic_rewardPoint() {
		return topic_rewardPoint;
	}
	public void setTopic_rewardPoint(Long topic_rewardPoint) {
		this.topic_rewardPoint = topic_rewardPoint;
	}
	public Long getComment_rewardPoint() {
		return comment_rewardPoint;
	}
	public void setComment_rewardPoint(Long comment_rewardPoint) {
		this.comment_rewardPoint = comment_rewardPoint;
	}
	public Long getReply_rewardPoint() {
		return reply_rewardPoint;
	}
	public void setReply_rewardPoint(Long reply_rewardPoint) {
		this.reply_rewardPoint = reply_rewardPoint;
	}
	public Long getQuestion_rewardPoint() {
		return question_rewardPoint;
	}
	public void setQuestion_rewardPoint(Long question_rewardPoint) {
		this.question_rewardPoint = question_rewardPoint;
	}
	public Long getAnswer_rewardPoint() {
		return answer_rewardPoint;
	}
	public void setAnswer_rewardPoint(Long answer_rewardPoint) {
		this.answer_rewardPoint = answer_rewardPoint;
	}
	public Long getAnswerReply_rewardPoint() {
		return answerReply_rewardPoint;
	}
	public void setAnswerReply_rewardPoint(Long answerReply_rewardPoint) {
		this.answerReply_rewardPoint = answerReply_rewardPoint;
	}
	
	
}
