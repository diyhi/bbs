package cms.model.upgrade;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Transient;

/**
 * 升级系统
 *
 */
@Getter
@Setter
@Entity
public class UpgradeSystem implements Serializable{
	@Serial
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
	
	/** 运行状态   10:开始复制文件  15:迁移旧模板文件  20:完成复制文件  30:开始删除文件  40:完成删除文件   完成状态:9999 **/
	private Integer runningStatus = 0;
	/** 中断状态 0:正常 1:错误 2:待重启 **/
	private Integer interruptStatus = 0;
	/** 升级时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime upgradeTime = LocalDateTime.now();
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
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime updatePackageTime;
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

}
