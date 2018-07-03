package cms.bean;

import java.io.Serializable;

/**
 * Zip 打包
 *
 */
public class ZipPack implements Serializable{
	private static final long serialVersionUID = -4816411879712943046L;

	/** 待压缩的目录或文件 **/
	private String source;
	
	/** 压缩内文件逻辑路径 **/
	private String entryPath;
	/** 是否为目录 **/
	private boolean isDirectory;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getEntryPath() {
		return entryPath;
	}

	public void setEntryPath(String entryPath) {
		this.entryPath = entryPath;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}
	
	
}
