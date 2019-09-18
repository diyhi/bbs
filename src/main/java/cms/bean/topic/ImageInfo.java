package cms.bean.topic;

import java.io.Serializable;

/**
 * 图片信息
 *
 */
public class ImageInfo implements Serializable{
	private static final long serialVersionUID = 5884756296465895791L;
	/** 图片路径 **/
	private String path;
	/** 图片名称 **/
	private String name;
	
	
	public ImageInfo() {}
	public ImageInfo(String path, String name) {
		this.path = path;
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
