package cms.bean.filePackage;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件打包
 *
 */
public class FilePackage implements Serializable{
	private static final long serialVersionUID = 7972769216570916786L;
	
	/** 文件名称 **/
	private String fileName;
	/** 文件创建时间 **/
	private Date createTime;
	/** 文件大小 **/
	private String size;
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
	
}
