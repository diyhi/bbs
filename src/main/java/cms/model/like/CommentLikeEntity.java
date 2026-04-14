package cms.model.like;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 评论点赞 实体类的抽象基类,定义基本属性
 *
 */
@Getter
@Setter
@MappedSuperclass
public class CommentLikeEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = 7818780595467664556L;
	
	/** ID 格式(评论Id_用户Id)**/
	@Id @Column(length=40)
	protected String id;
	/** 评论点赞的用户名称 **/
	@Column(length=30)
	protected String userName;
	
	/** 发布评论的用户名称 **/
	@Column(length=30)
	protected String postUserName;
	/** 加入时间 **/
    @Column(columnDefinition = "DATETIME")
    protected LocalDateTime addtime = LocalDateTime.now();
	/** 话题Id **/
	protected Long topicId;

	/** 话题标题 **/
	@Transient
	protected String topicTitle;
	
	/** 评论Id **/
	protected Long commentId;
	/** 评论内容摘要 **/
	@Transient
	protected String summary;
	
	/** 点赞Id **/
	@Column(length=36)
	protected String likeId;
	
}
