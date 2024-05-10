package cms.bean.like;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 答案点赞
 *
 */
@Entity
@Table(name="answerlike_0",indexes = {@Index(name="answerLike_1_idx", columnList="answerId,addtime")})
public class AnswerLike extends AnswerLikeEntity implements Serializable{

	private static final long serialVersionUID = 7914246886931328628L;


}
