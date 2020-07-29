package cms.bean.redEnvelope;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * 收红包 实体类的抽象基类,定义基本属性
 *
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class ReceiveRedEnvelopeEntity implements Serializable{
	private static final long serialVersionUID = -2608243371579974705L;

	/** ID (结构：发红包Id_收红包的用户Id)**/
	@Id @Column(length=80)
	protected String id;
	
	/** 收红包的用户Id **/
	protected Long receiveUserId;
	/** 收红包的用户名称 **/
	@Transient
	protected String receiveUserName;
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
	@Temporal(TemporalType.TIMESTAMP)
	protected Date receiveTime;
	/** 红包金额 **/
	@Column(precision=12, scale=2) 
	protected BigDecimal amount = new BigDecimal("0");
	
	/** 版本号 **/
	protected Integer version = 0;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getReceiveUserId() {
		return receiveUserId;
	}
	public void setReceiveUserId(Long receiveUserId) {
		this.receiveUserId = receiveUserId;
	}
	public Long getGiveUserId() {
		return giveUserId;
	}
	public void setGiveUserId(Long giveUserId) {
		this.giveUserId = giveUserId;
	}
	public String getGiveUserName() {
		return giveUserName;
	}
	public void setGiveUserName(String giveUserName) {
		this.giveUserName = giveUserName;
	}
	public String getGiveNickname() {
		return giveNickname;
	}
	public void setGiveNickname(String giveNickname) {
		this.giveNickname = giveNickname;
	}
	public String getGiveAvatarPath() {
		return giveAvatarPath;
	}
	public void setGiveAvatarPath(String giveAvatarPath) {
		this.giveAvatarPath = giveAvatarPath;
	}
	public String getGiveAvatarName() {
		return giveAvatarName;
	}
	public void setGiveAvatarName(String giveAvatarName) {
		this.giveAvatarName = giveAvatarName;
	}
	public Date getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getGiveRedEnvelopeId() {
		return giveRedEnvelopeId;
	}
	public void setGiveRedEnvelopeId(String giveRedEnvelopeId) {
		this.giveRedEnvelopeId = giveRedEnvelopeId;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getReceiveUserName() {
		return receiveUserName;
	}
	public void setReceiveUserName(String receiveUserName) {
		this.receiveUserName = receiveUserName;
	}
	public String getReceiveNickname() {
		return receiveNickname;
	}
	public void setReceiveNickname(String receiveNickname) {
		this.receiveNickname = receiveNickname;
	}
	public String getReceiveAvatarPath() {
		return receiveAvatarPath;
	}
	public void setReceiveAvatarPath(String receiveAvatarPath) {
		this.receiveAvatarPath = receiveAvatarPath;
	}
	public String getReceiveAvatarName() {
		return receiveAvatarName;
	}
	public void setReceiveAvatarName(String receiveAvatarName) {
		this.receiveAvatarName = receiveAvatarName;
	}
	
}
