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
 * 点赞 实体类的抽象基类,定义基本属性
 *
 */
@Getter
@Setter
@MappedSuperclass
public class LikeEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -1496184788206840632L;
	
	/** ID **/
	@Id @Column(length=36)
	protected String id;
	/** 点赞的用户名称 **/
	@Column(length=30)
	protected String userName;
	/** 账号 **/
	@Transient
	protected String account;
	/** 呢称 **/
	@Transient
	protected String nickname;
	/** 头像路径 **/
	@Transient
	protected String avatarPath;
	/** 头像名称 **/
	@Transient
	protected String avatarName;
	
	/** 发布话题的用户名称 **/
	@Column(length=30)
	protected String postUserName;
	
	/** 加入时间 **/
    @Column(columnDefinition = "DATETIME")
    protected LocalDateTime addtime = LocalDateTime.now();
	
	/** 模块  10:话题  20:评论  30:评论回复  40:问题  50:答案  60:答案回复 **/
	protected Integer module;
	
	/** 话题Id **/
	protected Long topicId;

	/** 话题标题 **/
	@Transient
	protected String topicTitle;
	
	/** 评论Id **/
	protected Long commentId;
	/** 评论回复Id **/
	protected Long commentReplyId;
	
	/** 问题Id **/
	protected Long questionId;
	/** 问题标题 **/
	@Transient
	protected String questionTitle;
	/** 答案Id **/
	protected Long answerId;
	/** 答案回复Id **/
	protected Long answerReplyId;
	
	
	/** 内容摘要 **/
	@Transient
	protected String summary;

}
