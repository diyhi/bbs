package cms.bean.message;

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
 * 订阅系统通知
 *
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class SubscriptionSystemNotifyEntity implements Serializable{
	private static final long serialVersionUID = 5915670417853444762L;
	
	/** Id  由18位系统通知Id + 18位用户Id组成 **/
	@Id @Column(length=36)
	protected String id;
	
	/** 系统通知Id **/
	protected Long systemNotifyId;
	
	/** 用户Id **/
	protected Long userId;
	
	/** 通知内容 **/
	@Transient
	protected String content;
	
	/** 消息状态 10:未读  20:已读  110:未读删除  120:已读删除 **/ 
	protected Integer status = 10;
	

	/** 发送时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	protected Date sendTime;
	/** 阅读时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	protected Date readTime;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getSystemNotifyId() {
		return systemNotifyId;
	}
	public void setSystemNotifyId(Long systemNotifyId) {
		this.systemNotifyId = systemNotifyId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
}
