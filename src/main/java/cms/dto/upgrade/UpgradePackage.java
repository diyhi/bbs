package cms.dto.upgrade;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 升级包
 *
 */
@Getter
@Setter
public class UpgradePackage implements Serializable{
	@Serial
    private static final long serialVersionUID = -574498890210058384L;
	
	/** 文件名称 **/
	private String name;
	/** 文件大小 **/
	private String size;
	
	/** 最后修改时间 **/
	private Date lastModifiedTime;
	
}
