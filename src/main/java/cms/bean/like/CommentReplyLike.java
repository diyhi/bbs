package cms.bean.like;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 评论回复点赞
 *
 */
@Entity
@Table(name="commentreplylike_0",indexes = {@Index(name="commentReply_1_idx", columnList="replyId,addtime")})
public class CommentReplyLike extends CommentReplyLikeEntity implements Serializable{

	private static final long serialVersionUID = 7235705644148152307L;

}
