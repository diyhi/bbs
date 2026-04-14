package cms.model.upgrade;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 升级日志
 *
 */
@Getter
@Setter
public class UpgradeLog implements Serializable{
	@Serial
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

}
