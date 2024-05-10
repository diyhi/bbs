package cms.bean.like;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 答案回复点赞
 *
 */
@Entity
@Table(name="answerreplylike_0",indexes = {@Index(name="answerReply_1_idx", columnList="replyId,addtime")})
public class AnswerReplyLike extends AnswerReplyLikeEntity implements Serializable{

	private static final long serialVersionUID = 7947897780287232591L;


}
