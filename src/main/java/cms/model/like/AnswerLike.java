package cms.model.like;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 答案点赞
 *
 */
@Entity
@Table(name="answerlike_0",indexes = {@Index(name="answerLike_1_idx", columnList="answerId,addtime")})
public class AnswerLike extends AnswerLikeEntity implements Serializable{

	@Serial
    private static final long serialVersionUID = 7914246886931328628L;


}
