package cms.model.redEnvelope;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 收红包 实体类的抽象基类,定义基本属性
 *
 */
@Getter
@Setter
@MappedSuperclass
public class ReceiveRedEnvelopeEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -2608243371579974705L;

	/** ID (结构：发红包Id_收红包的用户Id)**/
	@Id @Column(length=80)
	protected String id;
	
	/** 收红包的用户Id **/
	protected Long receiveUserId;
	/** 收红包的用户名称 **/
	@Transient
	protected String receiveUserName;
	/** 收红包的账号 **/
	@Transient
	protected String receiveAccount;
	/** 收红包的用户呢称 **/
	@Transient
	protected String receiveNickname;
	/** 收红包的用户头像路径 **/
	@Transient
	protected String receiveAvatarPath;
	/** 收红包的用户头像名称 **/
	@Transient
	protected String receiveAvatarName;
	
	
	
	/** 发红包Id **/
	@Column(length=32)
	protected String giveRedEnvelopeId;
	/** 发红包的用户Id **/
	protected Long giveUserId;
	/** 发红包的用户名称 **/
	@Transient
	protected String giveUserName;
	/** 发红包的账号 **/
	@Transient
	protected String giveAccount;
	/** 发红包的用户呢称 **/
	@Transient
	protected String giveNickname;
	/** 发红包的用户头像路径 **/
	@Transient
	protected String giveAvatarPath;
	/** 发红包的用户头像名称 **/
	@Transient
	protected String giveAvatarName;
	
	/** 收取时间 **/
    @Column(columnDefinition = "DATETIME")
    protected LocalDateTime receiveTime;
	/** 红包金额 **/
	@Column(precision=12, scale=2) 
	protected BigDecimal amount = new BigDecimal("0");
	
	/** 版本号 **/
	protected Integer version = 0;
	

}
