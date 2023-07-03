package cms.bean.topic;

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
 * 回复
 *
 */
@Entity
@Table(name="reply",indexes = {@Index(name="reply_1_idx", columnList="commentId,status"),@Index(name="reply_2_idx", columnList="topicId"),@Index(name="reply_3_idx", columnList="userName,isStaff")})
public class Reply implements Serializable{
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
	/** IP **/
	@Column(length=45)
	private String ip;
	/** IP归属地 **/
	@Transient
	private String ipAddress;
	/** 话题Id **/
	private Long topicId;
	/** 话题标题 **/
	@Transient
	private String topicTitle;
	/** 回复内容 **/
	@Lob
	private String content;
	/** 评论Id **/
	private Long commentId;
	/** 回复时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date postTime = new Date();
	/** 最后修改时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdateTime;
	
	/** 状态 10.待审核 20.已发布 **/
	private Integer status = 10;
	
	/** 用户角色名称集合 **/
	@Transient
	private List<String> userRoleNameList = new ArrayList<String>();
	
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
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Long getTopicId() {
		return topicId;
	}
	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Long getCommentId() {
		return commentId;
	}
	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}
	public Date getPostTime() {
		return postTime;
	}
	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getTopicTitle() {
		return topicTitle;
	}
	public void setTopicTitle(String topicTitle) {
		this.topicTitle = topicTitle;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
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
