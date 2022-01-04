package cms.bean.membershipCard;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 会员卡赠送项Entity
 *
 */

@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class MembershipCardGiftItemEntity implements Serializable{
	private static final long serialVersionUID = -5763871320407326485L;
	
	/** Id  格式: 会员卡赠送任务Id- 用户Id **/
	@Id @Column(length=65)
	protected String id;
	/** 任务类型 10:长期  20:一次性 **/
	protected Integer type = 10;
	/** 会员卡赠送任务Id **/
	protected Long membershipCardGiftTaskId;
	
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
	
	/** 用户名称 **/
	@Column(length=30)
	protected String userName;
	/** 赠送时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	protected Date postTime = new Date();
	
	/** 赠送时长 **/
	protected Integer duration;
	/** 赠送时长时长单位 10.小时 20.日 30.月 40.年 **/
	protected Integer unit;
	
	/** 限制条件 restrictionGroupList值的json格式字符 **/
	@Lob
	private String restriction;
	/** 限制条件组集合 **/
	@Transient
	private RestrictionGroup restrictionGroup = new RestrictionGroup();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getPostTime() {
		return postTime;
	}
	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public Integer getUnit() {
		return unit;
	}
	public void setUnit(Integer unit) {
		this.unit = unit;
	}
	public Long getMembershipCardGiftTaskId() {
		return membershipCardGiftTaskId;
	}
	public void setMembershipCardGiftTaskId(Long membershipCardGiftTaskId) {
		this.membershipCardGiftTaskId = membershipCardGiftTaskId;
	}
	public String getRestriction() {
		return restriction;
	}
	public void setRestriction(String restriction) {
		this.restriction = restriction;
	}
	public RestrictionGroup getRestrictionGroup() {
		return restrictionGroup;
	}
	public void setRestrictionGroup(RestrictionGroup restrictionGroup) {
		this.restrictionGroup = restrictionGroup;
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
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}

	
}
