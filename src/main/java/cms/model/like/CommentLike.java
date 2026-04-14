package cms.model.like;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 评论点赞
 *
 */
@Entity
@Table(name="commentlike_0",indexes = {@Index(name="commentLike_1_idx", columnList="commentId,addtime")})
public class CommentLike extends CommentLikeEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = 6298354746822361476L;


}
