package cms.bean.template;

import java.io.Serializable;

/**
 * 广告
 *
 */
public class Advert implements Serializable{
	private static final long serialVersionUID = -4444170325536292033L;
	
	/** 图片名称 **/
	private String name;
	/** 图片链接 **/
	private String link;
	/** 图片路径 **/
	private String path;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
