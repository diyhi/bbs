package cms.bean.message;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * 私信Entity
 *
 */

@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class PrivateMessageEntity implements Serializable{
	private static final long serialVersionUID = -1184889560330149363L;
	
	/** Id  Id的后4位为用户Id的后4位**/
	@Id @Column(length=36)
	protected String id;
	/** 私信用户Id(虚拟字段)   userId和friendUserId字段值对调 **/
	protected Long userId;
	/** 私信对方用户Id (虚拟字段) **/
	protected Long friendUserId;
	/** 私信对方用户名称 **/
	@Transient
	protected String friendUserName;
	/** 私信对方头像路径 **/
	@Transient
	private String friendAvatarPath;
	/** 私信对方头像名称 **/
	@Transient
	private String friendAvatarName;
	
	
	
	
	/** 发送者用户Id **/
	protected Long senderUserId;
	/** 接受者用户Id **/
	protected Long receiverUserId;
	
	/** 发送者用户名称  **/
	@Transient
	protected String senderUserName;
	/** 发送者头像路径 **/
	@Transient
	private String senderAvatarPath;
	/** 发送者头像名称 **/
	@Transient
	private String senderAvatarName;
	
	/** 消息内容 **/
	@Lob
	protected String messageContent;
	/** 消息状态 10:未读  20:已读  110:未读删除  120:已读删除 **/ 
	protected Integer status = 10;
	/** 发送时间格式化 **/
	protected Long sendTimeFormat;
	
	/** 阅读时间格式化 **/
	protected Long readTimeFormat;
	
	
	
	/** 发送时间 **/
	@Transient
	protected Date sendTime;
	/** 阅读时间 **/
	@Transient
	protected Date readTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getFriendUserId() {
		return friendUserId;
	}
	public void setFriendUserId(Long friendUserId) {
		this.friendUserId = friendUserId;
	}
	public Long getSenderUserId() {
		return senderUserId;
	}
	public void setSenderUserId(Long senderUserId) {
		this.senderUserId = senderUserId;
	}
	public Long getReceiverUserId() {
		return receiverUserId;
	}
	public void setReceiverUserId(Long receiverUserId) {
		this.receiverUserId = receiverUserId;
	}
	public String getMessageContent() {
		return messageContent;
	}
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Long getSendTimeFormat() {
		return sendTimeFormat;
	}
	public void setSendTimeFormat(Long sendTimeFormat) {
		this.sendTimeFormat = sendTimeFormat;
	}
	public Long getReadTimeFormat() {
		return readTimeFormat;
	}
	public void setReadTimeFormat(Long readTimeFormat) {
		this.readTimeFormat = readTimeFormat;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	public Date getReadTime() {
		return readTime;
	}
	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}
	public String getFriendUserName() {
		return friendUserName;
	}
	public void setFriendUserName(String friendUserName) {
		this.friendUserName = friendUserName;
	}
	public String getSenderUserName() {
		return senderUserName;
	}
	public void setSenderUserName(String senderUserName) {
		this.senderUserName = senderUserName;
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
	public String getSenderAvatarPath() {
		return senderAvatarPath;
	}
	public void setSenderAvatarPath(String senderAvatarPath) {
		this.senderAvatarPath = senderAvatarPath;
	}
	public String getSenderAvatarName() {
		return senderAvatarName;
	}
	public void setSenderAvatarName(String senderAvatarName) {
		this.senderAvatarName = senderAvatarName;
	}

}
