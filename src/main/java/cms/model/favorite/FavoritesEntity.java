package cms.model.favorite;

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
 * 收藏夹 实体类的抽象基类,定义基本属性
 *
 */
@Getter
@Setter
@MappedSuperclass
public class FavoritesEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = 7804036863650809463L;
	
	/** ID **/
	@Id @Column(length=36)
	protected String id;
	
	/** 模块 10:话题 20:问题 **/
	protected Integer module;
	
	/** 收藏夹的用户名称 **/
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
	
	
	/** 发布 话题/问题 的用户名称 **/
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
	
	/** 问题Id **/
	protected Long questionId;

	/** 问题标题 **/
	@Transient
	protected String questionTitle;


}
