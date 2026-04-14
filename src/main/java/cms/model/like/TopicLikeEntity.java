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
 * 话题点赞 实体类的抽象基类,定义基本属性
 *
 */
@Getter
@Setter
@MappedSuperclass
public class TopicLikeEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = 3112294654438891737L;
	
	/** ID 格式(话题Id_用户Id)**/
	@Id @Column(length=40)
	protected String id;
	/** 话题点赞的用户名称 **/
	@Column(length=30)
	protected String userName;
	
	/** 发布话题的用户名称 **/
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
	
	/** 点赞Id **/
	@Column(length=36)
	protected String likeId;

}
