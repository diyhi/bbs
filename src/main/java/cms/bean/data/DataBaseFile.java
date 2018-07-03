package cms.bean.data;

import java.io.Serializable;

/**
 * 数据库文件
 *
 */
public class DataBaseFile implements Serializable{
	private static final long serialVersionUID = -7800541966223236988L;
	/** 文件名称 **/
	private String fileName;
	/** 文件大小 **/
	private String fileSize;
	/** 数据库版本 **/
	private String version;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

}
