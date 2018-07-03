package cms.bean.statistic;

import java.io.Serializable;

/**
 * 访问信息
 *
 */
public class AccessInfo implements Serializable{
	private static final long serialVersionUID = -100218312963154112L;
	
	/** 浏览器名称 **/
	private String browserName;
	/** 浏览器大类 **/
	private String group;
	/** 详细版本 **/
	private String browserVersion;
	/** 浏览器主版本 **/
	private String version;
	/** 访问设备系统 **/
	private String operatingSystem;
	/** 访问设备类型 **/
	private String deviceType;
	public String getBrowserName() {
		return browserName;
	}
	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getBrowserVersion() {
		return browserVersion;
	}
	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
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
