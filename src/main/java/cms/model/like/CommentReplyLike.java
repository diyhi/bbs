package cms.model.like;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 评论回复点赞
 *
 */
@Entity
@Table(name="commentreplylike_0",indexes = {@Index(name="commentReply_1_idx", columnList="replyId,addtime")})
public class CommentReplyLike extends CommentReplyLikeEntity implements Serializable{

	@Serial
    private static final long serialVersionUID = 7235705644148152307L;

}
