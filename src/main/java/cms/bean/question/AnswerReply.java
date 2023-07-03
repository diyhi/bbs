package cms.bean.question;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 答案回复
 *
 */
@Entity
@Table(name="answerreply",indexes = {@Index(name="answerReply_1_idx", columnList="answerId,status"),@Index(name="answerReply_2_idx", columnList="questionId"),@Index(name="answerReply_3_idx", columnList="userName,isStaff")})
public class AnswerReply implements Serializable{
	private static final long serialVersionUID = 4931492011929337590L;
	
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 是否是员工   true:员工   false:会员 **/
	private Boolean isStaff = false;
	/** 用户名称 **/
	@Column(length=30)
	private String userName;
	/** 账号 **/
	@Transient
	private String account;
	/** 呢称 **/
	@Transient
	private String nickname;
	/** 头像路径 **/
	@Transient
	private String avatarPath;
	/** 头像名称 **/
	@Transient
	private String avatarName;
	
	/** 对方回复Id **/
	private Long friendReplyId;
	/** 对方回复Id组 **/
	@Lob
	private String friendReplyIdGroup = ",0,";
	/** 对方是否是员工   true:员工   false:会员 **/
	private Boolean isFriendStaff = false;
	/** 对方用户名称 **/
	@Column(length=30)
	private String friendUserName;
	/** 对方账号 **/
	@Transient
	private String friendAccount;
	/** 对方呢称 **/
	@Transient
	private String friendNickname;
	/** 对方头像路径 **/
	@Transient
	private String friendAvatarPath;
	/** 对方头像名称 **/
	@Transient
	private String friendAvatarName;
	
	/** 用户信息状态 -30.账号已注销(不显示数据) -20.账号已逻辑删除(不显示数据) -10.账号已禁用(不显示数据)  0.正常 10.账号已禁用(显示数据) 20.账号已逻辑删除(显示数据) **/
	@Transient
	private Integer userInfoStatus = 0;
	/** 用户角色名称集合 **/
	@Transient
	private List<String> userRoleNameList = new ArrayList<String>();
	/** IP **/
	@Column(length=45)
	private String ip;
	/** IP归属地 **/
	@Transient
	private String ipAddress;
	/** 问题Id **/
	private Long questionId;
	/** 问题标题 **/
	@Transient
	private String questionTitle;
	/** 回复内容 **/
	@Lob
	private String content;
	/** 答案Id **/
	private Long answerId;
	/** 回复时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date postTime = new Date();
	/** 最后修改时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdateTime;
	
	/** 状态 10.待审核 20.已发布 110.待审核用户删除 120.已发布用户删除 100010.待审核员工删除 100020.已发布员工删除 **/
	private Integer status = 10;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getIsStaff() {
		return isStaff;
	}

	public void setIsStaff(Boolean isStaff) {
		this.isStaff = isStaff;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public String getQuestionTitle() {
		return questionTitle;
	}

	public void setQuestionTitle(String questionTitle) {
		this.questionTitle = questionTitle;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getAnswerId() {
		return answerId;
	}

	public void setAnswerId(Long answerId) {
		this.answerId = answerId;
	}

	public Date getPostTime() {
		return postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<String> getUserRoleNameList() {
		return userRoleNameList;
	}

	public void setUserRoleNameList(List<String> userRoleNameList) {
		this.userRoleNameList = userRoleNameList;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getUserInfoStatus() {
		return userInfoStatus;
	}

	public void setUserInfoStatus(Integer userInfoStatus) {
		this.userInfoStatus = userInfoStatus;
	}

	public Long getFriendReplyId() {
		return friendReplyId;
	}

	public void setFriendReplyId(Long friendReplyId) {
		this.friendReplyId = friendReplyId;
	}

	public String getFriendReplyIdGroup() {
		return friendReplyIdGroup;
	}

	public void setFriendReplyIdGroup(String friendReplyIdGroup) {
		this.friendReplyIdGroup = friendReplyIdGroup;
	}

	public Boolean getIsFriendStaff() {
		return isFriendStaff;
	}

	public void setIsFriendStaff(Boolean isFriendStaff) {
		this.isFriendStaff = isFriendStaff;
	}

	public String getFriendUserName() {
		return friendUserName;
	}

	public void setFriendUserName(String friendUserName) {
		this.friendUserName = friendUserName;
	}

	public String getFriendAccount() {
		return friendAccount;
	}

	public void setFriendAccount(String friendAccount) {
		this.friendAccount = friendAccount;
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
	
	
}
