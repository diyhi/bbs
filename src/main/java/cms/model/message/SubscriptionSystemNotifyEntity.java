package cms.model.message;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 订阅系统通知
 *
 */
@Getter
@Setter
@MappedSuperclass
public class SubscriptionSystemNotifyEntity implements Serializable{
	@Serial
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
    @Column(columnDefinition = "DATETIME")
    protected LocalDateTime sendTime;
	/** 阅读时间 **/
    @Column(columnDefinition = "DATETIME")
    protected LocalDateTime readTime;
	

}
