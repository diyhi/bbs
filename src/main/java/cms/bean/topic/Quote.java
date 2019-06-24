package cms.bean.topic;

import java.io.Serializable;



/**
 * 引用
 *
 */
public class Quote implements Serializable{
	private static final long serialVersionUID = 3839982541792197957L;
	
	/** 评论Id **/
	private Long commentId;
	/** 是否是员工   true:员工   false:会员 **/
	private Boolean isStaff = false;
	/** IP **/
	private String ip;
	/** IP归属地 **/
	private String ipAddress;
	/** 用户名称 **/
	private String userName;
	/** 呢称 **/
	private String nickname;
	/** 评论内容 **/
	private String content;
	public Long getCommentId() {
		return commentId;
	}
	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}
	public Boolean getIsStaff() {
		return isStaff;
	}
	public void setIsStaff(Boolean isStaff) {
		this.isStaff = isStaff;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	
	
}
