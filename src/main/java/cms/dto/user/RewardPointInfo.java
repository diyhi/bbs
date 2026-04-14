package cms.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 奖励积分信息
 * @author Gao
 *
 */
@Getter
@Setter
public class RewardPointInfo implements Serializable{

	@Serial
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

	
}
