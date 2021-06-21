package cms.bean.topic;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * 取消隐藏 实体类的抽象基类,定义基本属性
 *
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class UnhideEntity implements Serializable{
	private static final long serialVersionUID = -5556786175535354645L;
	
	/** ID (结构：取消隐藏的用户Id_隐藏标签类型_话题Id)**/
	@Id @Column(length=43)
	protected String id;
	/** 取消隐藏的用户名称 **/
	@Column(length=30)
	protected String userName;
	/** 呢称 **/
	@Transient
	private String nickname;
	/** 头像路径 **/
	@Transient
	private String avatarPath;
	/** 头像名称 **/
	@Transient
	private String avatarName;
	
	
	
	
	/** 消费积分 **/
	protected Long point;
	
	/** 消费余额  精度为12位，小数点位数为2位**/
	protected BigDecimal amount;
	
	/** 取消隐藏时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	protected Date cancelTime = new Date();
	
	/** 隐藏标签类型 10:输入密码可见  40:积分购买可见  50:余额购买可见  **/
	protected Integer hideTagType;

	/** 发布话题的用户名称 **/
	@Column(length=30)
	protected String postUserName;
	
	/** 话题Id **/
	protected Long topicId;
	
	/** 话题标题 **/
	@Transient
	protected String topicTitle;

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

	public Long getPoint() {
		return point;
	}

	public void setPoint(Long point) {
		this.point = point;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getCancelTime() {
		return cancelTime;
	}

	public void setCancelTime(Date cancelTime) {
		this.cancelTime = cancelTime;
	}

	public Integer getHideTagType() {
		return hideTagType;
	}

	public void setHideTagType(Integer hideTagType) {
		this.hideTagType = hideTagType;
	}

	public String getPostUserName() {
		return postUserName;
	}

	public void setPostUserName(String postUserName) {
		this.postUserName = postUserName;
	}

	public Long getTopicId() {
		return topicId;
	}

	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}

	public String getTopicTitle() {
		return topicTitle;
	}

	public void setTopicTitle(String topicTitle) {
		this.topicTitle = topicTitle;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatarPath() {
		return avatarPath;
	}

	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}

	public String getAvatarName() {
		return avatarName;
	}

	public void setAvatarName(String avatarName) {
		this.avatarName = avatarName;
	}

	
	
}
