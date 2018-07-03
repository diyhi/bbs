package cms.bean.upgrade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 升级系统
 *
 */
@Entity
public class UpgradeSystem implements Serializable{
	private static final long serialVersionUID = 9036923836935751296L;
	
	/** ID 当前版本 **/
	@Id @Column(length=32)
	private String id;
	/** 旧系统版本 **/
	@Column(length=100)
	private String oldSystemVersion;
	/** 升级包版本 **/
	@Column(length=100)
	private String updatePackageVersion;
	
	/** 运行状态   10:开始复制文件   20:完成复制文件  30:开始删除文件  40:完成删除文件 100:完成重启服务器  完成状态:9999 **/
	private Integer runningStatus = 0;
	/** 中断状态 0:正常 1:错误 2:待重启 **/
	private Integer interruptStatus = 0;
	/** 升级时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date upgradeTime = new Date();
	/** 删除文件路径 json List<String>类型**/
	@Lob
	private String deleteFilePath = "";
	/** 日志 **/
	@Lob
	private String upgradeLog = "[";
	@Transient
	private List<UpgradeLog> upgradeLogList = new ArrayList<UpgradeLog>();
	/** 升级包文件名称 **/
	@Column(length=100)
	private String updatePackageName;
	/** 升级包上传时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatePackageTime;
	/** 升级包第一级目录名称 **/
	@Column(length=100)
	private String updatePackageFirstDirectory;
	
	/** 说明 **/
	@Lob
	private String explanation;
	/** 排序 格式： 4位年 + 2位月 + 2位日 + 2位时 + 2位分   **/
	private Long sort = 0L;
	/** 版本号 **/
	private Integer version = 0;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getRunningStatus() {
		return runningStatus;
	}
	public void setRunningStatus(Integer runningStatus) {
		this.runningStatus = runningStatus;
	}
	public Integer getInterruptStatus() {
		return interruptStatus;
	}
	public void setInterruptStatus(Integer interruptStatus) {
		this.interruptStatus = interruptStatus;
	}
	public Date getUpgradeTime() {
		return upgradeTime;
	}
	public void setUpgradeTime(Date upgradeTime) {
		this.upgradeTime = upgradeTime;
	}
	public String getUpgradeLog() {
		return upgradeLog;
	}
	public void setUpgradeLog(String upgradeLog) {
		this.upgradeLog = upgradeLog;
	}
	public List<UpgradeLog> getUpgradeLogList() {
		return upgradeLogList;
	}
	public void setUpgradeLogList(List<UpgradeLog> upgradeLogList) {
		this.upgradeLogList = upgradeLogList;
	}
	public String getExplanation() {
		return explanation;
	}
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	public Long getSort() {
		return sort;
	}
	public void setSort(Long sort) {
		this.sort = sort;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getOldSystemVersion() {
		return oldSystemVersion;
	}
	public void setOldSystemVersion(String oldSystemVersion) {
		this.oldSystemVersion = oldSystemVersion;
	}
	public String getUpdatePackageVersion() {
		return updatePackageVersion;
	}
	public void setUpdatePackageVersion(String updatePackageVersion) {
		this.updatePackageVersion = updatePackageVersion;
	}
	public String getUpdatePackageName() {
		return updatePackageName;
	}
	public void setUpdatePackageName(String updatePackageName) {
		this.updatePackageName = updatePackageName;
	}
	public Date getUpdatePackageTime() {
		return updatePackageTime;
	}
	public void setUpdatePackageTime(Date updatePackageTime) {
		this.updatePackageTime = updatePackageTime;
	}
	public String getUpdatePackageFirstDirectory() {
		return updatePackageFirstDirectory;
	}
	public void setUpdatePackageFirstDirectory(String updatePackageFirstDirectory) {
		this.updatePackageFirstDirectory = updatePackageFirstDirectory;
	}
	public String getDeleteFilePath() {
		return deleteFilePath;
	}
	public void setDeleteFilePath(String deleteFilePath) {
		this.deleteFilePath = deleteFilePath;
	}
	
	
}
