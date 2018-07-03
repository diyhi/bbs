package cms.bean.upgrade;

import java.io.Serializable;
import java.util.Date;

/**
 * 升级包
 *
 */
public class UpgradePackage implements Serializable{
	private static final long serialVersionUID = -574498890210058384L;
	
	/** 文件名称 **/
	private String name;
	/** 文件大小 **/
	private String size;
	
	/** 最后修改时间 **/
	private Date lastModifiedTime;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	
	
}
