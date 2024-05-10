package cms.bean.like;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 问题点赞
 *
 */
@Entity
@Table(name="questionlike_0",indexes = {@Index(name="question_1_idx", columnList="questionId,addtime")})
public class QuestionLike extends QuestionLikeEntity implements Serializable{

	private static final long serialVersionUID = -2836487930297475391L;

}
