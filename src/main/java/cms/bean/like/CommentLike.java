package cms.bean.like;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 评论点赞
 *
 */
@Entity
@Table(name="commentlike_0",indexes = {@Index(name="commentLike_1_idx", columnList="commentId,addtime")})
public class CommentLike extends CommentLikeEntity implements Serializable{
	private static final long serialVersionUID = 6298354746822361476L;


}
