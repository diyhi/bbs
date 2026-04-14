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
 * 答案点赞 实体类的抽象基类,定义基本属性
 *
 */
@Getter
@Setter
@MappedSuperclass
public class AnswerLikeEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -1386787741210428896L;
	
	/** ID 格式(答案Id_用户Id)**/
	@Id @Column(length=40)
	protected String id;
	/** 答案点赞的用户名称 **/
	@Column(length=30)
	protected String userName;
	
	/** 发布答案的用户名称 **/
	@Column(length=30)
	protected String postUserName;
	/** 加入时间 **/

    @Column(columnDefinition = "DATETIME")
    protected LocalDateTime addtime = LocalDateTime.now();
	
	/** 问题Id **/
	protected Long questionId;

	/** 问题标题 **/
	@Transient
	protected String questionTitle;
	
	/** 答案Id **/
	protected Long answerId;
	/** 答案内容摘要 **/
	@Transient
	protected String summary;
	
	/** 点赞Id **/
	@Column(length=36)
	protected String likeId;
	

}
