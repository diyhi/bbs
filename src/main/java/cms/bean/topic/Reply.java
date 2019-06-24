package cms.bean.topic;

import java.io.Serializable;
import java.util.Date;

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
	/** 呢称 **/
	@Transient
	private String nickname;
	/** 头像路径 **/
	@Transient
	private String avatarPath;
	/** 头像名称 **/
	@Transient
	private String avatarName;
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
	
	/** 状态 10.待审核 20.已发布 **/
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
	
	
}
