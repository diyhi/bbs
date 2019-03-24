package cms.bean.user;

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
 * 用户登录日志 实体类的抽象基类,定义基本属性
 *
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class UserLoginLogEntity implements Serializable{
	private static final long serialVersionUID = -2196946312722895529L;
	
	/** ID **/
	@Id @Column(length=36)
	protected String id;
	/** 用户Id **/
	protected Long userId;
	
	/** 用户名称 **/
	@Transient
	protected String userName;
	
	/** 类型编号 10:登录 20:续期 **/
	protected Integer typeNumber = 10;
	
	/** 登录时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	protected Date logonTime = new Date();
	
	/** 登录IP **/
	@Column(length=45)
	protected String ip;
	/** IP归属地 **/
	@Transient
	protected String ipAddress;

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

	public Date getLogonTime() {
		return logonTime;
	}

	public void setLogonTime(Date logonTime) {
		this.logonTime = logonTime;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getTypeNumber() {
		return typeNumber;
	}

	public void setTypeNumber(Integer typeNumber) {
		this.typeNumber = typeNumber;
	}
	
	
}
