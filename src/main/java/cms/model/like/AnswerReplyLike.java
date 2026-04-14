package cms.model.like;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 答案回复点赞
 *
 */
@Entity
@Table(name="answerreplylike_0",indexes = {@Index(name="answerReply_1_idx", columnList="replyId,addtime")})
public class AnswerReplyLike extends AnswerReplyLikeEntity implements Serializable{

	@Serial
    private static final long serialVersionUID = 7947897780287232591L;


}
