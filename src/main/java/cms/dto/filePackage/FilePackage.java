package cms.dto.filePackage;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 文件打包
 *
 */
@Getter
@Setter
public class FilePackage implements Serializable{
	@Serial
    private static final long serialVersionUID = 7972769216570916786L;
	
	/** 文件名称 **/
	private String fileName;
	/** 文件创建时间 **/
	private Date createTime;
	/** 文件大小 **/
	private String size;

}
