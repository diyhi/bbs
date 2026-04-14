package cms.model.follow;

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
 * 关注 实体类的抽象基类,定义基本属性
 *
 */
@Getter
@Setter
@MappedSuperclass
public class FollowEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -8282342335765365195L;
	/** ID (结构：对方用户Id-关注的用户Id)**/
	@Id @Column(length=40)
	protected String id;
	
	/** 关注的用户名称 **/
	@Column(length=30)
	protected String userName;
	
	
	/** 对方的用户名称 **/
	@Column(length=30)
	protected String friendUserName;
	/** 对方账号 **/
	@Transient
	protected String friendAccount;
	/** 对方呢称 **/
	@Transient
	protected String friendNickname;
	/** 对方头像路径 **/
	@Transient
	protected String friendAvatarPath;
	/** 对方头像名称 **/
	@Transient
	protected String friendAvatarName;
	
	/** 加入时间 **/
    @Column(columnDefinition = "DATETIME")
    protected LocalDateTime addtime = LocalDateTime.now();


}
