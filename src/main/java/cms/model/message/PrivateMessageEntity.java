package cms.model.message;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 私信Entity
 *
 */
@Getter
@Setter
@MappedSuperclass
public class PrivateMessageEntity implements Serializable{
	@Serial
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
	/** 私信对方账号 **/
	@Transient
	protected String friendAccount;
	/** 私信对方呢称 **/
	@Transient
	protected String friendNickname;
	/** 私信对方头像路径 **/
	@Transient
	protected String friendAvatarPath;
	/** 私信对方头像名称 **/
	@Transient
	protected String friendAvatarName;
	
	
	
	
	/** 发送者用户Id **/
	protected Long senderUserId;
	/** 接受者用户Id **/
	protected Long receiverUserId;
	
	/** 发送者用户名称  **/
	@Transient
	protected String senderUserName;
	/** 发送者账号  **/
	@Transient
	protected String senderAccount;
	/** 发送者呢称  **/
	@Transient
	protected String senderNickname;
	/** 发送者头像路径 **/
	@Transient
	protected String senderAvatarPath;
	/** 发送者头像名称 **/
	@Transient
	protected String senderAvatarName;
	
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
	protected LocalDateTime sendTime;
	/** 阅读时间 **/
	@Transient
	protected LocalDateTime readTime;


}
