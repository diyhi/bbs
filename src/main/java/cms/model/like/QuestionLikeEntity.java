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
 * 问题点赞 实体类的抽象基类,定义基本属性
 *
 */
@Getter
@Setter
@MappedSuperclass
public class QuestionLikeEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -8709974245848271510L;
	
	/** ID 格式(问题Id_用户Id)**/
	@Id @Column(length=40)
	protected String id;
	/** 问题点赞的用户名称 **/
	@Column(length=30)
	protected String userName;
	
	/** 发布问题的用户名称 **/
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
	
	/** 点赞Id **/
	@Column(length=36)
	protected String likeId;

	
}
