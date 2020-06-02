package cms.bean.follow;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 关注 实体类的抽象基类,定义基本属性
 *
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class FollowEntity implements Serializable{
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
	@Temporal(TemporalType.TIMESTAMP)
	protected Date addtime = new Date();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFriendUserName() {
		return friendUserName;
	}

	public void setFriendUserName(String friendUserName) {
		this.friendUserName = friendUserName;
	}

	public String getFriendNickname() {
		return friendNickname;
	}

	public void setFriendNickname(String friendNickname) {
		this.friendNickname = friendNickname;
	}

	public String getFriendAvatarPath() {
		return friendAvatarPath;
	}

	public void setFriendAvatarPath(String friendAvatarPath) {
		this.friendAvatarPath = friendAvatarPath;
	}

	public String getFriendAvatarName() {
		return friendAvatarName;
	}

	public void setFriendAvatarName(String friendAvatarName) {
		this.friendAvatarName = friendAvatarName;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	

	
}
