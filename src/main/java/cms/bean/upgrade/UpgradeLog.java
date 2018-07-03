package cms.bean.upgrade;

import java.io.Serializable;
import java.util.Date;

/**
 * 升级日志
 *
 */
public class UpgradeLog implements Serializable{
	private static final long serialVersionUID = -5895893722151074924L;
	
	/** 时间 **/
	private Date time;
	/** 内容 **/
	private String content;
	/** 级别 1:正常  2:错误 **/
	private Integer grade = 1;
	
	
	public UpgradeLog() {}
	public UpgradeLog(Date time, String content, Integer grade) {
		this.time = time;
		this.content = content;
		this.grade = grade;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getGrade() {
		return grade;
	}
	public void setGrade(Integer grade) {
		this.grade = grade;
	}
	
}
