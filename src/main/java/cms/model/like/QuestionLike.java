package cms.model.like;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 问题点赞
 *
 */
@Entity
@Table(name="questionlike_0",indexes = {@Index(name="question_1_idx", columnList="questionId,addtime")})
public class QuestionLike extends QuestionLikeEntity implements Serializable{

	@Serial
    private static final long serialVersionUID = -2836487930297475391L;

}
