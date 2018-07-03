package cms.bean.staff;

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
 * 员工登录日志 实体类的抽象基类,定义基本属性
 *
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class StaffLoginLogEntity implements Serializable{
	private static final long serialVersionUID = -6977609383555316054L;
	
	/** ID **/
	@Id @Column(length=36)
	protected String id;
	/** 员工Id **/
	@Column(length=36)
	protected String staffId;
	
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

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
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

	
	
}
