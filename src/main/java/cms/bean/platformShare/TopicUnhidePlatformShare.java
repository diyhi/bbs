package cms.bean.platformShare;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 解锁话题隐藏内容平台分成
 *
 */
@Entity
@Table(indexes = {@Index(name="topicUnhidePlatformShare_1_idx", columnList="unlockTime")})
public class TopicUnhidePlatformShare implements Serializable{
	private static final long serialVersionUID = -2776115664125447453L;

	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 话题Id **/
	private Long topicId;
	/** 话题标题 **/
	@Transient
	private String topicTitle;
	
	/** 分成用户是否为员工 **/
	private boolean staff;
	
	/** 发布话题的用户名称 **/
	@Column(length=80)
	private String postUserName;
	/** 发布话题的用户账号 **/
	@Transient
	private String postAccount;
	/** 发布话题的用户呢称 **/
	@Transient
	private String postNickname;
	/** 发布话题的用户头像路径 **/
	@Transient
	private String postAvatarPath;
	/** 发布话题的用户头像名称 **/
	@Transient
	private String postAvatarName;
	
	
	
	/** 解锁话题的用户名称 **/ 
	@Column(length=30)
	private String unlockUserName;
	/** 解锁话题的用户账号 **/ 
	@Transient
	private String unlockAccount;
	/** 解锁话题的用户呢称 **/
	@Transient
	private String unlockNickname;
	/** 解锁话题的用户头像路径 **/
	@Transient
	private String unlockAvatarPath;
	/** 解锁话题的用户头像名称 **/
	@Transient
	private String unlockAvatarName;
	
	/** 平台分成比例 **/
	private Integer platformShareProportion;
	
	/** 发布话题的用户分成流水号 **/
	@Column(length=32)
	private String postUserShareRunningNumber;
	
	/** 总金额  精度为12位，小数点位数为2位 **/
	@Column(nullable=false,precision=12, scale=2)
	private BigDecimal totalAmount = new BigDecimal("0");
	
	/** 平台分成金额  精度为14位，小数点位数为4位 如果用户为员工，则分成全部归平台 **/
	@Column(nullable=false,precision=14, scale=4)
	private BigDecimal shareAmount = new BigDecimal("0");

	
	/** 解锁时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date unlockTime = new Date();


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getTopicId() {
		return topicId;
	}


	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}


	public boolean isStaff() {
		return staff;
	}


	public void setStaff(boolean staff) {
		this.staff = staff;
	}


	public String getUnlockUserName() {
		return unlockUserName;
	}


	public void setUnlockUserName(String unlockUserName) {
		this.unlockUserName = unlockUserName;
	}


	public String getPostUserName() {
		return postUserName;
	}


	public void setPostUserName(String postUserName) {
		this.postUserName = postUserName;
	}


	public Integer getPlatformShareProportion() {
		return platformShareProportion;
	}


	public void setPlatformShareProportion(Integer platformShareProportion) {
		this.platformShareProportion = platformShareProportion;
	}


	public BigDecimal getTotalAmount() {
		return totalAmount;
	}


	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}


	public BigDecimal getShareAmount() {
		return shareAmount;
	}


	public void setShareAmount(BigDecimal shareAmount) {
		this.shareAmount = shareAmount;
	}


	public Date getUnlockTime() {
		return unlockTime;
	}


	public void setUnlockTime(Date unlockTime) {
		this.unlockTime = unlockTime;
	}


	public String getPostUserShareRunningNumber() {
		return postUserShareRunningNumber;
	}


	public void setPostUserShareRunningNumber(String postUserShareRunningNumber) {
		this.postUserShareRunningNumber = postUserShareRunningNumber;
	}


	public String getTopicTitle() {
		return topicTitle;
	}


	public void setTopicTitle(String topicTitle) {
		this.topicTitle = topicTitle;
	}


	public String getPostNickname() {
		return postNickname;
	}


	public void setPostNickname(String postNickname) {
		this.postNickname = postNickname;
	}


	public String getPostAvatarPath() {
		return postAvatarPath;
	}


	public void setPostAvatarPath(String postAvatarPath) {
		this.postAvatarPath = postAvatarPath;
	}


	public String getPostAvatarName() {
		return postAvatarName;
	}


	public void setPostAvatarName(String postAvatarName) {
		this.postAvatarName = postAvatarName;
	}


	public String getUnlockNickname() {
		return unlockNickname;
	}


	public void setUnlockNickname(String unlockNickname) {
		this.unlockNickname = unlockNickname;
	}


	public String getUnlockAvatarPath() {
		return unlockAvatarPath;
	}


	public void setUnlockAvatarPath(String unlockAvatarPath) {
		this.unlockAvatarPath = unlockAvatarPath;
	}


	public String getUnlockAvatarName() {
		return unlockAvatarName;
	}


	public void setUnlockAvatarName(String unlockAvatarName) {
		this.unlockAvatarName = unlockAvatarName;
	}


	public String getPostAccount() {
		return postAccount;
	}


	public void setPostAccount(String postAccount) {
		this.postAccount = postAccount;
	}


	public String getUnlockAccount() {
		return unlockAccount;
	}


	public void setUnlockAccount(String unlockAccount) {
		this.unlockAccount = unlockAccount;
	}
	
	
}
