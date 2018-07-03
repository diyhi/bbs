package cms.bean.template;

import java.io.Serializable;

/**
 * 版块---广告相关--图片广告
 *
 */
public class Forum_AdvertisingRelated_Image implements Serializable{
	private static final long serialVersionUID = -804692041640768504L;

	/** 版块---广告相关--轮播广告  Id **/
	private String image_id;
	
	/** 图片名称 **/
	private String image_name;
	/** 图片链接 **/
	private String image_link;

	/** 图片文件路径 储存时不包括模板目录路径 使用时示例: image_filePath + 模板目录 + /   **/
	private String image_filePath;
	/** 图片文件名称 **/
	private String image_fileName;
	
	
	/** 图片路径 
	private String image_path; **/
	
	public String getImage_id() {
		return image_id;
	}
	public void setImage_id(String image_id) {
		this.image_id = image_id;
	}
	public String getImage_name() {
		return image_name;
	}
	public void setImage_name(String image_name) {
		this.image_name = image_name;
	}
	public String getImage_link() {
		return image_link;
	}
	public void setImage_link(String image_link) {
		this.image_link = image_link;
	}
	public String getImage_filePath() {
		return image_filePath;
	}
	public void setImage_filePath(String image_filePath) {
		this.image_filePath = image_filePath;
	}
	public String getImage_fileName() {
		return image_fileName;
	}
	public void setImage_fileName(String image_fileName) {
		this.image_fileName = image_fileName;
	}
	

	
	
}
