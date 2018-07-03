package cms.bean.statistic;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 页面访问量
 *
 */
@Entity
@Table(indexes = {@Index(name="pv_1_idx", columnList="times")})
public class PV implements Serializable{
	private static final long serialVersionUID = -8183252635387484081L;
	
	/** ID **/
	@Id @Column(length=32)
	private String id;
	/** 页面来源 **/
	@Lob
	private String referrer;
	/** 受访URL **/
	@Lob
	private String url;
	/** IP **/
	@Column(length=45)
	private String ip;
	/** IP归属地 **/
	@Transient
	private String ipAddress;
	
	/** 访问时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date times = new Date();
	
	
	/** 浏览器名称 **/
	private String browserName;
	/** 访问设备系统 **/
	private String operatingSystem;
	/** 访问设备类型 **/
	private String deviceType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public Date getTimes() {
		return times;
	}

	public void setTimes(Date times) {
		this.times = times;
	}

	public String getBrowserName() {
		return browserName;
	}

	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}

	public String getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	
	
}
