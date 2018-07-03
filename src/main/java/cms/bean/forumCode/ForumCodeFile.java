package cms.bean.forumCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 版块代码文件
 *
 */
public class ForumCodeFile implements Serializable{
	private static final long serialVersionUID = -7467303194521376510L;

	/** 名称 **/
	private String fileName;
	
	/** 备注 **/
	private String remark;
	
	/** 电脑版最后修改时间 **/
	private Date pc_lastTime;
	/** 移动版最后修改时间 **/
	private Date wap_lastTime;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getPc_lastTime() {
		return pc_lastTime;
	}
	public void setPc_lastTime(Date pc_lastTime) {
		this.pc_lastTime = pc_lastTime;
	}
	public Date getWap_lastTime() {
		return wap_lastTime;
	}
	public void setWap_lastTime(Date wap_lastTime) {
		this.wap_lastTime = wap_lastTime;
	}
	
	
}
